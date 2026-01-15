package io.dataease.extensions.datafilling.plugin;

import io.dataease.exception.DEException;
import io.dataease.extensions.datafilling.factory.ExtDDLProviderFactory;
import io.dataease.extensions.datafilling.provider.ExtDDLProvider;
import io.dataease.extensions.datafilling.vo.XpackPluginsDfVO;
import io.dataease.license.utils.JsonUtil;
import io.dataease.plugins.template.DataEasePlugin;
import io.dataease.plugins.vo.DataEasePluginVO;

/**
 * 数据填报插件抽象基类
 * 为数据填报扩展插件提供统一的生命周期管理和配置加载机制
 *
 * <p>设计模式：</p>
 * <ul>
 *     <li>模板方法模式：定义插件加载和卸载的标准流程，具体DDL实现由子类提供</li>
 *     <li>策略模式：不同数据库类型的插件实现不同的DDL生成策略</li>
 * </ul>
 *
 * <p>在数据填报模块中的作用：</p>
 * <ul>
 *     <li>作为所有数据填报插件的基类，统一管理插件的生命周期</li>
 *     <li>继承ExtDDLProvider，为不同数据库提供DDL（数据定义语言）生成能力</li>
 *     <li>实现DataEasePlugin接口，集成到DataEase的插件管理体系中</li>
 *     <li>负责将插件注册到ExtDDLProviderFactory，供业务层调用</li>
 * </ul>
 *
 * <p>实现要求：</p>
 * <ul>
 *     <li>子类必须实现ExtDDLProvider中定义的所有抽象方法（如createTableSql、dropTableSql等）</li>
 *     <li>子类需要提供plugin.json配置文件，包含插件的元数据信息</li>
 *     <li>插件的type字段必须与DatasourceConfiguration.DatasourceType枚举值对应</li>
 *     <li>子类通常以数据库类型命名，如OracleDataFillingPlugin、SqlServerDataFillingPlugin</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *     <li>为Oracle、SQL Server、PostgreSQL等不同数据库开发数据填报插件</li>
 *     <li>插件在系统启动时通过SPI机制自动加载</li>
 *     <li>用户创建数据填报表单时，根据数据源类型动态选择相应的插件</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>
 * // 创建一个Oracle数据填报插件
 * public class OracleDataFillingPlugin extends DataFillingPlugin {
 *
 *     &#64;Override
 *     public String createTableSql(String table, List&lt;ExtTableField&gt; formFields) {
 *         // 实现Oracle特定的建表SQL生成逻辑
 *         StringBuilder sql = new StringBuilder("CREATE TABLE ");
 *         sql.append(table).append(" (");
 *         // ... 拼接字段定义
 *         return sql.toString();
 *     }
 *
 *     &#64;Override
 *     public String dropTableSql(String table) {
 *         return "DROP TABLE " + table;
 *     }
 *
 *     // 实现其他抽象方法...
 * }
 * </pre>
 */
public abstract class DataFillingPlugin extends ExtDDLProvider implements DataEasePlugin {

    /**
     * 加载插件
     * 在插件初始化时调用，负责读取插件配置并将插件注册到工厂中
     *
     * <p>执行流程：</p>
     * <ol>
     *     <li>调用getConfig()方法获取插件配置信息</li>
     *     <li>从配置中提取插件类型（type）</li>
     *     <li>调用ExtDDLProviderFactory.loadPlugin()将插件实例注册到工厂的缓存中</li>
     *     <li>注册完成后，业务层可以通过类型查找到对应的插件实例</li>
     * </ol>
     *
     * <p>该方法由DataEase插件管理框架在系统启动时自动调用</p>
     */
    @Override
    public void loadPlugin() {
        XpackPluginsDfVO viewConfig = getConfig();
        ExtDDLProviderFactory.loadPlugin(viewConfig.getType(), this);
    }


    /**
     * 获取插件配置信息
     * 从plugin.json配置文件中读取插件的元数据配置，包括类型、图标、分类等信息
     *
     * <p>配置加载流程：</p>
     * <ol>
     *     <li>调用getPluginInfo()方法读取plugin.json文件内容</li>
     *     <li>从插件信息中提取config字段（JSON格式的配置字符串）</li>
     *     <li>将config字符串反序列化为XpackPluginsDfVO对象</li>
     *     <li>补充设置插件图标信息</li>
     * </ol>
     *
     * <p>plugin.json配置示例：</p>
     * <pre>
     * {
     *   "moduleName": "oracle-datafilling",
     *   "icon": "&lt;svg&gt;...&lt;/svg&gt;",
     *   "config": "{\"type\":\"oracle\",\"category\":\"datafilling\",\"flag\":1}"
     * }
     * </pre>
     *
     * @return 插件配置对象，包含插件的所有元数据信息
     * @throws DEException 如果配置文件读取失败或配置格式错误
     */
    public XpackPluginsDfVO getConfig() {
        DataEasePluginVO pluginInfo = null;
        try {
            pluginInfo = getPluginInfo();
        } catch (Exception e) {
            DEException.throwException(e);
        }
        String config = pluginInfo.getConfig();
        XpackPluginsDfVO vo = JsonUtil.parseObject(config, XpackPluginsDfVO.class);
        vo.setIcon(pluginInfo.getIcon());
        return vo;
    }

    /**
     * 卸载插件
     * 在插件卸载时调用，用于清理插件占用的资源
     *
     * <p>当前实现为空方法，子类可以根据需要覆盖此方法，执行以下清理操作：</p>
     * <ul>
     *     <li>关闭数据库连接池</li>
     *     <li>清理缓存数据</li>
     *     <li>释放其他系统资源</li>
     * </ul>
     *
     * <p>注意：由于数据填报插件通常在系统运行期间持续使用，很少需要卸载操作</p>
     */
    @Override
    public void unloadPlugin() {

    }
}
