package io.dataease.dataset.utils;

/**
 * 数据集表类型常量工具类
 * 定义数据集表的各种类型常量，用于业务逻辑中的类型判断和处理
 *
 * <p>支持的数据集表类型：</p>
 * <ul>
 *   <li><b>DB类型：</b>直接连接数据库表的数据集</li>
 *   <li><b>SQL类型：</b>基于SQL查询的数据集</li>
 * </ul>
 *
 * @author Junjun
 */
public class DatasetTableTypeConstants {

    /**
     * 数据库表类型
     * 表示数据集直接映射到数据库物理表
     */
    public static String DATASET_TABLE_DB = "db";

    /**
     * SQL查询类型
     * 表示数据集基于SQL查询结果
     */
    public static String DATASET_TABLE_SQL = "sql";
}
