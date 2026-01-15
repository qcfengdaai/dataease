package io.dataease.api.permissions.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 权限值视图对象
 *
 * <p>表示用户对资源的权限值，包括基础权重和扩展权重。
 * 这是一个轻量级对象，仅包含权限的数值信息，不包含资源和用户的详细信息。</p>
 *
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>快速权限查询：仅需要权限值而不需要详细配置</li>
 *   <li>权限比较：比较不同用户的权限大小</li>
 *   <li>权限计算：计算最终生效的权限值</li>
 *   <li>性能优化：减少不必要的数据传输</li>
 * </ul>
 *
 * <p><strong>与 PermissionItem 的区别：</strong></p>
 * <ul>
 *   <li>PermissionItem: 完整的权限项，包含资源 ID、行列权限等详细配置</li>
 *   <li>PermissionValVO: 仅包含权限值，用于快速查询和比较</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "权限值视图")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PermissionValVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6677497144947905694L;

    /**
     * 权限权重
     * <p>表示权限级别的数值，权重越高权限越大。</p>
     *
     * <p>标准权重值：
     * <ul>
     *   <li>0: 无权限</li>
     *   <li>1: 使用/读取权限</li>
     *   <li>3: 编辑权限</li>
     *   <li>7: 管理权限</li>
     *   <li>15: 完全控制权限</li>
     * </ul>
     * </p>
     *
     * <p>权重采用二进制位设计，支持位运算进行权限判断：
     * <ul>
     *   <li>读取权限: (weight & 1) != 0</li>
     *   <li>编辑权限: (weight & 2) != 0</li>
     *   <li>管理权限: (weight & 4) != 0</li>
     *   <li>控制权限: (weight & 8) != 0</li>
     * </ul>
     * </p>
     */
    @Schema(description = "权限权重")
    private int weight;

    /**
     * 扩展权重标识
     * <p>用于扩展的权限标识位，支持业务模块定义特殊的权限控制。
     * 具体含义由各业务模块自行定义和解释。</p>
     *
     * <p>可能的用途：
     * <ul>
     *   <li>标记特殊权限类型（如临时权限、审批权限等）</li>
     *   <li>控制额外的功能开关</li>
     *   <li>实现自定义的权限逻辑</li>
     *   <li>支持业务特定的权限需求</li>
     * </ul>
     * </p>
     */
    @Schema(description = "扩展权重")
    private int ext;
}
