package io.dataease.api.permissions.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 业务权限查询请求对象
 *
 * <p>用于查询业务资源的权限信息，支持从对象维度或资源维度查询权限。
 * 该对象定义了权限查询的三个核心维度：对象 ID、对象类型和资源类型。</p>
 *
 * <p><strong>查询维度：</strong></p>
 * <ul>
 *   <li><strong>对象维度查询：</strong>查询指定对象（用户/角色/组织）对哪些资源有权限</li>
 *   <li><strong>资源维度查询：</strong>查询指定资源被授权给了哪些对象</li>
 * </ul>
 *
 * <p><strong>对象类型（type）：</strong></p>
 * <ul>
 *   <li>0: 用户</li>
 *   <li>1: 角色</li>
 *   <li>2: 组织/部门</li>
 * </ul>
 *
 * <p><strong>资源类型（flag）：</strong></p>
 * <ul>
 *   <li>dataset: 数据集</li>
 *   <li>dashboard: 仪表板</li>
 *   <li>datasource: 数据源</li>
 *   <li>folder: 文件夹</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "权限查询条件")
@Data
public class BusiPermissionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -2424587989223319563L;

    /**
     * 对象 ID
     * <p>查询的目标对象 ID，可以是用户 ID、角色 ID 或组织 ID，
     * 具体类型由 type 字段决定。</p>
     */
    @Schema(description = "对象ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 对象类型
     * <p>标识 ID 所代表的对象类型：</p>
     * <ul>
     *   <li>0: 用户 - 查询用户的资源权限</li>
     *   <li>1: 角色 - 查询角色的资源权限</li>
     *   <li>2: 组织 - 查询组织的资源权限</li>
     * </ul>
     */
    @Schema(description = "对象类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;

    /**
     * 资源类型标识
     * <p>指定要查询的资源类型，用于区分不同类别的业务资源：</p>
     * <ul>
     *   <li>dataset: 数据集资源</li>
     *   <li>dashboard: 仪表板资源</li>
     *   <li>datasource: 数据源资源</li>
     *   <li>folder: 文件夹资源</li>
     * </ul>
     */
    @Schema(description = "资源类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String flag;
}
