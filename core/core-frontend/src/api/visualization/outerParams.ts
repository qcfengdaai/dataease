import request from '@/config/axios'

/**
 * 外部参数相关 API
 * 用于仪表板的外部参数配置和管理
 */

// ==================== 外部参数查询操作 ====================

/**
 * 根据可视化 ID 查询外部参数配置
 * @param dvId 数据可视化 ID
 * @returns 外部参数配置信息
 */
export function queryWithVisualizationId(dvId) {
  return request.get({
    url: '/outerParams/queryWithVisualizationId/' + dvId
  })
}

/**
 * 获取外部参数信息
 * 查询指定仪表板的详细外部参数配置
 * @param dvId 数据可视化 ID
 * @returns 外部参数详细信息
 */
export function getOuterParamsInfo(dvId) {
  return request.get({
    url: '/outerParams/getOuterParamsInfo/' + dvId,
    method: 'get',
    loading: false
  })
}

// ==================== 外部参数管理操作 ====================

/**
 * 更新外部参数配置
 * @param requestInfo 外部参数配置信息
 * @returns 更新结果
 */
export function updateOuterParamsSet(requestInfo) {
  return request.post({
    url: '/outerParams/updateOuterParamsSet',
    data: requestInfo,
    loading: true
  })
}
