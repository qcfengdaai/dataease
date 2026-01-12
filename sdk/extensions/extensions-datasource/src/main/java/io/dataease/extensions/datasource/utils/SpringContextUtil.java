package io.dataease.extensions.datasource.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataEase Spring上下文工具类
 * <p>
 * 提供静态访问Spring容器和Bean的工具方法。
 * 实现ApplicationContextAware接口，自动注入Spring上下文。
 * </p>
 *
 * <p><b>主要功能：</b></p>
 * <ul>
 *   <li>静态获取Spring容器中的Bean实例</li>
 *   <li>提供Bean工厂访问，支持动态注册Bean</li>
 *   <li>获取所有Bean的元数据信息</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    /**
     * 获取Bean工厂实例
     * <p>
     * 获取Spring的DefaultListableBeanFactory实例，用于动态注册Bean。
     * 注意：必须使用相同的类加载器，否则会出现类未找到的异常。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>动态注册新的Bean定义</li>
     *   <li>插件式架构中的组件动态加载</li>
     *   <li>Bean的运行时管理和操作</li>
     * </ul>
     *
     * @return DefaultListableBeanFactory Bean工厂实例
     * @throws IllegalStateException 如果Spring上下文未初始化
     */
    public static DefaultListableBeanFactory getBeanFactory(){
        return (DefaultListableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
    }



    /**
     * 获取所有Bean的元数据信息
     * <p>
     * 遍历Spring容器中所有已注册的Bean，返回包含Bean名称、
     * 类型和包信息的列表。主要用于调试和系统监控。
     * </p>
     *
     * <p><b>返回的Map包含以下键：</b></p>
     * <ul>
     *   <li><b>BeanName</b> - Bean的名称</li>
     *   <li><b>beanType</b> - Bean的类型</li>
     *   <li><b>package</b> - Bean所在的包</li>
     * </ul>
     *
     * @return 包含所有Bean信息的列表
     */
    public static List<Map<String, Object>> getAllBean() {
        List<Map<String, Object>> list = new ArrayList<>();

        // 获取所有Bean定义名称
        String[] beans = getApplicationContext().getBeanDefinitionNames();

        for (String beanName : beans) {
            Class<?> beanType = getApplicationContext().getType(beanName);

            Map<String, Object> map = new HashMap<>();
            map.put("BeanName", beanName);
            map.put("beanType", beanType);
            map.put("package", beanType.getPackage());
            list.add(map);
        }

        return list;
    }




    /**
     * Spring应用上下文实例
     * <p>
     * 静态存储Spring的ApplicationContext实例，
     * 由Spring容器通过setApplicationContext方法自动注入。
     * </p>
     */
    private static ApplicationContext applicationContext;


    /**
     * 设置Spring应用上下文
     * <p>
     * 实现ApplicationContextAware接口的方法，
     * 由Spring容器在初始化时自动调用。
     * </p>
     *
     * @param applicationContext Spring应用上下文实例
     * @throws BeansException 如果设置过程中发生错误
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 获取Spring应用上下文
     * <p>
     * 返回Spring的ApplicationContext实例，
     * 可用于直接访问Spring容器的各种功能。
     * </p>
     *
     * @return ApplicationContext Spring应用上下文实例
     * @throws IllegalStateException 如果Spring上下文尚未初始化
     */
    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("Spring ApplicationContext 尚未初始化");
        }
        return applicationContext;
    }


    /**
     * 根据Bean名称获取Bean实例
     * <p>
     * 通过指定的Bean名称从 Spring容器中获取Bean实例。
     * 返回的是Object类型，需要手动转换类型。
     * </p>
     *
     * @param name Bean的名称
     * @return Bean实例
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException 如果Bean不存在
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 根据Bean类型获取Bean实例
     * <p>
     * 通过指定的类型从 Spring容器中获取Bean实例。
     * 使用泛型返回，无需手动转换类型。
     * </p>
     *
     * @param <T> Bean的类型
     * @param clazz Bean的Class对象
     * @return 指定类型的Bean实例
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException 如果Bean不存在
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 根据Bean名称和类型获取Bean实例
     * <p>
     * 通过指定的Bean名称和类型从 Spring容器中获取Bean实例。
     * 提供最精确的Bean定位，适用于同一类型多个Bean的场景。
     * </p>
     *
     * @param <T> Bean的类型
     * @param name Bean的名称
     * @param clazz Bean的Class对象
     * @return 指定名称和类型的Bean实例
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException 如果Bean不存在
     * @throws org.springframework.beans.factory.BeanNotOfRequiredTypeException 如果Bean类型不匹配
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
