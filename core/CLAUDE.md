# Core 模块开发规范

本文档定义 DataEase Core 模块(core-backend 和 core-frontend)的开发规范和最佳实践。

**重要**: 所有开发工作必须遵循根目录 `/CLAUDE.md` 中定义的全局规范,特别是**所有交互和注释必须使用中文**。

## Core 模块定位

Core 模块是 DataEase 的业务实现层,包含:
- **core-backend**: Spring Boot 后端应用,实现所有业务逻辑
- **core-frontend**: Vue 3 前端应用,提供用户界面

## 后端开发规范 (core-backend)

### 1. 项目结构规范

```
io.dataease/
├── {module}/              # 业务模块(如 chart, dataset, datasource)
│   ├── controller/        # REST 控制器
│   ├── service/           # 服务接口
│   │   └── impl/         # 服务实现
│   ├── mapper/            # MyBatis Mapper 接口
│   ├── domain/            # 实体类 / PO
│   ├── dto/               # 数据传输对象
│   └── manage/            # 复杂业务协调层(可选)
├── config/                # 配置类
├── interceptor/           # 拦截器
├── listener/              # 监听器
└── commons/               # 公共工具类
```

### 2. Controller 层规范

**命名**: `{功能}Controller.java`

**职责**:
- 接收 HTTP 请求
- 参数校验(使用 `@Valid`)
- 调用 Service 层
- 返回统一格式响应

**示例**:
```java
@RestController
@RequestMapping("/api/chart")
@Tag(name = "图表管理", description = "图表相关接口")
public class ChartController {

    @Resource
    private ChartService chartService;

    @PostMapping("/create")
    @Operation(summary = "创建图表")
    @DePermission(type = "chart", value = "manage")
    public ResultHolder<ChartDTO> create(@Valid @RequestBody ChartRequest request) {
        ChartDTO chart = chartService.create(request);
        return ResultHolder.success(chart);
    }
}
```

**规范要点**:
- 使用 `@RestController` 和 `@RequestMapping`
- 使用 Knife4j 注解(`@Tag`, `@Operation`)编写 API 文档
- 使用 `@DePermission` 控制权限
- 统一返回 `ResultHolder<T>`
- 所有注释使用中文

### 3. Service 层规范

**命名**:
- 接口: `{功能}Service.java`
- 实现: `{功能}ServiceImpl.java`

**职责**:
- 实现业务逻辑
- 事务管理
- 调用 Mapper 或其他 Service

**示例**:
```java
public interface ChartService {
    /**
     * 创建图表
     * @param request 图表请求
     * @return 图表信息
     */
    ChartDTO create(ChartRequest request);
}

@Service
public class ChartServiceImpl implements ChartService {

    @Resource
    private ChartMapper chartMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChartDTO create(ChartRequest request) {
        // 业务逻辑实现
        ChartView chart = new ChartView();
        // ... 设置属性
        chartMapper.insert(chart);
        return convertToDTO(chart);
    }
}
```

**规范要点**:
- Service 定义接口,Impl 实现
- 使用 `@Transactional` 进行事务管理
- 复杂业务逻辑在 Service 层处理
- 使用 `@Resource` 注入依赖

### 4. Mapper 层规范

**命名**: `{实体}Mapper.java`

**职责**:
- 数据库访问
- SQL 映射

**示例**:
```java
@Mapper
public interface ChartMapper extends BaseMapper<ChartView> {
    /**
     * 根据数据集ID查询图表列表
     * @param datasetId 数据集ID
     * @return 图表列表
     */
    List<ChartView> selectByDatasetId(@Param("datasetId") Long datasetId);
}
```

**规范要点**:
- 继承 MyBatis Plus 的 `BaseMapper<T>`
- 简单 CRUD 使用 MyBatis Plus 提供的方法
- 复杂查询在 Mapper XML 中定义
- Mapper XML 位置: `resources/mapper/{module}/{Entity}Mapper.xml`

### 5. 实体类规范

**命名**: `{实体名}.java`

**位置**: `domain/` 或 `model/` 目录

**示例**:
```java
@Data
@TableName("core_chart_view")
public class ChartView implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 图表名称
     */
    private String name;

    /**
     * 图表类型
     */
    private String type;

    /**
     * 数据集ID
     */
    private Long datasetId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;
}
```

**规范要点**:
- 使用 Lombok `@Data` 注解
- 使用 MyBatis Plus 注解(`@TableName`, `@TableId`, `@TableField`)
- 实现 `Serializable`
- 添加中文注释说明每个字段的含义
- 时间字段使用 `Long` 类型(时间戳)

### 6. DTO 规范

**命名**: `{功能}DTO.java` / `{功能}Request.java` / `{功能}Response.java`

**职责**:
- 数据传输对象
- 与前端交互的数据格式

**示例**:
```java
@Data
@Schema(description = "图表请求")
public class ChartRequest {

    @Schema(description = "图表名称")
    @NotBlank(message = "图表名称不能为空")
    private String name;

    @Schema(description = "图表类型")
    private String type;

    @Schema(description = "数据集ID")
    @NotNull(message = "数据集ID不能为空")
    private Long datasetId;
}
```

**规范要点**:
- 使用 Lombok `@Data`
- 使用 Swagger 注解 `@Schema` 描述字段
- 使用 JSR-303 校验注解(`@NotNull`, `@NotBlank`, 等)
- 请求和响应分开定义

### 7. 异常处理规范

**使用统一异常类**:
```java
// 抛出业务异常
throw new DEException("图表不存在");

// 带参数的异常
throw new DEException("数据集[{}]不存在", datasetId);
```

**全局异常处理**:
- 定义在 `sdk/common` 模块
- 所有异常会被转换为统一的 JSON 响应

### 8. 日志规范

**使用 SLF4J**:
```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChartServiceImpl implements ChartService {

    public void someMethod() {
        log.info("开始处理图表创建请求");
        log.debug("图表参数: {}", request);

        try {
            // 业务逻辑
        } catch (Exception e) {
            log.error("图表创建失败", e);
            throw new DEException("图表创建失败: " + e.getMessage());
        }
    }
}
```

**日志级别**:
- `DEBUG`: 调试信息(开发环境)
- `INFO`: 重要操作日志
- `WARN`: 警告信息
- `ERROR`: 错误和异常

### 9. 权限控制规范

**使用 @DePermission 注解**:
```java
@DePermission(type = "dataset", value = "manage")
public void deleteDataset(Long id) {
    // 只有拥有 dataset:manage 权限的用户才能执行
}
```

**权限类型**:
- `chart`: 图表权限
- `dataset`: 数据集权限
- `datasource`: 数据源权限
- `visualization`: 仪表板权限
- `system`: 系统管理权限

### 10. 数据库迁移规范

**Flyway 脚本命名**: `V{版本号}__{描述}.sql`

**位置**: `sdk/api/api-base/src/main/resources/db/migration/`

**示例**: `V2_10__add_chart_new_field.sql`

```sql
-- 添加图表新字段
ALTER TABLE core_chart_view ADD COLUMN new_field VARCHAR(255) COMMENT '新字段说明';
```

**注意事项**:
- 版本号递增,不可重复
- 脚本一旦执行不可修改
- 必须添加注释说明变更内容
- 兼容 MySQL 和 H2 语法

## 前端开发规范 (core-frontend)

### 1. 项目结构规范

```
core-frontend/src/
├── api/                  # API 接口调用
│   ├── chart.ts         # 图表相关 API
│   ├── dataset.ts       # 数据集相关 API
│   └── ...
├── views/               # 页面视图
│   ├── chart/          # 图表管理页面
│   │   ├── index.vue   # 列表页
│   │   └── edit.vue    # 编辑页
│   └── ...
├── components/          # 公共组件
│   ├── layout/         # 布局组件
│   ├── common/         # 通用组件
│   └── ...
├── custom-component/    # 自定义业务组件
│   ├── common/         # 通用自定义组件
│   └── v-chart/        # 图表组件
├── store/               # Pinia 状态管理
│   ├── modules/        # 模块化 store
│   └── index.ts        # Store 入口
├── router/              # 路由配置
│   ├── index.ts        # 主路由
│   └── modules/        # 模块路由
├── hooks/               # Composition API Hooks
├── utils/               # 工具函数
├── locales/             # 国际化语言包
│   ├── zh-CN/          # 简体中文
│   └── en-US/          # 英文
└── assets/              # 静态资源
```

### 2. 组件命名规范

**文件命名**: `PascalCase.vue`

**组件名**: 使用 PascalCase
```vue
<script setup lang="ts" name="ChartList">
// 组件逻辑
</script>
```

### 3. Vue 3 Composition API 规范

**使用 `<script setup>`**:
```vue
<template>
  <div class="chart-container">
    <el-button @click="handleCreate">创建图表</el-button>
    <el-table :data="chartList">
      <!-- 表格内容 -->
    </el-table>
  </div>
</template>

<script setup lang="ts" name="ChartList">
import { ref, onMounted } from 'vue'
import { getChartList, createChart } from '@/api/chart'
import type { ChartDTO } from '@/api/chart'

// 响应式数据
const chartList = ref<ChartDTO[]>([])

// 方法
const loadChartList = async () => {
  const res = await getChartList()
  chartList.value = res.data
}

const handleCreate = () => {
  // 创建图表逻辑
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

**规范要点**:
- 使用 `<script setup lang="ts">`
- 明确指定组件 `name`
- 使用 TypeScript 类型标注
- 样式使用 `scoped` 避免污染

### 4. API 调用规范

**API 文件组织**: 按模块划分,每个模块一个文件

**示例** (`api/chart.ts`):
```typescript
import request from '@/utils/request'

// 类型定义
export interface ChartDTO {
  id: number
  name: string
  type: string
  datasetId: number
}

export interface ChartRequest {
  name: string
  type: string
  datasetId: number
}

// API 方法
export const getChartList = (params?: any) => {
  return request.get<ChartDTO[]>('/api/chart/list', { params })
}

export const createChart = (data: ChartRequest) => {
  return request.post<ChartDTO>('/api/chart/create', data)
}

export const updateChart = (id: number, data: ChartRequest) => {
  return request.put<ChartDTO>(`/api/chart/${id}`, data)
}

export const deleteChart = (id: number) => {
  return request.delete(`/api/chart/${id}`)
}
```

**规范要点**:
- 导出类型定义(`interface`)
- API 方法使用具体的请求方法(`get`, `post`, `put`, `delete`)
- 使用泛型指定返回类型
- RESTful 风格的 URL

### 5. 状态管理规范

**使用 Pinia**:
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
    // State
    currentChart,
    chartList,
    // Actions
    setCurrentChart,
    updateChartList,
    // Getters
    hasCurrentChart
  }
})
```

**规范要点**:
- 使用 Composition API 风格
- State 使用 `ref` 或 `reactive`
- 导出 state, actions, getters
- 模块化组织,按功能划分

### 6. 路由配置规范

**路由定义** (`router/modules/chart.ts`):
```typescript
import type { RouteRecordRaw } from 'vue-router'

const chartRoutes: RouteRecordRaw[] = [
  {
    path: '/chart',
    name: 'Chart',
    meta: { title: '图表管理', icon: 'chart' },
    children: [
      {
        path: 'list',
        name: 'ChartList',
        component: () => import('@/views/chart/index.vue'),
        meta: { title: '图表列表' }
      },
      {
        path: 'edit/:id',
        name: 'ChartEdit',
        component: () => import('@/views/chart/edit.vue'),
        meta: { title: '编辑图表' }
      }
    ]
  }
]

export default chartRoutes
```

**规范要点**:
- 路由懒加载(`import()`)
- 添加 `meta` 信息(标题、图标、权限等)
- 使用 TypeScript 类型

### 7. 国际化规范

**语言文件结构**:
```
locales/
├── zh-CN/
│   ├── common.ts      # 公共翻译
│   ├── chart.ts       # 图表相关
│   └── ...
└── en-US/
    ├── common.ts
    ├── chart.ts
    └── ...
```

**使用示例**:
```typescript
// locales/zh-CN/chart.ts
export default {
  chart: {
    title: '图表',
    create: '创建图表',
    edit: '编辑图表',
    delete: '删除图表',
    name: '图表名称',
    type: '图表类型'
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

### 8. 样式规范

**使用 Less**:
```vue
<style scoped lang="less">
.chart-container {
  padding: 20px;

  .chart-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;

    .title {
      font-size: 18px;
      font-weight: 500;
    }
  }

  .chart-content {
    background-color: #fff;
    border-radius: 4px;
  }
}
</style>
```

**规范要点**:
- 使用 `scoped` 限制作用域
- 使用 BEM 命名规范(可选)
- 嵌套不超过 3 层
- 使用变量定义颜色、字体等

### 9. TypeScript 类型规范

**类型定义位置**:
- API 相关: `api/*.ts` 中定义
- 组件 Props: 组件内定义
- 全局类型: `types/*.d.ts`

**示例**:
```typescript
// types/common.d.ts
export interface PageInfo {
  page: number
  size: number
  total: number
}

export interface ListResponse<T> {
  data: T[]
  pageInfo: PageInfo
}
```

### 10. 组件通信规范

**Props 传递**:
```vue
<script setup lang="ts">
interface Props {
  chartId: number
  mode?: 'view' | 'edit'
}

const props = withDefaults(defineProps<Props>(), {
  mode: 'view'
})
</script>
```

**Emit 事件**:
```vue
<script setup lang="ts">
interface Emits {
  (e: 'update', id: number): void
  (e: 'delete', id: number): void
}

const emit = defineEmits<Emits>()

const handleUpdate = () => {
  emit('update', props.chartId)
}
</script>
```

## 前后端协作规范

### 1. API 接口规范

**请求格式**:
```json
{
  "name": "销售趋势图",
  "type": "line",
  "datasetId": 123
}
```

**响应格式**:
```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "销售趋势图",
    "type": "line",
    "datasetId": 123,
    "createTime": 1677649200000
  }
}
```

**错误响应**:
```json
{
  "code": 500,
  "msg": "图表名称不能为空",
  "data": null
}
```

### 2. 时间格式规范

- **后端**: 使用时间戳(Long 类型,毫秒)
- **前端**: 根据需要格式化显示
- **传输**: 始终使用时间戳

### 3. ID 类型规范

- 统一使用 `Long` 类型(后端) / `number` 类型(前端)
- 不使用 UUID

## 测试规范

### 后端测试

**单元测试**:
```java
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChartServiceTest {

    @Resource
    private ChartService chartService;

    @Test
    public void testCreateChart() {
        ChartRequest request = new ChartRequest();
        request.setName("测试图表");
        request.setType("line");
        request.setDatasetId(1L);

        ChartDTO chart = chartService.create(request);

        assertNotNull(chart.getId());
        assertEquals("测试图表", chart.getName());
    }
}
```

### 前端测试

目前前端暂未强制要求单元测试,但建议对核心工具函数编写测试。

## Git 提交规范

**Commit 消息格式** (使用中文):
```
<类型>(<模块>): <简短描述>

<详细描述>
```

**类型**:
- `feat`: 新功能
- `fix`: 修复问题
- `refactor`: 重构
- `style`: 代码格式调整
- `docs`: 文档更新
- `test`: 测试相关
- `chore`: 构建/工具相关

**示例**:
```
feat(chart): 添加柱状图类型支持

- 添加柱状图配置选项
- 实现柱状图渲染逻辑
- 更新图表类型枚举
```

## 性能优化建议

### 后端优化
1. 使用 MyBatis Plus 的批量操作
2. 合理使用缓存(`@Cacheable`)
3. 避免 N+1 查询问题
4. 大数据量使用分页
5. 异步处理耗时操作(`@Async`)

### 前端优化
1. 路由懒加载
2. 组件懒加载
3. 虚拟滚动(大列表)
4. 防抖/节流
5. 图片懒加载

## 跨平台开发注意事项

### 前端脚本跨平台支持

**所有 npm scripts 已优化为跨平台兼容**:
- 使用 `cross-env` 设置环境变量 (Windows/Linux/Mac 兼容)
- 路径处理自动适配不同操作系统
- 无需手动修改脚本即可在不同平台使用

**开发环境准备**:
```bash
# 安装依赖 (首次或更新后)
cd core/core-frontend
npm install

# 所有命令在各平台均可正常使用
npm run dev              # 开发模式
npm run build:base       # 构建单机版
npm run build:distributed # 构建分布式版
```

### Windows 开发者注意事项

1. **环境变量**: 使用 npm scripts 而不是直接设置环境变量
2. **路径分隔符**: 使用正斜杠 `/` 或让工具自动处理
3. **Shell 选择**: PowerShell、CMD 或 Git Bash 均可
4. **换行符配置**:
   ```bash
   git config --global core.autocrlf true
   ```

### Linux/Mac 开发者注意事项

1. **脚本权限**: 某些脚本可能需要执行权限
2. **换行符配置**:
   ```bash
   git config --global core.autocrlf input
   ```

### 跨平台最佳实践

1. **始终使用 npm scripts**: 不要直接运行底层命令
2. **路径使用相对路径**: 避免硬编码绝对路径
3. **环境变量通过 cross-env**: 不要使用平台特定语法
4. **文件操作使用工具**: 如 `rimraf` 而不是 `rm -rf`

## 相关文档

- [/CLAUDE.md](../CLAUDE.md) - 全局开发规范
- [/PROJECT.md](../PROJECT.md) - 系统整体架构
- [core/PROJECT.md](./PROJECT.md) - Core 模块详细架构
