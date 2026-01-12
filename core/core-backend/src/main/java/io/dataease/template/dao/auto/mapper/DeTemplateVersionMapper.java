package io.dataease.template.dao.auto.mapper;

import io.dataease.template.dao.auto.entity.DeTemplateVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 模板版本 Mapper 接口
 * 提供模板版本表的数据库访问操作
 *
 * @author fit2cloud
 * @since 2024-05-07
 */
@Mapper
public interface DeTemplateVersionMapper extends BaseMapper<DeTemplateVersion> {

}
