package io.dataease.utils;

import io.dataease.constant.AuthConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Servlet工具类
 * <p>
 * 提供便捷的HTTP请求和响应对象获取方法，以及常用请求头信息提取功能。
 * 基于Spring的RequestContextHolder实现，可在任意Service层或工具类中获取当前HTTP请求上下文。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>HttpServletRequest获取 - 在任意位置获取当前HTTP请求对象</li>
 *   <li>HttpServletResponse获取 - 在任意位置获取当前HTTP响应对象</li>
 *   <li>请求头提取 - 快速获取指定请求头信息</li>
 *   <li>认证信息提取 - 获取Token、LDAP、CAS、OIDC等认证信息</li>
 * </ul>
 *
 * <p><b>应用场景：</b></p>
 * <ul>
 *   <li>Service层获取请求信息 - 无需通过Controller层层传递Request对象</li>
 *   <li>工具类中获取请求头 - 如日志记录、权限验证等</li>
 *   <li>统一认证处理 - 提取各种认证方式的用户信息</li>
 *   <li>过滤器和拦截器 - Token验证、权限校验等</li>
 *   <li>审计日志 - 记录请求来源、用户信息等</li>
 * </ul>
 *
 * <p><b>实际使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.utils.IPUtils#get} - 获取请求对象以提取客户端IP</li>
 *   <li>{@link io.dataease.utils.TokenUtils} - Token解析和验证</li>
 *   <li>{@link io.dataease.auth.filter.TokenFilter#doFilter} - 认证过滤器中验证Token</li>
 *   <li>{@link io.dataease.auth.filter.CommunityTokenFilter#doFilter} - 社区版Token过滤</li>
 *   <li>{@link io.dataease.share.interceptor.LinkInterceptor} - 分享链接拦截器</li>
 *   <li>{@link io.dataease.share.interceptor.DeLinkAop} - 分享链接AOP切面</li>
 *   <li>{@link io.dataease.share.manage.XpackShareManage} - 分享管理</li>
 * </ul>
 *
 * <p><b>支持的认证方式：</b></p>
 * <ul>
 *   <li>Token认证 - DataEase自有Token机制</li>
 *   <li>LDAP认证 - 轻量级目录访问协议</li>
 *   <li>CAS认证 - 中央认证服务</li>
 *   <li>OIDC认证 - OpenID Connect单点登录</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：在Service层获取当前请求对象
 * {@literal @}Service
 * public class UserService {
 *     public void someMethod() {
 *         HttpServletRequest request = ServletUtils.request();
 *         if (request != null) {
 *             String userAgent = request.getHeader("User-Agent");
 *             log.info("用户浏览器: {}", userAgent);
 *         }
 *     }
 * }
 *
 * // 示例2：获取响应对象并设置响应头
 * public void exportFile() {
 *     HttpServletResponse response = ServletUtils.response();
 *     if (response != null) {
 *         response.setContentType("application/vnd.ms-excel");
 *         response.setHeader("Content-Disposition", "attachment; filename=data.xlsx");
 *     }
 * }
 *
 * // 示例3：获取自定义请求头
 * public String getCustomHeader() {
 *     String customValue = ServletUtils.getHead("X-Custom-Header");
 *     return customValue != null ? customValue : "default";
 * }
 *
 * // 示例4：Token认证（最常用场景）
 * public void validateToken() {
 *     String token = ServletUtils.getToken();
 *     if (StringUtils.isBlank(token)) {
 *         throw new UnauthorizedException("未登录");
 *     }
 *     // 验证Token有效性
 *     TokenUserBO user = TokenUtils.validateToken(token);
 * }
 *
 * // 示例5：LDAP认证
 * public void ldapAuth() {
 *     String ldapUser = ServletUtils.getLdapUser();
 *     if (StringUtils.isNotBlank(ldapUser)) {
 *         // 处理LDAP用户登录
 *         log.info("LDAP用户登录: {}", ldapUser);
 *     }
 * }
 *
 * // 示例6：CAS单点登录
 * public void casAuth() {
 *     String casUser = ServletUtils.getCasUser();
 *     if (StringUtils.isNotBlank(casUser)) {
 *         // 处理CAS用户登录
 *         log.info("CAS用户登录: {}", casUser);
 *     }
 * }
 *
 * // 示例7：OIDC认证
 * public void oidcAuth() {
 *     String oidcUser = ServletUtils.getXUserinfo();
 *     if (StringUtils.isNotBlank(oidcUser)) {
 *         // 解析OIDC用户信息（通常是JSON格式）
 *         UserInfo userInfo = JSON.parseObject(oidcUser, UserInfo.class);
 *         log.info("OIDC用户登录: {}", userInfo.getUsername());
 *     }
 * }
 *
 * // 示例8：在工具类中获取IP（实际场景）
 * // 参考 IPUtils.java:26
 * public static String getClientIp() {
 *     HttpServletRequest request = ServletUtils.request();
 *     if (request == null) {
 *         return null;
 *     }
 *     return request.getRemoteAddr();
 * }
 *
 * // 示例9：审计日志记录
 * public void auditLog(String operation) {
 *     HttpServletRequest request = ServletUtils.request();
 *     if (request != null) {
 *         String ip = IPUtils.get();
 *         String token = ServletUtils.getToken();
 *         String uri = request.getRequestURI();
 *         String method = request.getMethod();
 *
 *         AuditLog log = new AuditLog();
 *         log.setIp(ip);
 *         log.setUri(uri);
 *         log.setMethod(method);
 *         log.setOperation(operation);
 *         auditService.save(log);
 *     }
 * }
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>所有方法必须在HTTP请求线程中调用，异步线程或定时任务中无法获取</li>
 *   <li>如果不在HTTP请求上下文中，{@link #request()}和{@link #response()}返回null</li>
 *   <li>使用前建议先判空，避免NullPointerException</li>
 *   <li>异步任务如需使用请求信息，应在任务提交前提取并传递</li>
 *   <li>RequestContextHolder使用ThreadLocal实现，线程安全</li>
 *   <li>子线程无法继承父线程的请求上下文</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>优先在Controller层处理请求响应，Service层专注业务逻辑</li>
 *   <li>确需在Service层访问请求信息时，使用本工具类</li>
 *   <li>避免在Service层直接操作Response（如写响应），应返回数据由Controller处理</li>
 *   <li>异步任务应在提交前提取必要信息作为参数传递</li>
 *   <li>使用Token等认证信息建议通过方法参数传递，而非频繁调用本工具类</li>
 * </ul>
 *
 * <p><b>线程安全说明：</b></p>
 * <pre>
 * // 正确用法：HTTP请求线程
 * {@literal @}GetMapping("/api/user")
 * public Result getUser() {
 *     String token = ServletUtils.getToken();  // ✓ 可以获取到
 *     return userService.getUserInfo(token);
 * }
 *
 * // 错误用法：异步线程
 * {@literal @}Service
 * public class UserService {
 *     {@literal @}Async
 *     public void asyncTask() {
 *         String token = ServletUtils.getToken();  // ✗ 返回null
 *     }
 * }
 *
 * // 正确的异步用法：先提取再传递
 * public void processAsync() {
 *     String token = ServletUtils.getToken();  // 在HTTP线程中提取
 *     CompletableFuture.runAsync(() -> {
 *         asyncService.process(token);  // 通过参数传递
 *     });
 * }
 * </pre>
 *
 * @author DataEase
 * @since 1.0.0
 * @see org.springframework.web.context.request.RequestContextHolder
 * @see io.dataease.utils.IPUtils
 * @see io.dataease.utils.TokenUtils
 * @see io.dataease.auth.filter.TokenFilter
 */
public class ServletUtils {

    /**
     * 获取当前HTTP请求对象
     * <p>
     * 从Spring的RequestContextHolder中获取当前线程的HttpServletRequest对象。
     * 该方法可在Controller、Service、工具类等任意位置调用。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>Service层获取请求头信息</li>
     *   <li>工具类中获取请求参数</li>
     *   <li>日志记录请求URI和方法</li>
     *   <li>权限验证时获取认证信息</li>
     * </ul>
     *
     * @return 当前HTTP请求对象；如果不在HTTP请求上下文中则返回null
     */
    public static HttpServletRequest request() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjectUtils.isEmpty(servletRequestAttributes)) return null;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request;
    }

    /**
     * 获取当前HTTP响应对象
     * <p>
     * 从Spring的RequestContextHolder中获取当前线程的HttpServletResponse对象。
     * 通常用于在Service层直接操作响应，如设置响应头、写入响应内容等。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>文件下载 - 设置Content-Disposition头</li>
     *   <li>流式输出 - 直接写入响应流</li>
     *   <li>自定义响应头 - 添加缓存控制、CORS头等</li>
     *   <li>重定向 - 在Service层发起重定向</li>
     * </ul>
     *
     * <p><b>注意：</b>建议在Controller层处理响应，避免Service层直接操作Response</p>
     *
     * @return 当前HTTP响应对象；如果不在HTTP请求上下文中则返回null
     */
    public static HttpServletResponse response() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjectUtils.isEmpty(servletRequestAttributes)) return null;
        HttpServletResponse response = servletRequestAttributes.getResponse();
        return response;
    }

    /**
     * 获取指定名称的请求头
     * <p>
     * 便捷方法，从当前HTTP请求中获取指定名称的请求头值。
     * </p>
     *
     * @param key 请求头名称（如"User-Agent"、"Authorization"等）
     * @return 请求头的值；如果请求头不存在或无法获取请求对象则返回null
     */
    public static String getHead(String key) {
        HttpServletRequest request = request();
        return request.getHeader(key);
    }

    /**
     * 获取认证Token
     * <p>
     * 从HTTP请求头中提取DataEase的认证Token。
     * Token通常用于验证用户身份和权限。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>认证过滤器 - 验证用户是否已登录</li>
     *   <li>权限校验 - 从Token中解析用户信息和权限</li>
     *   <li>日志记录 - 记录操作用户</li>
     *   <li>API调用 - 服务间调用时传递认证信息</li>
     * </ul>
     *
     * @return Token字符串；如果未提供Token或无法获取请求对象则返回null
     * @see io.dataease.constant.AuthConstant#TOKEN_KEY
     * @see io.dataease.auth.filter.TokenFilter
     */
    public static String getToken() {
        return getHead(AuthConstant.TOKEN_KEY);
    }

    /**
     * 获取OIDC用户信息
     * <p>
     * 从HTTP请求头中提取OpenID Connect（OIDC）的用户信息。
     * OIDC是基于OAuth 2.0的身份认证协议，用于单点登录（SSO）。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>OIDC单点登录 - 获取第三方身份提供商返回的用户信息</li>
     *   <li>用户信息同步 - 同步OIDC用户信息到本地系统</li>
     *   <li>身份验证 - 验证OIDC用户身份</li>
     * </ul>
     *
     * @return OIDC用户信息字符串（通常是JSON格式）；
     *         如果未使用OIDC认证或无法获取请求对象则返回null
     * @see io.dataease.constant.AuthConstant#OIDC_X_USER
     */
    public static String getXUserinfo() {
        return getHead(AuthConstant.OIDC_X_USER);
    }

    /**
     * 获取LDAP用户认证信息
     * <p>
     * 从HTTP请求头中提取LDAP（轻量级目录访问协议）的认证信息。
     * LDAP常用于企业环境的统一身份认证。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>LDAP认证 - 企业目录服务认证</li>
     *   <li>Active Directory集成 - 与微软AD系统集成</li>
     *   <li>统一身份管理 - 企业统一身份认证系统</li>
     * </ul>
     *
     * @return LDAP认证信息字符串；
     *         如果未使用LDAP认证、认证信息为空或无法获取请求对象则返回null
     * @see io.dataease.constant.AuthConstant#DE_LDAP_AUTHORIZATION
     */
    public static String getLdapUser() {
        String authorization = getHead(AuthConstant.DE_LDAP_AUTHORIZATION);
        if (StringUtils.isBlank(authorization)) return null;
        return authorization;
    }

    /**
     * 获取CAS用户信息
     * <p>
     * 从HTTP请求头中提取CAS（中央认证服务）的用户信息。
     * CAS是一种企业级单点登录协议，广泛应用于高校和企业。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>CAS单点登录 - 集成CAS认证系统</li>
     *   <li>高校统一认证 - 对接教育网认证系统</li>
     *   <li>企业SSO - 企业单点登录解决方案</li>
     * </ul>
     *
     * @return CAS用户信息字符串；
     *         如果未使用CAS认证或无法获取请求对象则返回null
     * @see io.dataease.constant.AuthConstant#CAS_X_USER
     */
    public static String getCasUser() {
        return getHead(AuthConstant.CAS_X_USER);
    }

    /**
     * Apisix网关检查
     * <p>
     * 用于检查请求是否来自Apisix网关。
     * 当前实现始终返回true，表示跳过检查。
     * </p>
     *
     * <p><b>注意：</b></p>
     * <ul>
     *   <li>该方法当前未实现实际检查逻辑</li>
     *   <li>如需实际验证，应检查特定的网关请求头</li>
     *   <li>可用于网关鉴权、请求来源验证等场景</li>
     * </ul>
     *
     * @return 始终返回true
     */
    public static boolean apisixCheck() {
        return true;
    }


}
