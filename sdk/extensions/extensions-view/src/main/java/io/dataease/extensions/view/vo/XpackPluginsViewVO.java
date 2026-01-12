package io.dataease.extensions.view.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * X-pack插件视图VO
 * 用于传输插件视图配置信息
 */
@Data
public class XpackPluginsViewVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5059944608544058565L;

    /**
     * 插件ID
     */
    private Long id;

    /**
     * 插件名称
     */
    private String name;

    /**
     * 插件图标
     */
    private String icon;

    /**
     * 插件分类
     */
    private String category;

    /**
     * 插件标题
     */
    private String title;

    /**
     * 图表值配置
     */
    private String chartValue;

    /**
     * 图表标题
     */
    private String chartTitle;

    /**
     * 渲染器类型
     */
    private String render;

    /**
     * 静态资源映射
     */
    private Map<String, String> staticMap;
}
