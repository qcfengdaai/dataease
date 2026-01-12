package io.dataease.api.permissions.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 用户编辑器
 *
 * <p>用于编辑已存在用户信息的数据传输对象，继承自 UserCreator。
 * 包含用户的完整信息和用户 ID 标识。</p>
 *
 * <p>可编辑内容：</p>
 * <ul>
 *   <li>基本信息：姓名、邮箱、电话等</li>
 *   <li>角色分配：可以重新分配用户角色</li>
 *   <li>用户状态：启用或禁用用户</li>
 *   <li>系统变量：修改用户级配置参数</li>
 *   <li>MFA 设置：修改多因素认证配置</li>
 * </ul>
 *
 * <p>注意：账号（account）创建后不可修改</p>
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户编辑器")
@Data
public class UserEditor extends UserCreator{

    @Serial
    private static final long serialVersionUID = 1580870660998152922L;

    /**
     * 用户 ID
     * 必填项，指定要编辑的用户唯一标识
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
}
