package io.dataease.job.schedule;

import com.fit2cloud.quartz.anno.QuartzScheduled;
import io.dataease.datasource.server.DatasourceServer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 定时调度器
 * 负责执行系统级的定时任务
 */
@Component
public class Schedular {

    @Resource
    private DatasourceServer datasourceServer;

    /**
     * 更新已停止的定时任务状态
     * 每3分钟执行一次
     */
    @QuartzScheduled(cron = "0 0/3 * * * ?")
    public void updateStopJobStatus() {
        datasourceServer.updateStopJobStatus();
    }

}
