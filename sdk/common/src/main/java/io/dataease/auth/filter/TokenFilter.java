package io.dataease.auth.filter;

import io.dataease.auth.bo.TokenUserBO;
import io.dataease.constant.AuthConstant;
import io.dataease.exception.DEException;
import io.dataease.license.utils.LicenseUtil;
import io.dataease.result.ResultMessage;
import io.dataease.utils.*;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Token认证过滤器
 * 负责处理所有HTTP请求的身份认证和授权验证
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>HTTP方法验证 - 只允许GET、POST、OPTIONS、DELETE方法</li>
 *   <li>白名单检查 - 无需认证的接口直接放行</li>
 *   <li>Token验证 - 验证用户Token和分享链接Token的有效性</li>
 *   <li>用户上下文 - 设置当前请求的用户信息到线程本地变量</li>
 *   <li>桌面版支持 - 桌面版环境下自动设置默认用户</li>
 *   <li>异常处理 - 统一处理认证失败的响应</li>
 * </ul>
 */
public class TokenFilter implements Filter {

    /**
     * 网关标识请求头名称
     * 用于标识请求来源和错误信息传递
     */
    private static final String headName = "DE-GATEWAY-FLAG";

    /**
     * 执行过滤逻辑
     *
     * <p>过滤流程：</p>
     * <ol>
     *   <li>HTTP方法验证 - 限制允许的HTTP方法</li>
     *   <li>OPTIONS预检处理 - 处理CORS预检请求</li>
     *   <li>白名单检查 - 验证是否为免认证接口</li>
     *   <li>桌面版特殊处理 - 桌面版环境下的用户设置</li>
     *   <li>分享链接Token验证 - 处理公开分享访问</li>
     *   <li>普通Token验证 - 验证登录用户的Token</li>
     *   <li>异常处理 - 认证失败时的响应处理</li>
     * </ol>
     *
     * @param servletRequest  HTTP请求对象
     * @param servletResponse HTTP响应对象
     * @param filterChain     过滤器链，用于继续执行后续过滤器
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String method = request.getMethod();

        // 1. HTTP方法验证 - 只允许GET、POST、OPTIONS、DELETE方法
        if (!StringUtils.equalsAny(method, "GET", "POST", "OPTIONS", "DELETE")) {
            HttpServletResponse res = (HttpServletResponse) servletResponse;
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        // 2. OPTIONS预检请求处理 - 用于CORS跨域请求的预检
        if (StringUtils.equalsIgnoreCase("OPTIONS", method)) {
            String origin = request.getHeader("Origin");
            if (StringUtils.isBlank(origin)) {
                HttpServletResponse res = (HttpServletResponse) servletResponse;
                res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String requestURI = request.getRequestURI();

        // 3. 白名单检查 - 验证是否为无需认证的接口
        boolean match = false;
        try {
            match = WhitelistUtils.match(requestURI);
        } catch (DEException e) {
            // 白名单匹配异常，返回未授权错误
            HttpServletResponse res = (HttpServletResponse) servletResponse;
            ResultMessage resultMessage = new ResultMessage(e.getCode(), e.getMessage());
            ResponseEntity<ResultMessage> entity = new ResponseEntity<>(resultMessage, HttpStatus.UNAUTHORIZED);
            sendResponseEntity(res, entity);
            LogUtil.error(e.getMessage(), e);
            return;
        }

        // 白名单匹配成功，直接放行无需认证
        if (match) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try {
            // 4. 桌面版特殊处理 - 桌面版环境下自动设置默认用户
            if (ModelUtils.isDesktop()) {
                UserUtils.setDesktopUser();
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            // 5. 版本信息处理 - 添加执行版本信息到响应头
            String executeVersion = null;
            if (StringUtils.isNotBlank(executeVersion = VersionUtil.getRandomVersion())) {
                Objects.requireNonNull(ServletUtils.response()).addHeader(AuthConstant.DE_EXECUTE_VERSION, executeVersion);
            }

            // 6. 分享链接Token验证 - 处理公开分享访问（如仪表板分享）
            String linkToken = ServletUtils.getHead(AuthConstant.LINK_TOKEN_KEY);
            if (StringUtils.isNotBlank(linkToken)) {
                TokenUserBO tokenUserBO = TokenUtils.validateLinkToken(linkToken);
                UserUtils.setUserInfo(tokenUserBO);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            // 7. 普通Token验证 - 验证登录用户的访问Token
            String token = ServletUtils.getToken();
            TokenUserBO userBO = TokenUtils.validate(token);
            UserUtils.setUserInfo(userBO);
            filterChain.doFilter(servletRequest, servletResponse);

        } catch (Exception e) {
            // 8. 异常处理 - 处理认证失败的情况
            if (!LicenseUtil.licenseValid()) {
                // 许可证无效时，返回未授权响应
                HttpServletResponse res = (HttpServletResponse) servletResponse;
                ResultMessage resultMessage = new ResultMessage(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
                HttpHeaders headers = new HttpHeaders();
                String msg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8).replace("+", "%20");
                headers.add(headName, msg);
                ResponseEntity<ResultMessage> entity = new ResponseEntity<>(resultMessage, headers, HttpStatus.UNAUTHORIZED);
                sendResponseEntity(res, entity);
                LogUtil.error(e.getMessage(), e);
            } else {
                // 许可证有效时，重新抛出异常由上层处理
                throw e;
            }
        } finally {
            // 9. 清理用户上下文 - 确保请求结束后清除线程本地变量
            UserUtils.removeUser();
        }
    }

    /**
     * 发送响应实体到客户端
     *
     * <p>主要功能：</p>
     * <ul>
     *   <li>设置HTTP状态码</li>
     *   <li>设置响应字符编码为UTF-8</li>
     *   <li>复制自定义响应头信息</li>
     *   <li>将响应体序列化为JSON并写入响应流</li>
     * </ul>
     *
     * @param httpResponse   HTTP响应对象
     * @param responseEntity Spring的ResponseEntity响应实体
     * @throws IOException 写入响应流时可能发生的IO异常
     */
    private void sendResponseEntity(HttpServletResponse httpResponse, ResponseEntity<ResultMessage> responseEntity) throws IOException {
        // 设置HTTP状态码
        HttpStatusCode statusCode = responseEntity.getStatusCode();
        httpResponse.setStatus(statusCode.value());

        // 设置响应字符编码为UTF-8
        httpResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 复制ResponseEntity中的自定义请求头
        HttpHeaders headers = responseEntity.getHeaders();
        if (ObjectUtils.isNotEmpty(headers)) {
            headers.forEach((key, value) -> httpResponse.addHeader(key, value.toString()));
        }

        // 将响应体序列化为JSON字符串并写入响应流
        httpResponse.getWriter().write(Objects.requireNonNull(JsonUtil.toJSONString(responseEntity.getBody()).toString()));
    }

}
