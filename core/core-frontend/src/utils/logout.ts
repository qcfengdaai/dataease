/**
 * 用户登出工具
 *
 * 功能说明：
 * 1. 统一的用户登出处理逻辑
 * 2. 清理用户状态、权限状态、缓存数据
 * 3. 支持多种认证方式（OIDC、CAS、自定义）
 * 4. 自动跳转到登录页面或第三方登出地址
 *
 * 支持的认证方式：
 * - 默认登录：跳转到应用登录页
 * - OIDC 单点登录：跳转到 OIDC 登出端点
 * - CAS 单点登录：跳转到 CAS 登出端点
 * - 自定义认证：跳转到配置的自定义登出地址
 *
 * @example
 * import { logoutHandler } from '@/utils/logout'
 *
 * // 完整登出并跳转到登录页
 * logoutHandler()
 *
 * // 只清理状态不跳转（保存当前平台状态）
 * logoutHandler(false, true)
 *
 * // 清理状态并跳转到当前页面（登录后返回）
 * logoutHandler(true)
 */

import { useUserStoreWithOut } from '@/store/modules/user'
import router from '@/router'
import { usePermissionStoreWithOut } from '@/store/modules/permission'
import { interactiveStoreWithOut } from '@/store/modules/interactive'
import { useCache } from '@/hooks/web/useCache'

const { wsCache } = useCache()
const permissionStore = usePermissionStoreWithOut()
const userStore = useUserStoreWithOut()
const interactiveStore = interactiveStoreWithOut()

// ==================== 登出处理 ====================

/**
 * 用户登出处理函数
 *
 * 执行完整的登出流程：
 * 1. 清理用户状态（token、用户信息等）
 * 2. 清理权限状态（路由、菜单等）
 * 3. 清理交互状态
 * 4. 清理缓存数据
 * 5. 根据认证方式跳转到相应页面
 *
 * @param justClean - 是否只清理状态
 *   - false: 跳转到登录页（默认）
 *   - true: 跳转到当前页面的重定向路径
 *
 * @param save_platform_status - 是否保存平台状态
 *   - false: 执行完整登出（默认）
 *   - true: 只清理本地状态，不执行第三方登出
 *
 * @example
 * // 完整登出，跳转到登录页
 * logoutHandler()
 *
 * // 清理状态，登录后返回当前页面
 * logoutHandler(true)
 *
 * // 只清理状态，不跳转到第三方登出
 * logoutHandler(false, true)
 */
export const logoutHandler = (justClean?: boolean, save_platform_status = false) => {
  // 清理用户状态
  userStore.clear()
  userStore.$reset()

  // 清理权限状态
  permissionStore.clear()
  permissionStore.$reset()

  // 清理交互状态
  interactiveStore.clear()
  interactiveStore.$reset()

  // 清理特定缓存
  removeCache()

  // 默认重定向路径
  let queryRedirectPath = '/workbranch/index'

  // 如果当前路由有值，使用当前路由作为重定向目标
  if (router.currentRoute.value.fullPath) {
    queryRedirectPath = router.currentRoute.value.fullPath as string
  }

  // 处理不同的认证方式
  let pathname = window.location.pathname
  if (pathname) {
    // OIDC 单点登录
    if (pathname.includes('oidcbi/')) {
      if (save_platform_status) {
        return
      }
      pathname = pathname.replace('oidcbi/', '')
      if (pathname.includes('mobile.html')) {
        pathname = pathname.replace('mobile.html', '')
      }
      pathname = pathname.substring(0, pathname.length - 1)
      window.location.href = pathname + '/oidcbi/oidc/logout'
      return
    }
    // CAS 单点登录
    else if (pathname.includes('casbi/')) {
      if (save_platform_status) {
        return
      }
      pathname = pathname.replace('casbi/', '')
      if (pathname.includes('mobile.html')) {
        pathname = pathname.replace('mobile.html', '')
      }
      pathname = pathname.substring(0, pathname.length - 1)
      const uri = window.location.href
      window.location.href = pathname + '/casbi/cas/logout?service=' + uri
      return
    }
    pathname = pathname.substring(0, pathname.length - 1)
  }

  // 自定义认证登出地址
  if (wsCache.get('custom_auth_logout_url')) {
    window.location.href = wsCache.get('custom_auth_logout_url')
  }

  // 默认登录页跳转
  router.push(justClean ? queryRedirectPath : `/login?redirect=${queryRedirectPath}`)
}

// ==================== 缓存清理 ====================

/**
 * 清理特定缓存数据
 *
 * 删除与插件、平台、密码策略、分布式模式相关的缓存
 * 确保登出后敏感信息被清除
 *
 * @example
 * // 清理的缓存键包括：
 * // - de-plugin-*: 所有插件相关缓存
 * // - de-platform-client: 平台客户端信息
 * // - pwd-validity-period: 密码有效期
 * // - xpack-model-distributed: 分布式模式标识
 */
const removeCache = () => {
  const keys = Object.keys(wsCache['storage'])
  keys.forEach(key => {
    if (
      key.startsWith('de-plugin-') ||
      key === 'de-platform-client' ||
      key === 'pwd-validity-period' ||
      key === 'xpack-model-distributed'
    ) {
      wsCache.delete(key)
    }
  })
}
