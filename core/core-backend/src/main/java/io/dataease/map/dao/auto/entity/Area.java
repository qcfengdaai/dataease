package io.dataease.map.dao.auto.entity;

import java.io.Serializable;

/**
 * 地理区域实体类
 *
 * 功能描述：
 * 表示系统内置的地理区域信息，用于构建世界地图的层级结构
 *
 * 区域层级：
 * - world: 世界（根节点）
 * - continent: 洲
 * - country: 国家
 * - province: 省/州
 * - city: 市
 * - district: 区/县
 *
 * 数据表：area
 *
 * @author fit2cloud
 * @since 2023-07-09
 */
public class Area implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 区域ID
     * 与地图文件名对应，例如：156代表中国，156110000代表北京市
     */
    private String id;

    /**
     * 区域级别
     * 从高到低依次为：world(世界) > country(国家) > province(省份) > city(城市) > district(区县)
     */
    private String level;

    /**
     * 区域名称
     * 例如：中国、北京市、朝阳区等
     */
    private String name;

    /**
     * 父级区域ID
     * 用于构建树形结构，根节点的pid为"000"（世界）
     */
    private String pid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "Area{" +
        "id = " + id +
        ", level = " + level +
        ", name = " + name +
        ", pid = " + pid +
        "}";
    }
}
