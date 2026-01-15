/**
 * 全局事件总线
 *
 * 功能说明：
 * 1. 提供跨组件通信能力
 * 2. 使用 mitt 库实现发布-订阅模式
 * 3. 支持类型安全的事件传递
 *
 * 使用场景：
 * - 跨层级组件通信
 * - 兄弟组件间通信
 * - 非关联组件间通信
 * - 全局状态变化通知
 *
 * 注意事项：
 * - 事件名称建议使用常量定义，避免硬编码
 * - 组件销毁时记得取消事件监听，避免内存泄漏
 * - 不要用于父子组件通信（应使用 props/emit）
 *
 * @example
 * // 发送事件
 * import eventBus from '@/utils/eventBus'
 * eventBus.emit('user-login', { username: 'admin' })
 *
 * // 监听事件
 * eventBus.on('user-login', (data) => {
 *   console.log(data.username) // 'admin'
 * })
 *
 * // 取消监听
 * eventBus.off('user-login')
 *
 * // 清除所有监听
 * eventBus.all.clear()
 */

import mitt from 'mitt'

// ==================== 创建事件总线实例 ====================

/**
 * Mitt 事件总线实例
 *
 * 提供以下 API：
 * - on(type, handler): 注册事件监听
 * - off(type, handler): 移除事件监听
 * - emit(type, event): 触发事件
 * - all.clear(): 清除所有事件监听
 */
const emitter = mitt()

// ==================== 导出 ====================

/**
 * 导出事件总线实例
 * 用于全局事件的监听和触发
 */
export default emitter
