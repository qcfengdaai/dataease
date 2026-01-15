/**
 * 角色管理视图对象包
 *
 * <p>本包包含角色管理模块的所有 VO (Value Object / View Object) 类,
 * 用于封装返回给前端的角色数据视图。</p>
 *
 * <h2>VO 分类</h2>
 *
 * <h3>1. 角色视图类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.role.vo.RoleVO} - 角色基本信息视图
 *     <ul>
 *       <li>字段: id、name、type、description、createTime、creator、orgId</li>
 *       <li>用途: 角色列表展示,下拉选择等场景</li>
 *       <li>特点: 轻量级,只包含基本信息</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.role.vo.RoleDetailVO} - 角色详细信息视图
 *     <ul>
 *       <li>字段: 继承自 RoleVO,额外包含 userCount、permissions、menuPermissions</li>
 *       <li>用途: 角色详情页面,角色编辑表单</li>
 *       <li>特点: 包含完整信息,包括权限配置和统计数据</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>2. 用户视图类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.role.vo.ExternalUserVO} - 组织外用户视图
 *     <ul>
 *       <li>字段: userId、account、name、email、orgName、orgId</li>
 *       <li>用途: 组织外用户搜索和选择</li>
 *       <li>特点: 包含用户和所属组织信息,用于跨组织协作</li>
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
 * </ul>
 *
 * <h2>与 DTO 的区别</h2>
 * <table border="1">
 *   <tr>
 *     <th>对比项</th>
 *     <th>DTO (Data Transfer Object)</th>
 *     <th>VO (View Object)</th>
 *   </tr>
 *   <tr>
 *     <td>数据流向</td>
 *     <td>客户端 → 服务端</td>
 *     <td>服务端 → 客户端</td>
 *   </tr>
 *   <tr>
 *     <td>主要用途</td>
 *     <td>接收请求参数</td>
 *     <td>返回响应数据</td>
 *   </tr>
 *   <tr>
 *     <td>字段验证</td>
 *     <td>需要验证注解</td>
 *     <td>不需要验证</td>
 *   </tr>
 *   <tr>
 *     <td>可变性</td>
 *     <td>可变(有 setter)</td>
 *     <td>不可变(推荐只有 getter)</td>
 *   </tr>
 *   <tr>
 *     <td>业务逻辑</td>
 *     <td>不包含</td>
 *     <td>不包含</td>
 *   </tr>
 * </table>
 *
 * <h2>使用场景</h2>
 *
 * <h3>1. 角色列表展示</h3>
 * <pre>{@code
 * // 接口返回
 * List<RoleVO> roles = roleApi.query(request);
 *
 * // 前端展示
 * roles.forEach(role -> {
 *     System.out.println("角色: " + role.getName());
 *     System.out.println("类型: " + role.getType());
 *     System.out.println("创建时间: " + role.getCreateTime());
 * });
 * }</pre>
 *
 * <h3>2. 角色详情展示</h3>
 * <pre>{@code
 * // 接口返回
 * RoleDetailVO detail = roleApi.detail(roleId);
 *
 * // 前端展示
 * System.out.println("角色名称: " + detail.getName());
 * System.out.println("角色描述: " + detail.getDescription());
 * System.out.println("用户数量: " + detail.getUserCount());
 * System.out.println("权限配置: " + detail.getPermissions());
 * }</pre>
 *
 * <h3>3. 组织外用户搜索</h3>
 * <pre>{@code
 * // 接口返回
 * ExternalUserVO externalUser = roleApi.searchExternalUser("zhang");
 *
 * // 前端展示
 * System.out.println("用户账号: " + externalUser.getAccount());
 * System.out.println("用户姓名: " + externalUser.getName());
 * System.out.println("所属组织: " + externalUser.getOrgName());
 * }</pre>
 *
 * <h2>字段说明</h2>
 *
 * <h3>RoleVO 字段说明</h3>
 * <ul>
 *   <li><strong>id</strong>: 角色唯一标识</li>
 *   <li><strong>name</strong>: 角色名称,用于显示</li>
 *   <li><strong>type</strong>: 角色类型 (0:系统角色, 1:自定义角色)</li>
 *   <li><strong>description</strong>: 角色描述说明</li>
 *   <li><strong>createTime</strong>: 创建时间戳</li>
 *   <li><strong>creator</strong>: 创建人账号</li>
 *   <li><strong>orgId</strong>: 所属组织ID</li>
 * </ul>
 *
 * <h3>RoleDetailVO 额外字段说明</h3>
 * <ul>
 *   <li><strong>userCount</strong>: 拥有此角色的用户数量</li>
 *   <li><strong>permissions</strong>: 业务权限配置列表</li>
 *   <li><strong>menuPermissions</strong>: 菜单权限配置列表</li>
 * </ul>
 *
 * <h3>ExternalUserVO 字段说明</h3>
 * <ul>
 *   <li><strong>userId</strong>: 用户ID</li>
 *   <li><strong>account</strong>: 用户账号</li>
 *   <li><strong>name</strong>: 用户姓名</li>
 *   <li><strong>email</strong>: 用户邮箱</li>
 *   <li><strong>orgName</strong>: 所属组织名称</li>
 *   <li><strong>orgId</strong>: 所属组织ID</li>
 * </ul>
 *
 * <h2>数据脱敏</h2>
 * <p>VO 对象在返回给前端前,应该对敏感信息进行脱敏处理:</p>
 * <ul>
 *   <li><strong>手机号</strong>: 中间4位显示为 ****</li>
 *   <li><strong>邮箱</strong>: 部分字符显示为 ***</li>
 *   <li><strong>密码</strong>: 绝不返回密码字段</li>
 *   <li><strong>内部ID</strong>: 敏感的内部标识可以转换为业务ID</li>
 * </ul>
 *
 * <h2>JSON 序列化</h2>
 * <p>VO 对象使用 Jackson 进行 JSON 序列化,可以使用注解控制序列化行为:</p>
 * <ul>
 *   <li>{@code @JsonProperty}: 自定义字段名</li>
 *   <li>{@code @JsonIgnore}: 忽略字段,不序列化到 JSON</li>
 *   <li>{@code @JsonFormat}: 格式化日期、数字等</li>
 *   <li>{@code @JsonInclude}: 控制 null 值字段是否包含</li>
 * </ul>
 *
 * <h3>序列化示例</h3>
 * <pre>{@code
 * public class RoleVO {
 *     private Long id;
 *     private String name;
 *
 *     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
 *     private Date createTime;
 *
 *     @JsonIgnore
 *     private String internalFlag;  // 内部标识,不返回给前端
 *
 *     @JsonInclude(JsonInclude.Include.NON_NULL)
 *     private String description;   // null 时不包含在 JSON 中
 * }
 * }</pre>
 *
 * <h2>最佳实践</h2>
 * <ul>
 *   <li><strong>按需返回</strong>: 根据前端需求选择返回字段,避免返回不必要的数据</li>
 *   <li><strong>分层设计</strong>: 基础视图(简要信息)、详细视图(完整信息)分开设计</li>
 *   <li><strong>统一格式</strong>: 日期、金额、枚举等字段使用统一的格式</li>
 *   <li><strong>性能考虑</strong>: 列表查询返回轻量级 VO,详情查询返回完整 VO</li>
 *   <li><strong>版本兼容</strong>: 字段变更时保持向后兼容,使用 {@code @Deprecated} 标记废弃字段</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>VO 对象不应该包含业务逻辑,只负责数据展示</li>
 *   <li>避免在 VO 中暴露数据库表结构和内部实现细节</li>
 *   <li>时间戳字段建议使用 Long 类型,方便前端处理</li>
 *   <li>集合类型字段建议初始化为空集合,避免返回 null</li>
 *   <li>修改 VO 结构时需要考虑 API 版本兼容性</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.role.api
 * @see io.dataease.api.permissions.role.dto
 * @since 1.0
 */
package io.dataease.api.permissions.role.vo;
