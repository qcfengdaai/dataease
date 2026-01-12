package io.dataease.listener;

import io.dataease.datasource.dao.auto.entity.CoreDatasourceTask;
import io.dataease.datasource.manage.DataSourceManage;
import io.dataease.datasource.manage.DatasourceSyncManage;
import io.dataease.datasource.manage.EngineManage;
import io.dataease.datasource.provider.CalciteProvider;
import io.dataease.datasource.server.DatasourceServer;
import io.dataease.datasource.server.DatasourceTaskServer;
import io.dataease.system.dao.auto.entity.CoreSysSetting;
import io.dataease.system.manage.SysParameterManage;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 数据源初始化启动监听器
 *
 * <p>功能描述：</p>
 * <ul>
 *   <li>在应用启动后，自动执行数据源的初始化任务</li>
 *   <li>初始化简单引擎和Calcite连接池</li>
 *   <li>恢复所有数据源同步任务的调度</li>
 *   <li>重新添加数据源的定时任务</li>
 *   <li>加密数据源配置信息</li>
 * </ul>
 *
 * <p>执行顺序：@Order(value = 2)，在第2位执行，优先级很高，确保数据源相关功能尽早可用</p>
 * <p>主要任务：</p>
 * <ol>
 *   <li>初始化简单引擎（用于数据源查询）</li>
 *   <li>初始化Calcite连接池（用于SQL解析和优化）</li>
 *   <li>恢复所有有效的数据源同步任务调度</li>
 *   <li>删除已过期的数据源同步任务</li>
 *   <li>从系统参数中读取并添加数据源定时任务</li>
 *   <li>对数据源配置进行加密处理</li>
 * </ol>
 *
 * @author DataEase
 * @since 2.0.0
 */
@Component
@Order(value = 2)
public class DataSourceInitStartListener implements ApplicationListener<ApplicationReadyEvent> {
    @Resource
    private DatasourceSyncManage datasourceSyncManage;
    @Resource
    private DatasourceServer datasourceServer;
    @Resource
    private DataSourceManage dataSourceManage;
    @Resource
    private DatasourceTaskServer datasourceTaskServer;
    @Resource
    private CalciteProvider calciteProvider;
    @Resource
    private EngineManage engineManage;
    @Resource
    private SysParameterManage sysParameterManage;

    /**
     * 应用启动完成后的回调方法
     * 执行数据源的初始化操作
     *
     * @param applicationReadyEvent 应用启动完成事件
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        // 1. 初始化简单引擎（用于数据源查询）
        try {
            engineManage.initSimpleEngine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. 初始化Calcite连接池（用于SQL解析和优化）
        try {
            calciteProvider.initConnectionPool();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. 恢复所有数据源同步任务的调度
        List<CoreDatasourceTask> list = datasourceTaskServer.listAll();
        for (CoreDatasourceTask task : list) {
            try {
                // 跳过"立即执行"类型的任务（不需要调度）
                if (!StringUtils.equalsIgnoreCase(task.getSyncRate(), DatasourceTaskServer.ScheduleType.RIGHTNOW.toString())) {
                    // 检查任务是否有结束时间限制
                    if (task.getEndTime() != null && task.getEndTime() > 0) {
                        // 如果结束时间未到，恢复调度
                        if (task.getEndTime() > System.currentTimeMillis()) {
                            datasourceSyncManage.addSchedule(task);
                        } else {
                            // 如果已过期，删除调度
                            datasourceSyncManage.deleteSchedule(task);
                        }
                    } else {
                        // 没有结束时间限制，永久恢复调度
                        datasourceSyncManage.addSchedule(task);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 4. 从系统参数中读取并添加数据源定时任务
        try {
            List<CoreSysSetting> coreSysSettings = sysParameterManage.groupList("basic.");
            datasourceServer.addJob(coreSysSettings);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 5. 加密数据源配置信息
        dataSourceManage.encryptDsConfig();
    }


}
