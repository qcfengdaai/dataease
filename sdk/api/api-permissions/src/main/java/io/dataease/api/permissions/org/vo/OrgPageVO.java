package io.dataease.api.permissions.org.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 组织列表视图对象
 * <p>
 * 用于在前端展示组织树结构的视图对象。
 * 采用递归结构，通过 children 字段构建完整的组织树。
 * 适用于全量加载模式，一次性返回所有组织节点。
 * <p>
 * 特点：
 * <ul>
 *   <li>包含权限信息（readOnly 字段），控制前端是否允许编辑</li>
 *   <li>Long 类型 ID 序列化为字符串，避免 JavaScript 精度丢失</li>
 *   <li>递归的 children 结构，便于前端树形组件渲染</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 */
@Schema(description = "组织列表VO")
@Data
public class OrgPageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7788232223396601785L;

    /**
     * 组织 ID
     * <p>
     * 使用 ToStringSerializer 序列化为字符串，
     * 避免 JavaScript 中 Number 类型的精度问题（JS 最大安全整数为 2^53-1）
     */
    @Schema(description = "ID")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /**
     * 组织名称
     * <p>
     * 组织的显示名称，在树形结构中作为节点标签显示
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 创建时间
     * <p>
     * 组织创建的时间戳（毫秒），用于排序和展示
     */
    @Schema(description = "创建时间")
    private Long createTime;

    /**
     * 只读标识
     * <p>
     * 标识当前用户对该组织是否只有只读权限：
     * <ul>
     *   <li>true：只读权限，不能编辑或删除该组织</li>
     *   <li>false：管理权限，可以编辑或删除该组织</li>
     * </ul>
     * <p>
     * 默认值为 true，即默认为只读。
     * 前端根据此字段决定是否显示编辑、删除等操作按钮。
     */
    @Schema(description = "只读")
    private boolean readOnly = true;

    /**
     * 子组织列表
     * <p>
     * 当前组织的直接子组织集合，采用递归结构。
     * 如果没有子组织，则为 null 或空列表。
     * <p>
     * 通过递归的 children 字段，可以构建出完整的组织树结构。
     */
    @Schema(description = "子集")
    private List<OrgPageVO> children;
}
