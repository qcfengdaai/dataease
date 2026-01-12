package io.dataease.websocket.handler;

import io.dataease.websocket.entity.DePrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * WebSocket握手处理器
 *
 * 继承Spring WebSocket的DefaultHandshakeHandler类
 * 在WebSocket握手阶段确定用户身份
 *
 * 主要功能:
 * - 从HTTP请求参数中提取userId
 * - 创建DePrincipal对象作为用户主体
 * - 将用户信息关联到WebSocket会话
 *
 * 使用场景:
 * - 前端连接WebSocket时需要在URL中携带userId参数
 * - 例如: /websocket?userId=123
 *
 * @author DataEase
 */
public class PrincipalHandshakeHandler extends DefaultHandshakeHandler {

    /**
     * 确定用户主体
     *
     * 在WebSocket握手阶段,从HTTP请求参数中提取userId
     * 创建DePrincipal对象并将其关联到WebSocket会话
     *
     * @param request HTTP请求对象
     * @param wsHandler WebSocket处理器
     * @param attributes 握手属性
     * @return 用户主体对象,如果userId为空则返回null
     */
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // 检查请求类型是否为ServletServerHttpRequest
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletServerHttpRequest.getServletRequest();
            // 从请求参数中获取userId
            final String userId = httpRequest.getParameter("userId");
            // 如果userId为空则返回null
            if (StringUtils.isEmpty(userId)) {
                return null;
            }
            // 创建DePrincipal对象并返回
            return new DePrincipal(userId);
        }
        return null;
    }
}
