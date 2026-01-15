package io.dataease.extensions.view.filter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.dataease.extensions.datasource.dto.DatasetTableFieldDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 过滤器树项
 * 表示过滤条件树中的单个过滤条件项或子树
 *
 * @Author Junjun
 */
@Data
public class FilterTreeItem implements Serializable {
    /**
     * 过滤器类型，'item'表示具体过滤项，'tree'表示子树
     */
    private String type;

    // === 过滤项相关属性 ===
    /**
     * 字段ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fieldId;

    /**
     * 字段对象信息
     */
    private DatasetTableFieldDTO field;

    /**
     * 过滤器类型，'logic'逻辑过滤或'enum'枚举过滤
     */
    private String filterType;

    /**
     * 过滤条件操作符
     * 支持：'eq'(等于), 'not_eq'(不等于), 'lt'(小于), 'le'(小于等于),
     * 'gt'(大于), 'ge'(大于等于), 'in'(包含), 'not in'(不包含),
     * 'like'(模糊匹配), 'not like'(非模糊匹配), 'null'(为空), 'not_null'(非空),
     * 'empty'(空字符串), 'not_empty'(非空字符串), 'between'(区间)
     */
    private String term;

    /**
     * 过滤值，单个值情况下使用
     */
    private String value;

    /**
     * 枚举值列表，多个值情况下使用
     */
    private List<String> enumValue;

    /**
     * 时间过滤类型，'dateValue'固定日期值或'dynamicDate'动态日期
     */
    private String filterTypeTime;

    /**
     * 动态时间设置配置
     */
    private DynamicTimeSetting dynamicTimeSetting;

    /**
     * 时间字段的粒度类型
     */
    private String timeType;

    // === 子树相关属性 ===
    /**
     * 子过滤树对象，当type为'tree'时使用
     */
    private FilterTreeObj subTree;

    /**
     * 值类型标识
     */
    private String valueType;
}
