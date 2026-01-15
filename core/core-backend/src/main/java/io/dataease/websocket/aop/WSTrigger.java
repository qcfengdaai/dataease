package io.dataease.websocket.aop;

import io.dataease.websocket.WsMessage;
import io.dataease.websocket.WsService;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * WebSocket触发器
 *
 * 通过AOP切面监听站内消息发送事件,在消息发送成功后触发WebSocket推送
 * 用于实现实时消息推送功能,当系统发送站内消息时自动推送给前端
 *
 * @author DataEase
 */
@Aspect
@Component
public class WSTrigger {

    @Autowired
    private WsService wsService;

    /**
     * 切面方法：在站内消息发送成功后触发WebSocket推送
     *
     * 拦截SendStation.sendMsg方法的返回,提取用户ID并向该用户推送WebSocket消息
     * 通知前端刷新消息列表
     *
     * @param point 切入点信息,包含方法参数等元数据
     */
    @AfterReturning(value = "execution(* io.dataease.service.message.service.strategy.SendStation.sendMsg(..))")
    public void after(JoinPoint point) {
        // 获取方法参数
        Object[] args = point.getArgs();
        Optional.ofNullable(args).ifPresent(objs -> {
            // 如果参数为空则直接返回
            if (ArrayUtils.isEmpty(objs)) return;
            // 提取第一个参数作为用户ID
            Object arg = args[0];
            Long userId = (Long) arg;
            // 构造WebSocket消息,目标主题为/web-msg-topic,消息内容为refresh(刷新)
            WsMessage message = new WsMessage(userId, "/web-msg-topic", "refresh");
            // 发送WebSocket消息
            wsService.releaseMessage(message);
        });

    }
}
