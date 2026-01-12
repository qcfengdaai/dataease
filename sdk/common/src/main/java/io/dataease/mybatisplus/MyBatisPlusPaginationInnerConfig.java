package io.dataease.mybatisplus;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 分页配置类
 * 配置分页插件和缓存功能，为DataEase提供统一的数据访问层分页支持
 *
 * 主要功能：
 * 1. 配置MyBatis Plus分页拦截器
 * 2. 启用Spring缓存机制
 * 3. 支持MySQL数据库的分页查询优化
 *
 * 使用场景：
 * - 列表查询的分页显示
 * - 大数据量表格的分页加载
 * - 报表数据的分页查询
 */
@Configuration
@EnableCaching
public class MyBatisPlusPaginationInnerConfig {

    /**
     * 配置MyBatis Plus拦截器
     * 注册分页内部拦截器，用于自动处理分页查询的SQL改写和总数统计
     *
     * 功能特点：
     * - 自动根据Page对象生成LIMIT分页SQL
     * - 自动执行COUNT查询获取总记录数
     * - 支持多种数据库方言的分页语法
     * - 优化分页查询性能，避免全表扫描
     *
     * @return MybatisPlusInterceptor 配置好的MyBatis Plus拦截器实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建MyBatis Plus拦截器实例
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页内部拦截器，指定数据库类型为MySQL
        // 这样可以生成适合MySQL的分页SQL语句(LIMIT offset, size)
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
