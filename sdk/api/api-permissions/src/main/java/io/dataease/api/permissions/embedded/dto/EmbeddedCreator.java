package io.dataease.api.permissions.embedded.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 嵌入式应用创建对象
 *
 * <p>用于创建新的嵌入式应用时传递参数。
 * 系统会根据这些参数自动生成 AppId 和 AppSecret。</p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Schema(description = "嵌入式应用构造器")
@Data
public class EmbeddedCreator implements Serializable {
    /**
     * 应用名称
     *
     * <p>嵌入式应用的显示名称，用于标识不同的集成应用。
     * 建议使用有意义的名称，如"OA系统集成"、"门户网站"等，便于后续管理。</p>
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 域名白名单
     *
     * <p>允许嵌入 DataEase 页面的域名列表，多个域名用逗号分隔。
     * 只有在白名单中的域名才能通过 iframe 嵌入 DataEase 页面。
     * 必须填写完整的域名协议和地址，如："https://oa.company.com,https://portal.company.com"</p>
     */
    @Schema(description = "应用域名")
    private String domain;

    /**
     * 密钥长度
     *
     * <p>生成 AppSecret 的字符长度，默认为 16 位。
     * 支持的长度范围通常为 8-32 位，长度越长安全性越高。</p>
     */
    @Schema(description = "密钥长度")
    private Integer secretLength = 16;
}
