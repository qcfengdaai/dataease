package io.dataease.extensions.view.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 动态值DTO
 * 用于表示图表中的动态数值字段
 *
 * @Author Junjun
 */
@Data
public class DynamicValueDTO {
    /** 字段ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fieldId;

    /** 动态值 */
    private BigDecimal value;
}
