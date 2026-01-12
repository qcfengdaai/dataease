package io.dataease.api.permissions.dataset.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 语言切换请求对象
 *
 * <p>用于前端切换系统显示语言的请求参数封装。
 * DataEase 支持多语言国际化，用户可以在界面上切换不同的显示语言。</p>
 *
 * <p>支持的语言包括：
 * <ul>
 *   <li>zh-CN: 简体中文</li>
 *   <li>zh-TW: 繁体中文</li>
 *   <li>en-US: 英语</li>
 *   <li>ja-JP: 日语</li>
 *   <li>ko-KR: 韩语</li>
 * </ul>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Data
public class LangSwitchRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -6779697711311519431L;

    /**
     * 语言代码
     *
     * <p>采用标准的 locale 格式，如 zh-CN、en-US 等。
     * 切换语言后会影响系统界面、提示信息、日期格式等的显示方式。</p>
     */
    private String lang;
}
