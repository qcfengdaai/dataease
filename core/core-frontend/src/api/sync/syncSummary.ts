import request from '@/config/axios'

/**
 * 数据同步汇总统计相关 API
 * 用于获取同步任务、数据源、日志等资源的统计信息
 */

/**
 * 资源统计信息接口
 */
interface IResourceCount {
  /** 同步任务数量 */
  jobCount: number
  /** 数据源数量 */
  datasourceCount: number
  /** 任务日志数量 */
  jobLogCount: number
}

// ==================== 统计查询操作 ====================

/**
 * 获取资源统计数量
 * 返回同步任务、数据源、任务日志的数量统计
 * @returns 资源统计信息
 */
export const getResourceCount = () => {
  return request
    .get({
      url: 'sync/summary/resourceCount',
      method: 'get'
    })
    .then(res => {
      return res.data as IResourceCount
    })
}

/**
 * 获取任务日志折线图信息
 * 用于展示任务执行趋势的图表数据
 * @returns 折线图数据
 */
export const getJobLogLienChartInfo = () => {
  return request.post({
    url: '/sync/summary/logChartData',
    method: 'post',
    data: ''
  })
}
