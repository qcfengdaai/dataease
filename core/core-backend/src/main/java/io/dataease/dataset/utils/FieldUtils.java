package io.dataease.dataset.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字段工具类
 * 提供数据库字段类型与DataEase内部类型之间的转换功能
 * 支持多种数据库的字段类型识别和标准化处理
 *
 * <p>DataEase字段类型定义：</p>
 * <ul>
 *   <li><b>0 - 文本类型：</b>VARCHAR、TEXT等字符串类型</li>
 *   <li><b>1 - 时间类型：</b>DATE、DATETIME、TIMESTAMP等时间类型</li>
 *   <li><b>2 - 整数类型：</b>INT、BIGINT等整数类型</li>
 *   <li><b>3 - 浮点类型：</b>FLOAT、DOUBLE、DECIMAL等浮点类型</li>
 *   <li><b>4 - 布尔类型：</b>BOOLEAN、BIT等布尔类型</li>
 * </ul>
 *
 * @author Junjun
 */
public class FieldUtils {

    /**
     * 将数据库字段类型转换为DataEase内部类型
     * 根据数据库字段类型名称，识别并返回对应的DataEase类型码
     * 支持主流数据库的字段类型识别
     *
     * @param type 数据库字段类型名称
     * @return DataEase字段类型码 (0-文本, 1-时间, 2-整数, 3-浮点, 4-布尔)
     */
    public static int transType2DeType(final String type) {
        List<String> text = Arrays.asList("CHAR", "VARCHAR", "TEXT", "TINYTEXT", "MEDIUMTEXT", "LONGTEXT", "ENUM", "ANY", "STRING", "BOOL", "BOOLEAN");
        List<String> time = Arrays.asList("DATE", "TIME", "YEAR", "DATETIME", "TIMESTAMP", "DATEV2", "DATETIMEV2", "DATETIME2", "DATETIMEOFFSET", "SMALLDATETIME", "DATETIME64", "_TIMESTAMPTZ", "TIMESTAMPTZ");
        List<String> num = Arrays.asList("INT", "SMALLINT", "MEDIUMINT", "INTEGER", "BIGINT", "LONG", "INT2", "INT4", "INT8", "int2", "int4", "int8", "INT16", "INT32", "INT64", "UINT8", "UINT16", "UINT32", "UINT64");
        List<String> doubleList = Arrays.asList("NUMBER", "FLOAT", "DOUBLE", "DECIMAL", "REAL", "MONEY", "NUMERIC", "float4", "float8", "FLOAT4", "FLOAT8", "DECFLOAT", "FLOAT32", "FLOAT64");
        List<String> boolType = Arrays.asList("BIT", "TINYINT");
        if (boolType.contains(type)) {
            return 4;// 布尔
        }
        if (doubleList.contains(type)) {
            return 3;// 浮点
        }
        if (num.contains(type)) {
            return 2;// 整型
        }
        if (time.contains(type)) {
            return 1;// 时间
        }
        if (text.contains(type)) {
            return 0;// 文本
        }

        if (boolType.stream().anyMatch(l -> type.contains(l))) {
            return 4;// 布尔
        }
        if (doubleList.stream().anyMatch(l -> type.contains(l))) {
            return 3;// 浮点
        }
        if (num.stream().anyMatch(l -> type.contains(l))) {
            return 2;// 整型
        }
        if (time.stream().anyMatch(l -> type.contains(l))) {
            return 1;// 时间
        }
        return 0;// 文本
    }

    /**
     * 将DataEase字段类型转换为维度/度量标识
     * 根据DataEase内部字段类型，返回对应的分析维度标识
     *
     * @param deType DataEase字段类型码
     * @return "d"表示维度字段(文本、时间等)，"q"表示度量字段(数值、布尔等)
     */
    public static String transDeType2DQ(int deType) {
        switch (deType) {
            case 0:
            case 1:
            case 5:
                return "d";
            case 2:
            case 3:
            case 4:
                return "q";
            default:
                return "d";
        }
    }
}
