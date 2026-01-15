package io.dataease.api.permissions.user.dto;

import io.dataease.model.KeywordRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 用户列表查询请求
 *
 * <p>用于用户列表分页查询的请求对象，继承自 KeywordRequest，
 * 支持多维度的筛选条件和排序选项。</p>
 *
 * <p>查询功能：</p>
 * <ul>
 *   <li>关键字搜索：支持按用户名、账号等信息模糊搜索</li>
 *   <li>状态筛选：可以筛选启用或禁用的用户</li>
 *   <li>来源筛选：可以筛选本地用户或第三方平台用户</li>
 *   <li>角色筛选：可以筛选拥有特定角色的用户</li>
 *   <li>时间排序：支持按创建时间升序或降序排列</li>
 * </ul>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserGridRequest extends KeywordRequest implements Serializable {

    /**
     * 用户状态列表
     * 筛选条件，可以同时筛选多个状态
     * true：启用状态的用户
     * false：禁用状态的用户
     */
    private List<Boolean> statusList;

    /**
     * 用户来源列表
     * 筛选条件，可以同时筛选多个来源
     * 不同的数字代表不同的用户来源：
     * - 0：本地用户
     * - 1：LDAP 用户
     * - 2：OAuth 用户
     * 等（具体值由系统定义）
     */
    private List<Integer> originList;

    /**
     * 角色 ID 列表
     * 筛选条件，可以筛选拥有指定角色的用户
     * 支持同时筛选多个角色
     */
    private List<Long> roleIdList;

    /**
     * 时间降序排列
     * 排序选项，指定查询结果的排序方式
     * true：按创建时间降序（最新的在前）
     * false 或 null：按创建时间升序或默认排序
     */
    private Boolean timeDesc;
}
