/**
 * API Key 视图对象包
 *
 * <p>本包包含 API Key 管理模块的所有 VO (View Object) 类，
 * 用于封装返回给客户端的响应数据。VO 对象是数据展示层对象，通常包含格式化后的数据。</p>
 *
 * <h2>VO 列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.apikey.vo.ApiKeyVO} - API Key 视图对象</li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 VO 类实现 {@link java.io.Serializable} 接口，支持序列化</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化 getter/setter</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>Long 类型的 ID 使用 {@code @JsonSerialize(using=ToStringSerializer.class)} 避免前端精度丢失</li>
 *   <li>敏感字段（如 accessSecret）根据场景选择性返回</li>
 * </ul>
 *
 * <h2>VO 与实体的区别</h2>
 * <ul>
 *   <li><strong>实体(Entity)</strong>：与数据库表一一对应，包含所有数据库字段</li>
 *   <li><strong>VO</strong>：面向前端展示，可能包含多个实体的组合数据，或对敏感字段进行脱敏</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <p>VO 对象主要用于：</p>
 * <ol>
 *   <li>封装接口响应数据</li>
 *   <li>对敏感字段进行脱敏或过滤</li>
 *   <li>组合多个实体的数据</li>
 *   <li>对数据进行格式化展示</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.apikey.api
 * @see io.dataease.api.permissions.apikey.dto
 * @since 1.0
 */
package io.dataease.api.permissions.apikey.vo;
