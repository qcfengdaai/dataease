package io.dataease.extensions.view.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 维度、指标、过滤器
 */
@Data
public class ChartViewFieldDTO extends ChartViewFieldBaseDTO implements Serializable {
    /**
     * 过滤
     */
    private List<ChartViewFieldFilterDTO> filter;

    /**
     * 排序
     */
    private List<String> customSort;

    /**
     * 业务类型
     */
    private String busiType;

    /**
     * 是否聚合
     */
    private boolean isAgg;

    /**
     * 是否隐藏
     */
    private boolean hide;

    /**
     * 分组类型
     */
    private String groupType;

    /**
     * 字段来源
     */
    @JsonIgnore
    private FieldSource source;

    /**
     * 显隐
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean show;

    /**
     * 字段名称
     */
    private String field;
}
