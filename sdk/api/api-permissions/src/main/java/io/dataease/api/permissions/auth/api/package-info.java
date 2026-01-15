/**
 * 权限管理 API 接口层
 *
 * <p>本包定义了权限管理的所有 RESTful 接口，分为对外接口和内部交互接口两类。</p>
 *
 * <h2>接口分类</h2>
 *
 * <h3>1. 对外接口 (AuthApi)</h3>
 * <p>{@link io.dataease.api.permissions.auth.api.AuthApi} - 提供给前端的权限管理接口</p>
 * <ul>
 *   <li>查询资源树</li>
 *   <li>查询和保存业务权限</li>
 *   <li>查询和保存菜单权限</li>
 *   <li>批量授权</li>
 * </ul>
 *
 * <h3>2. 内部接口 (InteractiveAuthApi)</h3>
 * <p>{@link io.dataease.api.permissions.auth.api.InteractiveAuthApi} - 提供给系统内部模块的权限交互接口</p>
 * <ul>
 *   <li>资源同步：创建、更新、删除、移动资源</li>
 *   <li>权限校验：检查用户是否有权限</li>
 *   <li>数据查询：查询有权限的资源 ID 列表</li>
 *   <li>批量授权：内部批量授权接口</li>
 * </ul>
 *
 * <h2>接口访问路径</h2>
 * <ul>
 *   <li>对外接口基础路径：{@code /api/auth}</li>
 *   <li>内部接口基础路径：{@code /api/interactiveAuth}</li>
 * </ul>
 *
 * <h2>权限控制</h2>
 * <p>对外接口需要用户登录认证（JWT Token），内部接口通常用于服务间调用。</p>
 *
 * <h2>使用建议</h2>
 * <ul>
 *   <li><strong>前端页面</strong>：使用 AuthApi 进行权限管理</li>
 *   <li><strong>后端服务</strong>：使用 InteractiveAuthApi 进行资源同步和权限校验</li>
 *   <li><strong>批量操作</strong>：优先使用批量接口，减少网络开销</li>
 * </ul>
 *
 * @see io.dataease.api.permissions.auth.dto
 * @see io.dataease.api.permissions.auth.vo
 * @since 1.0
 */
package io.dataease.api.permissions.auth.api;
