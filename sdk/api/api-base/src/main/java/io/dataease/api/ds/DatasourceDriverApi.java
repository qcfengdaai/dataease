package io.dataease.api.ds;


import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.ds.vo.DriveDTO;
import io.dataease.api.ds.vo.DriveJarDTO;
import io.dataease.extensions.datasource.dto.DatasourceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
/**
 * DataEase数据源驱动管理API接口
 * <p>
 * 提供数据源驱动程序的管理功能，包括驱动的安装、配置、更新和删除。
 * 支持各种数据库和数据源的JDBC驱动管理，实现动态驱动加载和版本控制。
 * </p>
 *
 * <p><b>主要功能：</b></p>
 * <ul>
 *   <li><b>驱动管理</b> - 安装、更新、删除数据源驱动</li>
 *   <li><b>JAR包管理</b> - 上传和管理驱动JAR文件</li>
 *   <li><b>版本控制</b> - 支持多版本驱动共存和切换</li>
 *   <li><b>类型分组</b> - 按数据源类型组织驱动程序</li>
 * </ul>
 *
 * @author DataEase团队
 * @since 1.0.0
 */
@Tag(name = "数据源管理:驱动")
@ApiSupport(order = 968)
public interface DatasourceDriverApi {
    /**
     * 查询数据源树形结构
     * <p>
     * 根据关键字过滤查询数据源，返回树形结构的数据源列表。
     * 此接口主要用于驱动管理中的数据源选择和关联。
     * </p>
     *
     * @param keyWord 过滤关键字，支持数据源名称模糊匹配
     * @return 匹配的数据源列表
     */
    @Operation(summary = "查询数据源树", hidden = true)
    @GetMapping("/query/{keyWord}")
    List<DatasourceDTO> query(@PathVariable("keyWord") String keyWord);

    /**
     * 获取所有数据源驱动列表
     * <p>
     * 返回系统中已安装的所有数据源驱动程序列表。
     * 包含驱动名称、版本、支持的数据源类型等基本信息。
     * </p>
     *
     * @return 数据源驱动列表
     */
    @Operation(summary = "列表")
    @GetMapping("/list")
    List<DriveDTO> list();

    /**
     * 根据数据源类型获取驱动列表
     * <p>
     * 获取支持指定数据源类型的所有驱动程序。不同类型的数据源
     * 可能需要不同的驱动程序，此接口用于筛选兼容的驱动。
     * </p>
     *
     * @param dsType 数据源类型（如mysql、postgresql、oracle等）
     * @return 支持该数据源类型的驱动列表
     */
    @Operation(summary = "根据数据源类型获取")
    @GetMapping("/list/{dsType}")
    List<DriveDTO> listByDsType(@PathVariable("dsType") String dsType);

    /**
     * 保存新的数据源驱动
     * <p>
     * 创建新的数据源驱动配置。包括驱动名称、类路径、支持的数据源类型、
     * 版本信息等。保存后驱动将可用于数据源连接。
     * </p>
     *
     * @param datasourceDrive 数据源驱动配置信息
     * @return 保存后的驱动信息
     */
    @Operation(summary = "保存")
    @PostMapping("/save")
    DriveDTO save(@RequestBody DriveDTO datasourceDrive);

    /**
     * 更新数据源驱动配置
     * <p>
     * 修改已存在的数据源驱动配置信息。可以更新驱动名称、描述、
     * 支持的数据源类型等属性。更新后立即生效。
     * </p>
     *
     * @param datasourceDrive 更新的驱动配置信息
     * @return 更新后的驱动信息
     */
    @Operation(summary = "更新")
    @PostMapping("/update")
    DriveDTO update(@RequestBody DriveDTO datasourceDrive);

    /**
     * 删除数据源驱动
     * <p>
     * 删除指定的数据源驱动及其相关的JAR文件。删除前会检查是否有
     * 数据源正在使用此驱动，如果有则无法删除。
     * </p>
     *
     * @param driverId 要删除的驱动ID
     */
    @Operation(summary = "删除")
    @PostMapping("/delete/{driverId}")
    void delete(@PathVariable("driverId") String driverId);

    /**
     * 获取驱动JAR文件列表
     * <p>
     * 获取指定驱动的所有JAR文件列表。一个驱动可能包含多个JAR文件，
     * 包括主驱动JAR和依赖库JAR文件。
     * </p>
     *
     * @param driverId 驱动ID
     * @return 该驱动的JAR文件列表
     */
    @Operation(summary = "获取驱动jar列表")
    @GetMapping("/listDriverJar/{driverId}")
    List<DriveJarDTO> listDriverJar(@PathVariable("driverId") String driverId);

    /**
     * 删除驱动JAR文件
     * <p>
     * 删除指定的驱动JAR文件。如果是驱动的主JAR文件，删除前会检查
     * 是否影响驱动的正常使用。删除后会更新驱动的类路径配置。
     * </p>
     *
     * @param jarId 要删除的JAR文件ID
     */
    @Operation(summary = "删除驱动jar")
    @PostMapping("/deleteDriverJar/{jarId}")
    void deleteDriverJar(@PathVariable("jarId") String jarId);

    /**
     * 上传驱动JAR文件
     * <p>
     * 为指定的驱动上传JAR文件。支持上传主驱动JAR和依赖库JAR。
     * 上传后系统会自动分析JAR文件并更新驱动的类路径配置。
     * </p>
     *
     * @param deDriverId 目标驱动ID
     * @param jarFile 要上传的JAR文件
     * @return 上传后的JAR文件信息
     * @throws Exception 当文件上传或处理失败时抛出异常
     */
    @Operation(summary = "上传驱动jar")
    @PostMapping("/uploadJar")
    DriveJarDTO uploadJar(@RequestParam("deDriverId") String deDriverId, @RequestParam("jarFile") MultipartFile jarFile) throws Exception;
}
