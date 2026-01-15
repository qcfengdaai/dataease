package io.dataease.extensions.view.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 图表维度DTO
 * 用于表示图表的维度信息
 *
 * @Author gin
 */
@Data
public class ChartDimensionDTO {
    /**
     * 维度ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 维度值
     */
    private String value;
}
