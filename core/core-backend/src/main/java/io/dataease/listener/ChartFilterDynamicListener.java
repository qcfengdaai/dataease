package io.dataease.listener;

import io.dataease.chart.manage.ChartViewOldDataMergeService;
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
 * 图表过滤器动态刷新监听器
 *
 * <p>功能描述：</p>
 * <ul>
 *   <li>在应用启动完成后，自动执行图表过滤器的动态刷新任务</li>
 *   <li>通过检查系统启动任务表，判断是否需要执行刷新</li>
 *   <li>执行完成后将任务状态标记为"done"，避免重复执行</li>
 * </ul>
 *
 * <p>执行顺序：@Order(value = 9)，在第9位执行</p>
 * <p>任务ID：chartFilterDynamic</p>
 *
 * @author Junjun
 * @since 2.0.0
 */
@Component
@Order(value = 9)
public class ChartFilterDynamicListener implements ApplicationListener<ApplicationReadyEvent> {
    private final Logger logger = LoggerFactory.getLogger(ChartFilterDynamicListener.class);

    /** 系统启动任务ID */
    public static final String JOB_ID = "chartFilterDynamic";

    @Resource
    private CoreSysStartupJobMapper coreSysStartupJobMapper;
    @Resource
    private ChartViewOldDataMergeService chartViewOldDataMergeService;

    /**
     * 应用启动完成后的回调方法
     * 执行图表过滤器的动态刷新任务
     *
     * @param applicationReadyEvent 应用启动完成事件
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        logger.info("====chart filter dynamic [start]====");

        // 1. 从数据库查询该启动任务
        CoreSysStartupJob sysStartupJob = coreSysStartupJobMapper.selectById(JOB_ID);

        // 2. 检查任务是否存在且状态为"ready"（待执行）
        if (ObjectUtils.isNotEmpty(sysStartupJob) && StringUtils.equalsIgnoreCase(sysStartupJob.getStatus(), "ready")) {
            logger.info("====chart filter dynamic [doing]====");

            // 3. 执行图表过滤器刷新
            chartViewOldDataMergeService.refreshFilter();

            // 4. 将任务状态更新为"done"（已完成）
            sysStartupJob.setStatus("done");
            coreSysStartupJobMapper.updateById(sysStartupJob);
        }
        logger.info("====chart filter dynamic [end]====");
    }
}
