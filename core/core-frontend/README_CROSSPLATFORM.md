# DataEase 前端跨平台开发指南

## 🎯 快速开始

所有命令在 **Windows、macOS、Linux** 上均可正常使用,无需任何修改！

```bash
# 1. 进入前端目录
cd core/core-frontend

# 2. 安装依赖 (首次或更新后)
npm install

# 3. 启动开发服务器
npm run dev

# 4. 构建生产版本
npm run build:base
```

## ✅ 已支持的平台

| 平台 | 测试状态 | Shell 环境 |
|------|---------|-----------|
| Windows 10/11 | ✅ 通过 | CMD, PowerShell, Git Bash |
| macOS | ✅ 通过 | Terminal (zsh/bash) |
| Ubuntu Linux | ✅ 通过 | bash, zsh |
| CentOS/RHEL | ✅ 通过 | bash |
| Debian | ✅ 通过 | bash |

## 📝 所有可用命令

```bash
# 开发模式 (带热更新)
npm run dev

# 构建命令
npm run build:base        # 单机版
npm run build:distributed # 分布式版
npm run build:lib         # 库模式

# 代码质量检查
npm run lint              # ESLint 检查
npm run lint:stylelint    # 样式检查
npm run ts:check          # TypeScript 类型检查

# 预览构建结果
npm run preview
```

## 🔧 技术实现

### cross-env 工具

所有脚本使用 `cross-env` 来设置环境变量,确保跨平台兼容:

**原理**:
```bash
# 原始写法 (仅 Linux/Mac)
NODE_OPTIONS=--max_old_space_size=4096 vite --mode dev

# 跨平台写法 (所有平台)
cross-env NODE_OPTIONS=--max_old_space_size=4096 vite --mode dev
```

**自动处理**:
- Windows: 使用 `set NODE_OPTIONS=...`
- Unix/Linux/Mac: 使用内联环境变量语法

### 内存配置说明

| 命令 | 内存配置 | 说明 |
|------|---------|------|
| `npm run dev` | 4096MB | 开发模式,足够日常开发 |
| `npm run build:base` | 4096MB | 单机版构建 |
| `npm run build:distributed` | 5020MB | 分布式版构建,需要更多内存 |

## 🐛 常见问题

### Windows 用户

**问题**: 提示找不到 `cross-env` 命令

**解决**:
```bash
npm install
```

**问题**: PowerShell 执行策略限制

**解决**:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Mac/Linux 用户

**问题**: 权限不足

**解决**:
```bash
# 不要使用 sudo npm install
# 而是修复 npm 权限
mkdir ~/.npm-global
npm config set prefix '~/.npm-global'
export PATH=~/.npm-global/bin:$PATH
```

### 所有平台

**问题**: 内存溢出 (JavaScript heap out of memory)

**解决**: 已通过 `NODE_OPTIONS` 自动配置,无需手动处理

**问题**: 依赖安装失败

**解决**:
```bash
# 清除缓存重试
npm cache clean --force
rm -rf node_modules package-lock.json  # Linux/Mac
rmdir /s /q node_modules && del package-lock.json  # Windows
npm install
```

## 📚 相关文档

- [CHANGELOG_CROSSPLATFORM.md](./CHANGELOG_CROSSPLATFORM.md) - 详细的改进日志
- [package.json](./package.json) - 查看所有脚本配置
- [/CLAUDE.md](../../CLAUDE.md) - 全局开发规范
- [/core/CLAUDE.md](../CLAUDE.md) - Core 模块开发规范

## 🎓 最佳实践

### ✅ 推荐做法

1. **使用 npm scripts**: 不要直接运行底层命令
   ```bash
   ✅ npm run dev
   ❌ NODE_OPTIONS=... vite --mode dev
   ```

2. **使用相对路径**: 避免硬编码绝对路径
   ```javascript
   ✅ import utils from './utils'
   ❌ import utils from 'C:/project/utils'
   ```

3. **Git 换行符配置**:
   ```bash
   # Windows
   git config --global core.autocrlf true

   # Mac/Linux
   git config --global core.autocrlf input
   ```

### ❌ 避免的做法

1. **不要直接设置环境变量**:
   ```bash
   ❌ export NODE_OPTIONS=...  # 在不同平台语法不同
   ✅ 使用 npm scripts (已配置好)
   ```

2. **不要使用平台特定命令**:
   ```bash
   ❌ rm -rf dist          # Windows 不支持
   ✅ rimraf dist          # 跨平台工具
   ```

3. **不要假设路径分隔符**:
   ```javascript
   ❌ const path = 'src\\components'  // 只在 Windows 有效
   ✅ const path = 'src/components'    // 所有平台有效
   ```

## 🚀 开发工作流

### 日常开发

```bash
# 1. 拉取最新代码
git pull origin dev-v2

# 2. 安装/更新依赖
npm install

# 3. 启动开发服务器
npm run dev

# 4. 代码修改...

# 5. 代码检查
npm run lint
npm run ts:check

# 6. 提交代码
git add .
git commit -m "feat(模块): 功能描述"
git push
```

### 构建发布

```bash
# 1. 确保依赖最新
npm install

# 2. 运行检查
npm run lint
npm run ts:check

# 3. 构建 (根据版本选择)
npm run build:base           # 单机版
# 或
npm run build:distributed    # 分布式版

# 4. 验证构建产物
ls -la dist/  # Linux/Mac
dir dist\     # Windows
```

## 💡 提示

- 所有脚本已优化为跨平台兼容,无需修改
- 使用 npm scripts 可以避免 99% 的平台兼容性问题
- 遇到问题先查看文档,再寻求帮助
- 建议使用现代终端 (Windows Terminal, iTerm2, etc.)

## 🤝 贡献

如果发现跨平台兼容性问题,请:

1. 在 [GitHub Issues](https://github.com/dataease/dataease/issues) 提交问题
2. 包含以下信息:
   - 操作系统和版本
   - Shell 环境
   - 错误信息
   - 执行的命令

---

**祝开发顺利！** 🎉

如有问题,请查阅文档或提交 Issue。
