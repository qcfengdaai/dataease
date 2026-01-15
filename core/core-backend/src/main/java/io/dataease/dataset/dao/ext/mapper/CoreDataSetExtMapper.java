package io.dataease.dataset.dao.ext.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dataease.api.dataset.vo.DataSetBarVO;
import io.dataease.dataset.dao.ext.po.DataSetNodePO;
import io.dataease.model.BusiNodeRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 数据集扩展查询映射接口
 * 提供数据集相关的自定义查询功能，支持复杂的业务查询需求
 * 包含数据集节点查询和详细信息查询等功能
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>数据集节点的条件查询</li>
 *   <li>数据集详情信息的获取</li>
 *   <li>支持动态SQL查询条件</li>
 * </ul>
 *
 * @author Junjun
 */
@Mapper
public interface CoreDataSetExtMapper {

    /**
     * 查询数据集节点信息
     * 根据动态查询条件获取数据集节点的基本信息
     *
     * @param queryWrapper 查询条件包装器
     * @return 数据集节点信息列表
     */
    @Select("""
            select id, name, node_type, pid from core_dataset_group
            ${ew.customSqlSegment}
            """)
    List<DataSetNodePO> query(@Param("ew") QueryWrapper queryWrapper);

    /**
     * 查询数据集详细信息
     * 获取指定数据集的详细元数据信息，用于展示栏信息
     *
     * @param id 数据集ID
     * @return 数据集详细信息
     */
    @Select("select id, name, node_type, create_by, create_time, update_by, last_update_time, is_cross from core_dataset_group where id = #{id}")
    DataSetBarVO queryBarInfo(@Param("id") Long id);
}
