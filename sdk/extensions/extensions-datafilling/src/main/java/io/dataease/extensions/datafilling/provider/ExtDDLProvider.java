package io.dataease.extensions.datafilling.provider;


import io.dataease.extensions.datasource.dto.TableField;
import io.dataease.extensions.datafilling.dto.ExtIndexField;
import io.dataease.extensions.datafilling.dto.ExtTableField;
import io.dataease.extensions.datasource.dto.TableFieldWithValue;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据填报扩展DDL提供者抽象基类
 * 为不同数据库类型提供DDL（数据定义语言）SQL语句的生成能力
 *
 * <p>设计模式：</p>
 * <ul>
 *     <li>策略模式：不同数据库类型实现不同的DDL生成策略</li>
 *     <li>模板方法模式：定义SQL生成的标准流程，具体SQL语法由子类实现</li>
 * </ul>
 *
 * <p>在数据填报模块中的作用：</p>
 * <ul>
 *     <li>根据表单字段配置生成数据库表的DDL语句（CREATE TABLE、ALTER TABLE等）</li>
 *     <li>为数据填报表单提供表结构的动态创建和修改能力</li>
 *     <li>生成数据的增删改查SQL语句，支持数据填报的数据操作</li>
 *     <li>处理不同数据库的SQL方言差异，提供统一的接口</li>
 * </ul>
 *
 * <p>职责说明：</p>
 * <ul>
 *     <li>表结构DDL：建表、删表、增加列、删除列、索引管理</li>
 *     <li>数据DML：插入、更新、删除、查询数据</li>
 *     <li>数据验证：唯一性校验、数据查询等辅助SQL</li>
 *     <li>类型映射：提供字段类型到数据库类型的映射</li>
 * </ul>
 *
 * <p>实现要求：</p>
 * <ul>
 *     <li>子类必须针对特定数据库实现所有抽象方法</li>
 *     <li>生成的SQL必须符合目标数据库的语法规范</li>
 *     <li>必须正确处理字段名和表名的引号转义（如MySQL的反引号、Oracle的双引号）</li>
 *     <li>需要考虑数据库的关键字冲突，对表名和列名进行适当的转义</li>
 *     <li>SQL注入防护：使用参数化查询，避免直接拼接用户输入</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *     <li>用户在数据填报设计器中设计表单时，根据字段配置生成对应的数据库表结构</li>
 *     <li>表单字段修改时，生成ALTER TABLE语句更新表结构</li>
 *     <li>用户填写表单提交数据时，生成INSERT或UPDATE语句保存数据</li>
 *     <li>查询已填报的数据时，生成SELECT语句检索数据</li>
 * </ul>
 *
 * <p>典型实现示例：</p>
 * <pre>
 * // MySQL数据库的DDL提供者实现
 * public class MysqlExtDDLProvider extends ExtDDLProvider {
 *
 *     &#64;Override
 *     public String createTableSql(String table, List&lt;ExtTableField&gt; formFields) {
 *         StringBuilder sql = new StringBuilder("CREATE TABLE `" + table + "` (");
 *         // 添加字段定义...
 *         sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
 *         return sql.toString();
 *     }
 *
 *     &#64;Override
 *     public Integer getColumnType(String name) {
 *         // 返回java.sql.Types中定义的类型常量
 *         return switch(name) {
 *             case "nvarchar" -&gt; Types.VARCHAR;
 *             case "number" -&gt; Types.INTEGER;
 *             default -&gt; Types.VARCHAR;
 *         };
 *     }
 * }
 * </pre>
 */
public abstract class ExtDDLProvider {

    /**
     * 默认日期时间格式字符串
     * 用于统一数据填报系统中日期时间类型字段的格式化输出
     * 该格式应用于查询结果的日期格式化，保证前端显示的一致性
     */
    public final String DEFAULT_DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

    /**
     * 生成创建表的DDL语句
     * 根据数据填报表单的字段配置，生成创建数据库表的SQL语句
     *
     * <p>实现要点：</p>
     * <ul>
     *     <li>根据formFields中的字段配置，生成表的所有列定义</li>
     *     <li>处理字段类型映射（ExtTableField.BaseType到数据库类型）</li>
     *     <li>设置字段约束（NOT NULL、PRIMARY KEY等）</li>
     *     <li>处理字段长度和精度（VARCHAR长度、DECIMAL精度等）</li>
     *     <li>为表名和列名添加适当的引号转义</li>
     * </ul>
     *
     * @param table 要创建的表名
     * @param formFields 表单字段配置列表，包含字段名称、类型、约束等信息
     * @return 创建表的完整SQL语句，如 "CREATE TABLE `my_table` (`id` BIGINT PRIMARY KEY, `name` VARCHAR(100))"
     */
    public abstract String createTableSql(String table, List<ExtTableField> formFields);

    /**
     * 生成查询表字段的SQL语句
     *
     * @param table 表名
     * @return 查询表字段的SQL语句
     * @deprecated 该方法已废弃，不再使用
     *             废弃原因：使用JDBC的DatabaseMetaData.getColumns()方法获取表字段信息更加标准和可靠
     *             替代方案：使用 Connection.getMetaData().getColumns(catalog, schema, table, null) 获取字段信息
     */
    @Deprecated
    public String getTableFieldsSql(String table) {
        String sql = "SELECT * FROM `$TABLE_NAME$` LIMIT 0 OFFSET 0";
        return sql.replace("$TABLE_NAME$", table);
    }

    /**
     * 生成添加或修改表字段的DDL语句
     * 根据表单字段的变更，生成ALTER TABLE语句来添加新字段或修改现有字段
     *
     * <p>实现要点：</p>
     * <ul>
     *     <li>formFieldsToCreate：需要新增的字段列表，生成 ADD COLUMN 语句</li>
     *     <li>formFieldsToModify：需要修改的字段列表，生成 MODIFY COLUMN 或 ALTER COLUMN 语句</li>
     *     <li>注意不同数据库的ALTER TABLE语法差异（MySQL使用MODIFY，PostgreSQL使用ALTER COLUMN）</li>
     *     <li>可以将多个字段变更合并到一条ALTER TABLE语句中（用逗号分隔）</li>
     *     <li>字段修改时需要保留原有数据，避免数据丢失</li>
     * </ul>
     *
     * @param table 要修改的表名
     * @param formFieldsToCreate 需要添加的字段列表
     * @param formFieldsToModify 需要修改的字段列表
     * @return ALTER TABLE语句，如 "ALTER TABLE `my_table` ADD COLUMN `age` INT, MODIFY COLUMN `name` VARCHAR(200)"
     */
    public abstract String addTableColumnSql(String table, List<ExtTableField> formFieldsToCreate, List<ExtTableField> formFieldsToModify);

    /**
     * 生成删除表字段的DDL语句
     * 根据要删除的字段列表，生成ALTER TABLE DROP COLUMN语句
     *
     * <p>实现要点：</p>
     * <ul>
     *     <li>生成 ALTER TABLE DROP COLUMN 语句删除指定字段</li>
     *     <li>可以将多个字段删除合并到一条ALTER TABLE语句中</li>
     *     <li>删除字段时需要注意数据丢失风险，建议在业务层做好数据备份</li>
     *     <li>注意不同数据库的DROP COLUMN语法差异</li>
     * </ul>
     *
     * @param table 要修改的表名
     * @param formFields 要删除的字段列表
     * @return ALTER TABLE DROP COLUMN语句，如 "ALTER TABLE `my_table` DROP COLUMN `old_field`"
     */
    public abstract String dropTableColumnSql(String table, List<ExtTableField> formFields);

    /**
     * 生成查询数据的SQL语句
     *
     * @param table 表名
     * @param formFields 查询字段列表
     * @param whereSql WHERE条件子句（不含WHERE关键字）
     * @param limit 限制返回记录数，0表示不限制
     * @param offset 偏移量，用于分页查询
     * @return 完整的SELECT查询语句
     * @deprecated 该方法已废弃，不再使用
     *             废弃原因：直接拼接SQL字符串存在SQL注入风险，且不够灵活
     *             替代方案：使用MyBatis或JPA等ORM框架，通过参数化查询方式构建SQL
     */
    @Deprecated
    public String searchSql(String table, List<TableField> formFields, String whereSql, long limit, long offset) {
        String baseSql = "SELECT $Column_Fields$ FROM `$TABLE_NAME$` $WHERE_SQL$ ;";
        if (limit > 0) {
            baseSql = "SELECT $Column_Fields$ FROM `$TABLE_NAME$` $WHERE_SQL$ LIMIT $OFFSET_COUNT$, $LIMIT_COUNT$ ;";
        }
        baseSql = baseSql.replace("$TABLE_NAME$", table)
                .replace("$OFFSET_COUNT$", Long.toString(offset))
                .replace("$LIMIT_COUNT$", Long.toString(limit));
        if (StringUtils.isBlank(whereSql)) {
            baseSql = baseSql.replace("$WHERE_SQL$", "");
        } else {
            baseSql = baseSql.replace("$WHERE_SQL$", whereSql);
        }
        baseSql = baseSql.replace("$Column_Fields$", convertSearchFields(formFields));
        return baseSql;
    }

    /**
     * 将查询字段列表转换为SQL的SELECT字段列表
     * 特殊处理日期时间类型字段，使用DATE_FORMAT格式化为统一格式
     *
     * @param formFields 表单字段列表
     * @return SQL的字段列表字符串，如 "`id`, DATE_FORMAT(`create_time`,'%Y-%m-%d %H:%i:%S'), `name`"
     */
    private String convertSearchFields(List<TableField> formFields) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < formFields.size(); i++) {
            TableField f = formFields.get(i);
            if (StringUtils.equalsAnyIgnoreCase(f.getFieldType(), "datetime")) {
                //特殊处理，全部使用统一格式输出
                builder.append("DATE_FORMAT(`").append(f.getOriginName()).append("`,'%Y-%m-%d %H:%i:%S')");
            } else {
                builder.append("`").append(f.getOriginName()).append("`");
            }
            if (i < formFields.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    /**
     * 生成查询列的不重复值SQL语句
     * 用于获取某列的所有不重复值，常用于下拉选择框的选项数据源
     *
     * @param table 表名
     * @param column 列名
     * @param order 排序方式，asc（升序）或desc（降序）
     * @return SELECT DISTINCT查询语句
     * @deprecated 该方法已废弃，不再使用
     *             废弃原因：直接拼接SQL字符串存在SQL注入风险
     *             替代方案：使用参数化查询，如 PreparedStatement 或 MyBatis
     */
    @Deprecated
    public String searchColumnData(String table, String column, String order) {
        String baseSql = "SELECT DISTINCT `$Column_Field$` FROM `$TABLE_NAME$` ORDER BY `$Column_Field$` $Column_Order$;";
        baseSql = baseSql.replace("$TABLE_NAME$", table).replace("$Column_Field$", column).replace("$Column_Field$", column);
        if (StringUtils.equalsIgnoreCase(order, "desc")) {
            baseSql = baseSql.replace("$Column_Order$", "DESC");
        } else {
            baseSql = baseSql.replace("$Column_Order$", "ASC");
        }
        return baseSql;
    }

    /**
     * 生成根据字段值查询单条记录的SQL语句
     * 用于根据某个字段的值查询完整的记录信息
     *
     * @param table 表名
     * @param searchFields 需要返回的字段列表
     * @param tableFieldWithValue 查询条件字段及其值
     * @return SELECT查询语句，返回单条记录
     * @deprecated 该方法已废弃，不再使用
     *             废弃原因：功能过于简单，可以用通用的查询方法替代
     *             替代方案：使用ORM框架的findByXxx方法或自定义Repository查询方法
     */
    @Deprecated
    public String searchColumnRowDataOne(String table, List<TableField> searchFields, TableFieldWithValue tableFieldWithValue) {
        String baseSql = "SELECT $Column_Fields$ FROM `$TABLE_NAME$` WHERE `$Column_Field$` = ? LIMIT 1;";
        baseSql = baseSql
                .replace("$Column_Fields$", StringUtils.join(searchFields.stream().map(s -> "`" + s.getOriginName() + "`").toList(), ", "))
                .replace("$TABLE_NAME$", table)
                .replace("$Column_Field$", tableFieldWithValue.getFiledName());
        return baseSql;
    }

    /**
     * 生成统计记录数的SQL语句
     * 用于统计满足条件的记录总数，常用于分页查询的总数计算
     *
     * @param table 表名
     * @param whereSql WHERE条件子句（不含WHERE关键字）
     * @return COUNT查询语句
     * @deprecated 该方法已废弃，不再使用
     *             废弃原因：直接拼接SQL字符串存在SQL注入风险
     *             替代方案：使用MyBatis的count方法或JPA的count查询
     */
    @Deprecated
    public String countSql(String table, String whereSql) {
        String baseSql = "SELECT COUNT(1) FROM `$TABLE_NAME$` $WHERE_SQL$ ;";
        baseSql = baseSql.replace("$TABLE_NAME$", table);
        if (StringUtils.isBlank(whereSql)) {
            baseSql = baseSql.replace("$WHERE_SQL$", "");
        } else {
            baseSql = baseSql.replace("$WHERE_SQL$", whereSql);
        }
        return baseSql;
    }

    /**
     * 生成删除表的DDL语句
     * 生成DROP TABLE语句删除整个数据表
     *
     * <p>实现要点：</p>
     * <ul>
     *     <li>生成DROP TABLE语句，删除指定的表</li>
     *     <li>注意处理表名的引号转义</li>
     *     <li>可以考虑添加IF EXISTS选项，避免表不存在时报错</li>
     *     <li>删除表会导致数据永久丢失，建议在业务层做好数据备份</li>
     * </ul>
     *
     * @param table 要删除的表名
     * @return DROP TABLE语句，如 "DROP TABLE IF EXISTS `my_table`"
     */
    public abstract String dropTableSql(String table);

    /**
     * 生成创建表索引的DDL语句
     * 根据索引字段配置，生成CREATE INDEX语句
     *
     * <p>实现要点：</p>
     * <ul>
     *     <li>支持单列索引和组合索引的创建</li>
     *     <li>处理索引名称的生成规则（可以基于表名和列名组合）</li>
     *     <li>支持索引的排序方式（ASC、DESC）</li>
     *     <li>注意不同数据库的索引语法差异</li>
     *     <li>返回索引创建语句列表，每个索引一条语句</li>
     * </ul>
     *
     * @param table 表名
     * @param indexFields 索引字段配置列表，每个元素代表一个索引
     * @return CREATE INDEX语句列表，如 ["CREATE INDEX idx_name ON `my_table` (`name` ASC)"]
     */
    public abstract List<String> createTableIndexSql(String table, List<ExtIndexField> indexFields);

    /**
     * 生成删除表索引的DDL语句
     * 根据索引字段配置，生成DROP INDEX语句
     *
     * <p>实现要点：</p>
     * <ul>
     *     <li>生成DROP INDEX语句删除指定索引</li>
     *     <li>注意不同数据库的DROP INDEX语法差异（MySQL、Oracle、PostgreSQL语法不同）</li>
     *     <li>MySQL: DROP INDEX index_name ON table_name</li>
     *     <li>PostgreSQL: DROP INDEX index_name</li>
     *     <li>返回索引删除语句列表，每个索引一条语句</li>
     * </ul>
     *
     * @param table 表名
     * @param indexFields 要删除的索引字段配置列表
     * @return DROP INDEX语句列表
     */
    public abstract List<String> dropTableIndexSql(String table, List<ExtIndexField> indexFields);

    /**
     * 生成根据主键删除数据的SQL语句
     * 生成DELETE语句根据主键值批量删除记录
     *
     * <p>实现要点：</p>
     * <ul>
     *     <li>支持单主键和复合主键的删除</li>
     *     <li>支持批量删除，使用IN子句</li>
     *     <li>使用参数化查询，避免SQL注入</li>
     *     <li>返回的SQL应该使用占位符（?），由调用方设置参数值</li>
     * </ul>
     *
     * @param table 表名
     * @param pks 主键字段列表，包含字段名和值
     * @return DELETE语句，如 "DELETE FROM `my_table` WHERE `id` IN (?, ?, ?)"
     */
    public abstract String deleteDataByIdsSql(String table, List<TableFieldWithValue> pks);

    /**
     * 生成批量插入数据的SQL语句
     * 生成INSERT语句批量插入多条记录
     *
     * <p>实现要点：</p>
     * <ul>
     *     <li>支持批量插入，生成多组VALUES子句</li>
     *     <li>根据count参数生成对应数量的值占位符</li>
     *     <li>使用参数化查询，所有值使用?占位符</li>
     *     <li>注意不同数据库的批量插入语法差异</li>
     *     <li>MySQL支持: INSERT INTO table VALUES (...), (...), (...)</li>
     * </ul>
     *
     * @param tableName 表名
     * @param fields 字段列表，定义要插入的字段
     * @param count 批量插入的记录数
     * @return INSERT语句，如 "INSERT INTO `my_table` (`name`, `age`) VALUES (?, ?), (?, ?)"
     */
    public abstract String insertDataSql(String tableName, List<TableFieldWithValue> fields, int count);

    /**
     * 生成根据主键更新数据的SQL语句
     * 生成UPDATE语句根据主键更新单条记录
     *
     * <p>实现要点：</p>
     * <ul>
     *     <li>生成UPDATE语句，SET子句包含所有要更新的字段</li>
     *     <li>WHERE子句使用主键作为条件</li>
     *     <li>使用参数化查询，所有值使用?占位符</li>
     *     <li>注意参数顺序：先是SET子句的字段值，最后是WHERE子句的主键值</li>
     * </ul>
     *
     * @param tableName 表名
     * @param fields 要更新的字段列表
     * @param pk 主键字段及其值
     * @return UPDATE语句，如 "UPDATE `my_table` SET `name` = ?, `age` = ? WHERE `id` = ?"
     */
    public abstract String updateDataByIdSql(String tableName, List<TableFieldWithValue> fields, TableFieldWithValue pk);

    /**
     * 生成检查字段值唯一性的SQL语句
     * 用于验证字段值在表中是否唯一（用于唯一性校验）
     *
     * @param tableName 表名
     * @param field 要检查唯一性的字段及其值
     * @param pk 主键字段及其值，用于排除当前记录（更新时）
     * @return COUNT查询语句，返回值为0表示唯一，大于0表示重复
     * @deprecated 该方法已废弃，不再使用
     *             废弃原因：直接拼接SQL字符串存在SQL注入风险，且功能单一
     *             替代方案：使用ORM框架的exists查询或自定义唯一性验证器
     */
    @Deprecated
    public String checkUniqueValueSql(String tableName, TableFieldWithValue field, TableFieldWithValue pk) {
        String sql = "SELECT COUNT(1) FROM `$TABLE_NAME$` WHERE `$Column_Field$` = ? $PRIMARY_KEY_CONDITION$;";

        StringBuilder pkCondition = new StringBuilder();
        if (pk != null) {
            pkCondition.append("AND `").append(pk.getFiledName()).append("` != ?");
        }

        return sql.replace("$TABLE_NAME$", tableName)
                .replace("$Column_Field$", field.getFiledName())
                .replace("$PRIMARY_KEY_CONDITION$", pkCondition.toString());
    }

    /**
     * 生成WHERE条件子句
     * 根据查询字段列表构建WHERE条件，支持多种比较运算符
     *
     * <p>支持的查询条件类型：</p>
     * <ul>
     *     <li>eq（等于）：字段 = 值</li>
     *     <li>not_eq（不等于）：字段 != 值</li>
     *     <li>lt（小于）：字段 &lt; 值</li>
     *     <li>gt（大于）：字段 &gt; 值</li>
     *     <li>le（小于等于）：字段 &lt;= 值</li>
     *     <li>ge（大于等于）：字段 &gt;= 值</li>
     *     <li>null（为空）：字段 IS NULL</li>
     *     <li>not_null（不为空）：字段 IS NOT NULL</li>
     *     <li>in（包含多个值）：字段 IN (值1, 值2, ...)</li>
     * </ul>
     *
     * @param tableName 表名（该参数未使用，保留用于扩展）
     * @param searchFields 查询字段列表，每个字段包含term（条件类型）和inCount（IN条件的值数量）
     * @return WHERE子句字符串，如 "WHERE 1 = 1 AND `name` = ? AND `age` > ?"
     * @deprecated 该方法已废弃，不再使用
     *             废弃原因：直接拼接SQL字符串存在SQL注入风险，且不够灵活
     *             替代方案：使用MyBatis Dynamic SQL、JPA Criteria API或QueryDSL等动态SQL构建工具
     */
    @Deprecated
    public String whereSql(String tableName, List<TableField> searchFields) {
        StringBuilder builder = new StringBuilder("WHERE 1 = 1 ");
        for (TableField searchField : searchFields) {
            if (searchField.getInCount() > 1) {
                List<String> pList = new ArrayList<>();
                for (int i = 0; i < searchField.getInCount(); i++) {
                    pList.add("?");
                }
                String str = "AND $Column_Field$ IN (" + String.join(", ", pList) + ")";
                builder.append(str.replace("$Column_Field$", searchField.getOriginName()));
            } else {
                switch (searchField.getTerm()) {
                    case "not_eq":
                        builder.append(("AND $Column_Field$ " + "!=" + " ? ").replace("$Column_Field$", searchField.getOriginName()));
                        break;
                    case "lt":
                        builder.append(("AND $Column_Field$ " + "<" + " ? ").replace("$Column_Field$", searchField.getOriginName()));
                        break;
                    case "gt":
                        builder.append(("AND $Column_Field$ " + ">" + " ? ").replace("$Column_Field$", searchField.getOriginName()));
                        break;
                    case "le":
                        builder.append(("AND $Column_Field$ " + "<=" + " ? ").replace("$Column_Field$", searchField.getOriginName()));
                        break;
                    case "ge":
                        builder.append(("AND $Column_Field$ " + ">=" + " ? ").replace("$Column_Field$", searchField.getOriginName()));
                        break;
                    case "null":
                        builder.append("AND $Column_Field$ IS NULL ");
                        break;
                    case "not_null":
                        builder.append("AND $Column_Field$ IS NOT NULL ");
                        break;
                    default:
                        builder.append(("AND $Column_Field$ " + "=" + " ? ").replace("$Column_Field$", searchField.getOriginName()));
                        break;
                }

            }
        }
        return builder.toString();
    }

    /**
     * 获取数据库表名大小写配置的SQL语句
     * 用于查询MySQL数据库的lower_case_table_names配置项
     *
     * @return 查询lower_case_table_names配置的SQL语句
     * @deprecated 该方法已废弃，不再使用
     *             废弃原因：该方法仅适用于MySQL，不具有通用性
     *             替代方案：直接在代码中处理表名大小写，或使用数据库驱动的元数据API
     */
    @Deprecated
    public String getLowerCaseTaleNames() {
        return "SHOW VARIABLES LIKE 'lower_case_table_names'";
    }

    /**
     * 获取字段类型对应的JDBC类型码
     * 将数据填报字段类型（如nvarchar、number等）映射为java.sql.Types中定义的类型常量
     *
     * <p>JDBC类型常量示例：</p>
     * <ul>
     *     <li>Types.VARCHAR = 12（字符串类型）</li>
     *     <li>Types.INTEGER = 4（整数类型）</li>
     *     <li>Types.DECIMAL = 3（小数类型）</li>
     *     <li>Types.TIMESTAMP = 93（时间戳类型）</li>
     * </ul>
     *
     * @param name 字段类型名称，如"nvarchar"、"number"、"datetime"等
     * @return java.sql.Types中定义的类型常量值，用于PreparedStatement.setObject()
     */
    public abstract Integer getColumnType(String name);

    /**
     * 生成清空表数据的SQL语句
     * 生成TRUNCATE TABLE语句，删除表中的所有数据但保留表结构
     *
     * <p>TRUNCATE与DELETE的区别：</p>
     * <ul>
     *     <li>TRUNCATE速度更快，不记录详细日志</li>
     *     <li>TRUNCATE会重置自增计数器</li>
     *     <li>TRUNCATE不能回滚（在某些数据库中）</li>
     *     <li>TRUNCATE不触发DELETE触发器</li>
     * </ul>
     *
     * @param table 表名
     * @return TRUNCATE TABLE语句，如 "TRUNCATE TABLE `my_table`"
     */
    public abstract String truncateTable(String table);

    /**
     * 生成查询表中所有记录ID的SQL语句
     * 用于获取表中指定主键列的所有ID值，常用于批量数据处理
     *
     * @param table 表名
     * @param keyColumn 主键列名
     * @return SELECT查询语句，如 "SELECT `id` FROM `my_table`"
     */
    public abstract String listAllIds(String table, String keyColumn);

}
