package io.dataease.api.permissions.setting.api;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.permissions.setting.vo.PerSettingItemVO;
import io.dataease.license.config.XpackResource;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 权限设置 API
 * <p>
 * 提供系统权限相关的配置管理功能，包括基础认证设置和多因素认证（MFA）设置。
 * 这些设置影响系统的安全策略和用户登录行为。
 * <p>
 * 核心功能：
 * <ul>
 *   <li>基础认证设置：密码策略、会话超时、登录限制等</li>
 *   <li>MFA（多因素认证）设置：是否启用、认证方式、强制策略等</li>
 *   <li>单项配置查询：用于内部服务获取特定配置项</li>
 * </ul>
 * <p>
 * 注意：此接口标记为 {@code @XpackResource}，属于企业版功能。
 *
 * @author fit2cloud
 * @since 1.0
 */
@Tag(name = "认证相关设置")
@ApiSupport(order = 882)
@XpackResource
public interface PerSettingApi {

    /**
     * 查询基础认证设置
     * <p>
     * 获取系统的基础认证配置项列表。
     * 返回的设置项可能包括：
     * <ul>
     *   <li>密码复杂度要求（最小长度、必须包含特殊字符等）</li>
     *   <li>密码有效期（如 90 天后必须修改密码）</li>
     *   <li>会话超时时间（如 30 分钟无操作自动退出）</li>
     *   <li>登录失败锁定策略（如连续 5 次失败锁定账户）</li>
     *   <li>同一账户最大同时登录数</li>
     * </ul>
     *
     * @return 基础认证设置项列表，每项包含 key、value、type 等信息
     */
    @Operation(summary = "查询设置")
    @GetMapping("/basic/query")
    List<PerSettingItemVO> basicSetting();

    /**
     * 保存基础认证设置
     * <p>
     * 批量更新系统的基础认证配置。
     * 提交的设置项会覆盖现有配置。
     * <p>
     * 注意：
     * <ul>
     *   <li>修改设置需要管理员权限</li>
     *   <li>某些设置可能需要重启服务或用户重新登录才能生效</li>
     *   <li>设置不当可能影响系统安全性或用户体验</li>
     * </ul>
     *
     * @param settings 要保存的设置项列表，通常为键值对形式
     */
    @Operation(summary = "保存设置")
    @PostMapping("/baisc/save")
    void saveBasic(@RequestBody List<Object> settings);

    /**
     * 查询单个配置项的值
     * <p>
     * 根据配置项的 key 获取其对应的 value。
     * 此接口通常用于内部服务调用，不在 API 文档中公开。
     * <p>
     * 使用场景：
     * <ul>
     *   <li>服务启动时读取必要的配置参数</li>
     *   <li>业务逻辑中判断某个开关是否开启</li>
     *   <li>动态获取配置值而不重启服务</li>
     * </ul>
     *
     * @param key 配置项的键名
     * @return 配置项的值（字符串形式），如果不存在则返回 null
     */
    @Hidden
    @GetMapping("/baisc/single/{key}")
    String singleValue(@PathVariable("key") String key);

    /**
     * 查询 MFA（多因素认证）设置
     * <p>
     * 获取系统的 MFA 配置项列表。
     * MFA 是一种增强安全性的认证机制，要求用户提供两种或以上的验证因素。
     * <p>
     * 返回的设置项可能包括：
     * <ul>
     *   <li>是否启用 MFA</li>
     *   <li>MFA 认证方式（短信验证码、邮箱验证码、TOTP、硬件令牌等）</li>
     *   <li>MFA 强制策略（是否强制所有用户启用、哪些角色必须启用等）</li>
     *   <li>MFA 验证码有效期</li>
     *   <li>MFA 绑定设备管理策略</li>
     * </ul>
     *
     * @return MFA 设置项列表
     */
    @Operation(summary = "查询MFA设置")
    @GetMapping("/mfa/query")
    List<PerSettingItemVO> mfaSetting();

    /**
     * 保存 MFA（多因素认证）设置
     * <p>
     * 批量更新系统的 MFA 配置。
     * <p>
     * 注意事项：
     * <ul>
     *   <li>启用 MFA 会影响所有用户的登录流程</li>
     *   <li>如果启用强制 MFA，未绑定 MFA 的用户将无法登录</li>
     *   <li>修改 MFA 设置前应通知用户并做好准备</li>
     *   <li>建议先在测试环境验证 MFA 配置</li>
     * </ul>
     *
     * @param settings MFA 设置项列表
     */
    @Operation(summary = "保存MFA设置")
    @PostMapping("/mfa/save")
    void saveMfa(@RequestBody List<PerSettingItemVO> settings);

    /**
     * 查询 MFA 状态
     * <p>
     * 获取当前系统的 MFA 启用状态。
     * 返回值为整数，表示不同的 MFA 状态级别。
     * <p>
     * 可能的返回值：
     * <ul>
     *   <li>0：MFA 未启用</li>
     *   <li>1：MFA 已启用，但不强制</li>
     *   <li>2：MFA 已启用且强制所有用户使用</li>
     * </ul>
     * <p>
     * 此接口用于前端快速判断是否显示 MFA 相关功能，
     * 避免每次都查询完整的 MFA 设置列表。
     *
     * @return MFA 状态码
     */
    @Operation(summary = "查询MFA状态")
    @GetMapping("/mfaStatus")
    Integer mfaStatus();
}
