/**
 * 几何变换工具函数
 *
 * 功能说明：
 * 1. 提供组件旋转相关的几何计算
 * 2. 支持组件缩放时的坐标转换
 * 3. 用于画布中组件的定位和尺寸计算
 *
 * 使用场景：
 * - 仪表板组件旋转
 * - 组件尺寸缩放
 * - 画布坐标计算
 * - 组件对齐和布局
 *
 * 数学原理：
 * - 使用旋转矩阵进行点坐标变换
 * - 公式：nx = cosθ * (ax - cx) - sinθ * (ay - cy) + cx
 *        ny = sinθ * (ax - cx) + cosθ * (ay - cy) + cy
 *
 * @example
 * import {
 *   calculateRotatedPointCoordinate,
 *   changeStyleWithScale
 * } from '@/utils/translate'
 */

import { divide, multiply, floor } from 'mathjs'
import { dvMainStoreWithOut } from '@/store/modules/data-visualization/dvMain'
import { storeToRefs } from 'pinia'

const dvMainStore = dvMainStoreWithOut()
const { canvasStyleData } = storeToRefs(dvMainStore)

// ==================== 角度转换 ====================

/**
 * 角度转弧度
 *
 * 将角度转换为弧度用于三角函数计算
 * 数学关系：Math.PI = 180 度
 *
 * @param angle - 角度值（度）
 * @returns 弧度值
 *
 * @example
 * angleToRadian(180)   // 3.14159... (π)
 * angleToRadian(90)    // 1.57079... (π/2)
 * angleToRadian(360)   // 6.28318... (2π)
 */
function angleToRadian(angle) {
  return (angle * Math.PI) / 180
}

// ==================== 点坐标计算 ====================

/**
 * 计算根据圆心旋转后的点的坐标
 *
 * 使用旋转矩阵公式计算点绕旋转中心旋转后的坐标
 *
 * @param point - 旋转前的点坐标 {x, y}
 * @param center - 旋转中心坐标 {x, y}
 * @param rotate - 旋转的角度（度）
 * @returns 旋转后的坐标 {x, y}
 *
 * 旋转公式：
 * - 点a(x, y)
 * - 旋转中心c(x, y)
 * - 旋转后点n(x, y)
 * - 旋转角度θ
 *
 * nx = cosθ * (ax - cx) - sinθ * (ay - cy) + cx
 * ny = sinθ * (ax - cx) + cosθ * (ay - cy) + cy
 *
 * @example
 * const point = { x: 100, y: 50 }
 * const center = { x: 50, y: 50 }
 * const rotated = calculateRotatedPointCoordinate(point, center, 90)
 * // 旋转 90 度后的坐标
 *
 * @see https://www.zhihu.com/question/67425734/answer/252724399 旋转矩阵公式
 */
export function calculateRotatedPointCoordinate(point, center, rotate) {
  /**
   * 旋转公式：
   *  点a(x, y)
   *  旋转中心c(x, y)
   *  旋转后点n(x, y)
   *  旋转角度θ                tan ??
   * nx = cosθ * (ax - cx) - sinθ * (ay - cy) + cx
   * ny = sinθ * (ax - cx) + cosθ * (ay - cy) + cy
   */

  return {
    x:
      (point.x - center.x) * Math.cos(angleToRadian(rotate)) -
      (point.y - center.y) * Math.sin(angleToRadian(rotate)) +
      center.x,
    y:
      (point.x - center.x) * Math.sin(angleToRadian(rotate)) +
      (point.y - center.y) * Math.cos(angleToRadian(rotate)) +
      center.y
  }
}

/**
 * 获取旋转后的点坐标（八个控制点之一）
 *
 * 计算组件八个控制点（上、下、左、右、左上、右上、左下、右下）
 * 在旋转后的坐标位置
 *
 * @param style - 组件样式对象 {left, top, width, height, rotate}
 * @param center - 组件中心点坐标 {x, y}
 * @param name - 点名称
 *   - 't': 上边中点
 *   - 'b': 下边中点
 *   - 'l': 左边中点
 *   - 'r': 右边中点
 *   - 'lt': 左上角
 *   - 'rt': 右上角
 *   - 'lb': 左下角
 *   - 'rb': 右下角（默认）
 * @returns 旋转后的点坐标 {x, y}
 *
 * @example
 * const style = { left: 100, top: 100, width: 200, height: 150, rotate: 45 }
 * const center = { x: 200, y: 175 }
 * const topLeft = getRotatedPointCoordinate(style, center, 'lt')
 */
export function getRotatedPointCoordinate(style, center, name) {
  let point // point 是未旋转前的坐标
  switch (name) {
    case 't':
      // 上边中点
      point = {
        x: style.left + style.width / 2,
        y: style.top
      }
      break
    case 'b':
      // 下边中点
      point = {
        x: style.left + style.width / 2,
        y: style.top + style.height
      }
      break
    case 'l':
      // 左边中点
      point = {
        x: style.left,
        y: style.top + style.height / 2
      }
      break
    case 'r':
      // 右边中点
      point = {
        x: style.left + style.width,
        y: style.top + style.height / 2
      }
      break
    case 'lt':
      // 左上角
      point = {
        x: style.left,
        y: style.top
      }
      break
    case 'rt':
      // 右上角
      point = {
        x: style.left + style.width,
        y: style.top
      }
      break
    case 'lb':
      // 左下角
      point = {
        x: style.left,
        y: style.top + style.height
      }
      break
    default: // rb - 右下角
      point = {
        x: style.left + style.width,
        y: style.top + style.height
      }
      break
  }

  return calculateRotatedPointCoordinate(point, center, style.rotate)
}

/**
 * 求两点之间的中点坐标
 *
 * @param p1 - 第一个点坐标 {x, y}
 * @param p2 - 第二个点坐标 {x, y}
 * @returns 中点坐标 {x, y}
 *
 * @example
 * const p1 = { x: 0, y: 0 }
 * const p2 = { x: 100, y: 100 }
 * getCenterPoint(p1, p2)  // { x: 50, y: 50 }
 */
export function getCenterPoint(p1, p2) {
  return {
    x: p1.x + (p2.x - p1.x) / 2,
    y: p1.y + (p2.y - p1.y) / 2
  }
}

// ==================== 三角函数工具 ====================

/**
 * 计算旋转角度的正弦值（绝对值）
 *
 * @param rotate - 旋转角度（度）
 * @returns 正弦值的绝对值
 *
 * @example
 * sin(0)    // 0
 * sin(90)   // 1
 * sin(180)  // 0
 */
export function sin(rotate) {
  return Math.abs(Math.sin(angleToRadian(rotate)))
}

/**
 * 计算旋转角度的余弦值（绝对值）
 *
 * @param rotate - 旋转角度（度）
 * @returns 余弦值的绝对值
 *
 * @example
 * cos(0)    // 1
 * cos(90)   // 0
 * cos(180)  // 1
 */
export function cos(rotate) {
  return Math.abs(Math.cos(angleToRadian(rotate)))
}

// ==================== 角度标准化 ====================

/**
 * 将角度标准化到 [0, 360) 范围
 *
 * 处理负角度和超过 360 度的角度
 *
 * @param deg - 原始角度（度）
 * @returns 标准化后的角度 [0, 360)
 *
 * @example
 * mod360(-90)   // 270
 * mod360(450)   // 90
 * mod360(180)   // 180
 */
export function mod360(deg) {
  return (deg + 360) % 360
}

// ==================== 缩放计算 ====================

/**
 * 根据缩放比例调整样式值
 *
 * 用于画布缩放时计算组件的实际尺寸和位置
 *
 * @param value - 原始值
 * @param scale - 缩放比例（百分比），默认使用画布当前缩放比例
 * @returns 缩放后的值（向下取整）
 *
 * @example
 * // 画布缩放为 80% 时
 * changeStyleWithScale(100)  // 80
 * changeStyleWithScale(250)  // 200
 */
export function changeStyleWithScale(value, scale = canvasStyleData.value.scale) {
  return floor(multiply(value, divide(parseInt(scale + ''), 100)))
}

/**
 * 将小数值转换为百分比字符串
 *
 * @param val - 小数值（0-1）
 * @returns 百分比字符串
 *
 * @example
 * toPercent(0.5)   // '50%'
 * toPercent(1)     // '100%'
 * toPercent(0.75)  // '75%'
 */
export function toPercent(val) {
  return val * 100 + '%'
}
