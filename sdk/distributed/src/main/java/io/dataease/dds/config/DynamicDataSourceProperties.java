package io.dataease.dds.config;


import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 动态数据源配置属性类
 * 用于从配置文件中读取多个数据源的配置信息
 * 配置前缀为"dynamic"
 */
@ConfigurationProperties(prefix = "dynamic")
public class DynamicDataSourceProperties {

    /**
     * 动态数据源配置映射
     * key: 数据源名称
     * value: 对应的数据源配置属性
     */
    private Map<String, DataSourceProperties> datasource = new LinkedHashMap<>();

    public Map<String, DataSourceProperties> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, DataSourceProperties> datasource) {
        this.datasource = datasource;
    }
}
