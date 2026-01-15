package io.dataease.api.ds;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.ds.vo.*;
import io.dataease.auth.DeApiPath;
import io.dataease.auth.DePermit;
import io.dataease.exception.DEException;
import io.dataease.extensions.datasource.dto.ApiDefinition;
import io.dataease.extensions.datasource.dto.DatasetTableDTO;
import io.dataease.extensions.datasource.dto.DatasourceDTO;
import io.dataease.extensions.datasource.dto.TableField;
import io.dataease.extensions.datasource.vo.DatasourceConfiguration;
import io.dataease.model.BusiNodeRequest;
import io.dataease.model.BusiNodeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.dataease.constant.AuthResourceEnum.DATASOURCE;

/**
 * DataEase数据源管理API接口
 * <p>
 * 提供数据源的完整生命周期管理，包括数据源的创建、配置、测试、查询、
 * 元数据获取等功能。支持多种类型的数据源，如关系型数据库、大数据平台、
 * NoSQL数据库、Excel文件和API数据源等。
 * </p>
 *
 * <p><b>主要功能：</b></p>
 * <ul>
 *   <li><b>数据源管理</b> - 创建、更新、删除、重命名数据源</li>
 *   <li><b>连接测试</b> - 验证数据源连接配置的正确性</li>
 *   <li><b>元数据获取</b> - 获取数据库、表、字段等元数据信息</li>
 *   <li><b>文件上传</b> - 支持Excel文件上传和解析</li>
 *   <li><b>API数据源</b> - 支持API接口作为数据源</li>
 * </ul>
 *
 * @author DataEase团队
 * @since 1.0.0
 */
@Tag(name = "数据源管理:基础")
@ApiSupport(order = 969)
@DeApiPath(value = "/datasource", rt = DATASOURCE)
public interface DatasourceApi {
    /**
     * 查询数据源树形结构
     * <p>
     * 根据关键字查询数据源，返回树形结构的数据源列表。
     * 支持模糊匹配数据源名称，用于快速定位和筛选数据源。
     * </p>
     *
     * @param keyWord 过滤关键字，支持模糊匹配数据源名称
     * @return 匹配的数据源列表，以树形结构组织
     */
    @GetMapping("/query/{keyWord}")
    @Operation(summary = "查询数据源树")
    List<DatasourceDTO> query(@PathVariable("keyWord") String keyWord);

    /**
     * 保存数据源配置
     * <p>
     * 创建新的数据源配置，包括连接参数、认证信息等。
     * 在保存前会验证连接的有效性。
     * </p>
     *
     * @param dataSourceDTO 数据源配置信息
     * @return 保存成功的数据源信息
     * @throws DEException 当配置无效或连接失败时抛出异常
     */
    @PostMapping("/save")
    @Operation(summary = "保存数据源")
    DatasourceDTO save(@RequestBody BusiDsRequest dataSourceDTO) throws DEException;

    /**
     * 更新数据源配置
     * <p>
     * 修改已存在的数据源配置信息。更新时会重新验证连接的有效性，
     * 确保修改后的配置能够正常工作。
     * </p>
     *
     * @param dataSourceDTO 更新的数据源配置信息
     * @return 更新后的数据源信息
     * @throws DEException 当配置无效或连接失败时抛出异常
     */
    @PostMapping("/update")
    @Operation(summary = "更新数据源")
    DatasourceDTO update(@RequestBody BusiDsRequest dataSourceDTO) throws DEException;

    /**
     * 移动数据源位置
     * <p>
     * 将数据源移动到指定的文件夹中，用于组织和管理数据源的层级结构。
     * 支持数据源在不同文件夹之间的移动操作。
     * </p>
     *
     * @param dataSourceDTO 包含移动目标位置的请求信息
     * @return 移动后的数据源信息
     * @throws DEException 当移动操作失败时抛出异常
     */
    @PostMapping("/move")
    @Operation(summary = "移动数据源")
    DatasourceDTO move(@RequestBody BusiCreateFolderRequest dataSourceDTO) throws DEException;

    /**
     * 重命名数据源
     * <p>
     * 修改数据源的显示名称。重命名操作会检查新名称的唯一性，
     * 确保同级目录下不存在重复的名称。
     * </p>
     *
     * @param dataSourceDTO 包含新名称的重命名请求
     * @return 重命名后的数据源信息
     * @throws DEException 当名称冲突或操作失败时抛出异常
     */
    @PostMapping("/reName")
    @Operation(summary = "重命名数据源")
    DatasourceDTO reName(@RequestBody BusiRenameRequest dataSourceDTO) throws DEException;

    /**
     * 创建数据源文件夹
     * <p>
     * 在指定位置创建新的文件夹，用于组织和分类管理数据源。
     * 支持多级文件夹结构的创建。
     * </p>
     *
     * @param dataSourceDTO 包含文件夹信息的创建请求
     * @return 创建的文件夹信息
     * @throws DEException 当创建失败或名称冲突时抛出异常
     */
    @PostMapping("/createFolder")
    @Operation(summary = "新建数据源文件夹")
    DatasourceDTO createFolder(@RequestBody BusiCreateFolderRequest dataSourceDTO) throws DEException;

    /**
     * 校验数据源名称重复
     * <p>
     * 检查指定的数据源名称是否在同级目录下已经存在。
     * 用于创建或重命名数据源时的名称唯一性验证。
     * </p>
     *
     * @param dataSourceDTO 包含待检查名称的数据源信息
     * @return true表示名称重复，false表示名称可用
     * @throws DEException 当检查过程发生错误时抛出异常
     */
    @PostMapping("/checkRepeat")
    @Operation(summary = "校验名称重复")
    boolean checkRepeat(@RequestBody BusiDsRequest dataSourceDTO) throws DEException;

    /**
     * 获取支持的数据源类型列表
     * <p>
     * 返回系统支持的所有数据源类型，包括关系型数据库、大数据平台、
     * NoSQL数据库等。每种类型包含名称、标识和配置参数等信息。
     * </p>
     *
     * @return 支持的数据源类型列表
     * @throws DEException 当获取类型信息失败时抛出异常
     */
    @GetMapping("/types")
    @Operation(summary = "获取数据源类型")
    List<DatasourceConfiguration.DatasourceType> datasourceTypes() throws DEException;

    /**
     * 验证数据源连接
     * <p>
     * 测试数据源的连接配置是否正确，包括网络连通性、认证信息、
     * 权限等。用于在保存数据源前验证配置的有效性。
     * </p>
     *
     * @param dataSourceDTO 待验证的数据源配置
     * @return 验证结果和数据源信息
     * @throws DEException 当连接验证失败时抛出异常
     */
    @PostMapping("/validate")
    @Operation(summary = "验证数据源连接")
    DatasourceDTO validate(@RequestBody BusiDsRequest dataSourceDTO) throws DEException;

    /**
     * 获取数据库模式列表
     * <p>
     * 从指定的数据源获取所有可用的数据库模式（Schema）列表。
     * 主要用于支持多模式数据库的模式选择功能。
     * </p>
     *
     * @param dataSourceDTO 数据源配置信息
     * @return 可用的数据库模式名称列表
     * @throws DEException 当获取模式信息失败时抛出异常
     */
    @PostMapping("/getSchema")
    @Operation(summary = "获取数据库模式")
    List<String> getSchema(@RequestBody BusiDsRequest dataSourceDTO) throws DEException;

    /**
     * 验证指定数据源连接
     * <p>
     * 通过数据源ID验证现有数据源的连接状态。用于检查已配置的
     * 数据源是否仍然可以正常连接，主要用于定期健康检查。
     * </p>
     *
     * @param datasourceId 数据源ID
     * @return 验证后的数据源信息
     * @throws DEException 当验证失败或数据源不存在时抛出异常
     */
    @DePermit({"#p0+':manage'"})
    @GetMapping("/validate/{datasourceId}")
    @Operation(summary = "校验")
    DatasourceDTO validate(@PathVariable("datasourceId") Long datasourceId) throws DEException;

    /**
     * 检查数据源删除前置条件
     * <p>
     * 在删除数据源前检查是否有数据集正在使用此数据源。
     * 防止误删正在使用的数据源导致数据集无法访问。
     * </p>
     *
     * @param datasourceId 要删除的数据源ID
     * @return true表示可以删除，false表示有数据集正在使用
     */
    @DePermit({"#p0+':manage'"})
    @PostMapping("/perDelete/{datasourceId}")
    @Operation(summary = "是否有数据集正在使用此数据源")
    boolean perDelete(@PathVariable("datasourceId") Long datasourceId);

    /**
     * 删除数据源
     * <p>
     * 彻底删除指定的数据源配置。删除前会检查是否有关联的数据集，
     * 如果有关联数据集则无法删除。同时会清理相关的缓存和临时数据。
     * </p>
     *
     * @param datasourceId 要删除的数据源ID
     * @throws DEException 当删除失败或数据源不存在时抛出异常
     */
    @DePermit({"#p0+':manage'"})
    @GetMapping("/delete/{datasourceId}")
    @Operation(summary = "删除")
    void delete(@PathVariable("datasourceId") Long datasourceId) throws DEException;

    /**
     * 获取数据源详细信息
     * <p>
     * 根据数据源ID获取完整的数据源配置信息，包括连接参数、
     * 认证信息等。返回的信息包含敏感数据，需要管理权限。
     * </p>
     *
     * @param datasourceId 数据源ID
     * @return 完整的数据源配置信息
     * @throws DEException 当数据源不存在或访问权限不足时抛出异常
     */
    @DePermit({"#p0+':manage'"})
    @GetMapping("/get/{datasourceId}")
    @Operation(summary = "数据源详情")
    DatasourceDTO get(@PathVariable("datasourceId") Long datasourceId) throws DEException;

    /**
     * 获取数据源详情（隐藏密码）
     * <p>
     * 获取数据源的详细信息，但隐藏密码等敏感信息。
     * 用于前端展示数据源配置时保护敏感数据的安全性。
     * </p>
     *
     * @param datasourceId 数据源ID
     * @return 隐藏敏感信息的数据源配置
     * @throws DEException 当数据源不存在或访问权限不足时抛出异常
     */
    @DePermit({"#p0+':manage'"})
    @GetMapping("/hidePw/{datasourceId}")
    @Operation(summary = "数据源详情")
    DatasourceDTO hidePw(@PathVariable("datasourceId") Long datasourceId) throws DEException;

    /**
     * 获取简化的数据源信息
     * <p>
     * 获取数据源的基本信息，不包含详细的配置参数和敏感信息。
     * 主要用于列表展示和简单的数据源选择场景。
     * </p>
     *
     * @param datasourceId 数据源ID
     * @return 简化的数据源信息
     * @throws DEException 当数据源不存在或访问权限不足时抛出异常
     */
    @DePermit({"#p0+':read'"})
    @GetMapping("/getSimpleDs/{datasourceId}")
    @Operation(summary = "数据源详情")
    DatasourceDTO getSimpleDs(@PathVariable("datasourceId") Long datasourceId) throws DEException;


    /**
     * 获取数据表字段信息
     * <p>
     * 根据数据源和表名获取指定表的所有字段信息，包括字段名称、
     * 数据类型、是否为空、默认值、注释等元数据信息。
     * </p>
     *
     * @param req 包含数据源ID和表名的请求参数
     * @return 表字段信息列表
     * @throws DEException 当获取字段信息失败时抛出异常
     */
    @PostMapping("/getTableField")
    @Operation(summary = "获取表字段")
    List<TableField> getTableField(@RequestBody Map<String, String> req) throws DEException;

    /**
     * 同步API数据源的数据表
     * <p>
     * 对API类型的数据源执行数据表同步操作。更新API数据表的结构
     * 和数据，确保数据表与API接口的最新状态保持一致。
     * </p>
     *
     * @param req 同步请求参数，包含数据源ID等信息
     * @throws DEException 当同步操作失败时抛出异常
     */
    @PostMapping("/syncApiTable")
    @Operation(summary = "同步API数据表")
    void syncApiTable(@RequestBody Map<String, String> req) throws DEException;

    /**
     * 同步API数据源
     * <p>
     * 对API类型的数据源执行完整的同步操作。重新获取API接口的
     * 元数据信息，更新数据源的结构定义和配置信息。
     * </p>
     *
     * @param req 同步请求参数，包含数据源ID等信息
     * @throws Exception 当同步过程中发生任何错误时抛出异常
     */
    @PostMapping("/syncApiDs")
    @Operation(summary = "同步API数据源")
    void syncApiDs(@RequestBody Map<String, String> req) throws Exception;

    /**
     * 获取数据源树形结构列表
     * <p>
     * 根据指定条件查询数据源，返回层级化的树形结构。
     * 支持文件夹分组展示，便于数据源的分类管理和浏览。
     * </p>
     *
     * @param request 查询请求，包含过滤条件和排序参数
     * @return 树形结构的数据源列表
     * @throws DEException 当查询失败时抛出异常
     */
    @PostMapping("tree")
    @Operation(summary = "数据源列表")
    List<BusiNodeVO> tree(@RequestBody BusiNodeRequest request) throws DEException;


    /**
     * 获取数据源中的数据表列表
     * <p>
     * 根据数据源ID获取该数据源中所有可用的数据表。
     * 返回表的基本信息和结构元数据，用于数据集创建和表选择。
     * </p>
     *
     * @param datasetTableDTO 包含数据源ID等查询条件的请求参数
     * @return 数据表信息列表
     * @throws DEException 当获取表列表失败时抛出异常
     */
    @DePermit({"#p0.datasourceId+':read'"})
    @PostMapping("getTables")
    @Operation(summary = "获取表")
    List<DatasetTableDTO> getTables(@RequestBody DatasetTableDTO datasetTableDTO) throws DEException;

    /**
     * 获取数据表的更新状态
     * <p>
     * 检查指定数据表的状态信息，包括最后更新时间、记录数量、
     * 结构变更等。用于监控数据表的变化和同步状态。
     * </p>
     *
     * @param datasetTableDTO 包含要检查的数据表信息
     * @return 数据表状态信息列表
     * @throws DEException 当获取状态信息失败时抛出异常
     */
    @DePermit({"#p0.datasourceId+':read'"})
    @PostMapping("getTableStatus")
    @Operation(summary = "获取数据表更新状态")
    List<DatasetTableDTO> getTableStatus(@RequestBody DatasetTableDTO datasetTableDTO) throws DEException;

    /**
     * 校验API数据源配置
     * <p>
     * 验证API类型数据源的配置参数是否正确。检查API接口的
     * 可访问性、认证信息和数据格式，确保可以正常获取数据。
     * </p>
     *
     * @param data API数据源的配置参数
     * @return API接口的定义信息和验证结果
     * @throws DEException 当API配置验证失败时抛出异常
     */
    @PostMapping("/checkApiDatasource")
    @Operation(summary = "校验API数据源")
    ApiDefinition checkApiDatasource(@RequestBody Map<String, String> data) throws DEException;

    /**
     * 上传数据文件
     * <p>
     * 上传Excel等文件到指定的数据源。支持多种编辑模式，
     * 包括新增、替换、追加等。会解析文件内容并更新数据源。
     * </p>
     *
     * @param file 要上传的文件
     * @param datasourceId 目标数据源ID
     * @param editType 编辑类型（0-新增，1-替换，2-追加）
     * @return 解析后的文件数据信息
     * @throws DEException 当文件上传或解析失败时抛出异常
     */
    @PostMapping("/uploadFile")
    @Operation(summary = "上传文件")
    ExcelFileData uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("id") long datasourceId, @RequestParam("editType") Integer editType) throws DEException;

    /**
     * 预览数据源数据
     * <p>
     * 根据指定条件从数据源中获取限量的数据用于预览。
     * 支持设置查询条件、排序和返回数量限制，用于数据集创建前的验证。
     * </p>
     *
     * @param req 预览请求参数，包含数据源ID、查询条件等
     * @return 预览数据结果，包含字段信息和数据行
     * @throws DEException 当数据预览失败时抛出异常
     */
    @PostMapping("/previewData")
    @Operation(summary = "预览数据")
    Map<String, Object> previewDataWithLimit(@RequestBody Map<String, Object> req) throws DEException;

    /**
     * 获取最近使用的数据源
     * <p>
     * 返回当前用户最近使用过的数据源列表。
     * 用于快速访问和提高用户体验，减少搜索和选择时间。
     * </p>
     *
     * @return 最近使用的数据源ID或名称列表
     */
    @PostMapping("/latestUse")
    @Operation(summary = "最近常用")
    public List<String> latestUse();

    /**
     * 获取是否显示完成页面的设置
     * <p>
     * 检查系统设置，判断在数据源操作完成后是否需要显示
     * 完成页面。用于控制用户界面的展示逻辑和用户引导。
     * </p>
     *
     * @return true表示显示完成页面，false表示不显示
     * @throws DEException 当获取设置失败时抛出异常
     */
    @GetMapping("showFinishPage")
    @Operation(summary = "是否显示完成页面")
    public boolean showFinishPage() throws DEException;

    /**
     * 设置是否显示完成页面
     * <p>
     * 配置系统设置，控制在数据源操作完成后是否显示完成页面。
     * 用于个性化用户界面和优化用户体验。
     * </p>
     *
     * @throws DEException 当设置操作失败时抛出异常
     */
    @PostMapping("setShowFinishPage")
    @Operation(summary = "是否显示完成页面")
    public void setShowFinishPage() throws DEException;

    /**
     * 获取数据源同步记录
     * <p>
     * 分页查询指定数据源的同步任务日志记录。包含同步时间、
     * 执行结果、错误信息等，用于监控和排查数据同步问题。
     * </p>
     *
     * @param goPage 页码（从1开始）
     * @param pageSize 每页记录数
     * @param dsId 数据源ID
     * @return 分页的同步记录列表
     */
    @PostMapping("/listSyncRecord/{dsId}/{goPage}/{pageSize}")
    @Operation(summary = "更新日志")
    IPage<CoreDatasourceTaskLogDTO> listSyncRecord(@PathVariable("goPage") int goPage, @PathVariable("pageSize") int pageSize, @PathVariable("dsId") Long dsId);

    /**
     * 内部获取数据源信息
     * <p>
     * 内部接口，用于系统内部模块间的数据源信息获取。
     * 不经过权限检查，仅供系统内部使用。
     * </p>
     *
     * @param datasourceId 数据源ID
     * @return 数据源信息
     * @throws DEException 当数据源不存在时抛出异常
     */
    DatasourceDTO innerGet(Long datasourceId) throws DEException;

    /**
     * 内部获取数据源名称
     * <p>
     * 内部接口，用于系统内部模块间获取数据源的名称信息。
     * 不经过权限检查，仅返回数据源名称字符串。
     * </p>
     *
     * @param datasourceId 数据源ID
     * @return 数据源名称
     * @throws DEException 当数据源不存在时抛出异常
     */
    String getName(Long datasourceId) throws DEException;

    /**
     * 内部批量获取数据源信息
     * <p>
     * 内部接口，根据ID列表和类型筛选批量获取数据源信息。
     * 用于系统内部模块间的批量数据查询和关联操作。
     * </p>
     *
     * @param ids 数据源ID列表，可为空
     * @param types 数据源类型列表，可为空
     * @return 匹配条件的数据源列表
     * @throws DEException 当查询失败时抛出异常
     */
    List<DatasourceDTO> innerList(List<Long> ids, List<String> types) throws DEException;

    /**
     * 获取数据源简单信息
     * <p>
     * 返回数据源的基本信息，不包含详细配置和敏感数据。
     * 主要用于快速查询和列表展示场景。
     * </p>
     *
     * @param id 数据源ID
     * @return 数据源简单信息对象
     */
    @GetMapping("/simple/{id}")
    DsSimpleVO simple(@PathVariable("id") Long id);

    /**
     * 获取多维表格列表
     * <p>
     * 从数据源中获取支持多维分析的数据表列表。
     * 主要用于复杂的分析场景和高级图表组件。
     * </p>
     *
     * @param data 包含数据源信息的查询参数
     * @return 多维表格信息列表
     * @throws DEException 当获取多维表格失败时抛出异常
     */
    @PostMapping("/multidimensionalTables")
    @Operation(summary = "获取多维表格列表")
    List<Map<String, String>> multidimensionalTables(@RequestBody Map<String, String> data) throws DEException;

    /**
     * 加载远程文件
     * <p>
     * 从远程地址加载Excel等数据文件到数据源中。支持通过URL、
     * FTP等方式获取远程文件，并解析为数据源可用的数据格式。
     * </p>
     *
     * @param remoteExcelRequeste 远程文件加载请求，包含文件地址和加载参数
     * @return 加载和解析后的文件数据
     * @throws DEException 当文件加载或解析失败时抛出异常
     * @throws IOException 当文件IO操作失败时抛出异常
     */
    @PostMapping("/loadRemoteFile")
    @Operation(summary = "加载文件")
    ExcelFileData loadRemoteFile(@RequestBody RemoteExcelRequest remoteExcelRequeste) throws DEException, IOException;
}
