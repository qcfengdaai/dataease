package io.dataease.extensions.view.dto;

import lombok.Data;

import java.util.List;

/**
 * 图表字段自定义对比DTO
 * 用于定义图表字段的自定义对比计算规则
 */
@Data
public class ChartFieldCompareCustomDTO {
    /** 字段名称 */
    private String field;

    /** 计算类型，默认为"0" */
    private String calcType = "0";

    /** 时间类型，默认为"0" */
    private String timeType = "0";

    /** 当前时间 */
    private String currentTime;

    /** 对比时间 */
    private String compareTime;

    /** 当前时间范围 */
    private List<String> currentTimeRange;

    /** 对比时间范围 */
    private List<String> compareTimeRange;

}
