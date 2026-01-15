package io.dataease.extensions.view.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * SQL变量详情DTO
 * 用于定义SQL查询中的变量参数
 */
@Data
public class SqlVariableDetails {
    /** 变量名称 */
    private String variableName;

    /** 变量别名 */
    private String alias;

    /** 变量类型列表 */
    private List<String> type;

    /** 数据类型 */
    private int deType;

    /** 变量详情说明 */
    private String details;

    /** 默认值 */
    private String defaultValue;

    /** 默认值作用域 */
    private DefaultValueScope defaultValueScope;

    /** 变量ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    /** 数据集表ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long datasetTableId;

    /** 数据集组ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long datasetGroupId;

    /** 是否必填 */
    private boolean required;

    /** 操作符 */
    private String operator;

    /** 变量值列表 */
    private List<String> value;

    /** 数据集全名 */
    private String datasetFullName;

    /**
     * 默认值作用域枚举
     */
    public enum DefaultValueScope {
        /** 编辑范围 */
        EDIT("EDIT"),
        /** 全部范围 */
        ALLSCOPE("ALLSCOPE");

        private String type;

        DefaultValueScope(String type){
            this.type = type;
        }

        public String getType(){
            return type;
        }
    }
}
