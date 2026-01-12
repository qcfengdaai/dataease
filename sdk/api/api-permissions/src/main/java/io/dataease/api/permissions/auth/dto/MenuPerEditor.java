package io.dataease.api.permissions.auth.dto;

import io.dataease.api.permissions.auth.vo.PermissionItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 菜单权限编辑器
 *
 * <p>用于从对象维度编辑菜单的访问权限配置。通过该对象可以为指定的用户、角色或组织
 * 批量设置对多个菜单的访问权限，控制其可以访问的系统功能模块。</p>
 *
 * <p><strong>菜单权限特点：</strong></p>
 * <ul>
 *   <li>相对简单：主要是有权限（1）或无权限（0）</li>
 *   <li>支持继承：授予父菜单权限通常会自动授予子菜单权限</li>
 *   <li>前端控制：主要用于控制前端菜单的显示和页面的访问</li>
 *   <li>即时生效：权限变更后用户刷新页面即可生效</li>
 * </ul>
 *
 * <p><strong>编辑策略：</strong></p>
 * <ul>
 *   <li>新增：为对象添加新的菜单访问权限</li>
 *   <li>修改：调整已有菜单的访问权限</li>
 *   <li>删除：移除对象的菜单访问权限</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * MenuPerEditor editor = new MenuPerEditor();
 * editor.setId(roleId);           // 角色ID
 *
 * List&lt;PermissionItem&gt; items = new ArrayList&lt;&gt;();
 * PermissionItem item = new PermissionItem();
 * item.setId(menuId);             // 菜单ID
 * item.setWeight(1);              // 权重：有权限
 * items.add(item);
 *
 * editor.setPermissions(items);
 * </pre>
 *
 * @author DataEase
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "菜单权限编辑器")
@Data
public class MenuPerEditor extends MenuPermissionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 3410520935167596750L;

    /**
     * 菜单权限项列表
     * <p>要授予或修改的菜单权限配置列表，每个权限项包含：</p>
     * <ul>
     *   <li>菜单 ID：被授权的菜单标识</li>
     *   <li>权限权重：通常为 0（无权限）或 1（有权限）</li>
     * </ul>
     *
     * <p><strong>权重说明：</strong></p>
     * <ul>
     *   <li>0: 无权限，不能访问该菜单</li>
     *   <li>1: 有权限，可以访问该菜单</li>
     *   <li>其他值：保留，可用于扩展（如只读、编辑等）</li>
     * </ul>
     *
     * <p><strong>注意事项：</strong></p>
     * <ul>
     *   <li>空列表或 null 表示清空该对象的所有菜单权限</li>
     *   <li>权重为 0 的权限项将被删除</li>
     *   <li>同一菜单只能出现一次</li>
     *   <li>授予父菜单权限时需考虑子菜单的访问控制</li>
     * </ul>
     */
    @Schema(description = "菜单权限集合")
    private List<PermissionItem> permissions;
}
