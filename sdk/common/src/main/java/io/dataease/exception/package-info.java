/**
 * 异常处理包
 * <p>
 * 定义DataEase系统的自定义异常体系，提供统一的异常处理和错误响应机制。
 * 所有业务异常继承自DEException，通过全局异常处理器统一处理。
 * </p>
 *
 * <h2>核心异常类</h2>
 *
 * <h3>1. 基础异常</h3>
 * <ul>
 *   <li><code>DEException</code> - DataEase基础异常，所有自定义异常的父类</li>
 * </ul>
 *
 * <h2>异常处理流程</h2>
 *
 * <pre>
 * 1. 业务代码抛出DEException
 * 2. 全局异常处理器（@ControllerAdvice）捕获异常
 * 3. 根据异常类型构造错误响应
 * 4. 记录异常日志
 * 5. 返回统一格式的错误信息给前端
 * </pre>
 *
 * <h2>使用示例</h2>
 *
 * <h3>示例1：抛出业务异常</h3>
 * <pre>
 * {@literal @}Service
 * public class DatasetService {
 *
 *     public Dataset getById(Long id) {
 *         Dataset dataset = datasetMapper.selectById(id);
 *         if (dataset == null) {
 *             // 抛出业务异常
 *             throw new DEException("数据集不存在");
 *         }
 *         return dataset;
 *     }
 *
 *     public void deleteById(Long id) {
 *         // 权限检查
 *         if (!hasPermission(id, "delete")) {
 *             throw new DEException("无权限删除该数据集");
 *         }
 *
 *         // 业务逻辑检查
 *         if (isUsedByCharts(id)) {
 *             throw new DEException("数据集正在被图表使用，无法删除");
 *         }
 *
 *         datasetMapper.deleteById(id);
 *     }
 * }
 * </pre>
 *
 * <h3>示例2：带错误码的异常</h3>
 * <pre>
 * // 定义错误码常量
 * public class ErrorCode {
 *     public static final String DATASET_NOT_FOUND = "DATASET_001";
 *     public static final String NO_PERMISSION = "AUTH_001";
 *     public static final String INVALID_PARAM = "PARAM_001";
 * }
 *
 * // 使用错误码
 * if (dataset == null) {
 *     throw new DEException(ErrorCode.DATASET_NOT_FOUND, "数据集不存在");
 * }
 * </pre>
 *
 * <h3>示例3：全局异常处理器</h3>
 * <pre>
 * {@literal @}ControllerAdvice
 * public class GlobalExceptionHandler {
 *
 *     {@literal @}ExceptionHandler(DEException.class)
 *     {@literal @}ResponseBody
 *     public Result handleDEException(DEException e) {
 *         // 记录日志
 *         LogUtil.error("业务异常: " + e.getMessage(), e);
 *
 *         // 返回统一格式的错误响应
 *         return Result.error(e.getMessage());
 *     }
 *
 *     {@literal @}ExceptionHandler(Exception.class)
 *     {@literal @}ResponseBody
 *     public Result handleException(Exception e) {
 *         // 记录未知异常
 *         LogUtil.error("系统异常: " + e.getMessage(), e);
 *
 *         // 返回通用错误信息
 *         return Result.error("系统错误，请联系管理员");
 *     }
 * }
 * </pre>
 *
 * <h3>示例4：参数校验异常</h3>
 * <pre>
 * public void updateDataset(DatasetRequest request) {
 *     // 参数校验
 *     if (request.getId() == null) {
 *         throw new DEException("数据集ID不能为空");
 *     }
 *
 *     if (StringUtils.isBlank(request.getName())) {
 *         throw new DEException("数据集名称不能为空");
 *     }
 *
 *     if (request.getName().length() > 50) {
 *         throw new DEException("数据集名称不能超过50个字符");
 *     }
 *
 *     // 执行更新
 *     datasetMapper.updateById(request);
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
 * <h2>错误响应格式</h2>
 *
 * <h3>标准错误响应</h3>
 * <pre>
 * {
 *   "success": false,
 *   "code": 500,
 *   "msg": "数据集不存在",
 *   "data": null
 * }
 * </pre>
 *
 * <h3>带错误码的响应</h3>
 * <pre>
 * {
 *   "success": false,
 *   "code": 500,
 *   "errorCode": "DATASET_001",
 *   "msg": "数据集不存在",
 *   "data": null
 * }
 * </pre>
 *
 * <h3>参数校验错误</h3>
 * <pre>
 * {
 *   "success": false,
 *   "code": 400,
 *   "msg": "参数校验失败",
 *   "data": {
 *     "name": "数据集名称不能为空",
 *     "type": "数据集类型不合法"
 *   }
 * }
 * </pre>
 *
 * <h2>最佳实践</h2>
 *
 * <h3>1. 异常抛出时机</h3>
 * <ul>
 *   <li>发现业务规则违反时立即抛出</li>
 *   <li>不要吞掉异常或返回null</li>
 *   <li>避免使用异常控制正常业务流程</li>
 * </ul>
 *
 * <h3>2. 异常信息</h3>
 * <ul>
 *   <li>提供清晰的错误描述</li>
 *   <li>使用中文提示面向最终用户</li>
 *   <li>避免暴露敏感信息（如SQL、堆栈等）</li>
 *   <li>支持国际化（i18n）</li>
 * </ul>
 *
 * <h3>3. 异常日志</h3>
 * <ul>
 *   <li>记录完整的异常堆栈</li>
 *   <li>包含上下文信息（用户ID、资源ID等）</li>
 *   <li>区分日志级别（ERROR、WARN、INFO）</li>
 * </ul>
 *
 * <h3>4. 异常传递</h3>
 * <ul>
 *   <li>Service层抛出业务异常</li>
 *   <li>Controller层不处理异常，交给全局处理器</li>
 *   <li>避免过度捕获和重新抛出</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 *
 * <h3>1. 不要滥用异常</h3>
 * <ul>
 *   <li>异常是例外情况，不是正常流程控制</li>
 *   <li>频繁的异常抛出影响性能</li>
 *   <li>可预期的情况使用返回值而非异常</li>
 * </ul>
 *
 * <h3>2. 异常安全</h3>
 * <ul>
 *   <li>不要在异常信息中暴露敏感数据</li>
 *   <li>不要返回详细的堆栈信息给前端</li>
 *   <li>记录日志时注意脱敏</li>
 * </ul>
 *
 * <h3>3. 事务回滚</h3>
 * <ul>
 *   <li>DEException会触发Spring事务回滚</li>
 *   <li>确保异常抛出时事务能正确回滚</li>
 *   <li>注意checked exception的事务处理</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.result.Result
 */
package io.dataease.exception;
