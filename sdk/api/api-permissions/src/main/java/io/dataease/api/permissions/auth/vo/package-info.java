/**
 * 权限管理视图对象包
 *
 * <p>本包包含权限管理模块的所有 VO (View Object) 类，
 * 用于封装返回给客户端的权限数据。</p>
 *
 * <h2>VO 分类</h2>
 *
 * <h3>1. 资源视图类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.auth.vo.ResourceVO} - 资源节点视图对象（完整版）</li>
 *   <li>{@link io.dataease.api.permissions.auth.vo.ResourceNodeVO} - 资源节点视图对象（简化版）</li>
 * </ul>
 *
 * <h3>2. 权限视图类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.auth.vo.PermissionVO} - 权限视图对象（完整权限信息）</li>
 *   <li>{@link io.dataease.api.permissions.auth.vo.PermissionItem} - 权限项（单个资源的权限）</li>
 *   <li>{@link io.dataease.api.permissions.auth.vo.PermissionValVO} - 权限值视图对象（仅数值信息）</li>
 *   <li>{@link io.dataease.api.permissions.auth.vo.PermissionOrigin} - 权限来源视图对象（继承权限详情）</li>
 * </ul>
 *
 * <h2>设计特点</h2>
 *
 * <h3>1. ResourceVO vs ResourceNodeVO</h3>
 * <ul>
 *   <li><strong>ResourceVO</strong>：完整的资源树节点，包含子节点列表和详细权限配置</li>
 *   <li><strong>ResourceNodeVO</strong>：简化的资源节点，仅用于路径展示和面包屑导航</li>
 * </ul>
 *
 * <h3>2. PermissionVO 体系</h3>
 * <ul>
 *   <li><strong>PermissionVO</strong>：顶层权限对象，包含资源信息和权限映射</li>
 *   <li><strong>PermissionItem</strong>：单个资源的权限配置，包含权重和行列权限</li>
 *   <li><strong>PermissionValVO</strong>：仅包含权限数值，用于简单权限判断</li>
 *   <li><strong>PermissionOrigin</strong>：继承权限的来源信息，用于权限追踪</li>
 * </ul>
 *
 * <h2>数据结构示例</h2>
 *
 * <h3>ResourceVO - 资源树结构</h3>
 * <pre>{@code
 * {
 *   "resourceId": 1,
 *   "resourceName": "数据管理",
 *   "pid": 0,
 *   "resourceType": "folder",
 *   "children": [
 *     {
 *       "resourceId": 2,
 *       "resourceName": "销售数据集",
 *       "pid": 1,
 *       "resourceType": "dataset",
 *       "children": []
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <h3>PermissionVO - 权限信息</h3>
 * <pre>{@code
 * {
 *   "resourceId": 2,
 *   "resourceName": "销售数据集",
 *   "pid": 1,
 *   "permissions": {
 *     "1": {  // 菜单 ID
 *       "authType": 0,     // 使用权限
 *       "weight": 100,     // 权重
 *       "extraFlag": true, // 附加权限
 *       "columnPermissions": [],  // 列权限
 *       "rowPermissions": []      // 行权限
 *     }
 *   }
 * }
 * }</pre>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 VO 类实现 {@link java.io.Serializable} 接口</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化代码</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>Long 类型的 ID 使用 {@code @JsonSerialize(using=ToStringSerializer.class)} 避免精度丢失</li>
 *   <li>根据使用场景提供不同详细程度的视图对象</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <ol>
 *   <li><strong>资源树展示</strong>：使用 ResourceVO 展示完整的资源树结构</li>
 *   <li><strong>面包屑导航</strong>：使用 ResourceNodeVO 展示资源路径</li>
 *   <li><strong>权限配置页面</strong>：使用 PermissionVO 展示和编辑权限</li>
 *   <li><strong>权限检查</strong>：使用 PermissionValVO 进行快速权限判断</li>
 *   <li><strong>权限追踪</strong>：使用 PermissionOrigin 查看权限来源</li>
 * </ol>
 *
 * <h2>性能优化</h2>
 * <ul>
 *   <li>资源树采用懒加载策略，避免一次性加载大量数据</li>
 *   <li>权限数据进行缓存，减少数据库查询</li>
 *   <li>根据场景选择合适的 VO 对象，避免传输不必要的数据</li>
 * </ul>
 *
 * @see io.dataease.api.permissions.auth.api
 * @see io.dataease.api.permissions.auth.dto
 * @since 1.0
 */
package io.dataease.api.permissions.auth.vo;
