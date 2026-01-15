/**
 * 图片和画布导出工具
 *
 * 功能说明：
 * 1. 将画布内容导出为图片（PNG/JPEG）
 * 2. 将画布内容导出为 PDF 文档
 * 3. 导出应用模板和仪表板模板
 * 4. 处理静态资源（背景图、图片组件等）
 *
 * 使用场景：
 * - 仪表板导出为图片
 * - 仪表板导出为 PDF
 * - 保存应用模板
 * - 保存仪表板模板
 * - 静态资源路径处理
 *
 * 依赖库：
 * - html2canvas: 将 DOM 转换为 Canvas
 * - modern-screenshot: 现代化的截图方案
 * - jsPDF: 生成 PDF 文档
 * - file-saver: 文件下载保存
 *
 * @example
 * import { downloadCanvas, imgUrlTrans } from '@/utils/imgUtils'
 *
 * // 导出画布为图片
 * downloadCanvas('img', canvasDom, 'dashboard')
 *
 * // 转换图片 URL
 * const url = imgUrlTrans('/static-resource/xxx.jpg')
 */

import html2canvas from 'html2canvas'
import JsPDF from 'jspdf'
import { dvMainStoreWithOut } from '@/store/modules/data-visualization/dvMain'
import { useEmbedded } from '@/store/modules/embedded'
import { storeToRefs } from 'pinia'
import { findResourceAsBase64 } from '@/api/staticResource'
import FileSaver from 'file-saver'
import { deepCopy } from '@/utils/utils'
import { domToPng } from 'modern-screenshot'

const embeddedStore = useEmbedded()
const dvMainStore = dvMainStoreWithOut()
const { canvasStyleData, componentData, canvasViewInfo, canvasViewDataInfo, dvInfo } =
  storeToRefs(dvMainStore)
const basePath = import.meta.env.VITE_API_BASEPATH

// ==================== URL 格式化 ====================

/**
 * 格式化 URL
 *
 * 修复 URL 中的双斜杠问题
 *
 * @param url - 原始 URL
 * @returns 格式化后的 URL
 *
 * @example
 * formatterUrl('//de2api/xxx')  // '/de2api/xxx'
 */
export function formatterUrl(url: string) {
  return url.replace('//de2api', '/de2api')
}

/**
 * 转换图片 URL
 *
 * 处理静态资源的 URL，支持嵌入式模式
 * 自动添加基础路径和处理 URL 格式
 *
 * @param url - 原始图片 URL
 * @returns 处理后的完整 URL
 *
 * @example
 * // 静态资源 URL
 * imgUrlTrans('/static-resource/image.jpg')
 *
 * // 外部 URL
 * imgUrlTrans('https://example.com/image.jpg')
 */
export function imgUrlTrans(url) {
  if (url) {
    // 处理静态资源路径
    if (typeof url === 'string' && url.indexOf('static-resource') > -1) {
      const rawUrl = url
        ? (basePath.endsWith('/') ? basePath.substring(0, basePath.length - 1) : basePath) + url
        : null
      return formatterUrl(
        embeddedStore.baseUrl
          ? `${embeddedStore.baseUrl}${
              rawUrl.startsWith('/api') ? rawUrl.slice(5) : rawUrl
            }`.replace('com//', 'com/')
          : rawUrl
      )
    } else {
      // 处理外部 URL
      return formatterUrl(url.replace('com//', 'com/'))
    }
  }
}

// ==================== 模板导出 ====================

/**
 * 下载应用或仪表板模板
 *
 * 将当前画布内容打包为模板文件（.DET2 或 .DET2APP）
 * 包含画布样式、组件数据、视图数据、静态资源等
 *
 * @param downloadType - 下载类型
 *   - 'template': 仪表板模板（.DET2）
 *   - 'app': 应用模板（.DET2APP）
 * @param canvasDom - 画布 DOM 元素
 * @param name - 模板名称
 * @param attachParams - 附加参数（如 appName）
 * @param callBack - 回调函数（成功或失败后执行）
 *
 * @example
 * // 导出仪表板模板
 * download2AppTemplate('template', canvasDom, '销售仪表板', null, () => {
 *   console.log('导出完成')
 * })
 *
 * // 导出应用模板
 * download2AppTemplate('app', canvasDom, '销售应用', { appName: '我的应用' })
 */
export function download2AppTemplate(downloadType, canvasDom, name, attachParams, callBack?) {
  try {
    // 查找并处理静态资源
    findStaticSource(function (staticResource) {
      // 将画布转换为图片作为快照
      html2canvas(canvasDom).then(canvas => {
        // 深拷贝视图信息
        const canvasViewDataTemplate = deepCopy(canvasViewInfo.value)
        // 填充视图数据
        Object.keys(canvasViewDataTemplate).forEach(viewId => {
          canvasViewDataTemplate[viewId].data = canvasViewDataInfo.value[viewId]
        })

        // 生成低质量快照（用于模板预览）
        const snapshot = canvas.toDataURL('image/jpeg', 0.1) // 0.1是图片质量
        const templateName = attachParams?.appName ? attachParams.appName : name

        if (snapshot !== '') {
          // 构建模板信息对象
          const templateInfo = {
            name: templateName,
            templateType: 'self',
            snapshot: snapshot,
            dvType: dvInfo.value.type,
            nodeType: downloadType,
            version: 3,
            canvasStyleData: JSON.stringify(canvasStyleData.value),
            componentData: JSON.stringify(componentData.value),
            dynamicData: JSON.stringify(canvasViewDataTemplate),
            staticResource: JSON.stringify(staticResource || {}),
            appData: attachParams ? JSON.stringify(attachParams) : null
          }

          // 创建 Blob 并下载
          const blob = new Blob([JSON.stringify(templateInfo)], { type: '' })
          if (downloadType === 'template') {
            FileSaver.saveAs(blob, name + '-TEMPLATE.DET2')
          } else if (downloadType === 'app') {
            FileSaver.saveAs(blob, templateName + '-APP.DET2APP')
          }
        }

        // 执行回调
        if (callBack) {
          callBack()
        }
      })
    })
  } catch (e) {
    if (callBack) {
      callBack()
    }
    console.error(e)
  }
}

// ==================== 画布导出（html2canvas）====================

/**
 * 下载画布内容为图片或 PDF
 *
 * 使用 html2canvas 将 DOM 转换为 Canvas，然后导出
 *
 * @param type - 导出类型
 *   - 'img': 导出为 PNG 图片
 *   - 'pdf': 导出为 PDF 文档
 * @param canvasDom - 画布 DOM 元素
 * @param name - 导出文件名称（不含扩展名）
 * @param callBack - 回调函数
 *
 * @example
 * // 导出为图片
 * downloadCanvas('img', canvasDom, 'dashboard')
 *
 * // 导出为 PDF
 * downloadCanvas('pdf', canvasDom, 'report')
 */
export function downloadCanvas(type, canvasDom, name, callBack?) {
  if (canvasDom) {
    html2canvas(canvasDom)
      .then(canvas => {
        // 将 canvas 添加到 body（临时）
        const dom = document.body.appendChild(canvas)
        dom.style.display = 'none'
        document.body.removeChild(dom)

        // 转换为 Data URL
        const dataUrl = dom.toDataURL('image/png', 1)

        if (type === 'img') {
          // 导出为图片
          const a = document.createElement('a')
          a.setAttribute('download', name)
          a.href = dataUrl
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)
        } else {
          // 导出为 PDF
          const contentWidth = canvasDom.offsetWidth
          const contentHeight = canvasDom.offsetHeight
          const lp = contentWidth > contentHeight ? 'l' : 'p' // l=横向, p=纵向
          const PDF = new JsPDF(lp, 'pt', [contentWidth, contentHeight])
          PDF.addImage(dataUrl, 'PNG', 0, 0, contentWidth, contentHeight)
          PDF.save(name + '.pdf')
        }

        if (callBack) {
          callBack()
        }
      })
      .catch(error => {
        console.error('oops, something went wrong!', error)
        if (callBack) {
          callBack()
        }
      })
  }
}

// ==================== 画布导出（modern-screenshot）====================

/**
 * 下载画布内容为图片或 PDF（现代方案）
 *
 * 使用 modern-screenshot 库，提供更好的截图效果
 *
 * @param type - 导出类型
 *   - 'img': 导出为 PNG 图片
 *   - 'pdf': 导出为 PDF 文档
 * @param canvasDom - 画布 DOM 元素
 * @param name - 导出文件名称（不含扩展名）
 * @param callBack - 回调函数
 *
 * @example
 * // 导出为图片（推荐）
 * downloadCanvas2('img', canvasDom, 'dashboard')
 */
export function downloadCanvas2(type, canvasDom, name, callBack?) {
  domToPng(canvasDom)
    .then(dataUrl => {
      if (type === 'img') {
        // 导出为图片
        const a = document.createElement('a')
        a.setAttribute('download', name + '.png')
        a.href = dataUrl
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
      } else {
        // 导出为 PDF
        const contentWidth = canvasDom.offsetWidth
        const contentHeight = canvasDom.offsetHeight
        const lp = contentWidth > contentHeight ? 'l' : 'p'
        const PDF = new JsPDF(lp, 'pt', [contentWidth, contentHeight])
        PDF.addImage(dataUrl, 'PNG', 0, 0, contentWidth, contentHeight)
        PDF.save(name + '.pdf')
      }

      if (callBack) {
        callBack()
      }
    })
    .catch(error => {
      if (callBack) {
        callBack()
      }
      console.error('oops, something went wrong!', error)
    })
}

// ==================== 数据转换工具 ====================

/**
 * 将 Data URL 转换为 Blob
 *
 * 用于图片格式转换和文件下载
 * 支持 IE 浏览器
 *
 * @param dataUrl - Data URL 字符串
 * @returns Blob 对象
 *
 * @example
 * const blob = dataURLToBlob('data:image/png;base64,iVBORw0KG...')
 */
export function dataURLToBlob(dataUrl) {
  // ie 图片转格式
  const arr = dataUrl.split(',')
  const mime = arr[0].match(/:(.*?);/)[1]
  const bStr = atob(arr[1])
  let n = bStr.length
  const u8arr = new Uint8Array(n)
  while (n--) {
    u8arr[n] = bStr.charCodeAt(n)
  }
  return new Blob([u8arr], { type: mime })
}

// ==================== 静态资源处理 ====================

/**
 * 递归查找组件中的静态资源
 *
 * 遍历组件树，收集所有静态资源 URL
 * 支持的组件类型：
 * - 背景图片（outerImage）
 * - 图片组件（Picture）
 * - 图片组组件（picture-group）
 * - 组件组（Group）
 * - 标签页组件（DeTabs）
 *
 * @param componentDataInfo - 组件数据数组
 * @param staticResource - 静态资源 URL 数组（输出参数）
 */
function findStaticSourceInner(componentDataInfo, staticResource) {
  componentDataInfo?.forEach(item => {
    // 组件背景图片
    if (
      typeof item.commonBackground.outerImage === 'string' &&
      item.commonBackground.outerImage.indexOf('static-resource') > -1
    ) {
      staticResource.push(item.commonBackground.outerImage)
    }

    // 图片组件
    if (
      item.component === 'Picture' &&
      item.propValue['url'] &&
      typeof item.propValue['url'] === 'string' &&
      item.propValue['url'].indexOf('static-resource') > -1
    ) {
      staticResource.push(item.propValue['url'])
    }
    // 图片组组件
    else if (
      item.component === 'UserView' &&
      item.innerType === 'picture-group' &&
      item.propValue['urlList'] &&
      item.propValue['urlList'].length > 0
    ) {
      item.propValue['urlList'].forEach(urlInfo => {
        if (urlInfo.url.indexOf('static-resource') > -1) {
          staticResource.push(urlInfo.url)
        }
      })
    }
    // 组件组（递归处理）
    else if (item.component === 'Group') {
      findStaticSourceInner(item.propValue, staticResource)
    }
    // 标签页组件（递归处理每个标签）
    else if (item.component === 'DeTabs') {
      item.propValue.forEach(tabItem => {
        findStaticSourceInner(tabItem.componentData, staticResource)
      })
    }
  })
}

/**
 * 解析静态文件并转换为 Base64
 *
 * 收集画布中的所有静态资源，并从服务器获取 Base64 编码
 * 用于模板导出时嵌入静态资源
 *
 * @param callBack - 回调函数，参数为静态资源映射对象
 *
 * @example
 * findStaticSource((resourceMap) => {
 *   console.log('静态资源：', resourceMap)
 *   // {
 *   //   '/static-resource/xxx.jpg': 'data:image/jpeg;base64,...'
 *   // }
 * })
 */
export function findStaticSource(callBack) {
  const staticResource = []

  // 系统背景文件
  if (
    typeof canvasStyleData.value.background === 'string' &&
    canvasStyleData.value.background.indexOf('static-resource') > -1
  ) {
    staticResource.push(canvasStyleData.value.background)
  }

  // 收集组件中的静态资源
  findStaticSourceInner(componentData.value, staticResource)

  if (staticResource.length > 0) {
    try {
      // 从服务器获取 Base64 编码的资源
      findResourceAsBase64({ resourcePathList: staticResource }).then(rsp => {
        callBack(rsp.data)
      })
    } catch (e) {
      console.error('findResourceAsBase64 error', e)
      callBack()
    }
  } else {
    // 无静态资源，直接回调
    setTimeout(() => {
      callBack()
    }, 0)
  }
}
