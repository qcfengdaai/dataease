import request from '@/config/axios'

/**
 * PDF 模板相关 API
 * 用于 PDF 导出模板的管理
 */

// ==================== 模板查询操作 ====================

/**
 * 查询所有 PDF 模板
 * 获取系统中所有可用的 PDF 导出模板
 * @returns PDF 模板列表
 */
export function queryAll() {
  return request.get({
    url: '/pdf-template/queryAll',
    loading: false
  })
}
