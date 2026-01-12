package io.dataease.chart.charts.impl.bar;

import io.dataease.chart.charts.impl.YoyChartHandler;
import io.dataease.chart.utils.ChartDataBuild;
import io.dataease.extensions.datasource.model.SQLMeta;
import io.dataease.extensions.datasource.provider.Provider;
import io.dataease.extensions.view.dto.*;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 范围条形图处理器
 * 负责范围条形图的数据处理和渲染
 * 继承YoyChartHandler，用于数值范围的展示
 *
 * <p>支持的图表类型：</p>
 * <ul>
 *   <li>范围条形图 (range-bar)</li>
 * </ul>
 *
 * @author DataEase Team
 */
@Component
public class RangeBarHandler extends YoyChartHandler {
    @Getter
    private final String type = "bar-range";

    /**
     * 格式化坐标轴
     * 处理范围条形图的坐标轴配置，支持数值范围和日期范围的显示
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果，包含范围条形图特有的配置信息
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var result = super.formatAxis(view);
        var yAxis = new ArrayList<ChartViewFieldDTO>();
        var xAxis = new ArrayList<ChartViewFieldDTO>(view.getXAxis());
        var xAxisBase = new ArrayList<ChartViewFieldDTO>(view.getXAxis());
        boolean skipBarRange = false;
        boolean barRangeDate = false;
        if (CollectionUtils.isNotEmpty(view.getYAxis()) && CollectionUtils.isNotEmpty(view.getYAxisExt())) {
            ChartViewFieldDTO axis1 = view.getYAxis().get(0);
            ChartViewFieldDTO axis2 = view.getYAxisExt().get(0);

            if (StringUtils.equalsIgnoreCase(axis1.getGroupType(), "q") && StringUtils.equalsIgnoreCase(axis2.getGroupType(), "q")) {
                yAxis.add(axis1);
                yAxis.add(axis2);
            } else if (StringUtils.equalsIgnoreCase(axis1.getGroupType(), "d") && axis1.getDeType() == 1 && StringUtils.equalsIgnoreCase(axis2.getGroupType(), "d") && axis2.getDeType() == 1) {
                barRangeDate = true;
                if (BooleanUtils.isTrue(view.getAggregate())) {
                    axis1.setSummary("min");
                    axis2.setSummary("max");
                    yAxis.add(axis1);
                    yAxis.add(axis2);
                } else {
                    xAxis.add(axis1);
                    xAxis.add(axis2);
                }
            } else {
                skipBarRange = true;
            }
        } else {
            skipBarRange = true;
        }
        result.getContext().put("skipBarRange", skipBarRange);
        result.getContext().put("barRangeDate", barRangeDate);
        result.getAxisMap().put(ChartAxis.xAxis, xAxis);
        result.getAxisMap().put(ChartAxis.yAxis, yAxis);
        result.getContext().put("xAxisBase", xAxisBase);
        return result;
    }

    /**
     * 构建标准图表结果
     * 将查询数据转换为范围条形图可用的数据格式
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param data 查询返回的原始数据
     * @return 格式化后的范围条形图数据
     */
    @Override
    public Map<String, Object> buildNormalResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, List<String[]> data) {
        boolean isDrill = filterResult
                .getFilterList()
                .stream()
                .anyMatch(ele -> ele.getFilterType() == 1);
        var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
        var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
        var xAxisBase = (List<ChartViewFieldDTO>) formatResult.getContext().get("xAxisBase");
        var skipBarRange = (boolean) formatResult.getContext().get("skipBarRange");
        var barRangeDate = (boolean) formatResult.getContext().get("barRangeDate");
        Map<String, Object> result = ChartDataBuild.transBarRangeDataAntV(skipBarRange, barRangeDate, xAxisBase, xAxis, yAxis, view, data, isDrill);
        return result;
    }

    /**
     * 计算图表数据结果
     * 设置图表类型为范围条形图并执行数据计算
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param sqlMap SQL相关映射（包含数据源信息）
     * @param sqlMeta SQL元数据对象
     * @param provider 数据源提供者
     * @return 图表计算结果
     */
    @Override
    public <T extends ChartCalcDataResult> T calcChartResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, Map<String, Object> sqlMap, SQLMeta sqlMeta, Provider provider) {
        sqlMeta.setChartType(this.type);
        return super.calcChartResult(view, formatResult, filterResult, sqlMap, sqlMeta, provider);
    }
}
