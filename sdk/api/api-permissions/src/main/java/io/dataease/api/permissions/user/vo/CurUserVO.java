package io.dataease.api.permissions.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 当前登录用户信息视图对象
 *
 * <p>用于展示当前登录用户的基本信息。
 * 前端通过此对象获取当前用户的身份信息，用于页面展示和权限控制。</p>
 *
 * <p>包含信息：</p>
 * <ul>
 *   <li>用户身份：用户 ID 和姓名</li>
 *   <li>组织信息：当前工作组织 ID</li>
 *   <li>偏好设置：界面显示语言</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>顶部导航栏：显示当前登录用户姓名</li>
 *   <li>个人中心：展示用户基本信息</li>
 *   <li>权限判断：根据用户 ID 进行权限控制</li>
 *   <li>国际化：根据语言设置显示对应语言</li>
 * </ul>
 */
@Schema(description = "当前登录人信息VO")
@Data
public class CurUserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1190164294672439979L;

    /**
     * 用户 ID
     * 当前登录用户的唯一标识符
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @JsonSerialize(using= ToStringSerializer.class)
    @Schema(description = "ID")
    private Long id;

    /**
     * 用户姓名
     * 当前登录用户的显示名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 组织 ID
     * 用户当前所在的工作组织 ID
     * 用户可能属于多个组织，此字段表示当前选中的组织
     * 使用 ToStringSerializer 序列化为字符串，避免前端 Long 类型精度丢失
     */
    @JsonSerialize(using= ToStringSerializer.class)
    @Schema(description = "组织ID")
    private Long oid;

    /**
     * 界面语言
     * 用户选择的界面显示语言
     * 如：zh_CN（简体中文）、en_US（英语）等
     */
    @Schema(description = "语言")
    private String language;
}
