import request from '@/config/axios'

/**
 * 可视化跳转相关 API
 * 用于仪表板组件之间的跳转配置和管理
 */

// ==================== 跳转查询操作 ====================

/**
 * 根据视图 ID 获取表字段
 * 获取可用于跳转配置的字段列表
 * @param viewId 视图 ID
 * @returns 表字段列表
 */
export function getTableFieldWithViewId(viewId) {
  return request.get({
    url: '/linkJump/getTableFieldWithViewId/' + viewId
  })
}

/**
 * 根据 DV ID 和视图 ID 查询跳转配置
 * @param dvId 数据可视化 ID
 * @param viewId 视图 ID
 * @returns 跳转配置信息
 */
export function queryWithViewId(dvId, viewId) {
  return request.get({
    url: '/linkJump/queryWithViewId/' + dvId + '/' + viewId
  })
}

/**
 * 查询可视化跳转信息
 * 获取整个仪表板的跳转配置
 * @param dvId 数据可视化 ID
 * @param resourceTable 资源表类型，默认为 'snapshot'
 * @returns 跳转信息列表
 */
export function queryVisualizationJumpInfo(dvId, resourceTable = 'snapshot') {
  return request.get({
    url: '/linkJump/queryVisualizationJumpInfo/' + dvId + '/' + resourceTable,
    loading: false
  })
}

/**
 * 查询目标可视化跳转信息
 * 获取跳转目标仪表板的跳转配置信息
 * @param requestInfo 跳转信息参数
 * @returns 目标跳转信息
 */
export function queryTargetVisualizationJumpInfo(requestInfo) {
  return request.post({
    url: '/linkJump/queryTargetVisualizationJumpInfo',
    data: requestInfo,
    loading: true
  })
}

/**
 * 查询视图表详情列表
 * @param dvId 数据可视化 ID
 * @returns 表详情列表
 */
export function viewTableDetailList(dvId) {
  return request.get({
    url: '/linkJump/viewTableDetailList/' + dvId,
    loading: false
  })
}

// ==================== 跳转管理操作 ====================

/**
 * 更新跳转配置
 * @param requestInfo 跳转配置信息
 * @returns 更新结果
 */
export function updateJumpSet(requestInfo) {
  return request.post({
    url: '/linkJump/updateJumpSet',
    data: requestInfo,
    loading: true
  })
}

/**
 * 更新跳转激活状态
 * 启用或禁用跳转配置
 * @param requestInfo 跳转状态信息
 * @returns 更新结果
 */
export function updateJumpSetActive(requestInfo) {
  return request.post({
    url: '/linkJump/updateJumpSetActive',
    data: requestInfo,
    loading: true
  })
}

/**
 * 删除跳转配置
 * @param requestInfo 跳转删除信息
 * @returns 删除结果
 */
export function removeJumpSet(requestInfo) {
  return request.post({
    url: '/linkJump/removeJumpSet',
    data: requestInfo,
    loading: true
  })
}
