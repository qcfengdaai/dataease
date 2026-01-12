package io.dataease.system.dao.auto.mapper;

import io.dataease.system.dao.auto.entity.CoreTypeface;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统字体 Mapper 接口
 * <p>
 * 提供系统字体数据的数据库访问操作，继承 MyBatis Plus 的 BaseMapper
 * 支持字体信息的增删改查操作
 * </p>
 *
 * @author fit2cloud
 * @since 2024-08-08
 */
@Mapper
public interface CoreTypefaceMapper extends BaseMapper<CoreTypeface> {

}
