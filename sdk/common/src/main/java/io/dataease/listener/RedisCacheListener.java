package io.dataease.listener;

import io.dataease.utils.LogUtil;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Redis缓存启动监听器
 * <p>
 * 用于监听Spring Boot应用启动完成事件，在应用启动后执行缓存清理工作。
 * 主要功能是清理DataEase v2版本的旧缓存数据，确保系统升级或重启时缓存的一致性。
 * </p>
 *
 * <p><b>功能特点：</b></p>
 * <ul>
 *   <li>仅在Redis作为缓存类型时生效</li>
 *   <li>在应用完全启动后执行（Order=100）</li>
 *   <li>自动清理以"de_v2_"为前缀的缓存键</li>
 *   <li>支持批量扫描和删除操作</li>
 * </ul>
 *
 * <p><b>适用场景：</b></p>
 * <ul>
 *   <li>系统升级后的缓存清理</li>
 *   <li>版本切换时的数据一致性保障</li>
 *   <li>重新部署后的缓存初始化</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 */
@ConditionalOnExpression("'${spring.cache.type}'.equals('redis')")
@Component
@Order(100)
public class RedisCacheListener implements ApplicationListener<ApplicationReadyEvent> {

    /** Redis操作模板，用于执行 Redis 的各种操作 */
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 应用启动完成事件处理方法
     * <p>
     * 当Spring Boot应用完全启动并准备就绪后，此方法会被自动调用。
     * 主要用于清理DataEase v2版本的旧缓存数据，确保系统的数据一致性。
     * 如果清理过程中发生异常，会记录错误日志但不会影响应用启动。
     * </p>
     *
     * @param event Spring Boot应用启动完成事件
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            // 删除所有包含"de_v2_"的Redis缓存键
            deleteKeysContainingString(redisTemplate, "de_v2_");
        } catch (Exception e) {
            // 记录异常信息，但不中断应用启动
            LogUtil.error(e.getMessage(), e);
        }
    }


    /**
     * 批量删除包含指定字符串的Redis键
     * <p>
     * 使用Redis的SCAN命令逐个扫描所有匹配的键，然后批量删除。
     * 这种方式比KEYS命令更安全，不会阻塞Redis服务器。
     * 每次扫描1000个键，避免一次性加载过多数据到内存。
     * </p>
     *
     * @param redisTemplate Redis操作模板对象
     * @param searchString 要搜索和删除的字符串模式（会在前后加上通配符*）
     */
    public void deleteKeysContainingString(RedisTemplate<String, String> redisTemplate, String searchString) {
        // 配置扫描选项：匹配包含指定字符串的键，每次扫描1000个
        ScanOptions scanOptions = ScanOptions.scanOptions().match("*" + searchString + "*").count(1000).build();

        // 获取Redis连接并执行扫描操作
        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
                .getConnection()
                .scan(scanOptions);

        // 收集所有需要删除的键
        List<byte[]> keysToDelete = new ArrayList<>();
        while (cursor.hasNext()) {
            keysToDelete.add(cursor.next());
        }

        // 如果找到需要删除的键，则进行批量删除
        if (!keysToDelete.isEmpty()) {
            // 将字节数组转换为字符串列表
            List<String> keys = new ArrayList<>(keysToDelete.size());
            for (byte[] key : keysToDelete) {
                keys.add(new String(key, StandardCharsets.UTF_8));
            }
            // 执行批量删除操作
            redisTemplate.delete(keys);
        }
    }
}
