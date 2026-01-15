package io.dataease.config;

import io.dataease.constant.AuthConstant;
import io.dataease.share.interceptor.LinkInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static io.dataease.constant.StaticResourceConstants.*;
import static io.dataease.utils.StaticResourceUtils.ensureBoth;
import static io.dataease.utils.StaticResourceUtils.ensureSuffix;

/**
 * DataEase MVC配置类
 * 负责配置Spring MVC相关的静态资源处理和请求拦截器
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>配置静态资源访问路径和本地文件映射</li>
 *   <li>支持文件上传、地图数据、国际化等资源访问</li>
 *   <li>注册全局请求拦截器</li>
 *   <li>统一管理静态资源的URL路径规则</li>
 * </ul>
 *
 * <p>支持的静态资源类型：</p>
 * <ul>
 *   <li><b>上传文件：</b>用户上传的各类文件资源</li>
 *   <li><b>地图数据：</b>地理信息和地图相关文件</li>
 *   <li><b>自定义地图：</b>用户自定义的地理数据</li>
 *   <li><b>国际化资源：</b>多语言支持文件</li>
 * </ul>
 */
@Configuration
public class DeMvcConfig implements WebMvcConfigurer {

    /**
     * 链接访问拦截器
     * 用于处理分享链接和特殊访问场景的请求拦截
     */
    @Resource
    private LinkInterceptor linkInterceptor;

    /**
     * 配置静态资源访问路径
     * 为不同类型的静态资源配置URL路径和本地文件系统位置的映射关系
     *
     * <p>配置内容包括：</p>
     * <ul>
     *   <li>上传文件访问路径映射</li>
     *   <li>地图数据文件访问路径映射</li>
     *   <li>自定义地理数据访问路径映射</li>
     *   <li>国际化资源访问路径映射</li>
     * </ul>
     *
     * @param registry Spring静态资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件访问路径
        String workDir = FILE_PROTOCOL + ensureSuffix(WORK_DIR, FILE_SEPARATOR);
        String uploadUrlPattern = ensureBoth(URL_SEPARATOR + UPLOAD_URL_PREFIX, AuthConstant.DE_API_PREFIX, URL_SEPARATOR) + "**";
        registry.addResourceHandler(uploadUrlPattern).addResourceLocations(workDir);

        // 配置地图数据访问路径
        String mapDir = FILE_PROTOCOL + ensureSuffix(MAP_DIR, FILE_SEPARATOR);
        String mapUrlPattern = ensureBoth(MAP_URL, AuthConstant.DE_API_PREFIX, URL_SEPARATOR) + "**";
        registry.addResourceHandler(mapUrlPattern).addResourceLocations(mapDir);

        // 配置自定义地理数据访问路径
        String geoDir = FILE_PROTOCOL + ensureSuffix(CUSTOM_MAP_DIR, FILE_SEPARATOR);
        String geoUrlPattern = ensureBoth(GEO_URL, AuthConstant.DE_API_PREFIX, URL_SEPARATOR) + "**";
        registry.addResourceHandler(geoUrlPattern).addResourceLocations(geoDir);

        // 配置国际化资源访问路径
        String i18nDir = FILE_PROTOCOL + ensureSuffix(I18N_DIR, FILE_SEPARATOR);
        String i18nUrlPattern = ensureBoth(I18N_URL, AuthConstant.DE_API_PREFIX, URL_SEPARATOR) + "**";
        registry.addResourceHandler(i18nUrlPattern).addResourceLocations(i18nDir);
    }

    /**
     * 配置请求拦截器
     * 注册全局请求拦截器，用于处理特殊的访问场景
     *
     * <p>拦截器功能：</p>
     * <ul>
     *   <li>分享链接访问控制</li>
     *   <li>访问权限验证</li>
     *   <li>请求日志记录</li>
     * </ul>
     *
     * @param registry Spring拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册链接访问拦截器，拦截所有请求
        registry.addInterceptor(linkInterceptor).addPathPatterns("/**");
    }
}
