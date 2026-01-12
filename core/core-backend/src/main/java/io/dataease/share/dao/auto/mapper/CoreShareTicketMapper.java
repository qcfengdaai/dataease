package io.dataease.share.dao.auto.mapper;

import io.dataease.share.dao.auto.entity.CoreShareTicket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分享Ticket Mapper接口
 *
 * 提供core_share_ticket表的基础CRUD操作
 * 继承MyBatis Plus的BaseMapper，自动拥有常用的增删改查方法
 *
 * @author fit2cloud
 * @since 2024-06-21
 */
@Mapper
public interface CoreShareTicketMapper extends BaseMapper<CoreShareTicket> {

}
