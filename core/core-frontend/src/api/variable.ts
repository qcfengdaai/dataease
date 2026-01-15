/**
 * 系统变量管理相关 API
 *
 * 功能说明：
 * 1. 系统变量的增删改查
 * 2. 变量值的创建、编辑、删除
 * 3. 变量值与变量的关联管理
 * 4. 批量删除变量值
 */

import request from '@/config/axios'

// ==================== 变量管理 ====================

/**
 * 创建系统变量
 * @param data 变量信息
 * @returns 创建结果
 */
export const variableCreateApi = data => request.post({ url: '/sysVariable/create', data })

/**
 * 编辑系统变量
 * @param data 变量信息
 * @returns 编辑结果
 */
export const variableEditApi = data => request.post({ url: '/sysVariable/edit', data })

/**
 * 获取变量详情
 * @param id 变量 ID
 * @returns 变量详情
 */
export const variableDetailApi = id => request.get({ url: '/sysVariable/detail/' + id })

/**
 * 删除系统变量
 * @param id 变量 ID
 * @returns 删除结果
 */
export const variableDeletelApi = id => request.get({ url: '/sysVariable/delete/' + id })

/**
 * 搜索系统变量
 * @param data 搜索条件
 * @returns 变量列表
 */
export const searchVariableApi = async data => request.post({ url: '/sysVariable/query', data })

// ==================== 变量值管理 ====================

/**
 * 获取变量的已选值列表
 * @param page 页码
 * @param limit 每页数量
 * @param data 查询条件
 * @returns 变量值列表
 */
export const valueSelectedForVariableApi = (page: number, limit: number, data) =>
  request.post({ url: `/sysVariable/value/selected/${page}/${limit}`, data })

/**
 * 获取变量的所有值
 * @param id 变量 ID
 * @returns 变量值列表
 */
export const valueForVariable = id => request.get({ url: '/sysVariable/value/selected/' + id })

/**
 * 创建变量值
 * @param data 变量值信息
 * @returns 创建结果
 */
export const variableValueCreateApi = data =>
  request.post({ url: '/sysVariable/value/create', data })

/**
 * 编辑变量值
 * @param data 变量值信息
 * @returns 编辑结果
 */
export const variableValueEditApi = data => request.post({ url: '/sysVariable/value/edit', data })

/**
 * 删除变量值
 * @param id 变量值 ID
 * @returns 删除结果
 */
export const variableValueDeletelApi = id => request.get({ url: '/sysVariable/value/delete/' + id })

/**
 * 批量删除变量值
 * @param data 变量值 ID 列表
 * @returns 删除结果
 */
export const batchDelApi = data => request.post({ url: '/sysVariable/value/batchDel', data })
