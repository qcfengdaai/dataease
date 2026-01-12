package io.dataease.extensions.view.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 图表视图DTO
 * 用于表示完整的图表视图信息，继承自ChartViewBaseDTO
 *
 * @Author gin
 */
@Data
public class ChartViewDTO extends ChartViewBaseDTO {
    /**
     * 图表数据内容
     */
    private Map<String, Object> data;

    /**
     * 用户权限信息
     */
    private String privileges;

    /**
     * 是否为叶子节点
     */
    private Boolean isLeaf;

    /**
     * 父级ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long pid;

    /**
     * 查询SQL语句
     */
    private String sql;

    /**
     * 是否支持钻取功能
     */
    private boolean drill;

    /**
     * 钻取过滤条件列表
     */
    private List<ChartExtFilterDTO> drillFilters;

    /**
     * 图表位置信息
     */
    private String position;

    /**
     * 总页数
     */
    private long totalPage;

    /**
     * 总记录数
     */
    private long totalItems;

    /**
     * 数据集模式
     */
    private int datasetMode;

    /**
     * 数据源类型
     */
    private String datasourceType;

    /**
     * 图表扩展请求对象
     */
    private ChartExtRequest chartExtRequest;

    /**
     * 是否为Excel导出
     */
    private Boolean isExcelExport = false;

    /**
     * 是否导出数据集原始数据
     */
    private Boolean exportDatasetOriginData = false;

    /**
     * 是否启用缓存
     */
    private boolean cache;

    /**
     * 原始数据集表ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sourceTableId;

    /**
     * 数据下载模式，dataset指原始数据
     */
    private String downloadType;
}
