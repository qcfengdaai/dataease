package io.dataease.api.permissions.dataset.dto;


import lombok.Data;

/**
 * 白名单用户查询请求对象
 *
 * <p>用于查询指定权限规则中配置的白名单用户列表。
 * 白名单机制允许为特定用户设置例外权限，不受行权限或列权限规则的限制。</p>
 *
 * <p>白名单的应用场景：
 * <ul>
 *   <li>数据管理员：需要查看所有数据而不受行权限限制</li>
 *   <li>特殊权限用户：某些用户需要访问敏感字段而不受列权限限制</li>
 *   <li>临时授权：短期内允许某个用户访问受限数据</li>
 *   <li>审计人员：需要完整的数据访问权限进行审计</li>
 * </ul>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Data
public class WhiteListUsersRequest {
    /**
     * 授权目标ID
     *
     * <p>权限规则中配置的授权对象的ID，可能是组织ID、角色ID或用户ID</p>
     */
    private Long authTargetId;

    /**
     * 授权目标类型
     *
     * <p>权限规则中配置的授权对象类型，可选值：
     * <ul>
     *   <li>dept: 组织/部门</li>
     *   <li>role: 角色</li>
     *   <li>user: 用户</li>
     * </ul>
     * </p>
     */
    private String authTargetType;

    /**
     * 数据集ID
     *
     * <p>要查询白名单的数据集唯一标识</p>
     */
    private Long datasetId;
}
