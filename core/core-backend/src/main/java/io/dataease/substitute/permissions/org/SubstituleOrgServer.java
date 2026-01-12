package io.dataease.substitute.permissions.org;

import io.dataease.api.permissions.org.vo.MountedVO;
import io.dataease.model.KeywordRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织服务替补实现
 *
 * <p>这是企业版组织服务的社区版替补实现,用于桌面版(社区版)中。
 * 当企业版的权限服务不可用时,使用此替补实现提供基础的组织功能。</p>
 *
 * <p>此实现仅提供一个固定的超级管理员组织,不支持多组织管理。</p>
 *
 * @author DataEase
 * @since 2.0
 */
@Component
@ConditionalOnMissingBean(name = "orgServer")
@RestController
@RequestMapping("/org")
public class SubstituleOrgServer {

    /**
     * 获取已挂载的组织列表
     *
     * <p>在替补实现中,固定返回一个超级管理员组织。</p>
     *
     * @param request 关键字请求对象(此替补实现中不使用)
     * @return 组织列表,固定包含一个超级管理员组织
     */
    @PostMapping("/mounted")
    public List<MountedVO> mounted(KeywordRequest request) {
        MountedVO mountedVO = new MountedVO();
        mountedVO.setId(1L);
        mountedVO.setName("超级管理员");
        mountedVO.setReadOnly(false);
        List<MountedVO> result = new ArrayList<>();
        result.add(mountedVO);
        return result;
    }
}
