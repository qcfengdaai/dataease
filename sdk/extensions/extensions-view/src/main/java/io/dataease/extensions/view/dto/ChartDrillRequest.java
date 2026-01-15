package io.dataease.extensions.view.dto;

import lombok.Data;

import java.util.List;

/**
 * 图表钻取请求DTO
 * 用于处理图表的钻取操作请求
 *
 * @Author gin
 */
@Data
public class ChartDrillRequest {
    /** 维度列表，用于指定钻取的维度层级 */
    private List<ChartDimensionDTO> dimensionList;
}
