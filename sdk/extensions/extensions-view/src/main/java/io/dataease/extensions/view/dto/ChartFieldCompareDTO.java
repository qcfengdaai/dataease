package io.dataease.extensions.view.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 图表字段对比DTO
 * 用于定义图表字段的对比计算配置
 */
@Data
public class ChartFieldCompareDTO {
    /** 对比类型，默认为"none"（不对比） */
    private String type = "none";

    /** 结果数据类型，默认为"percent"（百分比） */
    private String resultData = "percent";

    /** 字段ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long field;

    /** 自定义对比配置 */
    private ChartFieldCompareCustomDTO custom;
}
