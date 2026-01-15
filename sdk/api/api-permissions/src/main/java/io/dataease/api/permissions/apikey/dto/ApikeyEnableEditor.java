package io.dataease.api.permissions.apikey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * API Key 状态切换器
 *
 * <p>用于切换 API Key 的启用/禁用状态的数据传输对象。
 * 通过此对象可以控制指定 API Key 的可用性，而无需删除密钥。</p>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "状态切换器")
@Data
public class ApikeyEnableEditor implements Serializable {

    /**
     * API Key 的唯一标识符
     * <p>对应数据库中 api_key 表的主键 ID</p>
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 目标状态
     * <ul>
     *   <li>true: 启用 API Key，允许使用该密钥进行 API 调用</li>
     *   <li>false: 禁用 API Key，拒绝使用该密钥的所有 API 调用</li>
     * </ul>
     * <p>默认值为 false，确保安全性</p>
     */
    @Schema(description = "状态", defaultValue = "false")
    private Boolean enable = false;
}
