package io.dataease.dds.interceptor;

import io.dataease.dds.DynamicContextHolder;
import io.dataease.dds.constant.DataSourceConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 数据源切换拦截器
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>支持多租户环境下的自动数据源切换</li>
 *   <li>基于HTTP请求头中的租户信息动态选择数据源</li>
 *   <li>通过nginx代理设置租户ID和创建时间到请求头</li>
 *   <li>根据租户信息生成对应的数据源名称并切换</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>不同租户使用不同的二级域名访问系统</li>
 *   <li>nginx负载均衡器根据域名设置对应的租户信息到请求头</li>
 *   <li>系统根据请求头自动切换到租户专属数据源</li>
 * </ul>
 */
public class DSInterceptor implements HandlerInterceptor {

    /**
     * 请求预处理方法
     * 在请求到达Controller之前执行，负责解析租户信息并切换数据源
     *
     * @param request HTTP请求对象，包含租户信息的请求头
     * @param response HTTP响应对象
     * @param handler 处理器对象
     * @return 始终返回true，表示继续执行后续处理
     * @throws Exception 处理异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取租户ID
        String tenant = request.getHeader("tenant");
        // 从请求头获取租户创建时间
        String time = request.getHeader("time");
        // 根据租户信息生成数据源键值并设置到上下文
        DynamicContextHolder.push(getTenantDsKey(tenant, time));
        return true;
    }

    /**
     * 生成租户数据源键值
     * 根据租户ID和创建时间生成对应的数据源名称标识
     *
     * @param tenant 租户ID，从请求头中获取
     * @param time 租户创建时间，从请求头中获取
     * @return 数据源键值，格式为"tenant_{租户ID}_{创建时间}"；如果租户信息缺失则返回默认数据源
     */
    private String getTenantDsKey(String tenant, String time) {
        // 如果租户ID或创建时间为空，使用官方默认数据源
        if (null == tenant || null == time) return "official-ds";
        // 使用预定义的格式生成租户数据源名称
        return String.format(DataSourceConstant.DS_NAME_PREFIX, tenant, time);
    }
}
