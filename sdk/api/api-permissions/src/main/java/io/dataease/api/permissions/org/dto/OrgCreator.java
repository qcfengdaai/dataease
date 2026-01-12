package io.dataease.api.permissions.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 组织创建器
 * <p>
 * 用于创建新组织单位的数据传输对象。
 * 包含创建组织所需的基本信息：组织名称和父组织 ID。
 * <p>
 * 使用场景：
 * <ul>
 *   <li>在组织树中添加新的组织节点</li>
 *   <li>支持创建根级组织（pid 为 null 或 0）</li>
 *   <li>支持在指定父组织下创建子组织</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 */
@Schema(description = "组织构造器")
@Data
public class OrgCreator implements Serializable {

    @Serial
    private static final long serialVersionUID = -4246980891732805368L;

    /**
     * 组织 ID
     * <p>
     * 创建时通常为 null，由系统自动生成。
     * 某些特殊场景下可能需要指定 ID（如数据迁移）。
     */
    private Long id;

    /**
     * 组织名称
     * <p>
     * 组织单位的显示名称，用于在组织树中展示。
     * 必填项，不能为空。
     * <p>
     * 示例：研发部、销售部、华东大区等
     */
    @Schema(description = "组织名称")
    private String name;

    /**
     * 父组织 ID
     * <p>
     * 指定新组织的上级组织。
     * <ul>
     *   <li>如果为 null 或 0，表示创建根级组织</li>
     *   <li>如果指定具体值，表示在该组织下创建子组织</li>
     * </ul>
     * <p>
     * 父组织 ID 必须是已存在的组织 ID，否则创建会失败。
     */
    @Schema(description = "上级ID")
    private Long pid;
}
