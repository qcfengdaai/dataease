package io.dataease.extensions.view.dto;

/**
 * 图表轴类型枚举
 * 定义图表中各种轴和扩展字段的类型
 */
public enum ChartAxis {
    /**
     * X轴（横轴）
     */
    xAxis,

    /**
     * X轴扩展
     */
    xAxisExt,

    /**
     * 堆叠项
     */
    extStack,

    /**
     * 扩展标签
     */
    extLabel,

    /**
     * 扩展提示信息
     */
    extTooltip,

    /**
     * Y轴（纵轴）
     */
    yAxis,

    /**
     * Y轴扩展
     */
    yAxisExt,

    /**
     * 钻取字段
     */
    drill,

    /**
     * 扩展颜色
     */
    extColor,

    /**
     * 扩展气泡大小
     */
    extBubble;
}
