package io.dataease.api.permissions.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 语言切换请求
 *
 * <p>用于切换用户界面显示语言的请求对象。
 * 系统支持多语言国际化，用户可以选择自己熟悉的语言。</p>
 *
 * <p>支持的语言：</p>
 * <ul>
 *   <li>zh_CN：简体中文</li>
 *   <li>zh_TW：繁体中文</li>
 *   <li>en_US：英语</li>
 *   <li>其他语言根据系统配置而定</li>
 * </ul>
 *
 * <p>切换效果：</p>
 * <ul>
 *   <li>界面文字：菜单、按钮、提示信息等将使用新语言显示</li>
 *   <li>数据内容：不影响用户输入的数据内容</li>
 *   <li>持久化：语言偏好会保存到用户配置中</li>
 * </ul>
 */
@Schema(description = "语言切换器")
@Data
public class LangSwitchRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -6779697711311519431L;

    /**
     * 目标语言代码
     * 必填项，要切换到的语言标识
     * 使用标准的语言代码格式，如：zh_CN、en_US
     */
    @Schema(description = "目标语言", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lang;
}
