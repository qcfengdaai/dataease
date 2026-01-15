package io.dataease.extensions.datasource.vo;

import lombok.Data;

import java.util.List;

/**
 * DataEase数据源配置扩展视图对象
 * <p>
 * 继承基础配置类，扩展数据源的特定配置信息。
 * 包含非法参数检查、表查询SQL等高级功能配置。
 * </p>
 *
 * @author fit2cloud
 * @since 1.0
 */
@Data
public class DatasourceConfiguration extends Configuration {
    /**
     * 非法参数列表
     * <p>
     * 定义不允许在数据源配置中使用的参数名称列表。
     * 用于安全性检查，防止非法参数的传入和使用。
     * </p>
     */
    private List<String> illegalParameters;
    /**
     * 表查询SQL列表
     * <p>
     * 用于查询数据库表信息的SQL语句列表。
     * 支持不同数据库类型的特定表查询需求和优化。
     * </p>
     */
    private List<String> showTableSqls;


    /**
     * DataEase支持的数据源类型枚举
     * <p>
     * 定义了DataEase系统支持的所有数据源类型，包括关系型数据库、
     * 大数据平台、NoSQL数据库和文件类型等。每种类型都包含相应的
     * 配置信息和语法特性。
     * </p>
     *
     * <p><b>数据源分类：</b></p>
     * <ul>
     *   <li><b>OLTP</b> - 在线事务处理数据库（MySQL、PostgreSQL等）</li>
     *   <li><b>OLAP</b> - 在线分析处理数据库（ClickHouse、Doris等）</li>
     *   <li><b>DL</b> - 数据湖和非结构化数据（SQL Server、MongoDB）</li>
     *   <li><b>LOCALFILE</b> - 本地文件类型（Excel等）</li>
     * </ul>
     *
     * @since 1.0
     */
    static public enum DatasourceType {
        /** 文件夹类型 - 用于组织和管理数据源的虚拟文件夹 */
        folder("folder", "folder", "folder", null, null, 25),
        /** API数据源 - 通过RESTful API获取数据的数据源类型 */
        API("API", "API", "API", "`", "`", 15),
        /** 本地Excel文件 - 本地上传的Excel文件数据源 */
        Excel("Excel", "Excel", "LOCALFILE", "`", "`", 16),
        /** 远程Excel文件 - 通过URL访问的远程Excel文件数据源 */
        ExcelRemote("ExcelRemote", "ExcelRemote", "LOCALFILE", "`", "`", 29),
        /** MySQL数据库 - 开源的关系型数据库管理系统 */
        mysql("mysql", "Mysql", "OLTP", "`", "`", 27),
        /** Apache Impala - Hadoop生态系统的实时SQL查询引擎 */
        impala("impala", "Apache Impala", "OLAP", "`", "`", 5),
        /** MariaDB数据库 - MySQL的分支，兼容MySQL的开源数据库 */
        mariadb("mariadb", "Mariadb", "OLTP", "`", "`", 6),
        /** StarRocks - 新一代极速全场景MPP数据库 */
        StarRocks("StarRocks", "StarRocks", "OLAP", "`", "`", 7),
        /** Elasticsearch - 分布式搜索和分析引擎 */
        es("es", "Elasticsearch", "OLAP", "\"", "\"", 14),
        /** Apache Doris - 高性能实时分析数据库 */
        doris("doris", "Apache Doris", "OLAP", "`", "`", 26),
        /** TiDB - 开源分布式关系型数据库 */
        TiDB("TiDB", "TiDB", "OLTP", "`", "`", 3),
        /** Oracle数据库 - 甲骨文公司的关系型数据库管理系统 */
        oracle("oracle", "ORACLE", "OLTP", "\"", "\"", 1),
        /** PostgreSQL - 开源的对象-关系型数据库管理系统 */
        pg("pg", "PostgreSQL", "OLTP", "\"", "\"", 9),
        /** AWS Redshift - 亚马逊的云数据仓库服务 */
        redshift("redshift", "AWS Redshift", "OLTP", "\"", "\"", 13),
        /** IBM Db2 - IBM开发的关系型数据库管理系统 */
        db2("db2", "Db2", "OLTP", "", "", 12),
        /** ClickHouse - 面向列的分析型数据库管理系统 */
        ck("ck", "Clickhouse", "OLAP", "`", "`", 11),
        /** H2数据库 - 嵌入式的关系型数据库，主要用于开发和测试 */
        h2("h2", "H2", "OLAP", "\"", "\"", 30),
        /** SQL Server - 微软公司开发的关系型数据库管理系统 */
        sqlServer("sqlServer", "Sqlserver", "DL", "[", "]", 2),
        /** MongoDB - 基于文档的NoSQL数据库 */
        mongo("mongo", "MongoDB", "DL", "`", "`", 10);

        /** 数据源类型标识 */
        private String type;
        /** 数据源显示名称 */
        private String name;
        /** 数据源类型标志位 */
        private Integer flag;
        /** 数据源分类（OLTP/OLAP/DL等） */
        private String catalog;
        /** SQL标识符前缀字符 */
        private String prefix;
        /** SQL标识符后缀字符 */
        private String suffix;

        DatasourceType(String type, String name, String catalog, String prefix, String suffix, Integer flag) {
            this.type = type;
            this.name = name;
            this.catalog = catalog;
            this.prefix = prefix;
            this.suffix = suffix;
            this.flag = flag;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getCatalog() {
            return catalog;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public Integer getFlag() {
            return flag;
        }
    }
}
