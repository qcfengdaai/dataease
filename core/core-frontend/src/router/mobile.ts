/**
 * 移动端路由配置
 *
 * 功能说明：
 * 1. 定义移动端应用的路由结构
 * 2. 包含移动端首页、登录、面板、分享链接等路由
 * 3. 使用 Hash 模式创建路由
 *
 * 与桌面端路由的区别：
 * - 使用独立的视图组件（views/mobile/）
 * - 路由结构更简化
 * - 支持移动端专属功能（如移动端预览）
 */

import { createRouter, createWebHashHistory } from 'vue-router_2'
import type { RouteRecordRaw } from 'vue-router_2'
import type { App } from 'vue'

// ==================== 移动端路由列表 ====================

/**
 * 移动端路由配置
 * 定义移动端应用的所有静态路由
 */
export const routes: AppRouteRecordRaw[] = [
  // ==================== 根路由 ====================
  {
    path: '/',
    name: '/',
    redirect: '/index', // 重定向到移动端首页
    hidden: true,
    meta: {}
  },
  // ==================== 首页路由 ====================
  {
    path: '/index',
    name: 'index',
    component: () => import('@/views/mobile/index.vue'),
    hidden: true,
    meta: {}
  },
  // ==================== 登录路由 ====================
  {
    path: '/login',
    name: 'login',
    hidden: true,
    meta: {},
    component: () => import('@/views/mobile/login/index.vue')
  },
  // ==================== 面板路由 ====================
  {
    path: '/panel',
    name: 'panel',
    hidden: true,
    meta: {},
    component: () => import('@/views/mobile/panel/index.vue')
  },
  // ==================== 分享链接路由（移动端）====================
  {
    path: '/de-link/:uuid',
    name: 'link',
    hidden: true,
    meta: {},
    component: () => import('@/views/share/link/mobile.vue')
  },
  // ==================== 分享链接路由（PC端）====================
  {
    path: '/pc/de-link/:uuid',
    name: 'linkPc',
    hidden: true,
    meta: {},
    component: () => import('@/views/share/link/index.vue')
  },
  // ==================== 移动端面板路由 ====================
  {
    path: '/panel/mobile',
    name: 'mobile',
    hidden: true,
    meta: {},
    component: () => import('@/views/mobile/panel/Mobile.vue')
  },
  // ==================== 空白仪表板路由 ====================
  {
    path: '/DashboardEmpty',
    name: 'DashboardEmpty',
    hidden: true,
    meta: {},
    component: () => import('@/views/mobile/panel/DashboardEmpty.vue')
  },
  // ==================== 预览路由 ====================
  {
    path: '/preview',
    name: 'preview',
    hidden: true,
    meta: {},
    component: () => import('@/views/data-visualization/PreviewCanvasMobile.vue')
  }
]

// ==================== 创建路由实例 ====================

/**
 * 移动端路由实例
 * 使用 Hash 模式
 */
const router = createRouter({
  history: createWebHashHistory(),
  routes: routes as RouteRecordRaw[]
})

// ==================== 路由工具函数 ====================

/**
 * 安装路由
 * @param app Vue 应用实例
 */
export const setupRouter = (app: App<Element>) => {
  app.use(router)
}

export default router
