package io.dataease.extensions.view.dto;

import lombok.Data;

import java.util.List;

/**
 * 表格阈值DTO
 * 用于定义表格字段的阈值告警配置
 *
 * @author jianneng
 * @date 2024/9/19 18:31
 **/
@Data
public class TableThresholdDTO {
    /**
     * 字段ID
     */
    private String fieldId;

    /**
     * 字段信息
     */
    private ChartViewFieldDTO field;

    /**
     * 阈值条件列表
     */
    private List<ChartSeniorThresholdDTO> conditions;
}
