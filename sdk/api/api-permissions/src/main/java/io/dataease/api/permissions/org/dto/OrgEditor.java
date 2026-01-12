package io.dataease.api.permissions.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 组织编辑器
 * <p>
 * 用于更新现有组织单位信息的数据传输对象。
 * 目前仅支持修改组织名称，不支持修改父组织（移动组织位置）。
 * <p>
 * 使用场景：
 * <ul>
 *   <li>重命名组织单位</li>
 *   <li>修正组织名称错误</li>
 *   <li>调整组织名称以符合命名规范</li>
 * </ul>
 * <p>
 * 注意：修改组织信息需要对该组织具有管理权限。
 *
 * @author fit2cloud
 * @since 1.0
 */
@Schema(description = "组织编辑器")
@Data
public class OrgEditor implements Serializable {

    @Serial
    private static final long serialVersionUID = -5571486179570725994L;

    /**
     * 组织 ID
     * <p>
     * 要编辑的组织的唯一标识。
     * 必填项，用于定位要更新的组织记录。
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 组织名称
     * <p>
     * 组织的新名称。
     * 必填项，不能为空。
     * <p>
     * 更新后的名称会立即在组织树和相关界面中生效。
     */
    @Schema(description = "组织名称")
    private String name;
}
