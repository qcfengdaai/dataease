package io.dataease.auth.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器配置类
 * 负责注册和配置系统中使用的各种过滤器
 *
 * <p>配置的过滤器：</p>
 * <ul>
 *   <li>TokenFilter - Token认证过滤器，优先级0（最高）</li>
 *   <li>CommunityTokenFilter - 社区版Token过滤器，优先级5</li>
 * </ul>
 */
@Configuration
public class FilterConfig {

    /**
     * 配置Token认证过滤器
     *
     * <p>功能说明：</p>
     * <ul>
     *   <li>处理所有URL路径（/*）</li>
     *   <li>执行顺序为0（最高优先级）</li>
     *   <li>负责用户身份认证和Token验证</li>
     * </ul>
     *
     * @return Token过滤器的注册Bean
     */
    @Bean
    public FilterRegistrationBean orderFilter() {
        FilterRegistrationBean filter = new FilterRegistrationBean<>();
        // 设置过滤器名称
        filter.setName("tokenFilter");
        // 设置过滤器实例
        filter.setFilter(new TokenFilter());
        // 设置拦截所有URL路径
        filter.addUrlPatterns("/*");
        // 设置最高优先级，确保在其他过滤器之前执行
        filter.setOrder(0);
        return filter;
    }

    /**
     * 配置社区版Token过滤器
     *
     * <p>功能说明：</p>
     * <ul>
     *   <li>处理所有URL路径（/*）</li>
     *   <li>执行顺序为5（在TokenFilter之后）</li>
     *   <li>提供社区版特定的Token处理逻辑</li>
     * </ul>
     *
     * @return 社区版过滤器的注册Bean
     */
    @Bean
    public FilterRegistrationBean communityFilter() {
        FilterRegistrationBean filter = new FilterRegistrationBean<>();
        // 设置过滤器名称
        filter.setName("communityTokenFilter");
        // 设置过滤器实例
        filter.setFilter(new CommunityTokenFilter());
        // 设置拦截所有URL路径
        filter.addUrlPatterns("/*");
        // 设置较低优先级，在TokenFilter之后执行
        filter.setOrder(5);
        return filter;
    }
}
