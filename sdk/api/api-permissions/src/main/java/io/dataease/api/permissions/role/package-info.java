/**
 * 角色管理模块
 *
 * <h2>模块概述</h2>
 * <p>本模块是 DataEase 权限系统中的核心模块之一,提供基于角色的访问控制（RBAC）功能。
 * 角色是连接用户和权限的桥梁,通过为用户分配角色,用户可以继承角色所拥有的所有权限。</p>
 *
 * <h2>核心功能</h2>
 * <ul>
 *   <li><strong>角色生命周期管理</strong>:创建、编辑、删除、查询角色信息</li>
 *   <li><strong>用户绑定管理</strong>:为角色绑定/解绑组织内用户和组织外用户</li>
 *   <li><strong>角色查询</strong>:支持关键字搜索、按组织查询、查询用户可选/已选角色</li>
 *   <li><strong>角色复制</strong>:快速复制已有角色及其权限配置</li>
 *   <li><strong>组织隔离</strong>:支持组织级别的角色管理,不同组织的角色相互隔离</li>
 * </ul>
 *
 * <h2>角色模型</h2>
 *
 * <h3>角色类型</h3>
 * <pre>
 * ┌──────────────────────────────────────────────┐
 * │            角色分类                           │
 * ├──────────────────────────────────────────────┤
 * │ 1. 系统角色: 系统内置角色,不可删除           │
 * │    - 系统管理员: 拥有所有系统权限            │
 * │    - 组织管理员: 管理组织内资源和用户        │
 * │                                              │
 * │ 2. 自定义角色: 用户创建的业务角色            │
 *     - 根据业务需求自定义权限配置              │
 * │    - 支持复制和修改                          │
 * └──────────────────────────────────────────────┘
 * </pre>
 *
 * <h3>角色与用户的关系</h3>
 * <pre>
 * ┌─────────┐      ┌─────────┐      ┌─────────┐
 * │  用户A   │ ─→  │  角色1   │ ─→  │  权限集  │
 * └─────────┘      └─────────┘      └─────────┘
 *                       ↑
 *                       │ 继承
 *                       │
 * ┌─────────┐      ┌─────────┐
 * │  用户B   │ ─→  │  角色1   │
 * └─────────┘      └─────────┘
 *
 * 说明:
 * - 多个用户可以分配同一个角色
 * - 一个用户可以拥有多个角色
 * - 用户的最终权限 = 所有角色权限的并集
 * </pre>
 *
 * <h2>包结构</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.role.api} - API 接口定义层
 *     <ul>
 *       <li>{@link io.dataease.api.permissions.role.api.RoleApi} - 角色管理接口</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.role.dto} - 数据传输对象
 *     <ul>
 *       <li>角色管理类:RoleCreator、RoleEditor、RoleRequest、RoleCopyRequest</li>
 *       <li>用户绑定类:MountUserRequest、MountExternalUserRequest、UnmountUserRequest、UserRequest</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.role.vo} - 视图对象
 *     <ul>
 *       <li>RoleVO:角色基本信息视图</li>
 *       <li>RoleDetailVO:角色详细信息视图</li>
 *       <li>ExternalUserVO:组织外用户视图</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>使用场景</h2>
 *
 * <h3>1. 创建业务角色</h3>
 * <pre>{@code
 * POST /api/role/create
 * Content-Type: application/json
 *
 * {
 *   "name": "数据分析师",
 *   "type": 1,                // 角色类型:自定义角色
 *   "description": "负责数据分析和报表制作"
 * }
 *
 * Response: 1001  // 返回角色ID
 * }</pre>
 *
 * <h3>2. 为角色绑定用户</h3>
 * <pre>{@code
 * POST /api/role/mountUser
 * Content-Type: application/json
 *
 * {
 *   "rid": 1001,              // 角色ID
 *   "userIds": [10, 20, 30]   // 用户ID列表
 * }
 * }</pre>
 *
 * <h3>3. 查询用户可选角色</h3>
 * <pre>{@code
 * POST /api/role/user/option
 * Content-Type: application/json
 *
 * {
 *   "userId": 10,             // 用户ID
 *   "keyword": ""             // 搜索关键字(可选)
 * }
 *
 * Response:
 * [
 *   {
 *     "id": 1001,
 *     "name": "数据分析师",
 *     "type": 1,
 *     "description": "负责数据分析和报表制作",
 *     "createTime": 1705123456789
 *   }
 * ]
 * }</pre>
 *
 * <h3>4. 复制角色</h3>
 * <pre>{@code
 * POST /api/role/copy
 * Content-Type: application/json
 *
 * {
 *   "sourceRoleId": 1001,     // 源角色ID
 *   "name": "高级数据分析师", // 新角色名称
 *   "description": "在数据分析师基础上增加高级权限"
 * }
 * }</pre>
 *
 * <h3>5. 查询角色详情</h3>
 * <pre>{@code
 * GET /api/role/detail/1001
 *
 * Response:
 * {
 *   "id": 1001,
 *   "name": "数据分析师",
 *   "type": 1,
 *   "description": "负责数据分析和报表制作",
 *   "createTime": 1705123456789,
 *   "creator": "admin",
 *   "userCount": 15,          // 拥有此角色的用户数量
 *   "permissions": [...]      // 角色权限列表
 * }
 * }</pre>
 *
 * <h3>6. 解绑用户前查询影响范围</h3>
 * <pre>{@code
 * POST /api/role/beforeUnmountInfo
 * Content-Type: application/json
 *
 * {
 *   "rid": 1001,              // 角色ID
 *   "userId": 10              // 用户ID
 * }
 *
 * Response: 5  // 解绑后该用户将失去5个资源的访问权限
 * }</pre>
 *
 * <h2>实现位置</h2>
 * <p>本模块的接口实现位于:</p>
 * <ul>
 *   <li><strong>单机版/社区版</strong>: {@code core-backend/src/main/java/io/dataease/commons/permission/}
 *     <ul>
 *       <li>controller: RoleController</li>
 *       <li>service: RoleService、RoleServiceImpl</li>
 *     </ul>
 *   </li>
 *   <li><strong>企业版</strong>: 分布式模块中的对应实现</li>
 * </ul>
 *
 * <h2>数据模型</h2>
 * <p>角色系统涉及的主要数据表:</p>
 * <ul>
 *   <li><strong>sys_role</strong>:角色表,存储角色基本信息</li>
 *   <li><strong>sys_user_role</strong>:用户角色关系表,存储用户和角色的绑定关系</li>
 *   <li><strong>auth_detail</strong>:权限详情表,存储角色的权限配置</li>
 * </ul>
 *
 * <h2>权限计算规则</h2>
 * <ol>
 *   <li><strong>多角色权限合并</strong>:用户拥有多个角色时,最终权限 = 所有角色权限的并集</li>
 *   <li><strong>权限继承</strong>:用户继承其所属角色的所有权限</li>
 *   <li><strong>权限优先级</strong>:管理权限 > 使用权限,授权权限是特殊权限</li>
 *   <li><strong>组织隔离</strong>:不同组织的角色权限相互独立</li>
 * </ol>
 *
 * <h2>安全说明</h2>
 * <ul>
 *   <li><strong>角色操作权限</strong>:只有管理员和角色管理员可以创建、编辑、删除角色</li>
 *   <li><strong>用户绑定权限</strong>:需要对角色有管理权限才能绑定/解绑用户</li>
 *   <li><strong>系统角色保护</strong>:系统内置角色不允许删除和修改类型</li>
 *   <li><strong>操作审计</strong>:所有角色变更操作都记录审计日志</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>删除角色前需要确认该角色下没有绑定用户,或先解绑所有用户</li>
 *   <li>解绑用户时需要注意该用户是否还有其他角色,避免用户失去所有权限</li>
 *   <li>复制角色时,权限配置会一并复制,但用户绑定关系不会复制</li>
 *   <li>系统管理员角色是特殊角色,始终拥有所有权限</li>
 *   <li>组织外用户绑定功能用于跨组织协作,需要谨慎使用</li>
 * </ol>
 *
 * <h2>最佳实践</h2>
 * <ul>
 *   <li><strong>职责分离</strong>:根据业务职责创建不同的角色,避免权限过度集中</li>
 *   <li><strong>最小权限原则</strong>:角色只赋予完成工作所需的最小权限集合</li>
 *   <li><strong>定期审查</strong>:定期审查角色的权限配置和用户分配,及时调整</li>
 *   <li><strong>命名规范</strong>:角色命名应清晰表达其职责和权限范围</li>
 *   <li><strong>文档记录</strong>:为每个角色添加清晰的描述,说明其用途和权限</li>
 * </ul>
 *
 * @author DataEase Team
 * @since 1.0
 * @see io.dataease.api.permissions.auth
 * @see io.dataease.api.permissions.user
 * @see io.dataease.api.permissions.org
 */
package io.dataease.api.permissions.role;
