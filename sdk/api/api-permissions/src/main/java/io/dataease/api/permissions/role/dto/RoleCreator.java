package io.dataease.api.permissions.role.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色创建器
 *
 * <p>用于创建新角色的数据传输对象，包含角色创建所需的基本信息。
 * 角色是权限管理的基本单位，可以将多个权限组合在一起分配给用户。</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>创建新的系统角色</li>
 *   <li>角色复制功能的基础数据对象</li>
 *   <li>批量创建角色</li>
 * </ul>
 */
@Schema(description = "角色构造器")
@Data
public class RoleCreator implements Serializable {
    @Serial
    private static final long serialVersionUID = -5311145649863484035L;

    /**
     * 角色名称
     * 必填项，用于标识角色的显示名称，应具有业务含义且易于理解
     */
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 角色类型代码
     * 必填项，用于区分不同类型的角色（如管理员角色、普通用户角色等）
     * 不同的类型代码对应不同的权限范围和管理规则
     */
    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer typeCode;

    /**
     * 角色描述
     * 可选项，用于详细说明角色的用途、权限范围等信息
     * 帮助管理员理解角色的具体职责
     */
    @Schema(description = "描述", hidden = true)
    private String desc;

    /**
     * 角色 ID
     * 内部使用字段，不对外暴露
     * 在某些场景下用于关联已存在的角色
     */
    @JsonIgnore
    @Schema(hidden = true)
    private Long rid;


}
