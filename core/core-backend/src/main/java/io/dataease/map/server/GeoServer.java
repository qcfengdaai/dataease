package io.dataease.map.server;

import io.dataease.api.map.GeoApi;
import io.dataease.api.map.dto.GeometryNodeCreator;
import io.dataease.map.manage.MapManage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 地理区域服务控制器
 *
 * 功能描述：
 * 提供地理区域的RESTful API接口，包括区域的创建、删除等操作
 *
 * 接口路径：/geometry
 *
 * @author DataEase
 * @since 2023-07-09
 */
@RestController
@RequestMapping("/geometry")
public class GeoServer implements GeoApi {

    /**
     * 地图管理业务逻辑组件
     */
    @Resource
    private MapManage mapManage;

    /**
     * 保存自定义地图地理区域
     * 支持上传GeoJSON格式的地理边界文件，创建新的自定义区域
     *
     * @param request 地理区域创建请求，包含编码、名称、父节点ID等信息
     * @param file GeoJSON格式的地理边界文件
     */
    @Override
    public void saveMapGeo(GeometryNodeCreator request, MultipartFile file) {
        mapManage.saveMapGeo(request, file);
    }

    /**
     * 删除自定义地理区域
     * 删除指定区域及其所有子区域的数据库记录和文件
     *
     * @param id 要删除的区域编码
     */
    @Override
    public void deleteGeo(String id) {
        mapManage.deleteGeo(id);
    }
}
