import request from '@/config/axios'
import type { BusiTreeRequest } from '@/models/tree/TreeNode'
import { originNameHandleWithArr } from '@/utils/CalculateFields'
import { cloneDeep } from 'lodash-es'

/**
 * 数据可视化相关 API
 * 用于仪表板、数据大屏等可视化资源的管理
 */

// ==================== 类型定义 ====================

/**
 * 资源或文件夹信息接口
 */
export interface ResourceOrFolder {
  /** 名称 */
  name: string
  /** ID */
  id?: number | string
  /** 父 ID */
  pid?: number | string
  /** 节点类型: folder 文件夹, leaf 叶子节点 */
  nodeType: 'folder' | 'leaf'
  /** 类型 */
  type: string
  /** 是否移动布局 */
  mobileLayout: boolean
  /** 状态 */
  status: boolean
}

/**
 * 面板信息接口
 */
export interface Panel {
  /** 名称 */
  name: string
  /** 类型 */
  type: string
  /** 更新时间 */
  updateTime: number
  /** 创建者 */
  createBy: string
  /** 更新者 */
  updateBy: string
}

// ==================== 资源查询操作 ====================

/**
 * 查询资源树
 * @param data 查询条件
 * @returns 资源树结构数据
 */
export const queryTreeApi = async (data: BusiTreeRequest): Promise<IResponse> => {
  return request.post({ url: '/dataVisualization/tree', data }).then(res => {
    return res?.data
  })
}

/**
 * 查询业务交互树
 * @param data 查询条件
 * @returns 交互树结构数据
 */
export const queryBusiTreeApi = async (data): Promise<IResponse> => {
  return request.post({ url: '/dataVisualization/interactiveTree', data }).then(res => {
    return res?.data
  })
}

/**
 * 根据 ID 查找数据可视化资源
 * @param dvId 数据可视化 ID
 * @param busiFlag 业务标识
 * @param attachInfo 附加信息，包含来源和任务 ID
 * @returns 资源详细信息
 */
export const findById = async (
  dvId,
  busiFlag,
  attachInfo = { source: 'main', taskId: null }
): Promise<IResponse> => {
  let busiFlagResult = busiFlag
  if (!busiFlagResult) {
    await findDvType(dvId).then(res => {
      busiFlagResult = res.data
    })
  }
  const data = { id: dvId, busiFlag: busiFlagResult, ...attachInfo }
  return request.post({ url: '/dataVisualization/findById', data })
}

/**
 * 查找可复制的资源
 * @param dvId 数据可视化 ID
 * @param busiFlag 业务标识
 * @returns 可复制资源信息
 */
export const findCopyResource = async (dvId, busiFlag): Promise<IResponse> => {
  return request.get({ url: '/dataVisualization/findCopyResource/' + dvId + '/' + busiFlag })
}

/**
 * 查找 DV 类型
 * @param dvId 数据可视化 ID
 * @returns DV 类型信息
 */
export const findDvType = async dvId =>
  request.get({ url: `/dataVisualization/findDvType/${dvId}` })

/**
 * 检查版本更新
 * @param dvId 数据可视化 ID
 * @returns 版本检查结果
 */
export const updateCheckVersion = dvId =>
  request.get({ url: `/dataVisualization/updateCheckVersion/${dvId}` })

/**
 * 获取视图详情列表
 * @param dvId 数据可视化 ID
 * @returns 视图详情列表
 */
export const viewDetailList = dvId => {
  return request.get({
    url: '/dataVisualization/viewDetailList/' + dvId,
    method: 'get',
    loading: false
  })
}

/**
 * 获取组件信息
 * @param dvId 数据可视化 ID
 * @returns 组件信息
 */
export const getComponentInfo = dvId => {
  return request.get({
    url: '/panel/view/getComponentInfo/' + dvId,
    loading: false
  })
}

/**
 * 查询外部参数数据源信息
 * @param dvId 数据可视化 ID
 * @returns 外部参数数据源信息
 */
export const queryOuterParamsDsInfo = async dvId => {
  return request.get({
    url: '/outerParams/queryDsWithVisualizationId/' + dvId,
    method: 'get',
    loading: false
  })
}

/**
 * 查询分享基础配置
 * @returns 分享基础配置信息
 */
export const queryShareBaseApi = () => {
  return request.get({
    url: '/sysParameter/shareBase',
    loading: false
  })
}

// ==================== 资源保存操作 ====================

/**
 * 保存数据可视化资源
 * @param data 资源数据
 * @returns 保存结果
 */
export const save = data => request.post({ url: '/dataVisualization/save', data })

/**
 * 检查画布变更
 * @param data 画布数据
 * @returns 检查结果
 */
export const checkCanvasChange = data =>
  request.post({ url: '/dataVisualization/checkCanvasChange', data, loading: true })

/**
 * 保存画布
 * @param data 画布数据
 * @returns 保存结果
 */
export const saveCanvas = data =>
  request.post({ url: '/dataVisualization/saveCanvas', data, loading: true })

/**
 * 更新画布
 * @param data 画布数据（会处理字段原始名称）
 * @returns 更新结果
 */
export const updateCanvas = data => {
  const copyData = cloneDeep(data)
  const fields = [
    'xAxis',
    'xAxisExt',
    'yAxis',
    'yAxisExt',
    'extBubble',
    'extLabel',
    'extStack',
    'extTooltip',
    'extColor'
  ]

  for (const key in copyData.canvasViewInfo) {
    originNameHandleWithArr(copyData.canvasViewInfo[key], fields)
  }
  return request.post({ url: '/dataVisualization/updateCanvas', data: copyData, loading: true })
}

/**
 * 更新基础信息
 * @param data 基础信息数据
 * @returns 更新结果
 */
export const updateBase = data => request.post({ url: '/dataVisualization/updateBase', data })

/**
 * 更新发布状态
 * @param data 发布状态数据
 * @returns 更新结果
 */
export const updatePublishStatus = data =>
  request.post({ url: '/dataVisualization/updatePublishStatus', data, loading: false })

/**
 * 恢复到已发布版本
 * @param data 资源数据
 * @returns 恢复结果
 */
export const recoverToPublished = data =>
  request.post({ url: '/dataVisualization/recoverToPublished', data, loading: true })

/**
 * 应用画布名称检查
 * @param data 名称检查数据
 * @returns 检查结果
 */
export const appCanvasNameCheck = async data =>
  request.post({ url: '/dataVisualization/appCanvasNameCheck', data, loading: false })

/**
 * DV 名称检查
 * @param data 名称检查数据
 * @returns 检查结果
 */
export const dvNameCheck = async data => request.post({ url: '/dataVisualization/nameCheck', data })

// ==================== 资源管理操作 ====================

/**
 * 移动资源
 * @param data 移动数据
 * @returns 移动结果
 */
export const moveResource = data => request.post({ url: '/dataVisualization/move', data })

/**
 * 复制资源
 * @param data 复制数据
 * @returns 复制结果
 */
export const copyResource = data => request.post({ url: '/dataVisualization/copy', data })

/**
 * 逻辑删除资源
 * @param dvId 数据可视化 ID
 * @param busiFlag 业务标识
 * @returns 删除结果
 */
export const deleteLogic = (dvId, busiFlag) =>
  request.post({ url: '/dataVisualization/deleteLogic/' + dvId + '/' + busiFlag })

/**
 * 解压资源
 * @param data 解压数据
 * @returns 解压结果
 */
export const decompression = async data =>
  request.post({ url: '/dataVisualization/decompression', data, loading: true })

// ==================== 主题管理操作 ====================

/**
 * 查询带分组的主题
 * @param data 查询条件
 * @returns 主题列表
 */
export const querySubjectWithGroupApi = data =>
  request.post({ url: '/visualizationSubject/querySubjectWithGroup', data })

/**
 * 保存或更新主题
 * @param data 主题数据
 * @returns 保存结果
 */
export const saveOrUpdateSubject = data =>
  request.post({ url: '/visualizationSubject/update', data })

/**
 * 删除主题
 * @param id 主题 ID
 * @returns 删除结果
 */
export const deleteSubject = id => request.post({ url: '/visualizationSubject/delete/' + id })

// ==================== 收藏操作 ====================

/**
 * 执行收藏/取消收藏操作
 * @param data 收藏操作数据
 * @returns 操作结果
 */
export const storeApi = (data): Promise<IResponse> => {
  return request.post({ url: '/store/execute', data })
}

/**
 * 查询收藏状态
 * @param id 资源 ID
 * @returns 收藏状态
 */
export const storeStatusApi = (id: string): Promise<IResponse> => {
  return request.get({ url: `/store/favorited/${id}` })
}

// ==================== 导出操作 ====================

/**
 * 导出为应用检查
 * @param params 导出参数
 * @returns 检查结果
 */
export const export2AppCheck = params => {
  return request.post({
    url: '/dataVisualization/export2AppCheck',
    data: params,
    loading: true
  })
}

/**
 * 导出日志为应用
 * @param data 导出数据
 * @returns 导出结果
 */
export const exportLogApp = data => request.post({ url: '/dataVisualization/exportLogApp', data })

/**
 * 导出日志为模板
 * @param data 导出数据
 * @returns 导出结果
 */
export const exportLogTemplate = data =>
  request.post({ url: '/dataVisualization/exportLogTemplate', data })

/**
 * 导出日志为 PDF
 * @param data 导出数据
 * @returns 导出结果
 */
export const exportLogPDF = data => request.post({ url: '/dataVisualization/exportLogPDF', data })

/**
 * 导出日志为图片
 * @param data 导出数据
 * @returns 导出结果
 */
export const exportLogImg = data => request.post({ url: '/dataVisualization/exportLogImg', data })
