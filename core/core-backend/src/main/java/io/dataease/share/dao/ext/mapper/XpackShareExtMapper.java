package io.dataease.share.dao.ext.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dataease.api.xpack.share.vo.TicketVO;
import io.dataease.share.dao.auto.entity.CoreShareTicket;
import io.dataease.share.dao.ext.po.XpackSharePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Xpack分享扩展Mapper接口
 *
 * 提供扩展的自定义查询方法，处理复杂的多表关联查询
 */
@Mapper
public interface XpackShareExtMapper {

    /**
     * 分页查询分享信息
     * 关联xpack_share和data_visualization_info表
     *
     * @param page 分页对象
     * @param ew 查询条件包装器
     * @return 分页结果
     */
    @Select("""
            select
            s.id as share_id,
            v.id as resource_id,
            v.mobile_layout as ext_flag,
            v.status as ext_flag1,
            v.type,
            s.creator,
            s.time,
            s.exp,
            v.name
            from xpack_share s
            left join data_visualization_info v on s.resource_id = v.id
            ${ew.customSqlSegment}
            """)
    IPage<XpackSharePO> query(IPage<XpackSharePO> page, @Param("ew") QueryWrapper<Object> ew);

    /**
     * 查询可视化资源类型
     *
     * @param id 资源ID
     * @return 资源类型（panel或screen）
     */
    @Select("select type from data_visualization_info where id = #{id}")
    String visualizationType(@Param("id") Long id);

    /**
     * 更新Ticket的UUID
     * 当分享链接的UUID变更时，同步更新关联的Ticket记录
     *
     * @param originUuid 原始UUID
     * @param ticketUuid 新的UUID
     */
    @Update("update core_share_ticket set uuid = #{ticketUuid} where uuid = #{originUuid}")
    void updateTicketUuid(@Param("originUuid") String originUuid, @Param("ticketUuid") String ticketUuid);

    /**
     * 分页查询Ticket信息
     *
     * @param page 分页对象
     * @param ew 查询条件包装器
     * @return 分页结果
     */
    @Select("""
           select * from core_share_ticket
            ${ew.customSqlSegment}
           """)
    IPage<CoreShareTicket> pager(IPage<TicketVO> page, @Param("ew") QueryWrapper<CoreShareTicket> ew);
}
