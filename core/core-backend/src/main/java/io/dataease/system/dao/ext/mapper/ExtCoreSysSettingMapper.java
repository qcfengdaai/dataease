package io.dataease.system.dao.ext.mapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataease.system.dao.auto.entity.CoreSysSetting;
import io.dataease.system.dao.auto.mapper.CoreSysSettingMapper;
import org.springframework.stereotype.Component;

/**
 * 系统设置扩展 Mapper
 * <p>
 * 提供系统设置相关的扩展功能，继承 MyBatis Plus 的 ServiceImpl
 * 实现批量保存等增强功能
 * </p>
 */
@Component("extCoreSysSettingMapper")
public class ExtCoreSysSettingMapper extends ServiceImpl<CoreSysSettingMapper, CoreSysSetting> {
}
