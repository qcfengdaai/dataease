package io.dataease.extensions.datafilling.factory;

import io.dataease.exception.DEException;
import io.dataease.extensions.datafilling.plugin.DataFillingPlugin;
import io.dataease.extensions.datafilling.provider.ExtDDLProvider;
import io.dataease.extensions.datafilling.vo.XpackPluginsDfVO;
import io.dataease.extensions.datasource.utils.SpringContextUtil;
import io.dataease.extensions.datasource.vo.DatasourceConfiguration;
import io.dataease.license.utils.LicenseUtil;
import io.dataease.license.utils.LogUtil;
import io.dataease.plugins.factory.DataEasePluginFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据填报扩展DDL提供者工厂类
 * 负责管理和获取不同数据库类型的ExtDDLProvider实例
 *
 * <p>设计模式：</p>
 * <ul>
 *     <li>工厂模式：根据数据库类型创建或获取对应的DDL提供者</li>
 *     <li>单例模式：每种数据库类型只维护一个DDL提供者实例</li>
 *     <li>注册表模式：使用ConcurrentHashMap缓存已加载的插件实例</li>
 * </ul>
 *
 * <p>在数据填报模块中的作用：</p>
 * <ul>
 *     <li>作为数据填报插件系统的核心工厂，统一管理所有DDL提供者</li>
 *     <li>提供插件的加载、注册和查找功能</li>
 *     <li>区分内置数据库（MySQL/MariaDB）和插件数据库（Oracle/SQL Server等）</li>
 *     <li>确保同一数据库类型的DDL提供者在系统中只有一个实例</li>
 * </ul>
 *
 * <p>工作流程：</p>
 * <ol>
 *     <li>系统启动时，插件通过SPI机制自动加载并调用loadPlugin()方法注册</li>
 *     <li>注册时将插件实例存入templateMap缓存，key为"df_数据库类型"</li>
 *     <li>业务层需要使用时，通过getExtDDLProvider()方法获取对应的DDL提供者</li>
 *     <li>内置的MySQL/MariaDB直接从Spring容器获取，插件数据库从缓存获取</li>
 * </ol>
 *
 * <p>使用示例：</p>
 * <pre>
 * // 获取Oracle数据库的DDL提供者
 * ExtDDLProvider provider = ExtDDLProviderFactory.getExtDDLProvider("oracle");
 *
 * // 生成建表SQL
 * String createTableSql = provider.createTableSql("my_form_table", formFields);
 *
 * // 生成插入数据SQL
 * String insertSql = provider.insertDataSql("my_form_table", fields, 1);
 * </pre>
 */
public class ExtDDLProviderFactory {

    /**
     * 插件缓存容器
     * 存储已加载的数据填报插件实例，key为"df_数据库类型"，value为插件实例
     * 使用ConcurrentHashMap保证线程安全
     */
    private static final Map<String, DataFillingPlugin> templateMap = new ConcurrentHashMap<>();

    /**
     * 获取指定数据库类型的DDL提供者
     * 根据数据库类型返回对应的ExtDDLProvider实例，用于生成该数据库的DDL和DML语句
     *
     * <p>处理逻辑：</p>
     * <ol>
     *     <li>如果是MySQL或MariaDB，直接从Spring容器获取内置的mysqlExtDDLProvider Bean</li>
     *     <li>如果是其他数据库类型，从插件缓存中查找对应的插件实例</li>
     *     <li>如果找不到对应的插件，抛出异常提示用户检查插件</li>
     * </ol>
     *
     * <p>使用示例：</p>
     * <pre>
     * // 在数据填报服务中使用
     * public void createFormTable(String datasourceType, String tableName, List&lt;ExtTableField&gt; fields) {
     *     // 获取对应数据库的DDL提供者
     *     ExtDDLProvider provider = ExtDDLProviderFactory.getExtDDLProvider(datasourceType);
     *
     *     // 生成建表SQL
     *     String createTableSql = provider.createTableSql(tableName, fields);
     *
     *     // 执行SQL创建表...
     * }
     * </pre>
     *
     * @param type 数据库类型，必须是DatasourceConfiguration.DatasourceType枚举中定义的值
     *             如："mysql"、"oracle"、"sqlserver"、"postgresql"等
     * @return 对应数据库类型的DDL提供者实例
     * @throws DEException 如果找不到对应的插件或插件未正确加载
     */
    public static ExtDDLProvider getExtDDLProvider(String type) {
        DatasourceConfiguration.DatasourceType datasourceType = DatasourceConfiguration.DatasourceType.valueOf(type);
        switch (datasourceType) {
            case mysql, mariadb -> {
                return SpringContextUtil.getApplicationContext().getBean("mysqlExtDDLProvider", ExtDDLProvider.class);
            }
        }
        ExtDDLProvider instance = getInstance(type);
        if (instance == null) {
            DEException.throwException("插件异常，请检查插件");
        }
        return instance;
    }

    /**
     * 从缓存中获取指定类型的插件实例
     * 内部方法，用于从templateMap缓存中查找已加载的插件
     *
     * <p>注意事项：</p>
     * <ul>
     *     <li>该方法不进行插件是否存在的校验，调用方需自行处理null情况</li>
     *     <li>key的格式为"df_数据库类型"，如"df_oracle"、"df_sqlserver"</li>
     * </ul>
     *
     * @param type 数据库类型
     * @return 插件实例，如果未找到返回null
     */
    public static ExtDDLProvider getInstance(String type) {
        //if (!LicenseUtil.licenseValid()) DEException.throwException("插件功能只对企业版本可用！");
        String key = "df_" + type;
        return templateMap.get(key);
    }

    /**
     * 加载并注册数据填报插件
     * 在插件初始化时调用，将插件实例注册到工厂的缓存中
     *
     * <p>执行流程：</p>
     * <ol>
     *     <li>构造缓存key："df_" + 数据库类型</li>
     *     <li>检查该类型的插件是否已注册，如已注册则直接返回（避免重复加载）</li>
     *     <li>将插件实例存入templateMap缓存</li>
     *     <li>调用DataEasePluginFactory.loadTemplate()完成插件的全局注册</li>
     * </ol>
     *
     * <p>该方法通常由DataFillingPlugin.loadPlugin()自动调用，不需要手动调用</p>
     *
     * <p>插件加载示例：</p>
     * <pre>
     * // 在OracleDataFillingPlugin中（系统自动调用）
     * public class OracleDataFillingPlugin extends DataFillingPlugin {
     *     &#64;Override
     *     public void loadPlugin() {
     *         XpackPluginsDfVO config = getConfig(); // 获取插件配置
     *         // 调用工厂方法注册插件，config.getType()返回"oracle"
     *         ExtDDLProviderFactory.loadPlugin(config.getType(), this);
     *     }
     * }
     * </pre>
     *
     * @param type 数据库类型，如"oracle"、"sqlserver"、"postgresql"等
     * @param plugin 数据填报插件实例
     * @throws DEException 如果插件加载失败
     */
    public static void loadPlugin(String type, DataFillingPlugin plugin) {
        //if (!LicenseUtil.licenseValid()) DEException.throwException("插件功能只对企业版本可用！");
        String key = "df_" + type;
        if (templateMap.containsKey(key)) return;
        templateMap.put(key, plugin);
        try {
            String moduleName = plugin.getPluginInfo().getModuleName();
            DataEasePluginFactory.loadTemplate(moduleName, plugin);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), new Throwable(e));
            DEException.throwException(e);
        }
    }

    /**
     * 获取所有已加载的数据填报插件配置列表
     * 返回系统中所有已注册的数据填报插件的配置信息
     *
     * <p>使用场景：</p>
     * <ul>
     *     <li>前端数据填报设计器中，显示可用的数据库类型列表供用户选择</li>
     *     <li>系统管理界面，展示已安装的数据填报插件</li>
     *     <li>插件状态检查和诊断</li>
     * </ul>
     *
     * <p>使用示例：</p>
     * <pre>
     * // 在DfPluginManageApi的实现类中
     * &#64;Override
     * public List&lt;XpackPluginsDfVO&gt; queryPluginDf() {
     *     // 获取所有已加载的插件配置
     *     return ExtDDLProviderFactory.getDfConfigList();
     * }
     *
     * // 前端调用后可能得到：
     * // [
     * //   {type: "oracle", icon: "&lt;svg&gt;...&lt;/svg&gt;", category: "datafilling"},
     * //   {type: "sqlserver", icon: "&lt;svg&gt;...&lt;/svg&gt;", category: "datafilling"},
     * //   {type: "postgresql", icon: "&lt;svg&gt;...&lt;/svg&gt;", category: "datafilling"}
     * // ]
     * </pre>
     *
     * @return 插件配置列表，每个元素包含一个插件的配置信息（类型、图标、分类等）
     */
    public static List<XpackPluginsDfVO> getDfConfigList() {
        //if (!LicenseUtil.licenseValid()) DEException.throwException("插件功能只对企业版本可用！");
        return templateMap.values().stream().map(DataFillingPlugin::getConfig).toList();
    }

}
