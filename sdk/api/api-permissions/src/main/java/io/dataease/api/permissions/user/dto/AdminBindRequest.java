package io.dataease.api.permissions.user.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员绑定请求
 *
 * <p>用于管理员为其他用户绑定第三方平台账号的请求对象，
 * 继承自 UserBindRequest。与普通用户绑定的区别在于，
 * 管理员可以为任意用户执行绑定操作。</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>批量绑定：管理员批量为用户绑定第三方账号</li>
 *   <li>强制绑定：在用户无法自行完成绑定时，由管理员协助</li>
 *   <li>初始配置：系统初始化时配置用户的第三方账号关联</li>
 *   <li>故障恢复：修复用户绑定关系异常的情况</li>
 * </ul>
 *
 * <p>权限要求：需要系统管理员或用户管理员权限</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AdminBindRequest extends UserBindRequest {

    /**
     * 用户 ID
     * 指定要为哪个用户执行绑定操作
     * 管理员通过此字段指定目标用户
     */
    private Long uid;
}
