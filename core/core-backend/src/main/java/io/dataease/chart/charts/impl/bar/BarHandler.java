package io.dataease.chart.charts.impl.bar;

import io.dataease.api.dataset.union.DatasetGroupInfoDTO;
import io.dataease.chart.charts.impl.YoyChartHandler;
import io.dataease.engine.utils.Utils;
import io.dataease.extensions.datasource.dto.DatasourceRequest;
import io.dataease.extensions.datasource.dto.DatasourceSchemaDTO;
import io.dataease.extensions.datasource.model.SQLMeta;
import io.dataease.extensions.datasource.provider.Provider;
import io.dataease.extensions.view.dto.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 柱状图处理器
 * 负责柱状图、水平柱状图及其变体的数据处理和渲染
 * 继承YoyChartHandler支持同比环比计算
 *
 * <p>支持的图表类型：</p>
 * <ul>
 *   <li>基础柱状图 (bar)</li>
 *   <li>水平柱状图 (bar-horizontal)</li>
 *   <li>堆叠柱状图 (bar-stack)</li>
 *   <li>分组柱状图 (bar-group)</li>
 *   <li>进度条、子弹图、范围条图等变体</li>
 * </ul>
 *
 * @author DataEase Team
 */
@Component
public class BarHandler extends YoyChartHandler {

    /**
     * 初始化处理器
     * 注册柱状图和水平柱状图的处理器
     */
    @Override
    public void init() {
        chartHandlerManager.registerChartHandler(this.getRender(), "bar", this);
        chartHandlerManager.registerChartHandler(this.getRender(), "bar-horizontal", this);
    }
    /**
     * 格式化坐标轴
     * 处理柱状图的坐标轴配置，包括扩展标签和提示字段
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var result = super.formatAxis(view);
        var yAxis = result.getAxisMap().get(ChartAxis.yAxis);
        yAxis.addAll(view.getExtLabel());
        yAxis.addAll(view.getExtTooltip());
        result.getAxisMap().put(ChartAxis.extLabel, view.getExtLabel());
        result.getAxisMap().put(ChartAxis.extTooltip, view.getExtTooltip());
        return result;
    }
    /**
     * 计算图表数据结果
     * 执行柱状图的数据计算，包括辅助线、辅助字段等特殊处理
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param sqlMap SQL相关映射（包含数据源信息）
     * @param sqlMeta SQL元数据对象
     * @param provider 数据源提供者
     * @return 图表计算结果
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
        var result = (T) super.calcChartResult(view, formatResult, filterResult, sqlMap, sqlMeta, provider);
        try {
            //如果有同环比过滤,应该用原始sql
            var originSql = result.getQuerySql();
            var dynamicAssistFields = getDynamicAssistFields(view);
            var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
            var assistFields = getAssistFields(dynamicAssistFields, yAxis);
            if (CollectionUtils.isNotEmpty(assistFields)) {
                var req = new DatasourceRequest();
                req.setIsCross(crossDs);
                req.setDsList(dsMap);

                List<ChartSeniorAssistDTO> assists = dynamicAssistFields.stream().filter(ele -> !StringUtils.equalsIgnoreCase(ele.getSummary(), "last_item")).toList();
                if (ObjectUtils.isNotEmpty(assists)) {
                    var assistSql = assistSQL(originSql, assistFields, dsMap, crossDs);
                    req.setQuery(assistSql);
                    logger.debug("calcite assistSql sql: " + assistSql);
                    var assistData = (List<String[]>) provider.fetchResultField(req).get("data");
                    result.setAssistData(assistData);
                    result.setDynamicAssistFields(assists);
                }

                List<ChartSeniorAssistDTO> assistsOriginList = dynamicAssistFields.stream().filter(ele -> StringUtils.equalsIgnoreCase(ele.getSummary(), "last_item")).toList();
                if (ObjectUtils.isNotEmpty(assistsOriginList)) {
                    var assistSqlOriginList = assistSQLOriginList(originSql, assistFields, dsMap, crossDs);
                    req.setQuery(assistSqlOriginList);
                    logger.debug("calcite assistSql sql origin list: " + assistSqlOriginList);
                    var assistDataOriginList = (List<String[]>) provider.fetchResultField(req).get("data");
                    result.setAssistDataOriginList(assistDataOriginList);
                    result.setDynamicAssistFieldsOriginList(assistsOriginList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
