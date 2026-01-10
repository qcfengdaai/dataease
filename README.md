<p align="center"><a href="https://dataease.cn"><img src="https://dataease.oss-cn-hangzhou.aliyuncs.com/img/dataease-logo.png" alt="DataEase" width="300" /></a></p>
<h3 align="center">人人可用的开源 BI 工具</h3>
<p align="center">
  <a href="https://www.gnu.org/licenses/gpl-3.0.html"><img src="https://img.shields.io/github/license/dataease/dataease?color=%231890FF" alt="License: GPL v3"></a>
  <a href="https://app.codacy.com/gh/dataease/dataease?utm_source=github.com&utm_medium=referral&utm_content=dataease/dataease&utm_campaign=Badge_Grade_Dashboard"><img src="https://app.codacy.com/project/badge/Grade/da67574fd82b473992781d1386b937ef" alt="Codacy"></a>
  <a href="https://github.com/dataease/dataease"><img src="https://img.shields.io/github/stars/dataease/dataease?color=%231890FF&style=flat-square" alt="GitHub Stars"></a>
  <a href="https://github.com/dataease/dataease/releases"><img src="https://img.shields.io/github/v/release/dataease/dataease" alt="GitHub release"></a>
  <a href="https://gitee.com/fit2cloud-feizhiyun/DataEase"><img src="https://gitee.com/fit2cloud-feizhiyun/DataEase/badge/star.svg?theme=gvp" alt="Gitee Stars"></a>
  <a href="https://gitcode.com/feizhiyun/DataEase"><img src="https://gitcode.com/feizhiyun/DataEase/star/badge.svg" alt="GitCode Stars"></a>
</p>
<p align="center">
  <a href="/README.md"><img alt="中文(简体)" src="https://img.shields.io/badge/中文(简体)-d9d9d9"></a>
  <a href="/docs/README.en.md"><img alt="English" src="https://img.shields.io/badge/English-d9d9d9"></a>
  <a href="/docs/README.zh-Hant.md"><img alt="中文(繁體)" src="https://img.shields.io/badge/中文(繁體)-d9d9d9"></a>
  <a href="/docs/README.ja.md"><img alt="日本語" src="https://img.shields.io/badge/日本語-d9d9d9"></a>
  <a href="/docs/README.pt-br.md"><img alt="Português (Brasil)" src="https://img.shields.io/badge/Português (Brasil)-d9d9d9"></a>
  <a href="/docs/README.ar.md"><img alt="العربية" src="https://img.shields.io/badge/العربية-d9d9d9"></a>
  <a href="/docs/README.de.md"><img alt="Deutsch" src="https://img.shields.io/badge/Deutsch-d9d9d9"></a>
  <a href="/docs/README.es.md"><img alt="Español" src="https://img.shields.io/badge/Español-d9d9d9"></a>
  <a href="/docs/README.fr.md"><img alt="français" src="https://img.shields.io/badge/français-d9d9d9"></a>
  <a href="/docs/README.ko.md"><img alt="한국어" src="https://img.shields.io/badge/한국어-d9d9d9"></a>
  <a href="/docs/README.id.md"><img alt="Bahasa Indonesia" src="https://img.shields.io/badge/Bahasa Indonesia-d9d9d9"></a>
  <a href="/docs/README.tr.md"><img alt="Türkçe" src="https://img.shields.io/badge/Türkçe-d9d9d9"></a>
</p>
<p align="center">
  <a href="https://trendshift.io/repositories/1563" target="_blank"><img src="https://trendshift.io/api/badge/repositories/1563" alt="dataease%2Fdataease | Trendshift" style="width: 250px; height: 55px;" width="250" height="55"/></a>
</p>

------------------------------

## 什么是 DataEase？

DataEase 是开源的 BI 工具，帮助用户快速分析数据并洞察业务趋势，从而实现业务的改进与优化。DataEase 支持丰富的数据源连接，能够通过拖拉拽方式快速制作图表，并可以方便的与他人分享。

**DataEase 的优势：**

-   开源开放：零门槛，线上快速获取和安装，按月迭代；
-   简单易用：极易上手，通过鼠标点击和拖拽即可完成分析；
-   全场景支持：多平台安装和多样化嵌入支持；
-   安全分享：支持多种数据分享方式，确保数据安全；
-   AI 加持：无缝集成 [SQLBot](https://github.com/dataease/SQLBot) 实现智能问数。

**DataEase 支持的数据源：**

-   OLTP 数据库： MySQL、Oracle、SQL Server、PostgreSQL、MariaDB、Db2、TiDB、MongoDB-BI 等；
-   OLAP 数据库： ClickHouse、Apache Doris、Apache Impala、StarRocks 等；
-   数据仓库/数据湖： Amazon RedShift 等；
-   数据文件： Excel、CSV 等；
-   API 数据源。

如果您需要向团队介绍 DataEase，可以使用这个 [官方 PPT 材料](https://fit2cloud.com/dataease/download/introduce-dataease_202511.pdf)，或者购买由华东师大和 DataEase 联合出品的图书： [《数据可视化分析与实践》](https://item.jd.com/10207058297099.html)。



## 快速开始

**桌面版：**

你可以在 PC 上安装 DataEasae 桌面版，下载地址为：https://dataease.cn/desktop/index.html

**服务器版：**

```
# 准备一台 2 核 4G 以上的 Linux 服务器，并以 root 用户运行以下一键安装脚本：

curl -sSL https://dataease.oss-cn-hangzhou.aliyuncs.com/quick_start_v2.sh | bash

# 用户名: admin
# 密码: DataEase@123456
```

你也可以通过 [1Panel 应用商店](https://dataease.io/docs/v2/installation/1panel_installation/) 快速部署 DataEase。如果是用于生产环境，推荐使用 [离线安装包方式](https://dataease.io/docs/v2/installation/offline_INSTL_and_UPG/) 进行安装部署。

如你有更多问题，可以查看在线文档，或者通过论坛和交流群与我们交流。

-   [视频介绍](https://www.bilibili.com/video/BV1Y8dAYLErb/)
-   [在线文档](https://dataease.io/docs/)
-   [社区论坛](https://bbs.fit2cloud.com/c/de/6)
-   微信交流群

  <img width="150" height="150" alt="image" src="https://github.com/user-attachments/assets/a8e4cd48-ed0f-4754-ba34-d047063b1633" />


## UI 展示

<table style="border-collapse: collapse; border: 1px solid black;">
  <tr>
    <td style="padding: 5px;background-color:#fff;"><img src= "https://github.com/dataease/dataease/assets/41712985/8dbed4e1-39f0-4392-aa8c-d1fd83ba42eb" alt="DataEase 工作台"   /></td>
    <td style="padding: 5px;background-color:#fff;"><img src= "https://github.com/dataease/dataease/assets/41712985/7c54cb07-51ef-4bb6-a931-8a95c64c7e11" alt="DataEase 仪表板"   /></td>
  </tr>

  <tr>
    <td style="padding: 5px;background-color:#fff;"><img src= "https://github.com/dataease/dataease/assets/41712985/ffa79361-a7b3-4486-b14a-f3fd3a28f01a" alt="DataEase 数据源"   /></td>
    <td style="padding: 5px;background-color:#fff;"><img src= "https://github.com/dataease/dataease/assets/41712985/bb28f4e4-636e-4ab0-85c5-1dfbd7a5397e" alt="DataEase 模板中心"   /></td>
  </tr>
</table>

## 技术栈

-   前端：[Vue.js](https://vuejs.org/)、[Element](https://element.eleme.cn/)
-   图库：[AntV](https://antv.vision/zh)
-   后端：[Spring Boot](https://spring.io/projects/spring-boot)
-   数据库：[MySQL](https://www.mysql.com/)
-   数据处理：[Apache Calcite](https://github.com/apache/calcite/)、[Apache SeaTunnel](https://github.com/apache/seatunnel)
-   基础设施：[Docker](https://www.docker.com/)

## 开发者文档

### 架构文档

- **[CLAUDE.md](./CLAUDE.md)** - 开发规范和构建指南(用于 Claude Code 辅助开发)
- **[PROJECT.md](./PROJECT.md)** - 系统架构设计文档
- **[core/CLAUDE.md](./core/CLAUDE.md)** - Core 模块开发规范
- **[core/PROJECT.md](./core/PROJECT.md)** - Core 模块架构文档

### 项目结构

DataEase 采用模块化架构,主要分为两大部分:

```
dataease/
├── sdk/              # 基础设施层(SDK)
│   ├── common/       # 公共组件和工具
│   ├── api/          # API 接口定义层
│   ├── extensions/   # 扩展点定义
│   └── distributed/  # 分布式组件(企业版)
│
└── core/             # 业务实现层
    ├── core-backend/   # Spring Boot 后端
    └── core-frontend/  # Vue 3 前端
```

### 模块依赖关系

```
core-backend (业务逻辑)
    ↓ 依赖
sdk/api/* (API 接口)
    ↓ 依赖
sdk/common (公共组件)
    ↓ 依赖
sdk/extensions/* (扩展点)
```

**依赖说明**:
- **sdk 模块**: 提供基础设施、公共工具、API 定义和扩展点
- **core 模块**: 实现具体的业务逻辑,依赖 sdk 模块
- **构建顺序**: 必须先构建 sdk,再构建 core

### 本地开发环境搭建

#### 环境要求

- **Java 21+** (必须)
- **Node.js 16+** (推荐 18+)
- **Maven 3.6+**
- **MySQL 8.0+** (可选,开发环境可使用 H2)

#### 快速启动

**方式一: 分别启动前后端(推荐用于开发)**

```bash
# 1. 构建 SDK 模块
cd sdk
mvn clean install

# 2. 启动后端 (使用 H2 数据库)
cd ../core/core-backend
mvn spring-boot:run

# 3. 启动前端 (新终端)
cd ../core-frontend
npm install
npm run dev

# 访问: http://localhost:5173
# 默认账号: admin / DataEase@123456
```

**方式二: 完整打包运行**

```bash
# 1. 构建前端
cd core/core-frontend
npm install
npm run build:base

# 2. 构建完整应用
cd ../..
mvn clean package

# 3. 运行
java -jar core/core-backend/target/CoreApplication.jar

# 访问: http://localhost:8081
```

#### 构建不同版本

DataEase 支持三种构建版本:

**单机版 (standalone, 默认)**
```bash
mvn clean package
# 或
mvn clean package -P standalone
```
- 包含完整功能
- 使用 H2 内嵌数据库
- 包含 PDF 导出、邮件发送等功能

**桌面版 (desktop)**
```bash
mvn clean package -P desktop
```
- 轻量级版本
- 使用简化权限实现
- 适合个人使用

**分布式版 (distributed, 企业版)**
```bash
mvn clean package -P distributed
```
- 支持分布式部署
- 需要外部 MySQL 数据库
- 完整权限管理和多租户支持

### 前端开发

#### 开发模式

```bash
cd core/core-frontend

# 开发模式(带热更新)
npm run dev

# TypeScript 类型检查
npm run ts:check

# 代码检查和格式化
npm run lint
npm run lint:stylelint
```

#### 构建模式

```bash
# 单机版构建
npm run build:base

# 分布式版构建
npm run build:distributed

# 库模式构建
npm run build:lib
```

### 后端开发

#### 常用命令

```bash
# 编译
mvn clean compile

# 打包(跳过测试)
mvn clean package -DskipTests

# 运行测试
mvn test

# 只构建 SDK
cd sdk && mvn clean install

# 只构建 Core
cd core && mvn clean package
```

#### 开发配置

后端配置文件位于 `core/core-backend/src/main/resources/`:

- `application.yml` - 主配置
- `application-standalone.yml` - 单机版配置
- `application-desktop.yml` - 桌面版配置
- `application-distributed.yml` - 分布式版配置

修改配置后重启应用生效。

### 数据库

#### 开发环境(H2)

单机版和桌面版默认使用 H2 内嵌数据库,无需额外配置。

数据文件位置: `~/.dataease/data/dataease.mv.db`

#### 生产环境(MySQL)

创建数据库:
```sql
CREATE DATABASE dataease DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

修改配置 `application-distributed.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dataease?useUnicode=true&characterEncoding=UTF-8
    username: root
    password: your_password
```

数据库表结构由 Flyway 自动创建和管理。

### 数据库迁移

DataEase 使用 Flyway 管理数据库版本:

- 迁移脚本位置: `sdk/api/api-base/src/main/resources/db/migration/`
- 命名规则: `V{版本号}__{描述}.sql`
- 执行时机: 应用启动时自动执行未应用的迁移

### 常见问题

#### 1. 前端启动失败

**问题**: `npm run dev` 报错

**解决方案**:
```bash
# 清除缓存并重新安装
rm -rf node_modules package-lock.json
npm install

# 确保 Node.js 版本正确
node -v  # 应该是 16.x 或更高
```

#### 2. 后端启动失败

**问题**: `端口 8081 已被占用`

**解决方案**:
```bash
# 修改端口
java -jar CoreApplication.jar --server.port=8082
```

**问题**: `找不到 SDK 模块`

**解决方案**:
```bash
# 先安装 SDK 到本地 Maven 仓库
cd sdk
mvn clean install
```

#### 3. 构建失败

**问题**: `前端资源未找到`

**解决方案**:
```bash
# 确保先构建前端
cd core/core-frontend
npm run build:base

# 再构建后端
cd ../core-backend
mvn clean package
```

### API 文档

启动应用后,访问 Knife4j API 文档:

```
http://localhost:8081/doc.html
```

### 调试技巧

**后端调试**: 使用 IDE (IntelliJ IDEA / Eclipse) 的调试模式运行 `CoreApplication`

**前端调试**:
- Chrome DevTools
- Vue DevTools 浏览器扩展

**日志查看**:
```bash
# 后端日志
tail -f logs/dataease.log

# 修改日志级别(application.yml)
logging:
  level:
    io.dataease: DEBUG
```

### 贡献代码

欢迎提交 Pull Request! 请确保:

1. 代码符合项目规范(参考 [CLAUDE.md](./CLAUDE.md))
2. 添加必要的注释(使用中文)
3. 通过所有测试
4. 提交信息清晰明了

详见 [CONTRIBUTING.md](./CONTRIBUTING.md)

### 技术交流

## 飞致云的其他明星项目

- [1Panel](https://github.com/1panel-dev/1panel/) - 现代化、开源的 Linux 服务器运维管理面板
- [MaxKB](https://github.com/1panel-dev/MaxKB/) - 基于 LLM 大语言模型的开源知识库问答系统
- [JumpServer](https://github.com/jumpserver/jumpserver/) - 广受欢迎的开源堡垒机
- [Cordys CRM](https://github.com/1Panel-dev/CordysCRM) - 新一代的开源 AI CRM 系统
- [Halo](https://github.com/halo-dev/halo/) - 强大易用的开源建站工具
- [MeterSphere](https://github.com/metersphere/metersphere/) - 新一代的开源持续测试工具

## License

Copyright (c) 2014-2026 [FIT2CLOUD 飞致云](https://fit2cloud.com/), All rights reserved.

Licensed under The GNU General Public License version 3 (GPLv3)  (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

<https://www.gnu.org/licenses/gpl-3.0.html>

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
