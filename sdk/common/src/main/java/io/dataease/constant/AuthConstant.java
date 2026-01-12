package io.dataease.constant;

/**
 * DataEase认证相关常量类
 * <p>
 * 定义系统中所有与用户认证、授权和API访问相关的常量值，包括请求头键名、
 * API路径前缀、令牌标识等。这些常量用于统一管理认证系统中的各种标识符，
 * 确保系统各模块之间的一致性。
 * </p>
 *
 * <p><b>主要功能领域：</b></p>
 * <ul>
 *   <li><b>令牌管理</b> - 各种认证令牌的请求头键名定义</li>
 *   <li><b>单点登录</b> - OIDC、CAS等单点登录协议的用户信息头定义</li>
 *   <li><b>API路径</b> - 不同认证方式的API路径前缀定义</li>
 *   <li><b>网关集成</b> - APISIX等API网关的集成标识</li>
 *   <li><b>外部认证</b> - LDAP、OIDC、CAS等外部认证系统集成</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 */
public class AuthConstant {

    /**
     * DataEase主要认证令牌请求头键名
     * <p>
     * 用于在HTTP请求头中传递用户的认证令牌，这是系统最主要的身份验证方式。
     * 所有需要认证的API请求都应该在请求头中包含此键和对应的令牌值。
     * </p>
     */
    public final static String TOKEN_KEY = "X-DE-TOKEN";

    /**
     * 嵌入式认证令牌请求头键名
     * <p>
     * 用于嵌入式场景（如iframe、第三方集成）下的认证令牌传递。
     * 当DataEase作为组件嵌入到其他应用中时使用此令牌进行身份验证。
     * </p>
     */
    public final static String EMBEDDED_TOKEN_KEY = "X-EMBEDDED-TOKEN";

    /**
     * APISIX网关检查标志键名
     * <p>
     * 用于标识请求是否通过APISIX API网关，帮助系统判断请求来源
     * 和执行相应的网关集成逻辑。当此标志存在时，系统知道请求已经
     * 经过了APISIX网关的处理。
     * </p>
     */
    public final static String APISIX_FLAG_KEY = "APISIX_CHECK";

    /**
     * OIDC用户信息请求头键名
     * <p>
     * OpenID Connect (OIDC) 协议下用于传递用户信息的请求头键名。
     * 当系统集成了OIDC认证提供者时，用户的身份信息会通过此请求头传递，
     * 通常包含用户的基本信息如用户名、邮箱等。
     * </p>
     */
    public final static String OIDC_X_USER = "X-Userinfo";

    /**
     * CAS用户信息请求头键名
     * <p>
     * Central Authentication Service (CAS) 协议下用于传递用户信息的请求头键名。
     * 当系统集成了CAS单点登录服务时，认证后的用户信息会通过此请求头传递，
     * 包含用户的唯一标识和基本属性信息。
     * </p>
     */
    public final static String CAS_X_USER = "X-CAS-USER";

    /**
     * DataEase标准API路径前缀
     * <p>
     * 用于标识DataEase核心API接口的统一路径前缀。所有标准的业务API
     * 都应该以此前缀开头，便于路由识别和权限控制。
     * </p>
     */
    public final static String DE_API_PREFIX = "/de2api";

    /**
     * DataEase CAS集成API路径前缀
     * <p>
     * 专用于CAS单点登录集成的API路径前缀。当系统通过CAS进行认证时，
     * 相关的API请求会使用此前缀，以区别于标准认证的API调用。
     * </p>
     */
    public final static String DE_CASAPI_PREFIX = "/casbi/de2api";

    /**
     * DataEase OIDC集成API路径前缀
     * <p>
     * 专用于OIDC (OpenID Connect) 集成的API路径前缀。当系统通过OIDC
     * 进行认证时，相关的API请求会使用此前缀，支持OIDC协议的特殊处理需求。
     * </p>
     */
    public final static String DE_OIDCAPI_PREFIX = "/oidcbi/de2api";

    // /**
    //  * 刷新令牌请求头键名（已注释）
    //  * <p>
    //  * 原计划用于令牌刷新机制的请求头键名，目前暂未启用。
    //  * 保留此定义用于未来可能的令牌自动刷新功能实现。
    //  * </p>
    //  */
    // public final static String REFRESH_TOKEN_KEY = "X-DE-REFRESH-TOKEN";

    /**
     * 用户导入错误信息缓存键前缀
     * <p>
     * 用于在用户批量导入过程中存储错误信息的缓存键前缀。
     * 当批量导入用户出现错误时，错误详情会以此为键前缀存储在缓存中，
     * 便于后续查询和错误报告生成。
     * </p>
     */
    public final static String USER_IMPORT_ERROR_KEY = "USER-IMPORT-ERROR-KEY";

    /**
     * 链接分享令牌请求头键名
     * <p>
     * 用于分享链接访问时的认证令牌传递。当用户通过分享链接访问
     * DataEase资源时，会使用此令牌进行身份验证，确保分享权限的正确性。
     * </p>
     */
    public final static String LINK_TOKEN_KEY = "X-DE-LINK-TOKEN";

    /**
     * 询问服务令牌请求头键名
     * <p>
     * 用于智能问答或咨询服务功能的专用认证令牌。当用户使用AI助手
     * 或其他问答功能时，会通过此令牌进行身份验证和权限控制。
     * </p>
     */
    public final static String ASK_TOKEN_KEY = "X-DE-ASK-TOKEN";

    /**
     * DataEase执行版本请求头键名
     * <p>
     * 用于在请求头中标识当前执行的DataEase版本信息。
     * 主要用于版本兼容性检查和功能特性的版本控制，
     * 确保API调用与系统版本的兼容性。
     * </p>
     */
    public final static String DE_EXECUTE_VERSION = "X-DE-EXECUTE-VERSION";

    /**
     * LDAP认证授权请求头键名
     * <p>
     * 用于LDAP (Lightweight Directory Access Protocol) 认证的标准授权请求头。
     * 当系统集成LDAP目录服务进行用户认证时，会使用此请求头传递认证凭证，
     * 遵循HTTP Basic认证或其他LDAP支持的认证协议。
     * </p>
     */
    public final static String DE_LDAP_AUTHORIZATION = "Authorization";


}
