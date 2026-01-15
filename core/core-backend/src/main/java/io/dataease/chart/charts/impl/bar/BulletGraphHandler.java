package io.dataease.chart.charts.impl.bar;

import io.dataease.api.dataset.union.DatasetGroupInfoDTO;
import io.dataease.chart.charts.impl.YoyChartHandler;
import io.dataease.chart.utils.ChartDataBuild;
import io.dataease.extensions.datasource.dto.DatasourceRequest;
import io.dataease.extensions.datasource.dto.DatasourceSchemaDTO;
import io.dataease.extensions.datasource.model.SQLMeta;
import io.dataease.extensions.datasource.provider.Provider;
import io.dataease.extensions.view.dto.*;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 子弹图处理器
 * 负责子弹图的数据处理和渲染
 * 继承YoyChartHandler，用于目标值与实际值的对比展示
 *
 * <p>支持的图表类型：</p>
 * <ul>
 *   <li>子弹图 (bullet)</li>
 * </ul>
 *
 * <p>用途：</p>
 * <ul>
 *   <li>KPI指标展示</li>
 *   <li>目标进度跟踪</li>
 *   <li>实际值与目标值对比</li>
 * </ul>
 *
 * @author DataEase Team
 */
@Component
public class BulletGraphHandler extends YoyChartHandler {
    @Getter
    private String type = "bullet-graph";

    /**
     * 格式化坐标轴
     * 处理子弹图的坐标轴配置，包括Y轴扩展字段、气泡字段和提示字段
     *
     * @param view 图表视图信息
     * @return 坐标轴格式化结果，包含Y轴、Y轴扩展、气泡和提示字段
     */
    @Override
    public AxisFormatResult formatAxis(ChartViewDTO view) {
        var result = super.formatAxis(view);
        var yAxis = result.getAxisMap().get(ChartAxis.yAxis);
        yAxis.addAll(view.getYAxisExt());
        if (!view.getExtBubble().isEmpty()
                && !Objects.equals(view.getExtBubble().getFirst().getId(), view.getYAxisExt().getFirst().getId())
                && !Objects.equals(view.getExtBubble().getFirst().getId(), view.getYAxis().getFirst().getId())) {
            yAxis.addAll(view.getExtBubble());
            result.getAxisMap().put(ChartAxis.extBubble, view.getExtBubble());
        }
        yAxis.addAll(view.getExtTooltip());
        result.getAxisMap().put(ChartAxis.yAxis, yAxis);
        result.getAxisMap().put(ChartAxis.yAxisExt, view.getYAxisExt());
        result.getAxisMap().put(ChartAxis.extBubble, view.getExtBubble());
        result.getAxisMap().put(ChartAxis.extTooltip, view.getExtTooltip());
        return result;
    }

    /**
     * 构建标准图表结果
     * 将查询数据转换为图表可用的数据格式
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param data 查询返回的原始数据
     * @return 格式化后的图表数据
     */
    @Override
    public Map<String, Object> buildNormalResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, List<String[]> data) {
        boolean isDrill = filterResult
                .getFilterList()
                .stream()
                .anyMatch(ele -> ele.getFilterType() == 1);
        var xAxis = formatResult.getAxisMap().get(ChartAxis.xAxis);
        var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
        return ChartDataBuild.transChartData(xAxis, yAxis, view, data, isDrill);
    }

    /**
     * 计算图表数据结果
     * 执行子弹图的数据计算，包括辅助线的特殊处理
     *
     * @param view 图表视图信息
     * @param formatResult 坐标轴格式化结果
     * @param filterResult 过滤器结果
     * @param sqlMap SQL相关映射（包含数据源信息）
     * @param sqlMeta SQL元数据对象
     * @param provider 数据源提供者
     * @return 图表计算结果，包含辅助线数据
     */
    @Override
    public <T extends ChartCalcDataResult> T calcChartResult(ChartViewDTO view, AxisFormatResult formatResult, CustomFilterResult filterResult, Map<String, Object> sqlMap, SQLMeta sqlMeta, Provider provider) {
        var dsMap = (Map<Long, DatasourceSchemaDTO>) sqlMap.get("dsMap");
        List<String> dsList = new ArrayList<>();
        for (Map.Entry<Long, DatasourceSchemaDTO> next : dsMap.entrySet()) {
            dsList.add(next.getValue().getType());
        }
        var result = (T) super.calcChartResult(view, formatResult, filterResult, sqlMap, sqlMeta, provider);
        try {
            //如果有同环比过滤,应该用原始sql
            var originSql = result.getQuerySql();
            var dynamicAssistFields = getDynamicAssistFields(view);
            var yAxis = formatResult.getAxisMap().get(ChartAxis.yAxis);
            var assistFields = getAssistFields(dynamicAssistFields, yAxis);
            if (CollectionUtils.isNotEmpty(assistFields)) {
                var req = new DatasourceRequest();
                req.setIsCross(((DatasetGroupInfoDTO) formatResult.getContext().get("dataset")).getIsCross());
                req.setDsList(dsMap);
                var assistSql = assistSQL(originSql, assistFields, dsMap, ((DatasetGroupInfoDTO) formatResult.getContext().get("dataset")).getIsCross());
                req.setQuery(assistSql);
                logger.debug("calcite assistSql sql: " + assistSql);
                var assistData = (List<String[]>) provider.fetchResultField(req).get("data");
                result.setAssistData(assistData);
                result.setDynamicAssistFields(dynamicAssistFields);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
