package io.dataease.visualization.dao.ext.mapper;

import io.dataease.api.visualization.dto.LinkageInfoDTO;
import io.dataease.api.visualization.dto.VisualizationLinkageDTO;
import io.dataease.extensions.datasource.dto.DatasetTableFieldDTO;
import io.dataease.visualization.dao.auto.entity.VisualizationLinkage;
import io.dataease.visualization.dao.auto.entity.VisualizationLinkageField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 可视化联动扩展Mapper
 * <p>
 * 提供图表联动的扩展查询功能
 *
 * @author DataEase
 * @since 2024-06-21
 */
@Mapper
public interface ExtVisualizationLinkageMapper {

    /**
     * 获取视图联动信息集合
     *
     * @param dvId         仪表板ID
     * @param sourceViewId 源视图ID
     * @param targetViewIds 目标视图ID列表
     * @return 联动信息列表
     */
    List<VisualizationLinkageDTO> getViewLinkageGather(@Param("dvId") Long dvId, @Param("sourceViewId") Long sourceViewId, @Param("targetViewIds") List<String> targetViewIds);

    /**
     * 获取仪表板的所有联动信息
     *
     * @param dvId 仪表板ID
     * @return 联动信息列表
     */
    List<LinkageInfoDTO> getPanelAllLinkageInfo(@Param("dvId") Long dvId);

    /**
     * 获取镜像表的视图联动信息集合
     *
     * @param dvId         仪表板ID
     * @param sourceViewId 源视图ID
     * @param targetViewIds 目标视图ID列表
     * @return 联动信息列表
     */
    List<VisualizationLinkageDTO> getViewLinkageGatherSnapshot(@Param("dvId") Long dvId, @Param("sourceViewId") Long sourceViewId, @Param("targetViewIds") List<String> targetViewIds);

    /**
     * 获取镜像表的仪表板所有联动信息
     *
     * @param dvId 仪表板ID
     * @return 联动信息列表
     */
    List<LinkageInfoDTO> getPanelAllLinkageInfoSnapshot(@Param("dvId") Long dvId);

    /**
     * 查询表字段
     *
     * @param table_id 表ID
     * @return 字段列表
     */
    List<DatasetTableFieldDTO> queryTableField(@Param("table_id") Long tableId);

    /**
     * 根据视图ID查询表字段
     *
     * @param viewId 视图ID
     * @return 字段列表
     */
    List<DatasetTableFieldDTO> queryTableFieldWithViewId(@Param("viewId") Long viewId);

    /**
     * 删除视图联动
     *
     * @param dvId         仪表板ID
     * @param sourceViewId 源视图ID
     */
    void deleteViewLinkage(@Param("dvId") Long dvId,@Param("sourceViewId") Long sourceViewId);

    /**
     * 删除视图联动字段
     *
     * @param dvId         仪表板ID
     * @param sourceViewId 源视图ID
     */
    void deleteViewLinkageField(@Param("dvId") Long dvId,@Param("sourceViewId") Long sourceViewId);

    /**
     * 删除镜像表的视图联动
     *
     * @param dvId         仪表板ID
     * @param sourceViewId 源视图ID
     */
    void deleteViewLinkageSnapshot(@Param("dvId") Long dvId,@Param("sourceViewId") Long sourceViewId);

    /**
     * 删除镜像表的视图联动字段
     *
     * @param dvId         仪表板ID
     * @param sourceViewId 源视图ID
     */
    void deleteViewLinkageFieldSnapshot(@Param("dvId") Long dvId,@Param("sourceViewId") Long sourceViewId);

    /**
     * 复制视图联动
     *
     * @param copyId 复制批次ID
     */
    void copyViewLinkage(@Param("copyId") Long copyId);

    /**
     * 复制视图联动字段
     *
     * @param copyId 复制批次ID
     */
    void copyViewLinkageField(@Param("copyId") Long copyId);

    /**
     * 查找仪表板的联动配置
     *
     * @param dvId 仪表板ID
     * @return 联动配置列表
     */
    List<VisualizationLinkage> findLinkageWithDvId(@Param("dvId") Long dvId);

    /**
     * 查找仪表板的联动字段配置
     *
     * @param dvId 仪表板ID
     * @return 联动字段配置列表
     */
    List<VisualizationLinkageField> findLinkageFieldWithDvId(@Param("dvId") Long dvId);
}
