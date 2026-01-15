package io.dataease.system.dao.auto.mapper;

import io.dataease.system.dao.auto.entity.CoreSysSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统设置 Mapper 接口
 * <p>
 * 提供系统设置数据的数据库访问操作，继承 MyBatis Plus 的 BaseMapper
 * 支持系统参数配置的增删改查操作
 * </p>
 *
 * @author fit2cloud
 * @since 2023-10-27
 */
@Mapper
public interface CoreSysSettingMapper extends BaseMapper<CoreSysSetting> {

}
