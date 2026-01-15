package io.dataease.api.permissions.auth.dto;

import io.dataease.api.permissions.auth.vo.PermissionItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 权限业务对象
 *
 * <p>扩展了 PermissionItem，增加了资源 ID 字段，用于内部业务逻辑处理。
 * 该对象通常用于权限计算、权限合并等内部场景。</p>
 *
 * <p><strong>与 PermissionItem 的区别：</strong></p>
 * <ul>
 *   <li>PermissionItem: 视图对象，用于前端展示，ID 字段表示权限记录的 ID</li>
 *   <li>PermissionBO: 业务对象，用于后端处理，增加了 resourceId 字段明确表示资源 ID</li>
 * </ul>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>权限计算：计算用户的最终生效权限</li>
 *   <li>权限合并：合并多个来源的权限</li>
 *   <li>权限传递：在服务间传递权限信息</li>
 *   <li>权限缓存：缓存权限数据</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "权限业务对象")
@EqualsAndHashCode(callSuper = true)
@Data
public class PermissionBO extends PermissionItem {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 资源 ID
     * <p>被授权资源的唯一标识符，用于明确标识权限所属的资源。
     * 此字段与父类 PermissionItem 中的 id 字段含义不同：</p>
     * <ul>
     *   <li>父类 id: 权限记录的数据库 ID</li>
     *   <li>resourceId: 被授权资源的业务 ID</li>
     * </ul>
     */
    @Schema(description = "资源ID")
    private Long resourceId;
}
