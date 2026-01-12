package io.dataease;

import io.dataease.rpc.DeFeignRegister;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * DataEase Feign客户端自动配置类
 * 负责启用和配置分布式环境下的Feign客户端功能
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>启用Spring Cloud OpenFeign功能</li>
 *   <li>导入自定义的DeFeignRegister注册器</li>
 *   <li>自动扫描和注册@DeFeign注解的接口</li>
 *   <li>为分布式系统提供服务间RPC调用支持</li>
 * </ul>
 *
 * <p>配置特点：</p>
 * <ul>
 *   <li>使用@EnableFeignClients启用Feign客户端功能</li>
 *   <li>通过@Import导入DeFeignRegister实现自定义注册逻辑</li>
 *   <li>支持动态服务发现和负载均衡</li>
 *   <li>集成熔断降级和重试机制</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>微服务架构中的服务间通信</li>
 *   <li>分布式系统的远程调用</li>
 *   <li>需要负载均衡和容错的RPC调用</li>
 * </ul>
 */
@Configuration
@EnableFeignClients
@Import(DeFeignRegister.class)
public class DeFeignConfiguration {
}
