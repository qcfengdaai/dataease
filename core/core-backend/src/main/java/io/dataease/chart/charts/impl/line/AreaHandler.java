package io.dataease.chart.charts.impl.line;

import io.dataease.chart.utils.ChartDataBuild;
import io.dataease.extensions.view.dto.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AreaHandler extends LineHandler {
    @Getter
    private String type = "area";

    /**
     * 格式化坐标轴
     * 处理面积图的坐标轴配置
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var result = super.formatAxis(view);
        result.getAxisMap().put(ChartAxis.xAxis, view.getXAxis());
        return result;
    }


    /**
     * 构建标准图表结果
     * 将查询数据转换为面积图可用的数据格式
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param data 查询返回的原始数据
     * @return 格式化后的面积图数据
     */
    @Override
    public Map<String, Object> buildNormalResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, List<String[]> data) {
        boolean isDrill = filterResult.getFilterList().stream().anyMatch(ele -> ele.getFilterType() == 1);
        var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
        var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
        return ChartDataBuild.transChartData(xAxis, yAxis, view, data, isDrill);
    }
}
