/**
 * 路由建立工具函数
 *
 * 功能说明：
 * 1. 后端控制路由生成 - 根据后端返回的路由配置动态生成前端路由
 * 2. 路由格式化 - 处理单子路由的特殊情况
 * 3. 路由装饰 - 为需要布局的路由添加父级包装
 * 4. 路径解析 - 解析路由的默认跳转路径
 */

import { isExternal } from '@/utils/validate'
import { cloneDeep } from 'lodash'
import { XpackComponent } from '@/components/plugin'

// ==================== 动态导入视图模块 ====================

/**
 * 动态导入所有视图组件
 * 使用 Vite 的 glob 功能自动导入 views 目录下的所有 .vue 文件
 */
const modules = import.meta.glob('../views/**/*.vue')

// ==================== 布局组件 ====================

/**
 * 主布局组件
 */
export const Layout = () => import('@/layout/index.vue')

/**
 * 插件组件名称
 */
const xpackComName = 'components/plugin'

/**
 * 带过渡效果的布局组件
 */
export const LayoutTransition = () => import('@/layout/components/LayoutTransition.vue')

// ==================== 后端控制路由生成 ====================

/**
 * 根据后端返回的路由配置生成前端路由
 *
 * @param routes 后端返回的自定义路由配置
 * @returns 处理后的路由配置
 *
 * 处理逻辑：
 * 1. 处理插件路由（使用 XpackComponent）
 * 2. 处理布局路由（使用 Layout/LayoutTransition）
 * 3. 动态导入组件
 * 4. 递归处理子路由
 */
export const generateRoutesFn2 = (routes: AppCustomRouteRecordRaw[]): AppRouteRecordRaw[] => {
  const res: AppRouteRecordRaw[] = []

  for (const router of routes) {
    let route = { ...router }

    // 如果是顶级路由且需要布局，进行装饰
    if (route.top && route.inLayout) {
      route = decorate(route)
    }

    // 处理插件路由
    if (route.plugin) {
      const jsName = route.component
      route.component = xpackComName
      route.props = {
        jsname: jsName,
        inLayout: route.inLayout
      }
    }

    const data: AppRouteRecordRaw = {
      path: route.path,
      hidden: route.hidden,
      name: route.name,
      redirect: route.redirect,
      meta: route.meta,
      props: route.props as Recordable
    }

    // 处理组件
    if (route.component) {
      let comModule = null

      // 插件组件
      if (route.component === xpackComName) {
        comModule = XpackComponent
      }
      // 动态导入组件（排除 Layout 组件）
      else if (!route.component.startsWith('Layout')) {
        comModule = modules[`../views/${route.component}/index.vue`]
      }

      // Layout 组件
      if (route.component === 'Layout') {
        data.component = Layout
      }
      // LayoutTransition 组件
      else if (route.component === 'LayoutTransition') {
        data.component = LayoutTransition
      }
      // 动态导入的组件
      else if (!comModule) {
        // 组件不存在，跳过
      } else {
        data.component = comModule
      }
    }

    // 递归处理子路由
    if (route.children) {
      data.children = generateRoutesFn2(route.children)
    }

    res.push(data as AppRouteRecordRaw)
  }

  return res
}

// ==================== 路由格式化 ====================

/**
 * 格式化路由配置
 *
 * 处理单子路由的特殊情况：
 * 如果路由只有一个子路由，将该子路由提升到父级
 *
 * @param arr 路由配置数组
 * @returns 格式化后的路由配置
 */
export const formatRoute = (arr: AppCustomRouteRecordRaw[]): AppCustomRouteRecordRaw[] => {
  return arr.map(ele => {
    const router = cloneDeep(ele)
    const { path, children = [] } = router

    // 只有一个子路由且不是数据路由，则提升子路由
    if (children?.length === 1 && router.path !== '/data') {
      const [route] = children
      router.path = `${path}/${route.path}`
      router.children = []
    }
    return router
  })
}

// ==================== 路由装饰 ====================

/**
 * 装饰路由，添加父级布局
 *
 * 为需要布局的路由添加一层父级目录
 * 保持原始路由作为子路由
 *
 * @param router 原始路由
 * @returns 装饰后的路由
 */
export const decorate = (router: AppCustomRouteRecordRaw): AppCustomRouteRecordRaw => {
  const { path, meta, children = [], inLayout, hidden } = router

  // 创建父级路由
  const parent = {
    path,
    meta,
    inLayout,
    component: 'Layout', // 使用布局组件
    children,
    hidden
  }

  // 当前路由
  const current = { ...router }
  current.inLayout = false

  // 如果没有子路由，将当前路由作为子路由
  if (!children?.length) {
    current.path = 'index'
    parent.children = [current]
  }

  return parent
}

// ==================== 路径解析 ====================

/**
 * 解析路由的默认跳转路径
 *
 * 用于获取路由的默认跳转路径：
 * - 如果是根路由，返回重定向路径
 * - 如果有子路由，返回第一个子路由的路径
 * - 否则返回当前路径
 *
 * @param item 路由配置
 * @returns 默认跳转路径
 */
export const resolvePath = (item: AppRouteRecordRaw) => {
  // 如果是首页，就返回重定向路由
  if (item.path === '/') {
    return item.redirect as string
  }

  // 如果有子项，默认跳转第一个子项路由
  let path = ''

  /**
   * 递归获取默认路径
   * @param ele 路由子项
   * @param parent 路由父项
   */
  const getDefaultPath = (ele, parent?) => {
    // 如果path是个外部链接（不建议），直接返回链接
    if (isExternal(ele.path)) {
      path = ele.path
      return
    }
    // 第一次需要父项路由拼接，所以只是第一个传parent
    if (parent) {
      path += parent.path + '/' + ele.path
    } else {
      path += '/' + ele.path
    }
    // 如果还有子项，继续递归
    if (ele.children) {
      getDefaultPath(ele.children[0])
    }
  }

  if (item.children) {
    getDefaultPath(item.children[0], item)
    return path
  }

  return item.path
}
