package io.dataease.api.permissions.role.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.dataease.api.permissions.role.dto.RoleCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色详情视图对象
 *
 * <p>用于展示角色完整详细信息的视图对象，继承自 RoleCreator，
 * 包含角色的所有基本信息和 ID 标识。</p>
 *
 * <p>继承的属性：</p>
 * <ul>
 *   <li>name：角色名称</li>
 *   <li>typeCode：角色类型代码</li>
 *   <li>desc：角色描述</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>角色详情页面展示</li>
 *   <li>角色编辑表单的数据回显</li>
 *   <li>查询单个角色的完整信息</li>
 * </ul>
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色详情VO")
@Data
public class RoleDetailVO extends RoleCreator {

    /**
     * 角色 ID
     * 角色的唯一标识符
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @Schema(description = "ID")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;
}
