package io.dataease.api.permissions.org.dto;

import io.dataease.model.KeywordRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;


/**
 * 组织查询请求
 * <p>
 * 用于查询组织树的请求对象，继承自 {@link KeywordRequest}，支持按关键词搜索。
 * 此请求用于全量加载组织树场景。
 * <p>
 * 查询特性：
 * <ul>
 *   <li>支持按组织名称进行模糊搜索（通过继承的 keyword 字段）</li>
 *   <li>支持按创建时间排序（默认降序）</li>
 *   <li>一次性返回完整的组织树结构</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 * @see KeywordRequest
 */
@Schema(description = "组织列表过滤器")
@EqualsAndHashCode(callSuper = true)
@Data
public class OrgRequest extends KeywordRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1697526057837588192L;

    /**
     * 是否降序排列
     * <p>
     * 控制组织列表的排序方向：
     * <ul>
     *   <li>true：按创建时间降序（最新创建的在前）</li>
     *   <li>false：按创建时间升序（最早创建的在前）</li>
     * </ul>
     * <p>
     * 默认值为 true，即默认显示最新创建的组织。
     */
    @Schema(description = "是否降序", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean desc = true;
}
