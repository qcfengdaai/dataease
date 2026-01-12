package io.dataease.utils;

import io.micrometer.common.util.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring Bean工厂工具类
 * <p>
 * 提供在非Spring管理的类中获取Spring容器中Bean实例的能力。通过实现ApplicationContextAware接口，
 * 在应用启动时自动注入ApplicationContext，之后可以在任何地方通过静态方法获取Bean。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>按Bean名称获取Bean实例</li>
 *   <li>按Class类型获取Bean实例</li>
 *   <li>获取Bean的代理对象（支持AOP增强）</li>
 *   <li>获取ApplicationContext对象</li>
 *   <li>异常安全 - 获取失败返回null而不抛出异常</li>
 * </ul>
 *
 * <p><b>应用场景：</b></p>
 * <ul>
 *   <li>在工具类、静态方法中获取Spring Bean</li>
 *   <li>在非Spring管理的对象中访问Spring容器</li>
 *   <li>动态获取Bean实例，避免循环依赖</li>
 *   <li>获取代理Bean以确保事务、缓存等AOP功能生效</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.rsa.manage.RsaManage#proxy()} - 获取自身代理对象以确保事务生效</li>
 *   <li>{@link io.dataease.auth.filter.CommunityTokenFilter#doFilter} - 动态获取缓存管理器Bean</li>
 *   <li>{@link io.dataease.cache.impl.RedisCacheImpl} - 在缓存实现中获取Redis模板</li>
 *   <li>各种工具类中获取Service层Bean</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：按Class类型获取Bean（最常用）
 * UserService userService = CommonBeanFactory.getBean(UserService.class);
 * if (userService != null) {
 *     User user = userService.getUserById(1L);
 * }
 *
 * // 示例2：按Bean名称获取Bean
 * Object redisTemplate = CommonBeanFactory.getBean("redisTemplate");
 * if (redisTemplate instanceof RedisTemplate) {
 *     RedisTemplate template = (RedisTemplate) redisTemplate;
 * }
 *
 * // 示例3：获取代理对象以确保事务生效（重要！）
 * // 在Service内部调用自身的事务方法时，需要通过代理对象调用
 * public class RsaManage {
 *     public void methodA() {
 *         // 直接调用methodB()事务不生效，需要通过代理调用
 *         proxy().methodB();
 *     }
 *
 *     private RsaManage proxy() {
 *         return CommonBeanFactory.getBean(RsaManage.class);
 *     }
 *
 *     &#64;Transactional
 *     public void methodB() {
 *         // 事务方法
 *     }
 * }
 *
 * // 示例4：动态获取Bean避免循环依赖
 * // 在构造函数或@Autowired时可能出现循环依赖，可以延迟获取
 * public class MyService {
 *     private OtherService otherService;
 *
 *     public void doSomething() {
 *         if (otherService == null) {
 *             otherService = CommonBeanFactory.getBean(OtherService.class);
 *         }
 *         otherService.process();
 *     }
 * }
 *
 * // 示例5：在工具类中使用
 * public class SomeUtils {
 *     public static void processData() {
 *         DataService dataService = CommonBeanFactory.getBean(DataService.class);
 *         if (dataService != null) {
 *             dataService.save(...);
 *         }
 *     }
 * }
 *
 * // 示例6：获取ApplicationContext进行高级操作
 * ApplicationContext context = CommonBeanFactory.getApplicationContext();
 * if (context != null) {
 *     String[] beanNames = context.getBeanNamesForType(UserService.class);
 *     Environment env = context.getEnvironment();
 * }
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>该工具类必须被Spring容器扫描并实例化，否则context为null</li>
 *   <li>在Spring容器完全初始化之前调用可能返回null</li>
 *   <li>获取失败时返回null而不是抛出异常，使用前需判空</li>
 *   <li>不要过度使用，优先使用依赖注入（@Autowired/@Resource）</li>
 *   <li>proxy()方法返回的是Spring代理对象，确保AOP增强生效</li>
 *   <li>在单例Bean中缓存获取的Bean引用是安全的</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>优先使用@Autowired依赖注入，仅在无法注入时使用本工具</li>
 *   <li>在Service内部调用自身事务方法时，使用proxy()获取代理对象</li>
 *   <li>获取Bean后务必判空，避免NullPointerException</li>
 *   <li>对于频繁使用的Bean，可以缓存引用避免重复获取</li>
 *   <li>不要在static初始化块中使用，此时Spring容器可能未初始化</li>
 * </ul>
 *
 * <p><b>实现原理：</b></p>
 * <ul>
 *   <li>通过@Component注解被Spring容器管理</li>
 *   <li>实现ApplicationContextAware接口，Spring自动注入ApplicationContext</li>
 *   <li>将ApplicationContext保存为静态变量，提供静态方法访问</li>
 *   <li>使用try-catch捕获BeansException，确保异常安全</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ApplicationContextAware
 * @see io.dataease.rsa.manage.RsaManage
 * @see io.dataease.auth.filter.CommunityTokenFilter
 */
@Component
public class CommonBeanFactory implements ApplicationContextAware {
    /**
     * Spring应用上下文静态引用
     * <p>
     * 由Spring容器在Bean初始化时通过{@link #setApplicationContext}方法注入
     * </p>
     */
    private static ApplicationContext context;

    /**
     * 默认构造函数
     */
    public CommonBeanFactory() {
    }

    /**
     * 设置ApplicationContext
     * <p>
     * 该方法由Spring容器在Bean初始化时自动调用，将ApplicationContext注入到静态变量中。
     * 实现ApplicationContextAware接口的回调方法。
     * </p>
     *
     * @param ctx Spring应用上下文对象
     * @throws BeansException 如果上下文设置失败
     */
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        context = ctx;
    }

    /**
     * 根据Bean名称获取Bean实例
     * <p>
     * 通过Bean的名称从Spring容器中获取Bean实例。如果Bean不存在或获取失败，返回null而不抛出异常。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 获取名为"userService"的Bean
     * Object bean = CommonBeanFactory.getBean("userService");
     * if (bean instanceof UserService) {
     *     UserService service = (UserService) bean;
     * }
     *
     * // 获取Redis模板
     * Object redisTemplate = CommonBeanFactory.getBean("redisTemplate");
     * </pre>
     *
     * @param beanName Bean的名称（不能为空）
     * @return Bean实例，如果不存在或获取失败则返回null
     */
    public static Object getBean(String beanName) {
        try {
            return context != null && !StringUtils.isBlank(beanName) ? context.getBean(beanName) : null;
        } catch (BeansException e) {
            return null;
        }
    }

    /**
     * 根据Class类型获取Bean实例（推荐使用）
     * <p>
     * 通过Class类型从Spring容器中获取Bean实例，这是最常用和最安全的方式。
     * 如果存在多个相同类型的Bean，会抛出异常，此时应使用{@link #getBean(String)}方法。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 获取UserService类型的Bean
     * UserService userService = CommonBeanFactory.getBean(UserService.class);
     * if (userService != null) {
     *     userService.save(user);
     * }
     *
     * // 获取数据源管理器
     * DataSourceManage dsManage = CommonBeanFactory.getBean(DataSourceManage.class);
     * </pre>
     *
     * @param <T> Bean的类型
     * @param className Bean的Class对象
     * @return 指定类型的Bean实例，如果不存在或获取失败则返回null
     */
    public static <T> T getBean(Class<T> className) {
        try {
            return context != null && className != null ? context.getBean(className) : null;
        } catch (BeansException e) {
            return null;
        }
    }

    /**
     * 获取ApplicationContext对象
     * <p>
     * 返回Spring应用上下文对象，可用于执行更复杂的Bean操作，如获取Bean定义、环境变量等。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * ApplicationContext context = CommonBeanFactory.getApplicationContext();
     * if (context != null) {
     *     // 获取所有UserService类型的Bean名称
     *     String[] beanNames = context.getBeanNamesForType(UserService.class);
     *
     *     // 获取环境变量
     *     Environment env = context.getEnvironment();
     *     String dbUrl = env.getProperty("spring.datasource.url");
     *
     *     // 发布事件
     *     context.publishEvent(new CustomEvent(this));
     * }
     * </pre>
     *
     * @return Spring应用上下文对象，如果未初始化则返回null
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * 获取Bean的代理对象（用于确保AOP增强生效）
     * <p>
     * 获取Spring管理的Bean代理对象，确保事务、缓存、安全等AOP增强功能生效。
     * 在Service类内部调用自身的事务方法时，必须通过代理对象调用，否则事务不会生效。
     * </p>
     *
     * <p><b>为什么需要代理对象？</b></p>
     * Spring的AOP是基于代理实现的，直接调用this.method()不会经过代理，因此AOP增强不生效。
     * 必须通过Spring容器获取的代理对象调用方法，才能触发事务、缓存等功能。
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 示例1：确保事务生效（最常见场景）
     * &#64;Service
     * public class RsaManage {
     *     // 非事务方法调用事务方法
     *     public void saveData() {
     *         // 错误：直接调用，事务不生效
     *         // this.saveWithTransaction();
     *
     *         // 正确：通过代理对象调用，事务生效
     *         proxy().saveWithTransaction();
     *     }
     *
     *     &#64;Transactional
     *     public void saveWithTransaction() {
     *         // 数据库操作
     *     }
     *
     *     private RsaManage proxy() {
     *         return CommonBeanFactory.proxy(RsaManage.class);
     *     }
     * }
     *
     * // 示例2：确保缓存生效
     * &#64;Service
     * public class UserService {
     *     public User getUserInfo(Long userId) {
     *         // 通过代理调用，缓存注解生效
     *         return proxy().getCachedUser(userId);
     *     }
     *
     *     &#64;Cacheable("users")
     *     public User getCachedUser(Long userId) {
     *         return userMapper.selectById(userId);
     *     }
     *
     *     private UserService proxy() {
     *         return CommonBeanFactory.proxy(UserService.class);
     *     }
     * }
     * </pre>
     *
     * @param <T> Bean的类型
     * @param className Bean的Class对象
     * @return Spring代理对象，如果不存在或获取失败则返回null
     */
    public static <T> T proxy(Class<T> className) {
        try {
            return context != null && className != null ? context.getBean(className) : null;
        } catch (BeansException e) {
            return null;
        }
    }
}
