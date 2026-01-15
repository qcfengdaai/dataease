/**
 * 图表扩展视图DTO模块
 *
 * <p>本模块包含所有与图表视图相关的数据传输对象（DTO），用于前后端数据交互</p>
 *
 * <h2>主要分类</h2>
 * <ul>
 *   <li><b>图表视图DTO</b>：图表主体、视图字段、样式配置等</li>
 *   <li><b>数据展示DTO</b>：图表数据、轴数据、系列数据等</li>
 *   <li><b>表格相关DTO</b>：表头、表尾、阈值、分页等</li>
 *   <li><b>过滤相关DTO</b>：自定义过滤、字段过滤等</li>
 *   <li><b>权限相关DTO</b>：列权限、行权限等</li>
 *   <li><b>配置相关DTO</b>：插件配置、字段配置等</li>
 * </ul>
 *
 * <h2>核心DTO说明</h2>
 *
 * <h3>1. 图表视图核心DTO</h3>
 * <ul>
 *   <li>{@link io.dataease.extensions.view.dto.ChartViewDTO} - 图表视图主体DTO，包含完整的图表信息</li>
 *   <li>{@link io.dataease.extensions.view.dto.ChartViewFieldDTO} - 图表视图字段DTO，表示图表中的维度或指标</li>
 *   <li>{@link io.dataease.extensions.view.dto.ChartViewBaseDTO} - 图表视图基础DTO，包含图表的通用属性</li>
 *   <li>{@link io.dataease.extensions.view.dto.ChartDimensionDTO} - 图表维度DTO</li>
 *   <li>{@link io.dataease.extensions.view.dto.ChartQuotaDTO} - 图表指标DTO</li>
 * </ul>
 *
 * <h3>2. 图表数据DTO</h3>
 * <ul>
 *   <li>{@link io.dataease.extensions.view.dto.AxisChartDataAntVDTO} - 轴图表数据DTO（AntV格式）</li>
 *   <li>{@link io.dataease.extensions.view.dto.ChartSeriesDataDTO} - 图表系列数据DTO</li>
 *   <li>{@link io.dataease.extensions.view.dto.ChartFieldDataDTO} - 图表字段数据DTO</li>
 * </ul>
 *
 * <h3>3. 图表扩展DTO</h3>
 * <ul>
 *   <li>{@link io.dataease.extensions.view.dto.ChartExtFilterDTO} - 图表扩展过滤DTO，用于钻取等场景</li>
 *   <li>{@link io.dataease.extensions.view.dto.ChartExtRequest} - 图表扩展请求DTO，包含查询参数</li>
 *   <li>{@link io.dataease.extensions.view.dto.ChartFieldCustomFilterDTO} - 图表字段自定义过滤DTO</li>
 *   <li>{@link io.dataease.extensions.view.dto.ChartCustomFilterItemDTO} - 图表自定义过滤项DTO</li>
 * </ul>
 *
 * <h3>4. 表格相关DTO</h3>
 * <ul>
 *   <li>{@link io.dataease.extensions.view.dto.TableHeader} - 表格表头DTO</li>
 *   <li>{@link io.dataease.extensions.view.dto.TableTotal} - 表格总计DTO</li>
 *   <li>{@link io.dataease.extensions.view.dto.TableThreshold} - 表格阈值DTO，用于条件格式化</li>
 *   <li>{@link io.dataease.extensions.view.dto.TablePageInfo} - 表格分页信息DTO</li>
 * </ul>
 *
 * <h3>5. 样式配置DTO</h3>
 * <ul>
 *   <li>{@link io.dataease.extensions.view.dto.YAxisStyleAntVDTO} - Y轴样式配置DTO</li>
 *   <li>{@link io.dataease.extensions.view.dto.YAxisExtStack} - Y轴扩展堆叠配置DTO</li>
 *   <li>{@link io.dataease.extensions.view.dto.Yaxis} - Y轴配置DTO</li>
 *   <li>{@link io.dataease.extensions.view.dto.ChartAxisDTO} - 图表坐标轴DTO</li>
 * </ul>
 *
 * <h3>6. 权限相关DTO</h3>
 * <ul>
 *   <li>{@link io.dataease.extensions.view.dto.ColumnPermissionItem} - 列权限项DTO，包含脱敏规则</li>
 *   <li>{@link io.dataease.extensions.view.dto.DatasetRowPermissionsTreeItem} - 数据集行权限树项DTO</li>
 * </ul>
 *
 * <h3>7. 配置相关DTO</h3>
 * <ul>
 *   <li>{@link io.dataease.extensions.view.dto.PluginViewSetParam} - 插件视图设置参数DTO</li>
 *   <li>{@link io.dataease.extensions.view.dto.SqlVariableDTO} - SQL变量DTO</li>
 *   <li>{@link io.dataease.extensions.view.dto.FieldParam} - 字段参数DTO</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>1. 创建图表视图</h3>
 * <pre>{@code
 * // 创建图表视图DTO
 * ChartViewDTO chartView = new ChartViewDTO();
 * chartView.setId(1L);
 * chartView.setName("销售趋势图");
 * chartView.setType("line");
 * chartView.setDatasetMode(0); // 数据集模式
 *
 * // 设置X轴字段
 * List<ChartViewFieldDTO> xAxis = new ArrayList<>();
 * ChartViewFieldDTO field = new ChartViewFieldDTO();
 * field.setName("日期");
 * field.setType("date");
 * xAxis.add(field);
 * chartView.setXAxis(xAxis);
 *
 * // 设置Y轴字段
 * List<ChartViewFieldDTO> yAxis = new ArrayList<>();
 * ChartViewFieldDTO quota = new ChartViewFieldDTO();
 * quota.setName("销售额");
 * quota.setType("number");
 * yAxis.add(quota);
 * chartView.setYAxis(yAxis);
 * }</pre>
 *
 * <h3>2. 查询图表数据</h3>
 * <pre>{@code
 * // 构建查询请求
 * ChartExtRequest request = new ChartExtRequest();
 * request.setChartId(1L);
 * request.setDatasetMode(0);
 *
 * // 设置自定义过滤条件
 * List<ChartFieldCustomFilterDTO> filters = new ArrayList<>();
 * ChartFieldCustomFilterDTO filter = new ChartFieldCustomFilterDTO();
 * filter.setName("地区");
 * filter.setFilterTerm("eq"); // 等于
 * filter.setValue("华东");
 * filters.add(filter);
 * request.setCustomFilter(filters);
 *
 * // 发起查询
 * ChartViewDTO result = chartService.getData(request);
 * }</pre>
 *
 * <h3>3. 配置表格阈值</h3>
 * <pre>{@code
 * // 配置条件格式化阈值
 * TableThreshold threshold = new TableThreshold();
 * threshold.setTerm("gt"); // 大于
 * threshold.setValue("10000");
 * threshold.setColor("#ff0000"); // 红色
 * threshold.setBackgroundColor("#ffcccc");
 *
 * // 应用到字段
 * List<TableThreshold> thresholds = new ArrayList<>();
 * thresholds.add(threshold);
 * tableHeader.setThresholds(thresholds);
 * }</pre>
 *
 * <h3>4. 设置列权限脱敏</h3>
 * <pre>{@code
 * // 配置列权限
 * ColumnPermissionItem permission = new ColumnPermissionItem();
 * permission.setId(1L);
 * permission.setName("手机号");
 * permission.setSelected(true);
 * permission.setOpt("desensitization"); // 脱敏操作
 *
 * // 设置脱敏规则
 * ColumnPermissionItem.DesensitizationRule rule = new DesensitizationRule();
 * rule.setBuiltInRule(BuiltInRule.KeepFirstAndLastThreeCharacters);
 * permission.setDesensitizationRule(rule);
 * }</pre>
 *
 * <h2>被使用位置</h2>
 * <ul>
 *   <li>图表管理模块：创建、编辑、删除图表时使用</li>
 *   <li>数据可视化模块：前端展示图表数据时使用</li>
 *   <li>数据集模块：关联数据集和图表时使用</li>
 *   <li>权限管理模块：设置列权限和行权限时使用</li>
 *   <li>仪表板模块：在仪表板中展示图表时使用</li>
 *   <li>数据导出模块：导出图表数据时使用</li>
 *   <li>视图扩展插件：各图表插件使用这些DTO进行数据交互</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>所有DTO都使用Lombok的@Data注解，自动生成getter/setter</li>
 *   <li>时间字段统一使用Long类型（时间戳）</li>
 *   <li>ID字段使用@JsonSerialize(using = ToStringSerializer.class)避免前端精度丢失</li>
 *   <li>枚举类型字段使用字符串存储，方便前端展示</li>
 *   <li>复杂对象字段使用内部类定义，提高可读性</li>
 *   <li>所有属性都有详细的中文注释，便于理解字段含义</li>
 *   <li>DTO主要用于数据传输，不包含业务逻辑</li>
 *   <li>前后端交互时使用统一的DTO格式</li>
 * </ul>
 *
 * <h2>依赖关系</h2>
 * <ul>
 *   <li>依赖 io.dataease.extensions.datasource.dto.DatasetTableFieldDTO（数据集表字段DTO）</li>
 *   <li>被 io.dataease.chart 模块的Service和Controller使用</li>
 *   <li>被 io.dataease.extensions.view 插件系统使用</li>
 *   <li>前端通过API接口与这些DTO进行数据交互</li>
 * </ul>
 *
 * @package io.dataease.extensions.view.dto
 * @since 2.0.0
 */
package io.dataease.extensions.view.dto;
