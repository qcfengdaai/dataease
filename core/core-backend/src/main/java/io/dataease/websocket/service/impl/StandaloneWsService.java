package io.dataease.websocket.service.impl;

import io.dataease.websocket.WsMessage;
import io.dataease.websocket.WsService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


/**
 * 单机版WebSocket服务实现
 *
 * 实现WsService接口,提供单机环境下的WebSocket消息推送功能
 * 使用Spring的SimpMessagingTemplate实现点对点消息推送
 *
 * 使用场景:
 * - 站内消息推送: 当系统发送站内消息时,通过WebSocket实时推送给用户
 * - 通知刷新: 通知前端刷新消息列表、仪表板等
 *
 * @author DataEase
 */
@Service
public class StandaloneWsService implements WsService {

    /**
     * Spring WebSocket消息模板
     * 用于发送STOMP格式的WebSocket消息
     */
    @Resource
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 发送WebSocket消息
     *
     * 向指定用户发送WebSocket消息
     * 使用点对点模式(/user前缀)将消息发送给特定用户
     *
     * @param wsMessage WebSocket消息对象,包含用户ID、主题和消息数据
     */
    public void releaseMessage(WsMessage wsMessage){
        // 参数校验: 消息对象、用户ID、主题都不能为空
        if(ObjectUtils.isEmpty(wsMessage) || ObjectUtils.isEmpty(wsMessage.getUserId()) ||  ObjectUtils.isEmpty(wsMessage.getTopic())) return;
        // 使用Spring的消息模板向指定用户发送消息
        // 消息路由格式: /user/{userId}/{topic}
        // 例如: /user/123/web-msg-topic
        messagingTemplate.convertAndSendToUser(String.valueOf(wsMessage.getUserId()), wsMessage.getTopic(),wsMessage.getData());
    }
}
