package io.dataease.api.permissions.org.dto;

import io.dataease.model.KeywordRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
/**
 * 组织懒加载查询请求
 * <p>
 * 用于懒加载方式查询组织树的请求对象，继承自 {@link KeywordRequest}。
 * 与 {@link OrgRequest} 不同，此请求支持按需加载组织节点，提高大规模组织树的加载性能。
 * <p>
 * 懒加载机制：
 * <ul>
 *   <li>首次加载：pid 为 null，返回根级组织节点</li>
 *   <li>展开子节点：指定 pid，返回该节点的直接子节点</li>
 *   <li>搜索模式：设置 keyword，返回匹配的节点及其父节点路径</li>
 * </ul>
 * <p>
 * 适用场景：组织数量较多（数百个以上）时，避免一次性加载造成的性能问题。
 *
 * @author fit2cloud
 * @since 1.0
 * @see KeywordRequest
 * @see OrgRequest
 */
@Schema(description = "组织列表过滤器")
@EqualsAndHashCode(callSuper = true)
@Data
public class OrgLazyRequest extends KeywordRequest implements Serializable {

    /**
     * 父节点 ID
     * <p>
     * 指定要加载子节点的父组织 ID：
     * <ul>
     *   <li>null：加载根级组织（顶层节点）</li>
     *   <li>具体值：加载指定组织的直接子组织</li>
     * </ul>
     * <p>
     * 懒加载的核心参数，前端通过此参数实现按需加载。
     */
    @Schema(description = "上级节点ID")
    private Long pid;

    /**
     * 是否降序排列
     * <p>
     * 控制同级组织的排序方向：
     * <ul>
     *   <li>true：按创建时间降序（最新创建的在前）</li>
     *   <li>false：按创建时间升序（最早创建的在前）</li>
     * </ul>
     * <p>
     * 默认值为 true，即同级组织中最新创建的显示在前面。
     */
    @Schema(description = "是否降序", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean desc = true;
}
