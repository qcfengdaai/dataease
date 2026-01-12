package io.dataease.system.manage;

import io.dataease.license.config.XpackInteract;
import org.springframework.stereotype.Component;

/**
 * 核心用户管理类
 * <p>
 * 提供用户相关的管理功能，支持通过 Xpack 扩展点进行功能增强
 * 社区版返回默认用户信息，企业版可扩展为完整的用户管理功能
 * </p>
 */
@Component
public class CoreUserManage {

    /**
     * 根据用户ID获取用户名称
     * <p>
     * 该方法使用 Xpack 交互机制，支持企业版扩展。
     * 社区版默认返回“管理员”，企业版可扩展为查询真实用户信息
     * </p>
     *
     * @param uid 用户ID
     * @return 用户名称，社区版默认返回“管理员”
     */
    @XpackInteract(value = "coreUserManage", replace = true)
    public String getUserName(Long uid) {
        // 社区版默认返回管理员名称
        return "管理员";
    }
}
