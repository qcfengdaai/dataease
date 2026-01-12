package io.dataease.chart.charts.impl.map;

import io.dataease.chart.charts.impl.DefaultChartHandler;
import io.dataease.chart.utils.ChartDataBuild;
import io.dataease.extensions.view.dto.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class HeatMapHandler extends DefaultChartHandler {
    @Getter
    private String type = "heat-map";

    /**
     * 构建图表结果
     * 将查询数据转换为热力图可用的数据格式
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param data 查询返回的原始数据
     * @return 格式化后的热力图数据
     */
    @Override
    public Map<String, Object> buildResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, List<String[]> data) {
        boolean isDrill = filterResult
                .getFilterList()
                .stream()
                .anyMatch(ele -> ele.getFilterType() == 1);
        var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
        var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
        Map<String, Object> result = ChartDataBuild.transHeatMapChartDataAntV(xAxis, xAxis, yAxis, view, data, isDrill);
        return result;
    }
}
