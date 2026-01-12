package io.dataease.auth.interceptor;

import io.dataease.constant.AuthConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * CORS跨域配置类
 * 负责配置跨域资源共享(CORS)策略和API路径前缀
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>跨域策略配置 - 支持严格模式和宽松模式的跨域控制</li>
 *   <li>API路径前缀 - 为所有RestController添加统一的API前缀</li>
 *   <li>动态域名管理 - 支持运行时添加允许的跨域源</li>
 *   <li>安全策略控制 - 根据配置决定是否启用严格的跨域检查</li>
 * </ul>
 *
 * <p>配置模式：</p>
 * <ul>
 *   <li><strong>严格模式</strong> - 只允许配置的域名进行跨域访问</li>
 *   <li><strong>宽松模式</strong> - 允许所有域名进行跨域访问</li>
 * </ul>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * CORS严格模式开关
     * true: 只允许配置的域名列表进行跨域访问
     * false: 允许所有域名进行跨域访问（开发环境推荐）
     */
    @Value("${dataease.cors-strict:false}")
    private boolean corsStrict;

    /**
     * 允许跨域访问的源列表
     * 逗号分隔的域名列表，默认包含本地开发地址
     * 示例：http://127.0.0.1:8100,https://dataease.io
     */
    @Value("#{'${dataease.origin-list:http://127.0.0.1:8100}'.split(',')}")
    private List<String> originList;

    /**
     * CORS注册对象
     * 用于动态修改跨域配置，支持运行时添加新的允许源
     */
    private CorsRegistration operateCorsRegistration;

    /**
     * 配置API路径匹配规则
     *
     * <p>功能说明：</p>
     * <ul>
     *   <li>为所有DataEase的RestController添加API前缀</li>
     *   <li>只影响带有@RestController注解且包名以io.dataease开头的类</li>
     *   <li>统一API路径管理，便于版本控制和路由规则</li>
     * </ul>
     *
     * @param configurer 路径匹配配置器
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 为DataEase的所有RestController添加API前缀
        configurer.addPathPrefix(
            AuthConstant.DE_API_PREFIX,
            c -> c.isAnnotationPresent(RestController.class) && c.getPackageName().startsWith("io.dataease")
        );
    }

    /**
     * 配置CORS跨域映射规则
     *
     * <p>配置项说明：</p>
     * <ul>
     *   <li>映射路径：/**（所有路径）</li>
     *   <li>允许凭证：false（不发送Cookie等凭证信息）</li>
     *   <li>允许请求头：*（所有请求头）</li>
     *   <li>预检缓存：3600秒</li>
     *   <li>允许方法：GET、POST、DELETE</li>
     * </ul>
     *
     * <p>跨域源控制：</p>
     * <ul>
     *   <li>严格模式：只允许配置的域名列表</li>
     *   <li>宽松模式：允许所有域名（*）</li>
     * </ul>
     *
     * @param registry CORS注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置基本的CORS规则
        operateCorsRegistration = registry.addMapping("/**")
                .allowCredentials(false)           // 不允许发送凭证信息
                .allowedHeaders("*")               // 允许所有请求头
                .maxAge(3600)                      // 预检请求缓存时间3600秒
                .allowedMethods("GET", "POST", "DELETE");  // 允许的HTTP方法

        // 根据严格模式决定允许的跨域源
        if (corsStrict) {
            // 严格模式：只允许配置的域名列表
            operateCorsRegistration.allowedOrigins(originList.toArray(new String[0]));
            return;
        }
        // 宽松模式：允许所有域名
        operateCorsRegistration.allowedOrigins("*");
    }

    /**
     * 动态添加允许的跨域源
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>运行时动态添加新的允许域名</li>
     *   <li>插件或扩展模块需要添加特定域名</li>
     *   <li>多租户环境下的域名管理</li>
     * </ul>
     *
     * <p>注意事项：</p>
     * <ul>
     *   <li>只在严格模式下生效</li>
     *   <li>会自动去重，避免重复配置</li>
     *   <li>与原有配置的域名列表合并</li>
     * </ul>
     *
     * @param origins 要添加的跨域源列表
     */
    public void addAllowedOrigins(List<String> origins) {
        // 非严格模式或空列表时不处理
        if (!corsStrict || CollectionUtils.isEmpty(origins)) {
            return;
        }

        // 合并原有域名列表和新增域名列表
        origins.addAll(originList);
        // 去重处理
        List<String> newOrigins = origins.stream().distinct().toList();
        String[] originArray = newOrigins.toArray(new String[0]);

        // 更新CORS配置
        operateCorsRegistration.allowedOrigins(originArray);
    }
}
