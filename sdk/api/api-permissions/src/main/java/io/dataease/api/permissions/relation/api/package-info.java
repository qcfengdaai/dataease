/**
 * 关系管理接口层
 *
 * <p>本包定义了关系管理的核心接口，主要用于内部服务调用。
 * 这些接口通常不直接暴露给前端，而是在后端服务中用于权限校验和资源过滤。</p>
 *
 * <h2>接口列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.relation.api.RelationApi} - 关系管理核心接口</li>
 * </ul>
 *
 * <h2>实现类位置</h2>
 * <p>接口实现类位于 core-backend 模块的 service 包中，通过 Spring 的 {@code @Service} 注解标注。</p>
 *
 * <h2>调用方式</h2>
 * <p>关系接口的调用方式有两种：</p>
 *
 * <h3>1. 内部服务调用（推荐）</h3>
 * <pre>{@code
 * @Service
 * public class DatasourceService {
 *
 *     @Autowired
 *     private RelationApi relationApi;
 *
 *     public DatasourceVO getById(Long id) {
 *         // 查询数据源所属组织
 *         Long orgId = relationApi.getDsResource(id);
 *
 *         // 检查权限
 *         if (!checkPermission(orgId)) {
 *             throw new DEException("无权访问该数据源");
 *         }
 *
 *         return datasourceMapper.selectById(id);
 *     }
 * }
 * }</pre>
 *
 * <h3>2. Feign 远程调用（企业版分布式环境）</h3>
 * <pre>{@code
 * @FeignClient(name = "permission-service")
 * public interface RelationFeignClient extends RelationApi {
 *     // 继承 RelationApi 的所有方法
 * }
 *
 * @Service
 * public class DatasourceService {
 *
 *     @Autowired
 *     private RelationFeignClient relationClient;
 *
 *     public DatasourceVO getById(Long id) {
 *         // 通过 Feign 远程调用
 *         Long orgId = relationClient.getDsResource(id);
 *         // ...
 *     }
 * }
 * }</pre>
 *
 * <h2>接口功能说明</h2>
 *
 * <h3>1. getDsResource() - 获取数据源的组织 ID</h3>
 * <p><strong>用途</strong>：查询指定数据源归属的组织</p>
 * <p><strong>参数</strong>：数据源 ID</p>
 * <p><strong>返回</strong>：组织 ID，如果数据源不存在或未关联组织则返回 null</p>
 * <p><strong>使用场景</strong>：</p>
 * <ul>
 *   <li>数据源访问权限判断</li>
 *   <li>数据源列表过滤</li>
 *   <li>数据源操作前的权限预检查</li>
 * </ul>
 *
 * <h3>2. getDatasetResource() - 获取数据集的组织 ID</h3>
 * <p><strong>用途</strong>：查询指定数据集归属的组织</p>
 * <p><strong>参数</strong>：数据集 ID</p>
 * <p><strong>返回</strong>：组织 ID，如果数据集不存在或未关联组织则返回 null</p>
 * <p><strong>使用场景</strong>：</p>
 * <ul>
 *   <li>数据集访问权限判断</li>
 *   <li>数据集列表过滤</li>
 *   <li>数据集操作前的权限预检查</li>
 * </ul>
 *
 * <h3>3. checkAuth() - 权限校验</h3>
 * <p><strong>用途</strong>：执行自定义的权限校验逻辑</p>
 * <p><strong>参数</strong>：无（从上下文获取当前用户信息）</p>
 * <p><strong>返回</strong>：无返回值，权限校验失败时抛出 DEException</p>
 * <p><strong>使用场景</strong>：</p>
 * <ul>
 *   <li>敏感操作前的统一权限检查</li>
 *   <li>自定义权限逻辑的入口</li>
 *   <li>API 调用前的预检查</li>
 * </ul>
 *
 * <h2>典型应用流程</h2>
 *
 * <h3>场景 1: 数据源列表查询（带权限过滤）</h3>
 * <pre>
 * 1. 用户请求数据源列表
 *    └─> GET /api/datasource/list
 *
 * 2. Controller 调用 Service
 *    └─> datasourceService.list()
 *
 * 3. Service 获取用户可见组织
 *    └─> orgService.getUserVisibleOrgIds(userId)
 *    └─> 返回: [1, 2, 3]
 *
 * 4. Service 查询所有数据源
 *    └─> datasourceMapper.selectList()
 *    └─> 返回: [ds1, ds2, ds3, ds4]
 *
 * 5. Service 过滤权限
 *    ├─> relationApi.getDsResource(ds1.id) → 1 ✓ (用户可见)
 *    ├─> relationApi.getDsResource(ds2.id) → 2 ✓ (用户可见)
 *    ├─> relationApi.getDsResource(ds3.id) → 5 ✗ (用户不可见)
 *    └─> relationApi.getDsResource(ds4.id) → 1 ✓ (用户可见)
 *
 * 6. 返回过滤后的结果
 *    └─> [ds1, ds2, ds4]
 * </pre>
 *
 * <h3>场景 2: 数据源详情查询（带权限校验）</h3>
 * <pre>
 * 1. 用户请求数据源详情
 *    └─> GET /api/datasource/detail/1000
 *
 * 2. Controller 调用 Service
 *    └─> datasourceService.getById(1000)
 *
 * 3. Service 查询资源所属组织
 *    └─> relationApi.getDsResource(1000)
 *    └─> 返回: 100 (组织 ID)
 *
 * 4. Service 检查用户权限
 *    └─> orgService.hasOrgPermission(userId, 100)
 *    └─> 返回: false (用户无权限)
 *
 * 5. 抛出权限异常
 *    └─> throw new DEException("无权访问该数据源")
 *
 * 6. 返回 403 错误给前端
 * </pre>
 *
 * <h2>性能优化建议</h2>
 * <ol>
 *   <li><strong>批量查询</strong>：避免循环调用 {@code getDsResource()}，使用批量接口一次性查询
 *       <pre>{@code
 * // 不推荐：循环调用
 * for (Datasource ds : datasources) {
 *     Long orgId = relationApi.getDsResource(ds.getId());
 * }
 *
 * // 推荐：批量查询
 * List<Long> dsIds = datasources.stream()
 *     .map(Datasource::getId)
 *     .collect(Collectors.toList());
 * Map<Long, Long> dsOrgMap = relationApi.batchGetDsResource(dsIds);
 *       }</pre>
 *   </li>
 *   <li><strong>缓存结果</strong>：对于不经常变化的关系，可以缓存查询结果
 *       <pre>{@code
 * @Cacheable(value = "ds-org-relation", key = "#dsId")
 * public Long getDsResource(Long dsId) {
 *     return relationMapper.selectOrgIdByDsId(dsId);
 * }
 *       }</pre>
 *   </li>
 *   <li><strong>提前过滤</strong>：在 SQL 层面进行权限过滤，避免在应用层过滤
 *       <pre>{@code
 * // SQL 层面直接关联组织权限
 * SELECT ds.* FROM datasource ds
 * INNER JOIN rel_resource_org rro ON ds.id = rro.resource_id
 * WHERE rro.org_id IN (1, 2, 3)  -- 用户可见的组织 ID
 *       }</pre>
 *   </li>
 * </ol>
 *
 * <h2>错误处理</h2>
 * <p>接口调用可能出现的错误：</p>
 * <ul>
 *   <li><strong>资源不存在</strong>: 返回 null 而不是抛出异常，由调用方判断</li>
 *   <li><strong>权限不足</strong>: 抛出 DEException，包含明确的错误信息</li>
 *   <li><strong>系统错误</strong>: 抛出运行时异常，由全局异常处理器捕获</li>
 * </ul>
 *
 * <h2>扩展接口</h2>
 * <p>如果需要支持新的资源类型，按以下步骤扩展：</p>
 * <ol>
 *   <li>在 RelationApi 中添加新方法，如 {@code getDashboardResource()}</li>
 *   <li>在实现类中实现该方法</li>
 *   <li>在关联表中添加对应的资源类型记录</li>
 *   <li>更新缓存策略（如果使用缓存）</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.relation.dto
 * @since 1.0
 */
package io.dataease.api.permissions.relation.api;
