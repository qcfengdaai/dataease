package io.dataease.dds;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 动态上下文持有者
 * 基于ThreadLocal实现线程安全的数据源上下文管理
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>使用ThreadLocal确保每个线程拥有独立的数据源上下文</li>
 *   <li>采用双端队列（Deque）支持数据源的嵌套切换</li>
 *   <li>提供栈式的push/pop操作，支持数据源的临时切换和恢复</li>
 *   <li>自动管理ThreadLocal生命周期，防止内存泄漏</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>在拦截器中设置当前请求的目标数据源</li>
 *   <li>在业务方法中临时切换到其他数据源</li>
 *   <li>支持嵌套的事务场景下的数据源管理</li>
 * </ul>
 */
public class DynamicContextHolder {

    /**
     * 线程本地变量，存储数据源名称的双端队列
     * 使用Deque支持数据源的嵌套切换，类似方法调用栈
     */
    private static final ThreadLocal<Deque<String>> CONTEXT_HOLDER = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            // 初始化时创建一个空的ArrayDeque
            return new ArrayDeque();
        }
    };

    /**
     * 查看当前数据源键值
     * 获取栈顶的数据源名称，但不移除
     *
     * @return 当前线程上下文中的数据源键值，如果栈为空则返回null
     */
    public static String peek() {
        return CONTEXT_HOLDER.get().peek();
    }

    /**
     * 推入新的数据源键值
     * 将数据源名称压入栈顶，成为当前活跃的数据源
     *
     * @param dataSource 要设置的数据源名称
     */
    public static void push(String dataSource) {
        CONTEXT_HOLDER.get().push(dataSource);
    }

    /**
     * 移除当前数据源键值
     * 从栈顶移除一个数据源名称，恢复到上一个数据源
     * 如果栈变为空，则清理ThreadLocal以防止内存泄漏
     */
    public static void poll() {
        Deque<String> deque = CONTEXT_HOLDER.get();
        // 移除栈顶元素
        deque.poll();
        // 如果栈为空，清理ThreadLocal变量
        if (deque.isEmpty()) {
            CONTEXT_HOLDER.remove();
        }
    }
}
