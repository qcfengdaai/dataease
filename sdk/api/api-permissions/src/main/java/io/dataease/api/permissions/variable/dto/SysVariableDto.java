package io.dataease.api.permissions.variable.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 系统变量数据传输对象
 *
 * <p>用于系统变量的定义和配置。系统变量是参数化配置的基础，
 * 可以定义不同类型的变量及其取值规则，支持在系统中灵活使用。</p>
 *
 * <p>变量类型支持：</p>
 * <ul>
 *   <li>文本类型（text）：自由文本输入</li>
 *   <li>数值类型（number）：支持最小值、最大值限制</li>
 *   <li>日期类型（date）：支持日期范围限制</li>
 *   <li>枚举类型（enum）：预定义的多个可选值</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>查询参数：在数据查询中作为动态参数</li>
 *   <li>报表参数：在报表生成时提供参数化能力</li>
 *   <li>用户配置：存储用户级别的配置信息</li>
 *   <li>系统配置：存储系统级别的全局参数</li>
 * </ul>
 */
@Data
public class SysVariableDto {

    /**
     * 变量 ID
     * 变量的唯一标识符
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 变量类型
     * 定义变量的数据类型，决定了变量的输入方式和验证规则
     * 可选值：text（文本）、number（数值）、date（日期）、enum（枚举）等
     */
    private String type;

    /**
     * 变量名称
     * 变量的显示名称，用于在界面上标识变量
     * 应具有业务含义，便于用户理解
     */
    private String name;

    /**
     * 最小值
     * 对于数值类型变量，限制可输入的最小值
     * 对于其他类型变量，此字段可能不适用
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long min;

    /**
     * 最大值
     * 对于数值类型变量，限制可输入的最大值
     * 对于其他类型变量，此字段可能不适用
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long max;

    /**
     * 开始时间
     * 对于日期类型变量，限制可选择的起始日期
     * 格式通常为：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
     */
    private String startTime;

    /**
     * 结束时间
     * 对于日期类型变量，限制可选择的截止日期
     * 格式通常为：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
     */
    private String endTime;

    /**
     * 根变量标识
     * 标识该变量是否为根级变量（系统核心变量）
     * true：根变量，通常是系统预置的重要变量
     * false：普通变量，用户自定义或业务相关变量
     * 默认为 false
     */
    private boolean root = false;

    /**
     * 禁用状态
     * 标识该变量是否被禁用
     * true：变量已禁用，不可使用
     * false：变量正常可用
     * 默认为 false
     */
    private boolean disabled = false;
}

