package io.dataease.api.permissions.user.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 第三方平台用户业务对象
 *
 * <p>用于表示来自第三方平台（如 LDAP、OAuth、CAS 等）的用户信息。
 * 该对象主要用于第三方认证集成，存储从外部系统同步过来的用户数据。</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>LDAP 用户同步：从 LDAP 服务器同步用户信息</li>
 *   <li>OAuth 登录：存储 OAuth 认证返回的用户信息</li>
 *   <li>CAS 单点登录：保存 CAS 认证的用户数据</li>
 *   <li>第三方系统集成：对接其他业务系统的用户信息</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatformUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 2749044307502902368L;

    /**
     * 用户账号
     * 第三方平台的用户登录账号或唯一标识
     */
    private String account;

    /**
     * 用户姓名
     * 用户的显示名称，从第三方平台获取
     */
    private String name;

    /**
     * 用户邮箱
     * 用户的电子邮箱地址
     */
    private String email;

    /**
     * 用户电话
     * 用户的联系电话号码
     */
    private String phone;

    /**
     * 用户类型
     * 标识用户的分类或级别，具体含义由业务定义
     */
    private Integer type;
}
