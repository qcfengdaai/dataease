package io.dataease.visualization.dao.ext.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dataease.visualization.dao.ext.po.StorePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 存储扩展Mapper
 * <p>
 * 提供存储相关的扩展查询功能
 *
 * @author DataEase
 * @since 2024-06-21
 */
@Mapper
public interface CoreStoreExtMapper {

    /**
     * 查询存储列表
     * <p>
     * 关联查询仪表板信息和存储信息
     *
     * @param page 分页对象
     * @param ew   查询条件
     * @return 存储信息列表
     */
    @Select("""
            select
            s.id as store_id,
            v.id as resource_id,
            v.type,
            v.create_by as creator,
            v.update_by as editor,
            v.update_time as edit_time,
            v.name,
            v.mobile_layout as ext_flag,
            v.status as ext_flag1
            from core_store s
            inner join data_visualization_info v on s.resource_id = v.id
            ${ew.customSqlSegment}
            """)
    IPage<StorePO> query(IPage<StorePO> page, @Param("ew") QueryWrapper<Object> ew);
}
