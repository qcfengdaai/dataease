package io.dataease.extensions.datasource.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * DataEase表字段数据传输对象
 * <p>
 * 用于传输数据表字段的元数据信息，包括字段名称、类型、精度等。
 * 主要用于数据集创建、字段映射和数据类型转换等场景。
 * </p>
 *
 * @author fit2cloud
 * @since 1.0
 */
@Data
public class TableField implements Serializable {
    /**
     * 字段名称
     * <p>
     * 字段的显示名称，通常是对原始字段名进行处理后的结果。
     * 用于DataEase系统内部的字段标识和显示。
     * </p>
     */
    private String name;
    /**
     * 字段原始名称
     * <p>
     * 数据库表中的原始字段名称，保持数据库的原始字段名不变。
     * 用于生成SQL语句和与数据库进行交互时的字段引用。
     * </p>
     */
    private String originName;
    /**
     * 字段数据类型
     * <p>
     * 字段在数据库中的SQL数据类型，对应java.sql.Types中的类型。
     * 用于数据类型的识别和转换处理。
     * </p>
     */
    private String type;
    /**
     * 数据精度
     * <p>
     * 数值类型字段的精度，即数字的总位数。
     * 对于数值和小数类型字段的精度控制。
     * </p>
     */
    private int precision;
    /**
     * 字段大小
     * <p>
     * 字段的最大存储大小，对于字符串类型表示最大长度。
     * 用于数据验证和存储优化。
     * </p>
     */
    private long size;
    /**
     * 小数位数
     * <p>
     * 数值类型字段的小数位数，即小数点后的数字位数。
     * 与精度配合使用，定义数值的准确范围。
     * </p>
     */
    private int scale;
    /**
     * 字段长度
     * <p>
     * 字段的长度信息，通常为字符串类型的字符数限制。
     * 用于前端数据验证和显示的参考。
     * </p>
     */
    private String length;
    /**
     * 选中状态
     * <p>
     * 标识字段是否被选中或启用，默认为false。
     * 用于数据集创建时的字段选择和过滤。
     * </p>
     */
    private boolean checked = false;
    /**
     * 主键标识
     * <p>
     * 标识字段是否为数据表的主键，默认为false。
     * 用于数据表结构分析和连接关系处理。
     * </p>
     */
    private boolean primaryKey = false;
    /**
     * 字段类型分类
     * <p>
     * DataEase定义的字段类型分类，如维度、指标等。
     * 用于数据分析和图表展示中的字段角色划分。
     * </p>
     */
    private String fieldType;
    /**
     * DataEase字段类型
     * <p>
     * DataEase系统内部定义的字段数据类型数值编码。
     * 用于系统内部的数据类型识别和处理。
     * </p>
     */
    private Integer deType;
    /**
     * DataEase抽取类型
     * <p>
     * DataEase中字段数据的抽取类型定义。
     * 用于数据抽取和转换过程中的类型处理。
     * </p>
     */
    private Integer deExtractType;
    /**
     * 扩展字段标识
     * <p>
     * 标识字段是否为扩展字段或计算字段。
     * 用于区分原始字段和计算得出的扩展字段。
     * </p>
     */
    private int extField;
    /**
     * JSON路径
     * <p>
     * 对于JSON数据类型，指定字段在JSON结构中的路径。
     * 用于JSON数据的解析和字段提取。
     * </p>
     */
    private String jsonPath;
    /**
     * 主要字段标识
     * <p>
     * 标识字段是否为主要字段或重要字段。
     * 用于数据分析中的字段重要性排序和选择。
     * </p>
     */
    private boolean primary;
    /**
     * 自增字段标识
     * <p>
     * 标识字段是否为数据库的自增字段。
     * 用于数据表结构分析和数据插入策略制定。
     * </p>
     */
    private boolean autoIncrement;
    /**
     * 字段数值列表
     * <p>
     * 存储字段的数值列表，用于批量数据处理和数值缓存。
     * 在数据查询和结果集处理中使用。
     * </p>
     */
    List<Object> value;

    /**
     * 输入记录数
     * <p>
     * 字段相关的输入数据记录数量统计。
     * 用于数据质量分析和性能优化参考。
     * </p>
     */
    private int inCount;
    /**
     * 查询条件类型
     * <p>
     * 字段查询的条件类型，默认为“eq”（等于）。
     * 支持多种查询条件如大于、小于、包含等。
     * </p>
     */
    private String term = "eq";
    /**
     * 类型数值编码
     * <p>
     * 字段数据类型的数值编码标识。
     * 用于系统内部的类型匹配和处理逻辑。
     * </p>
     */
    private Integer typeNumber;

}
