package io.dataease.extensions.view.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * AntV图表轴数据DTO
 * 用于表示AntV图表组件的坐标轴数据结构
 *
 * @Author gin
 */
@Data
public class AxisChartDataAntVDTO {
    /** 数值 */
    private BigDecimal value;

    /** 维度列表 */
    private List<ChartDimensionDTO> dimensionList;

    /** 指标列表 */
    private List<ChartQuotaDTO> quotaList;

    /** 字段名称 */
    private String field;

    /** 显示名称 */
    private String name;

    /** 分类 */
    private String category;

    /** 气泡大小（用于气泡图等图表） */
    private BigDecimal popSize;

    /** 分组 */
    private String group;

    /** 动态标签值列表 */
    private List<DynamicValueDTO> dynamicLabelValue;

    /** 动态提示值列表 */
    private List<DynamicValueDTO> dynamicTooltipValue;
}
