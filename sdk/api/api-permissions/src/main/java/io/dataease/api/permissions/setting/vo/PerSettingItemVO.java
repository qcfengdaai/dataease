package io.dataease.api.permissions.setting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 权限设置项视图对象
 * <p>
 * 用于表示单个权限相关的配置项。
 * 每个配置项包含键、值、类型和排序信息。
 * <p>
 * 使用场景：
 * <ul>
 *   <li>基础认证配置项的展示和编辑</li>
 *   <li>MFA 配置项的展示和编辑</li>
 *   <li>系统安全策略的配置管理</li>
 * </ul>
 * <p>
 * 配置项示例：
 * <ul>
 *   <li>pkey="password.min.length", pval="8", type="number"</li>
 *   <li>pkey="session.timeout", pval="30", type="number"</li>
 *   <li>pkey="mfa.enabled", pval="true", type="boolean"</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 */
@Schema(description = "设置项VO")
@Data
@NoArgsConstructor
public class PerSettingItemVO implements Serializable {

    /**
     * 配置键
     * <p>
     * 配置项的唯一标识符，通常采用点号分隔的命名方式。
     * <p>
     * 命名规范示例：
     * <ul>
     *   <li>password.min.length：密码最小长度</li>
     *   <li>password.complexity：密码复杂度要求</li>
     *   <li>session.timeout：会话超时时间（分钟）</li>
     *   <li>login.max.fail.times：登录最大失败次数</li>
     *   <li>mfa.enabled：是否启用 MFA</li>
     *   <li>mfa.force：是否强制使用 MFA</li>
     * </ul>
     */
    @Schema(description = "key")
    private String pkey;

    /**
     * 配置值
     * <p>
     * 配置项的值，以字符串形式存储。
     * 实际使用时根据 type 字段转换为对应的数据类型。
     * <p>
     * 值的格式取决于配置类型：
     * <ul>
     *   <li>number 类型："8"、"30"、"5" 等数字字符串</li>
     *   <li>boolean 类型："true" 或 "false"</li>
     *   <li>string 类型：任意文本内容</li>
     *   <li>enum 类型：枚举值之一，如 "SMS"、"EMAIL"、"TOTP"</li>
     * </ul>
     */
    @Schema(description = "value")
    private String pval;

    /**
     * 配置类型
     * <p>
     * 标识配置值的数据类型，用于前端渲染相应的输入组件和进行数据验证。
     * <p>
     * 支持的类型：
     * <ul>
     *   <li>number：数字类型，前端显示为数字输入框</li>
     *   <li>boolean：布尔类型，前端显示为开关或复选框</li>
     *   <li>string：字符串类型，前端显示为文本输入框</li>
     *   <li>enum：枚举类型，前端显示为下拉选择框</li>
     *   <li>password：密码类型，前端显示为密码输入框</li>
     * </ul>
     */
    @Schema(description = "类型")
    private String type;

    /**
     * 排序序号
     * <p>
     * 用于控制配置项在界面中的显示顺序。
     * 数值越小，显示位置越靠前。
     * <p>
     * 通常将重要的、常用的配置项排在前面，
     * 将高级选项或不常用的配置项排在后面。
     */
    @Schema(description = "顺序")
    private Integer sort;
}
