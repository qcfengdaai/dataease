/**
 * 权限设置模块
 *
 * <h2>模块概述</h2>
 * <p>本模块提供 DataEase 系统的权限和认证相关配置管理功能。
 * 权限设置是系统安全策略的核心，涵盖基础认证配置和多因素认证（MFA）配置，
 * 用于控制用户的登录行为、密码策略、会话管理等安全相关功能。</p>
 *
 * <h2>核心功能</h2>
 * <ul>
 *   <li>基础认证设置：密码策略、会话超时、登录限制等安全配置</li>
 *   <li>MFA（多因素认证）设置：启用/禁用 MFA、配置认证方式、设置强制策略</li>
 *   <li>配置项管理：查询和保存系统安全配置项</li>
 *   <li>MFA 状态查询：快速获取当前系统的 MFA 启用状态</li>
 * </ul>
 *
 * <h2>包结构</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.setting.api} - API 接口定义层</li>
 *   <li>{@link io.dataease.api.permissions.setting.vo} - 视图对象（响应数据）</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <ol>
 *   <li><strong>安全加固</strong>：配置严格的密码策略和会话管理，提升系统安全性
 *       <ul>
 *         <li>设置密码复杂度要求（最小长度、必须包含特殊字符等）</li>
 *         <li>设置密码有效期（如 90 天后必须修改密码）</li>
 *         <li>设置登录失败锁定策略（如连续 5 次失败锁定账户）</li>
 *       </ul>
 *   </li>
 *   <li><strong>合规要求</strong>：满足行业安全标准和监管要求
 *       <ul>
 *         <li>等保合规：满足等级保护要求的密码策略</li>
 *         <li>ISO 27001：符合信息安全管理体系标准</li>
 *         <li>SOC 2：满足服务组织控制报告要求</li>
 *       </ul>
 *   </li>
 *   <li><strong>企业安全策略</strong>：根据企业内部安全规范配置系统
 *       <ul>
 *         <li>统一的密码策略</li>
 *         <li>统一的会话超时策略</li>
 *         <li>统一的 MFA 要求</li>
 *       </ul>
 *   </li>
 *   <li><strong>高安全场景</strong>：为敏感环境启用 MFA
 *       <ul>
 *         <li>金融行业：强制所有用户启用 MFA</li>
 *         <li>医疗行业：保护患者隐私数据</li>
 *         <li>政府部门：保护敏感政务数据</li>
 *       </ul>
 *   </li>
 * </ol>
 *
 * <h2>实现位置</h2>
 * <p>本模块的接口实现位于：</p>
 * <ul>
 *   <li><strong>企业版</strong>: 分布式模块中的对应实现（标记为 {@code @XpackResource}）</li>
 *   <li><strong>社区版</strong>: 不支持此功能，或提供基础版本</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>1. 查询基础认证设置</h3>
 * <pre>{@code
 * // 前端调用示例
 * GET /api/setting/basic/query
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *
 * Response:
 * [
 *   {
 *     "key": "password.min.length",
 *     "value": "8",
 *     "type": "INTEGER",
 *     "description": "密码最小长度"
 *   },
 *   {
 *     "key": "password.require.special.char",
 *     "value": "true",
 *     "type": "BOOLEAN",
 *     "description": "密码必须包含特殊字符"
 *   },
 *   {
 *     "key": "password.expire.days",
 *     "value": "90",
 *     "type": "INTEGER",
 *     "description": "密码有效期（天）"
 *   },
 *   {
 *     "key": "session.timeout.minutes",
 *     "value": "30",
 *     "type": "INTEGER",
 *     "description": "会话超时时间（分钟）"
 *   },
 *   {
 *     "key": "login.fail.lock.times",
 *     "value": "5",
 *     "type": "INTEGER",
 *     "description": "登录失败锁定次数"
 *   },
 *   {
 *     "key": "login.fail.lock.duration",
 *     "value": "30",
 *     "type": "INTEGER",
 *     "description": "账户锁定时长（分钟）"
 *   },
 *   {
 *     "key": "max.concurrent.sessions",
 *     "value": "3",
 *     "type": "INTEGER",
 *     "description": "同一账户最大同时登录数"
 *   }
 * ]
 * }</pre>
 *
 * <h3>2. 保存基础认证设置</h3>
 * <pre>{@code
 * // 前端调用示例
 * POST /api/setting/baisc/save
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *   Content-Type: application/json
 *
 * Request Body:
 * [
 *   {
 *     "key": "password.min.length",
 *     "value": "10"  // 修改密码最小长度为 10
 *   },
 *   {
 *     "key": "password.require.special.char",
 *     "value": "true"
 *   },
 *   {
 *     "key": "session.timeout.minutes",
 *     "value": "15"  // 修改会话超时为 15 分钟
 *   }
 * ]
 * }</pre>
 *
 * <h3>3. 查询单个配置项</h3>
 * <pre>{@code
 * // 内部服务调用示例（用于获取特定配置值）
 * GET /api/setting/baisc/single/password.min.length
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *
 * Response:
 * "8"  // 返回配置值字符串
 *
 * // Java 代码调用
 * @Service
 * public class UserService {
 *
 *     @Autowired
 *     private PerSettingApi perSettingApi;
 *
 *     public void validatePassword(String password) {
 *         // 获取密码最小长度配置
 *         String minLengthStr = perSettingApi.singleValue("password.min.length");
 *         int minLength = Integer.parseInt(minLengthStr);
 *
 *         if (password.length() < minLength) {
 *             throw new DEException("密码长度不能少于 " + minLength + " 位");
 *         }
 *
 *         // 检查是否要求特殊字符
 *         String requireSpecialChar = perSettingApi.singleValue("password.require.special.char");
 *         if ("true".equals(requireSpecialChar)) {
 *             if (!password.matches(".*[!@#$%^&*()].*")) {
 *                 throw new DEException("密码必须包含特殊字符");
 *             }
 *         }
 *     }
 * }
 * }</pre>
 *
 * <h3>4. 查询 MFA 设置</h3>
 * <pre>{@code
 * // 前端调用示例
 * GET /api/setting/mfa/query
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *
 * Response:
 * [
 *   {
 *     "key": "mfa.enabled",
 *     "value": "true",
 *     "type": "BOOLEAN",
 *     "description": "是否启用 MFA"
 *   },
 *   {
 *     "key": "mfa.methods",
 *     "value": "totp,sms,email",
 *     "type": "STRING",
 *     "description": "支持的 MFA 认证方式"
 *   },
 *   {
 *     "key": "mfa.enforce",
 *     "value": "false",
 *     "type": "BOOLEAN",
 *     "description": "是否强制所有用户启用 MFA"
 *   },
 *   {
 *     "key": "mfa.enforce.roles",
 *     "value": "admin,manager",
 *     "type": "STRING",
 *     "description": "必须启用 MFA 的角色（逗号分隔）"
 *   },
 *   {
 *     "key": "mfa.code.expire.seconds",
 *     "value": "300",
 *     "type": "INTEGER",
 *     "description": "MFA 验证码有效期（秒）"
 *   }
 * ]
 * }</pre>
 *
 * <h3>5. 保存 MFA 设置</h3>
 * <pre>{@code
 * POST /api/setting/mfa/save
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *   Content-Type: application/json
 *
 * Request Body:
 * [
 *   {
 *     "key": "mfa.enabled",
 *     "value": "true"
 *   },
 *   {
 *     "key": "mfa.enforce",
 *     "value": "true"  // 强制所有用户启用 MFA
 *   },
 *   {
 *     "key": "mfa.methods",
 *     "value": "totp,email"  // 只允许 TOTP 和邮箱验证
 *   }
 * ]
 * }</pre>
 *
 * <h3>6. 查询 MFA 状态</h3>
 * <pre>{@code
 * // 前端快速查询 MFA 状态（用于决定是否显示 MFA 相关功能）
 * GET /api/setting/mfaStatus
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *
 * Response:
 * 2  // 返回状态码
 *
 * // 状态码说明：
 * // 0 - MFA 未启用
 * // 1 - MFA 已启用，但不强制
 * // 2 - MFA 已启用且强制所有用户使用
 *
 * // 前端根据状态码决定行为
 * if (mfaStatus === 0) {
 *   // 不显示 MFA 相关功能
 * } else if (mfaStatus === 1) {
 *   // 显示 MFA 绑定入口，允许用户自主选择
 * } else if (mfaStatus === 2) {
 *   // 强制用户绑定 MFA，未绑定不允许登录
 *   if (!user.mfaBound) {
 *     redirectTo('/mfa/bind');
 *   }
 * }
 * }</pre>
 *
 * <h2>配置项分类</h2>
 *
 * <h3>基础认证设置</h3>
 * <table border="1">
 *   <tr>
 *     <th>配置项</th>
 *     <th>类型</th>
 *     <th>默认值</th>
 *     <th>说明</th>
 *   </tr>
 *   <tr>
 *     <td>password.min.length</td>
 *     <td>INTEGER</td>
 *     <td>8</td>
 *     <td>密码最小长度</td>
 *   </tr>
 *   <tr>
 *     <td>password.require.special.char</td>
 *     <td>BOOLEAN</td>
 *     <td>false</td>
 *     <td>密码必须包含特殊字符</td>
 *   </tr>
 *   <tr>
 *     <td>password.require.number</td>
 *     <td>BOOLEAN</td>
 *     <td>false</td>
 *     <td>密码必须包含数字</td>
 *   </tr>
 *   <tr>
 *     <td>password.require.uppercase</td>
 *     <td>BOOLEAN</td>
 *     <td>false</td>
 *     <td>密码必须包含大写字母</td>
 *   </tr>
 *   <tr>
 *     <td>password.expire.days</td>
 *     <td>INTEGER</td>
 *     <td>0 (永不过期)</td>
 *     <td>密码有效期（天）</td>
 *   </tr>
 *   <tr>
 *     <td>session.timeout.minutes</td>
 *     <td>INTEGER</td>
 *     <td>30</td>
 *     <td>会话超时时间（分钟）</td>
 *   </tr>
 *   <tr>
 *     <td>login.fail.lock.times</td>
 *     <td>INTEGER</td>
 *     <td>5</td>
 *     <td>登录失败锁定次数</td>
 *   </tr>
 *   <tr>
 *     <td>login.fail.lock.duration</td>
 *     <td>INTEGER</td>
 *     <td>30</td>
 *     <td>账户锁定时长（分钟）</td>
 *   </tr>
 *   <tr>
 *     <td>max.concurrent.sessions</td>
 *     <td>INTEGER</td>
 *     <td>0 (无限制)</td>
 *     <td>同一账户最大同时登录数</td>
 *   </tr>
 * </table>
 *
 * <h3>MFA 设置</h3>
 * <table border="1">
 *   <tr>
 *     <th>配置项</th>
 *     <th>类型</th>
 *     <th>默认值</th>
 *     <th>说明</th>
 *   </tr>
 *   <tr>
 *     <td>mfa.enabled</td>
 *     <td>BOOLEAN</td>
 *     <td>false</td>
 *     <td>是否启用 MFA</td>
 *   </tr>
 *   <tr>
 *     <td>mfa.methods</td>
 *     <td>STRING</td>
 *     <td>totp</td>
 *     <td>支持的认证方式（totp, sms, email）</td>
 *   </tr>
 *   <tr>
 *     <td>mfa.enforce</td>
 *     <td>BOOLEAN</td>
 *     <td>false</td>
 *     <td>是否强制所有用户启用</td>
 *   </tr>
 *   <tr>
 *     <td>mfa.enforce.roles</td>
 *     <td>STRING</td>
 *     <td>""</td>
 *     <td>必须启用 MFA 的角色（逗号分隔）</td>
 *   </tr>
 *   <tr>
 *     <td>mfa.code.expire.seconds</td>
 *     <td>INTEGER</td>
 *     <td>300</td>
 *     <td>验证码有效期（秒）</td>
 *   </tr>
 *   <tr>
 *     <td>mfa.backup.codes.count</td>
 *     <td>INTEGER</td>
 *     <td>10</td>
 *     <td>备用恢复码数量</td>
 *   </tr>
 * </table>
 *
 * <h2>MFA 认证方式说明</h2>
 * <ul>
 *   <li><strong>TOTP（Time-based One-Time Password）</strong>：基于时间的一次性密码
 *       <ul>
 *         <li>使用 Google Authenticator、Microsoft Authenticator 等应用</li>
 *         <li>每 30 秒生成一个新的 6 位数验证码</li>
 *         <li>不依赖网络，安全性高</li>
 *       </ul>
 *   </li>
 *   <li><strong>短信验证码（SMS）</strong>：通过手机短信接收验证码
 *       <ul>
 *         <li>需要配置短信服务商</li>
 *         <li>依赖手机信号</li>
 *         <li>可能产生短信费用</li>
 *       </ul>
 *   </li>
 *   <li><strong>邮箱验证码（Email）</strong>：通过邮箱接收验证码
 *       <ul>
 *         <li>需要配置邮件服务</li>
 *         <li>依赖邮箱可用性</li>
 *         <li>免费</li>
 *       </ul>
 *   </li>
 *   <li><strong>硬件令牌（Hardware Token）</strong>：专用硬件设备（企业版可能支持）</li>
 * </ul>
 *
 * <h2>权限说明</h2>
 * <ul>
 *   <li>查询设置：通常需要管理员权限</li>
 *   <li>修改设置：必须具有系统管理员权限</li>
 *   <li>查询 MFA 状态：所有登录用户可访问（用于前端判断）</li>
 * </ul>
 *
 * <h2>生效机制</h2>
 * <ul>
 *   <li><strong>立即生效</strong>：大部分配置修改后立即生效
 *       <ul>
 *         <li>密码策略：新密码修改时生效</li>
 *         <li>MFA 启用：下次登录时生效</li>
 *       </ul>
 *   </li>
 *   <li><strong>延迟生效</strong>：部分配置需要特定时机生效
 *       <ul>
 *         <li>会话超时：当前会话按旧配置，新会话按新配置</li>
 *         <li>登录限制：下次登录尝试时生效</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li><strong>配置备份</strong>：修改配置前建议备份当前配置</li>
 *   <li><strong>影响评估</strong>：某些配置变更可能影响所有用户，需提前通知
 *       <ul>
 *         <li>启用 MFA 强制策略：未绑定的用户将无法登录</li>
 *         <li>缩短会话超时：用户会更频繁地被要求重新登录</li>
 *         <li>提高密码复杂度：现有密码可能不符合新策略</li>
 *       </ul>
 *   </li>
 *   <li><strong>渐进式推进</strong>：建议分阶段启用安全策略
 *       <ol>
 *         <li>第一阶段：启用但不强制，允许用户适应</li>
 *         <li>第二阶段：对管理员角色强制启用</li>
 *         <li>第三阶段：对所有用户强制启用</li>
 *       </ol>
 *   </li>
 *   <li><strong>应急方案</strong>：准备应急恢复方案
 *       <ul>
 *         <li>保留超级管理员账户的特殊通道</li>
 *         <li>提供 MFA 备用恢复码</li>
 *         <li>保留配置回滚能力</li>
 *       </ul>
 *   </li>
 *   <li><strong>企业版功能</strong>：本模块标记为 {@code @XpackResource}，社区版可能不可用</li>
 * </ol>
 *
 * @author DataEase Team
 * @since 1.0
 */
package io.dataease.api.permissions.setting;
