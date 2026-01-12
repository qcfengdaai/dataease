/**
 * DataEase系统事件监听器包
 * <p>
 * 本包提供DataEase系统中各种事件监听器的实现，主要包括缓存事件监听和应用生命周期事件监听。
 * 这些监听器通过Spring框架的事件机制和AOP特性，自动响应系统中的各种事件，
 * 提供缓存管理、系统初始化、数据清理等重要功能。
 * </p>
 *
 * <h2>核心组件</h2>
 * <ul>
 *   <li><b>{@link io.dataease.listener.MyCacheListener}</b> - EhCache事件监听器</li>
 *   <li><b>{@link io.dataease.listener.RedisCacheListener}</b> - Redis缓存启动监听器</li>
 * </ul>
 *
 * <h2>主要功能</h2>
 * <ul>
 *   <li><b>缓存事件监控</b> - 监听和记录缓存操作事件，便于调试和性能分析</li>
 *   <li><b>系统启动处理</b> - 在应用启动时执行必要的初始化和清理操作</li>
 *   <li><b>版本兼容性</b> - 处理系统升级时的数据兼容性问题</li>
 *   <li><b>自动化管理</b> - 通过事件驱动机制自动响应系统状态变化</li>
 * </ul>
 *
 * <h2>设计模式</h2>
 * <p>本包采用<b>观察者模式</b>和<b>事件驱动架构</b>：</p>
 * <ul>
 *   <li><b>Spring事件机制</b> - 利用ApplicationEvent和ApplicationListener</li>
 *   <li><b>条件化加载</b> - 使用@ConditionalOnExpression实现按需加载</li>
 *   <li><b>自动注册</b> - 通过@Component注解自动注册到Spring容器</li>
 *   <li><b>执行顺序</b> - 使用@Order注解控制监听器执行顺序</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <ul>
 *   <li><b>缓存调试</b> - 在开发和测试环境中监控缓存行为</li>
 *   <li><b>性能分析</b> - 分析缓存命中率和使用模式</li>
 *   <li><b>系统升级</b> - 处理版本升级时的数据迁移和清理</li>
 *   <li><b>故障诊断</b> - 通过事件日志快速定位问题</li>
 *   <li><b>运维监控</b> - 监控系统运行状态和异常情况</li>
 * </ul>
 *
 * <h2>配置和使用</h2>
 *
 * <h3>1. EhCache事件监听配置</h3>
 * <p>MyCacheListener会自动监听EhCache的所有事件，无需额外配置：</p>
 * <pre><code>
 * // 在ehcache.xml或缓存配置中引用
 * &lt;config xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *         xmlns="http://www.ehcache.org/v3"&gt;
 *   &lt;cache alias="myCache"&gt;
 *     &lt;key-type&gt;java.lang.String&lt;/key-type&gt;
 *     &lt;value-type&gt;java.lang.Object&lt;/value-type&gt;
 *     &lt;listeners&gt;
 *       &lt;listener&gt;
 *         &lt;class&gt;io.dataease.listener.MyCacheListener&lt;/class&gt;
 *         &lt;event-firing-mode&gt;ASYNCHRONOUS&lt;/event-firing-mode&gt;
 *         &lt;event-ordering-mode&gt;UNORDERED&lt;/event-ordering-mode&gt;
 *         &lt;events-to-fire-on&gt;CREATED&lt;/events-to-fire-on&gt;
 *         &lt;events-to-fire-on&gt;UPDATED&lt;/events-to-fire-on&gt;
 *         &lt;events-to-fire-on&gt;EXPIRED&lt;/events-to-fire-on&gt;
 *         &lt;events-to-fire-on&gt;REMOVED&lt;/events-to-fire-on&gt;
 *       &lt;/listener&gt;
 *     &lt;/listeners&gt;
 *   &lt;/cache&gt;
 * &lt;/config&gt;
 * </code></pre>
 *
 * <h3>2. Redis缓存监听配置</h3>
 * <p>RedisCacheListener通过配置文件控制是否启用：</p>
 * <pre><code>
 * # application.yml 配置示例
 * spring:
 *   cache:
 *     type: redis  # 启用Redis缓存时，RedisCacheListener会自动生效
 *   redis:
 *     host: localhost
 *     port: 6379
 *     password: your_password
 *     database: 0
 *
 * # 当cache.type为redis时，启动监听器会：
 * # 1. 自动清理以"de_v2_"为前缀的过期缓存
 * # 2. 确保系统升级时的数据一致性
 * </code></pre>
 *
 * <h3>3. 自定义缓存监听器示例</h3>
 * <pre><code>
 * // 创建自定义缓存监听器
 * &#64;Component
 * &#64;ConditionalOnProperty(name = "dataease.cache.monitor.enabled", havingValue = "true")
 * public class CustomCacheListener implements CacheEventListener&lt;String, Object&gt; {
 *
 *     private static final Logger logger = LoggerFactory.getLogger(CustomCacheListener.class);
 *
 *     &#64;Override
 *     public void onEvent(CacheEvent&lt;? extends String, ?&gt; event) {
 *         switch (event.getType()) {
 *             case CREATED:
 *                 logger.info("缓存创建: key={}, value={}", event.getKey(), event.getNewValue());
 *                 break;
 *             case UPDATED:
 *                 logger.info("缓存更新: key={}, oldValue={}, newValue={}",
 *                           event.getKey(), event.getOldValue(), event.getNewValue());
 *                 break;
 *             case REMOVED:
 *                 logger.info("缓存删除: key={}", event.getKey());
 *                 break;
 *             case EXPIRED:
 *                 logger.info("缓存过期: key={}", event.getKey());
 *                 break;
 *             default:
 *                 logger.debug("其他缓存事件: type={}, key={}", event.getType(), event.getKey());
 *         }
 *     }
 * }
 * </code></pre>
 *
 * <h3>4. 应用启动监听器示例</h3>
 * <pre><code>
 * // 创建应用启动监听器
 * &#64;Component
 * &#64;Order(50)  // 控制执行顺序
 * public class CustomStartupListener implements ApplicationListener&lt;ApplicationReadyEvent&gt; {
 *
 *     &#64;Resource
 *     private DataInitService dataInitService;
 *
 *     &#64;Override
 *     public void onApplicationEvent(ApplicationReadyEvent event) {
 *         try {
 *             // 执行自定义初始化逻辑
 *             dataInitService.initializeSystemData();
 *             logger.info("系统数据初始化完成");
 *         } catch (Exception e) {
 *             logger.error("系统数据初始化失败", e);
 *         }
 *     }
 * }
 * </code></pre>
 *
 * <h2>监听器生命周期</h2>
 * <table border="1" style="border-collapse: collapse; width: 100%;">
 *   <tr>
 *     <th>阶段</th>
 *     <th>MyCacheListener</th>
 *     <th>RedisCacheListener</th>
 *   </tr>
 *   <tr>
 *     <td>注册</td>
 *     <td>通过EhCache配置文件注册</td>
 *     <td>Spring启动时自动注册（当cache.type=redis）</td>
 *   </tr>
 *   <tr>
 *     <td>激活</td>
 *     <td>缓存操作触发时</td>
 *     <td>ApplicationReadyEvent事件触发时</td>
 *   </tr>
 *   <tr>
 *     <td>执行</td>
 *     <td>异步执行事件处理</td>
 *     <td>同步执行清理操作</td>
 *   </tr>
 *   <tr>
 *     <td>销毁</td>
 *     <td>应用关闭时自动清理</td>
 *     <td>应用关闭时自动清理</td>
 *   </tr>
 * </table>
 *
 * <h2>事件类型说明</h2>
 * <h3>缓存事件类型</h3>
 * <ul>
 *   <li><b>CREATED</b> - 新缓存项被创建</li>
 *   <li><b>UPDATED</b> - 现有缓存项被更新</li>
 *   <li><b>REMOVED</b> - 缓存项被手动删除</li>
 *   <li><b>EXPIRED</b> - 缓存项因TTL到期而过期</li>
 *   <li><b>EVICTED</b> - 缓存项因内存不足被驱逐</li>
 * </ul>
 *
 * <h3>应用事件类型</h3>
 * <ul>
 *   <li><b>ApplicationStartingEvent</b> - 应用开始启动</li>
 *   <li><b>ApplicationEnvironmentPreparedEvent</b> - 环境准备完成</li>
 *   <li><b>ApplicationContextInitializedEvent</b> - 上下文初始化完成</li>
 *   <li><b>ApplicationPreparedEvent</b> - 应用准备完成</li>
 *   <li><b>ApplicationStartedEvent</b> - 应用启动完成</li>
 *   <li><b>ApplicationReadyEvent</b> - 应用就绪（RedisCacheListener监听此事件）</li>
 *   <li><b>ApplicationFailedEvent</b> - 应用启动失败</li>
 * </ul>
 *
 * <h2>最佳实践</h2>
 * <ol>
 *   <li><b>合理设置监听器顺序</b> - 使用@Order注解控制执行顺序，避免依赖冲突</li>
 *   <li><b>异常处理</b> - 监听器中的异常不应影响主业务流程</li>
 *   <li><b>性能考虑</b> - 避免在监听器中执行耗时操作，考虑异步处理</li>
 *   <li><b>条件化加载</b> - 使用条件注解避免不必要的监听器加载</li>
 *   <li><b>日志记录</b> - 详细记录监听器的执行情况，便于调试和监控</li>
 *   <li><b>资源清理</b> - 确保监听器正确释放占用的资源</li>
 * </ol>
 *
 * <h2>故障排查</h2>
 * <h3>常见问题</h3>
 * <ul>
 *   <li><b>监听器未生效</b> - 检查条件注解配置和Spring扫描路径</li>
 *   <li><b>执行顺序错误</b> - 调整@Order注解的数值</li>
 *   <li><b>缓存事件丢失</b> - 检查EhCache配置和监听器注册</li>
 *   <li><b>Redis清理失败</b> - 检查Redis连接和权限配置</li>
 * </ul>
 *
 * <h3>调试技巧</h3>
 * <pre><code>
 * # 启用调试日志
 * logging:
 *   level:
 *     io.dataease.listener: DEBUG
 *     org.ehcache: DEBUG
 *     org.springframework.data.redis: DEBUG
 * </code></pre>
 *
 * <h2>扩展指南</h2>
 * <p>如需添加新的监听器，请遵循以下步骤：</p>
 * <ol>
 *   <li>确定监听的事件类型（缓存事件、应用事件等）</li>
 *   <li>实现对应的监听器接口</li>
 *   <li>添加适当的条件注解和执行顺序</li>
 *   <li>实现异常处理和日志记录</li>
 *   <li>编写单元测试验证功能</li>
 *   <li>更新本包的文档说明</li>
 * </ol>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>监听器在Spring应用上下文中运行，可以注入其他Bean</li>
 *   <li>缓存事件监听器的性能直接影响缓存操作的性能</li>
 *   <li>应用事件监听器的异常会影响应用启动过程</li>
 *   <li>Redis清理操作使用SCAN命令，对生产环境友好但可能耗时较长</li>
 *   <li>监听器的日志输出可能较多，生产环境中建议适当调整日志级别</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 * @version 1.0
 */
package io.dataease.listener;