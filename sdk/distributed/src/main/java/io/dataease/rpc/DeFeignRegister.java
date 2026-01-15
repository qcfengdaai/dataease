package io.dataease.rpc;

import feign.Request;

import io.dataease.feign.DeFeign;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cloud.openfeign.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * DataEase Feign客户端注册器
 * 负责扫描和注册使用@DeFeign注解的Feign客户端接口
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>扫描指定包路径下的@DeFeign注解接口</li>
 *   <li>为每个Feign客户端创建对应的Bean定义并注册到Spring容器</li>
 *   <li>支持延迟初始化和即时初始化两种注册模式</li>
 *   <li>处理Feign客户端的URL、路径、熔断器等配置</li>
 *   <li>支持刷新范围的Bean定义注册</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>分布式环境下的服务间RPC调用</li>
 *   <li>微服务架构中的服务通信</li>
 *   <li>支持动态刷新的Feign客户端配置</li>
 * </ul>
 */
public class DeFeignRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    /**
     * Spring资源加载器
     * 用于类路径扫描和资源访问
     */
    private ResourceLoader resourceLoader;

    /**
     * Spring环境配置
     * 用于读取配置属性和解析占位符
     */
    private Environment environment;

    /**
     * 默认构造函数
     */
    public DeFeignRegister() {
    }

    /**
     * 验证熔断回退类
     * 确保回退类是具体实现类而不是接口
     *
     * @param clazz 回退类
     * @throws IllegalArgumentException 如果回退类是接口则抛出异常
     */
    static void validateFallback(final Class clazz) {
        Assert.isTrue(!clazz.isInterface(), "Fallback class must implement the interface annotated by @DeFeign");
    }

    /**
     * 验证熔断回退工厂类
     * 确保回退工厂类能够产生实现@DeFeign注解接口的实例
     *
     * @param clazz 回退工厂类
     * @throws IllegalArgumentException 如果回退工厂类是接口则抛出异常
     */
    static void validateFallbackFactory(final Class clazz) {
        Assert.isTrue(!clazz.isInterface(), "Fallback factory must produce instances "
                + "of fallback classes that implement the interface annotated by @DeFeign");
    }

    /**
     * 解析和验证服务名称
     * 从给定的名称中提取有效的主机名或服务ID
     *
     * @param name 原始服务名称或URL
     * @return 处理后的服务名称
     * @throws IllegalStateException 如果名称不是合法的主机名则抛出异常
     */
    static String getName(String name) {
        if (!StringUtils.hasText(name)) {
            return "";
        }

        String host = null;
        try {
            String url;
            // 如果不是完整URL，添加http://前缀进行解析
            if (!name.startsWith("http://") && !name.startsWith("https://")) {
                url = "http://" + name;
            } else {
                url = name;
            }
            // 提取主机名部分
            host = new URI(url).getHost();

        } catch (URISyntaxException ignored) {
            // 忽略URI语法异常
        }
        Assert.state(host != null, "Service id not legal hostname (" + name + ")");
        return name;
    }

    /**
     * 解析和验证URL地址
     * 确保URL格式正确且可访问，自动补全协议前缀
     *
     * @param url 原始URL地址或占位符表达式
     * @return 处理后的URL地址，如果是占位符表达式则原样返回
     * @throws IllegalArgumentException 如果URL格式不正确则抛出异常
     */
    static String getUrl(String url) {
        // 如果是占位符表达式（如 #{...}），则直接返回不做处理
        if (StringUtils.hasText(url) && !(url.startsWith("#{") && url.contains("}"))) {
            // 如果没有协议前缀，自动添加http://
            if (!url.contains("://")) {
                url = "http://" + url;
            }
            try {
                // 验证URL格式是否正确
                new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(url + " is malformed", e);
            }
        }
        return url;
    }

    /**
     * 标准化API路径格式
     * 确保路径以/开头但不以/结尾，符合RESTful规范
     *
     * @param path 原始API路径
     * @return 标准化后的路径，以/开头但不以/结尾；如果路径为空则返回null
     */
    static String getPath(String path) {
        if (StringUtils.hasText(path)) {
            // 去除首尾空格
            path = path.trim();
            // 确保路径以/开头
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            // 确保路径不以/结尾（除了根路径）
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
        }
        return path;
    }

    /**
     * 设置Spring资源加载器
     * 由Spring框架在Bean初始化时自动调用
     *
     * @param resourceLoader Spring资源加载器实例
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 注册Bean定义
     * ImportBeanDefinitionRegistrar接口的核心方法，负责扫描并注册@DeFeign注解的Bean定义
     *
     * @param metadata 导入类的注解元数据
     * @param registry Spring Bean定义注册表
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerDeFeigns(metadata, registry);
    }


    /**
     * 注册所有@DeFeign注解的客户端
     * 扫描指定包路径下的所有@DeFeign接口，并为每个接口创建对应的Bean定义
     *
     * @param metadata 导入类的注解元数据，包含扫描配置信息
     * @param registry Spring Bean定义注册表
     */
    public void registerDeFeigns(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>();
        // 获取@EnableFeignClients注解的属性配置
        Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableFeignClients.class.getName());
        final Class<?>[] clients = attrs == null ? null : (Class<?>[]) attrs.get("clients");

        if (clients == null || clients.length == 0) {
            // 如果没有指定具体的客户端类，则进行包扫描
            ClassPathScanningCandidateComponentProvider scanner = getScanner();
            scanner.setResourceLoader(this.resourceLoader);
            // 添加@DeFeign注解过滤器
            scanner.addIncludeFilter(new AnnotationTypeFilter(DeFeign.class));
            // 获取要扫描的基础包路径
            Set<String> basePackages = getBasePackages(metadata);
            for (String basePackage : basePackages) {
                candidateComponents.addAll(scanner.findCandidateComponents(basePackage));
            }
        } else {
            // 如果指定了具体的客户端类，则直接添加到候选组件中
            for (Class<?> clazz : clients) {
                candidateComponents.add(new AnnotatedGenericBeanDefinition(clazz));
            }
        }

        // 遍历所有候选组件，注册为Bean定义
        for (BeanDefinition candidateComponent : candidateComponents) {
            if (candidateComponent instanceof AnnotatedBeanDefinition beanDefinition) {
                // 验证注解类必须是接口
                AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                Assert.isTrue(annotationMetadata.isInterface(), "@DeFeign can only be specified on an interface");

                // 获取@DeFeign注解的属性配置
                Map<String, Object> attributes = annotationMetadata
                        .getAnnotationAttributes(DeFeign.class.getCanonicalName());

                // 注册单个Feign客户端Bean定义
                registerDeFeign(registry, annotationMetadata, attributes);
            }
        }
    }

    /**
     * 注册单个Feign客户端Bean定义
     * 根据配置选择即时注册或延迟注册模式
     *
     * @param registry Spring Bean定义注册表
     * @param annotationMetadata Feign客户端接口的注解元数据
     * @param attributes @DeFeign注解的属性配置
     */
    private void registerDeFeign(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata,
                                 Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        // 根据配置决定是即时注册还是延迟注册
        if (String.valueOf(false).equals(
                environment.getProperty("spring.cloud.openfeign.lazy-attributes-resolution", String.valueOf(false)))) {
            // 即时注册模式：在启动时就解析所有属性并创建Bean定义
            eagerlyRegisterDeFeignBeanDefinition(className, attributes, registry);
        } else {
            // 延迟注册模式：在Bean实际使用时才解析属性
            lazilyRegisterDeFeignBeanDefinition(className, attributes, registry);
        }
    }

    /**
     * 即时注册Feign客户端Bean定义
     * 在Spring容器启动时立即解析所有配置属性并创建Bean定义
     * 这种方式启动较慢但运行时性能更好
     *
     * @param className Feign客户端接口的完全限定类名
     * @param attributes @DeFeign注解的属性配置
     * @param registry Spring Bean定义注册表
     */
    private void eagerlyRegisterDeFeignBeanDefinition(String className, Map<String, Object> attributes,
                                                      BeanDefinitionRegistry registry) {
        // 验证注解属性的有效性
        validate(attributes);

        // 创建FeignClientFactoryBean的Bean定义构建器
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(FeignClientFactoryBean.class);

        // 设置Feign客户端的基本属性
        definition.addPropertyValue("url", getUrl(null, attributes));
        definition.addPropertyValue("path", getPath(null, attributes));
        String name = getName(attributes);
        definition.addPropertyValue("name", name);
        String contextId = getContextId(null, attributes);
        definition.addPropertyValue("contextId", contextId);
        definition.addPropertyValue("type", className);
        definition.addPropertyValue("dismiss404", Boolean.parseBoolean(String.valueOf(attributes.get("dismiss404"))));

        // 设置熔断回退类
        Object fallback = attributes.get("fallback");
        if (fallback != null) {
            definition.addPropertyValue("fallback",
                    (fallback instanceof Class ? fallback : ClassUtils.resolveClassName(fallback.toString(), null)));
        }

        // 设置熔断回退工厂类
        Object fallbackFactory = attributes.get("fallbackFactory");
        if (fallbackFactory != null) {
            definition.addPropertyValue("fallbackFactory", fallbackFactory instanceof Class ? fallbackFactory
                    : ClassUtils.resolveClassName(fallbackFactory.toString(), null));
        }
        definition.addPropertyValue("fallbackFactory", attributes.get("fallbackFactory"));

        // 设置自动装配模式为按类型装配
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        // 设置是否支持客户端刷新
        definition.addPropertyValue("refreshableClient", isClientRefreshEnabled());

        // 处理Bean限定符
        String[] qualifiers = getQualifiers(attributes);
        if (ObjectUtils.isEmpty(qualifiers)) {
            qualifiers = new String[]{contextId + "DeFeign"};
        }
        // 为AOT代码生成提供限定符信息
        definition.addPropertyValue("qualifiers", qualifiers);

        // 创建Bean定义并设置属性
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);
        // 设置是否为主要Bean
        boolean primary = (Boolean) attributes.get("primary");
        beanDefinition.setPrimary(primary);

        // 创建Bean定义持有者并注册到容器
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, qualifiers);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);

        // 注册支持刷新的相关Bean定义
        registerRefreshableBeanDefinition(registry, contextId, Request.Options.class, OptionsFactoryBean.class);
        registerRefreshableBeanDefinition(registry, contextId, RefreshableUrl.class, RefreshableUrlFactoryBean.class);
    }

    /**
     * 延迟注册Feign客户端Bean定义
     * 延迟模式下Bean的属性将在实际使用时才进行解析，提高启动性能
     * 但会在运行时带来额外的解析开销
     *
     * @param className Feign客户端接口的完全限定类名
     * @param attributes @DeFeign注解的属性配置
     * @param registry Spring Bean定义注册表
     */
    private void lazilyRegisterDeFeignBeanDefinition(String className, Map<String, Object> attributes,
                                                     BeanDefinitionRegistry registry) {
        // 尝试获取可配置的Bean工厂
        ConfigurableBeanFactory beanFactory = registry instanceof ConfigurableBeanFactory
                ? (ConfigurableBeanFactory) registry : null;

        // 解析客户端接口类
        Class clazz = ClassUtils.resolveClassName(className, null);
        String contextId = getContextId(beanFactory, attributes);
        String name = getName(attributes);

        // 创建FeignClientFactoryBean实例
        FeignClientFactoryBean factoryBean = new FeignClientFactoryBean();
        factoryBean.setBeanFactory(beanFactory);
        factoryBean.setName(name);
        factoryBean.setContextId(contextId);
        factoryBean.setType(clazz);
        factoryBean.setRefreshableClient(isClientRefreshEnabled());

        // 创建延迟初始化的Bean定义，使用供应商模式延迟属性解析
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(clazz, () -> {
            // 在Bean实际创建时才解析这些属性
            factoryBean.setUrl(getUrl(beanFactory, attributes));
            factoryBean.setPath(getPath(beanFactory, attributes));
            factoryBean.setDismiss404(Boolean.parseBoolean(String.valueOf(attributes.get("dismiss404"))));

            // 处理熔断回退类
            Object fallback = attributes.get("fallback");
            if (fallback != null) {
                factoryBean.setFallback(fallback instanceof Class ? (Class<?>) fallback
                        : ClassUtils.resolveClassName(fallback.toString(), null));
            }

            // 处理熔断回退工厂类
            Object fallbackFactory = attributes.get("fallbackFactory");
            if (fallbackFactory != null) {
                factoryBean.setFallbackFactory(fallbackFactory instanceof Class ? (Class<?>) fallbackFactory
                        : ClassUtils.resolveClassName(fallbackFactory.toString(), null));
            }

            return factoryBean.getObject();
        });

        // 设置自动装配模式和延迟初始化
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        definition.setLazyInit(true);
        validate(attributes);

        // 配置Bean定义属性
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);
        beanDefinition.setAttribute("DeFeignsRegistrarFactoryBean", factoryBean);

        // 设置是否为主要Bean
        boolean primary = (Boolean) attributes.get("primary");
        beanDefinition.setPrimary(primary);

        // 处理Bean限定符
        String[] qualifiers = getQualifiers(attributes);
        if (ObjectUtils.isEmpty(qualifiers)) {
            qualifiers = new String[]{contextId + "DeFeign"};
        }

        // 注册Bean定义到容器
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, qualifiers);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);

        // 注册支持刷新的相关Bean定义
        registerRefreshableBeanDefinition(registry, contextId, Request.Options.class, OptionsFactoryBean.class);
        registerRefreshableBeanDefinition(registry, contextId, RefreshableUrl.class, RefreshableUrlFactoryBean.class);
    }

    /**
     * 验证@DeFeign注解属性的有效性
     * 检查熔断回退类和回退工厂类的配置是否正确
     *
     * @param attributes @DeFeign注解的属性映射
     * @throws IllegalArgumentException 如果属性配置不正确则抛出异常
     */
    private void validate(Map<String, Object> attributes) {
        AnnotationAttributes annotation = AnnotationAttributes.fromMap(attributes);
        // 验证熔断回退类配置
        validateFallback(annotation.getClass("fallback"));
        // 验证熔断回退工厂类配置
        validateFallbackFactory(annotation.getClass("fallbackFactory"));
    }

    /**
     * 获取服务名称（测试专用方法）
     * 从@DeFeign注解属性中提取服务名称，用于单元测试
     *
     * @param attributes @DeFeign注解的属性映射
     * @return 解析后的服务名称
     */
    /* for testing */ String getName(Map<String, Object> attributes) {
        return getName(null, attributes);
    }

    /**
     * 从注解属性中获取服务名称
     * 按优先级从serviceId -> name -> value依次获取服务名称
     *
     * @param beanFactory Spring Bean工厂，用于解析占位符
     * @param attributes @DeFeign注解的属性映射
     * @return 解析后的服务名称
     */
    String getName(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
        // 优先从serviceId属性获取
        String name = (String) attributes.get("serviceId");
        if (!StringUtils.hasText(name)) {
            // 其次从name属性获取
            name = (String) attributes.get("name");
        }
        if (!StringUtils.hasText(name)) {
            // 最后从value属性获取
            name = (String) attributes.get("value");
        }
        // 解析占位符和表达式
        name = resolve(beanFactory, name);
        return getName(name);
    }

    /**
     * 获取Feign客户端的上下文ID
     * 上下文ID用于在多个Feign客户端之间进行隔离
     *
     * @param beanFactory Spring Bean工厂，用于解析占位符
     * @param attributes @DeFeign注解的属性映射
     * @return 解析后的上下文ID，如果未指定则使用服务名称
     */
    private String getContextId(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
        String contextId = (String) attributes.get("contextId");
        if (!StringUtils.hasText(contextId)) {
            // 如果没有指定contextId，使用服务名称作为默认值
            return getName(attributes);
        }

        // 解析占位符和表达式
        contextId = resolve(beanFactory, contextId);
        return getName(contextId);
    }

    /**
     * 解析配置值中的占位符和表达式
     * 支持${...}占位符和#{...}SpEL表达式的解析
     *
     * @param beanFactory Spring Bean工厂，提供解析上下文
     * @param value 待解析的配置值
     * @return 解析后的字符串值
     */
    private String resolve(ConfigurableBeanFactory beanFactory, String value) {
        if (StringUtils.hasText(value)) {
            if (beanFactory == null) {
                // 如果Bean工厂不可用，使用环境变量解析占位符
                return this.environment.resolvePlaceholders(value);
            }

            // 获取Bean表达式解析器
            BeanExpressionResolver resolver = beanFactory.getBeanExpressionResolver();
            // 解析嵌入的占位符值
            String resolved = beanFactory.resolveEmbeddedValue(value);

            if (resolver == null) {
                return resolved;
            }

            // 使用表达式解析器解析SpEL表达式
            Object evaluateValue = resolver.evaluate(resolved, new BeanExpressionContext(beanFactory, null));
            if (evaluateValue != null) {
                return String.valueOf(evaluateValue);
            }
            return null;
        }
        return value;
    }

    /**
     * 获取Feign客户端的URL地址
     * 从注解属性中解析URL并进行标准化处理
     *
     * @param beanFactory Spring Bean工厂，用于解析占位符
     * @param attributes @DeFeign注解的属性映射
     * @return 标准化后的URL地址
     */
    private String getUrl(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
        String url = resolve(beanFactory, (String) attributes.get("url"));
        return getUrl(url);
    }

    /**
     * 获取Feign客户端的API路径
     * 从注解属性中解析路径并进行标准化处理
     *
     * @param beanFactory Spring Bean工厂，用于解析占位符
     * @param attributes @DeFeign注解的属性映射
     * @return 标准化后的API路径
     */
    private String getPath(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
        String path = resolve(beanFactory, (String) attributes.get("path"));
        return getPath(path);
    }

    /**
     * 创建类路径扫描组件提供者
     * 用于扫描指定包路径下的@DeFeign注解接口
     *
     * @return 配置好的类路径扫描器，只扫描独立的非注解类
     */
    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                // 只处理独立的非注解类（即接口或类）
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    /**
     * 获取要扫描的基础包路径集合
     * 从@EnableFeignClients注解中解析包扫描路径配置
     *
     * @param importingClassMetadata 导入类的注解元数据
     * @return 所有要扫描的包路径集合
     */
    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableFeignClients.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();

        // 从@EnableFeignClients的value属性获取包路径
        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        // 从@EnableFeignClients的basePackages属性获取包路径
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        // 从@EnableFeignClients的basePackageClasses属性获取包路径
        for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        // 如果没有指定任何包路径，使用导入类所在的包作为默认扫描路径
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    /**
     * 获取单个Bean限定符
     * 从客户端配置中提取qualifier属性值
     *
     * @param client 客户端配置属性映射
     * @return Bean限定符字符串，如果未配置则返回null
     */
    private String getQualifier(Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        String qualifier = (String) client.get("qualifier");
        if (StringUtils.hasText(qualifier)) {
            return qualifier;
        }
        return null;
    }

    /**
     * 获取Bean限定符数组
     * 从客户端配置中获取所有有效的限定符
     *
     * @param client 客户端配置属性映射
     * @return Bean限定符数组，如果没有有效限定符则返回null
     */
    private String[] getQualifiers(Map<String, Object> client) {
        if (client == null) {
            return null;
        }

        // 获取qualifiers属性并过滤空值
        List<String> qualifierList = new ArrayList<>(Arrays.asList((String[]) client.get("qualifiers")));
        qualifierList.removeIf(qualifier -> !StringUtils.hasText(qualifier));

        // 如果qualifiers为空但qualifier不为空，使用qualifier作为默认值
        if (qualifierList.isEmpty() && getQualifier(client) != null) {
            qualifierList = Collections.singletonList(getQualifier(client));
        }

        return !qualifierList.isEmpty() ? qualifierList.toArray(new String[0]) : null;
    }


    /**
     * 设置Spring环境配置
     * 由Spring框架在Bean初始化时自动调用
     *
     * @param environment Spring环境配置实例
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * 注册支持刷新的Bean定义
     * 为指定的Bean类型创建支持refresh作用域的代理Bean
     * 这样在配置变更时可以动态刷新Bean实例
     *
     * @param registry Spring Bean定义注册表
     * @param contextId Feign客户端的上下文ID
     * @param beanType 要注册的Bean类型
     * @param factoryBeanType 相关的工厂Bean类型
     */
    private void registerRefreshableBeanDefinition(BeanDefinitionRegistry registry, String contextId, Class<?> beanType,
                                                   Class<?> factoryBeanType) {
        // 只有在启用客户端刷新功能时才注册
        if (isClientRefreshEnabled()) {
            // 生成唯一的Bean名称
            String beanName = beanType.getCanonicalName() + "-" + contextId;
            // 创建Bean定义构建器
            BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(factoryBeanType);
            // 设置为refresh作用域
            definitionBuilder.setScope("refresh");
            // 设置上下文ID属性
            definitionBuilder.addPropertyValue("contextId", contextId);

            // 创建Bean定义持有者
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(definitionBuilder.getBeanDefinition(),
                    beanName);
            // 创建作用域代理，支持动态刷新
            definitionHolder = ScopedProxyUtils.createScopedProxy(definitionHolder, registry, true);
            // 注册Bean定义
            BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
        }
    }

    /**
     * 检查是否启用客户端刷新功能
     * 从环境配置中读取spring.cloud.openfeign.client.refresh-enabled属性
     *
     * @return true如果启用了客户端刷新功能，否则返回false
     */
    private boolean isClientRefreshEnabled() {
        return environment.getProperty("spring.cloud.openfeign.client.refresh-enabled", Boolean.class, false);
    }
}
