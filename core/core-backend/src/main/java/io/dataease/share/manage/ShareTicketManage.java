package io.dataease.share.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.dataease.api.xpack.share.request.TicketCreator;
import io.dataease.api.xpack.share.request.TicketDelRequest;
import io.dataease.api.xpack.share.request.TicketSwitchRequest;
import io.dataease.api.xpack.share.vo.TicketVO;
import io.dataease.api.xpack.share.vo.TicketValidVO;
import io.dataease.commons.utils.CodingUtil;
import io.dataease.exception.DEException;
import io.dataease.share.dao.auto.entity.CoreShareTicket;
import io.dataease.share.dao.auto.entity.XpackShare;
import io.dataease.share.dao.auto.mapper.CoreShareTicketMapper;
import io.dataease.share.dao.auto.mapper.XpackShareMapper;
import io.dataease.share.dao.ext.mapper.XpackShareExtMapper;
import io.dataease.utils.AuthUtils;
import io.dataease.utils.BeanUtils;
import io.dataease.utils.CommonBeanFactory;
import io.dataease.utils.IDUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 分享Ticket业务管理类
 *
 * 负责处理分享链接的访问票据（Ticket）管理，包括：
 * <ul>
 * <li>Ticket的创建、删除、查询</li>
 * <li>Ticket有效期管理</li>
 * <li>Ticket验证和过期检查</li>
 * <li>分享链接的Ticket开关控制</li>
 * <li>Ticket的分页查询</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2024-06-21
 */
@Component
public class ShareTicketManage {

    @Resource
    private CoreShareTicketMapper coreShareTicketMapper;

    @Resource
    private XpackShareMapper xpackShareMapper;

    @Resource
    private XpackShareExtMapper xpackShareExtMapper;


    /**
     * 根据Ticket查询票据信息
     *
     * @param ticket Ticket票据
     * @return 票据信息，不存在则返回null
     */
    public CoreShareTicket getByTicket(String ticket) {
        QueryWrapper<CoreShareTicket> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ticket", ticket);
        return coreShareTicketMapper.selectOne(queryWrapper);
    }

    /**
     * 保存或更新Ticket票据
     * 如果Ticket已存在且要求生成新的，则删除旧的并生成新的
     * 如果Ticket已存在但不需要生成新的，则更新有效期和参数
     *
     * @param creator Ticket创建器
     * @return Ticket票据
     */
    @Transactional
    public String saveTicket(TicketCreator creator) {
        String ticket = creator.getTicket();
        // 如果提供了Ticket，先查询是否已存在
        if (StringUtils.isNotBlank(ticket)) {
            CoreShareTicket ticketEntity = getByTicket(ticket);
            if (ObjectUtils.isNotEmpty(ticketEntity)) {
                // 如果要求生成新的Ticket
                if (creator.isGenerateNew()) {
                    ticketEntity.setAccessTime(null);
                    ticketEntity.setTicket(CodingUtil.shortUuid());
                    // 删除旧记录并插入新记录
                    coreShareTicketMapper.deleteById(ticketEntity);
                    coreShareTicketMapper.insert(ticketEntity);
                    return ticketEntity.getTicket();
                }
                // 不需要生成新的，更新有效期和参数
                ticketEntity.setArgs(creator.getArgs());
                ticketEntity.setExp(creator.getExp());
                ticketEntity.setUuid(creator.getUuid());
                coreShareTicketMapper.deleteById(ticketEntity);
                coreShareTicketMapper.insert(ticketEntity);
                return ticketEntity.getTicket();
            }
        }
        // 如果未提供Ticket，生成一个新的
        if (StringUtils.isBlank(ticket)) {
            ticket = CodingUtil.shortUuid();
        }
        CoreShareTicket linkTicket = new CoreShareTicket();
        linkTicket.setId(IDUtils.snowID());
        linkTicket.setTicket(ticket);
        linkTicket.setArgs(creator.getArgs());
        linkTicket.setExp(creator.getExp());
        linkTicket.setUuid(creator.getUuid());
        // 使用代理对象调用以确保事务生效
        Objects.requireNonNull(CommonBeanFactory.proxy(this.getClass())).saveDao(linkTicket);
        return ticket;
    }

    /**
     * 保存Ticket到数据库
     * 此方法由代理对象调用以确保持久化
     *
     * @param ticket Ticket实体
     */
    public void saveDao(CoreShareTicket ticket) {
        coreShareTicketMapper.insert(ticket);
    }

    /**
     * 删除Ticket票据
     *
     * @param request 删除请求，包含要删除的Ticket
     */
    public void deleteTicket(TicketDelRequest request) {
        String ticket = request.getTicket();
        if (StringUtils.isBlank(ticket)) {
            DEException.throwException("ticket为必填参数");
        }
        QueryWrapper<CoreShareTicket> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ticket", ticket);
        coreShareTicketMapper.delete(queryWrapper);
    }

    /**
     * 切换分享链接的Ticket开关
     *
     * @param request Ticket开关请求
     */
    public void switchRequire(TicketSwitchRequest request) {
        String resourceId = request.getResourceId();
        Boolean require = request.getRequire();
        QueryWrapper<XpackShare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resource_id", resourceId);
        queryWrapper.eq("creator", AuthUtils.getUser().getUserId());
        XpackShare xpackShare = xpackShareMapper.selectOne(queryWrapper);
        xpackShare.setTicketRequire(require);
        xpackShareMapper.updateById(xpackShare);
    }

    /**
     * 分页查询资源的Ticket列表
     *
     * @param resourceId 资源ID
     * @param page 分页对象
     * @return Ticket分页结果
     */
    public IPage<TicketVO> query(Long resourceId, Page<TicketVO> page) {
        // 先查询资源的分享信息以获取UUID
        QueryWrapper<XpackShare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resource_id", resourceId);
        queryWrapper.eq("creator", AuthUtils.getUser().getUserId());
        XpackShare xpackShare = xpackShareMapper.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(xpackShare)) return null;
        String uuid = xpackShare.getUuid();
        if (StringUtils.isBlank(uuid)) return null;
        // 根据UUID查询所有Ticket
        QueryWrapper<CoreShareTicket> ticketQueryWrapper = new QueryWrapper<>();
        ticketQueryWrapper.eq("uuid", uuid);
        IPage<CoreShareTicket> pager = xpackShareExtMapper.pager(page, ticketQueryWrapper);
        List<CoreShareTicket> records = pager.getRecords();
        // 转换为VO对象
        IPage<TicketVO> iPage = new Page<>();
        iPage.setPages(pager.getPages());
        iPage.setTotal(pager.getTotal());
        iPage.setCurrent(pager.getCurrent());
        iPage.setSize(pager.getSize());
        List<TicketVO> vos = records.stream().map(record -> BeanUtils.copyBean(new TicketVO(), record)).toList();
        iPage.setRecords(vos);
        return iPage;
    }

    /**
     * 当分享UUID变更时，更新关联的Ticket
     *
     * @param originalUuid 原始UUID
     * @param newUuid 新UUID
     */
    @Transactional
    public void updateByUuidChange(String originalUuid, String newUuid) {
        xpackShareExtMapper.updateTicketUuid(originalUuid, newUuid);
    }

    /**
     * 根据分享UUID删除所有关联的Ticket
     *
     * @param uuid 分享UUID
     */
    @Transactional
    public void deleteByShare(String uuid) {
        QueryWrapper<CoreShareTicket> ticketQueryWrapper = new QueryWrapper<>();
        ticketQueryWrapper.eq("uuid", uuid);
        coreShareTicketMapper.delete(ticketQueryWrapper);
    }

    /**
     * 验证Ticket的有效性
     * 检查Ticket是否存在、是否过期等
     *
     * @param ticket Ticket票据
     * @param share 分享信息
     * @return Ticket验证结果
     */
    public TicketValidVO validateTicket(String ticket, XpackShare share) {
        TicketValidVO vo = new TicketValidVO();
        // 如果未提供Ticket，检查分享是否要求Ticket
        if (StringUtils.isBlank(ticket)) {
            vo.setTicketValid(!share.getTicketRequire());
            return vo;
        }
        // 查询Ticket信息
        CoreShareTicket linkTicket = getByTicket(ticket);
        if (ObjectUtils.isEmpty(linkTicket)) {
            vo.setTicketValid(false);
            return vo;
        }
        vo.setTicketValid(true);
        vo.setArgs(linkTicket.getArgs());
        Long accessTime = linkTicket.getAccessTime();
        long now = System.currentTimeMillis();
        // 如果是首次访问，记录访问时间
        if (ObjectUtils.isEmpty(accessTime)) {
            accessTime = now;
            vo.setTicketExp(false);
            linkTicket.setAccessTime(accessTime);
            coreShareTicketMapper.updateById(linkTicket);
            return vo;
        }
        // 检查Ticket是否过期
        Long exp = linkTicket.getExp();
        if (ObjectUtils.isEmpty(exp) || exp.equals(0L)) {
            vo.setTicketExp(false);
            return vo;
        }
        // 计算过期时间（分钟转换为毫秒）
        long expTime = exp * 60L * 1000L;
        long time = now - accessTime;
        vo.setTicketExp(time > expTime);
        return vo;
    }

    /**
     * 获取Ticket数量限制
     * 企业版可以扩展此方法以限制Ticket数量
     *
     * @return 限制数量，0表示不限制
     */
    public Integer getLimit() {
        return 0;
    }

    /**
     * 统计分享的Ticket数量
     *
     * @param uuid 分享UUID
     * @return Ticket数量
     */
    public long ticketCount(String uuid) {
        QueryWrapper<CoreShareTicket> ticketQueryWrapper = new QueryWrapper<>();
        ticketQueryWrapper.eq("uuid", uuid);
        return coreShareTicketMapper.selectCount(ticketQueryWrapper);
    }
}
