package io.dataease.dds;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


/**
 * 动态数据源实现类
 * 继承Spring的AbstractRoutingDataSource，实现多数据源的动态路由功能
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>根据当前线程上下文中的数据源键值动态选择目标数据源</li>
 *   <li>支持多租户环境下的数据源自动切换</li>
 *   <li>与DynamicContextHolder配合实现线程安全的数据源管理</li>
 * </ul>
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 确定当前数据源查找键值
     * 该方法被AbstractRoutingDataSource在获取数据库连接时调用
     * 返回的键值将用于从targetDataSources映射中查找对应的数据源
     *
     * @return 当前线程上下文中的数据源键值，用于标识具体的目标数据源
     */
    @Override
    protected Object determineCurrentLookupKey() {
        // 从线程本地变量中获取当前线程的数据源键值
        return DynamicContextHolder.peek();
    }
}
