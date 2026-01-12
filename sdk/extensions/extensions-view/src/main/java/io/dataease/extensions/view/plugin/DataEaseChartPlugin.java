package io.dataease.extensions.view.plugin;

import io.dataease.exception.DEException;
import io.dataease.extensions.view.factory.PluginsChartFactory;
import io.dataease.extensions.view.vo.XpackPluginsViewVO;
import io.dataease.license.utils.JsonUtil;
import io.dataease.plugins.template.DataEasePlugin;
import io.dataease.plugins.vo.DataEasePluginVO;

/**
 * DataEase图表插件基础类
 * 继承抽象图表插件并实现DataEase插件接口，提供插件加载和配置管理功能
 */
public abstract class DataEaseChartPlugin extends AbstractChartPlugin implements DataEasePlugin {

    /**
     * 加载插件
     * 获取插件配置信息并向插件工厂注册当前插件
     */
    @Override
    public void loadPlugin() {
        // 获取插件视图配置
        XpackPluginsViewVO viewConfig = getConfig();
        // 向插件工厂注册插件，包含渲染器类型、图表值和插件实例
        PluginsChartFactory.loadPlugin(viewConfig.getRender(), viewConfig.getChartValue(), this);
    }

    /**
     * 获取插件配置信息
     * 解析插件基础信息并构建插件视图配置对象
     *
     * @return 插件视图配置VO对象
     */
    public XpackPluginsViewVO getConfig() {
        DataEasePluginVO pluginInfo = null;
        try {
            // 获取插件基础信息
            pluginInfo = getPluginInfo();
        } catch (Exception e) {
            // 异常情况下抛出DataEase异常
            DEException.throwException(e);
        }
        // 解析插件配置字符串
        String config = pluginInfo.getConfig();
        XpackPluginsViewVO vo = JsonUtil.parseObject(config, XpackPluginsViewVO.class);
        // 设置插件图标信息
        vo.setIcon(pluginInfo.getIcon());
        return vo;
    }
}
