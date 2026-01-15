import request from '@/config/axios'

/**
 * AI 组件相关 API
 * 用于获取 AI 基础配置信息
 */

/**
 * 查找 AI 基础参数配置
 * 用于获取 AI 服务的目标 URL 等配置信息
 * @returns AI 基础参数配置信息
 */
export const findBaseParams = async () => request.get({ url: '/aiBase/findTargetUrl' })
