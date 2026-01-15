package io.dataease.extensions.datafilling.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 额外列配置项DTO
 * 用于配置数据填报表单中选项字段从数据表查询时需要额外显示的列信息
 * 当表单字段的选项来源为数据表时，除了主显示列外，可以配置其他列一同显示
 */
@Data
@Accessors(chain = true)
public class ExtraColumnItem {
    /**
     * 字段名称
     * 数据表中额外列的实际字段名，用于数据查询
     */
    private String fieldName;

    /**
     * 显示名称
     * 额外列在界面上显示的名称，用于用户友好展示
     */
    private String displayName;
}
