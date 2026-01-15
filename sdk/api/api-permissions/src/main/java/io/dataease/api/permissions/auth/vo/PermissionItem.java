package io.dataease.api.permissions.auth.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.dataease.api.permissions.dataset.dto.DataSetColumnPermissionsDTO;
import io.dataease.api.permissions.dataset.dto.DataSetRowPermissionsTreeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 权限项
 *
 * <p>表示单个资源的权限配置详情，包括基础的权限权重和针对数据集的
 * 精细化行列权限控制。权限项是权限管理的最小单元。</p>
 *
 * <p><strong>权限级别（weight）：</strong></p>
 * <ul>
 *   <li>0: 无权限</li>
 *   <li>1: 使用/读取权限（只能查看，不能修改）</li>
 *   <li>3: 编辑权限（可以修改内容）</li>
 *   <li>7: 授权/管理权限（可以管理和授权给他人）</li>
 *   <li>15: 完全控制权限</li>
 * </ul>
 *
 * <p><strong>权重计算规则：</strong></p>
 * <ul>
 *   <li>权重采用位运算设计，支持权限叠加</li>
 *   <li>高权重包含低权重的所有权限</li>
 *   <li>多个权限来源取最大权重值</li>
 * </ul>
 *
 * <p><strong>数据集精细化权限：</strong></p>
 * <ul>
 *   <li><strong>列权限：</strong>控制用户可以访问数据集的哪些字段</li>
 *   <li><strong>行权限：</strong>通过过滤条件控制用户可以访问哪些数据行</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "权限项")
@Data
public class PermissionItem implements Serializable {

    @Serial
    private static final long serialVersionUID = -6537851979745319692L;

    /**
     * 资源 ID
     * <p>被授权资源的唯一标识符，如数据集 ID、仪表板 ID 等。
     * 使用 Long 类型存储，前端序列化为字符串避免精度丢失。</p>
     */
    @JsonSerialize(using= ToStringSerializer.class)
    @Schema(description = "ID")
    private Long id;

    /**
     * 权限权重
     * <p>表示权限级别的数值，权重越高权限越大。权重采用二进制位设计，
     * 支持权限的叠加和比较。</p>
     *
     * <p>标准权重值：
     * <ul>
     *   <li>0: 无权限</li>
     *   <li>1: 使用/读取（二进制: 0001）</li>
     *   <li>3: 编辑（二进制: 0011，包含读取）</li>
     *   <li>7: 管理（二进制: 0111，包含读取和编辑）</li>
     *   <li>15: 完全控制（二进制: 1111，包含所有权限）</li>
     * </ul>
     * </p>
     */
    @Schema(description = "权重")
    private int weight;

    /**
     * 列权限配置
     * <p>针对数据集资源的列级权限控制。定义用户可以访问数据集的哪些字段，
     * 未授权的字段在查询结果中不会显示。</p>
     *
     * <p>列权限特点：
     * <ul>
     *   <li>支持白名单模式：明确指定可访问的字段</li>
     *   <li>支持黑名单模式：明确指定禁止访问的字段</li>
     *   <li>未配置时默认允许访问所有字段</li>
     * </ul>
     * </p>
     *
     * <p><strong>注意：</strong>仅对数据集类型资源有效，其他资源类型此字段为 null。</p>
     */
    @Schema(description = "列权限")
    private DataSetColumnPermissionsDTO columnPermissions;

    /**
     * 行权限配置
     * <p>针对数据集资源的行级权限控制。通过定义过滤条件限制用户可以访问的数据行，
     * 不满足条件的数据行不会出现在查询结果中。</p>
     *
     * <p>行权限配置方式：
     * <ul>
     *   <li>固定值过滤：如 region='华东'</li>
     *   <li>动态参数过滤：如 dept=${current_user_dept}</li>
     *   <li>表达式过滤：支持复杂的逻辑表达式</li>
     *   <li>多条件组合：支持 AND/OR 逻辑组合</li>
     * </ul>
     * </p>
     *
     * <p>行权限特点：
     * <ul>
     *   <li>自动注入到 SQL 查询的 WHERE 条件</li>
     *   <li>支持参数替换，实现用户级数据隔离</li>
     *   <li>多个行权限规则取并集</li>
     * </ul>
     * </p>
     *
     * <p><strong>注意：</strong>仅对数据集类型资源有效，其他资源类型此字段为 null。</p>
     */
    @Schema(description = "行权限")
    private DataSetRowPermissionsTreeDTO rowPermissions;

    /**
     * 扩展权重标识
     * <p>用于扩展的权限标识位，支持业务模块定义特殊的权限控制。
     * 具体含义由各业务模块自行定义和解释。</p>
     *
     * <p>可能的用途：
     * <ul>
     *   <li>标记特殊权限类型</li>
     *   <li>控制额外的功能开关</li>
     *   <li>实现自定义的权限逻辑</li>
     * </ul>
     * </p>
     */
    @Schema(description = "独立权重")
    private int ext;
}
