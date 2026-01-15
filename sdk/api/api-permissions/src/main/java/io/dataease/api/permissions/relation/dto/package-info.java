/**
 * 关系管理数据传输对象包
 *
 * <p>本包包含关系管理模块的所有 DTO (Data Transfer Object) 类，
 * 用于封装关系查询和权限校验的请求参数。DTO 对象通常用于接口的输入参数验证和数据传输。</p>
 *
 * <h2>DTO 列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.relation.dto.RelationDTO} - 关系数据传输对象</li>
 *   <li>{@link io.dataease.api.permissions.relation.dto.RelationListDTO} - 关系列表数据传输对象</li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 DTO 类实现 {@link java.io.Serializable} 接口，支持序列化</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化 getter/setter</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>字段命名遵循驼峰命名规范</li>
 *   <li>必填字段使用 JSR-303 注解进行参数校验</li>
 * </ul>
 *
 * <h2>DTO 详细说明</h2>
 *
 * <h3>1. RelationDTO - 关系数据传输对象</h3>
 * <p>用于表示单个资源与组织的关联关系：</p>
 * <pre>
 * {
 *   "resourceId": 1000,          // 资源 ID（必填）
 *   "resourceType": "datasource", // 资源类型（必填）
 *   "orgId": 100                 // 组织 ID（必填）
 * }
 * </pre>
 * <p><strong>支持的资源类型</strong>：</p>
 * <ul>
 *   <li>{@code datasource}: 数据源</li>
 *   <li>{@code dataset}: 数据集</li>
 *   <li>{@code dashboard}: 仪表板（如果支持）</li>
 *   <li>{@code report}: 报表（如果支持）</li>
 * </ul>
 *
 * <h3>2. RelationListDTO - 关系列表数据传输对象</h3>
 * <p>用于批量查询或设置资源与组织的关联关系：</p>
 * <pre>
 * {
 *   "resourceIds": [1000, 1001, 1002],  // 资源 ID 列表（必填）
 *   "resourceType": "datasource",       // 资源类型（必填）
 *   "orgIds": [100, 101]                // 组织 ID 列表（可选，用于批量设置）
 * }
 * </pre>
 * <p><strong>使用场景</strong>：</p>
 * <ul>
 *   <li>批量查询多个资源的组织关系</li>
 *   <li>批量为多个资源分配组织</li>
 *   <li>批量修改资源的组织归属</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <p>DTO 对象主要用于：</p>
 * <ol>
 *   <li>封装关系查询的请求参数</li>
 *   <li>批量操作时传递多个资源或组织 ID</li>
 *   <li>在控制器层进行参数验证</li>
 *   <li>在 Feign 远程调用时传递参数（企业版）</li>
 * </ol>
 *
 * <h2>典型使用示例</h2>
 *
 * <h3>示例 1: 单个资源关系设置</h3>
 * <pre>{@code
 * // 前端调用示例（管理员设置数据源归属组织）
 * POST /api/relation/setResourceOrg
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *   Content-Type: application/json
 *
 * Request Body:
 * {
 *   "resourceId": 1000,
 *   "resourceType": "datasource",
 *   "orgId": 100
 * }
 *
 * // 后端处理
 * @PostMapping("/setResourceOrg")
 * public void setResourceOrg(@RequestBody @Valid RelationDTO dto) {
 *     relationService.setResourceOrg(
 *         dto.getResourceId(),
 *         dto.getResourceType(),
 *         dto.getOrgId()
 *     );
 * }
 * }</pre>
 *
 * <h3>示例 2: 批量查询资源关系</h3>
 * <pre>{@code
 * // 前端调用示例
 * POST /api/relation/batchGetOrgIds
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *   Content-Type: application/json
 *
 * Request Body:
 * {
 *   "resourceIds": [1000, 1001, 1002],
 *   "resourceType": "datasource"
 * }
 *
 * Response:
 * {
 *   "1000": 100,  // 数据源 1000 归属组织 100
 *   "1001": 100,  // 数据源 1001 归属组织 100
 *   "1002": 101   // 数据源 1002 归属组织 101
 * }
 *
 * // 后端处理
 * @PostMapping("/batchGetOrgIds")
 * public Map<Long, Long> batchGetOrgIds(@RequestBody @Valid RelationListDTO dto) {
 *     return relationService.batchGetResourceOrgs(
 *         dto.getResourceIds(),
 *         dto.getResourceType()
 *     );
 * }
 * }</pre>
 *
 * <h3>示例 3: 批量设置资源关系</h3>
 * <pre>{@code
 * // 批量将多个数据源分配到同一个组织
 * POST /api/relation/batchSetResourceOrg
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *   Content-Type: application/json
 *
 * Request Body:
 * {
 *   "resourceIds": [1000, 1001, 1002],
 *   "resourceType": "datasource",
 *   "orgIds": [100]  // 目标组织
 * }
 *
 * // 后端处理
 * @PostMapping("/batchSetResourceOrg")
 * public void batchSetResourceOrg(@RequestBody @Valid RelationListDTO dto) {
 *     Long targetOrgId = dto.getOrgIds().get(0);
 *     for (Long resourceId : dto.getResourceIds()) {
 *         relationService.setResourceOrg(
 *             resourceId,
 *             dto.getResourceType(),
 *             targetOrgId
 *         );
 *     }
 * }
 * }</pre>
 *
 * <h2>参数校验</h2>
 * <p>DTO 类通常配合 Spring Validation 进行参数校验：</p>
 * <ul>
 *   <li>{@code @NotNull}: 字段不能为 null</li>
 *   <li>{@code @NotBlank}: 字符串不能为空（去除空格后长度必须大于 0）</li>
 *   <li>{@code @NotEmpty}: 集合不能为空（size 必须大于 0）</li>
 *   <li>{@code @Size(min, max)}: 集合或字符串的大小限制</li>
 *   <li>{@code @Pattern(regexp)}: 字符串格式验证</li>
 * </ul>
 *
 * <h3>校验示例</h3>
 * <pre>{@code
 * @Data
 * @Schema(description = "关系数据传输对象")
 * public class RelationDTO implements Serializable {
 *
 *     @NotNull(message = "资源 ID 不能为空")
 *     @Schema(description = "资源 ID", required = true)
 *     private Long resourceId;
 *
 *     @NotBlank(message = "资源类型不能为空")
 *     @Pattern(regexp = "^(datasource|dataset|dashboard|report)$",
 *              message = "资源类型必须为: datasource, dataset, dashboard, report")
 *     @Schema(description = "资源类型", required = true)
 *     private String resourceType;
 *
 *     @NotNull(message = "组织 ID 不能为空")
 *     @Schema(description = "组织 ID", required = true)
 *     private Long orgId;
 * }
 * }</pre>
 *
 * <h2>性能优化建议</h2>
 * <ol>
 *   <li><strong>批量操作</strong>：使用 RelationListDTO 进行批量查询和设置，避免多次网络请求</li>
 *   <li><strong>参数限制</strong>：对批量操作的数量进行限制（如最多 100 个资源）</li>
 *   <li><strong>异步处理</strong>：大批量操作考虑异步处理，返回任务 ID 供前端轮询</li>
 * </ol>
 *
 * <h2>安全注意事项</h2>
 * <ul>
 *   <li>验证资源 ID 是否存在，避免非法 ID</li>
 *   <li>验证组织 ID 是否存在，避免非法 ID</li>
 *   <li>检查当前用户是否有权限修改资源关系</li>
 *   <li>记录关系变更的审计日志</li>
 * </ul>
 *
 * <h2>与 VO 的区别</h2>
 * <ul>
 *   <li><strong>DTO</strong>: 用于接收请求参数，数据流向是<strong>前端 → 后端</strong></li>
 *   <li><strong>VO</strong>: 用于返回响应数据，数据流向是<strong>后端 → 前端</strong></li>
 *   <li>DTO 注重参数校验和业务逻辑传递</li>
 *   <li>关系模块通常不需要 VO，因为返回的数据结构简单（如 Map、List 等）</li>
 * </ul>
 *
 * <h2>扩展建议</h2>
 * <p>新增 DTO 类时应遵循以下规范：</p>
 * <ol>
 *   <li>类名以业务含义命名，后缀为 DTO</li>
 *   <li>所有字段添加 {@code @Schema} 注解说明字段含义</li>
 *   <li>必填字段添加校验注解</li>
 *   <li>枚举类型的字段使用 {@code @Pattern} 或自定义校验器验证</li>
 *   <li>提供清晰的类注释，说明使用场景和字段含义</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.relation.api
 * @since 1.0
 */
package io.dataease.api.permissions.relation.dto;
