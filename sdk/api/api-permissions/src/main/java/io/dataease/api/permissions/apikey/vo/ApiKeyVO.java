package io.dataease.api.permissions.apikey.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * API Key 视图对象
 *
 * <p>用于向前端展示 API Key 的详细信息。出于安全考虑，在列表查询时不返回 accessSecret，
 * 只有在首次生成时才会返回完整的密钥信息。</p>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "API Key VO")
@Data
public class ApiKeyVO implements Serializable {

    /**
     * API Key 的唯一标识符
     * <p>使用 Long 类型存储，前端序列化为字符串避免精度丢失</p>
     */
    @Schema(description = "ID")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /**
     * 访问密钥标识（公开部分）
     * <p>32位随机字符串，用于标识 API 调用者身份。
     * 此字段可以公开展示，但应妥善保管以防止被滥用。</p>
     */
    @Schema(description = "accessKey")
    private String accessKey;

    /**
     * 访问密钥密码（私密部分）
     * <p>64位随机字符串，用于验证 API 调用者身份。
     * 此字段仅在生成时返回一次，后续查询不会包含此字段。
     * 数据库中以加密形式存储，无法逆向解密。</p>
     *
     * <p><strong>重要：</strong>此字段仅在首次生成时有值，列表查询时为 null</p>
     */
    @Schema(description = "accessSecret")
    private String accessSecret;

    /**
     * API Key 的启用状态
     * <ul>
     *   <li>true: 已启用，可以正常使用</li>
     *   <li>false: 已禁用，使用该 Key 的 API 调用将被拒绝</li>
     * </ul>
     */
    @Schema(description = "状态")
    private Boolean enable;

    /**
     * API Key 的创建时间
     * <p>Unix 时间戳（毫秒），用于记录密钥的生成时间</p>
     */
    @Schema(description = "创建时间")
    private Long createTime;
}
