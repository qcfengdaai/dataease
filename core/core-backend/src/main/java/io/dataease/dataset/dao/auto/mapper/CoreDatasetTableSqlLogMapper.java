package io.dataease.dataset.dao.auto.mapper;

import io.dataease.dataset.dao.auto.entity.CoreDatasetTableSqlLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据集表SQL日志数据访问层接口
 * 提供对 core_dataset_table_sql_log 表的基础 CRUD 操作
 * 继承 MyBatis Plus 的 BaseMapper，获得通用的数据库操作方法
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>SQL执行日志的增删改查操作</li>
 *   <li>支持按表ID、执行时间等查询</li>
 *   <li>SQL性能监控和统计分析</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2023-08-08
 */
@Mapper
public interface CoreDatasetTableSqlLogMapper extends BaseMapper<CoreDatasetTableSqlLog> {

}
