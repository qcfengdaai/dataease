package io.dataease.extensions.view.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 图表字段过滤器DTO
 * 用于定义图表字段的过滤条件
 */
@Data
public class ChartViewFieldFilterDTO implements Serializable {
    /**
     * 过滤条件操作符（如：eq, ne, gt, lt等）
     */
    private String term;
    /**
     * 过滤值
     */
    private String value;
}
