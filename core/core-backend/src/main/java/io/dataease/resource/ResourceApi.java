package io.dataease.resource;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资源权限检查 API
 *
 * <p>提供资源权限检查的 REST 接口,用于验证用户对特定资源的访问权限。</p>
 *
 * @author Junjun
 */
@RestController
@RequestMapping("/resource")
public class ResourceApi {
    /**
     * 资源服务
     */
    @Resource
    private ResourceService resourceService;

    /**
     * 检查用户对资源的访问权限
     * @param id 资源ID
     * @return 是否有权限
     */
    @PostMapping("checkPermission/{id}")
    public boolean checkPermission(@PathVariable("id") Long id) {
        return resourceService.checkPermission(id);
    }
}
