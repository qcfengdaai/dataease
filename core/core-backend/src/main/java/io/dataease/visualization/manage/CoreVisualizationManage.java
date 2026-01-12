package io.dataease.visualization.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.dataease.api.visualization.request.DataVisualizationBaseRequest;
import io.dataease.api.visualization.request.VisualizationWorkbranchQueryRequest;
import io.dataease.api.visualization.vo.VisualizationResourceVO;
import io.dataease.chart.dao.ext.mapper.ExtChartViewMapper;
import io.dataease.chart.manage.ChartViewManege;
import io.dataease.commons.constants.DataVisualizationConstants;
import io.dataease.commons.constants.OptConstants;
import io.dataease.constant.BusiResourceEnum;
import io.dataease.constant.CommonConstants;
import io.dataease.exception.DEException;
import io.dataease.license.config.XpackInteract;
import io.dataease.model.BusiNodeRequest;
import io.dataease.model.BusiNodeVO;
import io.dataease.operation.manage.CoreOptRecentManage;
import io.dataease.utils.*;
import io.dataease.visualization.dao.auto.entity.DataVisualizationInfo;
import io.dataease.visualization.dao.auto.entity.SnapshotDataVisualizationInfo;
import io.dataease.visualization.dao.auto.mapper.DataVisualizationInfoMapper;
import io.dataease.visualization.dao.auto.mapper.SnapshotDataVisualizationInfoMapper;
import io.dataease.visualization.dao.ext.mapper.*;
import io.dataease.visualization.dao.ext.po.VisualizationNodePO;
import io.dataease.visualization.dao.ext.po.VisualizationResourcePO;
import io.dataease.visualization.dto.VisualizationNodeBO;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 核心可视化管理类
 * 负责可视化仪表板的CRUD操作和业务逻辑处理
 */
@Component
@Transactional
public class CoreVisualizationManage {


    @Resource
    private CoreVisualiationExtMapper extMapper;

    @Resource
    private DataVisualizationInfoMapper mapper;

    @Resource
    private SnapshotDataVisualizationInfoMapper snapshotMapper;

    @Resource
    private ExtVisualizationLinkageMapper linkageMapper;

    @Resource
    private ExtVisualizationLinkJumpMapper linkJumpMapper;

    @Resource
    private ExtVisualizationOuterParamsMapper outerParamsMapper;

    @Resource
    private ExtDataVisualizationMapper extDataVisualizationMapper;

    @Resource
    private CoreOptRecentManage coreOptRecentManage;

    @Resource
    private ExtChartViewMapper extCoreChartMapper;

    @Resource
    private ChartViewManege chartViewManege;

    /**
     * 查询可视化资源树
     * 支持企业版扩展
     *
     * @param request 查询请求
     * @return 资源树节点列表
     */
    @XpackInteract(value = "visualizationResourceTree", replace = true, invalid = true)
    public List<BusiNodeVO> tree(BusiNodeRequest request) {
        List<VisualizationNodeBO> nodes = new ArrayList<>();
        // 如果不是只查询叶子节点，则添加根节点
        if (ObjectUtils.isEmpty(request.getLeaf()) || !request.getLeaf()) {
            nodes.add(rootNode());
        }
        // 构建查询条件
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delete_flag", false);  // 只查询未删除的记录
        queryWrapper.ne("pid", -1);              // 排除顶级节点
        // 根据节点类型筛选（叶子节点或文件夹）
        queryWrapper.eq(ObjectUtils.isNotEmpty(request.getLeaf()), "node_type", ObjectUtils.isNotEmpty(request.getLeaf()) && request.getLeaf() ? "leaf" : "folder");
        queryWrapper.eq("type", request.getBusiFlag());  // 根据业务类型筛选（仪表板/数据大屏）
        // 社区版需要过滤企业版专属资源
        String info = CommunityUtils.getInfo();
        if (StringUtils.isNotBlank(info)) {
            queryWrapper.notExists(String.format(info, "data_visualization_info.id"));
        }
        // 如果查询的是快照表（编辑界面），只展示已发布和已下线的资源
        if(CommonConstants.RESOURCE_TABLE.SNAPSHOT.equals(request.getResourceTable())){
            queryWrapper.in("status", Arrays.asList(1,2));  // 1-已发布，2-已下线
        }
        // 按创建时间降序排序
        queryWrapper.orderByDesc("create_time");
        List<VisualizationNodePO> pos = extMapper.queryNodes(queryWrapper);
        if (CollectionUtils.isNotEmpty(pos)) {
            // 将持久化对象转换为业务对象
            nodes.addAll(pos.stream().map(this::convert).toList());
        }
        // 构建树形结构并返回
        return TreeUtils.mergeTree(nodes, BusiNodeVO.class, false);
    }

    /**
     * 删除可视化资源
     * 支持企业版扩展
     * 删除时会级联删除所有子资源和关联的图表
     *
     * @param id 资源ID
     */
    @XpackInteract(value = "visualizationResourceTree", before = false)
    public void delete(Long id) {
        // 检查资源是否存在
        DataVisualizationInfo info = mapper.selectById(id);
        if (ObjectUtils.isEmpty(info)) {
            DEException.throwException("resource not exist");
        }
        // 使用栈结构实现深度优先遍历，递归查找所有需要删除的子节点ID
        Set<Long> delIds = new LinkedHashSet<>();  // 使用LinkedHashSet保持顺序并去重
        Stack<Long> stack = new Stack<>();
        stack.add(id);  // 将根节点ID压入栈
        // 深度优先遍历所有子节点
        while (!stack.isEmpty()) {
            Long tempPid = stack.pop();  // 弹出栈顶节点
            if (isTopNode(tempPid)) continue;  // 跳过顶级节点
            delIds.add(tempPid);  // 添加到待删除集合
            // 查询当前节点的所有子节点
            List<Long> childrenIdList = extMapper.queryChildrenId(tempPid);
            if (CollectionUtils.isNotEmpty(childrenIdList)) {
                // 将子节点压入栈（如果还未添加）
                childrenIdList.forEach(kid -> {
                    if (!delIds.contains(kid)) {
                        stack.add(kid);
                    }
                });
            }
        }
        // 批量删除可视化资源（主表和快照表都需要删除）
        extDataVisualizationMapper.deleteDataVBatch(delIds,CommonConstants.RESOURCE_TABLE.CORE);       // 删除主表数据
        extDataVisualizationMapper.deleteDataVBatch(delIds,CommonConstants.RESOURCE_TABLE.SNAPSHOT);  // 删除快照表数据
        // 批量删除关联的图表信息（主表和快照表都需要删除）
        extDataVisualizationMapper.deleteViewsBatch(delIds,CommonConstants.RESOURCE_TABLE.CORE);       // 删除主表关联图表
        extDataVisualizationMapper.deleteViewsBatch(delIds,CommonConstants.RESOURCE_TABLE.SNAPSHOT);  // 删除快照表关联图表

        // 记录删除操作到最近操作列表
        coreOptRecentManage.saveOpt(id, OptConstants.OPT_RESOURCE_TYPE.VISUALIZATION, OptConstants.OPT_TYPE.DELETE);
    }

    @XpackInteract(value = "visualizationResourceTree", before = false)
    public void move(DataVisualizationBaseRequest request) {
        // 只有在非更新操作时才执行移动逻辑
        if (!request.getMoveFromUpdate()) {
            // 创建可视化信息对象并复制请求参数
            DataVisualizationInfo visualizationInfo = new DataVisualizationInfo();
            BeanUtils.copyBean(visualizationInfo, request);
            // 验证资源ID是否存在
            if (ObjectUtils.isEmpty(visualizationInfo.getId())) {
                DEException.throwException("resource not exist");
            }
            // 设置更新时间
            visualizationInfo.setUpdateTime(System.currentTimeMillis());
            // 创建快照信息对象并复制属性
            SnapshotDataVisualizationInfo snapshotVisualizationInfo = new SnapshotDataVisualizationInfo();
            BeanUtils.copyBean(snapshotVisualizationInfo, visualizationInfo);
            // 记录更新操作到最近操作列表
            coreOptRecentManage.saveOpt(visualizationInfo.getId(), OptConstants.OPT_RESOURCE_TYPE.VISUALIZATION, OptConstants.OPT_TYPE.UPDATE);
            // 同时更新主表和快照表
            mapper.updateById(visualizationInfo);
            snapshotMapper.updateById(snapshotVisualizationInfo);
        }
    }

    @XpackInteract(value = "visualizationResourceTree", before = false)
    public Long innerSave(DataVisualizationInfo visualizationInfo) {
        // 设置版本号为3（表示当前数据模型版本）
        visualizationInfo.setVersion(3);
        return preInnerSave(visualizationInfo);
    }

    public Long preInnerSave(DataVisualizationInfo visualizationInfo) {
        // 如果没有ID，生成新的雪花ID
        if (visualizationInfo.getId() == null) {
            Long id = IDUtils.snowID();
            visualizationInfo.setId(id);
        }
        // 设置基本信息
        visualizationInfo.setDeleteFlag(DataVisualizationConstants.DELETE_FLAG.AVAILABLE);  // 标记为可用
        visualizationInfo.setStatus(visualizationInfo.getStatus());  // 设置状态
        visualizationInfo.setCreateBy(AuthUtils.getUser().getUserId().toString());  // 设置创建者
        visualizationInfo.setUpdateBy(AuthUtils.getUser().getUserId().toString());  // 设置更新者
        visualizationInfo.setCreateTime(System.currentTimeMillis());  // 设置创建时间
        visualizationInfo.setUpdateTime(System.currentTimeMillis());  // 设置更新时间
        visualizationInfo.setOrgId(AuthUtils.getUser().getDefaultOid());  // 设置组织ID
        // 插入主表记录
        mapper.insert(visualizationInfo);
        // 创建快照记录（镜像）用于编辑和发布控制
        SnapshotDataVisualizationInfo snapshotVisualizationInfo = new SnapshotDataVisualizationInfo();
        BeanUtils.copyBean(snapshotVisualizationInfo,visualizationInfo);  // 复制所有属性
        snapshotMapper.insert(snapshotVisualizationInfo);  // 插入快照表
        // 记录新建操作到最近操作列表
        coreOptRecentManage.saveOpt(visualizationInfo.getId(), OptConstants.OPT_RESOURCE_TYPE.VISUALIZATION, OptConstants.OPT_TYPE.NEW);
        return visualizationInfo.getId();
    }

    @XpackInteract(value = "visualizationResourceTree", before = false)
    public void innerEdit(DataVisualizationInfo visualizationInfo) {
        // 设置更新时间和更新者
        visualizationInfo.setUpdateTime(System.currentTimeMillis());
        visualizationInfo.setUpdateBy(AuthUtils.getUser().getUserId().toString());
        visualizationInfo.setVersion(3);  // 设置版本号
        // 更新快照表（镜像表，用于编辑控制）
        SnapshotDataVisualizationInfo snapshotVisualizationInfo = new SnapshotDataVisualizationInfo();
        BeanUtils.copyBean(snapshotVisualizationInfo,visualizationInfo);  // 复制所有属性
        snapshotMapper.updateById(snapshotVisualizationInfo);  // 更新快照表
        // 更新主表（只更新关键字段，保持数据一致性）
        DataVisualizationInfo coreVisualizationInfo = new DataVisualizationInfo();
        coreVisualizationInfo.setId(visualizationInfo.getId());
        coreVisualizationInfo.setStatus(visualizationInfo.getStatus());
        coreVisualizationInfo.setPid(visualizationInfo.getPid());
        coreVisualizationInfo.setContentId(visualizationInfo.getContentId());
        coreVisualizationInfo.setName(visualizationInfo.getName());  // 主要更新名称
        coreVisualizationInfo.setUpdateTime(System.currentTimeMillis());
        coreVisualizationInfo.setUpdateBy(AuthUtils.getUser().getUserId().toString());
        coreVisualizationInfo.setVersion(3);
        mapper.updateById(coreVisualizationInfo);  // 更新主表
        // 记录更新操作到最近操作列表
        coreOptRecentManage.saveOpt(visualizationInfo.getId(), OptConstants.OPT_RESOURCE_TYPE.VISUALIZATION, OptConstants.OPT_TYPE.UPDATE);
    }

    private boolean isTopNode(Long pid) {
        return ObjectUtils.isEmpty(pid) || pid.equals(0L);
    }

    private VisualizationNodeBO rootNode() {
        return new VisualizationNodeBO(0L, "root", false, 7, -1L, 0,1);
    }

    private VisualizationNodeBO convert(VisualizationNodePO po) {
        return new VisualizationNodeBO(po.getId(), po.getName(), StringUtils.equals(po.getNodeType(), "leaf"), 9, po.getPid(), po.getExtraFlag(),po.getExtraFlag1());
    }

    public CoreVisualizationManage proxy() {
        return CommonBeanFactory.getBean(this.getClass());
    }

    @XpackInteract(value = "perFilterManage", recursion = true, invalid = true)
    public IPage<VisualizationResourceVO> query(int pageNum, int pageSize, VisualizationWorkbranchQueryRequest request) {
        IPage<VisualizationResourcePO> visualizationResourcePOPageIPage = proxy().queryVisualizationPage(pageNum, pageSize, request);
        if (ObjectUtils.isEmpty(visualizationResourcePOPageIPage)) {
            return null;
        }
        List<VisualizationResourceVO> vos = proxy().formatResult(visualizationResourcePOPageIPage.getRecords());
        IPage<VisualizationResourceVO> iPage = new Page<>();
        iPage.setCurrent(visualizationResourcePOPageIPage.getCurrent());
        iPage.setPages(visualizationResourcePOPageIPage.getPages());
        iPage.setSize(visualizationResourcePOPageIPage.getSize());
        iPage.setTotal(visualizationResourcePOPageIPage.getTotal());
        iPage.setRecords(vos);
        return iPage;
    }

    List<VisualizationResourceVO> formatResult(List<VisualizationResourcePO> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return new ArrayList<>();
        }
        return pos.stream().map(po ->
                new VisualizationResourceVO(
                        po.getId(), po.getResourceId(), po.getName(),
                        po.getType(), String.valueOf(po.getCreator()), String.valueOf(po.getLastEditor()), po.getLastEditTime(),
                        po.getFavorite(), 9, po.getExtFlag())).toList();
    }

    public IPage<VisualizationResourcePO> queryVisualizationPage(int goPage, int pageSize, VisualizationWorkbranchQueryRequest request) {
        Long uid = AuthUtils.getUser().getUserId();
        Map<String,Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(request.getType())) {
            BusiResourceEnum busiResourceEnum = BusiResourceEnum.valueOf(request.getType().toUpperCase());
            if (ObjectUtils.isEmpty(busiResourceEnum)) {
                DEException.throwException("type is invalid");
            }
            params.put("type",request.getType());
        }
        String info = CommunityUtils.getInfo();
        if (StringUtils.isNotBlank(info)) {
            params.put("info",info);
        }
        params.put("isAsc",request.isAsc());
        Page<VisualizationResourcePO> page = new Page<>(goPage, pageSize);
        return extDataVisualizationMapper.findRecent(page, uid, request.getKeyword(), params);
    }
    @Transactional
    public void removeSnapshot(Long dvId){
        if(dvId != null){
            // 清理快照表的旧数据，为新快照做准备
            Set<Long> dvIds = new HashSet<>();
            dvIds.add(dvId);
            // 删除可视化资源快照
            extDataVisualizationMapper.deleteDataVBatch(dvIds,CommonConstants.RESOURCE_TABLE.SNAPSHOT);
            // 删除关联的图表视图快照
            extCoreChartMapper.deleteViewsBySceneId(dvId,CommonConstants.RESOURCE_TABLE.SNAPSHOT);
            // 删除联动字段配置快照
            linkageMapper.deleteViewLinkageFieldSnapshot(dvId,null);
            // 删除联动配置快照
            linkageMapper.deleteViewLinkageSnapshot(dvId,null);
            // 删除跳转目标视图信息快照
            linkJumpMapper.deleteJumpTargetViewInfoWithVisualizationSnapshot(dvId);
            // 删除跳转信息快照
            linkJumpMapper.deleteJumpInfoWithVisualizationSnapshot(dvId);
            // 删除跳转配置快照
            linkJumpMapper.deleteJumpWithVisualizationSnapshot(dvId);
            // 删除外部参数目标配置快照
            outerParamsMapper.deleteOuterParamsTargetWithVisualizationIdSnapshot(dvId.toString());
            // 删除外部参数信息快照
            outerParamsMapper.deleteOuterParamsInfoWithVisualizationIdSnapshot(dvId.toString());
            // 删除外部参数配置快照
            outerParamsMapper.deleteOuterParamsWithVisualizationIdSnapshot(dvId.toString());
            // 删除阈值告警配置（企业版功能）
            chartViewManege.removeThreshold(dvId,CommonConstants.RESOURCE_TABLE.SNAPSHOT);

        }
    }
    @Transactional
    public void removeDvCore(Long dvId){
        if(dvId != null){
            // 清理主表的旧数据
            Set<Long> dvIds = new HashSet<>();
            dvIds.add(dvId);
            // 删除可视化资源主表记录
            extDataVisualizationMapper.deleteDataVBatch(dvIds,CommonConstants.RESOURCE_TABLE.CORE);
            // 删除关联的图表视图主表记录
            extCoreChartMapper.deleteViewsBySceneId(dvId,CommonConstants.RESOURCE_TABLE.CORE);
            // 删除联动字段配置
            linkageMapper.deleteViewLinkageField(dvId,null);
            // 删除联动配置
            linkageMapper.deleteViewLinkage(dvId,null);
            // 删除跳转目标视图信息
            linkJumpMapper.deleteJumpTargetViewInfoWithVisualization(dvId);
            // 删除跳转信息
            linkJumpMapper.deleteJumpInfoWithVisualization(dvId);
            // 删除跳转配置
            linkJumpMapper.deleteJumpWithVisualization(dvId);
            // 删除外部参数目标配置
            outerParamsMapper.deleteOuterParamsTargetWithVisualizationId(dvId.toString());
            // 删除外部参数信息
            outerParamsMapper.deleteOuterParamsInfoWithVisualizationId(dvId.toString());
            // 删除外部参数配置
            outerParamsMapper.deleteOuterParamsWithVisualizationId(dvId.toString());
            // 删除阈值告警配置（企业版功能）
            chartViewManege.removeThreshold(dvId,CommonConstants.RESOURCE_TABLE.CORE);
        }
    }

    @Transactional
    public void dvSnapshotRecover(Long dvId){
        // 清理旧的快照数据
        CoreVisualizationManage proxy = CommonBeanFactory.proxy(this.getClass());
        assert proxy != null;
        proxy.removeSnapshot(dvId);  // 删除旧快照
        // 从主表复制数据到快照表（用于发布后创建快照）
        extDataVisualizationMapper.snapshotDataV(dvId);  // 复制可视化资源
        extDataVisualizationMapper.snapshotViews(dvId);  // 复制图表视图
        extDataVisualizationMapper.snapshotLinkJumpTargetViewInfo(dvId);  // 复制跳转目标视图信息
        extDataVisualizationMapper.snapshotLinkJumpInfo(dvId);  // 复制跳转信息
        extDataVisualizationMapper.snapshotLinkJump(dvId);  // 复制跳转配置
        extDataVisualizationMapper.snapshotLinkageField(dvId);  // 复制联动字段
        extDataVisualizationMapper.snapshotLinkage(dvId);  // 复制联动配置
        extDataVisualizationMapper.snapshotOuterParamsTargetViewInfo(dvId);  // 复制外部参数目标视图信息
        extDataVisualizationMapper.snapshotOuterParamsInfo(dvId);  // 复制外部参数信息
        extDataVisualizationMapper.snapshotOuterParams(dvId);  // 复制外部参数配置
        // 恢复阈值告警配置（企业版功能）
        chartViewManege.restoreThreshold(dvId,CommonConstants.RESOURCE_TABLE.SNAPSHOT);
    }
    @Transactional
    public void dvRestore(Long dvId){
        // 从快照表恢复数据到主表（用于取消发布或回滚操作）
        extDataVisualizationMapper.restoreDataV(dvId);  // 恢复可视化资源
        extDataVisualizationMapper.restoreViews(dvId);  // 恢复图表视图
        extDataVisualizationMapper.restoreLinkJumpTargetViewInfo(dvId);  // 恢复跳转目标视图信息
        extDataVisualizationMapper.restoreLinkJumpInfo(dvId);  // 恢复跳转信息
        extDataVisualizationMapper.restoreLinkJump(dvId);  // 恢复跳转配置
        extDataVisualizationMapper.restoreLinkageField(dvId);  // 恢复联动字段
        extDataVisualizationMapper.restoreLinkage(dvId);  // 恢复联动配置
        extDataVisualizationMapper.restoreOuterParamsTargetViewInfo(dvId);  // 恢复外部参数目标视图信息
        extDataVisualizationMapper.restoreOuterParamsInfo(dvId);  // 恢复外部参数信息
        extDataVisualizationMapper.restoreOuterParams(dvId);  // 恢复外部参数配置
        // 恢复阈值告警配置（企业版功能）
        chartViewManege.restoreThreshold(dvId,CommonConstants.RESOURCE_TABLE.CORE);
    }

}
