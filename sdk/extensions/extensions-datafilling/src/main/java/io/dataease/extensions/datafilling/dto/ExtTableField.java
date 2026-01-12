package io.dataease.extensions.datafilling.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 数据填报扩展表字段DTO
 * 用于定义数据填报表单中的字段配置信息，包括字段类型、显示设置、数据映射等
 * 是数据填报表单设计器中字段组件的核心数据模型
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExtTableField implements Serializable {
    @Serial
    private static final long serialVersionUID = 9021129395822053871L;

    /**
     * 字段类型
     * 表示字段的表单组件类型，如input（输入框）、select（下拉选择）、date（日期选择器）等
     */
    private String type;

    /**
     * 字段类型显示名称
     * 用于在界面上显示的字段类型名称，如"文本输入"、"单选下拉"等
     */
    private String typeName;

    /**
     * 字段图标
     * 用于在表单设计器中显示的字段图标标识
     */
    private String icon;

    /**
     * 字段唯一标识
     * 用于在表单中唯一标识该字段，通常为UUID
     */
    private String id;

    /**
     * 字段详细设置
     * 包含字段的所有配置信息，如名称、映射、校验规则、选项等
     */
    private ExtTableFieldSetting settings;

    /**
     * 字段是否已删除标记
     * true表示该字段已被标记为删除，在表结构更新时会被移除
     */
    private boolean removed;

    /**
     * 扩展表字段设置
     * 包含字段的详细配置信息，如字段名称、校验规则、数据映射关系、选项配置等
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExtTableFieldSetting implements Serializable  {

        @Serial
        private static final long serialVersionUID = 8776508642526681125L;

        /**
         * 字段名称
         * 在表单中显示的字段标签名称
         */
        private String name;

        /**
         * 是否必填
         * true表示该字段为必填项，用户必须填写后才能提交
         */
        private boolean required;

        /**
         * 字段映射配置
         * 定义字段与数据库表列的映射关系，包括列名、数据类型、长度精度等
         */
        private ExtTableFieldMapping mapping;

        /**
         * 范围分隔符
         * 用于日期范围等范围类型字段的起止值分隔符，如"-"或"至"
         */
        private String rangeSeparator;

        /**
         * 是否唯一
         * true表示该字段值在数据表中必须唯一，用于唯一性校验
         */
        private boolean unique;

        /**
         * 输入框类型
         * 指定输入框的HTML类型，如text、number、password等
         */
        private String inputType;

        /**
         * 日期类型
         * 指定日期字段的显示格式，如date（日期）、datetime（日期时间）、year（年份）等
         */
        private String dateType;

        /**
         * 占位符文本
         * 单个输入框的占位符提示文本
         */
        private String placeholder;

        /**
         * 起始值占位符
         * 范围类型字段起始输入框的占位符文本
         */
        private String startPlaceholder;

        /**
         * 结束值占位符
         * 范围类型字段结束输入框的占位符文本
         */
        private String endPlaceholder;

        /**
         * 选项数据源类型
         * 指定下拉选择等字段的选项来源：0-自定义选项，1-数据表查询
         */
        private Integer optionSourceType;

        /**
         * 选项数据源ID
         * 当选项来源为数据表时，指定数据源的ID
         */
        @JsonSerialize(using = ToStringSerializer.class)
        private Long optionDatasource;

        /**
         * 选项数据表名
         * 当选项来源为数据表时，指定查询的表名
         */
        private String optionTable;

        /**
         * 选项数据列名
         * 当选项来源为数据表时，指定作为选项的列名
         */
        private String optionColumn;

        /**
         * 选项排序方式
         * 当选项来源为数据表时，指定选项的排序规则，如ASC或DESC
         */
        private String optionOrder;

        /**
         * 是否支持多选
         * true表示该字段支持多选，适用于下拉选择、复选框等组件
         */
        private boolean multiple;

        /**
         * 是否开启更新规则检查
         * true表示在更新数据时需要进行额外的规则校验
         */
        private boolean updateRuleCheck;

        /**
         * 自定义选项列表
         * 当选项来源为自定义时，存储字段的所有可选项
         */
        private List<Option> options;

        /**
         * 额外列配置列表
         * 当选项来源为数据表时，指定需要额外读取并显示的其他列信息
         */
        private List<ExtraColumnItem> extraColumns;

        /**
         * 是否启用默认时间
         * true表示日期字段启用默认时间设置
         */
        private boolean enableDefaultTime;

        /**
         * 是否启用当前时间
         * true表示日期字段默认值使用当前时间
         */
        private boolean enableCurrentTime;

        /**
         * 默认时间戳
         * 当enableDefaultTime为true且enableCurrentTime为false时，使用该时间戳作为默认值
         */
        private Long defaultTime;
    }

    /**
     * 选项配置
     * 用于下拉选择、单选框、复选框等字段的选项定义
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option implements Serializable  {

        @Serial
        private static final long serialVersionUID = -1681618296840344071L;

        /**
         * 选项显示名称
         * 在界面上展示给用户的选项文本
         */
        private String name;

        /**
         * 选项值
         * 选项对应的实际值，提交时使用该值存储到数据库
         */
        private Object value;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExtTableFieldMapping implements Serializable  {

        @Serial
        private static final long serialVersionUID = 4233066732126872840L;

        private String columnName;

        //dateRange下对应两个字段
        private String columnName1;
        private String columnName2;

        private String oldColumnName;
        private String oldColumnName1;
        private String oldColumnName2;

        private BaseType type;

        //长度
        private Integer size;
        //精度
        private Integer accuracy;

        private boolean useExistsTable;

    }

    public enum BaseType {
        nvarchar, //字符串
        text, //长文本
        number, //整型数字
        decimal, //小数数字
        datetime //日期
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TableField implements Serializable  {

        @Serial
        private static final long serialVersionUID = 85092190247927362L;

        private String columnName;

        private String oldColumnName;

        private BaseType type;

        private boolean required;

        private boolean primaryKey;

        //长度
        private Integer size;
        //精度
        private Integer accuracy;

        private String comment;

    }

}
