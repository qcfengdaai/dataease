package io.dataease.extensions.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DataEase SQL对象数据模型
 * <p>
 * 用于封装SQL查询的各个组成部分，包括表名、字段名、排序、
 * 分组、过滤条件等。用于动态构建和管理复杂的SQL查询语句。
 * </p>
 *
 * @author gin
 * @author fit2cloud
 * @since 1.0
 * @date 2023/3/23 16:12 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SQLObj {
    /**
     * 表模式名
     * <p>
     * 数据库表所属的模式（schema）名称。
     * 在某些数据库中用于区分不同的命名空间。
     * </p>
     */
    private String tableSchema;
    /**
     * 表名
     * <p>
     * SQL查询中的数据表名称。
     * 为SQL查询的核心对象，指定数据来源。
     * </p>
     */
    private String tableName;
    /**
     * 表别名
     * <p>
     * 数据表在SQL查询中使用的别名。
     * 用于简化查询语句或在连接查询中区分表。
     * </p>
     */
    private String tableAlias;

    /**
     * 字段名
     * <p>
     * SQL查询中的字段（列）名称。
     * 指定要查询的具体数据字段。
     * </p>
     */
    private String fieldName;
    /**
     * 字段别名
     * <p>
     * 字段在查询结果中的别名。
     * 用于改变输出列名或提供更有意义的显示名称。
     * </p>
     */
    private String fieldAlias;

    /**
     * 分组字段
     * <p>
     * GROUP BY子句中使用的字段名。
     * 用于数据分组聚合操作。
     * </p>
     */
    private String groupField;
    /**
     * 分组别名
     * <p>
     * 分组字段的别名，用于分组操作中的引用。
     * 在复杂聚合查询中提供更清晰的字段标识。
     * </p>
     */
    private String groupAlias;

    /**
     * 排序字段
     * <p>
     * ORDER BY子句中使用的字段名。
     * 指定结果集排序的依据字段。
     * </p>
     */
    private String orderField;
    /**
     * 排序别名
     * <p>
     * 排序字段的别名，用于排序操作中的引用。
     * 在使用计算字段或聚合函数排序时尤为有用。
     * </p>
     */
    private String orderAlias;
    /**
     * 排序方向
     * <p>
     * 指定排序的方向，如ASC（升序）或DESC（降序）。
     * 控制查询结果的排列顺序。
     * </p>
     */
    private String orderDirection;

    /**
     * 过滤字段
     * <p>
     * WHERE子句中使用的字段名。
     * 用于数据过滤和条件查询。
     * </p>
     */
    private String whereField;
    /**
     * 过滤别名
     * <p>
     * 过滤字段的别名，用于条件判断中的引用。
     * 在复杂条件查询中提供更清晰的字段标识。
     * </p>
     */
    private String whereAlias;
    /**
     * 过滤条件和值
     * <p>
     * WHERE子句中的完整条件表达式，包括操作符和值。
     * 如“> 100”、“= 'admin'”等具体的过滤条件。
     * </p>
     */
    private String whereTermAndValue;

    /**
     * 限制字段
     * <p>
     * LIMIT子句的参数，用于限制查询结果的数量。
     * 控制返回的记录数，用于分页和性能优化。
     * </p>
     */
    private String limitFiled;

    /**
     * 对象标识符
     * <p>
     * SQL对象的唯一标识符，用于区分和管理不同的SQL对象。
     * 在复杂查询构建和缓存管理中使用。
     * </p>
     */
    private Long id;
}
