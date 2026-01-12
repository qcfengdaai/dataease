/**
 * API Key 管理模块
 *
 * <h2>模块概述</h2>
 * <p>本模块提供完整的 API Key 生命周期管理功能，用于支持系统的外部 API 调用鉴权。
 * API Key 是一种基于密钥对的身份认证机制，允许外部应用程序通过 RESTful API 访问 DataEase 系统。</p>
 *
 * <h2>核心功能</h2>
 * <ul>
 *   <li>API Key 生成：为用户创建新的访问密钥对</li>
 *   <li>API Key 查询：查看当前用户的所有密钥列表</li>
 *   <li>状态管理：启用或禁用指定的 API Key</li>
 *   <li>API Key 删除：永久删除不再使用的密钥</li>
 * </ul>
 *
 * <h2>包结构</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.apikey.api} - API 接口定义层</li>
 *   <li>{@link io.dataease.api.permissions.apikey.dto} - 数据传输对象（请求参数）</li>
 *   <li>{@link io.dataease.api.permissions.apikey.vo} - 视图对象（响应数据）</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <ol>
 *   <li><strong>外部系统集成</strong>：第三方应用通过 API Key 调用 DataEase API</li>
 *   <li><strong>自动化脚本</strong>：定时任务或脚本使用 API Key 进行数据操作</li>
 *   <li><strong>移动应用</strong>：移动端 APP 通过 API Key 访问后端服务</li>
 *   <li><strong>微服务调用</strong>：内部微服务间的身份认证</li>
 * </ol>
 *
 * <h2>实现位置</h2>
 * <p>本模块的接口实现位于：</p>
 * <ul>
 *   <li><strong>单机版/社区版</strong>: {@code core-backend/src/main/java/io/dataease/commons/permission/controller/ApiKeyController}</li>
 *   <li><strong>企业版</strong>: 分布式模块中的对应实现</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>1. 生成 API Key</h3>
 * <pre>{@code
 * // 前端调用示例
 * POST /api/apikey/generate
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *
 * Response:
 * {
 *   "id": 123456789,
 *   "accessKey": "ak_xxxxxxxxxxxxxxxxxxxxxxxx",
 *   "accessSecret": "sk_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
 *   "enable": true,
 *   "createTime": 1705123456789
 * }
 * }</pre>
 *
 * <h3>2. 查询 API Key 列表</h3>
 * <pre>{@code
 * GET /api/apikey/query
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *
 * Response:
 * [
 *   {
 *     "id": 123456789,
 *     "accessKey": "ak_xxxxxxxxxxxxxxxxxxxxxxxx",
 *     "accessSecret": null,  // 列表查询不返回 secret
 *     "enable": true,
 *     "createTime": 1705123456789
 *   }
 * ]
 * }</pre>
 *
 * <h3>3. 切换 API Key 状态</h3>
 * <pre>{@code
 * POST /api/apikey/switch
 * Headers:
 *   Authorization: Bearer {jwt_token}
 *   Content-Type: application/json
 *
 * Request Body:
 * {
 *   "id": 123456789,
 *   "enable": false  // 禁用该 Key
 * }
 * }</pre>
 *
 * <h3>4. 删除 API Key</h3>
 * <pre>{@code
 * POST /api/apikey/delete/123456789
 * Headers:
 *   Authorization: Bearer {jwt_token}
 * }</pre>
 *
 * <h3>5. 使用 API Key 调用其他 API</h3>
 * <pre>{@code
 * GET /api/dataset/list
 * Headers:
 *   Access-Key: ak_xxxxxxxxxxxxxxxxxxxxxxxx
 *   Access-Secret: sk_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 * }</pre>
 *
 * <h2>安全说明</h2>
 * <ul>
 *   <li><strong>密钥存储</strong>：accessSecret 在数据库中加密存储，无法逆向解密</li>
 *   <li><strong>传输安全</strong>：建议使用 HTTPS 协议传输密钥</li>
 *   <li><strong>权限隔离</strong>：用户只能管理自己的 API Key</li>
 *   <li><strong>密钥轮换</strong>：建议定期更换 API Key，旧 Key 可以先禁用再删除</li>
 *   <li><strong>泄露处理</strong>：如发现密钥泄露，立即禁用或删除该 Key</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>accessSecret 仅在生成时返回一次，请妥善保管</li>
 *   <li>禁用的 API Key 可以重新启用，删除的 Key 无法恢复</li>
 *   <li>每个用户可以创建多个 API Key，用于不同的应用场景</li>
 *   <li>API Key 的权限与用户权限一致，不能超出用户的权限范围</li>
 * </ol>
 *
 * @author DataEase Team
 * @since 1.0
 */
package io.dataease.api.permissions.apikey;
