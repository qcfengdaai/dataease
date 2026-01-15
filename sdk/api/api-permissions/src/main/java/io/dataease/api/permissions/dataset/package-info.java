/**
 * 数据集权限管理模块
 *
 * <h2>模块概述</h2>
 * <p>本模块提供数据集的细粒度权限控制功能,支持列级别和行级别的权限管理。
 * 通过对数据集的列和行进行精细化的权限配置,实现数据的分层分级访问控制,
 * 确保不同用户只能访问其被授权的数据范围,保护数据安全和隐私。</p>
 *
 * <h2>核心功能</h2>
 * <ul>
 *   <li><strong>列权限管理</strong>:控制用户对数据集中特定列的访问权限
 *     <ul>
 *       <li>可见列配置:指定用户可以查看哪些数据列</li>
 *       <li>隐藏列配置:指定用户不能查看哪些数据列</li>
 *       <li>白名单机制:为特定用户设置列访问例外</li>
 *       <li>敏感字段保护:自动隐藏或脱敏敏感信息</li>
 *     </ul>
 *   </li>
 *   <li><strong>行权限管理</strong>:控制用户对数据集中特定行的访问权限
 *     <ul>
 *       <li>条件过滤:基于字段值设置数据访问条件</li>
 *       <li>组织隔离:不同组织只能查看自己的数据</li>
 *       <li>角色过滤:根据角色动态过滤数据行</li>
 *       <li>用户绑定:将数据行与特定用户关联</li>
 *     </ul>
 *   </li>
 *   <li><strong>权限查询</strong>:查询数据集的权限配置列表</li>
 *   <li><strong>权限编辑</strong>:创建、修改、删除权限规则</li>
 *   <li><strong>权限验证</strong>:在数据查询时自动应用权限过滤</li>
 * </ul>
 *
 * <h2>权限模型</h2>
 *
 * <h3>列权限模型</h3>
 * <pre>
 * ┌─────────────┐
 * │   数据集     │
 * └─────────────┘
 *       ↓
 * ┌─────────────────────────────┐
 * │   列1  │  列2  │  列3  │ ... │
 * └─────────────────────────────┘
 *       ↓         ↓         ↓
 *    可见      隐藏      可见
 *       ↓
 * ┌─────────────┐
 * │ 授权对象配置 │ (组织/角色/用户)
 * └─────────────┘
 * </pre>
 *
 * <h3>行权限模型</h3>
 * <pre>
 * ┌─────────────┐
 * │   数据集     │
 * └─────────────┘
 *       ↓
 * ┌─────────────────┐
 * │ 行1: dept='销售' │ ← 销售部可见
 * │ 行2: dept='研发' │ ← 研发部可见
 * │ 行3: dept='财务' │ ← 财务部可见
 * └─────────────────┘
 *       ↓
 * ┌─────────────────┐
 * │ 过滤条件配置      │
 * │ dept = ${dept}  │ (动态变量)
 * └─────────────────┘
 * </pre>
 *
 * <h2>包结构</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.dataset.api} - API 接口定义层
 *     <ul>
 *       <li>{@link io.dataease.api.permissions.dataset.api.ColumnPermissionsApi} - 列权限管理接口</li>
 *       <li>{@link io.dataease.api.permissions.dataset.api.RowPermissionsApi} - 行权限管理接口</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.dataset.dto} - 数据传输对象
 *     <ul>
 *       <li>DataSetColumnPermissionsDTO:列权限配置对象</li>
 *       <li>DataSetRowPermissionsTreeDTO:行权限树形对象</li>
 *       <li>DatasetRowPermissionsTreeRequest:行权限查询请求</li>
 *       <li>WhiteListUsersRequest:白名单用户请求</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>使用场景</h2>
 *
 * <h3>1. 敏感数据保护(列权限)</h3>
 * <pre>{@code
 * POST /api/dataset/columnPermissions/save
 * Content-Type: application/json
 *
 * {
 *   "datasetId": 1001,
 *   "authTargetType": 1,      // 授权类型:角色
 *   "authTargetId": 5,        // 角色ID:普通员工
 *   "permissions": [
 *     {
 *       "fieldId": "name",    // 可见:姓名
 *       "visible": true
 *     },
 *     {
 *       "fieldId": "idCard",  // 隐藏:身份证号
 *       "visible": false
 *     },
 *     {
 *       "fieldId": "salary",  // 隐藏:工资
 *       "visible": false
 *     }
 *   ],
 *   "enable": true
 * }
 * }</pre>
 *
 * <h3>2. 部门数据隔离(行权限)</h3>
 * <pre>{@code
 * POST /api/dataset/rowPermissions/save
 * Content-Type: application/json
 *
 * {
 *   "datasetId": 1001,
 *   "authTargetType": 0,           // 授权类型:组织
 *   "authTargetId": 10,            // 组织ID:销售部
 *   "expressType": "and",          // 表达式类型:且
 *   "tree": {
 *     "field": "department",       // 字段:部门
 *     "operator": "eq",            // 操作符:等于
 *     "value": "${currentDept}"    // 值:当前用户部门(动态变量)
 *   },
 *   "enable": true
 * }
 * }</pre>
 *
 * <h3>3. 查询列权限配置</h3>
 * <pre>{@code
 * GET /api/dataset/columnPermissions/pager/1001/1/10
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *
 * Response:
 * {
 *   "total": 5,
 *   "records": [
 *     {
 *       "id": 10001,
 *       "datasetId": 1001,
 *       "authTargetType": 1,
 *       "authTargetName": "普通员工",
 *       "permissions": [...],
 *       "whiteListUser": [...],
 *       "enable": true,
 *       "createTime": 1705123456789
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <h3>4. 查询行权限树</h3>
 * <pre>{@code
 * POST /api/dataset/rowPermissions/tree
 * Content-Type: application/json
 *
 * {
 *   "datasetId": 1001,
 *   "id": 10001
 * }
 *
 * Response:
 * {
 *   "id": 10001,
 *   "expressType": "and",
 *   "children": [
 *     {
 *       "field": "department",
 *       "operator": "eq",
 *       "value": "${currentDept}"
 *     },
 *     {
 *       "field": "status",
 *       "operator": "eq",
 *       "value": "active"
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <h2>权限计算规则</h2>
 * <ol>
 *   <li><strong>列权限</strong>:
 *     <ul>
 *       <li>默认所有列可见</li>
 *       <li>如果配置了可见列白名单,则只显示白名单中的列</li>
 *       <li>如果配置了隐藏列黑名单,则隐藏黑名单中的列</li>
 *       <li>白名单用户不受列权限限制</li>
 *       <li>多个规则取交集(最严格的规则)</li>
 *     </ul>
 *   </li>
 *   <li><strong>行权限</strong>:
 *     <ul>
 *       <li>默认所有行可见</li>
 *       <li>配置的过滤条件自动追加到数据查询 SQL</li>
 *       <li>支持动态变量替换(如:${userId}、${deptId})</li>
 *       <li>多个规则使用 AND 或 OR 逻辑组合</li>
 *       <li>行权限在数据查询时强制应用,无法绕过</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <h2>实现位置</h2>
 * <p>本模块的接口实现位于:</p>
 * <ul>
 *   <li><strong>单机版/社区版</strong>: {@code core-backend/src/main/java/io/dataease/dataset/permissions/}
 *     <ul>
 *       <li>controller: ColumnPermissionsController、RowPermissionsController</li>
 *       <li>service: ColumnPermissionsService、RowPermissionsService</li>
 *     </ul>
 *   </li>
 *   <li><strong>企业版</strong>: 分布式模块中的对应实现</li>
 * </ul>
 *
 * <h2>数据模型</h2>
 * <p>数据集权限涉及的主要数据表:</p>
 * <ul>
 *   <li><strong>dataset_column_permissions</strong>:列权限配置表</li>
 *   <li><strong>dataset_row_permissions</strong>:行权限配置表</li>
 *   <li><strong>dataset_row_permissions_tree</strong>:行权限过滤条件树表</li>
 *   <li><strong>column_permissions_white_list</strong>:列权限白名单表</li>
 * </ul>
 *
 * <h2>动态变量</h2>
 * <p>行权限支持的动态变量(在查询时自动替换为当前用户的实际值):</p>
 * <ul>
 *   <li><strong>${userId}</strong>:当前用户ID</li>
 *   <li><strong>${userName}</strong>:当前用户名</li>
 *   <li><strong>${deptId}</strong>:当前用户所属部门ID</li>
 *   <li><strong>${deptName}</strong>:当前用户所属部门名称</li>
 *   <li><strong>${roleId}</strong>:当前用户角色ID</li>
 *   <li><strong>${roleName}</strong>:当前用户角色名称</li>
 * </ul>
 *
 * <h2>安全说明</h2>
 * <ul>
 *   <li><strong>强制执行</strong>:权限在 SQL 层面强制执行,前端无法绕过</li>
 *   <li><strong>数据隔离</strong>:不同用户查询同一数据集看到的数据不同</li>
 *   <li><strong>缓存策略</strong>:权限配置缓存,提高查询性能</li>
 *   <li><strong>审计日志</strong>:记录权限配置的变更历史</li>
 *   <li><strong>性能优化</strong>:行权限条件会自动优化到 SQL 执行计划中</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>列权限配置后立即生效,用户刷新页面即可看到变化</li>
 *   <li>行权限会影响数据统计结果,不同用户看到的统计数据可能不同</li>
 *   <li>白名单用户不受列权限限制,但仍受行权限限制</li>
 *   <li>删除授权对象(组织/角色/用户)时,需要同步清理相关权限配置</li>
 *   <li>复杂的行权限条件可能影响查询性能,建议合理设计过滤条件</li>
 *   <li>动态变量必须在系统预定义的变量列表中,不支持自定义变量</li>
 * </ol>
 *
 * <h2>最佳实践</h2>
 * <ul>
 *   <li><strong>优先使用角色</strong>:通过角色配置权限,而不是为每个用户单独配置</li>
 *   <li><strong>最小权限原则</strong>:默认隐藏敏感字段,只对需要的角色开放</li>
 *   <li><strong>合理分层</strong>:按业务需求分层设置权限,避免过度复杂的权限规则</li>
 *   <li><strong>定期审计</strong>:定期检查权限配置,清理不必要的权限规则</li>
 *   <li><strong>性能测试</strong>:复杂权限规则上线前进行性能测试</li>
 * </ul>
 *
 * @author DataEase Team
 * @since 2.0
 * @see io.dataease.api.permissions.auth
 */
package io.dataease.api.permissions.dataset;
