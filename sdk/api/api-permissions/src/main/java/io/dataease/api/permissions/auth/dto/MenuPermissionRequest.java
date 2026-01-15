package io.dataease.api.permissions.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 菜单权限查询请求对象
 *
 * <p>用于查询菜单的权限信息，支持从对象维度或菜单维度查询权限。
 * 相比业务权限查询，菜单权限查询更为简化，只需要对象或菜单的 ID。</p>
 *
 * <p><strong>查询场景：</strong></p>
 * <ul>
 *   <li><strong>对象维度：</strong>查询指定用户/角色/组织可以访问哪些菜单</li>
 *   <li><strong>菜单维度：</strong>查询指定菜单被授权给了哪些用户/角色/组织</li>
 * </ul>
 *
 * <p><strong>与业务权限的区别：</strong></p>
 * <ul>
 *   <li>菜单权限不需要指定资源类型（flag），因为只有一种菜单资源</li>
 *   <li>菜单权限通常用于控制功能访问，而非数据访问</li>
 *   <li>菜单权限的权重配置相对简单，主要是有权限或无权限</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "菜单权限查询条件")
@Data
public class MenuPermissionRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -7609671259840867561L;

    /**
     * 对象或菜单的 ID
     * <p>根据查询场景的不同，该字段可以表示：</p>
     * <ul>
     *   <li>对象 ID：查询该对象可访问的菜单列表</li>
     *   <li>菜单 ID：查询该菜单已授权的对象列表</li>
     * </ul>
     */
    @Schema(description = "ID")
    private Long id;

}
