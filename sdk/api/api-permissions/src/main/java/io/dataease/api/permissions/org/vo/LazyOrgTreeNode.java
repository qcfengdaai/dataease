package io.dataease.api.permissions.org.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 懒加载组织树节点
 * <p>
 * 用于懒加载模式下表示单个组织节点的数据对象。
 * 与 {@link OrgPageVO} 相比，增加了 pid 和 hasChildren 字段，
 * 用于支持懒加载场景下的按需加载和节点状态判断。
 * <p>
 * 关键特性：
 * <ul>
 *   <li>hasChildren：标识节点是否有子节点，用于前端显示展开/折叠图标</li>
 *   <li>children：可选的子节点列表，仅在该节点被展开时加载</li>
 *   <li>pid：父节点 ID，用于建立父子关系</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 * @see OrgPageVO
 * @see LazyTreeVO
 */
@Schema(description = "组织列表VO")
@Data
public class LazyOrgTreeNode implements Serializable {

    /**
     * 组织 ID
     * <p>
     * 使用 ToStringSerializer 序列化为字符串，
     * 避免 JavaScript 中 Number 类型的精度问题
     */
    @Schema(description = "ID")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /**
     * 父组织 ID
     * <p>
     * 当前组织的上级组织 ID。
     * 使用 ToStringSerializer 序列化为字符串。
     * <ul>
     *   <li>根级组织：pid 为 null 或 0</li>
     *   <li>子组织：pid 为其父组织的 ID</li>
     * </ul>
     * <p>
     * 此字段用于懒加载时建立父子关系，前端可据此判断节点的层级位置。
     */
    @Schema(description = "PID")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long pid;

    /**
     * 组织名称
     * <p>
     * 组织的显示名称，在树形结构中作为节点标签显示
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 创建时间
     * <p>
     * 组织创建的时间戳（毫秒），用于排序和展示
     */
    @Schema(description = "创建时间")
    private Long createTime;

    /**
     * 只读标识
     * <p>
     * 标识当前用户对该组织是否只有只读权限：
     * <ul>
     *   <li>true：只读权限，不能编辑或删除该组织</li>
     *   <li>false：管理权限，可以编辑或删除该组织</li>
     * </ul>
     * <p>
     * 默认值为 true。前端根据此字段决定是否显示编辑、删除等操作按钮。
     */
    @Schema(description = "只读")
    private boolean readOnly = true;

    /**
     * 是否有子节点
     * <p>
     * 标识当前组织是否包含子组织，用于控制前端树组件的展开/折叠图标显示：
     * <ul>
     *   <li>true：有子节点，显示展开图标，点击时触发懒加载</li>
     *   <li>false：无子节点，不显示展开图标，视为叶子节点</li>
     * </ul>
     * <p>
     * 此字段是懒加载的关键，避免一次性加载所有节点，按需加载子节点。
     */
    @Schema(description = "有子集")
    private boolean hasChildren;

    /**
     * 子组织列表
     * <p>
     * 当前组织的直接子组织集合。
     * 在懒加载模式下：
     * <ul>
     *   <li>初始为 null 或空列表：表示子节点尚未加载</li>
     *   <li>有值：表示该节点已展开，子节点已加载</li>
     * </ul>
     * <p>
     * 前端在用户点击展开图标时，通过 API 请求加载子节点并填充此字段。
     */
    @Schema(description = "子集")
    private List<LazyOrgTreeNode> children;
}
