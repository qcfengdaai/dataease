package io.dataease.api.permissions.role.dto;

import io.dataease.model.KeywordRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;


/**
 * 用户查询请求
 *
 * <p>用于查询角色下用户列表的请求对象，支持关键字搜索和排序。
 * 继承自 KeywordRequest，可以使用关键字对用户名、账号等信息进行模糊搜索。</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>查询角色下的所有用户</li>
 *   <li>在角色管理界面展示该角色的成员列表</li>
 *   <li>搜索角色下的特定用户</li>
 * </ul>
 *
 * <p>查询功能：</p>
 * <ul>
 *   <li>关键字搜索：支持按用户名、账号等信息模糊搜索</li>
 *   <li>排序支持：可以指定查询结果的排序规则</li>
 * </ul>
 */
@Schema(description = "用户过滤器")
@EqualsAndHashCode(callSuper = true)
@Data
public class UserRequest extends KeywordRequest  {

    @Serial
    private static final long serialVersionUID = -2740015284392981297L;

    /**
     * 角色 ID
     * 必填项，指定要查询用户列表的角色
     * 查询结果将返回该角色下的所有用户
     */
    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long rid;

    /**
     * 排序规则
     * 可选项，指定查询结果的排序方式
     * 例如：按用户名排序、按加入时间排序等
     */
    @Schema(description = "排序规则")
    private String order;

}
