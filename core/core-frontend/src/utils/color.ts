/**
 * 颜色处理工具函数
 * 提供颜色格式转换和 CSS 变量读取功能
 */

/**
 * RGB 颜色值转换为十六进制
 * @param r - 红色分量 (0-255)
 * @param g - 绿色分量 (0-255)
 * @param b - 蓝色分量 (0-255)
 * @returns 十六进制颜色字符串（大写）
 *
 * @example
 * rgbToHex(255, 0, 0)    // '#FF0000'
 * rgbToHex(0, 255, 0)    // '#00FF00'
 */
function rgbToHex(r, g, b) {
  // 确保数值在0-255范围内
  r = Math.max(0, Math.min(255, r))
  g = Math.max(0, Math.min(255, g))
  b = Math.max(0, Math.min(255, b))

  // 转换为16进制并补零
  const hexR = r.toString(16).padStart(2, '0')
  const hexG = g.toString(16).padStart(2, '0')
  const hexB = b.toString(16).padStart(2, '0')

  return `#${hexR}${hexG}${hexB}`.toUpperCase()
}

/**
 * RGBA 颜色值转换为十六进制（包含透明度）
 * @param r - 红色分量 (0-255)
 * @param g - 绿色分量 (0-255)
 * @param b - 蓝色分量 (0-255)
 * @param a - 透明度 (0-1)
 * @returns 十六进制颜色字符串（包含透明度，大写）
 *
 * @example
 * rgbaToHex(255, 0, 0, 0.5)  // '#FF000080'
 * rgbaToHex(0, 255, 0, 1)     // '#00FF00FF'
 */
function rgbaToHex(r, g, b, a) {
  // 处理RGB部分
  const hexR = Math.max(0, Math.min(255, r)).toString(16).padStart(2, '0')
  const hexG = Math.max(0, Math.min(255, g)).toString(16).padStart(2, '0')
  const hexB = Math.max(0, Math.min(255, b)).toString(16).padStart(2, '0')

  // 处理透明度（可选）
  const hexA =
    a !== undefined
      ? Math.round(Math.max(0, Math.min(1, a)) * 255)
          .toString(16)
          .padStart(2, '0')
      : ''

  return `#${hexR}${hexG}${hexB}${hexA}`.toUpperCase()
}

/**
 * 颜色字符串转换为十六进制格式
 * @param colorStr - RGB 或 RGBA 格式的颜色字符串
 * @returns 十六进制颜色字符串或 null
 *
 * 支持的格式：
 * - rgb(r, g, b)
 * - rgba(r, g, b, a)
 *
 * @example
 * colorStringToHex('rgb(255, 0, 0)')      // '#FF0000'
 * colorStringToHex('rgba(255, 0, 0, 0.5)') // '#FF000080'
 * colorStringToHex('invalid')              // null
 */
export function colorStringToHex(colorStr) {
  // 提取颜色值
  const rgbRegex =
    /^(rgb|rgba)\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*(?:,\s*(\d+(?:\.\d+)?)\s*)?\)$/
  const match = colorStr.match(rgbRegex)
  if (!match) return null

  const r = parseInt(match[2])
  const g = parseInt(match[3])
  const b = parseInt(match[4])
  const a = match[5] ? parseFloat(match[5]) : undefined

  return a !== undefined ? rgbaToHex(r, g, b, a) : rgbToHex(r, g, b)
}

/**
 * 获取 CSS 变量的值
 * @param element - DOM 元素，默认为 document.body
 * @param property - CSS 变量名，默认为 '--ed-color-primary'
 * @returns CSS 变量的值或默认值
 *
 * @example
 * getCSSVariable()                              // 获取主题色
 * getCSSVariable(document.body, '--bg-color')  // 获取背景色
 */
export function getCSSVariable(element = document.body, property = '--ed-color-primary') {
  const style = window.getComputedStyle(element)
  return style.getPropertyValue(property) || '#3370FF'
}
