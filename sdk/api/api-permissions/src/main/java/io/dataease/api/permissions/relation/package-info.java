/**
 * 关系管理模块
 *
 * <h2>模块概述</h2>
 * <p>本模块提供 DataEase 权限体系中各类资源之间关联关系的管理功能。
 * 关系管理是权限系统的核心组成部分，负责维护用户、角色、组织与业务资源之间的映射关系，
 * 并提供基于这些关系的权限校验功能。</p>
 *
 * <h2>核心功能</h2>
 * <ul>
 *   <li>资源关系查询：获取业务资源（数据源、数据集等）与组织的关联关系</li>
 *   <li>权限校验：基于用户-角色-组织关系进行权限验证</li>
 *   <li>关系映射：支持多对多的资源关联关系</li>
 *   <li>权限继承：支持基于组织树的权限继承机制</li>
 * </ul>
 *
 * <h2>包结构</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.relation.api} - API 接口定义层</li>
 *   <li>{@link io.dataease.api.permissions.relation.dto} - 数据传输对象（请求参数）</li>
 * </ul>
 *
 * <h2>关系类型说明</h2>
 *
 * <h3>1. 用户-组织关系</h3>
 * <ul>
 *   <li>用户归属：用户所属的组织单位</li>
 *   <li>多组织：一个用户可以属于多个组织</li>
 *   <li>主组织：用户的主要归属组织</li>
 * </ul>
 *
 * <h3>2. 角色-权限关系</h3>
 * <ul>
 *   <li>权限集合：角色拥有的权限列表</li>
 *   <li>权限继承：子角色继承父角色的权限</li>
 *   <li>权限组合：用户的实际权限是其所有角色权限的并集</li>
 * </ul>
 *
 * <h3>3. 资源-组织关系</h3>
 * <ul>
 *   <li>资源归属：业务资源（数据源、数据集、仪表板等）归属的组织</li>
 *   <li>权限范围：用户通过组织权限访问该组织下的所有资源</li>
 *   <li>资源隔离：不同组织的资源相互隔离</li>
 * </ul>
 *
 * <h3>4. 用户-角色关系</h3>
 * <ul>
 *   <li>角色分配：用户被分配的角色列表</li>
 *   <li>多角色：一个用户可以拥有多个角色</li>
 *   <li>动态授权：角色可以动态添加或移除</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <ol>
 *   <li><strong>权限判断</strong>：判断用户是否有权限访问某个资源
 *       <ul>
 *         <li>查询资源所属的组织</li>
 *         <li>检查用户是否有该组织的访问权限</li>
 *       </ul>
 *   </li>
 *   <li><strong>资源过滤</strong>：根据用户权限过滤资源列表
 *       <ul>
 *         <li>获取用户可访问的组织列表</li>
 *         <li>过滤出这些组织下的所有资源</li>
 *       </ul>
 *   </li>
 *   <li><strong>权限授权</strong>：为用户或角色分配资源权限
 *       <ul>
 *         <li>建立用户-角色关系</li>
 *         <li>建立角色-资源关系</li>
 *       </ul>
 *   </li>
 *   <li><strong>组织隔离</strong>：实现多租户数据隔离
 *       <ul>
 *         <li>每个租户对应一个根组织</li>
 *         <li>租户的所有资源归属到该组织树下</li>
 *       </ul>
 *   </li>
 * </ol>
 *
 * <h2>实现位置</h2>
 * <p>本模块的接口实现位于：</p>
 * <ul>
 *   <li><strong>单机版/社区版</strong>: {@code core-backend/src/main/java/io/dataease/commons/permission/service}</li>
 *   <li><strong>企业版</strong>: 分布式模块中的对应实现</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>1. 获取数据源的组织 ID</h3>
 * <pre>{@code
 * // 内部服务调用示例
 * Long dsId = 1000L;
 * Long orgId = relationApi.getDsResource(dsId);
 *
 * if (orgId != null) {
 *     // 检查用户是否有该组织的权限
 *     boolean hasPermission = checkUserOrgPermission(userId, orgId);
 *     if (!hasPermission) {
 *         throw new DEException("无权访问该数据源");
 *     }
 * }
 * }</pre>
 *
 * <h3>2. 获取数据集的组织 ID</h3>
 * <pre>{@code
 * // 内部服务调用示例
 * Long datasetId = 2000L;
 * Long orgId = relationApi.getDatasetResource(datasetId);
 *
 * // 用于权限过滤
 * if (orgId != null && userVisibleOrgIds.contains(orgId)) {
 *     // 用户可以访问该数据集
 *     return dataset;
 * }
 * }</pre>
 *
 * <h3>3. 权限校验</h3>
 * <pre>{@code
 * // 在执行敏感操作前进行权限检查
 * try {
 *     relationApi.checkAuth();
 *     // 权限校验通过，执行业务逻辑
 *     executeBusinessLogic();
 * } catch (DEException e) {
 *     // 权限校验失败
 *     return ResultHolder.error("权限不足：" + e.getMessage());
 * }
 * }</pre>
 *
 * <h3>4. 资源列表过滤（实际应用）</h3>
 * <pre>{@code
 * // Controller 层查询数据源列表
 * @GetMapping("/datasource/list")
 * public List<DatasourceVO> listDatasources() {
 *     // 1. 获取用户可见的组织 ID 列表
 *     List<Long> visibleOrgIds = getCurrentUserVisibleOrgIds();
 *
 *     // 2. 查询所有数据源
 *     List<Datasource> allDatasources = datasourceService.list();
 *
 *     // 3. 过滤出用户有权限的数据源
 *     return allDatasources.stream()
 *         .filter(ds -> {
 *             Long orgId = relationApi.getDsResource(ds.getId());
 *             return orgId != null && visibleOrgIds.contains(orgId);
 *         })
 *         .map(this::convertToVO)
 *         .collect(Collectors.toList());
 * }
 * }</pre>
 *
 * <h2>数据模型</h2>
 * <p>关系数据通常存储在关联表中，采用多对多的映射关系：</p>
 *
 * <h3>资源-组织关联表</h3>
 * <pre>
 * core_sys_rel_resource_org
 * ├── id              // 主键
 * ├── resource_id     // 资源 ID（数据源 ID、数据集 ID 等）
 * ├── resource_type   // 资源类型（datasource、dataset、dashboard 等）
 * ├── org_id          // 组织 ID
 * ├── create_time     // 创建时间
 * └── update_time     // 更新时间
 * </pre>
 *
 * <h3>用户-角色关联表</h3>
 * <pre>
 * core_sys_rel_user_role
 * ├── id              // 主键
 * ├── user_id         // 用户 ID
 * ├── role_id         // 角色 ID
 * ├── create_time     // 创建时间
 * └── update_time     // 更新时间
 * </pre>
 *
 * <h2>权限校验流程</h2>
 * <p>典型的权限校验流程：</p>
 * <ol>
 *   <li><strong>识别用户</strong>：从 JWT Token 或 Session 中获取当前用户 ID</li>
 *   <li><strong>获取用户角色</strong>：查询用户-角色关联表，获取用户的所有角色</li>
 *   <li><strong>获取角色权限</strong>：查询角色-权限关联表，获取所有角色的权限集合</li>
 *   <li><strong>获取用户组织</strong>：查询用户-组织关联表，获取用户可访问的组织列表</li>
 *   <li><strong>查询资源组织</strong>：调用 {@code getDsResource()} 或 {@code getDatasetResource()} 获取资源所属组织</li>
 *   <li><strong>权限判断</strong>：检查用户是否有该组织的访问权限</li>
 *   <li><strong>返回结果</strong>：通过返回 true，不通过抛出 DEException</li>
 * </ol>
 *
 * <h2>性能优化</h2>
 * <ul>
 *   <li><strong>缓存策略</strong>：用户权限信息缓存在 Redis 中，减少数据库查询
 *       <ul>
 *         <li>用户-角色关系缓存</li>
 *         <li>角色-权限关系缓存</li>
 *         <li>资源-组织关系缓存</li>
 *       </ul>
 *   </li>
 *   <li><strong>批量查询</strong>：一次性查询多个资源的组织关系，避免 N+1 查询问题</li>
 *   <li><strong>懒加载</strong>：仅在需要时才进行权限校验</li>
 *   <li><strong>索引优化</strong>：在关联表的外键字段上建立索引</li>
 * </ul>
 *
 * <h2>安全说明</h2>
 * <ul>
 *   <li><strong>权限最小化原则</strong>：用户默认没有任何权限，需要明确授权</li>
 *   <li><strong>权限继承</strong>：父组织的权限自动应用到子组织</li>
 *   <li><strong>权限隔离</strong>：不同组织的数据严格隔离，防止越权访问</li>
 *   <li><strong>审计日志</strong>：权限变更操作应记录审计日志</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>关系接口通常用于内部服务调用，不直接暴露给前端</li>
 *   <li>资源删除时需要同步删除相关的关联关系</li>
 *   <li>组织删除时需要处理该组织下的所有资源</li>
 *   <li>权限校验失败时应抛出明确的异常信息，便于调试</li>
 *   <li>缓存更新：权限关系变更时需要及时刷新缓存</li>
 *   <li>企业版可能提供更细粒度的权限控制（如字段级权限、行级权限等）</li>
 * </ol>
 *
 * <h2>扩展资源类型</h2>
 * <p>当前支持的资源类型：</p>
 * <ul>
 *   <li><strong>datasource</strong>: 数据源</li>
 *   <li><strong>dataset</strong>: 数据集</li>
 *   <li><strong>dashboard</strong>: 仪表板（未来可能支持）</li>
 *   <li><strong>report</strong>: 报表（未来可能支持）</li>
 * </ul>
 * <p>新增资源类型时，需要实现对应的 {@code getXxxResource()} 方法。</p>
 *
 * @author DataEase Team
 * @since 1.0
 */
package io.dataease.api.permissions.relation;
