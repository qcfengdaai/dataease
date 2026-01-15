/**
 * 编辑器自定义事件
 *
 * 功能说明：
 * 1. 提供可配置的编辑器事件
 * 2. 支持动态注入事件处理器
 * 3. 用于富文本编辑器等自定义组件
 *
 * 支持的事件：
 * - redirect: 页面跳转事件
 * - alert: 弹窗提示事件
 *
 * 使用场景：
 * - 自定义编辑器组件
 * - 动态事件绑定
 * - 组件间通信
 *
 * @example
 * import { mixins, eventList } from '@/utils/events'
 *
 * // 在组件中使用 mixins
 * export default {
 *   mixins: [mixins],
 *   methods: {
 *     handleClick() {
 *       this.redirect('/home')
 *     }
 *   }
 * }
 */

// ==================== 事件定义 ====================

/**
 * 编辑器自定义事件集合
 *
 * 定义了可以在编辑器中使用的自定义事件
 */
const events = {
  /**
   * 页面跳转事件
   *
   * 跳转到指定的 URL
   *
   * @param url - 目标 URL
   *
   * @example
   * events.redirect('/home')
   * events.redirect('https://example.com')
   */
  redirect(url) {
    if (url) {
      window.location.href = url
    }
  },

  /**
   * Alert 弹窗事件
   *
   * 显示浏览器原生弹窗提示
   *
   * @param msg - 提示消息
   *
   * @example
   * events.alert('操作成功')
   * events.alert('发生错误')
   */
  alert(msg) {
    if (msg) {
      // eslint-disable-next-line no-alert
      alert(msg)
    }
  }
}

// ==================== Mixin 支持 ====================

/**
 * Vue Mixin 对象
 *
 * 将事件方法作为 Vue 组件的 methods 混入
 * 可以在组件中直接调用 this.redirect()、this.alert()
 *
 * @example
 * export default {
 *   mixins: [mixins],
 *   methods: {
 *     handleAction() {
 *       this.redirect('/dashboard')
 *     }
 *   }
 * }
 */
const mixins = {
  methods: events
}

// ==================== 事件列表 ====================

/**
 * 事件配置列表
 *
 * 用于动态生成事件选择器或配置界面
 * 每个事件包含：
 * - key: 事件唯一标识
 * - label: 事件显示名称
 * - event: 事件处理函数
 * - param: 事件参数描述
 *
 * @example
 * // 遍历事件列表
 * eventList.forEach(item => {
 *   console.log(item.key, item.label)
 * })
 */
const eventList = [
  {
    key: 'redirect',
    label: '跳转事件',
    event: events.redirect,
    param: ''
  },
  {
    key: 'alert',
    label: 'alert 事件',
    event: events.alert,
    param: ''
  }
]

// ==================== 导出 ====================

export { mixins, events, eventList }
