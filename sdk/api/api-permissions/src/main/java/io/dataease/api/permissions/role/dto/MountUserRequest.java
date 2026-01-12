package io.dataease.api.permissions.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户绑定请求
 *
 * <p>用于将一个或多个组织内用户绑定到指定角色的请求对象。
 * 绑定后，这些用户将获得该角色所拥有的所有权限。</p>
 *
 * <p>功能特点：</p>
 * <ul>
 *   <li>支持批量绑定：一次请求可以将多个用户绑定到同一角色</li>
 *   <li>仅限组织内用户：只能绑定与当前组织相关的用户</li>
 *   <li>权限验证：需要对目标角色有管理权限才能执行绑定操作</li>
 * </ul>
 *
 * <p>注意事项：</p>
 * <ul>
 *   <li>如果用户已拥有该角色，重复绑定不会产生错误</li>
 *   <li>绑定组织外用户请使用 MountExternalUserRequest</li>
 * </ul>
 */
@Schema(description = "用户绑定器")
@Data
public class MountUserRequest implements Serializable {

    /**
     * 角色 ID
     * 必填项，指定要绑定用户的目标角色
     * 用户绑定后将获得该角色的所有权限
     */
    @Schema(description = "组织ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long rid;

    /**
     * 用户 ID 集合
     * 必填项，要绑定到角色的用户 ID 列表
     * 支持批量绑定，可以同时绑定多个用户
     */
    @Schema(description = "用户ID集合", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> uids;
}
