package io.dataease.api.permissions.org.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * 懒加载组织树视图对象
 * <p>
 * 用于懒加载模式下返回组织树数据的包装对象。
 * 包含当前加载的节点列表和需要展开的节点 ID 列表。
 * <p>
 * 懒加载机制说明：
 * <ul>
 *   <li>nodes：当前加载的组织节点列表（可能是根节点或某个父节点的子节点）</li>
 *   <li>expandKeyList：需要自动展开的节点 ID 列表，用于搜索场景下自动展开匹配路径</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>首次加载根节点</li>
 *   <li>展开某个节点加载其子节点</li>
 *   <li>搜索时返回匹配节点并自动展开其父节点路径</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 * @see LazyOrgTreeNode
 */
@Schema(description = "组织树VO")
@Data
public class LazyTreeVO implements Serializable {

    /**
     * 组织节点列表
     * <p>
     * 当前加载的组织节点集合。
     * 在不同场景下包含不同内容：
     * <ul>
     *   <li>初始加载：包含所有根级组织</li>
     *   <li>展开节点：包含指定父节点的直接子组织</li>
     *   <li>关键词搜索：包含匹配的组织及其父节点路径</li>
     * </ul>
     */
    @Schema(description = "节点")
    private List<LazyOrgTreeNode> nodes;

    /**
     * 需要展开的节点 ID 列表
     * <p>
     * 用于控制前端树组件自动展开哪些节点。
     * 主要用于搜索场景：
     * <ul>
     *   <li>当用户搜索组织时，返回匹配的组织节点</li>
     *   <li>同时在 expandKeyList 中包含从根节点到匹配节点的完整路径</li>
     *   <li>前端根据此列表自动展开父节点，使匹配节点可见</li>
     * </ul>
     * <p>
     * 示例：搜索到组织 ID 为 100 的节点，其父节点路径为 1 -> 10 -> 100，
     * 则 expandKeyList 包含 ["1", "10"]，前端会自动展开这些节点。
     */
    @Schema(description = "展开节点")
    private List<String> expandKeyList;
}
