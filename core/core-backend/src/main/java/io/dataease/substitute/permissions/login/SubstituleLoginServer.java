package io.dataease.substitute.permissions.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import io.dataease.api.permissions.login.dto.PwdLoginDTO;
import io.dataease.auth.bo.TokenUserBO;
import io.dataease.auth.config.SubstituleLoginConfig;
import io.dataease.auth.vo.TokenVO;
import io.dataease.exception.DEException;
import io.dataease.i18n.Translator;
import io.dataease.utils.LogUtil;
import io.dataease.utils.Md5Utils;
import io.dataease.utils.RsaUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * 登录服务替补实现
 *
 * <p>这是企业版登录服务的社区版替补实现,用于桌面版(社区版)中。</p>
 *
 * <p>当企业版的权限服务不可用时,使用此替补实现提供基础的登录功能。</p>
 *
 * <p>此实现提供:</p>
 * <ul>
 * <li>本地登录功能(仅支持admin账号)</li>
 * <li>JWT令牌生成和验证</li>
 * <li>RSA加密的用户名密码解密</li>
 * <li>登出功能</li>
 * </ul>
 *
 * @author DataEase
 * @since 2.0
 */
@Component
@ConditionalOnMissingBean(name = "loginServer")
@RestController
@RequestMapping
public class SubstituleLoginServer {

    /**
     * 本地登录接口
     *
     * <p>提供基础的本地登录功能,仅支持admin账号。</p>
     *
     * <p>登录流程:</p>
     * <ol>
     * <li>解密RSA加密的用户名和密码</li>
     * <li>验证用户名必须为"admin"</li>
     * <li>验证密码是否匹配配置的密码</li>
     * <li>生成JWT令牌</li>
     * </ol>
     *
     * @param dto 登录请求对象,包含RSA加密的用户名和密码
     * @return 令牌信息对象,包含JWT访问令牌
     * @throws DEException 当用户名不是admin或密码错误时抛出异常
     */
    @PostMapping("/login/localLogin")
    public TokenVO localLogin(@RequestBody PwdLoginDTO dto) {

        String name = dto.getName();
        name = RsaUtils.decryptStr(name);
        String pwd = dto.getPwd();
        pwd = RsaUtils.decryptStr(pwd);

        dto.setName(name);
        dto.setPwd(pwd);

        if (!StringUtils.equals("admin", name)) {
            DEException.throwException("仅admin账号可用");
        }
        if (!StringUtils.equals(pwd, SubstituleLoginConfig.getPwd())) {
            DEException.throwException(Translator.get("i18n_login_name_pwd_err"));
        }
        TokenUserBO tokenUserBO = new TokenUserBO();
        tokenUserBO.setUserId(1L);
        tokenUserBO.setDefaultOid(1L);
        String md5Pwd = Md5Utils.md5(pwd);
        return generate(tokenUserBO, md5Pwd);
    }


    /**
     * 登出接口
     *
     * <p>提供基础的登出功能,仅记录日志。</p>
     *
     * <p>注意: 此替补实现不维护会话状态,因此登出操作仅记录日志。</p>
     */
    @GetMapping("/logout")
    public void logout() {
        LogUtil.info("substitule logout");
    }

    /**
     * 生成JWT令牌
     *
     * <p>使用用户密码的MD5值作为密钥生成JWT令牌。</p>
     *
     * @param bo 用户令牌信息对象,包含用户ID和组织ID
     * @param secret JWT签名密钥(使用密码的MD5值)
     * @return 令牌信息对象,包含JWT访问令牌和过期时间(0表示不过期)
     */
    private TokenVO generate(TokenUserBO bo, String secret) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Long userId = bo.getUserId();
        Long defaultOid = bo.getDefaultOid();
        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("uid", userId).withClaim("oid", defaultOid);
        String token = builder.sign(algorithm);
        return new TokenVO(token, 0L);
    }
}
