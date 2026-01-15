package io.dataease.api.chart;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.chart.request.ChartExcelRequest;
import io.dataease.auth.DeApiPath;
import io.dataease.auth.DePermit;
import io.dataease.extensions.view.dto.ChartViewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static io.dataease.constant.AuthResourceEnum.PANEL;

/**
 * DataEase图表数据管理API接口
 * <p>
 * 提供图表数据的获取、导出和字段值查询功能。专注于图表的数据层操作，
 * 与图表的显示配置分离，提供纯净的数据服务。
 * </p>
 *
 * <p><b>主要功能：</b></p>
 * <ul>
 *   <li><b>数据查询</b> - 执行图表数据查询和计算</li>
 *   <li><b>数据导出</b> - 导出Excel等格式的数据</li>
 *   <li><b>字段查询</b> - 获取字段的可选值和下钻数据</li>
 *   <li><b>权限控制</b> - 根据用户权限过滤数据结果</li>
 * </ul>
 *
 * @author Junjun
 * @author DataEase团队
 * @since 1.0.0
 */
@Tag(name = "图表管理:数据")
@ApiSupport(order = 989)
@DeApiPath(value = "/chartData", rt = PANEL)
public interface ChartDataApi {
    /**
     * 获取图表数据
     * <p>
     * 根据图表配置执行数据查询，返回图表展示所需的数据。
     * 支持各种类型的图表、过滤条件、排序等高级特性。
     * </p>
     *
     * @param chartViewDTO 图表视图配置，包含查询参数和显示设置
     * @return 包含数据结果的图表视图对象
     * @throws Exception 当数据查询失败时抛出异常
     */
    @Operation(summary = "获取图表数据")
    @PostMapping("getData")
    ChartViewDTO getData(@RequestBody ChartViewDTO chartViewDTO) throws Exception;

    /**
     * 导出图表数据
     * <p>
     * 将图表的数据查询结果导出为Excel文件。支持根据用户权限
     * 进行数据过滤，确保数据安全。导出的数据格式与图表显示一致。
     * </p>
     *
     * @param request 导出请求参数，包含图表配置和导出设置
     * @param response HTTP响应对象，用于返回Excel文件
     * @throws Exception 当数据查询或文件生成失败时抛出异常
     */
    @Operation(summary = "导出数据")
    @PostMapping("/innerExportDetails")
    @DePermit(value = {"#p0.dvId+':export_view'"}, busiFlag = "#p0.busiFlag")
    void innerExportDetails(@RequestBody ChartExcelRequest request, HttpServletResponse response) throws Exception;

    /**
     * 导出数据集明细数据
     * <p>
     * 导出图表所基于的数据集的原始明细数据，不经过图表的聚合和计算。
     * 支持根据用户权限过滤数据，保证数据安全和隐私。
     * </p>
     *
     * @param request 导出请求参数，包含数据集信息和过滤条件
     * @param response HTTP响应对象，用于返回Excel文件
     * @throws Exception 当数据查询或文件生成失败时抛出异常
     */
    @Operation(summary = "导出明细数据")
    @PostMapping("/innerExportDataSetDetails")
    @DePermit(value = {"#p0.dvId+':export_detail'"}, busiFlag = "#p0.busiFlag")
    void innerExportDataSetDetails(@RequestBody ChartExcelRequest request, HttpServletResponse response) throws Exception;

    /**
     * 获取字段的可选值列表
     * <p>
     * 根据指定的字段ID和类型，获取该字段在当前数据上下文中的所有可选值。
     * 主要用于过滤器组件的选项加载和数据验证。
     * </p>
     *
     * @param view 图表视图配置，提供数据上下文
     * @param fieldId 字段ID
     * @param fieldType 字段类型标识
     * @return 字段的可选值列表
     * @throws Exception 当字段数据查询失败时抛出异常
     */
    @Operation(summary = "获取字段值")
    @PostMapping("/getFieldData/{fieldId}/{fieldType}")
    List<String> getFieldData(@RequestBody ChartViewDTO view, @PathVariable Long fieldId, @PathVariable String fieldType) throws Exception;

    /**
     * 获取下钻字段的可选值
     * <p>
     * 获取用于图表下钻功能的字段可选值列表。下钻字段的数据的范围
     * 会根据当前的过滤条件和选中的数据范围进行动态调整。
     * </p>
     *
     * @param view 图表视图配置，包含当前的过滤状态
     * @param fieldId 下钻字段ID
     * @return 下钻字段的可选值列表
     * @throws Exception 当下钻数据查询失败时抛出异常
     */
    @Operation(summary = "获取下钻字段值")
    @PostMapping("/getDrillFieldData/{fieldId}")
    List<String> getDrillFieldData(@RequestBody ChartViewDTO view, @PathVariable Long fieldId) throws Exception;
}
