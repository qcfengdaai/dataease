/**
 * 系统变量管理模块
 *
 * <h2>模块概述</h2>
 * <p>本模块提供系统变量和变量值的完整管理功能。系统变量是一种灵活的参数化配置机制,
 * 支持多种数据类型和值域限制,用于存储系统级和用户级的配置参数。</p>
 *
 * <h2>核心功能</h2>
 * <ul>
 *   <li><strong>变量定义管理</strong>:创建、编辑、删除、查询系统变量定义</li>
 *   <li><strong>变量值管理</strong>:为变量创建、编辑、删除可选值</li>
 *   <li><strong>用户级变量</strong>:支持用户级别的变量赋值,不同用户可以有不同的变量值</li>
 *   <li><strong>类型支持</strong>:支持文本、数值、日期、枚举等多种数据类型</li>
 *   <li><strong>值域限制</strong>:支持定义变量的取值范围和可选值</li>
 * </ul>
 *
 * <h2>变量模型</h2>
 *
 * <h3>核心概念</h3>
 * <pre>
 * ┌──────────────────────────────────────────────┐
 * │          系统变量(SysVariable)                │
 * │    变量的定义,包括名称、类型、取值范围        │
 * └──────────────┬───────────────────────────────┘
 *                │
 *                │ 1:N
 *                ↓
 * ┌──────────────────────────────────────────────┐
 * │       变量值(SysVariableValue)                │
 * │    变量的具体取值,一个变量可以有多个可选值    │
 * └──────────────┬───────────────────────────────┘
 *                │
 *                │ N:M
 *                ↓
 * ┌──────────────────────────────────────────────┐
 * │         用户变量值                            │
 * │    用户级别的变量赋值,不同用户可以有不同的值  │
 * └──────────────────────────────────────────────┘
 * </pre>
 *
 * <h3>变量类型</h3>
 * <ul>
 *   <li><strong>文本类型(TEXT)</strong>:
 *     <ul>
 *       <li>存储字符串值</li>
 *       <li>支持长度限制</li>
 *       <li>使用场景:名称、描述、标签等</li>
 *     </ul>
 *   </li>
 *   <li><strong>数值类型(NUMBER)</strong>:
 *     <ul>
 *       <li>存储数字值(整数或小数)</li>
 *       <li>支持最小值、最大值限制</li>
 *       <li>使用场景:阈值、配额、计数等</li>
 *     </ul>
 *   </li>
 *   <li><strong>日期类型(DATE)</strong>:
 *     <ul>
 *       <li>存储日期或日期时间值</li>
 *       <li>支持日期范围限制</li>
 *       <li>使用场景:统计周期、有效期等</li>
 *     </ul>
 *   </li>
 *   <li><strong>枚举类型(ENUM)</strong>:
 *     <ul>
 *       <li>存储预定义的多个可选值之一</li>
 *       <li>通过 SysVariableValue 定义可选值列表</li>
 *       <li>使用场景:状态、类型、分类等</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>变量作用域</h3>
 * <ul>
 *   <li><strong>系统级变量</strong>:全局配置,所有用户共享同一个值</li>
 *   <li><strong>用户级变量</strong>:个性化配置,每个用户可以有自己的值</li>
 *   <li><strong>组织级变量</strong>:组织范围内共享的配置</li>
 * </ul>
 *
 * <h2>包结构</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.variable.api} - API 接口定义层
 *     <ul>
 *       <li>{@link io.dataease.api.permissions.variable.api.SysVariablesApi} - 系统变量管理接口</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.variable.dto} - 数据传输对象
 *     <ul>
 *       <li>SysVariableDto:系统变量定义的数据传输对象</li>
 *       <li>SysVariableValueDto:变量值的数据传输对象</li>
 *       <li>SysVariableValueItem:变量值选项</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>使用场景</h2>
 *
 * <h3>1. 创建系统变量</h3>
 * <pre>{@code
 * POST /api/sysVariable/create
 * Content-Type: application/json
 *
 * {
 *   "name": "report_retention_days",
 *   "displayName": "报表保留天数",
 *   "type": "NUMBER",
 *   "defaultValue": "90",
 *   "minValue": 30,
 *   "maxValue": 365,
 *   "description": "报表数据的保留天数",
 *   "scope": "SYSTEM"
 * }
 *
 * Response:
 * {
 *   "id": 1001,
 *   "name": "report_retention_days",
 *   ...
 * }
 * }</pre>
 *
 * <h3>2. 创建枚举类型变量</h3>
 * <pre>{@code
 * // 步骤1:创建变量
 * POST /api/sysVariable/create
 * {
 *   "name": "report_export_format",
 *   "displayName": "报表导出格式",
 *   "type": "ENUM",
 *   "description": "报表支持的导出格式",
 *   "scope": "USER"
 * }
 *
 * // 步骤2:为变量创建可选值
 * POST /api/sysVariable/value/create
 * {
 *   "variableId": 1002,
 *   "value": "PDF",
 *   "displayValue": "PDF格式",
 *   "sortOrder": 1
 * }
 *
 * POST /api/sysVariable/value/create
 * {
 *   "variableId": 1002,
 *   "value": "EXCEL",
 *   "displayValue": "Excel格式",
 *   "sortOrder": 2
 * }
 * }</pre>
 *
 * <h3>3. 查询变量的所有可选值</h3>
 * <pre>{@code
 * GET /api/sysVariable/value/selected/1002
 *
 * Response:
 * [
 *   {
 *     "id": "v1",
 *     "variableId": 1002,
 *     "value": "PDF",
 *     "displayValue": "PDF格式",
 *     "sortOrder": 1
 *   },
 *   {
 *     "id": "v2",
 *     "variableId": 1002,
 *     "value": "EXCEL",
 *     "displayValue": "Excel格式",
 *     "sortOrder": 2
 *   }
 * ]
 * }</pre>
 *
 * <h3>4. 查询系统变量列表</h3>
 * <pre>{@code
 * POST /api/sysVariable/query
 * Content-Type: application/json
 *
 * {
 *   "type": "NUMBER",        // 按类型筛选
 *   "scope": "SYSTEM",       // 按作用域筛选
 *   "keyword": "report"      // 关键字搜索
 * }
 *
 * Response:
 * [
 *   {
 *     "id": 1001,
 *     "name": "report_retention_days",
 *     "displayName": "报表保留天数",
 *     "type": "NUMBER",
 *     "defaultValue": "90",
 *     "currentValue": "120",  // 当前用户设置的值
 *     "scope": "SYSTEM"
 *   }
 * ]
 * }</pre>
 *
 * <h3>5. 编辑变量定义</h3>
 * <pre>{@code
 * POST /api/sysVariable/edit
 * Content-Type: application/json
 *
 * {
 *   "id": 1001,
 *   "displayName": "报表数据保留天数",
 *   "defaultValue": "120",
 *   "minValue": 60,
 *   "maxValue": 730,
 *   "description": "报表数据在系统中的保留天数,超过此天数的数据将被自动清理"
 * }
 * }</pre>
 *
 * <h3>6. 删除变量值</h3>
 * <pre>{@code
 * GET /api/sysVariable/value/delete/v1
 *
 * // 注意:如果该值正在被用户使用,删除后用户的配置将失效
 * }</pre>
 *
 * <h2>实现位置</h2>
 * <p>本模块的接口实现位于:</p>
 * <ul>
 *   <li><strong>单机版/社区版</strong>: {@code core-backend/src/main/java/io/dataease/system/}
 *     <ul>
 *       <li>controller: SysVariableController</li>
 *       <li>service: SysVariableService、SysVariableServiceImpl</li>
 *     </ul>
 *   </li>
 *   <li><strong>企业版</strong>: 分布式模块中的对应实现</li>
 * </ul>
 *
 * <h2>数据模型</h2>
 * <p>系统变量涉及的主要数据表:</p>
 * <ul>
 *   <li><strong>sys_variable</strong>:系统变量定义表,存储变量的元数据</li>
 *   <li><strong>sys_variable_value</strong>:变量值表,存储枚举类型变量的可选值</li>
 *   <li><strong>sys_user_variable</strong>:用户变量值表,存储用户级别的变量赋值</li>
 * </ul>
 *
 * <h2>应用场景</h2>
 *
 * <h3>1. 系统配置</h3>
 * <p>使用系统变量存储全局性的系统参数:</p>
 * <ul>
 *   <li>数据保留策略</li>
 *   <li>性能参数(并发数、超时时间等)</li>
 *   <li>功能开关</li>
 *   <li>默认值配置</li>
 * </ul>
 *
 * <h3>2. 用户偏好</h3>
 * <p>使用用户级变量存储个性化设置:</p>
 * <ul>
 *   <li>界面主题</li>
 *   <li>默认导出格式</li>
 *   <li>数据刷新频率</li>
 *   <li>通知偏好</li>
 * </ul>
 *
 * <h3>3. 数据筛选</h3>
 * <p>在查询、报表中使用变量作为参数:</p>
 * <ul>
 *   <li>时间范围变量(开始日期、结束日期)</li>
 *   <li>状态筛选变量</li>
 *   <li>分类筛选变量</li>
 *   <li>阈值参数</li>
 * </ul>
 *
 * <h3>4. 权限控制</h3>
 * <p>基于变量值的权限判断:</p>
 * <ul>
 *   <li>根据部门变量控制数据访问范围</li>
 *   <li>根据级别变量控制功能权限</li>
 *   <li>根据区域变量控制资源可见性</li>
 * </ul>
 *
 * <h2>变量命名规范</h2>
 * <ul>
 *   <li><strong>name</strong>: 变量标识,使用小写字母和下划线,如 {@code report_retention_days}</li>
 *   <li><strong>displayName</strong>: 显示名称,使用中文或其他用户友好的名称,如 "报表保留天数"</li>
 *   <li><strong>命名原则</strong>:
 *     <ul>
 *       <li>见名知意,清晰表达变量用途</li>
 *       <li>使用统一的命名风格</li>
 *       <li>系统变量使用 system_ 前缀</li>
 *       <li>用户变量使用 user_ 前缀</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>变量值的存储格式</h2>
 * <ul>
 *   <li><strong>文本类型</strong>: 直接存储字符串值</li>
 *   <li><strong>数值类型</strong>: 存储为字符串,使用时转换为数字</li>
 *   <li><strong>日期类型</strong>: 存储为时间戳或 ISO 8601 格式字符串</li>
 *   <li><strong>枚举类型</strong>: 存储枚举值的 value 字段</li>
 * </ul>
 *
 * <h2>变量的继承和覆盖</h2>
 * <ol>
 *   <li><strong>默认值</strong>: 变量定义中设置的默认值</li>
 *   <li><strong>系统级设置</strong>: 系统管理员设置的全局值</li>
 *   <li><strong>组织级设置</strong>: 组织管理员设置的组织值</li>
 *   <li><strong>用户级设置</strong>: 用户自己设置的个人值</li>
 * </ol>
 * <p>优先级: 用户级 > 组织级 > 系统级 > 默认值</p>
 *
 * <h2>安全说明</h2>
 * <ul>
 *   <li><strong>访问控制</strong>: 系统变量管理需要管理员权限</li>
 *   <li><strong>敏感变量</strong>: 密码、密钥等敏感信息不应该使用系统变量存储</li>
 *   <li><strong>变量验证</strong>: 设置变量值时需要验证是否符合类型和值域限制</li>
 *   <li><strong>操作审计</strong>: 系统变量的变更应该记录审计日志</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>删除变量定义会同时删除该变量的所有值和用户设置</li>
 *   <li>修改变量类型需要谨慎,可能导致已有值无效</li>
 *   <li>枚举类型变量删除可选值前,应检查是否有用户正在使用该值</li>
 *   <li>变量名一旦创建建议不要修改,因为可能被代码引用</li>
 *   <li>为变量提供清晰的描述和文档,方便使用者理解</li>
 *   <li>合理使用默认值,减少用户配置负担</li>
 * </ol>
 *
 * <h2>最佳实践</h2>
 * <ul>
 *   <li><strong>分类管理</strong>: 使用前缀或分组对变量进行分类管理</li>
 *   <li><strong>文档完善</strong>: 为每个变量提供清晰的描述和使用说明</li>
 *   <li><strong>合理默认值</strong>: 设置合理的默认值,覆盖大多数使用场景</li>
 *   <li><strong>值域限制</strong>: 为数值和日期类型设置合理的范围限制</li>
 *   <li><strong>枚举优先</strong>: 对于固定选项,优先使用枚举类型而不是文本类型</li>
 *   <li><strong>版本控制</strong>: 重要的系统变量变更应该有版本记录</li>
 *   <li><strong>定期清理</strong>: 定期清理不再使用的变量定义</li>
 * </ul>
 *
 * <h2>扩展功能</h2>
 * <ul>
 *   <li>支持变量表达式,如 {@code ${var1} + ${var2}}</li>
 *   <li>支持变量依赖关系,一个变量的可选值依赖于另一个变量</li>
 *   <li>支持变量的导入导出,方便环境迁移</li>
 *   <li>支持变量的版本管理和回滚</li>
 * </ul>
 *
 * @author DataEase Team
 * @since 1.0
 * @see io.dataease.api.permissions.user
 * @see io.dataease.api.permissions.org
 */
package io.dataease.api.permissions.variable;
