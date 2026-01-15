package io.dataease.api.permissions.role.api;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.permissions.role.dto.*;
import io.dataease.api.permissions.role.vo.ExternalUserVO;
import io.dataease.api.permissions.role.vo.RoleDetailVO;
import io.dataease.api.permissions.role.vo.RoleVO;
import io.dataease.auth.DeApiPath;
import io.dataease.auth.DePermit;
import io.dataease.model.KeywordRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static io.dataease.constant.AuthResourceEnum.ROLE;

/**
 * 角色管理 API 接口
 *
 * <p>提供角色的完整生命周期管理功能，包括角色的创建、编辑、删除、查询等操作，
 * 以及角色与用户之间的绑定关系管理。支持组织内用户和组织外用户的角色绑定。</p>
 *
 * <p>主要功能模块：</p>
 * <ul>
 *   <li>角色基本操作：创建、编辑、删除、查询角色信息</li>
 *   <li>用户绑定管理：绑定/解绑组织内用户，绑定组织外用户</li>
 *   <li>角色查询：支持关键字搜索、按组织查询、查询用户可选/已选角色</li>
 *   <li>角色复制：支持复制已有角色及其权限配置</li>
 * </ul>
 *
 * <p>权限控制：所有接口均通过 @DePermit 注解进行权限验证，确保操作安全性</p>
 *
 * @author fit2cloud-someone
 * @since 1.0
 */
@Tag(name = "角色")
@ApiSupport(order = 887, author = "fit2cloud-someone")
@DeApiPath(value = "/role", rt = ROLE)
public interface RoleApi {

    /**
     * 查询角色列表
     *
     * <p>根据关键字查询角色信息，支持模糊搜索角色名称。
     * 返回当前用户有权限查看的角色列表。</p>
     *
     * @param request 查询请求对象，包含关键字等查询条件
     * @return 角色列表，包含角色基本信息
     */
    @Operation(summary = "查询")
    @DePermit("m:read")
    @PostMapping("/query")
    List<RoleVO> query(@RequestBody KeywordRequest request);

    /**
     * 创建角色
     *
     * <p>创建新的角色，需要指定角色名称和类型。
     * 创建成功后返回新角色的 ID。</p>
     *
     * @param creator 角色创建信息，包含角色名称、类型、描述等
     * @return 新创建角色的 ID
     */
    @Operation(summary = "创建")
    @DePermit("m:read")
    @PostMapping("/create")
    Long create(@RequestBody RoleCreator creator);

    /**
     * 编辑角色
     *
     * <p>更新角色的基本信息，如名称和描述。
     * 需要对该角色有管理权限。</p>
     *
     * @param editor 角色编辑信息，包含角色 ID 和需要更新的字段
     */
    @Operation(summary = "编辑")
    @DePermit({"m:read", "#p0.id + ':manage'"})
    @PostMapping("/edit")
    void edit(@RequestBody RoleEditor editor);

    /**
     * 绑定组织内用户到角色
     *
     * <p>将一个或多个组织内的用户绑定到指定角色，
     * 使这些用户获得该角色的权限。需要对该角色有管理权限。</p>
     *
     * @param request 用户绑定请求，包含角色 ID 和用户 ID 列表
     */
    @Operation(summary = "绑定用户")
    @DePermit({"m:read", "#p0.rid + ':manage'"})
    @PostMapping("/mountUser")
    void mountUser(@RequestBody MountUserRequest request);

    /**
     * 绑定组织外用户到角色
     *
     * <p>将组织外的用户绑定到指定角色。组织外用户指的是
     * 不属于当前组织但需要访问组织资源的用户。需要对该角色有管理权限。</p>
     *
     * @param request 外部用户绑定请求，包含角色 ID 和外部用户 ID
     */
    @Operation(summary = "绑定组织外用户")
    @DePermit({"m:read", "#p0.rid + ':manage'"})
    @PostMapping("/mountExternalUser")
    void mountExternalUser(@RequestBody MountExternalUserRequest request);

    /**
     * 查询组织外用户
     *
     * <p>根据关键字搜索组织外的用户，用于在绑定组织外用户时
     * 进行用户选择。支持按账号、姓名、邮箱等信息进行模糊搜索。</p>
     *
     * @param keyword 搜索关键字，可以是用户名、邮箱等信息
     * @return 匹配的组织外用户信息
     */
    @Operation(summary = "查询组织外用户")
    @GetMapping("/searchExternalUser/{keyword}")
    ExternalUserVO searchExternalUser(@PathVariable("keyword") String keyword);

    /**
     * 解绑用户角色
     *
     * <p>将指定用户从角色中移除，用户将失去该角色的权限。
     * 需要对该角色有管理权限。</p>
     *
     * @param request 用户解绑请求，包含角色 ID 和用户 ID
     */
    @Operation(summary = "解绑用户")
    @DePermit({"m:read", "#p0.rid + ':manage'"})
    @PostMapping("/unMountUser")
    void unMountUser(@RequestBody UnmountUserRequest request);

    /**
     * 查询用户可选角色
     *
     * <p>查询指定用户可以被分配的角色列表。
     * 返回用户尚未拥有的角色，用于在用户管理界面进行角色分配。</p>
     *
     * @param request 角色查询请求，包含用户 ID 等筛选条件
     * @return 用户可选的角色列表
     */
    @Operation(summary = "用户可选角色")
    @PostMapping("/user/option")
    List<RoleVO> optionForUser(@RequestBody RoleRequest request);

    /**
     * 查询用户已选角色
     *
     * <p>查询指定用户已经拥有的角色列表。
     * 用于在用户管理界面显示用户当前的角色分配情况。</p>
     *
     * @param request 角色查询请求，包含用户 ID 等筛选条件
     * @return 用户已拥有的角色列表
     */
    @Operation(summary = "用户已选角色")
    @PostMapping("/user/selected")
    List<RoleVO> selectedForUser(@RequestBody RoleRequest request);

    /**
     * 查询角色详情
     *
     * <p>获取指定角色的详细信息，包括角色名称、类型、描述等完整信息。</p>
     *
     * @param rid 角色 ID
     * @return 角色详细信息
     */
    @Operation(summary = "角色详情")
    @Parameter(name = "rid", description = "角色ID", required = true, in = ParameterIn.PATH)
    @GetMapping("/detail/{rid}")
    RoleDetailVO detail(@PathVariable("rid") Long rid);

    /**
     * 删除角色
     *
     * <p>删除指定的角色，同时会解除该角色与所有用户的绑定关系。
     * 需要对该角色有管理权限。系统内置角色不允许删除。</p>
     *
     * @param rid 角色 ID
     */
    @Operation(summary = "删除角色")
    @Parameter(name = "rid", description = "角色ID", required = true, in = ParameterIn.PATH)
    @DePermit({"m:manage", "#p0 + ':manage'"})
    @PostMapping("/delete/{rid}")
    void delete(@PathVariable("rid") Long rid);

    /**
     * 解绑用户前的确认信息
     *
     * <p>在解绑用户前获取相关信息，用于向用户展示解绑的影响。
     * 例如，该用户在此角色下拥有的资源数量等。</p>
     *
     * @param request 用户解绑请求，包含角色 ID 和用户 ID
     * @return 解绑影响的资源数量或其他统计信息
     */
    @Operation(summary = "解绑用户询问")
    @PostMapping("/beforeUnmountInfo")
    Integer beforeUnmountInfo(@RequestBody UnmountUserRequest request);

    /**
     * 复制角色
     *
     * <p>复制一个已存在的角色，包括其权限配置。
     * 用于快速创建具有相似权限的新角色。此接口为隐藏接口，
     * 仅在特定场景下使用。</p>
     *
     * @param request 角色复制请求，包含源角色 ID 和新角色信息
     */
    @Operation(summary = "复制", hidden = true)
    @PostMapping("/copy")
    void copy(@RequestBody RoleCopyRequest request);

    /**
     * 查询当前组织内的角色
     *
     * <p>查询当前用户所在组织内的所有角色列表，
     * 支持关键字搜索。用于组织管理界面显示组织内的角色信息。</p>
     *
     * @param request 查询请求对象，包含关键字等查询条件
     * @return 当前组织内的角色列表
     */
    @Operation(summary = "查询组织内角色")
    @PostMapping("/byCurOrg")
    List<RoleVO> byCurOrg(@RequestBody KeywordRequest request);

    /**
     * 根据组织 ID 查询角色
     *
     * <p>查询指定组织的角色列表。此接口为内部接口，
     * 不对外暴露，用于系统内部的角色查询。</p>
     *
     * @param oid 组织 ID
     * @return 指定组织的角色列表
     */
    @Hidden
    @GetMapping("/queryWithOid/{oid}")
    List<RoleVO> queryWithOid(@PathVariable("oid") Long oid);
}
