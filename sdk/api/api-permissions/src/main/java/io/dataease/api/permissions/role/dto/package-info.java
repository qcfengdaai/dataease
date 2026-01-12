/**
 * 角色管理数据传输对象包
 *
 * <p>本包包含角色管理模块的所有 DTO (Data Transfer Object) 类,
 * 用于封装角色管理相关的请求参数和数据传输。</p>
 *
 * <h2>DTO 分类</h2>
 *
 * <h3>1. 角色管理类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.role.dto.RoleCreator} - 角色创建请求
 *     <ul>
 *       <li>字段: name(角色名称)、type(角色类型)、description(描述)</li>
 *       <li>用途: 创建新角色</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.role.dto.RoleEditor} - 角色编辑请求
 *     <ul>
 *       <li>字段: id(角色ID)、name(角色名称)、description(描述)</li>
 *       <li>用途: 编辑已有角色信息</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.role.dto.RoleRequest} - 角色查询请求
 *     <ul>
 *       <li>字段: keyword(关键字)、userId(用户ID)、orgId(组织ID)</li>
 *       <li>用途: 查询角色列表,支持多种筛选条件</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.role.dto.RoleCopyRequest} - 角色复制请求
 *     <ul>
 *       <li>字段: sourceRoleId(源角色ID)、name(新角色名称)、description(描述)</li>
 *       <li>用途: 复制已有角色及其权限配置</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>2. 用户绑定管理类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.role.dto.MountUserRequest} - 组织内用户绑定请求
 *     <ul>
 *       <li>字段: rid(角色ID)、userIds(用户ID列表)</li>
 *       <li>用途: 批量绑定组织内用户到角色</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.role.dto.MountExternalUserRequest} - 组织外用户绑定请求
 *     <ul>
 *       <li>字段: rid(角色ID)、externalUserId(外部用户ID)、source(来源)</li>
 *       <li>用途: 绑定组织外用户到角色,用于跨组织协作</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.role.dto.UnmountUserRequest} - 用户解绑请求
 *     <ul>
 *       <li>字段: rid(角色ID)、userId(用户ID)</li>
 *       <li>用途: 从角色中移除用户</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.role.dto.UserRequest} - 用户查询请求
 *     <ul>
 *       <li>字段: roleId(角色ID)、keyword(关键字)、orgId(组织ID)</li>
 *       <li>用途: 查询角色可绑定或已绑定的用户列表</li>
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
 *   <li>提供合理的默认值和字段验证注解</li>
 * </ul>
 *
 * <h2>命名规范</h2>
 * <ul>
 *   <li><strong>Creator</strong>: 创建器类,用于封装创建操作的参数</li>
 *   <li><strong>Editor</strong>: 编辑器类,用于封装编辑操作的参数</li>
 *   <li><strong>Request</strong>: 请求类,用于封装查询或通用请求参数</li>
 *   <li><strong>MountXxxRequest</strong>: 用户绑定请求类</li>
 *   <li><strong>UnmountXxxRequest</strong>: 用户解绑请求类</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 *
 * <h3>1. 创建角色</h3>
 * <pre>{@code
 * RoleCreator creator = new RoleCreator();
 * creator.setName("数据分析师");
 * creator.setType(1);
 * creator.setDescription("负责数据分析和报表制作");
 *
 * Long roleId = roleApi.create(creator);
 * }</pre>
 *
 * <h3>2. 编辑角色</h3>
 * <pre>{@code
 * RoleEditor editor = new RoleEditor();
 * editor.setId(1001L);
 * editor.setName("高级数据分析师");
 * editor.setDescription("负责复杂数据分析和报表制作");
 *
 * roleApi.edit(editor);
 * }</pre>
 *
 * <h3>3. 批量绑定用户</h3>
 * <pre>{@code
 * MountUserRequest request = new MountUserRequest();
 * request.setRid(1001L);
 * request.setUserIds(Arrays.asList(10L, 20L, 30L));
 *
 * roleApi.mountUser(request);
 * }</pre>
 *
 * <h3>4. 查询用户可选角色</h3>
 * <pre>{@code
 * RoleRequest request = new RoleRequest();
 * request.setUserId(10L);
 * request.setKeyword("分析");
 *
 * List<RoleVO> roles = roleApi.optionForUser(request);
 * }</pre>
 *
 * <h3>5. 复制角色</h3>
 * <pre>{@code
 * RoleCopyRequest request = new RoleCopyRequest();
 * request.setSourceRoleId(1001L);
 * request.setName("数据分析师副本");
 * request.setDescription("从原角色复制的新角色");
 *
 * roleApi.copy(request);
 * }</pre>
 *
 * <h2>字段验证</h2>
 * <p>DTO 类使用 JSR-303 验证注解确保数据合法性:</p>
 * <ul>
 *   <li>{@code @NotNull}: 字段不能为空</li>
 *   <li>{@code @NotBlank}: 字符串不能为空且不能只包含空白字符</li>
 *   <li>{@code @Size}: 限制字符串长度或集合大小</li>
 *   <li>{@code @Min/@Max}: 限制数值范围</li>
 *   <li>{@code @Pattern}: 正则表达式验证</li>
 * </ul>
 *
 * <h3>验证示例</h3>
 * <pre>{@code
 * public class RoleCreator {
 *     @NotBlank(message = "角色名称不能为空")
 *     @Size(max = 50, message = "角色名称长度不能超过50个字符")
 *     private String name;
 *
 *     @NotNull(message = "角色类型不能为空")
 *     @Min(value = 0, message = "角色类型不合法")
 *     @Max(value = 2, message = "角色类型不合法")
 *     private Integer type;
 * }
 * }</pre>
 *
 * <h2>最佳实践</h2>
 * <ul>
 *   <li><strong>参数校验</strong>: 在 Controller 层使用 {@code @Valid} 注解触发参数校验</li>
 *   <li><strong>空值处理</strong>: 对于可选字段,使用包装类型而不是基本类型</li>
 *   <li><strong>默认值</strong>: 在构造函数或字段初始化时设置合理的默认值</li>
 *   <li><strong>不可变对象</strong>: 对于只读的传输对象,考虑使用 {@code @Value} 注解创建不可变对象</li>
 *   <li><strong>文档完善</strong>: 使用 {@code @Schema} 注解为每个字段提供清晰的说明</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>DTO 对象只用于数据传输,不包含业务逻辑</li>
 *   <li>修改 DTO 结构时需要考虑前后端兼容性</li>
 *   <li>敏感信息(如密码)不应该在 DTO 中明文传输</li>
 *   <li>批量操作的 DTO 应该限制批量大小,避免单次操作数据量过大</li>
 *   <li>使用 Builder 模式可以提高 DTO 对象的构建灵活性</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.role.api
 * @see io.dataease.api.permissions.role.vo
 * @since 1.0
 */
package io.dataease.api.permissions.role.dto;
