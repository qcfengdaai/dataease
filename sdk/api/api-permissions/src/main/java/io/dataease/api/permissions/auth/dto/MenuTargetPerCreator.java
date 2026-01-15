package io.dataease.api.permissions.auth.dto;

import io.dataease.api.permissions.auth.vo.PermissionItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * 菜单目标权限构造器
 *
 * <p>用于从菜单维度批量授权，为一个或多个菜单批量授予访问权限给多个对象（用户/角色/组织）。
 * 适用于需要快速为一批菜单统一配置访问权限的场景。</p>
 *
 * <p><strong>授权模式：</strong></p>
 * <ul>
 *   <li>批量菜单授权：为多个菜单统一配置相同的访问对象</li>
 *   <li>灵活权限配置：可以为不同对象设置不同的权限级别</li>
 *   <li>快速初始化：新增功能模块后快速配置菜单权限</li>
 * </ul>
 *
 * <p><strong>典型应用场景：</strong></p>
 * <ul>
 *   <li>新增功能模块后，为相关角色批量授予菜单访问权限</li>
 *   <li>快速为新角色配置其功能访问范围</li>
 *   <li>批量调整功能模块的访问控制</li>
 *   <li>组织架构调整后批量更新菜单权限</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * MenuTargetPerCreator creator = new MenuTargetPerCreator();
 *
 * // 设置要授权的对象ID列表
 * List&lt;Long&gt; ids = Arrays.asList(roleId1, roleId2);
 * creator.setIds(ids);
 *
 * // 设置权限配置列表（为每个菜单配置权限）
 * List&lt;PermissionItem&gt; items = new ArrayList&lt;&gt;();
 * PermissionItem item1 = new PermissionItem();
 * item1.setId(menuId1);
 * item1.setWeight(1);          // 有权限
 * items.add(item1);
 *
 * PermissionItem item2 = new PermissionItem();
 * item2.setId(menuId2);
 * item2.setWeight(1);          // 有权限
 * items.add(item2);
 *
 * creator.setPermissions(items);
 * authApi.saveMenuTargetPer(creator);
 * </pre>
 *
 * @author DataEase
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "菜单权限构造器")
@Data
public class MenuTargetPerCreator extends TargetPerCreator{

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单权限项列表
     * <p>要授予的菜单权限配置列表，每个权限项包含：</p>
     * <ul>
     *   <li>菜单 ID：要授权的菜单标识</li>
     *   <li>权限权重：通常为 1（有权限）</li>
     * </ul>
     *
     * <p><strong>权限配置说明：</strong></p>
     * <ul>
     *   <li>所有在 ids 列表中的对象都会被授予这些菜单的访问权限</li>
     *   <li>权重通常为 0（无权限）或 1（有权限）</li>
     *   <li>同一菜单只能出现一次</li>
     *   <li>权重为 0 的权限项会被忽略或删除</li>
     * </ul>
     *
     * <p><strong>批量授权逻辑：</strong></p>
     * <ul>
     *   <li>对 ids 列表中的每个对象</li>
     *   <li>授予 permissions 列表中所有菜单的访问权限</li>
     *   <li>覆盖之前的权限配置</li>
     * </ul>
     */
    @Schema(description = "权限集合", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PermissionItem> permissions;
}
