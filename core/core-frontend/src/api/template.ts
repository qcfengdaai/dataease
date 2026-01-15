/**
 * 模板管理相关 API
 *
 * 功能说明：
 * 1. 模板的增删改查
 * 2. 模板分类管理
 * 3. 模板名称重复检查
 * 4. 批量操作模板
 * 5. 模板市场相关
 */

import request from '@/config/axios'

// ==================== 模板保存 ====================

/**
 * 保存模板
 * 创建或更新模板
 * @param data 模板数据
 * @returns 保存结果
 */
export function save(data) {
  return request.post({
    url: '/templateManage/save',
    data: data,
    loading: true
  })
}

// ==================== 模板查询 ====================

/**
 * 查询单个模板详情
 * @param id 模板 ID
 * @returns 模板详情
 */
export function findOne(id) {
  return request.get({
    url: '/templateManage/findOne/' + id
  })
}

/**
 * 查询模板列表
 * @param data 查询条件（分页、筛选等）
 * @returns 模板列表
 */
export function showTemplateList(data) {
  return request.post({
    url: '/templateManage/templateList',
    data: data
  })
}

/**
 * 查找模板
 * @param data 查询条件
 * @returns 模板数据
 */
export function find(data) {
  return request.post({
    url: '/templateManage/find',
    data: data,
    loading: true
  })
}

// ==================== 模板删除 ====================

/**
 * 删除模板
 * @param id 模板 ID
 * @param categoryId 分类 ID
 * @returns 删除结果
 */
export function templateDelete(id, categoryId) {
  return request.post({
    url: '/templateManage/delete/' + id + '/' + categoryId
  })
}

/**
 * 批量删除模板
 * @param data 模板 ID 列表
 * @returns 删除结果
 */
export function batchDelete(data) {
  return request.post({
    url: '/templateManage/batchDelete',
    data: data
  })
}

// ==================== 模板分类管理 ====================

/**
 * 删除模板分类
 * @param id 分类 ID
 * @returns 删除结果
 */
export function deleteCategory(id) {
  return request.post({
    url: '/templateManage/deleteCategory/' + id
  })
}

/**
 * 查找模板分类
 * @param data 查询条件
 * @returns 分类列表
 */
export function findCategories(data) {
  return request.post({
    url: '/templateManage/findCategories',
    data: data,
    loading: true
  })
}

/**
 * 根据模板 ID 查找分类
 * @param data 模板 ID 列表
 * @returns 分类信息
 */
export function findCategoriesByTemplateIds(data) {
  return request.post({
    url: '/templateManage/findCategoriesByTemplateIds',
    data: data
  })
}

// ==================== 名称检查 ====================

/**
 * 检查模板名称是否重复
 * @param data 包含名称的数据
 * @returns 是否重复
 */
export function nameCheck(data) {
  return request.post({
    url: '/templateManage/nameCheck',
    data: data
  })
}

/**
 * 检查分类下的模板名称是否重复
 * @param data 包含分类 ID 和名称的数据
 * @returns 是否重复
 */
export function categoryTemplateNameCheck(data) {
  return request.post({
    url: '/templateManage/categoryTemplateNameCheck',
    data: data
  })
}

/**
 * 批量检查模板名称
 * @param data 模板名称列表
 * @returns 检查结果
 */
export function checkCategoryTemplateBatchNames(data) {
  return request.post({
    url: '/templateManage/categoryTemplateNameCheck',
    data: data
  })
}

// ==================== 批量操作 ====================

/**
 * 批量更新模板
 * @param data 更新数据
 * @returns 更新结果
 */
export function batchUpdate(data) {
  return request.post({
    url: '/templateManage/batchUpdate',
    data: data
  })
}
