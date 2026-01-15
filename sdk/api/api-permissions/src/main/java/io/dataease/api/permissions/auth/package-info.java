/**
 * 权限授权管理模块
 *
 * <h2>模块概述</h2>
 * <p>本模块是 DataEase 权限系统的核心模块，提供完整的权限管理功能。
 * 采用 RBAC (Role-Based Access Control) 模型，支持对业务资源和菜单的细粒度权限控制。</p>
 *
 * <h2>核心功能</h2>
 * <ul>
 *   <li><strong>业务权限管理</strong>：管理数据集、仪表板等业务资源的权限</li>
 *   <li><strong>菜单权限管理</strong>：控制用户可以访问的系统菜单</li>
 *   <li><strong>权限查询</strong>：查询用户对特定资源的权限</li>
 *   <li><strong>权限校验</strong>：验证用户是否有权执行某项操作</li>
 *   <li><strong>批量授权</strong>：支持一次性为多个对象授予权限</li>
 *   <li><strong>资源同步</strong>：业务资源变更时自动同步权限数据</li>
 * </ul>
 *
 * <h2>权限模型</h2>
 *
 * <h3>三个核心维度</h3>
 * <pre>
 * ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
 * │   对象维度   │ ←→ │   资源维度   │ ←→ │   目标维度   │
 * │ (用户/角色)  │     │  (数据集等)  │     │  (菜单/按钮) │
 * └─────────────┘     └─────────────┘     └─────────────┘
 *       ↓                    ↓                    ↓
 *   谁(Who)          对什么(What)         有什么权限(How)
 * </pre>
 *
 * <h3>权限类型</h3>
 * <ul>
 *   <li><strong>使用权限 (0)</strong>：查看和使用资源的基本权限</li>
 *   <li><strong>管理权限 (1)</strong>：管理资源的高级权限，包含使用权限</li>
 *   <li><strong>授权权限 (2)</strong>：可以将权限授予其他用户</li>
 * </ul>
 *
 * <h3>权限继承</h3>
 * <p>权限系统支持多层级继承：</p>
 * <ol>
 *   <li><strong>直接权限</strong>：直接授予用户或角色的权限</li>
 *   <li><strong>角色继承</strong>：用户继承其所属角色的权限</li>
 *   <li><strong>父级继承</strong>：子资源继承父资源的权限</li>
 * </ol>
 *
 * <h2>包结构</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.auth.api} - API 接口定义层
 *     <ul>
 *       <li>{@link io.dataease.api.permissions.auth.api.AuthApi} - 对外权限管理接口</li>
 *       <li>{@link io.dataease.api.permissions.auth.api.InteractiveAuthApi} - 内部资源交互接口</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.auth.dto} - 数据传输对象
 *     <ul>
 *       <li>查询请求类：BusiPermissionRequest、MenuPermissionRequest</li>
 *       <li>编辑器类：BusiPerEditor、MenuPerEditor</li>
 *       <li>资源管理类：BusiResourceCreator、BusiResourceEditor、BusiResourceMover</li>
 *       <li>批量授权类：BusiBatchAuthorizeRequest、BusiBatchAuthorizeNode</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.auth.vo} - 视图对象
 *     <ul>
 *       <li>ResourceVO、ResourceNodeVO：资源节点视图</li>
 *       <li>PermissionVO、PermissionItem：权限视图</li>
 *       <li>PermissionValVO、PermissionOrigin：权限详情</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>使用场景</h2>
 *
 * <h3>1. 授予用户数据集权限</h3>
 * <pre>{@code
 * POST /api/auth/saveBusi
 * Content-Type: application/json
 *
 * {
 *   "busiEditor": {
 *     "id": 1001,              // 数据集 ID
 *     "authTargetType": 0,     // 目标类型：用户
 *     "targetIds": [10, 20],   // 用户 ID 列表
 *     "authType": 0,           // 权限类型：使用权限
 *     "extraFlag": true,       // 附加权限
 *     "columnPermissions": [], // 列权限（可选）
 *     "rowPermissions": []     // 行权限（可选）
 *   }
 * }
 * }</pre>
 *
 * <h3>2. 查询用户对资源的权限</h3>
 * <pre>{@code
 * POST /api/auth/queryBusi
 * Content-Type: application/json
 *
 * {
 *   "busiId": 1001,          // 数据集 ID
 *   "authTargetType": 0,     // 目标类型：用户
 *   "targetId": 10           // 用户 ID
 * }
 *
 * Response:
 * {
 *   "resourceId": 1001,
 *   "resourceName": "销售数据集",
 *   "pid": 0,
 *   "permissions": {
 *     "1": {                 // 菜单 ID
 *       "authType": 0,       // 使用权限
 *       "extraFlag": true,
 *       "columnPermissions": [],
 *       "rowPermissions": []
 *     }
 *   }
 * }
 * }</pre>
 *
 * <h3>3. 批量授权</h3>
 * <pre>{@code
 * POST /api/auth/batchAuth
 * Content-Type: application/json
 *
 * {
 *   "authTargetType": 1,           // 目标类型：角色
 *   "targetIds": [1, 2],           // 角色 ID 列表
 *   "authNodes": [
 *     {
 *       "authType": 0,             // 权限类型：使用权限
 *       "extraFlag": false,
 *       "targetIds": [1, 2, 3],    // 菜单 ID 列表
 *       "resourceIds": [100, 101]  // 资源 ID 列表
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <h3>4. 资源创建时同步权限</h3>
 * <pre>{@code
 * POST /api/interactiveAuth/syncResource
 * Content-Type: application/json
 *
 * {
 *   "resourceId": 1002,        // 新资源 ID
 *   "pid": 1000,               // 父资源 ID
 *   "resourceName": "Q1报表", // 资源名称
 *   "resourceType": "dataset"  // 资源类型
 * }
 * }</pre>
 *
 * <h3>5. 权限校验</h3>
 * <pre>{@code
 * POST /api/interactiveAuth/checkAuth
 * Content-Type: application/json
 *
 * {
 *   "busiId": 1001,        // 资源 ID
 *   "authType": 0,         // 需要的权限类型
 *   "targetId": 1          // 菜单 ID
 * }
 *
 * Response: true/false
 * }</pre>
 *
 * <h2>实现位置</h2>
 * <p>本模块的接口实现位于：</p>
 * <ul>
 *   <li><strong>单机版/社区版</strong>: {@code core-backend/src/main/java/io/dataease/commons/permission/}
 *     <ul>
 *       <li>controller: AuthController、InteractiveAuthController</li>
 *       <li>service: AuthService、InteractiveAuthService</li>
 *     </ul>
 *   </li>
 *   <li><strong>企业版</strong>: 分布式模块中的对应实现</li>
 * </ul>
 *
 * <h2>数据模型</h2>
 * <p>权限系统涉及的主要数据表：</p>
 * <ul>
 *   <li><strong>auth_resource</strong>：资源表，存储所有可授权的资源</li>
 *   <li><strong>auth_detail</strong>：权限详情表，存储具体的权限配置</li>
 *   <li><strong>auth_mapping</strong>：权限映射表，关联对象、资源和目标</li>
 * </ul>
 *
 * <h2>权限计算规则</h2>
 * <ol>
 *   <li><strong>权限合并</strong>：同一用户的多个权限来源取最高权限</li>
 *   <li><strong>继承规则</strong>：子资源默认继承父资源的权限</li>
 *   <li><strong>显式优先</strong>：直接授予的权限优先于继承的权限</li>
 *   <li><strong>拒绝优先</strong>：如果存在明确的拒绝权限，则优先拒绝</li>
 * </ol>
 *
 * <h2>安全说明</h2>
 * <ul>
 *   <li><strong>最小权限原则</strong>：默认无权限，需要明确授权</li>
 *   <li><strong>权限分离</strong>：使用、管理、授权权限分离</li>
 *   <li><strong>操作审计</strong>：所有权限变更都记录操作日志</li>
 *   <li><strong>缓存策略</strong>：权限数据缓存，变更后自动刷新</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>权限变更后需要刷新用户的权限缓存</li>
 *   <li>删除资源时需要同步删除相关的权限数据</li>
 *   <li>移动资源时需要重新计算继承的权限</li>
 *   <li>批量授权操作是事务性的，要么全部成功，要么全部失败</li>
 *   <li>授权权限是特殊权限，只有管理员和资源创建者默认拥有</li>
 * </ol>
 *
 * @author DataEase Team
 * @since 1.0
 * @see io.dataease.api.permissions.role
 * @see io.dataease.api.permissions.user
 * @see io.dataease.api.permissions.org
 */
package io.dataease.api.permissions.auth;
