/**
 * 组织架构数据传输对象包
 *
 * <p>本包包含组织架构管理模块的所有 DTO (Data Transfer Object) 类，
 * 用于封装客户端请求参数。DTO 对象通常用于接口的输入参数验证和数据传输。</p>
 *
 * <h2>DTO 列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.org.dto.OrgCreator} - 组织创建请求对象</li>
 *   <li>{@link io.dataease.api.permissions.org.dto.OrgEditor} - 组织编辑请求对象</li>
 *   <li>{@link io.dataease.api.permissions.org.dto.OrgRequest} - 组织查询请求对象（全量加载）</li>
 *   <li>{@link io.dataease.api.permissions.org.dto.OrgLazyRequest} - 组织查询请求对象（懒加载）</li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 DTO 类实现 {@link java.io.Serializable} 接口，支持序列化</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化 getter/setter</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>字段命名遵循驼峰命名规范</li>
 *   <li>必填字段使用 JSR-303 注解进行参数校验（如 {@code @NotNull}、{@code @NotBlank}）</li>
 * </ul>
 *
 * <h2>DTO 详细说明</h2>
 *
 * <h3>1. OrgCreator - 组织创建请求</h3>
 * <p>用于创建新的组织单位，包含以下字段：</p>
 * <pre>
 * {
 *   "name": "研发部",      // 组织名称（必填，长度限制 1-50）
 *   "pid": 1              // 父组织 ID（可选，0 或空表示创建根级组织）
 * }
 * </pre>
 *
 * <h3>2. OrgEditor - 组织编辑请求</h3>
 * <p>用于更新现有组织单位的信息，包含以下字段：</p>
 * <pre>
 * {
 *   "id": 10,             // 组织 ID（必填）
 *   "name": "技术研发部"   // 新名称（必填，长度限制 1-50）
 * }
 * </pre>
 *
 * <h3>3. OrgRequest - 组织查询请求（全量加载）</h3>
 * <p>用于查询完整的组织树，包含以下字段：</p>
 * <pre>
 * {
 *   "keyword": "研发",    // 搜索关键词（可选，模糊匹配组织名称）
 *   "sort": "asc",       // 排序方式（可选，asc/desc）
 *   "includeDisabled": false  // 是否包含已禁用的组织（可选）
 * }
 * </pre>
 *
 * <h3>4. OrgLazyRequest - 组织查询请求（懒加载）</h3>
 * <p>用于按需加载组织树节点，包含以下字段：</p>
 * <pre>
 * {
 *   "pid": 0,            // 父组织 ID（必填，0 表示查询根节点）
 *   "keyword": "",       // 搜索关键词（可选）
 *   "expandIds": [1, 2]  // 需要展开的节点 ID 列表（可选）
 * }
 * </pre>
 * <p>懒加载流程：</p>
 * <ol>
 *   <li>首次请求 pid=0，返回所有根节点</li>
 *   <li>用户点击展开某个节点，前端发送该节点 ID 作为 pid</li>
 *   <li>后端返回该节点的直接子节点列表</li>
 *   <li>如果设置了 expandIds，后端会递归加载这些节点的子树</li>
 * </ol>
 *
 * <h2>使用场景</h2>
 * <p>DTO 对象主要用于：</p>
 * <ol>
 *   <li>接收前端提交的表单数据</li>
 *   <li>封装复杂的请求参数</li>
 *   <li>在控制器层进行参数验证</li>
 *   <li>统一请求格式，便于 API 文档生成</li>
 * </ol>
 *
 * <h2>参数校验</h2>
 * <p>DTO 类通常配合 Spring Validation 进行参数校验：</p>
 * <ul>
 *   <li>{@code @NotNull}: 不能为 null</li>
 *   <li>{@code @NotBlank}: 字符串不能为空</li>
 *   <li>{@code @Size(min, max)}: 字符串或集合的长度限制</li>
 *   <li>{@code @Pattern(regexp)}: 字符串格式验证（如正则表达式）</li>
 * </ul>
 *
 * <h2>与 VO 的区别</h2>
 * <ul>
 *   <li><strong>DTO</strong>: 用于接收请求参数，数据流向是<strong>前端 → 后端</strong></li>
 *   <li><strong>VO</strong>: 用于返回响应数据，数据流向是<strong>后端 → 前端</strong></li>
 *   <li>DTO 注重参数校验，VO 注重数据展示和脱敏</li>
 * </ul>
 *
 * <h2>扩展建议</h2>
 * <p>新增 DTO 类时应遵循以下规范：</p>
 * <ol>
 *   <li>类名以业务含义命名，后缀为 Creator/Editor/Request 等</li>
 *   <li>所有字段添加 {@code @Schema} 注解说明字段含义</li>
 *   <li>必填字段添加校验注解（{@code @NotNull}、{@code @NotBlank} 等）</li>
 *   <li>提供清晰的类注释，说明使用场景和字段含义</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.org.api
 * @see io.dataease.api.permissions.org.vo
 * @since 1.0
 */
package io.dataease.api.permissions.org.dto;
