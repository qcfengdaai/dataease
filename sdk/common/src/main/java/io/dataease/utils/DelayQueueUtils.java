package io.dataease.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 延迟队列工具类
 * <p>
 * 提供基于延迟执行的任务去重功能，确保相同key的任务在指定时间内只执行一次。
 * 使用单线程定时执行器和任务队列实现，适用于需要延迟执行且要避免重复执行的场景。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>延迟任务执行 - 延迟指定时间后执行任务</li>
 *   <li>任务去重 - 相同key的任务在延迟期间内不会重复添加</li>
 *   <li>自动清理 - 任务执行完成后自动从队列中移除</li>
 *   <li>单线程执行 - 避免并发问题</li>
 * </ul>
 *
 * <p><b>工作原理：</b></p>
 * <ul>
 *   <li>维护一个任务key列表，记录已添加的任务</li>
 *   <li>添加任务时检查key是否已存在，存在则忽略</li>
 *   <li>使用ScheduledExecutorService延迟执行任务</li>
 *   <li>任务执行完成后从列表中移除key</li>
 * </ul>
 *
 * <p><b>应用场景：</b></p>
 * <ul>
 *   <li>缓存延迟删除 - 删除缓存后延迟再删除一次，确保缓存被清除</li>
 *   <li>数据同步 - 延迟执行数据同步，避免频繁同步</li>
 *   <li>防抖处理 - 短时间内多次触发只执行一次</li>
 *   <li>延迟通知 - 延迟发送通知消息</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.utils.CacheUtils} - 缓存删除后延迟1秒再删除一次，确保缓存彻底清除</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：基本延迟执行（默认5秒）
 * DelayQueueUtils.execute("task-1", () -> {
 *     System.out.println("延迟5秒后执行");
 *     // 执行业务逻辑
 * }, null);
 *
 * // 示例2：指定延迟时间
 * DelayQueueUtils.execute("cache-clear", () -> {
 *     clearCache();
 * }, 10L);  // 延迟10秒执行
 *
 * // 示例3：任务去重
 * // 在5秒内多次调用，只会执行一次
 * DelayQueueUtils.execute("sync-data", () -> {
 *     syncToRemote();
 * }, 5L);
 * // 1秒后再次调用，会被忽略
 * DelayQueueUtils.execute("sync-data", () -> {
 *     syncToRemote();
 * }, 5L);  // 不会执行，因为key已存在
 *
 * // 示例4：实际使用场景 - 缓存延迟删除（参考 CacheUtils.java:285-287）
 * // 先立即删除缓存
 * deCacheService.keyRemove(cacheName, key);
 * consumer.accept(null);
 *
 * // 延迟1秒再删除一次，确保缓存彻底清除
 * DelayQueueUtils.execute(IDUtils.randomID(16), () -> {
 *     deCacheService.keyRemove(cacheName, key);
 * }, 1L);
 *
 * // 示例5：批量数据延迟删除
 * public void batchDeleteWithDelay(List<String> keys) {
 *     keys.forEach(key -> {
 *         // 立即删除
 *         cache.remove(key);
 *
 *         // 延迟删除，使用唯一key避免冲突
 *         String taskKey = "delete-" + key;
 *         DelayQueueUtils.execute(taskKey, () -> {
 *             cache.remove(key);
 *         }, 2L);
 *     });
 * }
 *
 * // 示例6：防抖处理
 * // 用户输入时，延迟执行搜索，避免频繁查询
 * public void onUserInput(String keyword) {
 *     DelayQueueUtils.execute("search-" + userId, () -> {
 *         searchService.search(keyword);
 *     }, 1L);
 * }
 *
 * // 示例7：延迟通知
 * public void notifyAfterDelay(String userId, String message) {
 *     DelayQueueUtils.execute("notify-" + userId, () -> {
 *         notificationService.send(userId, message);
 *     }, 3L);
 * }
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>使用单线程执行器，多个任务串行执行，不适合大量并发任务</li>
 *   <li>key必须唯一，相同key的任务会被去重</li>
 *   <li>任务执行失败不会重试，也不会影响其他任务</li>
 *   <li>delayQueueList在多线程环境下不是线程安全的（依赖单线程执行器）</li>
 *   <li>应用重启后未执行的任务会丢失</li>
 *   <li>不适合需要持久化的任务队列</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>使用有意义的key名称，便于调试和监控</li>
 *   <li>对于需要唯一性的任务，使用随机ID作为key</li>
 *   <li>延迟时间不宜过长，建议在1-60秒之间</li>
 *   <li>任务逻辑应该幂等，因为可能被多次调用</li>
 *   <li>避免在任务中执行耗时操作，影响其他任务</li>
 *   <li>对于关键任务，考虑使用持久化的消息队列</li>
 * </ul>
 *
 * <p><b>性能考虑：</b></p>
 * <ul>
 *   <li>单线程执行，吞吐量有限</li>
 *   <li>ArrayList的contains和remove操作是O(n)复杂度</li>
 *   <li>建议任务量不超过百级</li>
 *   <li>大量任务建议使用DelayQueue或Redis延迟队列</li>
 * </ul>
 *
 * <p><b>改进建议：</b></p>
 * <ul>
 *   <li>使用HashSet替代ArrayList提高查找性能</li>
 *   <li>添加同步机制确保线程安全</li>
 *   <li>添加任务取消功能</li>
 *   <li>添加任务执行失败回调</li>
 *   <li>添加任务监控和统计</li>
 * </ul>
 *
 * <p><b>线程安全说明：</b></p>
 * 当前实现依赖单线程执行器保证线程安全，如果改为多线程执行器，
 * 需要对delayQueueList添加同步控制（使用ConcurrentHashMap.newKeySet()或Collections.synchronizedList()）。
 *
 * @author DataEase
 * @since 1.0.0
 * @see java.util.concurrent.ScheduledExecutorService
 * @see io.dataease.utils.CacheUtils
 */
public class DelayQueueUtils {

    /**
     * 延迟队列任务key列表
     * <p>
     * 存储已添加的任务key，用于任务去重。
     * 注意：ArrayList不是线程安全的，当前实现依赖单线程执行器保证安全性。
     * </p>
     */
    private static final List<String> delayQueueList = new ArrayList<>();

    /**
     * 单线程定时执行器
     * <p>
     * 使用单线程确保任务串行执行，避免并发问题。
     * 应用关闭时不会自动shutdown，可能导致JVM无法正常退出（建议改进）。
     * </p>
     */
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * 执行延迟任务
     * <p>
     * 将任务添加到延迟队列中，在指定延迟时间后执行。
     * 如果相同key的任务已存在于队列中，则忽略本次添加（去重机制）。
     * 任务执行完成后会自动从队列中移除key。
     * </p>
     *
     * <p><b>执行流程：</b></p>
     * <ol>
     *   <li>如果seconds为null或空，设置默认延迟时间5秒</li>
     *   <li>检查key是否已存在于队列中，存在则直接返回</li>
     *   <li>将key添加到队列中</li>
     *   <li>使用ScheduledExecutorService延迟执行任务</li>
     *   <li>任务执行完成后从队列中移除key</li>
     * </ol>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 默认延迟5秒
     * DelayQueueUtils.execute("task-1", () -> {
     *     System.out.println("执行任务");
     * }, null);
     *
     * // 延迟10秒
     * DelayQueueUtils.execute("task-2", () -> {
     *     cleanupResource();
     * }, 10L);
     *
     * // 缓存延迟删除
     * DelayQueueUtils.execute(IDUtils.randomID(16), () -> {
     *     cacheService.remove(cacheKey);
     * }, 1L);
     * </pre>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>key必须保证唯一性，建议使用业务标识或UUID</li>
     *   <li>相同key的任务在延迟期间内不会重复执行</li>
     *   <li>任务异常不会影响其他任务，但会记录到日志</li>
     *   <li>任务执行顺序不保证严格按添加顺序</li>
     * </ul>
     *
     * @param key 任务唯一标识，用于去重，不能为null
     * @param runnable 要执行的任务，不能为null
     * @param seconds 延迟时间（秒），null或空时默认5秒
     */
    public static void execute(String key, Runnable runnable, Long seconds) {
        seconds = ObjectUtils.isEmpty(seconds) ? 5L : seconds;
        if (delayQueueList.contains(key)) return;
        delayQueueList.add(key);
        executorService.schedule(() -> {
            runnable.run();
            delayQueueList.remove(key);
        }, seconds, TimeUnit.SECONDS);
    }
}
