package io.dataease.traffic;

import io.dataease.exception.DEException;
import io.dataease.traffic.dao.entity.CoreApiTraffic;
import io.dataease.traffic.dao.mapper.CoreApiTrafficMapper;
import io.dataease.utils.IDUtils;
import io.dataease.utils.LogUtil;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * API流量控制切面类
 * 基于AOP机制实现对带有@DeTraffic注解方法的并发限制
 *
 * 工作原理：
 * 1. 拦截带有@DeTraffic注解的方法调用
 * 2. 检查当前API的并发数是否超过阈值
 * 3. 如果未超过阈值，则允许执行并增加并发计数
 * 4. 如果超过阈值，则抛出异常拒绝访问
 * 5. 无论执行成功还是失败，最终都会释放并发计数
 *
 * 限流策略：
 * - 基于数据库计数器实现
 * - 支持动态阈值配置
 * - 支持API级别的独立限流
 */
@Aspect
@Component
public class DeTrafficAop {

    @Resource
    private CoreApiTrafficMapper coreApiTrafficMapper;

    /**
     * 默认并发阈值配置
     * 从配置文件中读取dataease.traffic属性值，默认为2
     */
    @Value("${dataease.traffic:2}")
    private Integer defaultTraffic;

    /**
     * 限流错误消息模板
     */
    final private static String errorMsg = "当前API【%s】设定并发阈值为【%s】，现已经达到限流阈值，请稍后再试！";

    /**
     * 环绕通知，实现流量控制核心逻辑
     * 对所有标注了@DeTraffic注解的方法进行流量控制
     * @param point 切入点，包含被拦截方法的信息和执行上下文
     * @return 被拦截方法的返回值
     * @throws Throwable 方法执行过程中可能抛出的异常
     */
    @Around(value = "@annotation(io.dataease.traffic.DeTraffic)")
    public Object trafficAround(ProceedingJoinPoint point) throws Throwable {
        // 获取方法签名和注解信息
        MethodSignature ms = (MethodSignature) point.getSignature();
        Method method = ms.getMethod();
        DeTraffic traffic = method.getAnnotation(DeTraffic.class);

        // 确定并发阈值：优先使用注解中配置的值，否则使用默认值
        int value = traffic.value();
        if (value == 0) {
            value = defaultTraffic;
        }

        // 获取API标识
        String api = traffic.api();
        Object result = null;
        boolean access = false;

        try {
            // 检查API是否已存在流量控制记录
            Integer count = coreApiTrafficMapper.apiCount(api);
            if (count == 0) {
                // 首次访问该API，创建新的流量控制记录
                CoreApiTraffic apiTraffic = new CoreApiTraffic();
                apiTraffic.setId(IDUtils.snowID());
                apiTraffic.setAlive(1); // 初始并发数为1
                apiTraffic.setThreshold(value);
                apiTraffic.setApi(api);
                coreApiTrafficMapper.insert(apiTraffic);
                access = true;
                result = point.proceed(); // 执行原方法
                return result;
            }

            // 获取当前活跃并发数
            int alive = coreApiTrafficMapper.getAlive(api);
            if (alive < value) {
                // 未达到限流阈值，允许访问并增加并发计数
                coreApiTrafficMapper.upgrade(api);
                access = true;
                result = point.proceed(); // 执行原方法
                return result;
            }
            // 达到限流阈值，拒绝访问
        } catch (Exception e) {
            // 记录流量控制过程中的异常
            LogUtil.error(e.getMessage(), e);
        } finally {
            // 无论成功还是失败，都要释放并发计数
            if (access) {
                coreApiTrafficMapper.releaseAlive(api);
            }
        }

        // 抛出限流异常
        DEException.throwException(String.format(errorMsg, api, value));
        return null;
    }
}
