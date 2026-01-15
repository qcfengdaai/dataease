/**
 * DataEase视图扩展模块
 * 提供图表视图相关的扩展接口和插件开发框架
 *
 * <h2>模块概述</h2>
 * <p>本包是DataEase SDK中专门用于图表视图扩展的核心模块，为图表插件开发者提供了完整的
 * 开发框架和API接口。通过该模块，开发者可以创建自定义的图表类型、数据处理逻辑和可视化效果。</p>
 *
 * <h2>包结构说明</h2>
 * <ul>
 *   <li><strong>dto/</strong> - 数据传输对象，包含40个核心DTO类
 *     <ul>
 *       <li>{@link io.dataease.extensions.view.dto.ChartViewDTO} - 图表视图主要数据对象</li>
 *       <li>{@link io.dataease.extensions.view.dto.ChartViewFieldDTO} - 图表字段配置对象</li>
 *       <li>{@link io.dataease.extensions.view.dto.ChartViewBaseDTO} - 图表视图基础DTO</li>
 *       <li>其他专业化DTO类：维度、指标、排序、过滤等配置对象</li>
 *     </ul>
 *   </li>
 *   <li><strong>vo/</strong> - 视图对象
 *     <ul>
 *       <li>{@link io.dataease.extensions.view.vo.XpackPluginsViewVO} - X-pack插件视图配置</li>
 *     </ul>
 *   </li>
 *   <li><strong>util/</strong> - 工具类
 *     <ul>
 *       <li>{@link io.dataease.extensions.view.util.ChartDataUtil} - 图表数据处理工具类</li>
 *       <li>{@link io.dataease.extensions.view.util.FieldUtil} - 字段转换工具类</li>
 *       <li>{@link io.dataease.extensions.view.util.Utils} - 通用工具类</li>
 *     </ul>
 *   </li>
 *   <li><strong>filter/</strong> - 过滤器相关
 *     <ul>
 *       <li>{@link io.dataease.extensions.view.filter.FilterTreeObj} - 过滤器树对象</li>
 *       <li>{@link io.dataease.extensions.view.filter.FilterTreeItem} - 过滤器树项</li>
 *       <li>{@link io.dataease.extensions.view.filter.DynamicTimeSetting} - 动态时间设置</li>
 *     </ul>
 *   </li>
 *   <li><strong>plugin/</strong> - 插件框架
 *     <ul>
 *       <li>{@link io.dataease.extensions.view.plugin.AbstractChartPlugin} - 抽象图表插件基类</li>
 *       <li>{@link io.dataease.extensions.view.plugin.DataEaseChartPlugin} - DataEase图表插件实现</li>
 *     </ul>
 *   </li>
 *   <li><strong>factory/</strong> - 工厂模式
 *     <ul>
 *       <li>{@link io.dataease.extensions.view.factory.PluginsChartFactory} - 插件图表工厂</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>核心功能特性</h2>
 *
 * <h3>1. 图表插件开发框架</h3>
 * <p>提供了完整的插件开发生命周期支持：</p>
 * <ul>
 *   <li>轴字段处理 - {@code formatAxis()}</li>
 *   <li>自定义过滤 - {@code customFilter()}</li>
 *   <li>数据计算 - {@code calcChartResult()}</li>
 *   <li>视图构建 - {@code buildChart()}</li>
 * </ul>
 *
 * <h3>2. 数据处理能力</h3>
 * <ul>
 *   <li>支持多种数据源类型的统一处理</li>
 *   <li>提供强大的数据排序、过滤和聚合功能</li>
 *   <li>支持数据脱敏和权限控制</li>
 *   <li>支持跨数据源查询优化</li>
 * </ul>
 *
 * <h3>3. 过滤器系统</h3>
 * <ul>
 *   <li>树形结构的复合过滤条件</li>
 *   <li>丰富的过滤操作符支持</li>
 *   <li>动态时间过滤配置</li>
 *   <li>自定义过滤逻辑扩展</li>
 * </ul>
 *
 * <h2>主要使用场景</h2>
 *
 * <h3>场景一：开发自定义图表插件</h3>
 * <pre>{@code
 * // 1. 继承抽象插件类
 * public class MyCustomChartPlugin extends DataEaseChartPlugin {
 *
 *     @Override
 *     public AxisFormatResult formatAxis(ChartViewDTO view) {
 *         // 处理轴字段逻辑
 *         return new MyAxisFormatResult();
 *     }
 *
 *     @Override
 *     public CustomFilterResult customFilter(ChartViewDTO view,
 *                                           List<ChartExtFilterDTO> filterList,
 *                                           AxisFormatResult formatResult) {
 *         // 处理自定义过滤逻辑
 *         return new MyCustomFilterResult();
 *     }
 *
 *     @Override
 *     public ChartCalcDataResult calcChartResult(ChartViewDTO view,
 *                                              AxisFormatResult formatResult,
 *                                              CustomFilterResult filterResult,
 *                                              Map<String, Object> sqlMap,
 *                                              SQLMeta sqlMeta,
 *                                              Provider provider) {
 *         // 数据查询和计算逻辑
 *         return new MyChartCalcDataResult();
 *     }
 *
 *     @Override
 *     public ChartViewDTO buildChart(ChartViewDTO view,
 *                                   ChartCalcDataResult calcResult,
 *                                   AxisFormatResult formatResult,
 *                                   CustomFilterResult filterResult) {
 *         // 构建最终视图
 *         return enhancedView;
 *     }
 * }
 *
 * // 2. 注册插件
 * PluginsChartFactory.loadPlugin("my-render", "my-chart-type", new MyCustomChartPlugin());
 * }</pre>
 *
 * <h3>场景二：数据处理和转换</h3>
 * <pre>{@code
 * // 字段转换
 * List<ChartViewFieldBaseDTO> sourceFields = getSourceFields();
 * List<DatasetTableFieldDTO> targetFields = FieldUtil.transFields(sourceFields);
 *
 * // 图表数据转换
 * Map<String, Object> chartData = ChartDataUtil.transChartData(
 *     xAxis, yAxis, view, rawData, false);
 *
 * // 表格数据转换
 * Map<String, Object> tableData = ChartDataUtil.transTableNormal(
 *     fields, view, data, desensitizationConfig);
 * }</pre>
 *
 * <h3>场景三：构建复合过滤条件</h3>
 * <pre>{@code
 * // 构建过滤树
 * FilterTreeObj filterTree = new FilterTreeObj();
 * filterTree.setLogic("AND");
 *
 * List<FilterTreeItem> items = new ArrayList<>();
 *
 * // 添加字段过滤项
 * FilterTreeItem fieldFilter = new FilterTreeItem();
 * fieldFilter.setType("item");
 * fieldFilter.setFieldId(123L);
 * fieldFilter.setFilterType("logic");
 * fieldFilter.setTerm("eq");
 * fieldFilter.setValue("某个值");
 * items.add(fieldFilter);
 *
 * // 添加时间过滤项
 * FilterTreeItem timeFilter = new FilterTreeItem();
 * timeFilter.setType("item");
 * timeFilter.setFilterTypeTime("dynamicDate");
 *
 * DynamicTimeSetting timeSetting = new DynamicTimeSetting();
 * timeSetting.setRelativeToCurrent("today");
 * timeSetting.setTimeGranularity("date");
 * timeFilter.setDynamicTimeSetting(timeSetting);
 * items.add(timeFilter);
 *
 * filterTree.setItems(items);
 * }</pre>
 *
 * <h2>与其他模块的关系</h2>
 *
 * <h3>依赖关系</h3>
 * <ul>
 *   <li><strong>io.dataease.extensions.datasource</strong> - 数据源扩展模块
 *     <ul>
 *       <li>依赖 {@code DatasetTableFieldDTO} 进行字段定义</li>
 *       <li>依赖 {@code Provider} 进行数据查询</li>
 *       <li>依赖 {@code SQLMeta} 进行SQL元数据处理</li>
 *     </ul>
 *   </li>
 *   <li><strong>io.dataease.plugins</strong> - 插件基础框架
 *     <ul>
 *       <li>实现 {@code DataEasePlugin} 接口</li>
 *       <li>集成 {@code DataEasePluginFactory} 工厂</li>
 *     </ul>
 *   </li>
 *   <li><strong>io.dataease.exception</strong> - 异常处理模块</li>
 *   <li><strong>io.dataease.license</strong> - 许可证管理模块</li>
 * </ul>
 *
 * <h3>被使用关系</h3>
 * <ul>
 *   <li><strong>Core模块</strong> - DataEase核心业务模块使用本扩展进行图表渲染</li>
 *   <li><strong>第三方插件</strong> - 外部插件开发者基于本扩展开发自定义图表</li>
 * </ul>
 *
 * <h2>最佳实践建议</h2>
 *
 * <h3>1. 插件开发</h3>
 * <ul>
 *   <li>继承 {@code DataEaseChartPlugin} 而不是直接实现接口</li>
 *   <li>重写所有抽象方法，确保完整的插件生命周期</li>
 *   <li>在插件加载时检查依赖和配置的完整性</li>
 *   <li>使用工厂模式管理插件实例，避免重复创建</li>
 * </ul>
 *
 * <h3>2. 数据处理</h3>
 * <ul>
 *   <li>使用提供的工具类进行数据转换，保持一致性</li>
 *   <li>注意数据类型转换和精度处理</li>
 *   <li>合理使用缓存机制提高性能</li>
 *   <li>处理大数据量时考虑分页和流式处理</li>
 * </ul>
 *
 * <h3>3. 过滤器使用</h3>
 * <ul>
 *   <li>构建复杂过滤条件时使用树形结构</li>
 *   <li>合理使用动态时间过滤减少硬编码</li>
 *   <li>注意过滤器的性能影响，避免过度复杂的条件</li>
 *   <li>为用户提供友好的过滤条件构建界面</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>插件功能需要有效的企业版许可证支持</li>
 *   <li>自定义插件需要遵循DataEase的安全策略</li>
 *   <li>插件开发时需要考虑向前兼容性</li>
 *   <li>数据处理时要注意脱敏规则和权限控制</li>
 * </ul>
 *
 * @author DataEase Team
 * @since 2.0
 * @version 2.10.0
 */
package io.dataease.extensions.view;