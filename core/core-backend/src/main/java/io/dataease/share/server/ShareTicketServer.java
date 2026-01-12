package io.dataease.share.server;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.dataease.api.xpack.share.ShareTicketApi;
import io.dataease.api.xpack.share.request.TicketCreator;
import io.dataease.api.xpack.share.request.TicketDelRequest;
import io.dataease.api.xpack.share.request.TicketSwitchRequest;
import io.dataease.api.xpack.share.vo.TicketVO;
import io.dataease.commons.utils.CodingUtil;
import io.dataease.share.manage.ShareTicketManage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分享Ticket服务控制器
 *
 * 实现ShareTicketApi接口，提供分享Ticket相关的REST API
 * <p>主要功能包括：</p>
 * <ul>
 * <li>Ticket的创建和删除</li>
 * <li>Ticket开关控制</li>
 * <li>Ticket列表的分页查询</li>
 * <li>临时Ticket生成</li>
 * <li>Ticket数量限制查询</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2024-06-21
 */
@RestController
@RequestMapping("/ticket")
public class ShareTicketServer implements ShareTicketApi {

    @Resource
    private ShareTicketManage shareTicketManage;

    /**
     * 保存或更新Ticket
     *
     * @param creator Ticket创建器
     * @return Ticket票据
     */
    @Override
    public String saveTicket(TicketCreator creator) {
        return shareTicketManage.saveTicket(creator);
    }

    /**
     * 删除Ticket
     *
     * @param request 删除请求
     */
    @Override
    public void deleteTicket(TicketDelRequest request) {
        shareTicketManage.deleteTicket(request);
    }

    /**
     * 切换Ticket开关
     *
     * @param request Ticket开关请求
     */
    @Override
    public void switchRequire(TicketSwitchRequest request) {
        shareTicketManage.switchRequire(request);
    }

    /**
     * 分页查询Ticket列表
     *
     * @param resourceId 资源ID
     * @param goPage 当前页码
     * @param pageSize 每页大小
     * @return Ticket分页结果
     */
    @Override
    public IPage<TicketVO> pager(Long resourceId, int goPage, int pageSize) {
        Page<TicketVO> page = new Page<>(goPage, pageSize);
        return shareTicketManage.query(resourceId, page);
    }

    /**
     * 生成临时Ticket
     * 用于前端预览或测试
     *
     * @return 临时Ticket票据
     */
    @Override
    public String tempTicket() {
        return CodingUtil.shortUuid();
    }

    /**
     * 获取Ticket数量限制
     *
     * @return 限制数量，0表示不限制
     */
    @Override
    public Integer limit() {
        return shareTicketManage.getLimit();
    }
}
