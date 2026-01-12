package io.dataease.defeign.permissions.auth;

import io.dataease.api.permissions.auth.api.InteractiveAuthApi;
import io.dataease.feign.DeFeign;

/**
 * 交互式认证Feign客户端服务接口
 * 用于远程调用xpack-permissions服务中的交互式认证功能
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>作为交互式认证API的Feign客户端实现</li>
 *   <li>支持复杂的用户交互式认证流程</li>
 *   <li>提供多步骤认证和授权管理</li>
 *   <li>支持会话管理和状态维护</li>
 * </ul>
 *
 * <p>Feign配置说明：</p>
 * <ul>
 *   <li><b>服务名称：</b>xpack-permissions - 目标服务的注册名称</li>
 *   <li><b>请求路径：</b>/interactive - 交互式认证相关接口的统一路径前缀</li>
 * </ul>
 *
 * <p>交互式认证场景：</p>
 * <ul>
 *   <li>多因子认证（MFA）</li>
 *   <li>OAuth2授权码流程</li>
 *   <li>SAML单点登录</li>
 *   <li>第三方登录集成</li>
 * </ul>
 *
 * <p>继承的API接口：</p>
 * <ul>
 *   <li>{@link InteractiveAuthApi} - 交互式认证相关的所有API接口定义</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * @Service
 * public class LoginService {
 *
 *     @Resource
 *     private InteractiveAuthFeignService interactiveAuthService;
 *
 *     public AuthResponse startOAuthFlow(String provider) {
 *         // 启动OAuth认证流程
 *         return interactiveAuthService.initiateOAuth(provider);
 *     }
 *
 *     public TokenResponse handleCallback(String code, String state) {
 *         // 处理回调并获取令牌
 *         return interactiveAuthService.handleOAuthCallback(code, state);
 *     }
 * }
 * }</pre>
 *
 * @see InteractiveAuthApi
 * @see DeFeign
 */
@DeFeign(value = "xpack-permissions", path = "/interactive")
public interface InteractiveAuthFeignService extends InteractiveAuthApi {
}
