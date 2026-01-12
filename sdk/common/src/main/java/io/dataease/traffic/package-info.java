/**
 * DataEase API流量控制模块
 *
 * <h2>模块概述</h2>
 * 该包提供基于注解的API并发限流功能，通过AOP机制实现对API方法的并发访问控制。
 * 支持方法级别的细粒度限流配置，有效防止系统因高并发访问而过载。
 *
 * <h2>核心组件</h2>
 * <ul>
 *   <li>{@link io.dataease.traffic.DeTraffic} - 流量控制注解，用于标记需要限流的方法</li>
 *   <li>{@link io.dataease.traffic.DeTrafficAop} - AOP切面类，实现流量控制核心逻辑</li>
 *   <li>{@link io.dataease.traffic.dao.entity.CoreApiTraffic} - 流量控制数据实体</li>
 *   <li>{@link io.dataease.traffic.dao.mapper.CoreApiTrafficMapper} - 数据访问层</li>
 *   <li>{@link io.dataease.traffic.starter.DeTrafficStarter} - 应用启动器，负责数据初始化</li>
 * </ul>
 *
 * <h2>工作原理</h2>
 * <ol>
 *   <li>在需要限流的方法上标注{@code @DeTraffic}注解</li>
 *   <li>AOP切面拦截被注解的方法调用</li>
 *   <li>查询数据库获取当前API的并发数</li>
 *   <li>如果未超过阈值，允许执行并增加并发计数</li>
 *   <li>如果超过阈值，抛出异常拒绝访问</li>
 *   <li>方法执行完成后释放并发计数</li>
 * </ol>
 *
 * <h2>使用方式</h2>
 *
 * <h3>基本用法</h3>
 * <pre>{@code
 * @Service
 * public class UserService {
 *
 *     // 使用默认并发阈值(配置文件中的dataease.traffic值)
 *     @DeTraffic(api = "user.register")
 *     public Result register(UserDto userDto) {
 *         // 用户注册逻辑
 *         return Result.success();
 *     }
 *
 *     // 自定义并发阈值为3
 *     @DeTraffic(api = "user.login", value = 3)
 *     public Result login(String username, String password) {
 *         // 用户登录逻辑
 *         return Result.success();
 *     }
 *
 *     // 对批量操作进行严格限流，只允许1个并发
 *     @DeTraffic(api = "user.batch.import", value = 1)
 *     public Result batchImport(List<UserDto> users) {
 *         // 批量导入用户
 *         return Result.success();
 *     }
 * }
 * }</pre>
 *
 * <h3>API标识命名建议</h3>
 * <ul>
 *   <li>使用层级结构：{@code module.function}</li>
 *   <li>避免使用特殊字符，建议使用小写字母、数字和点号</li>
 *   <li>具有业务含义，便于维护和监控</li>
 * </ul>
 *
 * <h2>配置说明</h2>
 *
 * <h3>默认并发配置</h3>
 * 在{@code application.yml}中配置默认并发阈值：
 * <pre>{@code
 * dataease:
 *   traffic: 2  # 默认并发阈值，当@DeTraffic注解的value为0时使用
 * }</pre>
 *
 * <h3>数据库表结构</h3>
 * 需要创建{@code core_api_traffic}表：
 * <pre>{@code
 * CREATE TABLE core_api_traffic (
 *     id BIGINT PRIMARY KEY,      -- 主键ID
 *     api VARCHAR(255) NOT NULL,  -- API标识
 *     threshold INT NOT NULL,     -- 并发阈值
 *     alive INT NOT NULL         -- 当前活跃并发数
 * );
 *
 * CREATE INDEX idx_api ON core_api_traffic(api);
 * }</pre>
 *
 * <h2>被使用的位置</h2>
 * 该模块主要在以下场景中使用：
 * <ul>
 *   <li><strong>Core模块</strong> - 主要业务逻辑的API限流</li>
 *   <li><strong>数据源连接</strong> - 限制同时连接数据源的并发数</li>
 *   <li><strong>报表生成</strong> - 防止同时生成大量报表导致系统过载</li>
 *   <li><strong>数据导入导出</strong> - 控制大数据量操作的并发</li>
 *   <li><strong>用户认证</strong> - 限制登录、注册等关键操作的并发</li>
 * </ul>
 *
 * <h2>实际应用示例</h2>
 *
 * <h3>数据源管理</h3>
 * <pre>{@code
 * @RestController
 * public class DatasourceController {
 *
 *     @PostMapping("/test")
 *     @DeTraffic(api = "datasource.test", value = 5)
 *     public Result testConnection(@RequestBody DatasourceDto dto) {
 *         // 数据源连接测试，最多允许5个并发
 *         return datasourceService.testConnection(dto);
 *     }
 * }
 * }</pre>
 *
 * <h3>报表生成</h3>
 * <pre>{@code
 * @Service
 * public class ReportService {
 *
 *     @DeTraffic(api = "report.export.excel", value = 3)
 *     public void exportExcel(Long reportId, HttpServletResponse response) {
 *         // Excel导出，限制并发数为3
 *         // 导出逻辑...
 *     }
 *
 *     @DeTraffic(api = "report.preview", value = 10)
 *     public ReportData preview(ReportRequest request) {
 *         // 报表预览，允许更高的并发数
 *         return generateReport(request);
 *     }
 * }
 * }</pre>
 *
 * <h2>注意事项和限制</h2>
 * <ul>
 *   <li><strong>数据库依赖</strong>：基于数据库计数器实现，需要确保数据库连接正常</li>
 *   <li><strong>性能考虑</strong>：每次方法调用都会进行数据库查询和更新，对性能有一定影响</li>
 *   <li><strong>分布式环境</strong>：在多实例部署时，限流是基于整个集群的，而不是单个实例</li>
 *   <li><strong>异常安全</strong>：即使方法执行异常，也会正确释放并发计数</li>
 *   <li><strong>应用重启</strong>：应用启动时会清空所有流量计数，重置为初始状态</li>
 *   <li><strong>API标识唯一性</strong>：确保不同业务使用不同的API标识，避免冲突</li>
 * </ul>
 *
 * <h2>错误处理</h2>
 * 当达到限流阈值时，系统会抛出{@link io.dataease.exception.DEException}异常，
 * 包含详细的错误信息，指明当前API和设定的阈值。
 *
 * <h2>监控和调试</h2>
 * <ul>
 *   <li>可以通过查询{@code core_api_traffic}表了解各API的使用情况</li>
 *   <li>日志中会记录流量控制过程中的异常信息</li>
 *   <li>建议在生产环境中配置适当的监控告警</li>
 * </ul>
 *
 * @author DataEase Team
 * @since 1.0.0
 * @version 1.0.0
 */
package io.dataease.traffic;