/**
 * 用户管理视图对象包
 *
 * <p>本包包含用户管理模块的所有 VO (Value Object / View Object) 类,
 * 用于封装返回给前端的用户数据视图。</p>
 *
 * <h2>VO 分类</h2>
 *
 * <h3>1. 列表视图类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.user.vo.UserGridVO} - 用户列表网格视图
 *     <ul>
 *       <li>字段: id、account、name、email、phone、enabled、createTime、roles</li>
 *       <li>用途: 用户管理页面的列表展示</li>
 *       <li>特点: 包含角色信息,用于列表展示</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.user.vo.UserItemVO} - 用户简要信息视图
 *     <ul>
 *       <li>字段: id、account、name、email</li>
 *       <li>用途: 下拉选择、用户列表等简单场景</li>
 *       <li>特点: 轻量级,只包含基本标识信息</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.user.vo.UserItem} - 用户选项视图
 *     <ul>
 *       <li>字段: id、name、account</li>
 *       <li>用途: 最简化的用户选项,如搜索结果</li>
 *       <li>特点: 最小数据集</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>2. 表单视图类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.user.vo.UserFormVO} - 用户表单视图
 *     <ul>
 *       <li>字段: id、account、name、email、phone、enabled、roleIds、orgId、createTime</li>
 *       <li>用途: 用户编辑表单的数据回显</li>
 *       <li>特点: 包含所有可编辑字段</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>3. 当前用户视图类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.user.vo.CurUserVO} - 当前登录用户视图
 *     <ul>
 *       <li>字段: id、account、name、avatar、email、phone、orgId、orgName、roles、permissions</li>
 *       <li>用途: 前端获取当前登录用户的完整信息</li>
 *       <li>特点: 包含权限信息,用于前端权限控制</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.user.vo.CurIpVO} - 客户端IP信息视图
 *     <ul>
 *       <li>字段: ip、location、browser、os</li>
 *       <li>用途: 显示当前客户端的访问信息</li>
 *       <li>特点: 用于安全审计和用户提示</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>4. 角色关联视图类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.user.vo.UserGridRoleItem} - 用户角色信息项
 *     <ul>
 *       <li>字段: roleId、roleName、roleType</li>
 *       <li>用途: 在用户列表中展示用户的角色信息</li>
 *       <li>特点: 嵌套在 UserGridVO 中使用</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>5. 导入相关视图类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.user.vo.UserImportVO} - 用户导入结果视图
 *     <ul>
 *       <li>字段: successCount、failCount、errorKey</li>
 *       <li>用途: 返回批量导入的结果统计</li>
 *       <li>特点: errorKey 用于下载失败记录</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>VO 对象是只读的,不应该提供 setter 方法(推荐使用 {@code @Value} 或只提供 getter)</li>
 *   <li>使用 Lombok 的 {@code @Data} 或 {@code @Value} 注解简化代码</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>字段命名遵循驼峰命名规范</li>
 *   <li>根据展示需求选择合适的字段,避免过度暴露内部数据</li>
 *   <li>敏感信息必须脱敏或不返回</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 *
 * <h3>1. 用户列表展示</h3>
 * <pre>{@code
 * // 接口返回
 * IPage<UserGridVO> page = userApi.pager(1, 20, request);
 *
 * // 前端展示
 * page.getRecords().forEach(user -> {
 *     System.out.println("账号: " + user.getAccount());
 *     System.out.println("姓名: " + user.getName());
 *     System.out.println("状态: " + (user.getEnabled() == 1 ? "启用" : "禁用"));
 *     System.out.println("角色: " + user.getRoles().stream()
 *         .map(UserGridRoleItem::getRoleName)
 *         .collect(Collectors.joining(", ")));
 * });
 * }</pre>
 *
 * <h3>2. 用户编辑表单回显</h3>
 * <pre>{@code
 * // 接口返回
 * UserFormVO user = userApi.queryById(userId);
 *
 * // 前端表单回显
 * form.setFieldsValue({
 *   account: user.getAccount(),
 *   name: user.getName(),
 *   email: user.getEmail(),
 *   phone: user.getPhone(),
 *   roleIds: user.getRoleIds(),
 *   enabled: user.getEnabled()
 * });
 * }</pre>
 *
 * <h3>3. 获取当前登录用户信息</h3>
 * <pre>{@code
 * // 接口返回
 * CurUserVO currentUser = userApi.info();
 *
 * // 前端使用
 * console.log("欢迎: " + currentUser.getName());
 * console.log("组织: " + currentUser.getOrgName());
 *
 * // 权限控制
 * if (currentUser.getPermissions().contains("user:create")) {
 *     // 显示创建用户按钮
 * }
 * }</pre>
 *
 * <h3>4. 用户下拉选择</h3>
 * <pre>{@code
 * // 接口返回
 * List<UserItemVO> users = userApi.optionForOrg();
 *
 * // 前端下拉框
 * <Select>
 *   {users.map(user => (
 *     <Option key={user.id} value={user.id}>
 *       {user.name} ({user.account})
 *     </Option>
 *   ))}
 * </Select>
 * }</pre>
 *
 * <h3>5. 批量导入结果处理</h3>
 * <pre>{@code
 * // 接口返回
 * UserImportVO result = userApi.batchImport(file);
 *
 * // 前端提示
 * if (result.getFailCount() > 0) {
 *     message.warning(
 *         `导入完成! 成功: ${result.getSuccessCount()}, 失败: ${result.getFailCount()}`
 *     );
 *     // 提供下载失败记录的链接
 *     downloadErrorRecord(result.getErrorKey());
 * } else {
 *     message.success(`导入成功! 共导入 ${result.getSuccessCount()} 个用户`);
 * }
 * }</pre>
 *
 * <h2>字段说明</h2>
 *
 * <h3>UserGridVO 字段说明</h3>
 * <ul>
 *   <li><strong>id</strong>: 用户唯一标识</li>
 *   <li><strong>account</strong>: 登录账号</li>
 *   <li><strong>name</strong>: 用户姓名</li>
 *   <li><strong>email</strong>: 邮箱地址</li>
 *   <li><strong>phone</strong>: 手机号码</li>
 *   <li><strong>enabled</strong>: 启用状态 (1:启用, 0:禁用)</li>
 *   <li><strong>createTime</strong>: 创建时间戳</li>
 *   <li><strong>roles</strong>: 角色列表,包含角色ID、名称、类型</li>
 * </ul>
 *
 * <h3>CurUserVO 字段说明</h3>
 * <ul>
 *   <li><strong>id</strong>: 用户ID</li>
 *   <li><strong>account</strong>: 账号</li>
 *   <li><strong>name</strong>: 姓名</li>
 *   <li><strong>avatar</strong>: 头像URL</li>
 *   <li><strong>email</strong>: 邮箱</li>
 *   <li><strong>phone</strong>: 手机号</li>
 *   <li><strong>orgId</strong>: 当前组织ID</li>
 *   <li><strong>orgName</strong>: 当前组织名称</li>
 *   <li><strong>roles</strong>: 角色列表</li>
 *   <li><strong>permissions</strong>: 权限标识列表,用于前端权限控制</li>
 * </ul>
 *
 * <h3>UserImportVO 字段说明</h3>
 * <ul>
 *   <li><strong>successCount</strong>: 导入成功的数量</li>
 *   <li><strong>failCount</strong>: 导入失败的数量</li>
 *   <li><strong>errorKey</strong>: 错误记录的下载key,用于下载失败记录</li>
 * </ul>
 *
 * <h2>数据脱敏</h2>
 * <p>VO 对象在返回给前端前,应该对敏感信息进行脱敏处理:</p>
 *
 * <h3>手机号脱敏</h3>
 * <pre>{@code
 * public class UserGridVO {
 *     private String phone;
 *
 *     public String getMaskedPhone() {
 *         if (phone != null && phone.length() == 11) {
 *             return phone.substring(0, 3) + "****" + phone.substring(7);
 *         }
 *         return phone;
 *     }
 * }
 *
 * // 示例: 13800138000 → 138****8000
 * }</pre>
 *
 * <h3>邮箱脱敏</h3>
 * <pre>{@code
 * public class UserGridVO {
 *     private String email;
 *
 *     public String getMaskedEmail() {
 *         if (email != null && email.contains("@")) {
 *             String[] parts = email.split("@");
 *             String username = parts[0];
 *             if (username.length() > 3) {
 *                 username = username.substring(0, 3) + "***";
 *             }
 *             return username + "@" + parts[1];
 *         }
 *         return email;
 *     }
 * }
 *
 * // 示例: zhangsan@example.com → zha***@example.com
 * }</pre>
 *
 * <h2>JSON 序列化</h2>
 * <p>VO 对象使用 Jackson 进行 JSON 序列化,可以使用注解控制序列化行为:</p>
 *
 * <h3>日期格式化</h3>
 * <pre>{@code
 * public class UserGridVO {
 *     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
 *     private Date createTime;
 *
 *     // 或使用时间戳
 *     private Long createTime;  // 前端处理为日期格式
 * }
 * }</pre>
 *
 * <h3>空值处理</h3>
 * <pre>{@code
 * public class UserGridVO {
 *     @JsonInclude(JsonInclude.Include.NON_NULL)
 *     private String email;  // email 为 null 时不包含在 JSON 中
 *
 *     @JsonInclude(JsonInclude.Include.NON_EMPTY)
 *     private List<UserGridRoleItem> roles;  // 空列表时不包含在 JSON 中
 * }
 * }</pre>
 *
 * <h3>字段别名</h3>
 * <pre>{@code
 * public class UserGridVO {
 *     @JsonProperty("userId")
 *     private Long id;  // JSON 中字段名为 userId
 *
 *     @JsonProperty("userName")
 *     private String name;  // JSON 中字段名为 userName
 * }
 * }</pre>
 *
 * <h2>嵌套对象</h2>
 * <p>复杂视图可以嵌套其他 VO 对象:</p>
 * <pre>{@code
 * public class UserGridVO {
 *     private Long id;
 *     private String account;
 *     private String name;
 *
 *     // 嵌套角色信息列表
 *     private List<UserGridRoleItem> roles;
 * }
 *
 * public class UserGridRoleItem {
 *     private Long roleId;
 *     private String roleName;
 *     private Integer roleType;
 * }
 *
 * // JSON 输出:
 * {
 *   "id": 10001,
 *   "account": "zhangsan",
 *   "name": "张三",
 *   "roles": [
 *     {"roleId": 1001, "roleName": "数据分析师", "roleType": 1},
 *     {"roleId": 1002, "roleName": "报表管理员", "roleType": 1}
 *   ]
 * }
 * }</pre>
 *
 * <h2>最佳实践</h2>
 * <ul>
 *   <li><strong>按需返回</strong>: 根据前端需求选择返回字段,避免返回不必要的数据</li>
 *   <li><strong>分层设计</strong>: 列表视图(简要信息)、表单视图(完整信息)分开设计</li>
 *   <li><strong>统一格式</strong>: 日期、枚举等字段使用统一的格式</li>
 *   <li><strong>性能考虑</strong>: 列表查询返回轻量级 VO,详情查询返回完整 VO</li>
 *   <li><strong>敏感信息脱敏</strong>: 手机号、邮箱、身份证等敏感信息必须脱敏</li>
 *   <li><strong>空值友好</strong>: 集合类型字段初始化为空集合,避免返回 null</li>
 *   <li><strong>文档完善</strong>: 使用 {@code @Schema} 注解为每个字段提供清晰说明</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>VO 对象不应该包含业务逻辑,只负责数据展示</li>
 *   <li>绝不能返回密码等敏感信息</li>
 *   <li>时间字段推荐使用 Long 类型的时间戳,方便前端处理</li>
 *   <li>集合类型字段建议初始化为空集合,避免返回 null</li>
 *   <li>修改 VO 结构时需要考虑 API 版本兼容性</li>
 *   <li>枚举值建议同时返回 code 和 name,方便前端展示</li>
 *   <li>ID 字段统一使用 Long 类型,避免前端 JavaScript 精度丢失问题</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.user.api
 * @see io.dataease.api.permissions.user.dto
 * @see io.dataease.api.permissions.user.bo
 * @since 1.0
 */
package io.dataease.api.permissions.user.vo;
