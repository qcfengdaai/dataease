/**
 * 权限设置接口层
 *
 * <p>本包定义了权限和认证相关的系统配置接口，遵循 Spring MVC 规范。
 * 接口使用 OpenAPI 3.0 注解进行文档标注，支持 Knife4j 在线文档。
 * 所有接口标记为 {@code @XpackResource}，属于企业版功能。</p>
 *
 * <h2>接口列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.setting.api.PerSettingApi} - 权限设置核心接口</li>
 * </ul>
 *
 * <h2>实现类位置</h2>
 * <p>接口实现类位于企业版（distributed）模块的 controller 包中，通过 Spring 的 {@code @RestController} 注解标注。</p>
 *
 * <h2>访问路径</h2>
 * <p>基础路径：{@code /api/setting}</p>
 * <ul>
 *   <li>基础认证设置：{@code /api/setting/basic/*}</li>
 *   <li>MFA 设置：{@code /api/setting/mfa/*}</li>
 * </ul>
 *
 * <h2>权限控制</h2>
 * <p>权限设置接口通常需要系统管理员权限：</p>
 * <ul>
 *   <li><strong>查询操作</strong>: 需要管理员权限或特定角色权限</li>
 *   <li><strong>修改操作</strong>: 必须具有系统管理员权限</li>
 *   <li><strong>MFA 状态查询</strong>: 所有登录用户可访问（用于前端功能判断）</li>
 * </ul>
 *
 * <h2>接口分类</h2>
 *
 * <h3>1. 基础认证设置接口</h3>
 * <ul>
 *   <li>{@code basicSetting()} - 查询基础认证配置项列表</li>
 *   <li>{@code saveBasic()} - 批量保存基础认证配置</li>
 *   <li>{@code singleValue()} - 查询单个配置项的值（内部使用）</li>
 * </ul>
 *
 * <h3>2. MFA（多因素认证）设置接口</h3>
 * <ul>
 *   <li>{@code mfaSetting()} - 查询 MFA 配置项列表</li>
 *   <li>{@code saveMfa()} - 批量保存 MFA 配置</li>
 *   <li>{@code mfaStatus()} - 快速查询 MFA 启用状态</li>
 * </ul>
 *
 * <h2>典型调用流程</h2>
 *
 * <h3>场景 1: 管理员修改密码策略</h3>
 * <pre>
 * 1. 管理员进入系统设置页面
 *    └─> 前端路由: /settings/auth
 *
 * 2. 前端加载当前配置
 *    └─> GET /api/setting/basic/query
 *    └─> 返回当前所有基础认证配置
 *
 * 3. 管理员修改配置
 *    ├─> 修改密码最小长度：8 → 10
 *    ├─> 启用特殊字符要求：false → true
 *    └─> 修改会话超时：30 → 15 分钟
 *
 * 4. 前端提交修改
 *    └─> POST /api/setting/baisc/save
 *    └─> Request Body: [修改后的配置项列表]
 *
 * 5. 后端保存配置
 *    ├─> 验证管理员权限
 *    ├─> 验证配置项合法性
 *    ├─> 保存到数据库
 *    └─> 刷新缓存
 *
 * 6. 配置生效
 *    ├─> 新用户修改密码时：立即应用新策略
 *    └─> 现有会话：按旧超时时间，新会话按新超时时间
 * </pre>
 *
 * <h3>场景 2: 启用 MFA</h3>
 * <pre>
 * 1. 管理员决定启用 MFA
 *    └─> 进入 MFA 设置页面: /settings/mfa
 *
 * 2. 前端加载 MFA 配置
 *    └─> GET /api/setting/mfa/query
 *    └─> 返回当前 MFA 配置
 *
 * 3. 管理员配置 MFA
 *    ├─> 启用 MFA: false → true
 *    ├─> 选择认证方式: totp, email
 *    └─> 不强制（先让用户适应）: enforce = false
 *
 * 4. 前端提交配置
 *    └─> POST /api/setting/mfa/save
 *
 * 5. 配置生效
 *    ├─> 用户登录时提示可以绑定 MFA
 *    └─> 但暂时不强制绑定
 *
 * 6. 一段时间后，强制特定角色启用
 *    ├─> 修改配置: enforce.roles = "admin,manager"
 *    └─> 管理员和经理角色用户下次登录时必须绑定 MFA
 *
 * 7. 最终全面强制
 *    ├─> 修改配置: enforce = true
 *    └─> 所有用户必须绑定 MFA 才能登录
 * </pre>
 *
 * <h3>场景 3: 服务内部读取配置</h3>
 * <pre>
 * 1. 用户尝试修改密码
 *    └─> PUT /api/user/password
 *
 * 2. Controller 调用 Service
 *    └─> userService.changePassword(newPassword)
 *
 * 3. Service 获取密码策略配置
 *    ├─> String minLength = perSettingApi.singleValue("password.min.length")
 *    ├─> String requireSpecial = perSettingApi.singleValue("password.require.special.char")
 *    └─> String requireNumber = perSettingApi.singleValue("password.require.number")
 *
 * 4. Service 验证新密码
 *    ├─> 检查长度: newPassword.length() >= Integer.parseInt(minLength)
 *    ├─> 检查特殊字符: "true".equals(requireSpecial) && 包含特殊字符
 *    └─> 检查数字: "true".equals(requireNumber) && 包含数字
 *
 * 5. 验证通过后保存
 *    └─> 加密密码并保存到数据库
 * </pre>
 *
 * <h3>场景 4: 前端判断是否显示 MFA 功能</h3>
 * <pre>
 * 1. 用户访问系统
 *    └─> 前端路由: /dashboard
 *
 * 2. 前端查询 MFA 状态
 *    └─> GET /api/setting/mfaStatus
 *    └─> 返回: 2 (MFA 已启用且强制)
 *
 * 3. 前端根据状态决定行为
 *    ├─> 状态 0: 不显示 MFA 相关功能
 *    ├─> 状态 1: 显示 MFA 绑定入口，用户可选择绑定
 *    └─> 状态 2: 检查用户是否已绑定
 *        ├─> 已绑定: 正常使用
 *        └─> 未绑定: 重定向到 MFA 绑定页面，强制绑定
 * </pre>
 *
 * <h2>数据存储</h2>
 * <p>配置数据通常存储在系统配置表中：</p>
 * <pre>
 * core_sys_setting (系统配置表)
 * ├── id              // 主键
 * ├── key             // 配置项 key（唯一）
 * ├── value           // 配置项 value（字符串形式）
 * ├── type            // 配置项类型（STRING, INTEGER, BOOLEAN 等）
 * ├── category        // 配置分类（basic, mfa 等）
 * ├── description     // 配置说明
 * ├── create_time     // 创建时间
 * └── update_time     // 更新时间
 * </pre>
 *
 * <h2>缓存策略</h2>
 * <p>配置项通常会缓存以提升性能：</p>
 * <ul>
 *   <li><strong>缓存位置</strong>: Redis 或本地缓存</li>
 *   <li><strong>缓存 key</strong>: {@code sys:setting:{category}:{key}}</li>
 *   <li><strong>缓存时间</strong>: 长期缓存（配置很少变更）</li>
 *   <li><strong>缓存刷新</strong>: 配置修改时主动清除缓存</li>
 * </ul>
 * <pre>{@code
 * @Cacheable(value = "sys-setting", key = "#category + ':' + #key")
 * public String getSetting(String category, String key) {
 *     return settingMapper.selectValueByCategoryAndKey(category, key);
 * }
 *
 * @CacheEvict(value = "sys-setting", allEntries = true)
 * public void saveSetting(List<PerSettingItemVO> settings) {
 *     // 保存配置
 *     settingMapper.batchUpdate(settings);
 * }
 * }</pre>
 *
 * <h2>安全注意事项</h2>
 * <ol>
 *   <li><strong>权限严格控制</strong>：只有系统管理员可以修改配置</li>
 *   <li><strong>操作审计</strong>：记录所有配置修改的审计日志
 *       <pre>
 * 审计日志示例：
 * - 操作人: admin@example.com
 * - 操作时间: 2025-01-12 10:30:00
 * - 操作类型: 修改基础认证设置
 * - 变更内容: password.min.length: 8 → 10
 *       </pre>
 *   </li>
 *   <li><strong>配置验证</strong>：后端必须验证配置项的合法性
 *       <ul>
 *         <li>密码最小长度不能小于 6</li>
 *         <li>会话超时不能小于 5 分钟</li>
 *         <li>登录失败锁定次数不能小于 3</li>
 *       </ul>
 *   </li>
 *   <li><strong>影响通知</strong>：重大配置变更应通知所有用户</li>
 *   <li><strong>回滚能力</strong>：保留配置历史记录，支持快速回滚</li>
 * </ol>
 *
 * <h2>性能优化</h2>
 * <ul>
 *   <li>配置项缓存，避免频繁查询数据库</li>
 *   <li>{@code singleValue()} 接口标记为 {@code @Hidden}，避免暴露给前端直接调用</li>
 *   <li>批量查询接口优于多次单独查询</li>
 *   <li>配置变更后异步刷新缓存</li>
 * </ul>
 *
 * <h2>版本兼容性</h2>
 * <p>本模块标记为 {@code @XpackResource}，仅在企业版中可用：</p>
 * <ul>
 *   <li><strong>企业版（Distributed）</strong>：完整功能支持</li>
 *   <li><strong>社区版（Standalone）</strong>：可能提供简化版或不支持</li>
 * </ul>
 *
 * <h2>扩展接口</h2>
 * <p>如果需要支持新的配置类别，按以下步骤扩展：</p>
 * <ol>
 *   <li>在 PerSettingApi 中添加新方法，如 {@code xxxSetting()}、{@code saveXxx()}</li>
 *   <li>在实现类中实现该方法</li>
 *   <li>在数据库中添加对应的配置记录</li>
 *   <li>更新前端设置页面</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.setting.vo
 * @since 1.0
 */
package io.dataease.api.permissions.setting.api;
