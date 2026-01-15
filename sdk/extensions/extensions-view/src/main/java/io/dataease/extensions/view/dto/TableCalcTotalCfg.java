package io.dataease.extensions.view.dto;

import lombok.Data;

/**
 * 表格计算总计配置DTO
 * 用于定义表格总计的具体计算规则
 */
@Data
public class TableCalcTotalCfg {
    /** DataEase字段名称 */
    private String dataeaseName;

    /** 聚合方式 */
    private String aggregation;

    /** 原始字段名称 */
    private String originName;

    /** 扩展字段类型 */
    private int extField;

    /** 图表ID */
    private long chartId;

    /** 数据集组ID */
    private long datasetGroupId;
}
