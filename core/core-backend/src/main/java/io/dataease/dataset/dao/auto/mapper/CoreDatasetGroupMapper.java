package io.dataease.dataset.dao.auto.mapper;

import io.dataease.dataset.dao.auto.entity.CoreDatasetGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据集分组表数据访问层接口
 * 提供对 core_dataset_group 表的基础 CRUD 操作
 * 继承 MyBatis Plus 的 BaseMapper，获得通用的数据库操作方法
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>数据集分组的增删改查操作</li>
 *   <li>支持条件查询和分页查询</li>
 *   <li>树形结构数据的层次查询</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2025-04-14
 */
@Mapper
public interface CoreDatasetGroupMapper extends BaseMapper<CoreDatasetGroup> {

}
