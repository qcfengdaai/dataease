package io.dataease.api.permissions.auth.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 权限来源视图对象
 *
 * <p>表示通过继承方式获得的权限及其来源信息。用于权限追溯和审计，
 * 帮助用户理解权限的继承关系和来源。</p>
 *
 * <p><strong>权限继承来源：</strong></p>
 * <ul>
 *   <li><strong>组织继承：</strong>用户从所属组织继承的权限</li>
 *   <li><strong>角色继承：</strong>用户从所属角色继承的权限</li>
 *   <li><strong>上级继承：</strong>资源从父节点继承的权限</li>
 *   <li><strong>组合继承：</strong>多个来源组合产生的权限</li>
 * </ul>
 *
 * <p><strong>继承权限特点：</strong></p>
 * <ul>
 *   <li>不能直接修改：需要在来源处修改</li>
 *   <li>自动生效：来源权限变更自动影响继承权限</li>
 *   <li>透明展示：清晰展示权限的来源和继承路径</li>
 *   <li>优先级低：直接权限优先级高于继承权限</li>
 * </ul>
 *
 * <p><strong>应用场景：</strong></p>
 * <ul>
 *   <li>权限审计：追溯用户权限的来源</li>
 *   <li>权限诊断：排查权限问题</li>
 *   <li>权限展示：向用户说明为什么拥有某些权限</li>
 *   <li>合规检查：确认权限分配的合理性</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "关联权限")
@Data
public class PermissionOrigin implements Serializable {
    @Serial
    private static final long serialVersionUID = 1455588932869130794L;

    /**
     * 权限来源的 ID
     * <p>标识权限来源对象的 ID，如组织 ID、角色 ID、父资源 ID 等。
     * 使用 Long 类型存储，前端序列化为字符串避免精度丢失。</p>
     */
    @JsonSerialize(using= ToStringSerializer.class)
    @Schema(description = "关联ID")
    private Long id;

    /**
     * 权限来源的名称
     * <p>权限来源对象的显示名称，用于在界面上展示权限的来源。
     * 例如："研发部"、"管理员角色"、"销售数据文件夹"等。</p>
     *
     * <p>名称格式示例：
     * <ul>
     *   <li>组织来源："[组织] 研发部"</li>
     *   <li>角色来源："[角色] 数据分析师"</li>
     *   <li>父节点来源："[继承] 销售数据"</li>
     * </ul>
     * </p>
     */
    @Schema(description = "关联名称")
    private String name;

    /**
     * 从该来源继承的权限项列表
     * <p>详细列出从该来源继承获得的所有权限项，每个权限项包含资源 ID、
     * 权限权重、行列权限等详细配置。</p>
     *
     * <p>列表特点：
     * <ul>
     *   <li>每个权限项对应一个具体的资源</li>
     *   <li>包含完整的权限配置信息</li>
     *   <li>按资源类型或名称排序</li>
     *   <li>支持展开查看详细信息</li>
     * </ul>
     * </p>
     *
     * <p><strong>注意：</strong>这些权限项不能直接修改，必须在来源对象处进行修改。</p>
     */
    @Schema(description = "关联权限项")
    private List<PermissionItem> permissions;
}
