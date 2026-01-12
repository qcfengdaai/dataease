/**
 * DataEase Web过滤器包
 * <p>
 * 本包提供DataEase系统中的Web过滤器实现，用于处理HTTP请求的预处理和后处理逻辑。
 * 主要包括缓存控制、异常处理、安全控制等功能，确保系统的稳定性和一致性。
 * </p>
 *
 * <h2>核心组件</h2>
 * <ul>
 *   <li><b>{@link io.dataease.filter.HtmlResourceFilter}</b> - HTML资源过滤器</li>
 * </ul>
 *
 * <h2>主要功能</h2>
 * <ul>
 *   <li><b>缓存管理</b> - 动态控制HTTP缓存策略，支持开发和生产环境的不同需求</li>
 *   <li><b>异常处理</b> - 统一处理过滤器链中的异常，返回标准化的错误响应</li>
 *   <li><b>响应头控制</b> - 设置HTTP响应头信息，控制浏览器缓存行为</li>
 *   <li><b>请求拦截</b> - 在请求到达Controller之前进行预处理</li>
 * </ul>
 *
 * <h2>架构设计</h2>
 * <p>本包采用<b>职责链模式</b>和<b>装饰器模式</b>：</p>
 * <ul>
 *   <li><b>Filter接口</b> - 标准的Servlet过滤器接口实现</li>
 *   <li><b>Ordered接口</b> - 控制过滤器执行顺序</li>
 *   <li><b>Spring组件</b> - 通过@Component注解自动注册到Spring容器</li>
 *   <li><b>配置驱动</b> - 通过@Value注解支持动态配置</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <ul>
 *   <li><b>开发环境</b> - 禁用缓存确保资源实时更新，便于调试</li>
 *   <li><b>测试环境</b> - 控制缓存策略，确保测试的一致性</li>
 *   <li><b>生产环境</b> - 启用缓存提高性能，减少服务器压力</li>
 *   <li><b>API服务</b> - 统一异常响应格式，提供一致的错误处理</li>
 *   <li><b>前端资源</b> - 控制静态资源的缓存策略</li>
 * </ul>
 *
 * <h2>配置和使用</h2>
 *
 * <h3>1. 基础配置</h3>
 * <p>在application.yml中配置HTTP缓存策略：</p>
 * <pre><code>
 * # DataEase HTTP缓存配置
 * dataease:
 *   http:
 *     cache: false  # 开发环境禁用缓存
 *
 * # 或者根据环境变量动态配置
 * dataease:
 *   http:
 *     cache: ${DATAEASE_HTTP_CACHE:false}
 * </code></pre>
 *
 * <h3>2. 环境特定配置</h3>
 * <pre><code>
 * # application-dev.yml (开发环境)
 * dataease:
 *   http:
 *     cache: false  # 禁用缓存，确保开发时资源实时更新
 *
 * # application-prod.yml (生产环境)
 * dataease:
 *   http:
 *     cache: true   # 启用缓存，提高生产环境性能
 *
 * # application-test.yml (测试环境)
 * dataease:
 *   http:
 *     cache: false  # 禁用缓存，确保测试一致性
 * </code></pre>
 *
 * <h3>3. Spring Boot启动类配置</h3>
 * <p>确保过滤器被正确扫描和注册：</p>
 * <pre><code>
 * &#64;SpringBootApplication
 * &#64;ComponentScan(basePackages = {
 *     "io.dataease.filter",  // 确保过滤器包被扫描
 *     // 其他包
 * })
 * public class DataEaseApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(DataEaseApplication.class, args);
 *     }
 * }
 * </code></pre>
 *
 * <h3>4. 自定义过滤器配置</h3>
 * <p>如果需要更精细的控制，可以通过FilterRegistrationBean进行配置：</p>
 * <pre><code>
 * &#64;Configuration
 * public class FilterConfig {
 *
 *     &#64;Bean
 *     public FilterRegistrationBean&lt;HtmlResourceFilter&gt; htmlResourceFilterRegistration(
 *             HtmlResourceFilter htmlResourceFilter) {
 *         FilterRegistrationBean&lt;HtmlResourceFilter&gt; registration = new FilterRegistrationBean&lt;&gt;();
 *         registration.setFilter(htmlResourceFilter);
 *
 *         // 设置过滤器URL模式
 *         registration.addUrlPatterns("/*");
 *
 *         // 设置执行顺序
 *         registration.setOrder(99);
 *
 *         // 设置过滤器名称
 *         registration.setName("htmlResourceFilter");
 *
 *         return registration;
 *     }
 * }
 * </code></pre>
 *
 * <h2>过滤器执行流程</h2>
 * <pre><code>
 * HTTP请求 → HtmlResourceFilter → 其他过滤器 → Controller
 *     ↓              ↑                ↑             ↓
 *     ↓              ↑                ↑             ↓
 *     ↓         设置缓存头           异常处理       业务逻辑
 *     ↓              ↑                ↑             ↓
 * HTTP响应 ← JSON错误响应 ← 异常捕获 ← 正常响应 ←
 * </code></pre>
 *
 * <h2>使用示例</h2>
 *
 * <h3>1. 开发环境配置示例</h3>
 * <pre><code>
 * # 开发环境application-dev.yml
 * dataease:
 *   http:
 *     cache: false
 *
 * logging:
 *   level:
 *     io.dataease.filter: DEBUG
 *
 * # 启动后的HTTP响应头会包含：
 * # Cache-Control: no-cache
 * # Cache: no-cache
 * # Pragma: no-cache
 * # Expires: 0
 * </code></pre>
 *
 * <h3>2. 生产环境配置示例</h3>
 * <pre><code>
 * # 生产环境application-prod.yml
 * dataease:
 *   http:
 *     cache: true
 *
 * # 启动后的HTTP响应头不会包含no-cache相关头信息
 * # 允许浏览器根据资源类型自动缓存
 * </code></pre>
 *
 * <h3>3. 异常处理示例</h3>
 * <p>当过滤器链中发生异常时，会返回统一格式的JSON错误响应：</p>
 * <pre><code>
 * // 异常情况下的响应示例
 * HTTP/1.1 400 Bad Request
 * Content-Type: application/json; charset=UTF-8
 *
 * {
 *   "code": 400,
 *   "message": "具体的错误信息",
 *   "data": null
 * }
 * </code></pre>
 *
 * <h3>4. 自定义扩展示例</h3>
 * <p>基于HtmlResourceFilter创建扩展过滤器：</p>
 * <pre><code>
 * &#64;Component
 * &#64;Order(98)  // 在HtmlResourceFilter之前执行
 * public class CustomSecurityFilter implements Filter {
 *
 *     &#64;Override
 *     public void doFilter(ServletRequest request, ServletResponse response,
 *                         FilterChain chain) throws IOException, ServletException {
 *         HttpServletRequest httpRequest = (HttpServletRequest) request;
 *         HttpServletResponse httpResponse = (HttpServletResponse) response;
 *
 *         // 自定义安全检查逻辑
 *         if (isValidRequest(httpRequest)) {
 *             // 继续执行过滤器链（包括HtmlResourceFilter）
 *             chain.doFilter(request, response);
 *         } else {
 *             // 返回403错误
 *             httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
 *             httpResponse.setContentType("application/json");
 *             httpResponse.getWriter().write("{\\"code\\": 403, \\"message\\": \\"Access Denied\\"}");
 *         }
 *     }
 *
 *     private boolean isValidRequest(HttpServletRequest request) {
 *         // 实现自定义的安全检查逻辑
 *         return true;
 *     }
 * }
 * </code></pre>
 *
 * <h2>缓存策略详解</h2>
 * <table border="1" style="border-collapse: collapse; width: 100%;">
 *   <tr>
 *     <th>响应头</th>
 *     <th>禁用缓存时的值</th>
 *     <th>说明</th>
 *   </tr>
 *   <tr>
 *     <td>Cache-Control</td>
 *     <td>no-cache</td>
 *     <td>指示浏览器在使用缓存前必须向服务器验证</td>
 *   </tr>
 *   <tr>
 *     <td>Cache</td>
 *     <td>no-cache</td>
 *     <td>通用缓存控制头（兼容性考虑）</td>
 *   </tr>
 *   <tr>
 *     <td>Pragma</td>
 *     <td>no-cache</td>
 *     <td>HTTP/1.0兼容的缓存控制头</td>
 *   </tr>
 *   <tr>
 *     <td>Expires</td>
 *     <td>0</td>
 *     <td>设置过期时间为过去，强制不缓存</td>
 *   </tr>
 * </table>
 *
 * <h2>过滤器顺序</h2>
 * <p>HtmlResourceFilter的执行顺序为99，这意味着：</p>
 * <ul>
 *   <li><b>Order < 99</b> - 在HtmlResourceFilter之前执行</li>
 *   <li><b>Order > 99</b> - 在HtmlResourceFilter之后执行</li>
 *   <li><b>默认顺序</b> - Spring Boot默认过滤器的顺序为0</li>
 * </ul>
 *
 * <p>常见的过滤器执行顺序：</p>
 * <pre><code>
 * 1. CharacterEncodingFilter (Order: -2147483648)
 * 2. FormContentFilter (Order: -9900)
 * 3. RequestContextFilter (Order: -105)
 * ...
 * 98. CustomSecurityFilter (Order: 98)
 * 99. HtmlResourceFilter (Order: 99)  ← 本包的过滤器
 * 100. CustomLoggingFilter (Order: 100)
 * </code></pre>
 *
 * <h2>性能考虑</h2>
 * <ul>
 *   <li><b>缓存策略</b> - 合理配置缓存可以显著提高系统性能</li>
 *   <li><b>异常处理</b> - 异常处理不会影响正常请求的性能</li>
 *   <li><b>执行顺序</b> - 将轻量级过滤器放在前面，重量级过滤器放在后面</li>
 *   <li><b>资源占用</b> - 过滤器实例在应用启动时创建，不会产生额外的内存开销</li>
 * </ul>
 *
 * <h2>监控和调试</h2>
 * <h3>启用调试日志</h3>
 * <pre><code>
 * # application.yml
 * logging:
 *   level:
 *     io.dataease.filter: DEBUG
 *     org.springframework.web.filter: DEBUG
 *     javax.servlet: DEBUG
 * </code></pre>
 *
 * <h3>监控指标</h3>
 * <p>可以通过Spring Boot Actuator监控过滤器的执行情况：</p>
 * <pre><code>
 * # 启用监控端点
 * management:
 *   endpoints:
 *     web:
 *       exposure:
 *         include: health, info, metrics, httptrace
 *   endpoint:
 *     httptrace:
 *       enabled: true
 * </code></pre>
 *
 * <h2>最佳实践</h2>
 * <ol>
 *   <li><b>环境配置</b> - 根据不同环境合理配置缓存策略</li>
 *   <li><b>异常处理</b> - 确保过滤器中的异常不会影响其他功能</li>
 *   <li><b>性能优化</b> - 避免在过滤器中执行耗时操作</li>
 *   <li><b>顺序控制</b> - 合理设置过滤器执行顺序</li>
 *   <li><b>日志记录</b> - 在关键节点添加适当的日志</li>
 *   <li><b>测试覆盖</b> - 编写单元测试验证过滤器功能</li>
 * </ol>
 *
 * <h2>故障排查</h2>
 * <h3>常见问题</h3>
 * <ul>
 *   <li><b>过滤器未生效</b> - 检查@Component注解和包扫描配置</li>
 *   <li><b>缓存配置无效</b> - 检查配置文件中的属性名和值</li>
 *   <li><b>异常响应格式错误</b> - 检查JsonUtil和ResultMessage类</li>
 *   <li><b>执行顺序错误</b> - 检查@Order注解的值</li>
 * </ul>
 *
 * <h3>调试步骤</h3>
 * <ol>
 *   <li>启用DEBUG日志级别</li>
 *   <li>检查过滤器注册情况</li>
 *   <li>验证配置值是否正确加载</li>
 *   <li>测试异常处理逻辑</li>
 *   <li>确认响应头设置是否生效</li>
 * </ol>
 *
 * <h2>扩展指南</h2>
 * <p>如需添加新的过滤器功能：</p>
 * <ol>
 *   <li>确定过滤器的职责和功能边界</li>
 *   <li>选择合适的执行顺序（Order值）</li>
 *   <li>实现Filter接口和必要的Spring注解</li>
 *   <li>添加相应的配置项支持</li>
 *   <li>编写完整的文档和测试</li>
 *   <li>更新本包的文档说明</li>
 * </ol>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>过滤器在Spring容器启动时注册，运行时无法动态修改</li>
 *   <li>过滤器的异常处理应该保证系统的稳定性</li>
 *   <li>缓存控制头的设置会影响浏览器和CDN的缓存行为</li>
 *   <li>生产环境中应谨慎配置缓存策略</li>
 *   <li>过滤器的性能直接影响所有HTTP请求的处理速度</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 * @version 1.0
 */
package io.dataease.filter;