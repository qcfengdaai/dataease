package io.dataease.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dataease.auth.bo.TokenUserBO;
import io.dataease.exception.DEException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * JWT Token工具类
 * <p>
 * 提供JWT（JSON Web Token）令牌的解析、验证和用户信息提取功能。
 * JWT是一种开放标准（RFC 7519），用于在各方之间安全地传输信息。
 * 本工具类主要用于处理用户身份认证令牌，从token中提取用户ID和组织ID。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>JWT解析 - 解析JWT令牌并提取用户信息</li>
 *   <li>Token验证 - 验证令牌格式和有效性</li>
 *   <li>用户信息提取 - 从令牌中提取用户ID（uid）和组织ID（oid）</li>
 *   <li>链接Token验证 - 支持特殊的分享链接令牌验证</li>
 * </ul>
 *
 * <p><b>Token结构：</b></p>
 * <ul>
 *   <li>标准JWT格式：Header.Payload.Signature</li>
 *   <li>Payload中包含的声明（Claims）：
 *     <ul>
 *       <li>uid - 用户ID（Long类型）</li>
 *       <li>oid - 组织ID/默认组织ID（Long类型）</li>
 *     </ul>
 *   </li>
 *   <li>最小长度：100字符（用于基本格式验证）</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.auth.filter.TokenFilter#doFilter} - HTTP请求过滤器中验证用户令牌</li>
 *   <li>{@link io.dataease.auth.filter.CommunityTokenFilter} - 社区版令牌验证过滤器</li>
 *   <li>API认证 - 所有需要身份认证的接口</li>
 *   <li>分享链接 - 仪表板、数据集等资源的分享链接验证</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：基本的Token验证和用户信息提取
 * String token = request.getHeader("Authorization");
 * TokenUserBO userBO = TokenUtils.validate(token);
 * Long userId = userBO.getUserId();
 * Long orgId = userBO.getDefaultOid();
 * System.out.println("用户ID: " + userId + ", 组织ID: " + orgId);
 *
 * // 示例2：在过滤器中验证Token（实际使用场景）
 * // 参考 TokenFilter.java:81
 * try {
 *     String token = ServletUtils.getToken();
 *     TokenUserBO userBO = TokenUtils.validate(token);
 *     UserUtils.setUserInfo(userBO);  // 将用户信息设置到上下文
 *     // 继续处理请求...
 * } catch (DEException e) {
 *     // Token无效，返回401错误
 *     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
 * }
 *
 * // 示例3：验证分享链接Token
 * // 参考 TokenFilter.java:75
 * String linkToken = ServletUtils.getHead("LINK-TOKEN");
 * if (StringUtils.isNotBlank(linkToken)) {
 *     TokenUserBO tokenUserBO = TokenUtils.validateLinkToken(linkToken);
 *     UserUtils.setUserInfo(tokenUserBO);
 *     // 允许访问分享的资源...
 * }
 *
 * // 示例4：直接解析Token（不进行格式验证）
 * String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
 * TokenUserBO userBO = TokenUtils.userBOByToken(token);
 * // 注意：此方法不验证token长度，建议使用validate()方法
 *
 * // 示例5：处理Token验证异常
 * try {
 *     TokenUserBO userBO = TokenUtils.validate(token);
 *     // 处理业务逻辑...
 * } catch (DEException e) {
 *     if (e.getMessage().contains("token is empty")) {
 *         // Token为空
 *         log.error("未提供认证令牌");
 *     } else if (e.getMessage().contains("token is invalid")) {
 *         // Token格式无效
 *         log.error("令牌格式错误");
 *     } else if (e.getMessage().contains("token格式错误")) {
 *         // Token中缺少必要字段
 *         log.error("令牌缺少用户ID");
 *     }
 * }
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>本工具类只负责解析和提取JWT中的用户信息，不负责生成JWT</li>
 *   <li>JWT的签名验证由其他组件（如TokenFilter）完成</li>
 *   <li>Token必须包含uid和oid两个声明，否则会抛出异常</li>
 *   <li>Token最小长度为100字符，这是一个基本的格式校验</li>
 *   <li>链接Token与普通Token的验证逻辑基本相同，但用于不同的业务场景</li>
 *   <li>Token验证失败会抛出DEException异常，调用方需要捕获并处理</li>
 *   <li>建议配合HTTPS使用，防止Token在传输过程中被窃取</li>
 * </ul>
 *
 * <p><b>安全建议：</b></p>
 * <ul>
 *   <li>Token应该通过HTTP Header传输，不建议通过URL参数传输</li>
 *   <li>Token应该设置合理的过期时间，避免长期有效</li>
 *   <li>在用户登出时应该失效对应的Token</li>
 *   <li>敏感操作应该要求重新验证身份，不能仅依赖Token</li>
 *   <li>应该记录Token的使用情况，便于安全审计</li>
 *   <li>建议使用刷新Token机制，定期更换访问Token</li>
 * </ul>
 *
 * <p><b>工作流程：</b></p>
 * <ol>
 *   <li>用户登录成功后，系统生成JWT令牌并返回给客户端</li>
 *   <li>客户端在后续请求中，将Token放在HTTP Header中发送</li>
 *   <li>服务端接收到请求后，通过TokenFilter拦截并验证Token</li>
 *   <li>TokenFilter调用本工具类的validate()方法验证Token格式</li>
 *   <li>工具类解析Token，提取用户ID和组织ID</li>
 *   <li>将用户信息设置到当前请求上下文中</li>
 *   <li>业务代码可以通过AuthUtils获取当前登录用户信息</li>
 * </ol>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.auth.filter.TokenFilter
 * @see io.dataease.auth.filter.CommunityTokenFilter
 * @see io.dataease.auth.bo.TokenUserBO
 * @see io.dataease.utils.UserUtils
 */
public class TokenUtils {

    /**
     * 从Token中提取用户信息
     * <p>
     * 解析JWT令牌，提取其中的用户ID（uid）和组织ID（oid）。
     * 本方法不进行Token格式验证（如长度检查），仅负责解析。
     * 建议使用 {@link #validate(String)} 方法，它包含了完整的验证逻辑。
     * </p>
     *
     * <p><b>JWT解析说明：</b></p>
     * <ul>
     *   <li>使用JWT库的decode方法解析Token</li>
     *   <li>从Payload中提取uid和oid声明</li>
     *   <li>如果uid为空，会抛出异常</li>
     *   <li>oid可以为空（某些场景下可能不需要组织ID）</li>
     * </ul>
     *
     * @param token JWT令牌字符串（格式：Header.Payload.Signature）
     * @return {@link TokenUserBO} 包含用户ID和组织ID的用户信息对象
     * @throws DEException 当Token格式错误或缺少uid字段时抛出
     * @throws com.auth0.jwt.exceptions.JWTDecodeException 当Token无法解析时抛出
     */
    public static TokenUserBO userBOByToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("uid").asLong();
        Long oid = jwt.getClaim("oid").asLong();
        if (ObjectUtils.isEmpty(userId)) {
            DEException.throwException("token格式错误！");
        }
        return new TokenUserBO(userId, oid);
    }

    /**
     * 验证Token并提取用户信息
     * <p>
     * 完整的Token验证方法，包括格式检查和用户信息提取。
     * 这是推荐使用的Token验证方法，在HTTP请求过滤器中被广泛使用。
     * </p>
     *
     * <p><b>验证步骤：</b></p>
     * <ol>
     *   <li>检查Token是否为空</li>
     *   <li>检查Token长度是否符合要求（至少100字符）</li>
     *   <li>解析Token并提取用户信息</li>
     *   <li>验证用户ID是否存在</li>
     * </ol>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>HTTP请求过滤器 - 验证所有需要认证的API请求</li>
     *   <li>中间件 - 在业务逻辑执行前验证用户身份</li>
     *   <li>API网关 - 统一的认证入口</li>
     * </ul>
     *
     * @param token JWT令牌字符串
     * @return {@link TokenUserBO} 包含用户ID和组织ID的用户信息对象
     * @throws DEException 当Token为空、格式无效或解析失败时抛出，异常信息包含：
     *         <ul>
     *           <li>"token is empty for uri {xxx}" - Token为空</li>
     *           <li>"token is invalid" - Token长度不足100字符</li>
     *           <li>"token格式错误！" - Token中缺少uid字段</li>
     *         </ul>
     * @see io.dataease.auth.filter.TokenFilter#doFilter
     */
    public static TokenUserBO validate(String token) {
        if (StringUtils.isBlank(token)) {
            String uri = ServletUtils.request().getRequestURI();
            DEException.throwException("token is empty for uri {" + uri + "}");
        }
        if (StringUtils.length(token) < 100) {
            DEException.throwException("token is invalid");
        }
        return userBOByToken(token);
    }

    /**
     * 验证链接Token并提取用户信息
     * <p>
     * 用于验证分享链接（如仪表板分享、数据集分享）的专用Token。
     * 链接Token与普通Token的格式相同，但用于不同的业务场景。
     * 分享链接允许未登录用户在特定权限下访问共享资源。
     * </p>
     *
     * <p><b>链接Token特点：</b></p>
     * <ul>
     *   <li>用于资源分享场景，无需用户登录</li>
     *   <li>通常有特定的访问权限限制</li>
     *   <li>可能有时效性限制</li>
     *   <li>Token中包含分享创建者的用户ID和组织ID</li>
     * </ul>
     *
     * <p><b>验证步骤：</b></p>
     * <ol>
     *   <li>检查链接Token是否为空</li>
     *   <li>检查Token长度是否符合要求（至少100字符）</li>
     *   <li>解析Token并提取用户信息</li>
     *   <li>验证用户ID是否存在</li>
     * </ol>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>仪表板分享 - 允许通过链接访问仪表板</li>
     *   <li>数据集分享 - 允许通过链接访问数据集</li>
     *   <li>报表分享 - 允许通过链接访问报表</li>
     *   <li>公开访问 - 提供有限的公开访问权限</li>
     * </ul>
     *
     * @param linkToken 分享链接的JWT令牌字符串
     * @return {@link TokenUserBO} 包含用户ID和组织ID的用户信息对象（通常是分享创建者的信息）
     * @throws DEException 当链接Token为空、格式无效或解析失败时抛出，异常信息包含：
     *         <ul>
     *           <li>"link token is empty for uri {xxx}" - 链接Token为空</li>
     *           <li>"token is invalid" - Token长度不足100字符</li>
     *           <li>"link token格式错误！" - Token中缺少uid字段</li>
     *         </ul>
     * @see io.dataease.auth.filter.TokenFilter#doFilter
     */
    public static TokenUserBO validateLinkToken(String linkToken) {
        if (StringUtils.isBlank(linkToken)) {
            String uri = ServletUtils.request().getRequestURI();
            DEException.throwException("link token is empty for uri {" + uri + "}");
        }
        if (StringUtils.length(linkToken) < 100) {
            DEException.throwException("token is invalid");
        }
        DecodedJWT jwt = JWT.decode(linkToken);
        Long userId = jwt.getClaim("uid").asLong();
        Long oid = jwt.getClaim("oid").asLong();
        if (ObjectUtils.isEmpty(userId)) {
            DEException.throwException("link token格式错误！");
        }
        return new TokenUserBO(userId, oid);
    }
}
