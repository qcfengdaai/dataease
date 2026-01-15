package io.dataease.extensions.datafilling.api;

import io.dataease.extensions.datafilling.vo.XpackPluginsDfVO;

import java.util.List;

/**
 * 数据填报插件管理API接口
 * 提供数据填报插件的查询和管理功能，用于获取系统中已加载的数据填报插件信息
 * 该接口是数据填报插件系统对外提供服务的核心接口，供业务层调用
 *
 * <p>职责说明：</p>
 * <ul>
 *     <li>查询系统中所有可用的数据填报插件配置</li>
 *     <li>为前端提供插件列表数据，用于展示和选择</li>
 *     <li>作为插件系统与业务层之间的桥梁接口</li>
 * </ul>
 *
 * <p>实现要求：</p>
 * <ul>
 *     <li>实现类需要从ExtDDLProviderFactory获取已加载的插件列表</li>
 *     <li>返回的插件配置应包含完整的元数据信息（类型、图标、分类等）</li>
 *     <li>需要处理插件未加载或加载失败的异常情况</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *     <li>数据填报表单设计器中选择数据库类型时，调用此接口获取支持的数据库插件列表</li>
 *     <li>系统管理员查看已安装的数据填报插件时，通过此接口获取插件信息</li>
 *     <li>前端根据返回的插件类型动态加载相应的数据库配置界面</li>
 * </ul>
 */
public interface DfPluginManageApi {

    /**
     * 查询所有数据填报插件配置
     * 从插件工厂中获取所有已加载的数据填报插件的配置信息
     *
     * <p>该方法会返回系统中所有已成功加载的数据填报插件配置，包括：</p>
     * <ul>
     *     <li>插件支持的数据库类型（如Oracle、SQL Server、PostgreSQL等）</li>
     *     <li>插件的显示图标和分类信息</li>
     *     <li>插件的元数据配置</li>
     * </ul>
     *
     * <p>使用示例：</p>
     * <pre>
     * // 在数据填报表单设计器中获取可用的数据库插件
     * DfPluginManageApi pluginApi = ...; // 通过依赖注入或服务定位获取实现类
     * List&lt;XpackPluginsDfVO&gt; plugins = pluginApi.queryPluginDf();
     *
     * // 遍历插件列表，展示给用户选择
     * for (XpackPluginsDfVO plugin : plugins) {
     *     System.out.println("数据库类型: " + plugin.getType());
     *     System.out.println("插件图标: " + plugin.getIcon());
     * }
     * </pre>
     *
     * @return 数据填报插件配置列表，每个元素包含一个插件的完整配置信息
     *         如果系统中没有加载任何插件，则返回空列表（不返回null）
     */
    List<XpackPluginsDfVO> queryPluginDf();
}
