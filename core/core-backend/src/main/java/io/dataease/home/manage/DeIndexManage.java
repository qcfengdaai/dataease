package io.dataease.home.manage;

import io.dataease.license.config.XpackInteract;
import org.springframework.stereotype.Component;

/**
 * 首页管理类
 * 用于处理首页相关的业务逻辑，包括企业版功能扩展
 */
@Component
public class DeIndexManage {

    /**
     * 获取企业版模式状态
     * 通过XpackInteract注解实现企业版功能扩展
     * @return 是否为企业版模式，true表示企业版，false表示社区版
     */
    @XpackInteract(value = "deIndexManage", replace = true)
    public Boolean xpackModel() {
        return null;
    }
}
