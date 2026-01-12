package io.dataease.chart.charts.impl.bar;

import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * 双向柱状图处理器
 * 负责双向柱状图（条形图）的数据处理和渲染
 * 继承ProgressBarHandler，用于展示正负值的对比
 *
 * <p>支持的图表类型：</p>
 * <ul>
 *   <li>双向柱状图 (bidirectional-bar)</li>
 * </ul>
 *
 * @author DataEase Team
 */
@Component
public class BidirectionalBarHandler extends ProgressBarHandler {
    @Getter
    private String type = "bidirectional-bar";
}
