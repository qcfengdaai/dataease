/**
 * 国际化状态管理
 *
 * 功能说明：
 * 1. 管理应用的语言设置
 * 2. 支持中英文切换
 * 3. 支持自定义语言包
 * 4. 管理 Element Plus 的语言包
 */

import { defineStore } from 'pinia'
import { store } from '../index'
import type { LocaleDropdownType } from 'types/localeDropdown'
import zhCn from 'element-plus-secondary/es/locale/lang/zh-cn'
import en from 'element-plus-secondary/es/locale/lang/en'
import tw from 'element-plus-secondary/es/locale/lang/zh-tw'
import { getLocale } from '@/utils/utils'
import request from '@/config/axios'

// ==================== Element Plus 语言映射 ====================

/**
 * Element Plus 语言包映射
 * 将语言代码映射到对应的 Element Plus 语言包
 */
const elLocaleMap = {
  'zh-CN': zhCn, // 简体中文
  en: en, // 英文
  tw: tw // 繁体中文
}

// ==================== 类型定义 ====================

/**
 * 国际化状态接口
 */
interface LocaleState {
  /** 自定义语言是否已加载 */
  customLoaded: boolean
  /** 当前语言 */
  currentLocale: LocaleDropdownType
  /** 语言列表 */
  localeMap: LocaleDropdownType[]
}

export const useLocaleStore = defineStore('locales', {
  // ==================== State ====================
  state: (): LocaleState => {
    return {
      customLoaded: false,
      currentLocale: {
        lang: getLocale(), // 从缓存或浏览器获取语言
        elLocale: elLocaleMap[getLocale()] // 对应的 Element Plus 语言包
      },
      // 多语言列表
      localeMap: [
        {
          lang: 'zh-CN',
          name: '简体中文'
        },
        {
          lang: 'en',
          name: 'English'
        },
        {
          lang: 'tw',
          name: '繁體中文'
        }
      ]
    }
  },

  // ==================== Getters ====================
  getters: {
    /** 获取当前语言 */
    getCurrentLocale(): LocaleDropdownType {
      return this.currentLocale
    },

    /**
     * 获取语言映射列表
     * 包括内置语言和自定义语言
     */
    async getLocaleMap(): Promise<LocaleDropdownType[]> {
      // 如果已加载自定义语言，直接返回
      if (this.customLoaded) {
        return this.localeMap
      }

      try {
        // 从后端获取自定义语言配置
        const res = await request.get({ url: '/sysParameter/i18nOptions' })
        this.customLoaded = true
        const customMap = res.data

        // 添加自定义语言到列表
        for (const key in customMap) {
          const item = {
            lang: key,
            name: customMap[key],
            custom: true
          }
          this.localeMap.push(item)
        }
        return this.localeMap
      } catch (error) {
        // 加载失败时标记为已加载，返回默认列表
        this.customLoaded = true
        return this.localeMap
      }
    }
  },

  // ==================== Actions ====================
  actions: {
    /**
     * 设置当前语言
     * @param localeMap 语言配置
     */
    setCurrentLocale(localeMap: LocaleDropdownType) {
      this.currentLocale.lang = localeMap?.lang
      this.currentLocale.elLocale = elLocaleMap[localeMap?.lang]
    },

    /**
     * 设置语言
     * @param language 语言代码
     */
    setLang(language: string) {
      this.currentLocale.lang = language
      this.currentLocale.elLocale = elLocaleMap[language]
    }
  }
})

/**
 * 导出语言 Store 的便捷方法
 */
export const useLocaleStoreWithOut = () => {
  return useLocaleStore(store)
}
