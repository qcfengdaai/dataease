package io.dataease.chart.charts.impl.pie;

import io.dataease.chart.charts.impl.YoyChartHandler;
import io.dataease.extensions.view.dto.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 饼图处理器
 * 负责饼图、环形图及其变体的数据处理和渲染
 * 继承YoyChartHandler支持同比环比计算
 *
 * <p>支持的图表类型：</p>
 * <ul>
 *   <li>基础饼图 (pie)</li>
 *   <li>玫瑰图 (pie-rose)</li>
 *   <li>环形图 (pie-donut)</li>
 *   <li>环形玫瑰图 (pie-donut-rose)</li>
 * </ul>
 *
 * <p>特殊处理：</p>
 * <ul>
 *   <li>自动过滤负值数据</li>
 *   <li>百分比计算和显示</li>
 * </ul>
 *
 * @author DataEase Team
 */
@Component
public class PieHandler extends YoyChartHandler {
    /**
     * 初始化处理器
     * 注册饼图、玫瑰图、环形图和环形玫瑰图的处理器
     */
    @Override
    public void init() {
        chartHandlerManager.registerChartHandler(this.getRender(), "pie", this);
        chartHandlerManager.registerChartHandler(this.getRender(), "pie-rose", this);
        chartHandlerManager.registerChartHandler(this.getRender(), "pie-donut", this);
        chartHandlerManager.registerChartHandler(this.getRender(), "pie-donut-rose", this);
    }

    /**
     * 格式化坐标轴
     * 处理饼图的坐标轴配置，包括扩展标签和提示字段
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果，包含扩展标签和提示字段
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var result = super.formatAxis(view);
        var yAxis = result.getAxisMap().get(ChartAxis.yAxis);
        yAxis.addAll(view.getExtLabel());
        yAxis.addAll(view.getExtTooltip());
        result.getAxisMap().put(ChartAxis.extLabel, view.getExtLabel());
        result.getAxisMap().put(ChartAxis.extTooltip, view.getExtTooltip());
        return result;
    }

    /**
     * 构建图表视图
     * 将计算结果转换为饼图视图对象，并过滤负值数据
     *
     * @param view 图表视图信息
     * @param calcResult 图表计算结果
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @return 构建完成的饼图视图对象，已过滤负值
     */
    @Override
    public ChartViewDTO buildChart(ChartViewDTO view, ChartCalcDataResult calcResult, AxisFormatResult formatResult, CustomFilterResult filterResult) {
        ChartViewDTO result = super.buildChart(view, calcResult, formatResult, filterResult);
        filterPositiveData(result, "data", AxisChartDataAntVDTO.class);
        filterPositiveData(result, "tableRow", Map.class, view.getYAxis().get(0).getDataeaseName());
        return result;
    }

    /**
     * 过滤正数数据根据data
     * @param result
     * @param key
     * @param clazz
     * @param <T>
     */
    private <T> void filterPositiveData(ChartViewDTO result, String key, Class<T> clazz) {
        if (result.getData().containsKey(key)) {
            List<T> list = ((List<T>) result.getData().get(key))
                    .stream()
                    .filter(item -> {
                        if (clazz == AxisChartDataAntVDTO.class) {
                            if (Objects.isNull(((AxisChartDataAntVDTO) item).getValue())) return false;
                            return ((AxisChartDataAntVDTO) item).getValue().compareTo(BigDecimal.ZERO) >= 0;
                        } else if (clazz == Map.class) {
                            return isPositive(((Map<String, Object>) item).get("value"));
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            result.getData().put(key, list);
        }
    }

    /**
     * 过滤正数数据根据tableRow
     * @param result
     * @param key
     * @param clazz
     * @param yAxisName
     * @param <T>
     */
    private <T> void filterPositiveData(ChartViewDTO result, String key, Class<T> clazz, String yAxisName) {
        if (result.getData().containsKey(key)) {
            List<T> list = ((List<T>) result.getData().get(key))
                    .stream()
                    .filter(item -> {
                        if (clazz == Map.class) {
                            Object value = ((Map<String, Object>) item).get(yAxisName);
                            return isPositive(value);
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            result.getData().put(key, list);
        }
    }

    private boolean isPositive(Object value) {
        if (value instanceof String) {
            try {
                return new BigDecimal((String) value).compareTo(BigDecimal.ZERO) >= 0;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).compareTo(BigDecimal.ZERO) >= 0;
        }
        return false;
    }
}
