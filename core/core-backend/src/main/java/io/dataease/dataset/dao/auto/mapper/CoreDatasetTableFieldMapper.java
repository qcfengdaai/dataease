package io.dataease.dataset.dao.auto.mapper;

import io.dataease.dataset.dao.auto.entity.CoreDatasetTableField;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据集表字段数据访问层接口
 * 提供对 core_dataset_table_field 表的基础 CRUD 操作
 * 继承 MyBatis Plus 的 BaseMapper，获得通用的数据库操作方法
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>数据集表字段的增删改查操作</li>
 *   <li>支持按表ID、数据集ID等查询字段</li>
 *   <li>字段元数据和展示配置管理</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2025-11-26
 */
@Mapper
public interface CoreDatasetTableFieldMapper extends BaseMapper<CoreDatasetTableField> {

}
