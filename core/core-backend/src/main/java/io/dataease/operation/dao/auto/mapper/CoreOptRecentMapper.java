package io.dataease.operation.dao.auto.mapper;

import io.dataease.operation.dao.auto.entity.CoreOptRecent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 核心操作记录 Mapper 接口
 * 提供对 core_opt_recent 表的数据库操作
 * 继承 MyBatis Plus 的 BaseMapper,包含基础 CRUD 方法
 * </p>
 *
 * @author fit2cloud
 * @since 2023-11-26
 */
@Mapper
public interface CoreOptRecentMapper extends BaseMapper<CoreOptRecent> {

}
