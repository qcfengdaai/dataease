package io.dataease.api.permissions.login.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 账户锁定状态对象
 *
 * <p>用于表示账户的锁定状态和相关信息。
 * 当用户连续登录失败达到一定次数后，系统会自动锁定账户，防止暴力破解攻击。</p>
 *
 * <p>锁定机制说明：
 * <ul>
 *   <li>连续失败次数达到阈值（如 5 次）后自动锁定账户</li>
 *   <li>锁定后在指定时间内（如 30 分钟）无法登录</li>
 *   <li>锁定时间到期后自动解锁</li>
 *   <li>管理员可以手动解锁账户</li>
 *   <li>成功登录后清除失败次数计数</li>
 * </ul>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Data
public class AccountLockStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 8310029430986389948L;

    /**
     * 是否已锁定
     *
     * <p>标识账户当前是否处于锁定状态：
     * <ul>
     *   <li>true: 账户已锁定，无法登录</li>
     *   <li>false: 账户未锁定，可以正常登录</li>
     * </ul>
     * </p>
     */
    private Boolean locked = false;

    /**
     * 账户名称
     *
     * <p>被锁定的账户的用户名或账号</p>
     */
    private String account;

    /**
     * 解锁时间
     *
     * <p>账户自动解锁的时间戳（毫秒）。
     * 到达该时间后，账户会自动解锁，用户可以重新尝试登录。
     * 如果为 null 表示需要管理员手动解锁。</p>
     */
    private Long unlockTime;

    /**
     * 解锁剩余次数
     *
     * <p>在账户被锁定前，用户还可以尝试登录的剩余次数。
     * 例如：如果设置锁定阈值为 5 次，当前已失败 3 次，则剩余次数为 2。</p>
     */
    private Integer relieveTimes;

    /**
     * 剩余尝试次数
     *
     * <p>用户当前还可以尝试登录的次数。
     * 该值等于锁定阈值减去已失败次数。
     * 当该值为 0 时，账户将被锁定。</p>
     */
    private Integer remainderTimes;
}
