/**
 * 应用全局状态管理
 *
 * 功能说明：
 * 1. 管理应用的全局配置和状态
 * 2. 管理页面加载状态
 * 3. 管理应用运行模式（桌面/浏览器/嵌入式）
 * 4. 管理 UI 相关状态（尺寸图标、侧边栏等）
 * 5. 管理页面标题
 *
 * 状态说明：
 * - size: 尺寸图标显示状态
 * - pageLoading: 路由跳转时的加载状态
 * - title: 页面标题
 * - dekey: DataEase 密钥
 * - desktop: 是否为桌面模式
 * - isDataEaseBi: 是否为 DataEase BI 模式
 * - isIframe: 是否在 iframe 中运行
 * - arrowSide: 侧边栏箭头方向
 */

import { defineStore } from 'pinia'
import { store } from '../index'
import { useCache } from '@/hooks/web/useCache'
const { wsCache } = useCache()
import { modelApi } from '@/api/login'

// ==================== 类型定义 ====================

/**
 * 应用状态接口
 */
interface AppState {
  /** 尺寸图标显示状态 */
  size: boolean
  /** 页面路由跳转加载状态 */
  pageLoading: boolean
  /** 页面标题 */
  title: string
  /** DataEase 密钥 */
  dekey: string
  /** 是否为桌面模式 */
  desktop: boolean
  /** 是否为 DataEase BI 模式 */
  isDataEaseBi: boolean
  /** 是否在 iframe 中运行 */
  isIframe: boolean
  /** 侧边栏箭头方向 */
  arrowSide: boolean
}

export const useAppStore = defineStore('app', {
  // ==================== State ====================
  state: (): AppState => {
    return {
      size: true, // 尺寸图标
      pageLoading: false, // 路由跳转loading
      title: '',
      dekey: 'DataEaseKey',
      isDataEaseBi: false,
      isIframe: false,
      desktop: false,
      arrowSide: false
    }
  },

  // ==================== Getters ====================
  getters: {
    /** 获取尺寸图标状态 */
    getSize(): boolean {
      return this.size
    },
    /** 获取侧边栏箭头方向 */
    getArrowSide(): boolean {
      return this.arrowSide
    },
    /** 获取页面加载状态 */
    getPageLoading(): boolean {
      return this.pageLoading
    },
    /** 获取页面标题 */
    getTitle(): string {
      return this.title
    },
    /** 是否为 DataEase BI 模式 */
    getIsDataEaseBi(): boolean {
      return this.isDataEaseBi
    },
    /** 是否在 iframe 中运行 */
    getIsIframe(): boolean {
      return this.isIframe
    },
    /** 获取 DataEase 密钥 */
    getDekey(): string {
      return this.dekey
    },
    /** 是否为桌面模式 */
    getDesktop(): string {
      return this.desktop
    }
  },

  // ==================== Actions ====================
  actions: {
    /**
     * 设置应用模式
     * 从后端获取应用运行模式（桌面/浏览器）
     */
    async setAppModel() {
      const res = await modelApi()
      const data = res.data
      this.desktop = data
      wsCache.set('app.desktop', this.desktop) // 同步到缓存
    },

    /**
     * 设置尺寸图标状态
     * @param size 尺寸图标显示状态
     */
    setSize(size: boolean) {
      this.size = size
    },

    /**
     * 设置侧边栏箭头方向
     * @param ArrowSide 箭头方向
     */
    setArrowSide(ArrowSide: boolean) {
      this.arrowSide = ArrowSide
    },

    /**
     * 设置是否为 DataEase BI 模式
     * @param isDataEaseBi 是否为 DataEase BI 模式
     */
    setIsDataEaseBi(isDataEaseBi: boolean) {
      this.isDataEaseBi = isDataEaseBi
    },

    /**
     * 设置是否在 iframe 中运行
     * @param isIframe 是否在 iframe 中
     */
    setIsIframe(isIframe: boolean) {
      this.isIframe = isIframe
    },

    /**
     * 设置页面加载状态
     * @param pageLoading 加载状态
     */
    setPageLoading(pageLoading: boolean) {
      this.pageLoading = pageLoading
    },

    /**
     * 设置页面标题
     * 同时更新 document.title
     * @param title 页面标题
     */
    setTitle(title: string) {
      this.title = title
      document.title = title
    },

    /**
     * 设置 DataEase 密钥
     * @param dekey 密钥
     */
    setDekey(dekey: string) {
      this.dekey = dekey
    },

    /**
     * 设置桌面模式
     * @param desktop 是否为桌面模式
     */
    setDesktop(desktop: boolean) {
      wsCache.set('app.desktop', desktop)
      this.desktop = desktop
    }
  }
})

/**
 * 导出应用 Store 的便捷方法
 * 可以在组件外部使用
 */
export const useAppStoreWithOut = () => {
  return useAppStore(store)
}
