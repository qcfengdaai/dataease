/**
 * 组织架构管理模块
 *
 * <h2>模块概述</h2>
 * <p>本模块提供完整的组织架构管理功能，支持多级层次的组织单位结构管理。
 * 组织架构是 DataEase 权限体系的基础，通过组织树形结构实现资源的层级管理和权限继承。</p>
 *
 * <h2>核心功能</h2>
 * <ul>
 *   <li>组织树查询：支持全量加载和懒加载两种模式</li>
 *   <li>组织单位管理：创建、编辑和删除组织单位</li>
 *   <li>权限过滤查询：基于用户权限范围的组织树查询</li>
 *   <li>组织信息查询：组织详情、存在性检查、子组织列表等</li>
 *   <li>组织关系管理：用户、角色与组织的关联关系维护</li>
 * </ul>
 *
 * <h2>包结构</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.org.api} - API 接口定义层</li>
 *   <li>{@link io.dataease.api.permissions.org.dto} - 数据传输对象（请求参数）</li>
 *   <li>{@link io.dataease.api.permissions.org.vo} - 视图对象（响应数据）</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <ol>
 *   <li><strong>部门组织管理</strong>：企业内部按照公司-部门-小组的层级结构管理组织架构</li>
 *   <li><strong>资源归属管理</strong>：数据源、数据集等资源归属到特定组织，便于权限管控</li>
 *   <li><strong>权限继承</strong>：用户对某个组织有权限，自动拥有其所有子组织的权限</li>
 *   <li><strong>多租户隔离</strong>：不同组织之间的数据和用户隔离</li>
 *   <li><strong>组织树展示</strong>：在用户界面展示可折叠的组织树结构</li>
 * </ol>
 *
 * <h2>实现位置</h2>
 * <p>本模块的接口实现位于：</p>
 * <ul>
 *   <li><strong>单机版/社区版</strong>: {@code core-backend/src/main/java/io/dataease/commons/permission/controller/OrgController}</li>
 *   <li><strong>企业版</strong>: 分布式模块中的对应实现</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>1. 查询组织树（全量加载）</h3>
 * <pre>{@code
 * // 前端调用示例
 * POST /api/org/page/tree
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *   Content-Type: application/json
 *
 * Request Body:
 * {
 *   "keyword": "研发",  // 可选：按名称模糊搜索
 *   "sort": "asc"      // 可选：排序方式
 * }
 *
 * Response:
 * [
 *   {
 *     "id": 1,
 *     "name": "总公司",
 *     "pid": 0,
 *     "children": [
 *       {
 *         "id": 2,
 *         "name": "研发部",
 *         "pid": 1,
 *         "children": [
 *           {
 *             "id": 3,
 *             "name": "前端组",
 *             "pid": 2,
 *             "children": []
 *           }
 *         ]
 *       }
 *     ]
 *   }
 * ]
 * }</pre>
 *
 * <h3>2. 懒加载组织树</h3>
 * <pre>{@code
 * POST /api/org/page/lazyTree
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *   Content-Type: application/json
 *
 * Request Body:
 * {
 *   "pid": 0,          // 父节点 ID，0 表示加载根节点
 *   "keyword": "",     // 可选：搜索关键词
 *   "expandIds": []    // 需要展开的节点 ID 列表
 * }
 *
 * Response:
 * {
 *   "nodes": [         // 当前层级的节点列表
 *     {
 *       "id": 1,
 *       "name": "总公司",
 *       "pid": 0,
 *       "hasChildren": true
 *     }
 *   ],
 *   "expandIds": [1]   // 需要自动展开的节点 ID
 * }
 * }</pre>
 *
 * <h3>3. 创建组织单位</h3>
 * <pre>{@code
 * POST /api/org/page/create
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *   Content-Type: application/json
 *
 * Request Body:
 * {
 *   "name": "技术部",
 *   "pid": 1          // 父组织 ID，0 或空表示创建根级组织
 * }
 *
 * Response:
 * 100  // 返回新创建的组织 ID
 * }</pre>
 *
 * <h3>4. 编辑组织单位</h3>
 * <pre>{@code
 * POST /api/org/page/edit
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *   Content-Type: application/json
 *
 * Request Body:
 * {
 *   "id": 100,
 *   "name": "技术研发部"  // 新名称
 * }
 * }</pre>
 *
 * <h3>5. 删除组织单位</h3>
 * <pre>{@code
 * POST /api/org/page/delete/100
 * Headers:
 *   Authorization: Bearer {jwt_token}
 * }</pre>
 *
 * <h3>6. 查询权限内的组织树</h3>
 * <pre>{@code
 * // 只返回当前用户有权限访问的组织节点
 * POST /api/org/mounted
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *   Content-Type: application/json
 *
 * Request Body:
 * {
 *   "keyword": "研发"  // 可选：搜索关键词
 * }
 *
 * Response:
 * [
 *   {
 *     "id": 2,
 *     "name": "研发部",
 *     "pid": 1,
 *     "children": [...]
 *   }
 * ]
 * }</pre>
 *
 * <h3>7. 获取组织详情</h3>
 * <pre>{@code
 * GET /api/org/detail/100
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *
 * Response:
 * {
 *   "id": 100,
 *   "name": "技术研发部",
 *   "pid": 1,
 *   "rootPath": "/1/100/"  // 从根节点到当前节点的完整路径
 * }
 * }</pre>
 *
 * <h2>数据模型</h2>
 * <p>组织架构采用<strong>左右值树</strong>（Nested Set Model）或<strong>路径枚举</strong>（Path Enumeration）模型存储，
 * 支持高效的树形查询和权限判断。</p>
 *
 * <h3>主要字段</h3>
 * <ul>
 *   <li><strong>id</strong>: 组织 ID（主键）</li>
 *   <li><strong>name</strong>: 组织名称</li>
 *   <li><strong>pid</strong>: 父组织 ID（0 表示根节点）</li>
 *   <li><strong>rootPath</strong>: 根路径，如 "/1/10/100/"</li>
 *   <li><strong>level</strong>: 层级深度</li>
 *   <li><strong>sort</strong>: 排序号</li>
 * </ul>
 *
 * <h2>权限说明</h2>
 * <ul>
 *   <li><strong>m:read</strong>: 查看组织树的基础权限</li>
 *   <li><strong>组织ID:manage</strong>: 对特定组织的管理权限（编辑、删除）</li>
 *   <li>权限继承：拥有父组织权限自动拥有所有子组织权限</li>
 *   <li>权限过滤：查询结果自动过滤掉用户无权访问的组织节点</li>
 * </ul>
 *
 * <h2>性能优化</h2>
 * <ul>
 *   <li><strong>懒加载模式</strong>：当组织数量超过一定阈值时，使用懒加载避免一次性加载大量数据</li>
 *   <li><strong>路径缓存</strong>：rootPath 字段支持快速的祖先节点查询和子树查询</li>
 *   <li><strong>权限缓存</strong>：用户的组织权限信息会缓存在 Redis 中</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>删除组织前需确保该组织下没有子组织、用户和业务资源</li>
 *   <li>组织树的层级深度建议不超过 10 层，过深影响查询性能</li>
 *   <li>组织名称支持中英文、数字和常用符号，长度限制为 50 字符</li>
 *   <li>根节点的 pid 为 0 或 null</li>
 *   <li>修改组织结构可能影响大量用户的权限，操作需谨慎</li>
 *   <li>企业版支持更丰富的组织管理功能（如组织负责人、组织类型等）</li>
 * </ol>
 *
 * @author DataEase Team
 * @since 1.0
 */
package io.dataease.api.permissions.org;
