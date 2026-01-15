/**
 * 远程 JavaScript 脚本加载工具
 *
 * 功能说明：
 * 1. 动态加载外部 JavaScript 文件
 * 2. 支持自定义脚本 ID
 * 3. 自动处理重复加载（先删除再加载）
 * 4. 提供加载成功和失败的 Promise 回调
 *
 * 使用场景：
 * - 动态加载第三方 SDK
 * - 按需加载插件脚本
 * - 加载远程组件库
 * - 条件性加载功能模块
 *
 * 安全提示：
 * - 只加载可信的脚本源
 * - 验证 URL 安全性
 * - 考虑使用 Subresource Integrity (SRI)
 *
 * @example
 * import { loadScript } from '@/utils/RemoteJs'
 *
 * // 基本使用
 * loadScript('https://example.com/sdk.js')
 *   .then(() => console.log('脚本加载成功'))
 *   .catch(err => console.error('加载失败', err))
 *
 * // 使用自定义 ID
 * loadScript('https://example.com/sdk.js', 'my-sdk')
 *   .then(() => {
 *     // 使用加载的脚本
 *     window.SDK.init()
 *   })
 */

/**
 * 动态加载远程 JavaScript 脚本
 *
 * 在页面中动态创建 script 标签加载外部 JS 文件
 * 如果指定 ID 的脚本已存在，会先删除再加载
 *
 * @param url - 要加载的脚本 URL
 * @param jsId - 脚本标签 ID，默认为 'de-fit2cloud-script-id'
 * @returns Promise 对象
 *   - 成功时 resolve(null)
 *   - 失败时 reject(Error 对象)
 *
 * @example
 * // 加载第三方 SDK
 * await loadScript('https://cdn.example.com/sdk.js')
 *
 * // 使用自定义 ID 避免冲突
 * loadScript('https://cdn.example.com/sdk.js', 'custom-sdk-id')
 *   .then(() => {
 *     console.log('SDK 已加载')
 *     // 初始化 SDK
 *     window.ExampleSDK.init()
 *   })
 *   .catch(error => {
 *     console.error('SDK 加载失败', error)
 *   })
 *
 * @example
 * // 重复加载同一脚本（会先删除旧的）
 * loadScript('https://cdn.example.com/sdk.js', 'my-sdk')
 * loadScript('https://cdn.example.com/sdk-v2.js', 'my-sdk') // 替换上面的脚本
 */
export const loadScript = (url: string, jsId?: string) => {
  return new Promise(function (resolve, reject) {
    // 脚本 ID，默认使用固定 ID
    const scriptId = jsId || 'de-fit2cloud-script-id'

    // 如果已存在相同 ID 的脚本，先删除
    let dom = document.getElementById(scriptId)
    if (dom) {
      dom.parentElement?.removeChild(dom)
      dom = null
    }

    // 创建 script 标签
    const script = document.createElement('script')
    script.id = scriptId

    // 加载成功回调
    script.onload = function () {
      return resolve(null)
    }

    // 加载失败回调
    script.onerror = function () {
      return reject(new Error('Load script from '.concat(url, ' failed')))
    }

    // 设置脚本 URL
    script.src = url

    // 添加到页面
    const head = document.head || document.getElementsByTagName('head')[0]
    ;(document.body || head).appendChild(script)
  })
}
