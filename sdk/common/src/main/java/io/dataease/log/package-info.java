/**
 * DataEase操作日志包
 * <p>
 * 本包提供DataEase系统的操作日志记录功能，通过AOP切面自动拦截和记录用户的各种操作行为。
 * 主要用于系统审计、安全监控、操作追踪和合规性管理等场景。
 * </p>
 *
 * <h2>核心组件</h2>
 * <ul>
 *   <li><b>{@link io.dataease.log.DeLog}</b> - 操作日志注解，用于标记需要记录日志的方法</li>
 *   <li><b>{@link io.dataease.constant.LogOT}</b> - 操作类型枚举，定义所有可能的操作类型</li>
 *   <li><b>{@link io.dataease.constant.LogST}</b> - 资源类型枚举，定义所有可操作的资源类型</li>
 * </ul>
 *
 * <h2>主要功能</h2>
 * <ul>
 *   <li><b>操作记录</b> - 自动记录用户对系统资源的各种操作</li>
 *   <li><b>权限审计</b> - 记录权限授予和撤销操作</li>
 *   <li><b>数据追踪</b> - 追踪数据的创建、修改、删除过程</li>
 *   <li><b>文件监控</b> - 记录文件的上传、下载、导出等操作</li>
 *   <li><b>任务管理</b> - 记录定时任务和同步任务的启停和执行</li>
 *   <li><b>系统安全</b> - 记录登录、授权等安全相关操作</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <ul>
 *   <li><b>合规审计</b> - 满足企业内控和监管要求的操作审计</li>
 *   <li><b>安全监控</b> - 监控异常操作和安全威胁</li>
 *   <li><b>故障排查</b> - 通过操作日志定位系统问题</li>
 *   <li><b>用户行为分析</b> - 分析用户使用习惯和系统使用情况</li>
 *   <li><b>数据保护</b> - 追踪敏感数据的访问和操作记录</li>
 * </ul>
 *
 * <h2>@DeLog注解使用指南</h2>
 *
 * <h3>基本使用方式</h3>
 * <pre><code>
 * &#64;RestController
 * public class UserController {
 *
 *     // 用户创建操作
 *     &#64;DeLog(ot = LogOT.CREATE, st = LogST.USER, id = "#result.id")
 *     &#64;PostMapping("/users")
 *     public Result&lt;User&gt; createUser(&#64;RequestBody User user) {
 *         User createdUser = userService.create(user);
 *         return Result.success(createdUser);
 *     }
 *
 *     // 用户修改操作
 *     &#64;DeLog(ot = LogOT.MODIFY, st = LogST.USER, id = "#userId")
 *     &#64;PutMapping("/users/{userId}")
 *     public Result&lt;Void&gt; updateUser(&#64;PathVariable Long userId, &#64;RequestBody User user) {
 *         userService.update(userId, user);
 *         return Result.success();
 *     }
 *
 *     // 用户删除操作
 *     &#64;DeLog(ot = LogOT.DELETE, st = LogST.USER, id = "#userId")
 *     &#64;DeleteMapping("/users/{userId}")
 *     public Result&lt;Void&gt; deleteUser(&#64;PathVariable Long userId) {
 *         userService.delete(userId);
 *         return Result.success();
 *     }
 * }
 * </code></pre>
 *
 * <h3>数据源管理示例</h3>
 * <pre><code>
 * &#64;RestController
 * public class DatasourceController {
 *
 *     // 创建数据源
 *     &#64;DeLog(ot = LogOT.CREATE, st = LogST.DATASOURCE, id = "#result.data.id")
 *     &#64;PostMapping("/datasources")
 *     public Result&lt;Datasource&gt; create(&#64;RequestBody Datasource datasource) {
 *         return Result.success(datasourceService.save(datasource));
 *     }
 *
 *     // 测试数据源连接
 *     &#64;DeLog(ot = LogOT.READ, st = LogST.DATASOURCE, id = "#datasourceId")
 *     &#64;PostMapping("/datasources/{datasourceId}/test")
 *     public Result&lt;Boolean&gt; testConnection(&#64;PathVariable Long datasourceId) {
 *         return Result.success(datasourceService.testConnection(datasourceId));
 *     }
 *
 *     // 删除数据源
 *     &#64;DeLog(ot = LogOT.DELETE, st = LogST.DATASOURCE, id = "#datasourceId")
 *     &#64;DeleteMapping("/datasources/{datasourceId}")
 *     public Result&lt;Void&gt; delete(&#64;PathVariable Long datasourceId) {
 *         datasourceService.delete(datasourceId);
 *         return Result.success();
 *     }
 * }
 * </code></pre>
 *
 * <h3>权限管理示例</h3>
 * <pre><code>
 * &#64;RestController
 * public class PermissionController {
 *
 *     // 授权操作
 *     &#64;DeLog(ot = LogOT.AUTHORIZE, st = LogST.PANEL,
 *            id = "#request.resourceId", pid = "#request.userId")
 *     &#64;PostMapping("/permissions/grant")
 *     public Result&lt;Void&gt; grantPermission(&#64;RequestBody PermissionRequest request) {
 *         permissionService.grant(request.getUserId(), request.getResourceId());
 *         return Result.success();
 *     }
 *
 *     // 撤销授权
 *     &#64;DeLog(ot = LogOT.UNAUTHORIZE, st = LogST.PANEL,
 *            id = "#request.resourceId", pid = "#request.userId")
 *     &#64;PostMapping("/permissions/revoke")
 *     public Result&lt;Void&gt; revokePermission(&#64;RequestBody PermissionRequest request) {
 *         permissionService.revoke(request.getUserId(), request.getResourceId());
 *         return Result.success();
 *     }
 * }
 * </code></pre>
 *
 * <h3>文件操作示例</h3>
 * <pre><code>
 * &#64;RestController
 * public class FileController {
 *
 *     // 文件上传
 *     &#64;DeLog(ot = LogOT.UPLOADFILE, st = LogST.DRIVER_FILE, id = "#result.data.id")
 *     &#64;PostMapping("/files/upload")
 *     public Result&lt;FileInfo&gt; uploadFile(&#64;RequestParam("file") MultipartFile file) {
 *         FileInfo fileInfo = fileService.upload(file);
 *         return Result.success(fileInfo);
 *     }
 *
 *     // 文件下载
 *     &#64;DeLog(ot = LogOT.DOWNLOAD, st = LogST.DRIVER_FILE, id = "#fileId")
 *     &#64;GetMapping("/files/{fileId}/download")
 *     public ResponseEntity&lt;byte[]&gt; downloadFile(&#64;PathVariable Long fileId) {
 *         return fileService.download(fileId);
 *     }
 *
 *     // 导出PDF
 *     &#64;DeLog(ot = LogOT.PDF_EXPORT, st = LogST.PANEL, id = "#panelId")
 *     &#64;PostMapping("/panels/{panelId}/export/pdf")
 *     public Result&lt;String&gt; exportToPdf(&#64;PathVariable Long panelId) {
 *         String downloadUrl = exportService.exportToPdf(panelId);
 *         return Result.success(downloadUrl);
 *     }
 * }
 * </code></pre>
 *
 * <h3>任务管理示例</h3>
 * <pre><code>
 * &#64;RestController
 * public class TaskController {
 *
 *     // 启用报表任务
 *     &#64;DeLog(ot = LogOT.TASK_ENABLE, st = LogST.REPORT_TASK, id = "#taskId")
 *     &#64;PostMapping("/tasks/{taskId}/enable")
 *     public Result&lt;Void&gt; enableTask(&#64;PathVariable Long taskId) {
 *         taskService.enable(taskId);
 *         return Result.success();
 *     }
 *
 *     // 禁用报表任务
 *     &#64;DeLog(ot = LogOT.TASK_DISENABLE, st = LogST.REPORT_TASK, id = "#taskId")
 *     &#64;PostMapping("/tasks/{taskId}/disable")
 *     public Result&lt;Void&gt; disableTask(&#64;PathVariable Long taskId) {
 *         taskService.disable(taskId);
 *         return Result.success();
 *     }
 *
 *     // 立即执行任务
 *     &#64;DeLog(ot = LogOT.TASK_RUN_IMMEDIATELY, st = LogST.REPORT_TASK, id = "#taskId")
 *     &#64;PostMapping("/tasks/{taskId}/run")
 *     public Result&lt;Void&gt; runTaskImmediately(&#64;PathVariable Long taskId) {
 *         taskService.runImmediately(taskId);
 *         return Result.success();
 *     }
 * }
 * </code></pre>
 *
 * <h3>使用SpEL表达式动态获取参数</h3>
 * <pre><code>
 * &#64;RestController
 * public class DatasetController {
 *
 *     // 从方法参数中获取ID
 *     &#64;DeLog(ot = LogOT.MODIFY, st = LogST.DATASET, id = "#dataset.id")
 *     public Result&lt;Void&gt; updateDataset(&#64;RequestBody Dataset dataset) {
 *         datasetService.update(dataset);
 *         return Result.success();
 *     }
 *
 *     // 从返回值中获取ID
 *     &#64;DeLog(ot = LogOT.CREATE, st = LogST.DATASET, id = "#result.data.id")
 *     public Result&lt;Dataset&gt; createDataset(&#64;RequestBody Dataset dataset) {
 *         Dataset created = datasetService.create(dataset);
 *         return Result.success(created);
 *     }
 *
 *     // 使用表达式动态确定资源类型
 *     &#64;DeLog(ot = LogOT.EXPORT, stExp = "#type == 'template' ? T(io.dataease.constant.LogST).TEMPLATE_EXPORT : T(io.dataease.constant.LogST).EXPORT",
 *            id = "#datasetId")
 *     public Result&lt;String&gt; exportData(&#64;PathVariable Long datasetId, &#64;RequestParam String type) {
 *         return Result.success(exportService.export(datasetId, type));
 *     }
 * }
 * </code></pre>
 *
 * <h2>注解参数详解</h2>
 * <table border="1" style="border-collapse: collapse; width: 100%;">
 *   <tr>
 *     <th>参数</th>
 *     <th>类型</th>
 *     <th>必填</th>
 *     <th>说明</th>
 *     <th>示例</th>
 *   </tr>
 *   <tr>
 *     <td>ot</td>
 *     <td>LogOT</td>
 *     <td>是</td>
 *     <td>操作类型，定义具体的操作行为</td>
 *     <td>LogOT.CREATE, LogOT.MODIFY, LogOT.DELETE</td>
 *   </tr>
 *   <tr>
 *     <td>st</td>
 *     <td>LogST</td>
 *     <td>否</td>
 *     <td>资源类型，默认为PANEL</td>
 *     <td>LogST.USER, LogST.DATASOURCE, LogST.DATASET</td>
 *   </tr>
 *   <tr>
 *     <td>id</td>
 *     <td>String</td>
 *     <td>否</td>
 *     <td>操作对象ID，支持SpEL表达式</td>
 *     <td>"#userId", "#result.data.id", "#request.id"</td>
 *   </tr>
 *   <tr>
 *     <td>pid</td>
 *     <td>String</td>
 *     <td>否</td>
 *     <td>父级对象ID，支持SpEL表达式</td>
 *     <td>"#datasourceId", "#result.parentId"</td>
 *   </tr>
 *   <tr>
 *     <td>stExp</td>
 *     <td>String</td>
 *     <td>否</td>
 *     <td>资源类型表达式，用于动态计算资源类型</td>
 *     <td>"#type == 'user' ? T(LogST).USER : T(LogST).ROLE"</td>
 *   </tr>
 * </table>
 *
 * <h2>操作类型(LogOT)分类</h2>
 * <h3>基础CRUD操作</h3>
 * <ul>
 *   <li><b>CREATE</b> - 创建新资源</li>
 *   <li><b>MODIFY</b> - 修改现有资源</li>
 *   <li><b>DELETE</b> - 删除资源</li>
 *   <li><b>READ</b> - 读取资源（重要数据访问）</li>
 * </ul>
 *
 * <h3>权限管理</h3>
 * <ul>
 *   <li><b>AUTHORIZE</b> - 授权</li>
 *   <li><b>UNAUTHORIZE</b> - 撤销授权</li>
 *   <li><b>BIND</b> - 绑定关联</li>
 *   <li><b>UNBIND</b> - 解除绑定</li>
 * </ul>
 *
 * <h3>文件操作</h3>
 * <ul>
 *   <li><b>UPLOADFILE</b> - 上传文件</li>
 *   <li><b>DOWNLOAD</b> - 下载文件</li>
 *   <li><b>EXPORT</b> - 通用导出</li>
 *   <li><b>TEMPLATE_EXPORT</b> - 模板导出</li>
 *   <li><b>PDF_EXPORT</b> - PDF导出</li>
 *   <li><b>IMG_EXPORT</b> - 图片导出</li>
 * </ul>
 *
 * <h3>任务管理</h3>
 * <ul>
 *   <li><b>TASK_ENABLE</b> - 启用任务</li>
 *   <li><b>TASK_DISENABLE</b> - 禁用任务</li>
 *   <li><b>TASK_RUN_IMMEDIATELY</b> - 立即执行任务</li>
 *   <li><b>SYNC_TASK_ENABLE</b> - 启用同步任务</li>
 *   <li><b>SYNC_TASK_DISENABLE</b> - 禁用同步任务</li>
 *   <li><b>SYNC_TASK_RUN_IMMEDIATELY</b> - 立即执行同步任务</li>
 *   <li><b>SYNC_TASK_RUN_TERMINATION</b> - 终止同步任务</li>
 * </ul>
 *
 * <h2>资源类型(LogST)分类</h2>
 * <h3>数据相关</h3>
 * <ul>
 *   <li><b>DATASOURCE</b> - 数据源</li>
 *   <li><b>DATASET</b> - 数据集</li>
 *   <li><b>DATA</b> - 数据记录</li>
 * </ul>
 *
 * <h3>可视化</h3>
 * <ul>
 *   <li><b>PANEL</b> - 仪表板</li>
 *   <li><b>SCREEN</b> - 数据大屏</li>
 *   <li><b>VIEW</b> - 视图组件</li>
 * </ul>
 *
 * <h3>用户管理</h3>
 * <ul>
 *   <li><b>USER</b> - 用户</li>
 *   <li><b>ROLE</b> - 角色</li>
 *   <li><b>ORG</b> - 组织部门</li>
 * </ul>
 *
 * <h3>系统功能</h3>
 * <ul>
 *   <li><b>MENU</b> - 系统菜单</li>
 *   <li><b>DRIVER</b> - 数据库驱动</li>
 *   <li><b>APIKEY</b> - API密钥</li>
 *   <li><b>LINK</b> - 分享链接</li>
 * </ul>
 *
 * <h2>最佳实践</h2>
 * <ol>
 *   <li><b>合理选择操作类型</b> - 根据实际业务操作选择合适的LogOT类型</li>
 *   <li><b>准确标识资源</b> - 使用准确的LogST类型标识操作的资源类型</li>
 *   <li><b>善用SpEL表达式</b> - 利用SpEL表达式动态获取参数，提高注解的灵活性</li>
 *   <li><b>记录关键操作</b> - 重点记录涉及数据安全、权限变更的关键操作</li>
 *   <li><b>避免过度记录</b> - 对于查询类操作，只记录重要数据的访问</li>
 *   <li><b>保持一致性</b> - 在同一模块中保持日志记录的一致性和完整性</li>
 * </ol>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>日志注解依赖AOP机制，确保被注解的方法能被Spring代理</li>
 *   <li>SpEL表达式在解析失败时会记录错误，但不会影响业务执行</li>
 *   <li>日志记录是异步执行的，不会影响主业务性能</li>
 *   <li>避免在高频调用的方法上使用日志注解，防止产生过多日志</li>
 *   <li>日志数据包含敏感信息，需要妥善保管和定期清理</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2023-04-03
 * @version 1.0
 */
package io.dataease.log;