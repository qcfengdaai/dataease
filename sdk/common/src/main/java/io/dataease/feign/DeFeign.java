package io.dataease.feign;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * DataEase自定义Feign客户端注解
 * <p>
 * 这是一个自定义的Feign客户端注解，用于包装和扩展Spring Cloud OpenFeign的功能。
 * 提供了与标准FeignClient注解相似的功能，同时支持定义与实现的分离，
 * 便于在微服务架构中进行服务间通信。
 * </p>
 *
 * <p><b>主要特性：</b></p>
 * <ul>
 *   <li>支持服务发现和负载均衡</li>
 *   <li>支持自定义配置和回退机制</li>
 *   <li>支持URL直连和服务名调用</li>
 *   <li>支持404错误处理</li>
 *   <li>支持路径前缀配置</li>
 * </ul>
 *
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li>微服务之间的HTTP调用</li>
 *   <li>第三方API接口调用</li>
 *   <li>分布式系统中的服务通信</li>
 *   <li>需要负载均衡的远程调用</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 * @see org.springframework.cloud.openfeign.FeignClient
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface DeFeign {
    /**
     * 服务名称（默认属性）
     * <p>
     * 指定要调用的微服务名称，与name()属性互为别名。
     * 在服务发现环境中，这个值用于从注册中心查找服务实例。
     * 如果同时配置了url()，则url()优先级更高。
     * </p>
     *
     * @return 微服务名称
     * @see #name()
     */
    @AliasFor("name")
    String value() default "";

    /**
     * Spring上下文中的Bean标识ID
     * <p>
     * 当存在多个相同服务名称的Feign客户端时，用于区分不同的Bean实例。
     * 如果指定了contextId，它将作为Bean的名称而不是服务名称，
     * 但不会用作服务ID进行服务发现。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>同一个服务需要不同配置的多个客户端</li>
     *   <li>避免Bean名称冲突</li>
     *   <li>需要精确控制Bean注入的场景</li>
     * </ul>
     *
     * @return Spring容器中的Bean标识ID
     */
    String contextId() default "";

    /**
     * 服务标识名称
     * <p>
     * 指定要调用的服务ID，可选择性地包含协议前缀。
     * 与value()属性是同义词，两者可以互换使用。
     * 在微服务环境中，这个名称用于从服务注册中心查找目标服务。
     * </p>
     *
     * <p><b>示例：</b></p>
     * <ul>
     *   <li>"user-service" - 简单服务名</li>
     *   <li>"http://user-service" - 带协议的服务名</li>
     *   <li>"https://api.external.com" - 外部API服务</li>
     * </ul>
     *
     * @return 带可选协议前缀的服务ID
     * @see #value()
     */
    @AliasFor("value")
    String name() default "";

    /**
     * Bean限定符数组
     * <p>
     * 为Feign客户端指定Spring的@Qualifier注解值，
     * 用于在存在多个相同类型的Bean时进行精确匹配。
     * 当依赖注入时，可以通过这些限定符来指定注入哪个特定的Feign客户端。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>存在多个相同接口的不同实现</li>
     *   <li>需要根据业务场景注入不同的客户端</li>
     *   <li>在测试环境中切换不同的服务实现</li>
     * </ul>
     *
     * @return Feign客户端的限定符值数组
     */
    String[] qualifiers() default {};

    /**
     * 目标服务的绝对URL地址
     * <p>
     * 指定Feign客户端调用的完整URL地址，可以是绝对URL或可解析的主机名。
     * 协议部分是可选的，如果不指定协议，默认使用HTTP。
     * 当配置了url时，将不会使用服务发现机制，而是直接调用指定的URL。
     * </p>
     *
     * <p><b>优先级：</b>url() > name()/value()</p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>调用外部第三方API</li>
     *   <li>调用固定IP地址的服务</li>
     *   <li>开发测试环境的直连调用</li>
     *   <li>不使用服务发现的场景</li>
     * </ul>
     *
     * @return 绝对URL或可解析的主机名
     */
    String url() default "";

    /**
     * 404错误处理策略
     * <p>
     * 控制当目标服务返回404状态码时的处理方式：
     * - true：将404响应解码为返回值，而不是抛出FeignException
     * - false：遇到404时抛出FeignException异常
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>查询可能不存在的资源时返回null而不是异常</li>
     *   <li>需要区分"资源不存在"和"服务异常"的场景</li>
     *   <li>提供更优雅的错误处理体验</li>
     * </ul>
     *
     * @return 是否应该解码404响应而不是抛出FeignException，默认为false
     */
    boolean dismiss404() default false;


    /**
     * 自定义配置类数组
     * <p>
     * 为当前Feign客户端指定自定义的配置类，用于覆盖默认的Feign配置。
     * 这些配置类可以包含编码器、解码器、拦截器、重试策略等自定义配置。
     * 配置类中的Bean定义仅对当前Feign客户端有效，不会影响其他客户端。
     * </p>
     *
     * <p><b>可配置的组件：</b></p>
     * <ul>
     *   <li>Encoder - 请求编码器</li>
     *   <li>Decoder - 响应解码器</li>
     *   <li>RequestInterceptor - 请求拦截器</li>
     *   <li>Retryer - 重试策略</li>
     *   <li>ErrorDecoder - 错误解码器</li>
     *   <li>Contract - 契约解析器</li>
     * </ul>
     *
     * @return 自定义配置类的数组
     */
    Class<?>[] configuration() default {};


    /**
     * 回退实现类
     * <p>
     * 当Feign调用失败时（如网络异常、服务不可用等），将使用此类作为回退实现。
     * 回退类必须实现与Feign接口相同的方法，并且需要是Spring管理的Bean。
     * 这是服务降级的一种实现方式，可以提供默认的业务逻辑。
     * </p>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>回退类必须实现Feign接口</li>
     *   <li>回退类需要被Spring容器管理</li>
     *   <li>不能与fallbackFactory同时使用</li>
     *   <li>回退方法无法访问导致失败的原因</li>
     * </ul>
     *
     * @return 服务降级时使用的回退实现类
     */
    Class<?> fallback() default void.class;


    /**
     * 回退工厂类
     * <p>
     * 提供动态创建回退实例的工厂类，相比于fallback()，
     * 工厂模式可以访问导致调用失败的异常信息，从而提供更精细的降级处理。
     * 工厂类必须实现FallbackFactory接口，并且需要是Spring管理的Bean。
     * </p>
     *
     * <p><b>优势：</b></p>
     * <ul>
     *   <li>可以访问异常信息，提供更精确的降级逻辑</li>
     *   <li>支持根据异常类型返回不同的回退结果</li>
     *   <li>可以记录异常信息用于监控和分析</li>
     *   <li>支持更复杂的降级策略</li>
     * </ul>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>不能与fallback同时使用</li>
     *   <li>必须实现FallbackFactory接口</li>
     *   <li>需要被Spring容器管理</li>
     * </ul>
     *
     * @return 动态创建回退实例的工厂类
     */
    Class<?> fallbackFactory() default void.class;

    /**
     * 统一路径前缀
     * <p>
     * 为所有方法级别的映射添加统一的路径前缀。
     * 当Feign接口中的方法都有相同的路径前缀时，
     * 可以通过此属性统一配置，避免在每个方法上重复定义。
     * </p>
     *
     * <p><b>示例：</b></p>
     * <pre><code>
     * &#64;DeFeign(name = "user-service", path = "/api/v1")
     * public interface UserClient {
     *     &#64;GetMapping("/users/{id}")  // 实际请求路径：/api/v1/users/{id}
     *     User getUser(&#64;PathVariable Long id);
     *
     *     &#64;PostMapping("/users")     // 实际请求路径：/api/v1/users
     *     User createUser(&#64;RequestBody User user);
     * }
     * </code></pre>
     *
     * @return 所有方法级映射使用的路径前缀
     */
    String path() default "";

    /**
     * 是否标记为主要Bean
     * <p>
     * 控制是否将当前Feign代理标记为主要（Primary）Bean。
     * 当存在多个相同类型的Bean时，标记为Primary的Bean将被优先选择进行依赖注入。
     * 默认为true，表示当前Feign客户端是该接口的主要实现。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>存在多个相同接口的不同实现时</li>
     *   <li>需要指定默认的服务调用实现</li>
     *   <li>在测试环境中切换主要实现</li>
     * </ul>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>同一接口只能有一个Primary Bean</li>
     *   <li>与@Qualifier注解配合使用效果更佳</li>
     * </ul>
     *
     * @return 是否标记为主要Bean，默认为true
     */
    boolean primary() default true;
}
