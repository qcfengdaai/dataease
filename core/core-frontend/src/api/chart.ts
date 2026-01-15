/**
 * 图表管理相关 API
 *
 * 功能说明：
 * 1. 图表数据的查询和获取
 * 2. 图表字段的管理（复制、删除）
 * 3. 图表的保存和详情查询
 * 4. 图表数据的导出
 * 5. 字段枚举值查询
 * 6. 下钻字段数据查询
 */

import request from '@/config/axios'
import { originNameHandleWithArr, originNameHandleBackWithArr } from '@/utils/CalculateFields'
import { cloneDeep } from 'lodash-es'

// ==================== 类型定义 ====================

/**
 * 图表字段接口
 * 定义图表中使用的字段属性
 */
export interface Field {
  /** 字段 ID */
  id: number | string
  /** 数据源 ID */
  datasourceId: number | string
  /** 数据集表 ID */
  datasetTableId: number | string
  /** 数据集分组 ID */
  datasetGroupId: number | string
  /** 原始字段名称 */
  originName: string
  /** 字段显示名称 */
  name: string
  /** DataEase 内部字段名称 */
  dataeaseName: string
  /** 分组类型（维度/度量） */
  groupType: string
  /** 字段类型 */
  type: string
  /** DataEase 字段类型 */
  deType: number
  /** DataEase 提取类型 */
  deExtractType: number
  /** 扩展字段标识 */
  extField: number
  /** 是否选中 */
  checked: boolean
  /** 字段短名称 */
  fieldShortName: string
  /** 是否脱敏 */
  desensitized: boolean
}

/**
 * 组件信息接口
 * 定义图表组件的基本信息
 */
export interface ComponentInfo {
  /** 组件 ID */
  id: string
  /** 组件名称 */
  name: string
  /** DataEase 类型 */
  deType: number
  /** 组件类型 */
  type: string
  /** 数据集 ID */
  datasetId: string
}

// ==================== 图表字段管理 ====================

/**
 * 通过数据集获取图表字段
 * @param id 数据集 ID
 * @param chartId 图表 ID
 * @param data 查询参数
 * @returns 字段列表（包含维度列表和度量列表）
 */
export const getFieldByDQ = async (id, chartId, data): Promise<IResponse> => {
  return request.post({ url: `/chart/listByDQ/${id}/${chartId}`, data: data }).then(res => {
    originNameHandleBackWithArr(res?.data, ['dimensionList', 'quotaList'])
    return res?.data
  })
}

/**
 * 复制图表字段
 * @param id 源字段 ID
 * @param chartId 目标图表 ID
 * @returns 复制结果
 */
export const copyChartField = async (id, chartId): Promise<IResponse> => {
  return request.post({ url: `/chart/copyField/${id}/${chartId}`, data: {} }).then(res => {
    return res?.data
  })
}

/**
 * 删除图表字段
 * @param id 字段 ID
 * @returns 删除结果
 */
export const deleteChartField = async (id): Promise<IResponse> => {
  return request.post({ url: `/chart/deleteField/${id}`, data: {} }).then(res => {
    return res?.data
  })
}

/**
 * 通过图表 ID 删除所有关联字段
 * @param chartId 图表 ID
 * @returns 删除结果
 */
export const deleteChartFieldByChartId = async (chartId): Promise<IResponse> => {
  return request.post({ url: `/chart/deleteFieldByChart/${chartId}`, data: {} }).then(res => {
    return res?.data
  })
}

// ==================== 图表数据查询 ====================

/**
 * 通过图表对象获取数据
 * @param data 图表查询参数，包含图表配置、维度、度量等信息
 * @returns 图表数据
 */
export const getData = async (data): Promise<IResponse> => {
  delete data.data // 删除不必要的数据字段
  const copyData = cloneDeep(data) // 深拷贝数据，避免修改原对象
  const fields = [
    'xAxis', // X 轴
    'xAxisExt', // X 轴扩展
    'yAxis', // Y 轴
    'yAxisExt', // Y 轴扩展
    'extBubble', // 气泡扩展
    'extLabel', // 标签扩展
    'extStack', // 堆叠扩展
    'extTooltip', // 提示扩展
    'extColor' // 颜色扩展
  ]
  const dataFields = ['fields', 'sourceFields']
  originNameHandleWithArr(copyData, fields) // 处理字段名称编码
  return request.post({ url: '/chartData/getData', data: copyData }).then(res => {
    if (res.code === 0) {
      originNameHandleBackWithArr(res?.data, fields) // 解码字段名称
      // 动态计算字段在数据中，也需要转码
      originNameHandleWithArr(res?.data?.data, dataFields)
      originNameHandleBackWithArr(res?.data?.data, dataFields)
      originNameHandleBackWithArr(res?.data?.data?.left, ['fields'])
      originNameHandleBackWithArr(res?.data?.data?.right, ['fields'])
      return res?.data
    } else {
      originNameHandleBackWithArr(res, fields)
      originNameHandleBackWithArr(res?.data, dataFields)
      originNameHandleBackWithArr(res?.data?.left, ['fields'])
      originNameHandleBackWithArr(res?.data?.right, ['fields'])
      return res
    }
  })
}

/**
 * 通过图表 ID 获取图表数据
 * @param id 图表 ID
 * @returns 图表详细数据
 */
export const getChart = async (id): Promise<IResponse> => {
  return request.post({ url: `/chart/getChart/${id}`, data: {} }).then(res => {
    return res?.data
  })
}

/**
 * 获取图表详情
 * @param id 图表 ID
 * @returns 图表详情
 */
export const getChartDetail = async (id: string): Promise<IResponse> => {
  return request.post({ url: `chart/getDetail/${id}`, data: {} }).then(res => {
    return res
  })
}

// ==================== 图表保存 ====================

/**
 * 保存图表
 * @param data 图表数据对象
 * @returns 保存后的图表信息
 */
export const saveChart = async (data): Promise<IResponse> => {
  delete data.data // 删除缓存的数据
  return request.post({ url: '/chart/save', data }).then(res => {
    return res?.data
  })
}

// ==================== 字段枚举值查询 ====================

/**
 * 获取单个字段的枚举值
 * @param fieldId 字段 ID
 * @param fieldType 字段类型
 * @param data 查询参数
 * @returns 字段枚举值列表
 */
export const getFieldData = async ({ fieldId, fieldType, data }): Promise<IResponse> => {
  delete data.data
  return request
    .post({ url: `/chartData/getFieldData/${fieldId}/${fieldType}`, data })
    .then(res => {
      return res
    })
}

/**
 * 获取下钻字段的枚举值
 * 用于图表下钻功能，获取下一层级的字段值
 * @param fieldId 字段 ID
 * @param data 查询参数
 * @returns 下钻字段枚举值列表
 */
export const getDrillFieldData = async ({ fieldId, data }): Promise<IResponse> => {
  delete data.data
  return request.post({ url: `/chartData/getDrillFieldData/${fieldId}`, data }).then(res => {
    return res
  })
}

// ==================== 图表数据导出 ====================

/**
 * 导出图表详情数据
 * @param data 导出参数
 * @returns 导出文件的 Blob 数据
 */
export const innerExportDetails = async (data): Promise<IResponse> => {
  return request.post({
    url: '/chartData/innerExportDetails',
    method: 'post',
    data: data,
    loading: true,
    responseType: 'blob'
  })
}

/**
 * 导出数据集详情数据
 * @param data 导出参数
 * @returns 导出文件的 Blob 数据
 */
export const innerExportDataSetDetails = async (data): Promise<IResponse> => {
  return request.post({
    url: '/chartData/innerExportDataSetDetails',
    method: 'post',
    data: data,
    loading: true,
    responseType: 'blob'
  })
}

// ==================== 其他功能 ====================

/**
 * 检查两个图表是否使用相同的数据集
 * @param viewIdSource 源图表 ID
 * @param viewIdTarget 目标图表 ID
 * @returns 是否使用相同数据集
 */
export const checkSameDataSet = async (viewIdSource, viewIdTarget) =>
  request.get({ url: '/chart/checkSameDataSet/' + viewIdSource + '/' + viewIdTarget })
