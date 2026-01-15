package io.dataease.visualization.utils;

import io.dataease.constant.DeTypeConstants;
import io.dataease.utils.ConfigUtils;
import io.dataease.utils.FileUtils;
import io.dataease.utils.LogUtil;
import io.dataease.utils.ModelUtils;
import io.dataease.visualization.bo.ExcelSheetModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 可视化Excel导出工具类
 * 提供将可视化数据导出为Excel文件的功能
 */
public class VisualizationExcelUtils {

    /** Excel文件后缀 */
    private static final String suffix = ".xlsx";
    /** 基础路径 */
    private static final String BASE_ROOT = getBaseRoot();

    /**
     * 获取基础路径
     * @return 基础路径
     */
    public static String getBaseRoot() {
        if (ModelUtils.isDesktop()) {
            return ConfigUtils.getConfig("dataease.path.report", "/opt/dataease2.0/data/report/");
        } else {
            return "/opt/dataease2.0/data/report/";
        }
    }

    /**
     * 导出Excel文件
     * @param sheets 工作表数据列表
     * @param fileName 文件名
     * @param folderId 文件夹ID
     * @return 导出的文件
     * @throws Exception 导出异常
     */
    public static File exportExcel(List<ExcelSheetModel> sheets, String fileName, String folderId) throws Exception {
        // 原子引用保证线程安全
        AtomicReference<String> realFileName = new AtomicReference<>(fileName);
        // 创建工作簿(使用流式处理大数据量)
        Workbook wb = new SXSSFWorkbook();

        // 遍历所有工作表
        sheets.forEach(sheet -> {

            List<List<String>> details = sheet.getData();
            List<Integer> fieldTypes = sheet.getFiledTypes();
            // 将表头添加到数据第一行
            details.add(0, sheet.getHeads());
            String sheetName = sheet.getSheetName();
            // 替换工作表名称中的非法字符
            Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
            Matcher matcher = pattern.matcher(sheetName);
            sheetName = matcher.replaceAll("-");
            Sheet curSheet = wb.createSheet(sheetName);
            // 如果文件名为空,使用第一个工作表名称
            if (StringUtils.isBlank(fileName)) {
                String cName = sheetName + suffix;
                realFileName.set(cName);
            }

            // 创建表头样式
            CellStyle cellStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setBold(true);
            cellStyle.setFont(font);
            cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 填充数据
            if (CollectionUtils.isNotEmpty(details)) {
                for (int i = 0; i < details.size(); i++) {
                    Row row = curSheet.createRow(i);
                    List<String> rowData = details.get(i);
                    if (rowData != null) {
                        for (int j = 0; j < rowData.size(); j++) {
                            Cell cell = row.createCell(j);
                            // 根据字段类型设置单元格值类型
                            if (i > 0 && (fieldTypes.get(j).equals(DeTypeConstants.DE_INT) || fieldTypes.get(j).equals(DeTypeConstants.DE_FLOAT)) && StringUtils.isNotEmpty(rowData.get(j))) {
                                // 数值类型
                                cell.setCellValue(Double.valueOf(rowData.get(j)));
                            } else {
                                // 文本类型
                                cell.setCellValue(rowData.get(j));
                            }
                            if (i == 0) {// 表头行
                                cell.setCellStyle(cellStyle);
                                // 设置列的宽度
                                curSheet.setColumnWidth(j, 255 * 20);
                            }
                        }
                    }
                }
            }
        });
        // 确保文件名有正确的后缀
        if (!StringUtils.endsWith(fileName, suffix)) {
            realFileName.set(realFileName.get() + suffix);
        }
        // 构建文件夹路径
        String folderPath = BASE_ROOT;
        if (StringUtils.isNotBlank(folderId)) {
            folderPath = BASE_ROOT + folderId + "/";
        }

        // 使用线程ID作为子文件夹名避免冲突
        folderPath += Thread.currentThread().getId() + "/";
        FileUtils.validateExist(folderPath);
        File result = new File(folderPath + realFileName.get());
        // 写入文件
        FileOutputStream fos = new FileOutputStream(result);
        BufferedOutputStream outputStream = new BufferedOutputStream(fos);
        try {
            wb.write(outputStream);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), new Throwable(e));
            throw e;
        } finally {
            wb.close();
            outputStream.flush();
            outputStream.close();
        }
        return result;
    }
}
