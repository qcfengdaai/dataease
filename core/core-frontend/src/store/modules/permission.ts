/**
 * 权限状态管理
 *
 * 功能说明：
 * 1. 管理用户可访问的路由列表
 * 2. 动态生成路由
 * 3. 路由权限验证
 * 4. 获取用户可访问的第一个菜单
 */

import { defineStore } from 'pinia'
import { routes } from '@/router'
import { generateRoutesFn2 } from '@/router/establish'
import { store } from '../index'
import { cloneDeep } from 'lodash-es'
import NotFoundPage from '@/views/404/index.vue'

// ==================== 类型定义 ====================

/**
 * 权限状态接口
 */
export interface PermissionState {
  /** 所有路由（静态 + 动态） */
  routers: AppRouteRecordRaw[]
  /** 动态添加的路由 */
  addRouters: AppRouteRecordRaw[]
  /** 是否已添加路由 */
  isAddRouters: boolean
  /** 当前访问路径 */
  currentPath: string
}

export const usePermissionStore = defineStore('permission', {
  // ==================== State ====================
  state: (): PermissionState => ({
    routers: [],
    addRouters: [],
    isAddRouters: false,
    currentPath: ''
  }),

  // ==================== Getters ====================
  getters: {
    /** 获取所有路由 */
    getRouters(): AppRouteRecordRaw[] {
      return this.routers
    },
    /** 获取非隐藏的路由（用于菜单渲染） */
    getRoutersNotHidden(): AppRouteRecordRaw[] {
      return this.routers.filter(ele => !ele.hidden)
    },
    /** 获取动态添加的路由 */
    getAddRouters(): AppRouteRecordRaw[] {
      return cloneDeep(this.addRouters)
    },
    /** 是否已添加路由 */
    getIsAddRouters(): boolean {
      return this.isAddRouters
    },
    /** 获取当前访问路径 */
    getCurrentPath(): boolean {
      return this.currentPath
    }
  },

  // ==================== Actions ====================
  actions: {
    /**
     * 清除路由
     * 重置为初始状态，用于退出登录
     */
    clear() {
      this.routers = cloneDeep(routes)
      this.addRouters = []
      this.isAddRouters = false
      this.currentPath = ''
    },

    /**
     * 生成路由
     * 根据后端返回的路由配置生成前端路由
     * @param routers 后端返回的路由配置
     * @returns Promise
     */
    generateRoutes(routers?: AppCustomRouteRecordRaw[] | string[]): Promise<unknown> {
      return new Promise<void>(resolve => {
        let routerMap: AppRouteRecordRaw[] = []
        routerMap = generateRoutesFn2(routers as AppCustomRouteRecordRaw[]) || []

        // 添加 404 页面作为兜底路由
        this.addRouters = routerMap.concat([
          {
            path: '/:catchAll(.*)',
            component: NotFoundPage,
            meta: {
              hidden: true
            }
          }
        ])

        // 合并静态路由和动态路由
        this.routers = cloneDeep(routes).concat(routerMap)
        resolve()
      })
    },

    /**
     * 设置当前访问路径
     * @param currentPath 当前路径
     */
    setCurrentPath(currentPath: string): void {
      this.currentPath = currentPath
    },

    /**
     * 设置是否已添加路由
     * @param state 是否已添加
     */
    setIsAddRouters(state: boolean): void {
      this.isAddRouters = state
    }
  }
})

/**
 * 导出权限 Store 的便捷方法
 */
export const usePermissionStoreWithOut = () => {
  return usePermissionStore(store)
}

// ==================== 路由验证函数 ====================

/**
 * 验证路径是否有效
 * 检查用户是否有权限访问该路径
 *
 * @param path 要验证的路径
 * @returns 是否有权限
 */
export const pathValid = path => {
  // 特殊处理数据集表单路径
  if (path?.startsWith('/dataset-form')) {
    path = '/data/dataset'
  }

  const permissionStore = usePermissionStore(store)
  const routers = permissionStore.getRouters
  const temp = path.startsWith('/') ? path.substr(1) : path
  const locations = temp.split('/')

  if (locations.length === 0) {
    return false
  }

  return hasCurrentRouter(locations, routers, 0)
}

/**
 * 递归验证每一层路由
 * @param locations 路径分段数组
 * @param routers 当前层级的路由列表
 * @param index 当前索引
 * @returns 是否有权限
 */
const hasCurrentRouter = (locations, routers, index) => {
  if (!routers?.length) {
    return false
  }

  const location = locations[index]
  let kids = []
  const isvalid = routers.some(router => {
    kids = router.children
    return router.path === location || '/' + location === router.path
  })

  if (isvalid && index < locations.length - 1) {
    return hasCurrentRouter(locations, kids, index + 1)
  }
  return isvalid
}

/**
 * 获取用户可访问的第一个菜单路径
 * @returns 第一个可访问的菜单路径
 */
export const getFirstAuthMenu = () => {
  const permissionStore = usePermissionStore(store)
  const routers = permissionStore.getRouters
  const nodePathArray = []

  getPathway(routers, nodePathArray)

  if (nodePathArray.length) {
    nodePathArray.reverse()
    return nodePathArray.join('/')
  }
  return null
}

/**
 * 递归获取菜单路径
 * @param tree 路由树
 * @param nodePathArray 路径数组
 * @returns 是否找到路径
 */
const getPathway = (tree, nodePathArray) => {
  for (let index = 0; index < tree.length; index++) {
    if (tree[index].children) {
      const endRecursiveLoop = getPathway(tree[index].children, nodePathArray)
      if (endRecursiveLoop) {
        nodePathArray.push(tree[index].path)
        return true
      }
    }
    if (!tree[index].children?.length && !tree[index].hidden) {
      nodePathArray.push(tree[index].path)
      return true
    }
  }
}
