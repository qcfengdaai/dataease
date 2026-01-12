package io.dataease.dds.config;

import com.zaxxer.hikari.HikariDataSource;
import io.dataease.dds.DynamicDataSource;
import io.dataease.dds.constant.DataSourceConstant;
import io.dataease.dds.interceptor.DSInterceptor;
import io.dataease.dds.provider.TenantDatasourceProvider;
import io.dataease.flyway.TenantFlywayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


/**
 * 动态数据源配置类
 * 负责配置和管理多租户环境下的动态数据源切换
 * 支持懒加载模式和多数据源的初始化管理
 */
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
public class DynamicDsConfig implements WebMvcConfigurer {

    /**
     * 租户数据源懒加载开关
     * true: 延迟加载租户数据源
     * false: 启动时预加载所有租户数据源
     */
    @Value("${de.tenant.ds.lazy:false}")
    private Boolean tenantDsLazyLoad;

    /**
     * 应用程序名称
     * 用于Flyway数据库迁移时的应用标识
     */
    @Value("${spring.application.name}")
    private String appName;

    /**
     * 动态数据源配置属性
     * 包含从配置文件中读取的多个数据源配置
     */
    @Autowired
    private DynamicDataSourceProperties properties;

    /**
     * 创建数据源配置属性Bean
     * 从配置文件中读取spring.datasource前缀的数据源配置
     *
     * @return 数据源配置属性对象
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 创建动态数据源Bean
     * 初始化并配置多租户环境下的动态数据源管理器
     *
     * @param dataSourceProperties 主数据源配置属性
     * @return 配置完成的动态数据源实例
     */
    @Bean
    public DynamicDataSource dynamicDataSource(DataSourceProperties dataSourceProperties) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        // 获取从配置文件加载的额外数据源映射
        Map<Object, Object> dynamicDataSourceMap = getDynamicDataSource();

        // 创建默认的管理数据源
        HikariDataSource defaultDataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        dynamicDataSourceMap.put(DataSourceConstant.DATA_SOURCE_MANAGE, defaultDataSource);

        // 根据懒加载配置决定是否预加载租户数据源
        if (!tenantDsLazyLoad) {
            // 非懒加载模式：从数据库获取所有租户的数据源信息并预加载
            Map<String, DataSource> dbInfo = TenantDatasourceProvider.getDbInfo(defaultDataSource);
            if (!CollectionUtils.isEmpty(dbInfo)) {
                dynamicDataSourceMap.putAll(dbInfo);
            }
        }

        // 设置目标数据源映射
        dynamicDataSource.setTargetDataSources(dynamicDataSourceMap);

        // 对每个数据源执行Flyway数据库迁移
        for (Map.Entry<Object, Object> entry : dynamicDataSourceMap.entrySet()) {
            Object key = entry.getKey();
            boolean isManage = key.equals(DataSourceConstant.DATA_SOURCE_MANAGE);
            try {
                TenantFlywayUtil.executeFlyway((HikariDataSource) entry.getValue(), isManage, appName);
            } catch (Exception e) {
                // 忽略Flyway执行异常，记录日志后继续处理其他数据源
            }
        }

        // 设置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);
        return dynamicDataSource;
    }

    /**
     * 从配置文件加载额外的数据源
     * 主要用于加载DataEase官方库和业务库等静态配置的数据源
     * 客户的租户业务库则通过数据库动态读取和加载
     *
     * @return 包含所有配置文件中定义的数据源的映射表
     */
    private Map<Object, Object> getDynamicDataSource() {
        // 获取配置文件中定义的数据源属性映射
        Map<String, DataSourceProperties> dataSourcePropertiesMap = properties.getDatasource();
        Map<Object, Object> targetDataSources = new HashMap<>(dataSourcePropertiesMap.size());

        // 遍历配置属性，为每个数据源创建HikariDataSource实例
        dataSourcePropertiesMap.forEach((k, v) -> {
            HikariDataSource hikariDataSource = v.initializeDataSourceBuilder().type(HikariDataSource.class).build();
            targetDataSources.put(k, hikariDataSource);
        });
        return targetDataSources;
    }

    /**
     * 注册数据源切换拦截器
     * 添加DSInterceptor到拦截器注册表中，用于自动切换数据源
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DSInterceptor());
    }
}
