package io.dataease.api.permissions.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户状态切换请求
 *
 * <p>用于启用或禁用用户账号的请求对象。
 * 管理员可以通过此接口控制用户的登录权限。</p>
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>启用（enable = true）：用户可以正常登录和使用系统</li>
 *   <li>禁用（enable = false）：用户无法登录，已登录的会话可能被强制退出</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>临时禁用：暂时限制用户访问（如调查安全问题）</li>
 *   <li>人员离职：禁用离职人员账号</li>
 *   <li>账号解冻：重新启用被暂停的账号</li>
 * </ul>
 */
@Schema(description = "用户状态重置器")
@Data
public class EnableSwitchRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 8477475476294666602L;

    /**
     * 用户 ID
     * 必填项，指定要修改状态的用户
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 目标状态
     * 必填项，要设置的用户状态
     * true：启用用户，允许登录
     * false：禁用用户，禁止登录
     */
    @Schema(description = "用户状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enable;
}
