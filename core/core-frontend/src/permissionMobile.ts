/**
 * 路由权限控制 - 移动端
 *
 * 功能说明：
 * 1. 移动端页面访问权限控制
 * 2. 移动端用户登录状态检查
 * 3. 移动端动态路由加载
 * 4. 移动端专属白名单管理
 *
 * 与桌面端权限控制的区别：
 * - 使用移动端专属路由 (router/mobile)
 * - 白名单包含移动端特有页面 (如 /panel, /DashboardEmpty)
 * - 更简化的权限检查逻辑
 * - 不处理移动端重定向（因为已经是移动端）
 */

import router from './router/mobile'
import { useUserStoreWithOut } from '@/store/modules/user'
import { useNProgress } from '@/hooks/web/useNProgress'
import { usePageLoading } from '@/hooks/web/usePageLoading'
import { useCache } from '@/hooks/web/useCache'
import { getRoleRouters } from '@/api/common'
import { usePermissionStoreWithOut } from '@/store/modules/permission'
import { interactiveStoreWithOut } from '@/store/modules/interactive'
import { useAppearanceStoreWithOut } from '@/store/modules/appearance'
import { useLinkStoreWithOut } from '@/store/modules/link'

// ==================== Store 初始化 ====================
const appearanceStore = useAppearanceStoreWithOut() // 外观设置 Store
const permissionStore = usePermissionStoreWithOut() // 权限 Store
const { wsCache } = useCache() // 缓存工具
const userStore = useUserStoreWithOut() // 用户信息 Store
const linkStore = useLinkStoreWithOut() // 分享链接 Store

// ==================== 进度条和加载状态 ====================
const { start, done } = useNProgress() // 进度条开始和结束
const interactiveStore = interactiveStoreWithOut() // 交互设置 Store
const { loadStart, loadDone } = usePageLoading() // 页面加载状态

/**
 * 移动端白名单
 * 无需登录即可访问的移动端页面
 * - /login: 登录页面
 * - /panel: 面板页面
 * - /DashboardEmpty: 空白仪表板页面
 * - /preview: 预览页面
 */
const whiteList = ['/login', '/panel', '/DashboardEmpty', '/preview']

// ==================== 路由前置守卫 ====================
/**
 * 移动端路由前置守卫
 * @param to 即将进入的目标路由
 * @param _ 当前导航正要离开的路由（未使用）
 * @param next 路由跳转控制函数
 *
 * 主要处理逻辑：
 * 1. 启动进度条和加载状态
 * 2. 加载外观设置
 * 3. 检查是否是分享链接（直接放行）
 * 4. 已登录用户：加载用户信息和路由
 * 5. 未登录用户：检查白名单，不在白名单则跳转登录
 */
router.beforeEach(async (to, _, next) => {
  start() // 启动进度条
  loadStart() // 启动页面加载

  // ==================== 加载外观设置 ====================
  await appearanceStore.setAppearance()

  // ==================== 分享链接处理 ====================
  // 分享链接可以直接访问，不需要登录
  if (to.name === 'link') {
    next()
    return
  }

  // ==================== 已登录用户处理 ====================
  if (wsCache.get('user.token')) {
    // 清空分享链接的 token
    linkStore.setLinkToken('')

    // 获取用户信息
    if (!userStore.getUid) {
      await userStore.setUser()
    }

    // 如果访问登录页，重定向到首页
    if (to.path === '/login') {
      next({ path: '/index' })
    } else {
      // ==================== 动态加载路由 ====================
      // 获取用户可访问的路由
      const roleRouters = (await getRoleRouters()) || []
      const routers: any[] = roleRouters as AppCustomRouteRecordRaw[]
      routers.forEach(item => (item['top'] = true)) // 标记为顶级路由

      // 生成可访问的路由表
      await permissionStore.generateRoutes(routers as AppCustomRouteRecordRaw[])
      permissionStore.setIsAddRouters(true) // 标记路由已添加

      // 初始化交互设置
      await interactiveStore.initInteractive(true)

      next()
    }
  }
  // ==================== 未登录用户处理 ====================
  else {
    // 清空分享链接的 token
    linkStore.setLinkToken('')

    // 在白名单中的页面可以直接访问
    if (whiteList.includes(to.path) || to.name === 'link') {
      next()
    } else {
      // 其他页面跳转到登录页
      next('/login')
    }
  }
})

// ==================== 路由后置守卫 ====================
/**
 * 移动端路由后置守卫
 * 路由跳转完成后执行，结束进度条和加载状态
 */
router.afterEach(() => {
  done() // 结束进度条
  loadDone() // 结束加载状态
})
