/**
 * 登录认证相关 API
 *
 * 功能说明：
 * 1. 用户登录（本地登录、第三方平台登录）
 * 2. 用户登出
 * 3. Token 刷新
 * 4. 获取系统配置
 * 5. 获取加密密钥
 */

import request from '@/config/axios'

// ==================== 登录相关 ====================

/**
 * 本地登录
 * @param data 登录信息（用户名、密码、验证码等）
 * @returns 登录结果，包含 token 等信息
 */
export const loginApi = data => request.post({ url: '/login/localLogin', data })

/**
 * 第三方平台登录
 * @param origin 平台标识（如：CAS、OIDC 等）
 * @returns 登录结果
 */
export const platformLoginApi = origin => request.post({ url: '/login/platformLogin/' + origin })

/**
 * 登出
 * @returns 登出结果
 */
export const logoutApi = () => request.get({ url: '/logout' })

/**
 * 刷新 Token
 * @param time 时间戳（可选）
 * @returns 新的 token 信息
 */
export const refreshApi = (time?: any) => request.get({ url: '/login/refresh', params: { time } })

// ==================== 系统配置 ====================

/**
 * 获取 UI 配置
 * @returns UI 配置信息
 */
export const uiLoadApi = () => request.get({ url: '/sysParameter/ui' })

/**
 * 获取默认登录方式
 * @returns 登录方式配置
 */
export const loginCategoryApi = () => request.get({ url: '/sysParameter/defaultLogin' })

/**
 * 获取应用运行模式
 * @returns 应用模式（桌面/浏览器）
 */
export const modelApi = () => request.get({ url: 'model' })

// ==================== 加密相关 ====================

/**
 * 获取 DataEase 密钥
 * @returns 密钥信息
 */
export const queryDekey = () => request.get({ url: 'dekey' })

/**
 * 获取对称加密密钥
 * @returns 对称密钥
 */
export const querySymmetricKey = () => request.get({ url: 'symmetricKey' })
