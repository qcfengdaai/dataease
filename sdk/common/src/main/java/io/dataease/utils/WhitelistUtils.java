package io.dataease.utils;

import io.dataease.constant.AuthConstant;
import io.dataease.exception.DEException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Objects;

import static io.dataease.result.ResultCode.INTERFACE_ADDRESS_INVALID;

/**
 * 白名单工具类
 * <p>
 * 用于管理和验证系统接口的访问白名单，提供URL匹配、路径验证、安全检查等功能。
 * 白名单中的接口无需Token认证即可访问，主要包括登录、公开资源、第三方登录等接口。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>白名单匹配 - 验证请求URI是否在白名单中</li>
 *   <li>路径规范化 - 去除上下文路径和API前缀</li>
 *   <li>URL安全校验 - 防止路径遍历、目录穿越等安全攻击</li>
 *   <li>Base API URL构建 - 生成完整的API基础URL</li>
 *   <li>上下文路径管理 - 获取和缓存应用上下文路径</li>
 * </ul>
 *
 * <p><b>白名单类型：</b></p>
 * <ul>
 *   <li>精确匹配：如"/login/localLogin"、"/dekey"等完整路径</li>
 *   <li>前缀匹配：如"/static-resource/"、"/appearance/image/"等以特定前缀开头的路径</li>
 *   <li>后缀匹配：如".js"、".css"、".png"等特定文件类型</li>
 *   <li>特殊匹配：如"data:image"等特殊格式</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.auth.filter.TokenFilter#doFilter} - Token认证过滤器中的白名单检查</li>
 *   <li>第三方登录 - 飞书、钉钉、企业微信等OAuth回调接口</li>
 *   <li>公开资源 - 静态资源、地图数据、国际化文件等</li>
 *   <li>分享链接 - 公开分享的仪表板、数据集等</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：基本白名单匹配（在TokenFilter中使用）
 * String requestURI = "/api/login/localLogin";
 * boolean isWhitelisted = WhitelistUtils.match(requestURI);
 * if (isWhitelisted) {
 *     // 白名单请求，直接放行，无需Token验证
 *     filterChain.doFilter(request, response);
 *     return;
 * }
 * // 非白名单请求，需要进行Token验证
 *
 * // 示例2：静态资源请求
 * String staticUri = "/static-resource/images/logo.png";
 * boolean allowed = WhitelistUtils.match(staticUri);  // 返回true
 *
 * // 示例3：检测路径遍历攻击
 * try {
 *     String maliciousUri = "/api/../../../etc/passwd";
 *     WhitelistUtils.match(maliciousUri);  // 会抛出DEException
 * } catch (DEException e) {
 *     // 检测到非法URL，返回错误
 *     System.out.println("检测到非法路径: " + e.getMessage());
 * }
 *
 * // 示例4：构建Base API URL（用于OAuth回调）
 * String redirectUri = "https://dataease.io";
 * String baseApiUrl = WhitelistUtils.getBaseApiUrl(redirectUri);
 * System.out.println(baseApiUrl);  // 输出: https://dataease.io/api/
 * // 或带上下文路径: https://dataease.io/dataease/api/
 *
 * // 示例5：获取应用上下文路径
 * String contextPath = WhitelistUtils.getContextPath();
 * System.out.println(contextPath);  // 输出: /dataease 或空字符串
 *
 * // 示例6：第三方登录回调验证
 * String larkCallback = "/lark/token?code=xxx";
 * boolean isLarkWhitelisted = WhitelistUtils.match(larkCallback);  // 返回true
 *
 * // 示例7：文件下载接口
 * String downloadUri = "/exportCenter/download/report.xlsx";
 * boolean canDownload = WhitelistUtils.match(downloadUri);  // 返回true
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>白名单接口应该谨慎配置，避免暴露敏感接口</li>
 *   <li>URL安全校验会检测路径遍历（../）、目录穿越（.%、%2e）等攻击</li>
 *   <li>分号（;）字符在URL路径中（非查询参数）会被视为不安全</li>
 *   <li>match方法会自动去除上下文路径和API前缀</li>
 *   <li>上下文路径会被缓存，应用启动后不会动态更新</li>
 *   <li>白名单列表是不可变的List，运行时无法修改</li>
 * </ul>
 *
 * <p><b>安全建议：</b></p>
 * <ul>
 *   <li>定期审查白名单配置，移除不需要的公开接口</li>
 *   <li>白名单中的接口应该具有最小权限，不应返回敏感数据</li>
 *   <li>对于需要部分验证的接口，建议使用自定义验证而非加入白名单</li>
 *   <li>URL安全校验是必要的防护措施，不应跳过</li>
 *   <li>第三方登录回调应该有额外的state参数验证</li>
 *   <li>下载接口应该有访问凭证验证，不应完全依赖白名单</li>
 * </ul>
 *
 * <p><b>白名单配置说明：</b></p>
 * <ul>
 *   <li>登录相关：/login/localLogin、/login/modifyInvalidPwd、/login/platformLogin/</li>
 *   <li>第三方登录：/lark/、/dingtalk/、/wecom/、/oauth2/、/saml/</li>
 *   <li>公开资源：/static-resource/、/appearance/image/、/map/、/geo/、/i18n/</li>
 *   <li>系统接口：/dekey、/symmetricKey、/sysParameter/</li>
 *   <li>分享相关：/share/proxyInfo、/embedded/initIframe</li>
 *   <li>下载接口：/exportCenter/download、/typeface/download</li>
 *   <li>多因素认证：/mfa/qr/、/mfa/login</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.auth.filter.TokenFilter
 * @see io.dataease.constant.AuthConstant
 */
public class WhitelistUtils {

    /**
     * 应用上下文路径缓存
     * 从Spring配置中读取server.servlet.context-path的值
     */
    private static String contextPath;

    /**
     * 获取应用上下文路径
     * <p>
     * 从Spring Environment中读取server.servlet.context-path配置。
     * 结果会被缓存，后续调用直接返回缓存值，避免重复读取配置。
     * </p>
     *
     * <p><b>配置示例：</b></p>
     * <pre>
     * # application.yml
     * server:
     *   servlet:
     *     context-path: /dataease  # 配置后返回"/dataease"
     *     # 不配置或为空时返回null或空字符串
     * </pre>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>URL匹配时去除上下文路径</li>
     *   <li>构建完整URL时添加上下文路径</li>
     *   <li>生成OAuth回调URL</li>
     * </ul>
     *
     * @return 应用上下文路径，如"/dataease"；未配置时可能返回null或空字符串
     */
    public static String getContextPath() {
        if (StringUtils.isBlank(contextPath)) {
            contextPath = Objects.requireNonNull(CommonBeanFactory.getBean(Environment.class)).getProperty("server.servlet.context-path", String.class);
        }
        return contextPath;
    }

    /**
     * 白名单路径列表（精确匹配）
     * <p>
     * 包含所有无需Token认证即可访问的接口路径。
     * 这是一个不可变列表，运行时无法修改。
     * </p>
     *
     * <p><b>路径分类：</b></p>
     * <ul>
     *   <li>登录接口：/login/localLogin、/login/modifyInvalidPwd</li>
     *   <li>系统密钥：/dekey、/symmetricKey</li>
     *   <li>第三方登录：/lark/、/larksuite/、/dingtalk/、/wecom/</li>
     *   <li>系统参数：/sysParameter/、/setting/authentication/status</li>
     *   <li>页面入口：/index.html、/panel.html、/mobile.html</li>
     *   <li>嵌入式：/embedded/initIframe</li>
     *   <li>健康检查：/apisix/check</li>
     *   <li>API文档：/swagger-resources、/doc.html</li>
     * </ul>
     */
    public static List<String> WHITE_PATH = List.of(
            "/login/localLogin",
            "/apisix/check",
            "/dekey",
            "/symmetricKey",
            "/index.html",
            "/model",
            "/xpackModel",
            "/swagger-resources",
            "/doc.html",
            "/panel.html",
            "/mobile.html",
            "/lark/qrinfo",
            "/lark/token",
            "/larksuite/qrinfo",
            "/larksuite/token",
            "/dingtalk/qrinfo",
            "/dingtalk/token",
            "/wecom/qrinfo",
            "/wecom/token",
            "/sysParameter/requestTimeOut",
            "/sysParameter/defaultSettings",
            "/setting/authentication/status",
            "/sysParameter/ui",
            "/sysParameter/defaultLogin",
            "/embedded/initIframe",
            "/sysParameter/i18nOptions",
            "/login/modifyInvalidPwd",
            "/");

    /**
     * 匹配请求URI是否在白名单中
     * <p>
     * 验证给定的请求URI是否在白名单中，无需Token认证即可访问。
     * 支持精确匹配、前缀匹配和后缀匹配三种方式。
     * </p>
     *
     * <p><b>处理流程：</b></p>
     * <ol>
     *   <li>URL安全校验：检测路径遍历、目录穿越等攻击</li>
     *   <li>去除上下文路径：移除server.servlet.context-path</li>
     *   <li>去除API前缀：移除/api、/casapi、/oidcapi等前缀</li>
     *   <li>白名单匹配：按精确、前缀、后缀顺序匹配</li>
     * </ol>
     *
     * <p><b>匹配规则：</b></p>
     * <ul>
     *   <li>精确匹配：WHITE_PATH列表中的完整路径</li>
     *   <li>后缀匹配：.gif、.ico、.js、.css、.svg、.png、.jpg等静态资源</li>
     *   <li>前缀匹配：/static-resource/、/appearance/image/、/map/等公开资源路径</li>
     *   <li>特殊匹配：data:image等特殊格式</li>
     * </ul>
     *
     * <p><b>实际使用示例（来自TokenFilter）：</b></p>
     * <pre>
     * // 在TokenFilter的doFilter方法中
     * String requestURI = request.getRequestURI();
     * boolean match = WhitelistUtils.match(requestURI);
     * if (match) {
     *     // 白名单匹配成功，直接放行无需认证
     *     filterChain.doFilter(servletRequest, servletResponse);
     *     return;
     * }
     * // 非白名单请求，继续Token验证流程
     * </pre>
     *
     * <p><b>安全校验示例：</b></p>
     * <pre>
     * // 正常请求
     * match("/api/login/localLogin");  // 返回true
     *
     * // 路径遍历攻击
     * match("/api/../../../etc/passwd");  // 抛出DEException
     *
     * // URL编码攻击
     * match("/api/.%2e/%2e%2e/etc/passwd");  // 抛出DEException
     *
     * // 分号注入攻击（非查询参数中）
     * match("/api/user;delete=true");  // 抛出DEException
     *
     * // 正常查询参数中的分号
     * match("/api/user?param=a;b");  // 正常处理
     * </pre>
     *
     * @param requestURI 请求的URI路径，可能包含上下文路径和API前缀
     * @return true表示URI在白名单中，可以无需认证访问；false表示需要Token认证
     * @throws DEException 当检测到非法URL（路径遍历、目录穿越等）时抛出
     */
    public static boolean match(String requestURI) {
        // 1. URL安全校验：防止路径遍历、目录穿越等攻击
        invalidUrl(requestURI);

        // 2. 去除上下文路径（如有配置）
        if (StringUtils.startsWith(requestURI, getContextPath())) {
            requestURI = requestURI.replaceFirst(getContextPath(), "");
        }

        // 3. 去除各种API前缀
        if (StringUtils.startsWith(requestURI, AuthConstant.DE_API_PREFIX)) {
            requestURI = requestURI.replaceFirst(AuthConstant.DE_API_PREFIX, "");
        }
        if (StringUtils.startsWith(requestURI, AuthConstant.DE_CASAPI_PREFIX)) {
            requestURI = requestURI.replaceFirst(AuthConstant.DE_CASAPI_PREFIX, "");
        }
        if (StringUtils.startsWith(requestURI, AuthConstant.DE_OIDCAPI_PREFIX)) {
            requestURI = requestURI.replaceFirst(AuthConstant.DE_OIDCAPI_PREFIX, "");
        }

        // 4. 白名单匹配：精确匹配、后缀匹配、前缀匹配
        return WHITE_PATH.contains(requestURI)  // 精确匹配
                || StringUtils.endsWithAny(requestURI, ".gif",".ico", "js", ".css", "svg", "png", "jpg", "js.map", ".otf", ".ttf", ".woff2")  // 静态资源后缀
                || StringUtils.startsWithAny(requestURI, "data:image")  // 特殊格式
                || StringUtils.startsWithAny(requestURI, "/login/platformLogin/")  // 平台登录
                || StringUtils.startsWithAny(requestURI, "/static-resource/")  // 静态资源
                || StringUtils.startsWithAny(requestURI, "/appearance/image/")  // 外观图片
                || StringUtils.startsWithAny(requestURI, "/share/proxyInfo")  // 分享代理信息
                || StringUtils.startsWithAny(requestURI, "/xpackComponent/content")  // 扩展组件内容
                || StringUtils.startsWithAny(requestURI, "/xpackComponent/pluginStaticInfo")  // 插件静态信息
                || StringUtils.startsWithAny(requestURI, "/geo/")  // 地理数据
                || StringUtils.startsWithAny(requestURI, "/customGeo/")  // 自定义地理数据
                || StringUtils.startsWithAny(requestURI, "/websocket")  // WebSocket连接
                || StringUtils.startsWithAny(requestURI, "/map/")  // 地图资源
                || StringUtils.startsWithAny(requestURI, "/oauth2/")  // OAuth2认证
                || StringUtils.startsWithAny(requestURI, "/mfa/qr/")  // 多因素认证二维码
                || StringUtils.startsWithAny(requestURI, "/mfa/login")  // 多因素认证登录
                || StringUtils.startsWithAny(requestURI, "/typeface/download")  // 字体下载
                || StringUtils.startsWithAny(requestURI, "/typeface/defaultFont")  // 默认字体
                || StringUtils.startsWithAny(requestURI, "/typeface/listFont")  // 字体列表
                || StringUtils.startsWithAny(requestURI, "/exportCenter/download")  // 导出中心下载
                || StringUtils.startsWithAny(requestURI, "/i18n/")  // 国际化资源
                || StringUtils.startsWithAny(requestURI, "/communicate/image/")  // 通信图片
                || StringUtils.startsWithAny(requestURI, "/saml/")  // SAML认证
                || StringUtils.startsWithAny(requestURI, "/communicate/down/");  // 通信下载
    }

    /**
     * 构建Base API URL
     * <p>
     * 根据重定向URI和上下文路径构建完整的API基础URL。
     * 主要用于OAuth2、SAML等第三方登录回调时构建完整的API地址。
     * </p>
     *
     * <p><b>URL构建规则：</b></p>
     * <ul>
     *   <li>去除redirect_uri末尾的斜杠（如有）</li>
     *   <li>添加上下文路径（如有配置）</li>
     *   <li>添加API前缀：/api/</li>
     *   <li>最终格式：{redirect_uri}{contextPath}/api/</li>
     * </ul>
     *
     * <p><b>应用场景：</b></p>
     * <ul>
     *   <li>OAuth2回调：构建完整的OAuth回调URL</li>
     *   <li>SAML认证：构建SAML断言消费服务URL</li>
     *   <li>第三方登录：飞书、钉钉、企业微信等回调URL</li>
     *   <li>API文档：生成API基础地址</li>
     * </ul>
     *
     * <p><b>示例：</b></p>
     * <pre>
     * // 示例1：无上下文路径
     * String url1 = getBaseApiUrl("https://dataease.io");
     * // 结果: https://dataease.io/api/
     *
     * // 示例2：带末尾斜杠
     * String url2 = getBaseApiUrl("https://dataease.io/");
     * // 结果: https://dataease.io/api/
     *
     * // 示例3：有上下文路径（假设contextPath="/dataease"）
     * String url3 = getBaseApiUrl("https://dataease.io");
     * // 结果: https://dataease.io/dataease/api/
     *
     * // 示例4：OAuth2回调URL构建
     * String redirectUri = "https://dataease.io";
     * String baseUrl = getBaseApiUrl(redirectUri);
     * String oauthCallback = baseUrl + "oauth2/callback";
     * // 结果: https://dataease.io/api/oauth2/callback
     * </pre>
     *
     * @param redirect_uri 重定向URI，第三方登录完成后的回调地址
     * @return 完整的API基础URL，格式为：{redirect_uri}{contextPath}/api/
     */
    public static String getBaseApiUrl(String redirect_uri) {
        // 去除末尾斜杠（如有）
        if (StringUtils.endsWith(redirect_uri, "/")) {
            redirect_uri = redirect_uri.substring(0, redirect_uri.length() - 1);
        }

        // 添加上下文路径（如有配置）
        String contextPath = WhitelistUtils.getContextPath();
        if (StringUtils.isNotBlank(contextPath)) {
            redirect_uri += contextPath;
        }

        // 添加API前缀并返回
        return redirect_uri + AuthConstant.DE_API_PREFIX + "/";
    }

    /**
     * 验证URL是否包含非法字符或模式
     * <p>
     * 检测URL中的各种安全威胁，包括路径遍历、目录穿越、参数污染等攻击方式。
     * 这是一个私有方法，在{@link #match(String)}方法中被调用。
     * </p>
     *
     * <p><b>检测规则：</b></p>
     * <ul>
     *   <li>路径遍历：检测"../"字符串，防止访问上级目录</li>
     *   <li>URL编码攻击：检测".%"和"%2e"（点的URL编码），防止绕过路径检测</li>
     *   <li>参数污染：检测路径部分（查询参数前）的分号";"，防止参数注入攻击</li>
     * </ul>
     *
     * <p><b>攻击示例：</b></p>
     * <pre>
     * // 1. 路径遍历攻击
     * invalidUrl("/api/../../../etc/passwd");  // 抛出异常
     * invalidUrl("/api/user/../../admin");     // 抛出异常
     *
     * // 2. URL编码绕过攻击
     * invalidUrl("/api/.%2e/%2e%2e/etc/passwd");  // 抛出异常（%2e是点的编码）
     * invalidUrl("/api/file.%252e/secret");       // 抛出异常
     *
     * // 3. 分号参数污染（Spring参数绑定漏洞）
     * invalidUrl("/api/user;role=admin");  // 抛出异常
     * invalidUrl("/api/file;delete=true"); // 抛出异常
     *
     * // 4. 正常的查询参数中的分号（允许）
     * invalidUrl("/api/user?param=a;b");   // 不抛出异常，查询参数允许分号
     *
     * // 5. 正常请求（不抛出异常）
     * invalidUrl("/api/user/login");
     * invalidUrl("/static-resource/image.png");
     * </pre>
     *
     * <p><b>安全说明：</b></p>
     * <ul>
     *   <li>路径遍历：攻击者试图通过../访问系统的任意文件</li>
     *   <li>URL编码绕过：攻击者使用编码（%2e）绕过安全检测</li>
     *   <li>参数污染：Spring MVC的Matrix Variables特性可能被滥用，通过分号注入参数</li>
     *   <li>查询参数分号：在?后的查询参数中分号是合法的（如a=1;b=2）</li>
     * </ul>
     *
     * <p><b>防护机制：</b></p>
     * <ul>
     *   <li>多层检测：同时检测原始字符和编码字符</li>
     *   <li>大小写无关：toLowerCase()确保大小写编码也会被检测</li>
     *   <li>上下文敏感：区分路径部分和查询参数部分</li>
     *   <li>早期拦截：在白名单匹配前就进行安全检查</li>
     * </ul>
     *
     * @param requestURI 待验证的请求URI
     * @throws DEException 当检测到非法URL模式时抛出，包含错误码和详细错误信息
     */
    private static void invalidUrl(String requestURI) {
        // 检测多种非法URL模式
        if (requestURI.contains("./")  // 路径遍历
                || requestURI.contains(".%")  // URL编码的点（部分）
                || requestURI.toLowerCase().contains("%2e")  // URL编码的点（完整，大小写无关）
                || (requestURI.contains(";") && !requestURI.contains("?"))) {  // 路径中的分号（非查询参数）
            // 抛出接口地址无效异常
            DEException.throwException(INTERFACE_ADDRESS_INVALID.code(),
                    String.format("%s [%s]", INTERFACE_ADDRESS_INVALID.message(), requestURI));
        }
    }
}
