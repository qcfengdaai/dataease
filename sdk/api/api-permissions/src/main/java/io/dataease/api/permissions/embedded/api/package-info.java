/**
 * 嵌入式集成管理接口层
 *
 * <p>本包定义了嵌入式集成管理的所有 RESTful 接口,遵循 Spring MVC 规范。
 * 接口使用 OpenAPI 3.0 注解进行文档标注,支持 Knife4j 在线文档。</p>
 *
 * <h2>接口列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.embedded.api.EmbeddedApi} - 嵌入式集成管理核心接口
 *     <ul>
 *       <li>分页查询嵌入式应用列表</li>
 *       <li>创建嵌入式应用</li>
 *       <li>编辑嵌入式应用</li>
 *       <li>删除嵌入式应用(单个/批量)</li>
 *       <li>重置应用密钥</li>
 *       <li>获取域名列表</li>
 *       <li>初始化 iframe 嵌入</li>
 *       <li>获取 Token 参数</li>
 *       <li>获取应用数量限制</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>实现类位置</h2>
 * <p>接口实现类位于企业版的 distributed 模块:</p>
 * <ul>
 *   <li>{@code distributed/src/main/java/io/dataease/embedded/controller/EmbeddedController}</li>
 * </ul>
 *
 * <h2>访问路径</h2>
 * <p>基础路径:{@code /api/embedded}</p>
 *
 * <h2>权限控制</h2>
 * <ul>
 *   <li><strong>管理接口</strong>:需要系统管理员权限才能创建、编辑、删除应用</li>
 *   <li><strong>查询接口</strong>:需要登录认证(JWT Token)</li>
 *   <li><strong>嵌入接口</strong>:使用 AppId/AppSecret 认证,不需要 JWT Token</li>
 * </ul>
 *
 * <h2>接口特点</h2>
 * <ul>
 *   <li><strong>RESTful 风格</strong>:遵循 REST API 设计规范</li>
 *   <li><strong>分页查询</strong>:列表接口支持分页和关键字搜索</li>
 *   <li><strong>批量操作</strong>:支持批量删除等批量操作</li>
 *   <li><strong>安全认证</strong>:提供多种认证方式,确保接口安全</li>
 *   <li><strong>企业功能</strong>:标注 {@code @XpackResource},仅企业版可用</li>
 * </ul>
 *
 * <h2>接口安全</h2>
 * <ul>
 *   <li><strong>密钥保护</strong>:AppSecret 仅在创建和重置时返回,其他接口不返回</li>
 *   <li><strong>操作审计</strong>:所有管理操作都记录操作日志</li>
 *   <li><strong>权限校验</strong>:严格的权限校验,防止越权访问</li>
 *   <li><strong>参数验证</strong>:使用 JSR-303 注解进行参数验证</li>
 * </ul>
 *
 * @see io.dataease.api.permissions.embedded.dto
 * @see io.dataease.api.permissions.embedded.vo
 * @since 2.0
 */
package io.dataease.api.permissions.embedded.api;
