/**
 * API Key 管理接口层
 *
 * <p>本包定义了 API Key 管理的所有 RESTful 接口，遵循 Spring MVC 规范。
 * 接口使用 OpenAPI 3.0 注解进行文档标注，支持 Knife4j 在线文档。</p>
 *
 * <h2>接口列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.apikey.api.ApiKeyApi} - API Key 管理核心接口</li>
 * </ul>
 *
 * <h2>实现类位置</h2>
 * <p>接口实现类位于 core-backend 模块的 controller 包中，通过 Spring 的 {@code @RestController} 注解标注。</p>
 *
 * <h2>访问路径</h2>
 * <p>基础路径：{@code /api/apikey}</p>
 *
 * <h2>权限控制</h2>
 * <p>所有接口都需要登录认证（JWT Token），用户只能管理自己的 API Key。</p>
 *
 * @see io.dataease.api.permissions.apikey.dto
 * @see io.dataease.api.permissions.apikey.vo
 * @since 1.0
 */
package io.dataease.api.permissions.apikey.api;
