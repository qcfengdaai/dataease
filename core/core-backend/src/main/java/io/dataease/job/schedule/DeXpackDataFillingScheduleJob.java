package io.dataease.job.schedule;

import io.dataease.license.utils.LicenseUtil;
import io.dataease.utils.CommonBeanFactory;
import io.dataease.utils.LogUtil;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 数据填报定时任务Job
 * 负责执行数据填报的定时任务，包括许可证验证
 */
@Component
public class DeXpackDataFillingScheduleJob implements Job {

    /**
     * 执行定时任务
     * @param jobExecutionContext 任务执行上下文
     * @throws JobExecutionException 任务执行异常
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Trigger trigger = jobExecutionContext.getTrigger();
        JobKey jobKey = trigger.getJobKey();
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        DeDataFillingTaskExecutor deTaskExecutor = CommonBeanFactory.getBean(DeDataFillingTaskExecutor.class);
        assert deTaskExecutor != null;
        try {
            // 验证许可证
            LicenseUtil.validate();
            // 执行任务
            boolean taskLoaded = deTaskExecutor.execute(jobDataMap);
            // 如果任务未加载，则删除任务
            if (!taskLoaded) {
                Objects.requireNonNull(CommonBeanFactory.getBean(ScheduleManager.class)).removeJob(jobKey, trigger.getKey());
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e.getCause());
        }
    }
}
