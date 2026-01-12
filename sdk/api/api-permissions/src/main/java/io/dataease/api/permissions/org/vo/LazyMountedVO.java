package io.dataease.api.permissions.org.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 懒加载权限内组织视图对象
 * <p>
 * 用于懒加载模式下返回用户有权限访问的组织树数据。
 * 结合了权限过滤和懒加载两个特性，既只返回有权限的组织，又支持按需加载。
 * <p>
 * 与 {@link LazyTreeVO} 的区别：
 * <ul>
 *   <li>LazyTreeVO：返回所有组织（需要管理权限）</li>
 *   <li>LazyMountedVO：只返回有权限的组织（用于业务场景）</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>大规模组织树中的权限过滤选择</li>
 *   <li>数据权限配置中的组织范围选择</li>
 *   <li>用户、角色关联组织时的组织选择</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 * @see LazyTreeVO
 * @see MountedVO
 */
@Data
public class LazyMountedVO implements Serializable {

    /**
     * 权限内的组织节点列表
     * <p>
     * 当前加载的、用户有权限访问的组织节点集合。
     * 根据请求的不同场景包含不同内容：
     * <ul>
     *   <li>初始加载：包含有权限的根级组织</li>
     *   <li>展开节点：包含指定父节点下有权限的子组织</li>
     *   <li>搜索：包含匹配且有权限的组织及其父节点路径</li>
     * </ul>
     */
    private List<MountedVO> nodes;

    /**
     * 名称
     * <p>
     * 预留字段，可能用于显示当前选择的组织名称或其他标识信息。
     * 目前使用场景不明确，可能在特定业务逻辑中使用。
     */
    private String name;

    /**
     * 需要展开的节点 ID 列表
     * <p>
     * 用于控制前端树组件自动展开哪些有权限的节点。
     * 主要用于搜索场景：
     * <ul>
     *   <li>当用户搜索组织时，返回匹配的有权限的组织节点</li>
     *   <li>在 expandKeyList 中包含从根节点到匹配节点的完整路径（仅包含有权限的节点）</li>
     *   <li>前端根据此列表自动展开父节点，使匹配节点可见</li>
     * </ul>
     * <p>
     * 与 {@link LazyTreeVO#expandKeyList} 类似，但只包含有权限的节点路径。
     */
    private List<String> expandKeyList;
}
