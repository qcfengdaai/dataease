package io.dataease.api.permissions.login.api;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.permissions.login.dto.MfaLoginDTO;
import io.dataease.api.permissions.login.dto.PwdLoginDTO;
import io.dataease.api.permissions.login.vo.MfaQrVO;
import io.dataease.api.permissions.user.dto.ModifyPwdRequest;
import io.dataease.auth.vo.TokenVO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户登录认证接口
 *
 * <p>提供 DataEase 系统的用户登录认证功能，支持多种登录方式和安全认证机制。
 * 登录本质上是获取访问令牌（Token）的过程，成功登录后将 Token 返回给前端，
 * 后续所有 API 请求都需要携带该 Token 进行身份验证。</p>
 *
 * <p>支持的登录方式：
 * <ul>
 *   <li>本地登录：使用用户名和密码进行认证</li>
 *   <li>第三方登录：通过 OIDC、LDAP、CAS 等第三方认证平台登录</li>
 *   <li>MFA 登录：支持多因子认证，提供更高的安全性</li>
 * </ul>
 * </p>
 *
 * <p>登录流程说明：
 * <ol>
 *   <li>本地登录：用户提交账号密码 → 系统验证 → 生成 Token → 返回 Token</li>
 *   <li>第三方登录：重定向到第三方平台 → 用户在第三方完成认证 → 回调获取用户信息 →
 *       系统匹配或创建用户 → 生成 Token → 返回 Token</li>
 *   <li>MFA 登录：用户提交账号密码 → 系统验证 → 返回 MFA 二维码 → 用户使用认证器扫码 →
 *       输入动态验证码 → 系统验证 → 生成 Token → 返回 Token</li>
 * </ol>
 * </p>
 *
 * <p>安全特性：
 * <ul>
 *   <li>密码加密传输：前端使用 RSA 加密密码后传输</li>
 *   <li>账户锁定机制：连续登录失败后自动锁定账户</li>
 *   <li>MFA 双因子认证：支持 TOTP 算法的动态验证码</li>
 *   <li>Token 过期机制：自动刷新和过期控制</li>
 *   <li>登录日志记录：记录所有登录行为，便于审计</li>
 * </ul>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 1.0
 */
@Tag(name = "登录")
@ApiSupport(order = 889, author = "fit2cloud-someone")
public interface LoginApi {
    /**
     * 本地账号密码登录
     *
     * <p>使用 DataEase 系统内的账号和密码进行登录认证。
     * 密码需要在前端使用 RSA 公钥加密后传输，确保传输安全。</p>
     *
     * <p>登录验证流程：
     * <ol>
     *   <li>验证账号是否存在</li>
     *   <li>检查账号是否被锁定或禁用</li>
     *   <li>验证密码是否正确</li>
     *   <li>检查是否需要 MFA 认证</li>
     *   <li>生成访问 Token 并返回</li>
     *   <li>记录登录日志</li>
     * </ol>
     * </p>
     *
     * <p>可能的失败情况：
     * <ul>
     *   <li>账号不存在：返回错误提示</li>
     *   <li>密码错误：记录失败次数，多次失败后锁定账户</li>
     *   <li>账户被锁定：返回锁定信息和解锁时间</li>
     *   <li>账户被禁用：返回禁用提示</li>
     *   <li>需要 MFA：返回 MFA 认证标识，跳转到 MFA 验证流程</li>
     * </ul>
     * </p>
     *
     * @param dto 登录参数，包含加密后的账号和密码
     * @return Token 信息，包含访问令牌、刷新令牌、过期时间等
     */
    @Operation(summary = "本地登录")
    @ApiOperationSupport(order = 1)
    @PostMapping("/login/localLogin")
    TokenVO localLogin(@Valid @RequestBody PwdLoginDTO dto);

    /**
     * Token 续期
     *
     * <p>在 Token 即将过期时，使用当前有效的 Token 换取新的 Token，延长登录有效期。
     * 避免用户频繁登录，提升用户体验。</p>
     *
     * <p>续期规则：
     * <ul>
     *   <li>Token 必须仍在有效期内</li>
     *   <li>每次续期会生成新的 Token，旧 Token 失效</li>
     *   <li>续期后的 Token 有效期重新计算</li>
     * </ul>
     * </p>
     *
     * @return 新的 Token 信息
     */
    @Operation(summary = "token续命", hidden = true)
    @ApiOperationSupport(order = 2)
    @GetMapping("/login/refresh")
    TokenVO refresh();

    /**
     * 第三方平台登录
     *
     * <p>通过第三方认证平台（如 OIDC、LDAP、CAS 等）进行登录。
     * 系统会从第三方平台的回调中获取用户信息，如果用户在 DataEase 中不存在，
     * 则自动创建账号；如果已存在，则直接登录。</p>
     *
     * <p>第三方登录流程：
     * <ol>
     *   <li>用户点击第三方登录按钮</li>
     *   <li>重定向到第三方认证平台</li>
     *   <li>用户在第三方平台完成认证</li>
     *   <li>第三方平台回调，在请求头 X-Userinfo 中返回用户信息</li>
     *   <li>系统解析用户信息，匹配或创建用户</li>
     *   <li>生成 Token 并返回</li>
     * </ol>
     * </p>
     *
     * @param origin 第三方平台类型，如：1-OIDC, 2-LDAP, 3-CAS
     * @return Token 信息
     */
    @Operation(summary = "第三方登录", hidden = true)
    @ApiOperationSupport(order = 3)
    @PostMapping("/login/platformLogin/{origin}")
    TokenVO platformLogin(@PathVariable("origin") Integer origin);

    /**
     * 用户登出
     *
     * <p>退出当前用户的登录状态，使当前 Token 失效。
     * 登出后需要重新登录才能访问系统。</p>
     *
     * <p>登出操作：
     * <ul>
     *   <li>将当前 Token 加入黑名单，使其立即失效</li>
     *   <li>清除用户的会话信息</li>
     *   <li>记录登出日志</li>
     * </ul>
     * </p>
     */
    @Operation(summary = "登出")
    @ApiOperationSupport(order = 4)
    @GetMapping("/logout")
    void logout();

    /**
     * 获取 MFA 二维码
     *
     * <p>为指定用户生成多因子认证（MFA）的二维码和密钥。
     * 用户可以使用认证器应用（如 Google Authenticator、Microsoft Authenticator）
     * 扫描二维码来绑定 MFA 设备。</p>
     *
     * <p>MFA 设置流程：
     * <ol>
     *   <li>管理员为用户启用 MFA</li>
     *   <li>用户登录时获取 MFA 二维码</li>
     *   <li>用户使用认证器应用扫描二维码</li>
     *   <li>认证器应用开始生成动态验证码</li>
     *   <li>用户输入验证码完成绑定</li>
     * </ol>
     * </p>
     *
     * @param id 用户ID
     * @return MFA 二维码信息，包含二维码图片和密钥
     */
    @Operation(summary = "MFA二维码信息")
    @ApiOperationSupport(order = 5)
    @PostMapping("/mfa/qr/{id}")
    MfaQrVO mfaQr(@PathVariable("id") Long id);

    /**
     * MFA 多因子认证登录
     *
     * <p>使用动态验证码完成 MFA 认证。在账号密码验证通过后，
     * 如果用户启用了 MFA，需要额外输入认证器生成的动态验证码才能完成登录。</p>
     *
     * <p>MFA 验证流程：
     * <ol>
     *   <li>用户输入账号密码，初步验证通过</li>
     *   <li>系统检测到用户启用了 MFA</li>
     *   <li>用户打开认证器应用，获取当前动态验证码</li>
     *   <li>用户输入验证码</li>
     *   <li>系统使用 TOTP 算法验证验证码的正确性</li>
     *   <li>验证通过后生成 Token 并返回</li>
     * </ol>
     * </p>
     *
     * <p>安全说明：
     * <ul>
     *   <li>验证码每 30 秒更新一次</li>
     *   <li>验证码使用一次即失效</li>
     *   <li>连续验证失败会触发账户锁定</li>
     * </ul>
     * </p>
     *
     * @param dto MFA 登录参数，包含用户ID、验证码和密钥
     * @return Token 信息
     */
    @Operation(summary = "MFA登录")
    @ApiOperationSupport(order = 6)
    @PostMapping("/mfa/login")
    TokenVO mfaLogin(@RequestBody MfaLoginDTO dto);

    /**
     * 修改无效密码
     *
     * <p>当用户的密码被标记为无效（如：初始密码、过期密码、被重置的密码）时，
     * 强制用户修改密码。用户必须修改密码后才能继续使用系统。</p>
     *
     * <p>触发场景：
     * <ul>
     *   <li>首次登录：使用管理员分配的初始密码首次登录</li>
     *   <li>密码过期：根据密码策略，密码超过有效期</li>
     *   <li>密码重置：管理员重置用户密码后</li>
     *   <li>安全策略：系统检测到密码存在安全风险</li>
     * </ul>
     * </p>
     *
     * @param request 密码修改请求，包含用户ID、旧密码和新密码
     */
    @Operation(summary = "修改无效密码", hidden = true)
    @PostMapping("/login/modifyInvalidPwd")
    void modifyInvalidPwd(@RequestBody ModifyPwdRequest request);
}
