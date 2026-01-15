/**
 * 用户登录认证模块
 *
 * <h2>模块概述</h2>
 * <p>本模块提供 DataEase 系统的用户登录认证功能,支持多种登录方式和安全认证机制。
 * 登录是获取访问令牌(Token)的过程,成功登录后返回 JWT Token,后续所有 API 请求都需要携带该 Token 进行身份验证。
 * 系统支持本地账号密码登录、第三方平台登录(OIDC、LDAP、CAS)以及多因子认证(MFA)等多种安全登录方式。</p>
 *
 * <h2>核心功能</h2>
 * <ul>
 *   <li><strong>本地登录</strong>:使用用户名和密码进行认证
 *     <ul>
 *       <li>密码 RSA 加密传输</li>
 *       <li>账户状态检查(禁用/锁定)</li>
 *       <li>登录失败次数限制</li>
 *       <li>自动锁定机制</li>
 *     </ul>
 *   </li>
 *   <li><strong>第三方登录</strong>:通过外部认证平台登录
 *     <ul>
 *       <li>OIDC(OpenID Connect)登录</li>
 *       <li>LDAP(Lightweight Directory Access Protocol)登录</li>
 *       <li>CAS(Central Authentication Service)登录</li>
 *       <li>自动创建用户或匹配现有用户</li>
 *     </ul>
 *   </li>
 *   <li><strong>MFA 多因子认证</strong>:提供更高级别的安全保护
 *     <ul>
 *       <li>TOTP(Time-based One-Time Password)算法</li>
 *       <li>二维码扫描绑定</li>
 *       <li>动态验证码验证</li>
 *       <li>支持 Google Authenticator、Microsoft Authenticator 等认证器</li>
 *     </ul>
 *   </li>
 *   <li><strong>Token 管理</strong>:访问令牌的生命周期管理
 *     <ul>
 *       <li>JWT Token 生成</li>
 *       <li>Token 自动续期</li>
 *       <li>Token 过期控制</li>
 *       <li>Refresh Token 机制</li>
 *     </ul>
 *   </li>
 *   <li><strong>登出管理</strong>:安全退出登录状态
 *     <ul>
 *       <li>Token 失效处理</li>
 *       <li>会话清理</li>
 *       <li>登出日志记录</li>
 *     </ul>
 *   </li>
 *   <li><strong>密码管理</strong>:密码安全策略
 *     <ul>
 *       <li>初始密码强制修改</li>
 *       <li>过期密码强制修改</li>
 *       <li>密码复杂度要求</li>
 *       <li>密码历史记录</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>登录流程</h2>
 *
 * <h3>本地登录流程</h3>
 * <pre>
 * ┌──────────┐   ①提交账号密码   ┌──────────┐
 * │   用户   │ ─────────────→ │  前端    │
 * └──────────┘                  └──────────┘
 *                                    ↓ ②RSA加密
 *                                    ↓
 *                               ┌──────────┐
 *                               │  后端    │
 *                               └──────────┘
 *                                    ↓ ③验证账号
 *                                    ↓ ④检查状态
 *                                    ↓ ⑤验证密码
 *                                    ↓ ⑥检查MFA
 *                                    ↓
 *                               ┌──────────┐
 *                               │生成Token │
 *                               └──────────┘
 *                                    ↓ ⑦返回Token
 *                                    ↓
 * ┌──────────┐  ⑧存储Token    ┌──────────┐
 * │   用户   │ ←───────────── │  前端    │
 * └──────────┘                  └──────────┘
 * </pre>
 *
 * <h3>MFA 登录流程</h3>
 * <pre>
 * ┌──────────┐  ①账号密码验证  ┌──────────┐
 * │   用户   │ ─────────────→ │  系统    │
 * └──────────┘                  └──────────┘
 *      ↑                             ↓ ②检测MFA启用
 *      │                             ↓
 *      │         ③返回MFA二维码       ↓
 *      │ ←─────────────────────────┘
 *      ↓
 * ┌──────────┐
 * │认证器扫码│
 * └──────────┘
 *      ↓ ④生成动态验证码
 *      ↓
 * ┌──────────┐  ⑤输入验证码    ┌──────────┐
 * │   用户   │ ─────────────→ │  系统    │
 * └──────────┘                  └──────────┘
 *      ↑                             ↓ ⑥验证验证码
 *      │                             ↓
 *      │         ⑦返回Token          ↓
 *      │ ←─────────────────────────┘
 * </pre>
 *
 * <h3>第三方登录流程</h3>
 * <pre>
 * ┌──────────┐  ①点击第三方登录 ┌──────────┐
 * │   用户   │ ─────────────→ │ DataEase │
 * └──────────┘                  └──────────┘
 *      ↑                             ↓ ②重定向到第三方
 *      │                             ↓
 *      │                        ┌──────────┐
 *      │                        │第三方平台│
 *      │ ←─────────────────────┘
 *      ↓ ③在第三方平台认证
 *      │
 *      │         ④回调返回用户信息
 *      │ ──────────────────────→ ┌──────────┐
 *      │                          │ DataEase │
 *      │                          └──────────┘
 *      │                               ↓ ⑤匹配或创建用户
 *      │                               ↓
 *      │         ⑥返回Token            ↓
 *      │ ←─────────────────────────────┘
 * </pre>
 *
 * <h2>包结构</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.login.api} - API 接口定义层
 *     <ul>
 *       <li>{@link io.dataease.api.permissions.login.api.LoginApi} - 登录认证核心接口</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.login.dto} - 数据传输对象
 *     <ul>
 *       <li>PwdLoginDTO:账号密码登录参数</li>
 *       <li>MfaLoginDTO:MFA 登录参数</li>
 *       <li>AccountLockStatus:账户锁定状态</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.login.vo} - 视图对象
 *     <ul>
 *       <li>MfaQrVO:MFA 二维码视图</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>使用场景</h2>
 *
 * <h3>1. 本地账号密码登录</h3>
 * <pre>{@code
 * POST /api/login/localLogin
 * Content-Type: application/json
 *
 * {
 *   "username": "admin",
 *   "password": "RSA_ENCRYPTED_PASSWORD"  // 前端使用RSA公钥加密后的密码
 * }
 *
 * Response:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "exp": 1705123456789,  // 过期时间戳
 *   "userId": 1,
 *   "username": "admin"
 * }
 * }</pre>
 *
 * <h3>2. Token 续期</h3>
 * <pre>{@code
 * GET /api/login/refresh
 * Headers:
 *   Authorization: Bearer {current_token}
 *
 * Response:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // 新Token
 *   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "exp": 1705127056789  // 新的过期时间
 * }
 * }</pre>
 *
 * <h3>3. 获取 MFA 二维码</h3>
 * <pre>{@code
 * POST /api/mfa/qr/1
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *
 * Response:
 * {
 *   "qrCode": "data:image/png;base64,iVBORw0KGgoAAAANS...",  // Base64编码的二维码图片
 *   "secret": "JBSWY3DPEHPK3PXP",  // 密钥(用于手动输入)
 *   "issuer": "DataEase",
 *   "account": "admin@dataease.com"
 * }
 * }</pre>
 *
 * <h3>4. MFA 验证码登录</h3>
 * <pre>{@code
 * POST /api/mfa/login
 * Content-Type: application/json
 *
 * {
 *   "userId": 1,
 *   "secret": "JBSWY3DPEHPK3PXP",
 *   "code": "123456"  // 认证器生成的6位动态验证码
 * }
 *
 * Response:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "exp": 1705123456789
 * }
 * }</pre>
 *
 * <h3>5. 登出</h3>
 * <pre>{@code
 * GET /api/logout
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *
 * Response: 200 OK
 * }</pre>
 *
 * <h2>实现位置</h2>
 * <p>本模块的接口实现位于:</p>
 * <ul>
 *   <li><strong>单机版/社区版</strong>: {@code core-backend/src/main/java/io/dataease/auth/}
 *     <ul>
 *       <li>controller: LoginController</li>
 *       <li>service: LoginService、MfaService</li>
 *     </ul>
 *   </li>
 *   <li><strong>企业版</strong>: 分布式模块中的对应实现</li>
 * </ul>
 *
 * <h2>数据模型</h2>
 * <p>登录认证涉及的主要数据表:</p>
 * <ul>
 *   <li><strong>core_sys_user</strong>:用户表,存储用户账号和密码</li>
 *   <li><strong>core_login_log</strong>:登录日志表,记录所有登录行为</li>
 *   <li><strong>core_token_blacklist</strong>:Token 黑名单表,存储已失效的 Token</li>
 *   <li><strong>core_user_mfa</strong>:MFA 配置表,存储用户的 MFA 设置</li>
 *   <li><strong>core_account_lock</strong>:账户锁定表,记录账户锁定状态</li>
 * </ul>
 *
 * <h2>安全机制</h2>
 *
 * <h3>1. 密码安全</h3>
 * <ul>
 *   <li><strong>传输加密</strong>:前端使用 RSA 公钥加密密码,确保传输安全</li>
 *   <li><strong>存储加密</strong>:数据库中使用 BCrypt 算法加密存储密码</li>
 *   <li><strong>密码复杂度</strong>:要求密码长度、大小写、数字、特殊字符等</li>
 *   <li><strong>密码历史</strong>:记录最近 N 次密码,禁止重复使用</li>
 * </ul>
 *
 * <h3>2. 账户保护</h3>
 * <ul>
 *   <li><strong>失败限制</strong>:连续登录失败 N 次后自动锁定账户</li>
 *   <li><strong>锁定时长</strong>:账户锁定后,M 分钟后自动解锁</li>
 *   <li><strong>状态检查</strong>:检查账户是否被禁用、删除、锁定</li>
 *   <li><strong>并发限制</strong>:限制同一账户的最大并发登录数</li>
 * </ul>
 *
 * <h3>3. Token 安全</h3>
 * <ul>
 *   <li><strong>JWT 签名</strong>:使用 RS256 或 HS256 算法签名,防止篡改</li>
 *   <li><strong>过期控制</strong>:Token 默认有效期 2 小时,过期自动失效</li>
 *   <li><strong>续期机制</strong>:Token 即将过期时可以续期,避免频繁登录</li>
 *   <li><strong>黑名单机制</strong>:登出时将 Token 加入黑名单,立即失效</li>
 * </ul>
 *
 * <h3>4. MFA 安全</h3>
 * <ul>
 *   <li><strong>TOTP 算法</strong>:使用标准的 TOTP 算法生成动态验证码</li>
 *   <li><strong>时间窗口</strong>:验证码每 30 秒更新一次</li>
 *   <li><strong>防重放</strong>:每个验证码只能使用一次</li>
 *   <li><strong>容错机制</strong>:允许前后 1 个时间窗口的验证码(防止时间不同步)</li>
 * </ul>
 *
 * <h3>5. 审计日志</h3>
 * <ul>
 *   <li><strong>登录日志</strong>:记录所有登录尝试(成功和失败)</li>
 *   <li><strong>登出日志</strong>:记录所有登出操作</li>
 *   <li><strong>异常检测</strong>:检测异常登录行为(如异地登录、频繁登录)</li>
 *   <li><strong>日志分析</strong>:支持日志查询和统计分析</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li><strong>HTTPS 传输</strong>:生产环境必须使用 HTTPS,确保数据传输安全</li>
 *   <li><strong>密钥管理</strong>:JWT 签名密钥、RSA 密钥对必须妥善保管</li>
 *   <li><strong>Token 存储</strong>:前端建议使用 SessionStorage 存储 Token,避免 XSS 攻击</li>
 *   <li><strong>跨域配置</strong>:配置正确的 CORS 策略,防止跨域攻击</li>
 *   <li><strong>MFA 备份</strong>:启用 MFA 时,建议提供备份码功能,防止设备丢失</li>
 *   <li><strong>密码策略</strong>:根据企业安全要求配置密码策略</li>
 *   <li><strong>监控告警</strong>:监控异常登录行为,及时告警</li>
 * </ol>
 *
 * <h2>最佳实践</h2>
 * <ul>
 *   <li><strong>强密码策略</strong>:要求密码长度至少 8 位,包含大小写字母、数字和特殊字符</li>
 *   <li><strong>启用 MFA</strong>:为管理员账户强制启用 MFA</li>
 *   <li><strong>定期修改密码</strong>:设置密码有效期,定期强制修改</li>
 *   <li><strong>单点登录</strong>:大型企业建议使用 OIDC 等单点登录方案</li>
 *   <li><strong>Session 管理</strong>:实现完善的 Session 管理,支持强制登出</li>
 *   <li><strong>安全培训</strong>:对用户进行安全意识培训</li>
 * </ul>
 *
 * @author DataEase Team
 * @since 1.0
 * @see io.dataease.api.permissions.user
 * @see io.dataease.api.permissions.auth
 */
package io.dataease.api.permissions.login;
