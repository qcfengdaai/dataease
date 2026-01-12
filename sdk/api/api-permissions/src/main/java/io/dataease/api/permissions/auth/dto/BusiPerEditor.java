package io.dataease.api.permissions.auth.dto;

import io.dataease.api.permissions.auth.vo.PermissionItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 业务权限编辑器
 *
 * <p>用于从对象维度编辑业务资源的权限配置。通过该对象可以为指定的用户、角色或组织
 * 批量设置对多个业务资源的访问权限。</p>
 *
 * <p><strong>编辑模式：</strong></p>
 * <ul>
 *   <li><strong>新增模式：</strong>为之前没有权限的资源添加访问权限</li>
 *   <li><strong>修改模式：</strong>调整已有权限的权重或行列权限配置</li>
 *   <li><strong>删除模式：</strong>传入空权限列表或权重为 0 表示移除权限</li>
 * </ul>
 *
 * <p><strong>批量操作：</strong></p>
 * <ul>
 *   <li>支持一次为一个对象配置多个资源的权限</li>
 *   <li>支持为不同资源设置不同的权重和行列权限</li>
 *   <li>操作具有原子性，要么全部成功要么全部失败</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * BusiPerEditor editor = new BusiPerEditor();
 * editor.setId(userId);           // 用户ID
 * editor.setType(0);              // 对象类型：用户
 * editor.setFlag("dataset");      // 资源类型：数据集
 *
 * List&lt;PermissionItem&gt; items = new ArrayList&lt;&gt;();
 * PermissionItem item = new PermissionItem();
 * item.setId(datasetId);          // 数据集ID
 * item.setWeight(1);              // 权重：读取
 * items.add(item);
 *
 * editor.setPermissions(items);
 * </pre>
 *
 * @author DataEase
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "业务权限编辑器")
@Data
public class BusiPerEditor extends BusiPermissionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 3067994331757489447L;

    /**
     * 权限项列表
     * <p>要授予或修改的权限配置列表，每个权限项包含：</p>
     * <ul>
     *   <li>资源 ID：被授权的资源标识</li>
     *   <li>权限权重：访问权限级别（0-15）</li>
     *   <li>列权限：针对数据集的列级权限配置（可选）</li>
     *   <li>行权限：针对数据集的行级权限配置（可选）</li>
     * </ul>
     *
     * <p><strong>注意事项：</strong></p>
     * <ul>
     *   <li>空列表或 null 表示清空该对象的所有权限</li>
     *   <li>权重为 0 的权限项将被删除</li>
     *   <li>对于数据集类型，可以配置行列权限实现精细化控制</li>
     *   <li>同一资源只能出现一次，重复配置会被覆盖</li>
     * </ul>
     */
    @Schema(description = "编辑权限节点集合")
    private List<PermissionItem> permissions;
}
