package io.dataease.chart.charts.impl.scatter;

import io.dataease.chart.charts.impl.YoyChartHandler;
import io.dataease.chart.utils.ChartDataBuild;
import io.dataease.extensions.view.dto.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class ScatterHandler extends YoyChartHandler {
    @Getter
    private String type = "scatter";

    /**
     * 格式化坐标轴
     * 处理散点图的坐标轴配置，包括气泡字段和扩展标签、提示字段
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果，包含气泡字段和扩展字段配置
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var result = super.formatAxis(view);
        var yAxis = new ArrayList<>(view.getYAxis());
        yAxis.addAll(view.getExtBubble());
        yAxis.addAll(view.getExtLabel());
        yAxis.addAll(view.getExtTooltip());
        result.getAxisMap().put(ChartAxis.yAxis, yAxis);
        result.getAxisMap().put(ChartAxis.extBubble, view.getExtBubble());
        result.getAxisMap().put(ChartAxis.extTooltip, view.getExtTooltip());
        result.getAxisMap().put(ChartAxis.extLabel, view.getExtLabel());
        return result;
    }

    /**
     * 构建标准图表结果
     * 将查询数据转换为散点图可用的数据格式
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param data 查询返回的原始数据
     * @return 格式化后的散点图数据
     */
    @Override
    public Map<String, Object> buildNormalResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, List<String[]> data) {
        boolean isDrill = filterResult.getFilterList().stream().anyMatch(ele -> ele.getFilterType() == 1);
        var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
        var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
        var yAxisTemp = new ArrayList<>(yAxis);
        var extBubble = formatResult.getAxisMap().get(ChartAxis.extBubble);
        if (!extBubble.isEmpty()) {
            // 剔除气泡大小，移除一个
            Iterator<ChartViewFieldDTO> iterator = yAxisTemp.iterator();
            while (iterator.hasNext()) {
                ChartViewFieldDTO obj = iterator.next();
                if (obj.getId().equals(extBubble.getFirst().getId())) {
                    iterator.remove();
                    break;
                }
            }
        }
        Map<String, Object> result = ChartDataBuild.transScatterDataAntV(xAxis, yAxisTemp, view, data, extBubble, isDrill);
        return result;
    }
}
