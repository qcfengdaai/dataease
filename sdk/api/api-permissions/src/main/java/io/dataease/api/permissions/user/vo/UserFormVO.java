package io.dataease.api.permissions.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.dataease.api.permissions.variable.dto.SysVariableValueItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户详情视图对象
 *
 * <p>用于展示用户完整详细信息的视图对象，包含用户的所有可编辑字段。
 * 主要用于用户详情页面展示和编辑表单的数据回显。</p>
 *
 * <p>包含的信息：</p>
 * <ul>
 *   <li>基本信息：ID、账号、姓名、邮箱、电话</li>
 *   <li>权限信息：角色 ID 集合、系统变量</li>
 *   <li>状态信息：启用状态、MFA 状态、用户来源</li>
 *   <li>访问信息：IP 地址、模式</li>
 * </ul>
 */
@Schema(description = "用户详情VO")
@Data
public class UserFormVO implements Serializable {

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
     * 用户的登录账号，系统内唯一
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
     * 角色 ID 集合
     * 用户拥有的所有角色的 ID 列表
     * 用于在编辑表单中回显已选角色
     */
    @Schema(description = "角色ID集合")
    private List<String> roleIds;

    /**
     * 用户邮箱
     * 用户的电子邮箱地址
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 用户状态
     * true：用户已启用，可以正常登录
     * false：用户已禁用，无法登录
     */
    @Schema(description = "状态")
    private Boolean enable;

    /**
     * 电话号码前缀
     * 国际电话区号（如 +86）
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
     * IP 地址
     * 用户最后登录的 IP 地址
     */
    @Schema(description = "IP")
    private String ip;

    /**
     * 模式
     * 用户的工作模式或访问模式
     * 具体含义由业务定义
     */
    @Schema(description = "模式")
    private String model;

    /**
     * MFA 启用状态
     * true：已启用多因素认证
     * false：未启用多因素认证
     * 默认为 false
     */
    @Schema(description = "MFA状态")
    private Boolean mfaEnable = false;

    /**
     * 用户来源
     * 标识用户的创建来源
     * 0：本地用户
     * 1：LDAP 用户
     * 2：OAuth 用户
     * 等（具体值由系统定义）
     * 默认为 0（本地用户）
     */
    @Schema(description = "用户来源")
    private Integer origin = 0;

    /**
     * 系统变量配置
     * 用户级别的系统变量值列表
     * 用于存储用户特定的配置参数
     */
    @Schema(description = "系统变量")
    private List<SysVariableValueItem> variables;
}
