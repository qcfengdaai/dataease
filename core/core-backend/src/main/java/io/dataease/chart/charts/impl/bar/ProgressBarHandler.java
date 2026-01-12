package io.dataease.chart.charts.impl.bar;

import io.dataease.chart.utils.ChartDataBuild;
import io.dataease.extensions.view.dto.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 进度条处理器
 * 负责进度条图的数据处理和渲染
 * 继承BarHandler，用于百分比和进度展示
 *
 * <p>支持的图表类型：</p>
 * <ul>
 *   <li>进度条图 (progress-bar)</li>
 * </ul>
 *
 * @author DataEase Team
 */
@Component
public class ProgressBarHandler extends BarHandler {
    @Getter
    private String type = "progress-bar";

    /**
     * 初始化处理器
     * 注册进度条图的处理器
     */
    @Override
    public void init() {
        chartHandlerManager.registerChartHandler(this.getRender(), this.getType(), this);
    }

    /**
     * 格式化坐标轴
     * 处理进度条图的坐标轴配置，包括Y轴扩展字段和提示字段
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果，包含Y轴、Y轴扩展和提示字段
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var result = super.formatAxis(view);
        var yAxis = new ArrayList<>(view.getYAxis());
        yAxis.addAll(view.getYAxisExt());
        yAxis.addAll(view.getExtTooltip());
        result.getAxisMap().put(ChartAxis.yAxis, yAxis);
        result.getAxisMap().put(ChartAxis.yAxisExt, view.getYAxisExt());
        return result;
    }

    /**
     * 构建标准图表结果
     * 将查询数据转换为进度条图可用的数据格式
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param data 查询返回的原始数据
     * @return 格式化后的进度条图数据
     */
    @Override
    public Map<String, Object> buildNormalResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, List<String[]> data) {
        boolean isDrill = filterResult
                .getFilterList()
                .stream()
                .anyMatch(ele -> ele.getFilterType() == 1);
        var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
        var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
        Map<String, Object> result = ChartDataBuild.transMixChartDataAntV(xAxis, xAxis, new ArrayList<>(), yAxis, view, data, isDrill);
        return result;
    }
}
