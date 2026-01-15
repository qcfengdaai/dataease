package io.dataease.extensions.view.dto;

import io.dataease.extensions.datasource.dto.DatasetTableFieldDTO;
import lombok.Data;

import java.util.List;

/**
 * 图表字段自定义过滤DTO
 * 用于定义图表字段的自定义过滤条件
 *
 * @Author Junjun
 */
@Data
public class ChartFieldCustomFilterDTO extends ChartViewFieldBaseDTO {
    /** 过滤项列表 */
    private List<ChartCustomFilterItemDTO> filter;

    /** 数据集表字段信息 */
    private DatasetTableFieldDTO field;

    /** 枚举检查字段列表 */
    private List<String> enumCheckField;
}
