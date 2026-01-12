/**
 * DataEase分布式核心功能包
 *
 * <p>本包提供了DataEase分布式环境下的核心功能组件，包括动态数据源管理、
 * Feign客户端配置、数据库迁移工具等关键功能。</p>
 *
 * <h2>主要功能模块：</h2>
 *
 * <h3>1. 动态数据源管理 (dds包)</h3>
 * <ul>
 *   <li><b>核心类：</b></li>
 *   <ul>
 *     <li>{@link io.dataease.dds.DynamicDataSource} - 动态数据源路由器</li>
 *     <li>{@link io.dataease.dds.DynamicContextHolder} - 线程安全的上下文管理器</li>
 *   </ul>
 *   <li><b>配置类：</b></li>
 *   <ul>
 *     <li>{@link io.dataease.dds.config.DynamicDsConfig} - 动态数据源配置类</li>
 *     <li>{@link io.dataease.dds.config.DynamicDataSourceProperties} - 数据源属性配置</li>
 *   </ul>
 *   <li><b>拦截器：</b></li>
 *   <ul>
 *     <li>{@link io.dataease.dds.interceptor.DSInterceptor} - 数据源切换拦截器</li>
 *   </ul>
 *   <li><b>提供者：</b></li>
 *   <ul>
 *     <li>{@link io.dataease.dds.provider.TenantDatasourceProvider} - 租户数据源提供者</li>
 *   </ul>
 *   <li><b>常量：</b></li>
 *   <ul>
 *     <li>{@link io.dataease.dds.constant.DataSourceConstant} - 数据源相关常量</li>
 *   </ul>
 * </ul>
 *
 * <h3>2. RPC远程调用 (rpc包)</h3>
 * <ul>
 *   <li><b>核心功能：</b></li>
 *   <ul>
 *     <li>{@link io.dataease.rpc.DeFeignRegister} - Feign客户端注册器</li>
 *     <li>{@link io.dataease.DeFeignConfiguration} - Feign客户端自动配置</li>
 *   </ul>
 * </ul>
 *
 * <h3>3. 数据库迁移 (flyway包)</h3>
 * <ul>
 *   <li><b>工具类：</b></li>
 *   <ul>
 *     <li>{@link io.dataease.flyway.TenantFlywayUtil} - 租户数据库迁移工具</li>
 *   </ul>
 * </ul>
 *
 * <h2>使用场景：</h2>
 * <ol>
 *   <li><b>多租户数据库管理：</b>支持为不同租户提供独立的数据源</li>
 *   <li><b>微服务通信：</b>提供服务间RPC调用的统一解决方案</li>
 *   <li><b>数据库版本管理：</b>支持租户数据库的结构升级和数据迁移</li>
 *   <li><b>分布式配置：</b>动态配置和管理分布式环境下的各种组件</li>
 * </ol>
 *
 * <h2>核心设计理念：</h2>
 * <ul>
 *   <li><b>租户隔离：</b>每个租户拥有独立的数据库实例和配置</li>
 *   <li><b>动态切换：</b>支持在运行时动态切换数据源和服务端点</li>
 *   <li><b>线程安全：</b>基于ThreadLocal保证多线程环境下的数据一致性</li>
 *   <li><b>配置驱动：</b>通过配置文件和注解驱动功能开启和参数设置</li>
 * </ul>
 *
 * <h2>使用示例：</h2>
 *
 * <h3>1. 动态数据源使用示例：</h3>
 * <pre>{@code
 * // 在Spring配置类中启用动态数据源
 * @Configuration
 * @EnableConfigurationProperties(DynamicDataSourceProperties.class)
 * public class DataSourceConfig {
 *
 *     @Bean
 *     @Primary
 *     public DynamicDataSource dynamicDataSource(
 *             DynamicDataSourceProperties properties) {
 *         DynamicDataSource dynamicDataSource = new DynamicDataSource();
 *         // 配置默认数据源和目标数据源
 *         return dynamicDataSource;
 *     }
 * }
 *
 * // 在业务代码中切换数据源
 * public class TenantService {
 *
 *     public void processData(String tenantId) {
 *         // 推入租户数据源键值
 *         DynamicContextHolder.push("tenant_" + tenantId);
 *         try {
 *             // 执行数据库操作，会自动路由到对应的租户数据源
 *             performDatabaseOperation();
 *         } finally {
 *             // 恢复到上一个数据源
 *             DynamicContextHolder.poll();
 *         }
 *     }
 * }
 * }</pre>
 *
 * <h3>2. Feign客户端使用示例：</h3>
 * <pre>{@code
 * // 启用Feign客户端配置
 * @Configuration
 * @Import(DeFeignConfiguration.class)
 * public class AppConfig {
 * }
 *
 * // 定义Feign客户端接口
 * @DeFeign(name = "user-service", url = "http://localhost:8080")
 * public interface UserServiceClient {
 *
 *     @GetMapping("/users/{id}")
 *     User getUserById(@PathVariable("id") Long id);
 *
 *     @PostMapping("/users")
 *     User createUser(@RequestBody User user);
 * }
 *
 * // 在业务代码中使用
 * @Service
 * public class UserBusinessService {
 *
 *     @Autowired
 *     private UserServiceClient userServiceClient;
 *
 *     public User getUser(Long id) {
 *         return userServiceClient.getUserById(id);
 *     }
 * }
 * }</pre>
 *
 * <h3>3. 数据库迁移使用示例：</h3>
 * <pre>{@code
 * public class DatabaseInitializer {
 *
 *     public void initializeTenantDatabase(DataSource tenantDataSource,
 *                                          String tenantId) throws Exception {
 *         // 执行租户数据库迁移
 *         TenantFlywayUtil.executeFlyway(tenantDataSource, false, tenantId);
 *     }
 *
 *     public void initializeManageDatabase(DataSource manageDataSource) throws Exception {
 *         // 执行管理数据库迁移
 *         TenantFlywayUtil.executeFlyway(manageDataSource, true, null);
 *     }
 * }
 * }</pre>
 *
 * <h3>4. 数据源拦截器使用示例：</h3>
 * <pre>{@code
 * @Configuration
 * public class WebConfig implements WebMvcConfigurer {
 *
 *     @Override
 *     public void addInterceptors(InterceptorRegistry registry) {
 *         // 注册数据源切换拦截器
 *         registry.addInterceptor(new DSInterceptor())
 *                .addPathPatterns("/api/**")  // 拦截API请求
 *                .excludePathPatterns("/health", "/info");  // 排除健康检查
 *     }
 * }
 *
 * // 拦截器会自动从请求头中读取租户信息：
 * // Header: tenant=12345
 * // Header: time=1640995200000
 * // 自动切换到对应的租户数据源
 * }</pre>
 *
 * <h2>配置参数：</h2>
 * <table border="1">
 *   <tr><th>配置项</th><th>说明</th><th>默认值</th></tr>
 *   <tr><td>spring.cloud.openfeign.lazy-attributes-resolution</td><td>Feign属性延迟解析</td><td>false</td></tr>
 *   <tr><td>spring.cloud.openfeign.client.refresh-enabled</td><td>客户端刷新功能</td><td>false</td></tr>
 * </table>
 *
 * <h2>注意事项：</h2>
 * <ul>
 *   <li><b>线程安全：</b>DynamicContextHolder基于ThreadLocal，确保在finally块中调用poll()方法</li>
 *   <li><b>数据库连接：</b>每个租户需要独立的数据库配置，建议使用连接池管理</li>
 *   <li><b>服务发现：</b>Feign客户端支持服务发现，建议配合Eureka或Consul使用</li>
 *   <li><b>版本管理：</b>数据库迁移脚本需要严格按照版本号命名和管理</li>
 * </ul>
 *
 * @author DataEase Team
 * @version 1.0
 * @since 1.0
 */
package io.dataease;