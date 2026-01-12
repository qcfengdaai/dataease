package io.dataease.chart.server;

import com.fasterxml.jackson.core.type.TypeReference;
import io.dataease.api.chart.ChartDataApi;
import io.dataease.api.chart.dto.ViewDetailField;
import io.dataease.api.chart.request.ChartExcelRequest;
import io.dataease.api.chart.request.ChartExcelRequestInner;
import io.dataease.auth.DeLinkPermit;
import io.dataease.chart.constant.ChartConstants;
import io.dataease.chart.manage.ChartDataManage;
import io.dataease.constant.*;
import io.dataease.dataset.manage.PermissionManage;
import io.dataease.dataset.server.DatasetFieldServer;
import io.dataease.dataset.utils.DatasetUtils;
import io.dataease.exception.DEException;
import io.dataease.exportCenter.dao.auto.entity.CoreExportTask;
import io.dataease.exportCenter.manage.ExportCenterDownLoadManage;
import io.dataease.exportCenter.manage.ExportCenterManage;
import io.dataease.exportCenter.util.ExportCenterUtils;
import io.dataease.extensions.datasource.dto.DatasetTableFieldDTO;
import io.dataease.extensions.view.dto.*;
import io.dataease.i18n.Lang;
import io.dataease.license.manage.F2CLicLimitedManage;
import io.dataease.log.DeLog;
import io.dataease.result.ResultCode;
import io.dataease.utils.CommonBeanFactory;
import io.dataease.utils.JsonUtil;
import io.dataease.utils.LogUtil;
import io.dataease.visualization.manage.VisualizationTemplateExtendDataManage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 图表数据服务控制器
 * 提供图表数据查询、计算、导出等API接口
 * 实现ChartDataApi接口，作为图表数据操作的HTTP入口
 *
 * <p>主要接口：</p>
 * <ul>
 *   <li>图表数据查询: getData</li>
 *   <li>图表数据导出: exportExcel, exportDetails</li>
 *   <li>字段枚举值: getFieldEnum</li>
 *   <li>数据详情查询: getDataFromDetail</li>
 * </ul>
 *
 * @author Junjun
 */
@RestController
@RequestMapping("/chartData")
public class ChartDataServer implements ChartDataApi {
    @Resource
    private ChartDataManage chartDataManage;
    @Resource
    private ExportCenterManage exportCenterManage;

    @Resource
    private VisualizationTemplateExtendDataManage extendDataManage;

    @Resource
    private PermissionManage permissionManage;
    @Resource
    private DatasetFieldServer datasetFieldServer;

    @Resource(name = "f2CLicLimitedManage")
    private F2CLicLimitedManage f2CLicLimitedManage;
    @Value("${dataease.export.page.size:50000}")
    private Integer extractPageSize;
    private final Long sheetLimit = 1000000L;


    @DeLinkPermit("#p0.sceneId")
    /**
     * 获取图表数据
     * 根据图表视图信息查询并计算图表数据
     *
     * @param chartViewDTO 图表视图信息，包含图表ID、字段配置、过滤条件等
     * @return 计算后的图表视图，包含数据、字段等信息
     * @throws Exception 数据计算异常
     */
    @Override
    public ChartViewDTO getData(ChartViewDTO chartViewDTO) throws Exception {
        try {
            // 从模板数据获取
            if (CommonConstants.VIEW_DATA_FROM.TEMPLATE.equalsIgnoreCase(chartViewDTO.getDataFrom())) {
                return extendDataManage.getChartDataInfo(chartViewDTO.getId(), chartViewDTO);
            } else {
                DatasetUtils.viewDecode(chartViewDTO);
                ChartViewDTO dto = chartDataManage.calcData(chartViewDTO);
                DatasetUtils.viewEncode(dto);
                chartDataManage.encodeData(dto);
                return dto;
            }
        } catch (Exception e) {
            DEException.throwException(ResultCode.DATA_IS_WRONG.code(), e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * 查找Excel数据
     * 为Excel导出查询图表数据，支持数据集原始数据导出
     *
     * @param request Excel导出请求，包含图表信息和导出类型
     * @return 包含导出数据的图表视图信息
     */
    public ChartViewDTO findExcelData(ChartExcelRequest request) {
        ChartViewDTO chartViewInfo = new ChartViewDTO();
        try {
            ChartViewDTO viewDTO = request.getViewInfo();
            viewDTO.setIsExcelExport(true);
            String[] dsHeader = null;
            Integer[] dsTypes = null;
            //downloadType = dataset 为下载原始名字 这里做数据转换模拟 table-info类型图表导出
            if ("dataset".equals(request.getDownloadType())) {
                viewDTO.setExportDatasetOriginData(true);
                viewDTO.setResultMode(ChartConstants.VIEW_RESULT_MODE.ALL);
                viewDTO.setType("table-info");
                viewDTO.setRender("antv");
                List<DatasetTableFieldDTO> sourceFields = datasetFieldServer.listByDatasetGroup(viewDTO.getTableId());
                List<String> fileNames = permissionManage.filterColumnPermissions(sourceFields, new HashMap<>(), viewDTO.getTableId(), null).stream().map(DatasetTableFieldDTO::getDataeaseName).collect(Collectors.toList());
                sourceFields = sourceFields.stream().filter(datasetTableFieldDTO -> fileNames.contains(datasetTableFieldDTO.getDataeaseName())).collect(Collectors.toList());
                dsHeader = sourceFields.stream().map(DatasetTableFieldDTO::getName).toArray(String[]::new);
                dsTypes = sourceFields.stream().map(DatasetTableFieldDTO::getDeType).toArray(Integer[]::new);
                TypeReference<List<ChartViewFieldDTO>> listTypeReference = new TypeReference<List<ChartViewFieldDTO>>() {
                };
                viewDTO.setXAxis(JsonUtil.parseList(JsonUtil.toJSONString(sourceFields).toString(), listTypeReference));
                viewDTO.getXAxis().forEach(x -> {
                    if (x.getOrderChecked()) {
                        x.setSort("asc");
                    }
                });
            }
            int curLimit = Math.toIntExact(ExportCenterUtils.getExportLimit("view"));
            int curDsLimit = Math.toIntExact(ExportCenterUtils.getExportLimit("dataset"));
            int viewLimit = Math.min(curLimit, curDsLimit);
            if (ChartConstants.VIEW_RESULT_MODE.CUSTOM.equals(viewDTO.getResultMode())) {
                Integer limitCount = viewDTO.getResultCount();
                viewDTO.setResultCount(Math.min(viewLimit, limitCount));
            } else {
                viewDTO.setResultCount(viewLimit);
            }
            if (CommonConstants.VIEW_DATA_FROM.TEMPLATE.equalsIgnoreCase(viewDTO.getDataFrom())) {
                chartViewInfo = extendDataManage.getChartDataInfo(viewDTO.getId(), viewDTO);
            } else {
                // 要走明细表的逻辑
                viewDTO.setIsPlugin(false);
                chartViewInfo = chartDataManage.calcData(viewDTO);
            }
            List<Object[]> tableRow = (List) chartViewInfo.getData().get("sourceData");
            if ("dataset".equals(request.getDownloadType())) {
                request.setHeader(dsHeader);
                request.setExcelTypes(dsTypes);
            }
            request.setDetails(tableRow);
            request.setData(chartViewInfo.getData());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return chartViewInfo;
    }


    /**
     * 数值格式化
     * 根据格式化配置对数值进行格式化处理
     *
     * @param value 待格式化的数值
     * @param formatter 格式化配置，包含类型、单位、小数位数等
     * @return 格式化后的字符串
     */
    public static String valueFormatter(BigDecimal value, FormatterCfgDTO formatter) {
        if (value == null) {
            return null;
        }
        String result;
        if (formatter.getType().equals("auto")) {
            result = transSeparatorAndSuffix(String.valueOf(transUnit(value, formatter)), formatter);
        } else if (formatter.getType().equals("value")) {
            result = transSeparatorAndSuffix(transDecimal(transUnit(value, formatter), formatter), formatter);
        } else if (formatter.getType().equals("percent")) {
            value = value.multiply(BigDecimal.valueOf(100));
            result = transSeparatorAndSuffix(transDecimal(value, formatter), formatter);
        } else {
            result = value.toString();
        }
        return result;
    }

    /**
     * 单位转换
     * 根据配置的单位(千、万、百万、亿等)对数值进行除法运算
     *
     * @param value 原始数值
     * @param formatter 格式化配置对象，包含单位信息
     * @return 转换单位后的数值
     */
    private static BigDecimal transUnit(BigDecimal value, FormatterCfgDTO formatter) {
        return value.divide(BigDecimal.valueOf(formatter.getUnit()));
    }

    /**
     * 小数位数格式化
     * 根据配置的小数位数对数值进行格式化
     *
     * @param value 原始数值
     * @param formatter 格式化配置对象，包含小数位数配置
     * @return 格式化后的字符串，保留指定小数位数
     */
    private static String transDecimal(BigDecimal value, FormatterCfgDTO formatter) {
        DecimalFormat df = new DecimalFormat("0." + new String(new char[formatter.getDecimalCount()]).replace('\0', '0'));
        return df.format(value);
    }

    /**
     * 千分位分隔符和后缀处理
     * 为数值添加千分位分隔符，并根据单位添加中文后缀或自定义后缀
     *
     * @param value 数值字符串
     * @param formatter 格式化配置对象，包含千分位分隔符、单位、后缀等配置
     * @return 添加了千分位分隔符和后缀的字符串
     */
    private static String transSeparatorAndSuffix(String value, FormatterCfgDTO formatter) {
        StringBuilder sb = new StringBuilder(value);

        if (formatter.getThousandSeparator()) {
            Pattern thousandsPattern = Pattern.compile("(\\d)(?=(\\d{3})+$)");
            String[] numArr = value.split("\\.");
            numArr[0] = addThousandSeparator(numArr[0], thousandsPattern);
            sb = new StringBuilder(String.join(".", numArr));
        }
        if (formatter.getType().equals("percent")) {
            sb.append('%');
        } else {
            switch (formatter.getUnit()) {
                case 1000:
                    sb.append("千");
                    break;
                case 10000:
                    sb.append("万");
                    break;
                case 1000000:
                    sb.append("百万");
                    break;
                case 100000000:
                    sb.append('亿');
                    break;
                default:
                    break;
            }
        }
        String suffix = formatter.getSuffix().trim();
        if (!suffix.isEmpty()) {
            if (suffix.equals("%")) {
                sb.append("\"%\"");
            } else {
                sb.append(suffix);
            }
        }
        return sb.toString();
    }


    /**
     * 添加千分位分隔符
     * 使用正则表达式为数字字符串添加千分位逗号分隔符
     *
     * @param numStr 数字字符串（整数部分）
     * @param pattern 正则表达式模式，用于匹配每三位数字
     * @return 添加了千分位分隔符的字符串
     */
    private static String addThousandSeparator(String numStr, Pattern pattern) {
        Matcher matcher = pattern.matcher(numStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1) + ",");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    /**
     * 导出图表详情
     * 将图表数据导出为Excel文件，支持分页导出大数据量
     *
     * @param request Excel导出请求，包含图表信息、导出字段、分页参数等
     * @param response HTTP响应对象
     * @throws Exception 导出异常
     */
    @DeLinkPermit("#p0.dvId")
    @Override
    public void innerExportDetails(ChartExcelRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String linkToken = httpServletRequest.getHeader(AuthConstant.LINK_TOKEN_KEY);
        LogUtil.info(request.getViewInfo().getId() + " " + StringUtils.isNotEmpty(linkToken) + " " + request.isDataEaseBi());
        if ((StringUtils.isNotEmpty(linkToken) && !request.isDataEaseBi()) || (request.isDataEaseBi() && StringUtils.isEmpty(linkToken))) {
            OutputStream outputStream = response.getOutputStream();
            try {
                Workbook wb = new SXSSFWorkbook();
                //给单元格设置样式
                CellStyle cellStyle = wb.createCellStyle();
                Font font = wb.createFont();
                //设置字体大小
                font.setFontHeightInPoints((short) 12);
                //设置字体加粗
                font.setBold(true);
                //给字体设置样式
                cellStyle.setFont(font);
                //设置单元格背景颜色
                cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                //设置单元格填充样式(使用纯色背景颜色填充)
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                if ("dataset".equals(request.getDownloadType()) || request.getViewInfo().getType().equalsIgnoreCase("table-info") || request.getViewInfo().getType().equalsIgnoreCase("table-normal")) {
                    List<Object[]> details = new ArrayList<>();
                    Sheet detailsSheet;
                    Integer sheetIndex = 1;
                    request.getViewInfo().getChartExtRequest().setPageSize(Long.valueOf(extractPageSize));
                    ChartViewDTO chartViewDTO = findExcelData(request);
                    for (long i = 1; i < chartViewDTO.getTotalPage() + 1; i++) {
                        request.getViewInfo().getChartExtRequest().setGoPage(i);
                        findExcelData(request);
                        details.addAll(request.getDetails());
                        if ((details.size() + extractPageSize) > sheetLimit || i == chartViewDTO.getTotalPage()) {
                            detailsSheet = wb.createSheet("数据" + sheetIndex);
                            Integer[] excelTypes = request.getExcelTypes();
                            List<ChartViewFieldDTO> xAxis = new ArrayList<>();
                            xAxis.addAll(request.getViewInfo().getXAxis());
                            xAxis.addAll(request.getViewInfo().getYAxis());
                            xAxis.addAll(request.getViewInfo().getXAxisExt());
                            xAxis.addAll(request.getViewInfo().getYAxisExt());
                            xAxis.addAll(request.getViewInfo().getExtStack());
                            Object[] header = Arrays.stream(request.getHeader()).filter(item -> xAxis.stream().map(d -> StringUtils.isNotBlank(d.getChartShowName()) ? d.getChartShowName() : d.getName()).toList().contains(item)).collect(Collectors.toList()).toArray();
                            details.add(0, header);
                            List<Integer> columnIndexs = new ArrayList<>();
                            for (int i1 = 0; i1 < xAxis.size(); i1++) {
                                ChartViewFieldDTO xAxi = xAxis.get(i1);
                                if (xAxi.isHide()) {
                                    columnIndexs.add(i1);
                                }
                            }
                            ExportCenterDownLoadManage.removeColumn(details, columnIndexs);
                            ViewDetailField[] detailFields = request.getDetailFields();
                            ChartDataServer.setExcelData(detailsSheet, cellStyle, header, details, detailFields, excelTypes, request.getViewInfo(), wb);
                            sheetIndex++;
                            details.clear();
                        }
                    }
                } else {
                    findExcelData(request);
                    if (CollectionUtils.isEmpty(request.getMultiInfo())) {
                        List<Object[]> details = request.getDetails();
                        Integer[] excelTypes = request.getExcelTypes();
                        details.add(0, request.getHeader());
                        ViewDetailField[] detailFields = request.getDetailFields();
                        Object[] header = request.getHeader();
                        Sheet detailsSheet = wb.createSheet("数据");
                        if (request.getViewInfo().getType().equalsIgnoreCase("table-normal")) {
                            setExcelData(detailsSheet, cellStyle, header, details, detailFields, excelTypes, request.getViewInfo(), wb);
                        } else {
                            setExcelData(detailsSheet, cellStyle, header, details, detailFields, excelTypes, request.getViewInfo(), null);
                        }
                    } else {
                        for (int i = 0; i < request.getMultiInfo().size(); i++) {
                            ChartExcelRequestInner requestInner = request.getMultiInfo().get(i);
                            List<Object[]> details = requestInner.getDetails();
                            Integer[] excelTypes = requestInner.getExcelTypes();
                            details.add(0, requestInner.getHeader());
                            ViewDetailField[] detailFields = requestInner.getDetailFields();
                            Object[] header = requestInner.getHeader();
                            Sheet detailsSheet = wb.createSheet("数据 " + (i + 1));
                            setExcelData(detailsSheet, cellStyle, header, details, detailFields, excelTypes, request.getViewInfo(), null);
                        }
                    }
                }
                exportCenterManage.addWatermarkTools(wb);
                response.setContentType("application/vnd.ms-excel");
                //文件名称
                response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(request.getViewName(), StandardCharsets.UTF_8) + ".xlsx");
                wb.write(outputStream);
                outputStream.flush();
                outputStream.close();

                try {
                    if (request.getBusiFlag().equalsIgnoreCase("dashboard")) {
                        CommonBeanFactory.proxy(this.getClass()).exportPanelViewLog(Long.parseLong(request.getViewId()));
                    } else {
                        CommonBeanFactory.proxy(this.getClass()).exportScreenViewLog(Long.parseLong(request.getViewId()));
                    }
                } catch (Exception e) {
                    LogUtil.error(e);
                }
            } catch (Exception e) {
                DEException.throwException(e);
            }
        } else {
            exportCenterManage.addTask(request.getViewId(), "chart", request, request.getBusiFlag());
        }
    }

    @DeLinkPermit("#p0.dvId")
    /**
     * 导出数据集详情
     * 将数据集的原始数据导出为Excel文件
     *
     * @param request Excel导出请求
     * @param response HTTP响应对象
     * @throws Exception 导出异常
     */
    @Override
    public void innerExportDataSetDetails(ChartExcelRequest request, HttpServletResponse response) throws Exception {
        this.innerExportDetails(request, response);
    }

    /**
     * 设置Excel数据
     * 将查询结果数据填充到Excel工作表中，支持表头分组、单元格合并等功能
     * 重载方法，默认不添加批注
     *
     * @param detailsSheet Excel工作表对象
     * @param cellStyle 单元格样式对象
     * @param header 表头数组
     * @param details 数据行列表
     * @param detailFields 详情字段配置
     * @param excelTypes Excel数据类型数组
     * @param viewInfo 图表视图信息，包含格式化配置
     * @param wb Excel工作簿对象
     */
    public static void setExcelData(Sheet detailsSheet, CellStyle cellStyle, Object[] header, List<Object[]> details, ViewDetailField[] detailFields, Integer[] excelTypes, ChartViewDTO viewInfo, Workbook wb) {
        setExcelData(detailsSheet, cellStyle, header, details, detailFields, excelTypes, null, viewInfo, wb);
    }


    /**
     * 设置Excel数据（完整版）
     * 将查询结果数据填充到Excel工作表中，支持表头分组、单元格合并、数据格式化等功能
     * 适用于表格类型图表的导出
     *
     * @param detailsSheet Excel工作表对象
     * @param cellStyle 单元格样式对象，包含字体、边框、背景等设置
     * @param header 表头数组
     * @param details 数据行列表，每行是一个Object数组
     * @param detailFields 详情字段配置数组
     * @param excelTypes Excel数据类型数组，用于区分文本和数值
     * @param comment 单元格批注（可选）
     * @param viewInfo 图表视图信息，包含格式化配置、表头分组配置等
     * @param wb Excel工作簿对象
     */
    public static void setExcelData(Sheet detailsSheet, CellStyle cellStyle, Object[] header, List<Object[]> details, ViewDetailField[] detailFields, Integer[] excelTypes, Comment comment, ChartViewDTO viewInfo, Workbook wb) {
        List<CellStyle> styles = new ArrayList<>();
        List<ChartViewFieldDTO> xAxis = new ArrayList<>();

        xAxis.addAll(viewInfo.getXAxis());
        xAxis.addAll(viewInfo.getYAxis());
        xAxis.addAll(viewInfo.getXAxisExt());
        xAxis.addAll(viewInfo.getYAxisExt());
        xAxis.addAll(viewInfo.getExtStack());
        xAxis.addAll(viewInfo.getDrillFields());
        TableHeader tableHeader = null;
        Integer totalDepth = 0;
        List<CellRangeAddress> mergeConfig = new ArrayList<>();
        if (StringUtils.equalsAnyIgnoreCase(viewInfo.getType(), "table-normal", "table-info")) {
            for (ChartViewFieldDTO xAxi : xAxis) {
                if (xAxi.isHide()) {
                    continue;
                }
                if (xAxi.getDeType().equals(DeTypeConstants.DE_INT) || xAxi.getDeType().equals(DeTypeConstants.DE_FLOAT)) {
                    CellStyle formatterCellStyle = createCellStyle(wb, xAxi.getFormatterCfg(), null);
                    styles.add(formatterCellStyle);
                } else {
                    styles.add(null);
                }
            }

            Map<String, Object> customAttr = viewInfo.getCustomAttr();
            Map<String, Object> tableHeaderMap = (Map<String, Object>) customAttr.get("tableHeader");
            if (tableHeaderMap.get("headerGroup") != null && Boolean.parseBoolean(tableHeaderMap.get("headerGroup").toString())) {
                var tmpHeader = JsonUtil.parseObject((String) JsonUtil.toJSONString(customAttr.get("tableHeader")), TableHeader.class);
                // 校验字段数量和顺序
                var allAxis = new ArrayList<>(viewInfo.getXAxis().stream().filter(x -> !x.isHide()).toList());
                if (StringUtils.equalsIgnoreCase(viewInfo.getType(), "table-normal")) {
                    allAxis.addAll(viewInfo.getYAxis().stream().filter(x -> !x.isHide()).toList());
                }
                if (validateHeaderGroup(tmpHeader, allAxis)) {
                    tableHeader = tmpHeader;
                    for (TableHeader.ColumnInfo column : tableHeader.getHeaderGroupConfig().getColumns()) {
                        totalDepth = Math.max(totalDepth, getDepth(column, 1));
                    }
                    for (TableHeader.ColumnInfo column : tableHeader.getHeaderGroupConfig().getColumns()) {
                        setWidth(column, 1);
                    }
                }
            }
            if ("table-info".equalsIgnoreCase(viewInfo.getType()) && !"dataset".equalsIgnoreCase(viewInfo.getDownloadType())) {
                Map<String, Object> tableCell = (Map<String, Object>) viewInfo.getCustomAttr().get("tableCell");
                Boolean mergeCells = (Boolean) tableCell.get("mergeCells");
                if (mergeCells != null && mergeCells) {
                    var mergeIndex = viewInfo.getXAxis().size();
                    for (int i = 0; i < viewInfo.getXAxis().size(); i++) {
                        if ("q".equalsIgnoreCase(viewInfo.getXAxis().get(i).getGroupType())) {
                            mergeIndex = i;
                            break;
                        }
                    }
                    if (mergeIndex >= 1 && details.size() > 1) {
                        mergeConfig = getMergeConfig(details.subList(1, details.size()), mergeIndex - 1, totalDepth == 0 ? 1 : totalDepth);
                    }
                }
            }
        }

        boolean mergeHead = false;
        if (ArrayUtils.isNotEmpty(detailFields)) {
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            String[] detailField = Arrays.stream(detailFields).map(ViewDetailField::getName).toList().toArray(new String[detailFields.length]);

            Row row = detailsSheet.createRow(0);
            int headLen = header.length;
            int detailFieldLen = detailField.length;
            for (int i = 0; i < headLen; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(header[i].toString());
                if (i < headLen - 1) {
                    CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 1, i, i);
                    detailsSheet.addMergedRegion(cellRangeAddress);
                } else {
                    for (int j = i + 1; j < detailFieldLen + i; j++) {
                        row.createCell(j).setCellStyle(cellStyle);
                    }
                    CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, i, i + detailFieldLen - 1);
                    detailsSheet.addMergedRegion(cellRangeAddress);
                }
                cell.setCellStyle(cellStyle);
                detailsSheet.setColumnWidth(i, 255 * 20);
            }

            Row detailRow = detailsSheet.createRow(1);
            for (int i = 0; i < headLen - 1; i++) {
                Cell cell = detailRow.createCell(i);
                cell.setCellStyle(cellStyle);
            }
            for (int i = 0; i < detailFieldLen; i++) {
                int colIndex = headLen - 1 + i;
                Cell cell = detailRow.createCell(colIndex);
                cell.setCellValue(detailField[i]);
                cell.setCellStyle(cellStyle);
                detailsSheet.setColumnWidth(colIndex, 255 * 20);
            }
            details.add(1, detailField);
            mergeHead = true;
        }
        if (CollectionUtils.isNotEmpty(details) && (!mergeHead || details.size() > 2)) {
            int realDetailRowIndex = tableHeader == null ? 2 : totalDepth;
            if (tableHeader != null) {
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                Map<String, Row> rowMap = new HashMap<>();
                for (Integer i = 0; i < totalDepth; i++) {
                    rowMap.put("row" + i, detailsSheet.createRow(i));
                }
                int width = 0;
                Integer depth = 0;
                for (TableHeader.ColumnInfo column : tableHeader.getHeaderGroupConfig().getColumns()) {
                    createCell(tableHeader, column, width, depth, detailsSheet, cellStyle, totalDepth, rowMap, xAxis);
                    width = width + column.getWidth();
                }
            }
            for (int i = (mergeHead ? 2 : 0); i < details.size(); i++) {
                int rowIndex = i;
                if (tableHeader != null) {
                    rowIndex = realDetailRowIndex - 1 + i;
                } else {
                    rowIndex = realDetailRowIndex > 2 ? realDetailRowIndex : i;
                }
                Row row = detailsSheet.createRow(rowIndex);
                Object[] rowData = details.get(i);
                if (rowData != null) {
                    for (int j = 0; j < rowData.length; j++) {
                        Object cellValObj = rowData[j];
                        if (mergeHead && j == rowData.length - 1 && (cellValObj.getClass().isArray() || cellValObj instanceof ArrayList)) {
                            Object[] detailRowArray = ((List<Object>) cellValObj).toArray(new Object[((List<?>) cellValObj).size()]);
                            int detailRowArrayLen = detailRowArray.length;
                            int temlJ = j;
                            while (detailRowArrayLen > 1 && temlJ-- > 0) {
                                CellRangeAddress cellRangeAddress = new CellRangeAddress(realDetailRowIndex, realDetailRowIndex + detailRowArrayLen - 1, temlJ, temlJ);
                                detailsSheet.addMergedRegion(cellRangeAddress);
                            }

                            for (int k = 0; k < detailRowArrayLen; k++) {
                                List<Object> detailRows = (List<Object>) detailRowArray[k];
                                Row curRow = row;
                                if (k > 0) {
                                    curRow = detailsSheet.createRow(realDetailRowIndex + k);
                                }

                                for (int l = 0; l < detailRows.size(); l++) {
                                    Object col = detailRows.get(l);
                                    Cell cell = curRow.createCell(j + l);
                                    cell.setCellValue(col.toString());
                                }
                            }
                            realDetailRowIndex += detailRowArrayLen;
                            break;
                        }

                        Cell cell = row.createCell(j);
                        if (i == 0) {// 头部
                            cell.setCellValue(cellValObj.toString());
                            cell.setCellStyle(cellStyle);
                            //设置列的宽度
                            detailsSheet.setColumnWidth(j, 255 * 20);
                        } else if (cellValObj != null) {
                            try {
                                if ((viewInfo.getType().equalsIgnoreCase("table-normal") || viewInfo.getType().equalsIgnoreCase("table-info")) && (xAxis.get(j).getDeType().equals(DeTypeConstants.DE_INT) || xAxis.get(j).getDeType().equals(DeTypeConstants.DE_FLOAT))) {
                                    try {
                                        FormatterCfgDTO formatterCfgDTO = xAxis.get(j).getFormatterCfg() == null ? new FormatterCfgDTO().setUnitLanguage(Lang.isChinese() ? "ch" : "en") : xAxis.get(j).getFormatterCfg();
                                        row.getCell(j).setCellStyle(styles.get(j));
                                        row.getCell(j).setCellValue(Double.valueOf(cellValue(formatterCfgDTO, new BigDecimal(cellValObj.toString()))));
                                    } catch (Exception e) {
                                        cell.setCellValue(cellValObj.toString());
                                    }
                                } else {
                                    if ((excelTypes[j].equals(DeTypeConstants.DE_INT) || excelTypes[j].equals(DeTypeConstants.DE_FLOAT)) && StringUtils.isNotEmpty(cellValObj.toString())) {
                                        cell.setCellValue(Double.valueOf(cellValObj.toString()));
                                    } else if (cellValObj != null) {
                                        cell.setCellValue(cellValObj.toString());
                                    }
                                }
                            } catch (Exception e) {
                                LogUtil.warn("export excel data transform error");
                            }
                        } else {
                            if (!viewInfo.getType().equalsIgnoreCase("circle-packing")) {
                                Map<String, Object> senior = viewInfo.getSenior();
                                viewInfo.getCustomAttr().get("");
                                ChartSeniorFunctionCfgDTO functionCfgDTO = JsonUtil.parseObject((String) JsonUtil.toJSONString(senior.get("functionCfg")), ChartSeniorFunctionCfgDTO.class);
                                if (functionCfgDTO != null && StringUtils.isNotEmpty(functionCfgDTO.getEmptyDataStrategy()) && functionCfgDTO.getEmptyDataStrategy().equalsIgnoreCase("setZero")) {
                                    if ((viewInfo.getType().equalsIgnoreCase("table-normal") || viewInfo.getType().equalsIgnoreCase("table-info"))) {
                                        if (functionCfgDTO.getEmptyDataFieldCtrl().contains(xAxis.get(j).getDataeaseName())) {
                                            cell.setCellValue(0);
                                        }
                                    } else {
                                        cell.setCellValue(0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(mergeConfig)) {
                mergeConfig.forEach(detailsSheet::addMergedRegion);
            }
        }
    }

    /**
     * 获取单元格合并配置
     * 为表格的相同值单元格生成合并区域配置，用于纵向单元格合并
     * 从第一列开始逐列处理，根据值的连续性确定合并范围
     *
     * @param data 数据列表，每行是一个Object数组
     * @param colIndex 需要处理合并的最大列索引
     * @param offsetHeight 行偏移量，用于处理表头占用多行的情况
     * @return 单元格合并区域列表
     */
    private static List<CellRangeAddress> getMergeConfig(List<Object[]> data, int colIndex, int offsetHeight) {
        var result = new ArrayList<CellRangeAddress>();
        var preRange = new ArrayList<Integer[]>();
        var initRange = new Integer[]{0, data.size() - 1};
        preRange.add(initRange);
        for (int curColIndex = 0; curColIndex <= colIndex; curColIndex++) {
            var curRange = new ArrayList<Integer[]>();
            for (int preRangeIndex = 0; preRangeIndex < preRange.size(); preRangeIndex++) {
                var preRowRange = preRange.get(preRangeIndex);
                var start = preRowRange[0];
                var end = preRowRange[1];
                var lastColValue = data.get(start)[curColIndex];
                if (lastColValue != null) {
                    lastColValue = lastColValue.toString();
                } else {
                    lastColValue = "";
                }
                var lastRowIndex = start;
                for (Integer curRowIndex = start + 1; curRowIndex <= end; curRowIndex++) {
                    var curRow = data.get(curRowIndex);
                    var curColValue = curRow[curColIndex];
                    if (curColValue != null) {
                        curColValue = curColValue.toString();
                    } else {
                        curColValue = "";
                    }
                    if (!StringUtils.equals(lastColValue.toString(), curColValue.toString()) && (curRowIndex - lastRowIndex > 1)) {
                        curRange.add(new Integer[]{lastRowIndex, curRowIndex - 1});
                        result.add(new CellRangeAddress(lastRowIndex + offsetHeight, curRowIndex + offsetHeight - 1, curColIndex, curColIndex));
                    }
                    if (curRowIndex.equals(end) && curColValue.equals(lastColValue) && curRowIndex - lastRowIndex > 0) {
                        curRange.add(new Integer[]{lastRowIndex, curRowIndex});
                        result.add(new CellRangeAddress(lastRowIndex + offsetHeight, curRowIndex + offsetHeight, curColIndex, curColIndex));
                    }
                    if (!StringUtils.equals(lastColValue.toString(), curColValue.toString())) {
                        lastColValue = curColValue;
                        lastRowIndex = curRowIndex;
                    }
                }
            }
            preRange = curRange;
        }
        return result;
    }

    /**
     * 校验表头分组配置
     * 验证表头分组配置的有效性，包括字段数量和字段顺序匹配
     *
     * @param header 表头对象，包含分组配置
     * @param fields 图表字段列表
     * @return 配置有效返回true，否则返回false
     */
    private static boolean validateHeaderGroup(TableHeader header, List<ChartViewFieldDTO> fields) {
        if (header == null) {
            return false;
        }
        var columns = header.getHeaderGroupConfig().getColumns();
        if (CollectionUtils.isEmpty(columns)) {
            return false;
        }
        var leafColumn = getHeaderLeafColumn(columns);
        if (CollectionUtils.isEmpty(leafColumn) || leafColumn.size() != fields.size()) {
            return false;
        }
        for (int i = 0; i < leafColumn.size(); i++) {
            var a = leafColumn.get(i);
            var b = fields.get(i).getDataeaseName();
            if (!StringUtils.equals(a, b)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取表头叶子节点字段
     * 递归获取表头分组配置中的所有叶子节点字段名
     *
     * @param columns 列配置列表
     * @return 叶子节点字段名列表
     */
    private static List<String> getHeaderLeafColumn(List<TableHeader.ColumnInfo> columns) {
        var result = new ArrayList<String>();
        for (TableHeader.ColumnInfo column : columns) {
            if (CollectionUtils.isEmpty(column.getChildren())) {
                result.add(column.getKey());
            } else {
                result.addAll(getHeaderLeafColumn(column.getChildren()));
            }
        }
        return result;
    }

    /**
     * 计算表头深度
     * 递归计算表头分组的最大深度（层数）
     *
     * @param column 列信息对象
     * @param parentDepth 父节点深度
     * @return 该节点的最大深度
     */
    private static Integer getDepth(TableHeader.ColumnInfo column, Integer parentDepth) {
        if (org.springframework.util.CollectionUtils.isEmpty(column.getChildren())) {
            return parentDepth;
        } else {
            Integer depth = 0;
            for (TableHeader.ColumnInfo child : column.getChildren()) {
                depth = Math.max(depth, getDepth(child, parentDepth + 1));
            }
            return depth;
        }
    }

    /**
     * 创建表头单元格
     * 递归创建分组的表头单元格，处理单元格合并和样式设置
     *
     * @param tableHeader 表头配置对象
     * @param column 当前列信息
     * @param width 列的起始宽度位置
     * @param depth 当前深度层级
     * @param sheet Excel工作表对象
     * @param cellStyle 单元格样式
     * @param totaalDepth 表头总深度
     * @param rowMap 行对象映射表
     * @param xAxis 图表X轴字段列表，用于获取字段显示名称
     */
    private static void createCell(TableHeader tableHeader, TableHeader.ColumnInfo column, Integer width, Integer depth, Sheet sheet, CellStyle cellStyle, Integer totaalDepth, Map<String, Row> rowMap, List<ChartViewFieldDTO> xAxis) {
        if (org.springframework.util.CollectionUtils.isEmpty(column.getChildren())) {
            Integer toDepth = totaalDepth - 1 > depth ? totaalDepth - 1 : depth;
            if (depth.equals(toDepth)) {
                Cell cell = rowMap.get("row" + depth).createCell(width);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(getDeFieldName(xAxis, column.getKey()));
            } else {
                for (int i = depth; i <= toDepth; i++) {
                    Cell cell1 = rowMap.get("row" + i).createCell(width);
                    cell1.setCellValue(getDeFieldName(xAxis, column.getKey()));
                    cell1.setCellStyle(cellStyle);
                }
                CellRangeAddress region = new CellRangeAddress(depth, toDepth, width, width);
                sheet.addMergedRegion(region);

                Cell mergedCell = rowMap.get("row" + depth).getCell(width);
                mergedCell.setCellStyle(cellStyle);

            }
        } else {
            Cell cell1 = rowMap.get("row" + depth).createCell(width);
            cell1.setCellValue(getGroupName(tableHeader, column.getKey()));
            cell1.setCellStyle(cellStyle);
            Cell cell2 = rowMap.get("row" + depth).createCell(width + column.getWidth() - 1);
            cell2.setCellValue(getGroupName(tableHeader, column.getKey()));
            cell2.setCellStyle(cellStyle);
            CellRangeAddress region = new CellRangeAddress(depth, depth, width, width + column.getWidth() - 1);
            sheet.addMergedRegion(region);
            Cell mergedCell = rowMap.get("row" + depth).getCell(width);
            mergedCell.setCellStyle(cellStyle);
            int subWith = width;
            for (TableHeader.ColumnInfo child : column.getChildren()) {
                createCell(tableHeader, child, subWith, depth + 1, sheet, cellStyle, totaalDepth, rowMap, xAxis);
                subWith = subWith + child.getWidth();
            }
        }
    }

    /**
     * 获取分组名称
     * 根据字段key从表头元数据配置中获取对应的分组显示名称
     *
     * @param tableHeader 表头配置对象
     * @param key 字段key
     * @return 分组显示名称，未找到返回空字符串
     */
    private static String getGroupName(TableHeader tableHeader, String key) {
        for (TableHeader.MetaInfo metaInfo : tableHeader.getHeaderGroupConfig().getMeta()) {
            if (metaInfo.getField().equals(key)) {
                return metaInfo.getName();
            }
        }
        return "";
    }

    /**
     * 获取DataEase字段显示名称
     * 根据字段dataeaseName获取字段在图表中的显示名称
     * 优先使用chartShowName，如果为空则使用name
     *
     * @param xAxis 图表字段列表
     * @param key 字段的dataeaseName
     * @return 字段显示名称，未找到返回空字符串
     */
    private static String getDeFieldName(List<ChartViewFieldDTO> xAxis, String key) {
        for (ChartViewFieldDTO xAxi : xAxis) {
            if (xAxi.getDataeaseName().equals(key)) {
                return StringUtils.isNotBlank(xAxi.getChartShowName()) ? xAxi.getChartShowName() : xAxi.getName();
            }
        }
        return "";
    }

    /**
     * 设置列宽度
     * 递归计算并设置表头分组的列宽度
     * 叶子节点的宽度为1，非叶子节点的宽度为所有子节点宽度之和
     *
     * @param column 列信息对象
     * @param parentWidth 父节点的宽度
     * @return 该列的宽度
     */
    private static Integer setWidth(TableHeader.ColumnInfo column, Integer parentWidth) {
        if (org.springframework.util.CollectionUtils.isEmpty(column.getChildren())) {
            column.setWidth(parentWidth);
            return parentWidth;
        } else {
            Integer depth = 0;
            for (TableHeader.ColumnInfo child : column.getChildren()) {
                depth = depth + setWidth(child, 1);
            }
            column.setWidth(depth);
            return depth;
        }
    }

    /**
     * 单元格数值转换
     * 根据格式化配置转换数值，百分比类型不进行单位转换，其他类型进行单位转换
     *
     * @param formatterCfgDTO 格式化配置对象
     * @param value 原始数值
     * @return 转换后的数值字符串
     */
    private static String cellValue(FormatterCfgDTO formatterCfgDTO, BigDecimal value) {
        if (formatterCfgDTO.getType().equalsIgnoreCase("percent")) {
            return value.toString();
        } else {
            return value.divide(BigDecimal.valueOf(formatterCfgDTO.getUnit())).toString();
        }
    }

    /**
     * 创建单元格样式
     * 根据格式化配置创建Excel单元格样式，支持自动、数值、百分比三种类型格式化
     *
     * @param workbook Excel工作簿对象
     * @param formatter 格式化配置对象
     * @param value 单元格值（用于自动类型判断小数位数）
     * @return 单元格样式对象
     */
    private static CellStyle createCellStyle(Workbook workbook, FormatterCfgDTO formatter, String value) {
        CellStyle cellStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();

        if (formatter == null) {
            cellStyle.setDataFormat(format.getFormat("General"));
            return cellStyle;
        }
        String formatStr = "";
        if (formatter.getType().equals("auto")) {
            String[] valueSplit = String.valueOf(value).split(".");
            if (StringUtils.isEmpty(value) || !value.contains(".")) {
                formatStr = "General";
            } else {
                formatStr = "0." + new String(new char[valueSplit.length]).replace('\0', '0');
            }
            switch (formatter.getUnit()) {
                case 1000:
                    formatStr = formatStr + (formatter.getUnitLanguage().equalsIgnoreCase("ch") ? "\"千\"" : "\"K\"");
                    break;
                case 10000:
                    formatStr = formatStr + "\"万\"";
                    break;
                case 1000000:
                    formatStr = formatStr + (formatter.getUnitLanguage().equalsIgnoreCase("ch") ? "\"百万\"" : "\"M\"");
                    break;
                case 100000000:
                    formatStr = formatStr + "\"亿\"";
                    break;
                case 1000000000:
                    formatStr = formatStr + "\"B\"";
                    break;
                default:
                    break;
            }
            if (formatter.getThousandSeparator()) {
                formatStr = "#,##" + formatStr;
            }
            if (StringUtils.isNotEmpty(formatter.getSuffix())) {
                if (formatter.getSuffix().equals("%")) {
                    formatStr = formatStr + "\"%\"";
                } else {
                    formatStr = formatStr + "\"" + formatter.getSuffix() + "\"";
                }
            }
        }
        if (formatter.getType().equals("value")) {
            if (formatter.getDecimalCount() > 0) {
                formatStr = "0." + new String(new char[formatter.getDecimalCount()]).replace('\0', '0');
            } else {
                formatStr = "0";
            }
            switch (formatter.getUnit()) {
                case 1000:
                    formatStr = formatStr + (formatter.getUnitLanguage().equalsIgnoreCase("ch") ? "\"千\"" : "\"K\"");
                    break;
                case 10000:
                    formatStr = formatStr + "\"万\"";
                    break;
                case 1000000:
                    formatStr = formatStr + (formatter.getUnitLanguage().equalsIgnoreCase("ch") ? "\"百万\"" : "\"M\"");
                    break;
                case 100000000:
                    formatStr = formatStr + "\"亿\"";
                    break;
                case 1000000000:
                    formatStr = formatStr + "\"B\"";
                    break;
                default:
                    break;
            }
            if (formatter.getThousandSeparator()) {
                formatStr = "#,##" + formatStr;
            }
            if (StringUtils.isNotEmpty(formatter.getSuffix())) {
                if (formatter.getSuffix().equals("%")) {
                    formatStr = formatStr + "\"%\"";
                } else {
                    formatStr = formatStr + "\"" + formatter.getSuffix() + "\"";
                }
            }
        } else if (formatter.getType().equals("percent")) {
            if (formatter.getDecimalCount() > 0) {
                formatStr = "0." + new String(new char[formatter.getDecimalCount()]).replace('\0', '0');
            } else {
                formatStr = "0";
            }
            formatStr = formatStr + "%";
        }
        if (StringUtils.isNotEmpty(formatStr)) {
            cellStyle.setDataFormat(format.getFormat(formatStr));
        } else {
            return null;
        }
        return cellStyle;
    }

    /**
     * 获取字段数据
     * 查询指定字段的所有不重复值，用于筛选组件
     *
     * @param view 图表视图信息
     * @param fieldId 字段ID
     * @param fieldType 字段类型（dimension/quota）
     * @return 字段值列表
     * @throws Exception 查询异常
     */
    @Override
    public List<String> getFieldData(ChartViewDTO view, Long fieldId, String fieldType) throws Exception {
        return chartDataManage.getFieldData(view, fieldId, fieldType);
    }

    /**
     * 获取钻取字段数据
     * 获取字段用于钻取分析的数据值
     *
     * @param view 图表视图信息
     * @param fieldId 字段ID
     * @return 钻取字段值列表
     * @throws Exception 查询异常
     */
    @Override
    public List<String> getDrillFieldData(ChartViewDTO view, Long fieldId) throws Exception {
        return chartDataManage.getDrillFieldData(view, fieldId);
    }

    @DeLog(id = "#p0", ot = LogOT.EXPORT, st = LogST.PANEL)
    /**
     * 导出仪表板视图日志
     * 记录仪表板视图的导出操作日志
     *
     * @param id 视图ID
     */
    public void exportPanelViewLog(Long id) {
    }

    @DeLog(id = "#p0", ot = LogOT.EXPORT, st = LogST.SCREEN)
    /**
     * 导出大屏视图日志
     * 记录大屏视图的导出操作日志
     *
     * @param id 视图ID
     */
    public void exportScreenViewLog(Long id) {
    }


}
