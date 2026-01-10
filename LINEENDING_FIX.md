# 换行符统一配置说明

## 问题背景

在跨平台开发中，不同操作系统使用不同的换行符：
- **Windows**: CRLF (`\r\n`)
- **Unix/Linux/Mac**: LF (`\n`)

这会导致以下问题：
1. ESLint/Prettier 报错: `Delete ␍`
2. Git 提交时产生大量无意义的差异
3. 代码审查困难
4. 构建失败

## 解决方案

本项目已统一配置为使用 **LF** 换行符，确保跨平台一致性。

### 1. Git 配置 (.gitattributes)

根目录的 `.gitattributes` 文件定义了换行符规则：

```gitattributes
# 所有文本文件使用 LF
* text=auto eol=lf

# 前端文件强制 LF
*.ts text eol=lf
*.js text eol=lf
*.vue text eol=lf
*.json text eol=lf
...

# Windows 批处理文件使用 CRLF
*.bat text eol=crlf
*.cmd text eol=crlf
```

### 2. EditorConfig 配置

`core/core-frontend/.editorconfig` 定义了编辑器行为：

```editorconfig
[*]
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true
```

### 3. Prettier 配置

`core/core-frontend/prettier.config.js` 强制使用 LF：

```javascript
module.exports = {
  endOfLine: 'lf',  // 强制 LF 换行符
  ...
}
```

## 开发者配置

### Windows 开发者

#### 1. Git 全局配置

```bash
# 设置 Git 自动转换
git config --global core.autocrlf true

# 这将：
# - 检出代码时转换 LF → CRLF
# - 提交代码时转换 CRLF → LF
```

#### 2. 编辑器配置

**Visual Studio Code**:
- 安装 EditorConfig 扩展
- 设置默认行尾: `Files: Eol` → `\n`

**IntelliJ IDEA / WebStorm**:
- 设置 → 编辑器 → 代码样式 → 行分隔符 → `Unix 和 macOS (\n)`

**Sublime Text**:
- View → Line Endings → Unix

### Mac/Linux 开发者

#### Git 配置

```bash
git config --global core.autocrlf input

# 这将：
# - 检出代码时不转换
# - 提交代码时转换 CRLF → LF（如果有的话）
```

编辑器通常默认使用 LF，无需额外配置。

## 修复现有文件

如果遇到换行符问题，执行以下步骤：

### 方法一：自动修复（推荐）

```bash
# 进入前端目录
cd core/core-frontend

# 运行 lint 自动修复
npm run lint -- --fix

# 或运行 Prettier 直接格式化
npx prettier --write "src/**/*.{ts,tsx,js,jsx,vue}"
```

### 方法二：Git 重新规范化

```bash
# 在项目根目录执行
git add --renormalize .

# 这会根据 .gitattributes 重新规范化所有文件
```

### 方法三：手动批量转换（Windows）

使用 PowerShell:
```powershell
# 转换所有 TypeScript 文件
Get-ChildItem -Recurse -Filter *.ts | ForEach-Object {
  $content = Get-Content $_.FullName -Raw
  $content = $content -replace "`r`n", "`n"
  [System.IO.File]::WriteAllText($_.FullName, $content)
}
```

## 常见问题

### 1. ESLint 报错: `Delete ␍`

**原因**: 文件包含 CRLF，但 Prettier 要求 LF

**解决**:
```bash
cd core/core-frontend
npm run lint -- --fix
```

### 2. Git 显示大量文件修改

**原因**: 换行符不一致

**解决**:
```bash
# 重新规范化
git add --renormalize .

# 查看真实修改
git diff --ignore-all-space
```

### 3. IDE 自动转换换行符

**解决**: 配置编辑器使用 LF（见上文"编辑器配置"）

### 4. Pull Request 中显示整个文件被修改

**原因**: 换行符不一致

**解决**:
- 团队统一配置 Git `core.autocrlf`
- 使用 `.gitattributes` 强制规则

## 验证配置

### 检查文件换行符

**Linux/Mac**:
```bash
file src/pages/mobile/main.ts
# 输出应包含: "with LF line terminators"
```

**Windows PowerShell**:
```powershell
(Get-Content src/pages/mobile/main.ts -Raw).Split("`n").Length
(Get-Content src/pages/mobile/main.ts -Raw).Split("`r`n").Length
# 第一个数字 > 第二个数字 = LF
```

### 检查 Git 配置

```bash
git config --global core.autocrlf
# Windows: 应输出 true
# Mac/Linux: 应输出 input
```

## 最佳实践

1. ✅ **团队统一**: 确保所有开发者配置一致
2. ✅ **使用 EditorConfig**: 让编辑器自动遵循规则
3. ✅ **Pre-commit Hook**: 提交前自动检查换行符
4. ✅ **CI/CD 检查**: 在 CI 中验证换行符一致性
5. ✅ **文档清晰**: 在新人入职时明确说明

## Pre-commit Hook (可选)

创建 `.git/hooks/pre-commit`:

```bash
#!/bin/sh
# 检查是否有 CRLF
if git diff --cached --name-only | xargs file | grep CRLF; then
    echo "错误: 检测到 CRLF 换行符"
    echo "请运行: npm run lint -- --fix"
    exit 1
fi
```

## 相关文档

- [.gitattributes](../.gitattributes) - Git 换行符规则
- [.editorconfig](../core/core-frontend/.editorconfig) - 编辑器配置
- [prettier.config.js](../core/core-frontend/prettier.config.js) - Prettier 配置
- [EditorConfig 官方文档](https://editorconfig.org/)
- [Git 换行符处理文档](https://git-scm.com/docs/gitattributes#_end_of_line_conversion)

## 总结

本项目已完成换行符统一配置：
- ✅ Git (.gitattributes)
- ✅ 编辑器 (.editorconfig)
- ✅ Prettier (prettier.config.js)
- ✅ 所有现有文件已修复为 LF

只需要开发者配置正确的 Git `core.autocrlf`，即可无缝协作。

---

**最后更新**: 2026-01-10
**文档维护**: 项目组
