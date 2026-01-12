package io.dataease.extensions.view.factory;

import io.dataease.exception.DEException;
import io.dataease.extensions.view.plugin.AbstractChartPlugin;
import io.dataease.extensions.view.plugin.DataEaseChartPlugin;
import io.dataease.extensions.view.vo.XpackPluginsViewVO;
import io.dataease.license.utils.LicenseUtil;
import io.dataease.license.utils.LogUtil;
import io.dataease.plugins.factory.DataEasePluginFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件图表工厂类
 * 负责管理和维护图表插件的注册、加载和获取功能
 * 使用线程安全的ConcurrentHashMap存储插件实例
 */
public class PluginsChartFactory {

    /**
     * 插件模板映射表
     * key: render_type格式的字符串，value: 对应的DataEase图表插件实例
     */
    private static final Map<String, DataEaseChartPlugin> templateMap = new ConcurrentHashMap<>();

    /**
     * 获取插件实例
     * 根据渲染器类型和图表类型获取对应的插件实例
     *
     * @param render 渲染器类型
     * @param type 图表类型
     * @return 对应的抽象图表插件实例，如果不存在则返回null
     * @throws DEException 当许可证无效时抛出异常
     */
    public static AbstractChartPlugin getInstance(String render, String type) {
        // 检查企业版许可证
        if (!LicenseUtil.licenseValid()) DEException.throwException("插件功能只对企业版本可用！");
        // 构建插件键值
        String key = render + "_" + type;
        return templateMap.get(key);
    }

    /**
     * 加载图表插件
     * 将图表插件注册到工厂中，并同步注册到DataEase插件工厂
     *
     * @param render 渲染器类型
     * @param type 图表类型
     * @param plugin 图表插件实例
     * @throws DEException 当许可证无效或插件加载失败时抛出异常
     */
    public static void loadPlugin(String render, String type, DataEaseChartPlugin plugin) {
        // 检查企业版许可证
        if (!LicenseUtil.licenseValid()) DEException.throwException("插件功能只对企业版本可用！");
        // 构建插件键值
        String key = render + "_" + type;
        // 如果插件已存在则跳过加载
        if (templateMap.containsKey(key)) return;
        // 注册插件到工厂
        templateMap.put(key, plugin);
        try {
            // 获取插件模块名并注册到DataEase插件工厂
            String moduleName = plugin.getPluginInfo().getModuleName();
            DataEasePluginFactory.loadTemplate(moduleName, plugin);
        } catch (Exception e) {
            // 记录错误日志并抛出异常
            LogUtil.error(e.getMessage(), new Throwable(e));
            DEException.throwException(e);
        }
    }

    /**
     * 获取视图配置列表
     * 返回所有已注册插件的视图配置信息列表
     *
     * @return 插件视图配置VO列表
     * @throws DEException 当许可证无效时抛出异常
     */
    public static List<XpackPluginsViewVO> getViewConfigList() {
        // 检查企业版许可证
        if (!LicenseUtil.licenseValid()) DEException.throwException("插件功能只对企业版本可用！");
        // 将所有插件的配置信息转换为VO列表返回
        return templateMap.values().stream().map(DataEaseChartPlugin::getConfig).toList();
    }
}
