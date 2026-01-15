package io.dataease.chart.charts.impl.bar;

import io.dataease.chart.utils.ChartDataBuild;
import io.dataease.extensions.view.dto.*;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 分组柱状图处理器
 * 负责分组柱状图的数据处理和渲染
 * 继承BarHandler，支持多系列并排显示
 *
 * <p>支持的图表类型：</p>
 * <ul>
 *   <li>分组柱状图 (bar-group)</li>
 * </ul>
 *
 * <p>特点：</p>
 * <ul>
 *   <li>支持多个系列并排显示</li>
 *   <li>支持X轴扩展字段</li>
 * </ul>
 *
 * @author DataEase Team
 */
@Component
public class GroupBarHandler extends BarHandler {
    @Getter
    private String type = "bar-group";

    /**
     * 格式化坐标轴
     * 处理分组柱状图的坐标轴配置，包括X轴扩展字段
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果，包含X轴和X轴扩展字段
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var result = super.formatAxis(view);
        var xAxis = result.getAxisMap().get(ChartAxis.xAxis);
        xAxis.addAll(view.getXAxisExt());
        result.getAxisMap().put(ChartAxis.xAxisExt, view.getXAxisExt());
        return result;
    }

    /**
     * 初始化处理器
     * 注册分组柱状图的处理器
     */
    @Override
    public void init() {
        chartHandlerManager.registerChartHandler(this.getRender(), this.getType(), this);
    }

    /**
     * 自定义过滤器处理
     * 处理分组维度下钻的过滤逻辑
     *
     * @param view 图表视图信息
     * @param filterList 过滤条件列表
     * @param formatResult 坐标轴格式化结果
     * @return 自定义过滤结果，包含下钻后的过滤条件
     */
    @Override
    public <T extends CustomFilterResult> T customFilter(ChartViewDTO view, List<ChartExtFilterDTO> filterList, AxisFormatResult formatResult) {
        var result = super.customFilter(view, filterList, formatResult);
        List<ChartDrillRequest> drillRequestList = view.getChartExtRequest().getDrill();
        var drillFields = formatResult.getAxisMap().get(ChartAxis.drill);
        // 分组维度下钻
        if (ObjectUtils.isNotEmpty(drillRequestList) && (drillFields.size() > drillRequestList.size())) {
            List<ChartExtFilterDTO> noDrillFilterList = filterList
                    .stream()
                    .filter(ele -> ele.getFilterType() != 1)
                    .collect(Collectors.toList());
            var noDrillFieldAxis = formatResult.getAxisMap().get(ChartAxis.xAxis)
                    .stream()
                    .filter(ele -> ele.getSource() != FieldSource.DRILL)
                    .collect(Collectors.toList());
            List<ChartExtFilterDTO> drillFilters = new ArrayList<>();
            ArrayList<ChartViewFieldDTO> fieldsToFilter = new ArrayList<>();
            var xAxisExt = formatResult.getAxisMap().get(ChartAxis.xAxisExt);
            if (ObjectUtils.isNotEmpty(xAxisExt) &&
                    Objects.equals(drillFields.get(0).getId(), xAxisExt.get(0).getId())) {
                fieldsToFilter.addAll(view.getXAxis());
                groupStackDrill(noDrillFieldAxis, noDrillFilterList, fieldsToFilter, drillFields, drillRequestList);
                formatResult.getAxisMap().put(ChartAxis.xAxis, noDrillFieldAxis);
                result.setFilterList(noDrillFilterList);
            }
        }
        return (T) result;
    }

    /**
     * 构建标准图表结果
     * 将查询数据转换为分组柱状图可用的数据格式
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param data 查询返回的原始数据
     * @return 格式化后的分组图表数据
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
        var drillAxis = xAxis.stream().filter(axis -> FieldSource.DRILL == axis.getSource()).toList();
        var xAxisBase = xAxis.subList(0, xAxis.size() - xAxisExt.size() - drillAxis.size());
        return ChartDataBuild.transBaseGroupDataAntV(xAxisBase, xAxis, xAxisExt, yAxis, view, data, isDrill);
    }
}
