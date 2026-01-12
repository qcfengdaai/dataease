/**
 * 缓存服务包
 * <p>
 * 提供DataEase系统的缓存抽象层，支持Redis和本地缓存两种实现。
 * 统一的缓存接口便于切换缓存实现，提高系统性能。
 * </p>
 *
 * <h2>核心组件</h2>
 *
 * <h3>1. 缓存服务接口</h3>
 * <ul>
 *   <li>{@link io.dataease.cache.DECacheService} - 缓存服务接口</li>
 * </ul>
 *
 * <h3>2. 接口说明</h3>
 * <ul>
 *   <li>该包当前只包含核心接口定义</li>
 *   <li>具体的缓存实现（Redis、本地缓存等）由各模块自行提供</li>
 *   <li>通过依赖注入的方式使用具体的缓存实现</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>示例1：基本使用</h3>
 * <pre>
 * {@literal @}Service
 * public class DatasetService {
 *
 *     {@literal @}Resource
 *     private DECacheService&lt;Dataset&gt; cacheService;
 *
 *     public Dataset getById(Long id) {
 *         String cacheName = "datasetCache";
 *         String cacheKey = "dataset:info:" + id;
 *
 *         // 先从缓存获取
 *         Dataset dataset = cacheService.get(cacheName, cacheKey);
 *         if (dataset != null) {
 *             return dataset;
 *         }
 *
 *         // 缓存未命中，查询数据库
 *         dataset = datasetMapper.selectById(id);
 *
 *         // 写入缓存，过期时间1小时
 *         cacheService.put(cacheName, cacheKey, dataset, 1L, TimeUnit.HOURS);
 *
 *         return dataset;
 *     }
 * }
 * </pre>
 *
 * <h3>示例2：缓存清除</h3>
 * <pre>
 * public void updateDataset(Dataset dataset) {
 *     // 更新数据库
 *     datasetMapper.updateById(dataset);
 *
 *     // 清除缓存
 *     String cacheName = "datasetCache";
 *     String cacheKey = "dataset:info:" + dataset.getId();
 *     cacheService.keyRemove(cacheName, cacheKey);
 * }
 * </pre>
 *
 * <h3>示例3：缓存存在性检查</h3>
 * <pre>
 * {@literal @}Service
 * public class CacheManagementService {
 *
 *     {@literal @}Resource
 *     private DECacheService&lt;Object&gt; cacheService;
 *
 *     public boolean isDataCached(String dataId) {
 *         String cacheName = "dataCache";
 *         String cacheKey = "data:info:" + dataId;
 *
 *         // 检查缓存空间是否存在
 *         if (!cacheService.cacheExist(cacheName)) {
 *             return false;
 *         }
 *
 *         // 检查具体的键是否存在
 *         return cacheService.keyExist(cacheName, cacheKey);
 *     }
 *
 *     public void clearExpiredCache() {
 *         String cacheName = "tempCache";
 *         String[] keys = {"temp:1", "temp:2", "temp:3"};
 *
 *         for (String key : keys) {
 *             if (cacheService.keyExist(cacheName, key)) {
 *                 cacheService.keyRemove(cacheName, key);
 *             }
 *         }
 *     }
 * }
 * </pre>
 *
 * <h3>示例4：缓存用户权限</h3>
 * <pre>
 * {@literal @}Service
 * public class UserPermissionService {
 *
 *     {@literal @}Resource
 *     private DECacheService&lt;List&lt;String&gt;&gt; cacheService;
 *
 *     public List&lt;String&gt; getUserPermissions(Long userId) {
 *         String cacheName = "permissionCache";
 *         String cacheKey = "auth:user:" + userId;
 *
 *         // 从缓存获取
 *         List&lt;String&gt; permissions = cacheService.get(cacheName, cacheKey);
 *         if (permissions != null) {
 *             return permissions;
 *         }
 *
 *         // 查询数据库
 *         permissions = permissionMapper.selectByUserId(userId);
 *
 *         // 缓存，过期时间30分钟
 *         cacheService.put(cacheName, cacheKey, permissions, 30L, TimeUnit.MINUTES);
 *
 *         return permissions;
 *     }
 * }
 * </pre>
 *
 * <h3>示例5：缓存过期时间管理</h3>
 * <pre>
 * {@literal @}Service
 * public class SessionService {
 *
 *     {@literal @}Resource
 *     private DECacheService&lt;String&gt; cacheService;
 *
 *     public void createUserSession(String userId, String sessionToken) {
 *         String cacheName = "sessionCache";
 *         String cacheKey = "session:" + userId;
 *
 *         // 缓存会话令牌，过期时间2小时
 *         cacheService.put(cacheName, cacheKey, sessionToken, 2L, TimeUnit.HOURS);
 *     }
 *
 *     public boolean isSessionValid(String userId) {
 *         String cacheName = "sessionCache";
 *         String cacheKey = "session:" + userId;
 *
 *         // 检查会话是否还存在（未过期）
 *         return cacheService.keyExist(cacheName, cacheKey);
 *     }
 *
 *     public void logout(String userId) {
 *         String cacheName = "sessionCache";
 *         String cacheKey = "session:" + userId;
 *
 *         // 立即清除会话缓存
 *         cacheService.keyRemove(cacheName, cacheKey);
 *     }
 * }
 * </pre>
 *
 * <h2>缓存策略</h2>
 *
 * <h3>1. 缓存穿透</h3>
 * <ul>
 *   <li>查询不存在的数据，缓存空值，设置较短过期时间</li>
 *   <li>使用布隆过滤器判断数据是否存在</li>
 * </ul>
 *
 * <h3>2. 缓存击穿</h3>
 * <ul>
 *   <li>热点数据过期，大量请求同时查询数据库</li>
 *   <li>使用分布式锁，只让一个请求查询数据库</li>
 *   <li>热点数据设置永不过期</li>
 * </ul>
 *
 * <h3>3. 缓存雪崩</h3>
 * <ul>
 *   <li>大量缓存同时过期</li>
 *   <li>过期时间加随机值，避免同时过期</li>
 *   <li>缓存预热，系统启动时加载热点数据</li>
 * </ul>
 *
 * <h2>缓存键设计</h2>
 *
 * <h3>键命名规范</h3>
 * <ul>
 *   <li>格式：<code>模块:类型:ID</code></li>
 *   <li>示例：<code>dataset:info:123</code></li>
 *   <li>使用冒号分隔，便于批量操作</li>
 * </ul>
 *
 * <h3>常用缓存键</h3>
 * <ul>
 *   <li><code>auth:user:{userId}</code> - 用户信息</li>
 *   <li><code>auth:permission:{userId}</code> - 用户权限</li>
 *   <li><code>dataset:info:{id}</code> - 数据集信息</li>
 *   <li><code>chart:config:{id}</code> - 图表配置</li>
 *   <li><code>panel:layout:{id}</code> - 仪表板布局</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 *
 * <h3>1. 缓存一致性</h3>
 * <ul>
 *   <li>更新数据库后立即清除缓存</li>
 *   <li>或使用先删除缓存再更新数据库的策略</li>
 *   <li>注意并发更新的一致性问题</li>
 * </ul>
 *
 * <h3>2. 缓存过期时间</h3>
 * <ul>
 *   <li>根据数据更新频率设置合理的过期时间</li>
 *   <li>热点数据：较长过期时间或永不过期</li>
 *   <li>临时数据：较短过期时间</li>
 *   <li>用户会话：根据业务需求设置</li>
 * </ul>
 *
 * <h3>3. 缓存大小</h3>
 * <ul>
 *   <li>避免缓存过大的对象</li>
 *   <li>大对象考虑压缩或分片</li>
 *   <li>监控Redis内存使用</li>
 * </ul>
 *
 * <h3>4. 序列化</h3>
 * <ul>
 *   <li>使用JSON序列化，便于调试和跨语言</li>
 *   <li>或使用Protobuf提高性能</li>
 *   <li>注意序列化兼容性</li>
 * </ul>
 *
 * <h2>系统中的使用位置</h2>
 *
 * <h3>Core模块中的使用</h3>
 * <ul>
 *   <li><strong>用户会话管理</strong> - 缓存用户登录状态和会话信息</li>
 *   <li><strong>权限缓存</strong> - 缓存用户权限和角色信息，减少数据库查询</li>
 *   <li><strong>数据集缓存</strong> - 缓存数据集结构和查询结果</li>
 *   <li><strong>图表配置缓存</strong> - 缓存图表配置信息，提高渲染性能</li>
 *   <li><strong>仪表板缓存</strong> - 缓存仪表板布局和组件信息</li>
 * </ul>
 *
 * <h3>SDK模块中的使用</h3>
 * <ul>
 *   <li><strong>认证模块</strong> - 缓存Token验证结果和用户认证状态</li>
 *   <li><strong>数据处理模块</strong> - 缓存计算结果和临时数据</li>
 *   <li><strong>API调用缓存</strong> - 缓存第三方API调用结果</li>
 *   <li><strong>配置信息缓存</strong> - 缓存系统配置和应用设置</li>
 * </ul>
 *
 * <h3>分布式环境中的使用</h3>
 * <ul>
 *   <li><strong>跨节点数据共享</strong> - 通过Redis实现多节点间的数据共享</li>
 *   <li><strong>分布式锁</strong> - 配合具体实现可用于分布式锁机制</li>
 *   <li><strong>集群同步</strong> - 集群环境下的配置和状态同步</li>
 * </ul>
 *
 * <h2>接口实现建议</h2>
 *
 * <h3>实现类应考虑的特性</h3>
 * <ul>
 *   <li><strong>序列化机制</strong> - 选择合适的序列化方式（JSON、Protobuf等）</li>
 *   <li><strong>异常处理</strong> - 优雅处理网络异常和连接失败</li>
 *   <li><strong>性能监控</strong> - 提供缓存命中率和性能指标</li>
 *   <li><strong>内存管理</strong> - 防止内存泄漏和缓存雪崩</li>
 *   <li><strong>配置管理</strong> - 支持动态配置更新</li>
 * </ul>
 *
 * <h3>推荐的实现方式</h3>
 * <ul>
 *   <li><strong>Redis实现</strong> - 适用于分布式环境和高并发场景</li>
 *   <li><strong>本地缓存实现</strong> - 适用于单机环境和低延迟要求</li>
 *   <li><strong>多级缓存</strong> - 结合本地缓存和分布式缓存的优势</li>
 *   <li><strong>缓存降级</strong> - 在缓存不可用时自动降级到数据库查询</li>
 * </ul>
 *
 * @author DataEase团队
 * @since 1.0.0
 * @version 2.0.0
 * @see java.util.concurrent.TimeUnit 时间单位枚举
 */
package io.dataease.cache;
