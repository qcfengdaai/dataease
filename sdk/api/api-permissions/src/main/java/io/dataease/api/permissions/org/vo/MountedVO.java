package io.dataease.api.permissions.org.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 权限内组织视图对象
 * <p>
 * 用于展示当前用户有权限访问的组织树结构。
 * 与 {@link OrgPageVO} 的主要区别在于此对象只包含用户有权限的组织节点，
 * 并增加了 leaf 字段用于标识叶子节点。
 * <p>
 * 使用场景：
 * <ul>
 *   <li>用户选择器中的组织树（只显示有权限的组织）</li>
 *   <li>资源授权时选择组织范围（限定在可见组织内）</li>
 *   <li>数据权限过滤中的组织选择</li>
 * </ul>
 * <p>
 * 权限过滤规则：
 * <ul>
 *   <li>只返回用户有读取权限的组织</li>
 *   <li>如果某个组织无权限，但其子组织有权限，仍会显示该组织（灰显或只读）</li>
 *   <li>完全没有权限的组织分支会被完全过滤掉</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 * @see OrgPageVO
 */
@Schema(description = "组织VO")
@Data
public class MountedVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7642741925705465785L;

    /**
     * 组织 ID
     * <p>
     * 使用 ToStringSerializer 序列化为字符串，
     * 避免 JavaScript 中 Number 类型的精度问题
     */
    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 组织名称
     * <p>
     * 组织的显示名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 只读标识
     * <p>
     * 标识当前用户对该组织的权限级别：
     * <ul>
     *   <li>true：只读权限，不能对该组织进行管理操作</li>
     *   <li>false：管理权限，可以对该组织进行编辑、删除等操作</li>
     * </ul>
     * <p>
     * 默认值为 true。即使在权限内的组织，也可能只有只读权限。
     */
    @Schema(description = "只读")
    private boolean readOnly = true;

    /**
     * 子组织列表
     * <p>
     * 当前组织下用户有权限访问的子组织集合。
     * 采用递归结构，只包含有权限的子组织节点。
     * <p>
     * 如果用户对某个子组织无权限，该子组织不会出现在此列表中。
     */
    @Schema(description = "子集")
    private List<MountedVO> children;

    /**
     * 是否为叶子节点
     * <p>
     * 标识该组织在当前用户的权限视图中是否为叶子节点：
     * <ul>
     *   <li>true：叶子节点，无可见的子组织（可能实际有子组织但用户无权限）</li>
     *   <li>false：非叶子节点，有可见的子组织</li>
     * </ul>
     * <p>
     * 此字段用于前端树组件渲染优化，决定是否显示展开图标。
     * 注意：leaf=true 不代表该组织在数据库中没有子组织，
     * 只是表示在当前用户的权限范围内，该组织下没有可见的子组织。
     */
    private boolean leaf;
}
