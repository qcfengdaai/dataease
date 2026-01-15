package io.dataease.chart.charts.impl.numeric;

import io.dataease.extensions.view.dto.AxisFormatResult;
import io.dataease.extensions.view.dto.ChartAxis;
import io.dataease.extensions.view.dto.ChartViewDTO;
import io.dataease.extensions.view.dto.ChartViewFieldDTO;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LiquidHandler extends NumericalChartHandler {
    @Getter
    private String type = "liquid";

    /**
     * 格式化坐标轴
     * 处理水波图的坐标轴配置，包括动态的最大值字段
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果，包含动态字段配置
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var axisMap = new HashMap<ChartAxis, List<ChartViewFieldDTO>>();
        var yAxis = new ArrayList<>(view.getYAxis());
        Map<String, Object> customAttr = view.getCustomAttr();
        Map<String, Object> misc = (Map<String, Object>) customAttr.get("misc");
        ChartViewFieldDTO liquidMaxViewField = getDynamicField(misc, "liquidMaxType", "liquidMaxField");
        if (liquidMaxViewField != null) {
            yAxis.add(liquidMaxViewField);
        }
        axisMap.put(ChartAxis.xAxis, new ArrayList<>());
        axisMap.put(ChartAxis.yAxis, yAxis);
        var context = new HashMap<String, Object>();
        var result = new AxisFormatResult(axisMap, context);
        return result;
    }
}
