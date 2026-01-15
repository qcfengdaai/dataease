package io.dataease.api.permissions.variable.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 系统变量值数据传输对象
 *
 * <p>用于表示系统变量的具体取值。一个系统变量可以有多个可选值，
 * 特别是对于枚举类型的变量。此对象定义了变量值的详细信息。</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>枚举变量：定义多个可选的枚举值</li>
 *   <li>日期范围：定义日期类型变量的起止日期</li>
 *   <li>用户选择：在用户选择变量值时展示可选项</li>
 *   <li>默认值设置：为变量设置默认值</li>
 * </ul>
 *
 * <p>变量值与变量的关系：</p>
 * <ul>
 *   <li>一对多：一个系统变量可以有多个变量值</li>
 *   <li>枚举变量：每个值代表一个可选项</li>
 *   <li>范围变量：begin 和 end 定义值的范围</li>
 * </ul>
 */
@Data
public class SysVariableValueDto {

    /**
     * 变量值 ID
     * 变量值的唯一标识符
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 系统变量 ID
     * 该值所属的系统变量的 ID
     * 建立变量值与系统变量的关联关系
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sysVariableId;

    /**
     * 变量值
     * 变量的具体取值
     * 对于不同类型的变量，此字段的含义不同：
     * - 文本类型：文本内容
     * - 数值类型：数值
     * - 日期类型：日期字符串
     * - 枚举类型：枚举值的标识
     */
    private String value;

    /**
     * 值描述
     * 对变量值的文字说明
     * 用于在界面上展示该值的含义，帮助用户理解
     */
    private String valueDesc;

    /**
     * 起始值
     * 用于定义范围的起始值
     * 例如：日期范围的开始日期、数值范围的最小值
     */
    private String begin;

    /**
     * 结束值
     * 用于定义范围的结束值
     * 例如：日期范围的结束日期、数值范围的最大值
     */
    private String end;
}
