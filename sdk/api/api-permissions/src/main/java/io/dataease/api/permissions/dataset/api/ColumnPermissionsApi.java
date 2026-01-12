package io.dataease.api.permissions.dataset.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dataease.api.permissions.dataset.dto.DataSetColumnPermissionsDTO;
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
 * 数据集列权限管理接口
 *
 * <p>提供数据集的列级别权限控制功能，支持按组织、角色、用户等维度控制数据列的可见性。
 * 通过配置列权限，可以实现敏感字段的脱敏、隐藏特定列、设置可见列白名单等功能。</p>
 *
 * <p>列权限的核心功能：
 * <ul>
 *   <li>授权目标：可以对组织(dept)、角色(role)、用户(user)设置列权限</li>
 *   <li>可见列配置：指定授权对象可以查看哪些数据列</li>
 *   <li>隐藏列配置：指定授权对象不能查看哪些数据列</li>
 *   <li>白名单机制：为特定用户设置例外的列访问权限</li>
 * </ul>
 * </p>
 *
 * <p>应用场景：
 * <ul>
 *   <li>敏感数据保护：隐藏身份证号、手机号、银行卡等敏感字段</li>
 *   <li>部门数据隔离：不同部门只能看到与自己相关的业务字段</li>
 *   <li>角色权限控制：普通员工和管理者看到的数据字段不同</li>
 *   <li>个人隐私保护：限制其他用户查看个人隐私信息</li>
 * </ul>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Hidden
@Tag(name = "列权限")
@DeApiPath(value = "/dataset/columnPermissions", rt = DATASET)
public interface ColumnPermissionsApi {

    /**
     * 分页查询数据集的列权限配置列表
     *
     * <p>根据数据集ID查询该数据集配置的所有列权限规则，支持分页查询。
     * 返回结果包含授权对象信息、可见列配置、白名单配置等完整信息。</p>
     *
     * @param datasetId 数据集ID
     * @param goPage 页码，从1开始
     * @param pageSize 每页记录数
     * @return 列权限配置的分页列表
     */
    @Operation(summary = "查询列权限列表")
    @GetMapping("/pager/{datasetId}/{goPage}/{pageSize}")
    public IPage<DataSetColumnPermissionsDTO> columnPermissions(@PathVariable("datasetId") Long datasetId, @PathVariable("goPage") int goPage, @PathVariable("pageSize") int pageSize);

    /**
     * 保存或更新列权限配置
     *
     * <p>创建新的列权限规则或更新已存在的规则。包含以下配置内容：
     * <ul>
     *   <li>授权目标：选择要授权的组织/角色/用户</li>
     *   <li>可见列：指定授权对象可以查看的数据列</li>
     *   <li>白名单：配置例外的用户列表</li>
     *   <li>启用状态：控制规则是否生效</li>
     * </ul>
     * </p>
     *
     * @param dataSetColumnPermissionsDTO 列权限配置信息
     */
    @Operation(summary = "保存")
    @PostMapping("save")
    public void save(@RequestBody DataSetColumnPermissionsDTO dataSetColumnPermissionsDTO);

    /**
     * 删除列权限配置
     *
     * <p>删除指定的列权限规则。删除后，相关授权对象将不再受该规则限制，
     * 默认可以查看数据集的所有列（如果没有其他权限限制）。</p>
     *
     * @param dataSetColumnPermissionsDTO 包含要删除的列权限ID的对象
     */
    @Operation(summary = "删除")
    @PostMapping("/delete")
    public void delete(@RequestBody DataSetColumnPermissionsDTO dataSetColumnPermissionsDTO);

    /**
     * 获取列权限配置的详细信息
     *
     * <p>根据列权限ID查询完整的配置信息，包括可见列详情、白名单用户列表等。
     * 用于编辑列权限时回显数据。</p>
     *
     * @param request 包含列权限ID的请求对象
     * @return 列权限的完整配置信息
     */
    @Operation(summary = "获取详细信息")
    @PostMapping("/info")
    public DataSetColumnPermissionsDTO DataSetColumnPermissionInfo(@RequestBody DataSetColumnPermissionsDTO request);

    /**
     * 查询列权限配置列表
     *
     * <p>根据条件查询列权限配置列表，支持按数据集ID、授权类型等条件过滤。
     * 内部方法，用于权限验证和数据查询。</p>
     *
     * @param request 查询条件，包含数据集ID、授权类型等
     * @return 符合条件的列权限配置列表
     */
    public List<DataSetColumnPermissionsDTO> list(@RequestBody DataSetColumnPermissionsDTO request);

}
