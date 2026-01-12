/**
 * 嵌入式集成管理模块
 *
 * <h2>模块概述</h2>
 * <p>本模块提供完整的嵌入式集成功能,允许将 DataEase 的仪表板、图表等数据可视化资源
 * 嵌入到第三方系统中。通过 AppId/AppSecret 密钥对进行安全认证,结合域名白名单机制,
 * 确保嵌入内容的安全性和可控性。</p>
 *
 * <h2>核心功能</h2>
 * <ul>
 *   <li><strong>应用管理</strong>:创建、编辑、删除嵌入式应用配置
 *     <ul>
 *       <li>应用基本信息管理</li>
 *       <li>密钥对(AppId/AppSecret)生成</li>
 *       <li>应用状态控制</li>
 *       <li>批量删除应用</li>
 *     </ul>
 *   </li>
 *   <li><strong>密钥管理</strong>:安全的密钥生成和重置机制
 *     <ul>
 *       <li>自动生成唯一的 AppId</li>
 *       <li>随机生成强密码 AppSecret</li>
 *       <li>密钥重置和轮换</li>
 *       <li>密钥加密存储</li>
 *     </ul>
 *   </li>
 *   <li><strong>域名白名单</strong>:配置允许嵌入的来源域名
 *     <ul>
 *       <li>多域名配置支持</li>
 *       <li>通配符域名支持</li>
 *       <li>跨域访问控制</li>
 *       <li>防止未授权嵌入</li>
 *     </ul>
 *   </li>
 *   <li><strong>Token 认证</strong>:基于 JWT 的安全认证机制
 *     <ul>
 *       <li>使用 AppSecret 签名生成 Token</li>
 *       <li>Token 自动过期机制</li>
 *       <li>访问来源验证</li>
 *       <li>防重放攻击</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>嵌入式集成架构</h2>
 * <pre>
 * ┌─────────────────────────────────────────────────────────────┐
 * │                     第三方系统前端                            │
 * │  ┌─────────────────────────────────────────────────────┐   │
 * │  │                                                       │   │
 * │  │   1. 后端使用 AppSecret 生成 Token                    │   │
 * │  │   2. 将 Token 传递给前端                              │   │
 * │  │   3. 前端使用 iframe 嵌入 DataEase                    │   │
 * │  │      <iframe src="https://dataease.com/embed?        │   │
 * │  │               token={jwt_token}"></iframe>            │   │
 * │  │                                                       │   │
 * │  └─────────────────────────────────────────────────────┘   │
 * └─────────────────────────────────────────────────────────────┘
 *                            ↓ HTTPS + Token
 * ┌─────────────────────────────────────────────────────────────┐
 * │                  DataEase 嵌入式服务                         │
 * │  ┌─────────────────────────────────────────────────────┐   │
 * │  │  1. 验证 Token 签名(使用 AppSecret)                   │   │
 * │  │  2. 验证 Token 是否过期                               │   │
 * │  │  3. 验证来源域名是否在白名单中                         │   │
 * │  │  4. 返回数据可视化内容                                │   │
 * │  └─────────────────────────────────────────────────────┘   │
 * └─────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <h2>包结构</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.embedded.api} - API 接口定义层
 *     <ul>
 *       <li>{@link io.dataease.api.permissions.embedded.api.EmbeddedApi} - 嵌入式管理核心接口</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.embedded.dto} - 数据传输对象
 *     <ul>
 *       <li>EmbeddedCreator:应用创建器</li>
 *       <li>EmbeddedEditor:应用编辑器</li>
 *       <li>EmbeddedResetRequest:密钥重置请求</li>
 *       <li>EmbeddedOrigin:来源验证对象</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.embedded.vo} - 视图对象
 *     <ul>
 *       <li>EmbeddedGridVO:嵌入式应用列表视图</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>使用场景</h2>
 *
 * <h3>1. 创建嵌入式应用</h3>
 * <pre>{@code
 * POST /api/embedded/create
 * Content-Type: application/json
 *
 * {
 *   "name": "OA系统集成",
 *   "domains": [
 *     "https://oa.company.com",
 *     "https://portal.company.com"
 *   ],
 *   "remark": "用于OA系统中嵌入销售报表"
 * }
 *
 * Response:
 * {
 *   "id": 10001,
 *   "name": "OA系统集成",
 *   "appId": "app_1705123456789",
 *   "appSecret": "sk_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",  // 仅返回一次
 *   "domains": ["https://oa.company.com", "https://portal.company.com"],
 *   "createTime": 1705123456789
 * }
 * }</pre>
 *
 * <h3>2. 第三方系统生成 Token(后端)</h3>
 * <pre>{@code
 * // Java 示例:使用 JWT 生成 Token
 * String appId = "app_1705123456789";
 * String appSecret = "sk_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
 * String resourceId = "dashboard_001";  // 要嵌入的仪表板ID
 *
 * // 构建 JWT Claims
 * Map<String, Object> claims = new HashMap<>();
 * claims.put("appId", appId);
 * claims.put("resourceId", resourceId);
 * claims.put("exp", System.currentTimeMillis() / 1000 + 3600);  // 1小时后过期
 *
 * // 使用 AppSecret 签名
 * String token = Jwts.builder()
 *     .setClaims(claims)
 *     .signWith(SignatureAlgorithm.HS256, appSecret)
 *     .compact();
 *
 * // 将 token 传递给前端
 * return token;
 * }</pre>
 *
 * <h3>3. 前端嵌入 DataEase 内容</h3>
 * <pre>{@code
 * <!-- HTML 示例 -->
 * <iframe
 *   src="https://dataease.company.com/embedded/dashboard?token=eyJhbGci......"
 *   width="100%"
 *   height="600px"
 *   frameborder="0"
 *   allowfullscreen>
 * </iframe>
 *
 * <!-- Vue 示例 -->
 * <template>
 *   <iframe
 *     :src="embeddedUrl"
 *     width="100%"
 *     height="600px"
 *     frameborder="0">
 *   </iframe>
 * </template>
 *
 * <script>
 * export default {
 *   data() {
 *     return {
 *       token: ''
 *     }
 *   },
 *   computed: {
 *     embeddedUrl() {
 *       return `https://dataease.company.com/embedded/dashboard?token=${this.token}`;
 *     }
 *   },
 *   async mounted() {
 *     // 从后端获取 Token
 *     const response = await fetch('/api/getEmbeddedToken');
 *     this.token = await response.text();
 *   }
 * }
 * </script>
 * }</pre>
 *
 * <h3>4. 查询嵌入式应用列表</h3>
 * <pre>{@code
 * POST /api/embedded/pager/1/10
 * Content-Type: application/json
 *
 * {
 *   "keyword": "OA"
 * }
 *
 * Response:
 * {
 *   "total": 5,
 *   "records": [
 *     {
 *       "id": 10001,
 *       "name": "OA系统集成",
 *       "appId": "app_1705123456789",
 *       "appSecret": null,  // 列表查询不返回 secret
 *       "domains": ["https://oa.company.com"],
 *       "createTime": 1705123456789
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <h3>5. 重置应用密钥</h3>
 * <pre>{@code
 * POST /api/embedded/reset
 * Content-Type: application/json
 *
 * {
 *   "id": 10001,
 *   "appSecret": "sk_new_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
 * }
 *
 * Response: 操作成功
 * 注意:重置后,使用旧密钥生成的 Token 立即失效
 * }</pre>
 *
 * <h2>实现位置</h2>
 * <p>本模块的接口实现位于:</p>
 * <ul>
 *   <li><strong>企业版</strong>: {@code distributed/src/main/java/io/dataease/embedded/}
 *     <ul>
 *       <li>controller: EmbeddedController</li>
 *       <li>service: EmbeddedService</li>
 *     </ul>
 *   </li>
 *   <li><strong>社区版</strong>: 不支持嵌入式功能</li>
 * </ul>
 *
 * <h2>数据模型</h2>
 * <p>嵌入式集成涉及的主要数据表:</p>
 * <ul>
 *   <li><strong>embedded_app</strong>:嵌入式应用表,存储应用配置和密钥</li>
 *   <li><strong>embedded_domain</strong>:域名白名单表,存储允许的域名</li>
 *   <li><strong>embedded_log</strong>:嵌入访问日志表,记录嵌入页面的访问</li>
 * </ul>
 *
 * <h2>安全机制</h2>
 *
 * <h3>1. 密钥安全</h3>
 * <ul>
 *   <li><strong>AppId 唯一性</strong>:系统生成,格式为 {@code app_timestamp},保证全局唯一</li>
 *   <li><strong>AppSecret 强度</strong>:64位随机字符串,包含大小写字母、数字和特殊字符</li>
 *   <li><strong>密钥加密</strong>:AppSecret 在数据库中使用 AES-256 加密存储</li>
 *   <li><strong>仅返回一次</strong>:AppSecret 仅在创建和重置时返回,其他时候不返回</li>
 * </ul>
 *
 * <h3>2. Token 安全</h3>
 * <ul>
 *   <li><strong>JWT 签名</strong>:使用 HS256 算法,用 AppSecret 签名,防止篡改</li>
 *   <li><strong>过期控制</strong>:Token 默认有效期 1 小时,过期自动失效</li>
 *   <li><strong>一次性使用</strong>:Token 使用后可选择性失效,防止重放</li>
 *   <li><strong>HTTPS 传输</strong>:生产环境必须使用 HTTPS 传输 Token</li>
 * </ul>
 *
 * <h3>3. 域名白名单</h3>
 * <ul>
 *   <li><strong>来源验证</strong>:验证 HTTP Referer 或 Origin 头,必须在白名单中</li>
 *   <li><strong>通配符支持</strong>:支持 {@code *.company.com} 形式的通配符域名</li>
 *   <li><strong>协议严格</strong>:域名必须包含协议(http/https),且协议必须匹配</li>
 *   <li><strong>端口区分</strong>:相同域名不同端口视为不同来源</li>
 * </ul>
 *
 * <h3>4. 内容安全策略</h3>
 * <ul>
 *   <li><strong>X-Frame-Options</strong>:设置为 ALLOW-FROM,限制嵌入来源</li>
 *   <li><strong>Content-Security-Policy</strong>:配置 CSP 头,防止 XSS 攻击</li>
 *   <li><strong>SameSite Cookie</strong>:设置 Cookie 的 SameSite 属性为 None</li>
 *   <li><strong>Sandbox 属性</strong>:建议 iframe 使用 sandbox 属性限制权限</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li><strong>AppSecret 保密</strong>:AppSecret 是敏感信息,切勿泄露或提交到代码仓库</li>
 *   <li><strong>Token 后端生成</strong>:Token 必须在第三方系统的后端生成,不能在前端生成</li>
 *   <li><strong>域名配置准确</strong>:域名白名单必须配置准确,包括协议和端口</li>
 *   <li><strong>及时轮换密钥</strong>:建议定期(如每3个月)轮换 AppSecret</li>
 *   <li><strong>泄露立即重置</strong>:如发现密钥泄露,立即重置并更新第三方系统</li>
 *   <li><strong>监控访问日志</strong>:定期检查嵌入访问日志,发现异常及时处理</li>
 *   <li><strong>限制并发数</strong>:可选配置同一 Token 的最大并发访问数</li>
 *   <li><strong>跨域调试</strong>:开发环境调试时注意跨域配置</li>
 * </ol>
 *
 * <h2>最佳实践</h2>
 * <ul>
 *   <li><strong>按系统创建应用</strong>:为每个第三方系统创建独立的嵌入式应用</li>
 *   <li><strong>最小权限域名</strong>:只配置确实需要的域名,不使用通配符(除非必要)</li>
 *   <li><strong>短有效期 Token</strong>:Token 有效期设置为 1-2 小时,根据需要刷新</li>
 *   <li><strong>服务端缓存 Token</strong>:第三方系统后端可缓存 Token,避免频繁生成</li>
 *   <li><strong>监控和告警</strong>:配置异常访问告警,及时发现安全问题</li>
 *   <li><strong>文档和培训</strong>:为第三方系统开发人员提供详细的集成文档</li>
 * </ul>
 *
 * <h2>故障排查</h2>
 * <p>常见问题和解决方案:</p>
 * <ul>
 *   <li><strong>Token 验证失败</strong>:
 *     <ul>
 *       <li>检查 AppSecret 是否正确</li>
 *       <li>检查 Token 是否过期</li>
 *       <li>检查 Token 签名算法是否正确(HS256)</li>
 *     </ul>
 *   </li>
 *   <li><strong>域名验证失败</strong>:
 *     <ul>
 *       <li>检查域名白名单是否包含当前域名</li>
 *       <li>检查协议(http/https)是否匹配</li>
 *       <li>检查端口是否正确</li>
 *     </ul>
 *   </li>
 *   <li><strong>iframe 无法加载</strong>:
 *     <ul>
 *       <li>检查浏览器 X-Frame-Options 设置</li>
 *       <li>检查 Content-Security-Policy 配置</li>
 *       <li>检查是否被浏览器跨域策略阻止</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * @author DataEase Team
 * @since 2.0
 */
package io.dataease.api.permissions.embedded;
