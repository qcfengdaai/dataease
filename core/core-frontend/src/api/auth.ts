/**
 * 认证授权相关 API
 *
 * 功能说明：
 * 1. 用户和角色查询
 * 2. 权限资源树管理
 * 3. 业务权限和菜单权限的查询与保存
 * 4. 目标权限管理
 */

import request from '@/config/axios'

// ==================== 用户和角色查询 ====================

/**
 * 查询当前组织的用户列表
 * @param data 查询条件
 * @returns 用户列表
 */
export const queryUserApi = data => request.post({ url: '/user/byCurOrg', data })

/**
 * 查询用户选项（用于下拉选择等场景）
 * @returns 用户选项列表
 */
export const queryUserOptionsApi = () => request.get({ url: '/user/org/option' })

/**
 * 查询当前组织的角色列表
 * @param data 查询条件
 * @returns 角色列表
 */
export const queryRoleApi = data => request.post({ url: '/role/byCurOrg', data })

// ==================== 资源树查询 ====================

/**
 * 获取业务资源树
 * @param flag 资源类型标识
 * @returns 资源树结构
 */
export const resourceTreeApi = (flag: string) => request.get({ url: '/auth/busiResource/' + flag })

/**
 * 获取菜单资源树
 * @returns 菜单树结构
 */
export const menuTreeApi = () => request.get({ url: '/auth/menuResource' })

// ==================== 权限查询 ====================

/**
 * 查询业务权限
 * @param data 权限查询参数
 * @returns 业务权限列表
 */
export const resourcePerApi = data => request.post({ url: '/auth/busiPermission', data })

/**
 * 查询菜单权限
 * @param data 权限查询参数
 * @returns 菜单权限列表
 */
export const menuPerApi = data => request.post({ url: '/auth/menuPermission', data })

// ==================== 权限保存 ====================

/**
 * 保存业务权限
 * @param data 权限数据
 * @returns 保存结果
 */
export const busiPerSaveApi = data => request.post({ url: '/auth/saveBusiPer', data })

/**
 * 保存菜单权限
 * @param data 权限数据
 * @returns 保存结果
 */
export const menuPerSaveApi = data => request.post({ url: '/auth/saveMenuPer', data })

// ==================== 目标权限 ====================

/**
 * 查询业务目标权限
 * @param data 权限查询参数
 * @returns 业务目标权限列表
 */
export const resourceTargetPerApi = data =>
  request.post({ url: '/auth/busiTargetPermission', data })

/**
 * 查询菜单目标权限
 * @param data 权限查询参数
 * @returns 菜单目标权限列表
 */
export const menuTargetPerApi = data => request.post({ url: '/auth/menuTargetPermission', data })

/**
 * 保存业务目标权限
 * @param data 权限数据
 * @returns 保存结果
 */
export const busiTargetPerSaveApi = data => request.post({ url: '/auth/saveBusiTargetPer', data })

/**
 * 保存菜单目标权限
 * @param data 权限数据
 * @returns 保存结果
 */
export const menuTargetPerSaveApi = data => request.post({ url: '/auth/saveMenuTargetPer', data })
