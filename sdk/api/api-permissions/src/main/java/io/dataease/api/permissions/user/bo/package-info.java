/**
 * 用户管理业务对象包
 *
 * <p>本包包含用户管理模块的所有 BO (Business Object) 类,
 * 用于封装业务逻辑处理过程中的数据对象。</p>
 *
 * <h2>BO 分类</h2>
 *
 * <h3>1. 第三方平台用户对象</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.user.bo.PlatformUser} - 第三方平台用户业务对象
 *     <ul>
 *       <li>用途: 封装第三方平台用户的完整信息</li>
 *       <li>包含字段: userId、account、name、email、phone、origin、platformId</li>
 *       <li>使用场景: 第三方登录、用户同步、账号绑定</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>BO 与 DTO、VO 的区别</h2>
 * <table border="1">
 *   <tr>
 *     <th>对比项</th>
 *     <th>DTO</th>
 *     <th>BO</th>
 *     <th>VO</th>
 *   </tr>
 *   <tr>
 *     <td>数据流向</td>
 *     <td>客户端 → 服务端</td>
 *     <td>服务内部</td>
 *     <td>服务端 → 客户端</td>
 *   </tr>
 *   <tr>
 *     <td>主要用途</td>
 *     <td>接收请求参数</td>
 *     <td>业务逻辑处理</td>
 *     <td>返回响应数据</td>
 *   </tr>
 *   <tr>
 *     <td>业务逻辑</td>
 *     <td>不包含</td>
 *     <td>可以包含简单逻辑</td>
 *     <td>不包含</td>
 *   </tr>
 *   <tr>
 *     <td>使用层次</td>
 *     <td>Controller 层</td>
 *     <td>Service 层</td>
 *     <td>Controller 层</td>
 *   </tr>
 * </table>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>BO 对象用于封装复杂的业务数据和业务规则</li>
 *   <li>BO 可以包含简单的业务逻辑方法</li>
 *   <li>BO 通常在 Service 层内部使用,不直接暴露给外部</li>
 *   <li>使用 Lombok 的 {@code @Data} 或 {@code @Builder} 注解简化代码</li>
 *   <li>BO 对象可以聚合多个实体的数据</li>
 * </ul>
 *
 * <h2>PlatformUser 详细说明</h2>
 *
 * <h3>字段说明</h3>
 * <ul>
 *   <li><strong>userId</strong>: 系统内用户ID,如果用户已存在</li>
 *   <li><strong>account</strong>: 第三方平台的账号</li>
 *   <li><strong>name</strong>: 第三方平台的用户名</li>
 *   <li><strong>email</strong>: 第三方平台的邮箱</li>
 *   <li><strong>phone</strong>: 第三方平台的手机号</li>
 *   <li><strong>origin</strong>: 平台来源标识
 *     <ul>
 *       <li>0: 本地用户</li>
 *       <li>1: LDAP</li>
 *       <li>2: OAuth (GitHub、GitLab等)</li>
 *       <li>3: CAS</li>
 *       <li>4: 钉钉</li>
 *       <li>5: 企业微信</li>
 *       <li>6: 飞书</li>
 *     </ul>
 *   </li>
 *   <li><strong>platformId</strong>: 第三方平台的用户唯一标识</li>
 * </ul>
 *
 * <h3>使用场景</h3>
 *
 * <h4>1. 第三方登录处理</h4>
 * <pre>{@code
 * // 从第三方平台获取用户信息
 * PlatformUser platformUser = new PlatformUser();
 * platformUser.setAccount("zhangsan");
 * platformUser.setName("张三");
 * platformUser.setEmail("zhangsan@example.com");
 * platformUser.setOrigin(1);  // LDAP
 * platformUser.setPlatformId("ldap_123456");
 *
 * // 查找或创建系统用户
 * User systemUser = userService.findOrCreateFromPlatform(platformUser);
 * }</pre>
 *
 * <h4>2. 账号绑定</h4>
 * <pre>{@code
 * // 将第三方账号绑定到现有用户
 * PlatformUser platformUser = new PlatformUser();
 * platformUser.setUserId(10001L);  // 现有用户ID
 * platformUser.setAccount("zhangsan_github");
 * platformUser.setOrigin(2);  // OAuth
 * platformUser.setPlatformId("github_789012");
 *
 * userService.bindPlatformAccount(platformUser);
 * }</pre>
 *
 * <h4>3. 用户同步</h4>
 * <pre>{@code
 * // 从LDAP同步用户信息
 * List<PlatformUser> ldapUsers = ldapService.fetchUsers();
 *
 * for (PlatformUser platformUser : ldapUsers) {
 *     platformUser.setOrigin(1);  // LDAP
 *     userService.syncPlatformUser(platformUser);
 * }
 * }</pre>
 *
 * <h2>业务逻辑方法</h2>
 * <p>BO 对象可以包含简单的业务逻辑方法:</p>
 *
 * <pre>{@code
 * public class PlatformUser {
 *     private Long userId;
 *     private String account;
 *     private String name;
 *     private Integer origin;
 *
 *     // 业务逻辑方法:判断是否为新用户
 *     public boolean isNewUser() {
 *         return userId == null;
 *     }
 *
 *     // 业务逻辑方法:获取平台名称
 *     public String getOriginName() {
 *         switch (origin) {
 *             case 1: return "LDAP";
 *             case 2: return "OAuth";
 *             case 3: return "CAS";
 *             case 4: return "钉钉";
 *             case 5: return "企业微信";
 *             case 6: return "飞书";
 *             default: return "本地";
 *         }
 *     }
 *
 *     // 业务逻辑方法:验证数据完整性
 *     public boolean isValid() {
 *         return account != null && !account.isEmpty()
 *             && name != null && !name.isEmpty()
 *             && origin != null;
 *     }
 * }
 * }</pre>
 *
 * <h2>数据转换</h2>
 *
 * <h3>从 DTO 转换到 BO</h3>
 * <pre>{@code
 * // Controller 层接收 DTO
 * PlatformUserCreator creator = ...; // 来自请求
 *
 * // Service 层转换为 BO
 * PlatformUser platformUser = new PlatformUser();
 * platformUser.setAccount(creator.getAccount());
 * platformUser.setName(creator.getName());
 * platformUser.setEmail(creator.getEmail());
 * platformUser.setOrigin(creator.getOrigin());
 *
 * // 进行业务处理
 * userService.processUser(platformUser);
 * }</pre>
 *
 * <h3>从 Entity 转换到 BO</h3>
 * <pre>{@code
 * // 从数据库查询实体
 * User userEntity = userMapper.selectById(userId);
 * UserPlatform platformEntity = userPlatformMapper.selectByUserId(userId);
 *
 * // 转换为 BO
 * PlatformUser platformUser = new PlatformUser();
 * platformUser.setUserId(userEntity.getId());
 * platformUser.setAccount(userEntity.getAccount());
 * platformUser.setName(userEntity.getName());
 * platformUser.setOrigin(platformEntity.getOrigin());
 * platformUser.setPlatformId(platformEntity.getPlatformId());
 * }</pre>
 *
 * <h2>最佳实践</h2>
 * <ul>
 *   <li><strong>职责单一</strong>: 一个 BO 对象应该专注于一个业务概念</li>
 *   <li><strong>不可变性</strong>: 考虑使用 {@code @Value} 和 Builder 模式创建不可变 BO</li>
 *   <li><strong>验证逻辑</strong>: 在 BO 中提供数据验证方法,确保业务数据合法</li>
 *   <li><strong>转换方法</strong>: 提供静态工厂方法或 Builder 方法简化对象创建</li>
 *   <li><strong>文档完善</strong>: 为业务逻辑方法提供清晰的注释说明</li>
 * </ul>
 *
 * <h2>Builder 模式示例</h2>
 * <pre>{@code
 * @Data
 * @Builder
 * public class PlatformUser {
 *     private Long userId;
 *     private String account;
 *     private String name;
 *     private String email;
 *     private Integer origin;
 *     private String platformId;
 * }
 *
 * // 使用 Builder 创建对象
 * PlatformUser user = PlatformUser.builder()
 *     .account("zhangsan")
 *     .name("张三")
 *     .email("zhangsan@example.com")
 *     .origin(1)
 *     .platformId("ldap_123456")
 *     .build();
 * }</pre>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>BO 对象不应该直接返回给前端,应该转换为 VO</li>
 *   <li>BO 对象可以包含多个实体的数据,但不要过度聚合</li>
 *   <li>业务逻辑应该主要在 Service 层实现,BO 只包含简单逻辑</li>
 *   <li>BO 对象的字段应该是业务概念,而不是数据库字段</li>
 *   <li>修改 BO 结构只影响内部实现,不影响 API 接口</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.user.api
 * @see io.dataease.api.permissions.user.dto
 * @see io.dataease.api.permissions.user.vo
 * @since 1.0
 */
package io.dataease.api.permissions.user.bo;
