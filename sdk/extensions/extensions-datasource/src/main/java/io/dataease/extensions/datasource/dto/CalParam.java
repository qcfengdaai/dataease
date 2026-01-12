package io.dataease.extensions.datasource.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * DataEase计算参数数据传输对象
 * <p>
 * 用于传输计算字段或数据处理过程中所需的参数信息。
 * 通常在数据源查询、字段计算和数据转换等场景中使用。
 * </p>
 *
 * @author Junjun
 * @author fit2cloud
 * @since 1.0
 */
@Data
public class CalParam implements Serializable {
    /**
     * 参数标识符
     * <p>
     * 参数的唯一标识，用于区分和引用不同的计算参数。
     * 在参数传递和处理过程中作为参数的标识键使用。
     * </p>
     */
    private String id;
    /**
     * 参数名称
     * <p>
     * 参数的显示名称或变量名称，用于参数的标识和引用。
     * 通常在计算表达式和查询构建中作为参数占位符使用。
     * </p>
     */
    private String name;
    /**
     * 参数值
     * <p>
     * 参数的具体数值或表达式内容。
     * 可以是常量值、变量引用或复杂的计算表达式。
     * </p>
     */
    private String value;
}
