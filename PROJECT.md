# DataEase 项目架构文档

## 项目概述

DataEase 是一个开源的商业智能(BI)平台,支持多种数据源连接、图表制作和数据分析。本文档描述项目的整体架构、模块划分和依赖关系。

## 技术栈

### 后端技术栈
- **Java 21** - 编程语言
- **Spring Boot 3.3.0** - 应用框架
- **Spring Cloud 2023.0.1** - 微服务框架(分布式版使用)
- **MyBatis Plus 3.5.6** - ORM 框架
- **Apache Calcite 1.35.24** - SQL 解析和优化引擎
- **Flyway** - 数据库版本管理
- **H2 2.2.220** - 内嵌数据库(开发和单机版)
- **MySQL 8** - 生产数据库
- **Redis** - 缓存(可选)

### 前端技术栈
- **Vue 3.3.4** - 前端框架
- **TypeScript 4.9.3** - 类型安全
- **Vite 4.1.3** - 构建工具
- **Element Plus** - UI 组件库
- **Pinia 2.0.32** - 状态管理
- **AntV (G2Plot, L7, S2)** - 图表库
- **ECharts 5.5.1** - 图表库
- **Axios 1.3.3** - HTTP 客户端

## 项目结构

```
dataease/
├── sdk/                          # SDK 模块 - 基础设施层
│   ├── common/                   # 公共组件和工具
│   ├── api/                      # API 接口定义
│   │   ├── api-base/            # 基础 API
│   │   ├── api-permissions/     # 权限 API
│   │   └── api-sync/            # 同步 API
│   ├── extensions/              # 扩展点定义
│   │   ├── extensions-datasource/  # 数据源扩展
│   │   ├── extensions-view/        # 视图扩展
│   │   └── extensions-datafilling/ # 数据填报扩展
│   └── distributed/             # 分布式组件(企业版)
│
├── core/                         # Core 模块 - 业务实现层
│   ├── core-backend/            # 后端业务实现
│   │   └── src/main/java/io/dataease/
│   │       ├── chart/           # 图表管理
│   │       ├── dataset/         # 数据集管理
│   │       ├── datasource/      # 数据源管理
│   │       ├── visualization/   # 可视化/仪表板
│   │       ├── template/        # 模板管理
│   │       ├── engine/          # 数据查询引擎
│   │       ├── system/          # 系统管理
│   │       ├── ai/              # AI 功能
│   │       └── ...
│   └── core-frontend/           # 前端应用
│       └── src/
│           ├── api/             # API 调用
│           ├── views/           # 页面视图
│           ├── components/      # 公共组件
│           ├── store/           # 状态管理
│           ├── router/          # 路由配置
│           └── pages/           # 多页面入口
│
├── de-xpack/                     # 企业版扩展(子模块)
├── drivers/                      # JDBC 驱动文件
├── mapFiles/                     # 地图数据文件
├── staticResource/               # 静态资源
├── installer/                    # 安装脚本
└── docs/                         # 文档

```

## 核心模块说明

### 1. SDK 模块 (基础设施层)

SDK 是整个系统的基础,提供通用能力,被 Core 模块依赖。

#### 1.1 sdk/common

**职责**: 提供公共工具、基础组件和横切关注点

**主要内容**:
- `auth/` - JWT 认证、Token 管理
- `cache/` - 缓存配置(EhCache + Redis)
- `constant/` - 常量定义
- `exception/` - 统一异常处理
- `filter/` - 请求过滤器
- `i18n/` - 国际化支持
- `log/` - 日志切面
- `model/` - 公共数据模型
- `result/` - 统一返回结果封装
- `utils/` - 工具类集合
- `websocket/` - WebSocket 支持

**关键依赖**:
- Spring Boot Starter (Web, Cache, Validation, AOP)
- MyBatis Plus
- JWT (java-jwt)
- EasyExcel
- Knife4j (API 文档)
- extensions-view, extensions-datafilling

#### 1.2 sdk/api

**职责**: 定义系统的 API 接口层,提供服务间调用的契约

**子模块**:

**api-base**:
- 基础业务 API 接口定义
- 数据模型和 DTO
- Mapper 接口
- 数据库迁移脚本(Flyway)

**api-permissions**:
- 权限管理 API
- 角色和用户管理接口
- 权限检查注解定义

**api-sync**:
- 数据同步相关 API
- 定时任务接口

**关键依赖**:
- sdk/common
- Flyway (数据库迁移)

#### 1.3 sdk/extensions

**职责**: 定义扩展点接口,支持插件化扩展

**子模块**:

**extensions-datasource**:
- 数据源扩展接口
- 支持添加新的数据源类型(MySQL, Oracle, ClickHouse 等)
- 数据源配置和连接管理

**extensions-view**:
- 视图和图表扩展接口
- 自定义图表类型支持
- 图表渲染引擎接口

**extensions-datafilling**:
- 数据填报扩展接口
- 表单定义和数据提交

#### 1.4 sdk/distributed

**职责**: 分布式/企业版特有组件

**内容** (仅企业版):
- 分布式权限实现
- 集群管理
- 多租户支持

**注意**: 此模块只在使用 `-P distributed` profile 时才会被引入。

### 2. Core 模块 (业务实现层)

Core 模块实现主要的业务逻辑,依赖 SDK 模块提供的基础能力。

详细架构见 `core/PROJECT.md` 和 `core/CLAUDE.md`。

#### 2.1 core-backend

**入口**: `io.dataease.CoreApplication`

**主要业务模块**:

| 模块 | 职责 | 位置 |
|------|------|------|
| chart | 图表管理(创建、编辑、查询) | io.dataease.chart |
| dataset | 数据集管理 | io.dataease.dataset |
| datasource | 数据源连接和管理 | io.dataease.datasource |
| visualization | 仪表板/可视化大屏 | io.dataease.visualization |
| template | 模板市场 | io.dataease.template |
| engine | SQL 查询引擎(Calcite) | io.dataease.engine |
| system | 系统配置、用户管理 | io.dataease.system |
| ai | AI 智能问答(SQLBot) | io.dataease.ai |
| share | 分享和公开链接 | io.dataease.share |
| exportCenter | 导出中心 | io.dataease.exportCenter |
| job | 定时任务 | io.dataease.job |

**依赖的 SDK 模块**:
- api-base
- api-permissions
- api-sync

**构建 Profile**:
- `standalone` (默认): 单机版,包含所有功能,使用 H2
- `desktop`: 桌面版,使用权限替补实现
- `distributed`: 分布式版,引入 distributed 模块

#### 2.2 core-frontend

**入口文件**:
- 主应用: `src/pages/index/main.ts` → `index.html`
- 仪表板: `src/pages/panel/main.ts` → `panel.html`
- 移动端: `src/pages/mobile/main.ts` → `mobile.html`

**目录结构**:
```
core-frontend/src/
├── api/              # API 接口调用
├── views/            # 页面视图
├── components/       # 公共组件
├── custom-component/ # 自定义业务组件
├── store/            # Pinia 状态管理
├── router/           # Vue Router 路由
├── hooks/            # Vue 3 Composition API Hooks
├── utils/            # 工具函数
├── assets/           # 静态资源
├── locales/          # 国际化语言文件
├── directive/        # Vue 指令
└── pages/            # 多页面入口
```

**构建模式**:
- `dev`: 开发模式,热更新
- `base`: 单机版构建
- `distributed`: 分布式版构建
- `lib`: 库模式构建

### 3. 前后端集成机制

**构建流程**:
1. 前端构建: `npm run build` → 生成 `core-frontend/dist/`
2. Maven 构建: maven-antrun-plugin 将 `dist/` 复制到 `core-backend/src/main/resources/static/`
3. Spring Boot 打包: 前端资源打包进 JAR,通过 Spring MVC 静态资源映射提供服务

**关键配置** (core-backend/pom.xml:165-177):
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-antrun-plugin</artifactId>
    <execution>
        <id>copy-front-2-back</id>
        <phase>generate-resources</phase>
        <configuration>
            <target>
                <copy todir="src/main/resources/static">
                    <fileset dir="../core-frontend/dist"/>
                </copy>
            </target>
        </configuration>
    </execution>
</plugin>
```

## 模块依赖关系

```
┌─────────────────────────────────────────────────────────┐
│                    Core Application                      │
│                   (core-backend.jar)                     │
│  ┌──────────────────────────────────────────────────┐   │
│  │          core-backend (业务逻辑)                 │   │
│  │  - chart, dataset, visualization, engine, etc.   │   │
│  │  - Spring Boot 应用                              │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │         core-frontend (前端静态资源)             │   │
│  │  - Vue 3 应用打包后的 dist/ 目录                 │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                        ▲
                        │ depends on
                        │
┌───────────────────────┴─────────────────────────────────┐
│                     SDK Modules                          │
│                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  api-base    │  │ api-perm.    │  │  api-sync    │  │
│  │              │  │              │  │              │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│         ▲                  ▲                  ▲          │
│         └──────────────────┴──────────────────┘          │
│                        │                                 │
│                        ▼                                 │
│  ┌───────────────────────────────────────────────────┐  │
│  │              common (公共组件)                     │  │
│  │  - auth, cache, utils, exception, etc.           │  │
│  └───────────────────────────────────────────────────┘  │
│         ▲                                                │
│         │                                                │
│  ┌──────┴──────────────────────────┐                    │
│  │ extensions-* (扩展点定义)       │                    │
│  │  - datasource, view, datafilling│                    │
│  └─────────────────────────────────┘                    │
│                                                          │
│  ┌──────────────────────────────────────────────────┐   │
│  │  distributed (企业版,可选)                       │   │
│  └──────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────┘
```

**依赖关系说明**:

1. **SDK → Core 单向依赖**
   - Core 依赖 SDK,SDK 不依赖 Core
   - SDK 提供基础设施,Core 实现业务逻辑

2. **API 层次**
   - `api-base`, `api-permissions`, `api-sync` 都依赖 `common`
   - `common` 依赖 `extensions-view`, `extensions-datafilling`

3. **构建顺序**
   ```bash
   1. sdk/extensions/*  (最底层)
   2. sdk/common        (依赖 extensions)
   3. sdk/api/*         (依赖 common)
   4. sdk/distributed   (可选,依赖 api)
   5. core-frontend     (独立构建)
   6. core-backend      (依赖 sdk, 集成 frontend)
   ```

## 数据流和架构模式

### 查询请求流程

```
用户浏览器
    ↓ HTTP Request
┌─────────────────────┐
│  Vue 3 Frontend     │
│  (core-frontend)    │
└─────────────────────┘
    ↓ Axios HTTP
┌─────────────────────────────────────────┐
│  Spring MVC Controller                  │
│  (core-backend/*/controller)            │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│  Service Layer                          │
│  (core-backend/*/service)               │
│  - 业务逻辑处理                          │
│  - 权限检查 (@DePermission)             │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│  Data Query Engine                      │
│  (engine 模块)                           │
│  - Apache Calcite SQL 解析               │
│  - 多数据源适配                          │
│  - SQL 方言转换                          │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│  Data Source                            │
│  - MySQL / Oracle / ClickHouse          │
│  - API / Excel / CSV                    │
└─────────────────────────────────────────┘
```

### 扩展机制

DataEase 使用接口定义扩展点,允许通过实现接口来扩展功能:

```
sdk/extensions/*           →    core-backend/实现类
  (定义接口)                     (实现业务逻辑)

例如:
extensions-datasource     →    datasource/provider/*
  └ DatasourceProvider           └ JdbcProvider
                                 └ ApiProvider
                                 └ ExcelProvider
```

## 数据库设计

### 数据库支持

- **开发/单机版**: H2 内嵌数据库(文件模式)
- **生产环境**: MySQL 8.0+
- **数据仓库**: 支持连接 ClickHouse, Doris, StarRocks 等

### 数据库迁移

使用 Flyway 管理数据库版本:
- 迁移脚本位置: `sdk/api/api-base/src/main/resources/db/migration/`
- 命名规则: `V{版本号}__{描述}.sql`
- 自动执行: 应用启动时自动执行未应用的迁移

### 核心表结构

| 表名 | 用途 |
|------|------|
| core_datasource | 数据源配置 |
| core_dataset_table | 数据集定义 |
| core_chart_view | 图表配置 |
| visualization_info | 仪表板信息 |
| core_sys_user | 用户信息 |
| core_sys_role | 角色信息 |

## 权限模型

### 权限架构

DataEase 使用 RBAC(基于角色的访问控制)模型:

```
用户 (User)
  ↓ 拥有
角色 (Role)
  ↓ 拥有
权限 (Permission)
  ↓ 控制
资源 (Resource: Dashboard, Dataset, Datasource)
```

### 权限实现

1. **社区版/桌面版**: 使用 `core-backend/substitute` 包的简化权限实现
2. **企业版**: 使用 `sdk/distributed` 模块的完整权限实现

### 权限检查

通过 `@DePermission` 注解在方法级别控制权限:

```java
@DePermission(type = "dataset", value = "manage")
public void deleteDataset(Long id) {
    // 业务逻辑
}
```

## 缓存策略

### 缓存层次

1. **一级缓存**: EhCache (本地内存)
2. **二级缓存**: Redis (可选,分布式缓存)

### 缓存使用

- 用户 Token 缓存
- 数据集元数据缓存
- 数据源连接池缓存
- 查询结果缓存(可配置)

配置位置: `common/src/main/resources/ehcache.xml`

## 性能优化

### 后端优化

1. **连接池**: 使用 HikariCP (Spring Boot 默认)
2. **SQL 优化**: Apache Calcite 查询优化
3. **异步处理**: Spring @Async 异步任务
4. **定时任务**: Quartz 调度器

### 前端优化

1. **代码分割**: Vite 自动代码分割
2. **懒加载**: 路由级别懒加载
3. **Tree Shaking**: 去除未使用代码
4. **压缩**: Gzip 压缩 (vite-plugin-compression)

## 部署架构

### 单机版部署

```
┌──────────────────────────┐
│   DataEase 单机版         │
│                          │
│  ┌────────────────────┐  │
│  │ CoreApplication.jar│  │
│  │  - Spring Boot     │  │
│  │  - H2 Database     │  │
│  │  - Static Files    │  │
│  └────────────────────┘  │
└──────────────────────────┘
      Port: 8081
```

### 分布式版部署

```
┌─────────────┐     ┌─────────────┐
│  Nginx LB   │────▶│  DataEase   │
└─────────────┘     │  Instance 1 │
                    └─────────────┘
                          │
                    ┌─────▼─────┐
                    │   MySQL   │
                    └─────┬─────┘
                          │
                    ┌─────▼─────────┐
                    │  DataEase     │
                    │  Instance 2   │
                    └───────────────┘
```

## 监控和日志

### 日志框架

- **SLF4J + Logback** (Spring Boot 默认)
- **日志切面**: 在 `sdk/common/log` 中定义

### 日志级别

- `DEBUG`: 详细的调试信息
- `INFO`: 常规操作日志
- `WARN`: 警告信息
- `ERROR`: 错误信息

### 监控端点

Spring Boot Actuator 提供健康检查和监控端点:
- `/actuator/health` - 健康检查
- `/actuator/metrics` - 指标监控

## 安全

### 认证机制

- **JWT Token**: 无状态认证
- **Token 刷新**: 支持 Token 刷新机制
- **SAML 2.0**: 企业级单点登录 (企业版)

### 数据加密

- **密码加密**: BCrypt
- **敏感数据**: RSA 加密
- **传输加密**: HTTPS (推荐)

### XSS 防护

- 前端使用 `xss` 库进行输入过滤
- 后端参数校验

## 国际化

### 支持语言

- 中文(简体)
- English
- 中文(繁體)
- 日本語
- 其他多种语言

### 实现方式

- **后端**: Spring MessageSource
- **前端**: Vue i18n
- **语言文件**: `core-frontend/src/locales/`

## 开发建议

### 添加新功能

1. 在 `sdk/api` 中定义 API 接口
2. 在 `core-backend` 中实现业务逻辑
3. 在 `core-frontend` 中实现前端界面
4. 添加 Flyway 迁移脚本(如需数据库变更)
5. 更新 API 文档(Knife4j 注解)

### 添加新数据源

1. 在 `sdk/extensions/extensions-datasource` 实现 `DatasourceProvider` 接口
2. 在 `core-backend/datasource/provider` 添加具体实现
3. 注册 Provider 到 Spring 容器

### 添加新图表

1. 在 `sdk/extensions/extensions-view` 定义视图接口
2. 在 `core-frontend/custom-component` 实现图表组件
3. 在图表配置中注册新图表类型

## 相关文档

- [core/PROJECT.md](./core/PROJECT.md) - Core 模块详细架构
- [core/CLAUDE.md](./core/CLAUDE.md) - Core 模块开发规范
- [在线文档](https://dataease.io/docs/) - 用户手册和 API 文档
- [GitHub](https://github.com/dataease/dataease) - 源代码仓库
