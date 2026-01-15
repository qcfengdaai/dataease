package io.dataease.dataset.constant;

/**
 * 数据集表类型常量类
 * 定义数据集中表的不同类型，用于区分数据来源和处理方式
 *
 * <p>数据集表类型说明：</p>
 * <ul>
 *   <li><b>DB类型：</b>直接来源于数据库表的数据集</li>
 *   <li><b>SQL类型：</b>基于SQL查询结果的数据集</li>
 *   <li><b>ES类型：</b>来源于Elasticsearch索引的数据集</li>
 * </ul>
 *
 * @author Junjun
 */
public class DatasetTableType {

    /**
     * 数据库表类型
     * 表示数据集直接映射到数据库中的物理表
     */
    public static String DB = "db";

    /**
     * SQL查询类型
     * 表示数据集基于SQL查询语句动态生成
     */
    public static String SQL = "sql";

    /**
     * Elasticsearch类型
     * 表示数据集来源于Elasticsearch搜索引擎的索引
     */
    public static String Es = "es";
}
