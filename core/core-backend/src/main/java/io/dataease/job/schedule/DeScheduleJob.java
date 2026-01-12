package io.dataease.job.schedule;

import io.dataease.utils.LogUtil;
import org.quartz.*;

/**
 * 数据集定时任务抽象类
 * 所有数据集相关的定时任务都需要继承此类
 */
public abstract class DeScheduleJob implements Job {

    /** 数据集表ID */
    protected Long datasetTableId;
    /** Cron表达式 */
    protected String expression;
    /** 任务ID */
    protected Long taskId;
    /** 更新类型 */
    protected String updateType;

    /**
     * 执行定时任务
     * @param context 任务执行上下文
     * @throws JobExecutionException 任务执行异常
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobKey jobKey = context.getTrigger().getJobKey();
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        // 从JobDataMap中获取任务参数
        this.datasetTableId = jobDataMap.getLong("datasetTableId");
        this.expression = jobDataMap.getString("expression");
        this.taskId = jobDataMap.getLong("taskId");
        this.updateType = jobDataMap.getString("updateType");
        LogUtil.info(jobKey.getName() + " Running: " + datasetTableId);
        LogUtil.info("CronExpression: " + expression);
        // 调用子类实现的业务逻辑
        businessExecute(context);
    }

    /**
     * 业务执行方法，由子类实现
     * @param context 任务执行上下文
     */
    abstract void businessExecute(JobExecutionContext context);
}
