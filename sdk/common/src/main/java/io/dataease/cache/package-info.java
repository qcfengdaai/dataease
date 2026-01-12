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
 * <h3>2. 缓存实现</h3>
 * <ul>
 *   <li>{@link io.dataease.cache.impl.RedisCacheImpl} - Redis缓存实现</li>
 *   <li>{@link io.dataease.cache.impl.DefaultCacheImpl} - 本地缓存实现（Caffeine）</li>
 * </ul>
 *
 * <h3>3. 缓存注解</h3>
 * <ul>
 *   <li>{@link io.dataease.cache.annotation.CacheClear} - 缓存清除注解</li>
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
 *     private DECacheService cacheService;
 *
 *     public Dataset getById(Long id) {
 *         // 构造缓存key
 *         String cacheKey = "dataset:info:" + id;
 *
 *         // 先从缓存获取
 *         Dataset dataset = cacheService.get(cacheKey);
 *         if (dataset != null) {
 *             return dataset;
 *         }
 *
 *         // 缓存未命中，查询数据库
 *         dataset = datasetMapper.selectById(id);
 *
 *         // 写入缓存，过期时间1小时
 *         cacheService.set(cacheKey, dataset, 3600);
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
 *     String cacheKey = "dataset:info:" + dataset.getId();
 *     cacheService.delete(cacheKey);
 *
 *     // 也可以使用通配符批量清除
 *     cacheService.deletePattern("dataset:*");
 * }
 * </pre>
 *
 * <h3>示例3：使用注解清除缓存</h3>
 * <pre>
 * {@literal @}Service
 * public class DatasetService {
 *
 *     {@literal @}CacheClear(key = "dataset:info:#p0.id")
 *     public void updateDataset(Dataset dataset) {
 *         // 更新数据库
 *         datasetMapper.updateById(dataset);
 *         // 方法执行完自动清除缓存
 *     }
 *
 *     {@literal @}CacheClear(pattern = "dataset:*")
 *     public void deleteDataset(Long id) {
 *         datasetMapper.deleteById(id);
 *         // 清除所有dataset相关缓存
 *     }
 * }
 * </pre>
 *
 * <h3>示例4：缓存用户权限</h3>
 * <pre>
 * public List&lt;String&gt; getUserPermissions(Long userId) {
 *     String cacheKey = "auth:user:" + userId;
 *
 *     // 从缓存获取
 *     List&lt;String&gt; permissions = cacheService.get(cacheKey);
 *     if (permissions != null) {
 *         return permissions;
 *     }
 *
 *     // 查询数据库
 *     permissions = permissionMapper.selectByUserId(userId);
 *
 *     // 缓存，过期时间30分钟
 *     cacheService.set(cacheKey, permissions, 1800);
 *
 *     return permissions;
 * }
 * </pre>
 *
 * <h3>示例5：分布式锁</h3>
 * <pre>
 * public void processTask(String taskId) {
 *     String lockKey = "lock:task:" + taskId;
 *
 *     // 尝试获取锁
 *     boolean locked = cacheService.setIfAbsent(lockKey, "locked", 60);
 *
 *     if (!locked) {
 *         throw new DEException("任务正在处理中");
 *     }
 *
 *     try {
 *         // 处理任务
 *         doProcess(taskId);
 *     } finally {
 *         // 释放锁
 *         cacheService.delete(lockKey);
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
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.constant.CacheConstant
 */
package io.dataease.cache;
