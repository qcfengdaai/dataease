package io.dataease.api.permissions.login.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * MFA 二维码视图对象
 *
 * <p>用于返回 MFA 多因子认证的二维码和密钥信息。
 * 用户使用认证器应用扫描二维码后，即可绑定 MFA 设备。</p>
 *
 * <p>使用流程：
 * <ol>
 *   <li>管理员为用户启用 MFA</li>
 *   <li>用户登录时系统返回 MFA 二维码</li>
 *   <li>用户打开认证器应用（如 Google Authenticator、Microsoft Authenticator）</li>
 *   <li>扫描二维码或手动输入密钥</li>
 *   <li>认证器应用开始生成动态验证码</li>
 *   <li>用户输入验证码完成 MFA 绑定和登录</li>
 * </ol>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Schema(description = "MFA二维码信息")
@Data
public class MfaQrVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3465640829593927730L;

    /**
     * 二维码图片
     *
     * <p>Base64 编码的二维码图片数据，可以直接在前端显示。
     * 用户可以使用认证器应用扫描该二维码来绑定 MFA。
     * 格式：data:image/png;base64,xxxxx</p>
     */
    @Schema(description = "图片")
    private String img;

    /**
     * MFA 密钥
     *
     * <p>用于生成动态验证码的密钥字符串。
     * 如果用户无法扫描二维码，可以在认证器应用中手动输入该密钥来完成绑定。
     * 该密钥需要妥善保管，泄露后他人可以生成相同的验证码。</p>
     */
    @Schema(description = "KEY")
    private String key;
}
