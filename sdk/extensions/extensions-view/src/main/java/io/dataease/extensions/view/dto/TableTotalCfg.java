package io.dataease.extensions.view.dto;

import lombok.Data;

/**
 * 表格总计配置DTO
 * 用于配置表格的总计和子总计显示
 */
@Data
public class TableTotalCfg {
    /** 是否显示总计 */
    private boolean showGrandTotals;

    /** 是否显示小计 */
    private boolean showSubTotals;

    /** 总计计算配置 */
    private TableCalcTotal calcTotals;

    /** 小计计算配置 */
    private TableCalcTotal calcSubTotals;
}
