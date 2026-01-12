/**
 * DataEase国际化(i18n)功能包
 *
 * <h2>模块概述</h2>
 * 该包提供完整的国际化功能支持，包括多语言消息翻译、动态资源加载、
 * 复杂对象自动翻译等功能，为DataEase系统提供全面的多语言支持。
 *
 * <h2>核心组件</h2>
 * <ul>
 *   <li>{@link io.dataease.i18n.I18n} - 国际化注解，标记需要翻译的方法</li>
 *   <li>{@link io.dataease.i18n.Lang} - 语言枚举，定义支持的语言类型</li>
 *   <li>{@link io.dataease.i18n.Translator} - 翻译器核心类，提供各种翻译功能</li>
 *   <li>{@link io.dataease.i18n.DeI18nStarter} - 国际化启动器，应用启动时加载资源</li>
 *   <li>{@link io.dataease.i18n.DynamicI18nUtils} - 动态国际化工具，运行时资源管理</li>
 *   <li>{@link io.dataease.i18n.DeI18nMessageConfig} - 国际化配置类，Spring Bean配置</li>
 *   <li>{@link io.dataease.i18n.DeReloadableResourceBundleMessageSource} - 自定义消息源</li>
 * </ul>
 *
 * <h2>支持的语言</h2>
 * <ul>
 *   <li>简体中文 (zh-CN) - 默认语言</li>
 *   <li>繁体中文 (zh-TW) - 支持台湾和香港</li>
 *   <li>英语 (en-US) - 国际化标准语言</li>
 * </ul>
 *
 * <h2>功能特性</h2>
 * <ol>
 *   <li><strong>自动语言检测</strong> - 根据HTTP请求头自动识别客户端语言</li>
 *   <li><strong>动态资源加载</strong> - 支持运行时动态加载新的国际化资源</li>
 *   <li><strong>复杂对象翻译</strong> - 递归翻译对象的所有字符串属性</li>
 *   <li><strong>JSON字符串支持</strong> - 自动解析和翻译JSON格式的字符串</li>
 *   <li><strong>缓存优化</strong> - 支持翻译结果缓存，提高性能</li>
 *   <li><strong>热更新</strong> - 无需重启应用即可更新国际化资源</li>
 * </ol>
 *
 * <h2>使用方式</h2>
 *
 * <h3>1. 基本消息翻译</h3>
 * <pre>{@code
 * // 简单翻译
 * String message = Translator.get("user.login.success");
 *
 * // 带参数翻译
 * String message = Translator.get("user.welcome.message", "张三", "管理员");
 * // 对应资源文件中：user.welcome.message=欢迎{0}，您的角色是{1}
 * }</pre>
 *
 * <h3>2. 对象自动翻译</h3>
 * <pre>{@code
 * // 翻译复杂对象
 * public class UserDTO {
 *     private String name;
 *     private String role;
 *     private String status;
 *     // getters and setters...
 * }
 *
 * UserDTO user = getUserData();
 * Translator.translateObject(user); // 自动翻译所有字符串属性
 * }</pre>
 *
 * <h3>3. 使用@I18n注解</h3>
 * <pre>{@code
 * @RestController
 * public class UserController {
 *
 *     @GetMapping("/users")
 *     @I18n("user.list") // 标记返回结果需要国际化
 *     public List<UserDTO> getUsers() {
 *         return userService.getAllUsers();
 *     }
 *
 *     @PostMapping("/users")
 *     @I18n // 使用默认翻译逻辑
 *     public Result createUser(@RequestBody UserDTO user) {
 *         return userService.createUser(user);
 *     }
 * }
 * }</pre>
 *
 * <h3>4. 语言判断和切换</h3>
 * <pre>{@code
 * // 判断当前是否为中文环境
 * if (Lang.isChinese()) {
 *     // 中文特定逻辑
 *     return "中文处理逻辑";
 * }
 *
 * // 根据语言标识获取语言枚举
 * Lang lang = Lang.getLang("en-US"); // 返回 Lang.en_US
 * Lang lang2 = Lang.getLang("zh-CN"); // 返回 Lang.zh_CN
 * }</pre>
 *
 * <h3>5. 动态添加国际化资源</h3>
 * <pre>{@code
 * // 运行时动态加载新的国际化文件
 * DynamicI18nUtils.addOrUpdate("file:/opt/dataease/custom/i18n/messages");
 * DynamicI18nUtils.addOrUpdate("classpath:i18n/plugin-messages");
 * }</pre>
 *
 * <h2>配置说明</h2>
 *
 * <h3>application.yml配置</h3>
 * <pre>{@code
 * spring:
 *   messages:
 *     basename: i18n/messages,i18n/validation,i18n/errors
 *     encoding: UTF-8
 *     cache-duration: 3600
 *
 * dataease:
 *   path:
 *     i18n: file:/opt/dataease2.0/data/i18n/custom
 * }</pre>
 *
 * <h3>资源文件结构</h3>
 * <pre>
 * src/main/resources/
 * ├── i18n/
 * │   ├── messages.properties         # 默认消息
 * │   ├── messages_zh_CN.properties   # 中文简体
 * │   ├── messages_zh_TW.properties   # 中文繁体
 * │   ├── messages_en_US.properties   # 英文
 * │   ├── validation_zh_CN.properties # 验证消息
 * │   └── errors_zh_CN.properties     # 错误消息
 * </pre>
 *
 * <h2>被使用的位置</h2>
 *
 * <h3>Core模块中的使用</h3>
 * <ul>
 *   <li><strong>API响应翻译</strong> - Controller层的返回数据自动翻译</li>
 *   <li><strong>数据字典翻译</strong> - 字典类型、状态等枚举值的翻译</li>
 *   <li><strong>错误消息翻译</strong> - 异常处理和错误提示的多语言支持</li>
 *   <li><strong>数据导出翻译</strong> - Excel导出时的表头和内容翻译</li>
 *   <li><strong>报表标题翻译</strong> - 图表标题、字段名称的自动翻译</li>
 * </ul>
 *
 * <h3>前端集成</h3>
 * <ul>
 *   <li><strong>HTTP请求头</strong> - Accept-Language自动识别客户端语言</li>
 *   <li><strong>API返回数据</strong> - 后端返回的数据已自动翻译为对应语言</li>
 *   <li><strong>错误提示</strong> - 统一的错误消息多语言展示</li>
 * </ul>
 *
 * <h3>第三方模块使用</h3>
 * <ul>
 *   <li><strong>数据源模块</strong> - 数据源类型、连接状态的翻译</li>
 *   <li><strong>权限模块</strong> - 权限名称、角色描述的翻译</li>
 *   <li><strong>数据同步模块</strong> - 同步状态、错误信息的翻译</li>
 *   <li><strong>扩展插件</strong> - 插件相关消息的多语言支持</li>
 * </ul>
 *
 * <h2>实际应用实例</h2>
 *
 * <h3>用户管理模块</h3>
 * <pre>{@code
 * @Service
 * public class UserService {
 *
 *     @I18n("user.operation")
 *     public Result createUser(UserRequest request) {
 *         try {
 *             User user = new User();
 *             user.setUsername(request.getUsername());
 *             user.setStatus("active"); // 这个状态值会被自动翻译
 *             userMapper.insert(user);
 *             return Result.success("user.create.success");
 *         } catch (Exception e) {
 *             return Result.error("user.create.failed", e.getMessage());
 *         }
 *     }
 *
 *     public List<UserVO> getUserList() {
 *         List<User> users = userMapper.selectList(null);
 *         List<UserVO> result = BeanUtils.copyList(users, UserVO.class);
 *         // 自动翻译用户列表中的状态、角色等字段
 *         return (List<UserVO>) Translator.translateObject(result);
 *     }
 * }
 * }</pre>
 *
 * <h3>数据导出场景</h3>
 * <pre>{@code
 * @RestController
 * public class ExportController {
 *
 *     @GetMapping("/export/users")
 *     public void exportUsers(HttpServletResponse response) {
 *         List<UserExportDTO> users = userService.getUsersForExport();
 *
 *         // 翻译导出数据
 *         Translator.translateObject(users);
 *
 *         // Excel表头也会通过AutoAdaptWidthStyleStrategy自动翻译
 *         EasyExcel.write(response.getOutputStream(), UserExportDTO.class)
 *                 .registerWriteHandler(new AutoAdaptWidthStyleStrategy())
 *                 .sheet(Translator.get("user.export.sheet.name"))
 *                 .doWrite(users);
 *     }
 * }
 * }</pre>
 *
 * <h3>报表数据翻译</h3>
 * <pre>{@code
 * @Service
 * public class ChartService {
 *
 *     public ChartData getChartData(ChartRequest request) {
 *         ChartData data = chartMapper.getChartData(request);
 *
 *         // 翻译图表数据中的分类、系列名称等
 *         data.getCategories().replaceAll(Translator::get);
 *         data.getSeries().forEach(series -> {
 *             series.setName(Translator.get(series.getName()));
 *         });
 *
 *         return data;
 *     }
 * }
 * }</pre>
 *
 * <h3>异常处理翻译</h3>
 * <pre>{@code
 * @ControllerAdvice
 * public class GlobalExceptionHandler {
 *
 *     @ExceptionHandler(BusinessException.class)
 *     public Result handleBusinessException(BusinessException e) {
 *         String message = Translator.get(e.getMessageKey(), e.getArgs());
 *         return Result.error(message);
 *     }
 *
 *     @ExceptionHandler(ValidationException.class)
 *     public Result handleValidationException(ValidationException e) {
 *         List<String> errors = e.getErrors().stream()
 *                 .map(Translator::get)
 *                 .collect(Collectors.toList());
 *         return Result.error("validation.failed", errors);
 *     }
 * }
 * }</pre>
 *
 * <h2>最佳实践</h2>
 *
 * <ol>
 *   <li><strong>资源文件命名</strong> - 使用层次化的key命名，如 user.login.success</li>
 *   <li><strong>参数化消息</strong> - 使用占位符 {0}, {1} 而不是字符串拼接</li>
 *   <li><strong>默认语言</strong> - 确保所有key在默认语言文件中都有对应值</li>
 *   <li><strong>缓存策略</strong> - 生产环境启用缓存，开发环境禁用以便调试</li>
 *   <li><strong>性能优化</strong> - 避免在循环中频繁调用翻译方法，考虑批量翻译</li>
 *   <li><strong>扩展性</strong> - 预留自定义国际化文件路径，支持插件化扩展</li>
 * </ol>
 *
 * <h2>注意事项</h2>
 *
 * <ul>
 *   <li><strong>字符编码</strong> - 确保所有properties文件使用UTF-8编码</li>
 *   <li><strong>缓存清理</strong> - 动态加载资源后需要清理缓存才能生效</li>
 *   <li><strong>循环引用</strong> - 复杂对象翻译时注意避免循环引用导致栈溢出</li>
 *   <li><strong>敏感信息</strong> - 密码等敏感字段会被自动忽略，不进行翻译</li>
 *   <li><strong>JSON格式</strong> - 自动识别和处理JSON格式的字符串字段</li>
 * </ul>
 *
 * @author DataEase Team
 * @since 1.0.0
 * @version 2.0.0
 */
package io.dataease.i18n;