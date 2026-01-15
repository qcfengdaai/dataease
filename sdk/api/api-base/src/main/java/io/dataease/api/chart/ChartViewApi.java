package io.dataease.api.chart;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.chart.vo.ChartBaseVO;
import io.dataease.api.chart.vo.ViewSelectorVO;
import io.dataease.extensions.view.dto.ChartViewDTO;
import io.dataease.extensions.view.dto.ChartViewFieldDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * DataEase图表视图管理API接口
 * <p>
 * 提供图表的查看、编辑、数据查询和字段管理功能。图表视图是DataEase中
 * 数据可视化的核心组件，支持多种图表类型和丰富的数据分析功能。
 * </p>
 *
 * <p><b>主要功能：</b></p>
 * <ul>
 *   <li><b>图表查看</b> - 获取图表详情和数据</li>
 *   <li><b>图表编辑</b> - 保存和更新图表配置</li>
 *   <li><b>字段管理</b> - 复制、删除和管理图表字段</li>
 *   <li><b>数据查询</b> - 根据配置获取图表数据</li>
 * </ul>
 *
 * @author Junjun
 * @author DataEase团队
 * @since 1.0.0
 */
@Tag(name = "图表管理:查看")
@ApiSupport(order = 988)
public interface ChartViewApi {
    /**
     * 获取图表数据和配置
     * <p>
     * 根据图表ID获取图表的详细配置并同时执行数据查询。
     * 返回的结果包含图表的完整配置和计算后的数据。
     * </p>
     *
     * @param id 图表ID
     * @return 完整的图表视图对象，包含数据和配置
     * @throws Exception 当数据查询或配置解析失败时抛出异常
     */
    @Operation(summary = "查询图表详情并同时计算数据", hidden = true)
    @PostMapping("getChart/{id}")
    ChartViewDTO getData(@PathVariable Long id) throws Exception;

    /**
     * 按数据查询获取图表字段
     * <p>
     * 根据数据集和图表配置获取字段列表。返回的结果按照字段类型
     * 进行分组，包括维度字段、指标字段等。
     * </p>
     *
     * @param id 数据集ID
     * @param chartId 图表ID
     * @param dto 图表配置信息
     * @return 分类后的字段列表，按类型组织
     */
    @Operation(summary = "获取图表字段")
    @PostMapping("listByDQ/{id}/{chartId}")
    Map<String, List<ChartViewFieldDTO>> listByDQ(@PathVariable Long id, @PathVariable Long chartId, @RequestBody ChartViewDTO dto);

    /**
     * 保存图表配置
     * <p>
     * 保存或更新图表的完整配置信息。包括图表类型、字段配置、
     * 样式设置、过滤条件等。保存后会返回更新后的图表信息。
     * </p>
     *
     * @param dto 图表配置数据传输对象
     * @return 保存后的图表视图对象
     * @throws Exception 当保存过程发生错误时抛出异常
     */
    @Operation(summary = "保存图表")
    @PostMapping("save")
    ChartViewDTO save(@RequestBody ChartViewDTO dto) throws Exception;

    /**
     * 检查两个图表是否使用相同数据集
     * <p>
     * 检查指定的两个图表视图是否基于相同的数据集。
     * 主要用于图表字段复制和关联操作的前置检查。
     * </p>
     *
     * @param viewIdSource 源图表视图ID
     * @param viewIdTarget 目标图表视图ID
     * @return 检查结果信息，表示是否使用相同数据集
     */
    @Operation(summary = "检查是否同数据集")
    @GetMapping("/checkSameDataSet/{viewIdSource}/{viewIdTarget}")
    String checkSameDataSet(@PathVariable String viewIdSource, @PathVariable String viewIdTarget);

    /**
     * 获取图表详细配置信息
     * <p>
     * 根据图表ID和资源表类型获取图表的详细配置信息。
     * 只返回配置信息，不执行数据查询。
     * </p>
     *
     * @param id 图表ID
     * @param resourceTable 资源表类型标识
     * @return 图表的详细配置信息
     */
    @Operation(summary = "查询图表详情")
    @PostMapping("getDetail/{id}/{resourceTable}")
    ChartViewDTO getDetail(@PathVariable Long id, @PathVariable String resourceTable);

    /**
     * 获取仪表板下的视图选项
     * <p>
     * 获取指定仪表板下所有可用的视图选项，用于仪表板组件中的
     * 视图选择器和联动过滤配置。
     * </p>
     *
     * @param resourceId 仪表板资源ID
     * @return 视图选项列表
     */
    @Operation(summary = "查询仪表板下视图项")
    @GetMapping("/viewOption/{resourceId}")
    List<ViewSelectorVO> viewOption(@PathVariable("resourceId") Long resourceId);

    /**
     * 复制视图字段
     * <p>
     * 将指定视图的字段配置复制到另一个图表中。
     * 只有使用相同数据集的图表才能进行字段复制操作。
     * </p>
     *
     * @param id 源视图ID，字段配置的来源
     * @param chartId 目标图表ID，复制到的目标图表
     */
    @Operation(summary = "视图复制字段")
    @PostMapping("copyField/{id}/{chartId}")
    void copyField(@PathVariable Long id, @PathVariable Long chartId);

    /**
     * 删除视图字段
     * <p>
     * 删除指定的视图字段配置。只删除在当前视图中的
     * 字段配置，不影响数据集的原始字段定义。
     * </p>
     *
     * @param id 要删除的字段ID
     */
    @Operation(summary = "视图删除字段")
    @PostMapping("deleteField/{id}")
    void deleteField(@PathVariable Long id);

    /**
     * 清空图表的计算字段
     * <p>
     * 删除指定图表中的所有计算字段配置。计算字段是用户
     * 自定义的数据处理字段，区别于数据集的原始字段。
     * </p>
     *
     * @param chartId 图表ID
     */
    @Operation(summary = "清空当前视图计算字段")
    @PostMapping("deleteFieldByChart/{chartId}")
    void deleteFieldByChart(@PathVariable Long chartId);

    /**
     * 获取图表的基本信息
     * <p>
     * 获取图表的基本标识信息，如名称、类型、创建信息等。
     * 主要用于图表编辑器的头部信息展示和导航。
     * </p>
     *
     * @param id 图表ID
     * @param resourceTable 资源表类型标识
     * @return 图表的基本信息对象
     */
    @Operation(summary = "视图头部信息")
    @GetMapping("/chartBaseInfo/{id}/{resourceTable}")
    ChartBaseVO chartBaseInfo(@PathVariable("id") Long id, @PathVariable String resourceTable);
}
