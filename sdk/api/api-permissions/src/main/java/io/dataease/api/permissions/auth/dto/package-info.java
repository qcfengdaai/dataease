/**
 * 权限管理数据传输对象包
 *
 * <p>本包包含权限管理模块的所有 DTO (Data Transfer Object) 类，
 * 用于封装权限管理相关的请求参数和数据传输。</p>
 *
 * <h2>DTO 分类</h2>
 *
 * <h3>1. 查询请求类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.auth.dto.BusiPermissionRequest} - 业务权限查询请求</li>
 *   <li>{@link io.dataease.api.permissions.auth.dto.MenuPermissionRequest} - 菜单权限查询请求</li>
 * </ul>
 *
 * <h3>2. 编辑器类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.auth.dto.BusiPerEditor} - 业务权限编辑器</li>
 *   <li>{@link io.dataease.api.permissions.auth.dto.MenuPerEditor} - 菜单权限编辑器</li>
 * </ul>
 *
 * <h3>3. 权限检查类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.auth.dto.BusiPerCheckDTO} - 业务权限检查参数</li>
 * </ul>
 *
 * <h3>4. 资源管理类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.auth.dto.BusiResourceCreator} - 业务资源创建器</li>
 *   <li>{@link io.dataease.api.permissions.auth.dto.BusiResourceEditor} - 业务资源编辑器</li>
 *   <li>{@link io.dataease.api.permissions.auth.dto.BusiResourceMover} - 业务资源移动器</li>
 * </ul>
 *
 * <h3>5. 权限构造器类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.auth.dto.TargetPerCreator} - 目标权限构造器基类</li>
 *   <li>{@link io.dataease.api.permissions.auth.dto.MenuTargetPerCreator} - 菜单目标权限构造器</li>
 *   <li>{@link io.dataease.api.permissions.auth.dto.BusiTargetPerCreator} - 业务目标权限构造器</li>
 * </ul>
 *
 * <h3>6. 批量授权类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.auth.dto.BusiBatchAuthorizeRequest} - 业务批量授权请求</li>
 *   <li>{@link io.dataease.api.permissions.auth.dto.BusiBatchAuthorizeNode} - 业务批量授权节点</li>
 * </ul>
 *
 * <h3>7. 业务对象类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.auth.dto.PermissionBO} - 权限业务对象</li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 DTO 类实现 {@link java.io.Serializable} 接口</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化代码</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>字段命名遵循驼峰命名规范</li>
 *   <li>提供合理的默认值</li>
 * </ul>
 *
 * <h2>命名规范</h2>
 * <ul>
 *   <li><strong>Request</strong>：查询请求类，用于封装查询条件</li>
 *   <li><strong>Editor</strong>：编辑器类，用于封装编辑操作的参数</li>
 *   <li><strong>Creator</strong>：创建器类，用于封装创建操作的参数</li>
 *   <li><strong>Mover</strong>：移动器类，用于封装移动操作的参数</li>
 *   <li><strong>CheckDTO</strong>：检查类，用于权限校验</li>
 *   <li><strong>BO</strong>：业务对象，用于内部业务处理</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <p>DTO 对象主要用于：</p>
 * <ol>
 *   <li>接收前端提交的权限管理请求</li>
 *   <li>封装复杂的权限配置参数</li>
 *   <li>在服务间传递权限数据</li>
 *   <li>在控制器层进行参数验证</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.auth.api
 * @see io.dataease.api.permissions.auth.vo
 * @since 1.0
 */
package io.dataease.api.permissions.auth.dto;
