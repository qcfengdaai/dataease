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
 * 图表过滤器数据合并监听器
 *
 * <p>功能描述：</p>
 * <ul>
 *   <li>在应用启动完成后，自动执行旧版图表过滤器数据的迁移合并任务</li>
 *   <li>用于版本升级时的数据兼容性处理</li>
 *   <li>执行完成后将任务状态标记为"done"</li>
 * </ul>
 *
 * <p>执行顺序：@Order(value = 4)，在第4位执行，较早执行以便其他功能可以使用合并后的数据</p>
 * <p>任务ID：chartFilterMerge</p>
 *
 * @author Junjun
 * @since 2.0.0
 */
@Component
@Order(value = 4)
public class ChartFilterMergeListener implements ApplicationListener<ApplicationReadyEvent> {
    private final Logger logger = LoggerFactory.getLogger(ChartFilterMergeListener.class);

    /** 系统启动任务ID */
    public static final String JOB_ID = "chartFilterMerge";

    @Resource
    private CoreSysStartupJobMapper coreSysStartupJobMapper;
    @Resource
    private ChartViewOldDataMergeService chartViewOldDataMergeService;

    /**
     * 应用启动完成后的回调方法
     * 执行旧版图表过滤器数据的迁移合并
     *
     * @param applicationReadyEvent 应用启动完成事件
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        logger.info("====chart filter merge [start]====");

        // 1. 从数据库查询该启动任务
        CoreSysStartupJob sysStartupJob = coreSysStartupJobMapper.selectById(JOB_ID);

        // 2. 检查任务是否存在且状态为"ready"
        if (ObjectUtils.isNotEmpty(sysStartupJob) && StringUtils.equalsIgnoreCase(sysStartupJob.getStatus(), "ready")) {
            logger.info("====chart filter merge [doing]====");

            // 3. 执行旧数据合并
            chartViewOldDataMergeService.mergeOldData();

            // 4. 更新任务状态为"done"
            sysStartupJob.setStatus("done");
            coreSysStartupJobMapper.updateById(sysStartupJob);
        }
        logger.info("====chart filter merge [end]====");
    }
}
