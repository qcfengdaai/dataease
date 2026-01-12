package io.dataease.visualization.dao.ext.mapper;


import io.dataease.api.dataset.vo.CoreDatasetGroupVO;
import io.dataease.api.visualization.dto.VisualizationOuterParamsDTO;
import io.dataease.api.visualization.dto.VisualizationOuterParamsInfoDTO;
import io.dataease.visualization.dao.auto.entity.SnapshotVisualizationOuterParamsInfo;
import io.dataease.visualization.dao.auto.entity.VisualizationOuterParamsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 可视化外部参数扩展Mapper
 * <p>
 * 提供可视化外部参数的扩展查询操作
 * <p>
 * 主要功能：
 * <ul>
 * <li>查询外部参数配置</li>
 * <li>删除外部参数相关数据</li>
 * <li>管理外部参数快照</li>
 * <li>查询数据集信息</li>
 * </ul>
 *
 * @author DataEase
 * @since 2024-01-12
 */
@Mapper
public interface ExtVisualizationOuterParamsMapper {

    /**
     * 根据可视化ID查询外部参数快照配置
     *
     * @param visualizationId 可视化ID
     * @return 外部参数配置
     */
    VisualizationOuterParamsDTO queryWithVisualizationIdSnapshot(@Param("visualizationId") String visualizationId);

    /**
     * 删除可视化外部参数目标配置
     *
     * @param visualizationId 可视化ID
     */
    void deleteOuterParamsTargetWithVisualizationId(@Param("visualizationId") String visualizationId);

    /**
     * 删除可视化外部参数信息
     *
     * @param visualizationId 可视化ID
     */
    void deleteOuterParamsInfoWithVisualizationId(@Param("visualizationId") String visualizationId);

    /**
     * 删除可视化外部参数配置
     *
     * @param visualizationId 可视化ID
     */
    void deleteOuterParamsWithVisualizationId(@Param("visualizationId") String visualizationId);

    /**
     * 删除可视化外部参数目标配置快照
     *
     * @param visualizationId 可视化ID
     */
    void deleteOuterParamsTargetWithVisualizationIdSnapshot(@Param("visualizationId") String visualizationId);

    /**
     * 删除可视化外部参数信息快照
     *
     * @param visualizationId 可视化ID
     */
    void deleteOuterParamsInfoWithVisualizationIdSnapshot(@Param("visualizationId") String visualizationId);

    /**
     * 删除可视化外部参数配置快照
     *
     * @param visualizationId 可视化ID
     */
    void deleteOuterParamsWithVisualizationIdSnapshot(@Param("visualizationId") String visualizationId);

    /**
     * 获取可视化外部参数信息
     *
     * @param visualizationId 可视化ID
     * @return 外部参数信息列表
     */
    List<VisualizationOuterParamsInfoDTO> getVisualizationOuterParamsInfo(@Param("visualizationId") String visualizationId);

    /**
     * 获取可视化外部参数基础信息
     *
     * @param visualizationId 可视化ID
     * @return 外部参数信息列表
     */
    List<SnapshotVisualizationOuterParamsInfo> getVisualizationOuterParamsInfoBase(@Param("visualizationId") String visualizationId);

    /**
     * 查询可视化关联的数据集
     *
     * @param visualizationId 可视化ID
     * @return 数据集列表
     */
    List<CoreDatasetGroupVO> queryDsWithVisualizationId(@Param("visualizationId") String visualizationId);
}
