# Core 模块架构文档

本文档详细描述 DataEase Core 模块的架构设计、模块职责和技术实现细节。

**注意**: 请先阅读根目录的 [/PROJECT.md](../PROJECT.md) 了解整体架构,本文档专注于 Core 模块的具体实现。

## Core 模块概述

Core 模块是 DataEase 的业务实现层,包含两个子模块:
- **core-backend**: Spring Boot 后端应用,实现所有业务逻辑
- **core-frontend**: Vue 3 前端应用,提供用户界面

## core-backend 架构

### 技术栈

- **Java 21** + **Spring Boot 3.3.0**
- **MyBatis Plus 3.5.6** - ORM 框架
- **Apache Calcite 1.35.24** - SQL 解析引擎
- **Quartz** - 定时任务调度
- **WebSocket** - 实时通信
- **H2** / **MySQL** - 数据库

### 应用入口

**主类**: `io.dataease.CoreApplication`

```java
@SpringBootApplication(exclude = {QuartzAutoConfiguration.class})
@EnableCaching
@EnableScheduling
public class CoreApplication {
    public static void main(String[] args) {
        SpringApplication context = new SpringApplication(CoreApplication.class);
        context.addInitializers(new EhCacheStartListener());
        context.run(args);
    }
}
```

**启动流程**:
1. 加载 Spring Boot 配置
2. 初始化 EhCache 缓存
3. 执行 Flyway 数据库迁移
4. 启动嵌入式 Tomcat 服务器
5. 加载静态资源(前端打包文件)

### 包结构详解

```
io.dataease/
├── ai/                     # AI 智能问答功能
├── chart/                  # 图表管理
├── commons/                # 公共工具类
├── config/                 # Spring 配置类
├── dataset/                # 数据集管理
├── datasource/             # 数据源管理
├── defeign/                # Feign 客户端(企业版)
├── engine/                 # 数据查询引擎
├── exportCenter/           # 导出中心
├── font/                   # 字体管理
├── home/                   # 首页/工作台
├── interceptor/            # 请求拦截器
├── job/                    # 定时任务
├── license/                # License 管理
├── listener/               # 事件监听器
├── map/                    # 地图服务
├── menu/                   # 菜单管理
├── msgCenter/              # 消息中心
├── operation/              # 操作日志
├── resource/               # 资源管理
├── share/                  # 分享管理
├── startup/                # 启动初始化
├── substitute/             # 权限替补实现(社区版)
├── system/                 # 系统管理
├── template/               # 模板管理
├── visualization/          # 可视化/仪表板
└── websocket/              # WebSocket 服务
```

### 核心业务模块

#### 1. chart - 图表管理

**职责**: 管理图表的创建、编辑、查询和渲染

**核心类**:
- `ChartController`: 图表 REST API
- `ChartViewService`: 图表业务逻辑
- `ChartDataService`: 图表数据查询

**主要功能**:
- 图表 CRUD 操作
- 图表数据查询
- 图表类型管理(柱状图、折线图、饼图等)
- 图表配置管理

**数据表**: `core_chart_view`

#### 2. dataset - 数据集管理

**职责**: 管理数据集,包括数据表、SQL 数据集、API 数据集

**核心类**:
- `DatasetController`: 数据集 REST API
- `DatasetTableService`: 数据集业务逻辑
- `DatasetDataService`: 数据集数据查询

**数据集类型**:
- **DB 表**: 直接连接数据库表
- **SQL 数据集**: 自定义 SQL 查询
- **Excel 数据集**: 上传 Excel 文件
- **API 数据集**: 调用外部 API

**数据表**: `core_dataset_table`, `core_dataset_table_field`

#### 3. datasource - 数据源管理

**职责**: 管理各类数据源的连接和配置

**核心类**:
- `DatasourceController`: 数据源 REST API
- `DatasourceService`: 数据源业务逻辑
- `provider/`: 各类数据源的实现
  - `JdbcProvider`: JDBC 数据源(MySQL, Oracle, etc.)
  - `ApiProvider`: API 数据源
  - `ExcelProvider`: Excel 文件数据源

**支持的数据源**:
- **OLTP**: MySQL, Oracle, SQL Server, PostgreSQL, MariaDB
- **OLAP**: ClickHouse, Doris, Impala, StarRocks
- **NoSQL**: MongoDB (BI 模式)
- **文件**: Excel, CSV
- **API**: RESTful API

**数据表**: `core_datasource`, `core_datasource_task`

#### 4. visualization - 可视化/仪表板

**职责**: 管理仪表板(Dashboard)的创建、编辑和展示

**核心类**:
- `VisualizationController`: 仪表板 REST API
- `DataVisualizationService`: 仪表板业务逻辑
- `VisualizationBackgroundService`: 背景管理

**主要功能**:
- 仪表板 CRUD
- 组件布局管理
- 图表联动
- 跳转配置
- 外部参数
- 公开链接
- 移动端适配

**数据表**: `visualization_info`, `visualization_background`

#### 5. engine - 数据查询引擎

**职责**: 使用 Apache Calcite 进行 SQL 解析、优化和执行

**核心类**:
- `CalciteProvider`: Calcite SQL 解析器
- `QueryProvider`: 查询执行器
- `DatasetSQLProvider`: 数据集 SQL 生成

**功能**:
- SQL 解析和验证
- SQL 方言转换(MySQL → ClickHouse, etc.)
- 查询优化
- 多数据源联邦查询
- 缓存管理

**工作流程**:
```
用户查询请求
    ↓
解析查询条件(过滤、排序、分组)
    ↓
生成 Calcite SQL
    ↓
SQL 方言转换
    ↓
执行目标数据源查询
    ↓
结果处理和格式化
    ↓
返回给前端
```

#### 6. template - 模板管理

**职责**: 模板市场,提供预制的仪表板模板

**核心类**:
- `TemplateController`: 模板 REST API
- `TemplateService`: 模板业务逻辑
- `TemplateMarketService`: 模板市场

**功能**:
- 模板浏览和搜索
- 模板应用(复制到工作区)
- 模板分类管理
- 官方模板和自定义模板

**数据表**: `visualization_template`, `visualization_template_category`

#### 7. system - 系统管理

**职责**: 系统配置、用户管理、角色权限

**核心类**:
- `SysUserController`: 用户管理
- `SysRoleController`: 角色管理
- `SysParameterService`: 系统参数

**功能**:
- 用户 CRUD
- 角色和权限管理
- 系统参数配置(邮件、LDAP、OIDC、SAML等)
- 主题和 Logo 配置

**数据表**: `core_sys_user`, `core_sys_role`, `core_sys_setting`

#### 8. share - 分享管理

**职责**: 管理仪表板的分享和公开链接

**核心类**:
- `ShareController`: 分享 REST API
- `ShareService`: 分享业务逻辑

**分享方式**:
- 公开链接(匿名访问)
- 密码保护
- 有效期控制
- 自动刷新

**数据表**: `core_share`, `core_share_ticket`

#### 9. exportCenter - 导出中心

**职责**: 管理仪表板和图表的导出任务

**核心类**:
- `ExportCenterController`: 导出 REST API
- `ExportCenterService`: 导出业务逻辑
- `ExportPdfService`: PDF 导出(使用 Selenium)

**导出格式**:
- **图片**: PNG, JPEG
- **PDF**: 完整仪表板
- **Excel**: 数据导出

**导出流程**:
```
用户触发导出
    ↓
创建导出任务
    ↓
(异步)使用 Selenium 渲染页面
    ↓
生成 PDF/图片
    ↓
保存到文件系统
    ↓
用户下载
```

**注意**: 仅 standalone 版本包含导出功能(依赖 Selenium)

#### 10. job - 定时任务

**职责**: 管理定时任务,如定时刷新数据、定时发送邮件

**核心类**:
- `ScheduleController`: 定时任务管理
- `ScheduleService`: 任务调度

**使用 Quartz**:
- 任务调度引擎
- Cron 表达式
- 任务持久化

**常见任务**:
- 数据集定时同步
- 仪表板定时推送
- 数据源连接检测

#### 11. ai - AI 智能问答

**职责**: 集成 SQLBot 实现自然语言查询

**核心类**:
- `AiController`: AI API
- `AiService`: AI 业务逻辑

**功能**:
- 自然语言转 SQL
- 智能推荐图表
- 数据分析建议

**依赖**: 外部 SQLBot 服务

#### 12. websocket - 实时通信

**职责**: 提供 WebSocket 服务,用于实时通知和消息推送

**核心类**:
- `WebSocketConfig`: WebSocket 配置
- `WebSocketHandler`: 消息处理器

**应用场景**:
- 实时通知
- 多人协作
- 任务状态更新

### Maven Profile 配置

Core-backend 支持三种构建 Profile:

#### 1. standalone (默认)

**特点**:
- 完整功能,包含所有模块
- 使用 H2 内嵌数据库
- 包含 PDF 导出功能(Selenium)
- 包含邮件发送功能
- 适合单机部署和开发环境

**额外依赖**:
- H2 Database
- Selenium Java
- Angus Mail (邮件)
- iText PDF
- Flexmark (Markdown 渲染)

**构建命令**: `mvn clean package` 或 `mvn clean package -P standalone`

#### 2. desktop (桌面版)

**特点**:
- 轻量级版本
- 使用 H2 数据库
- 使用权限替补实现(`substitute` 包)
- 不包含导出和邮件功能
- 适合个人使用

**构建命令**: `mvn clean package -P desktop`

#### 3. distributed (企业版)

**特点**:
- 分布式部署支持
- 需要外部 MySQL 数据库
- 引入 `sdk/distributed` 模块
- 排除 `substitute` 包
- 完整权限管理
- 多租户支持

**额外依赖**:
- `sdk/distributed` 模块

**构建命令**: `mvn clean package -P distributed`

**编译器配置**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <excludes>
            <exclude>io/dataease/substitute/**</exclude>
        </excludes>
    </configuration>
</plugin>
```

### 配置文件

**配置文件位置**: `src/main/resources/`

```
resources/
├── application.yml               # 主配置文件
├── application-standalone.yml    # standalone 配置
├── application-desktop.yml       # desktop 配置
├── application-distributed.yml   # distributed 配置
├── db/migration/                 # Flyway 迁移脚本
├── ehcache.xml                   # EhCache 配置
├── mapper/                       # MyBatis XML 映射
└── static/                       # 静态资源(前端打包文件)
```

**Profile 激活**:
```yaml
# application.yml
spring:
  profiles:
    active: @profiles.active@  # 由 Maven Profile 注入
```

### 依赖的 SDK 模块

Core-backend 依赖以下 SDK 模块:

1. **api-base**: 基础 API 和数据模型
2. **api-permissions**: 权限 API
3. **api-sync**: 数据同步 API

**pom.xml**:
```xml
<dependency>
    <groupId>io.dataease</groupId>
    <artifactId>api-base</artifactId>
    <version>${dataease.version}</version>
</dependency>
<dependency>
    <groupId>io.dataease</groupId>
    <artifactId>api-permissions</artifactId>
    <version>${dataease.version}</version>
</dependency>
<dependency>
    <groupId>io.dataease</groupId>
    <artifactId>api-sync</artifactId>
    <version>${dataease.version}</version>
</dependency>
```

## core-frontend 架构

### 技术栈

- **Vue 3.3.4** - 渐进式 JavaScript 框架
- **TypeScript 4.9.3** - 类型安全
- **Vite 4.1.3** - 构建工具
- **Element Plus** - UI 组件库
- **Pinia 2.0.32** - 状态管理
- **Vue Router 4.1.3** - 路由管理
- **Axios 1.3.3** - HTTP 客户端
- **AntV (G2Plot, L7, S2)** - 数据可视化
- **ECharts 5.5.1** - 图表库

### 应用入口

Core-frontend 是一个多页面应用(MPA),有三个入口:

#### 1. 主应用 (index.html)

**入口**: `src/pages/index/main.ts`

**用途**: 主管理界面

**路由**: 工作台、数据源、数据集、图表、仪表板、系统管理等

#### 2. 仪表板预览 (panel.html)

**入口**: `src/pages/panel/main.ts`

**用途**: 仪表板独立预览页面

**特点**: 全屏展示,支持公开链接

#### 3. 移动端 (mobile.html)

**入口**: `src/pages/mobile/main.ts`

**用途**: 移动端适配

**特点**: 响应式布局,触摸优化

### 目录结构详解

```
core-frontend/src/
├── api/                    # API 接口定义
│   ├── chart.ts           # 图表 API
│   ├── dataset.ts         # 数据集 API
│   ├── datasource.ts      # 数据源 API
│   ├── visualization/     # 仪表板 API
│   │   ├── dataVisualization.ts
│   │   ├── linkage.ts    # 联动配置
│   │   ├── linkJump.ts   # 跳转配置
│   │   └── outerParams.ts # 外部参数
│   └── ...
│
├── views/                  # 页面视图
│   ├── chart/             # 图表管理页面
│   ├── data/              # 数据管理(数据源、数据集)
│   ├── dashboard/         # 仪表板管理
│   ├── system/            # 系统管理
│   └── ...
│
├── components/             # 公共组件
│   ├── layout/            # 布局组件
│   │   ├── AppLayout.vue
│   │   ├── Sidebar.vue
│   │   └── Header.vue
│   ├── common/            # 通用组件
│   │   ├── Dialog.vue
│   │   ├── Table.vue
│   │   └── ...
│   └── ...
│
├── custom-component/       # 自定义业务组件
│   ├── common/            # 通用自定义组件
│   ├── v-chart/           # 图表组件封装
│   │   ├── LineChart.vue  # 折线图
│   │   ├── BarChart.vue   # 柱状图
│   │   ├── PieChart.vue   # 饼图
│   │   └── ...
│   └── component-list/    # 组件列表
│
├── store/                  # Pinia 状态管理
│   ├── modules/           # 模块化 store
│   │   ├── app.ts        # 应用全局状态
│   │   ├── user.ts       # 用户状态
│   │   ├── dashboard.ts  # 仪表板状态
│   │   └── ...
│   └── index.ts          # Store 入口
│
├── router/                 # 路由配置
│   ├── index.ts           # 主路由
│   ├── modules/           # 模块路由
│   │   ├── chart.ts
│   │   ├── data.ts
│   │   └── ...
│   └── permission.ts      # 路由权限控制
│
├── hooks/                  # Composition API Hooks
│   ├── useChart.ts        # 图表相关 Hook
│   ├── useDataset.ts      # 数据集相关 Hook
│   └── ...
│
├── utils/                  # 工具函数
│   ├── request.ts         # Axios 封装
│   ├── auth.ts            # 认证工具
│   ├── storage.ts         # 本地存储
│   └── ...
│
├── assets/                 # 静态资源
│   ├── images/            # 图片
│   ├── icons/             # 图标
│   └── styles/            # 全局样式
│
├── locales/                # 国际化
│   ├── zh-CN/             # 简体中文
│   │   ├── common.ts
│   │   ├── chart.ts
│   │   └── ...
│   └── en-US/             # 英文
│       └── ...
│
├── directive/              # Vue 指令
│   ├── permission.ts      # 权限指令
│   └── ...
│
├── models/                 # TypeScript 类型定义
│   ├── chart.d.ts
│   ├── dataset.d.ts
│   └── ...
│
├── pages/                  # 多页面入口
│   ├── index/             # 主应用
│   │   ├── main.ts
│   │   └── App.vue
│   ├── panel/             # 仪表板预览
│   │   ├── main.ts
│   │   └── App.vue
│   └── mobile/            # 移动端
│       ├── main.ts
│       └── App.vue
│
├── plugins/                # Vue 插件
│   ├── element.ts         # Element Plus 配置
│   └── ...
│
├── config/                 # 配置文件
│   ├── base.ts            # 基础配置
│   ├── dev.ts             # 开发配置
│   └── ...
│
├── permission.ts           # 全局权限控制
├── permissionMobile.ts     # 移动端权限控制
└── Types.ts                # 全局类型定义
```

### 核心功能模块

#### 1. 工作台 (home)

**路由**: `/`

**功能**:
- 最近访问
- 我的收藏
- 快捷入口
- 统计概览

#### 2. 数据源管理

**路由**: `/data/datasource`

**主要页面**:
- `views/data/datasource/index.vue`: 数据源列表
- `views/data/datasource/form.vue`: 数据源配置表单

**功能**:
- 添加/编辑数据源
- 测试连接
- 删除数据源

#### 3. 数据集管理

**路由**: `/data/dataset`

**主要页面**:
- `views/data/dataset/index.vue`: 数据集列表
- `views/data/dataset/form.vue`: 数据集配置
- `views/data/dataset/field-config.vue`: 字段配置

**功能**:
- 创建数据集(DB表、SQL、Excel、API)
- 字段管理
- 数据预览
- 关联关系

#### 4. 图表管理

**路由**: `/chart`

**主要页面**:
- `views/chart/index.vue`: 图表列表
- `views/chart/edit.vue`: 图表编辑器

**图表类型** (`custom-component/v-chart/`):
- 柱状图 (BarChart)
- 折线图 (LineChart)
- 饼图 (PieChart)
- 散点图 (ScatterChart)
- 雷达图 (RadarChart)
- 漏斗图 (FunnelChart)
- 地图 (MapChart)
- 表格 (TableChart)
- 指标卡 (IndicatorChart)

#### 5. 仪表板管理

**路由**: `/dashboard`

**主要页面**:
- `views/dashboard/index.vue`: 仪表板列表
- `views/dashboard/editor/`: 仪表板编辑器
  - `index.vue`: 编辑器主页面
  - `canvas/`: 画布组件
  - `toolbar/`: 工具栏
  - `component-panel/`: 组件面板

**功能**:
- 拖拽布局
- 组件配置
- 图表联动
- 跳转配置
- 外部参数
- 主题设置
- 移动端适配

**编辑器架构**:
```
DashboardEditor
├── Toolbar (工具栏)
│   ├── Save (保存)
│   ├── Preview (预览)
│   ├── Settings (设置)
│   └── Mobile (移动端)
├── ComponentPanel (组件面板)
│   ├── Charts (图表)
│   ├── Filters (过滤器)
│   ├── Texts (文本)
│   └── Media (媒体)
├── Canvas (画布)
│   ├── GridLayout (网格布局)
│   └── Components (组件实例)
└── PropertyPanel (属性面板)
    ├── BasicSettings (基础设置)
    ├── StyleSettings (样式设置)
    └── DataSettings (数据设置)
```

#### 6. 模板市场

**路由**: `/template`

**功能**:
- 浏览官方模板
- 应用模板到工作区
- 模板分类和搜索

#### 7. 系统管理

**路由**: `/system`

**子页面**:
- 用户管理 (`/system/user`)
- 角色管理 (`/system/role`)
- 系统参数 (`/system/parameter`)
- 外观设置 (`/system/appearance`)

### 状态管理

使用 Pinia 进行状态管理,主要 Store:

#### 1. appStore (应用状态)

**文件**: `store/modules/app.ts`

**State**:
- `theme`: 主题(dark/light)
- `language`: 语言
- `sidebar`: 侧边栏状态

#### 2. userStore (用户状态)

**文件**: `store/modules/user.ts`

**State**:
- `userInfo`: 用户信息
- `token`: JWT Token
- `permissions`: 用户权限列表

**Actions**:
- `login()`: 登录
- `logout()`: 登出
- `refreshToken()`: 刷新 Token

#### 3. dashboardStore (仪表板状态)

**文件**: `store/modules/dashboard.ts`

**State**:
- `currentDashboard`: 当前编辑的仪表板
- `components`: 仪表板组件列表
- `selectedComponent`: 当前选中的组件

**Actions**:
- `addComponent()`: 添加组件
- `updateComponent()`: 更新组件
- `deleteComponent()`: 删除组件
- `saveDashboard()`: 保存仪表板

### 路由配置

**主路由** (`router/index.ts`):
```typescript
const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '工作台', icon: 'home' }
      },
      ...dataRoutes,      // 数据管理路由
      ...chartRoutes,     // 图表管理路由
      ...dashboardRoutes, // 仪表板路由
      ...systemRoutes     // 系统管理路由
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  }
]
```

**路由守卫** (`permission.ts`):
```typescript
router.beforeEach((to, from, next) => {
  // 1. 检查 Token
  // 2. 检查权限
  // 3. 重定向或继续
})
```

### Vite 构建配置

**配置文件**: `vite.config.ts`

**构建模式**:

#### 1. dev (开发模式)

**配置**: `config/dev.ts`

```typescript
export default {
  server: {
    port: 5173,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
}
```

**特点**:
- 热更新(HMR)
- 代理后端 API
- Source Map

**启动**: `npm run dev`

#### 2. base (单机版构建)

**配置**: `config/base.ts`

**特点**:
- 生产优化
- 代码压缩
- Tree Shaking

**构建**: `npm run build:base`

**输出**: `dist/` 目录

#### 3. distributed (分布式版构建)

**配置**: `config/distributed.ts`

**特点**:
- 与 base 类似,但可能包含企业版特定功能

**构建**: `npm run build:distributed`

#### 4. lib (库模式)

**配置**: `config/lib.ts`

**用途**: 将组件打包为库供外部使用

**构建**: `npm run build:lib`

### API 请求封装

**请求工具** (`utils/request.ts`):
```typescript
import axios from 'axios'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器
service.interceptors.request.use(config => {
  const token = getToken()
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 0) {
      // 错误处理
      ElMessage.error(res.msg)
      return Promise.reject(new Error(res.msg))
    }
    return res.data
  },
  error => {
    // 统一错误处理
    return Promise.reject(error)
  }
)

export default service
```

### 图表渲染流程

```
用户选择图表类型
    ↓
配置图表属性(数据源、样式等)
    ↓
保存图表配置(JSON)
    ↓
渲染图表:
    ├─ 解析配置 JSON
    ├─ 调用 API 获取数据
    ├─ 根据图表类型选择渲染引擎
    │   ├─ AntV G2Plot (基础图表)
    │   ├─ AntV L7 (地图)
    │   ├─ AntV S2 (表格)
    │   └─ ECharts (复杂图表)
    └─ 渲染到 Canvas/SVG
```

### 国际化

**使用 vue-i18n**:

**语言切换**:
```typescript
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()

// 切换语言
locale.value = 'en-US' // 或 'zh-CN'

// 使用翻译
t('chart.create')
```

**语言包结构** (`locales/zh-CN/`):
```typescript
// common.ts
export default {
  common: {
    save: '保存',
    cancel: '取消',
    delete: '删除',
    confirm: '确认'
  }
}

// chart.ts
export default {
  chart: {
    title: '图表',
    create: '创建图表',
    type: {
      bar: '柱状图',
      line: '折线图',
      pie: '饼图'
    }
  }
}
```

## 前后端集成

### 构建流程

```
1. 前端构建
   cd core-frontend
   npm run build:base
   → 生成 dist/ 目录

2. Maven 复制前端资源
   maven-antrun-plugin
   → 复制 dist/ 到 core-backend/src/main/resources/static/

3. Spring Boot 打包
   mvn package
   → 生成 CoreApplication.jar (包含前端资源)

4. 运行
   java -jar CoreApplication.jar
   → 前端资源通过 Spring MVC 静态资源映射提供服务
```

### 静态资源映射

Spring Boot 自动配置静态资源映射:
- URL: `/*` 映射到 `classpath:/static/`
- 前端路由: 使用 HTML5 History 模式,由后端返回 `index.html`

**Spring MVC 配置**:
```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}
```

### API 调用

前端通过 Axios 调用后端 REST API:

**前端**:
```typescript
// api/chart.ts
export const getChartList = () => {
  return request.get('/chart/list')
}
```

**后端**:
```java
@RestController
@RequestMapping("/api/chart")
public class ChartController {
    @GetMapping("/list")
    public ResultHolder<List<ChartDTO>> list() {
        return ResultHolder.success(chartService.list());
    }
}
```

**请求流程**:
```
浏览器 → Axios → /api/chart/list → Spring MVC → ChartController → Service → Mapper → MySQL
```

## 数据库设计

### 核心表

#### 1. core_chart_view (图表)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR | 图表名称 |
| type | VARCHAR | 图表类型 |
| dataset_id | BIGINT | 数据集ID |
| style_config | TEXT | 样式配置(JSON) |
| view_fields | TEXT | 字段配置(JSON) |
| create_time | BIGINT | 创建时间 |

#### 2. core_dataset_table (数据集)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR | 数据集名称 |
| type | VARCHAR | 类型(db/sql/excel/api) |
| datasource_id | BIGINT | 数据源ID |
| info | TEXT | 配置信息(JSON) |

#### 3. core_datasource (数据源)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR | 数据源名称 |
| type | VARCHAR | 数据源类型 |
| configuration | TEXT | 连接配置(JSON) |
| status | VARCHAR | 状态(active/inactive) |

#### 4. visualization_info (仪表板)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR | 仪表板名称 |
| component_data | TEXT | 组件配置(JSON) |
| mobile_layout | TEXT | 移动端布局(JSON) |

## 部署和运维

### 开发环境

```bash
# 启动后端
cd core/core-backend
mvn spring-boot:run

# 启动前端
cd core/core-frontend
npm run dev
```

### 生产环境

```bash
# 完整构建
mvn clean package -P standalone

# 运行
cd core/core-backend/target
java -jar CoreApplication.jar

# 或指定配置
java -jar CoreApplication.jar \
  --spring.profiles.active=standalone \
  --server.port=8081
```

### Docker 部署

项目根目录提供 `Dockerfile`:

```bash
# 构建镜像
docker build -t dataease:latest .

# 运行容器
docker run -d \
  -p 8081:8081 \
  -v /opt/dataease/data:/opt/dataease/data \
  dataease:latest
```

## 性能优化

### 后端优化

1. **数据库连接池**: HikariCP (默认)
2. **查询优化**: 使用 MyBatis Plus 分页
3. **缓存**: EhCache + Redis
4. **异步处理**: `@Async` 注解

### 前端优化

1. **代码分割**: Vite 自动代码分割
2. **路由懒加载**: `() => import()`
3. **虚拟滚动**: 大列表使用虚拟滚动
4. **图表按需加载**: 只加载使用的图表类型

## 安全

### 认证授权

- **JWT Token**: 无状态认证
- **权限注解**: `@DePermission`
- **CORS**: 配置跨域策略

### 数据安全

- **SQL 注入防护**: MyBatis 参数绑定
- **XSS 防护**: 前端使用 `xss` 库
- **密码加密**: BCrypt

## 监控和日志

### 日志

- **日志框架**: SLF4J + Logback
- **日志级别**: DEBUG, INFO, WARN, ERROR
- **日志文件**: `logs/dataease.log`

### 监控

- **健康检查**: `/actuator/health`
- **指标监控**: `/actuator/metrics`

## 相关文档

- [/PROJECT.md](../PROJECT.md) - 系统整体架构
- [/CLAUDE.md](../CLAUDE.md) - 全局开发规范
- [core/CLAUDE.md](./CLAUDE.md) - Core 模块开发规范
