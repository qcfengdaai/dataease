package io.dataease.msgCenter;

import io.dataease.api.msgCenter.MsgCenterApi;
import io.dataease.license.config.XpackInteract;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息中心服务控制器
 * 提供消息中心相关的REST API接口，由企业版扩展实现
 */
@RestController
@RequestMapping("/msg-center")
public class MsgCenterServer implements MsgCenterApi {
    /**
     * 统计消息数量，由企业版实现
     * @return 消息数量
     */
    @Override
    @XpackInteract(value = "msgCenterServer", replace = true)
    public long count() {
        return 0;
    }
}
