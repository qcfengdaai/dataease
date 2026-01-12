package io.dataease.extensions.view.dto;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dataease.extensions.datasource.dto.DatasetTableFieldDTO;
import io.dataease.extensions.view.filter.FilterTreeObj;
import lombok.Data;


import java.util.ArrayList;
import java.util.List;

/**
 * 图表扩展过滤DTO
 * 用于定义图表的外部过滤条件，包括过滤组件、联动、下钻等场景
 */
@Data
public class ChartExtFilterDTO {
    /** 组件ID */
    private Long componentId;

    /** 字段ID */
    private String fieldId;

    /** 操作符 */
    private String operator;

    /** 过滤值列表 */
    private List<String> value;

    /** 视图ID列表 */
    private List<Long> viewIds;

    /** SQL参数详情列表 */
    private List<SqlVariableDetails> parameters;

    /** 数据集表字段信息 */
    private DatasetTableFieldDTO datasetTableField;

    /** 是否为树形结构 */
    private Boolean isTree = false;

    /** 数据集表字段列表 */
    private List<DatasetTableFieldDTO> datasetTableFieldList;

    /** 日期样式 */
    private String dateStyle;

    /** 日期格式模式 */
    private String datePattern;

    /** 原始值列表（不序列化到JSON） */
    @JsonIgnore
    private List<String> originValue;

    /** 过滤类型：0-过滤组件，1-下钻，2-联动外部参数，3-联动自定义参数 */
    private int filterType;

    /** 自定义过滤树对象 */
    private FilterTreeObj customFilter;
}
