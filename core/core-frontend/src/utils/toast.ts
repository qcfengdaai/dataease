/**
 * Toast 提示工具
 *
 * 功能说明：
 * 1. 统一的错误消息提示封装
 * 2. 基于 Element Plus 的 ElMessage 组件
 * 3. 快速显示错误提示信息
 *
 * 使用场景：
 * - API 请求失败提示
 * - 表单验证错误提示
 * - 操作失败反馈
 * - 异常错误提示
 *
 * 注意事项：
 * - 此工具默认显示错误类型消息（红色）
 * - 如需成功或警告提示，请直接使用 ElMessage
 *
 * @example
 * import toast from '@/utils/toast'
 *
 * // 显示错误提示
 * toast('操作失败，请重试')
 * toast('网络连接异常')
 */

import { ElMessage } from 'element-plus-secondary'

// ==================== 消息提示 ====================

/**
 * 显示错误提示消息
 *
 * 使用 Element Plus 的消息组件显示错误提示
 * 默认显示为错误类型（红色），持续 3 秒
 *
 * @param message - 提示消息内容，默认为空字符串
 *
 * @example
 * // 基本使用
 * toast('操作失败')
 *
 * // API 请求失败时使用
 * try {
 *   await apiCall()
 * } catch (error) {
 *   toast(error.message)
 * }
 */
export default function toast(message = '') {
  ElMessage.error(message)
}
