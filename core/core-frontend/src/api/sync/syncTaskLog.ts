import request from '@/config/axios'

/**
 * 数据同步任务日志相关 API
 * 用于查询、管理和监控同步任务执行日志
 */

// ==================== 日志查询操作 ====================

/**
 * 分页查询任务日志列表
 * @param current 当前页码
 * @param size 每页数量
 * @param data 查询条件
 * @returns 任务日志分页列表
 */
export const getTaskLogListApi = (current: number, size: number, data: any) => {
  return request.post({
    url: `/sync/task/log/pager/${current}/${size}`,
    data: data
  })
}

/**
 * 获取任务日志详情
 * 支持按行号获取日志内容，用于大日志文件的分页加载
 * @param logId 日志 ID
 * @param fromLineNum 起始行号
 * @returns 日志详情内容
 */
export const getTaskLogDetailApi = (logId: string, fromLineNum: number) => {
  return request.get({ url: `/sync/task/log/detail/${logId}/${fromLineNum}` })
}

// ==================== 日志管理操作 ====================

/**
 * 删除任务日志
 * @param logId 日志 ID
 * @returns 删除结果
 */
export const removeApi = (logId: string) => {
  return request.post({ url: `/sync/task/log/delete/${logId}` })
}

/**
 * 清空任务日志
 * 根据条件批量清理历史日志
 * @param clearData 清理条件参数
 * @returns 清理结果
 */
export const clear = (clearData: {}) => {
  return request.post({ url: `/sync/task/log/clear`, data: clearData })
}

// ==================== 日志控制操作 ====================

/**
 * 终止正在执行的任务
 * @param logId 日志 ID
 * @returns 终止结果
 */
export const terminationTaskApi = (logId: string) => {
  return request.post({ url: `/sync/task/log/terminationTask/${logId}`, data: {} })
}
