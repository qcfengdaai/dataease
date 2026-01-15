package io.dataease.api.permissions.dataset.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.dataease.api.permissions.role.vo.RoleVO;
import io.dataease.api.permissions.user.vo.UserFormVO;
import io.dataease.extensions.view.dto.DatasetRowPermissionsTreeObj;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 数据集行权限树形数据传输对象
 *
 * <p>封装数据集行级别权限的完整配置信息，包括授权对象、过滤表达式树、多层级白名单等。
 * 用于在前后端之间传输行权限配置数据。</p>
 *
 * <p>行权限配置说明：
 * <ul>
 *   <li>授权目标：支持对组织(dept)、角色(role)、用户(user)三种类型进行授权</li>
 *   <li>过滤规则：通过表达式树（expressionTree）定义复杂的数据过滤条件</li>
 *   <li>多层级白名单：支持用户、角色、组织三个层级的白名单配置</li>
 *   <li>数据导出控制：可以单独控制是否允许导出数据</li>
 * </ul>
 * </p>
 *
 * <p>表达式树结构示例：
 * <pre>
 * {
 *   "logic": "AND",
 *   "conditions": [
 *     {"field": "部门", "operator": "=", "value": "销售部"},
 *     {"field": "区域", "operator": "in", "value": ["华东", "华南"]}
 *   ]
 * }
 * </pre>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Getter
@Setter
public class DataSetRowPermissionsTreeDTO  {

    /**
     * 行权限规则ID
     *
     * <p>行权限配置的唯一标识，新建时为空，更新时必填</p>
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 是否启用
     *
     * <p>控制该行权限规则是否生效：
     * <ul>
     *   <li>true: 规则生效，按配置过滤数据行</li>
     *   <li>false: 规则禁用，授权对象可以查看全量数据</li>
     * </ul>
     * </p>
     */
    private Boolean enable;

    /**
     * 授权目标类型
     *
     * <p>指定权限应用的对象类型，可选值：
     * <ul>
     *   <li>dept: 组织/部门，对整个部门的成员生效</li>
     *   <li>role: 角色，对拥有该角色的所有用户生效</li>
     *   <li>user: 用户，仅对指定用户生效</li>
     * </ul>
     * </p>
     */
    private String authTargetType;

    /**
     * 授权目标ID
     *
     * <p>授权对象的唯一标识，根据 authTargetType 不同而含义不同：
     * <ul>
     *   <li>authTargetType=dept 时，表示组织ID</li>
     *   <li>authTargetType=role 时，表示角色ID</li>
     *   <li>authTargetType=user 时，表示用户ID</li>
     * </ul>
     * </p>
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long authTargetId;

    /**
     * 数据集ID
     *
     * <p>该行权限规则所属的数据集唯一标识</p>
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long datasetId;

    /**
     * 过滤表达式树
     *
     * <p>以 JSON 格式存储的过滤条件表达式树，支持复杂的逻辑组合（AND、OR、NOT）
     * 和多种比较操作符（=、!=、>、<、>=、<=、in、not in、like等）。
     * 系统会将表达式树转换为 SQL WHERE 子句来过滤数据。</p>
     */
    private String expressionTree;

    /**
     * 用户白名单
     *
     * <p>以逗号分隔的用户ID字符串，白名单中的用户不受该行权限规则限制，
     * 可以查看数据集的全量数据。优先级最高。</p>
     */
    private String whiteListUser;

    /**
     * 角色白名单
     *
     * <p>以逗号分隔的角色ID字符串，拥有白名单角色的用户不受该行权限规则限制，
     * 可以查看数据集的全量数据。优先级次之。</p>
     */
    private String whiteListRole;

    /**
     * 组织白名单
     *
     * <p>以逗号分隔的组织ID字符串，属于白名单组织的用户不受该行权限规则限制，
     * 可以查看数据集的全量数据。优先级最低。</p>
     */
    private String whiteListDept;

    /**
     * 更新时间
     *
     * <p>该行权限配置最后一次更新的时间戳（毫秒）</p>
     */
    private Long updateTime;

    /**
     * 数据集名称
     *
     * <p>数据集的显示名称，用于界面展示</p>
     */
    private String datasetName;

    /**
     * 授权目标名称
     *
     * <p>授权对象的显示名称，如组织名、角色名、用户名等，用于界面展示</p>
     */
    private String authTargetName;

    /**
     * 表达式树对象
     *
     * <p>将 expressionTree JSON 字符串反序列化后的对象，方便在业务逻辑中操作。
     * 包含完整的过滤条件树结构，用于生成 SQL 或进行条件验证。</p>
     */
    private DatasetRowPermissionsTreeObj tree;

    /**
     * 用户白名单详细信息列表
     *
     * <p>用户白名单中所有用户的完整信息，包含用户名、邮箱等，用于界面展示和编辑</p>
     */
    private List<UserFormVO> whiteListUsers;

    /**
     * 角色白名单详细信息列表
     *
     * <p>角色白名单中所有角色的完整信息，包含角色名、描述等，用于界面展示和编辑</p>
     */
    private List<RoleVO> whiteListRoles;

    /**
     * 授权目标ID列表
     *
     * <p>批量操作时使用，可以一次为多个授权对象设置相同的行权限规则</p>
     */
    private List<Long> authTargetIds;

    /**
     * 是否允许导出数据
     *
     * <p>控制该行权限规则限制的用户是否可以导出数据：
     * <ul>
     *   <li>true: 允许导出（导出时仍受行权限规则限制）</li>
     *   <li>false: 禁止导出数据</li>
     * </ul>
     * 可以用于限制敏感数据的导出权限。
     * </p>
     */
    private  boolean exportData;

}
