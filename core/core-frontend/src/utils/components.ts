/**
 * 组件映射工具
 * 提供组件和组件属性配置的查找功能
 */

// ==================== 组件导入 ====================

import VText from '@/custom-component/v-text/Component.vue'
import VQuery from '@/custom-component/v-query/Component.vue'
import VTextAttr from '@/custom-component/v-text/Attr.vue'
import Group from '@/custom-component/group/Component.vue'
import GroupAttr from '@/custom-component/group/Attr.vue'
import UserView from '@/custom-component/user-view/Component.vue'
import UserViewAttr from '@/custom-component/user-view/Attr.vue'
import Picture from '@/custom-component/picture/Component.vue'
import PictureAttr from '@/custom-component/picture/Attr.vue'
import DynamicBackground from '@/custom-component/dynamic_background/Component.vue'
import DynamicBackgroundAttr from '@/custom-component/dynamic_background/Attr.vue'
import DeDecoration from '@/custom-component/de-decoration/Component.vue'
import DeDecorationAttr from '@/custom-component/de-decoration/Attr.vue'
import CanvasBoard from '@/custom-component/canvas-board/Component.vue'
import CanvasBoardAttr from '@/custom-component/canvas-board/Attr.vue'
import CanvasIcon from '@/custom-component/canvas-icon/Component.vue'
import CanvasIconAttr from '@/custom-component/canvas-icon/Attr.vue'
import DeTabs from '@/custom-component/de-tabs/Component.vue'
import DeTabsAttr from '@/custom-component/de-tabs/Attr.vue'
import DeGraphical from '@/custom-component/de-graphical/Component.vue'
import DeGraphicalAttr from '@/custom-component/de-graphical/Attr.vue'
import CircleShape from '@/custom-component/circle-shape/Component.vue'
import CircleShapeAttr from '@/custom-component/circle-shape/Attr.vue'
import RectShape from '@/custom-component/rect-shape/Component.vue'
import RectShapeAttr from '@/custom-component/rect-shape/Attr.vue'
import SvgTriangle from '@/custom-component/svgs/svg-triangle/Component.vue'
import SvgTriangleAttr from '@/custom-component/svgs/svg-triangle/Attr.vue'
import DeTimeClock from '@/custom-component/de-time-clock/Component.vue'
import DeTimeClockAttr from '@/custom-component/de-time-clock/Attr.vue'
import GroupArea from '@/custom-component/group-area/Component.vue'
import GroupAreaAttr from '@/custom-component/group-area/Attr.vue'
import DeFrame from '@/custom-component/de-frame/ComponentFrame.vue'
import DeFrameAttr from '@/custom-component/de-frame/Attr.vue'
import DeScreen from '@/custom-component/de-screen/Component.vue'
import DeScreenAttr from '@/custom-component/de-screen//Attr.vue'
import DeVideo from '@/custom-component/de-video/Component.vue'
import DeVideoAttr from '@/custom-component/de-video/Attr.vue'
import DeStreamMedia from '@/custom-component/de-stream-media/Component.vue'
import DeStreamMediaAttr from '@/custom-component/de-stream-media/Attr.vue'
import ScrollText from '@/custom-component/scroll-text/Component.vue'
import ScrollTextAttr from '@/custom-component/scroll-text/Attr.vue'
import PopArea from '@/custom-component/pop-area/Component.vue'
import PopAreaAttr from '@/custom-component/pop-area/Attr.vue'
import PictureGroup from '@/custom-component/picture-group/Component.vue'
import PictureGroupAttr from '@/custom-component/picture-group/Attr.vue'

// ==================== 组件映射表 ====================

/**
 * 组件映射表
 * 将组件名称映射到对应的组件类和属性配置类
 *
 * 组件命名规则：
 * - 组件类：使用 PascalCase（如 VText）
 * - 属性配置类：组件名 + Attr（如 VTextAttr）
 */
export const componentsMap = {
  // 文本组件
  VText: VText,
  VQuery,
  VTextAttr: VTextAttr,

  // 分组组件
  Group: Group,
  GroupAttr: GroupAttr,

  // 用户视图组件
  UserView: UserView,
  UserViewAttr: UserViewAttr,

  // 图片组件
  Picture: Picture,
  PictureAttr: PictureAttr,

  // 动态背景组件
  DynamicBackground: DynamicBackground,
  DynamicBackgroundAttr: DynamicBackgroundAttr,

  // 画布组件
  CanvasBoard: CanvasBoard,
  CanvasBoardAttr: CanvasBoardAttr,

  // 画布图标组件
  CanvasIcon: CanvasIcon,
  CanvasIconAttr: CanvasIconAttr,

  // 标签页组件
  DeTabs: DeTabs,
  DeTabsAttr: DeTabsAttr,

  // 图形组件
  DeGraphical: DeGraphical,
  DeGraphicalAttr: DeGraphicalAttr,

  // 形状组件
  CircleShape: CircleShape,
  CircleShapeAttr: CircleShapeAttr,
  RectShape: RectShape,
  RectShapeAttr: RectShapeAttr,

  // SVG 组件
  SvgTriangle: SvgTriangle,
  SvgTriangleAttr: SvgTriangleAttr,

  // 时间时钟组件
  DeTimeClock: DeTimeClock,
  DeTimeClockAttr: DeTimeClockAttr,

  // 分组区域组件
  GroupArea: GroupArea,
  GroupAreaAttr: GroupAreaAttr,

  // 框架组件
  DeFrame: DeFrame,
  DeFrameAttr: DeFrameAttr,

  // 视频组件
  DeVideo: DeVideo,
  DeVideoAttr: DeVideoAttr,

  // 流媒体组件
  DeStreamMedia: DeStreamMedia,
  DeStreamMediaAttr: DeStreamMediaAttr,

  // 滚动文本组件
  ScrollText: ScrollText,
  ScrollTextAttr: ScrollTextAttr,

  // 弹出区域组件
  PopArea: PopArea,
  PopAreaAttr: PopAreaAttr,

  // 图片分组组件
  PictureGroup: PictureGroup,
  PictureGroupAttr: PictureGroupAttr,

  // 装饰组件
  DeDecoration: DeDecoration,
  DeDecorationAttr: DeDecorationAttr,

  // 屏幕组件
  DeScreen: DeScreen,
  DeScreenAttr: DeScreenAttr
}

// ==================== 组件查找函数 ====================

/**
 * 根据组件键名查找组件
 * @param key - 组件键名
 * @returns 对应的组件类或 undefined
 *
 * @example
 * findComponent('VText')  // 返回 VText 组件
 * findComponent('Group')  // 返回 Group 组件
 */
export default function findComponent(key) {
  return componentsMap[key]
}

/**
 * 根据组件对象查找对应的属性配置组件
 * @param component - 组件对象
 * @returns 对应的属性配置组件或 undefined
 *
 * 特殊处理：
 * - UserView 组件且 innerType 为 'picture-group' 时返回 PictureGroupAttr
 * - 其他情况返回 组件名 + Attr
 *
 * @example
 * findComponentAttr({ component: 'VText' })           // 返回 VTextAttr
 * findComponentAttr({ component: 'UserView', innerType: 'picture-group' })  // 返回 PictureGroupAttr
 */
export function findComponentAttr(component) {
  const key =
    component.component === 'UserView' && component.innerType === 'picture-group'
      ? 'PictureGroupAttr'
      : component.component + 'Attr'
  return componentsMap[key]
}
