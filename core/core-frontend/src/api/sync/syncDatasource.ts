import request from '@/config/axios'

/**
 * 数据同步数据源相关 API
 * 用于数据同步任务中源数据源和目标数据源的管理
 */

// ==================== 分页查询操作 ====================

/**
 * 分页查询源数据源列表
 * @param page 当前页码
 * @param limit 每页数量
 * @param data 查询条件参数
 * @returns 源数据源分页列表
 */
export const sourceDsPageApi = (page: number, limit: number, data) => {
  return request.post({ url: `/sync/datasource/source/pager/${page}/${limit}`, data })
}

/**
 * 分页查询目标数据源列表
 * @param page 当前页码
 * @param limit 每页数量
 * @param data 查询条件参数
 * @returns 目标数据源分页列表
 */
export const targetDsPageApi = (page: number, limit: number, data) => {
  return request.post({ url: `/sync/datasource/target/pager/${page}/${limit}`, data })
}

// ==================== 数据源查询操作 ====================

/**
 * 获取指定类型的最新使用数据源
 * @param sourceType 数据源类型
 * @returns 最新使用的数据源信息
 */
export const latestUseApi = (sourceType: string) => {
  return request.post({ url: `/sync/datasource/latestUse/${sourceType}`, data: {} })
}

/**
 * 根据 ID 获取数据源详情
 * @param id 数据源 ID
 * @returns 数据源详情信息
 */
export const getByIdApi = (id: string) => {
  return request.get({ url: `/sync/datasource/get/${id}` })
}

/**
 * 根据 ID 验证数据源连接
 * @param id 数据源 ID
 * @returns 验证结果
 */
export const validateByIdApi = (id: string) => {
  return request.get({ url: `/sync/datasource/validate/${id}` })
}

/**
 * 获取源数据库字段集合以及目标数据库数据类型集合
 * @param data 查询参数，包含数据源 ID 等信息
 * @returns 字段集合和数据类型信息
 */
export const getFieldListApi = data => {
  return request.post({ url: `/sync/datasource/fields`, data })
}

/**
 * 获取数据库 Schema 信息
 * @param data 查询参数
 * @returns Schema 信息
 */
export const getSchemaApi = data => {
  return request.post({ url: '/sync/datasource/getSchema', data })
}

// ==================== 数据源管理操作 ====================

/**
 * 验证数据源连接
 * @param data 数据源连接信息
 * @returns 验证结果
 */
export const validateApi = data => {
  return request.post({ url: '/sync/datasource/validate', data })
}

/**
 * 保存数据源
 * @param data 数据源信息
 * @returns 保存结果
 */
export const saveApi = data => {
  return request.post({ url: '/sync/datasource/save', data })
}

/**
 * 更新数据源信息
 * @param data 数据源信息
 * @returns 更新结果
 */
export const updateApi = data => {
  return request.post({ url: '/sync/datasource/update', data })
}

/**
 * 根据 ID 删除数据源
 * @param id 数据源 ID
 * @returns 删除结果
 */
export const deleteByIdApi = (id: string) => {
  return request.post({ url: `/sync/datasource/delete/${id}` })
}

/**
 * 批量删除数据源
 * @param ids 数据源 ID 数组
 * @returns 删除结果
 */
export const batchDelApi = (ids: string[]) => {
  return request.post({ url: `/sync/datasource/batchDel`, data: ids })
}
