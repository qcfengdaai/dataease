package io.dataease.extensions.datasource.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DataEase SQL元数据模型
 * <p>
 * 用于封装图表和仪表板中SQL查询的元数据信息。
 * 包括图表类型、字段配置、过滤条件、排序规则等。
 * </p>
 *
 * @author Junjun
 * @author fit2cloud
 * @since 1.0
 */
@Data
public class SQLMeta {

    /**
     * 图表类型
     * <p>
     * 指定图表的类型，如柱状图、线图、饼图等。
     * 用于决定SQL查询的构建策略和结果处理方式。
     * </p>
     */
    private String chartType;

    /**
     * 数据表对象
     * <p>
     * 主要的数据表SQL对象，包含表名、表别名等信息。
     * 作为整个SQL查询的数据源基础。
     * </p>
     */
    private SQLObj table;

    /**
     * 表的SQL方言占位符
     * <p>
     * 针对不同数据库类型的表名的SQL方言占位符。
     * 用于处理跨数据库的SQL语法兼容性问题。
     * </p>
     */
    private String tableDialect;

    /**
     * X轴字段列表
     * <p>
     * 图表X轴（横轴）使用的字段集合。
     * 通常用于分类字段、时间字段等维度数据。
     * </p>
     */
    private List<SQLObj> xFields;

    /**
     * X轴字段的SQL方言映射
     * <p>
     * X轴字段在不同数据库中的SQL方言对应关系。
     * 用于处理跨数据库的字段处理和函数调用。
     * </p>
     */
    private Map<String, String> xFieldsDialect;

    /**
     * X轴过滤条件列表
     * <p>
     * 针对X轴字段的过滤条件集合。
     * 用于对X轴维度数据进行筛选和过滤。
     * </p>
     */
    private List<String> xWheres;

    /**
     * X轴排序规则列表
     * <p>
     * X轴字段的排序规则配置。
     * 控制X轴维度数据的显示顺序。
     * </p>
     */
    private List<SQLObj> xOrders;

    /**
     * Y轴字段列表
     * <p>
     * 图表Y轴（纵轴）使用的字段集合。
     * 通常用于数值字段、聚合计算字段等指标数据。
     * </p>
     */
    private List<SQLObj> yFields;

    /**
     * Y轴字段的SQL方言映射
     * <p>
     * Y轴字段在不同数据库中的SQL方言对应关系。
     * 用于处理跨数据库的聚合函数和计算表达式。
     * </p>
     */
    private Map<String, String> yFieldsDialect;

    /**
     * Y轴过滤条件列表
     * <p>
     * 针对Y轴字段的过滤条件集合。
     * 用于对Y轴指标数据进行筛选和过滤。
     * </p>
     */
    private List<String> yWheres;

    /**
     * Y轴排序规则列表
     * <p>
     * Y轴字段的排序规则配置。
     * 控制Y轴指标数据的显示顺序。
     * </p>
     */
    private List<SQLObj> yOrders;

    /**
     * 自定义过滤条件
     * <p>
     * 图表级别的自定义过滤条件，由用户配置。
     * 用于对图表数据进行特定的过滤和筛选。
     * </p>
     */
    private String customWheres;

    /**
     * 自定义过滤条件的SQL方言映射
     * <p>
     * 自定义过滤条件在不同数据库中的SQL方言对应关系。
     * 保证过滤条件在跨数据库环境下的兼容性。
     * </p>
     */
    private Map<String, String> customWheresDialect;

    /**
     * 扩展过滤条件
     * <p>
     * 仪表板级别的扩展过滤条件，由仪表板组件传递。
     * 用于仪表板联动过滤和全局数据过滤。
     * </p>
     */
    private String extWheres;

    /**
     * 扩展过滤条件的SQL方言映射
     * <p>
     * 扩展过滤条件在不同数据库中的SQL方言对应关系。
     * 支持仪表板过滤在多种数据源上的一致性执行。
     * </p>
     */
    private Map<String, String> extWheresDialect;

    /**
     * 行级权限过滤条件
     * <p>
     * 基于行级数据权限的过滤条件树。
     * 用于根据用户权限自动过滤数据，保证数据安全。
     * </p>
     */
    private String whereTrees;

    /**
     * 行级权限过滤条件的SQL方言映射
     * <p>
     * 行级权限过滤条件在不同数据库中的SQL方言对应关系。
     * 确保数据权限控制在各种数据库上的一致性和有效性。
     * </p>
     */
    private Map<String, String> whereTreesDialect;

}
