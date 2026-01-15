/**
 * 日期工具函数扩展
 *
 * 功能说明：
 * 1. 扩展 JavaScript 原生 Date 对象
 * 2. 提供 format 方法用于日期格式化
 * 3. 支持多种日期格式输出
 *
 * 使用场景：
 * - 日期显示格式化
 * - 时间戳转换
 * - 报表时间显示
 *
 * 注意事项：
 * - 此文件直接扩展了 Date.prototype
 * - 建议在项目初始化时引入（main.ts）
 * - 全局可用，任何 Date 对象都可调用 format 方法
 *
 * @example
 * // 基本使用
 * new Date().format()                           // '2024-01-13 12:30:45'
 * new Date().format('yyyy-MM-dd')               // '2024-01-13'
 * new Date().format('yyyy/MM/dd hh:mm:ss')      // '2024/01/13 12:30:45'
 * new Date().format('yyyy年MM月dd日')           // '2024年01月13日'
 */

// ==================== Date 原型扩展 ====================

/**
 * 日期格式化方法
 *
 * 扩展 Date 对象，支持多种日期格式化输出
 *
 * @param fmt - 格式化字符串，默认为 'yyyy-MM-dd hh:mm:ss'
 * @returns 格式化后的日期字符串
 *
 * 格式化占位符说明：
 * - yyyy: 四位年份
 * - MM: 月份（01-12）
 * - dd: 日期（01-31）
 * - hh: 小时（00-23）
 * - mm: 分钟（00-59）
 * - ss: 秒数（00-59）
 * - q: 季度（1-4）
 * - S: 毫秒数
 *
 * @example
 * const date = new Date('2024-01-13 12:30:45')
 * date.format()                    // '2024-01-13 12:30:45'
 * date.format('yyyy-MM-dd')        // '2024-01-13'
 * date.format('yyyy年MM月dd日')     // '2024年01月13日'
 */
Date.prototype['format'] = function (fmt) {
  // 默认格式：yyyy-MM-dd hh:mm:ss
  fmt = fmt || 'yyyy-MM-dd hh:mm:ss'

  // 日期各部分映射
  const o = {
    'M+': this.getMonth() + 1, // 月份（从0开始，需要+1）
    'd+': this.getDate(), // 日
    'h+': this.getHours(), // 小时
    'm+': this.getMinutes(), // 分
    's+': this.getSeconds(), // 秒
    'q+': Math.floor((this.getMonth() + 3) / 3), // 季度
    S: this.getMilliseconds() // 毫秒
  }

  // 处理年份
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length))
  }

  // 处理其他占位符
  for (const k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(
        RegExp.$1,
        RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
      )
    }
  }

  return fmt
}
