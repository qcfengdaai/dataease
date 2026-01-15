package io.dataease.config;

import com.fit2cloud.autoconfigure.QuartzAutoConfiguration;
import io.dataease.utils.CommonThreadPool;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 公共配置类
 * 负责系统公共组件的配置，包括线程池、任务调度等基础设施
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>配置公共线程池，为系统提供统一的异步任务执行能力</li>
 *   <li>配置在Quartz调度框架之前初始化，确保线程资源优先可用</li>
 *   <li>管理系统公共资源的生命周期</li>
 * </ul>
 *
 * <p>主Spring Boot自动配置的特点：</p>
 * <ul>
 *   <li>使用@AutoConfigureBefore确保在Quartz配置之前执行</li>
 *   <li>为线程池配置自动销毁机制</li>
 *   <li>提供系统级别的异步任务执行能力</li>
 * </ul>
 */
@Configuration
@AutoConfigureBefore(QuartzAutoConfiguration.class)
public class CommonConfig {

    /**
     * 创建公共资源线程池
     * 为系统提供统一的线程池资源，用于执行各种异步任务
     *
     * <p>线程池配置参数：</p>
     * <ul>
     *   <li><b>核心线程数：</b>50 - 线程池中保持的最小线程数</li>
     *   <li><b>队列最大容量：</b>100 - 等待队列可容纳的最大任务数</li>
     *   <li><b>线程空闲时间：</b>3600秒 - 非核心线程的最大空闲时间</li>
     * </ul>
     *
     * <p>适用场景：</p>
     * <ul>
     *   <li>数据导入导出任务</li>
     *   <li>报表生成和发送</li>
     *   <li>文件处理和上传</li>
     *   <li>后台数据处理任务</li>
     * </ul>
     *
     * <p>注意事项：</p>
     * <ul>
     *   <li>配置了destroyMethod以确保应用关闭时正确释放资源</li>
     *   <li>线程池参数可根据实际业务负载进行调优</li>
     * </ul>
     *
     * @return 配置好的公共线程池实例
     */
    @Bean(destroyMethod = "shutdown")
    public CommonThreadPool resourcePoolThreadPool() {
        CommonThreadPool commonThreadPool = new CommonThreadPool();
        // 设置核心线程数为50个
        commonThreadPool.setCorePoolSize(50);
        // 设置任务队列最大容量为100个
        commonThreadPool.setMaxQueueSize(100);
        // 设置线程空闲时间为1小时（3600秒）
        commonThreadPool.setKeepAliveSeconds(3600);
        return commonThreadPool;
    }
}
