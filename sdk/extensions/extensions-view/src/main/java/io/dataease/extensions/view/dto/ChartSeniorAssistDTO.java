package io.dataease.extensions.view.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 图表高级辅助线DTO
 * 用于定义图表辅助线的详细配置信息
 *
 * @Author Junjun
 */
@Data
public class ChartSeniorAssistDTO {
    /** 辅助线名称 */
    private String name;

    /** 字段名称 */
    private String field;

    /** 字段ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fieldId;

    /** 汇总方式 */
    private String summary;

    /** 轴类型 */
    private String axis;

    /** Y轴类型 */
    @JsonProperty("yAxisType")
    private String yAxisType;

    /** 辅助线值 */
    private String value;

    /** 线条类型（实线、虚线等） */
    private String lineType;

    /** 辅助线颜色 */
    private String color;

    /** 当前字段信息 */
    private ChartViewFieldDTO curField;

    /** 字体大小 */
    private String fontSize;
}
