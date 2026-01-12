package io.dataease.share.dao.auto.mapper;

import io.dataease.share.dao.auto.entity.XpackShare;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Xpack分享Mapper接口
 *
 * 提供xpack_share表的基础CRUD操作
 * 继承MyBatis Plus的BaseMapper，自动拥有常用的增删改查方法
 *
 * @author fit2cloud
 * @since 2024-06-21
 */
@Mapper
public interface XpackShareMapper extends BaseMapper<XpackShare> {

}
