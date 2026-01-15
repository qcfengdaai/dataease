# DataEase Core 前端项目开发文档

> 基于 Vue 3 + TypeScript + Vite 4 的现代化 BI 前端应用

## 📋 目录

- [项目概述](#项目概述)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [核心模块详解](#核心模块详解)
- [开发指南](#开发指南)
- [API 说明](#api-说明)
- [组件库](#组件库)
- [状态管理](#状态管理)
- [路由配置](#路由配置)
- [样式系统](#样式系统)
- [构建部署](#构建部署)
- [常见问题](#常见问题)

---

## 项目概述

DataEase Core 前端是 DataEase BI 系统的前端应用，采用前后端分离架构，提供强大的数据可视化和分析能力。

### 核心特性

- **🎨 可视化编辑器**: 拖拽式图表设计，支持 40+ 图表类型
- **📱 多端适配**: 桌面端、移动端、大屏展示
- **🌍 国际化**: 支持中文、英文、繁体中文
- **🎭 主题系统**: 明暗主题切换，自定义配色
- **🔐 权限控制**: 细粒度的权限管理系统
- **⚡ 性能优化**: 路由懒加载、组件按需导入

---

## 技术栈

### 核心框架

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.3.x | 渐进式前端框架 |
| TypeScript | 5.x | 类型安全的 JavaScript 超集 |
| Vite | 4.x | 新一代前端构建工具 |
| Pinia | 2.x | Vue 3 官方状态管理 |
| Vue Router | 4.x | Vue 官方路由 |

### UI 组件库

| 组件库 | 用途 |
|--------|------|
| Element Plus | 基础 UI 组件（二次封装） |
| Vant | 移动端 UI 组件 |
| AntV G2Plot | 基础图表库 |
| AntV L7 | 地图可视化 |
| AntV S2 | 表格组件 |
| ECharts | 复杂图表 |

### 工具库

| 库名 | 用途 |
|------|------|
| Axios | HTTP 请求 |
| Lodash | JavaScript 工具库 |
| Day.js | 日期时间处理 |
| TinyMCE | 富文本编辑器 |
| Vue Draggable | 拖拽功能 |
| VXE Table | 高级表格 |

---

## 快速开始

### 环境要求

- Node.js >= 16.0.0
- npm >= 8.0.0

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

访问: http://localhost:5173

### 构建生产版本

```bash
# 单机版
npm run build:base

# 分布式版
npm run build:distributed

# 库模式
npm run build:lib
```

### 代码检查

```bash
# ESLint 检查
npm run lint

# 样式检查
npm run lint:stylelint

# TypeScript 类型检查
npm run ts:check
```

---

## 项目结构

```
core-frontend/
├── public/                    # 公共静态资源
├── src/                       # 源代码目录
│   ├── api/                   # API 接口定义
│   ├── assets/                # 静态资源（图片、SVG）
│   ├── components/            # 通用组件
│   ├── custom-component/      # 自定义业务组件
│   ├── locales/               # 国际化语言包
│   ├── router/                # 路由配置
│   ├── store/                 # Pinia 状态管理
│   ├── style/                 # 全局样式
│   ├── utils/                 # 工具函数
│   ├── views/                 # 页面视图
│   ├── App.vue                # 根组件
│   └── main.ts                # 应用入口
├── types/                     # TypeScript 类型定义
├── config/                    # Vite 构建配置
├── vite.config.ts             # Vite 主配置
├── tsconfig.json              # TypeScript 配置
├── package.json               # 项目依赖
└── index.html                 # HTML 模板
```

---

## 核心模块详解

### 📡 API 层 (src/api/)

负责所有后端 API 接口的调用和类型定义。

#### 主要模块

| 文件 | 职责 |
|------|------|
| `chart.ts` | 图表管理 API |
| `dataset.ts` | 数据集管理 API |
| `datasource.ts` | 数据源管理 API |
| `auth.ts` | 认证授权 API |
| `login.ts` | 登录注销 API |
| `map.ts` | 地图相关 API |
| `setting/` | 系统设置 API |
| `sync/` | 数据同步 API |
| `visualization/` | 可视化编辑器 API |

#### API 定义示例

```typescript
// api/chart.ts
import request from '@/utils/request'

// 图表数据传输对象
export interface ChartDTO {
  id: number
  name: string
  type: string
  datasetId: number
}

// 创建图表请求
export interface ChartRequest {
  name: string
  type: string
  datasetId: number
}

// 获取图表列表
export const getChartList = (params?: any) => {
  return request.get<ChartDTO[]>('/api/chart/list', { params })
}

// 创建图表
export const createChart = (data: ChartRequest) => {
  return request.post<ChartDTO>('/api/chart/create', data)
}
```

---

### 🎨 组件系统

#### 通用组件 (src/components/)

基础通用组件，可在整个应用中复用。

| 组件目录 | 说明 |
|----------|------|
| `common/` | 通用基础组件 |
| `dashboard/` | 仪表板组件 |
| `data-visualization/` | 数据可视化组件 |
| `grid-table/` | 网格表格组件 |
| `tree-select/` | 树形选择器 |
| `watermark/` | 水印组件 |
| `rich-text/` | 富文本组件 |
| `cron/` | 定时任务组件 |
| `color-scheme/` | 主题配色组件 |

#### 自定义业务组件 (src/custom-component/)

DataEase 特有的可视化组件。

| 组件目录 | 说明 |
|----------|------|
| `v-chart/` | 图表组件（核心） |
| `canvas-board/` | 画板组件 |
| `de-frame/` | 框架组件 |
| `de-tabs/` | 标签页组件 |
| `de-time-clock/` | 时钟组件 |
| `de-video/` | 视频组件 |
| `de-stream-media/` | 流媒体组件 |
| `dynamic_background/` | 动态背景 |
| `group/` | 分组组件 |
| `indicator/` | 指标组件 |
| `picture/` | 图片组件 |
| `v-text/` | 文本组件 |
| `v-query/` | 查询组件 |
| `user-view/` | 用户视图 |

#### 组件开发规范

```vue
<template>
  <div class="chart-container">
    <el-button @click="handleCreate">创建图表</el-button>
  </div>
</template>

<script setup lang="ts" name="ChartList">
import { ref, onMounted } from 'vue'
import { getChartList } from '@/api/chart'
import type { ChartDTO } from '@/api/chart'

// 响应式数据
const chartList = ref<ChartDTO[]>([])

// 方法
const loadChartList = async () => {
  const res = await getChartList()
  chartList.value = res.data
}

// 生命周期
onMounted(() => {
  loadChartList()
})
</script>

<style scoped lang="less">
.chart-container {
  padding: 20px;
}
</style>
```

---

### 🗄️ 状态管理 (src/store/)

使用 Pinia 进行状态管理，模块化组织。

#### 核心模块

| 模块 | 职责 |
|------|------|
| `app.ts` | 应用全局状态 |
| `user.ts` | 用户信息状态 |
| `permission.ts` | 权限状态 |
| `appearance.ts` | 外观设置 |
| `locale.ts` | 国际化状态 |
| `data-visualization/` | 数据可视化状态 |
| `interactive.ts` | 交互状态 |
| `map.ts` | 地图状态 |
| `share.ts` | 分享状态 |
| `request.ts` | 请求状态 |

#### Store 定义示例

```typescript
// store/modules/chart.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { ChartDTO } from '@/api/chart'

export const useChartStore = defineStore('chart', () => {
  // State
  const currentChart = ref<ChartDTO | null>(null)
  const chartList = ref<ChartDTO[]>([])

  // Actions
  const setCurrentChart = (chart: ChartDTO) => {
    currentChart.value = chart
  }

  const updateChartList = (list: ChartDTO[]) => {
    chartList.value = list
  }

  // Getters
  const hasCurrentChart = computed(() => !!currentChart.value)

  return {
    currentChart,
    chartList,
    setCurrentChart,
    updateChartList,
    hasCurrentChart
  }
})
```

---

### 🛣️ 路由配置 (src/router/)

支持多页面应用的路由系统。

#### 路由文件

| 文件 | 说明 |
|------|------|
| `index.ts` | 主路由配置 |
| `embedded.ts` | 嵌入式路由 |
| `mobile.ts` | 移动端路由 |
| `establish.ts` | 路由建立 |

#### 路由定义示例

```typescript
import type { RouteRecordRaw } from 'vue-router'

const chartRoutes: RouteRecordRaw[] = [
  {
    path: '/chart',
    name: 'Chart',
    component: () => import('@/views/chart/index.vue'),
    meta: {
      title: '图表管理',
      icon: 'chart',
      requiresAuth: true
    }
  }
]

export default chartRoutes
```

---

### 🌍 国际化 (src/locales/)

支持多语言的国际化系统。

#### 语言文件

| 语言 | 文件 |
|------|------|
| 简体中文 | `zh-CN.ts` |
| 英文 | `en.ts` |
| 繁体中文 | `tw.ts` |

#### 使用示例

```typescript
// locales/zh-CN/chart.ts
export default {
  chart: {
    title: '图表',
    create: '创建图表',
    edit: '编辑图表',
    delete: '删除图表'
  }
}
```

```vue
<template>
  <el-button>{{ t('chart.create') }}</el-button>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
</script>
```

---

### 🔧 工具函数 (src/utils/)

通用工具函数库。

| 文件 | 职责 |
|------|------|
| `utils.ts` | 通用工具函数 |
| `canvasUtils.ts` | 画布工具（核心） |
| `canvasStyle.ts` | 画布样式工具 |
| `calculateComponentPositionAndSize.ts` | 组件位置计算 |
| `components.ts` | 组件工具 |
| `style.ts` | 样式工具 |
| `color.ts` | 颜色处理 |
| `timeUitls.ts` | 时间工具 |
| `attr.ts` | 属性处理 |
| `cacheUtil.ts` | 缓存工具 |
| `check.ts` | 检查工具 |

---

## 开发指南

### 📝 代码规范

#### TypeScript 规范

1. **类型定义**: 所有函数参数和返回值必须定义类型
2. **接口优先**: 优先使用 `interface` 定义对象类型
3. **严格模式**: 启用 TypeScript 严格模式

#### Vue 组件规范

1. **使用 `<script setup>`**: Composition API 的推荐写法
2. **组件命名**: 组件文件使用 PascalCase，组件内使用 `name` 属性
3. **Props 定义**: 使用 TypeScript 接口定义 Props
4. **Emits 类型**: 使用 TypeScript 接口定义 Emit 事件

#### 样式规范

1. **使用 Less**: 样式预处理器
2. **Scoped 样式**: 避免样式污染
3. **BEM 命名**: 可选的 CSS 命名规范
4. **变量使用**: 使用 Less 变量定义颜色、尺寸等

### 🎯 开发流程

1. **创建组件**: 在对应的组件目录下创建 `.vue` 文件
2. **定义类型**: 在 `api/` 或 `types/` 中定义类型
3. **编写 API**: 在 `api/` 中定义接口调用
4. **状态管理**: 需要全局状态时在 `store/` 中定义
5. **路由配置**: 在 `router/` 中添加路由
6. **国际化**: 在 `locales/` 中添加翻译

### 🔍 调试技巧

1. **Vue DevTools**: 安装 Vue DevTools 浏览器插件
2. **网络请求**: 在浏览器开发者工具的 Network 面板查看
3. **控制台日志**: 使用 `console.log` 或 `debugger` 调试
4. **TypeScript 错误**: 查看 IDE 的类型检查提示

---

## 样式系统

### 全局样式变量 (src/style/variable.less)

```less
// 颜色变量
@primary-color: #3370ff;
@success-color: #00c49f;
@warning-color: #ff8800;
@danger-color: #f54a45;

// 字体
@font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;

// 间距
@spacing-xs: 4px;
@spacing-sm: 8px;
@spacing-md: 16px;
@spacing-lg: 24px;
@spacing-xl: 32px;

// 圆角
@border-radius-sm: 2px;
@border-radius-md: 4px;
@border-radius-lg: 8px;
```

### 样式组织

```
src/style/
├── variable.less      # 全局变量
├── global.less        # 全局样式
├── transition.less    # 过渡动画
└── index.less         # 样式入口
```

---

## 构建部署

### 构建配置

项目支持多种构建模式：

- **开发模式**: 热更新、源码映射
- **单机版**: 完整功能，H2 数据库
- **分布式版**: 分布式支持，MySQL 数据库
- **库模式**: 构建为可复用的组件库

### 环境变量

```
config/
├── dev.ts            # 开发环境
├── base.ts           # 单机版
└── distributed.ts    # 分布式版
```

### 构建优化

1. **代码分割**: 路由级别的代码分割
2. **组件懒加载**: 按需加载组件
3. **Tree Shaking**: 移除未使用的代码
4. **压缩**: CSS 和 JavaScript 压缩

---

## 常见问题

### Q1: npm install 失败？

**解决方案**:

```bash
# 清除缓存并重新安装
rm -rf node_modules package-lock.json
npm install

# Windows
rmdir /s /q node_modules
del package-lock.json
npm install
```

### Q2: 端口被占用？

修改 `vite.config.ts` 中的端口配置：

```typescript
server: {
  port: 5173, // 修改为其他端口
}
```

### Q3: TypeScript 类型错误？

运行类型检查查看详细错误：

```bash
npm run ts:check
```

### Q4: 样式不生效？

1. 检查 `scoped` 属性
2. 确认 Less 语法正确
3. 查看浏览器开发者工具

---

## 相关资源

- [Vue 3 官方文档](https://cn.vuejs.org/)
- [TypeScript 官方文档](https://www.typescriptlang.org/zh/)
- [Vite 官方文档](https://cn.vitejs.dev/)
- [Element Plus 官方文档](https://element-plus.org/zh-CN/)
- [Pinia 官方文档](https://pinia.vuejs.org/zh/)

---

*文档版本: 1.0.0*
*最后更新: 2026-01-12*
