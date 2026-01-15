package io.dataease.websocket.factory;

import io.dataease.websocket.util.WsUtil;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.Optional;

/**
 * WebSocket处理器装饰器
 *
 * 继承Spring WebSocket的WebSocketHandlerDecorator类
 * 在WebSocket连接生命周期中管理用户在线状态
 *
 * 主要功能:
 * - 连接建立时: 将用户添加到在线用户集合
 * - 连接断开时: 将用户从在线用户集合移除
 *
 * @author DataEase
 */
public class DeWebSocketHandlerDecorator extends WebSocketHandlerDecorator {


    /**
     * 构造函数
     *
     * @param delegate 被装饰的原始WebSocket处理器
     */
    public DeWebSocketHandlerDecorator(WebSocketHandler delegate) {
        super(delegate);
    }


    /**
     * WebSocket连接建立后的回调方法
     *
     * 在用户成功建立WebSocket连接后,将用户添加到在线用户集合
     * 从Session的Principal中提取用户ID
     *
     * @param session WebSocket会话对象
     * @throws Exception 如果处理过程中发生异常
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 获取Session中的用户主体信息
        Optional.ofNullable(session.getPrincipal()).ifPresent(principal -> {
            // 提取用户名称(实际是用户ID)
            String name = principal.getName();
            if(name != null){
                // 将字符串转换为Long类型的用户ID
                Long userId = Long.parseLong(name);
                // 将用户设置为在线状态
                WsUtil.onLine(userId);
            }
        });
        // 调用父类方法完成后续处理
        super.afterConnectionEstablished(session);
    }

    /**
     * WebSocket连接关闭后的回调方法
     *
     * 在用户WebSocket连接断开后,将用户从在线用户集合移除
     * 从Session的Principal中提取用户ID
     *
     * @param session WebSocket会话对象
     * @param closeStatus 连接关闭状态
     * @throws Exception 如果处理过程中发生异常
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        // 获取Session中的用户主体信息
        Optional.ofNullable(session.getPrincipal()).ifPresent(principal -> {
            // 提取用户名称(实际是用户ID)
            String name = principal.getName();
            if(name != null){
                // 将字符串转换为Long类型的用户ID
                Long userId = Long.parseLong(name);
                // 将用户设置为离线状态
                WsUtil.offLine(userId);
            }
        });

        // 调用父类方法完成后续处理
        super.afterConnectionClosed(session, closeStatus);
    }

}
