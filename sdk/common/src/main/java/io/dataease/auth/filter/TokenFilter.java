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

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String method = request.getMethod();
        if (!StringUtils.equalsAny(method, "GET", "POST", "OPTIONS", "DELETE")) {
            HttpServletResponse res = (HttpServletResponse) servletResponse;
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
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

        boolean match = false;
        try {
            match = WhitelistUtils.match(requestURI);
        } catch (DEException e) {
            HttpServletResponse res = (HttpServletResponse) servletResponse;
            ResultMessage resultMessage = new ResultMessage(e.getCode(), e.getMessage());
            ResponseEntity<ResultMessage> entity = new ResponseEntity<>(resultMessage, HttpStatus.UNAUTHORIZED);
            sendResponseEntity(res, entity);
            LogUtil.error(e.getMessage(), e);
            return;
        }
        if (match) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        try {
            if (ModelUtils.isDesktop()) {
                UserUtils.setDesktopUser();
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            String executeVersion = null;
            if (StringUtils.isNotBlank(executeVersion = VersionUtil.getRandomVersion())) {
                Objects.requireNonNull(ServletUtils.response()).addHeader(AuthConstant.DE_EXECUTE_VERSION, executeVersion);
            }
            String linkToken = ServletUtils.getHead(AuthConstant.LINK_TOKEN_KEY);
            if (StringUtils.isNotBlank(linkToken)) {
                TokenUserBO tokenUserBO = TokenUtils.validateLinkToken(linkToken);
                UserUtils.setUserInfo(tokenUserBO);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            String token = ServletUtils.getToken();
            TokenUserBO userBO = TokenUtils.validate(token);
            UserUtils.setUserInfo(userBO);
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            if (!LicenseUtil.licenseValid()) {
                HttpServletResponse res = (HttpServletResponse) servletResponse;
                ResultMessage resultMessage = new ResultMessage(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
                HttpHeaders headers = new HttpHeaders();
                String msg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8).replace("+", "%20");
                headers.add(headName, msg);
                ResponseEntity<ResultMessage> entity = new ResponseEntity<>(resultMessage, headers, HttpStatus.UNAUTHORIZED);
                sendResponseEntity(res, entity);
                LogUtil.error(e.getMessage(), e);
            } else {
                throw e;
            }
        } finally {
            UserUtils.removeUser();
        }
    }

    private void sendResponseEntity(HttpServletResponse httpResponse, ResponseEntity<ResultMessage> responseEntity) throws IOException {
        HttpStatusCode statusCode = responseEntity.getStatusCode();
        httpResponse.setStatus(statusCode.value());
        httpResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        HttpHeaders headers = responseEntity.getHeaders();
        if (ObjectUtils.isNotEmpty(headers)) {
            headers.forEach((key, value) -> httpResponse.addHeader(key, value.toString()));
        }
        httpResponse.getWriter().write(Objects.requireNonNull(JsonUtil.toJSONString(responseEntity.getBody()).toString()));
    }

}
