import request from '@/config/axios'

/**
 * 可视化联动相关 API
 * 用于仪表板组件之间的联动配置和管理
 */

// ==================== 联动查询操作 ====================

/**
 * 获取视图联动集合
 * 查询指定视图的联动配置信息
 * @param data 查询条件
 * @returns 联动配置集合
 */
export const getViewLinkageGather = data =>
  request.post({ url: '/linkage/getViewLinkageGather', data })

/**
 * 获取视图联动集合数组
 * 查询多个视图的联动配置信息
 * @param data 查询条件
 * @returns 联动配置数组
 */
export const getViewLinkageGatherArray = data =>
  request.post({ url: '/linkage/getViewLinkageGatherArray', data })

/**
 * 获取仪表板所有联动信息
 * 查询整个仪表板的联动配置
 * @param dvId 数据可视化 ID
 * @param resourceTable 资源表类型，默认为 'snapshot'
 * @returns 联动信息列表
 */
export const getPanelAllLinkageInfo = (dvId, resourceTable = 'snapshot') =>
  request.get({ url: '/linkage/getVisualizationAllLinkageInfo/' + dvId + '/' + resourceTable })

// ==================== 联动管理操作 ====================

/**
 * 保存联动配置
 * @param data 联动配置数据
 * @returns 保存结果
 */
export const saveLinkage = data => request.post({ url: '/linkage/saveLinkage', data })

/**
 * 更新联动激活状态
 * @param data 联动状态数据
 * @returns 更新结果
 */
export const updateLinkageActive = data =>
  request.post({ url: '/linkage/updateLinkageActive', data })

/**
 * 删除联动配置
 * @param data 联动删除数据
 * @returns 删除结果
 */
export const removeLinkage = data => request.post({ url: '/linkage/removeLinkage', data })
