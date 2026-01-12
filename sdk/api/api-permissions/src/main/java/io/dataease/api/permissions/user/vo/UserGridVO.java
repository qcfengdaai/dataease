package io.dataease.api.permissions.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户列表视图对象
 *
 * <p>用于用户列表页面展示的视图对象，包含用户的核心信息和状态。
 * 提供了用户管理所需的关键字段，适用于列表展示和筛选场景。</p>
 *
 * <p>显示信息：</p>
 * <ul>
 *   <li>基本信息：ID、账号、姓名、邮箱、电话</li>
 *   <li>角色信息：用户拥有的角色列表</li>
 *   <li>状态信息：启用状态、用户来源</li>
 *   <li>时间信息：创建时间</li>
 *   <li>系统变量：用户的系统变量配置（序列化为字符串）</li>
 * </ul>
 */
@Schema(description = "用户列表VO")
@Data
public class UserGridVO {

    /**
     * 用户 ID
     * 用户的唯一标识符
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @Schema(description = "ID")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /**
     * 用户账号
     * 用户的登录账号
     */
    @Schema(description = "账号")
    private String account;

    /**
     * 用户姓名
     * 用户的显示名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 角色列表
     * 用户拥有的所有角色信息
     * 每个角色包含 ID 和名称
     */
    @Schema(description = "角色")
    private List<UserGridRoleItem> roleItems;

    /**
     * 用户邮箱
     * 用户的电子邮箱地址
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 电话号码前缀
     * 国际电话区号
     */
    @Schema(description = "电话前缀")
    private String phonePrefix;

    /**
     * 电话号码
     * 用户的联系电话
     */
    @Schema(description = "电话")
    private String phone;

    /**
     * 用户状态
     * true：用户已启用
     * false：用户已禁用
     */
    @Schema(description = "状态")
    private Boolean enable;

    /**
     * 创建时间
     * 用户账号的创建时间戳（毫秒）
     */
    @Schema(description = "创建时间")
    private Long createTime;

    /**
     * 系统变量
     * 用户的系统变量配置，序列化为 JSON 字符串
     * 便于在列表中展示变量摘要信息
     */
    @Schema(description = "系统变量")
    private String sysVariable;

    /**
     * 用户来源
     * 标识用户的创建来源
     * 0：本地用户
     * 1：LDAP 用户
     * 2：OAuth 用户
     * 等（具体值由系统定义）
     */
    @Schema(description = "用户来源")
    private Integer origin;
}
