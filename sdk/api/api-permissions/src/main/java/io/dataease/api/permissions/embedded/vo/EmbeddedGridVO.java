package io.dataease.api.permissions.embedded.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 嵌入式应用列表视图对象
 *
 * <p>用于展示嵌入式应用的列表信息，包含应用的完整配置数据。
 * 该对象会在查询嵌入式应用列表时返回给前端。</p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Schema(description = "嵌入式列表VO")
@Data
public class EmbeddedGridVO implements Serializable {

    /**
     * 嵌入式应用ID
     *
     * <p>应用的唯一标识，用于编辑、删除等操作</p>
     */
    @Schema(description = "ID")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /**
     * 应用名称
     *
     * <p>嵌入式应用的显示名称，用于标识不同的集成应用，如"OA系统集成"、"门户网站"等</p>
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用ID (AppId)
     *
     * <p>系统生成的应用标识符，用于嵌入式集成时的身份识别。
     * 第三方系统需要使用此 AppId 和 AppSecret 来生成访问 Token。</p>
     */
    @Schema(description = "应用ID")
    private String appId;

    /**
     * 应用密钥 (AppSecret)
     *
     * <p>用于生成访问 Token 的密钥，需要妥善保管。
     * 密钥仅在创建和重置时完整显示，其他时候会部分隐藏（如：abc***xyz）。
     * 第三方系统使用 AppId 和 AppSecret 通过加密算法生成 Token 后才能访问嵌入内容。</p>
     */
    @Schema(description = "应用密钥")
    private String appSecret;

    /**
     * 域名白名单
     *
     * <p>允许嵌入 DataEase 页面的域名列表，多个域名用逗号分隔。
     * 只有在白名单中的域名才能通过 iframe 嵌入 DataEase 页面，用于防止跨域攻击。
     * 示例："https://oa.company.com,https://portal.company.com"</p>
     */
    @Schema(description = "应用域名")
    private String domain;

    /**
     * 密钥长度
     *
     * <p>AppSecret 的字符长度，默认为 16 位。
     * 可以根据安全需求设置不同的长度，长度越长安全性越高，但使用时也更复杂。</p>
     */
    @Schema(description = "密钥长度")
    private Integer secretLength = 16;
}
