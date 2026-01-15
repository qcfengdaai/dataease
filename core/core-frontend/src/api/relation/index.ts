import request from '@/config/axios'

/**
 * 资源关系管理相关 API
 * 用于查询数据源、数据集、可视化资源之间的关系
 * 以及检查资源权限
 */

// ==================== 关系查询操作 ====================

/**
 * 获取数据源关系信息
 * @param id 数据源 ID
 * @returns 数据源关系信息
 */
export function getDatasourceRelationship(id) {
  return request.post({
    url: `/relation/datasource/${id}`
  })
}

/**
 * 获取数据集关系信息
 * @param id 数据集 ID
 * @returns 数据集关系信息
 */
export function getDatasetRelationship(id) {
  return request.post({
    url: `/relation/dataset/${id}`
  })
}

/**
 * 获取数据可视化资源关系信息
 * @param id 数据可视化 ID
 * @returns 数据可视化关系信息
 */
export function getPanelRelationship(id) {
  return request.post({
    url: `/relation/dv/${id}`
  })
}

// ==================== 权限检查操作 ====================

/**
 * 检查资源权限
 * @param id 资源 ID
 * @returns 权限检查结果
 */
export function resourceCheckPermission(id) {
  return request.post({
    url: `/resource/checkPermission/${id}`
  })
}
