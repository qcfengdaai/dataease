/**
 * 用户管理相关 API
 *
 * 功能说明：
 * 1. 用户信息的增删改查
 * 2. 用户登录状态切换
 * 3. 用户导入导出
 * 4. 角色管理
 * 5. 用户与角色的关联管理
 * 6. 密码重置
 * 7. 用户状态切换
 */

import request from '@/config/axios'

// ==================== 用户信息 ====================

/**
 * 获取当前用户信息
 * @returns 用户详细信息
 */
export const userInfo = () => request.get({ url: '/user/info' })

/**
 * 获取个人信息
 * @returns 个人信息
 */
export const personInfoApi = () => request.get({ url: `/user/personInfo` })

/**
 * 获取用户 IP 信息
 * @returns IP 信息
 */
export const ipInfoApi = () => request.get({ url: `/user/ipInfo` })

/**
 * 根据 ID 查询用户表单信息
 * @param uid 用户 ID
 * @returns 用户表单信息
 */
export const queryFormApi = uid => request.get({ url: `/user/queryById/${uid}` })

/**
 * 获取个人系统变量信息
 * @param uid 用户 ID
 * @returns 系统变量信息
 */
export const personSysVariableInfoApi = uid =>
  request.get({ url: `/user/personSysVariableInfo/${uid}` })

// ==================== 用户管理 ====================

/**
 * 创建用户
 * @param data 用户信息
 * @returns 创建结果
 */
export const userCreateApi = data => request.post({ url: '/user/create', data })

/**
 * 编辑用户
 * @param data 用户信息
 * @returns 编辑结果
 */
export const userEditApi = data => request.post({ url: '/user/edit', data })

/**
 * 个人信息编辑
 * @param data 个人信息
 * @returns 编辑结果
 */
export const personEditApi = data => request.post({ url: '/user/personEdit', data })

/**
 * 删除用户
 * @param uid 用户 ID
 * @returns 删除结果
 */
export const userDelApi = uid => request.post({ url: `/user/delete/${uid}` })

/**
 * 批量删除用户
 * @param data 用户 ID 列表
 * @returns 删除结果
 */
export const batchDelApi = data => request.post({ url: '/user/batchDel', data })

/**
 * 用户分页查询
 * @param page 页码
 * @param limit 每页数量
 * @param data 查询条件
 * @returns 用户列表
 */
export const userPageApi = (page: number, limit: number, data) =>
  request.post({ url: `/user/pager/${page}/${limit}`, data })

// ==================== 密码管理 ====================

/**
 * 获取默认密码
 * @returns 默认密码
 */
export const defaultPwdApi = () => request.get({ url: '/user/defaultPwd' })

/**
 * 重置密码
 * @param uid 用户 ID
 * @returns 重置结果
 */
export const resetPwdApi = uid => request.post({ url: `/user/resetPwd/${uid}` })

// ==================== 用户状态 ====================

/**
 * 切换用户启用状态
 * @param data 用户 ID 和状态
 * @returns 切换结果
 */
export const switchEnableApi = data => request.post({ url: '/user/enable', data })

// ==================== 语言切换 ====================

/**
 * 切换用户语言
 * @param data 语言代码
 * @returns 切换结果
 */
export const switchLangApi = data => request.post({ url: '/user/switchLanguage', data })

// ==================== 用户导入导出 ====================

/**
 * 下载 Excel 导入模板
 * @returns Excel 文件
 */
export const downExcelTemplateApi = () =>
  request.post({ url: '/user/excelTemplate', responseType: 'blob' })

/**
 * 批量导入用户
 * @param data 包含文件的表单数据
 * @returns 导入结果
 */
export const importUserApi = data =>
  request.post({
    url: '/user/batchImport',
    headersType: 'multipart/form-data',
    data
  })

/**
 * 下载错误记录
 * @param key 导入批次 key
 * @returns 错误记录文件
 */
export const downErrorRecordApi = (key: string) =>
  request.get({ url: `/user/errorRecord/${key}`, responseType: 'blob' })

/**
 * 清除错误记录
 * @param key 导入批次 key
 * @returns 清除结果
 */
export const clearErrorApi = (key: string) => {
  request.get({ url: `/user/clearErrorRecord/${key}` })
}

// ==================== 组织切换 ====================

/**
 * 获取挂载的组织
 * @param keyword 搜索关键字
 * @returns 组织列表
 */
export const mountedOrg = (keyword?: string) =>
  request.post({ url: '/org/mounted', data: { keyword } })

/**
 * 切换组织
 * @param id 组织 ID
 * @returns 切换结果
 */
export const switchOrg = (id: number | string) => request.post({ url: `/user/switch/${id}` })

// ==================== 角色管理 ====================

/**
 * 搜索角色
 * @param keyword 搜索关键字
 * @returns 角色列表
 */
export const searchRoleApi = (keyword: string) =>
  request.post({ url: '/role/query', data: { keyword } })

/**
 * 创建角色
 * @param data 角色信息
 * @returns 创建结果
 */
export const roleCreateApi = data => request.post({ url: '/role/create', data })

/**
 * 编辑角色
 * @param data 角色信息
 * @returns 编辑结果
 */
export const roleEditApi = data => request.post({ url: '/role/edit', data })

/**
 * 获取角色详情
 * @param rid 角色 ID
 * @returns 角色详情
 */
export const roleDetailApi = rid => request.get({ url: `/role/detail/${rid}` })

/**
 * 删除角色
 * @param rid 角色 ID
 * @returns 删除结果
 */
export const roleDelApi = rid => request.post({ url: `/role/delete/${rid}` })

/**
 * 卸载用户前的信息查询
 * @param data 查询参数
 * @returns 用户列表信息
 */
export const beforeUnmountInfoApi = data => request.post({ url: '/role/beforeUnmountInfo', data })

/**
 * 卸载用户
 * @param data 卸载信息
 * @returns 卸载结果
 */
export const unMountUserApi = data => request.post({ url: '/role/unMountUser', data })

/**
 * 挂载用户
 * @param data 挂载信息
 * @returns 挂载结果
 */
export const mountUserApi = data => request.post({ url: '/role/mountUser', data })

/**
 * 搜索外部用户
 * @param keyword 搜索关键字
 * @returns 外部用户列表
 */
export const searchExternalUserApi = keyword =>
  request.get({ url: '/role/searchExternalUser/' + keyword })

/**
 * 挂载外部用户
 * @param data 挂载信息
 * @returns 挂载结果
 */
export const mountExternalUserApi = data => request.post({ url: '/role/mountExternalUser', data })

// ==================== 角色选项 ====================

/**
 * 角色的用户选项
 * @param data 查询参数
 * @returns 用户选项列表
 */
export const userOptionForRoleApi = data => request.post({ url: '/user/role/option', data })

/**
 * 用户的角色选项
 * @param data 查询参数
 * @returns 角色选项列表
 */
export const roleOptionForUserApi = data => request.post({ url: '/role/user/option', data })

/**
 * 用户已选择列表
 * @param page 页码
 * @param limit 每页数量
 * @param data 查询参数
 * @returns 已选择的用户列表
 */
export const userSelectedForRoleApi = (page: number, limit: number, data) =>
  request.post({ url: `/user/role/selected/${page}/${limit}`, data })
