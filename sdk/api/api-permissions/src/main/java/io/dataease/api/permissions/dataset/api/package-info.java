/**
 * 数据集权限管理接口层
 *
 * <p>本包定义了数据集权限管理的所有 RESTful 接口,包括列权限和行权限的管理接口。
 * 接口使用 OpenAPI 3.0 注解进行文档标注,支持 Knife4j 在线文档。</p>
 *
 * <h2>接口列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.dataset.api.ColumnPermissionsApi} - 列权限管理接口
 *     <ul>
 *       <li>查询列权限配置列表(分页)</li>
 *       <li>保存或更新列权限配置</li>
 *       <li>删除列权限配置</li>
 *       <li>获取列权限详细信息</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.dataset.api.RowPermissionsApi} - 行权限管理接口
 *     <ul>
 *       <li>查询行权限配置列表(分页)</li>
 *       <li>保存或更新行权限配置</li>
 *       <li>删除行权限配置</li>
 *       <li>获取行权限过滤条件树</li>
 *       <li>切换行权限启用状态</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>实现类位置</h2>
 * <p>接口实现类位于 core-backend 模块的 controller 包中:</p>
 * <ul>
 *   <li>{@code io.dataease.dataset.permissions.controller.ColumnPermissionsController}</li>
 *   <li>{@code io.dataease.dataset.permissions.controller.RowPermissionsController}</li>
 * </ul>
 *
 * <h2>访问路径</h2>
 * <ul>
 *   <li>列权限基础路径:{@code /api/dataset/columnPermissions}</li>
 *   <li>行权限基础路径:{@code /api/dataset/rowPermissions}</li>
 * </ul>
 *
 * <h2>权限控制</h2>
 * <p>所有接口都需要登录认证(JWT Token),并且需要对数据集有管理权限才能配置权限规则。</p>
 *
 * <h2>接口特点</h2>
 * <ul>
 *   <li><strong>RESTful 风格</strong>:遵循 REST API 设计规范</li>
 *   <li><strong>分页查询</strong>:列表接口支持分页,避免数据量过大</li>
 *   <li><strong>树形结构</strong>:行权限使用树形结构表达复杂的过滤条件</li>
 *   <li><strong>批量操作</strong>:支持批量删除等批量操作</li>
 * </ul>
 *
 * @see io.dataease.api.permissions.dataset.dto
 * @since 2.0
 */
package io.dataease.api.permissions.dataset.api;
