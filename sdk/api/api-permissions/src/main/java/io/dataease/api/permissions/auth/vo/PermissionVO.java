package io.dataease.api.permissions.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 权限视图对象
 *
 * <p>封装对象（用户/角色/组织）或资源的完整权限信息，包括直接授予的权限
 * 和通过继承获得的权限。用于权限管理界面展示和权限检查。</p>
 *
 * <p><strong>权限来源：</strong></p>
 * <ul>
 *   <li><strong>直接权限：</strong>明确授予该对象的权限，记录在 permissions 字段</li>
 *   <li><strong>继承权限：</strong>通过组织、角色等关系继承的权限，记录在 permissionOrigins 字段</li>
 * </ul>
 *
 * <p><strong>权限计算规则：</strong></p>
 * <ul>
 *   <li>直接权限优先级最高</li>
 *   <li>继承权限按来源（组织、角色等）分组展示</li>
 *   <li>最终生效权限为所有权限的最大值</li>
 * </ul>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>权限授予界面：展示当前已授权的资源</li>
 *   <li>权限查询接口：查询对象的权限详情</li>
 *   <li>权限审计：追溯权限来源</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "权限结点")
@Data
public class PermissionVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7951267541124410580L;

    /**
     * 是否为根节点
     * <p>标识当前查询的对象或资源是否为根节点。
     * 根节点通常拥有特殊的权限继承规则。</p>
     * <ul>
     *   <li>true: 根节点，如顶级组织、系统管理员</li>
     *   <li>false: 非根节点</li>
     * </ul>
     */
    @Schema(description = "是否根结点")
    private boolean root;

    /**
     * 是否只读
     * <p>标识权限配置是否为只读状态，只读权限不允许修改。</p>
     * <ul>
     *   <li>true: 只读，不允许修改权限配置（如系统内置权限）</li>
     *   <li>false: 可编辑，允许修改权限配置</li>
     * </ul>
     */
    @Schema(description = "是否只读")
    private boolean readonly;

    /**
     * 直接权限项列表
     * <p>明确授予该对象的权限列表，每个权限项包含资源 ID、权限权重、
     * 行列权限等详细信息。这些权限是直接分配的，优先级最高。</p>
     *
     * <p>直接权限特点：
     * <ul>
     *   <li>可以直接修改或删除</li>
     *   <li>优先级高于继承权限</li>
     *   <li>支持精细化的权限配置（行列权限）</li>
     * </ul>
     * </p>
     */
    @Schema(description = "直接权限项")
    private List<PermissionItem> permissions;

    /**
     * 关联权限项列表（继承权限）
     * <p>通过组织、角色等关系继承获得的权限列表。每个关联权限包含来源信息
     * （如所属组织名称）和具体的权限项。</p>
     *
     * <p>继承权限来源：
     * <ul>
     *   <li>所属组织的权限</li>
     *   <li>所属角色的权限</li>
     *   <li>父节点的权限（如父文件夹）</li>
     * </ul>
     * </p>
     *
     * <p>继承权限特点：
     * <ul>
     *   <li>不能直接修改，需要在来源处修改</li>
     *   <li>用于权限追溯和审计</li>
     *   <li>按来源分组展示</li>
     * </ul>
     * </p>
     */
    @Schema(description = "关联权限项")
    private List<PermissionOrigin> permissionOrigins;
}
