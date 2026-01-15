package io.dataease.api.permissions.auth.api;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.permissions.auth.dto.*;
import io.dataease.api.permissions.auth.vo.PermissionVO;
import io.dataease.api.permissions.auth.vo.ResourceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 权限管理 API 接口
 *
 * <p>提供业务资源和菜单的权限管理功能，包括资源树查询、权限授予、权限查询等核心操作。
 * 该接口支持两种权限模型：</p>
 *
 * <ul>
 *   <li><strong>业务资源权限：</strong>管理数据集、仪表板、数据源等业务对象的访问权限</li>
 *   <li><strong>菜单权限：</strong>管理系统功能菜单的访问权限</li>
 * </ul>
 *
 * <p><strong>权限授予方式：</strong></p>
 * <ul>
 *   <li>对象维度授权：为用户/角色/组织授予对多个资源的访问权限</li>
 *   <li>资源维度授权：为单个资源授予多个用户/角色/组织的访问权限</li>
 * </ul>
 *
 * <p><strong>权限粒度控制：</strong></p>
 * <ul>
 *   <li>支持多级权重控制（读取、编辑、管理等）</li>
 *   <li>支持数据集的行级、列级权限控制</li>
 *   <li>支持权限继承和权限叠加</li>
 * </ul>
 *
 * @author fit2cloud-someone
 * @since 1.0
 */
@Tag(name = "权限管理")
@ApiSupport(order = 885, author = "fit2cloud-someone")
public interface AuthApi {

    /**
     * 查询业务资源树
     *
     * <p>根据资源类型标识查询业务资源的树形结构，用于权限授予界面展示可授权的资源列表。
     * 返回的树形结构包含资源的层级关系，支持文件夹和叶子节点。</p>
     *
     * <p>支持的资源类型包括：
     * <ul>
     *   <li>dataset: 数据集</li>
     *   <li>dashboard: 仪表板</li>
     *   <li>datasource: 数据源</li>
     *   <li>folder: 文件夹</li>
     * </ul>
     * </p>
     *
     * @param flag 资源类型标识，用于区分不同类型的业务资源
     * @return 资源树形结构列表，按层级组织
     */
    @Operation(summary = "查询资源树")
    @ApiOperationSupport(order = 1)
    @Parameter(name = "flag", description = "类型")
    @GetMapping("/busiResource/{flag}")
    List<ResourceVO> busiResource(@PathVariable("flag") String flag);

    /**
     * 查询对象已授权的业务资源
     *
     * <p>查询指定对象（用户、角色或组织）对指定类型业务资源拥有的权限列表。
     * 用于在权限管理界面展示某个对象已被授予哪些资源的访问权限。</p>
     *
     * <p>返回结果包含：
     * <ul>
     *   <li>直接授予的权限：该对象直接被授予的权限</li>
     *   <li>继承权限：通过组织、角色继承获得的权限</li>
     *   <li>权限详情：包括权重、行列权限等详细配置</li>
     * </ul>
     * </p>
     *
     * @param request 权限查询请求，包含对象 ID、对象类型和资源类型
     * @return 权限视图对象，包含该对象的所有权限信息
     */
    @Operation(summary = "查询对象已授权资源")
    @ApiOperationSupport(order = 3)
    @PostMapping("/busiPermission")
    PermissionVO busiPermission(@RequestBody BusiPermissionRequest request);

    /**
     * 查询资源已授权的对象列表
     *
     * <p>查询哪些对象（用户、角色或组织）被授予了访问指定业务资源的权限。
     * 用于在资源权限管理界面展示该资源已授权给哪些对象。</p>
     *
     * <p>应用场景：
     * <ul>
     *   <li>查看某个数据集被授权给哪些用户/角色</li>
     *   <li>查看某个仪表板的权限分配情况</li>
     *   <li>审计特定资源的访问权限</li>
     * </ul>
     * </p>
     *
     * @param request 权限查询请求，包含资源 ID、对象类型和资源类型
     * @return 权限视图对象，包含被授权对象的列表及其权限详情
     */
    @Operation(summary = "查询资源已授权对象")
    @ApiOperationSupport(order = 5)
    @PostMapping("/busiTargetPermission")
    PermissionVO busiTargetPermission(@RequestBody BusiPermissionRequest request);

    /**
     * 查询系统菜单树
     *
     * <p>获取系统所有功能菜单的树形结构，用于菜单权限配置界面。
     * 返回的树形结构反映了系统的菜单层级关系。</p>
     *
     * <p>菜单树特点：
     * <ul>
     *   <li>包含所有系统功能模块的菜单</li>
     *   <li>反映菜单的父子层级关系</li>
     *   <li>包含菜单的基本信息（ID、名称）</li>
     * </ul>
     * </p>
     *
     * @return 菜单树形结构列表
     */
    @Operation(summary = "查询菜单树")
    @ApiOperationSupport(order = 2)
    @GetMapping("/menuResource")
    List<ResourceVO> menuResource();

    /**
     * 查询对象已授权的菜单列表
     *
     * <p>查询指定对象（用户、角色或组织）可以访问的系统菜单列表。
     * 用于判断对象是否有权访问某个功能模块。</p>
     *
     * <p>典型应用：
     * <ul>
     *   <li>构建用户的个性化菜单</li>
     *   <li>控制前端页面的显示权限</li>
     *   <li>配置角色的功能访问范围</li>
     * </ul>
     * </p>
     *
     * @param request 菜单权限查询请求，包含对象 ID
     * @return 权限视图对象，包含可访问的菜单列表
     */
    @Operation(summary = "查询对象已授权菜单")
    @ApiOperationSupport(order = 4)
    @PostMapping("/menuPermission")
    PermissionVO menuPermission(@RequestBody MenuPermissionRequest request);

    /**
     * 查询菜单已授权的对象列表
     *
     * <p>查询哪些对象（用户、角色或组织）被授予了访问指定菜单的权限。
     * 用于菜单权限管理界面展示该菜单的权限分配情况。</p>
     *
     * @param request 菜单权限查询请求，包含菜单 ID
     * @return 权限视图对象，包含被授权对象的列表
     */
    @Operation(summary = "查询菜单已授权对象")
    @ApiOperationSupport(order = 6)
    @PostMapping("/menuTargetPermission")
    PermissionVO menuTargetPermission(@RequestBody MenuPermissionRequest request);

    /**
     * 保存业务资源权限（对象维度）
     *
     * <p>从对象维度为用户/角色/组织授予或修改对业务资源的访问权限。
     * 一次性可以为一个对象配置对多个资源的权限。</p>
     *
     * <p>操作说明：
     * <ul>
     *   <li>新增权限：为之前没有权限的资源授予访问权限</li>
     *   <li>修改权限：调整已有权限的权重或行列权限配置</li>
     *   <li>删除权限：传入空权限列表表示移除所有权限</li>
     * </ul>
     * </p>
     *
     * <p><strong>注意：</strong>此操作会覆盖该对象对指定资源的现有权限配置。</p>
     *
     * @param editor 业务权限编辑器，包含对象信息、资源类型和权限配置列表
     */
    @Operation(summary = "保存资源权限")
    @ApiOperationSupport(order = 7)
    @PostMapping("/saveBusiPer")
    void saveBusiPer(@RequestBody BusiPerEditor editor);

    /**
     * 保存业务资源权限（资源维度）
     *
     * <p>从资源维度为单个或多个业务资源批量授予权限给多个对象。
     * 适用于需要快速为一批资源统一配置权限的场景。</p>
     *
     * <p>应用场景：
     * <ul>
     *   <li>新建资源后初始化权限配置</li>
     *   <li>批量调整多个资源的权限分配</li>
     *   <li>为新角色快速配置资源访问权限</li>
     * </ul>
     * </p>
     *
     * @param creator 业务目标权限构造器，包含资源 ID 列表和授权对象的权限配置
     */
    @Operation(summary = "资源维度保存权限")
    @ApiOperationSupport(order = 9)
    @PostMapping("/saveBusiTargetPer")
    void saveBusiTargetPer(@RequestBody BusiTargetPerCreator creator);

    /**
     * 保存菜单权限（对象维度）
     *
     * <p>从对象维度为用户/角色/组织授予或修改菜单访问权限。
     * 一次性可以为一个对象配置对多个菜单的访问权限。</p>
     *
     * <p>功能特点：
     * <ul>
     *   <li>支持菜单级联授权（授予父菜单自动授予子菜单）</li>
     *   <li>支持精确控制每个菜单的访问权限</li>
     *   <li>权限变更立即生效</li>
     * </ul>
     * </p>
     *
     * @param editor 菜单权限编辑器，包含对象 ID 和菜单权限配置列表
     */
    @Operation(summary = "保存菜单权限")
    @ApiOperationSupport(order = 8)
    @PostMapping("/saveMenuPer")
    void saveMenuPer(@RequestBody MenuPerEditor editor);

    /**
     * 保存菜单权限（菜单维度）
     *
     * <p>从菜单维度为单个或多个菜单批量授予访问权限给多个对象。
     * 适用于需要快速为一批菜单统一配置访问权限的场景。</p>
     *
     * <p>典型用例：
     * <ul>
     *   <li>新增功能模块后初始化菜单权限</li>
     *   <li>快速为新角色配置功能访问范围</li>
     *   <li>批量调整功能模块的访问控制</li>
     * </ul>
     * </p>
     *
     * @param creator 菜单目标权限构造器，包含菜单 ID 列表和授权对象的权限配置
     */
    @Operation(summary = "菜单维度保存权限")
    @ApiOperationSupport(order = 10)
    @PostMapping("/saveMenuTargetPer")
    void saveMenuTargetPer(@RequestBody MenuTargetPerCreator creator);


}
