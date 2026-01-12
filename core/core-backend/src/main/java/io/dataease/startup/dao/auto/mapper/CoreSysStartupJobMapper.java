package io.dataease.startup.dao.auto.mapper;

import io.dataease.startup.dao.auto.entity.CoreSysStartupJob;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目启动任务 Mapper 接口
 * <p>
 * 提供项目启动任务表的数据访问操作,继承自 MyBatis Plus 的 BaseMapper
 * </p>
 *
 * @author fit2cloud
 * @since 2024-05-15
 */
@Mapper
public interface CoreSysStartupJobMapper extends BaseMapper<CoreSysStartupJob> {

}
