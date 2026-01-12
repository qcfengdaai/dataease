package io.dataease.api.permissions.role.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色复制请求
 *
 * <p>用于复制已存在角色的请求对象。复制功能允许基于现有角色快速创建新角色，
 * 新角色将继承源角色的权限配置，但使用新的名称和描述。</p>
 *
 * <p>复制内容：</p>
 * <ul>
 *   <li>权限配置：复制源角色的所有权限设置</li>
 *   <li>角色类型：继承源角色的类型代码</li>
 *   <li>不复制：用户绑定关系不会被复制</li>
 * </ul>
 *
 * <p>使用场景：快速创建具有相似权限的新角色，提高角色管理效率</p>
 */
@Data
public class RoleCopyRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1631759805936434870L;

    /**
     * 源角色 ID
     * 要复制的角色的唯一标识，系统将复制该角色的权限配置
     */
    private Long copyId;

    /**
     * 新角色名称
     * 复制后创建的新角色的名称，必须与现有角色名称不重复
     */
    private String name;

    /**
     * 新角色描述
     * 复制后创建的新角色的详细说明
     */
    private String desc;
}
