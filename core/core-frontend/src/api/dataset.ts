/**
 * 数据集管理相关 API
 *
 * 功能说明：
 * 1. 数据集树的增删改查
 * 2. 数据集字段管理
 * 3. 数据预览和导出
 * 4. 行权限和列权限管理
 * 5. 数据集参数管理
 * 6. AI 辅助分析（Copilot）
 * 7. 导出任务管理
 */

import request from '@/config/axios'
import {
  originNameHandle,
  originNameHandleBack,
  originNameHandleBackWithArr
} from '@/utils/CalculateFields'
import { type Field } from '@/api/chart'
import { cloneDeep } from 'lodash-es'
import type { BusiTreeRequest } from '@/models/tree/TreeNode'
import { nameTrim } from '@/utils/utils'

// ==================== 类型定义 ====================

/**
 * 数据集或文件夹接口
 */
export interface DatasetOrFolder {
  /** 名称 */
  name: string
  /** 操作类型 */
  action?: string
  /** 是否跨数据源 */
  isCross?: boolean
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
 * 枚举值查询参数
 */
export interface EnumValue {
  /** 查询 ID */
  queryId: string
  /** 显示 ID */
  displayId?: string
  /** 排序 ID */
  sortId?: string
  /** 排序方式 */
  sort?: string
  /** 结果模式 */
  resultMode?: number
  /** 搜索文本 */
  searchText: string
  /** 过滤条件 */
  filter?: Array<{}>
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
 * 参数详情接口
 */
export interface ParamsDetail {
  /** 数据集分组 ID */
  datasetGroupId: string
  /** 类型数组 */
  type: Array<string | number>
  /** 变量名称 */
  variableName: string
}

/**
 * 数据集详情接口
 */
export interface DatasetDetail {
  /** 数据集 ID */
  id: string
  /** 数据集名称 */
  name: string
  /** 组件 ID */
  componentId: string
  /** 字段信息 */
  fields: {
    /** 维度列表 */
    dimensionList: Array<Field>
    /** 度量列表 */
    quotaList: Array<Field>
    /** 参数列表 */
    parameterList?: Array<Field>
  }
  /** 激活列表 */
  activelist?: string
  /** 是否有参数 */
  hasParameter?: boolean
  /** 选中列表 */
  checkList: string[]
  /** 字段列表 */
  list: Array<Field>
}

/**
 * 字段数据接口
 */
export interface FieldData {
  /** 所有字段 */
  allFields: Array<{}>
  /** 数据 */
  data: Fields
  /** 总数 */
  total?: number
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
  /** 是否跨数据源 */
  isCross?: boolean
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

// ==================== 数据集树管理 ====================

/**
 * 保存数据集树（编辑）
 * @param data 数据集或文件夹数据
 * @returns 保存结果
 */
export const saveDatasetTree = async (data: DatasetOrFolder): Promise<IResponse> => {
  nameTrim(data) // 去除名称首尾空格
  const copyData = cloneDeep(data)
  originNameHandle(copyData.allFields) // 处理字段名称编码
  return request.post({ url: '/datasetTree/save', data: copyData }).then(res => {
    if (res?.data?.allFields?.length) {
      originNameHandleBack(res?.data?.allFields) // 解码字段名称
    }
    return res?.data
  })
}

/**
 * 创建数据集树
 * @param data 数据集或文件夹数据
 * @returns 创建结果
 */
export const createDatasetTree = async (data: DatasetOrFolder): Promise<IResponse> => {
  nameTrim(data)
  const copyData = cloneDeep(data)
  originNameHandle(copyData.allFields)
  return request.post({ url: '/datasetTree/create', data: copyData }).then(res => {
    if (res?.data?.allFields?.length) {
      originNameHandleBack(res?.data?.allFields)
    }
    return res?.data
  })
}

/**
 * 重命名数据集树
 * @param data 数据集或文件夹数据
 * @returns 重命名结果
 */
export const renameDatasetTree = async (data: DatasetOrFolder): Promise<IResponse> => {
  nameTrim(data)
  return request.post({ url: '/datasetTree/rename', data }).then(res => {
    return res?.data
  })
}

/**
 * 移动数据集树
 * @param data 数据集或文件夹数据
 * @returns 移动结果
 */
export const moveDatasetTree = async (data: DatasetOrFolder): Promise<IResponse> => {
  return request.post({ url: '/datasetTree/move', data }).then(res => {
    return res?.data
  })
}

/**
 * 获取数据集树
 * @param data 查询参数
 * @returns 数据集树结构
 */
export const getDatasetTree = async (data: BusiTreeRequest): Promise<IResponse> => {
  data.busiFlag = 'dataset'
  return request.post({ url: '/datasetTree/tree', data }).then(res => {
    return res?.data
  })
}

/**
 * 获取数据集树节点信息
 * @param id 节点 ID
 * @returns 节点信息
 */
export const barInfoApi = async (id): Promise<IResponse> => {
  return request.get({ url: `/datasetTree/barInfo/${id}`, data: {} }).then(res => {
    return res?.data
  })
}

/**
 * 删除数据集树
 * @param id 数据集或文件夹 ID
 * @returns 删除结果
 */
export const delDatasetTree = async (id): Promise<IResponse> => {
  return request.post({ url: `/datasetTree/delete/${id}`, data: {} }).then(res => {
    return res?.data
  })
}

// ==================== 数据集查询 ====================

/**
 * 获取数据集详情
 * @param id 数据集 ID
 * @returns 数据集详情
 */
export const getDatasetDetails = async (id): Promise<Dataset> => {
  return request.post({ url: `/datasetTree/details/${id}`, data: {} }).then(res => {
    if (res?.data?.allFields?.length) {
      originNameHandleBack(res?.data?.allFields)
    }
    return res?.data
  })
}

/**
 * 获取数据集预览
 * @param id 数据集 ID
 * @returns 字段数据
 */
export const getDatasetPreview = async (id): Promise<FieldData> => {
  return request.post({ url: `/datasetTree/get/${id}`, data: {} }).then(res => {
    return res?.data
  })
}

/**
 * 获取数据集总数
 * @param id 数据集 ID
 * @returns 字段数据
 */
export const getDatasetTotal = async (id): Promise<FieldData> => {
  return request.post({ url: `/datasetData/getDatasetTotal`, data: { id: id } }).then(res => {
    return res?.data
  })
}

/**
 * 获取数据集详情（带权限）
 * @param data 查询参数
 * @returns 数据集详情列表
 */
export const getDsDetailsWithPerm = async (data): Promise<DatasetDetail[]> => {
  return request.post({ url: '/datasetTree/detailWithPerm', data }).then(res => {
    ;(res?.data || []).forEach(ele => {
      originNameHandleBackWithArr(ele, ['dimensionList', 'quotaList'])
    })
    return res?.data
  })
}

/**
 * 获取数据源详情
 * @param data 查询参数
 * @returns 数据集详情列表
 */
export const getDsDetails = async (data): Promise<DatasetDetail[]> => {
  return request.post({ url: '/datasetTree/dsDetails', data }).then(res => {
    return res?.data
  })
}

// ==================== 数据源和表管理 ====================

/**
 * 获取数据源列表
 * @param weight 权重（可选）
 * @returns 数据源列表
 */
export const getDatasourceList = async (weight?: number): Promise<IResponse> => {
  const data = { busiFlag: 'datasource' }
  if (weight) {
    data['weight'] = weight
  }
  return request.post({ url: '/datasource/tree', data }).then(res => {
    return res?.data
  })
}

/**
 * 获取表列表
 * @param data 查询参数
 * @returns 表列表
 */
export const getTables = async (data): Promise<Table[]> => {
  return request.post({ url: `/datasource/getTables`, data }).then(res => {
    return res?.data
  })
}

/**
 * 获取表字段
 * @param data 查询参数
 * @returns 字段数据
 */
export const getTableField = async (data): Promise<IResponse> => {
  return request.post({ url: '/datasetData/tableField', data }).then(res => {
    return res?.data
  })
}

// ==================== 数据预览 ====================

/**
 * 获取预览数据
 * @param data 查询参数
 * @returns 预览数据
 */
export const getPreviewData = async (data): Promise<IResponse> => {
  const copyData = cloneDeep(data)
  originNameHandle(copyData.allFields)
  return request.post({ url: '/datasetData/previewData', data: copyData }).then(res => {
    if (res?.data?.allFields?.length) {
      originNameHandleBack(res?.data?.allFields)
    }

    if (res?.data?.data?.fields?.length) {
      originNameHandleBack(res?.data?.data?.fields)
    }
    return res?.data
  })
}

/**
 * 获取预览 SQL
 * @param data 查询参数
 * @returns SQL 语句
 */
export const getPreviewSql = async (data): Promise<IResponse> => {
  return request.post({ url: '/datasetData/previewSql', data }).then(res => {
    return res?.data
  })
}

// ==================== 枚举值查询 ====================

/**
 * 获取字段枚举值对象
 * @param data 枚举值查询参数
 * @returns 枚举值对象数组
 */
export const enumValueObj = async (data: EnumValue): Promise<Record<string, string>[]> => {
  return request.post({ url: '/datasetData/enumValueObj', data }).then(res => {
    return res?.data
  })
}

/**
 * 获取数据集枚举值
 * @param data 查询参数
 * @returns 枚举值对象数组
 */
export const enumValueDs = async (data: any): Promise<Record<string, string>[]> => {
  return request.post({ url: '/datasetData/enumValueDs', data }).then(res => {
    return res?.data
  })
}

/**
 * 获取枚举值
 * @param data 查询参数
 * @returns 枚举值列表
 */
export const getEnumValue = async (data): Promise<DatasetDetail[]> => {
  return request.post({ url: '/datasetData/enumValue', data }).then(res => {
    return res?.data
  })
}

// ==================== SQL 参数管理 ====================

/**
 * 获取 SQL 参数列表
 * @param data 查询参数
 * @returns 参数详情列表
 */
export const getSqlParams = async (data): Promise<ParamsDetail[]> => {
  return request.post({ url: '/datasetTree/getSqlParams', data }).then(res => {
    return res?.data
  })
}

// ==================== 字段管理 ====================

/**
 * 根据数据集分组列出字段
 * @param datasetId 数据集 ID
 * @returns 字段列表
 */
export const listFieldByDatasetGroup = (datasetId: number) => {
  return request.post({ url: '/datasetField/listByDatasetGroup/' + datasetId }).then(res => {
    originNameHandleBack(res?.data)
    return res
  })
}

/**
 * 根据数据集 ID 列出字段（带权限）
 * @param datasetId 数据集 ID
 * @returns 字段列表
 */
export const listFieldsWithPermissions = (datasetId: number) => {
  return request.get({ url: '/datasetField/listWithPermissions/' + datasetId }).then(res => {
    originNameHandleBack(res?.data)
    return res
  })
}

/**
 * 获取 Copilot 字段
 * @param datasetId 数据集 ID
 * @returns 字段列表
 */
export const copilotFields = (datasetId: number) => {
  return request.post({ url: '/datasetField/copilotFields/' + datasetId })
}

/**
 * 多字段值查询（用于权限设置）
 * @param data 查询参数
 * @returns 字段值
 */
export const multFieldValuesForPermissions = (data = {}) => {
  return request.post({ url: '/datasetField/multFieldValuesForPermissions', data })
}

/**
 * 保存字段
 * @param data 字段数据
 * @returns 保存结果
 */
export const saveField = async (data): Promise<DatasetDetail[]> => {
  return request.post({ url: '/datasetField/save', data }).then(res => {
    return res?.data
  })
}

/**
 * 删除字段
 * @param id 字段 ID
 * @returns 删除结果
 */
export const deleteField = async (id): Promise<DatasetDetail[]> => {
  return request.post({ url: `/datasetField/delete/${id}`, data: {} }).then(res => {
    return res?.data
  })
}

/**
 * 根据图表 ID 删除字段
 * @param id 图表 ID
 * @returns 删除结果
 */
export const deleteFieldByChartId = async (id): Promise<DatasetDetail[]> => {
  return request.post({ url: `/datasetField/deleteByChartId/${id}`, data: {} }).then(res => {
    return res?.data
  })
}

/**
 * 获取函数列表
 * @returns 函数列表
 */
export const getFunction = async (): Promise<DatasetDetail[]> => {
  return request.post({ url: '/datasetField/getFunction', data: {} }).then(res => {
    return res?.data
  })
}

/**
 * 根据数据源 ID 列出字段
 * @param data 查询参数
 * @returns 字段列表
 */
export const listByDsIds = async (data): Promise<IResponse> => {
  return request.post({ url: 'datasetField/listByDsIds', data }).then(res => {
    return res?.data
  })
}

/**
 * 获取字段树
 * @param data 查询参数
 * @returns 字段树结构
 */
export const getFieldTree = async (data): Promise<IResponse> => {
  return request.post({ url: 'datasetData/getFieldTree', data }).then(res => {
    return res?.data
  })
}

// ==================== 权限管理 ====================

/**
 * 行权限列表
 * @param page 页码
 * @param limit 每页数量
 * @param datasetId 数据集 ID
 * @returns 行权限列表
 */
export const rowPermissionList = (page: number, limit: number, datasetId: number) =>
  request.get({ url: '/dataset/rowPermissions/pager/' + datasetId + '/' + page + '/' + limit })

/**
 * 列权限列表
 * @param page 页码
 * @param limit 每页数量
 * @param datasetId 数据集 ID
 * @returns 列权限列表
 */
export const columnPermissionList = (page: number, limit: number, datasetId: number) =>
  request.get({ url: '/dataset/columnPermissions/pager/' + datasetId + '/' + page + '/' + limit })

/**
 * 行权限目标对象列表
 * @param datasetId 数据集 ID
 * @param type 对象类型
 * @returns 目标对象列表
 */
export const rowPermissionTargetObjList = (datasetId: number, type: string) =>
  request.get({ url: '/dataset/rowPermissions/authObjs/' + datasetId + '/' + type })

/**
 * 保存行权限
 * @param data 权限数据
 * @returns 保存结果
 */
export const saveRowPermission = (data = {}) => {
  return request.post({ url: '/dataset/rowPermissions/save', data })
}

/**
 * 保存列权限
 * @param data 权限数据
 * @returns 保存结果
 */
export const saveColumnPermission = (data = {}) => {
  return request.post({ url: '/dataset/columnPermissions/save', data })
}

/**
 * 删除行权限
 * @param data 权限数据
 * @returns 删除结果
 */
export const deleteRowPermission = (data = {}) => {
  return request.post({ url: '/dataset/rowPermissions/delete', data })
}

/**
 * 删除列权限
 * @param data 权限数据
 * @returns 删除结果
 */
export const deleteColumnPermission = (data = {}) => {
  return request.post({ url: '/dataset/columnPermissions/delete', data })
}

/**
 * 权限白名单用户
 * @param data 查询参数
 * @returns 用户列表
 */
export const whiteListUsersForPermissions = (data = {}) => {
  return request.post({ url: '/dataset/rowPermissions/whiteListUsers', data })
}

// ==================== 数据导出 ====================

/**
 * 导出数据集数据
 * @param data 导出参数
 * @returns 导出文件的 Blob 数据
 */
export const exportDatasetData = (data = {}) => {
  return request.post({
    url: '/datasetTree/exportDataset',
    method: 'post',
    data: data,
    loading: true,
    responseType: 'blob'
  })
}

/**
 * 检查导出限制
 * @returns 是否可以导出
 */
export const exportLimit = async (): Promise<boolean> => {
  return request.post({ url: `/exportCenter/exportLimit`, data: {} }).then(res => {
    return res?.data
  })
}

/**
 * 永久删除数据集
 * @param id 数据集 ID
 * @returns 删除结果
 */
export const perDelete = async (id): Promise<boolean> => {
  return request.post({ url: `/datasetTree/perDelete/${id}`, data: {} }).then(res => {
    return res?.data
  })
}

// ==================== 导出任务管理 ====================

/**
 * 获取导出任务记录
 * @returns 导出任务记录列表
 */
export const exportTasksRecords = () =>
  request.post({ url: `/exportCenter/exportTasks/records`, data: {} })

/**
 * 获取导出任务列表
 * @param page 页码
 * @param limit 每页数量
 * @param status 状态
 * @returns 导出任务列表
 */
export const exportTasks = (page: number, limit: number, status: string) =>
  request.post({ url: `/exportCenter/exportTasks/${status}/${page}/${limit}`, data: {} })

/**
 * 重试导出任务
 * @param id 任务 ID
 * @returns 重试结果
 */
export const exportRetry = async (id): Promise<IResponse> => {
  return request.post({ url: '/exportCenter/retry/' + id, data: {} }).then(res => {
    return res?.data
  })
}

/**
 * 下载导出文件
 * @param id 任务 ID
 * @returns 文件 Blob 数据
 */
export const downloadFile = async (id): Promise<Blob> => {
  return request.get({ url: 'exportCenter/download/' + id, responseType: 'blob' }).then(res => {
    return res?.data
  })
}

/**
 * 删除导出任务
 * @param id 任务 ID
 * @returns 删除结果
 */
export const exportDelete = async (id): Promise<IResponse> => {
  return request.get({ url: '/exportCenter/delete/' + id }).then(res => {
    return res?.data
  })
}

/**
 * 生成下载 URI
 * @param id 任务 ID
 * @returns 下载 URI
 */
export const generateDownloadUri = async (id): Promise<IResponse> => {
  return request.get({ url: '/exportCenter/generateDownloadUri/' + id }).then(res => {
    return res?.data
  })
}

/**
 * 批量删除导出任务
 * @param type 类型
 * @param data 任务 ID 列表
 * @returns 删除结果
 */
export const exportDeleteAll = async (type, data): Promise<IResponse> => {
  return request.post({ url: '/exportCenter/deleteAll/' + type, data }).then(res => {
    return res?.data
  })
}

/**
 * POST 方式删除导出任务
 * @param data 任务数据
 * @returns 删除结果
 */
export const exportDeletePost = async (data): Promise<IResponse> => {
  return request.post({ url: '/exportCenter/delete', data }).then(res => {
    return res?.data
  })
}

// ==================== AI 辅助分析（Copilot）====================

/**
 * Copilot 聊天
 * @param data 聊天数据
 * @returns 聊天响应
 */
export const copilotChat = async (data): Promise<IResponse> => {
  return request.post({ url: '/copilot/chat', data }).then(res => {
    return res?.data
  })
}

/**
 * 获取 Copilot 列表
 * @returns Copilot 列表
 */
export const getListCopilot = async (): Promise<IResponse> => {
  return request.post({ url: '/copilot/getList' }).then(res => {
    return res?.data
  })
}

/**
 * 清除所有 Copilot 对话
 * @returns 清除结果
 */
export const clearAllCopilot = async (): Promise<IResponse> => {
  return request.post({ url: '/copilot/clearAll' }).then(res => {
    return res?.data
  })
}

// ==================== 表更新 ====================

/**
 * 更新表信息
 * @param data 表数据
 * @returns 更新结果
 */
export const tableUpdate = async (data): Promise<IResponse> => {
  return request.post({ url: '/dataset/table/update', data }).then(res => {
    return res?.data
  })
}
