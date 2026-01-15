package io.dataease.api.permissions.dataset.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dataease.api.permissions.dataset.dto.*;
import io.dataease.api.permissions.user.vo.UserFormVO;
import io.dataease.auth.DeApiPath;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static io.dataease.constant.AuthResourceEnum.DATASET;

/**
 * 数据集行权限管理接口
 *
 * <p>提供数据集的行级数据权限控制功能，支持按组织、角色、用户等维度进行数据行级别的访问控制。
 * 通过配置过滤表达式树，可以实现复杂的数据行权限规则，如只允许查看特定部门的数据、
 * 只能查看指定区域的数据等。</p>
 *
 * <p>行权限的核心概念：
 * <ul>
 *   <li>授权目标：可以对组织(dept)、角色(role)、用户(user)设置行权限</li>
 *   <li>过滤规则：通过表达式树定义数据过滤条件（如：部门='销售部' AND 区域='华东'）</li>
 *   <li>白名单机制：支持为特定用户/角色/组织设置例外访问权限</li>
 *   <li>优先级：白名单 > 行权限规则 > 默认全量数据</li>
 * </ul>
 * </p>
 *
 * <p>应用场景：
 * <ul>
 *   <li>多租户数据隔离：不同客户只能看到自己的数据</li>
 *   <li>部门数据隔离：销售人员只能查看自己部门的销售数据</li>
 *   <li>区域数据权限：区域经理只能查看负责区域的数据</li>
 *   <li>个人数据权限：员工只能查看与自己相关的数据</li>
 * </ul>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Hidden
@Tag(name = "行权限")
@DeApiPath(value = "/dataset/rowPermissions", rt = DATASET)
public interface RowPermissionsApi {

    /**
     * 分页查询数据集的行权限配置列表
     *
     * <p>根据数据集ID查询该数据集配置的所有行权限规则，支持分页查询。
     * 返回结果包含授权对象信息、过滤规则、白名单配置等完整信息。</p>
     *
     * @param datasetId 数据集ID
     * @param goPage 页码，从1开始
     * @param pageSize 每页记录数
     * @return 行权限配置的分页列表
     */
    @Operation(summary = "查询行权限列表")
    @GetMapping("/pager/{datasetId}/{goPage}/{pageSize}")
    public IPage<DataSetRowPermissionsTreeDTO> rowPermissions(@PathVariable("datasetId") Long datasetId, @PathVariable("goPage") int goPage, @PathVariable("pageSize") int pageSize);

    /**
     * 保存或更新行权限配置
     *
     * <p>创建新的行权限规则或更新已存在的规则。包含以下配置内容：
     * <ul>
     *   <li>授权目标：选择要授权的组织/角色/用户</li>
     *   <li>过滤规则：通过表达式树定义数据过滤条件</li>
     *   <li>白名单：配置例外的用户/角色/组织</li>
     *   <li>启用状态：控制规则是否生效</li>
     * </ul>
     * </p>
     *
     * @param datasetRowPermissions 行权限配置信息
     */
    @Operation(summary = "保存")
    @PostMapping("save")
    public void save(@RequestBody DataSetRowPermissionsTreeDTO datasetRowPermissions);

    /**
     * 删除行权限配置
     *
     * <p>删除指定的行权限规则。删除后，相关授权对象将不再受该规则限制，
     * 默认可以访问数据集的全量数据（如果没有其他权限限制）。</p>
     *
     * @param datasetRowPermissions 包含要删除的行权限ID的对象
     */
    @Operation(summary = "删除")
    @PostMapping("/delete")
    public void delete(@RequestBody DataSetRowPermissionsTreeDTO datasetRowPermissions);

    /**
     * 查询可授权的对象列表
     *
     * <p>根据授权类型查询可选的授权对象。例如：
     * <ul>
     *   <li>type=dept：返回所有组织列表</li>
     *   <li>type=role：返回所有角色列表</li>
     *   <li>type=user：返回所有用户列表</li>
     * </ul>
     * 用于在配置行权限时选择授权目标。</p>
     *
     * @param datasetId 数据集ID
     * @param type 授权类型：dept(组织)、role(角色)、user(用户)
     * @return 可授权对象列表
     */
    @Operation(summary = "授权对象")
    @GetMapping("/authObjs/{datasetId}/{type}")
    public List<Item> authObjs(@PathVariable("datasetId") Long datasetId, @PathVariable("type") String type);

    /**
     * 获取行权限配置的详细信息
     *
     * <p>根据行权限ID查询完整的配置信息，包括表达式树结构、白名单详情等。
     * 用于编辑行权限时回显数据。</p>
     *
     * @param request 包含行权限ID的请求对象
     * @return 行权限的完整配置信息
     */
    @Operation(summary = "获取详细信息")
    @PostMapping("/dataSetRowPermissionInfo")
    public DataSetRowPermissionsTreeDTO dataSetRowPermissionInfo(@RequestBody DataSetRowPermissionsTreeDTO request);

    /**
     * 查询白名单用户列表
     *
     * <p>根据授权目标类型和ID，查询在该行权限规则中配置的白名单用户列表。
     * 白名单用户不受行权限规则限制，可以访问全量数据。</p>
     *
     * @param request 包含授权目标类型、ID和数据集ID的请求对象
     * @return 白名单用户列表
     */
    @Operation(summary = "白名单")
    @PostMapping("/whiteListUsers")
    public List<UserFormVO> whiteListUsers(@RequestBody WhiteListUsersRequest request);

    /**
     * 根据用户ID获取用户信息
     *
     * <p>内部方法，用于获取指定用户的基本信息。</p>
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public UserFormVO getUserById(Long id);

    /**
     * 查询行权限配置列表
     *
     * <p>根据条件查询行权限配置列表，支持按数据集ID、授权类型等条件过滤。
     * 内部方法，用于权限验证和数据查询。</p>
     *
     * @param dataSetRowPermissionsTreeDTO 查询条件，包含数据集ID、授权类型等
     * @return 符合条件的行权限配置列表
     */
    public List<DataSetRowPermissionsTreeDTO> list(DatasetRowPermissionsTreeRequest dataSetRowPermissionsTreeDTO) ;
}
