package io.dataease.extensions.view.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 自定义过滤结果DTO
 * 用于存储自定义过滤器的处理结果
 */
@Data
@AllArgsConstructor
public class CustomFilterResult {
    /** 过滤器列表 */
    private List<ChartExtFilterDTO> filterList;

    /** 上下文信息 */
    private Map<String, Object> context;
}
