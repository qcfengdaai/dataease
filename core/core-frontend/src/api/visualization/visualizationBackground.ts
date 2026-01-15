import request from '@/config/axios'

/**
 * 可视化背景相关 API
 * 用于仪表板背景图片和配置的管理
 */

// ==================== 背景查询操作 ====================

/**
 * 查询所有可视化背景
 * 获取系统中所有可用的背景图片和配置
 * @returns 背景列表
 */
export const queryVisualizationBackground = () =>
  request.get({ url: '/visualizationBackground/findAll' })
