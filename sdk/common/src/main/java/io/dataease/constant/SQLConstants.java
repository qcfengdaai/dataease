package io.dataease.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * DataEase SQL相关常量类
 * <p>
 * 定义系统中所有与SQL查询、数据处理和数据库操作相关的常量值，包括数据类型定义、
 * SQL模板格式、别名前缀、函数模板等。这些常量主Apache Calcite查询引擎配合使用，
 * 实现跨数据源的统一SQL查询能力。
 * </p>
 *
 * <p><b>主要功能领域：</b></p>
 * <ul>
 *   <li><b>数据类型定义</b> - 维度、指标等数据类型的分类和标识</li>
 *   <li><b>SQL模板化</b> - 基于StringTemplate的SQL生成模板</li>
 *   <li><b>别名管理</b> - 数据库表、字段、分组等SQL元素的别名生成</li>
 *   <li><b>函数封装</b> - 跨数据库的时间、数值处理函数模板</li>
 *   <li><b>查询优化</b> - 聚合、排序、过滤等查询操作的标准化</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 */
public class SQLConstants {
    /**
     * 维度数据类型列表
     * <p>
     * 定义数据分析中作为维度使用的数据类型集合。维度通常用于数据分组、分类
     * 和筛选，不参与数值计算。支持的维度类型包括文本、时间和地理位置信息。
     * </p>
     *
     * <p><b>类型对应关系：</b></p>
     * <ul>
     *   <li>0 - 文本类型：字符串、分类标签等</li>
     *   <li>1 - 时间类型：日期、时间戳、时间间隔等</li>
     *   <li>5 - 地理位置类型：省份、城市、经纬度等</li>
     * </ul>
     */
    public static final List<Integer> DIMENSION_TYPE = new ArrayList<Integer>() {{
        add(0);// 文本类型 - 字符串、分类标签
        add(1);// 时间类型 - 日期时间、时间戳
        add(5);// 地理位置类型 - 省份、城市、坐标
    }};

    /**
     * 指标数据类型列表
     * <p>
     * 定义数据分析中作为指标使用的数据类型集合。指标通常用于数值计算、
     * 统计聚合和度量衡量，支持各种数学运算。支持的指标类型包括整数、浮点数和布尔值。
     * </p>
     *
     * <p><b>类型对应关系：</b></p>
     * <ul>
     *   <li>2 - 整数类型：计数、排名、编号等</li>
     *   <li>3 - 浮点数类型：金额、比率、平均值等</li>
     *   <li>4 - 布尔类型：是否标记、成功失败标志等</li>
     * </ul>
     */
    public static final List<Integer> QUOTA_TYPE = new ArrayList<Integer>() {{
        add(2);// 整数类型 - 计数、排名等
        add(3);// 浮点数类型 - 金额、比率等
        add(4);// 布尔类型 - 成功失败标志等
    }};

    /**
     * SQL StringTemplate模板文件名
     * <p>
     * 指向StringTemplate组模板引擎使用的模板文件，用于动态生成适用于
     * 不同数据库的SQL语句。模板中定义了各种数据库方言的SQL语法规则。
     * </p>
     */
    public static final String SQL_TEMPLATE = "sqlTemplate.stg";

    /**
     * 数据库模式名别名格式模板
     * <p>
     * 用于生成数据库模式（Schema）的别名，遵循 "s_a_" 前缀命名规范。
     * 主要用于多数据源环境下的数据库隔离和标识。
     * </p>
     */
    public static final String SCHEMA = "s_a_%s";

    /**
     * 数据库表别名格式模板
     * <p>
     * 用于在SQL查询中生成表的别名，遵循 "t_a_" 前缀命名规范。
     * 主要用于复杂查询中的表联接和字段消歧。
     * </p>
     */
    public static final String TABLE_ALIAS_PREFIX = "t_a_%s";

    /**
     * X轴字段别名格式模板
     * <p>
     * 用于生成图表X轴（维度）字段的别名，遵循 "f_ax_" 前缀命名规范。
     * 主要用于图表显示和数据分组时的字段标识。
     * </p>
     */
    public static final String FIELD_ALIAS_X_PREFIX = "f_ax_%s";

    /**
     * Y轴字段别名格式模板
     * <p>
     * 用于生成图表Y轴（指标）字段的别名，遵循 "f_ay_" 前缀命名规范。
     * 主要用于图表显示和数值计算时的字段标识。
     * </p>
     */
    public static final String FIELD_ALIAS_Y_PREFIX = "f_ay_%s";

    /**
     * 分组字段别名格式模板
     * <p>
     * 用于生成GROUP BY子句中分组字段的别名，遵循 "g_a_" 前缀命名规范。
     * 主要用于数据聚合和统计分析中的分组操作。
     * </p>
     */
    public static final String GROUP_ALIAS_PREFIX = "g_a_%s";

    /**
     * X轴排序字段别名格式模板
     * <p>
     * 用于生成ORDER BY子句中X轴排序字段的别名，遵循 "o_ax_" 前缀命名规范。
     * 主要用于图表数据的排序显示和维度排列。
     * </p>
     */
    public static final String ORDER_ALIAS_X_PREFIX = "o_ax_%s";

    /**
     * Y轴排序字段别名格式模板
     * <p>
     * 用于生成ORDER BY子句中Y轴排序字段的别名，遵循 "o_ay_" 前缀命名规范。
     * 主要用于图表数据的排序显示和指标排列。
     * </p>
     */
    public static final String ORDER_ALIAS_Y_PREFIX = "o_ay_%s";

    /**
     * 过滤条件别名格式模板
     * <p>
     * 用于生成WHERE子句中过滤条件的别名，遵循 "w_a_" 前缀命名规范。
     * 主要用于复杂查询中的条件过滤和数据筛选。
     * </p>
     */
    public static final String WHERE_ALIAS_PREFIX = "w_a_%s";

    /**
     * 完整表名格式模板
     * <p>
     * 用于生成带有数据库模式的完整表名，格式为 "schema.`table`"。
     * 适用于需要明确指定数据库和表名的查询场景。
     * </p>
     */
    public static final String TABLE_NAME = "%s.`%s`";

    /**
     * 完整字段名格式模板
     * <p>
     * 用于生成带有表别名的完整字段名，格式为 "table_alias.`field`"。
     * 适用于多表联接查询中的字段消歧和精确指定。
     * </p>
     */
    public static final String FIELD_NAME = "%s.`%s`";

    /**
     * 简单字段名格式模板
     * <p>
     * 用于生成不需要转义的字段名，直接传入字段值。
     * 适用于简单查询或计算列的场景。
     * </p>
     */
    public static final String FIELD_DOT = "%s";

    /**
     * 带转义的字段名格式模板
     * <p>
     * 用于生成带有反引号转义的字段名，格式为 "`field`"。
     * 适用于包含特殊字符或保留字的字段名处理。
     * </p>
     */
    public static final String FIELD_DOT_FIX = "`%s`";

    /**
     * Unix时间戳转换函数模板
     * <p>
     * 用于将日期时间转换为Unix时间戳的跨数据库函数封装。
     * DataEase自定义函数，在不同数据库中自动适配相应的实现。
     * </p>
     */
    public static final String UNIX_TIMESTAMP = "DE_UNIX_TIMESTAMP(%s)";

    /**
     * 日期格式化函数模板
     * <p>
     * 用于将日期时间格式化为指定格式字符串的跨数据库函数封装。
     * 第一个参数为日期字段，第二个参数为格式化模式。
     * </p>
     */
    public static final String DE_DATE_FORMAT = "DE_DATE_FORMAT(%s,'%s')";

    /**
     * 日期类型转换和格式化函数模板
     * <p>
     * 用于先将字段转换为日期类型，再进行格式化的组合操作。
     * 第一个参数为原始字段，第二个参数为源格式，第三个参数为目标格式。
     * </p>
     */
    public static final String DE_CAST_DATE_FORMAT = "DE_CAST_DATE_FORMAT(%s,'%s','%s')";

    /**
     * Unix时间戳转日期函数模板
     * <p>
     * 用于将Unix时间戳转换为指定格式日期字符串的跨数据库函数封装。
     * 第一个参数为时间戳字段，第二个参数为日期格式模式。
     * </p>
     */
    public static final String FROM_UNIXTIME = "DE_FROM_UNIXTIME(%s,'%s')";

    /**
     * 字符串转日期函数模板
     * <p>
     * 用于将日期字符串按指定格式解析为日期类型的跨数据库函数封装。
     * 第一个参数为日期字符串字段，第二个参数为日期格式模式。
     * </p>
     */
    public static final String DE_STR_TO_DATE = "DE_STR_TO_DATE(%s,'%s')";

    /**
     * 数据类型转换函数模板
     * <p>
     * 用于在SQL中将一种数据类型显式转换为另一种数据类型。
     * 第一个参数为源字段或值，第二个参数为目标数据类型。
     * </p>
     */
    public static final String CAST = "CAST(%s AS %s)";

    /**
     * 默认日期时间格式
     * <p>
     * 系统中使用的标准日期时间格式模式，遵循ISO 8601标准。
     * 用于日期时间字段的显示和存储格式统一，包括年月日和时分秒。
     * </p>
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认整数数据类型格式
     * <p>
     * 系统中整数类型字段的标准数据库类型定义，支持19位整数。
     * 适用于大部分整数范围的数据存储，包括ID、计数等字段。
     * </p>
     */
    public static final String DEFAULT_INT_FORMAT = "DECIMAL(19,0)";

    /**
     * 默认浮点数数据类型格式
     * <p>
     * 系统中浮点数类型字段的标准数据库类型定义，支持26位总长度和8位小数。
     * 适用于金额、比率、平均值等需要高精度的数值计算。
     * </p>
     */
    public static final String DEFAULT_FLOAT_FORMAT = "DECIMAL(26,8)";

    /**
     * WHERE子句空值条件模板
     * <p>
     * 用于在WHERE条件中匹配NULL或空字符串的条件模板。
     * 适用于空值检查和数据质量验证场景。
     * </p>
     */
    public static final String WHERE_VALUE_NULL = "(NULL,'')";

    /**
     * WHERE子句字符串值条件模板
     * <p>
     * 用于在WHERE条件中匹配字符串值的条件模板，自动添加单引号包装。
     * 适用于文本类型字段的精确匹配和模糊查询。
     * </p>
     */
    public static final String WHERE_VALUE_VALUE = "'%s'";

    /**
     * WHERE子句中文特殊编码值条件模板
     * <p>
     * 用于处理包含中文或特殊字符的数据值，带有特定的编码前缀标识。
     * "-DENS-" 前缀用于标识DataEase系统中的特殊编码字符串。
     * </p>
     */
    public static final String WHERE_VALUE_VALUE_CH = "'-DENS-%s'";

    /**
     * WHERE子句数值条件模板
     * <p>
     * 用于在WHERE条件中匹配数值的条件模板，不添加引号包装。
     * 适用于整数、浮点数等数值类型字段的数值比较。
     * </p>
     */
    public static final String WHERE_NUMBER_VALUE = "%s";

    /**
     * COUNT聚合函数模板
     * <p>
     * 用于统计查询结果的总记录数，是最常用的聚合统计函数。
     * 适用于数据量统计、分组计数等分析场景。
     * </p>
     */
    public static final String AGG_COUNT = "COUNT(*)";

    /**
     * 通用聚合函数模板
     * <p>
     * 用于生成各种聚合函数的通用模板，如SUM、AVG、MAX、MIN等。
     * 第一个参数为聚合函数名，第二个参数为字段名。
     * </p>
     */
    public static final String AGG_FIELD = "%s(%s)";

    /**
     * WHERE子句字符串范围条件模板
     * <p>
     * 用于生成BETWEEN范围查询的字符串值模板，自动添加单引号包装。
     * 适用于日期范围、文本范围等字符串类型的范围查询。
     * </p>
     */
    public static final String WHERE_BETWEEN = "'%s' AND '%s'";

    /**
     * WHERE子句数值范围条件模板
     * <p>
     * 用于生成BETWEEN范围查询的数值模板，不添加引号包装。
     * 适用于数值范围、金额范围等数值类型的范围查询。
     * </p>
     */
    public static final String WHERE_VALUE_BETWEEN = "%s AND %s";

    /**
     * SQL括号包装模板
     * <p>
     * 用于在SQL表达式或条件中添加括号，明确表达式的优先级和逻辑分组。
     * 适用于复杂的WHERE条件、子查询和数学计算表达式。
     * </p>
     */
    public static final String BRACKETS = "(%s)";

    /**
     * 数值圆舍函数模板
     * <p>
     * 用于对浮点数进行指定小数位数的四舍五入操作。
     * 第一个参数为数值字段，第二个参数为保留的小数位数。
     * </p>
     */
    public static final String ROUND = "ROUND(%s,%s)";

    /**
     * 可变长度字符串数据类型
     * <p>
     * SQL中可变长度字符串数据类型的名称，用于数据类型转换和字段定义。
     * 适用于文本类型数据的存储和处理，支持可变长度字符串。
     * </p>
     */
    public static final String VARCHAR = "VARCHAR";

    /**
     * 默认数据库引擎名称
     * <p>
     * DataEase系统中默认使用的数据库引擎名称标识。
     * 用于数据源配置和Apache Calcite查询引擎的适配器选择。
     * </p>
     */
    public static final String NAME = "engine_mysql";

    /**
     * 分组字符串连接函数模板
     * <p>
     * 用于在分组查询中将多个字符串值连接成一个字符串的函数模板。
     * 适用于数据聚合显示、标签合并等需要字符串连接的场景。
     * </p>
     */
    public static final String GROUP_CONCAT = "GROUP_CONCAT(%s)";

    /**
     * 季度提取函数模板
     * <p>
     * 用于从日期字段中提取季度信息的函数模板。
     * 返回值为1-4的整数，分别代表第一、二、三、四季度。
     * </p>
     */
    public static final String QUARTER = "QUARTER(%s)";

    /**
     * 空值标识符
     * <p>
     * 用于标识数据中的空值或缺失值的特殊字符串标识。
     * 主要用于数据处理和显示中的空值占位和标识。
     * </p>
     */
    public static final String EMPTY_SIGN = "_empty_$";

    /**
     * 字符串连接函数模板
     * <p>
     * 用于将两个或多个字符串值连接成一个字符串的函数模板。
     * 适用于字段拼接、文本格式化等字符串处理场景。
     * </p>
     */
    public static final String CONCAT = "CONCAT(%s, %s)";

    /**
     * SQL Server中文特殊编码前缀
     * <p>
     * 用于Microsoft SQL Server数据库中处理包含中文或特殊字符的数据时的前缀标识。
     * 与WHERE_VALUE_VALUE_CH中使用的前缀保持一致，确保跨数据库的兼容性。
     * </p>
     */
    public static final String MSSQL_N_PREFIX = "-DENS-";
}
