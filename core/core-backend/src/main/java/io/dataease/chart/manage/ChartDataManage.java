package io.dataease.chart.manage;

import io.dataease.api.dataset.union.DatasetGroupInfoDTO;
import io.dataease.api.permissions.auth.dto.BusiPerCheckDTO;
import io.dataease.api.permissions.dataset.dto.DataSetRowPermissionsTreeDTO;
import io.dataease.chart.charts.ChartHandlerManager;
import io.dataease.chart.constant.ChartConstants;
import io.dataease.constant.AuthEnum;
import io.dataease.dataset.manage.DatasetGroupManage;
import io.dataease.dataset.manage.DatasetSQLManage;
import io.dataease.dataset.manage.DatasetTableFieldManage;
import io.dataease.dataset.manage.PermissionManage;
import io.dataease.dataset.utils.DatasetUtils;
import io.dataease.engine.sql.SQLProvider;
import io.dataease.engine.trans.*;
import io.dataease.engine.utils.SQLUtils;
import io.dataease.engine.utils.Utils;
import io.dataease.exception.DEException;
import io.dataease.extensions.datasource.api.PluginManageApi;
import io.dataease.extensions.datasource.dto.DatasetTableFieldDTO;
import io.dataease.extensions.datasource.dto.DatasourceRequest;
import io.dataease.extensions.datasource.dto.DatasourceSchemaDTO;
import io.dataease.extensions.datasource.factory.ProviderFactory;
import io.dataease.extensions.datasource.model.SQLMeta;
import io.dataease.extensions.datasource.provider.Provider;
import io.dataease.extensions.view.dto.*;
import io.dataease.extensions.view.factory.PluginsChartFactory;
import io.dataease.extensions.view.filter.FilterTreeObj;
import io.dataease.extensions.view.plugin.AbstractChartPlugin;
import io.dataease.extensions.view.util.FieldUtil;
import io.dataease.i18n.Translator;
import io.dataease.result.ResultCode;
import io.dataease.system.manage.CorePermissionManage;
import io.dataease.utils.AuthUtils;
import io.dataease.utils.BeanUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 图表数据管理类
 * 负责图表数据的计算、生成和转换，是图表模块的核心业务逻辑处理类
 * 协调数据集、SQL引擎、权限控制、图表处理器等多个模块完成图表数据渲染
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>图表数据计算和生成（calcData）</li>
 *   <li>SQL构建和执行</li>
 *   <li>数据权限过滤（行权限和列权限）</li>
 *   <li>数据脱敏处理</li>
 *   <li>同比环比计算</li>
 *   <li>图表插件集成</li>
 *   <li>跨数据源查询支持</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>仪表板图表数据加载</li>
 *   <li>图表编辑器预览</li>
 *   <li>Excel数据导出</li>
 *   <li>大屏数据展示</li>
 * </ul>
 *
 * @author Junjun
 */
@Component
public class ChartDataManage {
    @Resource
    private DatasetTableFieldManage datasetTableFieldManage;
    @Resource
    private DatasetGroupManage datasetGroupManage;
    @Resource
    private DatasetSQLManage datasetSQLManage;
    @Resource
    private ChartViewManege chartViewManege;
    @Resource
    private PermissionManage permissionManage;
    @Resource
    private ChartFilterTreeService chartFilterTreeService;
    @Resource
    private ChartHandlerManager chartHandlerManager;

    @Resource
    private CorePermissionManage corePermissionManage;
    @Autowired(required = false)
    private PluginManageApi pluginManage;

    public static final String START_END_SEPARATOR = "_START_END_SPLIT";

    private static final Logger logger = LoggerFactory.getLogger(ChartDataManage.class);

    /**
     * 计算图表数据
     * 图表数据计算的核心方法，协调图表处理器完成数据处理和格式化
     *
     * @param view 图表视图信息，包含图表类型、字段配置、过滤条件等
     * @return 计算后的图表视图，包含数据、字段、样式等信息
     * @throws Exception 数据计算异常
     */
    public ChartViewDTO calcData(ChartViewDTO view) throws Exception {
        ChartExtRequest chartExtRequest = view.getChartExtRequest();
        if (chartExtRequest == null) {
            chartExtRequest = new ChartExtRequest();
        }

        if (ObjectUtils.isEmpty(view)) {
            DEException.throwException(ResultCode.DATA_IS_WRONG.code(), Translator.get("i18n_chart_delete"));
        }
        if (ObjectUtils.isNotEmpty(AuthUtils.getUser())) {
            chartExtRequest.setUser(AuthUtils.getUser().getUserId());
        }
        if (view.getChartExtRequest() == null) {
            view.setChartExtRequest(chartExtRequest);
        }

        //excel导出，如果是从仪表板获取图表数据，则仪表板的查询模式，查询结果的数量，覆盖图表对应的属性
        if (view.getIsExcelExport()) {
            view.setResultMode(ChartConstants.VIEW_RESULT_MODE.CUSTOM);
        } else if (ChartConstants.VIEW_RESULT_MODE.CUSTOM.equals(chartExtRequest.getResultMode())) {
            view.setResultMode(chartExtRequest.getResultMode());
            view.setResultCount(chartExtRequest.getResultCount());
        }

        AbstractChartPlugin chartHandler;
        if (BooleanUtils.isTrue(view.getIsPlugin())) {
            chartHandler = PluginsChartFactory.getInstance(view.getRender(), view.getType());
        } else {
            chartHandler = chartHandlerManager.getChartHandler(view.getRender(), view.getType());
        }
        if (chartHandler == null) {
            DEException.throwException(ResultCode.DATA_IS_WRONG.code(), Translator.get("i18n_chart_not_handler") + ": " + view.getRender() + "," + view.getType());
        }

        var drillAxis = new ArrayList<ChartViewFieldDTO>();
        DatasetGroupInfoDTO table = datasetGroupManage.getDatasetGroupInfoDTO(view.getTableId(), null);
        if (table == null) {
            DEException.throwException(ResultCode.DATA_IS_WRONG.code(), Translator.get("i18n_no_ds"));
        }
        // check permission
        BusiPerCheckDTO dto = new BusiPerCheckDTO();
        dto.setId(table.getId());
        dto.setAuthEnum(AuthEnum.READ);
        boolean checked = corePermissionManage.checkAuth(dto);
        if (!checked) {
            DEException.throwException(Translator.get("i18n_no_dataset_permission"));
        }

        List<ChartViewFieldDTO> allFields = getAllChartFields(view);
        // column permission
        Map<String, ColumnPermissionItem> desensitizationList = new HashMap<>();
        List<DatasetTableFieldDTO> columnPermissionFields = permissionManage.filterColumnPermissions(transFields(allFields), desensitizationList, table.getId(), chartExtRequest.getUser());
        // row permission
        List<DataSetRowPermissionsTreeDTO> rowPermissionsTree = permissionManage.getRowPermissionsTree(table.getId(), chartExtRequest.getUser());
        //将没有权限的列删掉
        List<String> dataeaseNames = columnPermissionFields.stream().map(DatasetTableFieldDTO::getDataeaseName).collect(Collectors.toList());
        //计数字段
        dataeaseNames.add("*");
        AxisFormatResult formatResult = chartHandler.formatAxis(view);
        formatResult.getContext().put("dataset", table);
        formatResult.getContext().put("desensitizationList", desensitizationList);
        var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
        var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
        formatResult.getContext().put("allFields", allFields);
        var axisMap = formatResult.getAxisMap();
        axisMap.forEach((axis, fields) -> fields.removeIf(fieldDTO -> !dataeaseNames.contains(fieldDTO.getDataeaseName())));

        // 过滤来自仪表板的条件
        List<ChartExtFilterDTO> extFilterList = new ArrayList<>();
        //组件过滤条件
        List<SqlVariableDetails> sqlVariables = datasetGroupManage.getSqlParams(Collections.singletonList(view.getTableId()));
        if (ObjectUtils.isNotEmpty(chartExtRequest.getFilter())) {
            for (ChartExtFilterDTO request : chartExtRequest.getFilter()) {
                // 解析多个fieldId,fieldId是一个逗号分隔的字符串
                String fieldId = request.getFieldId();
                if (request.getIsTree() == null) {
                    request.setIsTree(false);
                }

                boolean hasParameters = false;
                if (CollectionUtils.isNotEmpty(sqlVariables)) {
                    for (SqlVariableDetails parameter : Optional.ofNullable(request.getParameters()).orElse(new ArrayList<>())) {
                        String parameterId = StringUtils.endsWith(parameter.getId(), START_END_SEPARATOR) ? parameter.getId().split(START_END_SEPARATOR)[0] : parameter.getId();
                        if (sqlVariables.stream().map(SqlVariableDetails::getId).collect(Collectors.toList()).contains(parameterId)) {
                            hasParameters = true;
                        }
                    }
                }

                if (hasParameters) {
                    continue;
                }

                if (StringUtils.isNotEmpty(fieldId)) {
                    List<Long> fieldIds = Arrays.stream(fieldId.split(",")).map(Long::valueOf).collect(Collectors.toList());

                    if (request.getIsTree()) {
                        ChartExtFilterDTO filterRequest = new ChartExtFilterDTO();
                        BeanUtils.copyBean(filterRequest, request);
                        filterRequest.setFilterType(0);
                        filterRequest.setDatasetTableFieldList(new ArrayList<>());
                        for (Long fId : fieldIds) {
                            DatasetTableFieldDTO datasetTableField = datasetTableFieldManage.selectById(fId);
                            if (datasetTableField == null) {
                                continue;
                            }
                            if (Objects.equals(datasetTableField.getDatasetGroupId(), view.getTableId())) {
                                if (ObjectUtils.isNotEmpty(filterRequest.getViewIds())) {
                                    if (filterRequest.getViewIds().contains(view.getId())) {
                                        filterRequest.getDatasetTableFieldList().add(datasetTableField);
                                    }
                                } else {
                                    filterRequest.getDatasetTableFieldList().add(datasetTableField);
                                }
                            }
                        }
                        if (ObjectUtils.isNotEmpty(filterRequest.getDatasetTableFieldList())) {
                            extFilterList.add(filterRequest);
                        }
                    } else {
                        for (Long fId : fieldIds) {
                            ChartExtFilterDTO filterRequest = new ChartExtFilterDTO();
                            BeanUtils.copyBean(filterRequest, request);
                            filterRequest.setFilterType(0);
                            filterRequest.setFieldId(fId + "");

                            DatasetTableFieldDTO datasetTableField = datasetTableFieldManage.selectById(fId);
                            if (datasetTableField == null) {
                                continue;
                            }
                            filterRequest.setDatasetTableField(datasetTableField);
                            if (Objects.equals(datasetTableField.getDatasetGroupId(), view.getTableId())) {
                                if (ObjectUtils.isNotEmpty(filterRequest.getViewIds())) {
                                    if (filterRequest.getViewIds().contains(view.getId())) {
                                        extFilterList.add(filterRequest);
                                    }
                                } else {
                                    extFilterList.add(filterRequest);
                                }
                            }
                        }
                    }
                }
            }
        }

        List<ChartExtFilterDTO> filters = new ArrayList<>();
        FilterTreeObj customLinkageFilter = null;
        // 联动条件
        if (ObjectUtils.isNotEmpty(chartExtRequest.getLinkageFilters())) {
            for (ChartExtFilterDTO linkageFilter : chartExtRequest.getLinkageFilters()) {
                if(linkageFilter != null){
                    if (3 == linkageFilter.getFilterType()) {
                        customLinkageFilter = linkageFilter.getCustomFilter();
                    } else {
                        filters.add(linkageFilter);
                    }
                }
            }
        }
        // 外部参数条件
        if (ObjectUtils.isNotEmpty(chartExtRequest.getOuterParamsFilters())) {
            filters.addAll(chartExtRequest.getOuterParamsFilters());
        }

        // web参数条件
        if (ObjectUtils.isNotEmpty(chartExtRequest.getWebParamsFilters())) {
            filters.addAll(chartExtRequest.getWebParamsFilters());
        }


        //联动过滤条件和外部参数过滤条件全部加上
        if (ObjectUtils.isNotEmpty(filters)) {
            for (ChartExtFilterDTO request : filters) {
                // 包含 DE 的为数据集参数
                if (request.getFieldId().contains("DE")) {
                    // 组装sql 参数原始数据
                    if (CollectionUtils.isNotEmpty(sqlVariables)) {
                        for (SqlVariableDetails sourceVariables : sqlVariables) {
                            if (sourceVariables.getId().equals(request.getFieldId())) {
                                if (CollectionUtils.isEmpty(request.getParameters())) {
                                    request.setParameters(new ArrayList<>());
                                }
                                request.getParameters().add(sourceVariables);
                            }
                        }

                    }
                } else {
                    DatasetTableFieldDTO datasetTableField = datasetTableFieldManage.selectById(Long.valueOf(request.getFieldId()));
                    request.setDatasetTableField(datasetTableField);
                    request.setFilterType(2);
                    // 相同数据集
                    if (Objects.equals(datasetTableField.getDatasetGroupId(), view.getTableId())) {
                        if (ObjectUtils.isNotEmpty(request.getViewIds())) {
                            if (request.getViewIds().contains(view.getId())) {
                                extFilterList.add(request);
                            }
                        } else {
                            extFilterList.add(request);
                        }
                    }

                }
            }
        }

        // 下钻
        List<ChartDrillRequest> drillRequestList = chartExtRequest.getDrill();
        var drill = formatResult.getAxisMap().get(ChartAxis.drill);
        if (ObjectUtils.isNotEmpty(drillRequestList) && (drill.size() > drillRequestList.size())) {
            var fields = xAxis.stream().map(ChartViewFieldDTO::getId).collect(Collectors.toSet());
            for (int i = 0; i < drillRequestList.size(); i++) {
                ChartDrillRequest request = drillRequestList.get(i);
                for (ChartDimensionDTO dim : request.getDimensionList()) {
                    ChartViewFieldDTO viewField = drill.get(i);
                    // 将钻取值作为条件传递，将所有钻取字段作为xAxis并加上下一个钻取字段
                    if (Objects.equals(dim.getId(), viewField.getId())) {
                        DatasetTableFieldDTO datasetTableField = datasetTableFieldManage.selectById(dim.getId());
                        ChartViewFieldDTO d = new ChartViewFieldDTO();
                        BeanUtils.copyBean(d, datasetTableField);

                        ChartExtFilterDTO drillFilter = new ChartExtFilterDTO();
                        drillFilter.setFieldId(String.valueOf(dim.getId()));
                        drillFilter.setDatasetTableField(datasetTableField);
                        drillFilter.setDatePattern(viewField.getDatePattern());
                        drillFilter.setDateStyle(viewField.getDateStyle());
                        drillFilter.setFilterType(1);
                        if (datasetTableField.getDeType() == 1) {
                            drillFilter.setOperator("between");
                            drillFilter.setOriginValue(Collections.singletonList(dim.getValue()));
                            // 把value类似过滤组件处理，获得start time和end time
                            Map<String, Long> stringLongMap = Utils.parseDateTimeValue(dim.getValue());
                            drillFilter.setValue(Arrays.asList(String.valueOf(stringLongMap.get("startTime")), String.valueOf(stringLongMap.get("endTime"))));
                        } else {
                            drillFilter.setOperator("in");
                            drillFilter.setValue(Collections.singletonList(dim.getValue()));
                        }
                        extFilterList.add(drillFilter);

                        if (!fields.contains(dim.getId())) {
                            viewField.setSource(FieldSource.DRILL);
                            xAxis.add(viewField);
                            drillAxis.add(viewField);
                            fields.add(dim.getId());
                        }
                        if (i == drillRequestList.size() - 1) {
                            ChartViewFieldDTO nextDrillField = drill.get(i + 1);
                            if (!fields.contains(nextDrillField.getId())) {
                                nextDrillField.setSource(FieldSource.DRILL);
                                xAxis.add(nextDrillField);
                                drillAxis.add(nextDrillField);
                                fields.add(nextDrillField.getId());
                            } else {
                                Optional<ChartViewFieldDTO> axis = xAxis.stream().filter(x -> Objects.equals(x.getId(), nextDrillField.getId())).findFirst();
                                axis.ifPresent(field -> {
                                    field.setSort(nextDrillField.getSort());
                                    field.setCustomSort(nextDrillField.getCustomSort());
                                });
                                drillAxis.add(nextDrillField);
                            }
                        }
                    }
                }
            }
        }

        formatResult.getContext().put("drillAxis", drillAxis);

        //转义特殊字符
        extFilterList.forEach(ele -> {
            if (ObjectUtils.isNotEmpty(ele.getValue())) {
                List<String> collect = ele.getValue().stream().map(SQLUtils::transKeyword).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(ele.getOriginValue())) {
                    ele.setOriginValue(ele.getValue());
                }
                ele.setValue(collect);
            }
        });
        // 视图自定义过滤逻辑
        CustomFilterResult filterResult = chartHandler.customFilter(view, extFilterList, formatResult);

        if (ObjectUtils.isEmpty(xAxis) && ObjectUtils.isEmpty(yAxis)) {
            return emptyChartViewDTO(view);
        }
        // 字段过滤器
        FilterTreeObj fieldCustomFilter = view.getCustomFilter();
        // 指标表联动时 使用的CustomFilter
        if (customLinkageFilter != null) {
            fieldCustomFilter = customLinkageFilter;
        }
        chartFilterTreeService.searchFieldAndSet(fieldCustomFilter);
        fieldCustomFilter = chartFilterTreeService.charReplace(fieldCustomFilter);
        // 获取dsMap,union sql
        Map<String, Object> sqlMap = datasetSQLManage.getUnionSQLForEdit(table, chartExtRequest);
        String sql = (String) sqlMap.get("sql");
        Map<Long, DatasourceSchemaDTO> dsMap = (Map<Long, DatasourceSchemaDTO>) sqlMap.get("dsMap");
        boolean crossDs = table.getIsCross();
        if (!crossDs) {
            sql = Utils.replaceSchemaAlias(sql, dsMap);
        }

        if (ObjectUtils.isEmpty(dsMap)) {
            DEException.throwException(ResultCode.DATA_IS_WRONG.code(), Translator.get("i18n_datasource_delete"));
        }
        for (Map.Entry<Long, DatasourceSchemaDTO> next : dsMap.entrySet()) {
            DatasourceSchemaDTO ds = next.getValue();
            if (StringUtils.isNotEmpty(ds.getStatus()) && "Error".equalsIgnoreCase(ds.getStatus())) {
                DEException.throwException(ResultCode.DATA_IS_WRONG.code(), Translator.get("i18n_invalid_ds"));
            }
        }

        Provider provider;
        if (crossDs) {
            provider = ProviderFactory.getDefaultProvider();
        } else {
            provider = ProviderFactory.getProvider(dsMap.entrySet().iterator().next().getValue().getType());
        }

        if (ObjectUtils.isEmpty(view.getCalParams())) {
            view.setCalParams(Utils.getParams(transFields(allFields)));
        }

        SQLMeta sqlMeta = new SQLMeta();
        Table2SQLObj.table2sqlobj(sqlMeta, null, "(" + sql + ")", crossDs);
        CustomWhere2Str.customWhere2sqlObj(sqlMeta, fieldCustomFilter, transFields(allFields), crossDs, dsMap, Utils.getParams(transFields(allFields)), view.getCalParams(), pluginManage);
        ExtWhere2Str.extWhere2sqlOjb(sqlMeta, extFilterList, transFields(allFields), crossDs, dsMap, Utils.getParams(transFields(allFields)), view.getCalParams(), pluginManage);
        WhereTree2Str.transFilterTrees(sqlMeta, rowPermissionsTree, transFields(allFields), crossDs, dsMap, Utils.getParams(transFields(allFields)), view.getCalParams(), pluginManage);
        // TODO 數據源插件化之後放到插件裡面組裝SQL
        if (BooleanUtils.isTrue(view.getIsPlugin())) {
            List<String> dsList = new ArrayList<>();
            for (Map.Entry<Long, DatasourceSchemaDTO> next : dsMap.entrySet()) {
                dsList.add(next.getValue().getType());
            }
            boolean needOrder = Utils.isNeedOrder(dsList);
            Dimension2SQLObj.dimension2sqlObj(sqlMeta, xAxis, FieldUtil.transFields(allFields), crossDs, dsMap, Utils.getParams(FieldUtil.transFields(allFields)), view.getCalParams(), pluginManage);
            Quota2SQLObj.quota2sqlObj(sqlMeta, yAxis, FieldUtil.transFields(allFields), crossDs, dsMap, Utils.getParams(FieldUtil.transFields(allFields)), view.getCalParams(), pluginManage);
            String querySql = SQLProvider.createQuerySQL(sqlMeta, true, needOrder, view);
            querySql = provider.rebuildSQL(querySql, sqlMeta, crossDs, dsMap);
            filterResult.getContext().put("querySql", querySql);
        }

        ChartCalcDataResult calcResult = chartHandler.calcChartResult(view, formatResult, filterResult, sqlMap, sqlMeta, provider);
        return chartHandler.buildChart(view, calcResult, formatResult, filterResult);
    }

    /**
     * 获取图表动态大小字段
     * 从图表配置中提取仪表盘最小值、最大值和水球图最大值等动态字段配置
     *
     * @param view 图表视图信息
     * @return 动态字段列表
     * @throws Exception 字段获取异常
     */
    private List<ChartViewFieldDTO> getSizeField(ChartViewDTO view) throws Exception {
        List<ChartViewFieldDTO> list = new ArrayList<>();
        Map<String, Object> customAttr = view.getCustomAttr();

        Map<String, Object> size = (Map<String, Object>) customAttr.get("misc");

        ChartViewFieldDTO gaugeMinViewField = getDynamicField(size, "gaugeMinType", "gaugeMinField");
        if (gaugeMinViewField != null) {
            list.add(gaugeMinViewField);
        }
        ChartViewFieldDTO gaugeMaxViewField = getDynamicField(size, "gaugeMaxType", "gaugeMaxField");
        if (gaugeMaxViewField != null) {
            list.add(gaugeMaxViewField);
        }
        ChartViewFieldDTO liquidMaxViewField = getDynamicField(size, "liquidMaxType", "liquidMaxField");
        if (liquidMaxViewField != null) {
            list.add(liquidMaxViewField);
        }

        return list;
    }

    /**
     * 获取动态字段
     * 根据配置的类型和字段key获取动态字段信息，用于仪表盘等图表的动态极值配置
     *
     * @param sizeObj 图表大小配置对象
     * @param type 配置类型（如gaugeMinType、gaugeMaxType等）
     * @param field 字段key（如gaugeMinField、gaugeMaxField等）
     * @return 动态字段对象，如果不是动态类型则返回null
     */
    private ChartViewFieldDTO getDynamicField(Map<String, Object> sizeObj, String type, String field) {
        String maxType = (String) sizeObj.get(type);
        if (StringUtils.equalsIgnoreCase("dynamic", maxType)) {
            Map<String, Object> maxField = (Map<String, Object>) sizeObj.get(field);
            Long id = Long.valueOf((String) maxField.get("id"));
            String summary = (String) maxField.get("summary");
            DatasetTableFieldDTO datasetTableField = datasetTableFieldManage.selectById(id);
            if (ObjectUtils.isNotEmpty(datasetTableField)) {
                if (datasetTableField.getDeType() == 0 || datasetTableField.getDeType() == 1 || datasetTableField.getDeType() == 5) {
                    if (!StringUtils.containsIgnoreCase(summary, "count")) {
                        DEException.throwException(Translator.get("i18n_gauge_field_change"));
                    }
                }
                ChartViewFieldDTO dto = new ChartViewFieldDTO();
                BeanUtils.copyBean(dto, datasetTableField);
                dto.setSummary(summary);
                return dto;
            } else {
                DEException.throwException(Translator.get("i18n_gauge_field_delete"));
            }
        }
        return null;
    }

    /**
     * 创建空的图表视图DTO
     * 克隆一个图表视图对象，用于返回空数据场景
     *
     * @param view 原始图表视图对象
     * @return 克隆的空图表视图对象
     */
    private ChartViewDTO emptyChartViewDTO(ChartViewDTO view) {
        ChartViewDTO dto = new ChartViewDTO();
        BeanUtils.copyBean(dto, view);
        return dto;
    }

    /**
     * 获取钻取字段的排序方式
     * 从X轴字段列表中查找指定字段的排序配置
     *
     * @param xAxis X轴字段列表
     * @param field 要查找的字段
     * @return 排序方式（asc/desc），未找到返回空字符串
     */
    private String getDrillSort(List<ChartViewFieldDTO> xAxis, ChartViewFieldDTO field) {
        String res = "";
        for (ChartViewFieldDTO f : xAxis) {
            if (Objects.equals(f.getId(), field.getId())) {
                if (StringUtils.equalsIgnoreCase(f.getSort(), "asc") || StringUtils.equalsIgnoreCase(f.getSort(), "desc")) {
                    res = f.getSort();
                    break;
                }
            }
        }
        return res;
    }

    /**
     * 字段类型转换
     * 将图表视图字段转换为数据集表字段DTO
     *
     * @param list 图表视图字段列表
     * @return 数据集表字段DTO列表
     */
    private List<DatasetTableFieldDTO> transFields(List<? extends ChartViewFieldBaseDTO> list) {
        return list.stream().map(ele -> {
            DatasetTableFieldDTO dto = new DatasetTableFieldDTO();
            BeanUtils.copyBean(dto, ele);
            return dto;
        }).collect(Collectors.toList());
    }

    // 对结果排序
    /**
     * 结果自定义排序
     * 根据X轴字段对数据进行自定义排序
     *
     * @param xAxis X轴字段列表
     * @param data 原始数据
     * @return 排序后的数据
     */
    public List<String[]> resultCustomSort(List<ChartViewFieldDTO> xAxis, List<String[]> data) {
        List<String[]> res = new ArrayList<>(data);
        if (xAxis.size() > 0) {
            // 找到对应维度
            for (int i = 0; i < xAxis.size(); i++) {
                ChartViewFieldDTO item = xAxis.get(i);
                if (StringUtils.equalsIgnoreCase(item.getSort(), "custom_sort")) {
                    // 获取自定义值与data对应列的结果
                    if (i > 0) {
                        // 首先根据优先级高的字段分类，在每个前置字段相同的组里排序
                        Map<String, List<String[]>> map = new LinkedHashMap<>();
                        for (String[] d : res) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int j = 0; j < i; j++) {
                                if (StringUtils.equalsIgnoreCase(xAxis.get(j).getSort(), "none")) {
                                    continue;
                                }
                                stringBuilder.append(d[j]);
                            }
                            if (ObjectUtils.isEmpty(map.get(stringBuilder.toString()))) {
                                map.put(stringBuilder.toString(), new ArrayList<>());
                            }
                            map.get(stringBuilder.toString()).add(d);
                        }
                        Iterator<Map.Entry<String, List<String[]>>> iterator = map.entrySet().iterator();
                        List<String[]> list = new ArrayList<>();
                        while (iterator.hasNext()) {
                            Map.Entry<String, List<String[]>> next = iterator.next();
                            list.addAll(customSort(Optional.ofNullable(item.getCustomSort()).orElse(new ArrayList<>()), next.getValue(), i));
                        }
                        res.clear();
                        res.addAll(list);
                    } else {
                        res = customSort(Optional.ofNullable(item.getCustomSort()).orElse(new ArrayList<>()), res, i);
                    }
                }
            }
        }
        return res;
    }

    /**
     * 自定义排序
     * 按照指定的自定义顺序对数据进行排序
     *
     * @param custom 自定义排序的值列表
     * @param data 原始数据
     * @param index 排序依据的列索引
     * @return 排序后的数据
     */
    public List<String[]> customSort(List<String> custom, List<String[]> data, int index) {
        List<String[]> res = new ArrayList<>();

        List<Integer> indexArr = new ArrayList<>();
        List<String[]> joinArr = new ArrayList<>();
        for (int i = 0; i < custom.size(); i++) {
            String ele = custom.get(i);
            for (int j = 0; j < data.size(); j++) {
                String[] d = data.get(j);
                if (StringUtils.equalsIgnoreCase(ele, d[index])) {
                    joinArr.add(d);
                    indexArr.add(j);
                }
            }
        }
        // 取得 joinArr 就是两者的交集
        List<Integer> indexArrData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            indexArrData.add(i);
        }
        List<Integer> indexResult = new ArrayList<>();
        for (int i = 0; i < indexArrData.size(); i++) {
            if (!indexArr.contains(indexArrData.get(i))) {
                indexResult.add(indexArrData.get(i));
            }
        }

        List<String[]> subArr = new ArrayList<>();
        for (int i = 0; i < indexResult.size(); i++) {
            subArr.add(data.get(indexResult.get(i)));
        }
        res.addAll(joinArr);
        res.addAll(subArr);
        return res;
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
    public List<String> getFieldData(ChartViewDTO view, Long fieldId, String fieldType) throws Exception {
        ChartExtRequest requestList = view.getChartExtRequest();
        List<String[]> sqlData = sqlData(view, requestList, fieldId);
        List<ChartViewFieldDTO> fieldList = new ArrayList<>();
        switch (fieldType) {
            case "xAxis" -> fieldList = view.getXAxis();
            case "xAxisExt" -> fieldList = view.getXAxisExt();
            case "extStack" -> fieldList = view.getExtStack();
        }
        DatasetTableFieldDTO field = datasetTableFieldManage.selectById(fieldId);

        List<String> res = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(field) && fieldList.size() > 0) {
            // 找到对应维度
            ChartViewFieldDTO chartViewFieldDTO = null;
            int index = 0;
            int getIndex = 0;
            for (int i = 0; i < fieldList.size(); i++) {
                ChartViewFieldDTO item = fieldList.get(i);
                if (StringUtils.equalsIgnoreCase(item.getSort(), "custom_sort")) {// 此处与已有的自定义字段对比
                    chartViewFieldDTO = item;
                    index = i;
                }
                if (Objects.equals(item.getId(), field.getId())) {// 获得当前自定义的字段
                    getIndex = i;
                }
            }
            if (StringUtils.equalsIgnoreCase(fieldType, "xAxisExt")) {
                List<ChartViewFieldDTO> xAxis = view.getXAxis();
                index += xAxis.size();
                getIndex += xAxis.size();
            }
            if (StringUtils.equalsIgnoreCase(fieldType, "extStack")) {
                int xAxisSize = CollectionUtils.size(view.getXAxis());
                int extSize = CollectionUtils.size(view.getXAxisExt());
                index += xAxisSize + extSize;
                getIndex += xAxisSize + extSize;
            }

            List<String[]> sortResult = resultCustomSort(fieldList, sqlData);
            if (ObjectUtils.isNotEmpty(chartViewFieldDTO) && (getIndex >= index)) {
                // 获取自定义值与data对应列的结果
                List<String[]> strings = customSort(Optional.ofNullable(chartViewFieldDTO.getCustomSort()).orElse(new ArrayList<>()), sortResult, index);
                for (int i = 0; i < strings.size(); i++) {
                    res.add(strings.get(i)[getIndex]);
                }
            } else {
                // 返回请求结果
                for (int i = 0; i < sortResult.size(); i++) {
                    res.add(sortResult.get(i)[getIndex]);
                }
            }
        }
        return res.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 执行SQL查询获取字段数据
     * 通过SQL查询获取字段的枚举值
     *
     * @param view 图表视图信息
     * @param requestList 扩展请求参数
     * @param fieldId 字段ID
     * @return 查询结果数据
     * @throws Exception SQL执行异常
     */
    public List<String[]> sqlData(ChartViewDTO view, ChartExtRequest requestList, Long fieldId) throws Exception {
        if (ObjectUtils.isEmpty(view)) {
            DEException.throwException(Translator.get("i18n_chart_delete"));
        }

        // get all fields
        List<ChartViewFieldDTO> allFields = getAllChartFields(view);

        // 针对分组切换堆叠时会遇到的问题
        if (StringUtils.equalsIgnoreCase(view.getType(), "bar-stack") || StringUtils.equalsIgnoreCase(view.getType(), "chart-mix-stack")) {
            view.setXAxisExt(new ArrayList<>());
        }

        List<ChartViewFieldDTO> xAxis = new ArrayList<>(view.getXAxis());
        List<ChartViewFieldDTO> xAxisExt = new ArrayList<>(view.getXAxisExt());
        if (StringUtils.equalsIgnoreCase(view.getType(), "table-pivot")
                || StringUtils.containsIgnoreCase(view.getType(), "group")
                || ("antv".equalsIgnoreCase(view.getRender()) && "line".equalsIgnoreCase(view.getType()))
                || StringUtils.equalsIgnoreCase(view.getType(), "flow-map")
                || StringUtils.equalsIgnoreCase(view.getType(), "t-heatmap")
                || StringUtils.equalsIgnoreCase(view.getType(), "sankey")
        ) {
            xAxis.addAll(xAxisExt);
        }
        List<ChartViewFieldDTO> yAxis = new ArrayList<>(view.getYAxis());
        if (StringUtils.containsIgnoreCase(view.getType(), "chart-mix")) {
            List<ChartViewFieldDTO> yAxisExt = new ArrayList<>(view.getYAxisExt());
            yAxis.addAll(yAxisExt);
        }
        if (StringUtils.equalsIgnoreCase(view.getRender(), "antv") && StringUtils.equalsAnyIgnoreCase(view.getType(), "gauge", "liquid")) {
            List<ChartViewFieldDTO> sizeField = getSizeField(view);
            yAxis.addAll(sizeField);
        }
        List<ChartViewFieldDTO> extStack = new ArrayList<>(view.getExtStack());
        List<ChartViewFieldDTO> extBubble = new ArrayList<>(view.getExtBubble());
        FilterTreeObj fieldCustomFilter = view.getCustomFilter();
        List<ChartViewFieldDTO> drill = new ArrayList<>(view.getDrillFields());

        // 获取数据集,需校验权限
        DatasetGroupInfoDTO table = datasetGroupManage.getDatasetGroupInfoDTO(view.getTableId(), null);
        Map<String, ColumnPermissionItem> desensitizationList = new HashMap<>();
        List<DataSetRowPermissionsTreeDTO> rowPermissionsTree = permissionManage.getRowPermissionsTree(table.getId(), view.getChartExtRequest().getUser());

        chartFilterTreeService.searchFieldAndSet(fieldCustomFilter);

        if (ObjectUtils.isEmpty(xAxis) && ObjectUtils.isEmpty(yAxis)) {
            return new ArrayList<String[]>();
        }

        switch (view.getType()) {
            case "label":
                yAxis = new ArrayList<>();
                if (ObjectUtils.isEmpty(xAxis)) {
                    return new ArrayList<String[]>();
                }
                break;
            case "indicator":
            case "gauge":
            case "liquid":
                xAxis = new ArrayList<>();
                if (ObjectUtils.isEmpty(yAxis)) {
                    return new ArrayList<String[]>();
                }
                break;
            case "table-info":
                yAxis = new ArrayList<>();
                if (ObjectUtils.isEmpty(xAxis)) {
                    return new ArrayList<String[]>();
                }
                break;
            case "table-normal":
                break;
            case "bar-group":
            case "bar-group-stack":
            case "flow-map":
                break;
            default:
        }

        // 获取dsMap,union sql
        Map<String, Object> sqlMap = datasetSQLManage.getUnionSQLForEdit(table, null);
        String sql = (String) sqlMap.get("sql");
        Map<Long, DatasourceSchemaDTO> dsMap = (Map<Long, DatasourceSchemaDTO>) sqlMap.get("dsMap");
        List<String> dsList = new ArrayList<>();
        for (Map.Entry<Long, DatasourceSchemaDTO> next : dsMap.entrySet()) {
            dsList.add(next.getValue().getType());
        }
        boolean needOrder = Utils.isNeedOrder(dsList);
        boolean crossDs = table.getIsCross();
        if (!crossDs) {
            sql = Utils.replaceSchemaAlias(sql, dsMap);
        }

        // 调用数据源的calcite获得data
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setDsList(dsMap);
        datasourceRequest.setIsCross(crossDs);

        Provider provider;
        if (crossDs) {
            provider = ProviderFactory.getDefaultProvider();
        } else {
            provider = ProviderFactory.getProvider(dsMap.entrySet().iterator().next().getValue().getType());
        }

        List<String[]> data = new ArrayList<>();

        String querySql = null;
        //如果不是插件图表 走原生逻辑
        if (table.getMode() == 0) {// 直连
            if (ObjectUtils.isEmpty(dsMap)) {
                DEException.throwException(Translator.get("i18n_datasource_delete"));
            }
            for (Map.Entry<Long, DatasourceSchemaDTO> next : dsMap.entrySet()) {
                DatasourceSchemaDTO ds = next.getValue();
                if (StringUtils.isNotEmpty(ds.getStatus()) && "Error".equalsIgnoreCase(ds.getStatus())) {
                    DEException.throwException(Translator.get("i18n_invalid_ds"));
                }
            }

            SQLMeta sqlMeta = new SQLMeta();
            Table2SQLObj.table2sqlobj(sqlMeta, null, "(" + sql + ")", crossDs);
            WhereTree2Str.transFilterTrees(sqlMeta, rowPermissionsTree, transFields(allFields), crossDs, dsMap, Utils.getParams(transFields(allFields)), view.getCalParams(), pluginManage);

            if (StringUtils.equalsAnyIgnoreCase(view.getType(), "indicator", "gauge", "liquid")) {
                Quota2SQLObj.quota2sqlObj(sqlMeta, yAxis, transFields(allFields), crossDs, dsMap, Utils.getParams(transFields(allFields)), view.getCalParams(), pluginManage);
                querySql = SQLProvider.createQuerySQL(sqlMeta, true, needOrder, view);
            } else if (StringUtils.containsIgnoreCase(view.getType(), "stack")) {
                List<ChartViewFieldDTO> xFields = new ArrayList<>();
                xFields.addAll(xAxis);
                xFields.addAll(extStack);
                Dimension2SQLObj.dimension2sqlObj(sqlMeta, xFields, transFields(allFields), crossDs, dsMap, Utils.getParams(transFields(allFields)), view.getCalParams(), pluginManage);
                Quota2SQLObj.quota2sqlObj(sqlMeta, yAxis, transFields(allFields), crossDs, dsMap, Utils.getParams(transFields(allFields)), view.getCalParams(), pluginManage);
                querySql = SQLProvider.createQuerySQL(sqlMeta, true, needOrder, view);
            } else if (StringUtils.containsIgnoreCase(view.getType(), "scatter")) {
                List<ChartViewFieldDTO> yFields = new ArrayList<>();
                yFields.addAll(yAxis);
                yFields.addAll(extBubble);
                Dimension2SQLObj.dimension2sqlObj(sqlMeta, xAxis, transFields(allFields), crossDs, dsMap, Utils.getParams(transFields(allFields)), view.getCalParams(), pluginManage);
                Quota2SQLObj.quota2sqlObj(sqlMeta, yFields, transFields(allFields), crossDs, dsMap, Utils.getParams(transFields(allFields)), view.getCalParams(), pluginManage);
                querySql = SQLProvider.createQuerySQL(sqlMeta, true, needOrder, view);
            } else if (StringUtils.equalsIgnoreCase("table-info", view.getType())) {
                Dimension2SQLObj.dimension2sqlObj(sqlMeta, xAxis, transFields(allFields), crossDs, dsMap, Utils.getParams(transFields(allFields)), view.getCalParams(), pluginManage);
                querySql = SQLProvider.createQuerySQL(sqlMeta, false, true, view);
            } else {
                Dimension2SQLObj.dimension2sqlObj(sqlMeta, xAxis, transFields(allFields), crossDs, dsMap, Utils.getParams(transFields(allFields)), view.getCalParams(), pluginManage);
                Quota2SQLObj.quota2sqlObj(sqlMeta, yAxis, transFields(allFields), crossDs, dsMap, Utils.getParams(transFields(allFields)), view.getCalParams(), pluginManage);
                querySql = SQLProvider.createQuerySQL(sqlMeta, true, needOrder, view);
            }

            querySql = provider.rebuildSQL(querySql, sqlMeta, crossDs, dsMap);
            datasourceRequest.setQuery(querySql);
            logger.debug("calcite chart get field enum sql: " + querySql);

            data = (List<String[]>) provider.fetchResultField(datasourceRequest).get("data");
        }
        return data;
    }

    /**
     * 获取图表所有字段
     * 查询数据集的所有维度和度量字段，合并后返回
     *
     * @param view 图表视图信息
     * @return 所有可用字段的列表
     */
    private List<ChartViewFieldDTO> getAllChartFields(ChartViewDTO view) {
        // get all fields
        Map<String, List<ChartViewFieldDTO>> stringListMap = chartViewManege.listByDQ(view.getTableId(), view.getId(), view);
        List<ChartViewFieldDTO> dimensionList = stringListMap.get("dimensionList");
        List<ChartViewFieldDTO> quotaList = stringListMap.get("quotaList");
        List<ChartViewFieldDTO> allFields = new ArrayList<>();
        allFields.addAll(dimensionList);
        allFields.addAll(quotaList);
        return allFields.stream().filter(ele -> ele.getId() != -1L).collect(Collectors.toList());
    }

    /**
     * 从可视化保存图表视图
     * 批量保存仪表板中的图表视图
     *
     * @param checkData 校验数据
     * @param sceneId 场景ID（仪表板ID）
     * @param chartViewsInfo 图表视图信息Map
     */
    public void saveChartViewFromVisualization(String checkData, Long sceneId, Map<Long, ChartViewDTO> chartViewsInfo) {
        if (!MapUtils.isEmpty(chartViewsInfo)) {
            List<Long> disuseChartIdList = new ArrayList<>();
            chartViewsInfo.forEach((key, chartViewDTO) -> {
                if (checkData.contains(chartViewDTO.getId() + "")) {
                    try {
                        chartViewDTO.setSceneId(sceneId);
                        chartViewManege.save(chartViewDTO);
                    } catch (Exception e) {
                        DEException.throwException(e);
                    }
                } else {
                    disuseChartIdList.add(chartViewDTO.getId());
                }
            });
            // 阈值告警处理 统一在发布时处理
//            if (CollectionUtils.isNotEmpty(disuseChartIdList)) {
//                chartViewManege.disuse(disuseChartIdList);
//            }
        }
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
    public List<String> getDrillFieldData(ChartViewDTO view, Long fieldId) throws Exception {
        List<ChartViewFieldDTO> drillField = view.getDrillFields();
        ChartViewFieldDTO targetField = null;
        for (int i = 0; i < drillField.size(); i++) {
            ChartViewFieldDTO tmp = drillField.get(i);
            if (tmp.getId().equals(fieldId)) {
                targetField = tmp;
                break;
            }
        }
        if (targetField == null) {
            return Collections.emptyList();
        }
        view.setXAxis(Collections.singletonList(targetField));

        List<String[]> sqlData = sqlData(view, view.getChartExtRequest(), fieldId);
        List<String[]> result = customSort(Optional.ofNullable(targetField.getCustomSort()).orElse(new ArrayList<>()), sqlData, 0);
        return result.stream().map(i -> i[0]).distinct().collect(Collectors.toList());
    }

    /**
     * 编码图表数据
     * 对敏感字段和计算字段进行编码处理
     *
     * @param chartViewDTO 图表视图DTO
     */
    public void encodeData(ChartViewDTO chartViewDTO) {
        if (chartViewDTO.getData() != null) {
            if (chartViewDTO.getType().startsWith("chart-mix")) {
                DatasetUtils.listEncode((List<ChartViewFieldDTO>) ((Map<String, Object>) chartViewDTO.getData().get("left")).get("sourceFields"));
                DatasetUtils.listEncode((List<ChartViewFieldDTO>) ((Map<String, Object>) chartViewDTO.getData().get("right")).get("sourceFields"));
            } else {
                DatasetUtils.listEncode((List<ChartViewFieldDTO>) chartViewDTO.getData().get("sourceFields"));
            }
        }
    }
}
