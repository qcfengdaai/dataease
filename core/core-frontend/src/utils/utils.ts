import { BusiTreeNode } from '@/models/tree/TreeNode'
import { useCache } from '@/hooks/web/useCache'
import { loadScript } from '@/utils/RemoteJs'
import { ElMessage } from 'element-plus-secondary'

const { wsCache } = useCache()

/**
 * 深度克隆对象
 * @param target - 要克隆的目标对象
 * @returns 克隆后的新对象
 *
 * @example
 * const obj = { a: 1, b: { c: 2 }, d: new Date() }
 * const cloned = deepCopy(obj)
 * // cloned 是 obj 的深拷贝，互不影响
 */
export function deepCopy(target) {
  if (target === null || target === undefined) {
    return target
  } else if (typeof target == 'object') {
    const result = Array.isArray(target) ? [] : {}
    for (const key in target) {
      if (target[key] === null || target[key] === undefined) {
        result[key] = target[key]
      } else if (target[key] instanceof Date) {
        // 日期特殊处理
        result[key] = new Date(target[key])
      } else if (typeof target[key] == 'object') {
        result[key] = deepCopy(target[key])
      } else {
        result[key] = target[key]
      }
    }
    return result
  }
  return target
}

/**
 * 交换数组中两个元素的位置
 * @param arr - 目标数组
 * @param i - 第一个元素的索引
 * @param j - 第二个元素的索引
 *
 * @example
 * const arr = [1, 2, 3, 4]
 * swap(arr, 0, 2)
 * // arr 变为 [3, 2, 1, 4]
 */
export function swap(arr, i, j) {
  const temp = arr[i]
  arr[i] = arr[j]
  arr[j] = temp
}

/**
 * DOM 选择器（内部使用）
 * @param selector - CSS 选择器
 * @returns 匹配的第一个元素或 null
 * @deprecated 建议使用 $ 函数
 */
export function _$(selector) {
  return document.querySelector(selector)
}

/**
 * DOM 选择器
 * @param selector - CSS 选择器
 * @returns 匹配的第一个元素或 null
 *
 * @example
 * const div = $('.my-class')
 * const button = $('#submit-button')
 */
export function $(selector) {
  return document.querySelector(selector)
}

// 不允许拖拽的组件列表
const components = ['VText', 'RectShape', 'CircleShape']

/**
 * 检查组件是否阻止拖拽
 * @param component - 组件名称
 * @returns 是否阻止拖拽
 *
 * 组件允许拖拽的条件：
 * 1. 是预定义的组件（VText、RectShape、CircleShape）
 * 2. 或以 'SVG' 开头的组件
 */
export function isPreventDrop(component) {
  return !components.includes(component) && !component.startsWith('SVG')
}

/**
 * 为 URL 添加 http 协议前缀（如果缺失）
 * @param url - 待检查的 URL
 * @returns 处理后的 URL
 *
 * @example
 * checkAddHttp('example.com')      // 'http://example.com'
 * checkAddHttp('https://example.com') // 'https://example.com'
 * checkAddHttp('')                 // ''
 */
export function checkAddHttp(url) {
  if (!url) {
    return url
  } else if (/^(http(s)?:\/\/)/.test(url.toLowerCase())) {
    return url
  } else {
    return 'http://' + url
  }
}

/**
 * 为对象的名称字段设置搜索关键词高亮
 * @param obj - 目标对象
 * @param keyword - 搜索关键词
 * @param key - 名称字段的键名，默认为 'name'
 * @param colorKey - 颜色字段的键名，默认为 'colorName'
 *
 * 用于在搜索结果中高亮显示匹配的关键词
 */
export const setColorName = (obj, keyword: string, key?: string, colorKey?: string) => {
  key = key || 'name'
  colorKey = colorKey || 'colorName'
  if (!keyword) {
    obj[colorKey] = null
    return
  }
  const name = obj[key]
  const index = name.indexOf(keyword)
  if (index > -1) {
    const textCode =
      name.substring(0, index) +
      '<span class="search-key-span">' +
      keyword +
      '</span>' +
      name.substring(index + keyword.length, name.length)
    obj[colorKey] = textCode
    return
  }
  obj[colorKey] = null
}

/**
 * 从 URL 查询字符串中获取参数值
 * @param name - 参数名称
 * @returns 参数值或 null
 *
 * @example
 * // URL: http://example.com?id=123&name=test
 * getQueryString('id')    // '123'
 * getQueryString('name')  // 'test'
 */
export const getQueryString = (name: string) => {
  const reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i')
  const r = window.location.search.substr(1).match(reg)
  if (r != null) {
    return unescape(r[2])
  }
  return null
}

/**
 * 检查是否在飞书平台中
 * @returns 是否在飞书平台
 */
export const isLarkPlatform = () => {
  return !!getQueryString('state') && !!getQueryString('code')
}

/**
 * 检查是否在客户端平台中
 * @returns 是否在客户端平台
 */
export const isPlatformClient = () => {
  return !!getQueryString('client') || getQueryString('state')?.includes('client')
}

/**
 * 检查平台状态
 * @returns 平台检查结果
 *
 * 清除非特定平台的标识
 */
export const checkPlatform = () => {
  const flagArray = ['/casbi', 'oidcbi']
  const pathname = window.location.pathname
  if (
    !flagArray.some(flag => pathname.includes(flag)) &&
    !isLarkPlatform() &&
    !isPlatformClient()
  ) {
    return cleanPlatformFlag()
  }
  return true
}

/**
 * 清理平台标识
 * @returns false
 */
export const cleanPlatformFlag = () => {
  const platformKey = 'out_auth_platform'
  wsCache.delete(platformKey)
  return false
}

/**
 * 检查是否在 iframe 中
 * @returns 是否在 iframe 中
 */
export const isInIframe = () => {
  try {
    return window.top !== window.self
  } catch (error) {
    console.error(error)
    return true
  }
}

/**
 * 检查按钮是否显示
 * @param val - 按钮显示配置值：'0'=显示, '1'=隐藏, 其他=根据iframe状态判断
 * @returns 是否显示按钮
 */
export const isBtnShow = (val: string) => {
  if (!val || val === '0') {
    return true
  } else if (val === '1') {
    return false
  } else {
    return !isInIframe()
  }
}

/**
 * 检查是否为移动设备
 * @returns 是否为移动设备
 */
export function isMobile() {
  return (
    navigator.userAgent.match(
      /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i
    ) && !isTablet()
  )
}

/**
 * 检查是否为 iOS 移动设备
 * @returns 是否为 iOS 移动设备
 */
export function isISOMobile() {
  return navigator.userAgent.match(/(iPhone|iPad|iPod)/i) && !isTablet()
}

/**
 * 检查是否在钉钉中
 */
export const isDingTalk = window.navigator.userAgent.toLowerCase().includes('dingtalk')

/**
 * 设置页面标题
 * @param title - 页面标题
 *
 * 支持普通浏览器和钉钉客户端的标题设置
 */
export const setTitle = (title?: string) => {
  if (!isDingTalk) {
    document.title = title || 'DataEase'
    return
  }
  const jsUrl = 'https://g.alicdn.com/dingding/dingtalk-jsapi/3.0.25/dingtalk.open.js'
  const jsId = 'fit2cloud-dataease-v2-platform-client-dingtalk'
  if (window['dd'] && window['dd'].biz?.navigation?.setTitle) {
    window['dd'].biz.navigation.setTitle({
      title: title
    })
    return
  }
  const awaitMethod = loadScript(jsUrl, jsId)
  awaitMethod
    .then(() => {
      window['dd'].ready(() => {
        window['dd'].biz.navigation.setTitle({
          title: title
        })
      })
    })
    .catch(() => {
      document.title = title || 'DataEase'
    })
}

/**
 * 检查是否为平板设备
 * @returns 是否为平板设备
 */
export function isTablet() {
  const userAgent = navigator.userAgent
  const tabletRegex = /iPad|Silk|Galaxy Tab|PlayBook|BlackBerry|(tablet|ipad|playbook)/i
  return tabletRegex.test(userAgent)
}

/**
 * 从树结构中移除指定节点
 * @param tree - 树结构数组
 * @param targetId - 目标节点ID
 */
export function cutTargetTree(tree: BusiTreeNode[], targetId: string | number) {
  tree.forEach((node, index) => {
    if (node.id === targetId) {
      tree.splice(index, 1)
      return
    } else if (node.children) {
      cutTargetTree(node.children, targetId)
    }
  })
}

/**
 * 检查是否为链接视图
 * @returns 是否为链接视图
 */
export const isLink = () => {
  return window.location.hash.startsWith('#/de-link/')
}

/**
 * 检查值是否为空
 * @param arg - 待检查的值
 * @returns 是否为空（undefined、null 或字符串 'null'）
 */
export const isNull = arg => {
  return typeof arg === 'undefined' || arg === null || arg === 'null'
}

/**
 * 导出权限数据
 * @param weight - 权重
 * @param ext - 扩展权限
 * @returns 权限数组 [使用, 管理, 导出]
 */
export const exportPermission = (weight, ext) => {
  const result = [0, 0, 0]
  if (!weight || weight === 1) {
    return result
  } else if (weight === 9) {
    return [1, 1, 1]
  }
  if (!ext) {
    return result
  }
  const extArray = formatExt(ext) || []
  for (let index = 0; index < extArray.length; index++) {
    result[index] = extArray[index]
  }
  return result
}

/**
 * 格式化扩展数据
 * @param num - 数字
 * @returns 反转后的数字数组
 *
 * 将数字按位反转并转换为数组
 * 例如：123 -> [3, 2, 1]
 */
export const formatExt = (num: number): number[] | null => {
  if (!num) {
    return null
  }
  const reversedStr = num.toString().split('').reverse().join('')
  const reversedNumArray = reversedStr?.split('')?.map(Number) ?? []
  return reversedNumArray
}

/**
 * 获取浏览器语言设置
 * @returns 语言代码（如 'zh-CN'、'en'）
 */
export const getBrowserLocale = () => {
  const language = navigator.language
  if (!language) {
    return 'zh-CN'
  }
  if (language.startsWith('en')) {
    return 'en'
  }
  if (language.toLowerCase().startsWith('zh')) {
    const temp = language.toLowerCase().replace('_', '-')
    return temp === 'zh' ? 'zh-CN' : temp === 'zh-cn' ? 'zh-CN' : 'tw'
  }
  return language
}

/**
 * 获取当前语言设置
 * @returns 语言代码
 *
 * 优先级：缓存 > 浏览器设置 > 默认值
 */
export const getLocale = () => {
  return wsCache.get('user.language') || getBrowserLocale() || 'zh-CN'
}

/**
 * 检查是否为免费文件夹
 * @param node - 树节点
 * @param flag - 标志位
 * @returns 是否为免费文件夹
 */
export const isFreeFolder = (node, flag) => {
  const oid = wsCache.get('user.oid')
  if (!oid) {
    return false
  }
  const freeRootId = (Number(oid) + flag).toString()
  let cNode = node
  while (cNode) {
    const data = cNode.data
    const id = data['id']
    if (id === freeRootId) {
      return true
    }
    cNode = cNode['parent']
  }
  return false
}

/**
 * 过滤免费文件夹
 * @param list - 文件夹列表
 * @param flagText - 标志文本
 *
 * 从列表中移除免费文件夹节点
 */
export const filterFreeFolder = (list, flagText) => {
  const flagArray = ['dashboard', 'dataV', 'dataset', 'datasource']
  const index = flagArray.findIndex(item => item === flagText)
  const oid = wsCache.get('user.oid')
  if (!oid || index < 0) {
    return
  }
  const freeRootId = (Number(oid) + index + 1).toString()
  let len = list.length
  while (len--) {
    const node = list[len]
    if (node['id'] === freeRootId) {
      list.splice(len, 1)
      return
    }
    if (node['id'] === '0') {
      const children = node['children']
      let innerLen = children?.length
      while (innerLen--) {
        const kid = children[innerLen]
        if (kid['id'] === freeRootId) {
          children.splice(innerLen, 1)
          return
        }
      }
    }
  }
}

/**
 * 名称字段修剪和验证
 * @param target - 包含 name 字段的对象
 * @param msg - 错误提示信息
 * @throws {Error} 当名称长度不符合要求时抛出异常
 */
export const nameTrim = (target: {}, msg = '名称字段长度1-64个字符') => {
  if (target.name) {
    target.name = target.name.trim()
    if (target.name.length < 1 || target.name.length > 64) {
      ElMessage.warning(msg)
      throw new Error(msg)
    }
  }
}

/**
 * 获取激活的分类列表
 * @param contents - 内容列表
 * @returns 包含"最近使用"和所有显示分类的集合
 */
export const getActiveCategories = contents => {
  const result = ['最近使用']
  if (contents) {
    contents.forEach(item => {
      if (item.showFlag) {
        item.categories.forEach(category => {
          result.push(category.name)
        })
      }
    })
  }
  return new Set(result)
}
