import request from '@/config/axios'

/**
 * 数据同步任务相关 API
 * 用于数据同步任务的创建、管理、执行和监控
 */

// ==================== 类型定义 ====================

/**
 * 任务信息查询请求接口
 */
export interface IGetTaskInfoReq {
  /** 任务 ID */
  id?: string
  /** 任务名称 */
  name?: string
}

/**
 * 任务信息创建请求接口
 */
export interface ITaskInfoInsertReq {
  [key: string]: any
}

/**
 * 调度选项接口
 */
export interface ISchedulerOption {
  /** 调度间隔时间 */
  interval: number
  /** 时间单位 */
  unit: string
}

/**
 * 数据源表信息接口
 */
export interface IDsTable {
  /** 数据源 ID */
  datasourceId: string
  /** 表名称 */
  name: string
  /** 表备注 */
  remark: string
  /** 是否启用检查 */
  enableCheck: string
  /** 数据集路径 */
  datasetPath: string
}

/**
 * 表字段信息接口
 */
export interface ITableField {
  /** 字段 ID */
  id: string
  /** 字段来源 */
  fieldSource: string
  /** 字段名称 */
  fieldName: string
  /** 字段类型 */
  fieldType: string
  /** 字段备注 */
  remarks: string
  /** 字段大小 */
  fieldSize: number
  /** 字段精度 */
  fieldPrecision: number
  /** 是否主键 */
  fieldPk: boolean
  /** 是否索引 */
  fieldIndex: boolean
}

/**
 * 目标属性配置接口
 */
export interface ITargetProperty {
  /** 启用分区 on */
  partitionEnable: string
  /** 分区类型: DateRange 日期, NumberRange 数值, List 列 */
  partitionType: string
  /** 启用动态分区 on */
  dynamicPartitionEnable: string
  /** 动态分区结束偏移量 */
  dynamicPartitionEnd: number
  /** 动态分区间隔单位: HOUR WEEK DAY MONTH YEAR */
  dynamicPartitionTimeUnit: string
  /** 手动分区列值，多个以','隔开 */
  manualPartitionColumnValue: string
  /** 手动分区数值区间开始值 */
  manualPartitionStart: number
  /** 手动分区数值区间结束值 */
  manualPartitionEnd: number
  /** 手动分区数值区间间隔 */
  manualPartitionInterval: number
  /** 手动分区日期区间，格式: 2023-09-08 - 2023-09-15 */
  manualPartitionTimeRange: string
  /** 手动分区日期区间间隔单位 */
  manualPartitionTimeUnit: string
  /** 分区字段 */
  partitionColumn: string
}

/**
 * 数据源配置接口
 */
export interface ISource {
  /** 数据源类型 */
  type: string
  /** 查询语句 */
  query: string
  /** 表名称 */
  tables: string
  /** 数据源 ID */
  datasourceId: string
  /** 表抽取类型 */
  tableExtract: string
  /** 数据源表列表 */
  dsTableList?: IDsTable[]
  /** 数据源列表 */
  dsList?: []
  /** 字段列表 */
  fieldList?: ITableField[]
  /** 目标字段类型列表 */
  targetFieldTypeList?: string[]
  /** 增量复选框 */
  incrementCheckbox?: string
  /** 增量字段 */
  incrementField?: string
  /** ES 查询 */
  esQuery?: string
}

/**
 * 目标配置接口
 */
export interface ITarget {
  /** 建表语句 */
  createTable?: string
  /** 目标类型 */
  type: string
  /** 字段列表 */
  fieldList: ITableField[]
  /** 表名称 */
  tableName: string
  /** 数据源 ID */
  datasourceId: string
  /** 目标属性（JSON 字符串） */
  targetProperty: string
  /** 数据源列表 */
  dsList?: []
  /** 多选字段列表 */
  multipleSelection?: ITableField[]
  /** 属性配置 */
  property: ITargetProperty
  /** 增量同步 */
  incrementSync: string
  /** 增量字段 */
  incrementField: string
  /** 增量字段类型 */
  incrementFieldType: string
  /** 备注 */
  remarks: string
  /** 容错率 */
  faultToleranceRate: number
  /** 增量偏移量 */
  incrementOffset: number
  /** 增量偏移量单位 */
  incrementOffsetUnit: string
}

/**
 * 任务信息响应类
 */
export class ITaskInfoRes {
  /** 任务 ID */
  id: string
  /** 任务名称 */
  name: string
  /** 调度类型 */
  schedulerType: string
  /** 调度配置 */
  schedulerConf: string
  /** 调度选项 */
  schedulerOption: ISchedulerOption
  /** 任务标识 */
  taskKey: string
  /** 任务描述 */
  desc: string
  /** 执行器超时时间 */
  executorTimeout: number
  /** 执行器失败重试次数 */
  executorFailRetryCount: number
  /** 数据源配置 */
  source: ISource
  /** 目标配置 */
  target: ITarget
  /** 任务状态 */
  status: string
  /** 开始时间 */
  startTime: string
  /** 停止时间 */
  stopTime: string

  constructor(
    id: string,
    name: string,
    schedulerType: string,
    schedulerConf: string,
    taskKey: string,
    desc: string,
    executorTimeout: number,
    executorFailRetryCount: number,
    source: ISource,
    target: ITarget,
    status: string,
    startTime: string,
    stopTime: string,
    schedulerOption: ISchedulerOption
  ) {
    this.id = id
    this.name = name
    this.schedulerType = schedulerType
    this.schedulerConf = schedulerConf
    this.taskKey = taskKey
    this.desc = desc
    this.executorTimeout = executorTimeout
    this.executorFailRetryCount = executorFailRetryCount
    this.source = source
    this.target = target
    this.status = status
    this.startTime = startTime
    this.stopTime = stopTime
    this.schedulerOption = schedulerOption
  }
}

/**
 * 任务信息更新请求接口
 */
export interface ITaskInfoUpdateReq {
  [key: string]: any
}

// ==================== 任务查询操作 ====================

/**
 * 根据类型获取数据源列表
 * @param type 数据源类型
 * @returns 数据源列表
 */
export const getDatasourceListByTypeApi = (type: string) => {
  return request.get({ url: `/sync/datasource/list/${type}` })
}

/**
 * 分页查询任务信息列表
 * @param current 当前页码
 * @param size 每页数量
 * @param data 查询条件
 * @returns 任务分页列表
 */
export const getTaskInfoListApi = (current: number, size: number, data) => {
  return request.post({ url: `/sync/task/pager/${current}/${size}`, data: data })
}

/**
 * 根据 ID 获取任务详情
 * @param taskId 任务 ID
 * @returns 任务详情信息
 */
export const findTaskInfoByIdApi = (taskId: string) => {
  return request.get({ url: `/sync/task/get/${taskId}` })
}

/**
 * 获取数据源表列表
 * @param dsId 数据源 ID
 * @returns 表列表
 */
export const getDatasourceTableListApi = (dsId: string) => {
  return request.get({ url: `/sync/datasource/table/list/${dsId}` })
}

// ==================== 任务管理操作 ====================

/**
 * 添加同步任务
 * @param data 任务创建信息
 * @returns 创建结果
 */
export const addApi = (data: ITaskInfoInsertReq) => {
  return request.post({ url: `/sync/task/add`, data: data })
}

/**
 * 修改同步任务
 * @param data 任务更新信息
 * @returns 更新结果
 */
export const modifyApi = (data: ITaskInfoUpdateReq) => {
  return request.post({ url: `/sync/task/update`, data: data })
}

/**
 * 删除同步任务
 * @param taskId 任务 ID
 * @returns 删除结果
 */
export const removeApi = (taskId: string) => {
  return request.post({ url: `/sync/task/remove/${taskId}` })
}

/**
 * 批量删除同步任务
 * @param taskIds 任务 ID 数组
 * @returns 删除结果
 */
export const batchRemoveApi = (taskIds: string[]) => {
  return request.post({ url: `/sync/task/batch/del`, data: taskIds })
}

// ==================== 任务执行操作 ====================

/**
 * 执行一次任务
 * @param id 任务 ID
 * @returns 执行结果
 */
export const executeOneApi = (id: string) => {
  return request.get({ url: `/sync/task/execute/${id}` })
}

/**
 * 启动任务
 * @param id 任务 ID
 * @returns 启动结果
 */
export const startTaskApi = (id: string) => {
  return request.get({ url: `/sync/task/start/${id}` })
}

/**
 * 停止任务
 * @param id 任务 ID
 * @returns 停止结果
 */
export const stopTaskApi = (id: string) => {
  return request.get({ url: `/sync/task/stop/${id}` })
}
