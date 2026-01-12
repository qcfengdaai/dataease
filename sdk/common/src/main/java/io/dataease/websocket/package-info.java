/**
 * WebSocket实时通信包
 * <p>
 * 提供DataEase系统的WebSocket实时消息推送功能，支持服务端主动向前端推送消息。
 * 基于STOMP协议和Spring WebSocket实现，支持点对点消息推送和主题订阅。
 * </p>
 *
 * <h2>核心组件</h2>
 *
 * <h3>1. 消息实体</h3>
 * <ul>
 *   <li>{@link io.dataease.websocket.WsMessage} - WebSocket消息封装类，支持泛型数据</li>
 * </ul>
 *
 * <h3>2. 服务接口</h3>
 * <ul>
 *   <li>{@link io.dataease.websocket.WsService} - 消息发送服务接口</li>
 * </ul>
 *
 * <h3>3. 实现类（位于core-backend）</h3>
 * <ul>
 *   <li>StandaloneWsService - 单机版实现（使用SimpMessagingTemplate）</li>
 *   <li>DistributedWsService - 分布式版实现（使用Redis Pub/Sub）</li>
 * </ul>
 *
 * <h3>4. AOP切面（位于core-backend）</h3>
 * <ul>
 *   <li>WSTrigger - WebSocket消息触发器，拦截特定方法自动发送消息</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 *
 * <h3>当前已实现的场景</h3>
 * <ul>
 *   <li><b>导出任务通知</b>：{@link io.dataease.exportCenter.manage.ExportCenterDownLoadManage}
 *       <ul>
 *         <li>数据集导出完成通知</li>
 *         <li>图表导出完成通知</li>
 *         <li>仪表板导出完成通知</li>
 *         <li>Topic: /task-export-topic</li>
 *       </ul>
 *   </li>
 *   <li><b>站内消息通知</b>：{@link io.dataease.websocket.aop.WSTrigger}
 *       <ul>
 *         <li>新消息到达提醒</li>
 *         <li>消息列表刷新通知</li>
 *         <li>Topic: /web-msg-topic</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <h3>潜在使用场景</h3>
 * <ul>
 *   <li><b>协作编辑</b>：多用户同时编辑图表/仪表板时的实时同步</li>
 *   <li><b>数据刷新</b>：数据源数据更新时通知前端刷新图表</li>
 *   <li><b>任务进度</b>：长时间运行任务的进度实时更新</li>
 *   <li><b>系统通知</b>：系统维护、版本更新等通知</li>
 *   <li><b>权限变更</b>：用户权限变更后实时通知用户</li>
 *   <li><b>审批流程</b>：审批状态变更实时通知</li>
 * </ul>
 *
 * <h2>架构设计</h2>
 *
 * <h3>技术栈</h3>
 * <ul>
 *   <li><b>协议</b>：STOMP (Simple Text Oriented Messaging Protocol)</li>
 *   <li><b>传输</b>：WebSocket / SockJS (降级支持)</li>
 *   <li><b>后端</b>：Spring WebSocket + Spring Messaging</li>
 *   <li><b>前端</b>：SockJS-client + Stomp.js</li>
 * </ul>
 *
 * <h3>消息路由模型</h3>
 * <pre>
 * 后端发送消息路径：
 *   /user/{userId}/{topic}
 *
 * 前端订阅路径：
 *   /user/{topic}
 *
 * Spring WebSocket会自动匹配userId，
 * 只将消息推送给对应用户的连接。
 * </pre>
 *
 * <h3>单机版架构</h3>
 * <pre>
 * ┌─────────────┐
 * │  前端浏览器  │
 * │ (WebSocket) │
 * └──────┬──────┘
 *        │ /ws-endpoint
 *        │ STOMP协议
 *        ↓
 * ┌──────────────────┐
 * │  Spring Boot     │
 * │  WebSocket Server│
 * ├──────────────────┤
 * │ SimpMessaging    │← WsService
 * │ Template         │  .releaseMessage()
 * ├──────────────────┤
 * │ 业务代码          │
 * │ - ExportManager  │
 * │ - MessageService │
 * └──────────────────┘
 * </pre>
 *
 * <h3>分布式架构</h3>
 * <pre>
 * ┌──────┐  ┌──────┐  ┌──────┐
 * │浏览器1│  │浏览器2│  │浏览器3│
 * └───┬──┘  └───┬──┘  └───┬──┘
 *     │         │         │
 *     │         │         │
 * ┌───▼─────┐ ┌▼─────┐ ┌─▼──────┐
 * │ WS服务器1│ │WS服务器2│ │WS服务器3│
 * └───┬─────┘ └┬─────┘ └─┬──────┘
 *     │         │         │
 *     └────┬────┴────┬────┘
 *          │         │
 *     ┌────▼─────────▼────┐
 *     │  Redis Pub/Sub    │
 *     │  /ws-message/*    │
 *     └────▲──────────────┘
 *          │
 *     ┌────┴──────┐
 *     │ 业务服务器 │
 *     │ WsService │
 *     └───────────┘
 *
 * 流程：
 * 1. 业务代码调用WsService.releaseMessage()
 * 2. 消息发布到Redis频道
 * 3. 所有WS服务器订阅Redis频道
 * 4. 各WS服务器检查用户是否连接到本服务器
 * 5. 匹配的服务器推送消息到前端
 * </pre>
 *
 * <h2>完整使用示例</h2>
 *
 * <h3>后端示例：导出任务通知</h3>
 * <pre>
 * {@literal @}Component
 * public class ExportTaskManager {
 *
 *     {@literal @}Autowired
 *     private WsService wsService;
 *
 *     {@literal @}Autowired
 *     private CoreExportTaskMapper exportTaskMapper;
 *
 *     {@literal @}Scheduled(fixedRate = 5000)  // 每5秒检查一次
 *     public void checkRunningTasks() {
 *         // 查询所有运行中的任务
 *         List&lt;ExportTask&gt; runningTasks = exportTaskMapper.selectRunning();
 *
 *         for (ExportTask task : runningTasks) {
 *             // 检查任务是否完成
 *             if (task.getFuture().isDone()) {
 *                 // 更新任务状态
 *                 task.setStatus("SUCCESS");
 *                 task.setProgress("100");
 *                 exportTaskMapper.updateById(task);
 *
 *                 // 构建DTO
 *                 ExportTaskDTO dto = new ExportTaskDTO();
 *                 BeanUtils.copyBean(dto, task);
 *
 *                 // 发送WebSocket通知
 *                 WsMessage&lt;ExportTaskDTO&gt; message = new WsMessage&lt;&gt;(
 *                     task.getUserId(),         // 任务创建者
 *                     "/task-export-topic",     // 导出任务主题
 *                     dto                       // 任务详情
 *                 );
 *
 *                 // 推送消息
 *                 wsService.releaseMessage(message);
 *
 *                 LogUtil.info("导出任务完成通知已发送: taskId={}, userId={}",
 *                     task.getId(), task.getUserId());
 *             }
 *         }
 *     }
 * }
 * </pre>
 *
 * <h3>后端示例：站内消息通知（AOP方式）</h3>
 * <pre>
 * {@literal @}Aspect
 * {@literal @}Component
 * public class MessageNotificationAspect {
 *
 *     {@literal @}Autowired
 *     private WsService wsService;
 *
 *     // 拦截站内消息发送方法
 *     {@literal @}AfterReturning(
 *         value = "execution(* io.dataease.message.service.MessageService.sendMessage(..))",
 *         returning = "result"
 *     )
 *     public void afterSendMessage(JoinPoint point, Object result) {
 *         Object[] args = point.getArgs();
 *         if (args.length > 0 && args[0] instanceof Long) {
 *             Long userId = (Long) args[0];
 *
 *             // 发送刷新通知
 *             WsMessage&lt;String&gt; message = new WsMessage&lt;&gt;(
 *                 userId,
 *                 "/web-msg-topic",
 *                 "refresh"  // 简单的刷新标识
 *             );
 *
 *             wsService.releaseMessage(message);
 *
 *             LogUtil.debug("消息刷新通知已发送: userId={}", userId);
 *         }
 *     }
 * }
 * </pre>
 *
 * <h3>前端示例：连接和订阅</h3>
 * <pre>
 * // websocket.ts - WebSocket管理模块
 * import SockJS from 'sockjs-client'
 * import Stomp, { Client } from 'stompjs'
 *
 * class WebSocketService {
 *   private stompClient: Client | null = null
 *   private subscriptions: Map&lt;string, any&gt; = new Map()
 *
 *   // 连接WebSocket
 *   connect(userId: number) {
 *     const socket = new SockJS('/ws-endpoint')
 *     this.stompClient = Stomp.over(socket)
 *
 *     // 连接配置
 *     this.stompClient.connect(
 *       {},  // headers
 *       () => {
 *         console.log('WebSocket连接成功')
 *         this.subscribeTopics()
 *       },
 *       (error) => {
 *         console.error('WebSocket连接失败:', error)
 *         // 3秒后重连
 *         setTimeout(() => this.connect(userId), 3000)
 *       }
 *     )
 *   }
 *
 *   // 订阅主题
 *   private subscribeTopics() {
 *     // 订阅导出任务通知
 *     this.subscribe('/user/task-export-topic', (message) => {
 *       const taskData = JSON.parse(message.body)
 *       console.log('收到导出任务更新:', taskData)
 *
 *       // 更新任务列表
 *       this.updateTaskList(taskData)
 *
 *       // 显示通知
 *       if (taskData.status === 'SUCCESS') {
 *         this.$notify.success({
 *           title: '导出完成',
 *           message: `${taskData.exportFromName} 导出完成`
 *         })
 *       } else if (taskData.status === 'FAILED') {
 *         this.$notify.error({
 *           title: '导出失败',
 *           message: taskData.msg || '导出过程中发生错误'
 *         })
 *       }
 *     })
 *
 *     // 订阅站内消息通知
 *     this.subscribe('/user/web-msg-topic', (message) => {
 *       const action = JSON.parse(message.body)
 *       if (action === 'refresh') {
 *         console.log('收到消息刷新通知')
 *         // 刷新消息列表
 *         this.loadMessageList()
 *         // 更新未读消息数
 *         this.updateUnreadCount()
 *       }
 *     })
 *   }
 *
 *   // 订阅单个主题
 *   subscribe(topic: string, callback: (message: any) => void) {
 *     if (this.stompClient &amp;&amp; this.stompClient.connected) {
 *       const subscription = this.stompClient.subscribe(topic, callback)
 *       this.subscriptions.set(topic, subscription)
 *       console.log(`已订阅主题: ${topic}`)
 *     }
 *   }
 *
 *   // 取消订阅
 *   unsubscribe(topic: string) {
 *     const subscription = this.subscriptions.get(topic)
 *     if (subscription) {
 *       subscription.unsubscribe()
 *       this.subscriptions.delete(topic)
 *       console.log(`已取消订阅: ${topic}`)
 *     }
 *   }
 *
 *   // 断开连接
 *   disconnect() {
 *     if (this.stompClient) {
 *       this.stompClient.disconnect(() => {
 *         console.log('WebSocket已断开')
 *       })
 *     }
 *   }
 *
 *   // 更新任务列表（业务方法）
 *   private updateTaskList(taskData: any) {
 *     // 在Vuex中更新任务状态
 *     store.commit('updateExportTask', taskData)
 *   }
 *
 *   // 加载消息列表（业务方法）
 *   private loadMessageList() {
 *     // 调用API获取最新消息
 *     api.getMessageList().then(res => {
 *       store.commit('setMessages', res.data)
 *     })
 *   }
 *
 *   // 更新未读消息数（业务方法）
 *   private updateUnreadCount() {
 *     api.getUnreadCount().then(res => {
 *       store.commit('setUnreadCount', res.data)
 *     })
 *   }
 * }
 *
 * export const wsService = new WebSocketService()
 * </pre>
 *
 * <h3>前端示例：在Vue组件中使用</h3>
 * <pre>
 * // App.vue - 应用入口
 * &lt;script setup lang="ts"&gt;
 * import { onMounted, onUnmounted } from 'vue'
 * import { wsService } from '@/utils/websocket'
 * import { useUserStore } from '@/store/user'
 *
 * const userStore = useUserStore()
 *
 * onMounted(() => {
 *   // 用户登录后建立WebSocket连接
 *   if (userStore.userId) {
 *     wsService.connect(userStore.userId)
 *   }
 * })
 *
 * onUnmounted(() => {
 *   // 组件销毁时断开连接
 *   wsService.disconnect()
 * })
 * &lt;/script&gt;
 *
 * // ExportTaskList.vue - 导出任务列表页
 * &lt;template&gt;
 *   &lt;el-table :data="taskList"&gt;
 *     &lt;el-table-column prop="fileName" label="文件名" /&gt;
 *     &lt;el-table-column prop="exportProgress" label="进度"&gt;
 *       &lt;template #default="{ row }"&gt;
 *         &lt;el-progress :percentage="Number(row.exportProgress)" /&gt;
 *       &lt;/template&gt;
 *     &lt;/el-table-column&gt;
 *     &lt;el-table-column prop="exportStatus" label="状态" /&gt;
 *   &lt;/el-table&gt;
 * &lt;/template&gt;
 *
 * &lt;script setup lang="ts"&gt;
 * import { ref, onMounted } from 'vue'
 * import { getTaskList } from '@/api/export'
 *
 * const taskList = ref([])
 *
 * onMounted(async () => {
 *   // 初始加载任务列表
 *   const res = await getTaskList()
 *   taskList.value = res.data
 * })
 *
 * // WebSocket消息会自动更新Vuex中的任务数据
 * // 组件通过Vuex响应式获取最新数据
 * &lt;/script&gt;
 * </pre>
 *
 * <h2>Topic命名规范</h2>
 *
 * <h3>命名格式</h3>
 * <pre>
 * /&lt;业务模块&gt;-&lt;消息类型&gt;-topic
 *
 * 示例：
 * - /task-export-topic    (导出任务主题)
 * - /web-msg-topic        (站内消息主题)
 * - /chart-update-topic   (图表更新主题)
 * - /system-notice-topic  (系统通知主题)
 * </pre>
 *
 * <h3>已使用的Topic</h3>
 * <ul>
 *   <li><code>/task-export-topic</code> - 导出任务状态更新通知</li>
 *   <li><code>/web-msg-topic</code> - 站内消息刷新通知</li>
 * </ul>
 *
 * <h2>性能考虑</h2>
 *
 * <h3>1. 消息大小</h3>
 * <ul>
 *   <li>建议单条消息不超过1KB</li>
 *   <li>只发送必要的标识信息（如ID、状态）</li>
 *   <li>前端收到通知后再调用API获取详细数据</li>
 * </ul>
 *
 * <h3>2. 推送频率</h3>
 * <ul>
 *   <li>避免高频推送（建议间隔&gt;100ms）</li>
 *   <li>合并短时间内的多条消息</li>
 *   <li>使用定时任务批量检查而非实时推送</li>
 * </ul>
 *
 * <h3>3. 连接管理</h3>
 * <ul>
 *   <li>设置合理的心跳间隔（默认10秒）</li>
 *   <li>实现自动重连机制</li>
 *   <li>用户离线后及时清理连接</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 *
 * <h3>1. 消息可靠性</h3>
 * <ul>
 *   <li>WebSocket消息不保证送达</li>
 *   <li>用户离线时消息会丢失</li>
 *   <li>重要通知需要配合数据库持久化</li>
 *   <li>前端需要实现轮询作为降级方案</li>
 * </ul>
 *
 * <h3>2. 安全性</h3>
 * <ul>
 *   <li>WebSocket连接需要认证</li>
 *   <li>使用userId精确推送，避免信息泄露</li>
 *   <li>敏感数据需要加密传输</li>
 * </ul>
 *
 * <h3>3. 跨域问题</h3>
 * <ul>
 *   <li>配置CORS允许WebSocket连接</li>
 *   <li>使用SockJS作为降级方案</li>
 * </ul>
 *
 * <h3>4. 分布式部署</h3>
 * <ul>
 *   <li>需要Redis或消息队列支持</li>
 *   <li>所有WebSocket服务器需要订阅消息</li>
 *   <li>注意消息广播的性能影响</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.websocket.WsMessage
 * @see io.dataease.websocket.WsService
 * @see io.dataease.websocket.service.impl.StandaloneWsService
 */
package io.dataease.websocket;
