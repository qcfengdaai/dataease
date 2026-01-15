/**
 * URL 处理工具
 *
 * 功能说明：
 * 1. 处理嵌入式模式下的 URL 路径
 * 2. 为相对路径添加基础 URL 前缀
 * 3. 支持将应用嵌入到其他系统中
 *
 * 使用场景：
 * - iframe 嵌入式部署
 * - 应用集成到第三方平台
 * - 跨域场景下的资源路径处理
 *
 * @example
 * import { formatDataEaseBi } from '@/utils/url'
 *
 * // 嵌入式模式下会添加 baseUrl 前缀
 * const url = formatDataEaseBi('/api/chart/list')
 */

import { useEmbedded } from '@/store/modules/embedded'

const embeddedStore = useEmbedded()

// ==================== URL 格式化 ====================

/**
 * 格式化 DataEase BI 的 URL
 *
 * 在嵌入式模式下，为相对路径添加基础 URL 前缀
 * 非嵌入式模式下直接返回原 URL
 *
 * @param url - 原始 URL 路径
 * @returns 格式化后的完整 URL
 *
 * @example
 * // 非嵌入式模式
 * formatDataEaseBi('/api/chart/list')  // '/api/chart/list'
 *
 * // 嵌入式模式（baseUrl = 'https://example.com/embedded'）
 * formatDataEaseBi('/api/chart/list')  // 'https://example.com/embedded/api/chart/list'
 */
export const formatDataEaseBi = (url: string) => {
  return embeddedStore.baseUrl ? `${embeddedStore.baseUrl}${url}` : url
}
