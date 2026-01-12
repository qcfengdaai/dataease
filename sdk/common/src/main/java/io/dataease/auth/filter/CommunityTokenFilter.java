package io.dataease.auth.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import io.dataease.auth.bo.TokenUserBO;
import io.dataease.auth.config.SubstituleLoginConfig;
import io.dataease.license.utils.LicenseUtil;
import io.dataease.utils.*;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 社区版Token验证过滤器
 * 专门用于社区版环境下的JWT Token二次验证
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>社区版JWT Token验证 - 在无许可证环境下验证Token的完整性</li>
 *   <li>密钥动态获取 - 根据不同配置获取JWT签名密钥</li>
 *   <li>用户信息校验 - 验证Token中的用户ID和组织ID</li>
 *   <li>反射调用支持 - 支持通过反射获取缓存中的用户密钥</li>
 * </ul>
 *
 * <p>适用场景：</p>
 * <ul>
 *   <li>社区版部署环境</li>
 *   <li>无企业版许可证的系统</li>
 *   <li>需要额外JWT安全验证的场景</li>
 * </ul>
 */
public class CommunityTokenFilter implements Filter {

    /**
     * 网关标识请求头名称
     * 用于传递错误信息到客户端
     */
    private static final String headName = "DE-GATEWAY-FLAG";

    /**
     * 执行社区版Token验证过滤逻辑
     *
     * <p>验证流程：</p>
     * <ol>
     *   <li>检查Token是否存在且用户已登录</li>
     *   <li>验证许可证状态（仅在无许可证时执行验证）</li>
     *   <li>获取JWT签名密钥（支持两种获取方式）</li>
     *   <li>使用HMAC256算法验证JWT Token</li>
     *   <li>校验用户ID和组织ID声明</li>
     *   <li>验证失败时返回401未授权响应</li>
     * </ol>
     *
     * @param servletRequest  HTTP请求对象
     * @param servletResponse HTTP响应对象
     * @param filterChain     过滤器链
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Long userId = null;
        String token = ServletUtils.getToken();
        TokenUserBO userBO = null;

        // 1. 检查Token、用户登录状态和许可证状态
        if (StringUtils.isNotBlank(token)
            && ObjectUtils.isNotEmpty(userBO = AuthUtils.getUser())
            && ObjectUtils.isNotEmpty(userId = userBO.getUserId())
            && !LicenseUtil.licenseValid()) {

            String secret = null;

            // 2. 获取JWT签名密钥（支持两种方式）
            if (ObjectUtils.isEmpty(CommonBeanFactory.getBean("loginServer"))) {
                // 方式一：从替代登录配置获取密码并MD5加密作为密钥
                String pwd = SubstituleLoginConfig.getPwd();
                secret = Md5Utils.md5(pwd);
            } else {
                // 方式二：通过反射从缓存管理器获取用户密钥
                Object apisixCacheManage = CommonBeanFactory.getBean("apisixCacheManage");
                Method method = DeReflectUtil.findMethod(apisixCacheManage.getClass(), "userCacheBO");
                Object o = ReflectionUtils.invokeMethod(method, apisixCacheManage, userId);
                Method pwdMethod = DeReflectUtil.findMethod(o.getClass(), "getSecret");
                Object pwdObj = ReflectionUtils.invokeMethod(pwdMethod, o);
                secret = pwdObj.toString();
            }

            try {
                // 3. 构建JWT验证器并执行验证
                Algorithm algorithm = Algorithm.HMAC256(secret);
                Verification verification = JWT.require(algorithm)
                    .withClaim("uid", userId)           // 验证用户ID声明
                    .withClaim("oid", userBO.getDefaultOid()); // 验证组织ID声明
                JWTVerifier verifier = verification.build();

                // 4. 解码和验证JWT Token
                DecodedJWT decode = JWT.decode(token);
                algorithm.verify(decode);  // 验证签名
                verifier.verify(token);    // 验证完整性和声明

            } catch (Exception e) {
                // 5. JWT验证失败，返回401未授权响应
                HttpServletResponse res = (HttpServletResponse) servletResponse;
                LogUtil.error(e.getMessage(), e);
                HttpHeaders headers = new HttpHeaders();
                String msg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8).replace("+", "%20");
                headers.add(headName, msg);
                sendResponseEntity(res, new ResponseEntity<>(e.getMessage(), headers, HttpStatus.UNAUTHORIZED));
                return;
            }
        }

        // 6. 验证通过或无需验证，继续执行过滤器链
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * 发送响应实体到客户端
     *
     * <p>主要功能：</p>
     * <ul>
     *   <li>设置HTTP状态码</li>
     *   <li>复制自定义响应头信息</li>
     *   <li>将错误消息写入响应流</li>
     * </ul>
     *
     * @param httpResponse   HTTP响应对象
     * @param responseEntity 包含错误信息的响应实体
     * @throws IOException 写入响应流时可能发生的IO异常
     */
    private void sendResponseEntity(HttpServletResponse httpResponse, ResponseEntity<String> responseEntity) throws IOException {
        HttpHeaders headers = responseEntity.getHeaders();
        HttpStatusCode statusCode = responseEntity.getStatusCode();

        // 设置HTTP状态码
        httpResponse.setStatus(statusCode.value());

        // 复制所有自定义响应头
        for (String name : headers.keySet()) {
            httpResponse.setHeader(name, headers.getFirst(name));
        }

        // 将错误消息写入响应流
        httpResponse.getWriter().write(Objects.requireNonNull(responseEntity.getBody()));
    }
}
