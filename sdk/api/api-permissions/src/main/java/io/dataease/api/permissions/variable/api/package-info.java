/**
 * 系统变量管理 API 接口层
 *
 * <p>本包定义了系统变量管理的所有 RESTful 接口,提供变量定义和变量值的完整管理功能。</p>
 *
 * <h2>核心接口</h2>
 *
 * <h3>SysVariablesApi - 系统变量管理接口</h3>
 * <p>{@link io.dataease.api.permissions.variable.api.SysVariablesApi} - 提供系统变量管理的所有接口</p>
 *
 * <h3>主要功能分类</h3>
 *
 * <h4>1. 变量定义管理</h4>
 * <ul>
 *   <li><strong>创建变量</strong>: {@code POST /api/sysVariable/create}</li>
 *   <li><strong>编辑变量</strong>: {@code POST /api/sysVariable/edit}</li>
 *   <li><strong>删除变量</strong>: {@code GET /api/sysVariable/delete/{id}}</li>
 *   <li><strong>查询变量详情</strong>: {@code GET /api/sysVariable/detail/{id}}</li>
 *   <li><strong>查询变量列表</strong>: {@code POST /api/sysVariable/query}</li>
 * </ul>
 *
 * <h4>2. 变量值管理</h4>
 * <ul>
 *   <li><strong>创建变量值</strong>: {@code POST /api/sysVariable/value/create}</li>
 *   <li><strong>编辑变量值</strong>: {@code POST /api/sysVariable/value/edit}</li>
 *   <li><strong>删除变量值</strong>: {@code GET /api/sysVariable/value/delete/{id}}</li>
 *   <li><strong>批量删除变量值</strong>: {@code POST /api/sysVariable/value/batchDel}</li>
 *   <li><strong>查询变量的所有值</strong>: {@code GET /api/sysVariable/value/selected/{id}}</li>
 *   <li><strong>分页查询变量值</strong>: {@code POST /api/sysVariable/value/selected/{goPage}/{pageSize}}</li>
 * </ul>
 *
 * <h2>接口访问路径</h2>
 * <p>基础路径: {@code /api/sysVariable}</p>
 *
 * <h2>接口特点</h2>
 * <p>本接口标记为 {@code @Hidden},主要供系统内部使用,不对外暴露在 API 文档中。</p>
 *
 * <h2>权限控制</h2>
 * <p>系统变量管理功能需要系统管理员权限,所有接口都有相应的权限验证。</p>
 *
 * <h2>请求响应格式</h2>
 *
 * <h3>创建变量请求</h3>
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
 *   "scope": "SYSTEM",
 *   "required": false
 * }
 * }</pre>
 *
 * <h3>创建变量响应</h3>
 * <pre>{@code
 * {
 *   "code": 0,
 *   "data": {
 *     "id": 1001,
 *     "name": "report_retention_days",
 *     "displayName": "报表保留天数",
 *     "type": "NUMBER",
 *     "defaultValue": "90",
 *     "minValue": 30,
 *     "maxValue": 365,
 *     "description": "报表数据的保留天数",
 *     "scope": "SYSTEM",
 *     "createTime": 1705123456789
 *   },
 *   "message": "success"
 * }
 * }</pre>
 *
 * <h3>创建变量值请求</h3>
 * <pre>{@code
 * POST /api/sysVariable/value/create
 * Content-Type: application/json
 *
 * {
 *   "variableId": 1002,
 *   "value": "PDF",
 *   "displayValue": "PDF格式",
 *   "sortOrder": 1,
 *   "description": "导出为PDF格式文件"
 * }
 * }</pre>
 *
 * <h3>查询变量列表请求</h3>
 * <pre>{@code
 * POST /api/sysVariable/query
 * Content-Type: application/json
 *
 * {
 *   "type": "NUMBER",        // 按类型筛选(可选)
 *   "scope": "SYSTEM",       // 按作用域筛选(可选)
 *   "keyword": "report"      // 关键字搜索(可选)
 * }
 * }</pre>
 *
 * <h3>查询变量列表响应</h3>
 * <pre>{@code
 * {
 *   "code": 0,
 *   "data": [
 *     {
 *       "id": 1001,
 *       "name": "report_retention_days",
 *       "displayName": "报表保留天数",
 *       "type": "NUMBER",
 *       "defaultValue": "90",
 *       "currentValue": "120",
 *       "scope": "SYSTEM",
 *       "createTime": 1705123456789
 *     },
 *     {
 *       "id": 1002,
 *       "name": "report_export_format",
 *       "displayName": "报表导出格式",
 *       "type": "ENUM",
 *       "defaultValue": "PDF",
 *       "currentValue": "EXCEL",
 *       "scope": "USER",
 *       "createTime": 1705123456790
 *     }
 *   ],
 *   "message": "success"
 * }
 * }</pre>
 *
 * <h2>使用建议</h2>
 *
 * <h3>系统配置管理</h3>
 * <ul>
 *   <li>使用变量定义接口创建系统配置项</li>
 *   <li>使用查询接口展示配置列表</li>
 *   <li>使用编辑接口修改配置值</li>
 * </ul>
 *
 * <h3>枚举类型变量</h3>
 * <ol>
 *   <li>先创建变量定义,类型设置为 ENUM</li>
 *   <li>使用变量值接口为该变量创建多个可选值</li>
 *   <li>用户可以从可选值中选择一个作为自己的配置</li>
 * </ol>
 *
 * <h3>用户偏好设置</h3>
 * <ul>
 *   <li>创建变量时 scope 设置为 USER</li>
 *   <li>每个用户可以设置自己的变量值</li>
 *   <li>未设置时使用默认值</li>
 * </ul>
 *
 * <h2>数据类型说明</h2>
 *
 * <h3>变量类型(type)</h3>
 * <ul>
 *   <li><strong>TEXT</strong>: 文本类型
 *     <ul>
 *       <li>存储任意字符串</li>
 *       <li>可选字段: maxLength(最大长度)</li>
 *     </ul>
 *   </li>
 *   <li><strong>NUMBER</strong>: 数值类型
 *     <ul>
 *       <li>存储整数或小数</li>
 *       <li>可选字段: minValue(最小值)、maxValue(最大值)</li>
 *     </ul>
 *   </li>
 *   <li><strong>DATE</strong>: 日期类型
 *     <ul>
 *       <li>存储日期或日期时间</li>
 *       <li>可选字段: minDate(最小日期)、maxDate(最大日期)</li>
 *     </ul>
 *   </li>
 *   <li><strong>ENUM</strong>: 枚举类型
 *     <ul>
 *       <li>从预定义的可选值中选择</li>
 *       <li>需要配合变量值使用</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>变量作用域(scope)</h3>
 * <ul>
 *   <li><strong>SYSTEM</strong>: 系统级,全局共享</li>
 *   <li><strong>ORG</strong>: 组织级,组织内共享</li>
 *   <li><strong>USER</strong>: 用户级,每个用户独立</li>
 * </ul>
 *
 * <h2>错误处理</h2>
 * <p>接口可能返回的常见错误:</p>
 * <ul>
 *   <li><strong>400 Bad Request</strong>: 请求参数不合法
 *     <ul>
 *       <li>变量名格式不正确</li>
 *       <li>类型与值不匹配</li>
 *       <li>值超出范围限制</li>
 *     </ul>
 *   </li>
 *   <li><strong>401 Unauthorized</strong>: 未登录或登录已过期</li>
 *   <li><strong>403 Forbidden</strong>: 没有系统管理员权限</li>
 *   <li><strong>404 Not Found</strong>: 变量或变量值不存在</li>
 *   <li><strong>409 Conflict</strong>: 变量名已存在</li>
 *   <li><strong>422 Unprocessable Entity</strong>: 业务逻辑错误
 *     <ul>
 *       <li>删除正在使用的变量</li>
 *       <li>删除正在使用的变量值</li>
 *       <li>修改变量类型导致已有值无效</li>
 *     </ul>
 *   </li>
 *   <li><strong>500 Internal Server Error</strong>: 服务器内部错误</li>
 * </ul>
 *
 * <h2>业务规则</h2>
 *
 * <h3>变量定义规则</h3>
 * <ul>
 *   <li>变量名(name)必须唯一</li>
 *   <li>变量名只能包含字母、数字和下划线</li>
 *   <li>变量名建议使用小写字母和下划线</li>
 *   <li>displayName 用于界面展示,可以使用中文</li>
 *   <li>必须指定变量类型(type)</li>
 *   <li>defaultValue 必须符合类型和值域限制</li>
 * </ul>
 *
 * <h3>变量值规则</h3>
 * <ul>
 *   <li>只有 ENUM 类型的变量才需要创建变量值</li>
 *   <li>同一变量下的 value 必须唯一</li>
 *   <li>sortOrder 用于控制显示顺序</li>
 *   <li>displayValue 用于界面展示</li>
 * </ul>
 *
 * <h3>删除规则</h3>
 * <ul>
 *   <li>删除变量定义会级联删除所有变量值</li>
 *   <li>删除变量定义会清空所有用户的变量设置</li>
 *   <li>删除变量值会清空使用该值的用户设置</li>
 *   <li>建议删除前检查是否有用户正在使用</li>
 * </ul>
 *
 * <h2>性能优化</h2>
 * <ul>
 *   <li>变量定义和值会被缓存,减少数据库查询</li>
 *   <li>用户级变量值会被缓存到用户会话中</li>
 *   <li>变量列表支持分页,避免一次加载大量数据</li>
 *   <li>批量删除变量值使用批量操作,提高性能</li>
 * </ul>
 *
 * <h2>并发控制</h2>
 * <ul>
 *   <li>变量编辑操作支持乐观锁,防止并发修改</li>
 *   <li>变量删除会检查是否正在使用,避免并发删除</li>
 *   <li>用户设置变量值时会锁定,保证数据一致性</li>
 * </ul>
 *
 * <h2>扩展功能</h2>
 * <p>未来可能支持的扩展功能:</p>
 * <ul>
 *   <li>变量的导入导出功能</li>
 *   <li>变量的版本管理和回滚</li>
 *   <li>变量之间的依赖关系</li>
 *   <li>变量值的条件显示</li>
 *   <li>变量的使用统计和分析</li>
 * </ul>
 *
 * <h2>最佳实践</h2>
 * <ul>
 *   <li><strong>命名规范</strong>: 使用统一的命名风格和前缀</li>
 *   <li><strong>文档完善</strong>: 为每个变量提供清晰的描述</li>
 *   <li><strong>默认值</strong>: 设置合理的默认值,减少用户配置负担</li>
 *   <li><strong>值域限制</strong>: 为数值类型设置合理的范围</li>
 *   <li><strong>枚举优先</strong>: 固定选项使用枚举类型</li>
 *   <li><strong>定期审查</strong>: 定期清理不再使用的变量</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>变量名创建后建议不要修改,因为可能被代码引用</li>
 *   <li>修改变量类型需要谨慎,可能导致已有值无效</li>
 *   <li>删除操作不可逆,建议先备份</li>
 *   <li>敏感信息不要使用系统变量存储</li>
 *   <li>变量的变更应该记录审计日志</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.variable.dto
 * @since 1.0
 */
package io.dataease.api.permissions.variable.api;
