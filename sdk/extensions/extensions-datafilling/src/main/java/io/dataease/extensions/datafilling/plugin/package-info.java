/**
 * 数据填报插件包
 * <p>
 * 本包定义数据填报插件的基类和生命周期管理。所有数据填报插件都需要继承
 * {@link io.dataease.extensions.datafilling.plugin.DataFillingPlugin}抽象类，
 * 并实现DDL生成逻辑。
 * </p>
 *
 * <p><b>核心类：</b></p>
 * <ul>
 *   <li>{@link io.dataease.extensions.datafilling.plugin.DataFillingPlugin} - 数据填报插件抽象基类</li>
 * </ul>
 *
 * <p><b>插件架构：</b></p>
 * <pre>
 * DataEasePlugin (插件顶层接口)
 *       ↑
 *       |
 * DataFillingPlugin (数据填报插件基类)
 *       ↑
 *       |
 * ExtDDLProvider (DDL生成抽象类)
 *       ↑
 *       |
 *  ┌────┴────┬─────────┬──────────┐
 *  |         |         |          |
 * MySQL   Oracle   SQLServer   其他数据库
 * Plugin  Plugin    Plugin      Plugin
 * </pre>
 *
 * <p><b>插件生命周期：</b></p>
 * <ol>
 *   <li><b>加载</b> - 系统启动时调用loadPlugin()，将插件注册到工厂</li>
 *   <li><b>使用</b> - 通过ExtDDLProviderFactory获取插件实例，调用DDL生成方法</li>
 *   <li><b>卸载</b> - 系统关闭时调用unloadPlugin()，执行清理操作</li>
 * </ol>
 *
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li><b>插件开发</b> - 扩展支持新的数据库类型（如Oracle、SQL Server）</li>
 *   <li><b>DDL生成</b> - 根据表单配置生成数据库特定的DDL语句</li>
 *   <li><b>插件管理</b> - 系统自动加载和管理所有数据填报插件</li>
 * </ul>
 *
 * <p><b>插件实现示例：</b></p>
 * <pre>
 * // 1. 创建Oracle数据填报插件
 * public class OracleDataFillingPlugin extends DataFillingPlugin {
 *
 *     {@literal @}Override
 *     public String createTableSql(String table, List&lt;ExtTableField&gt; formFields) {
 *         // 生成Oracle的CREATE TABLE语句
 *         StringBuilder sql = new StringBuilder("CREATE TABLE ");
 *         sql.append(table).append(" (\\n");
 *
 *         for (ExtTableField field : formFields) {
 *             ExtTableFieldMapping mapping = field.getSettings().getMapping();
 *             sql.append("  ").append(mapping.getColumnName()).append(" ");
 *
 *             // 根据BaseType生成Oracle数据类型
 *             switch (mapping.getType()) {
 *                 case nvarchar -> sql.append("VARCHAR2(").append(mapping.getSize()).append(")");
 *                 case text -> sql.append("CLOB");
 *                 case number -> sql.append("NUMBER");
 *                 case decimal -> sql.append("NUMBER(").append(mapping.getSize())
 *                     .append(",").append(mapping.getAccuracy()).append(")");
 *                 case datetime -> sql.append("TIMESTAMP");
 *             }
 *
 *             if (field.getSettings().isRequired()) {
 *                 sql.append(" NOT NULL");
 *             }
 *             sql.append(",\\n");
 *         }
 *
 *         sql.append("  PRIMARY KEY (id)\\n");
 *         sql.append(")");
 *
 *         return sql.toString();
 *     }
 *
 *     // 实现其他抽象方法...
 * }
 *
 * // 2. 配置插件信息（plugin.json）
 * {
 *   "moduleName": "oracle-datafilling",
 *   "icon": "&lt;svg&gt;...&lt;/svg&gt;",
 *   "config": {
 *     "type": "oracle",
 *     "category": "datafilling",
 *     "flag": 1
 *   }
 * }
 *
 * // 3. 插件自动加载（系统启动时）
 * // DataFillingPlugin.loadPlugin() 会被自动调用
 * // 插件注册到 ExtDDLProviderFactory
 *
 * // 4. 使用插件生成DDL
 * ExtDDLProvider provider = ExtDDLProviderFactory.getExtDDLProvider("oracle");
 * String ddl = provider.createTableSql("t_user", fieldList);
 * </pre>
 *
 * <p><b>插件开发规范：</b></p>
 * <ul>
 *   <li><b>继承基类</b> - 必须继承DataFillingPlugin并实现所有抽象方法</li>
 *   <li><b>配置文件</b> - 提供plugin.json配置文件，定义插件元信息</li>
 *   <li><b>数据库兼容</b> - 生成的DDL必须符合目标数据库的语法规范</li>
 *   <li><b>异常处理</b> - 妥善处理异常，避免影响主流程</li>
 *   <li><b>性能优化</b> - 避免在DDL生成过程中执行耗时操作</li>
 * </ul>
 *
 * <p><b>与其他模块的关系：</b></p>
 * <ul>
 *   <li><b>provider包</b> - DataFillingPlugin继承自ExtDDLProvider</li>
 *   <li><b>factory包</b> - loadPlugin()方法将插件注册到ExtDDLProviderFactory</li>
 *   <li><b>dto包</b> - 使用ExtTableField、ExtIndexField等DTO生成DDL</li>
 *   <li><b>vo包</b> - getConfig()方法返回XpackPluginsDfVO插件配置</li>
 * </ul>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>插件类必须是public的，以便通过反射加载</li>
 *   <li>插件应该是无状态的，避免保存实例变量</li>
 *   <li>不同数据库的DDL语法差异较大，需要仔细处理</li>
 *   <li>插件异常不应导致系统崩溃</li>
 * </ul>
 *
 * @author DataEase
 * @since 2.0.0
 * @see io.dataease.extensions.datafilling.provider.ExtDDLProvider
 * @see io.dataease.extensions.datafilling.factory.ExtDDLProviderFactory
 * @see io.dataease.plugins.template.DataEasePlugin
 */
package io.dataease.extensions.datafilling.plugin;
