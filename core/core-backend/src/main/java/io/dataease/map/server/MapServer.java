package io.dataease.map.server;

import io.dataease.api.map.MapApi;
import io.dataease.api.map.vo.AreaNode;
import io.dataease.map.manage.MapManage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 地图服务控制器
 * 提供地图相关的REST API接口，包括世界地理区域的查询
 */
@RestController
@RequestMapping("/map")
public class MapServer implements MapApi {
    @Resource
    private MapManage mapManage;

    /**
     * 获取世界地理区域树
     * @return 地理区域树结构
     */
    @Override
    public AreaNode getWorldTree() {
        return mapManage.getWorldTree();
    }
}
