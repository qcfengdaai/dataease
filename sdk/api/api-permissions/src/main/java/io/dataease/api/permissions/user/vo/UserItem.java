package io.dataease.api.permissions.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户概要信息
 *
 * <p>用于展示用户简要信息的轻量级对象，只包含最核心的标识信息。
 * 适用于下拉选择、快速查询等只需要用户基本标识的场景。</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>用户选择器：在表单中选择用户</li>
 *   <li>用户列表：展示简化的用户列表</li>
 *   <li>用户搜索：快速搜索用户</li>
 *   <li>关联展示：显示关联的用户信息</li>
 * </ul>
 */
@Schema(description = "用户概要")
@Data
public class UserItem implements Serializable {
    @Serial
    private static final long serialVersionUID = -3423336650739339624L;

    /**
     * 用户 ID
     * 用户的唯一标识符
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @JsonSerialize(using= ToStringSerializer.class)
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 用户姓名
     * 用户的显示名称
     */
    @Schema(description = "用户名称")
    private String name;

    /**
     * 用户账号
     * 用户的登录账号
     */
    @Schema(description = "账号")
    private String account;
}
