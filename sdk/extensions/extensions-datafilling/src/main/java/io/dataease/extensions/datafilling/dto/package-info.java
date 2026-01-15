/**
 * 数据填报扩展DTO包
 * <p>
 * 本包包含数据填报模块的数据传输对象（Data Transfer Object），用于封装表单字段配置、索引配置、
 * 表单设置等业务数据。这些DTO类在前后端之间传递数据，以及在不同服务层之间传递业务对象。
 * </p>
 *
 * <p><b>核心DTO类：</b></p>
 * <ul>
 *   <li>{@link io.dataease.extensions.datafilling.dto.ExtTableField} - 表单字段配置DTO，定义字段类型、显示设置、数据映射等</li>
 *   <li>{@link io.dataease.extensions.datafilling.dto.ExtIndexField} - 索引字段配置DTO，定义数据库表的索引信息</li>
 *   <li>{@link io.dataease.extensions.datafilling.dto.ExtFormSettings} - 表单全局设置DTO，配置表单级别的规则和约束</li>
 *   <li>{@link io.dataease.extensions.datafilling.dto.ExtraColumnItem} - 额外列配置项DTO，用于选项字段的额外列显示</li>
 * </ul>
 *
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li><b>表单配置</b> - 前端设计器配置表单字段时使用ExtTableField传递字段信息</li>
 *   <li><b>表结构生成</b> - 后端根据ExtTableField生成数据库表结构（DDL语句）</li>
 *   <li><b>索引管理</b> - 使用ExtIndexField管理数据表的索引，优化查询性能</li>
 *   <li><b>表单验证</b> - 使用ExtFormSettings配置全局验证规则，如数字输入规则</li>
 *   <li><b>选项配置</b> - 使用ExtraColumnItem配置下拉选项字段的额外显示列</li>
 * </ul>
 *
 * <p><b>数据流转示例：</b></p>
 * <pre>
 * // 1. 前端设计器配置表单字段
 * ExtTableField field = ExtTableField.builder()
 *     .type("input")
 *     .typeName("单行文本")
 *     .settings(ExtTableFieldSetting.builder()
 *         .name("userName")
 *         .required(true)
 *         .mapping(ExtTableFieldMapping.builder()
 *             .columnName("user_name")
 *             .type(ExtTableField.BaseType.nvarchar)
 *             .size(100)
 *             .build())
 *         .build())
 *     .build();
 *
 * // 2. 后端接收DTO并生成表结构
 * ExtDDLProvider ddlProvider = ExtDDLProviderFactory.getExtDDLProvider("mysql");
 * String createTableSql = ddlProvider.createTableSql("t_user_form", List.of(field));
 * // 生成SQL: CREATE TABLE `t_user_form` (`user_name` VARCHAR(100) NOT NULL, ...)
 *
 * // 3. 创建索引提高查询性能
 * ExtIndexField index = ExtIndexField.builder()
 *     .name("idx_user_name")
 *     .columns(List.of(
 *         ExtIndexField.ColumnSetting.builder()
 *             .column("user_name")
 *             .order("ASC")
 *             .build()
 *     ))
 *     .build();
 * List&lt;String&gt; indexSqls = ddlProvider.createTableIndexSql("t_user_form", List.of(index));
 *
 * // 4. 配置表单全局设置
 * ExtFormSettings settings = ExtFormSettings.builder()
 *     .id("form_001")
 *     .disable(false)
 *     .numberInputRules(List.of(
 *         ExtFormSettings.NumberRule.builder()
 *             .column("age")
 *             .term("&gt;0 AND &lt;=150")
 *             .build()
 *     ))
 *     .build();
 * </pre>
 *
 * <p><b>DTO设计原则：</b></p>
 * <ul>
 *   <li><b>不可变性</b> - 使用Lombok的@Builder模式，支持链式构建</li>
 *   <li><b>序列化支持</b> - 所有DTO都实现Serializable接口，支持缓存和分布式传输</li>
 *   <li><b>嵌套结构</b> - 使用内部类组织复杂的嵌套配置（如ExtTableFieldSetting、ExtTableFieldMapping）</li>
 *   <li><b>类型安全</b> - 使用枚举类型（如BaseType）替代字符串常量，避免拼写错误</li>
 *   <li><b>向后兼容</b> - 使用serialVersionUID确保序列化版本兼容性</li>
 * </ul>
 *
 * <p><b>与其他模块的关系：</b></p>
 * <ul>
 *   <li><b>provider包</b> - ExtDDLProvider使用这些DTO生成数据库DDL语句</li>
 *   <li><b>factory包</b> - ExtDDLProviderFactory根据DTO中的type字段选择对应的provider实现</li>
 *   <li><b>plugin包</b> - DataFillingPlugin实现类需要处理这些DTO并生成数据库特定的SQL</li>
 *   <li><b>core模块</b> - 核心业务模块使用这些DTO与数据填报扩展交互</li>
 * </ul>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>DTO只负责数据传输，不包含业务逻辑</li>
 *   <li>修改DTO字段时需要考虑前后端兼容性和数据库版本升级</li>
 *   <li>ExtTableField的内部类较多，注意区分各自的职责</li>
 *   <li>修改serialVersionUID会导致序列化不兼容，需谨慎处理</li>
 * </ul>
 *
 * @author DataEase
 * @since 2.0.0
 * @see io.dataease.extensions.datafilling.provider.ExtDDLProvider
 * @see io.dataease.extensions.datafilling.factory.ExtDDLProviderFactory
 * @see io.dataease.extensions.datafilling.plugin.DataFillingPlugin
 */
package io.dataease.extensions.datafilling.dto;
