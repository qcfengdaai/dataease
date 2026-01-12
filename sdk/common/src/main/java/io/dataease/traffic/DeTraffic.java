package io.dataease.traffic;

import java.lang.annotation.*;

/**
 * API流量控制注解
 * 用于标记需要进行并发限制的方法
 * 支持自定义并发阈值和API标识
 *
 * 使用说明：
 * 1. 在需要限流的方法上添加此注解
 * 2. 指定API标识(api)用于区分不同的API接口
 * 3. 可选择指定并发阈值(value)，不指定则使用默认配置
 *
 * 示例：
 * &#64;DeTraffic(api = "user.login", value = 5)
 * public Result login(String username, String password) {
 *     // 登录逻辑，最大允许5个并发请求
 * }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeTraffic {
    /**
     * 并发阈值，定义允许的最大并发数
     * 默认值为0，表示使用系统配置的默认值(dataease.traffic)
     * 当设置具体数值时，将覆盖默认配置
     * @return 并发阈值
     */
    int value() default 0;

    /**
     * API标识，用于区分不同的API接口
     * 这个值将用于数据库存储和并发控制的唯一标识
     * 建议使用层级结构命名，如 "module.function"
     * @return API标识字符串
     */
    String api();
}
