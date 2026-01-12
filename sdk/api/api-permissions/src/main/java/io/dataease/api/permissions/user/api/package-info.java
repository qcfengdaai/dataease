/**
 * 用户管理 API 接口层
 *
 * <p>本包定义了用户管理的所有 RESTful 接口,提供用户的完整生命周期管理和安全功能。</p>
 *
 * <h2>核心接口</h2>
 *
 * <h3>UserApi - 用户管理接口</h3>
 * <p>{@link io.dataease.api.permissions.user.api.UserApi} - 提供用户管理的所有对外接口</p>
 *
 * <h3>主要功能分类</h3>
 *
 * <h4>1. 用户基本操作</h4>
 * <ul>
 *   <li><strong>创建用户</strong>: {@code POST /api/user/create}</li>
 *   <li><strong>创建第三方用户</strong>: {@code POST /api/user/createPlatform}</li>
 *   <li><strong>编辑用户</strong>: {@code POST /api/user/edit}</li>
 *   <li><strong>删除用户</strong>: {@code POST /api/user/delete/{id}}</li>
 *   <li><strong>批量删除用户</strong>: {@code POST /api/user/batchDel}</li>
 *   <li><strong>查询用户列表(分页)</strong>: {@code POST /api/user/pager/{goPage}/{pageSize}}</li>
 *   <li><strong>查询用户详情</strong>: {@code GET /api/user/queryById/{id}}</li>
 * </ul>
 *
 * <h4>2. 个人中心</h4>
 * <ul>
 *   <li><strong>查询个人信息</strong>: {@code GET /api/user/personInfo}</li>
 *   <li><strong>修改个人信息</strong>: {@code POST /api/user/personEdit}</li>
 *   <li><strong>修改个人密码</strong>: {@code POST /api/user/modifyPwd}</li>
 *   <li><strong>获取当前登录人信息</strong>: {@code GET /api/user/info}</li>
 *   <li><strong>查询客户端IP信息</strong>: {@code GET /api/user/ipInfo}</li>
 *   <li><strong>切换语言</strong>: {@code POST /api/user/switchLanguage}</li>
 * </ul>
 *
 * <h4>3. 账号管理</h4>
 * <ul>
 *   <li><strong>查询默认密码</strong>: {@code GET /api/user/defaultPwd}</li>
 *   <li><strong>重置为默认密码</strong>: {@code POST /api/user/resetPwd/{id}}</li>
 *   <li><strong>切换用户状态</strong>: {@code POST /api/user/enable}</li>
 * </ul>
 *
 * <h4>4. 组织和角色</h4>
 * <ul>
 *   <li><strong>切换组织</strong>: {@code POST /api/user/switch/{oId}}</li>
 *   <li><strong>查询组织内用户</strong>: {@code GET /api/user/org/option}</li>
 *   <li><strong>角色可绑用户</strong>: {@code POST /api/user/role/option}</li>
 *   <li><strong>角色已绑用户</strong>: {@code POST /api/user/role/selected/{goPage}/{pageSize}}</li>
 *   <li><strong>查询当前组织内用户</strong>: {@code POST /api/user/byCurOrg}</li>
 * </ul>
 *
 * <h4>5. 批量操作</h4>
 * <ul>
 *   <li><strong>下载批量导入模版</strong>: {@code POST /api/user/excelTemplate}</li>
 *   <li><strong>批量导入</strong>: {@code POST /api/user/batchImport}</li>
 *   <li><strong>下载批量导入失败记录</strong>: {@code GET /api/user/errorRecord/{key}}</li>
 *   <li><strong>清理批量导入失败记录</strong>: {@code GET /api/user/clearErrorRecord/{key}}</li>
 * </ul>
 *
 * <h4>6. 第三方平台集成</h4>
 * <ul>
 *   <li><strong>绑定第三方账号</strong>: {@code POST /api/user/bind} (内部接口)</li>
 *   <li><strong>管理员绑定第三方账号</strong>: {@code POST /api/user/admin/bind} (内部接口)</li>
 *   <li><strong>解除绑定</strong>: {@code POST /api/user/unBind/{origin}}</li>
 *   <li><strong>绑定状态</strong>: {@code GET /api/user/bindStatus}</li>
 * </ul>
 *
 * <h4>7. 多因素认证(MFA)</h4>
 * <ul>
 *   <li><strong>MFA二维码信息</strong>: {@code GET /api/user/mfaQr}</li>
 *   <li><strong>MFA绑定状态</strong>: {@code GET /api/user/mfabound}</li>
 *   <li><strong>绑定MFA</strong>: {@code POST /api/user/mfaBind}</li>
 *   <li><strong>解绑MFA</strong>: {@code POST /api/user/mfaUnbind/{code}}</li>
 *   <li><strong>重置MFA绑定状态</strong>: {@code POST /api/user/mfaRest/{id}}</li>
 * </ul>
 *
 * <h4>8. 内部接口(系统调用)</h4>
 * <ul>
 *   <li><strong>用户数量</strong>: {@code GET /api/user/userCount}</li>
 *   <li><strong>查询第一梯队用户</strong>: {@code GET /api/user/firstEchelon/{limit}}</li>
 *   <li><strong>根据账号查询用户</strong>: {@code GET /api/user/queryByAccount/{account}}</li>
 *   <li><strong>查询所有用户</strong>: {@code POST /api/user/all}</li>
 *   <li><strong>判断当前用户是否为组织管理员</strong>: {@code GET /api/user/orgAdmin}</li>
 *   <li><strong>判断当前用户是否为默认组织管理员</strong>: {@code GET /api/user/defaultOrgAdmin}</li>
 *   <li><strong>查询子组织用户</strong>: {@code POST /api/user/subOrgUser}</li>
 *   <li><strong>获取消息接收人</strong>: {@code GET /api/user/getRecipient}</li>
 *   <li><strong>获取用户语言偏好</strong>: {@code GET /api/user/lang}</li>
 * </ul>
 *
 * <h2>接口访问路径</h2>
 * <p>基础路径: {@code /api/user}</p>
 *
 * <h2>权限控制</h2>
 * <p>所有接口都通过 {@code @DePermit} 注解进行权限控制:</p>
 * <ul>
 *   <li><strong>查询权限</strong>: {@code m:read} - 需要模块读取权限</li>
 *   <li><strong>管理权限</strong>: {@code m:manage} 或 {@code {userId}:manage} - 需要模块管理权限或特定用户管理权限</li>
 *   <li><strong>个人操作</strong>: 不需要特殊权限,用户只能操作自己的数据</li>
 * </ul>
 *
 * <h2>请求响应格式</h2>
 *
 * <h3>创建用户请求</h3>
 * <pre>{@code
 * POST /api/user/create
 * Content-Type: application/json
 * Authorization: Bearer {jwt_token}
 *
 * {
 *   "account": "zhangsan",
 *   "name": "张三",
 *   "email": "zhangsan@example.com",
 *   "phone": "13800138000",
 *   "roleIds": [1001, 1002],
 *   "enabled": 1
 * }
 * }</pre>
 *
 * <h3>分页查询响应</h3>
 * <pre>{@code
 * {
 *   "code": 0,
 *   "data": {
 *     "total": 156,
 *     "current": 1,
 *     "size": 20,
 *     "pages": 8,
 *     "records": [
 *       {
 *         "id": 10001,
 *         "account": "zhangsan",
 *         "name": "张三",
 *         "email": "zhangsan@example.com",
 *         "enabled": 1,
 *         "createTime": 1705123456789,
 *         "roles": [
 *           {"id": 1001, "name": "数据分析师"}
 *         ]
 *       }
 *     ]
 *   }
 * }
 * }</pre>
 *
 * <h2>使用建议</h2>
 *
 * <h3>前端应用场景</h3>
 * <ul>
 *   <li><strong>用户管理页面</strong>: 使用分页查询、创建、编辑、删除接口</li>
 *   <li><strong>个人中心</strong>: 使用个人信息、密码修改、语言切换接口</li>
 *   <li><strong>角色管理页面</strong>: 使用角色相关的用户查询接口</li>
 *   <li><strong>批量导入</strong>: 使用模板下载、导入、错误记录处理接口</li>
 *   <li><strong>安全设置</strong>: 使用 MFA 和第三方账号绑定接口</li>
 * </ul>
 *
 * <h3>后端服务调用</h3>
 * <ul>
 *   <li>使用内部接口(@Hidden标记)进行系统内部的用户查询和判断</li>
 *   <li>消息推送功能使用接收人查询接口</li>
 *   <li>权限判断使用组织管理员判断接口</li>
 * </ul>
 *
 * <h2>错误处理</h2>
 * <p>接口可能返回的常见错误:</p>
 * <ul>
 *   <li><strong>400 Bad Request</strong>: 请求参数不合法(如账号格式错误、必填字段为空)</li>
 *   <li><strong>401 Unauthorized</strong>: 未登录或登录已过期</li>
 *   <li><strong>403 Forbidden</strong>: 没有权限执行该操作</li>
 *   <li><strong>404 Not Found</strong>: 用户不存在</li>
 *   <li><strong>409 Conflict</strong>: 用户账号已存在</li>
 *   <li><strong>422 Unprocessable Entity</strong>: 业务逻辑错误(如密码不符合要求)</li>
 *   <li><strong>500 Internal Server Error</strong>: 服务器内部错误</li>
 * </ul>
 *
 * <h2>安全注意事项</h2>
 * <ul>
 *   <li>密码相关接口必须使用 HTTPS 传输</li>
 *   <li>修改密码时必须验证旧密码</li>
 *   <li>重置密码、MFA 操作需要管理员权限</li>
 *   <li>敏感操作(删除用户、批量操作)需要二次确认</li>
 *   <li>所有操作都记录审计日志</li>
 *   <li>用户查询接口不返回密码等敏感信息</li>
 *   <li>个人信息接口只能查询和修改当前登录用户的信息</li>
 * </ul>
 *
 * <h2>性能优化</h2>
 * <ul>
 *   <li>用户列表查询必须分页,避免一次加载大量数据</li>
 *   <li>用户基本信息会被缓存,减少数据库查询</li>
 *   <li>批量导入使用异步处理,避免长时间占用连接</li>
 *   <li>批量删除使用批量操作,提高性能</li>
 *   <li>组织切换后的新 Token 包含用户权限信息,减少后续查询</li>
 * </ul>
 *
 * <h2>并发控制</h2>
 * <ul>
 *   <li>用户编辑操作支持乐观锁,防止并发修改</li>
 *   <li>密码修改、MFA 绑定等敏感操作在事务中执行</li>
 *   <li>批量导入支持并发处理,但会限制并发数</li>
 *   <li>组织切换会使旧 Token 失效,保证会话唯一性</li>
 * </ul>
 *
 * <h2>限流策略</h2>
 * <ul>
 *   <li>密码修改接口限制频率(如 5 次/小时)</li>
 *   <li>批量导入接口限制文件大小和记录数</li>
 *   <li>MFA 验证接口限制尝试次数,防止暴力破解</li>
 *   <li>用户查询接口限制每页最大记录数</li>
 * </ul>
 *
 * @see io.dataease.api.permissions.user.dto
 * @see io.dataease.api.permissions.user.vo
 * @see io.dataease.api.permissions.user.bo
 * @since 1.0
 */
package io.dataease.api.permissions.user.api;
