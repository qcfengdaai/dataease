package io.dataease.config;

import io.dataease.commons.utils.MybatisInterceptorConfig;
import io.dataease.datasource.dao.auto.entity.CoreDatasource;
import io.dataease.datasource.dao.auto.entity.CoreDeEngine;
import io.dataease.interceptor.MybatisInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;

/**
 * MyBatis配置类
 * 负责MyBatis框架的相关配置，包括拦截器设置和事务管理
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>启用Spring事务管理支持</li>
 *   <li>配置MyBatis拦截器，用于特定实体的字段处理</li>
 *   <li>提供数据库操作的统一拦截和处理机制</li>
 * </ul>
 *
 * <p>拦截器配置：</p>
 * <ul>
 *   <li>针对数据引擎实体(CoreDeEngine)的配置字段拦截</li>
 *   <li>针对数据源实体(CoreDatasource)的配置字段拦截</li>
 *   <li>支持敏感信息的加密解密处理</li>
 * </ul>
 *
 * <p>事务特性：</p>
 * <ul>
 *   <li>支持声明式事务管理</li>
 *   <li>提供事务传播和隔离级别控制</li>
 *   <li>支持事务回滚和异常处理</li>
 * </ul>
 */
@Configuration
@EnableTransactionManagement
public class MybatisConfig {

    /**
     * 创建数据库拦截器Bean
     * 配置MyBatis拦截器，用于对特定实体的敏感字段进行加密解密处理
     *
     * <p>拦截器功能：</p>
     * <ul>
     *   <li>对数据引擎配置信息进行加密存储和解密读取</li>
     *   <li>对数据源连接配置进行安全处理</li>
     *   <li>确保敏感信息在数据库中以加密形式存储</li>
     * </ul>
     *
     * <p>拦截的实体类：</p>
     * <ul>
     *   <li>{@link CoreDeEngine} - 数据引擎实体，拦截configuration字段</li>
     *   <li>{@link CoreDatasource} - 数据源实体，拦截configuration字段</li>
     * </ul>
     *
     * <p>工作原理：</p>
     * <ul>
     *   <li>插入/更新时：自动加密configuration字段中的敏感信息</li>
     *   <li>查询时：自动解密configuration字段并返回明文信息</li>
     *   <li>确保业务层操作透明，无需关心加密解密细节</li>
     * </ul>
     *
     * @return 配置完成的MyBatis拦截器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisInterceptor dbInterceptor() {
        MybatisInterceptor interceptor = new MybatisInterceptor();
        List<MybatisInterceptorConfig> configList = new ArrayList<>();

        // 添加数据引擎实体的configuration字段拦截配置
        configList.add(new MybatisInterceptorConfig(CoreDeEngine.class, "configuration"));
        // 添加数据源实体的configuration字段拦截配置
        configList.add(new MybatisInterceptorConfig(CoreDatasource.class, "configuration"));

        // 设置拦截器配置列表
        interceptor.setInterceptorConfigList(configList);
        return interceptor;
    }
}
