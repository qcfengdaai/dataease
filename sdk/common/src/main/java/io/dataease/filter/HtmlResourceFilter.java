package io.dataease.filter;

import io.dataease.result.ResultMessage;
import io.dataease.utils.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * HTML资源过滤器
 * <p>
 * 用于处理HTTP请求的过滤器，主要功能包括控制HTTP缓存策略和统一异常处理。
 * 该过滤器会拦截所有HTTP请求，根据配置决定是否启用缓存，并对过滤器链中的异常进行统一处理。
 * </p>
 *
 * <p><b>主要功能：</b></p>
 * <ul>
 *   <li><b>缓存控制</b> - 根据配置动态控制HTTP缓存头信息</li>
 *   <li><b>异常处理</b> - 捕获过滤器链中的异常并返回统一的JSON错误响应</li>
 *   <li><b>执行顺序</b> - 通过Ordered接口控制过滤器执行优先级</li>
 * </ul>
 *
 * <p><b>配置说明：</b></p>
 * <ul>
 *   <li><code>dataease.http.cache=true</code> - 启用HTTP缓存</li>
 *   <li><code>dataease.http.cache=false</code> - 禁用HTTP缓存（默认）</li>
 * </ul>
 *
 * <p><b>应用场景：</b></p>
 * <ul>
 *   <li>开发环境：禁用缓存确保资源实时更新</li>
 *   <li>生产环境：启用缓存提高性能</li>
 *   <li>API接口：统一异常响应格式</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 */
@Component
public class HtmlResourceFilter implements Filter, Ordered {

    /**
     * HTTP缓存开关配置
     * <p>
     * 从application.yml配置文件中读取dataease.http.cache配置项。
     * 如果未配置，默认值为false（禁用缓存）。
     * </p>
     *
     * @see #doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    @Value("${dataease.http.cache:false}")
    private Boolean httpCache;

    /**
     * 获取过滤器执行优先级
     * <p>
     * 返回过滤器在过滤器链中的执行顺序，数值越小优先级越高。
     * 设置为99确保该过滤器在大部分其他过滤器之后执行。
     * </p>
     *
     * @return 过滤器执行优先级，值为99
     */
    @Override
    public int getOrder() {
        return 99;
    }

    /**
     * 过滤器初始化方法
     * <p>
     * 在过滤器首次被创建时由Web容器调用，用于执行初始化操作。
     * 当前实现为空，表示该过滤器不需要特殊的初始化处理。
     * </p>
     *
     * @param filterConfig 过滤器配置对象，包含初始化参数
     * @throws ServletException 初始化过程中发生的Servlet异常
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 当前不需要特殊的初始化处理
    }

    /**
     * 过滤器核心处理方法
     * <p>
     * 对每个HTTP请求执行过滤处理，主要完成以下功能：
     * 1. 根据配置设置HTTP缓存头信息
     * 2. 执行过滤器链中的后续过滤器
     * 3. 捕获并处理过滤器链中的异常
     * </p>
     *
     * <p><b>缓存控制逻辑：</b></p>
     * <ul>
     *   <li>当httpCache为null或false时，设置no-cache相关响应头</li>
     *   <li>当httpCache为true时，不设置缓存控制头，允许浏览器缓存</li>
     * </ul>
     *
     * <p><b>异常处理：</b></p>
     * <ul>
     *   <li>捕获过滤器链中的所有异常</li>
     *   <li>返回统一格式的JSON错误响应</li>
     *   <li>设置响应状态码为400 Bad Request</li>
     * </ul>
     *
     * @param servletRequest  HTTP请求对象
     * @param servletResponse HTTP响应对象
     * @param filterChain     过滤器链，用于继续执行后续过滤器
     * @throws IOException      输入输出异常
     * @throws ServletException Servlet相关异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 将ServletResponse转换为HttpServletResponse以便设置HTTP头信息
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        // 根据配置决定是否禁用HTTP缓存
        if(httpCache == null || !httpCache){
            // 禁用缓存：设置多个缓存控制头确保浏览器不缓存响应
            httpResponse.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
            httpResponse.setHeader("Cache", "no-cache");
            httpResponse.setHeader(HttpHeaders.PRAGMA, "no-cache");
            httpResponse.setHeader(HttpHeaders.EXPIRES, "0");
        }

        // 继续执行过滤器链中的后续过滤器
        try {
            filterChain.doFilter(servletRequest, httpResponse);
        } catch (Exception e) {
            // 捕获过滤器链中的异常，返回统一的JSON错误响应
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            // 构建错误响应消息并写入响应体
            ResultMessage errorMessage = new ResultMessage(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            httpResponse.getWriter().write(JsonUtil.toJSONString(errorMessage).toString());
        }
    }

    /**
     * 过滤器销毁方法
     * <p>
     * 在过滤器从服务中移除时由Web容器调用，用于清理资源。
     * 当前实现调用父类的默认销毁方法，无需额外的清理操作。
     * </p>
     */
    @Override
    public void destroy() {
        // 调用父类的默认销毁方法
        Filter.super.destroy();
    }
}
