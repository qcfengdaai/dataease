package io.dataease.map.dao.auto.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 自定义地理子区域实体类
 *
 * 功能描述：
 * 表示自定义地理区域下的子分区，一个自定义区域可以包含多个子区域
 * 子区域用于更细粒度的地理数据分析和展示
 *
 * 使用场景：
 * - "华东销售区"可以细分为"江苏区"、"浙江区"、"上海区"等子区域
 * - 每个子区域可以包含具体的行政区划范围
 *
 * 数据表：core_custom_geo_sub_area
 *
 * @author fit2cloud
 * @since 2024-11-22
 */
@TableName("core_custom_geo_sub_area")
public class CoreCustomGeoSubArea implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 子区域ID
     * 使用雪花算法生成的唯一标识
     */
    private Long id;

    /**
     * 子区域名称
     * 例如："江苏区"、"浙江区"等
     */
    private String name;

    /**
     * 区域范围
     * JSON格式存储，包含该子区域所覆盖的行政区划代码列表
     * 例如：["156320000", "156330000"] 表示包含江苏省和浙江省
     */
    private String scope;

    /**
     * 所属自定义地理区域ID
     * 关联到core_custom_geo_area表的id字段
     */
    private String geoAreaId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getGeoAreaId() {
        return geoAreaId;
    }

    public void setGeoAreaId(String geoAreaId) {
        this.geoAreaId = geoAreaId;
    }

    @Override
    public String toString() {
        return "CoreCustomGeoSubArea{" +
        "id = " + id +
        ", name = " + name +
        ", scope = " + scope +
        ", geoAreaId = " + geoAreaId +
        "}";
    }
}
