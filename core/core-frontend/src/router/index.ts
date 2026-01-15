/**
 * 主路由配置文件
 *
 * 用于 PC 端应用的路由配置，定义了应用的主要路由结构
 * 包括工作台、数据可视化、仪表板、图表等核心功能路由
 */

import { createRouter, createWebHashHistory } from 'vue-router_2'
import type { RouteRecordRaw } from 'vue-router_2'
import type { App } from 'vue'

// ==================== 路由配置 ====================

/**
 * 应用静态路由列表
 *
 * 定义应用的基础路由结构，这些路由在应用启动时就会被注册
 */
export const routes: AppRouteRecordRaw[] = [
  // ==================== 根路由 ====================
  {
    path: '/',
    name: 'index',
    redirect: '/workbranch/index', // 重定向到工作台
    component: () => import('@/layout/index.vue'),
    hidden: true, // 不在菜单中显示
    meta: {},
    children: [
      {
        path: 'workbranch',
        name: 'workbranch',
        hidden: true,
        component: () => import('@/views/workbranch/index.vue'),
        meta: { hidden: true }
      }
    ]
  },
  // ==================== AI SQL Bot 路由 ====================
  {
    path: '/sqlbot',
    name: 'sqlbot',
    component: () => import('@/layout/index.vue'),
    hidden: true,
    meta: {},
    children: [
      {
        path: 'index',
        name: 'clt',
        hidden: true,
        component: () => import('@/views/sqlbot/index.vue'),
        meta: { hidden: true }
      }
    ]
  },
  // ==================== 登录相关路由 ====================
  {
    path: '/login',
    name: 'login',
    hidden: true,
    meta: {},
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/admin-login',
    name: 'admin-login',
    hidden: true,
    meta: {},
    component: () => import('@/views/login/index.vue')
  },
  // ==================== 错误页面路由 ====================
  {
    path: '/401',
    name: '401',
    hidden: true,
    meta: {},
    component: () => import('@/views/401/index.vue')
  },
  // ==================== 数据可视化路由 ====================
  {
    path: '/dvCanvas',
    name: 'dvCanvas',
    hidden: true,
    meta: {},
    component: () => import('@/views/data-visualization/index.vue')
  },
  // ==================== 仪表板路由 ====================
  {
    path: '/dashboard',
    name: 'dashboard',
    hidden: true,
    meta: {},
    component: () => import('@/views/dashboard/index.vue')
  },
  {
    path: '/dashboardPreview',
    name: 'dashboardPreview',
    hidden: true,
    meta: {},
    component: () => import('@/views/dashboard/DashboardPreviewShow.vue')
  },
  // ==================== 图表路由 ====================
  {
    path: '/chart',
    name: 'chart',
    hidden: true,
    meta: {},
    component: () => import('@/views/chart/index.vue')
  },
  // ==================== 预览相关路由 ====================
  {
    path: '/previewShow',
    name: 'previewShow',
    hidden: true,
    meta: {},
    component: () => import('@/views/data-visualization/PreviewShow.vue')
  },
  {
    path: '/preview',
    name: 'preview',
    hidden: true,
    meta: {},
    component: () => import('@/views/data-visualization/PreviewCanvas.vue')
  },
  // ==================== 公共组件路由 ====================
  {
    path: '/DeResourceTree',
    name: 'DeResourceTree',
    hidden: true,
    meta: {},
    component: () => import('@/views/common/DeResourceTree.vue')
  },
  // ==================== 数据集嵌入式路由 ====================
  {
    path: '/dataset-embedded',
    name: 'dataset-embedded',
    hidden: true,
    meta: {},
    component: () => import('@/views/visualized/data/dataset/index.vue')
  },
  {
    path: '/dataset-embedded-form',
    name: 'dataset-embedded-form',
    hidden: true,
    meta: {},
    component: () => import('@/views/visualized/data/dataset/form/index.vue')
  },
  // ==================== 分享链接路由 ====================
  {
    path: '/de-link/:uuid',
    name: 'link',
    hidden: true,
    meta: {},
    component: () => import('@/views/data-visualization/LinkContainer.vue')
  },
  // ==================== 富文本组件路由 ====================
  {
    path: '/rich-text',
    name: 'rich-text',
    hidden: true,
    meta: {},
    component: () => import('@/custom-component/rich-text/DeRichTextView.vue')
  },
  // ==================== 系统设置路由 ====================
  {
    path: '/modify-pwd',
    name: 'modify-pwd',
    hidden: true,
    meta: {},
    component: () => import('@/layout/index.vue'),
    children: [
      {
        path: 'index',
        name: 'mpi',
        hidden: true,
        component: () => import('@/views/system/modify-pwd/index.vue'),
        meta: { hidden: true }
      }
    ]
  },
  // ==================== 图表视图路由 ====================
  {
    path: '/chart-view',
    name: 'chart-view',
    hidden: true,
    meta: {},
    component: () => import('@/views/chart/ChartView.vue')
  },
  // ==================== 模板管理路由 ====================
  {
    path: '/template-manage',
    name: 'template-manage',
    hidden: true,
    meta: {},
    component: () => import('@/views/template/indexInject.vue')
  }
]

// ==================== 创建路由实例 ====================

/**
 * Vue Router 实例
 * 使用 Hash 模式创建路由
 */
const router = createRouter({
  history: createWebHashHistory(),
  routes: routes as RouteRecordRaw[]
})

// ==================== 路由工具函数 ====================

/**
 * 重置路由
 *
 * 用于退出登录时清空所有动态添加的路由
 * 保留白名单中的路由（如登录页）
 */
export const resetRouter = (): void => {
  const resetWhiteNameList = ['Login']
  router.getRoutes().forEach(route => {
    const { name } = route
    if (name && !resetWhiteNameList.includes(name as string)) {
      router.hasRoute(name) && router.removeRoute(name)
    }
  })
}

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
