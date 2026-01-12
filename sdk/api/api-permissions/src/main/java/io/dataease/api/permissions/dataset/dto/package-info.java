/**
 * 数据集权限数据传输对象包
 *
 * <p>本包包含数据集权限管理模块的所有 DTO (Data Transfer Object) 类,
 * 用于封装客户端请求参数。DTO 对象通常用于接口的输入参数验证和数据传输。</p>
 *
 * <h2>DTO 列表</h2>
 *
 * <h3>列权限相关</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.dataset.dto.DataSetColumnPermissionsDTO} - 列权限配置对象
 *     <ul>
 *       <li>封装列权限的完整配置信息</li>
 *       <li>包含授权对象、可见列、白名单等</li>
 *       <li>支持启用/禁用状态控制</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.dataset.dto.WhiteListUsersRequest} - 白名单用户请求
 *     <ul>
 *       <li>用于配置列权限的例外用户</li>
 *       <li>白名单用户不受列权限限制</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>行权限相关</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.dataset.dto.DataSetRowPermissionsTreeDTO} - 行权限树形对象
 *     <ul>
 *       <li>表达复杂的行过滤条件</li>
 *       <li>支持树形结构的条件组合</li>
 *       <li>支持 AND/OR 逻辑运算</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.dataset.dto.DatasetRowPermissionsTreeRequest} - 行权限查询请求
 *     <ul>
 *       <li>查询指定数据集的行权限配置</li>
 *       <li>获取行权限过滤条件树</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>通用对象</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.dataset.dto.BaseTreeNode} - 基础树节点
 *     <ul>
 *       <li>树形结构的基类</li>
 *       <li>提供树节点的通用属性和方法</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.dataset.dto.Item} - 通用项目对象
 *     <ul>
 *       <li>表示列表中的单个项目</li>
 *       <li>用于下拉选择、多选等场景</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.dataset.dto.LangSwitchRequest} - 语言切换请求
 *     <ul>
 *       <li>支持多语言环境下的权限配置</li>
 *       <li>处理国际化字段的权限控制</li>
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
 *   <li>使用 JSR-303 注解进行参数验证</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <p>DTO 对象主要用于:</p>
 * <ol>
 *   <li>接收前端提交的列权限配置数据</li>
 *   <li>接收前端提交的行权限过滤条件</li>
 *   <li>封装复杂的权限查询请求</li>
 *   <li>在控制器层进行参数验证</li>
 * </ol>
 *
 * <h2>数据流转</h2>
 * <pre>
 * 前端表单 → DTO 对象 → Controller 接收 → Service 处理 → Entity 持久化 → 数据库
 * </pre>
 *
 * <h2>树形结构说明</h2>
 * <p>行权限使用树形结构表达复杂的过滤条件:</p>
 * <pre>{@code
 * {
 *   "expressType": "and",
 *   "children": [
 *     {
 *       "field": "department",
 *       "operator": "eq",
 *       "value": "${currentDept}"
 *     },
 *     {
 *       "expressType": "or",
 *       "children": [
 *         {
 *           "field": "status",
 *           "operator": "eq",
 *           "value": "active"
 *         },
 *         {
 *           "field": "status",
 *           "operator": "eq",
 *           "value": "pending"
 *         }
 *       ]
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <p>上述树形结构转换为 SQL 条件:</p>
 * <pre>{@code
 * WHERE department = '销售部'
 *   AND (status = 'active' OR status = 'pending')
 * }</pre>
 *
 * @see io.dataease.api.permissions.dataset.api
 * @since 2.0
 */
package io.dataease.api.permissions.dataset.dto;
