package io.dataease.extensions.datasource.constant;

/**
 * DataEase数据源SQL占位符常量定义类
 * <p>
 * 定义了在数据源查询、计算字段处理和SQL解析过程中使用的各种占位符常量。
 * 这些占位符用于SQL模板的参数化处理、表名替换、关键字转义等场景，
 * 确保跨不同数据库的SQL语法兼容性和查询的正确性。
 * </p>
 *
 * <p><b>主要应用场景：</b></p>
 * <ul>
 *   <li><b>SQL模板处理</b> - 使用占位符替代实际的表名和字段名</li>
 *   <li><b>计算字段</b> - 在数据集中动态生成计算字段的SQL表达式</li>
 *   <li><b>关键字转义</b> - 处理数据库保留关键字和特殊字符</li>
 *   <li><b>查询优化</b> - 通过占位符实现SQL的预编译和重用</li>
 * </ul>
 *
 * @author Junjun
 * @author fit2cloud
 * @since 1.0
 */
public class SqlPlaceholderConstants {
    /**
     * 数据表占位符SQL模板
     * <p>
     * 标准的表占位符查询模板，用于在SQL构建过程中临时替代实际的表名。
     * 该占位符会在实际查询执行前被替换为真实的表名，确保SQL语法的正确性。
     * 主要用于数据集定义、查询模板生成和SQL验证等场景。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>数据集创建时的默认查询模板</li>
     *   <li>SQL语法验证和解析</li>
     *   <li>动态表名替换的基础模板</li>
     * </ul>
     */
    public static final String TABLE_PLACEHOLDER = "SELECT * FROM DE_PLACEHOLDER_TABLE_0";

    /**
     * 数据库关键字前缀正则表达式
     * <p>
     * 用于匹配数据库标识符（表名、字段名等）前缀的转义字符，支持多种数据库的转义语法。
     * 包含反引号(`)、单引号(')、双引号(")和方括号([)等常见的数据库标识符转义字符。
     * 该正则表达式使用"?"表示这些字符可选，用于处理有转义和无转义两种情况。
     * </p>
     *
     * <p><b>支持的转义字符：</b></p>
     * <ul>
     *   <li><b>`</b> - MySQL、MariaDB等使用的反引号转义</li>
     *   <li><b>'</b> - 部分数据库的单引号转义</li>
     *   <li><b>"</b> - PostgreSQL、Oracle等使用的双引号转义</li>
     *   <li><b>[</b> - SQL Server使用的方括号转义前缀</li>
     * </ul>
     */
    public static final String KEYWORD_PREFIX_REGEX = "[`'\"\\[]?";

    /**
     * 数据库关键字后缀正则表达式
     * <p>
     * 用于匹配数据库标识符（表名、字段名等）后缀的转义字符，与前缀正则表达式对应。
     * 包含反引号(`)、单引号(')、双引号(")和方括号(])等常见的数据库标识符转义字符。
     * 与前缀正则配合使用，完整匹配被转义的数据库标识符。
     * </p>
     *
     * <p><b>支持的转义字符：</b></p>
     * <ul>
     *   <li><b>`</b> - MySQL、MariaDB等使用的反引号转义</li>
     *   <li><b>'</b> - 部分数据库的单引号转义</li>
     *   <li><b>"</b> - PostgreSQL、Oracle等使用的双引号转义</li>
     *   <li><b>]</b> - SQL Server使用的方括号转义后缀</li>
     * </ul>
     */
    public static final String KEYWORD_SUFFIX_REGEX = "[`'\"\\]]?";

    /**
     * 表占位符完整匹配正则表达式
     * <p>
     * 用于匹配和识别包含表占位符的完整SQL语句的正则表达式。
     * 结合了前缀和后缀正则表达式，能够准确匹配各种数据库中可能存在转义字符的表占位符。
     * 主要用于SQL解析、占位符替换和查询模板识别等场景。
     * </p>
     *
     * <p><b>匹配示例：</b></p>
     * <ul>
     *   <li>SELECT * FROM DE_PLACEHOLDER_TABLE_0</li>
     *   <li>SELECT * FROM `DE_PLACEHOLDER_TABLE_0`</li>
     *   <li>SELECT * FROM "DE_PLACEHOLDER_TABLE_0"</li>
     *   <li>SELECT * FROM [DE_PLACEHOLDER_TABLE_0]</li>
     * </ul>
     *
     * <p><b>应用场景：</b></p>
     * <ul>
     *   <li>SQL模板解析和验证</li>
     *   <li>占位符自动替换</li>
     *   <li>跨数据库SQL兼容性处理</li>
     * </ul>
     */
    public static final String TABLE_PLACEHOLDER_REGEX = "SELECT \\* FROM " + KEYWORD_PREFIX_REGEX + "DE_PLACEHOLDER_TABLE_0" + KEYWORD_SUFFIX_REGEX;

    /**
     * 计算字段占位符模板
     * <p>
     * 用于生成计算字段占位符的字符串格式化模板。通过String.format()方法与具体的字段标识符结合，
     * 生成唯一的计算字段占位符。这些占位符在数据集处理过程中会被替换为实际的计算表达式，
     * 支持复杂的字段计算和数据转换逻辑。
     * </p>
     *
     * <p><b>使用方法：</b></p>
     * <pre><code>
     * // 生成计算字段占位符
     * String placeholder = String.format(CALC_FIELD_PLACEHOLDER, "field_001");
     * // 结果：DE_CALC_FIELD_PLACEHOLDER_field_001
     * </code></pre>
     *
     * <p><b>应用场景：</b></p>
     * <ul>
     *   <li>动态计算字段的SQL表达式生成</li>
     *   <li>数据集中的字段运算和转换</li>
     *   <li>复杂查询的字段占位和替换</li>
     *   <li>多层嵌套查询的字段引用管理</li>
     * </ul>
     */
    public static final String CALC_FIELD_PLACEHOLDER = "DE_CALC_FIELD_PLACEHOLDER_%s";
}
