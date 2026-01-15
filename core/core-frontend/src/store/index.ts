/**
 * Pinia 状态管理入口文件
 *
 * 功能说明：
 * 1. 创建 Pinia store 实例
 * 2. 提供 setupStore 函数用于在 Vue 应用中安装 store
 * 3. 导出 store 实例供其他模块使用
 *
 * 使用方式：
 * 在 main.ts 中调用 setupStore(app) 安装状态管理
 */

import type { App } from 'vue'
import { createPinia } from 'pinia'

// 创建 Pinia store 实例
const store = createPinia()

/**
 * 设置 Store
 * 在 Vue 应用中安装 Pinia 状态管理
 * @param app Vue 应用实例
 */
export const setupStore = (app: App<Element>) => {
  app.use(store)
}

export { store }
