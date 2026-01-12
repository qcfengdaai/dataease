/**
 * 数据填报扩展API包
 * <p>
 * 本包定义数据填报模块对外提供的API接口，供核心模块和其他扩展模块调用。
 * API接口采用面向接口编程，实现类位于core模块中。
 * </p>
 *
 * <p><b>核心API接口：</b></p>
 * <ul>
 *   <li>{@link io.dataease.extensions.datafilling.api.DfPluginManageApi} - 数据填报插件管理API</li>
 * </ul>
 *
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li><b>插件查询</b> - 核心模块查询可用的数据填报插件</li>
 *   <li><b>插件列表展示</b> - 前端获取插件列表用于UI渲染</li>
 *   <li><b>功能集成</b> - 其他模块通过API集成数据填报功能</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 1. 在Controller中使用API查询插件
 * {@literal @}RestController
 * {@literal @}RequestMapping("/api/datafilling")
 * public class DataFillingController {
 *
 *     {@literal @}Autowired
 *     private DfPluginManageApi dfPluginManageApi;
 *
 *     {@literal @}GetMapping("/plugins")
 *     public Result&lt;List&lt;XpackPluginsDfVO&gt;&gt; listPlugins() {
 *         List&lt;XpackPluginsDfVO&gt; plugins = dfPluginManageApi.queryPluginDf();
 *         return Result.success(plugins);
 *     }
 * }
 *
 * // 2. 在Service中使用API
 * {@literal @}Service
 * public class FormDesignService {
 *
 *     {@literal @}Autowired
 *     private DfPluginManageApi dfPluginManageApi;
 *
 *     public boolean isDatasourceSupported(String datasourceType) {
 *         List&lt;XpackPluginsDfVO&gt; plugins = dfPluginManageApi.queryPluginDf();
 *         return plugins.stream()
 *             .anyMatch(p -> p.getType().equals(datasourceType));
 *     }
 * }
 * </pre>
 *
 * <p><b>API设计原则：</b></p>
 * <ul>
 *   <li><b>面向接口编程</b> - 定义接口而非实现，降低耦合</li>
 *   <li><b>职责单一</b> - 每个API接口只负责一类功能</li>
 *   <li><b>稳定性</b> - API接口应保持稳定，避免频繁变更</li>
 *   <li><b>可扩展性</b> - 预留扩展点，方便未来功能增强</li>
 * </ul>
 *
 * <p><b>与其他模块的关系：</b></p>
 * <ul>
 *   <li><b>core模块</b> - 提供API接口的实现类</li>
 *   <li><b>factory包</b> - API实现类调用ExtDDLProviderFactory获取插件配置</li>
 *   <li><b>vo包</b> - API方法返回XpackPluginsDfVO视图对象</li>
 * </ul>
 *
 * <p><b>实现要求：</b></p>
 * <ul>
 *   <li>API接口的实现类应在core模块中，使用@Service注解注册为Spring Bean</li>
 *   <li>实现类需要处理异常，避免向调用者抛出底层异常</li>
 *   <li>API方法应该是线程安全的</li>
 *   <li>对于耗时操作，考虑使用异步处理或缓存机制</li>
 * </ul>
 *
 * @author DataEase
 * @since 2.0.0
 * @see io.dataease.extensions.datafilling.vo.XpackPluginsDfVO
 * @see io.dataease.extensions.datafilling.factory.ExtDDLProviderFactory
 */
package io.dataease.extensions.datafilling.api;
