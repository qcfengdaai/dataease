package io.dataease.api.permissions.auth.dto;

import io.dataease.constant.AuthEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 业务权限检查参数对象
 *
 * <p>用于权限校验接口的参数传递，封装了需要校验的资源 ID 和所需的权限级别。
 * 该对象主要用于内部服务间的权限验证调用。</p>
 *
 * <p><strong>权限检查流程：</strong></p>
 * <ol>
 *   <li>业务模块构造 BusiPerCheckDTO 对象</li>
 *   <li>调用权限服务的 checkAuth 接口</li>
 *   <li>权限服务验证当前用户是否具有所需权限</li>
 *   <li>无权限时抛出权限异常，有权限则正常返回</li>
 * </ol>
 *
 * <p><strong>权限级别（AuthEnum）：</strong></p>
 * <ul>
 *   <li>USE: 使用/读取权限</li>
 *   <li>EDIT: 编辑权限</li>
 *   <li>MANAGE: 管理权限</li>
 *   <li>GRANT: 授权权限</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * // 检查用户是否有编辑数据集的权限
 * BusiPerCheckDTO checkDTO = new BusiPerCheckDTO(datasetId, AuthEnum.EDIT);
 * authApi.checkAuth(checkDTO);  // 无权限时抛出异常
 * </pre>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "业务权限检查参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusiPerCheckDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6047004531129863548L;

    /**
     * 资源 ID
     * <p>要检查权限的资源的唯一标识符，如数据集 ID、仪表板 ID 等</p>
     */
    @Schema(description = "资源ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 所需权限级别
     * <p>业务操作所需的权限级别枚举：</p>
     * <ul>
     *   <li>USE: 查看/使用权限 - 用于读取操作</li>
     *   <li>EDIT: 编辑权限 - 用于修改操作</li>
     *   <li>MANAGE: 管理权限 - 用于删除、重命名等操作</li>
     *   <li>GRANT: 授权权限 - 用于权限管理操作</li>
     * </ul>
     *
     * <p>权限检查时会验证用户的权限权重是否满足所需级别，
     * 高级别权限包含低级别权限的所有操作。</p>
     */
    @Schema(description = "所需权限级别", requiredMode = Schema.RequiredMode.REQUIRED)
    private AuthEnum authEnum;
}
