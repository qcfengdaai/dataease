package io.dataease.api.permissions.role.dto;

import io.dataease.model.KeywordRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 角色查询请求
 *
 * <p>用于查询角色列表的请求对象，支持关键字搜索和按用户筛选。
 * 继承自 KeywordRequest，可以使用关键字对角色名称进行模糊搜索。</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>查询用户可选的角色列表（通过 uid 筛选）</li>
 *   <li>查询用户已拥有的角色列表</li>
 *   <li>通用的角色搜索功能</li>
 * </ul>
 */
@Schema(description = "角色过滤器")
@EqualsAndHashCode(callSuper = true)
@Data
public class RoleRequest extends KeywordRequest {


    @Serial
    private static final long serialVersionUID = 7354856549096378406L;

    /**
     * 用户 ID
     * 可选项，当指定用户 ID 时，查询结果将根据该用户的角色关系进行过滤
     * 例如：查询该用户可选的角色或已拥有的角色
     */
    @Schema(description = "用户ID")
    private Long uid;
}
