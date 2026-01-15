import { cos, sin } from '@/utils/translate'
import {
  CHART_FONT_FAMILY_MAP_TRANS,
  DEFAULT_COLOR_CASE,
  DEFAULT_COLOR_CASE_DARK
} from '@/views/chart/components/editor/util/chart'

import { dvMainStoreWithOut } from '@/store/modules/data-visualization/dvMain'
import { useEmitt } from '@/hooks/web/useEmitt'
import { defaultTo, merge } from 'lodash-es'
import { formatterViewInfo } from '@/views/chart/components/js/formatter'
const dvMainStore = dvMainStoreWithOut()

// ==================== 主题颜色常量 ====================

/** 亮色主题主色 */
export const LIGHT_THEME_COLOR_MAIN = '#000000'
/** 亮色主题辅助色 */
export const LIGHT_THEME_COLOR_SLAVE1 = '#CCCCCC'
/** 亮色主题仪表板背景 */
export const LIGHT_THEME_DASHBOARD_BACKGROUND = '#f5f6f7'
/** 亮色主题组件背景 */
export const LIGHT_THEME_COMPONENT_BACKGROUND = '#FFFFFF'

/** 暗色主题主色 */
export const DARK_THEME_COLOR_MAIN = '#FFFFFF'
/** 暗色主题辅助色 */
export const DARK_THEME_COLOR_SLAVE1 = '#858383'
/** 暗色主题仪表板背景 */
export const DARK_THEME_DASHBOARD_BACKGROUND = '#030B2E'
/** 暗色主题组件背景 */
export const DARK_THEME_COMPONENT_BACKGROUND = '#131E42'
/** 暗色主题组件背景（次要） */
export const DARK_THEME_COMPONENT_BACKGROUND_BACK = '#5a5c62'

// ==================== 样式转换映射 ====================

/**
 * 自定义属性转换映射
 * 定义了哪些属性需要根据缩放比例进行调整
 */
export const customAttrTrans = {
  basicStyle: [
    'barWidth',
    'lineWidth',
    'lineSymbolSize',
    'leftLineWidth',
    'leftLineSymbolSize',
    'tableColumnWidth'
  ],
  tableHeader: [
    'tableTitleFontSize',
    'tableTitleColFontSize',
    'tableTitleCornerFontSize',
    'tableTitleHeight'
  ],
  tableCell: ['tableItemFontSize', 'tableItemHeight'],
  misc: [
    'nameFontSize',
    'valueFontSize',
    'spaceSplit', // 间隔
    'scatterSymbolSize', // 气泡大小，散点图
    'radarSize', // 雷达占比
    'wordSizeRange',
    'wordSpacing'
  ],
  label: {
    fontSize: '',
    seriesLabelFormatter: ['fontSize'],
    proportionSeriesFormatter: ['fontSize']
  },
  tooltip: {
    fontSize: '',
    seriesTooltipFormatter: ['fontSize']
  },
  indicator: ['fontSize', 'suffixFontSize'],
  indicatorName: ['fontSize', 'nameValueSpacing']
}

/**
 * 自定义样式转换映射
 * 定义了图表样式属性的转换规则
 */
export const customStyleTrans = {
  text: ['fontSize'],
  legend: ['fontSize'],
  xAxis: {
    fontSize: 'fontSize',
    axisLabel: ['fontSize'],
    splitLine: {
      lineStyle: ['width']
    },
    axisLine: {
      lineStyle: ['width']
    }
  },
  yAxis: {
    fontSize: 'fontSize',
    axisLabel: ['fontSize'],
    splitLine: {
      lineStyle: ['width']
    },
    axisLine: {
      lineStyle: ['width']
    }
  },
  yAxisExt: {
    fontSize: 'fontSize',
    axisLabel: ['fontSize'],
    splitLine: {
      lineStyle: ['width']
    },
    axisLine: {
      lineStyle: ['width']
    }
  },
  misc: {
    fontSize: 'fontSize',
    axisLine: {
      lineStyle: ['width']
    },
    axisTick: {
      lineStyle: ['width']
    },
    axisLabel: ['margin', 'fontSize'],
    splitLine: {
      lineStyle: ['width']
    }
  }
}

// ==================== 主题样式转换映射 ====================

/**
 * 主题样式转换映射（主色和背景色）
 */
export const THEME_STYLE_TRANS_MAIN_BACK = {
  legend: {
    textStyle: ['color']
  },
  xAxis: {
    nameTextStyle: ['color'],
    axisLabel: ['color'],
    splitLine: {
      lineStyle: ['color']
    }
  },
  yAxis: {
    nameTextStyle: ['color'],
    axisLabel: ['color'],
    splitLine: {
      lineStyle: ['color']
    }
  },
  yAxisExt: {
    nameTextStyle: ['color'],
    axisLabel: ['color'],
    splitLine: {
      lineStyle: ['color']
    }
  },
  split: {
    name: ['color'],
    axisLine: {
      lineStyle: ['color']
    },
    axisTick: {
      lineStyle: ['color']
    },
    axisLabel: ['color'],
    splitLine: {
      lineStyle: ['color']
    }
  }
}

/**
 * 主题样式转换映射（主色）
 */
export const THEME_STYLE_TRANS_MAIN = {
  legend: ['color'],
  xAxis: {
    // 一级属性直接字符串
    color: 'color',
    axisLabel: ['color']
  },
  yAxis: {
    color: '',
    axisLabel: ['color']
  },
  yAxisExt: {
    color: '',
    axisLabel: ['color']
  },
  misc: {
    color: 'color',
    axisTick: {
      lineStyle: ['color']
    },
    axisLabel: ['color']
  }
}

/**
 * 主题样式转换映射（辅助色）
 */
export const THEME_STYLE_TRANS_SLAVE1 = {
  xAxis: {
    splitLine: {
      lineStyle: ['color']
    }
  },
  yAxis: {
    splitLine: {
      lineStyle: ['color']
    }
  },
  yAxisExt: {
    splitLine: {
      lineStyle: ['color']
    }
  },
  misc: {
    splitLine: {
      lineStyle: ['color']
    },
    axisLine: {
      lineStyle: ['color']
    }
  }
}

/**
 * 主题属性转换映射（主色）
 */
export const THEME_ATTR_TRANS_MAIN = {
  label: {
    color: 'color',
    proportionSeriesFormatter: ['color']
  },
  tooltip: ['color'],
  misc: {
    bullet: {
      bar: {
        target: ['fill']
      }
    }
  }
}

/**
 * 主题属性转换映射（符号主色）
 */
export const THEME_ATTR_TRANS_MAIN_SYMBOL = {
  label: ['color']
}

/**
 * 主题属性转换映射（辅助背景色）
 */
export const THEME_ATTR_TRANS_SLAVE1_BACKGROUND = {
  tooltip: ['backgroundColor']
}

// ==================== 移动端特殊属性 ====================

/**
 * 移动端特殊属性配置
 * 部分属性在移动端使用固定值以保证显示效果
 */
export const mobileSpecialProps = {
  lineWidth: 2, // 线宽固定值
  lineSymbolSize: 8 // 折点固定值
}

// ==================== 样式处理函数 ====================

/**
 * 获取样式对象
 * @param style - 原始样式对象
 * @param filter - 需要过滤的属性数组
 * @returns 处理后的样式对象
 *
 * 处理逻辑：
 * 1. 为需要的属性添加单位
 * 2. 将 rotate 转换为 transform
 * 3. 处理背景色和透明度
 * 4. 字体最小值为 12px
 */
export function getStyle(style, filter = []) {
  const needUnit = [
    'fontSize',
    'width',
    'height',
    'top',
    'left',
    'borderWidth',
    'letterSpacing',
    'borderRadius',
    'margin',
    'padding'
  ]

  const result = {}
  Object.keys(style).forEach(key => {
    if (!filter.includes(key)) {
      if (key !== 'rotate') {
        result[key] = style[key]
        if (key) {
          if (key === 'backgroundColor') {
            result[key] = colorRgb(style[key], style.opacity)
          }
          if (key === 'fontSize' && result[key] < 12) {
            result[key] = 12
          }
          if (needUnit.includes(key)) {
            result[key] += 'px'
          }
        }
      } else {
        result['transform'] = key + '(' + style[key] + 'deg)'
      }
    }
  })
  if (result['backgroundColor'] && (result['opacity'] || result['opacity'] === 0)) {
    delete result['opacity']
  }
  return result
}

/**
 * 获取组件旋转后的样式
 * @param style - 原始样式对象
 * @returns 旋转后的样式对象
 *
 * 计算组件旋转后的实际边界尺寸和位置
 */
export function getComponentRotatedStyle(style) {
  style = { ...style }
  if (style.rotate !== 0) {
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
 * 颜色转换为 RGBA 格式
 * @param color - 颜色值（支持 #RGB、#RRGGBB 格式）
 * @param opacity - 透明度（0-1）
 * @returns RGBA 格式的颜色字符串
 *
 * @example
 * colorRgb('#ff0000', 0.5)  // 'rgba(255,0,0,0.5)'
 * colorRgb('#f00')           // 'rgba(255,0,0,1)'
 */
export function colorRgb(color, opacity) {
  const reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/
  let sColor = color
  if (sColor && reg.test(sColor)) {
    sColor = sColor.toLowerCase()
    if (sColor.length === 4) {
      let sColorNew = '#'
      for (let i = 1; i < 4; i += 1) {
        sColorNew += sColor.slice(i, i + 1).concat(sColor.slice(i, i + 1))
      }
      sColor = sColorNew
    }
    // 处理六位的颜色值
    const sColorChange = []
    for (let i = 1; i < 7; i += 2) {
      sColorChange.push(parseInt('0x' + sColor.slice(i, i + 2)))
    }
    if (opacity || opacity === 0) {
      return 'rgba(' + sColorChange.join(',') + ',' + opacity + ')'
    } else {
      return 'rgba(' + sColorChange.join(',') + ')'
    }
  } else {
    return sColor
  }
}

/**
 * 获取缩放后的值
 * @param propValue - 原始值（可以是数组或单个值）
 * @param scale - 缩放比例
 * @returns 缩放后的值，最小为 1
 *
 * 用于根据画布缩放比例调整组件属性值
 */
export function getScaleValue(propValue, scale) {
  if (propValue instanceof Array) {
    propValue.forEach((v, i) => {
      const val = Math.round(v * scale)
      propValue[i] = val > 1 ? val : 1
    })
    return propValue
  }
  const propValueTemp = Math.round(propValue * scale)
  return propValueTemp > 1 ? propValueTemp : 1
}

/**
 * 主题属性转换数组配置
 */
export const THEME_ATTR_TRANS_ARR_MAIN = {
  label: {
    seriesLabelFormatter: {
      isArray: []
    }
  }
}

/**
 * 系列适配器
 * @param template - 模板对象
 * @param color - 目标颜色
 *
 * 为系列的标签和提示框设置颜色
 */
export function seriesAdaptor(template, color) {
  template.label?.seriesLabelFormatter?.forEach(series => {
    series['color'] = color
  })

  template.label?.seriesTooltipFormatter?.forEach(series => {
    series['color'] = color
  })
}

/**
 * 递归转换对象属性
 * @param template - 模板对象（定义转换规则）
 * @param infoObj - 待转换的对象
 * @param scale - 缩放比例
 * @param terminal - 终端类型（'mobile' 表示移动端）
 *
 * 根据模板定义的规则递归转换对象的数值属性
 */
export function recursionTransObj(template, infoObj, scale, terminal) {
  for (const templateKey in template) {
    // 如果是数组 进行赋值计算
    if (template[templateKey] instanceof Array) {
      // 词云图的大小区间，不需要缩放
      template[templateKey]
        .filter(field => field !== 'wordSizeRange')
        .forEach(templateProp => {
          if (
            infoObj[templateKey] &&
            (infoObj[templateKey][templateProp] || infoObj[templateKey].length)
          ) {
            // 移动端特殊属性值设置
            if (terminal === 'mobile' && mobileSpecialProps[templateProp] !== undefined) {
              infoObj[templateKey][templateProp] = mobileSpecialProps[templateProp]
            } else {
              // 数组依次设置
              if (infoObj[templateKey] instanceof Array) {
                infoObj[templateKey].forEach(v => {
                  v[templateProp] = getScaleValue(v[templateProp], scale)
                })
              } else {
                infoObj[templateKey][templateProp] = getScaleValue(
                  infoObj[templateKey][templateProp],
                  scale
                )
              }
            }
          }
        })
    } else if (typeof template[templateKey] === 'string') {
      // 一级字段为字符串直接赋值
      infoObj[templateKey] = getScaleValue(infoObj[templateKey], scale)
    } else {
      // 如果是对象 继续进行递归
      if (infoObj[templateKey]) {
        recursionTransObj(template[templateKey], infoObj[templateKey], scale, terminal)
      }
    }
  }
}

/**
 * 递归转换主题对象
 * @param template - 模板对象（定义转换规则）
 * @param infoObj - 待转换的对象
 * @param color - 目标颜色
 *
 * 根据模板定义的规则递归转换对象的颜色属性
 */
export function recursionThemTransObj(template, infoObj, color) {
  for (const templateKey in template) {
    // 如果是数组 进行赋值计算
    if (template[templateKey] instanceof Array) {
      template[templateKey].forEach(templateProp => {
        if (infoObj[templateKey]) {
          infoObj[templateKey][templateProp] = color
        }
      })
    } else if (typeof template[templateKey] === 'string') {
      // 一级字段为字符串直接赋值
      infoObj[templateKey] = color
    } else {
      // 如果是对象 继续进行递归
      if (infoObj[templateKey]) {
        recursionThemTransObj(template[templateKey], infoObj[templateKey], color)
      }
    }
  }
}

/**
 * 组件公共缩放处理
 * @param chartInfo - 图表信息对象
 * @param heightScale - 高度缩放比例
 * @param widthScale - 宽度缩放比例
 * @returns 处理后的图表信息对象
 *
 * 根据高度和宽度缩放比例调整图表的属性和样式
 */
export function componentScalePublic(chartInfo, heightScale, widthScale) {
  const scale = Math.min(heightScale, widthScale)
  // attr 缩放转换
  recursionTransObj(this.customAttrTrans, chartInfo.customAttr, scale, null)
  // style 缩放转换
  recursionTransObj(this.customStyleTrans, chartInfo.customStyle, scale, null)
  return chartInfo
}

/**
 * 适配当前主题
 * @param customStyle - 自定义样式对象
 * @param customAttr - 自定义属性对象
 *
 * 根据当前画布的主题颜色调整图表的样式和属性
 */
export function adaptCurTheme(customStyle, customAttr) {
  const canvasStyle = dvMainStore.canvasStyleData
  const themeColor = canvasStyle.dashboard.themeColor
  if (themeColor === 'light') {
    recursionThemTransObj(THEME_STYLE_TRANS_MAIN, customStyle, LIGHT_THEME_COLOR_MAIN)
    recursionThemTransObj(THEME_STYLE_TRANS_SLAVE1, customStyle, LIGHT_THEME_COLOR_SLAVE1)
    recursionThemTransObj(THEME_ATTR_TRANS_MAIN, customAttr, LIGHT_THEME_COLOR_MAIN)
    recursionThemTransObj(
      THEME_ATTR_TRANS_SLAVE1_BACKGROUND,
      customAttr,
      LIGHT_THEME_COMPONENT_BACKGROUND
    )
    seriesAdaptor(customAttr, LIGHT_THEME_COLOR_MAIN)
    merge(customAttr, DEFAULT_COLOR_CASE, canvasStyle.component.chartColor)
  } else {
    recursionThemTransObj(THEME_STYLE_TRANS_MAIN, customStyle, DARK_THEME_COLOR_MAIN)
    recursionThemTransObj(THEME_STYLE_TRANS_SLAVE1, customStyle, DARK_THEME_COLOR_SLAVE1)
    recursionThemTransObj(THEME_ATTR_TRANS_MAIN, customAttr, DARK_THEME_COLOR_MAIN)
    recursionThemTransObj(
      THEME_ATTR_TRANS_SLAVE1_BACKGROUND,
      customAttr,
      DARK_THEME_COMPONENT_BACKGROUND_BACK
    )
    seriesAdaptor(customAttr, DARK_THEME_COLOR_MAIN)
    merge(customAttr, DEFAULT_COLOR_CASE_DARK, canvasStyle.component.chartColor)
  }
  customStyle['text'] = {
    ...canvasStyle.component.chartTitle,
    title: customStyle['text']['title'],
    show: customStyle['text']['show'],
    remarkShow: customStyle['text']['remarkShow'],
    remark: customStyle['text']['remark']
  }
}

/**
 * 适配标题字体
 * @param fontFamily - 字体名称
 * @param viewInfo - 视图信息对象
 *
 * 设置图表标题的字体，对指标卡进行特殊处理
 */
export function adaptTitleFontFamily(fontFamily, viewInfo) {
  if (viewInfo) {
    const _fontFamily = defaultTo(CHART_FONT_FAMILY_MAP_TRANS[fontFamily], fontFamily)
    viewInfo.customStyle['text']['fontFamily'] = _fontFamily
    //针对指标卡设置字体
    if (viewInfo.type === 'indicator') {
      viewInfo.customAttr['indicator']['fontFamily'] = fontFamily
      viewInfo.customAttr['indicator']['suffixFontFamily'] = fontFamily
      viewInfo.customAttr['indicatorName']['fontFamily'] = fontFamily
    }
  }
}

/**
 * 适配所有组件的标题字体
 * @param fontFamily - 字体名称
 *
 * 遍历所有组件并设置字体，包括分组和标签页内的组件
 */
export function adaptTitleFontFamilyAll(fontFamily) {
  const componentData = dvMainStore.componentData
  componentData.forEach(item => {
    if (item.component === 'UserView') {
      const viewDetails = dvMainStore.canvasViewInfo[item.id]
      adaptTitleFontFamily(fontFamily, viewDetails)
      useEmitt().emitter.emit('renderChart-' + item.id, viewDetails)
    } else if (item.component === 'Group') {
      item.propValue.forEach(groupItem => {
        if (groupItem.component === 'UserView') {
          const viewDetails = dvMainStore.canvasViewInfo[groupItem.id]
          adaptTitleFontFamily(fontFamily, viewDetails)
          useEmitt().emitter.emit('renderChart-' + groupItem.id, viewDetails)
        }
      })
    } else if (item.component === 'DeTabs') {
      item.propValue?.forEach(tabItem => {
        tabItem.componentData?.forEach(tabComponent => {
          if (tabComponent.component === 'UserView') {
            const viewDetails = dvMainStore.canvasViewInfo[tabComponent.id]
            adaptTitleFontFamily(fontFamily, viewDetails)
            useEmitt().emitter.emit('renderChart-' + tabComponent.id, viewDetails)
          }
        })
      })
    }
  })
}

/**
 * 适配当前主题的通用样式
 * @param component - 组件对象
 * @returns 处理后的组件对象
 *
 * 根据当前主题调整组件的通用样式，包括背景、颜色等
 */
export function adaptCurThemeCommonStyle(component) {
  if (['DeTabs'].includes(component.component)) {
    component.commonBackground['innerPadding'] = 0
  }
  // 背景融合-Begin 如果是大屏['CanvasBoard', 'CanvasIcon', 'Picture']组件不需要设置背景
  if (
    dvMainStore.dvInfo.type === 'dataV' &&
    [
      'CanvasBoard',
      'CanvasIcon',
      'Picture',
      'Group',
      'SvgTriangle',
      'SvgStar',
      'RectShape',
      'CircleShape',
      'DeDecoration',
      'DynamicBackground'
    ].includes(component.component)
  ) {
    component.commonBackground['backgroundColorSelect'] = false
    component.commonBackground['innerPadding'] = 0
  } else {
    const commonStyle = dvMainStore.canvasStyleData.component.chartCommonStyle
    for (const key in commonStyle) {
      component.commonBackground[key] = commonStyle[key]
    }
  }
  // 背景融合-End
  // 通用样式-Begin
  if (component.style.color) {
    if (dvMainStore.canvasStyleData.dashboard.themeColor === 'light') {
      component.style.color = LIGHT_THEME_COLOR_MAIN
    } else {
      component.style.color = DARK_THEME_COLOR_MAIN
    }
  }
  if (component.component === 'UserView') {
    // 图表-Begin
    const curViewInfo = dvMainStore.canvasViewInfo[component.id]
    adaptCurTheme(curViewInfo.customStyle, curViewInfo.customAttr)
    formatterViewInfo(curViewInfo, dvMainStore.canvasStyleData.component.formatterItem)
    useEmitt().emitter.emit('renderChart-' + component.id, curViewInfo)
    // 图表-Begin
  } else if (component.component === 'Group') {
    component.propValue.forEach(groupItem => {
      adaptCurThemeCommonStyle(groupItem)
    })
  } else if (['DeTabs', 'DeScreen'].includes(component.component)) {
    if (dvMainStore.canvasStyleData.dashboard.themeColor === 'light') {
      component.style.headFontColor = LIGHT_THEME_COLOR_MAIN
      component.style.headFontActiveColor = LIGHT_THEME_COLOR_MAIN
    } else {
      component.style.headFontColor = DARK_THEME_COLOR_MAIN
      component.style.headFontActiveColor = DARK_THEME_COLOR_MAIN
    }
    component.propValue?.forEach(tabItem => {
      tabItem.componentData?.forEach(tabComponent => {
        adaptCurThemeCommonStyle(tabComponent)
      })
    })
  } else if (component.component === 'VQuery') {
    const viewInfo = dvMainStore.canvasViewInfo[component.id]
    if (viewInfo) {
      adaptCurThemeFilterStyleAllKeyComponent(viewInfo)
    }
  }

  return component
}

/**
 * 适配所有组件的当前主题通用样式
 *
 * 遍历所有组件并应用当前主题的样式
 */
export function adaptCurThemeCommonStyleAll() {
  const componentData = dvMainStore.componentData
  componentData.forEach(item => {
    adaptCurThemeCommonStyle(item)
  })
}

// ==================== 过滤组件样式处理 ====================

interface CanvasViewInfo {
  type: string
  customStyle: {
    component: object
  }
}

// 颜色相关属性
const colors = ['labelColor', 'borderColor', 'text', 'bgColor']
// 颜色开关属性
const colorsSwitch = ['borderShow', 'textColorShow', 'bgColorShow']

/**
 * 适配当前主题的过滤组件样式（所有关键字段）
 * @param component - 组件对象
 *
 * 设置过滤组件的颜色样式
 */
export function adaptCurThemeFilterStyleAllKeyComponent(component) {
  if (isFilterComponent(component.type)) {
    const filterStyle = dvMainStore.canvasStyleData.component.filterStyle
    colors.forEach(styleKey => {
      component.customStyle.component[styleKey] = filterStyle[styleKey]
      const index = colors.indexOf(styleKey)
      if (index !== -1) {
        component.customStyle.component[colorsSwitch[index]] = true
      }
    })
  }
}

/**
 * 适配当前主题的过滤组件样式（指定字段）
 * @param styleKey - 样式键名
 *
 * 更新所有过滤组件的指定样式字段
 */
export function adaptCurThemeFilterStyleAll(styleKey) {
  const componentViewData = Object.values(dvMainStore.canvasViewInfo) as CanvasViewInfo[]
  const filterStyle = dvMainStore.canvasStyleData.component.filterStyle
  componentViewData.forEach(item => {
    if (isFilterComponent(item.type)) {
      item.customStyle.component[styleKey] = filterStyle[styleKey]
      const index = colors.indexOf(styleKey)
      if (index !== -1) {
        item.customStyle.component[colorsSwitch[index]] = true
      }
    }
  })
}

/**
 * 检查是否为过滤组件
 * @param component - 组件类型
 * @returns 是否为过滤组件
 */
export function isFilterComponent(component) {
  return ['VQuery'].includes(component)
}

/**
 * 检查是否为标签页组件
 * @param component - 组件类型
 * @returns 是否为标签页组件
 */
export function isTabComponent(component) {
  return ['DeTabs'].includes(component)
}
