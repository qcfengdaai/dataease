/**
 * 组织架构管理接口层
 *
 * <p>本包定义了组织架构管理的所有 RESTful 接口，遵循 Spring MVC 规范。
 * 接口使用 OpenAPI 3.0 注解进行文档标注，支持 Knife4j 在线文档。</p>
 *
 * <h2>接口列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.org.api.OrgApi} - 组织架构管理核心接口</li>
 * </ul>
 *
 * <h2>实现类位置</h2>
 * <p>接口实现类位于 core-backend 模块的 controller 包中，通过 Spring 的 {@code @RestController} 注解标注。</p>
 *
 * <h2>访问路径</h2>
 * <p>基础路径：{@code /api/org}</p>
 *
 * <h2>权限控制</h2>
 * <p>组织架构接口采用分级权限控制：</p>
 * <ul>
 *   <li><strong>查询操作</strong>: 需要 {@code m:read} 基础权限</li>
 *   <li><strong>创建操作</strong>: 需要 {@code m:read} 权限</li>
 *   <li><strong>编辑操作</strong>: 需要 {@code m:read} + {@code 组织ID:manage} 权限</li>
 *   <li><strong>删除操作</strong>: 需要 {@code m:read} + {@code 组织ID:manage} 权限</li>
 * </ul>
 *
 * <h2>查询模式</h2>
 * <p>接口提供两种组织树查询模式：</p>
 *
 * <h3>全量加载模式</h3>
 * <ul>
 *   <li>适用场景：组织数量较少（建议 < 500 个节点）</li>
 *   <li>优点：一次性加载完整树结构，前端交互体验好</li>
 *   <li>缺点：组织数量多时加载慢，占用内存大</li>
 *   <li>接口：{@code pageTree()}、{@code mounted()}</li>
 * </ul>
 *
 * <h3>懒加载模式</h3>
 * <ul>
 *   <li>适用场景：组织数量较多（> 500 个节点）</li>
 *   <li>优点：按需加载，减少服务器压力和网络传输</li>
 *   <li>缺点：需要多次请求，前端实现相对复杂</li>
 *   <li>接口：{@code lazyPageTree()}、{@code lazyMounted()}</li>
 * </ul>
 *
 * <h2>接口分类</h2>
 *
 * <h3>管理类接口（需要管理权限）</h3>
 * <ul>
 *   <li>{@code pageTree()} - 查询完整组织树（管理视图）</li>
 *   <li>{@code lazyPageTree()} - 懒加载组织树（管理视图）</li>
 *   <li>{@code create()} - 创建组织单位</li>
 *   <li>{@code edit()} - 编辑组织单位</li>
 *   <li>{@code delete()} - 删除组织单位</li>
 * </ul>
 *
 * <h3>权限类接口（基于用户权限过滤）</h3>
 * <ul>
 *   <li>{@code mounted()} - 查询用户有权限的组织树</li>
 *   <li>{@code lazyMounted()} - 懒加载用户有权限的组织树</li>
 *   <li>{@code subOrgs()} - 获取用户可见的子组织列表</li>
 * </ul>
 *
 * <h3>内部调用接口（hidden）</h3>
 * <ul>
 *   <li>{@code resourceExist()} - 检查组织资源是否存在</li>
 *   <li>{@code detail()} - 获取组织详细信息</li>
 * </ul>
 *
 * <h2>返回数据结构</h2>
 * <p>组织树节点包含以下核心字段：</p>
 * <pre>
 * {
 *   "id": 1,              // 组织 ID
 *   "name": "研发部",      // 组织名称
 *   "pid": 0,             // 父组织 ID
 *   "children": [],       // 子节点列表（全量加载时）
 *   "hasChildren": true   // 是否有子节点（懒加载时）
 * }
 * </pre>
 *
 * <h2>错误处理</h2>
 * <p>接口可能返回的错误：</p>
 * <ul>
 *   <li><strong>403 Forbidden</strong>: 权限不足</li>
 *   <li><strong>404 Not Found</strong>: 组织不存在</li>
 *   <li><strong>400 Bad Request</strong>: 参数错误（如删除有子节点的组织）</li>
 *   <li><strong>500 Internal Server Error</strong>: 服务器内部错误</li>
 * </ul>
 *
 * @see io.dataease.api.permissions.org.dto
 * @see io.dataease.api.permissions.org.vo
 * @since 1.0
 */
package io.dataease.api.permissions.org.api;
