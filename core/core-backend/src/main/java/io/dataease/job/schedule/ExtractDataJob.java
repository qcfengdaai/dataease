package io.dataease.job.schedule;


import io.dataease.datasource.manage.DatasourceSyncManage;
import io.dataease.utils.CommonBeanFactory;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

/**
 * 数据抽取定时任务
 * 负责从数据源抽取数据到目标数据集
 */
@Component
public class ExtractDataJob extends DeScheduleJob {
    private DatasourceSyncManage datasourceSyncManage;

    /**
     * 构造函数，获取数据源同步管理器
     */
    public ExtractDataJob() {
        datasourceSyncManage = (DatasourceSyncManage) CommonBeanFactory.getBean(DatasourceSyncManage.class);
    }

    /**
     * 执行数据抽取业务逻辑
     * @param context 任务执行上下文
     */
    @Override
    void businessExecute(JobExecutionContext context) {
        datasourceSyncManage.extractData(datasetTableId, taskId, context);
    }

}
