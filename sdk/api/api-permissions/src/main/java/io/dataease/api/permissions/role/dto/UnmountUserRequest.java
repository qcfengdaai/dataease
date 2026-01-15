package io.dataease.api.permissions.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户解绑请求
 *
 * <p>用于将用户从指定角色中移除的请求对象。
 * 解绑后，用户将失去该角色所赋予的所有权限。</p>
 *
 * <p>功能特点：</p>
 * <ul>
 *   <li>精确解绑：一次只能解绑一个用户</li>
 *   <li>权限回收：解绑后立即生效，用户失去角色权限</li>
 *   <li>安全确认：解绑前可以通过 beforeUnmountInfo 接口查看影响范围</li>
 * </ul>
 *
 * <p>适用场景：</p>
 * <ul>
 *   <li>用户角色变更：调整用户的角色分配</li>
 *   <li>权限回收：收回用户不再需要的权限</li>
 *   <li>人员离职：移除离职人员的所有角色</li>
 * </ul>
 *
 * <p>注意事项：解绑操作不可逆，建议在执行前确认影响范围</p>
 */
@Schema(description = "用户解绑器")
@Data
public class UnmountUserRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1361046648092771178L;

    /**
     * 角色 ID
     * 必填项，指定要从中移除用户的角色
     */
    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long rid;

    /**
     * 用户 ID
     * 必填项，指定要解绑的用户
     * 解绑后该用户将失去指定角色的所有权限
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long uid;
}
