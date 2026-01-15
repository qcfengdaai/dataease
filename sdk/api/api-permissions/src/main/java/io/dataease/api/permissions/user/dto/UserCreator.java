package io.dataease.api.permissions.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dataease.api.permissions.variable.dto.SysVariableValueItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户创建器
 *
 * <p>用于创建新用户的数据传输对象，包含用户创建所需的完整信息。
 * 支持同时指定用户的基本信息、角色分配和系统变量配置。</p>
 *
 * <p>创建流程：</p>
 * <ul>
 *   <li>填写用户基本信息（账号、姓名、邮箱等）</li>
 *   <li>分配角色，确定用户权限范围</li>
 *   <li>设置用户状态（启用/禁用）</li>
 *   <li>配置用户级系统变量（可选）</li>
 *   <li>设置 MFA 选项（可选）</li>
 * </ul>
 */
@Schema(description = "用户构造器")
@Data
public class UserCreator implements Serializable {

    @Serial
    private static final long serialVersionUID = 5231186463604221044L;

    /**
     * 用户姓名
     * 必填项，用户的显示名称，用于界面展示
     */
    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 用户账号
     * 必填项，用户的登录账号，系统内唯一
     * 创建后不可修改，用于用户登录认证
     */
    @Schema(description = "账号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String account;

    /**
     * 用户邮箱
     * 必填项，用于接收系统通知和密码重置
     * 建议使用企业邮箱
     */
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    /**
     * 电话号码前缀
     * 可选项，国际电话区号（如 +86）
     */
    @Schema(description = "电话前缀")
    private String phonePrefix;

    /**
     * 电话号码
     * 可选项，用户的联系电话
     */
    @Schema(description = "电话")
    private String phone;

    /**
     * 角色 ID 集合
     * 必填项，为用户分配的角色列表
     * 角色决定了用户的权限范围
     */
    @Schema(description = "角色ID集合", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> roleIds;

    /**
     * 用户状态
     * 必填项，标识用户是否启用
     * true：用户可以正常登录使用系统
     * false：用户被禁用，无法登录
     */
    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enable;

    /**
     * 用户 ID
     * 内部使用字段，不对外暴露
     * 在某些场景下用于关联已存在的用户
     */
    @Schema(hidden = true)
    @JsonIgnore
    private Long uid;

    /**
     * MFA 启用状态
     * 标识是否为用户启用多因素认证
     * 默认为 false（不启用）
     */
    private Boolean mfaEnable = false;

    /**
     * 系统变量配置
     * 可选项，用户级别的系统变量值列表
     * 用于存储用户特定的配置参数
     */
    @Schema(description = "系统变量")
    private List<SysVariableValueItem> variables;
}
