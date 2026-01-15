/**
 * 路由权限控制 - 桌面端
 *
 * 功能说明：
 * 1. 路由前置守卫 - 处理页面访问权限、登录状态检查
 * 2. 路由后置守卫 - 结束进度条和加载状态
 * 3. 移动端自动重定向 - 检测移动设备并重定向到移动端页面
 * 4. 动态路由加载 - 根据用户权限动态添加路由
 * 5. 白名单管理 - 无需登录即可访问的页面
 */

import router from './router'
import { useUserStoreWithOut } from '@/store/modules/user'
import { useAppStoreWithOut } from '@/store/modules/app'
import type { RouteRecordRaw } from 'vue-router_2'
import { getDefaultSettings } from '@/api/common'
import { useNProgress } from '@/hooks/web/useNProgress'
import { usePermissionStoreWithOut, pathValid, getFirstAuthMenu } from '@/store/modules/permission'
import { usePageLoading } from '@/hooks/web/usePageLoading'
import { getRoleRouters } from '@/api/common'
import { useCache } from '@/hooks/web/useCache'
import { isMobile, checkPlatform, isLarkPlatform, isPlatformClient } from '@/utils/utils'
import { interactiveStoreWithOut } from '@/store/modules/interactive'
import { useAppearanceStoreWithOut } from '@/store/modules/appearance'
import { useEmbedded } from '@/store/modules/embedded'

// ==================== Store 初始化 ====================
const appearanceStore = useAppearanceStoreWithOut() // 外观设置 Store
const { wsCache } = useCache() // 缓存工具
const permissionStore = usePermissionStoreWithOut() // 权限 Store
const interactiveStore = interactiveStoreWithOut() // 交互设置 Store
const userStore = useUserStoreWithOut() // 用户信息 Store
const appStore = useAppStoreWithOut() // 应用配置 Store

// ==================== 进度条和加载状态 ====================
const { start, done } = useNProgress() // 进度条开始和结束
const { loadStart, loadDone } = usePageLoading() // 页面加载状态

// ==================== 白名单配置 ====================
/**
 * 无需登录即可访问的页面白名单
 * - /login: 登录页面
 * - /de-link: 分享链接页面
 * - /chart-view: 图表查看页面
 * - /admin-login: 管理员登录页面
 * - /401: 无权限页面
 */
const whiteList = ['/login', '/de-link', '/chart-view', '/admin-login', '/401']

/**
 * 嵌入式窗口白名单
 * 这些页面在 iframe 中打开时不需要登录验证
 */
const embeddedWindowWhiteList = ['/dvCanvas', '/dashboard', '/preview', '/dataset-embedded-form']

/**
 * 嵌入式路由白名单
 * 这些路由在 iframe 中可以访问
 */
const embeddedRouteWhiteList = ['/dataset-embedded', '/dataset-form', '/dataset-embedded-form']

// ==================== 路由前置守卫 ====================
/**
 * 路由前置守卫
 * @param to 即将进入的目标路由
 * @param from 当前导航正要离开的路由
 * @param next 路由跳转控制函数
 *
 * 主要处理逻辑：
 * 1. 启动进度条和加载状态
 * 2. 检测设备类型，移动端自动跳转
 * 3. 加载外观设置和字体配置
 * 4. 检查登录状态和用户权限
 * 5. 动态加载用户可访问的路由
 * 6. 处理嵌入式页面和白名单页面
 */
router.beforeEach(async (to, from, next) => {
  start() // 启动顶部进度条
  loadStart() // 启动页面加载动画

  // ==================== 平台检测 ====================
  const platform = checkPlatform()
  let isDesktop = wsCache.get('app.desktop')

  // 首次检测桌面模式
  if (isDesktop === null) {
    await appStore.setAppModel() // 设置应用模式（桌面/浏览器）
    isDesktop = appStore.getDesktop
  }

  // ==================== 移动端重定向 ====================
  // 如果是移动设备且不是图表查看页面，则跳转到移动端
  if (isMobile() && !['/chart-view'].includes(to.path)) {
    done() // 结束进度条
    loadDone() // 结束加载状态

    if (to.name === 'link') {
      // 处理分享链接的移动端跳转
      let linkQuery = ''
      if (Object.keys(to.query)) {
        const tempQuery = Object.keys(to.query)
          .map(key => key + '=' + to.query[key])
          .join('&')
        if (tempQuery) {
          linkQuery = '?' + tempQuery
        }
      }
      let pathname = window.location.pathname
      pathname = pathname.replace('casbi/', '')
      pathname = pathname.replace('oidc/', '')
      pathname = pathname.substring(0, pathname.length - 1)
      const prefix = window.origin + pathname
      let toPath = to.fullPath
      if (toPath.includes('?')) {
        toPath = to.fullPath.substring(0, to.fullPath.lastIndexOf('?'))
      }
      window.location.href = prefix + '/mobile.html#' + toPath + linkQuery
    } else if (
      wsCache.get('user.token') || // 已登录
      isDesktop || // 桌面模式
      (!isPlatformClient() && !isLarkPlatform()) // 非飞书/平台客户端
    ) {
      // 跳转到移动端首页
      let pathname = window.location.pathname
      pathname = pathname.substring(0, pathname.length - 1)
      let url = window.origin + pathname + '/mobile.html#/index'
      if (location.hash?.startsWith('#/preview')) {
        url = window.origin + pathname + '/mobile.html' + location.hash
      }
      if (window.location.search) {
        url += window.location.search
      }
      window.location.href = url
    }
  }

  // ==================== 加载全局配置 ====================
  await appearanceStore.setAppearance() // 设置外观主题
  await appearanceStore.setFontList() // 加载字体列表
  const defaultSort = await getDefaultSettings() // 获取系统默认设置

  // 缓存系统设置
  wsCache.set('TreeSort-backend', defaultSort['basic.defaultSort'] ?? '1') // 树形排序方式
  wsCache.set('open-backend', defaultSort['basic.defaultOpen'] ?? '0') // 树形展开方式

  // ==================== 已登录用户处理 ====================
  if ((wsCache.get('user.token') || isDesktop) && !to.path.startsWith('/de-link/')) {
    // 获取用户信息
    if (!userStore.getUid) {
      await userStore.setUser()
    }

    // 如果访问登录页，重定向到工作台
    if (to.path === '/login') {
      next({ path: '/workbranch/index' })
    } else {
      // 记录当前访问路径
      permissionStore.setCurrentPath(to.path)

      // 如果路由已添加，直接放行
      if (permissionStore.getIsAddRouters) {
        let str = ''
        if (((from.query.redirect as string) || '?').split('?')[0] === to.path) {
          str = ((window.location.hash as string) || '?').split('?').reverse()[0]
          if (str.includes('redirect=')) {
            str = ''
          }
        }
        if (str) {
          to.fullPath += '?' + str
          to.query = str.split('&').reduce((pre, itx) => {
            const [key, val] = itx.split('=')
            pre[key] = val
            return pre
          }, {})
        }

        // 验证路径有效性
        if (!pathValid(to.path) && to.path !== '/404' && !to.path.startsWith('/de-link')) {
          const firstPath = getFirstAuthMenu()
          next({ path: firstPath || '/404' })
          return
        }
        next()
        return
      }

      // ==================== 动态加载路由 ====================
      // 获取用户可访问的路由
      let roleRouters = (await getRoleRouters()) || []

      // 桌面模式隐藏系统管理
      if (isDesktop) {
        roleRouters = roleRouters.filter(item => item.name !== 'system')
      }

      const routers: any[] = roleRouters as AppCustomRouteRecordRaw[]
      routers.forEach(item => (item['top'] = true)) // 标记为顶级路由

      // 生成可访问的路由表
      await permissionStore.generateRoutes(routers as AppCustomRouteRecordRaw[])

      // 动态添加路由到 vue-router
      permissionStore.getAddRouters.forEach(route => {
        router.addRoute(route as unknown as RouteRecordRaw)
      })

      // 处理重定向
      const redirectPath = from.query.redirect || to.path
      const redirect = decodeURIComponent(redirectPath as string)
      const nextData = to.path === redirect ? { ...to, replace: true } : { path: redirect }

      permissionStore.setIsAddRouters(true) // 标记路由已添加
      await interactiveStore.initInteractive(true) // 初始化交互设置

      // 验证路径有效性
      if (!pathValid(to.path) && to.path !== '/404' && !to.path.startsWith('/de-link')) {
        const firstPath = getFirstAuthMenu()
        next({ path: firstPath || '/404' })
        return
      }
      next(nextData)
    }
  }
  // ==================== 未登录用户处理 ====================
  else {
    const embeddedStore = useEmbedded()

    // 嵌入式页面处理
    if (
      embeddedStore.getToken && // 有嵌入令牌
      appStore.getIsIframe && // 在 iframe 中
      embeddedRouteWhiteList.includes(to.path) // 在白名单中
    ) {
      // 数据集表单特殊处理
      if (to.path.includes('/dataset-form')) {
        next({ path: '/dataset-embedded-form', query: to.query })
        return
      }
      permissionStore.setCurrentPath(to.path)
      next()
    }
    // 白名单页面直接放行
    else if (
      (!platform && embeddedWindowWhiteList.includes(to.path)) || // 非平台客户端 + 嵌入式白名单
      whiteList.includes(to.path) || // 在白名单中
      to.path.startsWith('/de-link/') // 分享链接
    ) {
      await appearanceStore.setFontList()
      permissionStore.setCurrentPath(to.path)
      next()
    }
    // 其他情况重定向到登录页
    else {
      next(`/login?redirect=${to.fullPath || to.path}`) // 保存原始路径用于登录后跳转
    }
  }
})

// ==================== 路由后置守卫 ====================
/**
 * 路由后置守卫
 * 路由跳转完成后执行，结束进度条和加载状态
 */
router.afterEach(() => {
  done() // 结束进度条
  loadDone() // 结束加载状态
})
