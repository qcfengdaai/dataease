/**
 * 样式工具函数
 * 提供组件样式的获取和处理功能
 */

import { sin, cos, toPercent } from '@/utils/translate'
import { imgUrlTrans } from '@/utils/imgUtils'
import { hexColorToRGBA } from '@/views/chart/components/js/util'
import { dvMainStoreWithOut } from '@/store/modules/data-visualization/dvMain'
import { isMainCanvas, isTabCanvas } from '@/utils/canvasUtils'

const dvMainStore = dvMainStoreWithOut()

// ==================== 需要添加单位的样式属性 ====================

/**
 * 需要添加 'px' 单位的样式属性列表
 */
const needUnit = [
  'fontSize',
  'width',
  'height',
  'top',
  'left',
  'borderWidth',
  'letterSpacing',
  'borderRadius'
]

// ==================== 样式获取函数 ====================

/**
 * 获取形状组件的样式
 * @param style - 原始样式对象
 * @returns 处理后的样式对象
 *
 * 将样式对象转换为 CSS 样式字符串格式：
 * - 数值属性添加 'px' 单位
 * - rotate 转换为 transform
 */
export function getShapeStyle(style) {
  const result = {}
  ;['width', 'height', 'top', 'left', 'rotate'].forEach(attr => {
    if (attr != 'rotate') {
      result[attr] = style[attr] + 'px'
    } else {
      result['transform'] = 'rotate(' + style[attr] + 'deg)'
    }
  })

  return result
}

/**
 * 获取形状组件的样式（适配不同显示模式）
 * @param item - 组件对象
 * @param options - 配置选项
 * @param options.dvModel - 显示模式：'dashboard' 或 'dataV'
 * @param options.cellWidth - 单元格宽度
 * @param options.cellHeight - 单元格高度
 * @param options.curGap - 当前间距
 * @param options.showPosition - 显示位置：'preview' 或其他
 * @returns 处理后的样式对象
 *
 * 根据不同的显示模式和画布类型计算组件的位置和尺寸
 */
export function getShapeItemStyle(
  item,
  { dvModel, cellWidth, cellHeight, curGap, showPosition = 'preview' }
) {
  let result = {}
  if (dvModel === 'dashboard' && !item['isPlayer']) {
    // 仪表板模式：使用网格系统定位
    result = {
      padding: curGap + 'px!important',
      width: cellWidth * item.sizeX + 'px',
      height: cellHeight * item.sizeY + 'px',
      left: cellWidth * (item.x - 1) + 'px',
      top: cellHeight * (item.y - 1) + 'px'
    }
  } else if (dvModel === 'dataV' && isTabCanvas(item.canvasId) && showPosition === 'preview') {
    // 大屏模式的标签页：使用百分比定位
    result = {
      padding: curGap + 'px!important',
      width: toPercent(item.groupStyle.width),
      height: toPercent(item.groupStyle.height),
      top: toPercent(item.groupStyle.top),
      left: toPercent(item.groupStyle.left)
    }
  } else {
    // 其他模式：使用绝对像素定位
    result = {
      padding: curGap + 'px!important',
      width: item.style.width + 'px',
      height: item.style.height + 'px',
      left: item.style.left + 'px',
      top: item.style.top + 'px'
    }
  }

  return result
}

/**
 * 同步组件的样式属性
 * @param item - 组件对象
 * @param cellWidth - 单元格宽度
 * @param cellHeight - 单元格高度
 *
 * 根据网格坐标同步组件的实际样式属性
 */
export function syncShapeItemStyle(item, cellWidth, cellHeight) {
  item.style.left = cellWidth * (item.x - 1)
  item.style.top = cellHeight * (item.y - 1)
  item.style.width = cellWidth * item.sizeX
  item.style.height = cellHeight * item.sizeY
}

/**
 * 获取 SVG 组件的样式
 * @param style - 原始样式对象
 * @param filter - 需要过滤的属性数组
 * @returns 处理后的样式对象
 *
 * 专门用于 SVG 组件的样式处理
 */
export function getSVGStyle(style, filter = []) {
  const result = {}

  ;[
    'opacity',
    'width',
    'height',
    'top',
    'left',
    'rotate',
    'fontSize',
    'fontWeight',
    'lineHeight',
    'letterSpacing',
    'textAlign',
    'color'
  ].forEach(key => {
    if (!filter.includes(key)) {
      if (key != 'rotate') {
        if (style[key] !== '') {
          result[key] = style[key]

          if (needUnit.includes(key)) {
            result[key] += 'px'
          }
        }
      } else {
        result['transform'] = key + '(' + style[key] + 'deg)'
      }
    }
  })

  return result
}

/**
 * 获取组件的所有样式（包括通用背景）
 * @param item - 组件对象
 * @param filter - 需要过滤的属性数组
 * @returns 处理后的样式对象
 *
 * 整合组件的样式和通用背景样式，处理背景色和背景图
 */
export function getItemAllStyle(item, filter = []) {
  const style = item.style
  const commonBackground = item.commonBackground
  const result = {}
  Object.keys(style).forEach(key => {
    if (!filter.includes(key)) {
      if (key != 'rotate') {
        if (style[key] !== '') {
          result[key] = style[key]

          if (needUnit.includes(key)) {
            result[key] += 'px'
          }
        }
      } else {
        result['transform'] = key + '(' + style[key] + 'deg)'
      }
    }

    if (commonBackground) {
      //附加背景样式
      let colorRGBA = ''
      if (commonBackground.backgroundColorSelect) {
        colorRGBA = hexColorToRGBA(commonBackground.backgroundColor, commonBackground.alpha)
      }
      if (commonBackground.backgroundImageEnable) {
        if (
          commonBackground.backgroundType === 'outerImage' &&
          typeof commonBackground.outerImage === 'string'
        ) {
          result['background'] = `url(${imgUrlTrans(
            commonBackground.outerImage
          )}) no-repeat ${colorRGBA}`
        } else {
          result['background-color'] = colorRGBA
        }
      } else {
        result['background-color'] = colorRGBA
      }
    }
  })

  return result
}

/**
 * 获取组件的样式
 * @param style - 原始样式对象
 * @param filter - 需要过滤的属性数组
 * @returns 处理后的样式对象
 *
 * 通用样式处理函数
 */
export function getStyle(style, filter = []) {
  const result = {}
  Object.keys(style).forEach(key => {
    if (!filter.includes(key)) {
      if (key != 'rotate') {
        if (style[key] !== '') {
          result[key] = style[key]

          if (needUnit.includes(key)) {
            result[key] += 'px'
          }
        }
      } else {
        result['transform'] = key + '(' + style[key] + 'deg)'
      }
    }
  })
  return result
}

/**
 * 获取组件旋转后的样式
 * @param style - 原始样式对象
 * @returns 旋转后的样式对象
 *
 * 计算组件旋转后的实际边界尺寸和位置
 * 用于正确计算旋转组件的占用空间
 */
export function getComponentRotatedStyle(style) {
  style = { ...style }
  if (style.rotate != 0) {
    const newWidth = style.width * cos(style.rotate) + style.height * sin(style.rotate)
    const diffX = (style.width - newWidth) / 2 // 旋转后范围变小是正值，变大是负值
    style.left += diffX
    style.right = style.left + newWidth

    const newHeight = style.height * cos(style.rotate) + style.width * sin(style.rotate)
    const diffY = (newHeight - style.height) / 2 // 始终是正
    style.top -= diffY
    style.bottom = style.top + newHeight

    style.width = newWidth
    style.height = newHeight
  } else {
    style.bottom = style.top + style.height
    style.right = style.left + style.width
  }

  return style
}

/**
 * 获取画布样式
 * @param canvasStyleData - 画布样式数据
 * @param canvasId - 画布 ID，默认为 'canvas-main'
 * @returns 处理后的画布样式对象
 *
 * 根据画布样式数据生成 CSS 样式对象，支持背景图、背景色和移动端自定义样式
 */
export function getCanvasStyle(canvasStyleData, canvasId = 'canvas-main') {
  const {
    backgroundColorSelect,
    background,
    backgroundColor,
    backgroundImageEnable,
    fontSize,
    mobileSetting,
    fontFamily
  } = canvasStyleData
  const style = { fontSize: fontSize + 'px', color: canvasStyleData.color }
  if (isMainCanvas(canvasId)) {
    // 仪表板默认色#f5f6f7 大屏默认配色 #1a1a1a
    let colorRGBA = dvMainStore.dvInfo.type === 'dashboard' ? '#f5f6f7' : '#1a1a1a'
    if (backgroundColorSelect && backgroundColor) {
      colorRGBA = backgroundColor
    }
    if (backgroundImageEnable) {
      style['background'] = `url(${imgUrlTrans(background)}) no-repeat ${colorRGBA}`
    } else {
      style['background-color'] = colorRGBA
    }

    // 移动端自定义设置
    if (dvMainStore.mobileInPc && mobileSetting?.customSetting) {
      const { backgroundColorSelect, color, backgroundImageEnable, background } = mobileSetting
      if (backgroundColorSelect && backgroundImageEnable && typeof background === 'string') {
        style['background'] = `url(${imgUrlTrans(background)}) no-repeat ${color}`
      } else if (backgroundColorSelect) {
        style['background-color'] = color
      } else if (backgroundImageEnable) {
        style['background'] = `url(${imgUrlTrans(background)}) no-repeat`
      }
    }
    style['font-family'] = fontFamily + '!important'
  }

  return style
}

// ==================== 分组样式处理 ====================

/**
 * 创建分组样式
 * @param groupComponent - 分组组件对象
 *
 * 计算分组内每个组件相对于分组的位置比例
 * 用于在分组缩放时保持子组件的相对位置
 */
export function createGroupStyle(groupComponent) {
  const parentStyle = groupComponent.style
  groupComponent.propValue.forEach(component => {
    // 分组计算逻辑
    // 1.groupStyle记录left top width height 在出现分组缩放的时候进行等比例变更（缩放来源有两种a.整个大屏的缩放 b.分组尺寸的调整）
    // 2.component 内部进行位移或者尺寸的变更 要同步到这个比例中
    const style = { ...component.style }
    component.groupStyle.left = (style.left - parentStyle.left) / parentStyle.width
    component.groupStyle.top = (style.top - parentStyle.top) / parentStyle.height
    component.groupStyle.width = style.width / parentStyle.width
    component.groupStyle.height = style.height / parentStyle.height

    component.style.left = component.style.left - parentStyle.left
    component.style.top = component.style.top - parentStyle.top
  })
}

/**
 * 大屏标签页尺寸样式适配器
 * @param tabComponent - 标签页组件对象
 *
 * 处理大屏模式下标签页内组件的尺寸适配
 */
function dataVTabSizeStyleAdaptor(tabComponent) {
  const parentStyleAdaptor = { ...tabComponent.style }
  const offset = parentStyleAdaptor.showTabTitle ? 46 : 0
  const domId =
    dvMainStore.editMode === 'edit'
      ? 'component' + tabComponent.id
      : 'enlarge-inner-content' + tabComponent.id
  const tabDom = document.getElementById(domId)
  if (tabDom) {
    parentStyleAdaptor.height = tabDom.clientHeight - offset
    parentStyleAdaptor.width = tabDom.clientWidth
  } else {
    parentStyleAdaptor.height = parentStyleAdaptor.height - offset
  }

  tabComponent.propValue?.forEach(tabItem => {
    tabItem.componentData?.forEach(tabComponent => {
      groupItemStyleAdaptor(tabComponent, parentStyleAdaptor)
      if (['Group'].includes(tabComponent.component)) {
        groupSizeStyleAdaptor(tabComponent)
      }
    })
  })
}

/**
 * 分组内组件样式适配器
 * @param component - 组件对象
 * @param parentStyle - 父容器样式
 *
 * 根据比例计算组件在父容器中的实际尺寸和位置
 */
export function groupItemStyleAdaptor(component, parentStyle) {
  // 分组还原逻辑
  // 当发上分组缩放是，要将内部组件按照比例转换
  const styleScale = component.groupStyle
  component.style.left = parentStyle.width * styleScale.left
  component.style.top = parentStyle.height * styleScale.top
  component.style.width = parentStyle.width * styleScale.width
  component.style.height = parentStyle.height * styleScale.height
}

/**
 * 批量还原分组样式
 * @param groupComponent - 分组或标签页组件对象
 * @param parentStyle - 父容器样式
 *
 * 还原分组或标签页内所有组件的样式比例
 */
export function groupStyleRevertBatch(groupComponent, parentStyle) {
  if (groupComponent.component === 'DeTabs') {
    groupComponent.propValue?.forEach(tabItem => {
      tabItem.componentData?.forEach(tabComponent => {
        groupStyleRevert(tabComponent, parentStyle)
      })
    })
  }
}

/**
 * 还原标签页内部样式
 * @param tabOuterComponent - 标签页组件对象
 *
 * 还原标签页内所有组件的样式比例
 */
export function tabInnerStyleRevert(tabOuterComponent) {
  const parentStyle = {
    width: tabOuterComponent.style.width,
    height: tabOuterComponent.style.height - (tabOuterComponent.style.showTabTitle ? 46 : 0)
  }
  tabOuterComponent.propValue?.forEach(tabItem => {
    tabItem.componentData?.forEach(tabComponent => {
      groupStyleRevert(tabComponent, parentStyle)
    })
  })
}

/**
 * 还原分组样式
 * @param innerComponent - 内部组件对象
 * @param parentStyle - 父容器样式
 *
 * 计算组件相对于父容器的样式比例
 */
export function groupStyleRevert(innerComponent, parentStyle) {
  const innerStyle = { ...innerComponent.style }
  innerComponent.groupStyle.left = innerStyle.left / parentStyle.width
  innerComponent.groupStyle.top = innerStyle.top / parentStyle.height
  innerComponent.groupStyle.width = innerStyle.width / parentStyle.width
  innerComponent.groupStyle.height = innerStyle.height / parentStyle.height
}

/**
 * 分组尺寸样式适配器
 * @param groupComponent - 分组或标签页组件对象
 *
 * 适配分组或标签页内所有组件的尺寸
 */
export function groupSizeStyleAdaptor(groupComponent) {
  if (groupComponent.component === 'Group') {
    const parentStyle = groupComponent.style
    groupComponent.propValue.forEach(component => {
      groupItemStyleAdaptor(component, parentStyle)
    })
  } else {
    dataVTabSizeStyleAdaptor(groupComponent)
  }
}

/**
 * 大屏标签页添加组件时的样式处理
 * @param innerComponent - 内部组件对象
 * @param parentComponent - 父组件（标签页）对象
 *
 * 将组件添加到标签页时，初始化组件的样式比例
 */
export function dataVTabComponentAdd(innerComponent, parentComponent) {
  innerComponent.style.top = 0
  innerComponent.style.left = 0
  const parentStyleAdaptor = { ...parentComponent.style }
  // 去掉tab头部高度
  parentStyleAdaptor.height = parentStyleAdaptor.height - (parentComponent.showTabTitle ? 46 : 0)
  groupStyleRevert(innerComponent, parentStyleAdaptor)
}
