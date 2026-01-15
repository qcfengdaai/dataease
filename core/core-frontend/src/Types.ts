/**
 * 通用类型定义
 *
 * 本文件定义了整个应用中使用的通用类型
 * 主要包括边距、圆角等 UI 相关的配置类型
 */

/**
 * 简写模式枚举
 * 用于定义不同的边值设置模式
 */
export enum ShorthandMode {
  /**
   * 统一模式
   * 所有边/角使用相同的值
   * 例如：margin: 16px（四个边都是 16px）
   */
  Uniform = 'uniform',

  /**
   * 逐边模式
   * 可单独设置每条边/角的值
   * 例如：margin: 10px 20px 10px 20px（分别对应上右下左）
   */
  PerEdge = 'per_edge'
}

/**
 * 边距值接口
 * 用于定义元素的上、右、下、左边距
 *
 * @example
 * // 统一模式
 * const padding: EdgeValues = {
 *   mode: ShorthandMode.Uniform,
 *   top: 16  // 所有边都是 16
 * }
 *
 * @example
 * // 逐边模式
 * const margin: EdgeValues = {
 *   mode: ShorthandMode.PerEdge,
 *   top: 10,
 *   right: 20,
 *   bottom: 10,
 *   left: 20
 * }
 */
export interface EdgeValues {
  /**
   * 设置模式
   * @default ShorthandMode.Uniform
   */
  mode?: ShorthandMode

  /**
   * 上边距值
   * 单位：像素
   */
  top?: number

  /**
   * 右边距值
   * 单位：像素
   */
  right?: number

  /**
   * 下边距值
   * 单位：像素
   */
  bottom?: number

  /**
   * 左边距值
   * 单位：像素
   */
  left?: number
}

/**
 * 圆角值接口
 * 用于定义元素的四个角的圆角半径
 *
 * @example
 * // 统一模式
 * const borderRadius: CornerValues = {
 *   mode: ShorthandMode.Uniform,
 *   topLeft: 8  // 所有角都是 8
 * }
 *
 * @example
 * // 逐角模式
 * const borderRadius: CornerValues = {
 *   mode: ShorthandMode.PerEdge,
 *   topLeft: 8,
 *   topRight: 8,
 *   bottomLeft: 0,
 *   bottomRight: 0
 * }
 */
export interface CornerValues {
  /**
   * 设置模式
   * @default ShorthandMode.Uniform
   */
  mode?: ShorthandMode

  /**
   * 左上角圆角半径
   * 单位：像素
   */
  topLeft?: number

  /**
   * 右上角圆角半径
   * 单位：像素
   */
  topRight?: number

  /**
   * 左下角圆角半径
   * 单位：像素
   */
  bottomLeft?: number

  /**
   * 右下角圆角半径
   * 单位：像素
   */
  bottomRight?: number
}
