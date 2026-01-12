package io.dataease.visualization.manage;

import com.fasterxml.jackson.core.type.TypeReference;
import io.dataease.api.visualization.vo.DataVisualizationVO;
import io.dataease.chart.constant.ChartConstants;
import io.dataease.chart.manage.ChartDataManage;
import io.dataease.chart.manage.ChartViewManege;
import io.dataease.constant.CommonConstants;
import io.dataease.constant.DeTypeConstants;
import io.dataease.dataset.server.DatasetFieldServer;
import io.dataease.exception.DEException;
import io.dataease.exportCenter.util.ExportCenterUtils;
import io.dataease.extensions.view.dto.ChartExtFilterDTO;
import io.dataease.extensions.view.dto.ChartExtRequest;
import io.dataease.extensions.view.dto.ChartViewDTO;
import io.dataease.extensions.view.dto.ChartViewFieldDTO;
import io.dataease.utils.AuthUtils;
import io.dataease.utils.JsonUtil;
import io.dataease.visualization.bo.ExcelSheetModel;
import io.dataease.visualization.dao.ext.mapper.ExtDataVisualizationMapper;
import io.dataease.visualization.template.FilterBuildTemplate;
import io.dataease.visualization.utils.VisualizationExcelUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class CoreVisualizationExportManage {
    @Resource
    private ExtDataVisualizationMapper extDataVisualizationMapper;

    @Resource
    private ChartViewManege chartViewManege;

    @Resource
    private ChartDataManage chartDataManage;

    @Resource
    private VisualizationTemplateExtendDataManage extendDataManage;

    @Resource
    private DatasetFieldServer datasetFieldServer;

    public String getResourceName(Long dvId, String busiFlag) {
        DataVisualizationVO visualization = extDataVisualizationMapper.findDvInfo(dvId, busiFlag, "core");
        if (ObjectUtils.isEmpty(visualization)) DEException.throwException("资源不存在或已经被删除...");
        return visualization.getName();
    }

    public File exportExcel(Long dvId, String busiFlag, List<Long> viewIdList, boolean onlyDisplay, String filterJson) throws Exception {
        // 查询可视化资源信息
        DataVisualizationVO visualization = extDataVisualizationMapper.findDvInfo(dvId, busiFlag, "core");
        if (ObjectUtils.isEmpty(visualization)) DEException.throwException("资源不存在或已经被删除...");
        // 查询该仪表板下的所有图表视图
        List<ChartViewDTO> chartViewDTOS = chartViewManege.listBySceneId(dvId, CommonConstants.RESOURCE_TABLE.CORE);

        // 解析组件数据（包括嵌套在Tab中的组件）
        String componentsJson = visualization.getComponentData();
        List<Map<String, Object>> components = JsonUtil.parseList(componentsJson, tokenType);
        // 展开嵌套的Tab组件，获取所有子组件
        components = components.stream().flatMap(item -> {
            // 处理DeTabs组件，展开其内部的所有子组件
            if (ObjectUtils.isNotEmpty(item.get("innerType")) && StringUtils.equalsIgnoreCase(item.get("innerType").toString(), "DeTabs")) {
                if (ObjectUtils.isNotEmpty(item.get("propValue"))) {
                    List<Map<String, Object>> deTabs = (List<Map<String, Object>>) item.get("propValue");
                    return deTabs.stream().flatMap(tab -> ((List<Map<String, Object>>) tab.get("componentData")).stream());
                }
            }
            return Stream.of(item);
        }).toList();
        // 提取所有组件的ID
        List<Long> idList = components.stream().filter(c -> ObjectUtils.isNotEmpty(c.get("id"))).map(component -> Long.parseLong(component.get("id").toString())).toList();

        // 如果指定了要导出的视图ID列表，则过滤出指定的视图
        if (CollectionUtils.isNotEmpty(viewIdList)) {
            chartViewDTOS = chartViewDTOS.stream().filter(item -> idList.contains(item.getId()) && viewIdList.contains(item.getId())).collect(Collectors.toList());
        }
        // 如果没有视图数据，直接返回null
        if (CollectionUtils.isEmpty(chartViewDTOS)) return null;
        // 构建每个视图的查询请求（包括过滤条件）
        Map<Long, ChartExtRequest> chartExtRequestMap = buildViewRequest(filterJson);
        List<ExcelSheetModel> sheets = new ArrayList<>();
        // 遍历每个视图，导出数据到Excel sheet
        for (int i = 0; i < chartViewDTOS.size(); i++) {
            ChartViewDTO view = chartViewDTOS.get(i);
            // 获取该视图的扩展请求（包含过滤条件等）
            ChartExtRequest extRequest = chartExtRequestMap.get(view.getId());
            if (ObjectUtils.isNotEmpty(extRequest)) {
                view.setChartExtRequest(extRequest);
            } else {
                // 如果没有自定义请求，使用默认请求
                view.setChartExtRequest(buildDefaultRequest());
            }
            view.getChartExtRequest().setUser(AuthUtils.getUser().getUserId());  // 设置当前用户
            view.setTitle((i + 1) + "-" + view.getTitle());  // 设置sheet标题（添加序号）
            sheets.addAll(exportViewData(view));  // 导出视图数据
        }

        // 生成Excel文件并返回
        return VisualizationExcelUtils.exportExcel(sheets, visualization.getName(), visualization.getId().toString());
    }

    private ExcelSheetModel exportSingleData(Map<String, Object> chart, String title) {
        ExcelSheetModel result = new ExcelSheetModel();
        // 获取字段定义
        Object objectFields = chart.get("fields");
        List<ChartViewFieldDTO> fields = (List<ChartViewFieldDTO>) objectFields;
        List<String> heads = new ArrayList<>();  // 表头名称列表
        List<String> headKeys = new ArrayList<>();  // 表头字段key列表
        List<Integer> fieldTypes = new ArrayList<>();  // 字段类型列表
        // 处理字段定义
        if (CollectionUtils.isNotEmpty(fields)) {
            fields.forEach(field -> {
                Object name = field.getName();  // 字段显示名称
                Object dataeaseName = field.getDataeaseName();  // 字段唯一标识
                Object deType = field.getDeType();  // 字段数据类型
                if (ObjectUtils.isNotEmpty(name) && ObjectUtils.isNotEmpty(dataeaseName)) {
                    heads.add(name.toString());  // 添加表头
                    headKeys.add(dataeaseName.toString());  // 添加字段key
                    // 如果没有数据类型，默认设置为字符串类型
                    if (deType == null) {
                        field.setDeType(DeTypeConstants.DE_STRING);
                        deType = DeTypeConstants.DE_STRING;
                    }
                    fieldTypes.add((int) deType);  // 记录字段类型
                }
            });
        }
        // 获取表格数据行
        Object objectTableRow = chart.get("tableRow");
        if (objectTableRow == null) {
            // 如果没有tableRow，则使用sourceData
            objectTableRow = chart.get("sourceData");
        }
        List<Map<String, Object>> tableRow = (List<Map<String, Object>>) objectTableRow;

        // 转换数据行为Excel格式的二维列表
        List<List<String>> details = tableRow.stream().map(row -> {
            List<String> tempList = new ArrayList<>();
            // 遍历每个字段
            for (int i = 0; i < headKeys.size(); i++) {
                String key = headKeys.get(i);
                Object val = row.get(key);
                if (ObjectUtils.isEmpty(val)) {
                    // 空值处理
                    tempList.add(StringUtils.EMPTY);
                } else if (fieldTypes.get(i) == 3) {
                    // 数值类型：过滤无效的小数点
                    tempList.add(filterInvalidDecimal(val.toString()));
                } else {
                    // 其他类型：直接转换为字符串
                    tempList.add(val.toString());
                }
            }
            return tempList;
        }).collect(Collectors.toList());
        // 构建Excel sheet模型
        result.setHeads(heads);  // 设置表头
        result.setData(details);  // 设置数据
        result.setFiledTypes(fieldTypes);  // 设置字段类型
        result.setSheetName(title);  // 设置sheet名称
        return result;
    }

    private List<ExcelSheetModel> exportViewData(ChartViewDTO request) {

        ChartViewDTO chartViewDTO = null;
        request.setIsExcelExport(true);  // 标记为Excel导出模式
        String type = request.getType();
        // 如果是表格类型，设置导出限制和结果模式
        if (StringUtils.equals("table-info", type)) {
            request.setResultCount(Math.toIntExact(ExportCenterUtils.getExportLimit("view")));  // 设置最大导出行数
            request.setResultMode(ChartConstants.VIEW_RESULT_MODE.ALL);  // 设置为查询所有数据
        }
        // 根据数据来源获取图表数据
        if (CommonConstants.VIEW_DATA_FROM.TEMPLATE.equalsIgnoreCase(request.getDataFrom())) {
            // 从模板数据获取
            chartViewDTO = extendDataManage.getChartDataInfo(request.getId(), request);
        } else {
            // 从数据集计算获取
            try {
                chartViewDTO = chartDataManage.calcData(request);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String title = chartViewDTO.getTitle();  // 获取视图标题
        Map<String, Object> chart = chartViewDTO.getData();  // 获取图表数据
        List<ExcelSheetModel> resultList = new ArrayList<>();
        // 检查是否包含左右分轴数据（用于组合图表）
        boolean leftExist = ObjectUtils.isNotEmpty(chart.get("left"));
        boolean rightExist = ObjectUtils.isNotEmpty(chart.get("right"));
        if (!leftExist && !rightExist) {
            // 普通单轴图表：导出为单个sheet
            ExcelSheetModel sheetModel = exportSingleData(chart, title);
            resultList.add(sheetModel);
            return resultList;
        }
        // 双轴组合图表：分别导出左轴和右轴数据
        if (leftExist) {
            // 导出左轴数据
            ExcelSheetModel sheetModel = exportSingleData((Map<String, Object>) chart.get("left"), title + "_left");
            resultList.add(sheetModel);
        }
        if (rightExist) {
            // 导出右轴数据
            ExcelSheetModel sheetModel = exportSingleData((Map<String, Object>) chart.get("right"), title + "_right");
            resultList.add(sheetModel);
        }
        return resultList;
    }

    private String filterInvalidDecimal(String sourceNumberStr) {
        // 过滤数值字符串中无效的小数点和零
        if (StringUtils.isNotBlank(sourceNumberStr) && StringUtils.contains(sourceNumberStr, ".")) {
            // 去除小数点后无效的零（如：1.00 -> 1）
            sourceNumberStr = sourceNumberStr.replaceAll("0+?$", "");
            // 去除末尾的小数点（如：1. -> 1）
            sourceNumberStr = sourceNumberStr.replaceAll("[.]$", "");
        }
        return sourceNumberStr;
    }

    private final TypeReference<List<Map<String, Object>>> tokenType = new TypeReference<List<Map<String, Object>>>() {
    };

    private Map<Long, ChartExtRequest> buildViewRequest(String filterJson) {
        // 解析过滤条件JSON，构建每个视图的查询请求
        if (StringUtils.isBlank(filterJson)) {
            return new HashMap<>();  // 如果没有过滤条件，返回空Map
        }
        // 解析JSON为视图ID到扩展请求的映射
        Map<Long, ChartExtRequest> extRequestMap = JsonUtil.parseObject(filterJson, new TypeReference<Map<Long, ChartExtRequest>>() {
        });
        // 为每个视图设置默认的查询参数
        extRequestMap.forEach((key, chartExtRequest) -> {
            chartExtRequest.setQueryFrom("panel");  // 标记查询来源为仪表板
            chartExtRequest.setResultCount(Math.toIntExact(ExportCenterUtils.getExportLimit("view")));  // 设置最大导出行数
            chartExtRequest.setResultMode(ChartConstants.VIEW_RESULT_MODE.ALL);  // 设置为查询所有数据
            chartExtRequest.setPageSize(ExportCenterUtils.getExportLimit("view"));  // 设置分页大小
        });
        return extRequestMap;
    }

    private ChartExtRequest buildDefaultRequest() {
        // 构建默认的图表查询请求（当没有自定义过滤条件时使用）
        ChartExtRequest chartExtRequest = new ChartExtRequest();
        chartExtRequest.setQueryFrom("panel");  // 标记查询来源为仪表板
        chartExtRequest.setFilter(new ArrayList<>());  // 设置空的过滤条件列表
        chartExtRequest.setResultCount(Math.toIntExact(ExportCenterUtils.getExportLimit("view")));  // 设置最大导出行数
        chartExtRequest.setResultMode(ChartConstants.VIEW_RESULT_MODE.ALL);  // 设置为查询所有数据
        chartExtRequest.setPageSize(ExportCenterUtils.getExportLimit("view"));  // 设置分页大小
        return chartExtRequest;
    }

    private Map<String, ChartExtRequest> buildViewRequest(DataVisualizationVO panelDto, Boolean justView) {
        // 从仪表板组件数据中构建每个视图的查询请求
        String componentsJson = panelDto.getComponentData();
        List<Map<String, Object>> components = JsonUtil.parseList(componentsJson, tokenType);
        Map<String, ChartExtRequest> result = new HashMap<>();
        // 构建空的过滤条件模板（为每个组件创建空的过滤条件）
        Map<String, List<ChartExtFilterDTO>> panelFilters = FilterBuildTemplate.buildEmpty(components);
        // 为每个组件创建查询请求
        for (Map.Entry<String, List<ChartExtFilterDTO>> entry : panelFilters.entrySet()) {
            List<ChartExtFilterDTO> chartExtFilterRequests = entry.getValue();
            ChartExtRequest chartExtRequest = new ChartExtRequest();
            chartExtRequest.setQueryFrom("panel");  // 标记查询来源为仪表板
            chartExtRequest.setFilter(chartExtFilterRequests);  // 设置过滤条件
            chartExtRequest.setResultCount(Math.toIntExact(ExportCenterUtils.getExportLimit("view")));  // 设置最大导出行数
            chartExtRequest.setResultMode(ChartConstants.VIEW_RESULT_MODE.ALL);  // 设置为查询所有数据
            chartExtRequest.setPageSize(ExportCenterUtils.getExportLimit("view"));  // 设置分页大小
            result.put(entry.getKey(), chartExtRequest);  // 使用组件ID作为key
        }
        return result;
    }

}
