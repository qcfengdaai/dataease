package io.dataease.extensions.view.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 排序轴配置
 * 用于定义图表字段的排序优先级
 */
@Data
public class SortAxis {
    /**
     * 字段ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 字段名称
     */
    private String name;
}
