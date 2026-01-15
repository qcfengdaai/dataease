package io.dataease.extensions.view.dto;

import lombok.Data;

/**
 * 阈值动态字段DTO
 * 用于定义阈值条件中的动态字段配置
 *
 * @author jianneng
 * @date 2024/9/19 18:31
 **/
@Data
public class ThresholdDynamicFieldDTO {
    /**
     * 字段ID
     */
    private String fieldId;

    /**
     * 字段信息
     */
    private ChartViewFieldDTO field;

    /**
     * 汇总方式
     */
    private String summary;
}
