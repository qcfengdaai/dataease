package io.dataease.chart.dao.ext.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 图表基础持久化对象
 * 用于扩展查询中的图表基础信息传输
 * 包含图表的核心属性和各维度配置信息
 *
 * <p>主要属性：</p>
 * <ul>
 *   <li>图表基本信息（ID、类型、名称）</li>
 *   <li>资源关联信息（资源ID、类型、名称）</li>
 *   <li>坐标轴配置（X轴、Y轴及其扩展）</li>
 *   <li>显示样式配置（颜色、标签、提示等）</li>
 * </ul>
 *
 * @author Junjun
 */
@Data
public class ChartBasePO implements Serializable {
    @Serial
    private static final long serialVersionUID = 183064537525500481L;

    /**
     * 图表ID
     * 唯一标识图表视图
     */
    private Long chartId;

    /**
     * 图表类型
     * 标识图表的具体类型（柱状图、折线图等）
     */
    private String chartType;

    /**
     * 图表名称
     * 图表的显示标题
     */
    private String chartName;

    /**
     * 资源ID
     * 关联的资源标识符（数据集或仪表板）
     */
    private Long resourceId;

    /**
     * 资源类型
     * 资源的类型标识
     */
    private String resourceType;

    /**
     * 资源名称
     * 资源的显示名称
     */
    private String resourceName;

    /**
     * 数据表ID
     * 关联的数据表标识符
     */
    private Long tableId;

    /**
     * X轴配置
     * X轴维度字段的配置信息
     */
    private String xAxis;

    /**
     * X轴扩展配置
     * X轴的额外配置属性
     */
    private String xAxisExt;

    /**
     * Y轴配置
     * Y轴度量字段的配置信息
     */
    private String yAxis;

    /**
     * Y轴扩展配置
     * Y轴的额外配置属性
     */
    private String yAxisExt;

    /**
     * 堆叠扩展配置
     * 用于堆叠图表的分组配置
     */
    private String extStack;

    /**
     * 气泡扩展配置
     * 用于气泡图的气泡大小配置
     */
    private String extBubble;

    /**
     * 流向图起始名称
     * 流向图中起始节点的名称字段
     */
    private String flowMapStartName;

    /**
     * 流向图结束名称
     * 流向图中结束节点的名称字段
     */
    private String flowMapEndName;

    /**
     * 颜色扩展配置
     * 图表颜色分组的配置信息
     */
    private String extColor;

    /**
     * 标签扩展配置
     * 图表标签显示的配置信息
     */
    private String extLabel;

    /**
     * 提示扩展配置
     * 图表提示框的配置信息
     */
    private String extTooltip;
}
