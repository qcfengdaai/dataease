package io.dataease.extensions.datasource.dto;

import lombok.Data;

/**
 * DataEase数据源类型数据传输对象
 * <p>
 * 用于传输数据源类型的配置信息，包括数据库类型、名称、
 * SQL语法特性等。用于支持多种不同的数据库和数据源类型。
 * </p>
 *
 * @author Junjun
 * @author fit2cloud
 * @since 1.0
 */
@Data
public class DsTypeDTO {
    /**
     * 数据源类型标识
     * <p>
     * 数据源的类型标识符，如mysql、postgresql、oracle等。
     * 用于区分和标识不同的数据库类型。
     * </p>
     */
    private String type;
    /**
     * 数据源类型名称
     * <p>
     * 数据源类型的显示名称，用于用户界面展示。
     * 通常为中文或友好的显示名称，便于用户理解和选择。
     * </p>
     */
    private String name;

    /**
     * 数据库目录名
     * <p>
     * 数据库中的目录（catalog）名称，用于数据库元数据的管理。
     * 在某些数据库系统中用于多数据库实例的管理。
     * </p>
     */
    private String catalog;
    /**
     * SQL标识符前缀
     * <p>
     * 数据库标识符（表名、字段名等）的转义前缀字符。
     * 如MySQL的反引号（`）、PostgreSQL的双引号（"）等。
     * </p>
     */
    private String prefix;
    /**
     * SQL标识符后缀
     * <p>
     * 数据库标识符（表名、字段名等）的转义后缀字符。
     * 与前缀配合使用，完成数据库标识符的正确转义。
     * </p>
     */
    private String suffix;
}
