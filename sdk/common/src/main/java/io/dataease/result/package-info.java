/**
 * 统一响应结果包
 * <p>
 * 提供DataEase系统的统一响应格式，包括结果包装类、响应码、响应消息等。
 * 所有API接口返回统一的Result对象，便于前端统一处理。
 * </p>
 *
 * <h2>核心类</h2>
 *
 * <h3>1. 响应结果</h3>
 * <ul>
 *   <li><code>Result</code> - 统一响应结果类</li>
 *   <li>{@link io.dataease.result.ResultCode} - 响应状态码枚举</li>
 *   <li>{@link io.dataease.result.ResultMessage} - 响应消息类</li>
 * </ul>
 *
 * <h3>2. 响应处理</h3>
 * <ul>
 *   <li>{@link io.dataease.result.ResultResponseBodyAdvice} - 响应体增强，自动包装Result</li>
 * </ul>
 *
 * <h2>Result结构</h2>
 *
 * <h3>标准响应格式</h3>
 * <pre>
 * {
 *   "success": true,        // 是否成功
 *   "code": 200,           // HTTP状态码
 *   "msg": "操作成功",      // 提示消息
 *   "data": {...}          // 业务数据
 * }
 * </pre>
 *
 * <h3>成功响应</h3>
 * <pre>
 * {
 *   "success": true,
 *   "code": 200,
 *   "msg": "查询成功",
 *   "data": {
 *     "id": 123,
 *     "name": "销售数据集",
 *     "type": "db"
 *   }
 * }
 * </pre>
 *
 * <h3>失败响应</h3>
 * <pre>
 * {
 *   "success": false,
 *   "code": 500,
 *   "msg": "数据集不存在",
 *   "data": null
 * }
 * </pre>
 *
 * <h3>分页响应</h3>
 * <pre>
 * {
 *   "success": true,
 *   "code": 200,
 *   "msg": "查询成功",
 *   "data": {
 *     "total": 100,
 *     "records": [
 *       {"id": 1, "name": "数据集1"},
 *       {"id": 2, "name": "数据集2"}
 *     ]
 *   }
 * }
 * </pre>
 *
 * <h2>使用示例</h2>
 *
 * <h3>示例1：基本使用</h3>
 * <pre>
 * {@literal @}RestController
 * {@literal @}RequestMapping("/api/dataset")
 * public class DatasetController {
 *
 *     // 成功返回数据
 *     {@literal @}GetMapping("/{id}")
 *     public Result&lt;Dataset&gt; getDataset(@PathVariable Long id) {
 *         Dataset dataset = datasetService.getById(id);
 *         return Result.success(dataset);
 *     }
 *
 *     // 成功返回（无数据）
 *     {@literal @}DeleteMapping("/{id}")
 *     public Result&lt;Void&gt; deleteDataset(@PathVariable Long id) {
 *         datasetService.deleteById(id);
 *         return Result.success();
 *     }
 *
 *     // 失败返回
 *     {@literal @}PostMapping
 *     public Result&lt;Dataset&gt; createDataset(@RequestBody DatasetRequest request) {
 *         if (StringUtils.isBlank(request.getName())) {
 *             return Result.error("数据集名称不能为空");
 *         }
 *         Dataset dataset = datasetService.create(request);
 *         return Result.success(dataset);
 *     }
 * }
 * </pre>
 *
 * <h3>示例2：自定义消息</h3>
 * <pre>
 * // 成功，自定义消息
 * return Result.success(dataset, "数据集查询成功");
 *
 * // 失败，自定义消息
 * return Result.error("数据集名称已存在，请使用其他名称");
 *
 * // 自定义状态码
 * return Result.error(404, "数据集不存在");
 * </pre>
 *
 * <h3>示例3：分页查询</h3>
 * <pre>
 * {@literal @}GetMapping("/list")
 * public Result&lt;Page&lt;Dataset&gt;&gt; listDataset(
 *         {@literal @}RequestParam(defaultValue = "1") int page,
 *         {@literal @}RequestParam(defaultValue = "10") int size) {
 *
 *     Page&lt;Dataset&gt; pageResult = datasetService.page(page, size);
 *     return Result.success(pageResult);
 * }
 *
 * // 响应示例
 * {
 *   "success": true,
 *   "code": 200,
 *   "msg": "查询成功",
 *   "data": {
 *     "total": 100,
 *     "size": 10,
 *     "current": 1,
 *     "records": [...]
 *   }
 * }
 * </pre>
 *
 * <h3>示例4：异常处理（配合全局异常处理器）</h3>
 * <pre>
 * {@literal @}ControllerAdvice
 * public class GlobalExceptionHandler {
 *
 *     // 处理业务异常
 *     {@literal @}ExceptionHandler(DEException.class)
 *     public Result&lt;?&gt; handleDEException(DEException e) {
 *         return Result.error(e.getMessage());
 *     }
 *
 *     // 处理参数校验异常
 *     {@literal @}ExceptionHandler(MethodArgumentNotValidException.class)
 *     public Result&lt;?&gt; handleValidException(MethodArgumentNotValidException e) {
 *         String message = e.getBindingResult()
 *             .getFieldError()
 *             .getDefaultMessage();
 *         return Result.error("参数校验失败: " + message);
 *     }
 *
 *     // 处理未知异常
 *     {@literal @}ExceptionHandler(Exception.class)
 *     public Result&lt;?&gt; handleException(Exception e) {
 *         LogUtil.error("系统异常", e);
 *         return Result.error("系统错误，请联系管理员");
 *     }
 * }
 * </pre>
 *
 * <h3>示例5：条件返回</h3>
 * <pre>
 * {@literal @}PostMapping("/import")
 * public Result&lt;ImportResult&gt; importData(@RequestBody ImportRequest request) {
 *     try {
 *         ImportResult result = importService.doImport(request);
 *
 *         // 部分成功
 *         if (result.getFailCount() > 0) {
 *             return Result.success(result, "导入完成，部分数据失败");
 *         }
 *
 *         // 全部成功
 *         return Result.success(result, "导入成功");
 *     } catch (Exception e) {
 *         return Result.error("导入失败: " + e.getMessage());
 *     }
 * }
 * </pre>
 *
 * <h2>响应状态码</h2>
 *
 * <h3>HTTP标准状态码</h3>
 * <ul>
 *   <li><code>200</code> - 成功</li>
 *   <li><code>400</code> - 请求参数错误</li>
 *   <li><code>401</code> - 未认证（未登录）</li>
 *   <li><code>403</code> - 无权限</li>
 *   <li><code>404</code> - 资源不存在</li>
 *   <li><code>500</code> - 服务器内部错误</li>
 * </ul>
 *
 * <h3>自定义业务状态码（可选）</h3>
 * <ul>
 *   <li><code>1001</code> - 业务规则违反</li>
 *   <li><code>1002</code> - 数据重复</li>
 *   <li><code>1003</code> - 资源被占用</li>
 * </ul>
 *
 * <h2>前端处理</h2>
 *
 * <h3>Axios拦截器示例</h3>
 * <pre>
 * // 响应拦截器
 * axios.interceptors.response.use(
 *   response => {
 *     const result = response.data;
 *
 *     // 检查业务状态
 *     if (!result.success) {
 *       // 显示错误消息
 *       Message.error(result.msg || '操作失败');
 *       return Promise.reject(new Error(result.msg));
 *     }
 *
 *     // 返回业务数据
 *     return result.data;
 *   },
 *   error => {
 *     if (error.response) {
 *       switch (error.response.status) {
 *         case 401:
 *           // 未登录，跳转登录页
 *           router.push('/login');
 *           break;
 *         case 403:
 *           Message.error('无权限访问');
 *           break;
 *         case 404:
 *           Message.error('资源不存在');
 *           break;
 *         default:
 *           Message.error('系统错误');
 *       }
 *     }
 *     return Promise.reject(error);
 *   }
 * );
 * </pre>
 *
 * <h2>最佳实践</h2>
 *
 * <h3>1. 返回值规范</h3>
 * <ul>
 *   <li>所有Controller方法返回Result类型</li>
 *   <li>使用泛型指定data的类型</li>
 *   <li>空数据返回Result.success()而非null</li>
 * </ul>
 *
 * <h3>2. 错误消息</h3>
 * <ul>
 *   <li>消息要清晰易懂，面向最终用户</li>
 *   <li>使用中文提示</li>
 *   <li>支持国际化</li>
 *   <li>不要暴露技术细节（如SQL、堆栈）</li>
 * </ul>
 *
 * <h3>3. 响应体增强</h3>
 * <ul>
 *   <li>使用ResultResponseBodyAdvice自动包装响应</li>
 *   <li>Controller可以直接返回业务对象</li>
 *   <li>减少重复的Result.success()调用</li>
 * </ul>
 *
 * <h3>4. 性能优化</h3>
 * <ul>
 *   <li>大数据量使用分页</li>
 *   <li>避免返回过多无用字段</li>
 *   <li>使用VO对象而非直接返回Entity</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 *
 * <h3>1. 空值处理</h3>
 * <ul>
 *   <li>data为null时前端要判空</li>
 *   <li>空列表返回[]而非null</li>
 *   <li>空对象返回{}而非null</li>
 * </ul>
 *
 * <h3>2. 响应数据脱敏</h3>
 * <ul>
 *   <li>密码等敏感字段不要返回</li>
 *   <li>使用@JsonIgnore注解排除字段</li>
 *   <li>或使用专门的VO对象</li>
 * </ul>
 *
 * <h3>3. 接口版本兼容</h3>
 * <ul>
 *   <li>修改响应结构要考虑向后兼容</li>
 *   <li>新增字段可以，删除字段要慎重</li>
 *   <li>必要时使用API版本号</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.exception
 */
package io.dataease.result;
