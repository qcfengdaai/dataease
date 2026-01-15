/**
 * 登录认证数据传输对象包
 *
 * <p>本包包含登录认证模块的所有 DTO (Data Transfer Object) 类,
 * 用于封装客户端请求参数。DTO 对象通常用于接口的输入参数验证和数据传输。</p>
 *
 * <h2>DTO 列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.login.dto.PwdLoginDTO} - 账号密码登录参数
 *     <ul>
 *       <li>封装用户名和密码</li>
 *       <li>密码为 RSA 加密后的字符串</li>
 *       <li>支持验证码验证(可选)</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.login.dto.MfaLoginDTO} - MFA 登录参数
 *     <ul>
 *       <li>封装用户 ID、MFA 密钥和动态验证码</li>
 *       <li>用于 MFA 二次验证</li>
 *       <li>验证码为 6 位数字</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.login.dto.AccountLockStatus} - 账户锁定状态
 *     <ul>
 *       <li>记录账户锁定信息</li>
 *       <li>包含锁定原因、锁定时间、解锁时间</li>
 *       <li>用于返回锁定状态给前端</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 DTO 类实现 {@link java.io.Serializable} 接口,支持序列化</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化 getter/setter</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>字段命名遵循驼峰命名规范</li>
 *   <li>使用 JSR-303 注解进行参数验证(如 {@code @NotBlank}、{@code @Pattern})</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <p>DTO 对象主要用于:</p>
 * <ol>
 *   <li>接收前端提交的登录表单数据</li>
 *   <li>封装 MFA 验证请求</li>
 *   <li>返回账户锁定状态信息</li>
 *   <li>在控制器层进行参数验证</li>
 * </ol>
 *
 * <h2>数据流转</h2>
 * <pre>
 * 前端表单 → DTO 对象 → Controller 接收 → Service 验证 → 返回 Token/错误信息
 * </pre>
 *
 * <h2>参数验证示例</h2>
 * <pre>{@code
 * @Data
 * @Schema(description = "账号密码登录参数")
 * public class PwdLoginDTO implements Serializable {
 *
 *     @NotBlank(message = "用户名不能为空")
 *     @Schema(description = "用户名", required = true)
 *     private String username;
 *
 *     @NotBlank(message = "密码不能为空")
 *     @Schema(description = "RSA加密后的密码", required = true)
 *     private String password;
 *
 *     @Schema(description = "验证码")
 *     private String captcha;
 *
 *     @Schema(description = "验证码Key")
 *     private String captchaKey;
 * }
 * }</pre>
 *
 * <h2>密码加密说明</h2>
 * <p>为了确保密码传输安全,前端需要对密码进行 RSA 加密:</p>
 * <pre>{@code
 * // JavaScript 示例(使用 jsencrypt 库)
 * import JSEncrypt from 'jsencrypt';
 *
 * // 1. 从后端获取 RSA 公钥
 * const publicKey = await api.getPublicKey();
 *
 * // 2. 创建加密器
 * const encrypt = new JSEncrypt();
 * encrypt.setPublicKey(publicKey);
 *
 * // 3. 加密密码
 * const encryptedPassword = encrypt.encrypt(password);
 *
 * // 4. 提交登录请求
 * const response = await api.login({
 *   username: 'admin',
 *   password: encryptedPassword  // 加密后的密码
 * });
 * }</pre>
 *
 * <h2>MFA 验证码格式</h2>
 * <p>MFA 动态验证码的格式要求:</p>
 * <ul>
 *   <li><strong>长度</strong>:6 位数字</li>
 *   <li><strong>格式</strong>:纯数字,如 "123456"</li>
 *   <li><strong>有效期</strong>:30 秒更新一次</li>
 *   <li><strong>容错</strong>:允许前后 1 个时间窗口的验证码</li>
 * </ul>
 *
 * <pre>{@code
 * @Data
 * @Schema(description = "MFA登录参数")
 * public class MfaLoginDTO implements Serializable {
 *
 *     @NotNull(message = "用户ID不能为空")
 *     @Schema(description = "用户ID", required = true)
 *     private Long userId;
 *
 *     @NotBlank(message = "MFA密钥不能为空")
 *     @Schema(description = "MFA密钥", required = true)
 *     private String secret;
 *
 *     @NotBlank(message = "验证码不能为空")
 *     @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
 *     @Schema(description = "6位动态验证码", required = true)
 *     private String code;
 * }
 * }</pre>
 *
 * <h2>账户锁定状态</h2>
 * <p>当账户被锁定时,返回详细的锁定信息:</p>
 * <pre>{@code
 * @Data
 * @Schema(description = "账户锁定状态")
 * public class AccountLockStatus implements Serializable {
 *
 *     @Schema(description = "是否锁定")
 *     private Boolean locked;
 *
 *     @Schema(description = "锁定原因")
 *     private String reason;
 *
 *     @Schema(description = "锁定时间(时间戳)")
 *     private Long lockTime;
 *
 *     @Schema(description = "解锁时间(时间戳)")
 *     private Long unlockTime;
 *
 *     @Schema(description = "剩余锁定时长(秒)")
 *     private Long remainingSeconds;
 *
 *     @Schema(description = "失败次数")
 *     private Integer failedAttempts;
 * }
 * }</pre>
 *
 * <h2>前端错误处理</h2>
 * <p>前端需要根据不同的错误情况进行处理:</p>
 * <pre>{@code
 * // Vue 3 示例
 * async function login(username, password) {
 *   try {
 *     const response = await loginApi.localLogin({
 *       username,
 *       password: encryptPassword(password)
 *     });
 *
 *     // 登录成功,保存 Token
 *     localStorage.setItem('token', response.token);
 *     router.push('/dashboard');
 *
 *   } catch (error) {
 *     // 根据错误码处理
 *     switch (error.code) {
 *       case 1001:
 *         ElMessage.error('用户名或密码错误');
 *         break;
 *       case 1002:
 *         ElMessage.error('账户已被禁用,请联系管理员');
 *         break;
 *       case 1003:
 *         // 账户被锁定,显示详细信息
 *         const lockInfo = error.data;
 *         ElMessage.error(
 *           `账户已被锁定,${formatTime(lockInfo.remainingSeconds)}后自动解锁`
 *         );
 *         break;
 *       case 1007:
 *         ElMessage.error('验证码错误,请重新输入');
 *         break;
 *       case 1008:
 *         // 需要修改密码
 *         router.push('/changePassword');
 *         break;
 *       default:
 *         ElMessage.error('登录失败,请稍后重试');
 *     }
 *   }
 * }
 * }</pre>
 *
 * <h2>安全注意事项</h2>
 * <ul>
 *   <li><strong>密码不打印日志</strong>:DTO 对象在日志中输出时,密码字段应被脱敏</li>
 *   <li><strong>参数长度限制</strong>:用户名、密码等字段应限制最大长度,防止攻击</li>
 *   <li><strong>特殊字符过滤</strong>:对用户输入进行安全过滤,防止 SQL 注入</li>
 *   <li><strong>频率限制</strong>:对登录接口进行频率限制,防止暴力破解</li>
 * </ul>
 *
 * @see io.dataease.api.permissions.login.api
 * @see io.dataease.api.permissions.login.vo
 * @since 1.0
 */
package io.dataease.api.permissions.login.dto;
