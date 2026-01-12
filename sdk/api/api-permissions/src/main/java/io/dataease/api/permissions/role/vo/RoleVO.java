package io.dataease.api.permissions.role.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色视图对象
 *
 * <p>用于前端展示的角色基本信息对象，包含角色的核心属性和状态标识。
 * 提供轻量级的角色信息展示，适用于列表、下拉选择等场景。</p>
 *
 * <p>属性说明：</p>
 * <ul>
 *   <li>id：角色的唯一标识，使用 Long 类型以支持大数据量</li>
 *   <li>name：角色显示名称</li>
 *   <li>readonly：标识该角色是否为只读（系统预置角色不可修改）</li>
 *   <li>root：标识该角色是否为根角色（最高权限角色）</li>
 * </ul>
 */
@Schema(description = "角色VO")
@Data
public class RoleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3488550489306534641L;

    /**
     * 角色 ID
     * 角色的唯一标识符，用于标识和引用特定角色
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @JsonSerialize(using= ToStringSerializer.class)
    @Schema(description = "ID")
    private Long id;

    /**
     * 角色名称
     * 角色的显示名称，用于在界面上展示
     */
    @Schema(description = "角色名称")
    private String name;

    /**
     * 只读标识
     * true：该角色为只读角色（通常是系统预置角色），不允许编辑或删除
     * false：该角色可以被编辑或删除
     */
    @Schema(description = "只读")
    private boolean readonly;

    /**
     * 根角色标识
     * true：该角色为根角色，拥有系统最高权限
     * false：该角色为普通角色
     * 根角色通常用于系统管理员，具有所有权限
     */
    @Schema(description = "根结点")
    private boolean root;
}
