package io.dataease.extensions.view.dto;


import lombok.Data;

import java.util.List;

/**
 * 图表扩展请求DTO
 * 用于封装图表查询时的各种扩展条件和参数
 *
 * @Author gin
 */
@Data
public class ChartExtRequest {
    /** 过滤条件列表 */
    private List<ChartExtFilterDTO> filter;

    /** 联动过滤条件列表 */
    private List<ChartExtFilterDTO> linkageFilters;

    /** 外部参数过滤条件列表 */
    private List<ChartExtFilterDTO> outerParamsFilters;

    /** Web参数过滤条件列表 */
    private List<ChartExtFilterDTO> webParamsFilters;

    /** 钻取请求列表 */
    private List<ChartDrillRequest> drill;

    /** 查询来源 */
    private String queryFrom;

    /** 结果模式 */
    private String resultMode;

    /** 结果数量 */
    private Integer resultCount;

    /** 是否使用缓存 */
    private boolean cache = true;

    /** 用户ID */
    private Long user = null;

    /** 跳转页码 */
    private Long goPage;

    /** 每页大小 */
    private Long pageSize;

    /** 是否导出Excel标志 */
    private Boolean excelExportFlag = false;

}
