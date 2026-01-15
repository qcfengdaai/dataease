package io.dataease.api.permissions.embedded.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 嵌入式应用编辑对象
 *
 * <p>用于更新已存在的嵌入式应用配置。
 * 可以修改应用名称和域名白名单，但不能修改 AppId 和 AppSecret。</p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Schema(description = "嵌入式应用编辑器")
@Data
public class EmbeddedEditor implements Serializable {
    /**
     * 嵌入式应用ID
     *
     * <p>要编辑的应用的唯一标识，必填</p>
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 应用名称
     *
     * <p>嵌入式应用的新显示名称</p>
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 域名白名单
     *
     * <p>更新后的域名白名单，多个域名用逗号分隔。
     * 更新后立即生效，原有域名如果不在新名单中将无法继续嵌入。</p>
     */
    @Schema(description = "应用域名")
    private String domain;

    /**
     * 密钥长度
     *
     * <p>AppSecret 的字符长度配置，默认为 16 位。
     * 注意：修改此值不会重新生成密钥，仅作为配置记录。</p>
     */
    @Schema(description = "密钥长度")
    private Integer secretLength = 16;
}
