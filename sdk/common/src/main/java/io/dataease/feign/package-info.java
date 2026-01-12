/**
 * DataEase Feign客户端包
 * <p>
 * 本包提供DataEase系统的自定义Feign客户端注解和相关功能，用于简化微服务之间的HTTP调用。
 * 基于Spring Cloud OpenFeign构建，提供了更符合DataEase业务需求的功能扩展和配置支持。
 * </p>
 *
 * <h2>核心组件</h2>
 * <ul>
 *   <li><b>{@link io.dataease.feign.DeFeign}</b> - 自定义Feign客户端注解</li>
 * </ul>
 *
 * <h2>主要功能</h2>
 * <ul>
 *   <li><b>服务调用</b> - 简化微服务之间的HTTP调用，支持服务发现和负载均衡</li>
 *   <li><b>配置管理</b> - 提供丰富的配置选项，支持自定义编码器、解码器、拦截器等</li>
 *   <li><b>容错处理</b> - 支持fallback和fallbackFactory机制，提供服务降级能力</li>
 *   <li><b>灵活路由</b> - 支持URL直连和服务名调用两种方式</li>
 *   <li><b>异常处理</b> - 支持404错误特殊处理，提供更友好的错误处理体验</li>
 * </ul>
 *
 * <h2>架构设计</h2>
 * <p>本包采用<b>注解驱动</b>和<b>声明式编程</b>模式：</p>
 * <ul>
 *   <li><b>声明式接口</b> - 通过接口定义远程服务调用</li>
 *   <li><b>注解配置</b> - 使用@DeFeign注解进行配置</li>
 *   <li><b>动态代理</b> - 运行时生成代理实现类</li>
 *   <li><b>Spring集成</b> - 无缝集成Spring Boot和Spring Cloud</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <ul>
 *   <li><b>微服务调用</b> - DataEase各服务模块之间的相互调用</li>
 *   <li><b>分布式架构</b> - 支持分布式部署下的服务通信</li>
 *   <li><b>第三方API</b> - 调用外部第三方服务的API接口</li>
 *   <li><b>服务解耦</b> - 实现服务间的松耦合调用</li>
 *   <li><b>负载均衡</b> - 自动进行服务实例的负载均衡</li>
 * </ul>
 *
 * <h2>配置和使用</h2>
 *
 * <h3>1. 启用DeFeign支持</h3>
 * <p>在分布式模块中启用DeFeign功能：</p>
 * <pre><code>
 * &#64;SpringBootApplication
 * &#64;Import(DeFeignConfiguration.class)  // 导入DeFeign配置
 * public class DataEaseDistributedApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(DataEaseDistributedApplication.class, args);
 *     }
 * }
 * </code></pre>
 *
 * <h3>2. 基础服务调用示例</h3>
 * <p>创建简单的服务调用客户端：</p>
 * <pre><code>
 * &#64;DeFeign(name = "user-service")
 * public interface UserServiceClient {
 *
 *     &#64;GetMapping("/api/users/{id}")
 *     User getUserById(&#64;PathVariable("id") Long id);
 *
 *     &#64;PostMapping("/api/users")
 *     User createUser(&#64;RequestBody User user);
 *
 *     &#64;PutMapping("/api/users/{id}")
 *     User updateUser(&#64;PathVariable("id") Long id, &#64;RequestBody User user);
 *
 *     &#64;DeleteMapping("/api/users/{id}")
 *     void deleteUser(&#64;PathVariable("id") Long id);
 * }
 * </code></pre>
 *
 * <h3>3. URL直连调用示例</h3>
 * <p>直接指定目标服务URL：</p>
 * <pre><code>
 * &#64;DeFeign(name = "external-api", url = "https://api.external.com")
 * public interface ExternalApiClient {
 *
 *     &#64;GetMapping("/v1/data")
 *     ExternalData getData(&#64;RequestParam("type") String type);
 *
 *     &#64;PostMapping("/v1/webhook")
 *     void sendWebhook(&#64;RequestBody WebhookPayload payload);
 * }
 * </code></pre>
 *
 * <h3>4. 带路径前缀的服务调用</h3>
 * <pre><code>
 * &#64;DeFeign(name = "dataease-core", path = "/api/v1")
 * public interface DataEaseCoreClient {
 *
 *     &#64;GetMapping("/datasets/{id}")  // 实际请求：/api/v1/datasets/{id}
 *     Dataset getDataset(&#64;PathVariable Long id);
 *
 *     &#64;PostMapping("/charts")        // 实际请求：/api/v1/charts
 *     Chart createChart(&#64;RequestBody Chart chart);
 * }
 * </code></pre>
 *
 * <h3>5. 服务降级和容错示例</h3>
 * <p>使用fallback实现服务降级：</p>
 * <pre><code>
 * // Feign客户端接口
 * &#64;DeFeign(
 *     name = "notification-service",
 *     fallback = NotificationServiceFallback.class
 * )
 * public interface NotificationServiceClient {
 *     &#64;PostMapping("/api/notifications/send")
 *     boolean sendNotification(&#64;RequestBody NotificationRequest request);
 * }
 *
 * // 降级实现类
 * &#64;Component
 * public class NotificationServiceFallback implements NotificationServiceClient {
 *     private static final Logger logger = LoggerFactory.getLogger(NotificationServiceFallback.class);
 *
 *     &#64;Override
 *     public boolean sendNotification(NotificationRequest request) {
 *         logger.warn("通知服务不可用，使用降级处理：{}", request);
 *         // 可以选择写入队列、数据库或返回默认值
 *         return false;
 *     }
 * }
 * </code></pre>
 *
 * <h3>6. 使用FallbackFactory获取异常信息</h3>
 * <pre><code>
 * // Feign客户端
 * &#64;DeFeign(
 *     name = "data-service",
 *     fallbackFactory = DataServiceFallbackFactory.class
 * )
 * public interface DataServiceClient {
 *     &#64;GetMapping("/api/data/{id}")
 *     DataResult getData(&#64;PathVariable Long id);
 * }
 *
 * // 降级工厂实现
 * &#64;Component
 * public class DataServiceFallbackFactory implements FallbackFactory&lt;DataServiceClient&gt; {
 *     private static final Logger logger = LoggerFactory.getLogger(DataServiceFallbackFactory.class);
 *
 *     &#64;Override
 *     public DataServiceClient create(Throwable cause) {
 *         return new DataServiceClient() {
 *             &#64;Override
 *             public DataResult getData(Long id) {
 *                 logger.error("数据服务调用失败，ID：{}，原因：{}", id, cause.getMessage());
 *
 *                 // 根据异常类型返回不同的降级结果
 *                 if (cause instanceof TimeoutException) {
 *                     return DataResult.timeout(id);
 *                 } else if (cause instanceof ConnectException) {
 *                     return DataResult.unavailable(id);
 *                 } else {
 *                     return DataResult.error(id, cause.getMessage());
 *                 }
 *             }
 *         };
 *     }
 * }
 * </code></pre>
 *
 * <h3>7. 自定义配置示例</h3>
 * <pre><code>
 * // 自定义配置类
 * &#64;Configuration
 * public class CustomFeignConfiguration {
 *
 *     &#64;Bean
 *     public RequestInterceptor requestInterceptor() {
 *         return template -> {
 *             // 添加认证头
 *             template.header("Authorization", "Bearer " + getToken());
 *             // 添加请求ID
 *             template.header("X-Request-ID", UUID.randomUUID().toString());
 *         };
 *     }
 *
 *     &#64;Bean
 *     public Retryer retryer() {
 *         // 重试配置：初始间隔100ms，最大间隔1s，最多重试3次
 *         return new Retryer.Default(100, 1000, 3);
 *     }
 *
 *     &#64;Bean
 *     public ErrorDecoder errorDecoder() {
 *         return new CustomErrorDecoder();
 *     }
 *
 *     private String getToken() {
 *         // 获取认证token的逻辑
 *         return TokenManager.getCurrentToken();
 *     }
 * }
 *
 * // 使用自定义配置的Feign客户端
 * &#64;DeFeign(
 *     name = "secure-service",
 *     configuration = CustomFeignConfiguration.class
 * )
 * public interface SecureServiceClient {
 *     &#64;GetMapping("/api/secure/data")
 *     SecureData getSecureData();
 * }
 * </code></pre>
 *
 * <h3>8. 处理404错误示例</h3>
 * <pre><code>
 * &#64;DeFeign(
 *     name = "optional-service",
 *     dismiss404 = true  // 404时返回null而不抛异常
 * )
 * public interface OptionalServiceClient {
 *     &#64;GetMapping("/api/optional/{id}")
 *     OptionalData getOptionalData(&#64;PathVariable Long id);
 * }
 *
 * // 使用示例
 * &#64;Service
 * public class BusinessService {
 *     &#64;Resource
 *     private OptionalServiceClient optionalServiceClient;
 *
 *     public void processData(Long id) {
 *         OptionalData data = optionalServiceClient.getOptionalData(id);
 *         if (data != null) {
 *             // 处理存在的数据
 *             processExistingData(data);
 *         } else {
 *             // 处理数据不存在的情况
 *             handleMissingData(id);
 *         }
 *     }
 * }
 * </code></pre>
 *
 * <h3>9. 多实例和限定符示例</h3>
 * <pre><code>
 * // 生产环境客户端
 * &#64;DeFeign(
 *     name = "payment-service",
 *     contextId = "prodPaymentService",
 *     qualifiers = "production"
 * )
 * public interface ProdPaymentServiceClient {
 *     &#64;PostMapping("/api/payments")
 *     PaymentResult processPayment(&#64;RequestBody PaymentRequest request);
 * }
 *
 * // 测试环境客户端
 * &#64;DeFeign(
 *     name = "payment-service",
 *     contextId = "testPaymentService",
 *     qualifiers = "test",
 *     url = "http://test-payment-service:8080"
 * )
 * public interface TestPaymentServiceClient {
 *     &#64;PostMapping("/api/payments")
 *     PaymentResult processPayment(&#64;RequestBody PaymentRequest request);
 * }
 *
 * // 使用不同的客户端
 * &#64;Service
 * public class PaymentService {
 *
 *     &#64;Qualifier("production")
 *     &#64;Resource
 *     private ProdPaymentServiceClient prodPaymentClient;
 *
 *     &#64;Qualifier("test")
 *     &#64;Resource
 *     private TestPaymentServiceClient testPaymentClient;
 *
 *     public PaymentResult processPayment(PaymentRequest request, boolean useTestEnv) {
 *         if (useTestEnv) {
 *             return testPaymentClient.processPayment(request);
 *         } else {
 *             return prodPaymentClient.processPayment(request);
 *         }
 *     }
 * }
 * </code></pre>
 *
 * <h2>被使用的位置</h2>
 * <ul>
 *   <li><b>SDK Distributed模块</b> - DeFeignConfiguration和DeFeignRegister处理注解注册</li>
 *   <li><b>Core Backend模块</b> - 各种业务服务的Feign客户端定义</li>
 *   <li><b>Extensions模块</b> - 扩展服务的远程调用接口</li>
 *   <li><b>微服务模块</b> - 服务间通信的主要方式</li>
 * </ul>
 *
 * <h2>配置属性对比</h2>
 * <table border="1" style="border-collapse: collapse; width: 100%;">
 *   <tr>
 *     <th>属性</th>
 *     <th>类型</th>
 *     <th>必填</th>
 *     <th>默认值</th>
 *     <th>说明</th>
 *   </tr>
 *   <tr>
 *     <td>name/value</td>
 *     <td>String</td>
 *     <td>是</td>
 *     <td>""</td>
 *     <td>服务名称，用于服务发现</td>
 *   </tr>
 *   <tr>
 *     <td>url</td>
 *     <td>String</td>
 *     <td>否</td>
 *     <td>""</td>
 *     <td>直连URL，优先级高于服务名</td>
 *   </tr>
 *   <tr>
 *     <td>contextId</td>
 *     <td>String</td>
 *     <td>否</td>
 *     <td>""</td>
 *     <td>Bean标识ID，用于区分多实例</td>
 *   </tr>
 *   <tr>
 *     <td>path</td>
 *     <td>String</td>
 *     <td>否</td>
 *     <td>""</td>
 *     <td>统一路径前缀</td>
 *   </tr>
 *   <tr>
 *     <td>fallback</td>
 *     <td>Class</td>
 *     <td>否</td>
 *     <td>void.class</td>
 *     <td>降级实现类</td>
 *   </tr>
 *   <tr>
 *     <td>fallbackFactory</td>
 *     <td>Class</td>
 *     <td>否</td>
 *     <td>void.class</td>
 *     <td>降级工厂类</td>
 *   </tr>
 *   <tr>
 *     <td>dismiss404</td>
 *     <td>boolean</td>
 *     <td>否</td>
 *     <td>false</td>
 *     <td>404错误处理策略</td>
 *   </tr>
 *   <tr>
 *     <td>configuration</td>
 *     <td>Class[]</td>
 *     <td>否</td>
 *     <td>{}</td>
 *     <td>自定义配置类</td>
 *   </tr>
 *   <tr>
 *     <td>qualifiers</td>
 *     <td>String[]</td>
 *     <td>否</td>
 *     <td>{}</td>
 *     <td>Bean限定符</td>
 *   </tr>
 *   <tr>
 *     <td>primary</td>
 *     <td>boolean</td>
 *     <td>否</td>
 *     <td>true</td>
 *     <td>是否为主要Bean</td>
 *   </tr>
 * </table>
 *
 * <h2>与标准FeignClient的区别</h2>
 * <ul>
 *   <li><b>注册机制</b> - 使用自定义的DeFeignRegister进行Bean注册</li>
 *   <li><b>配置独立</b> - 不依赖@EnableFeignClients的扫描机制</li>
 *   <li><b>功能增强</b> - 可以根据DataEase需求进行功能扩展</li>
 *   <li><b>兼容性好</b> - 保持与标准FeignClient相似的使用方式</li>
 * </ul>
 *
 * <h2>最佳实践</h2>
 * <ol>
 *   <li><b>接口设计</b> - 保持接口简洁，遵循REST API设计原则</li>
 *   <li><b>异常处理</b> - 合理使用fallback和fallbackFactory进行降级</li>
 *   <li><b>配置管理</b> - 根据环境使用不同的配置策略</li>
 *   <li><b>超时设置</b> - 设置合理的连接和读取超时时间</li>
 *   <li><b>重试策略</b> - 针对不同的服务设置不同的重试策略</li>
 *   <li><b>监控日志</b> - 添加适当的日志和监控指标</li>
 *   <li><b>版本管理</b> - 使用path前缀管理API版本</li>
 * </ol>
 *
 * <h2>常见问题和解决方案</h2>
 * <h3>1. Bean名称冲突</h3>
 * <p>使用contextId和qualifiers进行区分：</p>
 * <pre><code>
 * &#64;DeFeign(name = "user-service", contextId = "userServiceV1")
 * public interface UserServiceV1Client { ... }
 *
 * &#64;DeFeign(name = "user-service", contextId = "userServiceV2")
 * public interface UserServiceV2Client { ... }
 * </code></pre>
 *
 * <h3>2. 循环依赖问题</h3>
 * <p>避免在Feign接口的fallback实现中注入其他Feign客户端。</p>
 *
 * <h3>3. 配置不生效</h3>
 * <p>确保配置类没有被@ComponentScan扫描到全局：</p>
 * <pre><code>
 * // 错误：会被全局扫描
 * &#64;Configuration
 * &#64;Component
 * public class FeignConfig { ... }
 *
 * // 正确：只在指定的Feign客户端中使用
 * &#64;Configuration
 * public class FeignConfig { ... }
 * </code></pre>
 *
 * <h2>性能优化建议</h2>
 * <ul>
 *   <li><b>连接池配置</b> - 使用HTTP连接池提高性能</li>
 *   <li><b>压缩传输</b> - 启用GZIP压缩减少网络传输</li>
 *   <li><b>缓存策略</b> - 对不经常变化的数据进行客户端缓存</li>
 *   <li><b>批量操作</b> - 合并多个小请求为批量请求</li>
 *   <li><b>异步调用</b> - 对于非关键路径使用异步调用</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>DeFeign接口必须是接口类型，不能是具体类</li>
 *   <li>fallback和fallbackFactory不能同时使用</li>
 *   <li>fallback实现类必须是Spring管理的Bean</li>
 *   <li>配置类中的Bean只对指定的Feign客户端生效</li>
 *   <li>url属性的优先级高于name属性</li>
 *   <li>dismiss404=true时，404错误会返回null而不是抛异常</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 * @version 1.0
 */
package io.dataease.feign;