package io.dataease.chart.charts.impl;

import io.dataease.extensions.view.dto.AxisFormatResult;
import io.dataease.extensions.view.dto.ChartAxis;
import io.dataease.extensions.view.dto.ChartViewDTO;
import io.dataease.extensions.view.dto.ChartViewFieldDTO;

import java.util.ArrayList;

public class ExtQuotaChartHandler extends DefaultChartHandler {
    /**
     * 格式化坐标轴
     * 处理扩展指标字段的坐标轴配置，包括扩展标签和提示字段
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果，包含Y轴、扩展标签和提示字段
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
}
