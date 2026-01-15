package io.dataease.dds.constant;

/**
 * 数据源常量定义类
 * 定义动态数据源系统中使用的各种常量
 * 包括数据源名称、命名模式等配置常量
 */
public class DataSourceConstant {

    /**
     * 默认管理数据源名称
     * 用于DataEase系统管理功能的主数据源
     * 命名规范：配置文件名称基础上添加dataSource前缀并改为小驼峰格式
     */
    public static final String DATA_SOURCE_MANAGE = "manege-ds";

    /**
     * 官方业务数据源名称
     * 用于DataEase官方业务功能的数据源
     * 命名规范：配置文件名称基础上添加dataSource前缀并改为小驼峰格式
     * 可以扩展为 db2、db3...dbn 等多个数据源
     */
    public static final String DATA_SOURCE_OFFICIAL = "official-ds";

    /**
     * 租户数据源命名模式
     * 用于动态生成租户专属数据源的名称模板
     * 格式：tenant_{租户ID}_{数据库标识}
     * 示例：tenant_123_main, tenant_456_report
     */
    public static final String DS_NAME_PREFIX = "tenant_%s_%s";
}
