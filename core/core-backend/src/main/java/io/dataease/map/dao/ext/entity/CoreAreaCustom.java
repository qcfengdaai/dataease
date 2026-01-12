package io.dataease.map.dao.ext.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义区域扩展实体类
 *
 * 功能描述：
 * 用于存储用户通过上传GeoJSON文件创建的自定义地理区域
 * 与系统内置区域一样，也支持层级结构（父子关系）
 *
 * 与CoreCustomGeoArea的区别：
 * - CoreAreaCustom: 通过上传GeoJSON文件创建，存储在扩展表，ID带geo_前缀
 * - CoreCustomGeoArea: 通过前端界面创建，可以包含多个子分区
 *
 * 数据表：core_area_custom（扩展表）
 *
 * @author DataEase
 */
@Data
public class CoreAreaCustom implements Serializable {

    /**
     * 自定义区域ID
     * 格式为"geo_" + 数字编码，例如：geo_156110000
     */
    private String id;

    /**
     * 父级区域ID
     * 用于构建树形结构，可以指向系统区域或其他自定义区域
     */
    private String pid;

    /**
     * 区域名称
     * 例如："北京市海淀区"、"某销售区域"等
     */
    private String name;
}
