package io.dataease.chart.charts.impl.map;

import io.dataease.chart.charts.impl.GroupChartHandler;
import io.dataease.extensions.view.dto.AxisFormatResult;
import io.dataease.extensions.view.dto.ChartAxis;
import io.dataease.extensions.view.dto.ChartViewDTO;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class FlowMapHandler extends GroupChartHandler {
    @Getter
    private String type = "flow-map";
    /**
     * 格式化坐标轴
     * 处理流向图的坐标轴配置，包括起始位置和结束位置字段
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果，包含起始位置和结束位置字段
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var result = super.formatAxis(view);
        var xAxis = result.getAxisMap().get(ChartAxis.xAxis);
        xAxis.addAll(Optional.ofNullable(view.getFlowMapStartName()).orElse(new ArrayList<>()));
        xAxis.addAll(Optional.ofNullable(view.getFlowMapEndName()).orElse(new ArrayList<>()));
        result.getAxisMap().put(ChartAxis.xAxis, xAxis);
        return result;
    }
}
