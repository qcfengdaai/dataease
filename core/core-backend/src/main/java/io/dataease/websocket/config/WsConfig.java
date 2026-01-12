package io.dataease.websocket.config;

import io.dataease.websocket.factory.DeWsHandlerFactory;
import io.dataease.websocket.handler.PrincipalHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * WebSocket配置类
 *
 * 配置基于STOMP协议的WebSocket消息代理
 * 支持点对点(/user)和广播(/topic)两种消息模式
 * 集成SockJS提供降级支持,当浏览器不支持WebSocket时自动降级
 *
 * @author DataEase
 */
@Configuration
@EnableWebSocketMessageBroker
public class WsConfig implements WebSocketMessageBrokerConfigurer {


    /**
     * 注册STOMP端点
     *
     * 配置WebSocket连接端点,设置跨域支持、握手处理器和SockJS降级
     *
     * @param registry STOMP端点注册器
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                .setAllowedOriginPatterns("*") // 允许所有来源的跨域请求
                .setHandshakeHandler(new PrincipalHandshakeHandler()) // 使用自定义握手处理器
                .withSockJS(); // 启用SockJS降级支持
    }

    /**
     * 配置消息代理
     *
     * 配置消息路由前缀,启用简单消息代理
     * - /topic: 广播模式,消息发送给所有订阅该主题的用户
     * - /user: 点对点模式,消息发送给指定用户
     *
     * @param registry 消息代理注册器
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用简单消息代理,支持/topic和/user前缀的消息
        registry.enableSimpleBroker("/topic", "/user");
        // 设置用户目标前缀,用于点对点消息推送
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * 配置WebSocket传输
     *
     * 配置消息处理器装饰器工厂和消息传输参数限制
     *
     * @param registry WebSocket传输注册器
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        // 添加自定义WebSocket处理器装饰器工厂
        registry.addDecoratorFactory(new DeWsHandlerFactory());
        // 设置消息大小限制为8KB
        registry.setMessageSizeLimit(8192) // 设置消息字节数大小
                .setSendBufferSizeLimit(8192)// 设置消息缓存大小
                .setSendTimeLimit(10000); // 设置消息发送时间限制毫秒
    }
}
