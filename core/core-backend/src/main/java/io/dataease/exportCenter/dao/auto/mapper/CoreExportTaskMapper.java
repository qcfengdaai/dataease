package io.dataease.exportCenter.dao.auto.mapper;

import io.dataease.exportCenter.dao.auto.entity.CoreExportTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 导出任务Mapper接口
 * 提供导出任务实体的数据库访问功能，继承MyBatis Plus的基础CRUD操作
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>继承自BaseMapper，提供基础的增删改查操作</li>
 *   <li>支持导出任务信息的条件查询和分页查询</li>
 *   <li>提供批量操作和事务支持</li>
 * </ul>
 *
 * <p>主要支持的操作：</p>
 * <ul>
 *   <li>insert - 插入新导出任务记录</li>
 *   <li>selectById - 根据任务ID查询导出任务信息</li>
 *   <li>selectList - 根据条件查询导出任务列表</li>
 *   <li>updateById - 根据ID更新导出任务状态和进度</li>
 *   <li>deleteById - 根据ID删除导出任务记录</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2024-06-12
 */
@Mapper
public interface CoreExportTaskMapper extends BaseMapper<CoreExportTask> {

}
