package io.dataease.listener;

import io.dataease.chart.manage.ChartViewOldDataMergeService;
import io.dataease.dataset.manage.DatasetSQLManage;
import io.dataease.startup.dao.auto.entity.CoreSysStartupJob;
import io.dataease.startup.dao.auto.mapper.CoreSysStartupJobMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * 数据集跨库默认值设置监听器
 *
 * <p>功能描述：</p>
 * <ul>
 *   <li>在应用启动完成后，自动初始化数据集的跨库配置默认值</li>
 *   <li>为数据集设置默认的跨库查询参数</li>
 *   <li>确保数据集跨库功能的正常运行</li>
 * </ul>
 *
 * <p>执行顺序：@Order(value = 10)，在第10位执行，较晚执行以确保相关数据已准备就绪</p>
 * <p>任务ID：datasetCrossListener</p>
 *
 * @author Junjun
 * @since 2.0.0
 */
@Component
@Order(value = 10)
public class DatasetCrossListener implements ApplicationListener<ApplicationReadyEvent> {
    private final Logger logger = LoggerFactory.getLogger(DatasetCrossListener.class);

    /** 系统启动任务ID */
    public static final String JOB_ID = "datasetCrossListener";

    @Resource
    private CoreSysStartupJobMapper coreSysStartupJobMapper;
    @Resource
    private DatasetSQLManage datasetSQLManage;

    /**
     * 应用启动完成后的回调方法
     * 执行数据集跨库默认值设置
     *
     * @param applicationReadyEvent 应用启动完成事件
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        logger.info("====dataset cross listener [start]====");

        // 1. 从数据库查询该启动任务
        CoreSysStartupJob sysStartupJob = coreSysStartupJobMapper.selectById(JOB_ID);

        // 2. 检查任务是否存在且状态为"ready"
        if (ObjectUtils.isNotEmpty(sysStartupJob) && StringUtils.equalsIgnoreCase(sysStartupJob.getStatus(), "ready")) {
            logger.info("====dataset cross listener [doing]====");

            // 3. 设置数据集跨库默认值
            datasetSQLManage.datasetCrossDefault();

            // 4. 更新任务状态为"done"
            sysStartupJob.setStatus("done");
            coreSysStartupJobMapper.updateById(sysStartupJob);
        }
        logger.info("====dataset cross listener [end]====");
    }
}
