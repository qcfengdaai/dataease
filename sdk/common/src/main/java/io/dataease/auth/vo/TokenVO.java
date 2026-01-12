package io.dataease.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "Token VO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 564596240616527258L;

    @Schema(description = "token")
    private String token;

    @Schema(description = "有效期")
    private Long exp;

    /**
     * 多因子认证信息
     * 包含MFA的启用状态和配置信息
     */
    private MfaItem mfa;

    /**
     * 密码失效信息
     * 当用户密码过期或需要强制修改时返回相关提示信息
     */
    private InvalidPwdVO invalidPwd;

    public TokenVO(String token, Long exp) {
        this.token = token;
        this.exp = exp;
    }
}
