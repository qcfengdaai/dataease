import request from '@/config/axios'

/**
 * 字体管理相关 API
 * 用于系统字体的增删改查和上传操作
 */

/**
 * 字体信息接口
 */
export interface Font {
  /** 字体 ID */
  id: string
  /** 字体名称 */
  name: string
  /** 字体文件名 */
  fileName: string
  /** 是否为默认字体 */
  isDefault: boolean
  /** 是否为内置字体 */
  isBuiltin?: boolean
}

// ==================== 字体查询操作 ====================

/**
 * 获取字体列表
 * @returns 字体列表数据
 */
export const list = () => {
  return request.get({ url: '/typeface/listFont' }).then(res => {
    return res?.data
  })
}

/**
 * 获取默认字体信息
 * @returns 默认字体信息
 */
export const defaultFont = () => {
  return request.get({ url: '/typeface/defaultFont' }).then(res => {
    return res?.data
  })
}

// ==================== 字体管理操作 ====================

/**
 * 创建新字体
 * @param data 字体数据，默认为空对象
 * @returns 创建结果
 */
export const create = (data = {}) => {
  return request.post({ url: '/typeface/create', data }).then(res => {
    return res?.data
  })
}

/**
 * 编辑字体信息
 * @param data 字体数据，默认为空对象
 * @returns 编辑结果
 */
export const edit = (data = {}) => {
  return request.post({ url: '/typeface/edit', data }).then(res => {
    return res?.data
  })
}

/**
 * 根据 ID 删除字体
 * @param id 字体 ID
 * @returns 删除结果
 */
export const deleteById = id => {
  return request.post({ url: '/typeface/delete/' + id, data: {} }).then(res => {
    return res?.data
  })
}

// ==================== 文件上传操作 ====================

/**
 * 上传字体文件
 * @param data 包含字体文件的 FormData 数据
 * @returns 上传结果响应
 */
export const uploadFontFile = async (data): Promise<IResponse> => {
  return request
    .post({
      url: '/typeface/uploadFile',
      data,
      loading: true,
      headersType: 'multipart/form-data;'
    })
    .then(res => {
      return res
    })
}
