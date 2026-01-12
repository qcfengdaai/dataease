package io.dataease.chart.charts.impl.mix;

import io.dataease.chart.utils.ChartDataBuild;
import io.dataease.extensions.view.dto.*;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StackMixHandler extends MixHandler {
    @Getter
    private final String type = "chart-mix-stack";

    /**
     * 格式化坐标轴
     * 处理堆叠混合图的坐标轴配置，包括左轴堆叠和右轴的配置
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果，包含左轴堆叠、右轴的完整配置
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var axisMap = new HashMap<ChartAxis, List<ChartViewFieldDTO>>();
        var context = new HashMap<String, Object>();
        AxisFormatResult result = new AxisFormatResult(axisMap, context);
        //左轴分组子维度,非分组不需要
        axisMap.put(ChartAxis.xAxisExt, Collections.emptyList());
        //左轴堆叠子维度,非堆叠不需要
        axisMap.put(ChartAxis.extStack, view.getExtStack());
        //左轴指标
        axisMap.put(ChartAxis.yAxis, view.getYAxis());
        //右轴分组子维度
        axisMap.put(ChartAxis.extBubble, view.getExtBubble());
        //右轴指标
        axisMap.put(ChartAxis.yAxisExt, view.getYAxisExt());
        //去除除了x轴以外的排序
        axisMap.forEach((k, v) -> {
            if (!ChartAxis.extStack.equals(k)) {
                v.forEach(x -> x.setSort("none"));
            }
        });
        axisMap.put(ChartAxis.extLabel, view.getExtLabel());
        axisMap.put(ChartAxis.extTooltip, view.getExtTooltip());
        //图表整体主维度
        var xAxis = new ArrayList<>(view.getXAxis());
        var xAxisStack = new ArrayList<>(view.getXAxis());
        xAxisStack.addAll(view.getExtStack());
        axisMap.put(ChartAxis.xAxis, xAxisStack);
        context.put("xAxisBase", xAxis);
        axisMap.put(ChartAxis.drill, new ArrayList<>(view.getDrillFields()));
        return result;
    }

    /**
     * 构建标准图表结果
     * 将查询数据转换为堆叠混合图可用的数据格式
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param data 查询返回的原始数据
     * @return 格式化后的堆叠混合图数据
     */
    @Override
    public Map<String, Object> buildNormalResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, List<String[]> data) {
        boolean isDrill = filterResult
                .getFilterList()
                .stream()
                .anyMatch(ele -> ele.getFilterType() == 1);
        var extStack = formatResult.getAxisMap().get(ChartAxis.extStack);
        var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
        if (CollectionUtils.isNotEmpty(extStack)) {
            // 堆叠左轴
            var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
            var drillAxis = xAxis.stream().filter(axis -> FieldSource.DRILL == axis.getSource()).toList();
            var xAxisBase = xAxis.subList(0, xAxis.size() - extStack.size() - drillAxis.size());
            //var xAxisBase = (List<ChartViewFieldDTO>) formatResult.getContext().get("xAxisBase");
            return ChartDataBuild.transMixChartStackDataAntV(xAxisBase, xAxis, extStack, yAxis, view, data, isDrill);
        } else {
            //无堆叠左轴和右轴还是走原逻辑
            var xAxisBase = (List<ChartViewFieldDTO>) formatResult.getContext().get("xAxisBase");
            var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
            var xAxisExt = formatResult.getAxisMap().get(ChartAxis.xAxisExt);
            return super.buildNormalResult(view, formatResult, filterResult, data);
        }
    }
}
