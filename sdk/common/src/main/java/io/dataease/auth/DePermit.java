package io.dataease.auth;

import java.lang.annotation.*;

/**
 * DataEase权限控制注解
 * <p>
 * 用于方法级别的权限控制，通过SpEL表达式定义访问该方法所需的权限。
 * 支持多个权限表达式的AND组合（全部满足才允许访问）。
 * </p>
 *
 * <p><b>核心特性：</b></p>
 * <ul>
 *   <li>基于SpEL表达式 - 灵活定义权限规则</li>
 *   <li>AND运算 - 多个权限表达式必须全部满足</li>
 *   <li>业务标识 - 支持按业务模块划分权限</li>
 *   <li>AOP实现 - 通过切面自动拦截和校验</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>Controller方法 - 控制API接口的访问权限</li>
 *   <li>Service方法 - 控制关键业务逻辑的执行权限</li>
 *   <li>数据集API - 控制数据集的查看、编辑、删除权限</li>
 *   <li>仪表板API - 控制仪表板的管理权限</li>
 *   <li>系统管理API - 限制只有管理员可访问</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：单个权限检查
 * {@literal @}DePermit(value = "#p0.id + ':read'")
 * {@literal @}GetMapping("/dataset/{id}")
 * public Dataset getDataset(@PathVariable Long id) {
 *     // 检查用户是否有该数据集的读权限
 *     return datasetService.getById(id);
 * }
 *
 * // 示例2：多个权限AND组合
 * {@literal @}DePermit(value = {
 *     "#p0.id + ':manage'",  // 需要管理权限
 *     "#p0.id + ':delete'"   // 且需要删除权限
 * })
 * {@literal @}DeleteMapping("/dataset/{id}")
 * public void deleteDataset(@PathVariable Long id) {
 *     // 两个权限都满足才能删除
 *     datasetService.deleteById(id);
 * }
 *
 * // 示例3：带业务标识
 * {@literal @}DePermit(value = "dataset:create", busiFlag = "dataset")
 * {@literal @}PostMapping("/dataset")
 * public Dataset createDataset(@RequestBody DatasetRequest request) {
 *     // 检查是否有创建数据集的权限
 *     return datasetService.create(request);
 * }
 *
 * // 示例4：系统管理员权限
 * {@literal @}DePermit(value = "system:admin")
 * {@literal @}PostMapping("/system/settings")
 * public void updateSystemSettings(@RequestBody Settings settings) {
 *     // 只有系统管理员可以修改系统设置
 *     systemService.updateSettings(settings);
 * }
 * </pre>
 *
 * <p><b>权限表达式格式：</b></p>
 * <ul>
 *   <li><code>resourceId:permission</code> - 资源ID + 权限类型</li>
 *   <li><code>#p0</code> - 第一个参数（SpEL语法）</li>
 *   <li><code>#p0.id</code> - 第一个参数的id属性</li>
 *   <li>权限类型：read（查看）、manage（管理）、edit（编辑）、delete（删除）、create（创建）</li>
 * </ul>
 *
 * <p><b>实现原理：</b></p>
 * <ol>
 *   <li>AOP切面拦截带有{@code @DePermit}注解的方法</li>
 *   <li>解析SpEL表达式，获取资源ID和权限类型</li>
 *   <li>从数据库查询用户的权限列表</li>
 *   <li>判断用户是否具有所需的所有权限（AND关系）</li>
 *   <li>权限不足则抛出403 Forbidden异常</li>
 * </ol>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>仅支持AND运算，不支持OR运算（多个权限必须全部满足）</li>
 *   <li>SpEL表达式中可以访问方法参数：#p0, #p1, ...或参数名</li>
 *   <li>权限检查失败会抛出异常，需要全局异常处理器捕获</li>
 *   <li>建议与{@link DeApiPath}配合使用，定义API路径和资源类型</li>
 *   <li>权限数据缓存在Redis中，修改权限后需要刷新缓存</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see DeApiPath
 * @see DeLinkPermit
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DePermit {

    /**
     * 鉴权SpEL表达式数组
     * <p>
     * DataEase产品中仅考虑权限的AND运算，不考虑OR运算。
     * 所有表达式必须全部满足才允许访问。
     * </p>
     *
     * <p><b>表达式示例：</b></p>
     * <ul>
     *   <li><code>"dataset:create"</code> - 创建数据集权限</li>
     *   <li><code>"#p0.id + ':read'"</code> - 读取指定ID资源的权限</li>
     *   <li><code>"#datasetId + ':manage'"</code> - 管理指定数据集的权限</li>
     * </ul>
     *
     * @return 权限表达式数组，默认为空（表示无权限要求）
     */
    String[] value() default {};

    /**
     * 业务标识
     * <p>
     * 用于标识权限所属的业务模块，便于权限管理和分类
     * </p>
     *
     * <p><b>常用业务标识：</b></p>
     * <ul>
     *   <li><code>dataset</code> - 数据集相关权限</li>
     *   <li><code>chart</code> - 图表相关权限</li>
     *   <li><code>panel</code> - 仪表板相关权限</li>
     *   <li><code>datasource</code> - 数据源相关权限</li>
     *   <li><code>system</code> - 系统管理相关权限</li>
     * </ul>
     *
     * @return 业务标识字符串，默认为空
     */
    String busiFlag() default "";
}
