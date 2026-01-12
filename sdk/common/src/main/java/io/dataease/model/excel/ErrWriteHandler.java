package io.dataease.model.excel;

import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.util.List;
import java.util.Map;

/**
 * Excel错误信息写入处理器
 * 基于EasyExcel的行写入处理器，用于在Excel单元格中添加错误信息批注和样式
 *
 * 主要功能：
 * 1. 为包含错误的单元格添加红色背景
 * 2. 为错误单元格添加批注，显示具体错误信息
 * 3. 在数据导出时自动标记问题数据
 *
 * 使用场景：
 * - 数据验证失败时的错误标记
 * - 导入数据时的异常信息展示
 * - 批量数据处理的错误报告
 */
public class ErrWriteHandler implements RowWriteHandler {

    /**
     * 错误信息列表
     * 每一行对应一个Map，Map的key是列索引，value是错误信息
     */
    private List<Map<Integer, String>> errMsgList;

    /**
     * 构造函数
     * @param errMsgList 错误信息列表，包含每行每列的错误信息
     */
    public ErrWriteHandler(List<Map<Integer, String>> errMsgList) {
        this.errMsgList = errMsgList;
    }

    /**
     * 行写入完成后的处理方法
     * 为包含错误信息的单元格添加批注和样式
     *
     * @param writeSheetHolder 工作表持有者
     * @param writeTableHolder 表格持有者
     * @param row 当前行对象
     * @param relativeRowIndex 相对行索引（从0开始，不包含表头）
     * @param isHead 是否为表头行
     */
    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row,
                                Integer relativeRowIndex, Boolean isHead) {
        // 只处理数据行，不处理表头行
        if (!isHead.booleanValue()) {
            Sheet sheet = writeSheetHolder.getSheet();
            // 获取当前行的错误信息映射
            Map<Integer, String> rowErrMap = errMsgList.get(relativeRowIndex.intValue());
            // 为每个有错误的单元格设置批注和样式
            for (Map.Entry<Integer, String> cellMap : rowErrMap.entrySet()) {
                setPostil(sheet, relativeRowIndex, cellMap.getKey(), cellMap.getValue());
            }
        }
    }

    /**
     * 为指定单元格设置错误批注和红色背景样式
     *
     * @param sheet 工作表对象
     * @param relativeRowIndex 相对行索引
     * @param columnIndex 列索引
     * @param msg 错误信息
     */
    private void setPostil(Sheet sheet, Integer relativeRowIndex, Integer columnIndex, String msg) {
        Workbook workbook = sheet.getWorkbook();

        // 创建红色背景的单元格样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 创建绘图对象用于添加批注
        Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
        XSSFClientAnchor xssfClientAnchor = new XSSFClientAnchor(0, 0, 0, 0, 0, 0, 2, 2);
        Comment comment = drawingPatriarch.createCellComment(xssfClientAnchor);
        comment.setString(new XSSFRichTextString(msg));

        // 获取单元格并应用样式和批注
        Cell cell = sheet.getRow(relativeRowIndex + 1).getCell(columnIndex.intValue());
        cell.setCellComment(comment);
        cell.setCellStyle(cellStyle);
    }

    /**
     * 行创建前的处理方法
     * 当前实现为空，可根据需要进行扩展
     */
    @Override
    public void beforeRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                               Integer rowIndex, Integer relativeRowIndex, Boolean isHead) {
        // 暂无处理逻辑
    }

    /**
     * 行创建后的处理方法
     * 当前实现为空，可根据需要进行扩展
     */
    @Override
    public void afterRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                              Row row, Integer relativeRowIndex, Boolean isHead) {
        // 暂无处理逻辑
    }
}
