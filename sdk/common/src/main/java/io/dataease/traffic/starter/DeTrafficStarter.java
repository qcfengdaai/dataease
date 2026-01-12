package io.dataease.traffic.starter;

import io.dataease.traffic.dao.mapper.CoreApiTrafficMapper;
import io.dataease.utils.LogUtil;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * DataEase流量控制启动器
 * 实现ApplicationRunner接口，在Spring Boot应用启动完成后执行初始化任务
 * 主要负责清理上次运行时遗留的流量控制数据，确保新的运行周期从干净状态开始
 */
@Component
public class DeTrafficStarter implements ApplicationRunner {

    @Resource
    private CoreApiTrafficMapper coreApiTrafficMapper;

    /**
     * 应用启动后执行的方法
     * 清空所有API流量控制数据，防止因异常关闭导致的并发计数不准确
     * @param args 应用程序启动参数
     * @throws Exception 执行过程中可能抛出的异常
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            // 清空流量控制表中的所有数据，重置并发计数状态
            coreApiTrafficMapper.cleanTraffic();
        } catch (Exception e) {
            // 记录清理过程中的异常，但不影响应用启动
            LogUtil.error(e.getMessage(), new Throwable(e));
        }
    }
}
