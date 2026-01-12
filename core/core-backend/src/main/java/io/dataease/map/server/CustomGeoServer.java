package io.dataease.map.server;

import io.dataease.api.map.CustomGeoApi;
import io.dataease.api.map.vo.AreaNode;
import io.dataease.api.map.vo.CustomGeoArea;
import io.dataease.api.map.vo.CustomGeoSubArea;
import io.dataease.map.manage.MapManage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 自定义地理区域服务控制器
 * 提供自定义地理区域的管理接口，包括区域的增删改查
 */
@RestController
@RequestMapping("/customGeo")
public class CustomGeoServer implements CustomGeoApi {

    @Resource
    private MapManage mapManage;

    /**
     * 查询所有自定义地理区域
     * @return 自定义地理区域列表
     */
    @Override
    public List<CustomGeoArea> listCustomGeoArea() {
        return mapManage.listCustomGeoArea();
    }

    /**
     * 获取指定地理区域的子区域
     * @param id 地理区域ID
     * @return 子区域列表
     */
    @Override
    public List<CustomGeoSubArea> getCustomGeoArea(String id) {
        return mapManage.getCustomGeoArea(id);
    }

    /**
     * 删除自定义地理区域
     * @param id 地理区域ID
     */
    @Override
    public void deleteCustomGeoArea(String id) {
        mapManage.deleteCustomGeoArea(id);
    }

    /**
     * 保存自定义地理区域
     * @param geoArea 地理区域对象
     */
    @Override
    public void saveCustomGeoArea(CustomGeoArea geoArea) {
        mapManage.saveCustomGeoArea(geoArea);
    }

    /**
     * 删除子区域
     * @param id 子区域ID
     */
    @Override
    public void deleteCustomGeoSubArea(long id) {
        mapManage.deleteCustomGeoSubArea(id);
    }

    /**
     * 保存子区域
     * @param geoSubArea 子区域对象
     */
    @Override
    public void saveCustomGeoSubArea(CustomGeoSubArea geoSubArea) {
        mapManage.saveCustomGeoSubArea(geoSubArea);
    }

    /**
     * 获取自定义子区域选项
     * @return 子区域节点列表
     */
    @Override
    public List<AreaNode> getCustomGeoSubAreaOptions() {
        return mapManage.getCustomGeoSubAreaOptions();
    }
}
