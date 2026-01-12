package io.dataease.api.ds.vo;

import lombok.Data;

import java.util.List;

/**
 * Excel文件数据对象
 * <p>
 * 用于封装Excel文件的解析结果和元数据信息。
 * 包含文件的基本信息和所有工作表的数据结构。
 * </p>
 *
 * @author DataEase团队
 * @since 1.0.0
 */
@Data
public class ExcelFileData {
    /**
     * 文件ID
     * <p>
     * 上传文件的唯一标识符，用于文件管理和引用。
     * </p>
     */
    private String id;

    /**
     * Excel文件名称
     * <p>
     * 上传的Excel文件的名称或标签。
     * </p>
     */
    private String excelLabel;

    /**
     * 工作表数据列表
     * <p>
     * Excel文件中所有工作表的数据解析结果。
     * 每个元素包含一个工作表的完整信息。
     * </p>
     */
    private List<ExcelSheetData> sheets;

    /**
     * 文件存储路径
     * <p>
     * Excel文件在服务器上的存储路径。
     * </p>
     */
    private String path;

    /**
     * 是否为单个工作表
     * <p>
     * 标识该数据对象是否只包含单个工作表的数据。
     * true表示只包含一个工作表，false表示包含多个工作表。
     * </p>
     */
    private boolean isSheet = false;
}
