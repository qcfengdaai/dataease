package io.dataease.share.server;

import io.dataease.api.visualization.request.VisualizationWorkbranchQueryRequest;
import io.dataease.api.xpack.share.XpackShareApi;
import io.dataease.api.xpack.share.request.*;
import io.dataease.api.xpack.share.vo.XpackShareGridVO;
import io.dataease.api.xpack.share.vo.XpackShareProxyVO;
import io.dataease.api.xpack.share.vo.XpackShareVO;
import io.dataease.utils.BeanUtils;
import io.dataease.share.dao.auto.entity.XpackShare;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Xpack分享服务控制器
 *
 * 实现XpackShareApi接口，提供分享相关的REST API
 * <p>主要功能包括：</p>
 * <ul>
 * <li>分享链接的创建、删除、查询</li>
 * <li>分享配置的编辑（UUID、过期时间、密码）</li>
 * <li>分享列表查询</li>
 * <li>分享链接的访问代理和验证</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2024-06-21
 */
@RequestMapping("/share")
@RestController
public class XpackShareServer implements XpackShareApi {

    @Resource(name = "xpackShareManage")
    private XpackShareManage xpackShareManage;

    /**
     * 查询资源的分享状态
     *
     * @param resourceId 资源ID
     * @return true-已分享，false-未分享
     */
    @Override
    public boolean status(Long resourceId) {
        return ObjectUtils.isNotEmpty(xpackShareManage.queryByResource(resourceId));
    }

    /**
     * 切换资源的分享状态
     *
     * @param resourceId 资源ID
     */
    @Override
    public void switcher(Long resourceId) {
        xpackShareManage.switcher(resourceId);
    }

    /**
     * 编辑分享链接的过期时间
     *
     * @param request 过期时间编辑请求
     */
    @Override
    public void editExp(XpackShareExpRequest request) {
        xpackShareManage.editExp(request.getResourceId(), request.getExp());
    }

    /**
     * 编辑分享链接的密码
     *
     * @param request 密码编辑请求
     */
    @Override
    public void editPwd(XpackSharePwdRequest request) {
        xpackShareManage.editPwd(request.getResourceId(), request.getPwd(), request.getAutoPwd());
    }

    /**
     * 查询资源的分享详情
     *
     * @param resourceId 资源ID
     * @return 分享详情，未分享则返回null
     */
    @Override
    public XpackShareVO detail(Long resourceId) {
        XpackShare xpackShare = xpackShareManage.queryByResource(resourceId);
        if (ObjectUtils.isEmpty(xpackShare)) return null;
        return BeanUtils.copyBean(new XpackShareVO(), xpackShare);
    }

    /**
     * 查询分享列表
     *
     * @param request 查询请求参数
     * @return 分享列表
     */
    @Override
    public List<XpackShareGridVO> query(VisualizationWorkbranchQueryRequest request) {
        return xpackShareManage.query(1, 20, request).getRecords();
    }

    /**
     * 获取分享代理信息
     * 验证分享链接并生成访问Token
     *
     * @param request 分享代理请求
     * @return 分享代理信息
     */
    @Override
    public XpackShareProxyVO proxyInfo(XpackShareProxyRequest request) {
        return xpackShareManage.proxyInfo(request);
    }

    /**
     * 验证分享密码
     *
     * @param validator 密码验证器
     * @return true-密码正确，false-密码错误
     */
    @Override
    public boolean validatePwd(XpackSharePwdValidator validator) {
        return xpackShareManage.validatePwd(validator);
    }

    /**
     * 查询用户的所有分享关系
     *
     * @param uid 用户ID
     * @return 资源ID->UUID的映射
     */
    @Override
    public Map<String, String> queryRelationByUserId(Long uid) {
        return xpackShareManage.queryRelationByUserId(uid);
    }

    /**
     * 编辑分享链接的UUID
     *
     * @param editor UUID编辑器
     * @return 错误信息，成功返回空字符串
     */
    @Override
    public String editUuid(XpackShareUuidEditor editor) {
        return xpackShareManage.editUuid(editor);
    }
}
