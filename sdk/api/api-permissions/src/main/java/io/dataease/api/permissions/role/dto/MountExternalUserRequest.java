package io.dataease.api.permissions.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 组织外用户绑定请求
 *
 * <p>用于将组织外的用户绑定到指定角色的请求对象。
 * 组织外用户是指不属于当前组织但需要访问组织资源的用户，
 * 例如：跨组织协作的用户、外部合作伙伴等。</p>
 *
 * <p>与 MountUserRequest 的区别：</p>
 * <ul>
 *   <li>MountUserRequest：用于绑定组织内用户，支持批量操作</li>
 *   <li>MountExternalUserRequest：用于绑定组织外用户，单个操作</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>跨组织协作：允许其他组织的用户访问本组织资源</li>
 *   <li>临时权限授予：为外部用户临时分配特定角色权限</li>
 *   <li>资源共享：实现组织间的数据和资源共享</li>
 * </ul>
 */
@Schema(description = "组外用户绑定器")
@Data
public class MountExternalUserRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -1682136323964916544L;

    /**
     * 角色 ID
     * 指定要绑定用户的目标角色
     * 组织外用户绑定后将获得该角色在本组织内的权限
     */
    @Schema(description = "角色ID")
    private Long rid;

    /**
     * 组织外用户 ID
     * 要绑定到角色的组织外用户的唯一标识
     * 该用户必须在系统中存在但不属于当前组织
     */
    @Schema(description = "组外用户ID")
    private Long uid;
}
