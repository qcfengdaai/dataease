package io.dataease.visualization.server;

import io.dataease.api.commons.BaseRspModel;
import io.dataease.api.visualization.VisualizationLinkageApi;
import io.dataease.api.visualization.dto.LinkageInfoDTO;
import io.dataease.api.visualization.dto.VisualizationLinkageDTO;
import io.dataease.api.visualization.request.VisualizationLinkageRequest;
import io.dataease.api.visualization.vo.VisualizationLinkageFieldVO;
import io.dataease.auth.DeLinkPermit;
import io.dataease.chart.dao.auto.entity.CoreChartView;
import io.dataease.chart.dao.auto.mapper.CoreChartViewMapper;
import io.dataease.constant.CommonConstants;
import io.dataease.utils.BeanUtils;
import io.dataease.utils.IDUtils;
import io.dataease.visualization.dao.auto.entity.*;
import io.dataease.visualization.dao.auto.mapper.*;
import io.dataease.visualization.dao.ext.mapper.ExtVisualizationLinkageMapper;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 可视化图表联动服务
 * <p>
 * 处理仪表板中图表之间的联动关系
 * <p>
 * 主要功能：
 * <ul>
 * <li>保存图表联动配置</li>
 * <li>查询图表联动关系</li>
 * <li>更新联动激活状态</li>
 * <li>删除联动配置</li>
 * </ul>
 *
 * @author DataEase
 * @since 2024-06-21
 */
@RestController
@RequestMapping("linkage")
public class VisualizationLinkageService implements VisualizationLinkageApi {

    @Resource
    private ExtVisualizationLinkageMapper extVisualizationLinkageMapper;

    @Resource
    private VisualizationLinkageFieldMapper visualizationLinkageFieldMapper;

    @Resource
    private SnapshotVisualizationLinkageFieldMapper snapshotVisualizationLinkageFieldMapper;

    @Resource
    private VisualizationLinkageMapper visualizationLinkageMapper;

    @Resource
    private SnapshotVisualizationLinkageMapper snapshotVisualizationLinkageMapper;

    @Resource
    private DataVisualizationInfoMapper dataVisualizationInfoMapper;

    @Resource
    private CoreChartViewMapper coreChartViewMapper;

    @Resource
    private SnapshotCoreChartViewMapper snapshotCoreChartViewMapper;

    /**
     * 获取视图联动信息集合
     *
     * @param request 联动请求
     * @return 视图ID到联动信息的映射
     */
    @Override
    public Map<String, VisualizationLinkageDTO> getViewLinkageGather(VisualizationLinkageRequest request) {
        // 1. 检查目标视图ID列表是否为空
        if (CollectionUtils.isNotEmpty(request.getTargetViewIds())) {
            List<VisualizationLinkageDTO> linkageDTOList = null;
            // 2. 根据资源表类型（主表或快照表）查询联动信息
            if (CommonConstants.RESOURCE_TABLE.SNAPSHOT.equals(request.getResourceTable())) {
                // 2.1 查询快照表的联动信息
                linkageDTOList = extVisualizationLinkageMapper.getViewLinkageGatherSnapshot(request.getDvId(), request.getSourceViewId(), request.getTargetViewIds());
            } else {
                // 2.2 查询主表的联动信息
                linkageDTOList =  extVisualizationLinkageMapper.getViewLinkageGather(request.getDvId(), request.getSourceViewId(), request.getTargetViewIds());
            }
            // 3. 将列表转换为Map，key为目标视图ID，value为联动信息
            return linkageDTOList.stream().collect(Collectors.toMap(targetViewId -> String.valueOf(targetViewId), PanelViewLinkageDTO -> PanelViewLinkageDTO));
        }
        // 4. 如果目标视图ID列表为空，返回空Map
        return new HashMap<>();
    }

    /**
     * 获取视图联动信息列表
     *
     * @param request 联动请求
     * @return 联动信息列表
     */
    @Override
    public List<VisualizationLinkageDTO> getViewLinkageGatherArray(VisualizationLinkageRequest request) {
        // 根据资源表类型查询联动信息列表
        if (CommonConstants.RESOURCE_TABLE.SNAPSHOT.equals(request.getResourceTable())) {
            // 查询快照表的联动信息
            return extVisualizationLinkageMapper.getViewLinkageGatherSnapshot(request.getDvId(), request.getSourceViewId(), request.getTargetViewIds());
        } else {
            // 查询主表的联动信息
            return extVisualizationLinkageMapper.getViewLinkageGather(request.getDvId(), request.getSourceViewId(), request.getTargetViewIds());
        }
    }

    /**
     * 保存图表联动配置
     * <p>
     * 会清除原有联动关系，然后建立新的联动关系
     *
     * @param request 联动配置请求
     * @return 操作响应
     */
    @Override
    @Transactional
    public BaseRspModel saveLinkage(VisualizationLinkageRequest request) {
        // ========== 第一阶段：初始化 ==========
        Long updateTime = System.currentTimeMillis();
        List<VisualizationLinkageDTO> linkageInfo = request.getLinkageInfo();
        Long sourceViewId = request.getSourceViewId();
        Long dvId = request.getDvId();

        // ========== 第二阶段：参数校验 ==========
        Assert.notNull(sourceViewId, "source View ID can not be null");
        Assert.notNull(dvId, "dvId can not be null");

        // ========== 第三阶段：清理原有联动关系 ==========
        // 3.1 删除原有的联动字段关系
        extVisualizationLinkageMapper.deleteViewLinkageFieldSnapshot(dvId, sourceViewId);
        // 3.2 删除原有的联动关系
        extVisualizationLinkageMapper.deleteViewLinkageSnapshot(dvId, sourceViewId);

        // ========== 第四阶段：重新建立联动关系 ==========
        for (VisualizationLinkageDTO linkageDTO : linkageInfo) {
            // 4.1 跳过源视图自身的联动（不需要自己联动自己）
            if (sourceViewId.equals(linkageDTO.getTargetViewId())) {
                continue;
            }
            // 4.2 获取联动字段列表
            List<VisualizationLinkageFieldVO> linkageFields = linkageDTO.getLinkageFields();
            // 4.3 生成联动关系ID
            Long linkageId = IDUtils.snowID();
            // 4.4 创建联动关系实体
            SnapshotVisualizationLinkage linkage = new SnapshotVisualizationLinkage();
            linkage.setId(linkageId);
            linkage.setDvId(dvId);
            linkage.setSourceViewId(sourceViewId);
            linkage.setTargetViewId(linkageDTO.getTargetViewId());
            linkage.setUpdatePeople("");
            linkage.setUpdateTime(updateTime);
            linkage.setLinkageActive(linkageDTO.getLinkageActive());
            // 4.5 保存联动关系到数据库
            snapshotVisualizationLinkageMapper.insert(linkage);
            // 4.6 如果有联动字段且联动已激活，保存字段关系
            if (CollectionUtils.isNotEmpty(linkageFields) && linkageDTO.getLinkageActive()) {
                linkageFields.forEach(linkageField -> {
                    // 4.6.1 生成字段ID并设置关联关系
                    linkageField.setId(IDUtils.snowID());
                    linkageField.setLinkageId(linkageId);
                    linkageField.setUpdateTime(updateTime);
                    // 4.6.2 创建字段实体并保存
                    SnapshotVisualizationLinkageField fieldInsert = new SnapshotVisualizationLinkageField();
                    snapshotVisualizationLinkageFieldMapper.insert(BeanUtils.copyBean(fieldInsert, linkageField));
                });
            }
        }
        // ========== 第五阶段：返回结果 ==========
        return new BaseRspModel();
    }

    /**
     * 获取仪表板的所有联动信息
     *
     * @param dvId         仪表板ID
     * @param resourceTable 资源表（core或snapshot）
     * @return 联动信息映射
     */
    @DeLinkPermit
    @Override
    public Map<String, List<String>> getVisualizationAllLinkageInfo(Long dvId, String resourceTable) {
        // 1. 根据资源表类型查询联动信息
        List<LinkageInfoDTO> info = null;
        if (CommonConstants.RESOURCE_TABLE.SNAPSHOT.equals(resourceTable)) {
            // 1.1 查询快照表的所有联动信息
            info = extVisualizationLinkageMapper.getPanelAllLinkageInfoSnapshot(dvId);
        }else{
            // 1.2 查询主表的所有联动信息
            info = extVisualizationLinkageMapper.getPanelAllLinkageInfo(dvId);
        }
        // 2. 将联动信息列表转换为Map，key为源信息，value为目标信息列表
        return Optional.ofNullable(info).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(LinkageInfoDTO::getSourceInfo, LinkageInfoDTO::getTargetInfoList));
    }

    /**
     * 更新联动激活状态
     *
     * @param request 联动请求
     * @return 所有联动信息
     */
    @Override
    public Map updateLinkageActive(VisualizationLinkageRequest request) {
        // 1. 创建快照图表视图实体
        SnapshotCoreChartView coreChartView = new SnapshotCoreChartView();
        // 2. 设置视图ID和联动激活状态
        coreChartView.setId(request.getSourceViewId());
        coreChartView.setLinkageActive(request.getActiveStatus());
        // 3. 更新数据库中的联动激活状态
        snapshotCoreChartViewMapper.updateById(coreChartView);
        // 4. 返回更新后的所有联动信息
        return getVisualizationAllLinkageInfo(request.getDvId(),CommonConstants.RESOURCE_TABLE.SNAPSHOT);
    }

    /**
     * 删除图表联动配置
     *
     * @param request 联动请求
     */
    @Override
    public void removeLinkage(VisualizationLinkageRequest request) {
        // 1. 删除联动字段关系
        extVisualizationLinkageMapper.deleteViewLinkageFieldSnapshot(request.getDvId(), request.getSourceViewId());
        // 2. 删除联动关系
        extVisualizationLinkageMapper.deleteViewLinkageSnapshot(request.getDvId(), request.getSourceViewId());
    }
}
