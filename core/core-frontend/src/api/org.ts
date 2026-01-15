import request from '@/config/axios'

/**
 * 组织管理相关 API
 * 用于组织架构的查询、创建、更新和删除操作
 */

// ==================== 组织查询操作 ====================

/**
 * 搜索组织（分页树形结构）
 * @param data 查询条件参数
 * @returns 组织树形列表数据
 */
export const searchApi = data => request.post({ url: '/org/page/tree', data })

/**
 * 检查资源是否存在
 * @param oid 组织 ID
 * @returns 资源是否存在
 */
export const resourceExistApi = oid => request.get({ url: '/org/resourceExist/' + oid })

// ==================== 组织管理操作 ====================

/**
 * 创建组织
 * @param data 组织信息
 * @returns 创建结果
 */
export const saveApi = data => request.post({ url: '/org/page/create', data })

/**
 * 更新组织信息
 * @param data 组织信息
 * @returns 更新结果
 */
export const updateApi = data => request.post({ url: '/org/page/edit', data })

/**
 * 删除组织
 * @param oid 组织 ID
 * @returns 删除结果
 */
export const deleteApi = oid => request.post({ url: '/org/page/delete/' + oid })
