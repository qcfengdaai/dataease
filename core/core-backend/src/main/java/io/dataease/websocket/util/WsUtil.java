package io.dataease.websocket.util;


import io.dataease.auth.bo.TokenUserBO;
import io.dataease.utils.AuthUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket工具类
 *
 * 提供用户在线状态管理功能
 * 维护当前在线用户集合,支持用户上线、下线、在线状态查询等操作
 *
 * @author DataEase
 */
public class WsUtil {

    /**
     * 在线用户集合
     * 使用CopyOnWriteArraySet保证线程安全,支持并发读写
     * 存储所有当前在线用户的ID
     */
    private static final CopyOnWriteArraySet<Long> ONLINE_USERS = new CopyOnWriteArraySet();

    /**
     * 将当前登录用户设置为在线状态
     *
     * 从认证上下文中获取当前用户信息,将其添加到在线用户集合
     *
     * @return 如果用户之前不在线则返回true,如果已在线则返回false
     */
    public static boolean onLine() {
        // 获取当前登录用户信息
        TokenUserBO user = AuthUtils.getUser();
        if (ObjectUtils.isNotEmpty(user) && ObjectUtils.isNotEmpty(user.getUserId()))
            // 将用户ID添加到在线集合
            return onLine(user.getUserId());
        return false;
    }

    /**
     * 将指定用户设置为在线状态
     *
     * 将用户ID添加到在线用户集合
     *
     * @param userId 用户ID
     * @return 如果用户之前不在线则返回true,如果已在线则返回false
     */
    public static boolean onLine(Long userId) {
        return ONLINE_USERS.add(userId);
    }

    /**
     * 将当前登录用户设置为离线状态
     *
     * 从认证上下文中获取当前用户信息,将其从在线用户集合中移除
     *
     * @return 如果用户之前在线则返回true,如果本来就不在线则返回false
     */
    public static boolean offLine() {
        // 获取当前登录用户信息
        TokenUserBO user = AuthUtils.getUser();
        if (ObjectUtils.isNotEmpty(user) && ObjectUtils.isNotEmpty(user.getUserId()))
            // 从在线集合中移除用户ID
            return offLine(user.getUserId());
        return false;
    }

    /**
     * 将指定用户设置为离线状态
     *
     * 将用户ID从在线用户集合中移除
     *
     * @param userId 用户ID
     * @return 如果用户之前在线则返回true,如果本来就不在线则返回false
     */
    public static boolean offLine(Long userId) {
        return ONLINE_USERS.remove(userId);
    }

    /**
     * 检查指定用户是否在线
     *
     * 判断用户ID是否存在于在线用户集合中
     *
     * @param userId 用户ID
     * @return 如果用户在线则返回true,否则返回false
     */
    public static boolean isOnLine(Long userId) {
        return ONLINE_USERS.contains(userId);
    }


}
