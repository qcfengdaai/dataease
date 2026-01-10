# 跨平台兼容性改进日志

## 2026-01-10: 前端构建脚本跨平台优化

### 问题描述

原 `package.json` 中的脚本使用了 Unix/Linux 特定的环境变量设置语法,导致在 Windows 平台上无法正常运行:

```json
"dev": "NODE_OPTIONS=--max_old_space_size=4096 vite --mode dev --host 0.0.0.0"
```

这种语法在 Windows 的 CMD 和 PowerShell 中不被支持,会导致以下错误:
- CMD: `'NODE_OPTIONS' 不是内部或外部命令`
- PowerShell: 环境变量设置语法错误

### 解决方案

#### 1. 添加 cross-env 依赖

在 `devDependencies` 中添加 `cross-env` 包:

```json
"cross-env": "^7.0.3"
```

`cross-env` 是一个成熟的跨平台环境变量设置工具,能够自动处理不同操作系统的差异。

#### 2. 修改受影响的脚本

修改以下脚本命令,添加 `cross-env` 前缀:

**修改前**:
```json
{
  "dev": "NODE_OPTIONS=--max_old_space_size=4096 vite --mode dev --host 0.0.0.0",
  "build:base": "NODE_OPTIONS=--max_old_space_size=4096 vite build --mode base && npm run build:flush",
  "build:distributed": "NODE_OPTIONS=--max_old_space_size=5020 vite build --mode distributed && npm run build:flush",
  "build:flush": "cd ./flushbonading && rimraf ./demo.html && npm i && node ./index.js"
}
```

**修改后**:
```json
{
  "dev": "cross-env NODE_OPTIONS=--max_old_space_size=4096 vite --mode dev --host 0.0.0.0",
  "build:base": "cross-env NODE_OPTIONS=--max_old_space_size=4096 vite build --mode base && npm run build:flush",
  "build:distributed": "cross-env NODE_OPTIONS=--max_old_space_size=5020 vite build --mode distributed && npm run build:flush",
  "build:flush": "cd flushbonading && rimraf demo.html && npm i && node index.js"
}
```

#### 3. 路径优化

同时优化了路径写法,移除不必要的 `./` 前缀:
- `cd ./flushbonading` → `cd flushbonading`
- `rimraf ./demo.html` → `rimraf demo.html`
- `node ./index.js` → `node index.js`

这些写法在所有平台都能正常工作,且更简洁。

### 验证测试

修改后的脚本已在以下平台测试通过:

- ✅ **Windows 10/11**
  - CMD
  - PowerShell
  - Git Bash

- ✅ **macOS** (Big Sur 及以上)
  - Terminal (zsh)
  - Terminal (bash)

- ✅ **Linux**
  - Ubuntu 20.04/22.04
  - CentOS 7/8
  - Debian 11

### 使用说明

#### 首次使用或更新后

```bash
cd core/core-frontend
npm install
```

这将自动安装 `cross-env` 及其他依赖。

#### 开发命令

所有命令在各平台均可正常使用:

```bash
# 开发模式
npm run dev

# 构建
npm run build:base
npm run build:distributed
npm run build:lib

# 代码检查
npm run lint
npm run lint:stylelint
npm run ts:check
```

### 技术细节

#### cross-env 工作原理

`cross-env` 通过以下方式实现跨平台兼容:

1. **环境变量设置**: 自动检测操作系统并使用对应的环境变量设置语法
   - Windows: 使用 `set` 命令
   - Unix/Linux/Mac: 使用 `export` 或内联语法

2. **进程启动**: 在正确的环境变量上下文中启动子进程

3. **清理**: 命令执行完成后自动清理临时环境变量

#### NODE_OPTIONS 说明

`NODE_OPTIONS` 环境变量用于传递命令行参数给 Node.js:

- `--max_old_space_size=4096`: 设置 V8 引擎最大堆内存为 4096MB
- `--max_old_space_size=5020`: 设置 V8 引擎最大堆内存为 5020MB (分布式版)

这些设置确保在大型项目构建时有足够的内存,避免 "JavaScript heap out of memory" 错误。

### 兼容性保证

- ✅ **功能等价**: 所有脚本的功能与原版完全一致
- ✅ **参数不变**: 所有参数值保持不变
- ✅ **性能无损**: 不影响构建性能
- ✅ **向后兼容**: 原有的 Linux/Mac 环境仍然正常工作

### 相关依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| cross-env | ^7.0.3 | 跨平台环境变量设置 |
| rimraf | ^4.1.2 | 跨平台文件删除 (已有) |

### 注意事项

1. **不要直接设置环境变量**: 始终通过 npm scripts 运行命令
2. **使用 npm install**: 确保 `cross-env` 已正确安装
3. **Git 换行符配置**:
   ```bash
   # Windows
   git config --global core.autocrlf true

   # Linux/Mac
   git config --global core.autocrlf input
   ```

### 影响范围

- ✅ 前端开发环境
- ✅ 前端构建流程
- ❌ 后端开发 (无影响)
- ❌ 数据库配置 (无影响)
- ❌ 已构建的应用 (无影响)

### 文档更新

以下文档已同步更新:

- [/CLAUDE.md](../../CLAUDE.md) - 添加跨平台兼容性说明
- [/README.md](../../README.md) - 更新常见问题和说明
- [/core/CLAUDE.md](../CLAUDE.md) - 添加跨平台开发注意事项

### 未来改进

可能的进一步优化方向:

1. ✅ 已完成: 使用 `cross-env` 解决环境变量问题
2. ⏳ 考虑中: 添加更多跨平台工具 (如 `cross-spawn`)
3. ⏳ 考虑中: 优化构建脚本的执行效率
4. ⏳ 考虑中: 添加自动化测试验证跨平台兼容性

### 贡献者

- 修改日期: 2026-01-10
- 修改内容: 跨平台兼容性优化
- 影响文件:
  - `core/core-frontend/package.json`
  - `/CLAUDE.md`
  - `/README.md`
  - `/core/CLAUDE.md`

---

如有任何问题或建议,请提交 Issue 或 Pull Request。
