package io.dataease.listener;


import io.dataease.utils.ConfigUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * EhCache缓存初始化监听器
 *
 * <p>功能描述：</p>
 * <ul>
 *   <li>在Spring应用上下文初始化阶段，设置EhCache相关的系统属性</li>
 *   <li>设置登录超时时间</li>
 *   <li>设置EhCache缓存文件的存储路径</li>
 * </ul>
 *
 * <p>执行时机：</p>
 * <p>实现ApplicationContextInitializer接口，在ApplicationContext创建并准备好之后、
 * bean定义加载之前执行，优先级非常高</p>
 *
 * <p>配置项：</p>
 * <ul>
 *   <li>dataease.login_timeout：登录超时时间（分钟），默认480分钟（8小时）</li>
 *   <li>dataease.path.ehcache：EhCache缓存文件存储路径，默认/opt/dataease2.0/cache</li>
 * </ul>
 *
 * @author DataEase
 * @since 2.0.0
 */
public class EhCacheStartListener implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    /**
     * 初始化应用上下文
     * 设置EhCache相关的系统属性
     *
     * @param applicationContext 可配置的应用上下文
     */
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // 1. 从环境变量中读取登录超时时间，默认480分钟（8小时）
        String property = applicationContext.getEnvironment().getProperty("dataease.login_timeout", String.class, "480");
        // 设置为系统属性，供EhCache配置使用
        System.setProperty("dataease.login_timeout", property);

        // 2. 从配置文件中读取EhCache缓存路径，默认/opt/dataease2.0/cache
        String ehcache = ConfigUtils.getConfig("dataease.path.ehcache", "/opt/dataease2.0/cache");
        // 设置为系统属性，供EhCache配置使用
        System.setProperty("dataease.path.ehcache", ehcache);
    }
}
