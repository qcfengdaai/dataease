package io.dataease.extensions.view.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 图表指标DTO
 * 用于表示图表中的指标（度量）信息
 *
 * @Author gin
 */
@Data
public class ChartQuotaDTO {
    /** 指标ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
}
