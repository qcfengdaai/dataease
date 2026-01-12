package io.dataease.api.permissions.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户绑定请求
 *
 * <p>用于用户绑定第三方平台账号的请求对象。
 * 绑定后，用户可以使用第三方账号登录系统，实现单点登录。</p>
 *
 * <p>绑定流程：</p>
 * <ul>
 *   <li>用户通过第三方平台完成认证</li>
 *   <li>系统获取第三方平台返回的用户标识（sub）</li>
 *   <li>将第三方用户标识与当前系统用户关联</li>
 *   <li>绑定成功后，可使用第三方账号登录</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>OAuth 登录绑定：绑定 GitHub、Google 等账号</li>
 *   <li>企业系统集成：绑定企业内部认证系统</li>
 *   <li>多账号关联：一个用户绑定多个第三方账号</li>
 * </ul>
 */
@Data
public class UserBindRequest implements Serializable {

    /**
     * 第三方平台标识
     * 标识要绑定的第三方平台类型
     * 不同的数字代表不同的平台：
     * - 1：LDAP
     * - 2：OAuth
     * - 3：CAS
     * 等（具体值由系统定义）
     */
    private Integer origin;

    /**
     * 第三方用户标识
     * 用户在第三方平台的唯一标识（subject）
     * 通常是第三方平台返回的用户 ID 或 sub 声明
     * 用于关联第三方账号与系统账号
     */
    private String sub;
}
