package io.dataease.api.permissions.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 密码修改请求
 *
 * <p>用于用户修改登录密码的请求对象。
 * 为了确保安全性，修改密码需要验证原密码。</p>
 *
 * <p>安全机制：</p>
 * <ul>
 *   <li>必须提供原密码进行验证，防止未授权修改</li>
 *   <li>新密码应符合系统密码强度要求</li>
 *   <li>密码修改后，之前的登录令牌可能失效</li>
 *   <li>建议定期修改密码以提高账号安全性</li>
 * </ul>
 */
@Schema(description = "密码修改器")
@Data
public class ModifyPwdRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -6583458043271002864L;

    /**
     * 用户 ID
     * 可选项，指定要修改密码的用户
     * 如果不指定，默认为当前登录用户
     */
    @Schema(description = "用户ID")
    private Long uid;

    /**
     * 原密码
     * 必填项，当前用户的登录密码
     * 用于验证操作者身份，确保密码修改的安全性
     */
    @Schema(description = "原始密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String pwd;

    /**
     * 新密码
     * 必填项，要设置的新密码
     * 应符合系统的密码强度要求（如长度、复杂度等）
     */
    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPwd;
}
