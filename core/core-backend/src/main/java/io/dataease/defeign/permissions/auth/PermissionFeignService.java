package io.dataease.defeign.permissions.auth;

import io.dataease.feign.DeFeign;
import io.dataease.api.permissions.auth.api.AuthApi;

/**
 * 权限认证Feign客户端服务接口
 * 用于远程调用xpack-permissions服务中的认证相关功能
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>作为认证API的Feign客户端实现</li>
 *   <li>提供对xpack-permissions服务认证模块的远程访问</li>
 *   <li>支持负载均衡和服务发现</li>
 *   <li>集成熔断降级和重试机制</li>
 * </ul>
 *
 * <p>Feign配置说明：</p>
 * <ul>
 *   <li><b>服务名称：</b>xpack-permissions - 目标服务的注册名称</li>
 *   <li><b>请求路径：</b>/auth - 认证相关接口的统一路径前缀</li>
 * </ul>
 *
 * <p>继承的API接口：</p>
 * <ul>
 *   <li>{@link AuthApi} - 认证相关的所有API接口定义</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * @Service
 * public class AuthService {
 *
 *     @Resource
 *     private PermissionFeignService permissionFeignService;
 *
 *     public void validateUser(String token) {
 *         // 远程调用权限服务验证用户
 *         UserInfo userInfo = permissionFeignService.validateToken(token);
 *         // 处理验证结果
 *     }
 * }
 * }</pre>
 *
 * @see AuthApi
 * @see DeFeign
 */
@DeFeign(value = "xpack-permissions", path = "/auth")
public interface PermissionFeignService extends AuthApi {

}
