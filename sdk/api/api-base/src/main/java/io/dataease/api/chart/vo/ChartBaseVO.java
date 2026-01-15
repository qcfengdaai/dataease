package io.dataease.api.chart.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.dataease.extensions.view.dto.ChartViewFieldDTO;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 图表基本信息视图对象
 * <p>
 * 封装图表的基本标识信息和字段配置。用于图表编辑器的头部信息展示
 * 和图表结构的数据传输。支持多种图表类型的字段配置。
 * </p>
 *
 * @author DataEase团队
 * @since 1.0.0
 */
@Data
public class ChartBaseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -2445689467486374464L;

    /**
     * 图表ID
     * <p>
     * 图表的唯一标识符，系统内部使用。
     * </p>
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chartId;

    /**
     * 图表类型
     * <p>
     * 图表的类型标识（如bar、line、pie等）。
     * </p>
     */
    private String chartType;

    /**
     * 图表名称
     * <p>
     * 用户定义的图表显示名称。
     * </p>
     */
    private String chartName;

    /**
     * 资源ID
     * <p>
     * 图表所属的资源（如仪表板）的ID。
     * </p>
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resourceId;

    /**
     * 资源类型
     * <p>
     * 图表所属资源的类型标识。
     * </p>
     */
    private String resourceType;

    /**
     * 资源名称
     * <p>
     * 图表所属资源的显示名称。
     * </p>
     */
    private String resourceName;

    /**
     * 数据表ID
     * <p>
     * 图表数据来源的数据表ID。
     * </p>
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tableId;

    /**
     * X轴（横轴）字段配置
     * <p>
     * 图表X轴使用的字段列表，通常为维度字段。
     * </p>
     */
    @JsonProperty("xAxis")
    private List<ChartViewFieldDTO> xAxis;

    /**
     * X轴扩展字段配置
     * <p>
     * X轴的扩展字段列表，用于复杂图表类型的维度扩展。
     * </p>
     */
    @JsonProperty("xAxisExt")
    private List<ChartViewFieldDTO> xAxisExt;

    /**
     * Y轴（纵轴）字段配置
     * <p>
     * 图表Y轴使用的字段列表，通常为指标字段。
     * </p>
     */
    @JsonProperty("yAxis")
    private List<ChartViewFieldDTO> yAxis;

    /**
     * Y轴副轴字段配置
     * <p>
     * 双 Y轴图表中的副轴字段列表，用于显示不同量纲的数据。
     * </p>
     */
    @JsonProperty("yAxisExt")
    private List<ChartViewFieldDTO> yAxisExt;

    /**
     * 堆叠字段配置
     * <p>
     * 用于堆叠图表的分组字段，控制数据的堆叠展示。
     * </p>
     */
    private List<ChartViewFieldDTO> extStack;

    /**
     * 气泡图大小字段配置
     * <p>
     * 气泡图中控制气泡大小的字段列表。
     * </p>
     */
    private List<ChartViewFieldDTO> extBubble;

    /**
     * 流向图起始名称字段
     * <p>
     * 流向图中表示起始节点名称的字段。
     * </p>
     */
    private List<ChartViewFieldDTO> flowMapStartName;

    /**
     * 流向图终止名称字段
     * <p>
     * 流向图中表示终止节点名称的字段。
     * </p>
     */
    private List<ChartViewFieldDTO> flowMapEndName;

    /**
     * 颜色字段配置
     * <p>
     * 用于控制图表颜色分组的字段列表。
     * </p>
     */
    private List<ChartViewFieldDTO> extColor;

    /**
     * 标签字段配置
     * <p>
     * 用于在图表中显示数据标签的字段列表。
     * </p>
     */
    private List<ChartViewFieldDTO> extLabel;

    /**
     * 提示信息字段配置
     * <p>
     * 用于图表悬浮提示信息显示的字段列表。
     * </p>
     */
    private List<ChartViewFieldDTO> extTooltip;

}
