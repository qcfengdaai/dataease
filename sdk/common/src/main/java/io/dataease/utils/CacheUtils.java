package io.dataease.utils;


import io.dataease.cache.DECacheService;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 缓存操作工具类
 * <p>
 * 提供统一的缓存操作接口，封装了{@link DECacheService}的核心功能，简化缓存的读写、删除等操作。
 * 支持多种缓存场景，包括单键操作、批量操作、延迟删除等功能。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>缓存存储 - 支持设置过期时间的键值对存储</li>
 *   <li>缓存读取 - 根据缓存名和键获取缓存值</li>
 *   <li>缓存删除 - 支持单键、多键、批量删除操作</li>
 *   <li>延迟删除 - 删除操作后延迟1秒再次删除，确保缓存清理</li>
 *   <li>键存在检查 - 判断指定的缓存键是否存在</li>
 * </ul>
 *
 * <p><b>缓存特性：</b></p>
 * <ul>
 *   <li>默认过期时间：8小时</li>
 *   <li>支持自定义过期时间和时间单位</li>
 *   <li>支持多缓存空间管理（通过cacheName区分）</li>
 *   <li>自动延迟删除机制，防止并发场景下的缓存不一致</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.i18n.Lang#isChinese()} - 缓存用户语言设置</li>
 *   <li>用户会话管理 - 缓存用户登录信息、权限数据</li>
 *   <li>数据查询结果缓存 - 缓存频繁查询的数据，提高性能</li>
 *   <li>系统配置缓存 - 缓存系统设置和配置信息</li>
 *   <li>临时数据存储 - 缓存验证码、临时令牌等短期数据</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：基本的缓存存取操作（使用默认8小时过期时间）
 * String cacheName = "user_cache";
 * String userId = "user_12345";
 * UserInfo userInfo = new UserInfo("张三", "admin");
 *
 * // 存储缓存（默认8小时过期）
 * CacheUtils.put(cacheName, userId, userInfo);
 *
 * // 读取缓存
 * UserInfo cached = (UserInfo) CacheUtils.get(cacheName, userId);
 * if (cached != null) {
 *     System.out.println("用户名: " + cached.getName());
 * }
 *
 * // 示例2：使用自定义过期时间
 * String verifyCode = "123456";
 * // 验证码缓存5分钟
 * CacheUtils.put("verify_code", "user_email@example.com", verifyCode, 5L, TimeUnit.MINUTES);
 *
 * // Token缓存24小时
 * CacheUtils.put("auth_token", "token_abc123", tokenData, 24L, TimeUnit.HOURS);
 *
 * // 示例3：检查缓存键是否存在
 * if (CacheUtils.keyExist(cacheName, userId)) {
 *     System.out.println("用户缓存存在");
 * } else {
 *     System.out.println("用户缓存已过期或不存在");
 * }
 *
 * // 示例4：简单删除缓存
 * CacheUtils.keyRemove(cacheName, userId);
 *
 * // 示例5：带回调的删除操作（用于需要执行后续操作的场景）
 * CacheUtils.remove(cacheName, userId, result -> {
 *     System.out.println("缓存已删除，执行后续业务逻辑");
 *     // 可以在这里更新数据库、发送通知等
 *     updateDatabase(userId);
 * });
 *
 * // 示例6：删除多个缓存空间中的同一个键
 * String[] cacheNames = {"user_cache", "permission_cache", "role_cache"};
 * CacheUtils.remove(cacheNames, userId, result -> {
 *     System.out.println("已从多个缓存空间删除用户数据");
 * });
 *
 * // 示例7：批量删除多个键
 * List&lt;String&gt; userIds = Arrays.asList("user_001", "user_002", "user_003");
 * CacheUtils.remove(cacheName, userIds, result -> {
 *     System.out.println("已批量删除" + userIds.size() + "个用户的缓存");
 * });
 *
 * // 示例8：实际应用 - 用户登录场景
 * public void userLogin(String userId, UserInfo userInfo) {
 *     // 存储用户信息到缓存，2小时过期
 *     CacheUtils.put("user_session", userId, userInfo, 2L, TimeUnit.HOURS);
 *
 *     // 存储用户权限到缓存
 *     CacheUtils.put("user_permission", userId, getUserPermissions(userId), 2L, TimeUnit.HOURS);
 * }
 *
 * // 示例9：实际应用 - 用户登出场景
 * public void userLogout(String userId) {
 *     String[] caches = {"user_session", "user_permission", "user_menu"};
 *     CacheUtils.remove(caches, userId, result -> {
 *         System.out.println("用户 " + userId + " 已登出，缓存已清理");
 *         // 记录登出日志
 *         logUserLogout(userId);
 *     });
 * }
 *
 * // 示例10：实际应用 - 语言设置缓存（参考Lang.isChinese()）
 * String language = "zh-CN";
 * CacheUtils.put("USER_COMMUNITY_LANGUAGE", "de", language, 30L, TimeUnit.DAYS);
 *
 * // 获取用户语言设置
 * Object langObj = CacheUtils.get("USER_COMMUNITY_LANGUAGE", "de");
 * String userLang = langObj != null ? langObj.toString() : "zh-CN";
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>缓存名称（cacheName）应该有明确的业务含义，便于管理</li>
 *   <li>缓存键（key）应该保证唯一性，避免不同业务数据冲突</li>
 *   <li>获取缓存时需要进行类型转换和空值检查</li>
 *   <li>remove方法会立即删除缓存，1秒后再次删除，确保分布式场景下的数据一致性</li>
 *   <li>Consumer回调函数在第一次删除后立即执行，不会等待延迟删除</li>
 *   <li>批量操作时注意数据量，避免一次性操作过多数据影响性能</li>
 *   <li>缓存过期时间应根据业务需求合理设置，避免过长或过短</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>使用常量定义缓存名称，避免硬编码字符串</li>
 *   <li>对于频繁访问但很少变化的数据，适合使用较长的缓存时间</li>
 *   <li>对于实时性要求高的数据，应使用较短的缓存时间或及时更新缓存</li>
 *   <li>删除缓存时，如果需要执行后续操作，使用带Consumer的remove方法</li>
 *   <li>在数据更新时，记得同步更新或删除相关缓存，保持数据一致性</li>
 *   <li>使用keyExist()方法判断缓存是否存在，避免不必要的get操作</li>
 * </ul>
 *
 * <p><b>线程安全性：</b></p>
 * <ul>
 *   <li>底层DECacheService保证了线程安全</li>
 *   <li>所有方法都可以在多线程环境下安全使用</li>
 *   <li>延迟删除通过DelayQueueUtils实现，确保异步删除的可靠性</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see DECacheService
 * @see DelayQueueUtils
 * @see io.dataease.i18n.Lang#isChinese()
 */
public class CacheUtils {

    private static DECacheService deCacheService;

    static {
        getService();
    }

    /**
     * 获取缓存服务实例
     * <p>
     * 通过Spring容器获取DECacheService的单例实例，使用懒加载模式。
     * 该方法是私有方法，仅在静态初始化块中调用。
     * </p>
     *
     * @return DECacheService缓存服务实例
     */
    private static DECacheService getService() {
        if (ObjectUtils.isEmpty(deCacheService)) {
            deCacheService = (DECacheService) CommonBeanFactory.getBean("dECacheService");
        }
        return deCacheService;
    }

    /**
     * 存储缓存数据（使用默认过期时间）
     * <p>
     * 将键值对存入指定的缓存空间，使用默认的8小时过期时间。
     * 这是最常用的缓存存储方法，适用于大多数缓存场景。
     * </p>
     *
     * @param cacheName 缓存空间名称，用于区分不同业务的缓存数据
     * @param key 缓存键，在同一缓存空间内必须唯一
     * @param val 缓存值，可以是任意对象类型
     */
    public static void put(String cacheName, String key, Object val) {
        deCacheService.put(cacheName, key, val, 8L, TimeUnit.HOURS);
    }

    /**
     * 存储缓存数据（自定义过期时间）
     * <p>
     * 将键值对存入指定的缓存空间，并设置自定义的过期时间。
     * 适用于需要精确控制缓存生命周期的场景，如验证码、临时令牌等。
     * </p>
     *
     * @param cacheName 缓存空间名称
     * @param key 缓存键
     * @param val 缓存值
     * @param expTime 过期时间数值
     * @param unit 时间单位（如TimeUnit.MINUTES、TimeUnit.HOURS等）
     */
    public static void put(String cacheName, String key, Object val, Long expTime, TimeUnit unit) {
        deCacheService.put(cacheName, key, val, expTime, unit);
    }

    /**
     * 获取缓存数据
     * <p>
     * 从指定的缓存空间中根据键获取缓存值。
     * 如果缓存不存在或已过期，返回null。
     * 调用方需要自行进行类型转换和空值检查。
     * </p>
     *
     * @param cacheName 缓存空间名称
     * @param key 缓存键
     * @return 缓存的对象值，如果不存在则返回null
     */
    public static Object get(String cacheName, String key) {
        return deCacheService.get(cacheName, key);
    }

    /**
     * 检查缓存键是否存在
     * <p>
     * 判断指定的缓存键在缓存空间中是否存在且未过期。
     * 这个方法比先get再判断null更高效。
     * </p>
     *
     * @param cacheName 缓存空间名称
     * @param key 缓存键
     * @return true表示缓存存在且未过期，false表示不存在或已过期
     */
    public static Boolean keyExist(String cacheName, String key) {
        return deCacheService.keyExist(cacheName, key);
    }

    /**
     * 简单删除缓存键
     * <p>
     * 从指定的缓存空间中立即删除指定的键。
     * 这是同步删除操作，没有延迟删除和回调功能。
     * 适用于简单的缓存清理场景。
     * </p>
     *
     * @param cacheName 缓存空间名称
     * @param key 要删除的缓存键
     */
    public static void keyRemove(String cacheName, String key) {
        deCacheService.keyRemove(cacheName, key);
    }

    /**
     * 删除单个缓存键（带回调和延迟删除）
     * <p>
     * 删除指定缓存键，支持回调函数和延迟删除机制：
     * 1. 立即删除缓存
     * 2. 执行回调函数
     * 3. 延迟1秒后再次删除（确保分布式场景下的数据一致性）
     * </p>
     *
     * <p>延迟删除的目的：</p>
     * <ul>
     *   <li>防止分布式缓存中的数据不一致问题</li>
     *   <li>处理缓存更新的时间窗口</li>
     *   <li>确保所有节点都能收到删除通知</li>
     * </ul>
     *
     * @param cacheName 缓存空间名称
     * @param key 要删除的缓存键
     * @param consumer 回调函数，在第一次删除后立即执行，可用于执行后续业务逻辑
     */
    public static void remove(String cacheName, String key, Consumer<Object> consumer) {
        deCacheService.keyRemove(cacheName, key);
        consumer.accept(null);
        DelayQueueUtils.execute(IDUtils.randomID(16), () -> {
            deCacheService.keyRemove(cacheName, key);
        }, 1L);
    }

    /**
     * 从多个缓存空间删除同一个键（带回调和延迟删除）
     * <p>
     * 同时从多个缓存空间中删除相同的键，适用于需要清理跨多个缓存空间的相关数据场景。
     * 例如：用户登出时，需要同时清理会话缓存、权限缓存、菜单缓存等。
     * </p>
     *
     * <p>执行流程：</p>
     * <ol>
     *   <li>立即从所有指定的缓存空间删除该键</li>
     *   <li>执行回调函数</li>
     *   <li>延迟1秒后再次从所有缓存空间删除（确保一致性）</li>
     * </ol>
     *
     * @param cacheNames 缓存空间名称数组，可以包含多个缓存空间
     * @param key 要删除的缓存键（在所有缓存空间中都会被删除）
     * @param consumer 回调函数，在第一次删除后立即执行
     */
    public static void remove(String[] cacheNames, String key, Consumer<Object> consumer) {
        Arrays.stream(cacheNames).forEach(cacheName -> deCacheService.keyRemove(cacheName, key));
        consumer.accept(null);
        DelayQueueUtils.execute(IDUtils.randomID(16), () -> {
            Arrays.stream(cacheNames).forEach(cacheName -> deCacheService.keyRemove(cacheName, key));
        }, 1L);

    }

    /**
     * 批量删除缓存键（带回调和延迟删除）
     * <p>
     * 从指定的缓存空间中批量删除多个键，适用于需要一次性清理多个相关缓存的场景。
     * 例如：批量删除多个用户的缓存数据。
     * </p>
     *
     * <p>执行流程：</p>
     * <ol>
     *   <li>立即删除列表中的所有缓存键</li>
     *   <li>执行回调函数</li>
     *   <li>延迟1秒后再次批量删除（确保一致性）</li>
     * </ol>
     *
     * @param cacheName 缓存空间名称
     * @param keys 要删除的缓存键列表
     * @param consumer 回调函数，在第一次删除后立即执行
     */
    public static void remove(String cacheName, List<String> keys, Consumer<Object> consumer) {
        keys.forEach(key -> deCacheService.keyRemove(cacheName, key));
        consumer.accept(null);
        DelayQueueUtils.execute(IDUtils.randomID(16), () -> {
            keys.forEach(key -> deCacheService.keyRemove(cacheName, key));
        }, 1L);
    }

    /**
     * 从多个缓存空间批量删除多个键（带回调和延迟删除）
     * <p>
     * 这是最强大的批量删除方法，可以同时从多个缓存空间中删除多个键。
     * 适用于需要大规模清理缓存的场景，如：
     * - 系统重置时清理所有用户的多种缓存
     * - 批量删除多个组织的权限、菜单、配置等缓存
     * </p>
     *
     * <p>执行流程：</p>
     * <ol>
     *   <li>遍历所有缓存空间，立即删除所有指定的键</li>
     *   <li>执行回调函数</li>
     *   <li>延迟1秒后再次遍历所有缓存空间批量删除（确保一致性）</li>
     * </ol>
     *
     * <p><b>性能注意：</b></p>
     * <ul>
     *   <li>该方法的操作数 = cacheNames.length × keys.size()</li>
     *   <li>大量删除操作可能影响性能，建议分批处理</li>
     *   <li>建议在缓存空间数量和键数量都较小时使用</li>
     * </ul>
     *
     * @param cacheNames 缓存空间名称数组
     * @param keys 要删除的缓存键列表
     * @param consumer 回调函数，在第一次删除后立即执行
     */
    public static void remove(String[] cacheNames, List<String> keys, Consumer<Object> consumer) {
        Arrays.stream(cacheNames).forEach(cacheName -> {
            keys.forEach(key -> deCacheService.keyRemove(cacheName, key));
        });
        consumer.accept(null);
        DelayQueueUtils.execute(IDUtils.randomID(16), () -> {
            Arrays.stream(cacheNames).forEach(cacheName -> {
                keys.forEach(key -> deCacheService.keyRemove(cacheName, key));
            });
        }, 1L);
    }
}
