package io.dataease.extensions.view.dto;

import io.dataease.extensions.datasource.dto.DatasetTableFieldDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * 图表字段基础DTO
 * 图表字段base类，与数据集字段表基本一致
 */
@Data
public class ChartViewFieldBaseDTO extends DatasetTableFieldDTO implements Serializable {

    /** 汇总方式（如：sum, avg, count等） */
    private String summary;

    /** 排序方式 */
    private String sort;

    /**
     * 日期解析格式，后端参与聚合运算
     */
    private String dateStyle;

    /**
     * 日期分隔符
     */
    private String datePattern;

    /**
     * 日期显示格式，仅前端图表格式化
     */
    private String dateShowFormat;

    /** 扩展字段类型 */
    private Integer extField;

    /** 图表类型 */
    private String chartType;

    /** 对比计算配置 */
    private ChartFieldCompareDTO compareCalc;

    /** 逻辑运算符 */
    private String logic;

    /** 过滤器类型 */
    private String filterType;

    /** 图表ID */
    private Long chartId;

    /** 字段索引 */
    private Integer index;

    /** 格式化配置 */
    private FormatterCfgDTO formatterCfg;

    /** 图表显示名称 */
    private String chartShowName;

}
