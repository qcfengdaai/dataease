package io.dataease.chart.charts.impl;


import io.dataease.extensions.view.dto.AxisFormatResult;
import io.dataease.extensions.view.dto.ChartAxis;
import io.dataease.extensions.view.dto.ChartViewDTO;
import io.dataease.extensions.view.dto.ChartViewFieldDTO;

import java.util.ArrayList;

public class GroupChartHandler extends YoyChartHandler {
    /**
     * 格式化坐标轴
     * 处理分组图表的坐标轴配置，包括X轴扩展字段
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果，包含X轴和X轴扩展字段
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var result = super.formatAxis(view);
        var xAxis = new ArrayList<ChartViewFieldDTO>(view.getXAxis());
        xAxis.addAll(view.getXAxisExt());
        result.getAxisMap().put(ChartAxis.xAxis, xAxis);
        return result;
    }
}
