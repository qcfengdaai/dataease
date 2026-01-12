/**
 * DataEase异常处理包
 * <p>
 * 提供DataEase系统的统一异常处理机制，包括自定义业务异常类和全局异常处理器。
 * 该包是DataEase错误处理体系的核心，为整个系统提供一致的异常抛出、捕获和响应格式。
 * </p>
 *
 * <h2>包组件概述</h2>
 *
 * <h3>核心异常类</h3>
 * <ul>
 *   <li>{@link io.dataease.exception.DEException} - DataEase自定义业务异常类</li>
 *   <li>{@link io.dataease.exception.GlobalExceptionHandler} - Spring全局异常处理器</li>
 * </ul>
 *
 * <h3>功能特性</h3>
 * <ul>
 *   <li><strong>统一异常体系</strong> - 所有业务异常继承自DEException</li>
 *   <li><strong>全局异常捕获</strong> - @RestControllerAdvice统一处理所有异常</li>
 *   <li><strong>多语言支持</strong> - 集成i18n模块，支持错误信息国际化</li>
 *   <li><strong>智能异常识别</strong> - 特殊处理用户未登录等常见异常</li>
 *   <li><strong>统一响应格式</strong> - 使用ResultMessage提供一致的错误响应</li>
 *   <li><strong>详细日志记录</strong> - 完整记录异常信息和堆栈便于调试</li>
 * </ul>
 *
 * <h2>异常处理流程</h2>
 *
 * <pre>
 * 1. 业务代码抛出DEException或系统异常
 * 2. GlobalExceptionHandler全局异常处理器捕获异常
 * 3. 根据异常类型选择对应的@ExceptionHandler方法
 * 4. 通过Translator.get()进行错误信息国际化翻译
 * 5. 使用LogUtil.error()记录详细的异常日志
 * 6. 构造ResultMessage统一错误响应格式
 * 7. 返回标准化的错误信息给前端
 * </pre>
 *
 * <h2>系统中的使用位置</h2>
 *
 * <h3>Core模块中的使用</h3>
 * <ul>
 *   <li><strong>用户管理模块</strong> - 登录验证、权限检查、用户状态验证异常</li>
 *   <li><strong>数据源模块</strong> - 数据源连接失败、配置错误、权限不足异常</li>
 *   <li><strong>数据集模块</strong> - 数据集不存在、数据格式错误、查询失败异常</li>
 *   <li><strong>图表模块</strong> - 图表配置错误、数据查询异常、渲染失败异常</li>
 *   <li><strong>仪表板模块</strong> - 仪表板权限异常、组件加载失败、布局错误异常</li>
 *   <li><strong>系统管理模块</strong> - 配置错误、系统状态异常、资源不足异常</li>
 * </ul>
 *
 * <h3>SDK模块中的使用</h3>
 * <ul>
 *   <li><strong>认证模块(auth)</strong> - Token验证失败、用户未登录、权限不足异常</li>
 *   <li><strong>数据处理模块</strong> - 数据转换错误、计算异常、格式化失败异常</li>
 *   <li><strong>文件处理模块</strong> - 文件上传失败、格式不支持、大小超限异常</li>
 *   <li><strong>缓存模块</strong> - 缓存连接失败、数据序列化异常、过期处理异常</li>
 *   <li><strong>WebSocket模块</strong> - 连接失败、消息发送异常、会话过期异常</li>
 * </ul>
 *
 * <h3>外部系统集成异常</h3>
 * <ul>
 *   <li><strong>数据库异常</strong> - MyBatis操作失败、连接池耗尽、SQL执行错误</li>
 *   <li><strong>第三方服务异常</strong> - API调用失败、超时异常、认证失败</li>
 *   <li><strong>邮件服务异常</strong> - SMTP连接失败、邮件发送失败、配置错误</li>
 *   <li><strong>文件存储异常</strong> - MinIO连接失败、文件读写异常、权限错误</li>
 * </ul>
 *
 * <h2>详细使用示例</h2>
 *
 * <h3>示例1：基本业务异常抛出</h3>
 * <pre>
 * {@literal @}Service
 * public class UserService {
 *
 *     public User getUserById(Long id) {
 *         User user = userMapper.selectById(id);
 *         if (user == null) {
 *             // 使用简单构造函数抛出异常
 *             DEException.throwException("用户不存在");
 *         }
 *         return user;
 *     }
 *
 *     public void updateUser(UserRequest request) {
 *         // 业务规则验证
 *         if (StringUtils.isBlank(request.getUsername())) {
 *             DEException.throwException("用户名不能为空");
 *         }
 *
 *         // 重复性检查
 *         if (existsByUsername(request.getUsername())) {
 *             DEException.throwException("用户名已存在");
 *         }
 *
 *         userMapper.updateById(request);
 *     }
 * }
 * </pre>
 *
 * <h3>示例2：带错误码的异常抛出</h3>
 * <pre>
 * {@literal @}Service
 * public class DatasetService {
 *
 *     public Dataset getDataset(Long id) {
 *         Dataset dataset = datasetMapper.selectById(id);
 *         if (dataset == null) {
 *             // 使用错误码和消息
 *             DEException.throwException(ResultCode.DATASET_NOT_FOUND.code(), "数据集不存在");
 *         }
 *         return dataset;
 *     }
 *
 *     public void deleteDataset(Long id) {
 *         // 权限检查失败
 *         if (!hasDeletePermission(id)) {
 *             DEException.throwException(ResultCode.ACCESS_DENIED.code(), "无权限删除该数据集");
 *         }
 *
 *         // 业务状态检查
 *         if (isInUse(id)) {
 *             DEException.throwException(ResultCode.RESOURCE_IN_USE.code(), "数据集正在使用中，无法删除");
 *         }
 *
 *         datasetMapper.deleteById(id);
 *     }
 * }
 * </pre>
 *
 * <h3>示例3：GlobalExceptionHandler实际处理逻辑</h3>
 * <pre>
 * // 当前系统的GlobalExceptionHandler实现
 * {@literal @}RestControllerAdvice
 * public class GlobalExceptionHandler {
 *
 *     // 处理参数校验异常
 *     {@literal @}ExceptionHandler(MethodArgumentNotValidException.class)
 *     public ResultMessage MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
 *         ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
 *         String msg = objectError.getDefaultMessage();
 *
 *         // 国际化翻译错误消息
 *         msg = Translator.get(msg);
 *         LogUtil.error(msg);
 *
 *         return new ResultMessage(ResultCode.PARAM_IS_INVALID.code(), msg);
 *     }
 *
 *     // 处理DataEase业务异常
 *     {@literal @}ExceptionHandler(DEException.class)
 *     public ResultMessage deExceptionHandler(DEException e) {
 *         LogUtil.error(e.getMessage(), e);
 *         return new ResultMessage(e.getCode(), e.getMessage());
 *     }
 *
 *     // 处理空指针异常(特别处理用户未登录)
 *     {@literal @}ExceptionHandler(NullPointerException.class)
 *     public ResultMessage noUserExceptionHandler(Exception e) {
 *         String message = e.getMessage();
 *         LogUtil.error(message, e);
 *
 *         // 特殊处理用户未登录导致的空指针
 *         if (StringUtils.contains(message, "Cannot invoke \"io.dataease.auth.bo.TokenUserBO.getUserId()\" because \"user\" is null")) {
 *             return new ResultMessage(ResultCode.USER_NOT_LOGGED_IN.code(), ResultCode.USER_NOT_LOGGED_IN.message());
 *         }
 *
 *         return new ResultMessage(ResultCode.PARAM_IS_BLANK.code(), message);
 *     }
 * }
 * </pre>
 *
 * <h3>示例4：Controller层异常处理</h3>
 * <pre>
 * {@literal @}RestController
 * {@literal @}RequestMapping("/api/user")
 * public class UserController {
 *
 *     {@literal @}Autowired
 *     private UserService userService;
 *
 *     {@literal @}GetMapping("/{id}")
 *     public ResultMessage getUserById({@literal @}PathVariable Long id) {
 *         // 不需要try-catch，异常由GlobalExceptionHandler处理
 *         User user = userService.getUserById(id);
 *         return ResultMessage.success(user);
 *     }
 *
 *     {@literal @}PostMapping
 *     public ResultMessage createUser({@literal @}Valid {@literal @}RequestBody UserRequest request) {
 *         // @Valid注解的校验异常会被GlobalExceptionHandler捕获
 *         User user = userService.createUser(request);
 *         return ResultMessage.success(user);
 *     }
 *
 *     {@literal @}DeleteMapping("/{id}")
 *     public ResultMessage deleteUser({@literal @}PathVariable Long id) {
 *         // 业务异常会被自动捕获并转换为统一响应格式
 *         userService.deleteUser(id);
 *         return ResultMessage.success("删除成功");
 *     }
 * }
 * </pre>
 *
 * <h3>示例5：异常传播和包装</h3>
 * <pre>
 * {@literal @}Service
 * public class FileService {
 *
 *     public void uploadFile(MultipartFile file) {
 *         try {
 *             // 可能抛出IOException的操作
 *             Files.write(Paths.get("/upload/" + file.getOriginalFilename()), file.getBytes());
 *         } catch (IOException e) {
 *             // 将检查型异常包装为业务异常
 *             DEException.throwException(e);
 *         }
 *     }
 *
 *     public String processData(String data) {
 *         try {
 *             return complexDataProcessing(data);
 *         } catch (Exception e) {
 *             // 包装异常并提供更友好的错误信息
 *             LogUtil.error("数据处理失败: " + data, e);
 *             DEException.throwException(ResultCode.DATA_PROCESS_ERROR.code(), "数据处理失败，请检查数据格式");
 *         }
 *         return null;
 *     }
 * }
 * </pre>
 *
 * <h2>异常分类</h2>
 *
 * <h3>按场景分类</h3>
 * <ul>
 *   <li><b>参数异常</b>：参数为空、格式错误、超出范围等</li>
 *   <li><b>业务异常</b>：资源不存在、状态不允许、业务规则违反等</li>
 *   <li><b>权限异常</b>：未登录、无权限、Token失效等</li>
 *   <li><b>数据异常</b>：数据重复、外键约束、数据不一致等</li>
 *   <li><b>系统异常</b>：数据库连接失败、第三方服务异常等</li>
 * </ul>
 *
 * <h3>按处理方式分类</h3>
 * <ul>
 *   <li><b>可恢复异常</b>：可以重试或提示用户修正</li>
 *   <li><b>不可恢复异常</b>：需要系统管理员介入</li>
 * </ul>
 *
 * <h2>统一错误响应格式</h2>
 *
 * <h3>ResultMessage响应结构</h3>
 * <pre>
 * // 所有异常都通过GlobalExceptionHandler转换为以下格式
 * {
 *   "code": 500,              // 错误码，来自ResultCode或DEException
 *   "msg": "用户不存在",       // 错误信息，支持国际化翻译
 *   "data": null,            // 数据字段，异常情况下通常为null
 *   "success": false         // 是否成功标识
 * }
 * </pre>
 *
 * <h3>不同异常类型的响应示例</h3>
 * <pre>
 * // DEException业务异常响应
 * {
 *   "code": 500,
 *   "msg": "数据集不存在",
 *   "data": null,
 *   "success": false
 * }
 *
 * // 参数校验异常响应
 * {
 *   "code": 400,
 *   "msg": "用户名不能为空",  // 经过Translator.get()翻译
 *   "data": null,
 *   "success": false
 * }
 *
 * // 用户未登录异常响应
 * {
 *   "code": 401,
 *   "msg": "用户未登录",
 *   "data": null,
 *   "success": false
 * }
 * </pre>
 *
 * <h2>与其他模块的集成</h2>
 *
 * <h3>国际化(i18n)集成</h3>
 * <ul>
 *   <li><strong>自动翻译</strong> - GlobalExceptionHandler使用Translator.get()自动翻译错误信息</li>
 *   <li><strong>多语言支持</strong> - 根据HTTP请求头Accept-Language返回对应语言的错误信息</li>
 *   <li><strong>消息占位符</strong> - 支持带参数的错误消息翻译</li>
 * </ul>
 *
 * <h3>日志模块(LogUtil)集成</h3>
 * <ul>
 *   <li><strong>异常记录</strong> - 所有异常都通过LogUtil.error()记录完整堆栈信息</li>
 *   <li><strong>上下文信息</strong> - 记录异常发生时的请求上下文和用户信息</li>
 *   <li><strong>分级记录</strong> - 根据异常类型选择适当的日志级别</li>
 * </ul>
 *
 * <h3>认证模块(auth)集成</h3>
 * <ul>
 *   <li><strong>用户状态检查</strong> - 特殊处理TokenUserBO相关的空指针异常</li>
 *   <li><strong>登录状态识别</strong> - 自动识别用户未登录导致的异常</li>
 *   <li><strong>权限验证失败</strong> - 统一处理权限相关的业务异常</li>
 * </ul>
 *
 * <h3>结果封装(ResultCode/ResultMessage)集成</h3>
 * <ul>
 *   <li><strong>错误码映射</strong> - DEException的错误码直接映射到ResultMessage</li>
 *   <li><strong>统一响应格式</strong> - 所有异常最终转换为ResultMessage格式</li>
 *   <li><strong>成功失败标识</strong> - 自动设置success字段为false</li>
 * </ul>
 *
 * <h2>开发最佳实践</h2>
 *
 * <h3>1. 异常抛出规范</h3>
 * <ul>
 *   <li><strong>使用静态方法</strong> - 优先使用DEException.throwException()静态方法</li>
 *   <li><strong>及时抛出</strong> - 发现业务规则违反时立即抛出，不要延迟处理</li>
 *   <li><strong>避免空返回</strong> - 不要返回null，使用异常明确表示错误状态</li>
 *   <li><strong>单一责任</strong> - 每个异常只表示一种明确的错误情况</li>
 * </ul>
 *
 * <h3>2. 错误信息编写</h3>
 * <ul>
 *   <li><strong>用户友好</strong> - 使用中文提示，面向最终用户编写错误信息</li>
 *   <li><strong>国际化支持</strong> - 错误信息会自动通过Translator.get()翻译</li>
 *   <li><strong>信息完整</strong> - 提供足够的上下文信息帮助用户理解和解决问题</li>
 *   <li><strong>避免技术细节</strong> - 不要暴露SQL语句、堆栈信息等技术细节</li>
 * </ul>
 *
 * <h3>3. 异常分层处理</h3>
 * <ul>
 *   <li><strong>Service层</strong> - 抛出具体的业务异常，包含明确的错误码和信息</li>
 *   <li><strong>Controller层</strong> - 不处理异常，完全交给GlobalExceptionHandler</li>
 *   <li><strong>Mapper层</strong> - 数据访问异常通过MyBatis传播到Service层处理</li>
 *   <li><strong>Util类</strong> - 工具类异常包装为DEException后抛出</li>
 * </ul>
 *
 * <h3>4. 异常日志记录</h3>
 * <ul>
 *   <li><strong>完整堆栈</strong> - GlobalExceptionHandler已自动记录完整异常堆栈</li>
 *   <li><strong>上下文信息</strong> - 在业务代码中可额外记录用户ID、操作参数等</li>
 *   <li><strong>分级处理</strong> - 业务异常使用ERROR级别，系统异常使用WARN级别</li>
 *   <li><strong>避免重复</strong> - 不要在多个层次重复记录同一个异常</li>
 * </ul>
 *
 * <h2>重要注意事项</h2>
 *
 * <h3>1. 性能考虑</h3>
 * <ul>
 *   <li><strong>异常成本</strong> - 异常创建和堆栈收集有性能开销，不要用于正常流程控制</li>
 *   <li><strong>频率控制</strong> - 避免在循环中频繁抛出异常</li>
 *   <li><strong>预期处理</strong> - 可预期的业务情况使用返回值，不要使用异常</li>
 * </ul>
 *
 * <h3>2. 安全考虑</h3>
 * <ul>
 *   <li><strong>敏感信息</strong> - GlobalExceptionHandler已确保不向前端暴露敏感信息</li>
 *   <li><strong>用户隐私</strong> - 异常信息中不要包含用户密码、令牌等隐私数据</li>
 *   <li><strong>系统信息</strong> - 不要暴露服务器路径、数据库结构等系统信息</li>
 * </ul>
 *
 * <h3>3. 事务处理</h3>
 * <ul>
 *   <li><strong>自动回滚</strong> - DEException继承RuntimeException，会自动触发Spring事务回滚</li>
 *   <li><strong>事务边界</strong> - 确保在事务边界内抛出异常以保证数据一致性</li>
 *   <li><strong>检查型异常</strong> - 将IOException等检查型异常包装为DEException</li>
 * </ul>
 *
 * <h3>4. 特殊异常处理</h3>
 * <ul>
 *   <li><strong>用户未登录</strong> - GlobalExceptionHandler已特殊处理TokenUserBO相关的空指针异常</li>
 *   <li><strong>参数校验</strong> - @Valid注解的校验异常会自动捕获并翻译</li>
 *   <li><strong>系统异常</strong> - 未知的RuntimeException会被包装为通用错误响应</li>
 * </ul>
 *
 * @author DataEase团队
 * @since 1.0.0
 * @version 2.0.0
 * @see io.dataease.result.ResultMessage 统一结果响应类
 * @see io.dataease.result.ResultCode 错误码枚举类
 * @see io.dataease.i18n.Translator 国际化翻译工具
 * @see io.dataease.utils.LogUtil 日志记录工具
 */
package io.dataease.exception;
