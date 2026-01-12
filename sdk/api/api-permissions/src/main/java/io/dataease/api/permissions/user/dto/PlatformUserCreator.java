package io.dataease.api.permissions.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 第三方平台用户创建器
 *
 * <p>用于创建第三方平台用户的数据传输对象，继承自 UserCreator。
 * 在基础用户信息之外，增加了用户来源标识，用于区分不同的第三方平台。</p>
 *
 * <p>支持的第三方平台：</p>
 * <ul>
 *   <li>LDAP：企业目录服务</li>
 *   <li>OAuth：开放授权协议（如 GitHub、Google 等）</li>
 *   <li>CAS：中央认证服务</li>
 *   <li>SAML：安全断言标记语言</li>
 *   <li>其他自定义第三方系统</li>
 * </ul>
 *
 * <p>与普通用户创建的区别：</p>
 * <ul>
 *   <li>需要指定用户来源（origin）</li>
 *   <li>认证由第三方平台管理，本系统只管理权限</li>
 *   <li>用户信息可能需要定期从第三方平台同步</li>
 * </ul>
 */
@Schema(description = "第三方平台用户构造器")
@EqualsAndHashCode(callSuper = true)
@Data
public class PlatformUserCreator extends UserCreator implements Serializable {

    /**
     * 用户来源
     * 标识用户来自哪个第三方平台
     * 不同的数字代表不同的平台：
     * - 1：LDAP
     * - 2：OAuth
     * - 3：CAS
     * 等（具体值由系统定义）
     */
    @Schema(description = "用户来源")
    private int origin;
}
