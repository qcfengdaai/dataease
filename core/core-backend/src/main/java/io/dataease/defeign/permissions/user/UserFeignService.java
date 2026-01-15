package io.dataease.defeign.permissions.user;

import io.dataease.api.permissions.user.api.UserApi;
import io.dataease.feign.DeFeign;

/**
 * 用户管理Feign客户端服务接口
 * 用于远程调用xpack-permissions服务中的用户管理功能
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>作为用户API的Feign客户端实现</li>
 *   <li>提供对xpack-permissions服务用户模块的远程访问</li>
 *   <li>支持用户信息的增删改查操作</li>
 *   <li>提供用户权限和角色管理功能</li>
 * </ul>
 *
 * <p>Feign配置说明：</p>
 * <ul>
 *   <li><b>服务名称：</b>xpack-permissions - 目标服务的注册名称</li>
 *   <li><b>请求路径：</b>/user - 用户管理相关接口的统一路径前缀</li>
 * </ul>
 *
 * <p>支持的用户管理功能：</p>
 * <ul>
 *   <li>用户信息的CRUD操作</li>
 *   <li>用户角色分配和管理</li>
 *   <li>用户权限查询和验证</li>
 *   <li>用户状态管理（启用/禁用）</li>
 * </ul>
 *
 * <p>继承的API接口：</p>
 * <ul>
 *   <li>{@link UserApi} - 用户管理相关的所有API接口定义</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * @Service
 * public class UserManagementService {
 *
 *     @Resource
 *     private UserFeignService userFeignService;
 *
 *     public UserInfo getUserById(Long userId) {
 *         // 远程获取用户信息
 *         return userFeignService.findById(userId);
 *     }
 *
 *     public boolean hasPermission(Long userId, String resource) {
 *         // 检查用户权限
 *         return userFeignService.checkPermission(userId, resource);
 *     }
 *
 *     public void updateUserRole(Long userId, List<String> roles) {
 *         // 更新用户角色
 *         userFeignService.updateUserRoles(userId, roles);
 *     }
 * }
 * }</pre>
 *
 * @see UserApi
 * @see DeFeign
 */
@DeFeign(value = "xpack-permissions", path = "/user")
public interface UserFeignService extends UserApi {
}
