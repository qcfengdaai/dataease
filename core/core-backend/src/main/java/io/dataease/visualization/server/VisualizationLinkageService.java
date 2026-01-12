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
        if (CollectionUtils.isNotEmpty(request.getTargetViewIds())) {
            List<VisualizationLinkageDTO> linkageDTOList = null;
            if (CommonConstants.RESOURCE_TABLE.SNAPSHOT.equals(request.getResourceTable())) {
                linkageDTOList = extVisualizationLinkageMapper.getViewLinkageGatherSnapshot(request.getDvId(), request.getSourceViewId(), request.getTargetViewIds());
            } else {
                linkageDTOList =  extVisualizationLinkageMapper.getViewLinkageGather(request.getDvId(), request.getSourceViewId(), request.getTargetViewIds());
            }
            return linkageDTOList.stream().collect(Collectors.toMap(targetViewId -> String.valueOf(targetViewId), PanelViewLinkageDTO -> PanelViewLinkageDTO));
        }
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
        if (CommonConstants.RESOURCE_TABLE.SNAPSHOT.equals(request.getResourceTable())) {
            return extVisualizationLinkageMapper.getViewLinkageGatherSnapshot(request.getDvId(), request.getSourceViewId(), request.getTargetViewIds());
        } else {
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
        // 向镜像中保存
        Long updateTime = System.currentTimeMillis();
        List<VisualizationLinkageDTO> linkageInfo = request.getLinkageInfo();
        Long sourceViewId = request.getSourceViewId();
        Long dvId = request.getDvId();

        Assert.notNull(sourceViewId, "source View ID can not be null");
        Assert.notNull(dvId, "dvId can not be null");

        // 清理原有关系
        extVisualizationLinkageMapper.deleteViewLinkageFieldSnapshot(dvId, sourceViewId);
        extVisualizationLinkageMapper.deleteViewLinkageSnapshot(dvId, sourceViewId);

        //重新建立关系
        for (VisualizationLinkageDTO linkageDTO : linkageInfo) {
            //去掉source view 的信息
            if (sourceViewId.equals(linkageDTO.getTargetViewId())) {
                continue;
            }
            List<VisualizationLinkageFieldVO> linkageFields = linkageDTO.getLinkageFields();
            Long linkageId = IDUtils.snowID();
            SnapshotVisualizationLinkage linkage = new SnapshotVisualizationLinkage();
            linkage.setId(linkageId);
            linkage.setDvId(dvId);
            linkage.setSourceViewId(sourceViewId);
            linkage.setTargetViewId(linkageDTO.getTargetViewId());
            linkage.setUpdatePeople("");
            linkage.setUpdateTime(updateTime);
            linkage.setLinkageActive(linkageDTO.getLinkageActive());
            snapshotVisualizationLinkageMapper.insert(linkage);
            if (CollectionUtils.isNotEmpty(linkageFields) && linkageDTO.getLinkageActive()) {
                linkageFields.forEach(linkageField -> {
                    linkageField.setId(IDUtils.snowID());
                    linkageField.setLinkageId(linkageId);
                    linkageField.setUpdateTime(updateTime);
                    SnapshotVisualizationLinkageField fieldInsert = new SnapshotVisualizationLinkageField();
                    snapshotVisualizationLinkageFieldMapper.insert(BeanUtils.copyBean(fieldInsert, linkageField));
                });
            }
        }
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
        List<LinkageInfoDTO> info = null;
        if (CommonConstants.RESOURCE_TABLE.SNAPSHOT.equals(resourceTable)) {
            info = extVisualizationLinkageMapper.getPanelAllLinkageInfoSnapshot(dvId);
        }else{
            info = extVisualizationLinkageMapper.getPanelAllLinkageInfo(dvId);
        }
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
        SnapshotCoreChartView coreChartView = new SnapshotCoreChartView();
        coreChartView.setId(request.getSourceViewId());
        coreChartView.setLinkageActive(request.getActiveStatus());
        snapshotCoreChartViewMapper.updateById(coreChartView);
        return getVisualizationAllLinkageInfo(request.getDvId(),CommonConstants.RESOURCE_TABLE.SNAPSHOT);
    }

    /**
     * 删除图表联动配置
     *
     * @param request 联动请求
     */
    @Override
    public void removeLinkage(VisualizationLinkageRequest request) {
        // 清理原有关系
        extVisualizationLinkageMapper.deleteViewLinkageFieldSnapshot(request.getDvId(), request.getSourceViewId());
        extVisualizationLinkageMapper.deleteViewLinkageSnapshot(request.getDvId(), request.getSourceViewId());
    }
}
