/**
 * 用户状态管理
 *
 * 功能说明：
 * 1. 管理用户登录状态和基本信息
 * 2. 管理 Token 和登录过期时间
 * 3. 管理用户语言设置
 * 4. 提供 Setter 方法更新用户状态
 * 5. 提供 Clear 方法清除用户状态（登出）
 *
 * 状态说明：
 * - token: 用户认证令牌
 * - uid: 用户 ID
 * - name: 用户名
 * - oid: 组织 ID
 * - language: 语言设置
 * - exp: Token 过期时间戳
 * - time: 登录时间戳
 */

import { defineStore } from 'pinia'
import { store } from '../index'
import { useCache } from '@/hooks/web/useCache'
import { useLocaleStoreWithOut } from './locale'
import { useLocale } from '@/hooks/web/useLocale'

const { wsCache } = useCache()
const { changeLocale } = useLocale()

// ==================== 类型定义 ====================

/**
 * 用户状态接口
 */
interface UserState {
  /** 用户认证令牌 */
  token: string
  /** 用户 ID */
  uid: string
  /** 用户名 */
  name: string
  /** 组织 ID */
  oid: string
  /** 语言设置 */
  language: string
  /** Token 过期时间（时间戳） */
  exp: number
  /** 登录时间（时间戳） */
  time: number
}

export const userStore = defineStore('user', {
  // ==================== State ====================
  state: (): UserState => {
    return {
      token: null,
      uid: null,
      name: null,
      oid: null,
      language: 'zh-CN', // 默认中文
      exp: null,
      time: null
    }
  },

  // ==================== Getters ====================
  getters: {
    /** 获取用户 Token */
    getToken(): string {
      return this.token
    },
    /** 获取用户 ID */
    getUid(): string {
      return this.uid
    },
    /** 获取用户名 */
    getName(): string {
      return this.name
    },
    /** 获取组织 ID */
    getOid(): string {
      return this.oid
    },
    /** 获取语言设置 */
    getLanguage(): string {
      return this.language
    },
    /** 获取 Token 过期时间 */
    getExp(): number {
      return this.exp
    },
    /** 获取登录时间 */
    getTime(): number {
      return this.time
    }
  },

  // ==================== Actions ====================
  actions: {
    /**
     * 设置用户信息
     * 从后端 API 获取用户信息并更新状态
     * 同时更新缓存和语言设置
     */
    async setUser() {
      const user = await import('@/api/user')
      const res = await user.userInfo()
      const data = res.data

      // 从缓存获取 token 和时间信息
      data.token = wsCache.get('user.token')
      data.exp = wsCache.get('user.exp')
      data.time = wsCache.get('user.time')

      const keys: string[] = ['token', 'uid', 'name', 'oid', 'language', 'exp', 'time']

      keys.forEach(key => {
        const dkey = key === 'uid' ? 'id' : key // 后端返回的是 id，前端使用 uid
        this[key] = data[dkey]
        wsCache.set('user.' + key, this[key]) // 同步到缓存
      })

      const locale = useLocaleStoreWithOut()
      // 如果语言发生变化，重新加载页面
      if (locale.getCurrentLocale?.lang !== this.language && !window.DataEaseBi) {
        window.location.reload()
      }
      this.setLanguage(this.language)
    },

    /**
     * 设置用户 Token
     * @param token 用户认证令牌
     */
    setToken(token: string) {
      wsCache.set('user.token', token)
      this.token = token
    },

    /**
     * 设置 Token 过期时间
     * @param exp 过期时间戳
     */
    setExp(exp: number) {
      wsCache.set('user.exp', exp)
      this.exp = exp
    },

    /**
     * 设置登录时间
     * @param time 登录时间戳
     */
    setTime(time: number) {
      wsCache.set('user.time', time)
      this.time = time
    },

    /**
     * 设置用户 ID
     * @param uid 用户 ID
     */
    setUid(uid: string) {
      wsCache.set('user.uid', uid)
      this.uid = uid
    },

    /**
     * 设置用户名
     * @param name 用户名
     */
    setName(name: string) {
      wsCache.set('user.name', name)
      this.name = name
    },

    /**
     * 设置组织 ID
     * @param oid 组织 ID
     */
    setOid(oid: string) {
      wsCache.set('user.oid', oid)
      this.oid = oid
    },

    /**
     * 设置语言
     * @param language 语言代码（如：zh-CN, en-US）
     */
    setLanguage(language: string) {
      const locale = useLocaleStoreWithOut()
      // 兼容旧的语言代码格式
      if (!language || language === 'zh_CN') {
        language = 'zh-CN'
      }
      wsCache.set('user.language', language)
      this.language = language
      locale.setLang(language) // 更新语言 store
      changeLocale(language as any) // 更新 i18n
    },

    /**
     * 清除用户信息
     * 登出时调用，清除所有用户相关的状态和缓存
     */
    clear() {
      const keys: string[] = ['token', 'uid', 'name', 'oid', 'language', 'exp', 'time']
      keys.forEach(key => wsCache.delete('user.' + key))
    }
  }
})

/**
 * 导出用户 Store 的便捷方法
 * 可以在组件外部使用
 */
export const useUserStoreWithOut = () => {
  return userStore(store)
}
