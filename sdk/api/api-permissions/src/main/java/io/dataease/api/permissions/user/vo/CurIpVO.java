package io.dataease.api.permissions.user.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 当前 IP 信息视图对象
 *
 * <p>用于展示当前访问请求的用户和 IP 信息。
 * 结合用户身份和 IP 地址，用于安全审计和访问控制。</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>安全审计：记录用户的访问 IP，追踪异常登录</li>
 *   <li>IP 白名单：基于 IP 地址进行访问控制</li>
 *   <li>地理位置：根据 IP 分析用户地理分布</li>
 *   <li>会话管理：关联用户会话和 IP 地址</li>
 * </ul>
 */
@Data
public class CurIpVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3025566841330382707L;

    /**
     * 用户账号
     * 当前请求用户的登录账号
     */
    private String account;

    /**
     * 用户姓名
     * 当前请求用户的显示名称
     */
    private String name;

    /**
     * IP 地址
     * 当前请求的客户端 IP 地址
     * 用于安全审计和访问控制
     */
    private String ip;
}
