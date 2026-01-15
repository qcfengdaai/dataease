package io.dataease.extensions.view.dto;

import lombok.Data;

import java.util.List;

/**
 * 列权限项DTO
 * 用于定义列级别的权限控制和脱敏规则
 */
@Data
public class ColumnPermissionItem {
    /** 字段ID */
    private Long id;

    /** 字段名称 */
    private String name;

    /** 数据类型 */
    private Integer deType;

    /** 是否选中 */
    private Boolean selected = false;

    /** 操作类型 */
    private String opt;

    /** 脱敏规则 */
    private DesensitizationRule desensitizationRule;


    /**
     * 脱敏规则内部类
     */
    @Data
    public class DesensitizationRule {
        /** 内置脱敏规则 */
        private BuiltInRule builtInRule;

        /** 自定义内置脱敏规则 */
        private CustomBuiltInRule customBuiltInRule;

        /** 参数M：用于自定义脱敏规则 */
        private Integer m;

        /** 参数N：用于自定义脱敏规则 */
        private Integer n;

        /** 特殊字符 */
        private String specialCharacter;

        /** 特殊字符列表 */
        private List<String> specialCharacterList;
    }

    /**
     * 内置脱敏规则枚举
     */
    public enum BuiltInRule {
        /** 完全脱敏 */
        CompleteDesensitization,
        /** 保留首尾各三个字符 */
        KeepFirstAndLastThreeCharacters,
        /** 保留中间三个字符 */
        KeepMiddleThreeCharacters,
        /** 自定义规则 */
        custom
    }

    /** 完全脱敏显示文本 */
    static public String CompleteDesensitization = "******";

    /** 保留首尾各三个字符的显示文本 */
    static public String KeepFirstAndLastThreeCharacters = "XXX***XXX";

    /** 保留中间三个字符的显示文本 */
    static public String KeepMiddleThreeCharacters = "***XXX***";

    /**
     * 自定义内置脱敏规则枚举
     */
    public enum CustomBuiltInRule {
        /** 保留前M个和后N个字符 */
        RetainBeforeMAndAfterN,
        /** 保留第M到第N个字符 */
        RetainMToN
    }
}
