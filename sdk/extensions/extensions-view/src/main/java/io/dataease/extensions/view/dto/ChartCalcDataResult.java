package io.dataease.extensions.view.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 图表计算数据结果
 * 用于存储图表数据处理和计算后的结果信息
 */
@Data
public class ChartCalcDataResult {
    /** 数据映射表，存储计算后的图表数据 */
    private Map<String, Object> data;

    /** 原始数据列表，以字符串数组形式存储 */
    private List<String[]> originData;

    /** 辅助线数据列表 */
    private List<String[]> assistData;

    /** 动态辅助字段列表 */
    private List<ChartSeniorAssistDTO> dynamicAssistFields;

    /** 辅助线原始数据列表 */
    private List<String[]> assistDataOriginList;

    /** 动态辅助字段原始列表 */
    private List<ChartSeniorAssistDTO> dynamicAssistFieldsOriginList;

    /** 上下文信息，包含计算过程中的相关数据 */
    private Map<String, Object> context;

    /** 查询SQL语句 */
    // TODO 数据源插件化之后换成整个请求对象
    private String querySql;
}
