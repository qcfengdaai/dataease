package io.dataease.api.permissions.login.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码登录请求对象
 *
 * <p>用于本地账号密码登录时传递登录凭证。
 * 为了保证安全性，账号和密码都需要在前端使用 RSA 公钥加密后传输。</p>
 *
 * <p>加密说明：
 * <ul>
 *   <li>前端获取服务端的 RSA 公钥</li>
 *   <li>使用公钥对账号和密码进行加密</li>
 *   <li>将加密后的字符串传递给后端</li>
 *   <li>后端使用私钥解密后进行验证</li>
 * </ul>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 1.0
 */
@Schema(description = "登录DTO")
@Data
public class PwdLoginDTO {

    /**
     * 用户账号（加密后）
     *
     * <p>用户的登录账号，可以是用户名、邮箱或手机号。
     * 前端需要使用 RSA 公钥加密后传输，后端会解密后再进行验证。</p>
     */
    @Schema(description = "账号(需加密)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "login.validator.name")
    private String name;

    /**
     * 用户密码（加密后）
     *
     * <p>用户的登录密码。
     * 前端需要使用 RSA 公钥加密后传输，后端会解密后再与数据库中存储的密码哈希进行比对。</p>
     */
    @Schema(description = "密码(需加密)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "login.validator.pwd")
    private String pwd;

    /**
     * 登录来源
     *
     * <p>标识登录请求的来源类型，用于区分不同的登录入口。
     * <ul>
     *   <li>0: 本地登录（默认）</li>
     *   <li>1: OIDC 登录</li>
     *   <li>2: LDAP 登录</li>
     *   <li>3: CAS 登录</li>
     * </ul>
     * 内部字段，前端无需传递。
     * </p>
     */
    @Hidden
    private Integer origin = 0;

}
