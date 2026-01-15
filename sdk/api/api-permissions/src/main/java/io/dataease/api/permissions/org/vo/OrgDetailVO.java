package io.dataease.api.permissions.org.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组织详情视图对象
 * <p>
 * 包含组织的完整详细信息，通常用于内部服务间调用或详情查询。
 * 相比于列表视图对象，此对象包含了组织的路径信息，便于进行权限判断和层级关系处理。
 * <p>
 * 使用场景：
 * <ul>
 *   <li>获取组织的完整信息（包括层级路径）</li>
 *   <li>内部服务调用时传递组织上下文</li>
 *   <li>权限判断时需要知道组织的完整路径</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrgDetailVO {

    /**
     * 组织 ID
     * <p>
     * 组织的唯一标识符
     */
    private Long id;

    /**
     * 组织名称
     * <p>
     * 组织的显示名称
     */
    private String name;

    /**
     * 父组织 ID
     * <p>
     * 上级组织的 ID，如果是根级组织则为 null 或 0
     */
    private Long pid;

    /**
     * 根路径
     * <p>
     * 从根节点到当前节点的完整路径，用于快速定位组织在树中的位置。
     * 路径格式通常为：/根节点ID/父节点ID/.../当前节点ID
     * <p>
     * 用途：
     * <ul>
     *   <li>快速判断组织的层级关系</li>
     *   <li>检查用户是否有权限访问该组织（通过路径匹配）</li>
     *   <li>查询某个组织下的所有子组织（通过路径前缀匹配）</li>
     * </ul>
     */
    private String rootPath;

}
