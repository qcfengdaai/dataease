package io.dataease.map.dao.auto.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 自定义地理区域实体类
 *
 * 功能描述：
 * 表示用户自定义的地理区域，用于扩展系统内置的地图功能
 * 用户可以创建自己的地理区域并进行数据分析
 *
 * 使用场景：
 * - 用户需要分析特定的行政区域或业务区域
 * - 用户需要将地图数据按自定义的区域进行聚合展示
 *
 * 数据表：core_custom_geo_area
 *
 * @author fit2cloud
 * @since 2024-11-22
 */
@TableName("core_custom_geo_area")
public class CoreCustomGeoArea implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自定义地理区域ID
     * 格式为"custom_" + 雪花算法ID，例如：custom_1234567890
     */
    private String id;

    /**
     * 区域名称
     * 用户定义的地理区域名称，例如："华东销售区"、"华南大区"等
     */
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CoreCustomGeoArea{" +
        "id = " + id +
        ", name = " + name +
        "}";
    }
}
