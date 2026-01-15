/**
 * 系统变量管理数据传输对象包
 *
 * <p>本包包含系统变量管理模块的所有 DTO (Data Transfer Object) 类,
 * 用于封装系统变量相关的请求参数和数据传输。</p>
 *
 * <h2>DTO 分类</h2>
 *
 * <h3>1. 变量定义类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.variable.dto.SysVariableDto} - 系统变量定义数据传输对象
 *     <ul>
 *       <li>字段: id、name、displayName、type、defaultValue、minValue、maxValue、description、scope、required</li>
 *       <li>用途: 变量定义的创建、编辑、查询</li>
 *       <li>特点: 同时用于请求和响应</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>2. 变量值类</h3>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.variable.dto.SysVariableValueDto} - 变量值数据传输对象
 *     <ul>
 *       <li>字段: id、variableId、value、displayValue、sortOrder、description、enabled</li>
 *       <li>用途: 变量值的创建、编辑、查询</li>
 *       <li>特点: 用于枚举类型变量的可选值管理</li>
 *     </ul>
 *   </li>
 *   <li>{@link io.dataease.api.permissions.variable.dto.SysVariableValueItem} - 变量值选项
 *     <ul>
 *       <li>字段: value、displayValue、description</li>
 *       <li>用途: 轻量级的变量值选项,用于下拉选择等场景</li>
 *       <li>特点: 简化版的 SysVariableValueDto</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 DTO 类实现 {@link java.io.Serializable} 接口</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化代码</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>字段命名遵循驼峰命名规范</li>
 *   <li>使用 JSR-303 验证注解确保数据合法性</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 *
 * <h3>1. 创建系统变量</h3>
 * <pre>{@code
 * SysVariableDto dto = new SysVariableDto();
 * dto.setName("report_retention_days");
 * dto.setDisplayName("报表保留天数");
 * dto.setType("NUMBER");
 * dto.setDefaultValue("90");
 * dto.setMinValue(30);
 * dto.setMaxValue(365);
 * dto.setDescription("报表数据在系统中的保留天数");
 * dto.setScope("SYSTEM");
 * dto.setRequired(false);
 *
 * SysVariableDto result = sysVariablesApi.create(dto);
 * }</pre>
 *
 * <h3>2. 编辑系统变量</h3>
 * <pre>{@code
 * SysVariableDto dto = new SysVariableDto();
 * dto.setId(1001L);
 * dto.setDisplayName("报表数据保留天数");
 * dto.setDefaultValue("120");
 * dto.setMinValue(60);
 * dto.setMaxValue(730);
 * dto.setDescription("报表数据在系统中的保留天数,超过此天数的数据将被自动清理");
 *
 * SysVariableDto result = sysVariablesApi.edit(dto);
 * }</pre>
 *
 * <h3>3. 查询系统变量列表</h3>
 * <pre>{@code
 * SysVariableDto queryDto = new SysVariableDto();
 * queryDto.setType("NUMBER");        // 按类型筛选
 * queryDto.setScope("SYSTEM");       // 按作用域筛选
 * queryDto.setKeyword("report");     // 关键字搜索
 *
 * List<SysVariableDto> variables = sysVariablesApi.query(queryDto);
 * }</pre>
 *
 * <h3>4. 创建变量值</h3>
 * <pre>{@code
 * SysVariableValueDto valueDto = new SysVariableValueDto();
 * valueDto.setVariableId(1002L);
 * valueDto.setValue("PDF");
 * valueDto.setDisplayValue("PDF格式");
 * valueDto.setSortOrder(1);
 * valueDto.setDescription("导出为PDF格式文件");
 * valueDto.setEnabled(true);
 *
 * SysVariableValueDto result = sysVariablesApi.createValue(valueDto);
 * }</pre>
 *
 * <h3>5. 分页查询变量值</h3>
 * <pre>{@code
 * SysVariableValueDto queryDto = new SysVariableValueDto();
 * queryDto.setVariableId(1002L);
 * queryDto.setEnabled(true);  // 只查询启用的值
 *
 * IPage<SysVariableValueDto> page = sysVariablesApi.selectPage(1, 20, queryDto);
 * }</pre>
 *
 * <h2>字段详细说明</h2>
 *
 * <h3>SysVariableDto 字段说明</h3>
 * <ul>
 *   <li><strong>id</strong>: 变量唯一标识,编辑时必填</li>
 *   <li><strong>name</strong>: 变量名称,系统内唯一标识
 *     <ul>
 *       <li>只能包含字母、数字和下划线</li>
 *       <li>建议使用小写字母和下划线</li>
 *       <li>例如: {@code report_retention_days}</li>
 *     </ul>
 *   </li>
 *   <li><strong>displayName</strong>: 显示名称,用于界面展示
 *     <ul>
 *       <li>可以使用中文或其他语言</li>
 *       <li>例如: "报表保留天数"</li>
 *     </ul>
 *   </li>
 *   <li><strong>type</strong>: 变量类型
 *     <ul>
 *       <li>TEXT: 文本类型</li>
 *       <li>NUMBER: 数值类型</li>
 *       <li>DATE: 日期类型</li>
 *       <li>ENUM: 枚举类型</li>
 *     </ul>
 *   </li>
 *   <li><strong>defaultValue</strong>: 默认值
 *     <ul>
 *       <li>所有类型都存储为字符串</li>
 *       <li>必须符合类型和值域限制</li>
 *     </ul>
 *   </li>
 *   <li><strong>minValue</strong>: 最小值,数值类型专用</li>
 *   <li><strong>maxValue</strong>: 最大值,数值类型专用</li>
 *   <li><strong>minLength</strong>: 最小长度,文本类型专用</li>
 *   <li><strong>maxLength</strong>: 最大长度,文本类型专用</li>
 *   <li><strong>minDate</strong>: 最小日期,日期类型专用</li>
 *   <li><strong>maxDate</strong>: 最大日期,日期类型专用</li>
 *   <li><strong>description</strong>: 变量描述说明</li>
 *   <li><strong>scope</strong>: 变量作用域
 *     <ul>
 *       <li>SYSTEM: 系统级,全局共享</li>
 *       <li>ORG: 组织级,组织内共享</li>
 *       <li>USER: 用户级,每个用户独立</li>
 *     </ul>
 *   </li>
 *   <li><strong>required</strong>: 是否必填
 *     <ul>
 *       <li>true: 用户必须设置值</li>
 *       <li>false: 用户可以不设置,使用默认值</li>
 *     </ul>
 *   </li>
 *   <li><strong>keyword</strong>: 查询用关键字,非持久化字段</li>
 * </ul>
 *
 * <h3>SysVariableValueDto 字段说明</h3>
 * <ul>
 *   <li><strong>id</strong>: 变量值唯一标识</li>
 *   <li><strong>variableId</strong>: 所属变量的ID</li>
 *   <li><strong>value</strong>: 变量值的实际值
 *     <ul>
 *       <li>用于程序内部引用</li>
 *       <li>例如: "PDF"</li>
 *     </ul>
 *   </li>
 *   <li><strong>displayValue</strong>: 变量值的显示名称
 *     <ul>
 *       <li>用于界面展示</li>
 *       <li>例如: "PDF格式"</li>
 *     </ul>
 *   </li>
 *   <li><strong>sortOrder</strong>: 排序序号,控制显示顺序</li>
 *   <li><strong>description</strong>: 变量值的描述说明</li>
 *   <li><strong>enabled</strong>: 是否启用
 *     <ul>
 *       <li>true: 启用,用户可以选择</li>
 *       <li>false: 禁用,用户不可选择</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>SysVariableValueItem 字段说明</h3>
 * <ul>
 *   <li><strong>value</strong>: 变量值的实际值</li>
 *   <li><strong>displayValue</strong>: 变量值的显示名称</li>
 *   <li><strong>description</strong>: 变量值的描述说明</li>
 * </ul>
 *
 * <h2>字段验证</h2>
 * <p>DTO 类使用 JSR-303 验证注解确保数据合法性:</p>
 *
 * <h3>SysVariableDto 验证示例</h3>
 * <pre>{@code
 * public class SysVariableDto {
 *     @NotBlank(message = "变量名称不能为空")
 *     @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "变量名称只能包含小写字母、数字和下划线,且必须以字母开头")
 *     @Size(max = 50, message = "变量名称长度不能超过50个字符")
 *     private String name;
 *
 *     @NotBlank(message = "显示名称不能为空")
 *     @Size(max = 100, message = "显示名称长度不能超过100个字符")
 *     private String displayName;
 *
 *     @NotBlank(message = "变量类型不能为空")
 *     @Pattern(regexp = "^(TEXT|NUMBER|DATE|ENUM)$", message = "变量类型不合法")
 *     private String type;
 *
 *     @NotBlank(message = "默认值不能为空")
 *     private String defaultValue;
 *
 *     @Size(max = 500, message = "描述长度不能超过500个字符")
 *     private String description;
 * }
 * }</pre>
 *
 * <h3>SysVariableValueDto 验证示例</h3>
 * <pre>{@code
 * public class SysVariableValueDto {
 *     @NotNull(message = "变量ID不能为空")
 *     private Long variableId;
 *
 *     @NotBlank(message = "变量值不能为空")
 *     @Size(max = 100, message = "变量值长度不能超过100个字符")
 *     private String value;
 *
 *     @NotBlank(message = "显示值不能为空")
 *     @Size(max = 100, message = "显示值长度不能超过100个字符")
 *     private String displayValue;
 *
 *     @Min(value = 0, message = "排序序号不能为负数")
 *     private Integer sortOrder;
 * }
 * }</pre>
 *
 * <h2>类型特定字段</h2>
 * <p>不同类型的变量使用不同的字段:</p>
 *
 * <h3>TEXT 类型</h3>
 * <ul>
 *   <li>minLength: 最小长度</li>
 *   <li>maxLength: 最大长度</li>
 *   <li>pattern: 正则表达式验证</li>
 * </ul>
 *
 * <h3>NUMBER 类型</h3>
 * <ul>
 *   <li>minValue: 最小值</li>
 *   <li>maxValue: 最大值</li>
 *   <li>precision: 小数位数</li>
 * </ul>
 *
 * <h3>DATE 类型</h3>
 * <ul>
 *   <li>minDate: 最小日期</li>
 *   <li>maxDate: 最大日期</li>
 *   <li>format: 日期格式</li>
 * </ul>
 *
 * <h3>ENUM 类型</h3>
 * <ul>
 *   <li>需要配合 SysVariableValueDto 使用</li>
 *   <li>通过变量值定义可选项</li>
 *   <li>multiSelect: 是否支持多选</li>
 * </ul>
 *
 * <h2>JSON 序列化</h2>
 * <p>DTO 对象使用 Jackson 进行 JSON 序列化:</p>
 *
 * <h3>忽略 null 值</h3>
 * <pre>{@code
 * @JsonInclude(JsonInclude.Include.NON_NULL)
 * public class SysVariableDto {
 *     private Long id;
 *     private String name;
 *     private Integer minValue;  // null 时不包含在 JSON 中
 *     private Integer maxValue;  // null 时不包含在 JSON 中
 * }
 * }</pre>
 *
 * <h3>日期格式化</h3>
 * <pre>{@code
 * public class SysVariableDto {
 *     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
 *     private Date createTime;
 *
 *     // 或使用时间戳
 *     private Long createTime;
 * }
 * }</pre>
 *
 * <h2>最佳实践</h2>
 * <ul>
 *   <li><strong>参数校验</strong>: 在 Controller 层使用 {@code @Valid} 注解触发参数校验</li>
 *   <li><strong>空值处理</strong>: 可选字段使用包装类型,必填字段可以使用基本类型</li>
 *   <li><strong>类型转换</strong>: 提供工具方法进行类型转换和验证</li>
 *   <li><strong>文档完善</strong>: 使用 {@code @Schema} 注解为每个字段提供清晰说明</li>
 *   <li><strong>默认值</strong>: 为可选字段设置合理的默认值</li>
 * </ul>
 *
 * <h2>数据转换工具</h2>
 * <p>提供工具方法进行类型转换和验证:</p>
 * <pre>{@code
 * public class SysVariableDto {
 *     // 将字符串默认值转换为指定类型
 *     public Object getTypedDefaultValue() {
 *         switch (type) {
 *             case "NUMBER":
 *                 return Double.parseDouble(defaultValue);
 *             case "DATE":
 *                 return LocalDate.parse(defaultValue);
 *             case "TEXT":
 *             case "ENUM":
 *             default:
 *                 return defaultValue;
 *         }
 *     }
 *
 *     // 验证值是否符合类型和范围
 *     public boolean isValidValue(String value) {
 *         switch (type) {
 *             case "NUMBER":
 *                 try {
 *                     double num = Double.parseDouble(value);
 *                     if (minValue != null && num < minValue) return false;
 *                     if (maxValue != null && num > maxValue) return false;
 *                     return true;
 *                 } catch (NumberFormatException e) {
 *                     return false;
 *                 }
 *             case "TEXT":
 *                 if (minLength != null && value.length() < minLength) return false;
 *                 if (maxLength != null && value.length() > maxLength) return false;
 *                 return true;
 *             default:
 *                 return true;
 *         }
 *     }
 * }
 * }</pre>
 *
 * <h2>注意事项</h2>
 * <ol>
 *   <li>DTO 对象只用于数据传输,不包含复杂业务逻辑</li>
 *   <li>变量名一旦创建建议不要修改</li>
 *   <li>修改 DTO 结构时需要考虑前后端兼容性</li>
 *   <li>类型相关的字段应该根据 type 字段有选择地使用</li>
 *   <li>defaultValue 必须符合类型和值域限制</li>
 *   <li>ENUM 类型的变量必须配合变量值使用</li>
 * </ol>
 *
 * @see io.dataease.api.permissions.variable.api
 * @since 1.0
 */
package io.dataease.api.permissions.variable.dto;
