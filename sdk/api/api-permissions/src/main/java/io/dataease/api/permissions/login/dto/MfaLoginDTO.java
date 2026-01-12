package io.dataease.api.permissions.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * MFA 多因子认证登录请求对象
 *
 * <p>用于 MFA 二次验证时传递验证信息。
 * 在用户通过账号密码初步验证后，如果启用了 MFA，需要输入认证器生成的动态验证码。</p>
 *
 * <p>MFA 验证流程：
 * <ol>
 *   <li>用户输入账号密码，通过初步验证</li>
 *   <li>系统检测到用户启用了 MFA</li>
 *   <li>用户打开认证器应用（如 Google Authenticator），查看动态验证码</li>
 *   <li>用户输入验证码，提交 MFA 验证请求</li>
 *   <li>系统使用 TOTP 算法验证验证码的正确性</li>
 *   <li>验证通过后生成 Token，完成登录</li>
 * </ol>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Schema(description = "MFA登录DTO")
@Data
public class MfaLoginDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8218773323394184937L;

    /**
     * 用户ID
     *
     * <p>要进行 MFA 验证的用户唯一标识</p>
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 动态验证码
     *
     * <p>认证器应用生成的 6 位数字验证码。
     * 验证码基于时间（TOTP 算法），每 30 秒更新一次，每个验证码只能使用一次。</p>
     */
    @Schema(description = "CODE")
    private String code;

    /**
     * MFA 密钥
     *
     * <p>用户绑定认证器时生成的密钥，用于验证码的生成和验证。
     * 该密钥在用户首次设置 MFA 时生成，之后保持不变。</p>
     */
    @Schema(description = "KEY")
    private String key;
}
