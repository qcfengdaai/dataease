package io.dataease.api.permissions.dataset.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.dataease.api.permissions.user.vo.UserFormVO;
import lombok.Data;

import java.util.List;

/**
 * 数据集列权限数据传输对象
 *
 * <p>封装数据集列级别权限的完整配置信息，包括授权对象、可见列、白名单等。
 * 用于在前后端之间传输列权限配置数据。</p>
 *
 * <p>列权限配置说明：
 * <ul>
 *   <li>授权目标：支持对组织(dept)、角色(role)、用户(user)三种类型进行授权</li>
 *   <li>可见列控制：通过 permissions 字段配置允许查看的数据列</li>
 *   <li>白名单：为特定用户设置例外权限，不受列权限规则限制</li>
 *   <li>启用状态：支持临时禁用规则而不删除配置</li>
 * </ul>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Data
public class DataSetColumnPermissionsDTO   {
    /**
     * 列权限规则ID
     *
     * <p>列权限配置的唯一标识，新建时为空，更新时必填</p>
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 是否启用
     *
     * <p>控制该列权限规则是否生效：
     * <ul>
     *   <li>true: 规则生效，按配置限制列的可见性</li>
     *   <li>false: 规则禁用，授权对象可以查看所有列</li>
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
     * <p>该列权限规则所属的数据集唯一标识</p>
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long datasetId;

    /**
     * 列权限配置
     *
     * <p>以 JSON 格式存储的可见列配置，包含允许查看的数据列ID列表。
     * 示例：["col1", "col2", "col3"]，表示只能查看这三列数据</p>
     */
    private String permissions;

    /**
     * 白名单用户ID列表
     *
     * <p>以逗号分隔的用户ID字符串，白名单中的用户不受该列权限规则限制，
     * 可以查看数据集的所有列。用于为特定用户设置例外权限。</p>
     */
    private String whiteListUser;

    /**
     * 更新时间
     *
     * <p>该列权限配置最后一次更新的时间戳（毫秒）</p>
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
     * 授权目标ID列表
     *
     * <p>批量操作时使用，可以一次为多个授权对象设置相同的列权限规则</p>
     */
    private List<Long> authTargetIds;

    /**
     * 白名单用户详细信息列表
     *
     * <p>白名单中所有用户的完整信息，包含用户名、邮箱等，用于界面展示和编辑</p>
     */
    private List<UserFormVO> whiteListUsers;
}
