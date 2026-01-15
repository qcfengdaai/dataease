import request from '@/config/axios'

/**
 * 模板市场相关 API
 * 用于从模板市场搜索和获取模板信息
 */

// ==================== 模板市场搜索操作 ====================

/**
 * 搜索模板市场
 * 获取模板市场的所有可用模板
 * @returns 模板市场列表
 */
export function searchMarket() {
  return request.get({
    url: '/templateMarket/search'
  })
}

/**
 * 搜索推荐的模板
 * 获取模板市场的推荐模板
 * @returns 推荐模板列表
 */
export function searchMarketRecommend() {
  return request.get({
    url: '/templateMarket/searchRecommend'
  })
}

/**
 * 搜索模板预览
 * 获取模板的预览信息
 * @returns 模板预览信息列表
 */
export function searchMarketPreview() {
  return request.get({
    url: '/templateMarket/searchPreview'
  })
}

// ==================== 分类查询操作 ====================

/**
 * 获取模板分类列表
 * @returns 分类列表
 */
export function getCategories() {
  return request.get({
    url: '/templateMarket/categories'
  })
}

/**
 * 获取分类对象
 * 返回以分类 ID 为键的对象结构
 * @returns 分类对象映射
 */
export function getCategoriesObject() {
  return request.get({
    url: '/templateMarket/categoriesObject'
  })
}
