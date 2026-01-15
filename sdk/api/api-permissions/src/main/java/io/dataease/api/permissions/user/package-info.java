/**
 * 用户管理模块
 *
 * <h2>模块概述</h2>
 * <p>本模块是 DataEase 权限系统的核心模块之一,提供用户账号的完整生命周期管理。
 * 支持本地用户和第三方平台用户,提供用户信息管理、权限控制、组织切换、多因素认证等功能。</p>
 *
 * <h2>核心功能</h2>
 * <ul>
 *   <li><strong>用户生命周期管理</strong>:创建、编辑、删除、查询用户账号</li>
 *   <li><strong>账号管理</strong>:密码重置、状态启用/禁用、语言切换</li>
 *   <li><strong>权限管理</strong>:角色绑定、组织切换、权限查询</li>
 *   <li><strong>批量操作</strong>:批量导入、批量删除用户</li>
 *   <li><strong>第三方集成</strong>:LDAP、OAuth、CAS 等第三方平台用户管理</li>
 *   <li><strong>安全功能</strong>:多因素认证(MFA)、密码策略、登录审计</li>
 *   <li><strong>个人中心</strong>:个人信息管理、偏好设置、第三方账号绑定</li>
 * </ul>
 *
 * <h2>用户模型</h2>
 *
 * <h3>用户类型</h3>
 * <pre>
 * ┌────────────────────────────────────────────────┐
 * │              用户分类                          │
 * ├────────────────────────────────────────────────┤
 * │ 1. 本地用户                                    │
 * │    - 系统内创建的用户                          │
 * │    - 使用账号密码登录                          │
 * │    - 密码存储在本地数据库                      │
 * │                                                │
 * │ 2. 第三方平台用户                              │
 * │    - LDAP 用户: 从 LDAP 服务器同步            │
 * │    - OAuth 用户: 通过 OAuth 2.0 认证          │
 * │    - CAS 用户: 通过 CAS 单点登录              │
 * │    - 钉钉/企业微信用户: 企业应用集成           │
 * │                                                │
 * │ 3. 混合用户                                    │
 * │    - 可以同时绑定多个第三方平台                │
 * │    - 支持多种登录方式                          │
 * └────────────────────────────────────────────────┘
 * </pre>
 *
 * <h3>用户状态</h3>
 * <ul>
 *   <li><strong>启用 (1)</strong>:正常状态,可以登录系统</li>
 *   <li><strong>禁用 (0)</strong>:被禁用,无法登录系统</li>
 * </ul>
 *
 * <h3>用户与组织、角色的关系</h3>
 * <pre>
 * ┌─────────┐      ┌─────────┐      ┌─────────┐
 * │  组织    │ ←─  │  用户    │ ─→  │  角色    │
 * └─────────┘      └─────────┘      └─────────┘
 *      │                │                 │
 *      │                │                 │
 *      ↓                ↓                 ↓
 *  组织隔离         个人信息           权限继承
 *
 * 说明:
 * - 用户属于一个主组织,可以切换到其他有权限的组织
 * - 用户可以拥有多个角色,继承所有角色的权限
 * - 不同组织的用户数据相互隔离
 * </pre>
 *
 * <h2>包结构</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.user.api} - API 接口定义层
 *     <ul>
 *       <li>{@link io.dataease.api.permissions.user.api.UserApi} - 用户管理接口</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.user.dto} - 数据传输对象
 *     <ul>
 *       <li>用户管理类:UserCreator、UserEditor、UserGridRequest、PlatformUserCreator</li>
 *       <li>账号管理类:ModifyPwdRequest、EnableSwitchRequest、LangSwitchRequest</li>
 *       <li>第三方集成类:UserBindRequest、AdminBindRequest、UserReciRequest</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.user.bo} - 业务对象
 *     <ul>
 *       <li>PlatformUser:第三方平台用户业务对象</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.user.vo} - 视图对象
 *     <ul>
 *       <li>列表视图:UserGridVO、UserItemVO、UserItem</li>
 *       <li>表单视图:UserFormVO</li>
 *       <li>当前用户:CurUserVO、CurIpVO</li>
 *       <li>导入相关:UserImportVO</li>
 *       <li>角色信息:UserGridRoleItem</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>使用场景</h2>
 *
 * <h3>1. 创建本地用户</h3>
 * <pre>{@code
 * POST /api/user/create
 * Content-Type: application/json
 *
 * {
 *   "account": "zhangsan",
 *   "name": "张三",
 *   "email": "zhangsan@example.com",
 *   "phone": "13800138000",
 *   "roleIds": [1001, 1002],    // 角色ID列表
 *   "enabled": 1                // 启用状态
 * }
 *
 * Response: 10001  // 返回用户ID
 * }</pre>
 *
 * <h3>2. 批量导入用户</h3>
 * <pre>{@code
 * // 1. 下载导入模板
 * POST /api/user/excelTemplate
 *
 * // 2. 填写模板后上传
 * POST /api/user/batchImport
 * Content-Type: multipart/form-data
 *
 * file: users.xlsx
 *
 * Response:
 * {
 *   "successCount": 95,         // 成功数量
 *   "failCount": 5,             // 失败数量
 *   "errorKey": "import_123456" // 失败记录下载key
 * }
 *
 * // 3. 下载失败记录
 * GET /api/user/errorRecord/import_123456
 * }</pre>
 *
 * <h3>3. 切换组织</h3>
 * <pre>{@code
 * POST /api/user/switch/2001
 *
 * Response:
 * {
 *   "accessToken": "eyJhbGciOiJIUzI1NiIs...",
 *   "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
 *   "expiresIn": 7200
 * }
 * }</pre>
 *
 * <h3>4. 修改个人密码</h3>
 * <pre>{@code
 * POST /api/user/modifyPwd
 * Content-Type: application/json
 *
 * {
 *   "oldPassword": "old123456",
 *   "newPassword": "new123456",
 *   "confirmPassword": "new123456"
 * }
 * }</pre>
 *
 * <h3>5. 绑定多因素认证(MFA)</h3>
 * <pre>{@code
 * // 1. 获取二维码
 * GET /api/user/mfaQr
 *
 * Response:
 * {
 *   "qrCodeUrl": "data:image/png;base64,...",
 *   "secret": "JBSWY3DPEHPK3PXP"
 * }
 *
 * // 2. 扫描二维码后,输入验证码绑定
 * POST /api/user/mfaBind
 * Content-Type: application/json
 *
 * {
 *   "code": "123456"
 * }
 * }</pre>
 *
 * <h3>6. 查询用户列表(分页)</h3>
 * <pre>{@code
 * POST /api/user/pager/1/20
 * Content-Type: application/json
 *
 * {
 *   "keyword": "张",            // 搜索关键字
 *   "enabled": 1,               // 状态筛选
 *   "roleId": 1001              // 角色筛选
 * }
 *
 * Response:
 * {
 *   "total": 156,
 *   "current": 1,
 *   "size": 20,
 *   "records": [
 *     {
 *       "id": 10001,
 *       "account": "zhangsan",
 *       "name": "张三",
 *       "email": "zhangsan@example.com",
 *       "enabled": 1,
 *       "roles": [
 *         {"id": 1001, "name": "数据分析师"}
 *       ]
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <h2>实现位置</h2>
 * <p>本模块的接口实现位于:</p>
 * <ul>
 *   <li><strong>单机版/社区版</strong>: {@code core-backend/src/main/java/io/dataease/commons/permission/}
 *     <ul>
 *       <li>controller: UserController</li>
 *       <li>service: UserService、UserServiceImpl</li>
 *     </ul>
 *   </li>
 *   <li><strong>企业版</strong>: 分布式模块中的对应实现</li>
 * </ul>
 *
 * <h2>数据模型</h2>
 * <p>用户系统涉及的主要数据表:</p>
 * <ul>
 *   <li><strong>sys_user</strong>:用户表,存储用户基本信息</li>
 *   <li><strong>sys_user_role</strong>:用户角色关系表</li>
 *   <li><strong>sys_user_assist</strong>:用户辅助信息表,存储密码、MFA密钥等敏感信息</li>
 *   <li><strong>sys_user_platform</strong>:第三方平台用户绑定表</li>
 * </ul>
 *
 * <h2>安全策略</h2>
 *
 * <h3>密码策略</h3>
 * <ul>
 *   <li><strong>加密存储</strong>:密码使用 BCrypt 算法加密存储</li>
 *   <li><strong>强度要求</strong>:密码长度不少于8位,包含大小写字母、数字</li>
 *   <li><strong>定期更换</strong>:支持配置密码过期策略</li>
 *   <li><strong>历史密码</strong>:新密码不能与最近N次使用过的密码相同</li>
 * </ul>
 *
 * <h3>登录安全</h3>
 * <ul>
 *   <li><strong>登录锁定</strong>:连续失败N次后锁定账号</li>
 *   <li><strong>会话管理</strong>:支持单设备登录或多设备登录</li>
 *   <li><strong>强制退出</strong>:管理员可以强制用户下线</li>
 *   <li><strong>登录审计</strong>:记录所有登录尝试和操作日志</li>
 * </ul>
 *
 * <h3>多因素认证(MFA)</h3>
 * <ul>
 *   <li><strong>TOTP 算法</strong>:基于时间的一次性密码</li>
 *   <li><strong>支持的认证器</strong>:Google Authenticator、Microsoft Authenticator 等</li>
 *   <li><strong>备用码</strong>:支持生成备用恢复码</li>
 *   <li><strong>强制启用</strong>:支持为特定角色强制启用 MFA</li>
 * </ul>
 *
 * <h2>第三方平台集成</h2>
 *
 * <h3>支持的平台</h3>
 * <ul>
 *   <li><strong>LDAP/AD</strong>:企业目录服务集成</li>
 *   <li><strong>OAuth 2.0</strong>:支持 GitHub、GitLab 等 OAuth 登录</li>
 *   <li><strong>CAS</strong>:企业单点登录</li>
 *   <li><strong>钉钉</strong>:钉钉企业应用集成</li>
 *   <li><strong>企业微信</strong>:企业微信应用集成</li>
 *   <li><strong>飞书</strong>:飞书企业应用集成</li>
 * </ul>
 *
 * <h3>集成方式</h3>
 * <ol>
 *   <li>用户首次通过第三方平台登录时,自动创建系统账号</li>
 *   <li>支持将已有系统账号绑定到第三方平台</li>
 *   <li>一个系统账号可以绑定多个第三方平台</li>
 *   <li>支持解绑第三方平台账号</li>
 * </ol>
 *
 * <h2>批量操作</h2>
 *
 * <h3>批量导入规则</h3>
 * <ul>
 *   <li>支持 Excel (.xlsx) 格式</li>
 *   <li>必填字段:账号、姓名</li>
 *   <li>可选字段:邮箱、手机、角色</li>
 *   <li>账号不能重复,重复则跳过</li>
 *   <li>导入失败的记录生成错误报告</li>
 * </ul>
 *
 * <h3>批量删除规则</h3>
 * <ul>
 *   <li>系统管理员账号不能删除</li>
 *   <li>当前登录用户不能删除自己</li>
 *   <li>删除前需要确认操作</li>
 *   <li>删除操作记录审计日志</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>创建用户时,如果不指定密码,使用系统默认密码</li>
 *   <li>禁用用户后,该用户的所有会话将被强制下线</li>
 *   <li>切换组织时会生成新的访问令牌,旧令牌失效</li>
 *   <li>删除用户会同时删除用户的所有关联数据(角色、权限等)</li>
 *   <li>第三方平台用户的密码存储在第三方平台,本地不存储密码</li>
 *   <li>MFA 绑定后,登录时必须提供动态验证码</li>
 *   <li>批量导入时,建议单次不超过1000条记录</li>
 * </ol>
 *
 * <h2>最佳实践</h2>
 * <ul>
 *   <li><strong>账号规范</strong>:统一账号命名规则,如使用工号或邮箱前缀</li>
 *   <li><strong>角色分配</strong>:遵循最小权限原则,只分配必要的角色</li>
 *   <li><strong>定期审查</strong>:定期审查用户状态,及时禁用离职人员账号</li>
 *   <li><strong>安全培训</strong>:要求用户设置强密码,启用 MFA</li>
 *   <li><strong>第三方集成</strong>:优先使用企业统一的身份认证平台</li>
 *   <li><strong>审计日志</strong>:定期检查用户操作日志,发现异常行为</li>
 * </ul>
 *
 * @author DataEase Team
 * @since 1.0
 * @see io.dataease.api.permissions.role
 * @see io.dataease.api.permissions.auth
 * @see io.dataease.api.permissions.org
 * @see io.dataease.api.permissions.login
 */
package io.dataease.api.permissions.user;
