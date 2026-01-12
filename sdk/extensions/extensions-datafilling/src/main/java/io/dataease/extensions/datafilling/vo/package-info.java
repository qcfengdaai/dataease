/**
 * 数据填报扩展VO包
 * <p>
 * 本包包含数据填报模块的视图对象（View Object），用于封装展示给前端或其他系统的数据。
 * VO通常用于API响应，包含经过处理和格式化的业务数据。
 * </p>
 *
 * <p><b>核心VO类：</b></p>
 * <ul>
 *   <li>{@link io.dataease.extensions.datafilling.vo.XpackPluginsDfVO} - 数据填报插件配置视图对象</li>
 * </ul>
 *
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li><b>插件列表展示</b> - 查询所有已加载的数据填报插件配置</li>
 *   <li><b>插件信息返回</b> - API接口返回插件的基本信息（ID、图标、类型等）</li>
 *   <li><b>前端渲染</b> - 前端根据VO中的数据渲染插件选择界面</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 1. 查询所有数据填报插件
 * {@literal @}Autowired
 * private DfPluginManageApi dfPluginManageApi;
 *
 * List&lt;XpackPluginsDfVO&gt; plugins = dfPluginManageApi.queryPluginDf();
 * // 返回结果示例：
 * // [
 * //   {id: 1, type: "oracle", icon: "&lt;svg&gt;...&lt;/svg&gt;", category: "datafilling", flag: 1},
 * //   {id: 2, type: "sqlserver", icon: "&lt;svg&gt;...&lt;/svg&gt;", category: "datafilling", flag: 1}
 * // ]
 *
 * // 2. 前端使用插件列表渲染UI
 * plugins.forEach(plugin -> {
 *     System.out.println("插件类型: " + plugin.getType());
 *     System.out.println("插件图标: " + plugin.getIcon());
 * });
 *
 * // 3. 工厂类内部获取插件配置
 * List&lt;XpackPluginsDfVO&gt; configList = ExtDDLProviderFactory.getDfConfigList();
 * </pre>
 *
 * <p><b>VO与DTO的区别：</b></p>
 * <ul>
 *   <li><b>DTO（数据传输对象）</b> - 用于系统内部数据传输，包含完整的业务字段</li>
 *   <li><b>VO（视图对象）</b> - 用于展示层，包含经过处理的展示数据，字段更简洁</li>
 *   <li><b>转换关系</b> - 通常由Service层将Entity或DTO转换为VO返回给前端</li>
 * </ul>
 *
 * <p><b>与其他模块的关系：</b></p>
 * <ul>
 *   <li><b>api包</b> - {@link io.dataease.extensions.datafilling.api.DfPluginManageApi} 返回XpackPluginsDfVO列表</li>
 *   <li><b>factory包</b> - {@link io.dataease.extensions.datafilling.factory.ExtDDLProviderFactory} 管理插件VO</li>
 *   <li><b>plugin包</b> - {@link io.dataease.extensions.datafilling.plugin.DataFillingPlugin} 生成插件VO</li>
 * </ul>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>VO只包含展示所需的字段，不应包含敏感信息</li>
 *   <li>VO是只读的，不应在前端修改后回传服务器</li>
 *   <li>修改VO字段需要同步更新前端代码</li>
 * </ul>
 *
 * @author DataEase
 * @since 2.0.0
 * @see io.dataease.extensions.datafilling.api.DfPluginManageApi
 * @see io.dataease.extensions.datafilling.factory.ExtDDLProviderFactory
 */
package io.dataease.extensions.datafilling.vo;
