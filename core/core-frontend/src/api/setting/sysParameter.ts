import request from '@/config/axios'

/**
 * 系统参数相关 API
 * 用于在线地图密钥等系统参数的管理
 */

// ==================== 地图密钥查询操作 ====================

/**
 * 查询在线地图密钥
 * 获取所有类型的在线地图配置
 * @returns 地图密钥配置信息
 */
export const queryMapKeyApi = () => request.get({ url: '/sysParameter/queryOnlineMap' })

/**
 * 根据类型查询在线地图密钥
 * @param type 地图类型（如高德、百度等）
 * @returns 指定类型的地图密钥配置
 */
export const queryMapKeyApiByType = (type: string) =>
  request.get({ url: `/sysParameter/queryOnlineMap/${type}` })

// ==================== 地图密钥管理操作 ====================

/**
 * 保存在线地图密钥
 * @param data 地图密钥配置信息
 * @returns 保存结果
 */
export const saveMapKeyApi = data => request.post({ url: '/sysParameter/saveOnlineMap', data })
