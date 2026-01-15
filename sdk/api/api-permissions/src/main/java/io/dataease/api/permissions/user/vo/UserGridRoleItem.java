package io.dataease.api.permissions.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户角色项
 *
 * <p>用于在用户列表中展示角色信息的轻量级对象。
 * 只包含角色的 ID 和名称，便于在用户列表中快速展示用户的角色信息。</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>用户列表页面：展示用户拥有的角色</li>
 *   <li>角色标签：以标签形式展示用户的多个角色</li>
 *   <li>角色筛选：提供角色信息用于列表筛选</li>
 * </ul>
 */
@Schema(description = "角色项")
@Data
public class UserGridRoleItem {

    /**
     * 角色 ID
     * 角色的唯一标识符
     */
    @Schema(description = "角色ID")
    private Long id;

    /**
     * 角色名称
     * 角色的显示名称，用于在界面上展示
     */
    @Schema(description = "角色名称")
    private String name;
}
