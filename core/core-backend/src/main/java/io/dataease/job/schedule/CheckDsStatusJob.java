package io.dataease.job.schedule;


import io.dataease.datasource.server.DatasourceServer;
import io.dataease.utils.CommonBeanFactory;
import io.dataease.utils.LogUtil;
import jakarta.annotation.Resource;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;


/**
 * 检查数据源状态的定时任务
 * 定期检查系统中所有数据源的连接状态并更新
 */
@Component
public class CheckDsStatusJob implements Job {
    @Resource
    private DatasourceServer datasourceServer;

    /**
     * 构造函数，获取数据源服务
     */
    public CheckDsStatusJob() {
        datasourceServer = (DatasourceServer) CommonBeanFactory.getBean(DatasourceServer.class);
    }

    /**
     * 执行数据源状态检查
     * @param context 任务执行上下文
     * @throws JobExecutionException 任务执行异常
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LogUtil.info("Begin to check ds status...");
        datasourceServer.updateDatasourceStatus();
    }

}
