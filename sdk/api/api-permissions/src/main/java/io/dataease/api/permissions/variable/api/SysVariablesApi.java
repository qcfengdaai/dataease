package io.dataease.api.permissions.variable.api;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.permissions.variable.dto.SysVariableDto;
import io.dataease.api.permissions.variable.dto.SysVariableValueDto;
import io.dataease.auth.DeApiPath;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static io.dataease.constant.AuthResourceEnum.SYSTEM;

/**
 * 系统变量管理 API 接口
 *
 * <p>提供系统变量和变量值的完整管理功能。系统变量用于存储系统级和用户级的配置参数，
 * 支持多种数据类型和值域限制，实现灵活的参数化配置。</p>
 *
 * <p>核心概念：</p>
 * <ul>
 *   <li>系统变量（SysVariable）：变量的定义，包括名称、类型、取值范围等</li>
 *   <li>变量值（SysVariableValue）：变量的具体取值，一个变量可以有多个可选值</li>
 *   <li>用户变量值：用户级别的变量赋值，不同用户可以有不同的变量值</li>
 * </ul>
 *
 * <p>支持的变量类型：</p>
 * <ul>
 *   <li>文本类型：字符串变量</li>
 *   <li>数值类型：支持最小值、最大值限制</li>
 *   <li>日期类型：支持日期范围限制</li>
 *   <li>枚举类型：预定义的多个可选值</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>系统配置：全局性的系统参数配置</li>
 *   <li>用户偏好：用户个性化设置</li>
 *   <li>数据筛选：在查询、报表中作为参数使用</li>
 *   <li>权限控制：基于变量值的权限判断</li>
 * </ul>
 *
 * <p>注意：此接口为隐藏接口（@Hidden），主要供系统内部使用</p>
 *
 * @author fit2cloud-someone
 * @since 1.0
 */
@Hidden
@Tag(name = "系统变量")
@ApiSupport(order = 881, author = "fit2cloud-someone")
@DeApiPath(value = "/sysVariable", rt = SYSTEM)
public interface SysVariablesApi {

    /**
     * 创建系统变量
     *
     * <p>创建新的系统变量定义，指定变量的名称、类型和取值规则。</p>
     *
     * @param sysVariableDto 系统变量信息，包含变量定义的所有属性
     * @return 创建成功的系统变量信息（包含生成的 ID）
     */
    @Operation(summary = "创建变量")
    @PostMapping("/create")
    SysVariableDto create(@RequestBody SysVariableDto sysVariableDto);

    /**
     * 编辑系统变量
     *
     * <p>更新已存在的系统变量定义，可以修改变量名称、取值规则等。</p>
     *
     * @param sysVariableDto 系统变量信息，必须包含变量 ID
     * @return 更新后的系统变量信息
     */
    @Operation(summary = "编辑变量")
    @PostMapping("/edit")
    SysVariableDto edit(@RequestBody SysVariableDto sysVariableDto);

    /**
     * 删除系统变量
     *
     * <p>删除指定的系统变量。删除变量时会同时删除该变量的所有值。
     * 如果变量正在被使用，可能会导致相关功能异常。</p>
     *
     * @param id 系统变量 ID
     */
    @Operation(summary = "删除变量")
    @GetMapping("/delete/{id}")
    void delete(@PathVariable("id") Long id);

    /**
     * 查询系统变量详情
     *
     * <p>获取指定系统变量的详细信息，包括变量的所有配置属性。</p>
     *
     * @param id 系统变量 ID
     * @return 系统变量详细信息
     */
    @Operation(summary = "变量详细信息")
    @GetMapping("/detail/{id}")
    SysVariableDto detail(@PathVariable("id") Long id);

    /**
     * 查询系统变量列表
     *
     * <p>根据条件查询系统变量列表，支持按变量类型、名称等条件筛选。</p>
     *
     * @param sysVariableDto 查询条件，包含筛选参数
     * @return 符合条件的系统变量列表
     */
    @Operation(summary = "系统变量列表")
    @PostMapping("/query")
    List<SysVariableDto> query(@RequestBody SysVariableDto sysVariableDto);

    /**
     * 创建变量值
     *
     * <p>为指定的系统变量创建一个新的可选值。
     * 对于枚举类型的变量，可以创建多个可选值供用户选择。</p>
     *
     * @param sysVariableDto 变量值信息，必须指定所属的系统变量 ID
     * @return 创建成功的变量值信息（包含生成的 ID）
     */
    @Operation(summary = "创建变量值")
    @PostMapping("/value/create")
    SysVariableValueDto createValue(@RequestBody SysVariableValueDto sysVariableDto);

    /**
     * 编辑变量值
     *
     * <p>更新已存在的变量值，可以修改值的内容和描述。</p>
     *
     * @param sysVariableDto 变量值信息，必须包含值的 ID
     * @return 更新后的变量值信息
     */
    @Operation(summary = "编辑变量值")
    @PostMapping("/value/edit")
    SysVariableValueDto editValue(@RequestBody SysVariableValueDto sysVariableDto);

    /**
     * 删除变量值
     *
     * <p>删除指定的变量值。如果该值正在被用户使用，
     * 删除后用户的变量配置将失效。</p>
     *
     * @param id 变量值 ID
     */
    @Operation(summary = "删除变量值")
    @GetMapping("/value/delete/{id}")
    void deleteValue(@PathVariable("id") String id);

    /**
     * 查询系统变量的所有值
     *
     * <p>获取指定系统变量的所有可选值列表。
     * 用于在用户选择变量值时展示可选项。</p>
     *
     * @param id 系统变量 ID
     * @return 该变量的所有可选值列表
     */
    @Operation(summary = "查看变量值详细信息")
    @GetMapping("/value/selected/{id}")
    List<SysVariableValueDto> selectVariableValue(@PathVariable("id") Long id);

    /**
     * 分页查询变量值列表
     *
     * <p>分页查询系统变量值，支持按条件筛选。
     * 用于变量值管理界面的列表展示。</p>
     *
     * @param goPage 目标页码，从 1 开始
     * @param pageSize 每页记录数
     * @param sysVariableValueDto 查询条件，包含筛选参数
     * @return 变量值分页数据
     */
    @Operation(summary = "系统变量值列表")
    @PostMapping("/value/selected/{goPage}/{pageSize}")
    IPage<SysVariableValueDto> selectPage(@PathVariable("goPage") int goPage, @PathVariable("pageSize") int pageSize, @RequestBody SysVariableValueDto sysVariableValueDto);

    /**
     * 批量删除变量值
     *
     * <p>批量删除多个变量值。适用于清理无效或过期的变量值。</p>
     *
     * @param ids 要删除的变量值 ID 列表
     */
    @Operation(summary = "批量删除变量值")
    @PostMapping("/value/batchDel")
    void batchDel(@RequestBody List<Long> ids);

}
