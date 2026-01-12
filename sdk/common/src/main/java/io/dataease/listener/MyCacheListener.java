package io.dataease.listener;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DataEase自定义缓存事件监听器
 * <p>
 * 实现EhCache的CacheEventListener接口，用于监听和记录缓存操作事件。
 * 主要功能包括监听缓存的创建、更新、删除、过期等事件，并将这些事件记录到日志中。
 * 这有助于缓存的调试、性能分析和问题排查。
 * </p>
 *
 * <p><b>监听的缓存事件类型：</b></p>
 * <ul>
 *   <li>CREATED - 缓存项被创建</li>
 *   <li>UPDATED - 缓存项被更新</li>
 *   <li>REMOVED - 缓存项被删除</li>
 *   <li>EXPIRED - 缓存项过期</li>
 *   <li>EVICTED - 缓存项被驱逐</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 */
public class MyCacheListener implements CacheEventListener<String, Object> {

    /** 日志记录器，用于记录缓存事件信息 */
    private static final Logger log = LoggerFactory.getLogger(MyCacheListener.class);

    /**
     * 构造函数，初始化缓存事件监听器
     * <p>
     * 在监听器创建时记录初始化日志，用于跟踪监听器的生命周期。
     * </p>
     */
    public MyCacheListener() {
        // 记录监听器初始化日志
        log.info("MyCacheListener: init");
    }

    /**
     * 缓存事件处理方法
     * <p>
     * 当缓存发生任何事件时，此方法会被自动调用。
     * 记录事件类型、缓存键和新值到日志中，便于监控缓存的使用情况和调试。
     * </p>
     *
     * @param cacheEvent 缓存事件对象，包含事件类型、缓存键、旧值、新值等信息
     *                   - getType(): 事件类型（CREATED、UPDATED、REMOVED等）
     *                   - getKey(): 缓存键
     *                   - getOldValue(): 旧值
     *                   - getNewValue(): 新值
     */
    @Override
    public void onEvent(CacheEvent<? extends String, ?> cacheEvent) {
        // 记录缓存事件：事件类型 + 缓存键 + 新值
        log.info("'{}' : [{}] --> {}", cacheEvent.getType(), cacheEvent.getKey(), cacheEvent.getNewValue());
    }
}
