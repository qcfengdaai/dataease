package io.dataease.engine.constant;

/**
 * 扩展字段类型常量类
 * 定义数据集中扩展字段的不同类型，用于字段处理和SQL生成
 *
 * <p>扩展字段是在原始数据基础上衍生的字段，包括：</p>
 * <ul>
 *   <li>普通字段 - 直接使用原始数据</li>
 *   <li>复制字段 - 复制现有字段</li>
 *   <li>计算字段 - 通过表达式计算得出</li>
 *   <li>分组字段 - 用于数据分组</li>
 * </ul>
 *
 * @author Junjun
 */
public class ExtFieldConstant {

    /**
     * 普通字段类型
     * 表示直接使用数据源中的原始字段，无需额外处理
     */
    public final static Integer EXT_NORMAL = 0;

    /**
     * 复制字段类型
     * 表示从现有字段复制而来的字段，通常用于字段重命名或格式转换
     */
    public final static Integer EXT_COPY = 1;

    /**
     * 计算字段类型
     * 表示通过表达式或函数计算得出的字段，支持复杂的数据处理逻辑
     */
    public final static Integer EXT_CALC = 2;

    /**
     * 分组字段类型
     * 表示用于数据分组聚合的字段，通常在GROUP BY子句中使用
     */
    public final static Integer EXT_GROUP = 3;
}
