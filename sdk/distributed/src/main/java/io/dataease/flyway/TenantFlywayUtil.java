package io.dataease.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.flywaydb.core.api.configuration.Configuration;

import javax.sql.DataSource;
import java.nio.charset.Charset;

/**
 * 租户Flyway数据库迁移工具类
 * 用于在多租户环境下管理数据库版本迁移和结构更新
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>支持管理数据库和租户数据库的分别迁移</li>
 *   <li>为不同租户创建独立的版本管理表</li>
 *   <li>支持乱序执行和基线迁移</li>
 *   <li>自动验证迁移脚本的正确性</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>租户数据库初始化和结构同步</li>
 *   <li>管理数据库的版本更新</li>
 *   <li>分布式环境下的数据库版本管理</li>
 * </ul>
 */
public class TenantFlywayUtil {

    /**
     * Flyway管理数据库的版本表名
     * 用于记录管理数据库的迁移历史和版本信息
     */
    private static final String FLYWAY_MANAGE_TABLE_NAME = "de_manage_version";

    /**
     * 租户Flyway版本表名格式模板
     * 使用应用名称作为参数生成独特的表名，避免命名冲突
     */
    private static final String FLYWAY_TABLENAME_FORMAT = "de_tenant_%s_version";

    /**
     * Flyway迁移脚本的默认位置
     * 存放租户数据库的迁移脚本文件
     */
    private static final String FLYWAY_LOCATION = "classpath:db/migration";

    /**
     * Flyway管理数据库迁移脚本位置
     * 存放管理数据库的专用迁移脚本文件
     */
    private static final String FLYWAY_MANAGE_LOCATION = "classpath:db/distributed/manage";


    /**
     * 构建Flyway配置对象
     * 根据是否为管理数据库和应用名称创建适当的Flyway配置
     *
     * @param dataSource 目标数据源，可以是管理数据库或租户数据库
     * @param isManager  是否为管理数据库，用于选择不同的表名和迁移目录
     * @param appName    应用名称，用于生成租户专用的版本表名
     * @return 配置完成的Flyway配置对象
     */
    private static Configuration buildConfiguration(DataSource dataSource, boolean isManager, String appName) {
        ClassicConfiguration configuration = new ClassicConfiguration();

        // 启用迁移验证，确保迁移脚本的正确性
        configuration.setValidateOnMigrate(true);
        // 启用自动基线，允许在现有数据库上运行Flyway
        configuration.setBaselineOnMigrate(true);
        // 根据是否为管理数据库选择不同的版本表名
        configuration.setTable(isManager ? FLYWAY_MANAGE_TABLE_NAME : getVersionTableName(appName));
        // 设置编码为UTF-8，支持中文字符
        configuration.setEncoding(Charset.forName("utf-8"));
        // 允许乱序执行迁移脚本，提高灵活性
        configuration.setOutOfOrder(true);
        // 设置基线版本为1
        configuration.setBaselineVersionAsString("1");
        // 根据数据库类型选择不同的迁移脚本目录
        configuration.setLocationsAsStrings(isManager ? FLYWAY_MANAGE_LOCATION : FLYWAY_LOCATION);
        // 设置目标数据源
        configuration.setDataSource(dataSource);

        return configuration;
    }

    /**
     * 执行Flyway数据库迁移
     * 根据指定的参数创建Flyway实例并执行迁移操作
     *
     * @param dataSource 目标数据源，可以是管理数据库或租户数据库
     * @param isManage   是否为管理数据库，影响迁移脚本的选择和版本表名
     * @param appName    应用名称，用于生成租户专用的版本表名
     * @throws Exception 如果迁移过程中发生异常则抛出
     */
    public static void executeFlyway(DataSource dataSource, boolean isManage, String appName) throws Exception {
        // 构建适当的Flyway配置
        Configuration configuration = buildConfiguration(dataSource, isManage, appName);
        // 创建Flyway实例
        Flyway flyway = new Flyway(configuration);
        // 执行数据库迁移
        flyway.migrate();
    }

    /**
     * 生成租户专用的Flyway版本表名
     * 使用应用名称作为参数，生成独特的表名以避免不同租户间的冲突
     *
     * @param appName 应用名称，用于标识不同的租户或应用实例
     * @return 格式化后的版本表名，格式为 "de_tenant_{appName}_version"
     */
    private static String getVersionTableName(String appName) {
        return String.format(FLYWAY_TABLENAME_FORMAT, appName);
    }

}
