/**
 * 嵌入式模式路由配置
 *
 * 功能说明：
 * 1. 用于嵌入式应用场景的路由配置
 * 2. 允许 DataEase 作为 iframe 组件嵌入到其他应用中
 * 3. 与主路由(index.ts)相比，嵌入式模式下使用空路由列表
 * 4. 由父应用控制路由跳转，本应用仅作为内容展示
 *
 * 使用场景：
 * - 将数据集、图表等组件嵌入到其他系统中
 * - 作为独立模块集成到第三方平台
 * - 多应用集成场景
 */

import { createRouter, createWebHashHistory } from 'vue-router_2'
import type { RouteRecordRaw } from 'vue-router_2'
import type { App } from 'vue'

// ==================== 路由配置 ====================

/**
 * 嵌入式模式路由列表
 *
 * 注意：嵌入式模式下使用空路由列表
 * 实际路由由父应用控制，此应用仅作为内容容器
 */
export const routes: AppRouteRecordRaw[] = []

// ==================== 创建路由实例 ====================

/**
 * Vue Router 实例
 * 使用 Hash 模式创建路由，支持嵌入式场景
 */
const router = createRouter({
  history: createWebHashHistory(),
  routes: routes as RouteRecordRaw[]
})

// ==================== 路由工具函数 ====================

/**
 * 安装路由
 *
 * 在 Vue 应用中使用路由插件的入口函数
 * @param app Vue 应用实例
 */
export const setupRouter = (app: App<Element>) => {
  app.use(router)
}

export default router
