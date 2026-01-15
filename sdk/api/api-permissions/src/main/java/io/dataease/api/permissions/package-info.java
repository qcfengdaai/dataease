/**
 * DataEase 权限管理 API 模块
 *
 * <h2>模块概述</h2>
 * <p>本模块是 DataEase 权限管理系统的 API 接口定义层，提供完整的权限、用户、角色、组织等管理功能。
 * 采用模块化设计，将不同的功能划分为独立的子模块，便于维护和扩展。</p>
 *
 * <p>本模块遵循 <strong>接口定义与实现分离</strong> 的设计原则：</p>
 * <ul>
 *   <li><strong>本模块 (api-permissions)</strong>：仅定义接口、DTO、VO，不包含实现代码</li>
 *   <li><strong>实现模块 (core-backend)</strong>：提供接口的具体实现（Controller、Service、Mapper）</li>
 *   <li><strong>优势</strong>：接口定义稳定，实现可以灵活替换（单机版、企业版、云版）</li>
 * </ul>
 *
 * <h2>架构设计</h2>
 *
 * <h3>模块层次结构</h3>
 * <pre>
 * ┌─────────────────────────────────────────────────────────┐
 * │              DataEase 权限管理系统                        │
 * └─────────────────────────────────────────────────────────┘
 *                          │
 *          ┌───────────────┴───────────────┐
 *          │                               │
 *  ┌───────▼────────┐            ┌────────▼────────┐
 *  │  API 接口定义层  │            │   实现层         │
 *  │ (api-permissions)│ ◄────────│ (core-backend)  │
 *  │  - 接口          │  Feign    │  - Controller   │
 *  │  - DTO/VO/BO    │  调用     │  - Service      │
 *  │  - 文档注解      │            │  - Mapper       │
 *  └─────────────────┘            └─────────────────┘
 * </pre>
 *
 * <h3>RBAC 权限模型</h3>
 * <pre>
 * ┌──────────┐        ┌──────────┐        ┌──────────┐
 * │  用户     │ ◄────► │   角色    │ ◄────► │  权限    │
 * │  User    │  N:N   │   Role   │  N:N   │  Auth    │
 * └──────────┘        └──────────┘        └──────────┘
 *      │                    │                    │
 *      │ N:N                │ N:1                │ N:1
 *      ▼                    ▼                    ▼
 * ┌──────────┐        ┌──────────┐        ┌──────────┐
 * │  组织     │        │  权限目标 │        │  业务资源 │
 * │   Org    │        │  Menu    │        │ Resource │
 * └──────────┘        └──────────┘        └──────────┘
 * </pre>
 *
 * <h2>子模块列表</h2>
 *
 * <h3>核心管理模块</h3>
 * <ol>
 *   <li>{@link io.dataease.api.permissions.user} - <strong>用户管理模块</strong>
 *     <ul>
 *       <li>用户信息管理（CRUD）</li>
 *       <li>用户状态控制（启用/禁用）</li>
 *       <li>密码管理和重置</li>
 *       <li>个人中心功能</li>
 *       <li>用户导入导出</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.role} - <strong>角色管理模块</strong>
 *     <ul>
 *       <li>角色定义和管理</li>
 *       <li>角色成员管理</li>
 *       <li>角色权限配置</li>
 *       <li>角色复制功能</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.org} - <strong>组织架构模块</strong>
 *     <ul>
 *       <li>组织树结构管理</li>
 *       <li>组织成员管理</li>
 *       <li>懒加载查询优化</li>
 *       <li>权限过滤支持</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.auth} - <strong>权限授权模块</strong>
 *     <ul>
 *       <li>业务资源权限管理</li>
 *       <li>菜单权限管理</li>
 *       <li>批量授权操作</li>
 *       <li>权限检查和校验</li>
 *       <li>资源同步机制</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.relation} - <strong>关系管理模块</strong>
 *     <ul>
 *       <li>用户-组织关系</li>
 *       <li>角色-权限关系</li>
 *       <li>资源-组织关系</li>
 *       <li>权限继承关系</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <h3>安全认证模块</h3>
 * <ol start="6">
 *   <li>{@link io.dataease.api.permissions.login} - <strong>登录认证模块</strong>
 *     <ul>
 *       <li>用户名密码登录</li>
 *       <li>MFA 多因子认证</li>
 *       <li>第三方平台登录（LDAP、CAS、OIDC）</li>
 *       <li>账户锁定和解锁</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.apikey} - <strong>API Key 管理模块</strong>
 *     <ul>
 *       <li>API Key 生成和管理</li>
 *       <li>密钥状态控制</li>
 *       <li>外部 API 调用鉴权</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <h3>扩展功能模块</h3>
 * <ol start="8">
 *   <li>{@link io.dataease.api.permissions.dataset} - <strong>数据集权限模块</strong>
 *     <ul>
 *       <li>列级权限控制</li>
 *       <li>行级权限控制</li>
 *       <li>白名单用户管理</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.embedded} - <strong>嵌入式集成模块</strong>
 *     <ul>
 *       <li>嵌入式应用管理</li>
 *       <li>Token 生成和验证</li>
 *       <li>域名白名单控制</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.setting} - <strong>权限设置模块</strong>
 *     <ul>
 *       <li>基础认证设置</li>
 *       <li>MFA 多因素认证设置</li>
 *       <li>密码策略配置</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.variable} - <strong>系统变量模块</strong>
 *     <ul>
 *       <li>系统级变量定义</li>
 *       <li>用户级变量值</li>
 *       <li>变量在报表中的应用</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <h2>技术栈</h2>
 * <ul>
 *   <li><strong>框架</strong>：Spring Boot 3.3.0 + Spring MVC</li>
 *   <li><strong>API 文档</strong>：Knife4j (OpenAPI 3.0)</li>
 *   <li><strong>数据验证</strong>：Hibernate Validator</li>
 *   <li><strong>服务调用</strong>：OpenFeign (内部微服务调用)</li>
 *   <li><strong>JSON 序列化</strong>：Jackson</li>
 *   <li><strong>工具类</strong>：Lombok、Apache Commons</li>
 * </ul>
 *
 * <h2>设计原则</h2>
 *
 * <h3>1. 接口定义规范</h3>
 * <ul>
 *   <li><strong>RESTful 风格</strong>：使用标准的 HTTP 方法（GET、POST、PUT、DELETE）</li>
 *   <li><strong>统一返回值</strong>：使用 {@code ResultHolder} 封装响应数据</li>
 *   <li><strong>异常处理</strong>：统一的异常处理机制</li>
 *   <li><strong>API 文档</strong>：使用 Swagger 注解进行文档标注</li>
 * </ul>
 *
 * <h3>2. 数据传输对象规范</h3>
 * <ul>
 *   <li><strong>DTO (Data Transfer Object)</strong>：用于接收请求参数
 *     <ul>
 *       <li>请求类：XxxRequest（查询条件）</li>
 *       <li>创建类：XxxCreator（创建参数）</li>
 *       <li>编辑类：XxxEditor（编辑参数）</li>
 *     </ul>
 *   </li>
 *   <li><strong>VO (View Object)</strong>：用于返回响应数据
 *     <ul>
 *       <li>列表视图：XxxVO、XxxGridVO</li>
 *       <li>详情视图：XxxDetailVO</li>
 *       <li>树形视图：XxxTreeVO</li>
 *     </ul>
 *   </li>
 *   <li><strong>BO (Business Object)</strong>：用于内部业务处理
 *     <ul>
 *       <li>包含业务逻辑方法</li>
 *       <li>不直接暴露给外部</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>3. 命名规范</h3>
 * <ul>
 *   <li><strong>包命名</strong>：全小写，使用点号分隔（io.dataease.api.permissions.xxx）</li>
 *   <li><strong>类命名</strong>：大驼峰（PascalCase），如 UserCreator</li>
 *   <li><strong>方法命名</strong>：小驼峰（camelCase），如 createUser</li>
 *   <li><strong>常量命名</strong>：全大写，下划线分隔，如 MAX_LOGIN_ATTEMPTS</li>
 * </ul>
 *
 * <h2>实现位置</h2>
 *
 * <h3>单机版/社区版</h3>
 * <p>接口实现位于：{@code core/core-backend/src/main/java/io/dataease/}</p>
 * <ul>
 *   <li><strong>Controller</strong>：{@code commons/permission/controller/}</li>
 *   <li><strong>Service</strong>：{@code commons/permission/service/}</li>
 *   <li><strong>Mapper</strong>：{@code commons/permission/mapper/}</li>
 *   <li><strong>Entity</strong>：{@code commons/permission/model/}</li>
 * </ul>
 *
 * <h3>企业版/分布式版</h3>
 * <p>接口实现位于：{@code sdk/distributed/} 模块</p>
 * <ul>
 *   <li>通过 Feign 进行远程服务调用</li>
 *   <li>支持分布式部署和微服务架构</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>1. 用户登录流程</h3>
 * <pre>{@code
 * // 步骤 1: 用户名密码登录
 * POST /api/login
 * {
 *   "username": "admin",
 *   "password": "DataEase123456"  // 前端加密后的密码
 * }
 *
 * // 返回 JWT Token
 * {
 *   "code": 0,
 *   "data": {
 *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *     "exp": 1705123456789
 *   }
 * }
 *
 * // 步骤 2: 使用 Token 访问其他 API
 * GET /api/user/current
 * Headers:
 *   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 * }</pre>
 *
 * <h3>2. 完整的权限授权流程</h3>
 * <pre>{@code
 * // 场景：为角色"数据分析师"授予"销售数据集"的查看权限
 *
 * // 步骤 1: 查询资源树
 * GET /api/auth/resourceTree?busiFlag=dataset
 *
 * // 步骤 2: 查询当前权限
 * POST /api/auth/queryBusi
 * {
 *   "busiId": 1001,        // 销售数据集 ID
 *   "authTargetType": 1,   // 角色
 *   "targetId": 100        // 数据分析师角色 ID
 * }
 *
 * // 步骤 3: 保存权限
 * POST /api/auth/saveBusi
 * {
 *   "busiEditor": {
 *     "id": 1001,
 *     "authTargetType": 1,
 *     "targetIds": [100],
 *     "authType": 0,       // 使用权限
 *     "extraFlag": true,
 *     "columnPermissions": ["name", "price"],  // 列权限
 *     "rowPermissions": [...]                  // 行权限
 *   }
 * }
 *
 * // 步骤 4: 权限校验
 * POST /api/interactiveAuth/checkAuth
 * {
 *   "busiId": 1001,
 *   "authType": 0,
 *   "targetId": 1          // 菜单 ID
 * }
 * // 返回: true/false
 * }</pre>
 *
 * <h3>3. 组织架构与用户管理</h3>
 * <pre>{@code
 * // 创建组织
 * POST /api/org/create
 * {
 *   "name": "技术部",
 *   "pid": 0
 * }
 *
 * // 创建用户并分配到组织
 * POST /api/user/create
 * {
 *   "username": "zhangsan",
 *   "nickName": "张三",
 *   "email": "zhangsan@example.com",
 *   "password": "encrypted_password",
 *   "oid": 100  // 技术部组织 ID
 * }
 *
 * // 为用户分配角色
 * POST /api/role/mount
 * {
 *   "roleId": 10,
 *   "userIds": [200]
 * }
 * }</pre>
 *
 * <h2>权限体系说明</h2>
 *
 * <h3>权限类型</h3>
 * <table border="1" cellpadding="5" cellspacing="0">
 *   <tr>
 *     <th>权限类型</th>
 *     <th>值</th>
 *     <th>说明</th>
 *     <th>包含的操作</th>
 *   </tr>
 *   <tr>
 *     <td>使用权限</td>
 *     <td>0</td>
 *     <td>查看和使用资源</td>
 *     <td>查看、预览、导出</td>
 *   </tr>
 *   <tr>
 *     <td>管理权限</td>
 *     <td>1</td>
 *     <td>管理资源</td>
 *     <td>查看、编辑、删除、移动</td>
 *   </tr>
 *   <tr>
 *     <td>授权权限</td>
 *     <td>2</td>
 *     <td>授予权限给他人</td>
 *     <td>所有操作 + 授权</td>
 *   </tr>
 * </table>
 *
 * <h3>权限计算规则</h3>
 * <ol>
 *   <li><strong>多源合并</strong>：用户的最终权限 = 直接权限 ∪ 角色权限 ∪ 继承权限，取最高权限</li>
 *   <li><strong>继承规则</strong>：子资源继承父资源的权限，除非有明确的覆盖设置</li>
 *   <li><strong>显式优先</strong>：直接授予的权限优先级高于继承的权限</li>
 *   <li><strong>权限传递</strong>：授权权限可以传递，但有深度限制</li>
 * </ol>
 *
 * <h2>安全机制</h2>
 *
 * <h3>认证机制</h3>
 * <ul>
 *   <li><strong>JWT Token</strong>：基于 JSON Web Token 的无状态认证</li>
 *   <li><strong>密码加密</strong>：使用 BCrypt 算法加密存储</li>
 *   <li><strong>MFA 认证</strong>：支持 Google Authenticator、短信、邮箱验证码</li>
 *   <li><strong>账户锁定</strong>：连续登录失败自动锁定账户</li>
 * </ul>
 *
 * <h3>授权机制</h3>
 * <ul>
 *   <li><strong>RBAC 模型</strong>：基于角色的访问控制</li>
 *   <li><strong>细粒度权限</strong>：支持资源级、行级、列级权限控制</li>
 *   <li><strong>权限缓存</strong>：使用 Redis 缓存权限数据，提升性能</li>
 *   <li><strong>动态刷新</strong>：权限变更后自动刷新用户的权限缓存</li>
 * </ul>
 *
 * <h3>安全策略</h3>
 * <ul>
 *   <li><strong>最小权限原则</strong>：默认无权限，需要明确授权</li>
 *   <li><strong>权限分离</strong>：使用、管理、授权权限分离</li>
 *   <li><strong>审计日志</strong>：记录所有权限变更操作</li>
 *   <li><strong>敏感数据脱敏</strong>：密码、手机号等敏感信息脱敏处理</li>
 * </ul>
 *
 * <h2>性能优化</h2>
 * <ul>
 *   <li><strong>懒加载</strong>：组织树、资源树支持按需加载，避免一次性加载大量数据</li>
 *   <li><strong>权限缓存</strong>：用户权限数据缓存在 Redis 中，减少数据库查询</li>
 *   <li><strong>批量操作</strong>：支持批量授权、批量查询，减少网络开销</li>
 *   <li><strong>分页查询</strong>：列表查询支持分页，避免大数据量查询</li>
 *   <li><strong>索引优化</strong>：权限相关表建立合理的索引，提升查询效率</li>
 * </ul>
 *
 * <h2>API 文档</h2>
 * <p>在线 API 文档地址：</p>
 * <ul>
 *   <li><strong>开发环境</strong>：http://localhost:8081/doc.html</li>
 *   <li><strong>生产环境</strong>：https://your-domain/doc.html</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>本模块仅定义接口，不包含实现代码</li>
 *   <li>所有接口的实现位于 core-backend 或 distributed 模块</li>
 *   <li>修改接口定义时需要同步更新实现代码</li>
 *   <li>DTO/VO 类的字段变更需要考虑前后端兼容性</li>
 *   <li>权限变更操作需要记录审计日志</li>
 *   <li>删除资源时需要同步删除相关的权限数据</li>
 *   <li>批量操作需要使用事务保证数据一致性</li>
 * </ol>
 *
 * <h2>版本历史</h2>
 * <ul>
 *   <li><strong>v1.0.0</strong>：初始版本，提供基础的权限管理功能</li>
 *   <li><strong>v1.1.0</strong>：新增数据集行列权限控制</li>
 *   <li><strong>v1.2.0</strong>：新增嵌入式集成功能</li>
 *   <li><strong>v1.3.0</strong>：新增 MFA 多因子认证</li>
 *   <li><strong>v2.0.0</strong>：重构权限模型，支持更细粒度的权限控制</li>
 * </ul>
 *
 * @author DataEase Team
 * @version 2.0.0
 * @since 1.0.0
 */
package io.dataease.api.permissions;
