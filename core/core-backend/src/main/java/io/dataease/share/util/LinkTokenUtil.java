package io.dataease.share.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 链接Token工具类
 *
 * 用于生成和验证分享链接的JWT Token
 * <p>功能包括：</p>
 * <ul>
 * <li>生成包含用户、资源和组织信息的JWT Token</li>
 * <li>支持设置Token过期时间</li>
 * <li>使用分享密码作为签名密钥</li>
 * </ul>
 *
 * 使用示例：
 * <pre>{@code
 * String token = LinkTokenUtil.generate(userId, resourceId, expTime, pwd, oid);
 * }</pre>
 */
public class LinkTokenUtil {

    /** 默认密码：当分享未设置密码时使用 */
    public static final String defaultPwd = "link-pwd-fit2cloud";

    /**
     * 生成分享链接的JWT Token
     *
     * @param uid 用户ID
     * @param resourceId 资源ID
     * @param exp 过期时间（时间戳，毫秒），0表示永不过期
     * @param pwd 分享密码，为空时使用默认密码
     * @param oid 组织ID
     * @return JWT Token字符串
     */
    public static String generate(Long uid, Long resourceId, Long exp, String pwd, Long oid) {
        // 如果未设置密码，使用默认密码
        pwd = StringUtils.isBlank(pwd) ? defaultPwd : pwd;
        // 使用HMAC256算法，以分享密码作为密钥
        Algorithm algorithm = Algorithm.HMAC256(pwd);
        JWTCreator.Builder builder = JWT.create();
        // 添加用户ID、资源ID和组织ID到Token中
        builder.withClaim("uid", uid).withClaim("resourceId", resourceId).withClaim("oid", oid);
        // 如果设置了过期时间且不为0，添加过期时间
        if (ObjectUtils.isNotEmpty(exp) && !exp.equals(0L)) {
            builder = builder.withExpiresAt(new Date(exp));
        }
        // 使用密钥签名并生成Token
        return builder.sign(algorithm);
    }
}
