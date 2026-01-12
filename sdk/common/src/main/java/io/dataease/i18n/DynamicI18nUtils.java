package io.dataease.i18n;

import jakarta.annotation.Resource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

/**
 * 动态国际化工具类
 * 提供动态添加和更新国际化资源文件的功能
 *
 * 主要功能：
 * 1. 动态添加新的国际化资源文件路径
 * 2. 刷新国际化缓存，使新资源立即生效
 * 3. 支持运行时热更新国际化配置
 *
 * 使用场景：
 * - 应用启动时加载自定义国际化文件
 * - 插件化系统的国际化资源动态加载
 * - 多租户系统的个性化国际化配置
 */
@Component
public class DynamicI18nUtils {

    /**
     * 可重载资源束消息源
     * 用于动态管理国际化资源文件
     */
    private static ReloadableResourceBundleMessageSource messageSource;

    /**
     * 设置消息源
     * 通过Spring依赖注入获取消息源实例
     *
     * @param messageSource 可重载资源束消息源
     */
    @Resource
    public void setMessageSource(ReloadableResourceBundleMessageSource messageSource) {
        DynamicI18nUtils.messageSource = messageSource;
    }

    /**
     * 添加或更新国际化资源文件
     * 动态添加新的国际化资源路径并刷新缓存
     *
     * @param baseName 资源文件基础名称，支持file:、classpath:等协议
     *                 例如：file:/opt/dataease/i18n/custom
     *                      classpath:i18n/messages
     */
    public static void addOrUpdate(String baseName) {
        // 添加新的资源文件路径到消息源
        messageSource.addBasenames(baseName);

        // 设置缓存秒数为0，禁用缓存以便立即生效
        messageSource.setCacheSeconds(0);

        // 清空现有缓存，强制重新加载资源
        messageSource.clearCache();
    }
}
