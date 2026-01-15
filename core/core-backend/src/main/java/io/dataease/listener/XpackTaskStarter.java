package io.dataease.listener;

import io.dataease.job.schedule.DeDataFillingTaskExecutor;
import io.dataease.job.schedule.DeTaskExecutor;
import io.dataease.job.schedule.DeXpackDataSyncTaskExecutor;
import io.dataease.license.utils.LicenseUtil;
import io.dataease.utils.LogUtil;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 企业版任务启动器
 *
 * <p>功能描述：</p>
 * <ul>
 *   <li>在应用启动完成后，初始化所有企业版的任务执行器</li>
 *   <li>包括：报表任务执行器、数据填报任务执行器、数据同步任务执行器</li>
 *   <li>每个执行器初始化前都会验证许可证的有效性</li>
 *   <li>如果许可证失效或初始化失败，记录错误日志但不影响系统启动</li>
 * </ul>
 *
 * <p>执行时机：</p>
 * <p>实现ApplicationRunner接口，在应用启动完成后、beforeStart方法之后执行</p>
 * <p>执行顺序：@Order(value = 4)，在第4位执行</p>
 *
 * <p>任务执行器：</p>
 * <ol>
 *   <li>DeTaskExecutor：报表任务执行器，处理报表相关的定时任务</li>
 *   <li>DeDataFillingTaskExecutor：数据填报任务执行器，处理数据填报的定时任务</li>
 *   <li>DeXpackDataSyncTaskExecutor：数据同步任务执行器，处理数据同步的定时任务</li>
 * </ol>
 *
 * @author DataEase
 * @since 2.0.0
 */
@Component
@Order(value = 4)
public class XpackTaskStarter implements ApplicationRunner {

    @Resource
    private DeTaskExecutor deTaskExecutor;

    @Resource
    private DeDataFillingTaskExecutor deDataFillingTaskExecutor;

    @Resource
    private DeXpackDataSyncTaskExecutor deXpackDataSyncTaskExecutor;

    /**
     * 应用启动完成后的回调方法
     * 初始化所有企业版任务的执行器
     *
     * @param args 应用启动参数
     */
    @Override
    public void run(ApplicationArguments args) {
        // 1. 初始化报表任务执行器
        try {
            // 验证许可证是否有效
            LicenseUtil.validate();
            // 初始化报表任务执行器（由企业版实现）
            deTaskExecutor.init();
        } catch (Exception e) {
            // 许可证失效或初始化失败，记录错误但继续执行
            LogUtil.error(e.getMessage(), e.getCause());
        }

        // 2. 初始化数据填报任务执行器
        try {
            LicenseUtil.validate();
            deDataFillingTaskExecutor.init();
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e.getCause());
        }

        // 3. 初始化数据同步任务执行器
        try {
            LicenseUtil.validate();
            deXpackDataSyncTaskExecutor.init();
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e.getCause());
        }
    }
}
