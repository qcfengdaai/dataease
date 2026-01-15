package io.dataease.chart.server;

import io.dataease.api.chart.ChartViewApi;
import io.dataease.api.chart.vo.ChartBaseVO;
import io.dataease.api.chart.vo.ViewSelectorVO;
import io.dataease.chart.manage.ChartViewManege;
import io.dataease.constant.CommonConstants;
import io.dataease.dataset.utils.DatasetUtils;
import io.dataease.exception.DEException;
import io.dataease.extensions.view.dto.ChartViewDTO;
import io.dataease.extensions.view.dto.ChartViewFieldDTO;
import io.dataease.result.ResultCode;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 图表视图服务控制器
 * 提供图表的增删改查和视图相关功能的API接口
 * 实现ChartViewApi接口，作为图表视图管理的HTTP入口
 *
 * <p>主要接口：</p>
 * <ul>
 *   <li>图表CRUD: save, getData, getDetail</li>
 *   <li>字段管理: listByDQ, copyField, deleteField</li>
 *   <li>视图选项: viewOption</li>
 *   <li>图表基础信息: chartBaseInfo</li>
 * </ul>
 *
 * @author Junjun
 */
@RestController
@RequestMapping("chart")
public class ChartViewServer implements ChartViewApi {
    @Resource
    private ChartViewManege chartViewManege;

    /**
     * 获取图表视图
     * 根据图表ID查询完整的图表视图信息
     *
     * @param id 图表ID
     * @return 图表视图数据传输对象
     * @throws Exception 查询异常
     */
    @Override
    public ChartViewDTO getData(Long id) throws Exception {
        try {
            return chartViewManege.getChart(id, CommonConstants.RESOURCE_TABLE.CORE);
        } catch (Exception e) {
            DEException.throwException(ResultCode.DATA_IS_WRONG.code(), e.getMessage());
        }
        return null;
    }

    /**
     * 获取数据集的字段列表（用于图表编辑）
     * 查询数据集的所有可用字段，包括维度和度量，并进行权限过滤
     *
     * @param id 数据集ID
     * @param chartId 图表ID（用于获取图表计算字段）
     * @param dto 图表视图信息
     * @return 包含维度列表和度量列表的Map，key为"dimensionList"和"quotaList"
     */
    @Override
    public Map<String, List<ChartViewFieldDTO>> listByDQ(Long id, Long chartId, ChartViewDTO dto) {
        Map<String, List<ChartViewFieldDTO>> stringListMap = chartViewManege.listByDQ(id, chartId, dto);
        DatasetUtils.listEncode(stringListMap.get("dimensionList"));
        DatasetUtils.listEncode(stringListMap.get("quotaList"));
        return stringListMap;
    }

    /**
     * 保存图表视图
     * 创建或更新图表视图配置信息
     *
     * @param dto 图表视图数据传输对象
     * @return 保存后的图表视图信息
     * @throws Exception 保存异常
     */
    @Override
    public ChartViewDTO save(ChartViewDTO dto) throws Exception {
        return chartViewManege.save(dto);
    }

    /**
     * 检查两个图表是否使用相同数据集
     * 对比源图表和目标图表的数据集ID
     *
     * @param viewIdSource 源图表ID
     * @param viewIdTarget 目标图表ID
     * @return "yes"表示相同，"no"表示不同
     */
    @Override
    public String checkSameDataSet(String viewIdSource, String viewIdTarget) {
        return chartViewManege.checkSameDataSet(viewIdSource, viewIdTarget);
    }

    /**
     * 获取图表详细信息
     * 根据ID和资源表类型查询图表的详细配置信息
     *
     * @param id 图表ID
     * @param resourceTable 资源表类型（CORE/SNAPSHOT）
     * @return 图表视图数据传输对象
     */
    @Override
    public ChartViewDTO getDetail(Long id, String resourceTable) {
        return chartViewManege.getDetails(id, resourceTable);
    }

    /**
     * 获取视图选择器列表
     * 查询指定仪表板下的所有图表，用于视图选择器
     *
     * @param resourceId 资源ID（仪表板ID）
     * @return 视图选择器VO列表
     */
    @Override
    public List<ViewSelectorVO> viewOption(Long resourceId) {
        return chartViewManege.viewOption(resourceId);
    }

    /**
     * 复制字段到图表
     * 将数据集字段复制为图表的计算字段
     *
     * @param id 原字段ID
     * @param chartId 目标图表ID
     */
    @Override
    public void copyField(Long id, Long chartId) {
        chartViewManege.copyField(id, chartId);
    }

    /**
     * 删除字段
     * 根据字段ID删除字段记录
     *
     * @param id 字段ID
     */
    @Override
    public void deleteField(Long id) {
        chartViewManege.deleteField(id);
    }

    /**
     * 批量删除图表字段
     * 删除指定图表的所有计算字段
     *
     * @param chartId 图表ID
     */
    @Override
    public void deleteFieldByChart(Long chartId) {
        chartViewManege.deleteFieldByChartId(chartId);
    }

    /**
     * 获取图表基础信息
     * 查询图表的基础配置，包括坐标轴、样式等
     *
     * @param id 图表ID
     * @param resourceTable 资源表类型
     * @return 图表基础信息VO，不存在则返回null
     */
    @Override
    public ChartBaseVO chartBaseInfo(Long id, String resourceTable) {
        return chartViewManege.chartBaseInfo(id, resourceTable);
    }
}
