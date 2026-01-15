/**
 * 用户登录认证接口层
 *
 * <p>本包定义了用户登录认证的所有 RESTful 接口,遵循 Spring MVC 规范。
 * 接口使用 OpenAPI 3.0 注解进行文档标注,支持 Knife4j 在线文档。</p>
 *
 * <h2>接口列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.login.api.LoginApi} - 登录认证核心接口
 *     <ul>
 *       <li>本地账号密码登录</li>
 *       <li>Token 续期</li>
 *       <li>第三方平台登录</li>
 *       <li>用户登出</li>
 *       <li>获取 MFA 二维码</li>
 *       <li>MFA 多因子认证登录</li>
 *       <li>修改无效密码</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>实现类位置</h2>
 * <p>接口实现类位于 core-backend 模块的 controller 包中:</p>
 * <ul>
 *   <li>{@code io.dataease.auth.controller.LoginController}</li>
 * </ul>
 *
 * <h2>访问路径</h2>
 * <ul>
 *   <li>本地登录:{@code /api/login/localLogin}</li>
 *   <li>Token 续期:{@code /api/login/refresh}</li>
 *   <li>第三方登录:{@code /api/login/platformLogin/{origin}}</li>
 *   <li>登出:{@code /api/logout}</li>
 *   <li>MFA 二维码:{@code /api/mfa/qr/{id}}</li>
 *   <li>MFA 登录:{@code /api/mfa/login}</li>
 * </ul>
 *
 * <h2>权限控制</h2>
 * <ul>
 *   <li><strong>登录接口</strong>:不需要认证,公开接口</li>
 *   <li><strong>续期接口</strong>:需要有效的 JWT Token</li>
 *   <li><strong>登出接口</strong>:需要有效的 JWT Token</li>
 *   <li><strong>MFA 接口</strong>:部分需要 Token,部分在登录过程中调用</li>
 * </ul>
 *
 * <h2>接口特点</h2>
 * <ul>
 *   <li><strong>RESTful 风格</strong>:遵循 REST API 设计规范</li>
 *   <li><strong>安全认证</strong>:多层次的安全认证机制</li>
 *   <li><strong>状态无关</strong>:使用 JWT Token,服务端无状态</li>
 *   <li><strong>多登录方式</strong>:支持本地、第三方、MFA 等多种方式</li>
 *   <li><strong>异常处理</strong>:统一的异常处理和错误返回</li>
 * </ul>
 *
 * <h2>接口安全</h2>
 * <ul>
 *   <li><strong>密码加密</strong>:密码使用 RSA 加密传输</li>
 *   <li><strong>Token 签名</strong>:JWT Token 使用密钥签名,防止篡改</li>
 *   <li><strong>HTTPS 传输</strong>:生产环境强制使用 HTTPS</li>
 *   <li><strong>防暴力破解</strong>:登录失败次数限制和账户锁定</li>
 *   <li><strong>操作审计</strong>:所有登录操作都记录日志</li>
 * </ul>
 *
 * <h2>错误处理</h2>
 * <p>登录接口可能返回的错误码:</p>
 * <ul>
 *   <li><strong>1001</strong>:用户名或密码错误</li>
 *   <li><strong>1002</strong>:账户已被禁用</li>
 *   <li><strong>1003</strong>:账户已被锁定</li>
 *   <li><strong>1004</strong>:账户不存在</li>
 *   <li><strong>1005</strong>:Token 已过期</li>
 *   <li><strong>1006</strong>:Token 无效</li>
 *   <li><strong>1007</strong>:MFA 验证码错误</li>
 *   <li><strong>1008</strong>:需要修改密码</li>
 * </ul>
 *
 * <h2>性能优化</h2>
 * <ul>
 *   <li><strong>Redis 缓存</strong>:使用 Redis 缓存 Token 和登录状态</li>
 *   <li><strong>异步日志</strong>:登录日志异步写入,不影响登录性能</li>
 *   <li><strong>连接池</strong>:使用连接池管理数据库连接</li>
 *   <li><strong>限流策略</strong>:对登录接口进行限流,防止攻击</li>
 * </ul>
 *
 * @see io.dataease.api.permissions.login.dto
 * @see io.dataease.api.permissions.login.vo
 * @since 1.0
 */
package io.dataease.api.permissions.login.api;
