package io.dataease.extensions.view.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 图表自定义过滤项DTO
 * 用于表示图表中的自定义过滤条件项
 *
 * @Author Junjun
 */
@Data
public class ChartCustomFilterItemDTO implements Serializable {
    /** 字段ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fieldId;

    /** 过滤条件操作符（如：eq, ne, gt, lt等） */
    private String term;

    /** 过滤值 */
    private String value;

    /** 日期过滤格式 */
    private String filterDateFormat;
}
