package io.dataease.cache;

import java.util.concurrent.TimeUnit;

/**
 * DataEase缓存服务接口
 * 提供统一的缓存操作API，支持多种缓存实现（Redis、本地缓存等）
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>缓存数据的存储和获取</li>
 *   <li>缓存和键的存在性检查</li>
 *   <li>缓存键的删除操作</li>
 *   <li>支持自定义过期时间和时间单位</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>
 * // 存储数据到缓存
 * cacheService.put("userCache", "user:123", userInfo, 30L, TimeUnit.MINUTES);
 *
 * // 获取缓存数据
 * UserInfo user = cacheService.get("userCache", "user:123");
 *
 * // 检查缓存是否存在
 * if (cacheService.cacheExist("userCache")) {
 *     // 缓存存在，执行相关操作
 * }
 *
 * // 删除缓存键
 * cacheService.keyRemove("userCache", "user:123");
 * </pre>
 *
 * @param <T> 缓存值的类型，支持泛型以提供类型安全
 * @author DataEase团队
 * @since 1.0.0
 * @see java.util.concurrent.TimeUnit 时间单位枚举
 */
public interface DECacheService<T> {

    /**
     * 将指定的值存储到缓存中
     *
     * <p>功能说明：</p>
     * <ul>
     *   <li>在指定的缓存空间中存储键值对</li>
     *   <li>支持设置过期时间，到期后自动删除</li>
     *   <li>如果键已存在，将覆盖原有值</li>
     * </ul>
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>缓存用户会话信息</li>
     *   <li>缓存查询结果数据</li>
     *   <li>临时存储计算结果</li>
     * </ul>
     *
     * @param cacheName 缓存空间名称，用于逻辑分组管理不同类型的缓存
     * @param key       缓存键，在指定缓存空间内唯一标识缓存项
     * @param value     要缓存的值，支持任意类型的对象
     * @param expTime   过期时间长度，配合unit参数确定具体过期时间
     * @param unit      时间单位，如SECONDS、MINUTES、HOURS等
     */
    void put(String cacheName, String key, T value, Long expTime, TimeUnit unit);

    /**
     * 从缓存中获取指定键对应的值
     *
     * <p>功能说明：</p>
     * <ul>
     *   <li>根据缓存空间和键查找对应的缓存值</li>
     *   <li>如果键不存在或已过期，返回null</li>
     *   <li>支持泛型返回，保证类型安全</li>
     * </ul>
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>获取用户登录状态信息</li>
     *   <li>获取缓存的查询结果</li>
     *   <li>读取临时计算数据</li>
     * </ul>
     *
     * @param cacheName 缓存空间名称，必须与存储时使用的名称一致
     * @param key       缓存键，用于定位具体的缓存项
     * @return 缓存的值对象，如果不存在或已过期则返回null
     */
    T get(String cacheName, String key);

    /**
     * 检查指定名称的缓存空间是否存在
     *
     * <p>功能说明：</p>
     * <ul>
     *   <li>判断缓存空间是否已经创建并可用</li>
     *   <li>用于防止对不存在的缓存空间进行操作</li>
     *   <li>可用于缓存空间的预检查</li>
     * </ul>
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>在执行缓存操作前验证缓存空间</li>
     *   <li>系统健康检查时验证缓存可用性</li>
     *   <li>动态缓存管理中的状态检查</li>
     * </ul>
     *
     * @param cacheName 要检查的缓存空间名称
     * @return true表示缓存空间存在且可用，false表示不存在
     */
    boolean cacheExist(String cacheName);

    /**
     * 检查指定缓存空间中是否存在指定的键
     *
     * <p>功能说明：</p>
     * <ul>
     *   <li>判断特定键是否在缓存空间中存在</li>
     *   <li>不会触发键的访问或刷新过期时间</li>
     *   <li>用于键存在性的快速检查</li>
     * </ul>
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>在获取缓存前先检查键是否存在</li>
     *   <li>避免不必要的缓存miss操作</li>
     *   <li>实现缓存预热策略时的状态检查</li>
     * </ul>
     *
     * @param cacheName 缓存空间名称，指定要检查的缓存区域
     * @param key       要检查的缓存键
     * @return true表示键存在，false表示键不存在或已过期
     */
    boolean keyExist(String cacheName, String key);

    /**
     * 从指定缓存空间中删除指定的键
     *
     * <p>功能说明：</p>
     * <ul>
     *   <li>立即从缓存空间中移除指定的键值对</li>
     *   <li>释放键值对占用的内存空间</li>
     *   <li>删除操作是幂等的，重复删除不会产生错误</li>
     * </ul>
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>数据更新后清除旧的缓存数据</li>
     *   <li>用户登出时清除会话缓存</li>
     *   <li>缓存空间清理和内存优化</li>
     *   <li>实现缓存一致性策略</li>
     * </ul>
     *
     * @param cacheName 缓存空间名称，指定要操作的缓存区域
     * @param key       要删除的缓存键，如果键不存在则操作无效但不报错
     */
    void keyRemove(String cacheName, String key);
}
