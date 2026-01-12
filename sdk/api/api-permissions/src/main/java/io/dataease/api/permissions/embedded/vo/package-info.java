/**
 * 嵌入式集成视图对象包
 *
 * <p>本包包含嵌入式集成管理模块的所有 VO (View Object) 类,
 * 用于封装返回给客户端的响应数据。VO 对象是数据展示层对象,通常包含格式化后的数据。</p>
 *
 * <h2>VO 列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.embedded.vo.EmbeddedGridVO} - 嵌入式应用列表视图
 *     <ul>
 *       <li>用于分页列表展示</li>
 *       <li>包含应用 ID、名称、AppId、域名等信息</li>
 *       <li>不包含 AppSecret(安全考虑)</li>
 *       <li>包含创建时间、更新时间等元数据</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 VO 类实现 {@link java.io.Serializable} 接口,支持序列化</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化 getter/setter</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>Long 类型的 ID 使用 {@code @JsonSerialize(using=ToStringSerializer.class)} 避免前端精度丢失</li>
 *   <li>敏感字段(如 AppSecret)不在列表视图中返回</li>
 *   <li>时间字段使用 Long 类型的时间戳,便于前端格式化</li>
 * </ul>
 *
 * <h2>VO 与实体的区别</h2>
 * <ul>
 *   <li><strong>实体(Entity)</strong>:与数据库表一一对应,包含所有数据库字段</li>
 *   <li><strong>VO</strong>:面向前端展示,可能包含多个实体的组合数据,或对敏感字段进行脱敏</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <p>VO 对象主要用于:</p>
 * <ol>
 *   <li>封装嵌入式应用列表的响应数据</li>
 *   <li>对敏感字段(AppSecret)进行脱敏或过滤</li>
 *   <li>组合多个实体的数据(如关联域名信息)</li>
 *   <li>对数据进行格式化展示(如时间格式化)</li>
 * </ol>
 *
 * <h2>数据流转</h2>
 * <pre>
 * 数据库 → Entity 实体 → Service 处理 → VO 对象 → Controller 返回 → 前端展示
 * </pre>
 *
 * <h2>敏感字段处理</h2>
 * <p>嵌入式应用的 AppSecret 是高度敏感的字段,处理规则:</p>
 * <ul>
 *   <li><strong>创建时</strong>:返回完整的 AppSecret,仅此一次</li>
 *   <li><strong>重置时</strong>:返回新的 AppSecret,仅此一次</li>
 *   <li><strong>列表查询</strong>:不返回 AppSecret,字段为 null</li>
 *   <li><strong>详情查询</strong>:不返回 AppSecret,字段为 null</li>
 * </ul>
 *
 * <h2>列表视图示例</h2>
 * <pre>{@code
 * @Data
 * @Schema(description = "嵌入式应用列表视图")
 * public class EmbeddedGridVO implements Serializable {
 *
 *     @JsonSerialize(using = ToStringSerializer.class)
 *     @Schema(description = "应用ID")
 *     private Long id;
 *
 *     @Schema(description = "应用名称")
 *     private String name;
 *
 *     @Schema(description = "AppId")
 *     private String appId;
 *
 *     // AppSecret 不在列表中返回,确保安全
 *     // private String appSecret;
 *
 *     @Schema(description = "域名白名单")
 *     private List<String> domains;
 *
 *     @Schema(description = "备注")
 *     private String remark;
 *
 *     @Schema(description = "创建时间(时间戳)")
 *     private Long createTime;
 *
 *     @Schema(description = "更新时间(时间戳)")
 *     private Long updateTime;
 *
 *     @Schema(description = "创建者")
 *     private String creator;
 * }
 * }</pre>
 *
 * <h2>前端展示示例</h2>
 * <pre>{@code
 * // Vue 3 示例
 * <template>
 *   <el-table :data="embeddedList">
 *     <el-table-column prop="name" label="应用名称" />
 *     <el-table-column prop="appId" label="AppId" />
 *     <el-table-column prop="domains" label="域名白名单">
 *       <template #default="{ row }">
 *         <el-tag v-for="domain in row.domains" :key="domain">
 *           {{ domain }}
 *         </el-tag>
 *       </template>
 *     </el-table-column>
 *     <el-table-column prop="createTime" label="创建时间">
 *       <template #default="{ row }">
 *         {{ formatTime(row.createTime) }}
 *       </template>
 *     </el-table-column>
 *   </el-table>
 * </template>
 * }</pre>
 *
 * <h2>JSON 序列化配置</h2>
 * <p>Long 类型 ID 的序列化处理:</p>
 * <ul>
 *   <li><strong>问题</strong>:JavaScript 的 Number 类型最大安全整数为 2^53-1,超过会丢失精度</li>
 *   <li><strong>解决</strong>:使用 {@code @JsonSerialize(using=ToStringSerializer.class)} 将 Long 转为字符串</li>
 *   <li><strong>前端</strong>:前端收到的 ID 是字符串类型,需要按字符串处理</li>
 * </ul>
 *
 * <pre>{@code
 * // 后端返回
 * {
 *   "id": "1234567890123456789",  // 字符串类型
 *   "name": "OA系统集成"
 * }
 *
 * // 前端使用
 * const id = row.id;  // 字符串 "1234567890123456789"
 * api.delete(`/api/embedded/delete/${id}`);  // 直接使用,不转数字
 * }</pre>
 *
 * <h2>域名列表展示</h2>
 * <p>域名白名单通常有多个,展示方式:</p>
 * <ul>
 *   <li><strong>标签展示</strong>:使用 Tag 组件展示多个域名</li>
 *   <li><strong>换行展示</strong>:每个域名一行</li>
 *   <li><strong>折叠展示</strong>:超过 N 个域名时折叠,点击展开</li>
 *   <li><strong>Tooltip 展示</strong>:鼠标悬停显示完整列表</li>
 * </ul>
 *
 * @see io.dataease.api.permissions.embedded.api
 * @see io.dataease.api.permissions.embedded.dto
 * @since 2.0
 */
package io.dataease.api.permissions.embedded.vo;
