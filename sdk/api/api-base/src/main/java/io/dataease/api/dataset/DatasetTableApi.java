package io.dataease.api.dataset;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.dataset.dto.MultFieldValuesRequest;
import io.dataease.api.dataset.engine.SQLFunctionDTO;
import io.dataease.extensions.datasource.dto.DatasetTableFieldDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * DataEase数据集表管理API接口
 * <p>
 * 提供数据集表的字段管理、数据查询和计算字段管理功能。数据集表是
 * DataEase中数据分析的基础组件，对应具体的数据表或视图。
 * </p>
 *
 * <p><b>主要功能：</b></p>
 * <ul>
 *   <li><b>字段管理</b> - 创建、编辑、删除数据表字段</li>
 *   <li><b>计算字段</b> - 管理用户自定义的计算字段</li>
 *   <li><b>数据查询</b> - 获取字段数据和统计信息</li>
 *   <li><b>函数支持</b> - 提供SQL函数和数据处理功能</li>
 * </ul>
 *
 * @author Junjun
 * @author DataEase团队
 * @since 1.0.0
 */
@Tag(name = "数据集管理:表")
@ApiSupport(order = 977)
public interface DatasetTableApi {

    /**
     * 保存数据表字段
     * <p>
     * 创建或更新数据表字段配置。主要用于图表计算字段的单独保存，
     * 支持自定义计算规则和数据类型转换。
     * </p>
     *
     * @param datasetTableFieldDTO 数据表字段配置信息
     * @return 保存后的字段信息
     * @throws Exception 当保存过程发生错误时抛出异常
     */
    @Operation(summary = "保存字段")
    @PostMapping("save")
    DatasetTableFieldDTO save(@RequestBody DatasetTableFieldDTO datasetTableFieldDTO) throws Exception;

    /**
     * 获取数据表字段详情
     * <p>
     * 根据字段ID获取字段的详细信息和配置。
     * 包括字段的类型、计算规则、显示设置等。
     * </p>
     *
     * @param id 字段ID
     * @return 字段的详细信息
     */
    @Operation(summary = "查询字段")
    @PostMapping("get/{id}")
    DatasetTableFieldDTO get(@PathVariable Long id);

    /**
     * 获取数据集的所有字段
     * <p>
     * 根据数据集ID获取该数据集下所有表的字段列表。
     * 返回的字段包括原始字段和计算字段。
     * </p>
     *
     * @param id 数据集ID
     * @return 数据集的所有字段列表
     */
    @Operation(summary = "获取数据集字段")
    @PostMapping("listByDatasetGroup/{id}")
    List<DatasetTableFieldDTO> listByDatasetGroup(@PathVariable Long id);

    /**
     * 批量获取多个数据集的字段映射
     * <p>
     * 根据数据集ID列表批量获取多个数据集的字段信息。
     * 返回以数据集ID为键的字段列表映射。
     * </p>
     *
     * @param ids 数据集ID列表
     * @return 数据集ID到字段列表的映射
     */
    @Operation(summary = "获取数据集字段map")
    @PostMapping("listByDsIds")
    Map<String, List<DatasetTableFieldDTO>> listByDsIds(@RequestBody List<Long> ids);

    /**
     * 删除数据表字段
     * <p>
     * 删除指定的数据表字段。主要用于删除计算字段，
     * 原始数据表字段不能直接删除。
     * </p>
     *
     * @param id 要删除的字段ID
     */
    @Operation(summary = "删除字段")
    @PostMapping("delete/{id}")
    void delete(@PathVariable Long id);

    @Operation(summary = "获取字段分组")
    @PostMapping("listByDQ/{id}")
    Map<String, List<DatasetTableFieldDTO>> listByDQ(@PathVariable Long id);

    @Operation(summary = "获取copilot字段分组")
    @PostMapping("copilotFields/{id}")
    Map<String, List<DatasetTableFieldDTO>> copilotFields(@PathVariable Long id) throws Exception;

    @Operation(summary = "获取字段")
    @GetMapping("listWithPermissions/{id}")
    List<DatasetTableFieldDTO> listFieldsWithPermissions(@PathVariable Long id);

    @Operation(summary = "获取枚举值")
    @PostMapping("multFieldValuesForPermissions")
    List<String> multFieldValuesForPermissions(@RequestBody MultFieldValuesRequest multFieldValuesRequest) throws Exception;

    @Operation(summary = "获取计算字段函数")
    @PostMapping("getFunction")
    List<SQLFunctionDTO> getFunction();

    @Operation(summary = "删除图表计算字段", hidden = true)
    @PostMapping("deleteByChartId/{id}")
    void deleteByChartId(@PathVariable Long id);
}
