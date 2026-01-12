/**
 * 组织架构视图对象包
 *
 * <p>本包包含组织架构管理模块的所有 VO (View Object) 类，
 * 用于封装返回给客户端的响应数据。VO 对象是数据展示层对象，通常包含格式化后的数据。</p>
 *
 * <h2>VO 列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.org.vo.OrgPageVO} - 组织树节点视图对象（全量加载）</li>
 *   <li>{@link io.dataease.api.permissions.org.vo.LazyOrgTreeNode} - 组织树节点视图对象（懒加载）</li>
 *   <li>{@link io.dataease.api.permissions.org.vo.LazyTreeVO} - 懒加载树视图容器对象</li>
 *   <li>{@link io.dataease.api.permissions.org.vo.MountedVO} - 权限内组织树节点视图对象（全量加载）</li>
 *   <li>{@link io.dataease.api.permissions.org.vo.LazyMountedVO} - 权限内组织树视图容器对象（懒加载）</li>
 *   <li>{@link io.dataease.api.permissions.org.vo.OrgDetailVO} - 组织详细信息视图对象</li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 VO 类实现 {@link java.io.Serializable} 接口，支持序列化</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化 getter/setter</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>Long 类型的 ID 使用 {@code @JsonSerialize(using=ToStringSerializer.class)} 避免前端精度丢失</li>
 *   <li>根据前端展示需求组织数据结构，可能包含多个实体的组合数据</li>
 * </ul>
 *
 * <h2>VO 详细说明</h2>
 *
 * <h3>1. OrgPageVO - 组织树节点（全量加载）</h3>
 * <p>用于全量加载模式下返回完整的组织树结构：</p>
 * <pre>
 * {
 *   "id": 1,                   // 组织 ID
 *   "name": "研发部",           // 组织名称
 *   "pid": 0,                  // 父组织 ID
 *   "children": [              // 子节点列表（递归结构）
 *     {
 *       "id": 2,
 *       "name": "前端组",
 *       "pid": 1,
 *       "children": []
 *     }
 *   ],
 *   "level": 1,                // 层级深度
 *   "sort": 1,                 // 排序号
 *   "createTime": 1705123456789  // 创建时间
 * }
 * </pre>
 *
 * <h3>2. LazyOrgTreeNode - 组织树节点（懒加载）</h3>
 * <p>用于懒加载模式下返回单层节点信息：</p>
 * <pre>
 * {
 *   "id": 1,                   // 组织 ID
 *   "name": "研发部",           // 组织名称
 *   "pid": 0,                  // 父组织 ID
 *   "hasChildren": true,       // 是否有子节点（用于前端显示展开按钮）
 *   "level": 1,                // 层级深度
 *   "sort": 1                  // 排序号
 * }
 * </pre>
 *
 * <h3>3. LazyTreeVO - 懒加载树视图容器</h3>
 * <p>包装懒加载查询的结果，提供额外的元数据：</p>
 * <pre>
 * {
 *   "nodes": [                 // 当前层级的节点列表
 *     { "id": 1, "name": "研发部", ... }
 *   ],
 *   "expandIds": [1, 2],       // 需要自动展开的节点 ID 列表
 *   "total": 10                // 总节点数（可选）
 * }
 * </pre>
 *
 * <h3>4. MountedVO - 权限内组织树节点（全量加载）</h3>
 * <p>返回用户有权限访问的组织树节点，结构类似 OrgPageVO，但只包含用户可见的节点：</p>
 * <pre>
 * {
 *   "id": 2,
 *   "name": "研发部",
 *   "pid": 1,
 *   "children": [...],         // 只包含用户有权限的子节点
 *   "hasPermission": true      // 标识用户是否有该节点的权限
 * }
 * </pre>
 *
 * <h3>5. LazyMountedVO - 权限内组织树视图容器（懒加载）</h3>
 * <p>类似 LazyTreeVO，但节点经过权限过滤：</p>
 * <pre>
 * {
 *   "nodes": [                 // 用户可见的当前层级节点
 *     { "id": 2, "name": "研发部", ... }
 *   ],
 *   "expandIds": [2]           // 需要展开的节点
 * }
 * </pre>
 *
 * <h3>6. OrgDetailVO - 组织详细信息</h3>
 * <p>返回单个组织的完整详细信息：</p>
 * <pre>
 * {
 *   "id": 100,
 *   "name": "技术研发部",
 *   "pid": 1,
 *   "rootPath": "/1/100/",     // 从根到当前节点的完整路径
 *   "level": 2,
 *   "sort": 1,
 *   "createTime": 1705123456789,
 *   "updateTime": 1705123456789,
 *   "creatorName": "管理员",    // 创建人姓名
 *   "parentName": "总公司"     // 父组织名称
 * }
 * </pre>
 *
 * <h2>VO 与实体的区别</h2>
 * <ul>
 *   <li><strong>实体(Entity)</strong>：与数据库表一一对应，包含所有数据库字段</li>
 *   <li><strong>VO</strong>：面向前端展示，可能包含多个实体的组合数据，或对字段进行加工处理</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <p>VO 对象主要用于：</p>
 * <ol>
 *   <li>封装接口响应数据，统一返回格式</li>
 *   <li>组合多个实体的数据（如组织 + 创建人信息）</li>
 *   <li>对数据进行格式化展示（如时间戳转换为日期字符串）</li>
 *   <li>根据前端需求定制数据结构（如树形结构、懒加载结构）</li>
 * </ol>
 *
 * <h2>树形结构处理</h2>
 * <p>组织架构的树形数据有两种典型结构：</p>
 *
 * <h3>递归树结构（全量加载）</h3>
 * <pre>
 * // 适用于 OrgPageVO、MountedVO
 * {
 *   "id": 1,
 *   "children": [
 *     {
 *       "id": 2,
 *       "children": [...]  // 递归嵌套
 *     }
 *   ]
 * }
 * </pre>
 *
 * <h3>扁平列表结构（懒加载）</h3>
 * <pre>
 * // 适用于 LazyOrgTreeNode
 * [
 *   { "id": 1, "pid": 0, "hasChildren": true },
 *   { "id": 2, "pid": 1, "hasChildren": false }
 * ]
 * // 前端根据 pid 字段自行组装树形结构
 * </pre>
 *
 * <h2>JSON 序列化注意事项</h2>
 * <ul>
 *   <li><strong>Long 类型 ID</strong>: 使用 {@code @JsonSerialize(using=ToStringSerializer.class)} 转为字符串，
 *       避免 JavaScript 的 Number 类型精度丢失（JS 的 Number 最大安全整数是 2^53-1）</li>
 *   <li><strong>日期时间</strong>: 统一使用时间戳（Long 类型），由前端根据需要格式化</li>
 *   <li><strong>循环引用</strong>: 树形结构避免循环引用（子节点不包含父节点引用）</li>
 *   <li><strong>null 字段</strong>: 使用 {@code @JsonInclude(JsonInclude.Include.NON_NULL)} 忽略 null 字段，减少传输数据量</li>
 * </ul>
 *
 * <h2>性能优化建议</h2>
 * <ol>
 *   <li>全量加载模式下，建议组织数量不超过 500 个节点</li>
 *   <li>懒加载模式下，每次返回的节点数建议不超过 100 个</li>
 *   <li>使用缓存减少数据库查询（特别是组织树的查询）</li>
 *   <li>对于大型组织树，使用分页查询 + 虚拟滚动提升前端性能</li>
 * </ol>
 *
 * <h2>扩展建议</h2>
 * <p>新增 VO 类时应遵循以下规范：</p>
 * <ol>
 *   <li>类名以业务含义命名，后缀为 VO</li>
 *   <li>所有字段添加 {@code @Schema} 注解说明字段含义</li>
 *   <li>Long 类型 ID 添加 {@code @JsonSerialize} 注解</li>
 *   <li>提供清晰的类注释，说明数据结构和使用场景</li>
 *   <li>避免在 VO 中包含敏感信息（如密码、密钥等）</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.org.api
 * @see io.dataease.api.permissions.org.dto
 * @since 1.0
 */
package io.dataease.api.permissions.org.vo;
