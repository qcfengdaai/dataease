package io.dataease.chart.charts.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import io.dataease.api.dataset.union.DatasetGroupInfoDTO;
import io.dataease.engine.sql.SQLProvider;
import io.dataease.engine.trans.ExtWhere2Str;
import io.dataease.engine.utils.Utils;
import io.dataease.extensions.datasource.dto.DatasourceRequest;
import io.dataease.extensions.datasource.dto.DatasourceSchemaDTO;
import io.dataease.extensions.datasource.model.SQLMeta;
import io.dataease.extensions.datasource.provider.Provider;
import io.dataease.extensions.view.dto.*;
import io.dataease.extensions.view.util.FieldUtil;
import io.dataease.utils.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 带同环比计算的图表处理器
 */
public class YoyChartHandler extends DefaultChartHandler {
    /**
     * 自定义过滤器处理
     * 处理同环比图表的过滤器，将同环比时间过滤条件往前推一年
     *
     * @param view 图表视图信息
     * @param filterList 过滤条件列表
     * @param formatResult 坐标轴格式化结果
     * @return 自定义过滤结果，包含原始过滤条件和同环比过滤标识
     */
    @Override
    public <T extends CustomFilterResult> T customFilter(ChartViewDTO view, List<ChartExtFilterDTO> filterList, AxisFormatResult formatResult) {
        var result = super.customFilter(view, filterList, formatResult);
        var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
        String originFilterJson = (String) JsonUtil.toJSONString(filterList);
        // 如果设置了同环比的指标字段设置了过滤器，那就需要把该过滤器的时间往前回调一年
        // 计算完同环比之后，再把计算结果和原有的过滤结果对比，去除不该出现的前一年的数据
        boolean yoyFiltered = checkYoyFilter(filterList, yAxis);
        if (yoyFiltered) {
            List<ChartExtFilterDTO> originFilter = JsonUtil.parseList(originFilterJson, new TypeReference<>() {
            });
            formatResult.getContext().put("originFilter", originFilter);
            formatResult.getContext().put("yoyFiltered", true);
        }
        return (T) result;
    }

    /**
     * 构建图表结果
     * 根据是否设置了同环比过滤来决定返回原始数据还是标准数据
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param data 查询返回的原始数据
     * @return 格式化后的图表数据
     */
    @Override
    public Map<String, Object> buildResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, List<String[]> data) {
        var yoyFiltered = filterResult.getContext().get("yoyFiltered") != null;
        // 带过滤同环比直接返回原始数据,再由视图重新组装
        if (yoyFiltered) {
            var result = new HashMap<String, Object>();
            result.put("data", data);
            return result;
        }
        return buildNormalResult(view, formatResult, filterResult, data);
    }

    /**
     * 构建同环比类型的数据
     *
     * @param view         视图对象
     * @param formatResult 处理后的轴
     * @param filterResult 处理后的过滤器
     * @param data         原始数据
     * @return 视图构建结果
     */
    public Map<String, Object> buildNormalResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, List<String[]> data) {
        return super.buildResult(view, formatResult, filterResult, data);
    }

    /**
     * 计算图表数据结果
     * 执行同环比图表的数据计算，处理同环比过滤后的数据匹配
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param sqlMap SQL相关映射（包含数据源信息）
     * @param sqlMeta SQL元数据对象
     * @param provider 数据源提供者
     * @return 图表计算结果，包含同环比计算后的数据
     */
    @Override
    public <T extends ChartCalcDataResult> T calcChartResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, Map<String, Object> sqlMap, SQLMeta sqlMeta, Provider provider) {
        var dsMap = (Map<Long, DatasourceSchemaDTO>) sqlMap.get("dsMap");
        List<String> dsList = new ArrayList<>();
        for (Map.Entry<Long, DatasourceSchemaDTO> next : dsMap.entrySet()) {
            dsList.add(next.getValue().getType());
        }
        boolean needOrder = Utils.isNeedOrder(dsList);
        boolean crossDs = ((DatasetGroupInfoDTO) formatResult.getContext().get("dataset")).getIsCross();
        // 这里拿到的可能有一年前的数据
        var expandedResult = (T) super.calcChartResult(view, formatResult, filterResult, sqlMap, sqlMeta, provider);
        // 检查同环比过滤，拿到实际数据
        var yoyFiltered = filterResult.getContext().get("yoyFiltered") != null;
        if (yoyFiltered) {
            var originFilter = (List<ChartExtFilterDTO>) filterResult.getContext().get("originFilter");
            var allFields = (List<ChartViewFieldDTO>) filterResult.getContext().get("allFields");
            ExtWhere2Str.extWhere2sqlOjb(sqlMeta, originFilter, FieldUtil.transFields(allFields), crossDs, dsMap, Utils.getParams(FieldUtil.transFields(allFields)), view.getCalParams(), pluginManage);
            var originSql = SQLProvider.createQuerySQL(sqlMeta, true, needOrder, view);
            originSql = provider.rebuildSQL(originSql, sqlMeta, crossDs, dsMap);
            var request = new DatasourceRequest();
            request.setIsCross(crossDs);
            request.setDsList(dsMap);
            request.setQuery(originSql);
            logger.debug("calcite yoy sql: " + originSql);
            // 实际过滤后的数据
            var originData = (List<String[]>) provider.fetchResultField(request).get("data");
            List<String[]> resultData = new ArrayList<>();
            // 包含一年前的数据, 已计算同环比
            var yoyData = (List<String[]>) expandedResult.getData().get("data");
            var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
            // 对比维度,只保留实际过滤后的数据
            for (String[] yoyDataLine : yoyData) {
                StringBuilder x1 = new StringBuilder();
                for (int i = 0; i < xAxis.size(); i++) {
                    x1.append(yoyDataLine[i]);
                }
                for (String[] originDataLine : originData) {
                    StringBuilder x2 = new StringBuilder();
                    for (int i = 0; i < xAxis.size(); i++) {
                        x2.append(originDataLine[i]);
                    }
                    if (StringUtils.equals(x1, x2)) {
                        resultData.add(yoyDataLine);
                        break;
                    }
                }
            }
            yoyData.clear();
            yoyData.addAll(resultData);
            var result = this.buildNormalResult(view, formatResult, filterResult, yoyData);
            expandedResult.setData(result);
            expandedResult.setOriginData(resultData);
            expandedResult.setQuerySql(originSql);
        }
        // 同环比数据排序
        expandedResult.setOriginData(sortData(view, expandedResult.getOriginData(), formatResult));
        return expandedResult;
    }

    /**
     * 对数据进行排序
     * 根据视图配置的维度和指标排序规则对数据进行排序
     *
     * @param view 图表视图信息，包含排序配置
     * @param data 待排序的数据
     * @param formatResult 坐标轴格式化结果
     * @return 排序后的数据列表
     */
    public static List<String[]> sortData(ChartViewDTO view, List<String[]> data, AxisFormatResult formatResult) {
        // 维度排序
        List<ChartViewFieldDTO> xAxisSortList = view.getXAxis().stream().filter(x -> !StringUtils.equalsIgnoreCase("none", x.getSort())).toList();
        // 指标排序
        List<ChartViewFieldDTO> yAxisSortList = view.getYAxis().stream().filter(y -> {
            //需要针对区间条形图的时间类型判断一下
            if (StringUtils.equalsIgnoreCase("bar-range", view.getType()) && StringUtils.equalsIgnoreCase(y.getGroupType(), "d") && y.getDeType() == 1) {
                return false;
            } else {
                return !StringUtils.equalsIgnoreCase("none", y.getSort());
            }
        }).toList();
        // 不包含维度排序时，指标排序生效
        if (!data.isEmpty() && CollectionUtils.isEmpty(xAxisSortList) && CollectionUtils.isNotEmpty(yAxisSortList)) {
            // 指标排序仅第一个生效
            ChartViewFieldDTO firstYAxis = yAxisSortList.getFirst();
            boolean asc = firstYAxis.getSort().equalsIgnoreCase("asc");
            // 维度指标
            List<ChartViewFieldDTO> allAxisList = new ArrayList<>();
            allAxisList.addAll(formatResult.getAxisMap().get(ChartAxis.xAxis));
            allAxisList.addAll(formatResult.getAxisMap().get(ChartAxis.yAxis));
            int index = findIndex(allAxisList, firstYAxis.getId());
            return sortData(data, asc, index);
        }
        return data;

    }

    /**
     * 根据指定列进行数据排序
     * 按照指定索引的列值进行升序或降序排序
     *
     * @param data 待排序的数据
     * @param ascending 是否升序排列
     * @param index 排序依据的列索引
     * @return 排序后的数据列表
     */
    public static List<String[]> sortData(List<String[]> data, boolean ascending, int index) {
        Comparator<String[]> comparator;
        if (ascending) {
            comparator = Comparator.comparing(item -> toBigDecimal(item[index]), Comparator.nullsFirst(Comparator.naturalOrder()));
        } else {
            comparator = Comparator.comparing(item -> toBigDecimal(item[index]), Comparator.nullsLast(Comparator.reverseOrder()));
        }
        return data.stream().sorted(comparator).collect(Collectors.toList());
    }

    private static BigDecimal toBigDecimal(String value) {
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + value, e);
        }
    }

    /**
     * 查找字段在列表中的索引
     * 根据字段ID查找其在字段列表中的位置
     *
     * @param list 字段列表
     * @param id 字段ID
     * @return 字段索引，未找到返回-1
     */
    public static int findIndex(List<ChartViewFieldDTO> list, Long id) {
        for (int i = 0; i < list.size(); i++) {
            if (StringUtils.equalsIgnoreCase(list.get(i).getId().toString(), id.toString())) {
                return i;
            }
        }
        return -1;
    }
}
