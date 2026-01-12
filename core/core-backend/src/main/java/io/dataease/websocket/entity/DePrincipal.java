package io.dataease.websocket.entity;

import java.security.Principal;

/**
 * WebSocket用户主体
 *
 * 实现Java Security的Principal接口
 * 用于在WebSocket握手和会话中标识用户身份
 *
 * @author DataEase
 */
public class DePrincipal implements Principal {

    /**
     * 用户名称
     * 在本系统中存储为用户ID的字符串形式
     */
    private String name;

    /**
     * 构造函数
     *
     * @param name 用户名称(用户ID)
     */
    public DePrincipal(String name) {
        this.name = name;
    }

    /**
     * 获取用户名称
     *
     * @return 用户名称(用户ID)
     */
    @Override
    public String getName() {
        return name;
    }
}
