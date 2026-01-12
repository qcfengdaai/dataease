package io.dataease.websocket;


/**
 * WebSocket消息服务接口
 * <p>
 * 定义WebSocket消息发送的统一接口，支持不同部署模式的实现（单机版、分布式版）。
 * 采用SPI（Service Provider Interface）模式，根据部署环境自动选择合适的实现。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>消息发布 - 向指定用户推送WebSocket消息</li>
 *   <li>实现解耦 - 业务代码无需关心具体的消息推送实现</li>
 *   <li>多模式支持 - 单机版和分布式版使用不同的实现策略</li>
 * </ul>
 *
 * <p><b>实现类：</b></p>
 * <ul>
 *   <li>{@link io.dataease.websocket.service.impl.StandaloneWsService} - 单机版实现（使用Spring WebSocket）</li>
 *   <li>DistributedWsService - 分布式版实现（使用Redis Pub/Sub或消息队列）</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.exportCenter.manage.ExportCenterDownLoadManage} - 导出中心，任务完成时通知前端</li>
 *   <li>{@link io.dataease.websocket.aop.WSTrigger} - AOP切面，站内消息发送后触发WebSocket通知</li>
 *   <li>其他需要实时推送的业务场景</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：导出任务完成通知（实际使用场景）
 * {@literal @}Component
 * public class ExportCenterDownLoadManage {
 *
 *     {@literal @}Autowired
 *     private WsService wsService;
 *
 *     {@literal @}Scheduled(fixedRate = 5000)
 *     public void checkRunningTask() {
 *         // 检查导出任务是否完成
 *         if (task.isDone()) {
 *             // 构建任务DTO
 *             ExportTaskDTO exportTaskDTO = new ExportTaskDTO();
 *             exportTaskDTO.setId(taskId);
 *             exportTaskDTO.setStatus("SUCCESS");
 *             exportTaskDTO.setProgress("100");
 *
 *             // 发送WebSocket消息通知前端
 *             WsMessage&lt;ExportTaskDTO&gt; message = new WsMessage&lt;&gt;(
 *                 task.getUserId(),
 *                 "/task-export-topic",
 *                 exportTaskDTO
 *             );
 *             wsService.releaseMessage(message);
 *         }
 *     }
 * }
 *
 * // 示例2：站内消息通知（AOP切面使用）
 * {@literal @}Aspect
 * {@literal @}Component
 * public class WSTrigger {
 *
 *     {@literal @}Autowired
 *     private WsService wsService;
 *
 *     {@literal @}AfterReturning("execution(* sendStationMessage(..))")
 *     public void after(JoinPoint point) {
 *         Long userId = (Long) point.getArgs()[0];
 *
 *         // 发送刷新通知
 *         WsMessage&lt;String&gt; message = new WsMessage&lt;&gt;(
 *             userId,
 *             "/web-msg-topic",
 *             "refresh"
 *         );
 *         wsService.releaseMessage(message);
 *     }
 * }
 *
 * // 示例3：自定义业务场景
 * {@literal @}Service
 * public class ChartService {
 *
 *     {@literal @}Autowired
 *     private WsService wsService;
 *
 *     public void updateChart(ChartDTO chart) {
 *         // 更新图表逻辑
 *         chartMapper.updateById(chart);
 *
 *         // 通知协作用户图表已更新
 *         List&lt;Long&gt; collaborators = getChartCollaborators(chart.getId());
 *         for (Long userId : collaborators) {
 *             WsMessage&lt;ChartUpdateNotice&gt; message = new WsMessage&lt;&gt;(
 *                 userId,
 *                 "/chart-update-topic",
 *                 new ChartUpdateNotice(chart.getId(), chart.getName(), "updated")
 *             );
 *             wsService.releaseMessage(message);
 *         }
 *     }
 * }
 * </pre>
 *
 * <p><b>单机版实现原理（StandaloneWsService）：</b></p>
 * <ol>
 *   <li>使用Spring的SimpMessagingTemplate发送消息</li>
 *   <li>调用convertAndSendToUser方法，指定用户和主题</li>
 *   <li>消息路径格式：/user/{userId}/{topic}</li>
 *   <li>前端订阅路径：/user/{topic}（Spring自动处理userId匹配）</li>
 * </ol>
 *
 * <p><b>分布式版实现原理（DistributedWsService）：</b></p>
 * <ol>
 *   <li>将消息发布到Redis Pub/Sub或消息队列</li>
 *   <li>所有WebSocket服务器订阅消息</li>
 *   <li>服务器收到消息后，检查用户是否连接到本服务器</li>
 *   <li>如果连接在本服务器，则推送消息给前端</li>
 * </ol>
 *
 * <p><b>消息推送流程：</b></p>
 * <pre>
 * 业务代码
 *    ↓
 * 调用 wsService.releaseMessage(message)
 *    ↓
 * ┌─────────────┬──────────────┐
 * │  单机版     │   分布式版    │
 * │             │              │
 * │ SimpMessaging │ Redis Pub/Sub│
 * │ Template    │   或MQ       │
 * │      ↓      │      ↓       │
 * │ WebSocket   │ 广播到所有   │
 * │ 推送        │ WebSocket    │
 * │             │ 服务器       │
 * │             │      ↓       │
 * │             │ 匹配用户连接 │
 * │             │      ↓       │
 * │             │ WebSocket    │
 * │             │ 推送         │
 * └─────────────┴──────────────┘
 *        ↓
 *    前端接收
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>消息发送是异步的，不保证立即送达</li>
 *   <li>用户未连接WebSocket时消息会丢失（不会缓存）</li>
 *   <li>重要消息需要配合数据库持久化和轮询机制</li>
 *   <li>消息体不宜过大，建议控制在1KB以内</li>
 *   <li>高频消息推送需要考虑性能影响</li>
 *   <li>分布式环境下需要确保所有服务器都能接收到消息</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>消息内容精简，只发送必要的标识信息</li>
 *   <li>前端收到消息后再调用API获取详细数据</li>
 *   <li>合并短时间内的多条消息，避免频繁推送</li>
 *   <li>为不同业务场景定义独立的topic</li>
 *   <li>做好消息发送失败的容错处理</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see WsMessage
 * @see io.dataease.websocket.service.impl.StandaloneWsService
 */
public interface WsService {

    /**
     * 发布WebSocket消息
     * <p>
     * 将消息推送给指定用户的指定主题。
     * 具体推送机制由实现类决定（单机版或分布式版）。
     * </p>
     *
     * <p><b>调用时机：</b></p>
     * <ul>
     *   <li>异步任务完成时（如导出、导入、计算任务）</li>
     *   <li>数据状态变更时（如审批通过、订单完成）</li>
     *   <li>协作通知时（如文档被编辑、评论被回复）</li>
     *   <li>系统通知时（如系统维护、重要公告）</li>
     * </ul>
     *
     * <p><b>参数校验：</b></p>
     * <ul>
     *   <li>wsMessage不能为null</li>
     *   <li>wsMessage.userId不能为null（必须指定接收者）</li>
     *   <li>wsMessage.topic不能为null或空字符串（必须指定主题）</li>
     *   <li>wsMessage.data可以为null（表示空消息体）</li>
     * </ul>
     *
     * <p><b>实现要求：</b></p>
     * <ul>
     *   <li>方法必须是非阻塞的，不影响主流程</li>
     *   <li>发送失败不应抛出异常，应记录日志</li>
     *   <li>参数校验失败应直接返回，不发送消息</li>
     * </ul>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 发送简单消息
     * WsMessage&lt;String&gt; message = new WsMessage&lt;&gt;(
     *     userId,
     *     "/notification-topic",
     *     "您有一条新消息"
     * );
     * wsService.releaseMessage(message);
     *
     * // 发送复杂对象
     * TaskCompleteDTO taskData = new TaskCompleteDTO();
     * taskData.setTaskId(123L);
     * taskData.setStatus("SUCCESS");
     * taskData.setResult("处理完成");
     *
     * WsMessage&lt;TaskCompleteDTO&gt; taskMessage = new WsMessage&lt;&gt;(
     *     userId,
     *     "/task-complete-topic",
     *     taskData
     * );
     * wsService.releaseMessage(taskMessage);
     *
     * // 批量发送（循环调用）
     * List&lt;Long&gt; userIds = getNotificationUsers();
     * for (Long uid : userIds) {
     *     WsMessage&lt;String&gt; msg = new WsMessage&lt;&gt;(
     *         uid,
     *         "/system-notice-topic",
     *         "系统将于今晚10点进行维护"
     *     );
     *     wsService.releaseMessage(msg);
     * }
     * </pre>
     *
     * @param wsMessage WebSocket消息对象，包含userId、topic、data三个属性
     *                  <ul>
     *                    <li>userId - 消息接收者的用户ID（必填）</li>
     *                    <li>topic - 消息主题（必填）</li>
     *                    <li>data - 消息数据（可选，泛型类型）</li>
     *                  </ul>
     * @see WsMessage
     * @see io.dataease.exportCenter.manage.ExportCenterDownLoadManage#checkRunningTask()
     * @see io.dataease.websocket.aop.WSTrigger#after(org.aspectj.lang.JoinPoint)
     */
    void releaseMessage(WsMessage wsMessage);


}
