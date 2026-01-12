package io.dataease.system.manage;

import io.dataease.api.permissions.auth.dto.BusiPerCheckDTO;
import io.dataease.license.config.XpackInteract;
import org.springframework.stereotype.Component;

/**
 * 核心权限管理类
 * <p>
 * 提供权限检查的核心功能，支持通过 Xpack 扩展点进行功能增强
 * 社区版默认允许所有操作，企业版可扩展为完整的权限校验体系
 * </p>
 */
@Component
public class CorePermissionManage {

    /**
     * 检查业务权限
     * <p>
     * 该方法使用 Xpack 交互机制，支持企业版扩展。
     * 社区版默认返回 true（允许所有操作），
     * 企业版可扩展为基于角色、资源的完整权限校验
     * </p>
     *
     * @param dto 业务权限检查 DTO，包含权限检查所需的参数
     * @return 是否有权限，社区版默认返回 true
     */
    @XpackInteract(value = "corePermissionManage", replace = true)
    public boolean checkAuth(BusiPerCheckDTO dto) {
        // 社区版默认返回 true，允许所有操作
        return true;
    }
}
