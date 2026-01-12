/**
 * 认证与权限控制包
 * <p>
 * 提供DataEase系统的认证（Authentication）和授权（Authorization）功能，
 * 包括用户登录、Token管理、权限验证、分享链接访问等核心安全机制。
 * </p>
 *
 * <h2>核心组件</h2>
 *
 * <h3>1. 权限控制注解</h3>
 * <ul>
 *   <li>{@link io.dataease.auth.DePermit} - 方法级权限控制，基于SpEL表达式</li>
 *   <li>{@link io.dataease.auth.DeApiPath} - Controller级API路径和资源类型定义</li>
 *   <li>{@link io.dataease.auth.DeLinkPermit} - 分享链接访问许可，无需登录</li>
 * </ul>
 *
 * <h3>2. 过滤器（Filter）</h3>
 * <ul>
 *   <li>{@link io.dataease.auth.filter.TokenFilter} - Token验证过滤器（企业版）</li>
 *   <li>{@link io.dataease.auth.filter.CommunityTokenFilter} - Token验证过滤器（社区版）</li>
 *   <li>{@link io.dataease.auth.filter.FilterConfig} - 过滤器配置</li>
 * </ul>
 *
 * <h3>3. 用户业务对象（BO）</h3>
 * <ul>
 *   <li>{@link io.dataease.auth.bo.TokenUserBO} - 登录用户信息</li>
 *   <li>{@link io.dataease.auth.bo.LinkTokenUserBO} - 分享链接用户信息</li>
 * </ul>
 *
 * <h3>4. 视图对象（VO）</h3>
 * <ul>
 *   <li>{@link io.dataease.auth.vo.TokenVO} - Token响应对象</li>
 *   <li>{@link io.dataease.auth.vo.MfaItem} - 多因素认证配置</li>
 *   <li>{@link io.dataease.auth.vo.InvalidPwdVO} - 密码失效信息</li>
 * </ul>
 *
 * <h3>5. 配置类</h3>
 * <ul>
 *   <li>{@link io.dataease.auth.config.SubstituleLoginConfig} - 替代登录配置</li>
 * </ul>
 *
 * <h3>6. 拦截器</h3>
 * <ul>
 *   <li>{@link io.dataease.auth.interceptor.CorsConfig} - 跨域配置</li>
 * </ul>
 *
 * <h2>认证流程</h2>
 *
 * <h3>正常用户登录流程</h3>
 * <pre>
 * 1. 用户提交用户名密码到 /api/login
 * 2. 后端验证用户名密码（MD5加密）
 * 3. 验证通过后生成JWT Token
 * 4. 将Token返回给前端（TokenVO）
 * 5. 前端将Token存储在localStorage
 * 6. 后续请求在Header中携带Token：Authorization: Bearer {token}
 * 7. TokenFilter拦截请求，验证Token有效性
 * 8. Token验证通过，解析用户信息并设置到ThreadLocal（AuthUtils）
 * 9. 请求处理完成后，清理ThreadLocal
 * </pre>
 *
 * <h3>分享链接访问流程</h3>
 * <pre>
 * 1. 用户创建分享链接，生成唯一shareId和linkToken
 * 2. 访客通过链接访问：/share/panel/{shareId}?linkToken=xxx
 * 3. TokenFilter识别{@code @DeLinkPermit}注解
 * 4. 跳过正常Token验证，验证linkToken有效性
 * 5. 构造LinkTokenUserBO并设置到ThreadLocal
 * 6. 执行业务逻辑，返回分享内容
 * 7. 清理ThreadLocal
 * </pre>
 *
 * <h2>权限验证流程</h2>
 *
 * <h3>基于{@code @DePermit}的权限验证</h3>
 * <pre>
 * 1. Controller方法标注{@code @DePermit}注解
 * 2. AOP切面拦截该方法调用
 * 3. 解析SpEL表达式，获取资源ID和权限类型
 * 4. 从当前线程获取用户信息（AuthUtils.getUser()）
 * 5. 查询用户对该资源的权限列表（缓存优先）
 * 6. 判断用户是否具有所需权限
 * 7. 权限不足抛出403异常，权限充足则放行
 * </pre>
 *
 * <h2>使用示例</h2>
 *
 * <h3>示例1：数据集Controller完整示例</h3>
 * <pre>
 * {@literal @}RestController
 * {@literal @}DeApiPath(value = "/api/dataset", rt = AuthResourceEnum.DATASET)
 * {@literal @}RequestMapping("/api/dataset")
 * public class DatasetController {
 *
 *     {@literal @}Resource
 *     private DatasetService datasetService;
 *
 *     // 查看数据集 - 需要read权限
 *     {@literal @}DePermit("#p0 + ':read'")
 *     {@literal @}GetMapping("/{id}")
 *     public Dataset getDataset(@PathVariable Long id) {
 *         return datasetService.getById(id);
 *     }
 *
 *     // 编辑数据集 - 需要edit权限
 *     {@literal @}DePermit("#p0.id + ':edit'")
 *     {@literal @}PutMapping
 *     public void updateDataset(@RequestBody DatasetRequest request) {
 *         datasetService.update(request);
 *     }
 *
 *     // 删除数据集 - 需要delete权限
 *     {@literal @}DePermit("#p0 + ':delete'")
 *     {@literal @}DeleteMapping("/{id}")
 *     public void deleteDataset(@PathVariable Long id) {
 *         datasetService.deleteById(id);
 *     }
 *
 *     // 创建数据集 - 无资源ID，检查创建权限
 *     {@literal @}DePermit(value = "dataset:create", busiFlag = "dataset")
 *     {@literal @}PostMapping
 *     public Dataset createDataset(@RequestBody DatasetRequest request) {
 *         return datasetService.create(request);
 *     }
 * }
 * </pre>
 *
 * <h3>示例2：仪表板分享示例</h3>
 * <pre>
 * {@literal @}RestController
 * {@literal @}RequestMapping("/api/share")
 * public class ShareController {
 *
 *     // 创建分享链接 - 需要登录和权限
 *     {@literal @}DePermit("#p0.resourceId + ':share'")
 *     {@literal @}PostMapping("/create")
 *     public ShareLinkVO createShare(@RequestBody ShareRequest request) {
 *         // 生成分享链接和token
 *         return shareService.create(request);
 *     }
 *
 *     // 访问分享链接 - 无需登录
 *     {@literal @}DeLinkPermit("panel:share")
 *     {@literal @}GetMapping("/panel/{shareId}")
 *     public PanelVO getSharedPanel(
 *             @PathVariable String shareId,
 *             @RequestParam String linkToken,
 *             @RequestParam(required = false) String password) {
 *         // 验证linkToken和密码
 *         return shareService.getPanel(shareId, linkToken, password);
 *     }
 * }
 * </pre>
 *
 * <h3>示例3：系统管理示例</h3>
 * <pre>
 * {@literal @}RestController
 * {@literal @}DeApiPath("/api/system", rt = AuthResourceEnum.SYSTEM)
 * {@literal @}RequestMapping("/api/system")
 * public class SystemController {
 *
 *     // 查看系统设置 - 需要系统管理权限
 *     {@literal @}DePermit("system:read")
 *     {@literal @}GetMapping("/settings")
 *     public SystemSettings getSettings() {
 *         return systemService.getSettings();
 *     }
 *
 *     // 修改系统设置 - 需要系统管理权限
 *     {@literal @}DePermit("system:manage")
 *     {@literal @}PostMapping("/settings")
 *     public void updateSettings(@RequestBody SystemSettings settings) {
 *         // 也可以在代码中判断是否为管理员
 *         if (!AuthUtils.isSysAdmin()) {
 *             throw new DEException("仅系统管理员可以修改系统设置");
 *         }
 *         systemService.updateSettings(settings);
 *     }
 * }
 * </pre>
 *
 * <h3>示例4：Service层权限判断</h3>
 * <pre>
 * {@literal @}Service
 * public class DatasetService {
 *
 *     public void deleteDataset(Long datasetId) {
 *         // 获取当前登录用户
 *         TokenUserBO user = AuthUtils.getUser();
 *         if (user == null) {
 *             throw new DEException("用户未登录");
 *         }
 *
 *         // 判断是否为系统管理员
 *         if (AuthUtils.isSysAdmin()) {
 *             // 管理员可以删除任何数据集
 *             datasetMapper.deleteById(datasetId);
 *             return;
 *         }
 *
 *         // 普通用户只能删除自己创建的数据集
 *         Dataset dataset = datasetMapper.selectById(datasetId);
 *         if (!dataset.getCreateBy().equals(user.getUserId())) {
 *             throw new DEException("无权限删除该数据集");
 *         }
 *
 *         datasetMapper.deleteById(datasetId);
 *     }
 * }
 * </pre>
 *
 * <h2>权限设计</h2>
 *
 * <h3>权限粒度</h3>
 * <ul>
 *   <li><b>资源级权限</b>：每个资源（数据集、图表、仪表板）都有独立的权限</li>
 *   <li><b>操作级权限</b>：每种操作（查看、编辑、删除、管理、分享）都需要单独授权</li>
 *   <li><b>组织级权限</b>：基于组织和部门的权限继承</li>
 * </ul>
 *
 * <h3>权限类型</h3>
 * <ul>
 *   <li><code>read</code> - 查看权限</li>
 *   <li><code>edit</code> - 编辑权限</li>
 *   <li><code>delete</code> - 删除权限</li>
 *   <li><code>manage</code> - 管理权限（包括授权给他人）</li>
 *   <li><code>share</code> - 分享权限</li>
 *   <li><code>create</code> - 创建权限（针对资源类型，非具体资源）</li>
 * </ul>
 *
 * <h3>权限存储</h3>
 * <ul>
 *   <li>数据库表：core_sys_auth - 存储所有权限记录</li>
 *   <li>Redis缓存：用户权限列表缓存，key格式：auth:user:{userId}</li>
 *   <li>过期策略：权限修改后自动刷新缓存</li>
 * </ul>
 *
 * <h2>安全特性</h2>
 *
 * <h3>1. Token安全</h3>
 * <ul>
 *   <li>使用JWT Token，包含用户ID、用户名、过期时间等信息</li>
 *   <li>Token有效期默认7天，可配置</li>
 *   <li>支持Token刷新机制</li>
 *   <li>Token使用RSA签名，防止篡改</li>
 * </ul>
 *
 * <h3>2. 密码安全</h3>
 * <ul>
 *   <li>密码使用MD5加密存储（建议升级为BCrypt）</li>
 *   <li>支持密码复杂度要求</li>
 *   <li>支持密码过期策略</li>
 *   <li>登录失败次数限制</li>
 * </ul>
 *
 * <h3>3. 会话管理</h3>
 * <ul>
 *   <li>支持单点登录（SSO）</li>
 *   <li>支持强制下线</li>
 *   <li>支持会话超时自动登出</li>
 * </ul>
 *
 * <h3>4. 多因素认证（MFA）</h3>
 * <ul>
 *   <li>支持TOTP（Time-based One-Time Password）</li>
 *   <li>支持短信验证码</li>
 *   <li>支持邮箱验证码</li>
 * </ul>
 *
 * <h2>扩展功能</h2>
 *
 * <h3>1. 第三方登录</h3>
 * <ul>
 *   <li>支持LDAP/AD集成</li>
 *   <li>支持OAuth 2.0</li>
 *   <li>支持CAS单点登录</li>
 *   <li>支持企业微信、钉钉登录</li>
 * </ul>
 *
 * <h3>2. 审计日志</h3>
 * <ul>
 *   <li>记录所有登录行为</li>
 *   <li>记录权限变更</li>
 *   <li>记录敏感操作</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 *
 * <h3>1. ThreadLocal清理</h3>
 * <ul>
 *   <li>必须在请求结束时调用{@code AuthUtils.remove()}清理ThreadLocal</li>
 *   <li>避免在Tomcat线程池复用时造成用户信息泄露</li>
 *   <li>TokenFilter的finally块负责清理</li>
 * </ul>
 *
 * <h3>2. 权限缓存</h3>
 * <ul>
 *   <li>权限数据缓存在Redis中，提高性能</li>
 *   <li>权限修改后需要刷新缓存</li>
 *   <li>缓存key格式要规范，便于批量清理</li>
 * </ul>
 *
 * <h3>3. 异步任务</h3>
 * <ul>
 *   <li>异步任务无法使用ThreadLocal获取用户信息</li>
 *   <li>需要在任务参数中显式传递userId</li>
 *   <li>或在任务启动前提取用户信息</li>
 * </ul>
 *
 * <h3>4. 性能优化</h3>
 * <ul>
 *   <li>合理使用权限缓存，减少数据库查询</li>
 *   <li>避免在循环中进行权限查询</li>
 *   <li>批量权限查询使用IN查询</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.auth.DePermit
 * @see io.dataease.auth.DeApiPath
 * @see io.dataease.auth.DeLinkPermit
 * @see io.dataease.utils.AuthUtils
 */
package io.dataease.auth;
