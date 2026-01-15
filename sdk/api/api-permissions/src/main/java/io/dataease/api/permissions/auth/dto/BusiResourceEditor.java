package io.dataease.api.permissions.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 业务资源编辑器
 *
 * <p>用于将业务模块中资源的变更同步到权限系统。当业务资源的信息发生变化时
 * （如重命名、状态变更等），需要调用权限服务的资源更新接口，保持权限系统
 * 中的资源信息与业务系统一致。</p>
 *
 * <p><strong>更新时机：</strong></p>
 * <ul>
 *   <li>资源重命名后同步</li>
 *   <li>资源状态变更后同步</li>
 *   <li>资源额外标识变更后同步</li>
 * </ul>
 *
 * <p><strong>更新限制：</strong></p>
 * <ul>
 *   <li>资源 ID 不可修改</li>
 *   <li>资源类型（flag）不可修改</li>
 *   <li>父节点关系通过移动接口修改，不使用此接口</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * BusiResourceEditor editor = new BusiResourceEditor();
 * editor.setId(datasetId);            // 数据集ID
 * editor.setName("新的数据集名称");    // 新名称
 * editor.setFlag("dataset");          // 资源类型（必填但不变）
 * editor.setExtraFlag(1);             // 更新额外标识
 *
 * authApi.editResource(editor);       // 同步更新到权限系统
 * </pre>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "业务资源编辑器")
@Data
public class BusiResourceEditor implements Serializable {

    @Serial
    private static final long serialVersionUID = 5193320462388120821L;

    /**
     * 资源 ID
     * <p>要编辑的资源的唯一标识符，必填且不可修改。</p>
     */
    @Schema(description = "资源ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 资源名称
     * <p>资源的新名称，用于更新权限管理界面的显示。
     * 如果不修改名称，也需要传入当前名称。</p>
     */
    @Schema(description = "资源名称")
    private String name;

    /**
     * 资源类型标识
     * <p>资源的类型标识，必填但不可修改。
     * 该字段主要用于识别资源所属的业务模块。</p>
     */
    @Schema(description = "资源类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String flag;

    /**
     * 额外标识 1
     * <p>用于标记资源的特殊状态或属性的扩展字段。
     * 具体含义由业务模块定义，例如：</p>
     * <ul>
     *   <li>是否公开（0: 私有，1: 公开）</li>
     *   <li>是否启用（0: 禁用，1: 启用）</li>
     *   <li>是否锁定（0: 未锁定，1: 已锁定）</li>
     * </ul>
     */
    @Schema(description = "额外标识1")
    private int extraFlag;

    /**
     * 额外标识 2
     * <p>第二个扩展标识字段，用于支持更多的资源状态标记。
     * 具体含义由业务模块定义。</p>
     */
    @Schema(description = "额外标识2")
    private int extraFlag1;
}
