package io.dataease.api.permissions.variable.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统变量值项
 *
 * <p>用于表示用户级别的变量赋值项。此对象组合了变量定义、变量值和用户选择，
 * 用于在用户配置、查询参数等场景中传递完整的变量信息。</p>
 *
 * <p>核心功能：</p>
 * <ul>
 *   <li>变量标识：记录变量的 ID、名称、类型</li>
 *   <li>值选择：记录用户选择的变量值</li>
 *   <li>值列表：提供变量的所有可选值</li>
 *   <li>有效性：标识该变量配置是否有效</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>用户变量配置：在用户信息中存储用户的变量赋值</li>
 *   <li>查询参数传递：在查询请求中携带变量参数</li>
 *   <li>报表参数：在报表生成时传递参数值</li>
 *   <li>权限控制：基于变量值进行权限判断</li>
 * </ul>
 *
 * <p>数据结构说明：</p>
 * <ul>
 *   <li>单值变量：使用 variableValue 和 variableValueId</li>
 *   <li>双值变量（范围）：使用 variableValue 和 variableValue2</li>
 *   <li>多值变量：使用 variableValueIds 列表</li>
 * </ul>
 */
@Data
public class SysVariableValueItem {

    /**
     * 变量值（主值）
     * 用户选择或输入的变量值
     * 对于单值变量，这是唯一的值
     * 对于范围变量，这是起始值
     */
    private String variableValue;

    /**
     * 变量值2（辅助值）
     * 用于范围类型的变量
     * 例如：日期范围的结束日期、数值范围的最大值
     * 使用 ToStringSerializer 序列化为字符串
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private String variableValue2;

    /**
     * 变量类型
     * 变量的数据类型，如：text、number、date、enum
     * 决定了变量的输入方式和验证规则
     */
    private String variableType;

    /**
     * 变量 ID
     * 变量定义的唯一标识
     * 关联到 SysVariableDto
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long variableId;

    /**
     * 变量值 ID 列表
     * 用于多选型变量，存储用户选择的多个值的 ID
     * 例如：多选枚举变量
     * 默认初始化为空列表
     */
    private List<String> variableValueIds = new ArrayList<>();

    /**
     * 变量值 ID（单选）
     * 用于单选型变量，存储用户选择的值的 ID
     * 关联到 SysVariableValueDto
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long variableValueId ;

    /**
     * 变量名称
     * 变量的显示名称
     * 用于在界面上标识该变量
     */
    private String variableName;

    /**
     * 有效性标识
     * 标识该变量配置是否有效
     * true：配置有效，可以正常使用
     * false：配置无效（如变量已删除、值已失效等）
     * 默认为 true
     */
    private boolean valid = true;

    /**
     * 变量可选值列表
     * 该变量的所有可选值
     * 用于枚举类型变量，提供给用户选择
     */
    private List<SysVariableValueDto> valueList;

    /**
     * 系统变量定义
     * 该变量的完整定义信息
     * 包含变量类型、名称、取值规则等
     */
    private SysVariableDto sysVariableDto;

}
