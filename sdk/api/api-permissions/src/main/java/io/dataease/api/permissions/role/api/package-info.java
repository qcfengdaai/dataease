/**
 * 角色管理 API 接口层
 *
 * <p>本包定义了角色管理的所有 RESTful 接口,提供角色的完整生命周期管理功能。</p>
 *
 * <h2>核心接口</h2>
 *
 * <h3>RoleApi - 角色管理接口</h3>
 * <p>{@link io.dataease.api.permissions.role.api.RoleApi} - 提供角色管理的所有对外接口</p>
 *
 * <h3>主要功能分类</h3>
 *
 * <h4>1. 角色基本操作</h4>
 * <ul>
 *   <li><strong>创建角色</strong>: {@code POST /api/role/create}</li>
 *   <li><strong>编辑角色</strong>: {@code POST /api/role/edit}</li>
 *   <li><strong>删除角色</strong>: {@code POST /api/role/delete/{rid}}</li>
 *   <li><strong>查询角色列表</strong>: {@code POST /api/role/query}</li>
 *   <li><strong>查询角色详情</strong>: {@code GET /api/role/detail/{rid}}</li>
 *   <li><strong>复制角色</strong>: {@code POST /api/role/copy}</li>
 * </ul>
 *
 * <h4>2. 用户绑定管理</h4>
 * <ul>
 *   <li><strong>绑定组织内用户</strong>: {@code POST /api/role/mountUser}</li>
 *   <li><strong>绑定组织外用户</strong>: {@code POST /api/role/mountExternalUser}</li>
 *   <li><strong>解绑用户</strong>: {@code POST /api/role/unMountUser}</li>
 *   <li><strong>查询组织外用户</strong>: {@code GET /api/role/searchExternalUser/{keyword}}</li>
 *   <li><strong>解绑前确认</strong>: {@code POST /api/role/beforeUnmountInfo}</li>
 * </ul>
 *
 * <h4>3. 角色查询接口</h4>
 * <ul>
 *   <li><strong>查询组织内角色</strong>: {@code POST /api/role/byCurOrg}</li>
 *   <li><strong>查询用户可选角色</strong>: {@code POST /api/role/user/option}</li>
 *   <li><strong>查询用户已选角色</strong>: {@code POST /api/role/user/selected}</li>
 *   <li><strong>根据组织ID查询角色</strong>: {@code GET /api/role/queryWithOid/{oid}} (内部接口)</li>
 * </ul>
 *
 * <h2>接口访问路径</h2>
 * <p>基础路径: {@code /api/role}</p>
 *
 * <h2>权限控制</h2>
 * <p>所有接口都通过 {@code @DePermit} 注解进行权限控制:</p>
 * <ul>
 *   <li><strong>查询权限</strong>: {@code m:read} - 需要模块读取权限</li>
 *   <li><strong>管理权限</strong>: {@code m:manage} 或 {@code {rid}:manage} - 需要模块管理权限或特定角色管理权限</li>
 * </ul>
 *
 * <h2>请求响应格式</h2>
 *
 * <h3>标准请求格式</h3>
 * <pre>{@code
 * POST /api/role/create
 * Content-Type: application/json
 * Authorization: Bearer {jwt_token}
 *
 * {
 *   "name": "角色名称",
 *   "type": 1,
 *   "description": "角色描述"
 * }
 * }</pre>
 *
 * <h3>标准响应格式</h3>
 * <pre>{@code
 * // 成功响应
 * {
 *   "code": 0,
 *   "data": {
 *     "id": 1001,
 *     "name": "角色名称",
 *     ...
 *   },
 *   "message": "success"
 * }
 *
 * // 错误响应
 * {
 *   "code": 500,
 *   "message": "角色名称已存在",
 *   "data": null
 * }
 * }</pre>
 *
 * <h2>使用建议</h2>
 * <ul>
 *   <li><strong>前端角色管理页面</strong>: 使用 RoleApi 的查询、创建、编辑、删除接口</li>
 *   <li><strong>用户管理页面</strong>: 使用 optionForUser 和 selectedForUser 查询用户角色</li>
 *   <li><strong>角色成员管理</strong>: 使用 mountUser、unMountUser 管理角色成员</li>
 *   <li><strong>跨组织协作</strong>: 使用 mountExternalUser 绑定组织外用户</li>
 *   <li><strong>批量操作</strong>: mountUser 支持批量绑定多个用户</li>
 * </ul>
 *
 * <h2>错误处理</h2>
 * <p>接口可能返回的常见错误:</p>
 * <ul>
 *   <li><strong>400 Bad Request</strong>: 请求参数不合法</li>
 *   <li><strong>401 Unauthorized</strong>: 未登录或登录已过期</li>
 *   <li><strong>403 Forbidden</strong>: 没有权限执行该操作</li>
 *   <li><strong>404 Not Found</strong>: 角色不存在</li>
 *   <li><strong>409 Conflict</strong>: 角色名称已存在</li>
 *   <li><strong>500 Internal Server Error</strong>: 服务器内部错误</li>
 * </ul>
 *
 * <h2>并发控制</h2>
 * <ul>
 *   <li>角色编辑操作支持乐观锁,防止并发修改</li>
 *   <li>用户绑定/解绑操作在事务中执行,保证数据一致性</li>
 *   <li>删除角色时会检查是否有用户绑定,有绑定则不允许删除</li>
 * </ul>
 *
 * <h2>性能优化</h2>
 * <ul>
 *   <li>角色列表查询支持分页,避免一次加载大量数据</li>
 *   <li>角色权限数据会被缓存,减少数据库查询</li>
 *   <li>批量绑定用户使用批量插入,提高性能</li>
 * </ul>
 *
 * @see io.dataease.api.permissions.role.dto
 * @see io.dataease.api.permissions.role.vo
 * @since 1.0
 */
package io.dataease.api.permissions.role.api;
