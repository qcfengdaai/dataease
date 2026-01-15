package io.dataease.api.permissions.role.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 组织外用户视图对象
 *
 * <p>用于展示组织外用户信息的视图对象。组织外用户是指不属于当前组织
 * 但需要访问组织资源的用户，例如跨组织协作的用户或外部合作伙伴。</p>
 *
 * <p>包含的信息：</p>
 * <ul>
 *   <li>基本标识：用户 ID 和账号</li>
 *   <li>显示信息：用户名称</li>
 *   <li>联系方式：邮箱和电话</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>搜索组织外用户：在绑定组织外用户时进行用户选择</li>
 *   <li>展示外部用户：在角色成员列表中标识组织外用户</li>
 *   <li>用户信息展示：显示外部用户的详细联系信息</li>
 * </ul>
 */
@Schema(description = "组织外用户VO")
@Data
public class ExternalUserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5244308239452360019L;

    /**
     * 用户 ID
     * 用户的唯一标识符
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @Schema(description = "用户ID")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long uid;

    /**
     * 用户账号
     * 用户的登录账号，全系统唯一
     */
    @Schema(description = "用户账号")
    private String account;

    /**
     * 用户名称
     * 用户的显示名称，用于界面展示
     */
    @Schema(description = "用户名称")
    private String name;

    /**
     * 用户邮箱
     * 用户的电子邮箱地址，用于联系和通知
     */
    @Schema(description = "用户邮箱")
    private String email;

    /**
     * 用户电话
     * 用户的联系电话号码
     */
    @Schema(description = "用户电话")
    private String phone;
}
