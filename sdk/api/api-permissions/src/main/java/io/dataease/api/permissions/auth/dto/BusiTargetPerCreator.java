package io.dataease.api.permissions.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 业务目标权限构造器
 *
 * <p>用于从资源维度批量授权，为一个或多个业务资源批量授予访问权限给多个对象（用户/角色/组织）。
 * 适用于需要快速为一批资源统一配置访问权限的场景。</p>
 *
 * <p><strong>授权模式：</strong></p>
 * <ul>
 *   <li>批量资源授权：为多个资源统一配置相同的访问对象</li>
 *   <li>灵活权限配置：可以为不同对象设置不同的权限级别和行列权限</li>
 *   <li>快速初始化：新建资源后快速配置初始权限</li>
 * </ul>
 *
 * <p><strong>典型应用场景：</strong></p>
 * <ul>
 *   <li>新建资源后初始化权限配置，授权给相关用户或角色</li>
 *   <li>批量调整多个资源的权限分配</li>
 *   <li>为新角色快速配置对资源的访问权限</li>
 *   <li>组织架构调整后批量更新资源权限</li>
 * </ul>
 *
 * <p><strong>与菜单权限的区别：</strong></p>
 * <ul>
 *   <li>业务资源权限：控制数据访问，支持行列权限等精细化配置</li>
 *   <li>菜单权限：控制功能访问，权限配置相对简单</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * BusiTargetPerCreator creator = new BusiTargetPerCreator();
 *
 * // 设置要授权的对象ID列表
 * List&lt;Long&gt; ids = Arrays.asList(userId1, userId2);
 * creator.setIds(ids);
 *
 * // 设置对象类型（0: 用户）
 * creator.setType(0);
 *
 * // 设置资源类型
 * creator.setFlag("dataset");
 *
 * // 设置权限配置列表（为每个资源配置权限）
 * List&lt;PermissionItem&gt; items = new ArrayList&lt;&gt;();
 * PermissionItem item = new PermissionItem();
 * item.setId(datasetId);
 * item.setWeight(1);           // 读取权限
 * // 可选：配置行列权限
 * item.setColumnPermissions(columnPerms);
 * item.setRowPermissions(rowPerms);
 * items.add(item);
 *
 * creator.setPermissions(items);
 * authApi.saveBusiTargetPer(creator);
 * </pre>
 *
 * @author DataEase
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "资源权限构造器")
@Data
public class BusiTargetPerCreator extends MenuTargetPerCreator{

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 对象类型
     * <p>标识 ids 列表中的对象类型：</p>
     * <ul>
     *   <li>0: 用户 - 为用户授予资源权限</li>
     *   <li>1: 角色 - 为角色授予资源权限</li>
     *   <li>2: 组织 - 为组织授予资源权限</li>
     * </ul>
     *
     * <p><strong>注意：</strong>ids 列表中的所有对象必须是同一类型。</p>
     */
    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;

    /**
     * 资源类型标识
     * <p>指定要授权的资源类型，用于区分不同类别的业务资源：</p>
     * <ul>
     *   <li>dataset: 数据集资源</li>
     *   <li>dashboard: 仪表板资源</li>
     *   <li>datasource: 数据源资源</li>
     *   <li>folder: 文件夹资源</li>
     * </ul>
     *
     * <p><strong>注意：</strong>permissions 列表中的资源必须是同一类型。</p>
     */
    @Schema(description = "标记", requiredMode = Schema.RequiredMode.REQUIRED)
    private String flag;
}
