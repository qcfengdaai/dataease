/**
 * 数据源管理相关 API
 *
 * 功能说明：
 * 1. 数据源的增删改查
 * 2. 数据源验证和测试连接
 * 3. 数据库表和字段的获取
 * 4. 数据预览
 * 5. API 数据源同步
 * 6. 文件上传（Excel 等）
 * 7. 数据源同步记录查询
 * 8. 数据引擎相关配置
 */

import request from '@/config/axios'
import { nameTrim } from '@/utils/utils'

// ==================== 类型定义 ====================

/**
 * 数据集或文件夹接口
 */
export interface DatasetOrFolder {
  /** 名称 */
  name: string
  /** ID */
  id?: number | string
  /** 父 ID */
  pid?: number | string
  /** 节点类型：文件夹或数据集 */
  nodeType: 'folder' | 'dataset'
  /** 关联关系 */
  union?: Array<{}>
  /** 所有字段 */
  allFields?: Array<{}>
}

/**
 * 字段数据接口
 */
interface Fields {
  /** 字段列表 */
  fields: Array<{}>
  /** 数据列表 */
  data: Array<{}>
}

/**
 * 字段数据接口
 */
export interface FieldData {
  /** 所有字段 */
  allFields: Array<{}>
  /** 数据 */
  data: Fields
}

/**
 * 数据集接口
 */
export interface Dataset {
  /** 数据集 ID */
  id: string
  /** 父 ID */
  pid: string
  /** 名称 */
  name: string
  /** 关联关系 */
  union?: Array<{}>
  /** 所有字段 */
  allFields?: Array<{}>
}

/**
 * 表接口
 */
export interface Table {
  /** 数据源 ID */
  datasourceId: string
  /** 名称 */
  name: string
  /** 表名 */
  tableName: string
  /** 类型 */
  type: string
  /** 是否不可选中 */
  unableCheck?: boolean
}

// ==================== 数据源树管理 ====================

/**
 * 获取数据源树列表
 * @param data 查询参数
 * @returns 数据源树结构
 */
export const listDatasources = data => {
  return request
    .post({ url: '/datasource/tree', data: { ...data, ...{ busiFlag: 'datasource' } } })
    .then(res => {
      return res?.data
    })
}

/**
 * 获取数据源树
 * @param data 查询参数
 * @returns 数据源树结构
 */
export const getDsTree = async (data = {}): Promise<IResponse> => {
  return request
    .post({ url: '/datasource/tree', data: { ...data, ...{ busiFlag: 'datasource' } } })
    .then(res => {
      return res?.data
    })
}

/**
 * 获取数据集树
 * @param data 查询参数
 * @returns 数据集树结构
 */
export const getDatasetTree = async (data = {}): Promise<IResponse> => {
  return request
    .post({ url: '/datasetTree/tree', data: { ...data, ...{ busiFlag: 'dataset' } } })
    .then(res => {
      return res?.data
    })
}

// ==================== 数据源类型 ====================

/**
 * 获取数据源类型列表
 * @param data 查询参数
 * @returns 数据源类型列表
 */
export const listDatasourceType = async (data = {}): Promise<IResponse> => {
  return request.post({ url: '/datasource/types', data }).then(res => {
    return res?.data
  })
}

// ==================== 表和字段管理 ====================

/**
 * 获取数据源下的表列表
 * @param data 查询参数（包含数据源 ID）
 * @returns 表列表
 */
export const listDatasourceTables = async (data = {}): Promise<IResponse> => {
  return request.post({ url: '/datasource/getTables', data }).then(res => {
    return res
  })
}

/**
 * 获取表字段
 * @param data 查询参数（数据源 ID、表名等）
 * @returns 字段列表
 */
export const getTableField = (data = {}) => request.post({ url: '/datasource/getTableField', data })

/**
 * 获取表状态
 * @param data 查询参数
 * @returns 表状态信息
 */
export const getTableStatus = async (data = {}): Promise<IResponse> => {
  return request.post({ url: '/datasource/getTableStatus', data }).then(res => {
    return res
  })
}

/**
 * 获取数据库 Schema
 * @param data 查询参数
 * @returns Schema 列表
 */
export const getSchema = (data = {}) => {
  return request.post({ url: '/datasource/getSchema', data })
}

// ==================== 数据预览 ====================

/**
 * 预览数据
 * @param data 查询参数
 * @returns 预览数据
 */
export const previewData = (data = {}) => {
  return request.post({ url: '/datasource/previewData', data }).then(res => {
    return res?.data
  })
}

// ==================== 数据源验证 ====================

/**
 * 验证数据源配置
 * @param data 数据源配置信息
 * @returns 验证结果
 */
export const validate = (data = {}) => {
  return request.post({ url: '/datasource/validate', data })
}

/**
 * 根据 ID 验证数据源
 * @param id 数据源 ID
 * @returns 验证结果
 */
export const validateById = (id: number) => request.get({ url: '/datasource/validate/' + id })

// ==================== 数据源 CRUD ====================

/**
 * 保存数据源
 * @param data 数据源信息
 * @returns 保存后的数据源信息
 */
export const save = async (data = {}): Promise<Dataset> => {
  nameTrim(data) // 去除名称首尾空格
  return request.post({ url: '/datasource/save', data }).then(res => {
    return res?.data
  })
}

/**
 * 更新数据源
 * @param data 数据源信息
 * @returns 更新后的数据源信息
 */
export const update = async (data = {}): Promise<Dataset> => {
  nameTrim(data)
  return request.post({ url: '/datasource/update', data }).then(res => {
    return res?.data
  })
}

/**
 * 重命名数据源
 * @param data 包含 ID 和新名称
 * @returns 重命名结果
 */
export const reName = async (data = {}): Promise<Dataset> => {
  nameTrim(data)
  return request.post({ url: '/datasource/reName', data }).then(res => {
    return res?.data
  })
}

/**
 * 创建文件夹
 * @param data 文件夹信息
 * @returns 创建的文件夹信息
 */
export const createFolder = async (data = {}): Promise<Dataset> => {
  nameTrim(data)
  return request.post({ url: '/datasource/createFolder', data }).then(res => {
    return res?.data
  })
}

/**
 * 移动数据源
 * @param data 移动信息（目标位置等）
 * @returns 移动结果
 */
export const move = async (data = {}): Promise<Dataset> => {
  return request.post({ url: '/datasource/move', data }).then(res => {
    return res?.data
  })
}

/**
 * 根据 ID 删除数据源
 * @param id 数据源 ID
 * @returns 删除结果
 */
export const deleteById = (id: number) => request.get({ url: '/datasource/delete/' + id })

/**
 * 永久删除数据源
 * @param id 数据源 ID
 * @returns 删除结果
 */
export const perDeleteDatasource = async (id): Promise<boolean> => {
  return request.post({ url: `/datasource/perDelete/${id}`, data: {} }).then(res => {
    return res?.data
  })
}

// ==================== 数据源查询 ====================

/**
 * 根据 ID 获取数据源详情
 * @param id 数据源 ID
 * @returns 数据源详情
 */
export const getById = (id: number) => request.get({ url: '/datasource/get/' + id })

/**
 * 获取隐藏密码的数据源信息
 * @param id 数据源 ID
 * @returns 数据源信息（密码已隐藏）
 */
export const getHidePwById = (id: number) => request.get({ url: '/datasource/hidePw/' + id })

/**
 * 获取简化版数据源信息
 * @param id 数据源 ID
 * @returns 简化版数据源信息
 */
export const getSimpleDs = (id: number) => request.get({ url: '/datasource/getSimpleDs/' + id })

// ==================== 重复检查 ====================

/**
 * 检查数据源名称是否重复
 * @param data 包含名称的数据
 * @returns 是否重复
 */
export const checkRepeat = async (data = {}): Promise<Dataset> => {
  return request.post({ url: '/datasource/checkRepeat', data }).then(res => {
    return res?.data
  })
}

/**
 * 检查 API 数据源
 * @param data API 数据源配置
 * @returns 检查结果
 */
export const checkApiItem = async (data = {}): Promise<IResponse> => {
  return request.post({ url: '/datasource/checkApiDatasource', data }).then(res => {
    return res
  })
}

// ==================== API 数据源同步 ====================

/**
 * 同步 API 表
 * @param data 同步参数
 * @returns 同步结果
 */
export const syncApiTable = (data = {}) => request.post({ url: '/datasource/syncApiTable', data })

/**
 * 同步 API 数据源
 * @param data 同步参数
 * @returns 同步结果
 */
export const syncApiDs = (data = {}) => request.post({ url: '/datasource/syncApiDs', data })

// ==================== 文件上传 ====================

/**
 * 上传文件（Excel 等）
 * @param data 文件数据
 * @returns 上传结果
 */
export const uploadFile = async (data): Promise<IResponse> => {
  return request
    .post({
      url: '/datasource/uploadFile',
      data,
      loading: true,
      headersType: 'multipart/form-data;'
    })
    .then(res => {
      return res
    })
}

/**
 * 加载远程文件
 * @param data 远程文件信息
 * @returns 加载结果
 */
export const loadRemoteFile = async (data = {}) => {
  return request.post({ url: '/datasource/loadRemoteFile', data })
}

// ==================== 同步记录 ====================

/**
 * 获取同步记录列表
 * @param page 页码
 * @param limit 每页数量
 * @param dsId 数据源 ID
 * @returns 同步记录列表
 */
export const listSyncRecord = (page: number, limit: number, dsId: number | string) =>
  request.post({ url: '/datasource/listSyncRecord/' + dsId + '/' + page + '/' + limit })

// ==================== 引擎配置 ====================

/**
 * 获取 DataEase 引擎信息
 * @returns 引擎信息
 */
export const getDeEngine = () => request.get({ url: '/engine/getEngine' })

/**
 * 是否支持设置密钥
 * @returns 是否支持
 */
export const supportSetKey = () => request.get({ url: '/engine/supportSetKey' })

// ==================== 完成页面设置 ====================

/**
 * 是否显示完成页面
 * @returns 是否显示
 */
export const isShowFinishPage = async () => {
  return request.get({ url: '/datasource/showFinishPage' })
}

/**
 * 设置是否显示完成页面
 * @param data 设置参数
 * @returns 设置结果
 */
export const setShowFinishPage = (data = {}) => {
  return request.post({ url: '/datasource/setShowFinishPage', data })
}

/**
 * 记录最近使用
 * @param data 使用信息
 * @returns 记录结果
 */
export const latestUse = async (data = {}) => {
  return request.post({ url: '/datasource/latestUse', data })
}
