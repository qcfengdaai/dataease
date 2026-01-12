package io.dataease.extensions.view.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 轴格式化结果
 * 用于存储图表轴格式化后的数据和上下文信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AxisFormatResult {
    /** 轴映射表，键为轴类型，值为该轴上的字段列表 */
    private Map<ChartAxis, List<ChartViewFieldDTO>> axisMap;

    /** 上下文信息，包含格式化过程中的相关数据 */
    private Map<String, Object> context;
}
