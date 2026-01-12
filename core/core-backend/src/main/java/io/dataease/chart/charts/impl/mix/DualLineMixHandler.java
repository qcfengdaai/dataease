package io.dataease.chart.charts.impl.mix;

import io.dataease.chart.utils.ChartDataBuild;
import io.dataease.extensions.view.dto.AxisFormatResult;
import io.dataease.extensions.view.dto.ChartAxis;
import io.dataease.extensions.view.dto.ChartViewDTO;
import io.dataease.extensions.view.dto.CustomFilterResult;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DualLineMixHandler extends GroupMixHandler {
    @Getter
    private final String type = "chart-mix-dual-line";

    /**
     * 构建标准图表结果
     * 将查询数据转换为双折线混合图可用的数据格式
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param data 查询返回的原始数据
     * @return 格式化后的双折线混合图数据
     */
    @Override
    public Map<String, Object> buildNormalResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, List<String[]> data) {
        boolean isDrill = filterResult
                .getFilterList()
                .stream()
                .anyMatch(ele -> ele.getFilterType() == 1);
        var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
        var xAxisExt = formatResult.getAxisMap().get(ChartAxis.xAxisExt);
        var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
        var xAxisBase = xAxis.subList(0, xAxis.size() - xAxisExt.size());
        return ChartDataBuild.transMixChartDataAntV(xAxisBase, xAxis, xAxisExt, yAxis, view, data, isDrill, true);
    }

}
