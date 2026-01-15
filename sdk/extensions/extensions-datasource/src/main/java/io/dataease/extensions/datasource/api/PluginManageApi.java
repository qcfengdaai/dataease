package io.dataease.extensions.datasource.api;

import io.dataease.extensions.datasource.vo.XpackPluginsDatasourceVO;

import java.util.List;

/**
 * DataEase数据源插件管理API接口
 * <p>
 * 定义数据源插件的管理操作接口，主要负责数据源插件的查询、管理和配置功能。
 * 该接口是数据源插件系统的核心API，提供统一的插件管理入口。
 * </p>
 *
 * <p><b>主要功能：</b></p>
 * <ul>
 *   <li><b>插件查询</b> - 获取已安装的数据源插件列表</li>
 *   <li><b>插件管理</b> - 支持插件的启用、禁用和配置管理</li>
 *   <li><b>插件信息</b> - 提供插件的详细信息和状态查询</li>
 * </ul>
 *
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li>系统管理员管理数据源插件</li>
 *   <li>动态加载和配置数据源连接</li>
 *   <li>插件状态监控和管理</li>
 *   <li>数据源类型扩展和集成</li>
 * </ul>
 *
 * <p><b>实现说明：</b></p>
 * <ul>
 *   <li>该接口通常由企业版(Xpack)模块实现</li>
 *   <li>支持SPI机制的插件发现和管理</li>
 *   <li>提供统一的插件生命周期管理</li>
 * </ul>
 *
 * @author Junjun
 * @author fit2cloud
 * @since 1.0
 */
public interface PluginManageApi {

    /**
     * 查询数据源插件列表
     * <p>
     * 获取系统中所有已安装和可用的数据源插件信息，包括插件状态、
     * 配置信息、支持的数据源类型等详细信息。该方法是插件管理的核心查询接口。
     * </p>
     *
     * <p><b>返回信息包含：</b></p>
     * <ul>
     *   <li><b>插件基本信息</b> - 插件名称、版本、描述等</li>
     *   <li><b>插件状态</b> - 是否启用、加载状态等</li>
     *   <li><b>数据源支持</b> - 支持的数据源类型和连接参数</li>
     *   <li><b>配置参数</b> - 插件的配置项和默认值</li>
     * </ul>
     *
     * <p><b>使用示例：</b></p>
     * <pre><code>
     * // 获取所有数据源插件
     * List&lt;XpackPluginsDatasourceVO&gt; plugins = pluginManageApi.queryPluginDs();
     *
     * // 遍历插件信息
     * for (XpackPluginsDatasourceVO plugin : plugins) {
     *     System.out.println("插件名称: " + plugin.getName());
     *     System.out.println("支持数据源: " + plugin.getDatasourceType());
     *     System.out.println("插件状态: " + plugin.getStatus());
     * }
     * </code></pre>
     *
     * <p><b>应用场景：</b></p>
     * <ul>
     *   <li>管理界面显示可用插件列表</li>
     *   <li>数据源创建时选择插件类型</li>
     *   <li>系统监控检查插件状态</li>
     *   <li>插件配置和参数管理</li>
     * </ul>
     *
     * @return 数据源插件信息列表，包含插件的详细信息和状态
     */
    List<XpackPluginsDatasourceVO> queryPluginDs();
}
