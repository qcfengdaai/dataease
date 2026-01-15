import request from '@/config/axios'

/**
 * 许可证管理相关 API
 * 用于许可证验证、版本管理和更新操作
 */

/**
 * 验证许可证
 * @param data 许可证验证数据
 * @returns 验证结果
 */
export const validateApi = data => request.post({ url: '/license/validate', data })

/**
 * 获取构建版本信息
 * @returns 版本信息
 */
export const buildVersionApi = () => request.get({ url: '/license/version' })

/**
 * 更新许可证信息
 * @param data 更新数据
 * @returns 更新结果
 */
export const updateInfoApi = data => request.post({ url: '/license/update', data })

/**
 * 回滚许可证
 * @returns 回滚结果
 */
export const revertApi = () => request.post({ url: '/license/revert' })
