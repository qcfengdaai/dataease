package io.dataease.visualization.dao.ext.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dataease.visualization.dao.ext.po.VisualizationNodePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

/**
 * 可视化扩展Mapper
 * <p>
 * 提供可视化相关的扩展查询功能
 *
 * @author DataEase
 * @since 2024-06-21
 */
@Mapper
public interface CoreVisualiationExtMapper {

    /**
     * 查询节点列表
     *
     * @param queryWrapper 查询条件
     * @return 节点列表
     */
    @Select("""
            select id, name, pid, node_type, mobile_layout as extraFlag, status as extraFlag1 from data_visualization_info
            ${ew.customSqlSegment}
            """)
    List<VisualizationNodePO> queryNodes(@Param("ew") QueryWrapper<Object> queryWrapper);

    /**
     * 查询子节点ID列表
     *
     * @param pid 父节点ID
     * @return 子节点ID列表
     */
    @Select("select id from data_visualization_info where pid = #{pid} and delete_flag = 0")
    List<Long> queryChildrenId(@Param("pid") Long pid);

    /**
     * 批量删除仪表板（逻辑删除）
     *
     * @param ids  ID集合
     * @param time 删除时间
     * @param uid 删除用户ID
     */
    @Update("""
            <script>
            update data_visualization_info set delete_flag = 1, delete_time = #{time}, delete_by = #{uid} where id in
            <foreach item='id' index='index' collection='ids' open='(' separator=',' close=')'>
            #{id}
            </foreach>
            </script>
            """)
    void batchDel(@Param("ids") Set<Long> ids, @Param("time") Long time, @Param("uid") Long uid);
}
