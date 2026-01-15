package io.dataease.visualization.bo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Excel工作表模型
 * 用于导出Excel时定义工作表的结构和数据
 */
@Data
public class ExcelSheetModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1122095875367371623L;

    /** 工作表名称 */
    private String sheetName;

    /** 表头列表 */
    private List<String> heads;

    /** 数据行列表 */
    private List<List<String>> data;

    /** 字段类型列表 */
    private List<Integer> filedTypes;
}
