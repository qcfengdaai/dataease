package io.dataease.extensions.datafilling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 数据填报扩展表单设置DTO
 * 用于配置数据填报表单的全局设置，包括表单状态和数字输入校验规则
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExtFormSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = -6236922011567180831L;

    /**
     * 表单唯一标识
     * 用于标识数据填报表单的ID
     */
    private String id;

    /**
     * 表单禁用状态
     * true表示表单被禁用，用户无法提交数据
     */
    private boolean disable;

    /**
     * 数字输入规则列表
     * 定义数字类型字段的输入限制规则，如大于、小于、等于等条件
     */
    private List<NumberRule> numberInputRules;

    /**
     * 数字输入规则
     * 定义单个数字字段的输入校验规则，包括字段名和校验条件
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NumberRule implements Serializable {
        @Serial
        private static final long serialVersionUID = -8841727448573594811L;

        /**
         * 字段列名
         * 需要应用输入规则的字段名称
         */
        private String  column;

        /**
         * 校验条件表达式
         * 定义数字字段的校验条件，如">0"、"<=100"、">=10 && <=100"等
         */
        private String  term;

    }
}
