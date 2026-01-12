package io.dataease.chart.charts;

import io.dataease.chart.charts.impl.DefaultChartHandler;
import io.dataease.extensions.view.plugin.AbstractChartPlugin;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 图表处理器管理类
 * 负责管理各种图表类型处理器的注册和获取
 * 使用策略模式为不同图表类型提供对应的处理器
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>图表处理器的注册管理</li>
 *   <li>根据图表类型获取对应的处理器</li>
 *   <li>提供默认处理器作为降级方案</li>
 * </ul>
 *
 * @author DataEase Team
 */
@Component
public class ChartHandlerManager {
    @Lazy
    @Resource
    private DefaultChartHandler defaultChartHandler;
    private static final ConcurrentHashMap<String, AbstractChartPlugin> CHART_HANDLER_MAP = new ConcurrentHashMap<>();

    public void registerChartHandler(String render, String type, AbstractChartPlugin chartHandler) {
        CHART_HANDLER_MAP.put(render + "-" + type, chartHandler);
    }

    public AbstractChartPlugin getChartHandler(String render, String type) {
        var handler =  CHART_HANDLER_MAP.get(render + "-" + type);
        if (handler == null) {
            return defaultChartHandler;
        }
        return handler;
    }
}
