package io.dataease.api.permissions.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 业务批量授权请求对象
 *
 * <p>用于执行批量授权操作，一次性为多个资源授予权限给指定对象。
 * 支持按权限级别分组，为不同的资源组设置不同的权限。</p>
 *
 * <p><strong>批量授权优势：</strong></p>
 * <ul>
 *   <li>性能优化：减少网络请求次数和数据库交互</li>
 *   <li>事务保证：所有授权操作在一个事务中完成，保证数据一致性</li>
 *   <li>灵活配置：支持为不同资源设置不同权限级别</li>
 *   <li>批量回滚：出错时可以整体回滚，避免数据不一致</li>
 * </ul>
 *
 * <p><strong>典型应用场景：</strong></p>
 * <ul>
 *   <li>系统初始化时批量配置权限</li>
 *   <li>新建文件夹后为其下所有资源批量授权</li>
 *   <li>组织架构调整后批量更新资源访问权限</li>
 *   <li>角色变更后批量调整资源权限</li>
 *   <li>批量导入资源后初始化权限配置</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * BusiBatchAuthorizeRequest request = new BusiBatchAuthorizeRequest();
 *
 * // 设置要授权的对象（用户/角色/组织）
 * request.setOid(userId);
 *
 * // 创建批量授权节点列表
 * List&lt;BusiBatchAuthorizeNode&gt; nodes = new ArrayList&lt;&gt;();
 *
 * // 节点1: 读取权限的资源
 * BusiBatchAuthorizeNode node1 = new BusiBatchAuthorizeNode();
 * node1.setIdList(Arrays.asList(datasetId1, datasetId2));
 * node1.setFlag(1);  // 读取权限
 * nodes.add(node1);
 *
 * // 节点2: 编辑权限的资源
 * BusiBatchAuthorizeNode node2 = new BusiBatchAuthorizeNode();
 * node2.setIdList(Arrays.asList(datasetId3, datasetId4));
 * node2.setFlag(3);  // 编辑权限
 * nodes.add(node2);
 *
 * request.setNodeList(nodes);
 * authApi.batchAuthorize(request);
 * </pre>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "批量授权请求")
@Data
public class BusiBatchAuthorizeRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5219199153835271350L;

    /**
     * 批量授权节点列表
     * <p>包含多个授权节点，每个节点代表一组拥有相同权限的资源。
     * 通过节点列表可以实现不同资源的差异化授权。</p>
     *
     * <p><strong>节点组织方式：</strong></p>
     * <ul>
     *   <li>按权限级别分组：相同权限的资源放在同一节点</li>
     *   <li>按资源类型分组：不同类型的资源可以分开处理</li>
     *   <li>按业务逻辑分组：根据业务需求灵活分组</li>
     * </ul>
     *
     * <p><strong>处理逻辑：</strong></p>
     * <ul>
     *   <li>遍历每个节点</li>
     *   <li>为 oid 指定的对象授予该节点中所有资源的访问权限</li>
     *   <li>权限级别由节点的 flag 字段决定</li>
     *   <li>所有操作在一个事务中完成</li>
     * </ul>
     *
     * <p><strong>注意事项：</strong></p>
     * <ul>
     *   <li>列表不能为空</li>
     *   <li>同一资源不应出现在多个节点中</li>
     *   <li>节点数量建议不超过 100 个，避免性能问题</li>
     * </ul>
     */
    @Schema(description = "授权节点列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<BusiBatchAuthorizeNode> nodeList;

    /**
     * 对象 ID
     * <p>要被授权的对象（用户、角色或组织）的唯一标识符。
     * 该对象将获得 nodeList 中所有资源的访问权限。</p>
     *
     * <p><strong>对象类型：</strong></p>
     * <ul>
     *   <li>用户 ID：为指定用户批量授权</li>
     *   <li>角色 ID：为指定角色批量授权</li>
     *   <li>组织 ID：为指定组织批量授权</li>
     * </ul>
     *
     * <p><strong>授权效果：</strong></p>
     * <ul>
     *   <li>用户授权：该用户可以直接访问这些资源</li>
     *   <li>角色授权：该角色的所有成员可以访问这些资源</li>
     *   <li>组织授权：该组织的所有成员可以访问这些资源</li>
     * </ul>
     */
    @Schema(description = "对象ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long oid;
}
