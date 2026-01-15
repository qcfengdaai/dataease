package io.dataease.extensions.view.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.dataease.extensions.datasource.dto.DatasetTableFieldDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 数据集行权限树节点项DTO
 * 用于定义数据集行级权限的树形结构节点
 */
@Data
public class DatasetRowPermissionsTreeItem implements Serializable {
    /** 节点类型：'item'表示叶子节点，'tree'表示树节点 */

    private String type;

    /** 字段ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fieldId;

    /** 字段对象 */
    private DatasetTableFieldDTO field;

    /** 过滤器类型：'logic'表示逻辑过滤，'enum'表示枚举过滤 */
    private String filterType;

    /** 过滤条件操作符：'eq'(等于), 'not_eq'(不等于), 'lt'(小于), 'le'(小于等于),
     * 'gt'(大于), 'ge'(大于等于), 'in'(包含), 'not in'(不包含), 'like'(模糊匹配),
     * 'not like'(不匹配), 'null'(为空), 'not_null'(不为空), 'empty'(空字符串),
     * 'not_empty'(非空字符串), 'between'(区间) */
    private String term;

    /** 普通过滤值（如：'a'） */
    private String value;

    /** 时间过滤值（如：'a'） */
    private String timeValue;

    /** 枚举过滤值列表（如：['a','b']） */
    private List<String> enumValue;

    /** 时间类型：时间细粒度 */
    private String timeType;

    /** 子树对象（当type为'tree'时使用） */
    private DatasetRowPermissionsTreeObj subTree;

    private static final long serialVersionUID = 1L;
}
