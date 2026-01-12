package io.dataease.extensions.view.dto;

import lombok.Data;

import java.util.List;

/**
 * 表格计算总计DTO
 * 用于定义表格的总计计算配置
 */
@Data
public class TableCalcTotal {
    /** 总计配置列表 */
    private List<TableCalcTotalCfg> cfg;
}
