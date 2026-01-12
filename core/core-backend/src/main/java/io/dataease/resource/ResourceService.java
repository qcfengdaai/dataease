package io.dataease.resource;

import io.dataease.api.permissions.auth.dto.BusiPerCheckDTO;
import io.dataease.constant.AuthEnum;
import io.dataease.system.manage.CorePermissionManage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 资源权限服务
 *
 * <p>提供资源权限检查的业务逻辑实现,调用权限管理模块进行权限验证。</p>
 *
 * @author Junjun
 */
@Component
public class ResourceService {
    /**
     * 核心权限管理
     */
    @Resource
    private CorePermissionManage corePermissionManage;

    /**
     * 检查用户对资源的读取权限
     * @param id 资源ID
     * @return 是否有读取权限,如果发生异常则返回 false
     */
    public boolean checkPermission(Long id) {
        // 构建权限检查 DTO
        BusiPerCheckDTO dto = new BusiPerCheckDTO();
        dto.setId(id);
        dto.setAuthEnum(AuthEnum.READ);

        // 调用权限管理进行检查,异常情况返回 false
        boolean b;
        try {
            b = corePermissionManage.checkAuth(dto);
        } catch (Exception e) {
            b = false;
        }
        return b;
    }
}
