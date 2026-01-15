package io.dataease.chart.dao.ext.mapper;

import io.dataease.api.chart.vo.ViewSelectorVO;
import io.dataease.api.dataset.vo.DataSQLBotDatasetVO;
import io.dataease.chart.dao.auto.entity.CoreChartView;
import io.dataease.chart.dao.ext.entity.ChartBasePO;
import io.dataease.extensions.view.dto.ChartViewDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 图表视图扩展数据访问层接口
 * 提供对图表视图的复杂查询操作，包括跨表查询、自定义SQL等
 * 通过 MyBatis 注解方式定义SQL语句
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>查询视图选择器列表（仪表板视图选择）</li>
 *   <li>图表详情查询（支持核心表和快照表）</li>
 *   <li>场景相关的批量查询和删除</li>
 *   <li>数据集和图表关联查询</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2024-12-12
 */
@Mapper
public interface ExtChartViewMapper {

    @Select("""
            select id, scene_id as pid, title, type from core_chart_view where type != 'VQuery' and scene_id = #{resourceId}
            """)
    List<ViewSelectorVO> queryViewOption(@Param("resourceId") Long resourceId);

    ChartBasePO queryChart(@Param("id") Long id, @Param("resourceTable")String resourceTable);

    List<CoreChartView> selectListCustom(@Param("sceneId") Long sceneId, @Param("resourceTable") String resourceTable);

    void deleteViewsBySceneId(@Param("sceneId") Long sceneId, @Param("resourceTable") String resourceTable);

    @Select("""
            SELECT id, scene_id as pid, title, type FROM (
                SELECT id, scene_id, title, type FROM core_chart_view 
                WHERE id = #{viewId}
                UNION ALL
                SELECT id, scene_id, title, type FROM snapshot_core_chart_view 
                WHERE id = #{viewId} 
            ) combined_views
            LIMIT 1
            """)
    ChartViewDTO findChartViewAround(@Param("viewId") String viewId);


    @Select("""
            select DISTINCT table_id from core_chart_view_snapshot where scene_id=#{dvId}
            """)
    List<Long> findDatasetGroupIdByDvId(@Param("dvId") String dvId);


    /**
     * 根据仪表板ID查找数据SQL机器人所需的数据集信息
     * 查询仪表板使用的数据集、数据源等关联信息，用于SQL机器人功能
     *
     * @param dvId 仪表板ID
     * @return 数据集和数据源信息列表
     */
    @Select("""
            SELECT
             DISTINCT
            	sdg.id AS table_id,
            	sdg.NAME AS table_name,
            	cd.id AS ds_id,
            	cd.NAME AS ds_name\s
            FROM
            	core_dataset_table sdt
            	INNER JOIN core_datasource cd ON sdt.datasource_id = cd.id
            	INNER JOIN core_dataset_group sdg ON sdt.dataset_group_id = sdg.id
            	INNER JOIN snapshot_core_chart_view sccv on  sccv.table_id = sdt.dataset_group_id\s
            WHERE
            	sccv.scene_id = #{dvId}
            """)
    List<DataSQLBotDatasetVO> findDataSQLBotDatasetDvId(@Param("dvId") String dvId);


}
