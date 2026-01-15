/**
 * 水印管理相关 API
 *
 * 功能说明：
 * 1. 保存水印配置
 * 2. 查询水印配置
 */

import request from '@/config/axios'

/**
 * 保存水印配置
 * @param params 水印配置信息
 * @returns 保存结果
 */
export const watermarkSave = params => request.post({ url: '/watermark/save', data: params })

/**
 * 查询水印配置
 * @returns 水印配置信息
 */
export const watermarkFind = async () => request.get({ url: 'watermark/find' })
