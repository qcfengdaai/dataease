package io.dataease.api.permissions.embedded.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 嵌入式来源验证对象
 *
 * <p>用于验证 iframe 嵌入请求的合法性。
 * 在 iframe 加载 DataEase 页面时，系统会检查请求的来源域名和 Token 是否合法。</p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Data
public class EmbeddedOrigin implements Serializable {

    /**
     * 访问令牌
     *
     * <p>第三方系统使用 AppId 和 AppSecret 生成的访问 Token。
     * 系统会验证 Token 的合法性和有效期，验证通过后才允许嵌入访问。</p>
     */
    private String token;

    /**
     * 来源域名
     *
     * <p>发起嵌入请求的页面域名，包含协议和主机名。
     * 系统会检查该域名是否在应用的域名白名单中，只有白名单中的域名才能嵌入。
     * 示例："https://oa.company.com"</p>
     */
    private String origin;
}
