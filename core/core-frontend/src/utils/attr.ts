/**
 * 属性配置常量
 * 定义组件属性的各种配置选项和映射关系
 */

// ==================== 位置属性配置 ====================

/**
 * 位置属性配置数组
 * 定义了组件位置和尺寸的可编辑属性
 */
export const positionData = [
  { key: 'left', label: 'X', min: -1000, max: 20000, step: 1 },
  { key: 'width', label: 'W', min: 2, max: 20000, step: 1 },
  { key: 'top', label: 'Y', min: -1000, max: 20000, step: 1 },
  { key: 'height', label: 'H', min: 2, max: 20000, step: 1 }
]

/**
 * 多维位置属性配置数组
 * 定义了 3D 空间中的旋转属性
 */
export const multiDimensionalData = [
  { key: 'x', label: 'X', min: -360, max: 360, step: 1 },
  { key: 'y', label: 'W', min: -360, max: 360, step: 1 },
  { key: 'z', label: 'Y', min: -360, max: 360, step: 1 }
]

// ==================== 样式属性配置 ====================

/**
 * 样式属性配置数组
 * 定义了组件样式的可编辑属性
 */
export const styleData = [
  { key: 'lineHeight', label: '行高', min: 0, max: 50, step: 1 },
  { key: 'opacity', label: '不透明度', min: 0, max: 1, step: 0.1 },
  { key: 'borderWidth', label: '边框宽度', min: 0, max: 20, step: 1 },
  { key: 'borderStyle', label: '边框风格' },
  { key: 'borderColor', label: '边框颜色' },
  { key: 'borderRadius', label: '圆角', min: 0, max: 50, step: 1 },
  { key: 'letterSpacing', label: '字间距', min: 0, max: 50, step: 1 },
  { key: 'fontSize', label: '字体大小', min: 0, max: 128, step: 1 },
  { key: 'activeFontSize', label: '激活字体大小', min: 0, max: 128, step: 1 },
  { key: 'headFontColor', label: '标题字体颜色' },
  { key: 'headFontActiveColor', label: '标题字体激活颜色' },
  { key: 'headBorderColor', label: '标题边框颜色' },
  { key: 'headBorderActiveColor', label: '标题激活边框颜色' },
  { key: 'headHorizontalPosition', label: '标题位置' },
  { key: 'fontWeight', label: '字体粗细', min: 100, max: 900, step: 100 },
  { key: 'textAlign', label: '左右对齐' },
  { key: 'verticalAlign', label: '上下对齐' },
  { key: 'color', label: '颜色' },
  { key: 'backgroundColor', label: '背景色' }
]

/**
 * 样式属性中文映射表
 * 将样式属性键名映射到中文标签
 */
export const styleMap = {
  left: 'x 坐标',
  top: 'y 坐标',
  rotate: '旋转角度',
  width: '宽',
  height: '高',
  color: '颜色',
  backgroundColor: '背景色',
  borderWidth: '边框宽度',
  borderStyle: '边框风格',
  borderColor: '边框颜色',
  borderRadius: '圆角',
  fontSize: '字体大小',
  fontWeight: '字体粗细',
  lineHeight: '行高',
  letterSpacing: '字间距',
  textAlign: '左右对齐',
  verticalAlign: '上下对齐',
  opacity: '不透明度'
}

// ==================== 选项配置 ====================

/**
 * 文本对齐选项
 */
export const textAlignOptions = [
  {
    label: '左对齐',
    value: 'left'
  },
  {
    label: '居中',
    value: 'center'
  },
  {
    label: '右对齐',
    value: 'right'
  }
]

/**
 * 边框风格选项
 */
export const borderStyleOptions = [
  {
    label: '实线',
    value: 'solid'
  },
  {
    label: '虚线',
    value: 'dashed'
  }
]

/**
 * 垂直对齐选项
 */
export const verticalAlignOptions = [
  {
    label: '上对齐',
    value: 'top'
  },
  {
    label: '居中对齐',
    value: 'middle'
  },
  {
    label: '下对齐',
    value: 'bottom'
  }
]

/**
 * 下拉选择类型的属性列表
 * 这些属性使用选项列表而不是数值输入
 */
export const selectKey = ['textAlign', 'borderStyle', 'verticalAlign']

/**
 * 水平位置属性列表
 */
export const horizontalPosition = ['headHorizontalPosition']

// ==================== 字段类型配置 ====================

/**
 * 字段类型代码列表
 */
export const fieldType = ['text', 'time', 'value', 'value', 'value', 'location', 'binary', 'url']

/**
 * 字段类型中文标签列表
 * 与 fieldType 数组一一对应
 */
export const fieldTypeText = [
  '文本',
  '时间',
  '数值',
  '数值(小数)',
  '数值',
  '地理位置',
  '文件',
  'URL'
]

/**
 * 选项映射表
 * 将属性键名映射到对应的选项列表
 */
export const optionMap = {
  textAlign: textAlignOptions,
  borderStyle: borderStyleOptions,
  verticalAlign: verticalAlignOptions
}
