package io.dataease.api.permissions.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 业务资源移动器
 *
 * <p>用于处理资源在树形结构中的移动操作，更新资源的父子关系。
 * 移动资源时需要同步到权限系统，以便重新计算权限继承关系。</p>
 *
 * <p><strong>移动规则：</strong></p>
 * <ul>
 *   <li>不能将资源移动到自己的子节点下（避免循环引用）</li>
 *   <li>不能将叶子节点移动为其他节点的父节点</li>
 *   <li>移动到根节点时，pid 设置为 0 或 null</li>
 * </ul>
 *
 * <p><strong>权限处理：</strong></p>
 * <ul>
 *   <li>移动后重新计算权限继承</li>
 *   <li>保留资源原有的直接授权</li>
 *   <li>从新父节点继承权限</li>
 *   <li>递归处理子资源的权限</li>
 * </ul>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>将数据集移动到其他文件夹</li>
 *   <li>将文件夹移动到其他位置</li>
 *   <li>调整资源的组织结构</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * BusiResourceMover mover = new BusiResourceMover();
 * mover.setId(datasetId);          // 要移动的数据集ID
 * mover.setPid(newFolderId);       // 新的父文件夹ID
 *
 * authApi.moveResource(mover);     // 同步移动到权限系统
 * </pre>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "业务资源移动器")
@Data
public class BusiResourceMover implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 资源 ID
     * <p>要移动的资源的唯一标识符</p>
     */
    @Schema(description = "资源ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 新的父节点 ID
     * <p>目标父节点的 ID，表示要将资源移动到哪个节点下。</p>
     * <ul>
     *   <li>0 或 null: 移动到根节点</li>
     *   <li>其他值: 移动到指定父节点下</li>
     * </ul>
     *
     * <p><strong>注意：</strong></p>
     * <ul>
     *   <li>目标父节点必须存在</li>
     *   <li>目标父节点不能是叶子节点</li>
     *   <li>目标父节点不能是当前资源的子节点</li>
     * </ul>
     */
    @Schema(description = "新父节点ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pid;
}
