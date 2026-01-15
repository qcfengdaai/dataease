package io.dataease.extensions.view.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 图表轴数据DTO
 * 用于表示图表坐标轴的基础数据结构
 *
 * @Author gin
 */
@Data
public class AxisChartDataDTO {
    /** 数值 */
    private BigDecimal value;

    /** 维度列表 */
    private List<ChartDimensionDTO> dimensionList;

    /** 指标列表 */
    private List<ChartQuotaDTO> quotaList;
}
