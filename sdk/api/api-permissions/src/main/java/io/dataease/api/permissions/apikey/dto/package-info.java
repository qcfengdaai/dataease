/**
 * API Key 数据传输对象包
 *
 * <p>本包包含 API Key 管理模块的所有 DTO (Data Transfer Object) 类，
 * 用于封装客户端请求参数。DTO 对象通常用于接口的输入参数验证和数据传输。</p>
 *
 * <h2>DTO 列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.apikey.dto.ApikeyEnableEditor} - API Key 状态切换器</li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 DTO 类实现 {@link java.io.Serializable} 接口，支持序列化</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化 getter/setter</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>字段命名遵循驼峰命名规范</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <p>DTO 对象主要用于：</p>
 * <ol>
 *   <li>接收前端提交的表单数据</li>
 *   <li>封装复杂的请求参数</li>
 *   <li>在控制器层进行参数验证</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.apikey.api
 * @see io.dataease.api.permissions.apikey.vo
 * @since 1.0
 */
package io.dataease.api.permissions.apikey.dto;
