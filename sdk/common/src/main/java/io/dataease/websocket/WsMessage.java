package io.dataease.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * WebSocket消息实体类
 * <p>
 * 用于封装通过WebSocket发送给前端的消息，支持泛型数据类型。
 * 采用订阅-发布模式，前端订阅指定topic，后端向该topic发送消息。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>点对点消息推送 - 通过userId指定消息接收者</li>
 *   <li>主题订阅 - 通过topic区分不同类型的消息</li>
 *   <li>泛型数据 - 支持任意类型的消息内容</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.exportCenter.manage.ExportCenterDownLoadManage#checkRunningTask()} - 导出任务完成时通知前端</li>
 *   <li>{@link io.dataease.websocket.aop.WSTrigger#after(org.aspectj.lang.JoinPoint)} - 站内消息发送后通知用户刷新</li>
 * </ul>
 *
 * <p><b>常用Topic列表：</b></p>
 * <ul>
 *   <li><code>/task-export-topic</code> - 导出任务状态更新</li>
 *   <li><code>/web-msg-topic</code> - 站内消息通知</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：发送导出任务完成通知
 * ExportTaskDTO exportTaskDTO = new ExportTaskDTO();
 * exportTaskDTO.setId("task123");
 * exportTaskDTO.setStatus("SUCCESS");
 * exportTaskDTO.setProgress("100");
 *
 * WsMessage&lt;ExportTaskDTO&gt; message = new WsMessage&lt;&gt;(
 *     userId,                    // 接收消息的用户ID
 *     "/task-export-topic",      // 消息主题
 *     exportTaskDTO              // 消息数据
 * );
 * wsService.releaseMessage(message);
 *
 * // 示例2：发送站内消息刷新通知
 * WsMessage&lt;String&gt; msgNotice = new WsMessage&lt;&gt;(
 *     userId,
 *     "/web-msg-topic",
 *     "refresh"                  // 简单的刷新标识
 * );
 * wsService.releaseMessage(msgNotice);
 *
 * // 示例3：发送复杂数据对象
 * Map&lt;String, Object&gt; customData = new HashMap&lt;&gt;();
 * customData.put("action", "update");
 * customData.put("resourceId", 123L);
 * customData.put("timestamp", System.currentTimeMillis());
 *
 * WsMessage&lt;Map&lt;String, Object&gt;&gt; customMessage = new WsMessage&lt;&gt;(
 *     userId,
 *     "/custom-topic",
 *     customData
 * );
 * wsService.releaseMessage(customMessage);
 * </pre>
 *
 * <p><b>前端订阅示例（JavaScript/TypeScript）：</b></p>
 * <pre>
 * import SockJS from 'sockjs-client'
 * import Stomp from 'stompjs'
 *
 * // 建立WebSocket连接
 * const socket = new SockJS('/ws-endpoint')
 * const stompClient = Stomp.over(socket)
 *
 * stompClient.connect({}, () => {
 *   // 订阅导出任务通知
 *   stompClient.subscribe('/user/task-export-topic', (message) => {
 *     const taskData = JSON.parse(message.body)
 *     console.log('导出任务更新:', taskData)
 *     // 更新任务列表UI
 *     updateTaskList(taskData)
 *   })
 *
 *   // 订阅站内消息通知
 *   stompClient.subscribe('/user/web-msg-topic', (message) => {
 *     const action = JSON.parse(message.body)
 *     if (action === 'refresh') {
 *       // 刷新消息列表
 *       loadMessageList()
 *     }
 *   })
 * })
 * </pre>
 *
 * <p><b>消息流转过程：</b></p>
 * <ol>
 *   <li>后端创建WsMessage对象，指定userId、topic和data</li>
 *   <li>调用WsService.releaseMessage(message)发送消息</li>
 *   <li>Spring WebSocket将消息推送到指定用户的topic</li>
 *   <li>前端订阅的回调函数接收消息</li>
 *   <li>前端解析消息内容并更新UI</li>
 * </ol>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>userId必须是有效的用户ID，用于点对点推送</li>
 *   <li>topic必须与前端订阅的主题保持一致</li>
 *   <li>data对象必须可序列化（实现Serializable或可转为JSON）</li>
 *   <li>大数据量消息会影响性能，建议发送摘要信息</li>
 *   <li>消息发送是异步的，不保证立即送达</li>
 *   <li>前端需要处理连接断开和重连的情况</li>
 * </ul>
 *
 * @param <T> 消息数据的类型，可以是任意可序列化的对象
 * @author DataEase
 * @since 1.0.0
 * @see WsService
 * @see io.dataease.websocket.service.impl.StandaloneWsService
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsMessage<T> implements Serializable {

    /**
     * 消息接收者的用户ID
     * <p>
     * 用于实现点对点消息推送，消息只会发送给指定userId的用户。
     * 对应前端订阅路径：/user/{userId}/{topic}
     * </p>
     *
     * <p><b>获取方式：</b></p>
     * <ul>
     *   <li>从当前登录用户获取：{@code AuthUtils.getUser().getUserId()}</li>
     *   <li>从业务数据中获取：{@code exportTask.getUserId()}</li>
     *   <li>从方法参数传入：{@code sendNotification(Long userId)}</li>
     * </ul>
     */
    private Long userId;

    /**
     * 消息主题（Topic）
     * <p>
     * 用于区分不同类型的消息，前端根据topic订阅不同的消息通道。
     * 完整的订阅路径为：/user/{userId}/{topic}
     * </p>
     *
     * <p><b>命名规范：</b></p>
     * <ul>
     *   <li>使用斜杠开头：/task-export-topic</li>
     *   <li>使用连字符分隔：/web-msg-topic</li>
     *   <li>语义化命名：/resource-update-topic</li>
     * </ul>
     *
     * <p><b>现有Topic：</b></p>
     * <ul>
     *   <li>/task-export-topic - 导出任务状态通知</li>
     *   <li>/web-msg-topic - 站内消息通知</li>
     * </ul>
     */
    private String topic;

    /**
     * 消息数据内容
     * <p>
     * 泛型类型，可以是任意可序列化的对象。
     * 常见类型：DTO、VO、Map、String等。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>ExportTaskDTO - 导出任务完成时发送任务详情</li>
     *   <li>String - 简单的刷新标识（如"refresh"）</li>
     *   <li>Map&lt;String, Object&gt; - 自定义复杂数据</li>
     *   <li>业务VO对象 - 包含具体业务数据</li>
     * </ul>
     *
     * <p><b>序列化要求：</b></p>
     * <ul>
     *   <li>对象必须可序列化（实现Serializable）</li>
     *   <li>或可以转换为JSON（通过Jackson）</li>
     *   <li>避免循环引用</li>
     *   <li>避免过大的数据对象</li>
     * </ul>
     */
    private T data;


}

