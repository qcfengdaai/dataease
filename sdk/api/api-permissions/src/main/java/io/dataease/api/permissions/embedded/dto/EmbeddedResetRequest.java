package io.dataease.api.permissions.embedded.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 嵌入式密钥重置请求对象
 *
 * <p>用于重置嵌入式应用的 AppSecret。
 * 重置后旧密钥立即失效，需要使用新密钥才能继续访问。</p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Schema(description = "嵌入式密钥重置器")
@Data
public class EmbeddedResetRequest implements Serializable {

    /**
     * 嵌入式应用ID
     *
     * <p>要重置密钥的应用的唯一标识</p>
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 新的应用密钥
     *
     * <p>重置后的新 AppSecret。
     * 如果不指定，系统会自动生成一个新的随机密钥。
     * 重置后需要立即更新所有使用该应用的第三方系统配置。</p>
     */
    @Schema(description = "新密钥")
    private String appSecret;
}
