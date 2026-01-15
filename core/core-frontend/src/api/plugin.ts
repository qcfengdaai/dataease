/**
 * 插件管理相关 API
 *
 * 功能说明：
 * 1. 加载插件组件内容
 * 2. 加载企业版扩展组件
 * 3. 获取插件模式信息
 */

import request from '@/config/axios'

/**
 * 加载插件内容
 * @param key 插件标识
 * @returns 插件内容
 */
export const load = (key: string) => request.get({ url: `/xpackComponent/content/${key}` })

/**
 * 加载插件 API
 * @param key 插件标识
 * @returns 插件 API
 */
export const loadPluginApi = (key: string) =>
  request.get({ url: `/xpackComponent/contentPlugin/${key}` })

/**
 * 加载分布式组件
 * @returns 分布式组件内容
 */
export const loadDistributed = () => request.get({ url: '/DEXPack.umd.js' })

/**
 * 获取插件模式信息
 * @returns 插件模式（社区版/企业版）
 */
export const xpackModelApi = () => request.get({ url: '/xpackModel' })
