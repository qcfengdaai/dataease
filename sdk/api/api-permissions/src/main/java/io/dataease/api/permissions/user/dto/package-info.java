/**
 * 用户管理数据传输对象包
 *
 * <p>本包包含用户管理模块的所有 DTO (Data Transfer Object) 类,
 * 用于封装用户管理相关的请求参数和数据传输。</p>
 *
 * <h2>DTO 分类</h2>
 *
 * <h3>1. 用户管理类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.user.dto.UserCreator} - 用户创建请求
 *     <ul>
 *       <li>字段: account、name、email、phone、password、roleIds、enabled、orgId</li>
 *       <li>用途: 创建本地用户</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.user.dto.UserEditor} - 用户编辑请求
 *     <ul>
 *       <li>字段: id、name、email、phone、roleIds、enabled</li>
 *       <li>用途: 编辑用户基本信息和角色分配</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.user.dto.UserGridRequest} - 用户列表查询请求
 *     <ul>
 *       <li>字段: keyword、enabled、roleId、orgId</li>
 *       <li>用途: 分页查询用户列表,支持多种筛选条件</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.user.dto.PlatformUserCreator} - 第三方用户创建请求
 *     <ul>
 *       <li>字段: account、name、email、origin、platformId、roleIds</li>
 *       <li>用途: 创建来自第三方平台的用户</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>2. 账号管理类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.user.dto.ModifyPwdRequest} - 修改密码请求
 *     <ul>
 *       <li>字段: oldPassword、newPassword、confirmPassword</li>
 *       <li>用途: 用户修改个人密码</li>
 *       <li>安全: 需要验证旧密码,新密码需要确认</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.user.dto.EnableSwitchRequest} - 状态切换请求
 *     <ul>
 *       <li>字段: id、enabled</li>
 *       <li>用途: 启用或禁用用户账号</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.user.dto.LangSwitchRequest} - 语言切换请求
 *     <ul>
 *       <li>字段: language</li>
 *       <li>用途: 切换用户界面语言偏好</li>
 *       <li>支持: zh_CN(中文)、en_US(英文)等</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>3. 第三方集成类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.user.dto.UserBindRequest} - 用户绑定请求
 *     <ul>
 *       <li>字段: userId、origin、platformId、platformAccount</li>
 *       <li>用途: 用户自己绑定第三方账号</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.user.dto.AdminBindRequest} - 管理员绑定请求
 *     <ul>
 *       <li>字段: userId、origin、platformId、platformAccount</li>
 *       <li>用途: 管理员为用户绑定第三方账号</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.user.dto.UserReciRequest} - 消息接收人查询请求
 *     <ul>
 *       <li>字段: userIds、roleIds、orgIds</li>
 *       <li>用途: 查询消息通知的接收人列表</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 DTO 类实现 {@link java.io.Serializable} 接口</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化代码</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>字段命名遵循驼峰命名规范</li>
 *   <li>使用 JSR-303 验证注解确保数据合法性</li>
 * </ul>
 *
 * <h2>命名规范</h2>
 * <ul>
 *   <li><strong>Creator</strong>: 创建器类,用于封装创建操作的参数</li>
 *   <li><strong>Editor</strong>: 编辑器类,用于封装编辑操作的参数</li>
 *   <li><strong>Request</strong>: 请求类,用于封装查询或通用请求参数</li>
 *   <li><strong>XxxRequest</strong>: 特定功能的请求类</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 *
 * <h3>1. 创建用户</h3>
 * <pre>{@code
 * UserCreator creator = new UserCreator();
 * creator.setAccount("zhangsan");
 * creator.setName("张三");
 * creator.setEmail("zhangsan@example.com");
 * creator.setPhone("13800138000");
 * creator.setRoleIds(Arrays.asList(1001L, 1002L));
 * creator.setEnabled(1);
 *
 * Long userId = userApi.create(creator);
 * }</pre>
 *
 * <h3>2. 编辑用户</h3>
 * <pre>{@code
 * UserEditor editor = new UserEditor();
 * editor.setId(10001L);
 * editor.setName("张三三");
 * editor.setEmail("zhangsan_new@example.com");
 * editor.setRoleIds(Arrays.asList(1001L, 1002L, 1003L));
 *
 * userApi.edit(editor);
 * }</pre>
 *
 * <h3>3. 查询用户列表</h3>
 * <pre>{@code
 * UserGridRequest request = new UserGridRequest();
 * request.setKeyword("张");        // 搜索关键字
 * request.setEnabled(1);           // 只查询启用的用户
 * request.setRoleId(1001L);        // 筛选特定角色
 *
 * IPage<UserGridVO> page = userApi.pager(1, 20, request);
 * }</pre>
 *
 * <h3>4. 修改密码</h3>
 * <pre>{@code
 * ModifyPwdRequest request = new ModifyPwdRequest();
 * request.setOldPassword("old123456");
 * request.setNewPassword("new123456");
 * request.setConfirmPassword("new123456");
 *
 * userApi.modifyPwd(request);
 * }</pre>
 *
 * <h3>5. 切换用户状态</h3>
 * <pre>{@code
 * EnableSwitchRequest request = new EnableSwitchRequest();
 * request.setId(10001L);
 * request.setEnabled(0);  // 禁用用户
 *
 * userApi.enable(request);
 * }</pre>
 *
 * <h3>6. 绑定第三方账号</h3>
 * <pre>{@code
 * UserBindRequest request = new UserBindRequest();
 * request.setUserId(10001L);
 * request.setOrigin(2);  // OAuth
 * request.setPlatformId("github_123456");
 * request.setPlatformAccount("zhangsan_github");
 *
 * userApi.bind(request);
 * }</pre>
 *
 * <h2>字段验证</h2>
 * <p>DTO 类使用 JSR-303 验证注解确保数据合法性:</p>
 *
 * <h3>常用验证注解</h3>
 * <ul>
 *   <li>{@code @NotNull}: 字段不能为空</li>
 *   <li>{@code @NotBlank}: 字符串不能为空且不能只包含空白字符</li>
 *   <li>{@code @Size}: 限制字符串长度或集合大小</li>
 *   <li>{@code @Min/@Max}: 限制数值范围</li>
 *   <li>{@code @Pattern}: 正则表达式验证</li>
 *   <li>{@code @Email}: 邮箱格式验证</li>
 * </ul>
 *
 * <h3>验证示例</h3>
 * <pre>{@code
 * public class UserCreator {
 *     @NotBlank(message = "账号不能为空")
 *     @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "账号格式不正确")
 *     private String account;
 *
 *     @NotBlank(message = "姓名不能为空")
 *     @Size(max = 50, message = "姓名长度不能超过50个字符")
 *     private String name;
 *
 *     @Email(message = "邮箱格式不正确")
 *     private String email;
 *
 *     @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
 *     private String phone;
 *
 *     @Size(min = 8, max = 20, message = "密码长度必须在8-20个字符之间")
 *     private String password;
 *
 *     @NotEmpty(message = "至少需要分配一个角色")
 *     private List<Long> roleIds;
 * }
 * }</pre>
 *
 * <h2>密码安全</h2>
 * <p>密码相关 DTO 的安全处理:</p>
 * <ul>
 *   <li><strong>传输加密</strong>: 必须使用 HTTPS 传输密码</li>
 *   <li><strong>前端加密</strong>: 建议前端先进行一次加密(如 RSA)再传输</li>
 *   <li><strong>日志脱敏</strong>: 密码字段不应该出现在日志中</li>
 *   <li><strong>JSON 序列化</strong>: 使用 {@code @JsonIgnore} 防止密码序列化到日志</li>
 * </ul>
 *
 * <h3>密码字段处理</h3>
 * <pre>{@code
 * public class UserCreator {
 *     private String account;
 *
 *     @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
 *     private String password;  // 只允许写入,不允许读取
 *
 *     @Override
 *     public String toString() {
 *         return "UserCreator{" +
 *             "account='" + account + '\'' +
 *             ", password='******'" +  // 密码脱敏
 *             '}';
 *     }
 * }
 * }</pre>
 *
 * <h2>分页查询参数</h2>
 * <p>列表查询 DTO 的设计模式:</p>
 * <pre>{@code
 * public class UserGridRequest {
 *     // 搜索关键字:支持账号、姓名、邮箱模糊搜索
 *     private String keyword;
 *
 *     // 状态筛选
 *     private Integer enabled;
 *
 *     // 角色筛选
 *     private Long roleId;
 *
 *     // 组织筛选
 *     private Long orgId;
 *
 *     // 排序字段
 *     private String sortField = "createTime";
 *
 *     // 排序方向
 *     private String sortOrder = "desc";
 * }
 * }</pre>
 *
 * <h2>批量操作参数</h2>
 * <p>批量操作 DTO 的设计要点:</p>
 * <ul>
 *   <li>使用 List 或 Set 封装批量 ID</li>
 *   <li>限制批量操作的数量(如最多 100 条)</li>
 *   <li>提供批量操作的验证逻辑</li>
 * </ul>
 *
 * <h3>批量删除示例</h3>
 * <pre>{@code
 * // 批量删除用户
 * List<Long> userIds = Arrays.asList(10001L, 10002L, 10003L);
 * userApi.batchDel(userIds);
 * }</pre>
 *
 * <h2>最佳实践</h2>
 * <ul>
 *   <li><strong>参数校验</strong>: 在 Controller 层使用 {@code @Valid} 注解触发参数校验</li>
 *   <li><strong>空值处理</strong>: 对于可选字段,使用包装类型而不是基本类型</li>
 *   <li><strong>默认值</strong>: 在构造函数或字段初始化时设置合理的默认值</li>
 *   <li><strong>文档完善</strong>: 使用 {@code @Schema} 注解为每个字段提供清晰的说明</li>
 *   <li><strong>分组校验</strong>: 使用 {@code @Validated} 的分组功能实现不同场景的不同校验规则</li>
 * </ul>
 *
 * <h2>分组校验示例</h2>
 * <pre>{@code
 * public class UserEditor {
 *     @NotNull(groups = {Update.class})
 *     private Long id;
 *
 *     @NotBlank(groups = {Create.class, Update.class})
 *     private String name;
 *
 *     @NotBlank(groups = {Create.class})
 *     private String account;  // 创建时必填,编辑时不能修改
 * }
 *
 * // 控制器中使用
 * @PostMapping("/create")
 * public Long create(@Validated(Create.class) @RequestBody UserEditor editor) {
 *     return userService.create(editor);
 * }
 *
 * @PostMapping("/edit")
 * public void edit(@Validated(Update.class) @RequestBody UserEditor editor) {
 *     userService.edit(editor);
 * }
 * }</pre>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>DTO 对象只用于数据传输,不包含业务逻辑</li>
 *   <li>修改 DTO 结构时需要考虑前后端兼容性</li>
 *   <li>敏感信息(如密码)不应该在日志和响应中明文显示</li>
 *   <li>批量操作的 DTO 应该限制批量大小,避免单次操作数据量过大</li>
 *   <li>可选字段使用包装类型,必填字段可以使用基本类型</li>
 *   <li>日期时间字段建议使用 Long 类型的时间戳,方便前后端交互</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.user.api
 * @see io.dataease.api.permissions.user.vo
 * @see io.dataease.api.permissions.user.bo
 * @since 1.0
 */
package io.dataease.api.permissions.user.dto;
