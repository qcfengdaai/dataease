package io.dataease.i18n;

import io.dataease.utils.CacheUtils;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import static io.dataease.constant.CacheConstant.UserCacheConstant.USER_COMMUNITY_LANGUAGE;

/**
 * 语言枚举类
 * 定义DataEase系统支持的所有语言类型
 *
 * 支持的语言：
 * - 简体中文 (zh-CN)
 * - 繁体中文 (zh-TW)
 * - 英语 (en-US)
 */
@Getter
public enum Lang {

    /**
     * 简体中文
     */
    zh_CN("zh-CN"),

    /**
     * 繁体中文
     */
    zh_TW("zh-TW"),

    /**
     * 英语
     */
    en_US("en-US");

    /**
     * 语言标识描述
     */
    private final String desc;

    /**
     * 构造函数
     * @param desc 语言标识描述
     */
    Lang(String desc) {
        this.desc = desc;
    }

    /**
     * 根据语言标识获取语言枚举
     * 如果找不到匹配的语言，则返回默认语言（简体中文）
     *
     * @param lang 语言标识字符串
     * @return 对应的语言枚举，默认返回 zh_CN
     */
    public static Lang getLang(String lang) {
        // 先尝试获取匹配的语言
        Lang result = getLangWithoutDefault(lang);
        // 如果没有找到匹配的语言，使用默认语言（简体中文）
        if (result == null) {
            result = zh_CN;
        }
        return result;
    }

    /**
     * 根据语言标识获取语言枚举（不使用默认值）
     * 支持精确匹配和模糊匹配
     *
     * @param lang 语言标识字符串
     * @return 对应的语言枚举，找不到时返回 null
     */
    public static Lang getLangWithoutDefault(String lang) {
        if (StringUtils.isBlank(lang)) {
            return null;
        }

        // 精确匹配预定义的语言
        for (Lang lang1 : values()) {
            if (StringUtils.equalsIgnoreCase(lang1.getDesc(), lang)) {
                return lang1;
            }
        }

        // 模糊匹配中文简体
        if (StringUtils.startsWithIgnoreCase(lang, "zh-CN")) {
            return zh_CN;
        }

        // 模糊匹配中文繁体（支持香港和台湾）
        if (StringUtils.startsWithIgnoreCase(lang, "zh-HK") || StringUtils.startsWithIgnoreCase(lang, "zh-TW")) {
            return zh_TW;
        }

        // 模糊匹配英语
        if (StringUtils.startsWithIgnoreCase(lang, "en")) {
            return en_US;
        }

        return null;
    }

    /**
     * 判断当前用户是否使用中文语言环境
     * 从缓存中获取用户的语言设置进行判断
     *
     * @return true 表示使用中文，false 表示使用其他语言
     */
    public static boolean isChinese() {
        String lang = null;
        // 从缓存中获取用户的语言设置
        Object langObj = CacheUtils.get(USER_COMMUNITY_LANGUAGE, "de");
        if (ObjectUtils.isNotEmpty(langObj) && StringUtils.isNotBlank(langObj.toString())) {
            lang = langObj.toString();
        }

        // 如果没有设置语言，默认认为是中文
        if (StringUtils.isBlank(lang)) {
            return true;
        }

        // 判断是否以"zh"开头（包含所有中文变体）
        if (StringUtils.startsWithIgnoreCase(lang, "zh")) {
            return true;
        }
        return false;
    }

}
