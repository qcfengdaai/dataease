/**
 * Loading 加载状态管理工具
 *
 * 功能说明：
 * 1. 统一管理页面和组件的加载状态
 * 2. 基于 Pinia Store 实现全局加载状态跟踪
 * 3. 支持多个并发的加载请求
 *
 * 使用场景：
 * - 页面数据加载时显示 loading
 * - API 请求时显示加载状态
 * - 防止重复提交和操作
 *
 * 使用方式：
 * 1. 在 Vue 页面模板中添加 v-loading 指令
 * 2. 在 API 请求配置中添加 loading 标识
 *
 * @example
 * // 页面中使用
 * // <template>
 * //   <div v-loading="requestStore.loadingMap[permissionStore.currentPath]">
 * //     <!-- 页面内容 -->
 * //   </div>
 * // </template>
 *
 * // API 中配置
 * // export const userLists = (data) => {
 * //   return request.post({ url: '/user/lists', data, loading: true })
 * // }
 */

import { useRequestStoreWithOut } from '@/store/modules/request'

const requestStore = useRequestStoreWithOut()

// ==================== 加载状态控制 ====================

/**
 * 显示加载状态
 *
 * 增加指定标识的加载计数
 * 如果计数 > 0，则显示 loading 状态
 *
 * @param identification - 加载状态标识符（通常是路由路径）
 *
 * @example
 * // API 请求开始时调用
 * tryShowLoading('/workbranch/index')
 */
export const tryShowLoading = identification => {
  if (!identification) return
  requestStore.addLoading(identification)
}

/**
 * 隐藏加载状态
 *
 * 减少指定标识的加载计数
 * 当计数减到 0 时，隐藏 loading 状态
 *
 * @param identification - 加载状态标识符（通常是路由路径）
 *
 * @example
 * // API 请求结束时调用
 * tryHideLoading('/workbranch/index')
 */
export const tryHideLoading = identification => {
  if (!identification) return
  const count = requestStore.loadingMap[identification]
  if (count > 0) {
    requestStore.reduceLoading(identification)
  }
}

// ==================== 使用说明 ====================

/**
 * 使用说明
 *
 * 要在 view 中添加 loading 只需要两个步骤：
 *
 * 步骤 1：在 Vue 页面文件中添加 v-loading 指令
 * @example
 * <!-- views/system/user/index.vue -->
 * <template>
 *   <div v-loading="requestStore.loadingMap[permissionStore.currentPath]">
 *     <!-- 页面内容 -->
 *   </div>
 * </template>
 *
 * 步骤 2：在需要 loading 的 API 中添加 config {loading: true}
 * @example
 * // api/system/user.js
 * export const userLists = (data) => {
 *   return request.post({
 *     url: '/user/lists',
 *     data,
 *     loading: true  // 启用 loading
 *   })
 * }
 *
 * 注意：针对整个 view 页面，需要局部 loading 请在对应页面中自己添加
 */
