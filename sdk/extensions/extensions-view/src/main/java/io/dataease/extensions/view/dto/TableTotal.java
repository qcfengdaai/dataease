package io.dataease.extensions.view.dto;

import lombok.Data;

/**
 * 表格合计配置
 * 用于配置表格的行合计和列合计
 */
@Data
public class TableTotal {
    /**
     * 行合计配置
     */
    private TableTotalCfg row;

    /**
     * 列合计配置
     */
    private TableTotalCfg col;
}
