package io.dataease.websocket.factory;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

/**
 * WebSocket处理器装饰器工厂
 *
 * 实现Spring WebSocket的WebSocketHandlerDecoratorFactory接口
 * 用于创建自定义的WebSocket处理器装饰器,在WebSocket连接建立和断开时执行额外逻辑
 *
 * 主要功能:
 * - 在用户连接时将用户添加到在线用户集合
 * - 在用户断开时将用户从在线用户集合移除
 *
 * @author DataEase
 */
public class DeWsHandlerFactory implements WebSocketHandlerDecoratorFactory {


    /**
     * 装饰WebSocket处理器
     *
     * 创建自定义的DeWebSocketHandlerDecorator包装原始处理器
     * 添加连接建立和断开时的用户在线状态管理逻辑
     *
     * @param webSocketHandler 原始WebSocket处理器
     * @return 装饰后的WebSocket处理器
     */
    @Override
    public WebSocketHandler decorate(WebSocketHandler webSocketHandler) {
        return new DeWebSocketHandlerDecorator(webSocketHandler);
    }
}
