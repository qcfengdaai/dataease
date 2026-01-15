/**
 * 权限设置视图对象包
 *
 * <p>本包包含权限设置模块的所有 VO (View Object) 类，
 * 用于封装返回给客户端的配置数据。VO 对象是数据展示层对象，通常包含格式化后的数据。</p>
 *
 * <h2>VO 列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.setting.vo.PerSettingItemVO} - 权限设置项视图对象</li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 VO 类实现 {@link java.io.Serializable} 接口，支持序列化</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化 getter/setter</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>字段类型统一为字符串，便于前端处理（前端根据 type 字段转换类型）</li>
 *   <li>包含必要的元数据（如配置项类型、描述等）</li>
 * </ul>
 *
 * <h2>VO 详细说明</h2>
 *
 * <h3>PerSettingItemVO - 权限设置项视图对象</h3>
 * <p>用于表示单个配置项的完整信息：</p>
 * <pre>
 * {
 *   "key": "password.min.length",      // 配置项 key（唯一标识）
 *   "value": "8",                      // 配置项 value（字符串形式）
 *   "type": "INTEGER",                 // 配置项类型
 *   "category": "basic",               // 配置分类
 *   "description": "密码最小长度",      // 配置说明（用于前端展示）
 *   "defaultValue": "6",               // 默认值（可选）
 *   "required": true,                  // 是否必填（可选）
 *   "editable": true,                  // 是否可编辑（可选）
 *   "options": ["6", "8", "10", "12"]  // 可选值列表（可选，用于下拉选择）
 * }
 * </pre>
 *
 * <h3>字段说明</h3>
 * <table border="1">
 *   <tr>
 *     <th>字段名</th>
 *     <th>类型</th>
 *     <th>说明</th>
 *   </tr>
 *   <tr>
 *     <td>key</td>
 *     <td>String</td>
 *     <td>配置项的唯一标识，采用点分隔符（如 password.min.length）</td>
 *   </tr>
 *   <tr>
 *     <td>value</td>
 *     <td>String</td>
 *     <td>配置项的值，统一为字符串类型（前端根据 type 字段转换）</td>
 *   </tr>
 *   <tr>
 *     <td>type</td>
 *     <td>String</td>
 *     <td>配置项的数据类型（STRING, INTEGER, BOOLEAN, ENUM 等）</td>
 *   </tr>
 *   <tr>
 *     <td>category</td>
 *     <td>String</td>
 *     <td>配置分类（basic, mfa 等）</td>
 *   </tr>
 *   <tr>
 *     <td>description</td>
 *     <td>String</td>
 *     <td>配置项的中文说明，用于前端展示</td>
 *   </tr>
 *   <tr>
 *     <td>defaultValue</td>
 *     <td>String</td>
 *     <td>配置项的默认值（可选）</td>
 *   </tr>
 *   <tr>
 *     <td>required</td>
 *     <td>Boolean</td>
 *     <td>是否为必填项（可选）</td>
 *   </tr>
 *   <tr>
 *     <td>editable</td>
 *     <td>Boolean</td>
 *     <td>是否可编辑（可选，某些系统级配置可能不可编辑）</td>
 *   </tr>
 *   <tr>
 *     <td>options</td>
 *     <td>List&lt;String&gt;</td>
 *     <td>可选值列表（可选，用于前端下拉选择或单选按钮）</td>
 *   </tr>
 * </table>
 *
 * <h2>配置项类型</h2>
 * <p>type 字段支持的数据类型：</p>
 * <ul>
 *   <li><strong>STRING</strong>: 字符串类型
 *       <ul>
 *         <li>前端渲染：文本输入框或文本域</li>
 *         <li>示例：mfa.methods = "totp,sms,email"</li>
 *       </ul>
 *   </li>
 *   <li><strong>INTEGER</strong>: 整数类型
 *       <ul>
 *         <li>前端渲染：数字输入框</li>
 *         <li>示例：password.min.length = "8"</li>
 *       </ul>
 *   </li>
 *   <li><strong>BOOLEAN</strong>: 布尔类型
 *       <ul>
 *         <li>前端渲染：开关（Switch）或复选框（Checkbox）</li>
 *         <li>示例：mfa.enabled = "true"</li>
 *       </ul>
 *   </li>
 *   <li><strong>ENUM</strong>: 枚举类型
 *       <ul>
 *         <li>前端渲染：下拉选择或单选按钮</li>
 *         <li>配合 options 字段使用</li>
 *         <li>示例：log.level = "INFO", options = ["DEBUG", "INFO", "WARN", "ERROR"]</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <p>VO 对象主要用于：</p>
 * <ol>
 *   <li>封装配置项的完整信息，返回给前端</li>
 *   <li>前端根据 VO 的元数据动态渲染配置表单</li>
 *   <li>前端进行配置项的类型转换和验证</li>
 * </ol>
 *
 * <h2>前端使用示例</h2>
 *
 * <h3>示例 1: 动态渲染配置表单</h3>
 * <pre>{@code
 * // Vue 3 组件示例
 * <template>
 *   <el-form :model="settings" label-width="200px">
 *     <el-form-item
 *       v-for="item in settingItems"
 *       :key="item.key"
 *       :label="item.description"
 *     >
 *       <!-- 字符串类型：文本输入框 -->
 *       <el-input
 *         v-if="item.type === 'STRING'"
 *         v-model="item.value"
 *       />
 *
 *       <!-- 整数类型：数字输入框 -->
 *       <el-input-number
 *         v-else-if="item.type === 'INTEGER'"
 *         v-model.number="item.value"
 *       />
 *
 *       <!-- 布尔类型：开关 -->
 *       <el-switch
 *         v-else-if="item.type === 'BOOLEAN'"
 *         v-model="item.value"
 *         active-value="true"
 *         inactive-value="false"
 *       />
 *
 *       <!-- 枚举类型：下拉选择 -->
 *       <el-select
 *         v-else-if="item.type === 'ENUM'"
 *         v-model="item.value"
 *       >
 *         <el-option
 *           v-for="option in item.options"
 *           :key="option"
 *           :label="option"
 *           :value="option"
 *         />
 *       </el-select>
 *     </el-form-item>
 *
 *     <el-form-item>
 *       <el-button type="primary" @click="handleSave">保存</el-button>
 *     </el-form-item>
 *   </el-form>
 * </template>
 *
 * <script setup>
 * import { ref, onMounted } from 'vue'
 * import { getBasicSetting, saveBasicSetting } from '@/api/setting'
 *
 * const settingItems = ref([])
 *
 * onMounted(async () => {
 *   // 加载配置项
 *   settingItems.value = await getBasicSetting()
 * })
 *
 * const handleSave = async () => {
 *   // 保存配置
 *   await saveBasicSetting(settingItems.value)
 *   ElMessage.success('保存成功')
 * }
 * </script>
 * }</pre>
 *
 * <h3>示例 2: 类型转换</h3>
 * <pre>{@code
 * // JavaScript/TypeScript 示例
 * function parseSettingValue(item: PerSettingItemVO): any {
 *   const { value, type } = item
 *
 *   switch (type) {
 *     case 'INTEGER':
 *       return parseInt(value, 10)
 *     case 'BOOLEAN':
 *       return value === 'true'
 *     case 'STRING':
 *     case 'ENUM':
 *     default:
 *       return value
 *   }
 * }
 *
 * // 使用
 * const settings = await getBasicSetting()
 * const passwordMinLength = parseSettingValue(
 *   settings.find(s => s.key === 'password.min.length')
 * ) // 返回 number 类型
 *
 * const mfaEnabled = parseSettingValue(
 *   settings.find(s => s.key === 'mfa.enabled')
 * ) // 返回 boolean 类型
 * }</pre>
 *
 * <h3>示例 3: 配置项分组展示</h3>
 * <pre>{@code
 * // 前端根据 category 分组展示
 * <el-tabs v-model="activeCategory">
 *   <el-tab-pane label="基础认证" name="basic">
 *     <SettingForm :items="basicSettings" @save="handleSaveBasic" />
 *   </el-tab-pane>
 *
 *   <el-tab-pane label="多因素认证" name="mfa">
 *     <SettingForm :items="mfaSettings" @save="handleSaveMfa" />
 *   </el-tab-pane>
 * </el-tabs>
 *
 * <script setup>
 * const settingItems = ref([])
 *
 * const basicSettings = computed(() =>
 *   settingItems.value.filter(item => item.category === 'basic')
 * )
 *
 * const mfaSettings = computed(() =>
 *   settingItems.value.filter(item => item.category === 'mfa')
 * )
 * </script>
 * }</pre>
 *
 * <h2>VO 与实体的区别</h2>
 * <ul>
 *   <li><strong>实体(Entity)</strong>：与数据库表一一对应，包含所有数据库字段</li>
 *   <li><strong>VO</strong>：面向前端展示，包含额外的元数据（如 description、type、options）</li>
 * </ul>
 *
 * <h2>数据转换</h2>
 * <p>后端需要将实体（Entity）转换为 VO：</p>
 * <pre>{@code
 * public List<PerSettingItemVO> getBasicSetting() {
 *     // 1. 从数据库查询配置项
 *     List<SysSetting> entities = settingMapper.selectByCategory("basic");
 *
 *     // 2. 转换为 VO
 *     return entities.stream()
 *         .map(entity -> {
 *             PerSettingItemVO vo = new PerSettingItemVO();
 *             vo.setKey(entity.getKey());
 *             vo.setValue(entity.getValue());
 *             vo.setType(entity.getType());
 *             vo.setCategory(entity.getCategory());
 *             vo.setDescription(entity.getDescription());
 *             // 可以在这里添加额外的元数据
 *             if ("password.min.length".equals(entity.getKey())) {
 *                 vo.setOptions(Arrays.asList("6", "8", "10", "12", "16"));
 *             }
 *             return vo;
 *         })
 *         .collect(Collectors.toList());
 * }
 * }</pre>
 *
 * <h2>JSON 序列化示例</h2>
 * <pre>
 * // 基础认证设置响应示例
 * [
 *   {
 *     "key": "password.min.length",
 *     "value": "8",
 *     "type": "INTEGER",
 *     "category": "basic",
 *     "description": "密码最小长度",
 *     "options": ["6", "8", "10", "12", "16"]
 *   },
 *   {
 *     "key": "password.require.special.char",
 *     "value": "true",
 *     "type": "BOOLEAN",
 *     "category": "basic",
 *     "description": "密码必须包含特殊字符"
 *   },
 *   {
 *     "key": "session.timeout.minutes",
 *     "value": "30",
 *     "type": "INTEGER",
 *     "category": "basic",
 *     "description": "会话超时时间（分钟）"
 *   }
 * ]
 * </pre>
 *
 * <h2>扩展建议</h2>
 * <p>新增 VO 类时应遵循以下规范：</p>
 * <ol>
 *   <li>类名以业务含义命名，后缀为 VO</li>
 *   <li>所有字段添加 {@code @Schema} 注解说明字段含义</li>
 *   <li>提供清晰的类注释，说明数据结构和使用场景</li>
 *   <li>如果配置项有可选值，提供 options 字段</li>
 *   <li>如果配置项有默认值，提供 defaultValue 字段</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.setting.api
 * @since 1.0
 */
package io.dataease.api.permissions.setting.vo;
