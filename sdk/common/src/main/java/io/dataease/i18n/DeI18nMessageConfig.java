package io.dataease.i18n;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.Locale;

/**
 * DataEase国际化消息配置类
 * 配置Spring国际化相关的Bean，包括区域解析器和消息源
 *
 * 主要功能：
 * 1. 配置区域解析器，处理HTTP请求中的语言信息
 * 2. 配置消息源，加载国际化资源文件
 * 3. 支持多个资源文件路径的配置
 *
 * 配置说明：
 * - 默认语言：中文（中国）
 * - 消息源类型：可重载资源包消息源
 * - 资源加载器：Spring应用上下文
 */
@Configuration
public class DeI18nMessageConfig {

    /**
     * 消息资源文件基础名称
     * 从配置文件中读取，支持逗号分隔的多个文件
     * 例如：i18n/messages,i18n/validation
     */
    @Value("${spring.messages.basename}")
    private String messageBaseName;

    /**
     * 配置区域解析器
     * 用于从HTTP请求头中解析客户端的语言偏好
     *
     * @return 配置好的区域解析器
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        // 设置默认语言为中文（中国）
        localeResolver.setDefaultLocale(Locale.CHINA);
        return localeResolver;
    }

    /**
     * 配置可重载资源包消息源
     * 用于加载和管理国际化资源文件
     *
     * @return 配置好的消息源
     */
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        // 使用自定义的可重载资源包消息源
        ReloadableResourceBundleMessageSource messageSource = new DeReloadableResourceBundleMessageSource();

        // 设置资源加载器为Spring应用上下文
        messageSource.setResourceLoader(new AnnotationConfigServletWebServerApplicationContext());

        // 解析配置的消息基础名称，添加classpath前缀并注册到消息源
        Arrays.stream(messageBaseName.split(","))
                .map(item -> "classpath:" + item)
                .forEach(messageSource::addBasenames);

        return messageSource;
    }
}
