package io.dataease.extensions.view.dto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 图表高级功能配置DTO
 * 用于配置图表的高级功能，如空数据处理等
 */
@Data
public class ChartSeniorFunctionCfgDTO {
    /** 空数据策略（如：忽略、设为0、设为空字符串、自定义值等） */
    private String emptyDataStrategy;

    /** 空数据自定义值 */
    private String emptyDataCustomValue;

    /** 空数据字段控制列表 */
    private List<String> emptyDataFieldCtrl = new ArrayList<>();
}
