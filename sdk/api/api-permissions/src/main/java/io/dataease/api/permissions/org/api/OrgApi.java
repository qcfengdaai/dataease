package io.dataease.api.permissions.org.api;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.permissions.org.dto.OrgCreator;
import io.dataease.api.permissions.org.dto.OrgEditor;
import io.dataease.api.permissions.org.dto.OrgLazyRequest;
import io.dataease.api.permissions.org.dto.OrgRequest;
import io.dataease.api.permissions.org.vo.*;
import io.dataease.auth.DeApiPath;
import io.dataease.auth.DePermit;
import io.dataease.model.KeywordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static io.dataease.constant.AuthResourceEnum.ORG;

/**
 * 组织架构管理 API
 * <p>
 * 提供组织单位的层级结构管理功能，包括：
 * <ul>
 *   <li>组织树的查询和展示（支持全量加载和懒加载两种模式）</li>
 *   <li>组织单位的创建、编辑和删除操作</li>
 *   <li>基于用户权限的组织树查询</li>
 *   <li>组织资源的存在性检查和详情查询</li>
 * </ul>
 * <p>
 * 组织架构采用树形结构，支持多级层次关系。
 * 所有查询操作都会根据用户的权限范围进行过滤。
 *
 * @author fit2cloud
 * @since 1.0
 */
@Tag(name = "组织")
@ApiSupport(order = 886, author = "fit2cloud-someone")
@DeApiPath(value = "/org", rt = ORG)
public interface OrgApi {

    /**
     * 查询组织树（全量加载）
     * <p>
     * 一次性加载完整的组织树结构，适用于组织数量较少的场景。
     * 树形结构通过 children 字段递归构建。
     *
     * @param request 组织查询请求，包含关键词搜索和排序等过滤条件
     * @return 组织树列表，每个节点包含 id、名称、子节点等信息
     */
    @Operation(summary = "查询组织树")
    @PostMapping("/page/tree")
    @DePermit("m:read")
    List<OrgPageVO> pageTree(@RequestBody OrgRequest request);

    /**
     * 懒加载组织树
     * <p>
     * 按需加载组织树节点，适用于组织数量较多的场景。
     * 初次加载根节点，后续根据用户展开操作加载子节点。
     *
     * @param request 懒加载请求，包含父节点 ID 和查询条件
     * @return 懒加载树视图对象，包含当前层级节点列表和需要展开的节点 ID 列表
     */
    @Operation(summary = "懒加载组织树")
    @PostMapping("/page/lazyTree")
    @DePermit("m:read")
    LazyTreeVO lazyPageTree(@RequestBody OrgLazyRequest request);

    /**
     * 创建组织单位
     * <p>
     * 在指定的父组织下创建新的组织单位。
     * 如果父组织 ID 为空或为 0，则创建根级组织。
     *
     * @param creator 组织创建对象，包含组织名称和父组织 ID
     * @return 新创建的组织 ID
     */
    @Operation(summary = "创建")
    @DePermit({"m:read"})
    @PostMapping("/page/create")
    Long create(@RequestBody OrgCreator creator);

    /**
     * 编辑组织单位
     * <p>
     * 更新指定组织的基本信息（如名称）。
     * 需要对该组织具有管理权限。
     *
     * @param editor 组织编辑对象，包含要更新的组织 ID 和新名称
     */
    @Operation(summary = "编辑")
    @DePermit({"m:read", "#p0.id+':manage'"})
    @PostMapping("/page/edit")
    void edit(@RequestBody OrgEditor editor);

    /**
     * 删除组织单位
     * <p>
     * 删除指定的组织单位。
     * 如果组织下有子组织或关联的用户/资源，删除操作可能会失败。
     *
     * @param id 要删除的组织 ID
     */
    @Operation(summary = "删除")
    @Parameter(name = "id", description = "ID", required = true, in = ParameterIn.PATH)
    @PostMapping("/page/delete/{id}")
    @DePermit({"m:read", "#p0+':manage'"})
    void delete(@PathVariable("id") Long id);

    /**
     * 查询权限内的组织树（全量加载）
     * <p>
     * 查询当前用户有权限访问的组织树。
     * 与 pageTree 不同，此方法只返回用户有权限的组织节点。
     *
     * @param request 关键词查询请求，支持按名称模糊搜索
     * @return 用户可见的组织树列表
     */
    @Operation(summary = "查询权限内组织树")
    @PostMapping("/mounted")
    List<MountedVO> mounted(@RequestBody KeywordRequest request);

    /**
     * 查询权限内的组织树（懒加载）
     * <p>
     * 按需加载用户有权限访问的组织树节点。
     * 结合了权限过滤和懒加载两个特性。
     *
     * @param request 懒加载请求，包含父节点 ID 和查询条件
     * @return 懒加载的组织树视图对象，包含可见节点和展开节点列表
     */
    @Operation(summary = "查询权限内组织树(懒加载)")
    @PostMapping("/lazyMounted")
    LazyMountedVO lazyMounted(@RequestBody OrgLazyRequest request);

    /**
     * 检查组织资源是否存在
     * <p>
     * 用于验证指定 ID 的组织是否存在于系统中。
     * 此接口通常用于内部调用，不在 API 文档中显示。
     *
     * @param oid 组织 ID
     * @return true 表示组织存在，false 表示不存在
     */
    @Operation(summary = "", hidden = true)
    @GetMapping("/resourceExist/{oid}")
    boolean resourceExist(@PathVariable("oid") Long oid);

    /**
     * 获取组织详细信息
     * <p>
     * 查询指定组织的详细信息，包括 ID、名称、父组织和根路径等。
     * 此接口通常用于内部调用，不在 API 文档中显示。
     *
     * @param oid 组织 ID
     * @return 组织详细信息对象
     */
    @Operation(hidden = true)
    @GetMapping("/detail/{oid}")
    OrgDetailVO detail(@PathVariable("oid") Long oid);

    /**
     * 获取子组织列表
     * <p>
     * 查询当前用户可见的所有子组织 ID 列表。
     * 此接口通常用于内部调用，不在 API 文档中显示。
     *
     * @return 子组织 ID 列表（字符串形式）
     */
    @Operation(hidden = true)
    @GetMapping("/subOrgs")
    List<String> subOrgs();
}
