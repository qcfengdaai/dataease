package io.dataease.api.permissions.user.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户接收人查询请求
 *
 * <p>用于查询消息通知接收人的请求对象。
 * 支持按用户 ID 或角色 ID 查询接收人列表，用于系统消息推送功能。</p>
 *
 * <p>查询方式：</p>
 * <ul>
 *   <li>按用户 ID：直接指定接收消息的用户列表</li>
 *   <li>按角色 ID：查询拥有指定角色的所有用户作为接收人</li>
 *   <li>组合查询：同时指定用户 ID 和角色 ID，取并集</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>系统通知：向特定用户或角色推送系统消息</li>
 *   <li>报表分发：将报表发送给指定的接收人</li>
 *   <li>审批流程：向审批人发送待办通知</li>
 *   <li>告警通知：向相关责任人发送告警信息</li>
 * </ul>
 */
@Data
public class UserReciRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -2165057126540959376L;

    /**
     * 用户 ID 列表
     * 直接指定接收消息的用户 ID 集合
     * 适用于向特定用户推送消息的场景
     */
    private List<Long> uidList;

    /**
     * 角色 ID 列表
     * 指定接收消息的角色 ID 集合
     * 系统会查询拥有这些角色的所有用户作为接收人
     * 适用于向某类用户群体推送消息的场景
     */
    private List<Long> ridList;
}
