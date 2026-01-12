package io.dataease.job.schedule;

import io.dataease.commons.utils.CronUtils;
import io.dataease.license.config.XpackInteract;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 数据填报任务执行器
 * 负责管理数据填报相关的定时任务，包括普通任务、重试任务和临时任务
 */
@Component("deDataFillingTaskExecutor")
public class DeDataFillingTaskExecutor {

    /** 是否为临时任务的标识 */
    protected static final String IS_TEMP_TASK = "isTempTask";
    /** 是否为重试任务的标识 */
    protected static final String IS_RETRY_TASK = "isRetryTask";

    /** 普通任务组 */
    private static final String JOB_GROUP = "DATA_FILLING_TASK";
    /** 重试任务组 */
    private static final String RETRY_JOB_GROUP = "RETRY_DATA_FILLING_TASK";
    /** 临时任务组 */
    private static final String TEMP_JOB_GROUP = "TEMP_DATA_FILLING_TASK";

    @Resource
    private ScheduleManager scheduleManager;

    /**
     * 执行任务，由企业版实现
     * @param taskData 任务数据
     * @return 是否执行成功
     */
    @XpackInteract(value = "dataFillingTaskExecutor", replace = true)
    public boolean execute(Map<String, Object> taskData) {
        return false;
    }

    /**
     * 初始化任务执行器，由企业版实现
     */
    @XpackInteract(value = "dataFillingTaskExecutor", replace = true)
    public void init() {
    }

    /**
     * 添加或更新普通任务
     * @param taskId 任务ID
     * @param cron Cron表达式
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public void addOrUpdateTask(Long taskId, String cron, Long startTime, Long endTime) {
        if (CronUtils.taskExpire(endTime)) {
            return;
        }
        String key = taskId.toString();
        JobKey jobKey = new JobKey(key, JOB_GROUP);
        TriggerKey triggerKey = new TriggerKey(key, JOB_GROUP);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("taskId", taskId);
        jobDataMap.put(IS_TEMP_TASK, false);
        Date end = null;
        if (ObjectUtils.isNotEmpty(endTime)) end = new Date(endTime);
        scheduleManager.addOrUpdateCronJob(jobKey, triggerKey, DeXpackDataFillingScheduleJob.class, cron, startTime == null ? null : new Date(startTime), end, jobDataMap);
    }

    /**
     * 添加重试任务
     * @param taskId 任务ID
     * @param retryLimit 重试次数限制
     * @param retryInterval 重试间隔（分钟）
     */
    public void addRetryTask(Long taskId, Integer retryLimit, Integer retryInterval) {
        long saltTime = 3000L;
        long interval = retryInterval == null ? 5L : retryInterval;
        long intervalMill = interval * 60000L;
        long now = System.currentTimeMillis();
        String cron = "0 */" + interval + " * * * ?";
        long endTime = (retryLimit + 1) * intervalMill + now - saltTime;
        String key = taskId.toString();
        if (CronUtils.taskExpire(endTime)) {
            return;
        }
        JobKey jobKey = new JobKey(key, RETRY_JOB_GROUP);
        TriggerKey triggerKey = new TriggerKey(key, RETRY_JOB_GROUP);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("taskId", taskId);
        jobDataMap.put(IS_RETRY_TASK, true);
        Date end = null;
        if (ObjectUtils.isNotEmpty(endTime)) end = new Date(endTime);
        scheduleManager.addOrUpdateCronJob(jobKey, triggerKey, DeXpackDataFillingScheduleJob.class, cron, new Date(now), end, jobDataMap);
    }

    /**
     * 立即触发任务
     * @param taskId 任务ID
     * @return 是否触发成功
     * @throws Exception 触发异常
     */
    public boolean fireNow(Long taskId) throws Exception {
        String key = taskId.toString();
        JobKey jobKey = new JobKey(key, JOB_GROUP);
        if (scheduleManager.exist(jobKey)) {
            scheduleManager.fireNow(jobKey);
            return true;
        }
        return false;
    }

    /**
     * 添加临时任务
     * @param taskId 任务ID
     * @param startTime 开始时间
     */
    public void addTempTask(Long taskId, Long startTime) {
        String key = taskId.toString();
        JobKey jobKey = new JobKey(key, TEMP_JOB_GROUP);
        TriggerKey triggerKey = new TriggerKey(key, TEMP_JOB_GROUP);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(IS_TEMP_TASK, true);
        String cron = CronUtils.tempCron();
        jobDataMap.put("taskId", taskId);
        scheduleManager.addOrUpdateCronJob(jobKey, triggerKey, DeXpackDataFillingScheduleJob.class, cron, new Date(startTime), null, jobDataMap);
    }

    /**
     * 删除任务
     * @param taskId 任务ID
     * @param isTemp 是否为临时任务
     */
    public void removeTask(Long taskId, boolean isTemp) {
        String key = taskId.toString();
        JobKey jobKey = new JobKey(key, isTemp ? TEMP_JOB_GROUP : JOB_GROUP);
        TriggerKey triggerKey = new TriggerKey(key, isTemp ? TEMP_JOB_GROUP : JOB_GROUP);
        scheduleManager.removeJob(jobKey, triggerKey);
    }

    /**
     * 删除重试任务
     * @param taskId 任务ID
     */
    public void removeRetryTask(Long taskId) {
        String key = taskId.toString();
        JobKey jobKey = new JobKey(key, RETRY_JOB_GROUP);
        TriggerKey triggerKey = new TriggerKey(key, RETRY_JOB_GROUP);
        scheduleManager.removeJob(jobKey, triggerKey);
    }

    /**
     * 清除所有重试任务
     * @throws Exception 清除异常
     */
    public void clearRetryTask() throws Exception {
        scheduleManager.clearByGroup(RETRY_JOB_GROUP);
    }
}
