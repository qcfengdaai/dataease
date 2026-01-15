package io.dataease.share.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import io.dataease.auth.DeLinkPermit;
import io.dataease.constant.AuthConstant;
import io.dataease.exception.DEException;
import io.dataease.share.manage.XpackShareManage;
import io.dataease.share.util.LinkTokenUtil;
import io.dataease.utils.LogUtil;
import io.dataease.utils.ServletUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 分享链接权限拦截AOP
 *
 * 使用AOP拦截带有@DeLinkPermit注解的方法，验证分享链接的Token
 * <p>主要功能包括：</p>
 * <ul>\n * <li>从请求头中提取LinkToken</li>
 * <li>解析JWT Token并验证签名</li>
 * <li>验证Token中的资源ID与请求参数是否匹配</li>
 * <li>支持Spring EL表达式提取参数</li>
 * </ul>
 *
 * 使用示例：
 * <pre>{@code
 * @DeLinkPermit
 * public void someMethod(@Param("resourceId") Long resourceId) {
 *     // 方法实现
 * }
 *
 * @DeLinkPermit("#p0")
 * public void someMethod(Long resourceId) {
 *     // 方法实现
 * }
 * }</pre>
 *
 * @author fit2cloud
 * @since 2024-06-21
 */
@Aspect
@Component
public class DeLinkAop {

    /** 参数变量前缀 */
    private static final String PARAM_VARIABLE_PREFIX = "p";

    /** Spring EL表达式标志 */
    private static final String SPRING_EL_FLAG = "#";

    /** Spring EL表达式解析器 */
    private final ExpressionParser parser = new SpelExpressionParser();

    @Resource
    private XpackShareManage xpackShareManage;

    /**
     * 环绕通知，拦截带有@DeLinkPermit注解的方法
     *
     * @param point 连接点
     * @return 方法执行结果
     * @throws Throwable 执行异常
     */
    @Around(value = "@annotation(io.dataease.auth.DeLinkPermit)")
    public Object logAround(ProceedingJoinPoint point) throws Throwable {
        Object[] params = point.getArgs();
        // 从请求头中获取LinkToken
        String linkToken = ServletUtils.getHead(AuthConstant.LINK_TOKEN_KEY);
        if (StringUtils.isNotBlank(linkToken)) {
            MethodSignature ms = (MethodSignature) point.getSignature();
            Method method = ms.getMethod();
            // 获取@DeLinkPermit注解
            DeLinkPermit deLinkPermit = method.getAnnotation(DeLinkPermit.class);
            String value = deLinkPermit.value();
            // 如果未指定表达式，默认使用第一个参数
            if (StringUtils.isBlank(value)) {
                value = SPRING_EL_FLAG + PARAM_VARIABLE_PREFIX + "0";
            }
            // 从参数中提取资源ID
            Long id = getExpression(params, value);
            // 解码JWT Token
            DecodedJWT jwt = JWT.decode(linkToken);
            Long resourceId = jwt.getClaim("resourceId").asLong();
            // 验证Token中的资源ID与参数中的资源ID是否一致
            if (!id.equals(resourceId)) {
                DEException.throwException("link token invalid");
                return false;
            }

            // 获取Token中的用户ID
            Long uid = jwt.getClaim("uid").asLong();
            // 查询分享密码作为验证密钥
            String secret = xpackShareManage.queryPwd(resourceId, uid);
            if (StringUtils.isBlank(secret)) {
                // 如果未设置密码，使用默认密码
                secret = LinkTokenUtil.defaultPwd;
            }
            // 使用HMAC256算法验证Token签名
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Verification verification = JWT.require(algorithm);
            JWTVerifier verifier = verification.build();
            DecodedJWT decode = JWT.decode(linkToken);
            algorithm.verify(decode);
            verifier.verify(linkToken);
        }
        try {
            // 执行目标方法
            return point.proceed(params);
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            throw e;
        }
    }

    /**
     * 从参数中提取表达式值
     * 支持Spring EL表达式解析
     *
     * @param params 方法参数数组
     * @param expression 表达式（支持SpEL）
     * @return 解析后的值
     */
    public Long getExpression(Object[] params, String expression) {
        StandardEvaluationContext context = buildContext(params);
        Object o = resolveValue(expression, context);
        if (ObjectUtils.isNotEmpty(o)) return Long.parseLong(o.toString());
        return null;
    }

    /**
     * 构建Spring EL上下文
     * 将方法参数设置为上下文变量
     *
     * @param params 方法参数数组
     * @return EL上下文
     */
    private StandardEvaluationContext buildContext(Object[] params) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 如果只有一个参数，设置为根对象
        if (params != null && params.length == 1) {
            context.setRootObject(params[0]);
        }
        // 将所有参数设置为变量（p0, p1, p2...）
        for (int i = 0; i < Objects.requireNonNull(params).length; i++) {
            Object paramValue = params[i];
            context.setVariable(PARAM_VARIABLE_PREFIX + i, paramValue);
        }
        return context;
    }

    /**
     * 解析表达式值
     * 如果表达式包含SpEL标志，则解析表达式，否则直接返回表达式本身
     *
     * @param exp 表达式
     * @param context EL上下文
     * @return 解析后的值
     */
    private Object resolveValue(String exp, EvaluationContext context) {
        if (StringUtils.contains(exp, SPRING_EL_FLAG)) {
            Expression expression = parser.parseExpression(exp);
            return expression.getValue(context);
        }
        return exp;
    }
}
