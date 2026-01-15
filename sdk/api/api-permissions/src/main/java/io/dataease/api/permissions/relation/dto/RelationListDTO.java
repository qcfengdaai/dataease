package io.dataease.api.permissions.relation.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 关系列表数据传输对象
 * <p>
 * 用于展示多种业务资源的关联关系列表。
 * 整合了数据源、数据集、仪表板、数据可视化等资源的基本信息。
 * <p>
 * 主要用途：
 * <ul>
 *   <li>在关系列表中同时展示多种类型的资源</li>
 *   <li>权限管理界面中展示用户/角色关联的资源清单</li>
 *   <li>资源使用情况统计和报表</li>
 *   <li>跨资源类型的关联查询结果</li>
 * </ul>
 * <p>
 * 设计说明：
 * 此 DTO 采用扁平化设计，包含所有可能的资源类型字段。
 * 根据 type 字段确定具体的资源类型，只有对应类型的字段才有值。
 *
 * @author Junjun
 * @since 1.0
 */
@Data
public class RelationListDTO implements Serializable {

    // ==================== 数据源相关字段 ====================

    /**
     * 数据源 ID
     * <p>
     * 数据源的唯一标识。
     * 当 type = "datasource" 时，此字段有值。
     */
    private Long dsId;

    /**
     * 数据源名称
     * <p>
     * 数据源的显示名称。
     * 当 type = "datasource" 时，此字段有值。
     */
    private String dsName;

    /**
     * 数据源创建者
     * <p>
     * 创建该数据源的用户标识。
     * 当 type = "datasource" 时，此字段有值。
     */
    private String dsCreator;

    /**
     * 数据源更新时间
     * <p>
     * 数据源最后更新的时间戳（毫秒）。
     * 当 type = "datasource" 时，此字段有值。
     */
    private Long dsUpdateTime;

    // ==================== 数据集相关字段 ====================

    /**
     * 数据集 ID
     * <p>
     * 数据集的唯一标识。
     * 当 type = "dataset" 时，此字段有值。
     */
    private Long datasetId;

    /**
     * 数据集名称
     * <p>
     * 数据集的显示名称。
     * 当 type = "dataset" 时，此字段有值。
     */
    private String datasetName;

    /**
     * 数据集创建者
     * <p>
     * 创建该数据集的用户标识。
     * 当 type = "dataset" 时，此字段有值。
     */
    private String datasetCreator;

    /**
     * 数据集更新时间
     * <p>
     * 数据集最后更新的时间戳（毫秒）。
     * 当 type = "dataset" 时，此字段有值。
     */
    private Long datasetUpdateTime;

    // ==================== 仪表板相关字段 ====================

    /**
     * 仪表板 ID
     * <p>
     * 仪表板的唯一标识。
     * 当 type = "dashboard" 时，此字段有值。
     */
    private Long dashboardId;

    /**
     * 仪表板名称
     * <p>
     * 仪表板的显示名称。
     * 当 type = "dashboard" 时，此字段有值。
     */
    private String dashboardName;

    /**
     * 仪表板创建者
     * <p>
     * 创建该仪表板的用户标识。
     * 当 type = "dashboard" 时，此字段有值。
     */
    private String dashboardCreator;

    /**
     * 仪表板更新时间
     * <p>
     * 仪表板最后更新的时间戳（毫秒）。
     * 当 type = "dashboard" 时，此字段有值。
     */
    private Long dashboardUpdateTime;

    // ==================== 数据可视化相关字段 ====================

    /**
     * 数据可视化 ID
     * <p>
     * 数据可视化（DataViz）的唯一标识。
     * 当 type = "datavisualization" 或 "dv" 时，此字段有值。
     */
    private Long dvId;

    /**
     * 数据可视化名称
     * <p>
     * 数据可视化的显示名称。
     * 当 type = "datavisualization" 或 "dv" 时，此字段有值。
     */
    private String dvName;

    /**
     * 数据可视化创建者
     * <p>
     * 创建该数据可视化的用户标识。
     * 当 type = "datavisualization" 或 "dv" 时，此字段有值。
     */
    private String dvCreator;

    /**
     * 数据可视化更新时间
     * <p>
     * 数据可视化最后更新的时间戳（毫秒）。
     * 当 type = "datavisualization" 或 "dv" 时，此字段有值。
     */
    private Long dvUpdateTime;

    // ==================== 通用字段 ====================

    /**
     * 资源类型
     * <p>
     * 标识当前记录的资源类型，用于确定上述哪组字段有效。
     * <p>
     * 可能的值：
     * <ul>
     *   <li>"datasource" 或 "ds"：数据源</li>
     *   <li>"dataset"：数据集</li>
     *   <li>"dashboard"：仪表板</li>
     *   <li>"datavisualization" 或 "dv"：数据可视化</li>
     * </ul>
     * <p>
     * 根据此字段判断应该读取哪组 ID、名称、创建者、更新时间字段。
     */
    private String type;
}
