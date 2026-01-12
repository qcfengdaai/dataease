package io.dataease.model.excel;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import io.dataease.i18n.Translator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel列宽自适应样式策略
 * 基于EasyExcel的列宽样式策略，用于根据单元格内容自动调整列宽
 *
 * 主要功能：
 * 1. 根据单元格内容长度自动调整列宽
 * 2. 支持中文字符的宽度计算
 * 3. 对表头进行国际化处理
 * 4. 缓存列宽信息，提高性能
 *
 * 特点：
 * - 支持多种数据类型的宽度计算（字符串、数字、布尔值）
 * - 设置最大列宽限制，防止列宽过大
 * - 集成国际化功能，自动翻译表头
 */
public class AutoAdaptWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {

    /**
     * 列宽缓存
     * 外层Map的key是工作表编号，内层Map的key是列索引，value是该列的最大宽度
     */
    private Map<Integer, Map<Integer, Integer>> CACHE = new HashMap(8);

    /**
     * 默认构造函数
     */
    public AutoAdaptWidthStyleStrategy() {
    }

    /**
     * 设置列宽的核心方法
     * 根据单元格内容计算合适的列宽并应用
     *
     * @param writeSheetHolder 工作表持有者
     * @param cellDataList 单元格数据列表
     * @param cell 当前单元格
     * @param head 表头信息
     * @param relativeRowIndex 相对行索引
     * @param isHead 是否为表头行
     */
    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList,
                                 Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        // 判断是否需要设置列宽（表头行或有数据的单元格）
        boolean needSetWidth = isHead || !CollectionUtils.isEmpty(cellDataList);
        if (needSetWidth) {
            // 获取或创建当前工作表的列宽缓存
            Map<Integer, Integer> maxColumnWidthMap = CACHE.get(writeSheetHolder.getSheetNo());
            if (maxColumnWidthMap == null) {
                maxColumnWidthMap = new HashMap(16);
                CACHE.put(writeSheetHolder.getSheetNo(), maxColumnWidthMap);
            }

            // 计算当前单元格内容的宽度
            Integer columnWidth = this.dataLength(cellDataList, cell, isHead);
            if (columnWidth >= 0) {
                // 限制最大列宽为255（Excel限制）
                if (columnWidth > 255) {
                    columnWidth = 255;
                }

                // 获取当前列的最大宽度
                Integer maxColumnWidth = (Integer) ((Map) maxColumnWidthMap).get(cell.getColumnIndex());
                // 如果当前宽度大于已记录的最大宽度，则更新列宽
                if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
                    ((Map) maxColumnWidthMap).put(cell.getColumnIndex(), columnWidth);
                    // 设置Excel列宽（7250是一个固定宽度值，可根据需要调整）
                    writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), 7250);
                }
            }
        }
    }

    /**
     * 计算单元格内容的字节长度
     * 根据不同数据类型计算合适的显示宽度
     *
     * @param cellDataList 单元格数据列表
     * @param cell 当前单元格
     * @param isHead 是否为表头
     * @return 内容的字节长度，-1表示无法计算
     */
    private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead) {
        if (isHead) {
            // 表头直接使用字符串值的字节长度
            return cell.getStringCellValue().getBytes().length;
        } else {
            // 数据行根据不同类型计算长度
            CellData cellData = cellDataList.get(0);
            CellDataTypeEnum type = cellData.getType();
            if (type == null) {
                return -1;
            } else {
                switch (type) {
                    case STRING:
                        return cellData.getStringValue().getBytes().length;
                    case BOOLEAN:
                        return cellData.getBooleanValue().toString().getBytes().length;
                    case NUMBER:
                        return cellData.getNumberValue().toString().getBytes().length;
                    default:
                        return -1;
                }
            }
        }
    }

    /**
     * 单元格创建前的处理方法
     * 主要用于对表头进行国际化处理
     *
     * @param context 单元格写入上下文
     */
    @Override
    public void beforeCellCreate(CellWriteHandlerContext context) {
        // 只处理表头行
        if (!context.getHead()) return;

        Head headData = context.getHeadData();
        List<String> headNameList = null;

        // 如果表头数据不为空且包含名称列表，则进行国际化处理
        if (ObjectUtils.isNotEmpty(headData) &&
            CollectionUtils.isNotEmpty(headNameList = headData.getHeadNameList())) {
            // 使用国际化翻译器翻译表头名称
            List<String> i18nNameList = headNameList.stream()
                    .map(Translator::get)
                    .collect(Collectors.toList());
            // 设置翻译后的表头名称
            context.getHeadData().setHeadNameList(i18nNameList);
        }
    }
}
