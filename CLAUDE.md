# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 重要约定：语言使用规范

**所有与用户的交互、代码注释、提交信息、文档编写必须使用中文。**

- 与用户对话时使用中文
- 代码注释使用中文
- Git commit 信息使用中文
- 文档说明使用中文
- 变量命名使用英文,但注释说明使用中文

## 项目概述

DataEase 是一个开源的 BI(Business Intelligence)工具,采用前后端分离架构:
- 后端: Spring Boot 3.3.0 + Java 21
- 前端: Vue 3 + TypeScript + Vite 4
- 数据库: MySQL (生产) / H2 (开发)
- 数据处理: Apache Calcite

## 架构文档引用

本项目采用分层文档结构:
- `/PROJECT.md` - 根项目架构说明 (系统整体架构、模块依赖关系)
- `/core/PROJECT.md` - Core 模块架构 (业务核心实现)
- `/core/CLAUDE.md` - Core 模块开发规范

开发前请先阅读相关的 PROJECT.md 文件了解架构设计。

## 构建命令

### 后端构建

```bash
# 构建整个项目 (单机版,默认)
mvn clean package

# 构建桌面版
mvn clean package -P desktop

# 构建分布式版 (企业版)
mvn clean package -P distributed

# 只构建 SDK 模块
cd sdk && mvn clean install

# 只构建 Core 模块
cd core && mvn clean package
```

### 前端构建

```bash
cd core/core-frontend

# 安装依赖 (首次运行或依赖更新后)
npm install

# 开发模式运行 (支持热更新)
npm run dev

# 构建单机版
npm run build:base

# 构建分布式版
npm run build:distributed

# 构建为库模式
npm run build:lib

# 代码检查
npm run lint

# 样式检查
npm run lint:stylelint

# TypeScript 类型检查
npm run ts:check
```

**注意**: 前端构建脚本已优化为跨平台兼容,在 Windows、macOS 和 Linux 上均可正常使用。所有脚本使用 `cross-env` 来设置环境变量,确保在不同操作系统上的一致性。

## 启动应用

### 开发环境启动

```bash
# 1. 启动后端 (在 core/core-backend 目录)
# 确保 MySQL 已启动,或使用 H2 数据库
cd core/core-backend
mvn spring-boot:run

# 2. 启动前端 (在 core/core-frontend 目录)
cd core/core-frontend
npm run dev

# 访问: http://localhost:5173
# 后端 API: http://localhost:8081
```

### 生产环境启动

```bash
# 构建完整应用
mvn clean package -P standalone

# 运行打包后的应用
java -jar core/core-backend/target/CoreApplication.jar
```

## 测试命令

```bash
# 运行所有测试
mvn test

# 跳过测试构建
mvn clean package -DskipTests

# 运行特定测试类
mvn test -Dtest=YourTestClass

# 运行特定测试方法
mvn test -Dtest=YourTestClass#testMethod
```

## 项目结构关键点

### SDK 模块 (基础设施层)

SDK 是整个项目的基础设施层,被 Core 模块依赖:

1. **sdk/common** - 公共工具和基础组件
   - 认证(auth)、缓存(cache)、异常处理(exception)
   - 国际化(i18n)、日志(log)、工具类(utils)
   - WebSocket 支持、Feign 客户端配置

2. **sdk/api** - API 接口定义层
   - `api-base`: 基础 API 和数据模型
   - `api-permissions`: 权限相关 API
   - `api-sync`: 数据同步相关 API

3. **sdk/extensions** - 扩展点定义
   - `extensions-datasource`: 数据源扩展接口
   - `extensions-view`: 视图/图表扩展接口
   - `extensions-datafilling`: 数据填报扩展接口

4. **sdk/distributed** - 分布式组件 (企业版专用)

### Core 模块 (业务实现层)

Core 模块包含主要的业务逻辑实现,详细说明见 `core/PROJECT.md` 和 `core/CLAUDE.md`。

**核心特点:**
- `core-frontend`: Vue 3 前端应用
- `core-backend`: Spring Boot 后端应用
- 后端在构建时会将前端打包文件复制到 `static` 目录实现前后端集成

## Maven Profile 说明

项目支持三种构建 Profile:

1. **standalone** (默认)
   - 单机版,包含完整功能
   - 使用 H2 数据库
   - 包含 Selenium、邮件、PDF 等功能

2. **desktop** (桌面版/社区版)
   - 轻量级版本
   - 使用 H2 数据库
   - 使用权限替补实现 (substitute 包)

3. **distributed** (分布式/企业版)
   - 引入 distributed 模块
   - 不使用 H2 数据库
   - 排除 substitute 包
   - 需要外部 MySQL 数据库

## 开发规范

### 后端开发规范

1. **包结构约定**
   - Controller: `{module}/controller`
   - Service: `{module}/service` (接口) 和 `{module}/service/impl` (实现)
   - Mapper: `{module}/mapper`
   - Entity: `{module}/domain` 或 `{module}/model`

2. **API 规范**
   - 使用 RESTful 风格
   - 统一返回值: 使用 `io.dataease.result.ResultHolder`
   - 使用 Knife4j (Swagger) 进行 API 文档标注
   - API 路径前缀: `/api/`

3. **数据库规范**
   - 使用 MyBatis Plus 作为 ORM
   - 数据库迁移使用 Flyway
   - 实体类使用 Lombok 注解
   - SQL 脚本放在 `resources/db/migration`

4. **权限控制**
   - 使用 `@DePermission` 注解控制接口权限
   - 权限检查在 `api-permissions` 模块定义
   - 社区版使用 substitute 包的替补实现

### 前端开发规范

1. **组件规范**
   - 组件文件使用 PascalCase 命名
   - 组件放在 `src/components` 或 `src/views`
   - 自定义组件放在 `src/custom-component`

2. **API 调用**
   - API 接口定义在 `src/api` 目录
   - 使用 Axios 进行请求
   - 统一错误处理在 axios 拦截器中

3. **状态管理**
   - 使用 Pinia 进行状态管理
   - Store 定义在 `src/store` 目录

4. **路由管理**
   - 路由定义在 `src/router`
   - 支持多页面应用 (index, panel, mobile)

5. **国际化**
   - 使用 vue-i18n
   - 语言文件在 `src/locales`
   - 支持中文、英文等多语言

## 代码生成器

项目包含 MyBatis Plus 代码生成器:

```bash
# 运行生成器 (在 core-backend 模块)
# 修改 io.dataease.MybatisPlusGenerator 配置后运行
java io.dataease.MybatisPlusGenerator
```

## 常见问题

### 1. 前端构建失败

**问题**: 依赖安装失败或构建报错

**解决方案**:
```bash
# 清除缓存并重新安装
rm -rf node_modules package-lock.json
npm install

# Windows 下使用
rmdir /s /q node_modules
del package-lock.json
npm install

# 检查 Node 版本 (推荐 Node 16+)
node -v
```

**问题**: `NODE_OPTIONS` 环境变量设置失败 (Windows)

**解决方案**: 项目已使用 `cross-env` 解决跨平台兼容性问题,确保依赖已安装:
```bash
npm install
```

### 2. 后端启动失败
- 检查 Java 版本 (必须 Java 21)
- 检查数据库连接配置
- 查看 `application.yml` 中的 profile 配置

### 3. Profile 切换问题
- Maven Profile 和 Spring Profile 要匹配
- 检查 `pom.xml` 中的 `<profiles.active>` 属性
- 确认对应的 `application-{profile}.yml` 文件存在

### 4. 模块依赖问题
- SDK 模块必须先构建: `cd sdk && mvn clean install`
- Core 模块依赖 SDK 模块,构建顺序: SDK → Core
- 使用 `mvn clean install` 而不是 `package` 来安装到本地仓库

### 5. 跨平台开发注意事项

**Windows 开发者**:
- 所有前端脚本已使用 `cross-env` 确保兼容性
- 路径分隔符会自动处理,无需手动调整
- PowerShell 和 CMD 均可正常使用 npm scripts

**Linux/Mac 开发者**:
- 使用标准的 bash 命令即可
- 确保脚本具有执行权限 (如需要)

**通用建议**:
- 统一使用 npm scripts 而不是直接运行底层命令
- Git 配置正确的换行符设置:
  ```bash
  git config --global core.autocrlf input  # Linux/Mac
  git config --global core.autocrlf true   # Windows
  ```

## 数据处理引擎

DataEase 使用 Apache Calcite 进行 SQL 解析和优化:
- 支持多种数据源的统一查询
- SQL 方言转换
- 查询优化

相关代码在 `core-backend/src/main/java/io/dataease/engine`

## 图表库

前端使用多个图表库:
- **AntV G2Plot**: 基础图表
- **AntV L7**: 地图可视化
- **AntV S2**: 表格组件
- **ECharts**: 复杂图表

## 外部依赖

- **Apache SeaTunnel**: 数据同步
- **MinIO**: 对象存储 (可选)
- **Redis**: 缓存 (可选)

## License

本项目采用 GPLv3 许可证,二次开发时请注意许可证要求。
