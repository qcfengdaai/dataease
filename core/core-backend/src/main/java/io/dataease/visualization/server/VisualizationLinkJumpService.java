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
        Map<String, VisualizationLinkJumpInfoDTO> resultBase = new HashMap<>();
        List<VisualizationLinkJumpDTO> resultLinkJumpList = null;
        if (CommonConstants.RESOURCE_TABLE.SNAPSHOT.equals(resourceTable)) {
            resultLinkJumpList = extVisualizationLinkJumpMapper.queryWithDvIdSnapshot(dvId, AuthUtils.getUser().getUserId(), ModelUtils.isDesktop());
        } else {
            resultLinkJumpList = extVisualizationLinkJumpMapper.queryWithDvId(dvId, AuthUtils.getUser().getUserId(), ModelUtils.isDesktop());
        }
        Optional.ofNullable(resultLinkJumpList).orElse(new ArrayList<>()).forEach(resultLinkJump -> {
            if (resultLinkJump.getChecked()) {
                Long sourceViewId = resultLinkJump.getSourceViewId();
                Optional.ofNullable(resultLinkJump.getLinkJumpInfoArray()).orElse(new ArrayList<>()).forEach(linkJumpInfo -> {
                    if (linkJumpInfo.getChecked()) {
                        String sourceJumpInfo = sourceViewId + "#" + linkJumpInfo.getSourceFieldId();
                        // 内部仪表板跳转 需要设置好仪表板ID
                        if ("inner".equals(linkJumpInfo.getLinkType())) {
                            if (linkJumpInfo.getTargetDvId() != null) {
                                resultBase.put(sourceJumpInfo, linkJumpInfo);
                            }
                        } else {
                            // 外部跳转
                            resultBase.put(sourceJumpInfo, linkJumpInfo);
                        }
                    }
                });
            }
        });
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
        Long dvId = jumpDTO.getSourceDvId();
        Long viewId = jumpDTO.getSourceViewId();
        Assert.notNull(dvId, "dvId cannot be null");
        Assert.notNull(viewId, "viewId cannot be null");
        //清理原有数据
        extVisualizationLinkJumpMapper.deleteJumpTargetViewInfoSnapshot(dvId, viewId);
        extVisualizationLinkJumpMapper.deleteJumpInfoSnapshot(dvId, viewId);
        extVisualizationLinkJumpMapper.deleteJumpSnapshot(dvId, viewId);

        // 插入新的数据
        Long linkJumpId = IDUtils.snowID();
        jumpDTO.setId(linkJumpId);
        SnapshotVisualizationLinkJump insertParam = new SnapshotVisualizationLinkJump();
        BeanUtils.copyBean(insertParam, jumpDTO);
        snapshotVisualizationLinkJumpMapper.insert(insertParam);
        Optional.ofNullable(jumpDTO.getLinkJumpInfoArray()).orElse(new ArrayList<>()).forEach(linkJumpInfo -> {
            Long linkJumpInfoId = IDUtils.snowID();
            linkJumpInfo.setId(linkJumpInfoId);
            linkJumpInfo.setLinkJumpId(linkJumpId);
            SnapshotVisualizationLinkJumpInfo insertJumpInfoParam = new SnapshotVisualizationLinkJumpInfo();
            BeanUtils.copyBean(insertJumpInfoParam, linkJumpInfo);
            snapshotVisualizationLinkJumpInfoMapper.insert(insertJumpInfoParam);
            Optional.ofNullable(linkJumpInfo.getTargetViewInfoList()).orElse(new ArrayList<>()).forEach(targetViewInfo -> {
                Long targetViewInfoId = IDUtils.snowID();
                targetViewInfo.setTargetId(targetViewInfoId);
                targetViewInfo.setLinkJumpInfoId(linkJumpInfoId);
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
        List<VisualizationLinkJumpDTO> result = null;
        if (CommonConstants.RESOURCE_TABLE.SNAPSHOT.equals(request.getResourceTable())) {
            result = extVisualizationLinkJumpMapper.getTargetVisualizationJumpInfoSnapshot(request);
        } else {
            result = extVisualizationLinkJumpMapper.getTargetVisualizationJumpInfo(request);
        }
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
        DataVisualizationInfo dvInfo = dataVisualizationInfoMapper.selectById(dvId);
        List<VisualizationViewTableVO> result;
        List<VisualizationOutParamsJumpVO> outParamsJumpInfo;
        String componentData;
        if (dvInfo != null) {
            result = extVisualizationLinkJumpMapper.getViewTableDetails(dvId).stream().filter(viewTableInfo -> dvInfo.getComponentData().indexOf(viewTableInfo.getId().toString()) > -1).collect(Collectors.toList());
            componentData = dvInfo.getComponentData();
            outParamsJumpInfo = extVisualizationLinkJumpMapper.queryOutParamsTargetWithDvId(dvId);
        } else {
            result = new ArrayList<>();
            outParamsJumpInfo = new ArrayList<>();
            componentData = "[]";
        }
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
        SnapshotCoreChartView coreChartView = new SnapshotCoreChartView();
        coreChartView.setId(Long.valueOf(request.getSourceViewId()));
        coreChartView.setJumpActive(request.getActiveStatus());
        snapshotCoreChartViewMapper.updateById(coreChartView);
        return queryVisualizationJumpInfo(request.getSourceDvId(), CommonConstants.RESOURCE_TABLE.SNAPSHOT);
    }

    /**
     * 删除跳转配置
     *
     * @param jumpDTO 跳转配置
     */
    @Override
    public void removeJumpSet(VisualizationLinkJumpDTO jumpDTO) {
        //清理原有数据
        extVisualizationLinkJumpMapper.deleteJumpTargetViewInfoSnapshot(jumpDTO.getSourceDvId(), jumpDTO.getSourceViewId());
        extVisualizationLinkJumpMapper.deleteJumpInfoSnapshot(jumpDTO.getSourceDvId(), jumpDTO.getSourceViewId());
        extVisualizationLinkJumpMapper.deleteJumpSnapshot(jumpDTO.getSourceDvId(), jumpDTO.getSourceViewId());
    }

}
