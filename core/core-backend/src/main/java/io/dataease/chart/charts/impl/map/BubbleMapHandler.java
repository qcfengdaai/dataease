package io.dataease.chart.charts.impl.map;

import io.dataease.chart.charts.impl.ExtQuotaChartHandler;
import io.dataease.extensions.view.dto.AxisFormatResult;
import io.dataease.extensions.view.dto.ChartViewDTO;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class BubbleMapHandler extends ExtQuotaChartHandler {
    @Getter
    private String type = "bubble-map";

    /**
     * 格式化坐标轴
     * 使用父类的坐标轴格式化逻辑
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        return super.formatAxis(view);
    }
}


