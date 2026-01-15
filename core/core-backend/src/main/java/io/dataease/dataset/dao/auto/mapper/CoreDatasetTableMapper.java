package io.dataease.dataset.dao.auto.mapper;

import io.dataease.dataset.dao.auto.entity.CoreDatasetTable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据集表数据访问层接口
 * 提供对 core_dataset_table 表的基础 CRUD 操作
 * 继承 MyBatis Plus 的 BaseMapper，获得通用的数据库操作方法
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>数据集中表的增删改查操作</li>
 *   <li>支持按数据源、数据集等维度查询</li>
 *   <li>不同类型表（db/sql/union等）的统一管理</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2023-04-14
 */
@Mapper
public interface CoreDatasetTableMapper extends BaseMapper<CoreDatasetTable> {

}
