import request from '@/config/axios'
import { guid } from '@/views/visualized/data/dataset/form/util.js'
import { ElMessage } from 'element-plus-secondary'

/**
 * 静态资源管理相关 API
 * 用于图片等静态资源的上传、查询和管理
 */

/** 静态资源访问路径前缀 */
const staticResourcePath = '/static-resource/'

// ==================== 文件上传操作 ====================

/**
 * 上传文件到静态资源服务
 * @param fileId 文件 ID
 * @param param 包含文件的 FormData 参数
 * @returns 上传结果
 */
export const uploadFile = (fileId: number | string, param) =>
  request.post({
    url: '/staticResource/upload/' + fileId,
    headersType: 'multipart/form-data',
    loading: true,
    data: param
  })

/**
 * 上传前检查文件
 * 验证文件类型和大小是否符合要求
 * @param file 待上传的文件对象
 * @returns 是否通过检查
 */
export function beforeUploadCheck(file) {
  const isImage = file.type.startsWith('image/')
  const isSizeValid = file.size / 1024 / 1024 < 15 // 15MB

  if (!isImage) {
    ElMessage.error('请上传图片')
    return false
  }
  if (!isSizeValid) {
    ElMessage.error('图片大小不能超过15M')
    return false
  }
  return true
}

/**
 * 上传文件并返回结果
 * 生成唯一文件名并上传，成功后回调返回文件 URL
 * @param file 待上传的文件对象
 * @param callback 上传成功后的回调函数，接收文件 URL 参数
 * @returns 上传 Promise
 */
export function uploadFileResult(file, callback) {
  const fileId = guid()
  const fileName = file.name
  const newFileName = fileId + fileName.substr(fileName.lastIndexOf('.'), fileName.length)
  const fileUrl = staticResourcePath + newFileName
  const param = new FormData()
  param.append('file', file)
  return uploadFile(fileId, param).then(() => {
    callback(fileUrl)
  })
}

// ==================== 资源查询操作 ====================

/**
 * 将静态资源转换为 Base64 格式
 * @param params 资源查询参数
 * @returns Base64 格式的资源数据
 */
export function findResourceAsBase64(params) {
  return request.post({
    url: '/staticResource/findResourceAsBase64',
    data: params
  })
}
