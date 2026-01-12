package io.dataease.chart.charts.impl.mix;

import io.dataease.extensions.view.dto.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GroupMixHandler extends MixHandler {
    @Getter
    private final String type = "chart-mix-group";

    /**
     * 格式化坐标轴
     * 处理分组混合图的坐标轴配置，包括左轴分组和右轴的配置
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果，包含左轴、右轴的完整配置
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var axisMap = new HashMap<ChartAxis, List<ChartViewFieldDTO>>();
        var context = new HashMap<String, Object>();
        AxisFormatResult result = new AxisFormatResult(axisMap, context);
        //左轴分组子维度,非分组不需要
        axisMap.put(ChartAxis.xAxisExt, view.getXAxisExt());
        //左轴堆叠子维度,非堆叠不需要
        axisMap.put(ChartAxis.extStack, Collections.emptyList());
        //左轴指标
        axisMap.put(ChartAxis.yAxis, view.getYAxis());
        //右轴分组子维度
        axisMap.put(ChartAxis.extBubble, view.getExtBubble());
        //右轴指标
        axisMap.put(ChartAxis.yAxisExt, view.getYAxisExt());
        //去除除了x轴以外的排序
        axisMap.forEach((k, v) -> {
            if (!ChartAxis.xAxisExt.equals(k)) {
                v.forEach(x -> x.setSort("none"));
            }
        });
        axisMap.put(ChartAxis.extLabel, view.getExtLabel());
        axisMap.put(ChartAxis.extTooltip, view.getExtTooltip());
        //图表整体主维度
        var xAxis = new ArrayList<>(view.getXAxis());
        var xAxisGroup = new ArrayList<>(view.getXAxis());
        xAxisGroup.addAll(view.getXAxisExt());
        axisMap.put(ChartAxis.xAxis, xAxisGroup);
        context.put("xAxisBase", xAxis);
        axisMap.put(ChartAxis.drill, new ArrayList<>(view.getDrillFields()));
        return result;
    }

    /**
     * 构建标准图表结果
     * 使用父类的方法构建分组混合图的数据格式
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param data 查询返回的原始数据
     * @return 格式化后的分组混合图数据
     */
    @Override
    public Map<String, Object> buildNormalResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, List<String[]> data) {
        return super.buildNormalResult(view, formatResult, filterResult, data);
    }

}
