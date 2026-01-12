package io.dataease.i18n;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * DataEase国际化启动器
 * 在Spring Boot应用启动完成后，自动加载自定义的国际化资源文件
 *
 * 功能特点：
 * 1. 应用启动时自动执行
 * 2. 加载自定义国际化资源路径
 * 3. 支持外部国际化文件配置
 * 4. 执行顺序为1000，在大部分组件之后执行
 *
 * 配置说明：
 * - 通过 dataease.path.i18n 配置自定义国际化文件路径
 * - 默认路径：file:/opt/dataease2.0/data/i18n/custom
 */
@Component
@Order(1000)
public class DeI18nStarter implements ApplicationRunner {

    /**
     * 国际化资源文件路径
     * 从配置文件读取，支持file:、classpath:等协议
     * 默认值：file:/opt/dataease2.0/data/i18n/custom
     */
    @Value("${dataease.path.i18n:file:/opt/dataease2.0/data/i18n/custom}")
    private String i18nPath;

    /**
     * 应用启动后执行的方法
     * 加载自定义国际化资源文件到消息源中
     *
     * @param args 应用启动参数
     * @throws Exception 执行过程中可能抛出的异常
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 调用动态国际化工具加载自定义资源
        DynamicI18nUtils.addOrUpdate(i18nPath);
    }

}
