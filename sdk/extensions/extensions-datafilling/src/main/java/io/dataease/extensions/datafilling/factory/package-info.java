/**
 * 数据填报工厂类包
 * <p>
 * 本包提供数据填报插件的工厂类，负责插件的加载、注册、管理和获取。
 * 采用工厂模式+单例模式+注册表模式，确保插件的高效管理和使用。
 * </p>
 *
 * <p><b>核心工厂类：</b></p>
 * <ul>
 *   <li>{@link io.dataease.extensions.datafilling.factory.ExtDDLProviderFactory} - DDL提供者工厂类</li>
 * </ul>
 *
 * <p><b>设计模式：</b></p>
 * <ul>
 *   <li><b>工厂模式</b> - 统一的插件获取接口，屏蔽创建细节</li>
 *   <li><b>单例模式</b> - 每种类型的插件在系统中只有一个实例</li>
 *   <li><b>注册表模式</b> - 使用ConcurrentHashMap管理所有已加载的插件</li>
 * </ul>
 *
 * <p><b>工厂工作流程：</b></p>
 * <pre>
 * 1. 系统启动
 *    ↓
 * 2. DataFillingPlugin.loadPlugin()
 *    ↓
 * 3. ExtDDLProviderFactory.loadPlugin(type, plugin)
 *    ↓
 * 4. 插件注册到 templateMap (ConcurrentHashMap)
 *    ↓
 * 5. 业务代码调用
 *    ↓
 * 6. ExtDDLProviderFactory.getExtDDLProvider(type)
 *    ↓
 * 7. 从 templateMap 获取插件实例
 *    ↓
 * 8. 调用插件方法生成DDL
 * </pre>
 *
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li><b>插件获取</b> - 根据数据库类型获取对应的DDL提供者</li>
 *   <li><b>插件注册</b> - 系统启动时自动注册所有数据填报插件</li>
 *   <li><b>插件列表</b> - 查询所有已加载的插件配置信息</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 1. 获取DDL提供者（最常用）
 * {@literal @}Service
 * public class DataFillingService {
 *
 *     public void createTable(String datasourceType, String tableName, List&lt;ExtTableField&gt; fields) {
 *         // 根据数据源类型获取对应的DDL提供者
 *         ExtDDLProvider provider = ExtDDLProviderFactory.getExtDDLProvider(datasourceType);
 *
 *         // 生成建表SQL
 *         String createTableSql = provider.createTableSql(tableName, fields);
 *
 *         // 执行SQL
 *         jdbcTemplate.execute(createTableSql);
 *     }
 * }
 *
 * // 2. 获取所有插件配置
 * {@literal @}RestController
 * public class PluginController {
 *
 *     {@literal @}GetMapping("/api/plugins/datafilling")
 *     public Result&lt;List&lt;XpackPluginsDfVO&gt;&gt; listPlugins() {
 *         List&lt;XpackPluginsDfVO&gt; configList = ExtDDLProviderFactory.getDfConfigList();
 *         return Result.success(configList);
 *     }
 * }
 *
 * // 3. 判断数据源是否支持数据填报
 * public boolean isDatasourceSupported(String type) {
 *     ExtDDLProvider provider = ExtDDLProviderFactory.getInstance(type);
 *     return provider != null;
 * }
 *
 * // 4. 插件自动加载（由系统完成）
 * // 插件类实现：
 * public class OracleDataFillingPlugin extends DataFillingPlugin {
 *     {@literal @}Override
 *     public void loadPlugin() {
 *         XpackPluginsDfVO config = getConfig();
 *         // 自动调用工厂注册方法
 *         ExtDDLProviderFactory.loadPlugin(config.getType(), this);
 *     }
 * }
 * </pre>
 *
 * <p><b>支持的数据库类型：</b></p>
 * <table border="1" cellpadding="5">
 *   <tr>
 *     <th>数据库类型</th>
 *     <th>type值</th>
 *     <th>实现方式</th>
 *   </tr>
 *   <tr>
 *     <td>MySQL</td>
 *     <td>mysql</td>
 *     <td>内置（Spring Bean）</td>
 *   </tr>
 *   <tr>
 *     <td>MariaDB</td>
 *     <td>mariadb</td>
 *     <td>内置（Spring Bean）</td>
 *   </tr>
 *   <tr>
 *     <td>Oracle</td>
 *     <td>oracle</td>
 *     <td>插件（需要企业版）</td>
 *   </tr>
 *   <tr>
 *     <td>SQL Server</td>
 *     <td>sqlserver</td>
 *     <td>插件（需要企业版）</td>
 *   </tr>
 *   <tr>
 *     <td>PostgreSQL</td>
 *     <td>postgresql</td>
 *     <td>插件（需要企业版）</td>
 *   </tr>
 *   <tr>
 *     <td>达梦</td>
 *     <td>dm</td>
 *     <td>插件（需要企业版）</td>
 *   </tr>
 * </table>
 *
 * <p><b>内置实现与插件的区别：</b></p>
 * <ul>
 *   <li><b>内置实现（MySQL/MariaDB）</b>
 *     <ul>
 *       <li>无需加载插件，直接从Spring容器获取</li>
 *       <li>社区版和企业版都可用</li>
 *       <li>通过switch语句返回Spring Bean</li>
 *     </ul>
 *   </li>
 *   <li><b>插件实现（Oracle/SQL Server等）</b>
 *     <ul>
 *       <li>需要加载插件JAR包</li>
 *       <li>仅企业版可用（需要License验证）</li>
 *       <li>通过templateMap注册表获取</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p><b>线程安全性：</b></p>
 * <ul>
 *   <li>使用ConcurrentHashMap保证并发安全</li>
 *   <li>loadPlugin()方法在系统启动时调用，无并发问题</li>
 *   <li>getExtDDLProvider()方法可以并发调用，线程安全</li>
 * </ul>
 *
 * <p><b>与其他模块的关系：</b></p>
 * <ul>
 *   <li><b>plugin包</b> - DataFillingPlugin调用loadPlugin()注册插件</li>
 *   <li><b>provider包</b> - 返回ExtDDLProvider接口的实现类</li>
 *   <li><b>vo包</b> - getDfConfigList()返回XpackPluginsDfVO列表</li>
 *   <li><b>core模块</b> - 业务逻辑调用工厂获取DDL提供者</li>
 * </ul>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>插件加载失败时会抛出DEException异常</li>
 *   <li>不存在的数据库类型会返回null或抛出异常</li>
 *   <li>插件重复注册会被忽略（containsKey判断）</li>
 *   <li>License验证代码已被注释，但保留了验证逻辑</li>
 * </ul>
 *
 * @author DataEase
 * @since 2.0.0
 * @see io.dataease.extensions.datafilling.plugin.DataFillingPlugin
 * @see io.dataease.extensions.datafilling.provider.ExtDDLProvider
 * @see io.dataease.extensions.datafilling.vo.XpackPluginsDfVO
 */
package io.dataease.extensions.datafilling.factory;
