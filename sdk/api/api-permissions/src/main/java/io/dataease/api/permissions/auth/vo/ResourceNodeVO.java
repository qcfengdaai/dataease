package io.dataease.api.permissions.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 资源节点简化视图对象
 *
 * <p>表示资源节点的简化信息，仅包含 ID 和名称，用于路径展示、
 * 面包屑导航等不需要完整资源信息的场景。</p>
 *
 * <p><strong>与 ResourceVO 的区别：</strong></p>
 * <ul>
 *   <li>ResourceVO: 完整的资源树节点，包含子节点、叶子标识等信息</li>
 *   <li>ResourceNodeVO: 简化的资源节点，只包含基本的 ID 和名称</li>
 * </ul>
 *
 * <p><strong>典型应用场景：</strong></p>
 * <ul>
 *   <li>面包屑导航：显示从当前位置到根节点的路径</li>
 *   <li>权限继承链：展示权限的继承路径</li>
 *   <li>资源引用：简单的资源引用关系</li>
 *   <li>日志记录：记录资源的基本信息</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "资源节点简化视图")
@Data
public class ResourceNodeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 资源 ID
     * <p>资源的唯一标识符</p>
     */
    @Schema(description = "资源ID")
    private Long id;

    /**
     * 资源名称
     * <p>用于显示的资源名称</p>
     */
    @Schema(description = "资源名称")
    private String name;
}
