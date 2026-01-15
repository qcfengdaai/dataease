/**
 * 唯一标识符生成工具
 *
 * 功能说明：
 * 1. 生成全局唯一的标识符（GUID/UUID）
 * 2. 用于组件、数据、资源等唯一标识
 * 3. 基于 Snowflake 算法思想实现
 *
 * 使用场景：
 * - 生成组件唯一 ID
 * - 生成数据记录标识
 * - 生成临时资源标识
 * - 前端生成唯一键值
 *
 * @example
 * import generateID from '@/utils/generateID'
 *
 * const componentId = generateID()  // '1a2b3c4d-5e6f-7g8h-9i0j-1k2l3m4n5o6p'
 */

import { guid } from '@/views/visualized/data/dataset/form/util.js'

// ==================== 唯一 ID 生成 ====================

/**
 * 生成唯一标识符
 *
 * 使用 GUID 算法生成全局唯一的字符串标识
 * 确保在分布式环境下也能保持唯一性
 *
 * @returns 唯一标识符字符串
 *
 * @example
 * generateID()  // 返回类似 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx' 的 GUID
 */
export const generateID = () => {
  return guid()
}

// ==================== 默认导出 ====================

export default generateID
