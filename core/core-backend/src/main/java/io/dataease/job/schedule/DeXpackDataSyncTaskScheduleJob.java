package io.dataease.job.schedule;

import io.dataease.license.utils.LicenseUtil;
import io.dataease.utils.CommonBeanFactory;
import io.dataease.utils.LogUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * 数据同步定时任务Job
 * 负责执行数据同步的定时任务，包括许可证验证
 */
@Component
public class DeXpackDataSyncTaskScheduleJob implements Job {

    /**
     * 执行定时任务
     * @param jobExecutionContext 任务执行上下文
     * @throws JobExecutionException 任务执行异常
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        DeXpackDataSyncTaskExecutor deTaskExecutor = CommonBeanFactory.getBean(DeXpackDataSyncTaskExecutor.class);
        assert deTaskExecutor != null;
        try {
            // 验证许可证
            LicenseUtil.validate();
            // 执行任务
            deTaskExecutor.execute(jobDataMap);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e.getCause());
        }
    }
}
