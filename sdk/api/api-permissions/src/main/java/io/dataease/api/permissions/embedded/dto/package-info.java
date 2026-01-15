/**
 * 嵌入式集成数据传输对象包
 *
 * <p>本包包含嵌入式集成管理模块的所有 DTO (Data Transfer Object) 类,
 * 用于封装客户端请求参数。DTO 对象通常用于接口的输入参数验证和数据传输。</p>
 *
 * <h2>DTO 列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.embedded.dto.EmbeddedCreator} - 嵌入式应用创建器
 *     <ul>
 *       <li>创建新的嵌入式应用</li>
 *       <li>包含应用名称、域名白名单、备注等信息</li>
 *       <li>系统自动生成 AppId 和 AppSecret</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.embedded.dto.EmbeddedEditor} - 嵌入式应用编辑器
 *     <ul>
 *       <li>编辑已存在的嵌入式应用</li>
 *       <li>可修改应用名称、域名白名单、备注等</li>
 *       <li>不能修改 AppId 和 AppSecret</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.embedded.dto.EmbeddedResetRequest} - 密钥重置请求
 *     <ul>
 *       <li>重置嵌入式应用的 AppSecret</li>
 *       <li>包含应用 ID 和新的 AppSecret</li>
 *       <li>重置后旧密钥立即失效</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.embedded.dto.EmbeddedOrigin} - 来源验证对象
 *     <ul>
 *       <li>验证嵌入页面的来源域名</li>
 *       <li>包含 Token 和 Origin 信息</li>
 *       <li>用于 iframe 初始化时的安全验证</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 DTO 类实现 {@link java.io.Serializable} 接口,支持序列化</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化 getter/setter</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>字段命名遵循驼峰命名规范</li>
 *   <li>使用 JSR-303 注解进行参数验证(如 {@code @NotBlank}、{@code @NotNull})</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <p>DTO 对象主要用于:</p>
 * <ol>
 *   <li>接收前端提交的嵌入式应用配置数据</li>
 *   <li>封装密钥重置请求</li>
 *   <li>验证嵌入页面的来源和 Token</li>
 *   <li>在控制器层进行参数验证</li>
 * </ol>
 *
 * <h2>数据流转</h2>
 * <pre>
 * 前端表单 → DTO 对象 → Controller 接收 → Service 处理 → Entity 持久化 → 数据库
 * </pre>
 *
 * <h2>参数验证示例</h2>
 * <pre>{@code
 * @Data
 * @Schema(description = "嵌入式应用创建参数")
 * public class EmbeddedCreator implements Serializable {
 *
 *     @NotBlank(message = "应用名称不能为空")
 *     @Schema(description = "应用名称", required = true)
 *     private String name;
 *
 *     @NotNull(message = "域名白名单不能为空")
 *     @Size(min = 1, message = "至少配置一个域名")
 *     @Schema(description = "域名白名单", required = true)
 *     private List<String> domains;
 *
 *     @Schema(description = "备注")
 *     private String remark;
 * }
 * }</pre>
 *
 * <h2>域名配置说明</h2>
 * <p>域名白名单配置要求:</p>
 * <ul>
 *   <li><strong>完整格式</strong>:必须包含协议,如 {@code https://example.com}</li>
 *   <li><strong>端口可选</strong>:可以指定端口,如 {@code https://example.com:8080}</li>
 *   <li><strong>支持通配符</strong>:可以使用 {@code *} 通配符,如 {@code https://*.example.com}</li>
 *   <li><strong>多个域名</strong>:可以配置多个域名,用数组表示</li>
 * </ul>
 *
 * <h2>安全注意事项</h2>
 * <ul>
 *   <li><strong>AppSecret 敏感</strong>:DTO 中的 AppSecret 字段不应该在日志中打印</li>
 *   <li><strong>域名验证</strong>:域名格式必须严格验证,防止配置错误</li>
 *   <li><strong>参数长度限制</strong>:应用名称、备注等字段应限制长度,防止数据库溢出</li>
 *   <li><strong>SQL 注入防护</strong>:所有用户输入都需要进行安全过滤</li>
 * </ul>
 *
 * @see io.dataease.api.permissions.embedded.api
 * @see io.dataease.api.permissions.embedded.vo
 * @since 2.0
 */
package io.dataease.api.permissions.embedded.dto;
