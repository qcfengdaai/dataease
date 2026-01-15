package io.dataease.share.interceptor;

import io.dataease.auth.DeLinkPermit;
import io.dataease.constant.AuthConstant;
import io.dataease.exception.DEException;
import io.dataease.utils.ServletUtils;
import io.dataease.utils.WhitelistUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;


/**
 * 分享链接拦截器
 * <p>
 * 用于拦截带有分享链接Token的请求，验证Token是否支持访问当前URL
 * <p>
 * 主要功能：
 * <ul>
 * <li>从请求头中提取LinkToken</li>
 * <li>验证目标方法是否有@DeLinkPermit注解</li>
 * <li>如果无注解，检查URL是否在白名单中</li>
 * <li>不在白名单则抛出异常</li>
 * </ul>
 *
 * @author DataEase
 * @since 2024-06-21
 */
@Component
public class LinkInterceptor implements HandlerInterceptor {

    /**
     * 白名单URL列表（精确匹配）
     * 这些URL即使在分享链接模式下也可以访问
     */
    private final static String whiteListText = "/user/ipInfo, /apisix/check, /datasetData/enumValue, /datasetData/enumValueObj, /datasetData/getFieldTree, /dekey, /symmetricKey, /share/validate, /sysParameter/queryOnlineMap, /xpackComponent/viewPlugins";

    /**
     * 白名单URL前缀列表（前缀匹配）
     * 以这些前缀开头的URL在分享链接模式下可以访问
     */
    private final static String whiteStartListText = "/dataVisualization/findDvType/";

    /**
     * 检查URL是否以白名单前缀开头
     *
     * @param url 待检查的URL
     * @return 如果URL以任一白名单前缀开头，返回true；否则返回false
     */
    private boolean isWhiteStart(String url) {
        List<String> whiteStartList = Arrays.stream(StringUtils.split(whiteStartListText, ",")).map(String::trim).toList();
        return whiteStartList.stream().anyMatch(item -> StringUtils.startsWith(url, item));
    }

    /**
     * 在处理请求之前进行拦截
     * <p>
     * 拦截逻辑：
     * <ol>
     * <li>检查请求头中是否包含LinkToken</li>
     * <li>如果没有Token，直接放行</li>
     * <li>如果有Token，检查目标方法是否有@DeLinkPermit注解</li>
     * <li>如果没有注解，验证URL是否在白名单中</li>
     * <li>不在白名单则抛出异常</li>
     * </ol>
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  处理器（目标方法）
     * @return 如果验证通过或无需验证，返回true；否则返回false
     * @throws Exception 如果验证失败，抛出异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取LinkToken
        String linkToken = ServletUtils.getHead(AuthConstant.LINK_TOKEN_KEY);
        // 如果没有Token，直接放行
        if (linkToken == null) {
            return true;
        }
        // 检查处理器是否为HandlerMethod（即是否为Controller方法）
        if (handler instanceof HandlerMethod handlerMethod) {
            // 获取方法上的@DeLinkPermit注解
            DeLinkPermit deLinkPermit = handlerMethod.getMethodAnnotation(DeLinkPermit.class);
            // 如果没有注解，需要进行白名单验证
            if (deLinkPermit == null) {
                // 解析白名单列表
                List<String> whiteList = Arrays.stream(StringUtils.split(whiteListText, ",")).map(String::trim).toList();

                // 获取请求URI
                String requestURI = ServletUtils.request().getRequestURI();
                // 移除上下文路径前缀
                if (StringUtils.startsWith(requestURI, WhitelistUtils.getContextPath())) {
                    requestURI = requestURI.replaceFirst(WhitelistUtils.getContextPath(), "");
                }
                // 移除API前缀
                if (StringUtils.startsWith(requestURI, AuthConstant.DE_API_PREFIX)) {
                    requestURI = requestURI.replaceFirst(AuthConstant.DE_API_PREFIX, "");
                }
                // 检查URL是否在白名单中（精确匹配、前缀匹配或全局白名单）
                boolean valid = whiteList.contains(requestURI) || isWhiteStart(requestURI) || WhitelistUtils.match(requestURI);
                // 如果不在白名单中，抛出异常
                if (!valid) {
                    DEException.throwException("分享链接Token不支持访问当前url[" + requestURI + "]");
                }
                return true;
            }
        }
        return true;
    }


}
