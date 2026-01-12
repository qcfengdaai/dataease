package io.dataease.api.permissions.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户项视图对象
 *
 * <p>用于展示用户详细信息的视图对象，比 UserItem 包含更多信息。
 * 除了基本标识外，还包含邮箱等联系方式，适用于需要用户完整信息的场景。</p>
 *
 * <p>与 UserItem 的区别：</p>
 * <ul>
 *   <li>UserItem：只包含 ID、姓名、账号（最精简）</li>
 *   <li>UserItemVO：增加了邮箱信息（更详细）</li>
 *   <li>UserFormVO：包含所有字段（最完整）</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>用户选择：在需要展示邮箱的用户选择器中使用</li>
 *   <li>成员列表：展示团队或角色的成员信息</li>
 *   <li>通知接收人：选择消息或邮件的接收人</li>
 * </ul>
 */
@Schema(description = "用户项VO")
@Data
public class UserItemVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -311077645822242697L;

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
     * 用户邮箱
     * 用户的电子邮箱地址
     * 用于联系和发送通知
     */
    @Schema(description = "邮箱")
    private String email;
}
