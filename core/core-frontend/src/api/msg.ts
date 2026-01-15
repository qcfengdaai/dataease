import request from '@/config/axios'

/**
 * 消息中心相关 API
 * 用于获取消息数量和消息管理
 */

/**
 * 获取消息统计数量
 * 用于获取当前用户的各类未读消息数量
 * @returns 消息统计信息，包含各类消息的数量
 */
export const msgCountApi = () => request.post({ url: '/msg-center/count', data: {} })
