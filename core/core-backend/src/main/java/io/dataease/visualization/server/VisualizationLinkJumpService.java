package io.dataease.visualization.server;

import io.dataease.api.visualization.VisualizationLinkJumpApi;
import io.dataease.api.visualization.dto.VisualizationComponentDTO;
import io.dataease.api.visualization.dto.VisualizationLinkJumpDTO;
import io.dataease.api.visualization.dto.VisualizationLinkJumpInfoDTO;
import io.dataease.api.visualization.request.VisualizationLinkJumpBaseRequest;
import io.dataease.api.visualization.response.VisualizationLinkJumpBaseResponse;
import io.dataease.api.visualization.vo.VisualizationOutParamsJumpVO;
import io.dataease.api.visualization.vo.VisualizationViewTableVO;
import io.dataease.auth.DeLinkPermit;
import io.dataease.chart.dao.auto.entity.CoreChartView;
import io.dataease.chart.dao.auto.mapper.CoreChartViewMapper;
import io.dataease.constant.CommonConstants;
import io.dataease.extensions.datasource.dto.DatasetTableFieldDTO;
import io.dataease.utils.AuthUtils;
import io.dataease.utils.BeanUtils;
import io.dataease.utils.IDUtils;
import io.dataease.utils.ModelUtils;
import io.dataease.visualization.dao.auto.entity.*;
import io.dataease.visualization.dao.auto.mapper.*;
import io.dataease.visualization.dao.ext.mapper.ExtVisualizationLinkJumpMapper;
import io.dataease.visualization.dao.ext.mapper.ExtVisualizationLinkageMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 可视化图表跳转服务
 * <p>
 * 处理仪表板中图表的跳转配置
 * <p>
 * 主要功能：
 * <ul>
 * <li>配置图表跳转（内部跳转或外部链接）</li>
 * <li>查询跳转信息</li>
 * <li>更新跳转激活状态</li>
 * <li>删除跳转配置</li>
 * </ul>
 *
 * @author DataEase
 * @since 2024-06-21
 */
@RestController
@RequestMapping("linkJump")
public class VisualizationLinkJumpService implements VisualizationLinkJumpApi {

    @Resource
    private ExtVisualizationLinkageMapper extVisualizationLinkageMapper;

    @Resource
    private ExtVisualizationLinkJumpMapper extVisualizationLinkJumpMapper;

    @Resource
    private VisualizationLinkJumpMapper visualizationLinkJumpMapper;

    @Resource
    private VisualizationLinkJumpInfoMapper visualizationLinkJumpInfoMapper;

    @Resource
    private VisualizationLinkJumpTargetViewInfoMapper visualizationLinkJumpTargetViewInfoMapper;

    @Resource
    private SnapshotVisualizationLinkJumpMapper snapshotVisualizationLinkJumpMapper;

    @Resource
    private SnapshotVisualizationLinkJumpInfoMapper snapshotVisualizationLinkJumpInfoMapper;

    @Resource
    private SnapshotVisualizationLinkJumpTargetViewInfoMapper snapshotVisualizationLinkJumpTargetViewInfoMapper;

    @Resource
    private CoreChartViewMapper coreChartViewMapper;

    @Resource
    private SnapshotCoreChartViewMapper snapshotCoreChartViewMapper;


    @Resource
    private DataVisualizationInfoMapper dataVisualizationInfoMapper;

    /**
     * 根据视图ID获取表字段
     *
     * @param viewId 视图ID
     * @return 字段列表
     */
    @Override
    public List<DatasetTableFieldDTO> getTableFieldWithViewId(Long viewId) {
        // 根据视图ID查询关联的数据集字段列表
        return extVisualizationLinkageMapper.queryTableFieldWithViewId(viewId);
    }

    /**
     * 获取仪表板的跳转信息
     *
     * @param dvId          仪表板ID
     * @param resourceTable 资源表（core或snapshot）
     * @return 跳转信息
     */
    @DeLinkPermit
    @Override
    public VisualizationLinkJumpBaseResponse queryVisualizationJumpInfo(Long dvId, String resourceTable) {
        // 1. 创建结果Map，key为"源视图ID#源字段ID"，value为跳转信息
        Map<String, VisualizationLinkJumpInfoDTO> resultBase = new HashMap<>();
        // 2. 根据资源表类型查询跳转配置
        List<VisualizationLinkJumpDTO> resultLinkJumpList = null;
        if (CommonConstants.RESOURCE_TABLE.SNAPSHOT.equals(resourceTable)) {
            // 2.1 查询快照表的跳转配置
            resultLinkJumpList = extVisualizationLinkJumpMapper.queryWithDvIdSnapshot(dvId, AuthUtils.getUser().getUserId(), ModelUtils.isDesktop());
        } else {
            // 2.2 查询主表的跳转配置
            resultLinkJumpList = extVisualizationLinkJumpMapper.queryWithDvId(dvId, AuthUtils.getUser().getUserId(), ModelUtils.isDesktop());
        }
        // 3. 遍历跳转配置，构建结果Map
        Optional.ofNullable(resultLinkJumpList).orElse(new ArrayList<>()).forEach(resultLinkJump -> {
            // 3.1 只处理已启用的跳转配置
            if (resultLinkJump.getChecked()) {
                Long sourceViewId = resultLinkJump.getSourceViewId();
                // 3.2 遍历该视图的所有跳转信息
                Optional.ofNullable(resultLinkJump.getLinkJumpInfoArray()).orElse(new ArrayList<>()).forEach(linkJumpInfo -> {
                    // 3.2.1 只处理已启用的跳转字段
                    if (linkJumpInfo.getChecked()) {
                        // 3.2.2 构建key：源视图ID#源字段ID
                        String sourceJumpInfo = sourceViewId + "#" + linkJumpInfo.getSourceFieldId();
                        // 3.2.3 判断跳转类型
                        if ("inner".equals(linkJumpInfo.getLinkType())) {
                            // 3.2.3.1 内部仪表板跳转：需要检查目标仪表板ID是否存在
                            if (linkJumpInfo.getTargetDvId() != null) {
                                resultBase.put(sourceJumpInfo, linkJumpInfo);
                            }
                        } else {
                            // 3.2.3.2 外部链接跳转：直接添加
                            resultBase.put(sourceJumpInfo, linkJumpInfo);
                        }
                    }
                });
            }
        });
        // 4. 返回跳转信息响应
        return new VisualizationLinkJumpBaseResponse(resultBase, null);
    }

    /**
     * 根据视图ID查询跳转信息
     *
     * @param dvId  仪表板ID
     * @param viewId 视图ID
     * @return 跳转信息
     */
    @Override
    public VisualizationLinkJumpDTO queryWithViewId(Long dvId, Long viewId) {
        // 根据仪表板ID和视图ID查询跳转配置
        return extVisualizationLinkJumpMapper.queryWithViewId(dvId, viewId, AuthUtils.getUser().getUserId(), ModelUtils.isDesktop());
    }

    /**
     * 更新跳转配置
     * <p>
     * 会清除原有跳转配置，然后保存新的配置
     *
     * @param jumpDTO 跳转配置
     */
    @Transactional
    @Override
    public void updateJumpSet(VisualizationLinkJumpDTO jumpDTO) {
        // ========== 第一阶段：参数校验 ==========
        Long dvId = jumpDTO.getSourceDvId();
        Long viewId = jumpDTO.getSourceViewId();
        Assert.notNull(dvId, "dvId cannot be null");
        Assert.notNull(viewId, "viewId cannot be null");

        // ========== 第二阶段：清理原有跳转配置 ==========
        // 2.1 删除目标视图信息
        extVisualizationLinkJumpMapper.deleteJumpTargetViewInfoSnapshot(dvId, viewId);
        // 2.2 删除跳转字段信息
        extVisualizationLinkJumpMapper.deleteJumpInfoSnapshot(dvId, viewId);
        // 2.3 删除跳转配置
        extVisualizationLinkJumpMapper.deleteJumpSnapshot(dvId, viewId);

        // ========== 第三阶段：插入新的跳转配置 ==========
        // 3.1 生成跳转配置ID
        Long linkJumpId = IDUtils.snowID();
        jumpDTO.setId(linkJumpId);
        // 3.2 创建跳转配置实体并保存
        SnapshotVisualizationLinkJump insertParam = new SnapshotVisualizationLinkJump();
        BeanUtils.copyBean(insertParam, jumpDTO);
        snapshotVisualizationLinkJumpMapper.insert(insertParam);
        // 3.3 遍历跳转字段信息数组
        Optional.ofNullable(jumpDTO.getLinkJumpInfoArray()).orElse(new ArrayList<>()).forEach(linkJumpInfo -> {
            // 3.3.1 生成跳转字段信息ID
            Long linkJumpInfoId = IDUtils.snowID();
            linkJumpInfo.setId(linkJumpInfoId);
            linkJumpInfo.setLinkJumpId(linkJumpId);
            // 3.3.2 创建跳转字段信息实体并保存
            SnapshotVisualizationLinkJumpInfo insertJumpInfoParam = new SnapshotVisualizationLinkJumpInfo();
            BeanUtils.copyBean(insertJumpInfoParam, linkJumpInfo);
            snapshotVisualizationLinkJumpInfoMapper.insert(insertJumpInfoParam);
            // 3.3.3 遍历目标视图信息列表
            Optional.ofNullable(linkJumpInfo.getTargetViewInfoList()).orElse(new ArrayList<>()).forEach(targetViewInfo -> {
                // 3.3.3.1 生成目标视图信息ID
                Long targetViewInfoId = IDUtils.snowID();
                targetViewInfo.setTargetId(targetViewInfoId);
                targetViewInfo.setLinkJumpInfoId(linkJumpInfoId);
                // 3.3.3.2 创建目标视图信息实体并保存
                SnapshotVisualizationLinkJumpTargetViewInfo insertTargetViewInfoParam = new SnapshotVisualizationLinkJumpTargetViewInfo();
                BeanUtils.copyBean(insertTargetViewInfoParam, targetViewInfo);
                snapshotVisualizationLinkJumpTargetViewInfoMapper.insert(insertTargetViewInfoParam);
            });
        });
    }

    /**
     * 查询目标仪表板的跳转信息
     *
     * @param request 跳转请求
     * @return 跳转信息
     */
    @DeLinkPermit("#p0.targetDvId")
    @Override
    public VisualizationLinkJumpBaseResponse queryTargetVisualizationJumpInfo(VisualizationLinkJumpBaseRequest request) {
        // 1. 根据资源表类型查询目标仪表板的跳转信息
        List<VisualizationLinkJumpDTO> result = null;
        if (CommonConstants.RESOURCE_TABLE.SNAPSHOT.equals(request.getResourceTable())) {
            // 1.1 查询快照表
            result = extVisualizationLinkJumpMapper.getTargetVisualizationJumpInfoSnapshot(request);
        } else {
            // 1.2 查询主表
            result = extVisualizationLinkJumpMapper.getTargetVisualizationJumpInfo(request);
        }
        // 2. 将结果转换为Map，key为源信息，value为目标信息列表
        return new VisualizationLinkJumpBaseResponse(null, Optional.ofNullable(result).orElse(new ArrayList<>()).stream().filter(item -> StringUtils.isNotEmpty(item.getSourceInfo())).collect(Collectors.toMap(VisualizationLinkJumpDTO::getSourceInfo, VisualizationLinkJumpDTO::getTargetInfoList)));
    }

    /**
     * 获取视图表详情列表
     *
     * @param dvId 仪表板ID
     * @return 视图组件详情
     */
    @Override
    public VisualizationComponentDTO viewTableDetailList(Long dvId) {
        // 1. 查询仪表板基本信息
        DataVisualizationInfo dvInfo = dataVisualizationInfoMapper.selectById(dvId);
        List<VisualizationViewTableVO> result;
        List<VisualizationOutParamsJumpVO> outParamsJumpInfo;
        String componentData;
        // 2. 判断仪表板是否存在
        if (dvInfo != null) {
            // 2.1 查询视图详情列表，并过滤出组件数据中实际使用的视图
            result = extVisualizationLinkJumpMapper.getViewTableDetails(dvId).stream().filter(viewTableInfo -> dvInfo.getComponentData().indexOf(viewTableInfo.getId().toString()) > -1).collect(Collectors.toList());
            // 2.2 获取组件数据（JSON字符串）
            componentData = dvInfo.getComponentData();
            // 2.3 查询外部参数跳转信息
            outParamsJumpInfo = extVisualizationLinkJumpMapper.queryOutParamsTargetWithDvId(dvId);
        } else {
            // 2.4 仪表板不存在，返回空数据
            result = new ArrayList<>();
            outParamsJumpInfo = new ArrayList<>();
            componentData = "[]";
        }
        // 3. 返回组件详情DTO
        return new VisualizationComponentDTO(componentData, result, outParamsJumpInfo);
    }

    /**
     * 更新跳转激活状态
     *
     * @param request 跳转请求
     * @return 跳转信息
     */
    @Override
    public VisualizationLinkJumpBaseResponse updateJumpSetActive(VisualizationLinkJumpBaseRequest request) {
        // 1. 创建快照图表视图实体
        SnapshotCoreChartView coreChartView = new SnapshotCoreChartView();
        // 2. 设置视图ID和跳转激活状态
        coreChartView.setId(Long.valueOf(request.getSourceViewId()));
        coreChartView.setJumpActive(request.getActiveStatus());
        // 3. 更新数据库中的跳转激活状态
        snapshotCoreChartViewMapper.updateById(coreChartView);
        // 4. 查询并返回更新后的跳转信息
        return queryVisualizationJumpInfo(request.getSourceDvId(), CommonConstants.RESOURCE_TABLE.SNAPSHOT);
    }

    /**
     * 删除跳转配置
     *
     * @param jumpDTO 跳转配置
     */
    @Override
    public void removeJumpSet(VisualizationLinkJumpDTO jumpDTO) {
        // 1. 删除目标视图信息
        extVisualizationLinkJumpMapper.deleteJumpTargetViewInfoSnapshot(jumpDTO.getSourceDvId(), jumpDTO.getSourceViewId());
        // 2. 删除跳转字段信息
        extVisualizationLinkJumpMapper.deleteJumpInfoSnapshot(jumpDTO.getSourceDvId(), jumpDTO.getSourceViewId());
        // 3. 删除跳转配置
        extVisualizationLinkJumpMapper.deleteJumpSnapshot(jumpDTO.getSourceDvId(), jumpDTO.getSourceViewId());
    }

}
