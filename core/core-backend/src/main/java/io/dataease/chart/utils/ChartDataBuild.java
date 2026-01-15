package io.dataease.chart.utils;

import io.dataease.api.chart.dto.ScatterChartDataDTO;
import io.dataease.api.chart.dto.Series;
import io.dataease.extensions.view.dto.*;
import io.dataease.i18n.Lang;
import io.dataease.i18n.Translator;
import io.dataease.utils.IDUtils;
import io.dataease.utils.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 图表数据构建工具类
 * 负责将原始数据转换为各种图表类型所需的数据格式
 * 支持AntV、ECharts等多种图表库的数据格式转换
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>AntV图表数据格式转换</li>
 *   <li>散点图数据构建</li>
 *   <li>表格数据构建</li>
 *   <li>数据脱敏处理</li>
 *   <li>数值格式化和精度处理</li>
 * </ul>
 *
 * @author DataEase Team
 */
public class ChartDataBuild {

    private final static String format = "(%s)";

    /**
     * 将原始数据转换为 AntV 图表数据格式
     * <p>支持基础图表类型(柱状图、折线图、饼图等)的数据格式转换</p>
     *
     * @param xAxis X轴字段列表(维度字段)
     * @param yAxis Y轴字段列表(指标字段)
     * @param view 图表视图对象,包含图表配置信息
     * @param data 原始数据数组,每行代表一条记录
     * @param isDrill 是否为钻取状态
     * @return 包含 AntV 数据格式(列表形式)的 Map,键为 "data"
     */
    // AntV
    public static Map<String, Object> transChartDataAntV(List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();

        // 创建数据列表,用于存储转换后的图表数据
        List<AxisChartDataAntVDTO> dataList = new ArrayList<>();
        // 遍历每一行原始数据
        for (int i1 = 0; i1 < data.size(); i1++) {
            String[] row = data.get(i1);

            // 构建X轴字段值(支持多维度字段拼接)
            StringBuilder a = new StringBuilder();
            if (isDrill) {
                // 钻取状态:只取最后一个维度字段
                a.append(row[xAxis.size() - 1]);
            } else {
                // 正常状态:拼接所有维度字段,用换行符分隔
                for (int i = 0; i < xAxis.size(); i++) {
                    if (i == xAxis.size() - 1) {
                        a.append(row[i]);
                    } else {
                        a.append(row[i]).append("\n");
                    }
                }
            }

            // 根据图表类型进行不同的处理
            if (StringUtils.containsIgnoreCase(view.getType(), "table")) {
                // 表格类型:需要展开所有字段(维度+指标)
                for (int i = 0; i < xAxis.size() + yAxis.size(); i++) {
                    AxisChartDataAntVDTO axisChartDataDTO = new AxisChartDataAntVDTO();
                    axisChartDataDTO.setField(a.toString());
                    axisChartDataDTO.setName(a.toString());

                    // 构建维度列表
                    List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                    List<ChartQuotaDTO> quotaList = new ArrayList<>();

                    // 添加所有维度字段
                    for (int j = 0; j < xAxis.size(); j++) {
                        ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                        chartDimensionDTO.setId(xAxis.get(j).getId());
                        chartDimensionDTO.setValue(row[j]);
                        dimensionList.add(chartDimensionDTO);
                    }
                    axisChartDataDTO.setDimensionList(dimensionList);

                    // 处理指标字段
                    int j = i - xAxis.size();
                    if (j > -1) {
                        // 添加指标字段信息
                        ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                        chartQuotaDTO.setId(yAxis.get(j).getId());
                        quotaList.add(chartQuotaDTO);
                        axisChartDataDTO.setQuotaList(quotaList);
                        // 解析数值,异常时设置为0
                        try {
                            axisChartDataDTO.setValue(StringUtils.isEmpty(row[i]) ? null : new BigDecimal(row[i]));
                        } catch (Exception e) {
                            axisChartDataDTO.setValue(new BigDecimal(0));
                        }
                        // 设置分类名称(优先使用图表显示名称)
                        axisChartDataDTO.setCategory(StringUtils.defaultIfBlank(yAxis.get(j).getChartShowName(), yAxis.get(j).getName()));
                    }
                    dataList.add(axisChartDataDTO);
                }
            } else {
                // 非表格类型:只处理指标字段
                // 计算扩展字段(extLabel和extTooltip)的数量,这些字段需要从yAxis中排除
                int size = xAxis.size() + yAxis.size();
                int extSize = view.getExtLabel().size() + view.getExtTooltip().size();

                // 遍历指标字段(排除扩展字段)
                for (int i = xAxis.size(); i < size - extSize; i++) {
                    AxisChartDataAntVDTO axisChartDataDTO = new AxisChartDataAntVDTO();
                    axisChartDataDTO.setField(a.toString());
                    axisChartDataDTO.setName(a.toString());

                    // 构建维度列表
                    List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                    List<ChartQuotaDTO> quotaList = new ArrayList<>();

                    // 添加所有维度字段
                    for (int j = 0; j < xAxis.size(); j++) {
                        ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                        chartDimensionDTO.setId(xAxis.get(j).getId());
                        chartDimensionDTO.setValue(row[j]);
                        dimensionList.add(chartDimensionDTO);
                    }
                    axisChartDataDTO.setDimensionList(dimensionList);

                    // 计算当前指标在yAxis中的索引
                    int j = i - xAxis.size();
                    // 添加指标字段信息
                    ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                    chartQuotaDTO.setId(yAxis.get(j).getId());
                    quotaList.add(chartQuotaDTO);
                    axisChartDataDTO.setQuotaList(quotaList);
                    // 解析数值,异常时设置为0
                    try {
                        axisChartDataDTO.setValue(StringUtils.isEmpty(row[i]) ? null : new BigDecimal(row[i]));
                    } catch (Exception e) {
                        axisChartDataDTO.setValue(new BigDecimal(0));
                    }
                    // 设置分类名称(优先使用图表显示名称)
                    axisChartDataDTO.setCategory(StringUtils.defaultIfBlank(yAxis.get(j).getChartShowName(), yAxis.get(j).getName()));
                    // 构建动态标签和提示值(从扩展字段中提取)
                    buildDynamicValue(view, axisChartDataDTO, row, size, extSize);
                    dataList.add(axisChartDataDTO);
                }
            }
        }
        // 将数据列表放入Map中返回
        map.put("data", dataList);
        return map;
    }

    /**
     * 将原始数据转换为 AntV 热力图数据格式
     * <p>热力图需要两个维度字段作为 X 和 Y 轴,一个指标作为颜色映射</p>
     *
     * @param xAxisBase 基础 X 轴字段列表(必须包含2个维度字段)
     * @param xAxis 完整的 X 轴字段列表
     * @param yAxis Y 轴字段列表(指标字段)
     * @param view 图表视图对象,包含图表配置信息
     * @param data 原始数据数组
     * @param isDrill 是否为钻取状态
     * @return 包含热力图数据格式(包含 x, y 坐标)的 Map,键为 "data"
     */
    public static Map<String, Object> transHeatMapChartDataAntV(List<ChartViewFieldDTO> xAxisBase, List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();

        if (xAxisBase.size() != 2) {
            map.put("data", dataList);
            return map;
        }

        for (int i1 = 0; i1 < data.size(); i1++) {
            String[] row = data.get(i1);

            StringBuilder a = new StringBuilder();
            if (isDrill) {
                a.append(row[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxis.size(); i++) {
                    if (i == xAxis.size() - 1) {
                        a.append(row[i]);
                    } else {
                        a.append(row[i]).append("\n");
                    }
                }
            }

            // yAxis最后的数据对应extLabel和extTooltip，将他们从yAxis中去掉，同时转换成动态值
            int size = xAxis.size() + yAxis.size();
            int extSize = view.getExtLabel().size() + view.getExtTooltip().size();

            for (int i = xAxis.size(); i < size - extSize; i++) {
                AxisChartDataAntVDTO axisChartDataDTO = new AxisChartDataAntVDTO();
                axisChartDataDTO.setField(a.toString());
                axisChartDataDTO.setName(a.toString());

                List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                List<ChartQuotaDTO> quotaList = new ArrayList<>();

                for (int j = 0; j < xAxis.size(); j++) {
                    ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                    chartDimensionDTO.setId(xAxis.get(j).getId());
                    chartDimensionDTO.setValue(row[j]);
                    dimensionList.add(chartDimensionDTO);
                }
                axisChartDataDTO.setDimensionList(dimensionList);

                int j = i - xAxis.size();
                ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                chartQuotaDTO.setId(yAxis.get(j).getId());
                quotaList.add(chartQuotaDTO);
                axisChartDataDTO.setQuotaList(quotaList);
                try {
                    axisChartDataDTO.setValue(StringUtils.isEmpty(row[i]) ? null : new BigDecimal(row[i]));
                } catch (Exception e) {
                    axisChartDataDTO.setValue(new BigDecimal(0));
                }
                axisChartDataDTO.setCategory(StringUtils.defaultIfBlank(yAxis.get(j).getChartShowName(), yAxis.get(j).getName()));
                buildDynamicValue(view, axisChartDataDTO, row, size, extSize);

                Map<String, Object> object = JsonUtil.parse((String) JsonUtil.toJSONString(axisChartDataDTO), HashMap.class);

                object.put("x", new BigDecimal(row[0]));
                object.put("y", new BigDecimal(row[1]));

                dataList.add(object);
            }

        }
        map.put("data", dataList);
        return map;
    }

    /**
     * 将原始数据转换为 AntV 分组图表数据格式
     * <p>用于将数据按维度分组展示,支持多系列图表</p>
     *
     * @param xAxisBase 基础 X 轴字段列表(主维度)
     * @param xAxis 完整的 X 轴字段列表(包含主维度和扩展维度)
     * @param xAxisExt 扩展 X 轴字段列表(分组维度)
     * @param yAxis Y 轴字段列表(指标字段)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param isDrill 是否为钻取状态
     * @return 包含分组图表数据格式(每个数据点包含 group 信息)的 Map,键为 "data"
     */
    public static Map<String, Object> transBaseGroupDataAntV(List<ChartViewFieldDTO> xAxisBase, List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> xAxisExt, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();

        List<AxisChartDataAntVDTO> dataList = new ArrayList<>();
        for (int i1 = 0; i1 < data.size(); i1++) {
            String[] row = data.get(i1);

            StringBuilder a = new StringBuilder();
            if (isDrill) {
                a.append(row[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxisBase.size(); i++) {
                    if (i == xAxisBase.size() - 1) {
                        a.append(row[i]);
                    } else {
                        a.append(row[i]).append("\n");
                    }
                }
            }

            StringBuilder b = new StringBuilder();
            for (int i = xAxisBase.size(); i < xAxisBase.size() + xAxisExt.size(); i++) {
                if (i == xAxisBase.size() + xAxisExt.size() - 1) {
                    b.append(row[i]);
                } else {
                    b.append(row[i]).append("\n");
                }
            }

            // yAxis最后的数据对应extLabel和extTooltip，将他们从yAxis中去掉，同时转换成动态值
            int size = xAxis.size() + yAxis.size();
            int extSize = view.getExtLabel().size() + view.getExtTooltip().size();

            for (int i = xAxis.size(); i < size - extSize; i++) {
                AxisChartDataAntVDTO axisChartDataDTO = new AxisChartDataAntVDTO();
                axisChartDataDTO.setField(a.toString());
                axisChartDataDTO.setName(a.toString());

                List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                List<ChartQuotaDTO> quotaList = new ArrayList<>();

                for (int j = 0; j < xAxis.size(); j++) {
                    ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                    chartDimensionDTO.setId(xAxis.get(j).getId());
                    chartDimensionDTO.setValue(row[j]);
                    dimensionList.add(chartDimensionDTO);
                }
                axisChartDataDTO.setDimensionList(dimensionList);

                int j = i - xAxis.size();
                ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                chartQuotaDTO.setId(yAxis.get(j).getId());
                quotaList.add(chartQuotaDTO);
                axisChartDataDTO.setQuotaList(quotaList);
                try {
                    axisChartDataDTO.setValue(StringUtils.isEmpty(row[i]) ? null : new BigDecimal(row[i]));
                } catch (Exception e) {
                    axisChartDataDTO.setValue(new BigDecimal(0));
                }
                axisChartDataDTO.setCategory(b.toString());
                buildDynamicValue(view, axisChartDataDTO, row, size, extSize);
                dataList.add(axisChartDataDTO);

                if ("line".equals(view.getType())) {
                    if (ObjectUtils.isEmpty(xAxisExt)) {
                        axisChartDataDTO.setCategory(StringUtils.defaultIfBlank(yAxis.get(j).getChartShowName(), yAxis.get(j).getName()));
                    } else {
                        // 多指标只取第一个
                        break;
                    }
                }
            }
        }
        map.put("data", dataList);
        return map;
    }

    /**
     * 将原始数据转换为 AntV 堆叠柱状图数据格式
     * <p>支持维度堆叠和指标堆叠两种模式</p>
     *
     * @param xAxisBase 基础 X 轴字段列表
     * @param xAxis 完整的 X 轴字段列表
     * @param yAxis Y 轴字段列表(指标字段)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param extStack 堆叠字段列表,如果为空则按指标堆叠
     * @param isDrill 是否为钻取状态
     * @return 包含堆叠柱状图数据格式(每个数据点包含 category 堆叠信息)的 Map,键为 "data"
     */
    // AntV柱状堆叠图
    public static Map<String, Object> transStackChartDataAntV(List<ChartViewFieldDTO> xAxisBase, List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, List<ChartViewFieldDTO> extStack, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();

        List<AxisChartDataAntVDTO> dataList = new ArrayList<>();

        if (ObjectUtils.isNotEmpty(extStack)) {
            for (int i1 = 0; i1 < data.size(); i1++) {
                String[] row = data.get(i1);

                AxisChartDataAntVDTO axisChartDataDTO = new AxisChartDataAntVDTO();
                StringBuilder a = new StringBuilder();
                if (isDrill) {
                    a.append(row[xAxis.size() - 1]);
                } else {
                    for (int i = 0; i < xAxisBase.size(); i++) {
                        if (i == xAxisBase.size() - 1) {
                            a.append(row[i]);
                        } else {
                            a.append(row[i]).append("\n");
                        }
                    }
                }
                axisChartDataDTO.setField(a.toString());
                axisChartDataDTO.setName(a.toString());
                axisChartDataDTO.setCategory(row[xAxisBase.size()]);

                List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                List<ChartQuotaDTO> quotaList = new ArrayList<>();

                for (int k = 0; k < xAxis.size(); k++) {
                    ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                    chartDimensionDTO.setId(xAxis.get(k).getId());
                    chartDimensionDTO.setValue(row[k]);
                    dimensionList.add(chartDimensionDTO);
                }
                axisChartDataDTO.setDimensionList(dimensionList);

                if (ObjectUtils.isNotEmpty(yAxis)) {
                    int valueIndex = xAxis.size();
                    ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                    chartQuotaDTO.setId(yAxis.get(0).getId());
                    quotaList.add(chartQuotaDTO);
                    axisChartDataDTO.setQuotaList(quotaList);
                    try {
                        axisChartDataDTO.setValue(StringUtils.isEmpty(row[valueIndex]) ? null : new BigDecimal(row[valueIndex]));
                    } catch (Exception e) {
                        axisChartDataDTO.setValue(new BigDecimal(0));
                    }
                } else {
                    axisChartDataDTO.setQuotaList(quotaList);
                    axisChartDataDTO.setValue(new BigDecimal(0));
                }
                dataList.add(axisChartDataDTO);
            }
        } else {
            for (int i1 = 0; i1 < data.size(); i1++) {
                String[] row = data.get(i1);

                StringBuilder a = new StringBuilder();
                if (isDrill) {
                    a.append(row[xAxis.size() - 1]);
                } else {
                    for (int i = 0; i < xAxis.size(); i++) {
                        if (i == xAxis.size() - 1) {
                            a.append(row[i]);
                        } else {
                            a.append(row[i]).append("\n");
                        }
                    }
                }

                for (int i = xAxis.size(); i < xAxis.size() + yAxis.size(); i++) {
                    AxisChartDataAntVDTO axisChartDataDTO = new AxisChartDataAntVDTO();
                    axisChartDataDTO.setField(a.toString());
                    axisChartDataDTO.setName(a.toString());

                    List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                    List<ChartQuotaDTO> quotaList = new ArrayList<>();

                    for (int j = 0; j < xAxis.size(); j++) {
                        ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                        chartDimensionDTO.setId(xAxis.get(j).getId());
                        chartDimensionDTO.setValue(row[j]);
                        dimensionList.add(chartDimensionDTO);
                    }
                    axisChartDataDTO.setDimensionList(dimensionList);

                    int j = i - xAxis.size();
                    ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                    chartQuotaDTO.setId(yAxis.get(j).getId());
                    quotaList.add(chartQuotaDTO);
                    axisChartDataDTO.setQuotaList(quotaList);
                    try {
                        axisChartDataDTO.setValue(StringUtils.isEmpty(row[i]) ? null : new BigDecimal(row[i]));
                    } catch (Exception e) {
                        axisChartDataDTO.setValue(new BigDecimal(0));
                    }
                    axisChartDataDTO.setCategory(StringUtils.defaultIfBlank(yAxis.get(j).getChartShowName(), yAxis.get(j).getName()));
                    dataList.add(axisChartDataDTO);
                }
            }
        }
        map.put("data", dataList);
        return map;
    }

    /**
     * 将原始数据转换为 AntV 散点图数据格式
     * <p>支持气泡图,可以通过 extBubble 字段控制气泡大小</p>
     *
     * @param xAxis X 轴字段列表(维度字段)
     * @param yAxis Y 轴字段列表(指标字段)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param extBubble 气泡大小字段列表,如果不为空则生成气泡图
     * @param isDrill 是否为钻取状态
     * @return 包含散点图数据格式(包含 popSize 气泡大小信息)的 Map,键为 "data"
     */
    //AntV scatter
    public static Map<String, Object> transScatterDataAntV(List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, List<ChartViewFieldDTO> extBubble, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();

        List<AxisChartDataAntVDTO> dataList = new ArrayList<>();
        for (int i1 = 0; i1 < data.size(); i1++) {
            String[] row = data.get(i1);

            StringBuilder a = new StringBuilder();
            if (isDrill) {
                a.append(row[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxis.size(); i++) {
                    if (i == xAxis.size() - 1) {
                        a.append(row[i]);
                    } else {
                        a.append(row[i]).append("\n");
                    }
                }
            }

            // yAxis最后的数据对应extLabel和extTooltip，将他们从yAxis中去掉，同时转换成动态值
            int size = xAxis.size() + yAxis.size() + extBubble.size();
            int extSize = view.getExtLabel().size() + view.getExtTooltip().size() + extBubble.size();

            for (int i = xAxis.size(); i < size - extSize; i++) {
                AxisChartDataAntVDTO axisChartDataDTO = new AxisChartDataAntVDTO();
                axisChartDataDTO.setField(a.toString());
                axisChartDataDTO.setName(a.toString());

                List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                List<ChartQuotaDTO> quotaList = new ArrayList<>();

                for (int j = 0; j < xAxis.size(); j++) {
                    ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                    chartDimensionDTO.setId(xAxis.get(j).getId());
                    chartDimensionDTO.setValue(row[j]);
                    dimensionList.add(chartDimensionDTO);
                }
                axisChartDataDTO.setDimensionList(dimensionList);

                int j = i - xAxis.size();
                ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                chartQuotaDTO.setId(yAxis.get(j).getId());
                quotaList.add(chartQuotaDTO);
                axisChartDataDTO.setQuotaList(quotaList);
                try {
                    axisChartDataDTO.setValue(StringUtils.isEmpty(row[i]) ? null : new BigDecimal(row[i]));
                } catch (Exception e) {
                    axisChartDataDTO.setValue(new BigDecimal(0));
                }
                axisChartDataDTO.setCategory(StringUtils.defaultIfBlank(yAxis.get(j).getChartShowName(), yAxis.get(j).getName()));
                buildDynamicValue(view, axisChartDataDTO, row, size, ObjectUtils.isNotEmpty(extBubble) ? extSize - 1 : extSize);
                // pop
                if (ObjectUtils.isNotEmpty(extBubble)) {
                    try {
                        var popIndex = xAxis.size() + yAxis.size();
                        axisChartDataDTO.setPopSize(StringUtils.isEmpty(row[popIndex]) ? null : new BigDecimal(row[popIndex]));
                        ChartQuotaDTO bubbleQuotaDTO = new ChartQuotaDTO();
                        bubbleQuotaDTO.setId(extBubble.get(0).getId());
                        quotaList.add(bubbleQuotaDTO);
                    } catch (Exception e) {
                        axisChartDataDTO.setPopSize(new BigDecimal(0));
                    }
                }
                dataList.add(axisChartDataDTO);
            }
        }
        map.put("data", dataList);
        return map;
    }

    /**
     * 将原始数据转换为 AntV 雷达图数据格式
     * <p>雷达图用于多维数据对比展示</p>
     *
     * @param xAxis 维度字段列表(雷达图的各个维度)
     * @param yAxis Y 轴字段列表(指标字段,对应雷达图的数值轴)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param isDrill 是否为钻取状态
     * @return 包含雷达图数据格式的 Map,键为 "data"
     */
    // antv radar
    public static Map<String, Object> transRadarChartDataAntV(List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();

        List<AxisChartDataAntVDTO> dataList = new ArrayList<>();
        for (int i1 = 0; i1 < data.size(); i1++) {
            String[] row = data.get(i1);

            StringBuilder a = new StringBuilder();
            if (isDrill) {
                a.append(row[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxis.size(); i++) {
                    if (i == xAxis.size() - 1) {
                        a.append(row[i]);
                    } else {
                        a.append(row[i]).append("\n");
                    }
                }
            }

            // yAxis最后的数据对应extLabel和extTooltip，将他们从yAxis中去掉，同时转换成动态值
            int size = xAxis.size() + yAxis.size();
            int extSize = view.getExtLabel().size() + view.getExtTooltip().size();

            for (int i = xAxis.size(); i < size - extSize; i++) {
                AxisChartDataAntVDTO axisChartDataDTO = new AxisChartDataAntVDTO();
                axisChartDataDTO.setField(a.toString());
                axisChartDataDTO.setName(a.toString());

                List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                List<ChartQuotaDTO> quotaList = new ArrayList<>();

                for (int j = 0; j < xAxis.size(); j++) {
                    ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                    chartDimensionDTO.setId(xAxis.get(j).getId());
                    chartDimensionDTO.setValue(row[j]);
                    dimensionList.add(chartDimensionDTO);
                }
                axisChartDataDTO.setDimensionList(dimensionList);

                int j = i - xAxis.size();
                ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                chartQuotaDTO.setId(yAxis.get(j).getId());
                quotaList.add(chartQuotaDTO);
                axisChartDataDTO.setQuotaList(quotaList);
                try {
                    axisChartDataDTO.setValue(StringUtils.isEmpty(row[i]) ? null : new BigDecimal(row[i]));
                } catch (Exception e) {
                    axisChartDataDTO.setValue(new BigDecimal(0));
                }
                axisChartDataDTO.setCategory(StringUtils.defaultIfBlank(yAxis.get(j).getChartShowName(), yAxis.get(j).getName()));
                buildDynamicValue(view, axisChartDataDTO, row, size, extSize);
                dataList.add(axisChartDataDTO);
            }
        }
        map.put("data", dataList);
        return map;
    }

    /**
     * 将原始数据转换为 AntV 组合图数据格式
     * <p>组合图支持在一个图表中展示多种图表类型(如柱状图+折线图)</p>
     *
     * @param xAxisBase 基础 X 轴字段列表
     * @param xAxis 完整的 X 轴字段列表
     * @param xAxisExt 扩展 X 轴字段列表(用于分组)
     * @param yAxis Y 轴字段列表,每个字段可以配置不同的图表类型
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param isDrill 是否为钻取状态
     * @return 包含组合图数据格式(按系列分类)的 Map,键为 "data"
     */
    // antV组合图形
    public static Map<String, Object> transMixChartDataAntV(List<ChartViewFieldDTO> xAxisBase, List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> xAxisExt, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, boolean isDrill) {
        return transMixChartDataAntV(xAxisBase, xAxis, xAxisExt, yAxis, view, data, isDrill, false);
    }

    /**
     * 将原始数据转换为 AntV 组合图数据格式(增强版)
     * <p>支持指定是否为折线图模式,提供更灵活的配置选项</p>
     *
     * @param xAxisBase 基础 X 轴字段列表
     * @param xAxis 完整的 X 轴字段列表
     * @param xAxisExt 扩展 X 轴字段列表(用于分组)
     * @param yAxis Y 轴字段列表,每个字段可以配置不同的图表类型
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param isDrill 是否为钻取状态
     * @param isLine 是否强制使用折线图模式
     * @return 包含组合图数据格式(按系列分类,包含 categories)的 Map,键为 "data"
     */
    public static Map<String, Object> transMixChartDataAntV(List<ChartViewFieldDTO> xAxisBase, List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> xAxisExt, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, boolean isDrill, boolean isLine) {

        Map<String, Object> map = new HashMap<>();

        // 创建系列列表,每个指标对应一个系列
        List<Series> series = new ArrayList<>();
        // 初始化系列,配置系列名称和类型
        for (ChartViewFieldDTO y : yAxis) {
            Series series1 = new Series();
            series1.setName(y.getName());
            series1.setType(y.getChartType());
            series1.setData(new ArrayList<>());
            series.add(series1);
        }
        // 用于收集所有分类名称
        Set<String> categories = new HashSet<>();

        // 遍历每一行原始数据
        for (int i1 = 0; i1 < data.size(); i1++) {
            String[] d = data.get(i1);

            // 计算扩展字段(extLabel和extTooltip)的数量
            int size = xAxis.size() + yAxis.size();
            int extSize = view.getExtLabel().size() + view.getExtTooltip().size();

            // 遍历每个指标字段(排除扩展字段)
            for (int i = xAxis.size(); i < size - extSize; i++) {
                AxisChartDataAntVDTO axisChartDataDTO = new AxisChartDataAntVDTO();

                // 构建基础X轴字段值(主维度)
                StringBuilder a = new StringBuilder();
                if (isDrill) {
                    a.append(d[xAxis.size() - 1]);
                } else {
                    for (int ii = 0; ii < xAxisBase.size(); ii++) {
                        if (ii == xAxisBase.size() - 1) {
                            a.append(d[ii]);
                        } else {
                            a.append(d[ii]).append("\n");
                        }
                    }
                }
                // 构建扩展X轴字段值(分组维度)
                StringBuilder b = new StringBuilder();
                for (int ii = xAxisBase.size(); ii < xAxisBase.size() + xAxisExt.size(); ii++) {
                    if (ii == xAxisBase.size() + xAxisExt.size() - 1) {
                        b.append(d[ii]);
                    } else {
                        b.append(d[ii]).append("\n");
                    }
                }

                axisChartDataDTO.setName(a.toString());
                axisChartDataDTO.setField(a.toString());

                // 构建维度列表和指标列表
                List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                List<ChartQuotaDTO> quotaList = new ArrayList<>();

                // 添加所有维度字段
                for (int j = 0; j < xAxis.size(); j++) {
                    ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                    chartDimensionDTO.setId(xAxis.get(j).getId());
                    chartDimensionDTO.setValue(d[j]);
                    dimensionList.add(chartDimensionDTO);
                }
                axisChartDataDTO.setDimensionList(dimensionList);

                // 计算当前指标在yAxis中的索引
                int j = i - xAxis.size();
                // 添加指标字段信息
                ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                chartQuotaDTO.setId(yAxis.get(j).getId());
                quotaList.add(chartQuotaDTO);
                axisChartDataDTO.setQuotaList(quotaList);
                // 解析数值,异常时设置为0
                try {
                    axisChartDataDTO.setValue(StringUtils.isEmpty(d[i]) ? null : new BigDecimal(d[i]));
                } catch (Exception e) {
                    axisChartDataDTO.setValue(new BigDecimal(0));
                }

                // 确定分类名称:优先使用分组维度,其次使用图表显示名称
                String category = StringUtils.defaultIfBlank(b.toString(),
                        StringUtils.defaultIfBlank(yAxis.get(j).getChartShowName(), yAxis.get(j).getName()));

                // 折线图模式特殊处理:如果没有扩展维度,使用指标名称作为分类
                if (isLine) {
                    if (ObjectUtils.isEmpty(xAxisExt)) {
                        category = StringUtils.defaultIfBlank(yAxis.get(j).getChartShowName(), yAxis.get(j).getName());
                    }
                }

                axisChartDataDTO.setCategory(category);
                categories.add(category);

                // 构建动态标签和提示值
                buildDynamicValue(view, axisChartDataDTO, d, size, extSize);
                // 将数据点添加到对应的系列中
                series.get(j).getData().add(axisChartDataDTO);
            }
        }
        // 将收集到的分类设置到第一个系列中
        if (CollectionUtils.isNotEmpty(series)) {
            series.get(0).setCategories(categories);
        }

        // 将系列数据放入Map中返回
        map.put("data", series);
        return map;
    }

    /**
     * 将原始数据转换为 AntV 组合堆叠图数据格式
     * <p>组合图的基础上增加堆叠功能,支持多系列堆叠展示</p>
     *
     * @param xAxisBase 基础 X 轴字段列表
     * @param xAxis 完整的 X 轴字段列表
     * @param extStack 堆叠字段列表,如果为空则退化为普通组合图
     * @param yAxis Y 轴字段列表(指标字段)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param isDrill 是否为钻取状态
     * @return 包含组合堆叠图数据格式(按系列分类,包含堆叠信息)的 Map,键为 "data"
     */
    public static Map<String, Object> transMixChartStackDataAntV(List<ChartViewFieldDTO> xAxisBase, List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> extStack, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, boolean isDrill) {

        if (CollectionUtils.isEmpty(extStack)) {
            return transMixChartDataAntV(xAxisBase, xAxis, new ArrayList<>(), yAxis, view, data, isDrill);
        }

        Map<String, Object> map = new HashMap<>();

        List<Series> series = new ArrayList<>();
        for (ChartViewFieldDTO y : yAxis) {
            Series series1 = new Series();
            series1.setName(y.getName());
            series1.setType(y.getChartType());
            series1.setData(new ArrayList<>());
            series.add(series1);
        }
        Set<String> categories = new HashSet<>();

        for (int i1 = 0; i1 < data.size(); i1++) {
            String[] row = data.get(i1);

            AxisChartDataAntVDTO axisChartDataDTO = new AxisChartDataAntVDTO();
            StringBuilder a = new StringBuilder();
            if (isDrill) {
                a.append(row[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxisBase.size(); i++) {
                    if (i == xAxisBase.size() - 1) {
                        a.append(row[i]);
                    } else {
                        a.append(row[i]).append("\n");
                    }
                }
            }
            axisChartDataDTO.setField(a.toString());
            axisChartDataDTO.setName(a.toString());
            String category = row[xAxisBase.size()];
            axisChartDataDTO.setCategory(category);
            if (category != null) {
                categories.add(category);
            }

            List<ChartDimensionDTO> dimensionList = new ArrayList<>();
            List<ChartQuotaDTO> quotaList = new ArrayList<>();

            for (int k = 0; k < xAxis.size(); k++) {
                ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                chartDimensionDTO.setId(xAxis.get(k).getId());
                chartDimensionDTO.setValue(row[k]);
                dimensionList.add(chartDimensionDTO);
            }
            axisChartDataDTO.setDimensionList(dimensionList);

            if (ObjectUtils.isNotEmpty(yAxis)) {
                int valueIndex = xAxis.size();
                ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                chartQuotaDTO.setId(yAxis.get(0).getId());
                quotaList.add(chartQuotaDTO);
                axisChartDataDTO.setQuotaList(quotaList);
                try {
                    axisChartDataDTO.setValue(StringUtils.isEmpty(row[valueIndex]) ? null : new BigDecimal(row[valueIndex]));
                } catch (Exception e) {
                    axisChartDataDTO.setValue(new BigDecimal(0));
                }
            } else {
                axisChartDataDTO.setQuotaList(quotaList);
                axisChartDataDTO.setValue(new BigDecimal(0));
            }
            series.get(0).getData().add(axisChartDataDTO);
        }

        if (CollectionUtils.isNotEmpty(series)) {
            series.get(0).setCategories(categories);
        }

        map.put("data", series);
        return map;
    }

    /**
     * 将原始数据转换为基础图表数据格式
     * <p>通用数据转换方法,支持大部分基础图表类型</p>
     *
     * @param xAxis X 轴字段列表(维度字段)
     * @param yAxis Y 轴字段列表(指标字段)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param isDrill 是否为钻取状态
     * @return 包含图表数据格式(支持动态标签和提示)的 Map,键为 "data"
     */
    // 基础图形
    public static Map<String, Object> transChartData(List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();

        List<AxisChartDataAntVDTO> dataList = new ArrayList<>();
        for (int i1 = 0; i1 < data.size(); i1++) {
            String[] row = data.get(i1);

            StringBuilder a = new StringBuilder();
            if (isDrill) {
                a.append(row[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxis.size(); i++) {
                    if (i == xAxis.size() - 1) {
                        a.append(row[i]);
                    } else {
                        a.append(row[i]).append("\n");
                    }
                }
            }
            // yAxis最后的数据对应extLabel和extTooltip，将他们从yAxis中去掉，同时转换成动态值
            int size = xAxis.size() + yAxis.size();
            int extSize = view.getExtLabel().size() + view.getExtTooltip().size();

            for (int i = xAxis.size(); i < size - extSize; i++) {
                AxisChartDataAntVDTO axisChartDataDTO = new AxisChartDataAntVDTO();
                axisChartDataDTO.setField(a.toString());
                axisChartDataDTO.setName(a.toString());

                List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                List<ChartQuotaDTO> quotaList = new ArrayList<>();

                for (int j = 0; j < xAxis.size(); j++) {
                    ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                    chartDimensionDTO.setId(xAxis.get(j).getId());
                    chartDimensionDTO.setValue(row[j]);
                    dimensionList.add(chartDimensionDTO);
                }
                axisChartDataDTO.setDimensionList(dimensionList);

                int j = i - xAxis.size();
                ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                chartQuotaDTO.setId(yAxis.get(j).getId());
                quotaList.add(chartQuotaDTO);
                axisChartDataDTO.setQuotaList(quotaList);
                try {
                    axisChartDataDTO.setValue(StringUtils.isEmpty(row[i]) ? null : new BigDecimal(row[i]));
                } catch (Exception e) {
                    axisChartDataDTO.setValue(new BigDecimal(0));
                }
                axisChartDataDTO.setCategory(StringUtils.defaultIfBlank(yAxis.get(j).getChartShowName(), yAxis.get(j).getName()));
                buildDynamicValue(view, axisChartDataDTO, row, size, extSize);
                dataList.add(axisChartDataDTO);
            }
        }
        map.put("data", dataList);
        return map;
    }

    /**
     * 将原始数据转换为组合图数据格式
     * <p>ECharts 风格的组合图数据转换,支持在一个图表中展示多种图表类型</p>
     *
     * @param xAxis X 轴字段列表(维度字段)
     * @param yAxis Y 轴字段列表,每个字段可以配置不同的图表类型
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param isDrill 是否为钻取状态
     * @return 包含组合图数据的 Map,包含 "x"(X轴数据) 和 "series"(系列数据) 两个键
     */
    // 组合图形
    public static Map<String, Object> transMixChartData(List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();

        List<String> x = new ArrayList<>();
        List<Series> series = new ArrayList<>();
        for (ChartViewFieldDTO y : yAxis) {
            Series series1 = new Series();
            series1.setName(y.getName());
            series1.setType(y.getChartType());
            series1.setData(new ArrayList<>());
            series.add(series1);
        }
        for (int i1 = 0; i1 < data.size(); i1++) {
            String[] d = data.get(i1);

            StringBuilder a = new StringBuilder();
            for (int i = xAxis.size(); i < xAxis.size() + yAxis.size(); i++) {
                List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                List<ChartQuotaDTO> quotaList = new ArrayList<>();
                AxisChartDataDTO axisChartDataDTO = new AxisChartDataDTO();

                for (int j = 0; j < xAxis.size(); j++) {
                    ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                    chartDimensionDTO.setId(xAxis.get(j).getId());
                    chartDimensionDTO.setValue(d[j]);
                    dimensionList.add(chartDimensionDTO);
                }
                axisChartDataDTO.setDimensionList(dimensionList);

                int j = i - xAxis.size();
                ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                chartQuotaDTO.setId(yAxis.get(j).getId());
                quotaList.add(chartQuotaDTO);
                axisChartDataDTO.setQuotaList(quotaList);
                try {
                    axisChartDataDTO.setValue(StringUtils.isEmpty(d[i]) ? null : new BigDecimal(d[i]));
                } catch (Exception e) {
                    axisChartDataDTO.setValue(new BigDecimal(0));
                }
                series.get(j).getData().add(axisChartDataDTO);
            }
            if (isDrill) {
                a.append(d[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxis.size(); i++) {
                    if (i == xAxis.size() - 1) {
                        a.append(d[i]);
                    } else {
                        a.append(d[i]).append("\n");
                    }
                }
            }
            x.add(a.toString());
        }

        map.put("x", x);
        map.put("series", series);
        return map;
    }

    /**
     * 将原始数据转换为文本卡图表数据格式
     * <p>文本卡用于展示单个维度的文本数据,通常用于指标卡片展示</p>
     *
     * @param xAxis X 轴字段列表(维度字段,通常只有一个)
     * @param yAxis Y 轴字段列表(本方法中不使用)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param isDrill 是否为钻取状态
     * @return 包含文本卡数据的 Map,包含 "x"(文本列表) 和 "series"(系列数据) 两个键
     */
    // 文本卡图形
    public static Map<String, Object> transLabelChartData(List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();

        List<String> x = new ArrayList<>();
        List<Series> series = new ArrayList<>();
        Series series1 = new Series();
        series1.setName(xAxis.get(0).getName());
        series1.setType(view.getType());
        series1.setData(new ArrayList<>());
        series.add(series1);
        for (String[] d : data) {
            StringBuilder a = new StringBuilder();
            if (isDrill) {
                a.append(d[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxis.size(); i++) {
                    if (i == xAxis.size() - 1) {
                        a.append(d[i]);
                    } else {
                        a.append(d[i]).append("\n");
                    }
                }
            }
            x.add(a.toString());
            series.get(0).getData().add(a.toString());
        }

        map.put("x", x);
        map.put("series", series);
        return map;
    }

    /**
     * 将原始数据转换为常规图表数据格式
     * <p>适用于柱状图、折线图、面积图等常规图表类型</p>
     *
     * @param xAxis X 轴字段列表(维度字段)
     * @param yAxis Y 轴字段列表(指标字段)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param isDrill 是否为钻取状态
     * @return 包含常规图表数据的 Map,包含 "x"(X轴数据) 和 "series"(系列数据) 两个键
     */
    // 常规图形
    public static Map<String, Object> transNormalChartData(List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();

        List<String> x = new ArrayList<>();
        List<Series> series = new ArrayList<>();
        for (ChartViewFieldDTO y : yAxis) {
            Series series1 = new Series();
            series1.setName(y.getName());
            series1.setType(view.getType());
            series1.setData(new ArrayList<>());
            series.add(series1);
        }
        for (String[] d : data) {
            StringBuilder a = new StringBuilder();
            if (isDrill) {
                a.append(d[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxis.size(); i++) {
                    if (i == xAxis.size() - 1) {
                        a.append(d[i]);
                    } else {
                        a.append(d[i]).append("\n");
                    }
                }
            }
            x.add(a.toString());
            for (int i = xAxis.size(); i < xAxis.size() + yAxis.size(); i++) {
                int j = i - xAxis.size();
                try {
                    series.get(j).getData().add(StringUtils.isEmpty(d[i]) ? null : new BigDecimal(d[i]));
                } catch (Exception e) {
                    series.get(j).getData().add(new BigDecimal(0));
                }
            }
        }

        map.put("x", x);
        map.put("series", series);
        return map;
    }

    /**
     * 将原始数据转换为雷达图数据格式
     * <p>ECharts 风格的雷达图数据转换</p>
     *
     * @param xAxis 维度字段列表(雷达图的各个维度)
     * @param yAxis Y 轴字段列表(指标字段)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param isDrill 是否为钻取状态
     * @return 包含雷达图数据的 Map,包含 "x"(维度列表) 和 "series"(系列数据) 两个键
     */
    // radar图
    public static Map<String, Object> transRadarChartData(List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();

        List<String> x = new ArrayList<>();
        List<Series> series = new ArrayList<>();
        for (ChartViewFieldDTO y : yAxis) {
            Series series1 = new Series();
            series1.setName(y.getName());
            series1.setType(view.getType());
            series1.setData(new ArrayList<>());
            series.add(series1);
        }
        for (String[] d : data) {
            StringBuilder a = new StringBuilder();
            if (isDrill) {
                a.append(d[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxis.size(); i++) {
                    if (i == xAxis.size() - 1) {
                        a.append(d[i]);
                    } else {
                        a.append(d[i]).append("\n");
                    }
                }
            }
            x.add(a.toString());
            for (int i = xAxis.size(); i < xAxis.size() + yAxis.size(); i++) {
                int j = i - xAxis.size();
                try {
                    series.get(j).getData().add(StringUtils.isEmpty(d[i]) ? null : new BigDecimal(d[i]));
                } catch (Exception e) {
                    series.get(j).getData().add(new BigDecimal(0));
                }
            }
        }

        map.put("x", x);
        map.put("series", series);
        return map;
    }

    /**
     * 将原始数据转换为堆叠图数据格式
     * <p>支持维度堆叠和指标堆叠两种模式,ECharts 风格</p>
     *
     * @param xAxis X 轴字段列表(维度字段)
     * @param yAxis Y 轴字段列表(指标字段)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param extStack 堆叠字段列表,如果为空则按指标堆叠
     * @param isDrill 是否为钻取状态
     * @return 包含堆叠图数据的 Map,包含 "x"(X轴数据) 和 "series"(堆叠系列数据) 两个键
     */
    // 堆叠图
    public static Map<String, Object> transStackChartData(List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, List<ChartViewFieldDTO> extStack, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();

        // X轴数据列表
        List<String> x = new ArrayList<>();
        // 堆叠维度列表
        List<String> stack = new ArrayList<>();
        // 系列数据列表
        List<Series> series = new ArrayList<>();

        // 判断是否为维度堆叠模式(有堆叠字段)
        if (ObjectUtils.isNotEmpty(extStack)) {
            // 创建默认数据对象,用于填充空值
            AxisChartDataDTO defaultAxisChartDataDTO = new AxisChartDataDTO();
            // 折线图默认值为0,其他图表默认值为null
            BigDecimal defaultValue = StringUtils.containsIgnoreCase(view.getType(), "line") ? new BigDecimal(0) : null;
            defaultAxisChartDataDTO.setValue(defaultValue);

            // 构建横轴数据:遍历所有数据,提取X轴维度值并去重
            for (String[] d : data) {
                StringBuilder a = new StringBuilder();
                if (isDrill) {
                    a.append(d[xAxis.size() - 1]);
                } else {
                    for (int i = 0; i < xAxis.size(); i++) {
                        if (i == xAxis.size() - 1) {
                            a.append(d[i]);
                        } else {
                            a.append(d[i]).append("\n");
                        }
                    }
                }
                x.add(a.toString());
            }
            // 去重X轴数据
            x = x.stream().distinct().collect(Collectors.toList());

            // 构建堆叠维度列表:提取所有堆叠字段的值并去重
            for (String[] d : data) {
                stack.add(d[xAxis.size()]);
            }
            stack = stack.stream().distinct().collect(Collectors.toList());

            // 为每个堆叠维度创建一个系列,并用默认值初始化
            for (String s : stack) {
                Series series1 = new Series();
                series1.setName(s);
                series1.setType(view.getType());
                List<Object> list = new ArrayList<>();
                // 为每个X轴位置创建一个默认数据点
                for (int i = 0; i < x.size(); i++) {
                    list.add(defaultAxisChartDataDTO);
                }
                series1.setData(list);
                series.add(series1);
            }

            // 填充实际数据:遍历每个系列和每个X轴位置,查找对应的数据值
            for (Series ss : series) {
                for (int i = 0; i < x.size(); i++) {
                    // 在原始数据中查找匹配的记录
                    for (String[] row : data) {
                        String stackColumn = row[xAxis.size()];
                        // 判断堆叠维度是否匹配
                        if (StringUtils.equals(ss.getName(), stackColumn)) {
                            // 构建当前行的X轴维度值
                            StringBuilder a = new StringBuilder();
                            if (isDrill) {
                                a.append(row[xAxis.size() - 1]);
                            } else {
                                for (int j = 0; j < xAxis.size(); j++) {
                                    if (j == xAxis.size() - 1) {
                                        a.append(row[j]);
                                    } else {
                                        a.append(row[j]).append("\n");
                                    }
                                }
                            }
                            // 判断X轴维度是否匹配
                            if (StringUtils.equals(a.toString(), x.get(i))) {
                                // 检查数据长度是否足够
                                if (row.length > xAxis.size() + extStack.size()) {
                                    // 构建数据对象
                                    List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                                    List<ChartQuotaDTO> quotaList = new ArrayList<>();
                                    AxisChartDataDTO axisChartDataDTO = new AxisChartDataDTO();

                                    // 添加指标字段
                                    ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                                    chartQuotaDTO.setId(yAxis.get(0).getId());
                                    quotaList.add(chartQuotaDTO);
                                    axisChartDataDTO.setQuotaList(quotaList);

                                    // 添加所有维度字段
                                    for (int k = 0; k < xAxis.size(); k++) {
                                        ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                                        chartDimensionDTO.setId(xAxis.get(k).getId());
                                        chartDimensionDTO.setValue(row[k]);
                                        dimensionList.add(chartDimensionDTO);
                                    }
                                    // 添加堆叠维度字段
                                    ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                                    chartDimensionDTO.setId(extStack.get(0).getId());
                                    chartDimensionDTO.setValue(row[xAxis.size()]);
                                    dimensionList.add(chartDimensionDTO);
                                    axisChartDataDTO.setDimensionList(dimensionList);

                                    // 提取数值并更新数据点
                                    String s = row[xAxis.size() + extStack.size()];
                                    if (StringUtils.isNotEmpty(s)) {
                                        axisChartDataDTO.setValue(new BigDecimal(s));
                                        ss.getData().set(i, axisChartDataDTO);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            // 指标堆叠模式:每个指标作为一个系列
            // 为每个指标创建一个系列
            for (ChartViewFieldDTO y : yAxis) {
                Series series1 = new Series();
                series1.setName(y.getName());
                series1.setType(view.getType());
                series1.setData(new ArrayList<>());
                series.add(series1);
            }

            // 遍历每一行原始数据
            for (int i1 = 0; i1 < data.size(); i1++) {
                String[] d = data.get(i1);

                StringBuilder a = new StringBuilder();
                // 处理每个指标字段
                for (int i = xAxis.size(); i < xAxis.size() + yAxis.size(); i++) {
                    List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                    List<ChartQuotaDTO> quotaList = new ArrayList<>();
                    AxisChartDataDTO axisChartDataDTO = new AxisChartDataDTO();

                    // 添加所有维度字段
                    for (int j = 0; j < xAxis.size(); j++) {
                        ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                        chartDimensionDTO.setId(xAxis.get(j).getId());
                        chartDimensionDTO.setValue(d[j]);
                        dimensionList.add(chartDimensionDTO);
                    }
                    axisChartDataDTO.setDimensionList(dimensionList);

                    // 计算当前指标在yAxis中的索引
                    int j = i - xAxis.size();
                    // 添加指标字段信息
                    ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                    chartQuotaDTO.setId(yAxis.get(j).getId());
                    quotaList.add(chartQuotaDTO);
                    axisChartDataDTO.setQuotaList(quotaList);
                    // 解析数值,异常时设置为0
                    try {
                        axisChartDataDTO.setValue(StringUtils.isEmpty(d[i]) ? null : new BigDecimal(d[i]));
                    } catch (Exception e) {
                        axisChartDataDTO.setValue(new BigDecimal(0));
                    }
                    // 将数据点添加到对应的系列中
                    series.get(j).getData().add(axisChartDataDTO);
                }

                // 构建X轴维度值
                if (isDrill) {
                    a.append(d[xAxis.size() - 1]);
                } else {
                    for (int i = 0; i < xAxis.size(); i++) {
                        if (i == xAxis.size() - 1) {
                            a.append(d[i]);
                        } else {
                            a.append(d[i]).append("\n");
                        }
                    }
                }
                x.add(a.toString());
            }
        }

        // 将X轴数据和系列数据放入Map中返回
        map.put("x", x);
        map.put("series", series);
        return map;
    }

    /**
     * 将原始数据转换为散点图数据格式
     * <p>ECharts 风格的散点图数据转换,支持气泡图</p>
     *
     * @param xAxis X 轴字段列表(维度字段)
     * @param yAxis Y 轴字段列表(指标字段)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param extBubble 气泡大小字段列表,如果不为空则生成气泡图
     * @param isDrill 是否为钻取状态
     * @return 包含散点图数据的 Map,包含 "x"(X轴数据) 和 "series"(散点系列数据) 两个键
     */
    // 散点图
    public static Map<String, Object> transScatterData(List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, List<ChartViewFieldDTO> extBubble, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();

        List<String> x = new ArrayList<>();
        List<Series> series = new ArrayList<>();
        for (ChartViewFieldDTO y : yAxis) {
            Series series1 = new Series();
            series1.setName(y.getName());
            series1.setType(view.getType());
            series1.setData(new ArrayList<>());
            series.add(series1);
        }
        for (int i1 = 0; i1 < data.size(); i1++) {
            String[] d = data.get(i1);

            StringBuilder a = new StringBuilder();
            if (isDrill) {
                a.append(d[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxis.size(); i++) {
                    if (i == xAxis.size() - 1) {
                        a.append(d[i]);
                    } else {
                        a.append(d[i]).append("\n");
                    }
                }
            }
            x.add(a.toString());
            for (int i = xAxis.size(); i < xAxis.size() + yAxis.size(); i++) {
                List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                List<ChartQuotaDTO> quotaList = new ArrayList<>();
                ScatterChartDataDTO scatterChartDataDTO = new ScatterChartDataDTO();

                for (int j = 0; j < xAxis.size(); j++) {
                    ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                    chartDimensionDTO.setId(xAxis.get(j).getId());
                    chartDimensionDTO.setValue(d[j]);
                    dimensionList.add(chartDimensionDTO);
                }
                scatterChartDataDTO.setDimensionList(dimensionList);

                int j = i - xAxis.size();
                ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                chartQuotaDTO.setId(yAxis.get(j).getId());
                quotaList.add(chartQuotaDTO);
                scatterChartDataDTO.setQuotaList(quotaList);

                if (ObjectUtils.isNotEmpty(extBubble) && extBubble.size() > 0) {
                    try {
                        scatterChartDataDTO.setValue(new Object[]{
                                a.toString(),
                                StringUtils.isEmpty(d[i]) ? null : new BigDecimal(d[i]),
                                StringUtils.isEmpty(d[xAxis.size() + yAxis.size()]) ? null : new BigDecimal(d[xAxis.size() + yAxis.size()])
                        });
                    } catch (Exception e) {
                        scatterChartDataDTO.setValue(new Object[]{a.toString(), new BigDecimal(0), new BigDecimal(0)});
                    }
                } else {
                    try {
                        scatterChartDataDTO.setValue(new Object[]{
                                a.toString(),
                                StringUtils.isEmpty(d[i]) ? null : new BigDecimal(d[i])
                        });
                    } catch (Exception e) {
                        scatterChartDataDTO.setValue(new Object[]{a.toString(), new BigDecimal(0)});
                    }
                }
                series.get(j).getData().add(scatterChartDataDTO);
            }
        }

        map.put("x", x);
        map.put("series", series);
        return map;
    }

    /**
     * 将原始数据转换为表格数据格式
     * <p>支持基础表格和堆叠表格的数据转换</p>
     *
     * @param xAxis X 轴字段列表(维度字段)
     * @param yAxis Y 轴字段列表(指标字段)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param extStack 堆叠字段列表(如果是堆叠表格)
     * @param desensitizationList 脱敏规则映射表,键为字段名称
     * @return 包含表格数据的 Map,包含 "fields"(字段列表) 和 "tableRow"(表格行数据) 两个键
     */
    // 表格
    public static Map<String, Object> transTableNormal(List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, List<ChartViewFieldDTO> extStack, Map<String, ColumnPermissionItem> desensitizationList) {
        List<ChartViewFieldDTO> fields = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(xAxis)) {
            fields.addAll(xAxis);
        }
        if (StringUtils.containsIgnoreCase(view.getType(), "stack")) {
            if (ObjectUtils.isNotEmpty(extStack)) {
                fields.addAll(extStack);
            }
        }
        fields.addAll(yAxis);
        return transTableNormal(fields, view, data, desensitizationList);
    }

    /**
     * 将原始数据转换为带详情的表格数据格式
     * <p>支持主表数据和明细数据的关联展示</p>
     *
     * @param xAxis X 轴字段列表(维度字段)
     * @param yAxis Y 轴字段列表(指标字段)
     * @param data 主表原始数据数组
     * @param detailFields 明细字段列表(包含主表字段和明细字段)
     * @param detailData 明细数据数组
     * @param desensitizationList 脱敏规则映射表,键为字段名称
     * @return 包含表格数据和明细数据的 Map,包含 "fields"、"detailFields" 和 "tableRow"(包含 details) 三个键
     */
    public static Map<String, Object> transTableNormalWithDetail(List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, List<String[]> data, List<ChartViewFieldDTO> detailFields, List<String[]> detailData, Map<String, ColumnPermissionItem> desensitizationList) {
        // 计算明细字段在detailFields中的起始索引
        int detailIndex = xAxis.size();

        // 提取真正的明细字段(排除主表字段)
        List<ChartViewFieldDTO> realDetailFields = detailFields.subList(detailIndex, detailFields.size());

        // 构建主表字段列表:维度字段 + 指标字段
        List<ChartViewFieldDTO> fields = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(xAxis))
            fields.addAll(xAxis);
        if (ObjectUtils.isNotEmpty(yAxis))
            fields.addAll(yAxis);

        // 转换主表数据为表格格式
        Map<String, Object> map = transTableNormal(fields, null, data, desensitizationList);
        List<Map<String, Object>> tableRow = (List<Map<String, Object>>) map.get("tableRow");

        // 将明细数据按主表维度字段分组
        final int xEndIndex = detailIndex;
        Map<String, List<String[]>> groupDataList = detailData.stream().collect(Collectors.groupingBy(item -> "(" + StringUtils.join(ArrayUtils.subarray(item, 0, xEndIndex), ")-de-(") + ")"));

        // 将明细数据关联到主表数据中
        tableRow.forEach(row -> {
            // 构建主表数据的分组键
            String key = xAxis.stream().map(x -> String.format(format, row.get(x.getDataeaseName()).toString())).collect(Collectors.joining("-de-"));
            // 获取对应的明细数据列表
            List<String[]> detailFieldValueList = groupDataList.get(key);
            // 将明细数据转换为Map格式
            List<Map<String, Object>> detailValueMapList = detailFieldValueList.stream().map((detailArr -> {
                Map<String, Object> temp = new HashMap<>();
                // 遍历所有明细字段,提取对应的值
                for (int i = 0; i < realDetailFields.size(); i++) {
                    ChartViewFieldDTO realDetailField = realDetailFields.get(i);
                    temp.put(realDetailField.getDataeaseName(), detailArr[detailIndex + i]);
                }
                return temp;
            })).collect(Collectors.toList());
            // 将明细数据添加到主表行中
            row.put("details", detailValueMapList);
        });

        // 创建一个虚拟的明细字段,用于标识明细数据列
        ChartViewFieldDTO detailFieldDTO = new ChartViewFieldDTO();
        detailFieldDTO.setId(IDUtils.snowID());
        detailFieldDTO.setName("detail");
        detailFieldDTO.setDataeaseName("detail");
        fields.add(detailFieldDTO);

        // 组装返回结果
        map.put("fields", fields);
        map.put("detailFields", realDetailFields);
        map.put("tableRow", tableRow);
        return map;
    }

    /**
     * 将原始数据转换为表格数据格式(基于字段映射)
     * <p>通过字段映射表构建表格,自动区分维度和指标字段</p>
     *
     * @param fieldMap 字段映射表,包含 xAxis、yAxis、tooltipAxis、labelAxis 等键
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param desensitizationList 脱敏规则映射表,键为字段名称
     * @return 包含表格数据的 Map,包含 "fields"(字段列表) 和 "tableRow"(表格行数据) 两个键
     */
    // 表格
    public static Map<String, Object> transTableNormal(Map<String, List<ChartViewFieldDTO>> fieldMap, ChartViewDTO view, List<String[]> data, Map<String, ColumnPermissionItem> desensitizationList) {

        List<ChartViewFieldDTO> fields = new ArrayList<>();
        List<ChartViewFieldDTO> yfields = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(fieldMap.get("xAxis"))) fields.addAll(fieldMap.get("xAxis"));
        if (ObjectUtils.isNotEmpty(fieldMap.get("tooltipAxis"))) {
            fieldMap.get("tooltipAxis").forEach(field -> {
                Integer deType = field.getDeType();
                if (deType == 2 || deType == 3) {
                    yfields.add(field);
                } else {
                    fields.add(field);
                }
            });
        }
        if (ObjectUtils.isNotEmpty(fieldMap.get("labelAxis"))) {
            fieldMap.get("labelAxis").forEach(field -> {
                Integer deType = field.getDeType();
                if (deType == 2 || deType == 3) {
                    yfields.add(field);
                } else {
                    fields.add(field);
                }
            });
        }
        if (ObjectUtils.isNotEmpty(fieldMap.get("yAxis"))) fields.addAll(fieldMap.get("yAxis"));
        if (ObjectUtils.isNotEmpty(yfields)) fields.addAll(yfields);
        return transTableNormal(fields, view, data, desensitizationList);
    }

    /**
     * 根据脱敏规则对原始值进行脱敏处理
     * <p>支持多种内置脱敏规则和自定义脱敏规则</p>
     *
     * @param columnPermissionItem 列权限项,包含脱敏规则配置
     * @param originStr 原始字符串值
     * @return 脱敏后的字符串值
     */
    public static String desensitizationValue(ColumnPermissionItem columnPermissionItem, String originStr) {
        String desensitizationStr = "";
        if (!columnPermissionItem.getDesensitizationRule().getBuiltInRule().toString().equalsIgnoreCase("custom")) {
            switch (columnPermissionItem.getDesensitizationRule().getBuiltInRule()) {
                case CompleteDesensitization:
                    desensitizationStr = ColumnPermissionItem.CompleteDesensitization;
                    break;
                case KeepMiddleThreeCharacters:
                    if (StringUtils.isEmpty(originStr) || originStr.length() < 4) {
                        desensitizationStr = ColumnPermissionItem.KeepMiddleThreeCharacters;
                    } else {
                        desensitizationStr = "***" + StringUtils.substring(originStr, originStr.length() / 2 - 1, originStr.length() / 2 + 2) + "***";
                    }
                    break;
                case KeepFirstAndLastThreeCharacters:
                    if (StringUtils.isEmpty(originStr) || originStr.length() < 7) {
                        desensitizationStr = ColumnPermissionItem.KeepFirstAndLastThreeCharacters;
                    } else {
                        desensitizationStr = StringUtils.substring(originStr, 0, 3) + "***" + StringUtils.substring(originStr, originStr.length() - 3, originStr.length());
                    }
                    break;
                default:
                    break;

            }
        } else {
            switch (columnPermissionItem.getDesensitizationRule().getCustomBuiltInRule()) {
                case RetainBeforeMAndAfterN:
                    if (StringUtils.isEmpty(originStr) || originStr.length() < columnPermissionItem.getDesensitizationRule().getM() + columnPermissionItem.getDesensitizationRule().getN()) {
                        desensitizationStr = String.join("", Collections.nCopies(columnPermissionItem.getDesensitizationRule().getM(), "X")) + "***" + String.join("", Collections.nCopies(columnPermissionItem.getDesensitizationRule().getN(), "X"));
                    } else {
                        desensitizationStr = StringUtils.substring(originStr, 0, columnPermissionItem.getDesensitizationRule().getM()) + "***" + StringUtils.substring(originStr, originStr.length() - columnPermissionItem.getDesensitizationRule().getN(), originStr.length());
                    }
                    break;
                case RetainMToN:
                    if (columnPermissionItem.getDesensitizationRule().getM() > columnPermissionItem.getDesensitizationRule().getN()) {
                        desensitizationStr = "*** ***";
                        break;
                    }
                    if (StringUtils.isEmpty(originStr) || originStr.length() < columnPermissionItem.getDesensitizationRule().getM()) {
                        desensitizationStr = "*** ***";
                        break;
                    }
                    if (columnPermissionItem.getDesensitizationRule().getM() == 1) {
                        desensitizationStr = StringUtils.substring(originStr, columnPermissionItem.getDesensitizationRule().getM() - 1, columnPermissionItem.getDesensitizationRule().getN()) + "***";
                        break;
                    } else {
                        desensitizationStr = "***" + StringUtils.substring(originStr, columnPermissionItem.getDesensitizationRule().getM() - 1, columnPermissionItem.getDesensitizationRule().getN()) + "***";
                        break;
                    }
                default:
                    break;

            }
        }
        return desensitizationStr;
    }

    /**
     * 将原始数据转换为表格数据格式(基于字段列表)
     * <p>核心表格转换方法,支持字段类型识别和数据脱敏</p>
     *
     * @param fields 完整的字段列表(包含维度和指标)
     * @param view 图表视图对象,用于判断是否为 Excel 导出
     * @param data 原始数据数组
     * @param desensitizationList 脱敏规则映射表,键为字段名称
     * @return 包含表格数据的 Map,包含 "fields"(字段列表) 和 "tableRow"(表格行数据) 两个键
     */
    public static Map<String, Object> transTableNormal(List<ChartViewFieldDTO> fields, ChartViewDTO view, List<String[]> data, Map<String, ColumnPermissionItem> desensitizationList) {
        Map<String, Object> map = new TreeMap<>();
        List<Map<String, Object>> tableRow = new ArrayList<>();
        data.forEach(ele -> {
            Map<String, Object> d = new HashMap<>();
            for (int i = 0; i < fields.size(); i++) {
                if (ObjectUtils.isNotEmpty(desensitizationList.keySet()) && desensitizationList.containsKey(fields.get(i).getDataeaseName())) {
                    String desensitizationValue = desensitizationValue(desensitizationList.get(fields.get(i).getDataeaseName()), String.valueOf(ele[i]));
                    ele[i] = desensitizationValue;
                    d.put(fields.get(i).getDataeaseName(), desensitizationValue);
                    continue;
                }
                if (i == ele.length) break;
                ChartViewFieldDTO chartViewFieldDTO = fields.get(i);
                if (chartViewFieldDTO.getDeType() == 0 || chartViewFieldDTO.getDeType() == 1 || chartViewFieldDTO.getDeType() == 5 || chartViewFieldDTO.getDeType() == 7) {
                    d.put(fields.get(i).getDataeaseName(), StringUtils.isEmpty(ele[i]) ? "" : ele[i]);
                } else if (chartViewFieldDTO.getDeType() == 2 || chartViewFieldDTO.getDeType() == 3 || chartViewFieldDTO.getDeType() == 4) {
                    // 如果是在维度中展示，导出excel时展示为字符串，其它情况展示为数值类型
                    if (view.getIsExcelExport() || StringUtils.equalsIgnoreCase(chartViewFieldDTO.getGroupType(), "d")) {
                        d.put(fields.get(i).getDataeaseName(), StringUtils.isEmpty(ele[i]) ? "" : ele[i]);
                    } else {
                        d.put(fields.get(i).getDataeaseName(), StringUtils.isEmpty(ele[i]) ? null : new BigDecimal(ele[i]).setScale(8, RoundingMode.HALF_UP));
                    }
                }
            }
            tableRow.add(d);
        });
        map.put("fields", fields);
        map.put("tableRow", tableRow);
        return map;
    }

    /**
     * 将原始数据转换为分组堆叠图数据格式
     * <p>支持分组堆叠、普通分组、普通堆叠三种模式的智能识别和转换</p>
     *
     * @param xAxisBase 基础 X 轴字段列表
     * @param xAxis 完整的 X 轴字段列表
     * @param xAxisExt 扩展 X 轴字段列表(分组维度)
     * @param yAxis Y 轴字段列表(指标字段)
     * @param extStack 堆叠字段列表
     * @param data 原始数据数组
     * @param view 图表视图对象
     * @param isDrill 是否为钻取状态
     * @return 包含分组堆叠图数据格式(包含 group 和 category 信息)的 Map,键为 "data"
     */
    public static Map<String, Object> transGroupStackDataAntV(List<ChartViewFieldDTO> xAxisBase, List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> xAxisExt, List<ChartViewFieldDTO> yAxis, List<ChartViewFieldDTO> extStack, List<String[]> data, ChartViewDTO view, boolean isDrill) {
        // 模式1: 堆叠柱状图(没有分组维度)
        if (ObjectUtils.isEmpty(xAxisExt)) {
            return transStackChartDataAntV(xAxisBase, xAxis, yAxis, view, data, extStack, isDrill);
        // 模式2: 分组柱状图(没有堆叠维度)
        } else if (ObjectUtils.isNotEmpty(xAxisExt) && ObjectUtils.isEmpty(extStack)) {
            return transBaseGroupDataAntV(xAxisBase, xAxis, xAxisExt, yAxis, view, data, isDrill);
        // 模式3: 分组堆叠柱状图(既有分组又有堆叠)
        } else {
            Map<String, Object> map = new HashMap<>();

            List<AxisChartDataAntVDTO> dataList = new ArrayList<>();
            // 遍历每一行原始数据
            for (int i1 = 0; i1 < data.size(); i1++) {
                String[] row = data.get(i1);

                // 构建X轴字段值(基础维度)
                StringBuilder xField = new StringBuilder();
                if (isDrill) {
                    xField.append(row[xAxis.size() - 1]);
                } else {
                    for (int i = 0; i < xAxisBase.size(); i++) {
                        if (i == xAxisBase.size() - 1) {
                            xField.append(row[i]);
                        } else {
                            xField.append(row[i]).append("\n");
                        }
                    }
                }

                // 构建分组字段值(分组维度)
                StringBuilder groupField = new StringBuilder();
                for (int i = xAxisBase.size(); i < xAxisBase.size() + xAxisExt.size(); i++) {
                    if (i == xAxisBase.size() + xAxisExt.size() - 1) {
                        groupField.append(row[i]);
                    } else {
                        groupField.append(row[i]).append("\n");
                    }
                }

                // 构建堆叠字段值(堆叠维度)
                StringBuilder stackField = new StringBuilder();
                for (int i = xAxisBase.size() + xAxisExt.size(); i < xAxisBase.size() + xAxisExt.size() + extStack.size(); i++) {
                    if (i == xAxisBase.size() + xAxisExt.size() + extStack.size() - 1) {
                        stackField.append(row[i]);
                    } else {
                        stackField.append(row[i]).append("\n");
                    }
                }

                // 创建数据对象
                AxisChartDataAntVDTO axisChartDataDTO = new AxisChartDataAntVDTO();
                axisChartDataDTO.setField(xField.toString());
                axisChartDataDTO.setName(xField.toString());

                // 构建维度列表和指标列表
                List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                List<io.dataease.extensions.view.dto.ChartQuotaDTO> quotaList = new ArrayList<>();

                // 添加所有维度字段
                for (int j = 0; j < xAxis.size(); j++) {
                    ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                    chartDimensionDTO.setId(xAxis.get(j).getId());
                    chartDimensionDTO.setValue(row[j]);
                    dimensionList.add(chartDimensionDTO);
                }
                axisChartDataDTO.setDimensionList(dimensionList);

                // 处理指标字段
                if (ObjectUtils.isNotEmpty(yAxis)) {
                    // 计算指标值在数据行中的索引
                    int valueIndex = xAxis.size();
                    // 添加指标字段信息
                    ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                    chartQuotaDTO.setId(yAxis.get(0).getId());
                    quotaList.add(chartQuotaDTO);
                    axisChartDataDTO.setQuotaList(quotaList);
                    // 解析数值,异常时设置为0
                    try {
                        axisChartDataDTO.setValue(StringUtils.isEmpty(row[valueIndex]) ? null : new BigDecimal(row[valueIndex]));
                    } catch (Exception e) {
                        axisChartDataDTO.setValue(new BigDecimal(0));
                    }
                } else {
                    axisChartDataDTO.setQuotaList(quotaList);
                    axisChartDataDTO.setValue(new BigDecimal(0));
                }

                // 设置分组和堆叠信息
                axisChartDataDTO.setGroup(groupField.toString());
                axisChartDataDTO.setCategory(stackField.toString());
                dataList.add(axisChartDataDTO);
            }
            // 将数据列表放入Map中返回
            map.put("data", dataList);
            return map;
        }
    }

    // 计算动态标签和提示
    private static void buildDynamicValue(ChartViewDTO view, AxisChartDataAntVDTO axisChartDataDTO, String[] row, int size, int extSize) {
        List<DynamicValueDTO> dynamicLabelValue = new ArrayList<>();
        List<DynamicValueDTO> dynamicTooltipValue = new ArrayList<>();
        // 计算动态标签和提示
        if (ObjectUtils.isNotEmpty(view.getExtLabel())) {
            for (int ii = 0; ii < view.getExtLabel().size(); ii++) {
                DynamicValueDTO valueDTO = new DynamicValueDTO();
                ChartViewFieldDTO chartViewFieldDTO = view.getExtLabel().get(ii);
                BigDecimal value = StringUtils.isEmpty(row[ii + (size - extSize)]) ? null : new BigDecimal(row[ii + (size - extSize)]);
                valueDTO.setFieldId(chartViewFieldDTO.getId());
                valueDTO.setValue(value);
                dynamicLabelValue.add(valueDTO);
            }
        }
        if (ObjectUtils.isNotEmpty(view.getExtTooltip())) {
            for (int ii = 0; ii < view.getExtTooltip().size(); ii++) {
                DynamicValueDTO valueDTO = new DynamicValueDTO();
                ChartViewFieldDTO chartViewFieldDTO = view.getExtTooltip().get(ii);
                BigDecimal value = StringUtils.isEmpty(row[ii + (size - extSize) + view.getExtLabel().size()]) ? null : new BigDecimal(row[ii + (size - extSize) + view.getExtLabel().size()]);
                valueDTO.setFieldId(chartViewFieldDTO.getId());
                valueDTO.setValue(value);
                dynamicTooltipValue.add(valueDTO);
            }
        }

        axisChartDataDTO.setDynamicLabelValue(dynamicLabelValue);
        axisChartDataDTO.setDynamicTooltipValue(dynamicTooltipValue);
    }

    /**
     * 将原始数据转换为象限图数据格式
     * <p>象限图用于在二维坐标系中展示数据分布,通常用于多维数据分析</p>
     *
     * @param xAxis X 轴字段列表(维度字段)
     * @param yAxis Y 轴字段列表(指标字段,可以有多个,每个指标生成一个系列)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param extBubble 气泡字段列表(本方法中未使用,保留参数兼容性)
     * @param isDrill 是否为钻取状态
     * @return 包含象限图数据格式的 Map,键为 "data"
     */
    //AntV quadrant
    public static Map<String, Object> transQuadrantDataAntV(List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, List<ChartViewFieldDTO> extBubble, boolean isDrill) {
        Map<String, Object> map = new HashMap<>();

        List<AxisChartDataAntVDTO> dataList = new ArrayList<>();
        for (int i1 = 0; i1 < data.size(); i1++) {
            String[] row = data.get(i1);

            StringBuilder a = new StringBuilder();
            if (isDrill) {
                a.append(row[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxis.size(); i++) {
                    if (i == xAxis.size() - 1) {
                        a.append(row[i]);
                    } else {
                        a.append(row[i]).append("\n");
                    }
                }
            }
            for (int i = 0; i < xAxis.size() + yAxis.size(); i++) {
                AxisChartDataAntVDTO axisChartDataDTO = new AxisChartDataAntVDTO();
                axisChartDataDTO.setField(a.toString());
                axisChartDataDTO.setName(a.toString());

                List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                List<ChartQuotaDTO> quotaList = new ArrayList<>();


                for (int j = 0; j < xAxis.size(); j++) {
                    ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                    chartDimensionDTO.setId(xAxis.get(j).getId());
                    chartDimensionDTO.setValue(row[j]);
                    dimensionList.add(chartDimensionDTO);
                }
                axisChartDataDTO.setDimensionList(dimensionList);

                int j = i - xAxis.size();
                if (j > -1) {
                    ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                    chartQuotaDTO.setId(yAxis.get(j).getId());
                    quotaList.add(chartQuotaDTO);
                    axisChartDataDTO.setQuotaList(quotaList);
                    try {
                        axisChartDataDTO.setValue(StringUtils.isEmpty(row[i]) ? null : new BigDecimal(row[i]));
                        axisChartDataDTO.setField(yAxis.get(j).getOriginName());
                        axisChartDataDTO.setName(yAxis.get(j).getName());
                    } catch (Exception e) {
                        axisChartDataDTO.setValue(new BigDecimal(0));
                    }
                    axisChartDataDTO.setCategory(StringUtils.defaultIfBlank(yAxis.get(j).getChartShowName(), yAxis.get(j).getName()));
                }
                dataList.add(axisChartDataDTO);
            }
        }
        map.put("data", dataList);
        return map;
    }

    /**
     * 将原始数据转换为区间柱状图数据格式
     * <p>支持日期区间和数值区间两种类型,自动计算最大最小值和间隔</p>
     *
     * @param skipBarRange 是否跳过区间处理,如果为 true 则返回空数据
     * @param isDate 是否为日期类型,true 表示日期区间,false 表示数值区间
     * @param xAxisBase 基础 X 轴字段列表
     * @param xAxis 完整的 X 轴字段列表
     * @param yAxis Y 轴字段列表(包含区间起始和结束值)
     * @param view 图表视图对象
     * @param data 原始数据数组
     * @param isDrill 是否为钻取状态
     * @return 包含区间图数据的 Map,包含 "data"、"isDate"、"min"/"minTime"、"max"/"maxTime" 等键
     */
    public static Map<String, Object> transBarRangeDataAntV(boolean skipBarRange, boolean isDate, List<ChartViewFieldDTO> xAxisBase, List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, ChartViewDTO view, List<String[]> data, boolean isDrill) {

        Map<String, Object> map = new HashMap<>();
        // 如果跳过区间处理,直接返回空数据
        if (skipBarRange) {
            map.put("data", new ArrayList<>());
            return map;
        }

        // 用于收集所有日期值或数值,以便计算最大最小值
        List<Date> dates = new ArrayList<>();
        List<BigDecimal> numbers = new ArrayList<>();

        // 日期字段和日期格式化器
        ChartViewFieldDTO dateAxis1 = null;
        SimpleDateFormat sdf = null;

        // 如果是日期类型,初始化日期格式化器
        if (isDate) {
            // 根据是否聚合确定日期字段位置
            if (BooleanUtils.isTrue(view.getAggregate())) {
                dateAxis1 = yAxis.get(0);
            } else {
                dateAxis1 = xAxis.get(xAxisBase.size());
            }
            // 根据日期样式和分隔符获取日期格式
            sdf = new SimpleDateFormat(getDateFormat(dateAxis1.getDateStyle(), dateAxis1.getDatePattern()));
        }

        // 构建数据列表
        List<Object> dataList = new ArrayList<>();
        // 遍历每一行原始数据
        for (int i1 = 0; i1 < data.size(); i1++) {
            String[] row = data.get(i1);

            // 构建X轴字段值(基础维度)
            StringBuilder xField = new StringBuilder();
            if (isDrill) {
                xField.append(row[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxisBase.size(); i++) {
                    if (i == xAxisBase.size() - 1) {
                        xField.append(row[i]);
                    } else {
                        xField.append(row[i]).append("\n");
                    }
                }
            }

            // 创建数据对象
            Map<String, Object> obj = new HashMap<>();
            obj.put("field", xField.toString());
            obj.put("category", xField.toString());

            // 构建维度列表
            List<ChartDimensionDTO> dimensionList = new ArrayList<>();
            // 添加基础维度字段
            for (int i = 0; i < xAxisBase.size(); i++) {
                ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                chartDimensionDTO.setId(xAxis.get(i).getId());
                chartDimensionDTO.setValue(row[i]);
                dimensionList.add(chartDimensionDTO);
            }
            // 如果是钻取状态,添加钻取维度
            if (isDrill) {
                int index = xAxis.size() - 1;
                ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                chartDimensionDTO.setId(xAxis.get(index).getId());
                chartDimensionDTO.setValue(row[index]);
                dimensionList.add(chartDimensionDTO);
            }
            obj.put("dimensionList", dimensionList);

            // 构建区间值列表
            List<Object> values = new ArrayList<>();

            // 检查区间起始和结束值是否为空
            if (row[xAxisBase.size()] == null || row[xAxisBase.size() + 1] == null) {
                continue;
            }

            if (isDate) {
                // 日期区间处理
                // 根据是否聚合确定区间值在数据行中的索引
                int index;
                if (BooleanUtils.isTrue(view.getAggregate())) {
                    index = xAxis.size();
                } else {
                    index = xAxisBase.size();
                }

                // 添加区间起始和结束日期
                values.add(row[index]);
                values.add(row[index + 1]);
                obj.put("values", values);

                // 解析日期并收集到列表中
                Date date1 = null, date2 = null;
                try {
                    date1 = sdf.parse(row[index]);
                    if (date1 != null) {
                        dates.add(date1);
                    }
                } catch (Exception ignore) {
                }
                try {
                    date2 = sdf.parse(row[index + 1]);
                    if (date2 != null) {
                        dates.add(date2);
                    }
                } catch (Exception ignore) {
                }
                // 计算并设置时间间隔
                obj.put("gap", getTimeGap(date1, date2, dateAxis1.getDateStyle()));

            } else {
                // 数值区间处理
                // 添加区间起始和结束数值
                values.add(new BigDecimal(row[xAxis.size()]));
                values.add(new BigDecimal(row[xAxis.size() + 1]));
                obj.put("values", values);

                // 收集数值到列表中
                numbers.add(new BigDecimal(row[xAxis.size()]));
                numbers.add(new BigDecimal(row[xAxis.size() + 1]));

                // 计算并设置数值间隔差
                obj.put("gap", new BigDecimal(row[xAxis.size() + 1]).subtract(new BigDecimal(row[xAxis.size()])));
            }

            dataList.add(obj);
        }

        // 计算并设置最大最小值
        if (isDate) {
            // 日期类型:计算最小和最大日期
            Date minDate = dates.stream().min(Date::compareTo).orElse(null);
            if (minDate != null) {
                map.put("minTime", sdf.format(minDate));
            }
            Date maxDate = dates.stream().max(Date::compareTo).orElse(null);
            if (maxDate != null) {
                map.put("maxTime", sdf.format(maxDate));
            }
        } else {
            // 数值类型:计算最小和最大值
            map.put("min", numbers.stream().min(BigDecimal::compareTo).orElse(null));
            map.put("max", numbers.stream().max(BigDecimal::compareTo).orElse(null));
        }

        // 设置返回结果
        map.put("isDate", isDate);
        map.put("data", dataList);
        return map;

    }

    private static String getDateFormat(String dateStyle, String datePattern) {
        String split;
        if (StringUtils.equalsIgnoreCase(datePattern, "date_split")) {
            split = "/";
        } else {
            split = "-";
        }
        switch (dateStyle) {
            case "y":
                return "yyyy";
            case "y_M":
                return "yyyy" + split + "MM";
            case "y_M_d":
                return "yyyy" + split + "MM" + split + "dd";
            case "M_d":
                return "MM" + split + "dd";
            case "H_m_s":
                return "HH:mm:ss";
            case "y_M_d_H":
                return "yyyy" + split + "MM" + split + "dd" + " HH";
            case "y_M_d_H_m":
                return "yyyy" + split + "MM" + split + "dd" + " HH:mm";
            case "y_M_d_H_m_s":
                return "yyyy" + split + "MM" + split + "dd" + " HH:mm:ss";
            default:
                return "yyyy-MM-dd HH:mm:ss";
        }
    }

    private static String getTimeGap(Date from, Date to, String dateStyle) {
        if (from == null || to == null) {
            return "";
        }
        Calendar fromCalender = Calendar.getInstance();
        fromCalender.setTime(from);

        Calendar toCalender = Calendar.getInstance();
        toCalender.setTime(to);

        long yearGap = 0;
        long monthGap = 0;
        long dayGap = (toCalender.getTimeInMillis() - fromCalender.getTimeInMillis()) / (1000 * 3600 * 24);
        long hourGap = ((toCalender.getTimeInMillis() - fromCalender.getTimeInMillis()) / (1000 * 3600)) % 24;
        long minuteGap = ((toCalender.getTimeInMillis() - fromCalender.getTimeInMillis()) / (1000 * 60)) % 60;
        long secondGap = ((toCalender.getTimeInMillis() - fromCalender.getTimeInMillis()) / 1000) % 60;

        String language = "zh-CN"; //国际化
        Lang lang = Lang.getLangWithoutDefault(language);
        boolean isEnUs = Lang.en_US.equals(lang);
        String splitter = isEnUs ? " " : "";

        String yearGapStr = "";
        String monthGapStr = "";

        String dayGapStr = "";
        if (dayGap != 0) {
            dayGapStr = dayGap + splitter + Translator.get("i18n_day") + (isEnUs && dayGap != 1 ? "s" : "");
        }
        String hourGapStr = "";
        if (hourGap != 0) {
            hourGapStr = hourGap + splitter + Translator.get("i18n_hour") + (isEnUs && hourGap != 1 ? "s" : "");
        }
        String minuteGapStr = "";
        if (minuteGap != 0) {
            minuteGapStr = minuteGap + splitter + Translator.get("i18n_minute") + (isEnUs && minuteGap != 1 ? "s" : "");
        }
        String secondGapStr = "";
        if (secondGap != 0) {
            secondGapStr = secondGap + splitter + Translator.get("i18n_second") + (isEnUs && secondGap != 1 ? "s" : "");
        }

        List<String> list = new ArrayList<>();

        switch (dateStyle) {
            case "y":
                yearGap = toCalender.get(Calendar.YEAR) - fromCalender.get(Calendar.YEAR);
                yearGapStr = yearGap == 0 ? "" : (yearGap + splitter + Translator.get("i18n_year") + (isEnUs && yearGap != 1 ? "s" : ""));
                return yearGapStr;
            case "y_M":
                yearGap = ((toCalender.get(Calendar.YEAR) - fromCalender.get(Calendar.YEAR)) * 12L + (toCalender.get(Calendar.MONTH) - fromCalender.get(Calendar.MONTH))) / 12;
                monthGap = ((toCalender.get(Calendar.YEAR) - fromCalender.get(Calendar.YEAR)) * 12L + (toCalender.get(Calendar.MONTH) - fromCalender.get(Calendar.MONTH))) % 12;

                yearGapStr = yearGap == 0 ? "" : (yearGap + splitter + Translator.get("i18n_year") + (isEnUs && yearGap != 1 ? "s" : ""));
                monthGapStr = monthGap == 0 ? "" : (monthGap + splitter + Translator.get("i18n_month") + (isEnUs && monthGap != 1 ? "s" : ""));

                if (!yearGapStr.isEmpty()) {
                    list.add(yearGapStr);
                }
                if (!monthGapStr.isEmpty()) {
                    list.add(monthGapStr);
                }
                return StringUtils.join(list, splitter);
            case "y_M_d":
            case "M_d":
                return dayGapStr;
            case "y_M_d_H":
                if (!dayGapStr.isEmpty()) {
                    list.add(dayGapStr);
                }
                if (!hourGapStr.isEmpty()) {
                    list.add(hourGapStr);
                }
                return StringUtils.join(list, splitter);
            case "y_M_d_H_m":
                if (!dayGapStr.isEmpty()) {
                    list.add(dayGapStr);
                }
                if (!hourGapStr.isEmpty()) {
                    list.add(hourGapStr);
                }
                if (!minuteGapStr.isEmpty()) {
                    list.add(minuteGapStr);
                }
                return StringUtils.join(list, splitter);
            case "H_m_s":
            case "y_M_d_H_m_s":
                if (!dayGapStr.isEmpty()) {
                    list.add(dayGapStr);
                }
                if (!hourGapStr.isEmpty()) {
                    list.add(hourGapStr);
                }
                if (!minuteGapStr.isEmpty()) {
                    list.add(minuteGapStr);
                }
                if (!secondGapStr.isEmpty()) {
                    list.add(secondGapStr);
                }
                return StringUtils.join(list, splitter);
            default:
                return "";
        }
    }

    /**
     * 将原始数据转换为符号地图数据格式(带明细)
     * <p>用于符号地图展示,支持气泡大小和明细数据关联</p>
     *
     * @param view 图表视图对象
     * @param xAxis X 轴字段列表(维度字段)
     * @param yAxis Y 轴字段列表(指标字段)
     * @param extBubble 气泡大小字段列表
     * @param data 主表原始数据数组
     * @param detailFields 明细字段列表
     * @param detailData 明细数据数组
     * @return 包含符号地图数据的 Map,包含 "fields"、"detailFields" 和 "tableRow"(包含 details) 三个键
     */
    public static Map<String, Object> transSymbolicMapNormalWithDetail(ChartViewDTO view, List<ChartViewFieldDTO> xAxis, List<ChartViewFieldDTO> yAxis, List<ChartViewFieldDTO> extBubble, List<String[]> data, List<ChartViewFieldDTO> detailFields, List<String[]> detailData) {
        // 计算明细字段在detailFields中的起始索引
        int detailIndex = xAxis.size();

        // 提取真正的明细字段(排除主表字段)
        List<ChartViewFieldDTO> realDetailFields = detailFields.subList(detailIndex, detailFields.size());

        // 构建主表字段列表:维度字段 + 气泡字段 + 指标字段
        List<ChartViewFieldDTO> fields = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(xAxis))
            fields.addAll(xAxis);
        if (ObjectUtils.isNotEmpty(extBubble))
            fields.addAll(extBubble);
        if (ObjectUtils.isNotEmpty(yAxis))
            fields.addAll(yAxis);

        // 转换主表数据为表格格式(不进行脱敏)
        Map<String, Object> map = transTableNormal(fields, view, data, new HashMap<>());
        List<Map<String, Object>> tableRow = (List<Map<String, Object>>) map.get("tableRow");

        // 将明细数据按主表维度字段分组
        final int xEndIndex = detailIndex;
        Map<String, List<String[]>> groupDataList = detailData.stream().collect(Collectors.groupingBy(item -> "(" + StringUtils.join(ArrayUtils.subarray(item, 0, xEndIndex), ")-de-(") + ")"));

        // 获取气泡字段名称(如果有)
        String extBubbleDataeaseName = ObjectUtils.isNotEmpty(extBubble) ? extBubble.get(0).getDataeaseName() : "";

        // 将明细数据关联到主表数据中
        tableRow.forEach(row -> {
            // 获取当前行的气泡大小值(如果没有则为0)
            BigDecimal rowValue = row.get(extBubbleDataeaseName) == null ? BigDecimal.ZERO : new BigDecimal(row.get(extBubbleDataeaseName).toString());
            // 构建主表数据的分组键
            String key = xAxis.stream().map(x -> String.format(format, row.get(x.getDataeaseName()).toString())).collect(Collectors.joining("-de-"));
            // 获取对应的明细数据列表
            List<String[]> detailFieldValueList = groupDataList.get(key);
            // 将明细数据转换为Map格式
            List<Map<String, Object>> detailValueMapList = Optional.ofNullable(detailFieldValueList).orElse(new ArrayList<>()).stream().map((detailArr -> {
                Map<String, Object> temp = new HashMap<>();
                // 遍历所有明细字段,提取对应的值
                for (int i = 0; i < realDetailFields.size(); i++) {
                    ChartViewFieldDTO realDetailField = realDetailFields.get(i);
                    // 如果当前字段是气泡字段,使用主表的气泡值
                    if (StringUtils.equalsIgnoreCase(extBubbleDataeaseName, realDetailField.getDataeaseName())) {
                        temp.put(realDetailField.getDataeaseName(), rowValue);
                    } else {
                        temp.put(realDetailField.getDataeaseName(), detailArr[detailIndex + i]);
                    }
                }
                return temp;
            })).collect(Collectors.toList());
            // 明细数据只要一个(取第一条)
            row.put("details", !detailValueMapList.isEmpty() ? Collections.singletonList(detailValueMapList.getFirst()) : detailValueMapList);
        });

        // 过滤掉记录数字段(*)
        List<ChartViewFieldDTO> filterCountAxis = fields.stream()
                .filter(item -> !StringUtils.equalsIgnoreCase(item.getDataeaseName(), "*"))
                .collect(Collectors.toList());

        // 如果气泡大小是记录数,添加所有指标字段到字段列表中
        if (ObjectUtils.isNotEmpty(extBubble) && "*".equals(extBubble.get(0).getDataeaseName())) {
            filterCountAxis.addAll(yAxis);
        }

        // 组装返回结果
        map.put("fields", filterCountAxis);
        map.put("detailFields", realDetailFields);
        map.put("tableRow", tableRow);
        return map;
    }

}
