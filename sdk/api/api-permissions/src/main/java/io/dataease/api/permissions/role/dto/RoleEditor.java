package io.dataease.api.permissions.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色编辑器
 *
 * <p>用于更新已存在角色信息的数据传输对象。
 * 只能修改角色的基本信息（名称和描述），不包括角色类型等关键属性的修改。</p>
 *
 * <p>设计说明：</p>
 * <ul>
 *   <li>角色类型创建后不可修改，以保证权限体系的稳定性</li>
 *   <li>需要指定角色 ID 以确定要编辑的目标角色</li>
 *   <li>编辑操作需要对该角色有管理权限</li>
 * </ul>
 */
@Schema(description = "角色编辑器")
@Data
public class RoleEditor implements Serializable {
    @Serial
    private static final long serialVersionUID = -4071819873019095722L;

    /**
     * 角色 ID
     * 必填项，指定要编辑的角色唯一标识
     */
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 角色名称
     * 必填项，更新后的角色显示名称
     * 应具有业务含义且易于理解
     */
    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 角色描述
     * 可选项，更新后的角色详细说明
     * 用于描述角色的用途、权限范围等信息
     */
    @Schema(description = "名称", hidden = true)
    private String desc;

}
