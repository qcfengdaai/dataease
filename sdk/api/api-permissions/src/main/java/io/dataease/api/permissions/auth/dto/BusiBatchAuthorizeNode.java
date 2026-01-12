package io.dataease.api.permissions.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 业务批量授权节点
 *
 * <p>表示批量授权操作中的一个授权节点，包含一组资源 ID 和统一的权限标识。
 * 该对象用于批量授权请求中，将相同权限的资源分组处理。</p>
 *
 * <p><strong>设计目的：</strong></p>
 * <ul>
 *   <li>提高批量授权的灵活性，支持为不同资源组设置不同权限</li>
 *   <li>减少请求数量，一次请求可以处理多组不同权限的资源</li>
 *   <li>优化性能，按权限分组批量处理</li>
 * </ul>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>将多个资源按权限级别分组授权</li>
 *   <li>批量初始化新建资源的权限</li>
 *   <li>组织架构调整后批量更新权限</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "批量授权节点")
@Data
public class BusiBatchAuthorizeNode implements Serializable {

    @Serial
    private static final long serialVersionUID = 5804450226135199435L;

    /**
     * 资源 ID 列表
     * <p>要被授权的资源 ID 集合，这些资源将被授予相同的权限级别。</p>
     *
     * <p><strong>分组策略：</strong></p>
     * <ul>
     *   <li>相同权限级别的资源放在同一个节点</li>
     *   <li>不同权限级别的资源分别创建不同的节点</li>
     *   <li>列表不能为空</li>
     *   <li>ID 不能重复</li>
     * </ul>
     */
    @Schema(description = "资源ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> idList;

    /**
     * 权限标识
     * <p>该节点中所有资源的统一权限级别标识。具体含义由业务模块定义。</p>
     *
     * <p><strong>常见标识值：</strong></p>
     * <ul>
     *   <li>0: 无权限或默认权限</li>
     *   <li>1: 使用/读取权限</li>
     *   <li>3: 编辑权限</li>
     *   <li>7: 管理权限</li>
     *   <li>15: 完全控制权限</li>
     * </ul>
     *
     * <p><strong>注意：</strong>该标识会应用到 idList 中的所有资源。</p>
     */
    @Schema(description = "权限标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private int flag;
}
