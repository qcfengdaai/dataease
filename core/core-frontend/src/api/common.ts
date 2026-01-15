/**
 * 公共 API 接口
 *
 * 功能说明：
 * 1. 获取用户权限路由
 * 2. 获取系统默认设置
 */

import request from '@/config/axios'

// ==================== 权限路由 ====================

/**
 * 获取用户权限路由
 * 根据当前登录用户的权限，返回可访问的路由列表
 * @returns 用户可访问的路由配置
 */
export const getRoleRouters = async (): Promise<Array<AppCustomRouteRecordRaw>> => {
  return request.get({ url: '/menu/query' }).then(res => {
    return res?.data
  })
}

// ==================== 系统设置 ====================

/**
 * 获取系统默认设置
 * 获取系统的默认配置参数，如树形排序方式、展开方式等
 * @returns 系统默认设置
 */
export const getDefaultSettings = async (): Promise<IResponse> => {
  return request.get({ url: '/sysParameter/defaultSettings' }).then(res => {
    return res?.data
  })
}
