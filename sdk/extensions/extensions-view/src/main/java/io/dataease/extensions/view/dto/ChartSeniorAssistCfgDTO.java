package io.dataease.extensions.view.dto;

import lombok.Data;

import java.util.List;


/**
 * 图表高级辅助线配置DTO
 * 用于配置图表的辅助线显示及相关属性
 */
@Data
public class ChartSeniorAssistCfgDTO {
    /** 是否启用辅助线 */
    private boolean enable;

    /** 辅助线列表 */
    private List<ChartSeniorAssistDTO> assistLine;
}
