/**
 * 数据填报扩展模块
 * <p>
 * 数据填报（Data Filling）是DataEase的企业版功能，允许用户通过Web表单收集数据并存储到数据库中。
 * 本模块提供数据填报功能的扩展接口和抽象实现，支持多种数据库类型的表单数据管理。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li><b>表单设计</b> - 通过拖拽式界面设计数据填报表单，配置字段类型、验证规则等</li>
 *   <li><b>自动建表</b> - 根据表单配置自动生成数据库表结构（DDL）</li>
 *   <li><b>数据收集</b> - 用户填写表单，数据自动保存到数据库</li>
 *   <li><b>数据查询</b> - 查询和展示已填报的数据，支持筛选、分页</li>
 *   <li><b>数据导出</b> - 将填报数据导出为Excel等格式</li>
 *   <li><b>多数据库支持</b> - 支持MySQL、Oracle、SQL Server、PostgreSQL等主流数据库</li>
 * </ul>
 *
 * <p><b>模块架构：</b></p>
 * <pre>
 * ┌─────────────────────────────────────────────────────────────┐
 * │                      数据填报模块架构                          │
 * ├─────────────────────────────────────────────────────────────┤
 * │                                                             │
 * │  前端表单设计器                                               │
 * │  ┌──────────────────────────────────────────────┐          │
 * │  │ 字段配置 │ 验证规则 │ 样式设置 │ 数据源选择  │          │
 * │  └──────────────────────────────────────────────┘          │
 * │                        ↓                                     │
 * │  API层 (api包)                                              │
 * │  ┌──────────────────────────────────────────────┐          │
 * │  │ DfPluginManageApi - 插件管理接口               │          │
 * │  └──────────────────────────────────────────────┘          │
 * │                        ↓                                     │
 * │  工厂层 (factory包)                                         │
 * │  ┌──────────────────────────────────────────────┐          │
 * │  │ ExtDDLProviderFactory - DDL提供者工厂          │          │
 * │  │   ┌──────────┬──────────┬──────────┐        │          │
 * │  │   │  MySQL   │  Oracle  │SqlServer │        │          │
 * │  │   │ Provider │ Provider │ Provider │        │          │
 * │  │   └──────────┴──────────┴──────────┘        │          │
 * │  └──────────────────────────────────────────────┘          │
 * │                        ↓                                     │
 * │  插件层 (plugin包)                                          │
 * │  ┌──────────────────────────────────────────────┐          │
 * │  │ DataFillingPlugin - 插件基类                   │          │
 * │  │     ├─ MysqlDataFillingPlugin                │          │
 * │  │     ├─ OracleDataFillingPlugin               │          │
 * │  │     └─ SqlServerDataFillingPlugin            │          │
 * │  └──────────────────────────────────────────────┘          │
 * │                        ↓                                     │
 * │  提供者层 (provider包)                                      │
 * │  ┌──────────────────────────────────────────────┐          │
 * │  │ ExtDDLProvider - DDL生成抽象类                 │          │
 * │  │   - createTableSql()      建表               │          │
 * │  │   - addTableColumnSql()   加列               │          │
 * │  │   - createIndexSql()      建索引             │          │
 * │  │   - insertDataSql()       插入数据           │          │
 * │  │   - updateDataSql()       更新数据           │          │
 * │  │   - deleteDataSql()       删除数据           │          │
 * │  └──────────────────────────────────────────────┘          │
 * │                        ↓                                     │
 * │  DTO/VO层 (dto、vo包)                                       │
 * │  ┌──────────────────────────────────────────────┐          │
 * │  │ ExtTableField       - 表单字段配置             │          │
 * │  │ ExtIndexField       - 索引配置                │          │
 * │  │ ExtFormSettings     - 表单设置                │          │
 * │  │ XpackPluginsDfVO    - 插件信息VO              │          │
 * │  └──────────────────────────────────────────────┘          │
 * │                        ↓                                     │
 * │  数据库                                                      │
 * │  ┌──────────────────────────────────────────────┐          │
 * │  │ MySQL / Oracle / SQL Server / PostgreSQL    │          │
 * │  └──────────────────────────────────────────────┘          │
 * └─────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <p><b>包结构说明：</b></p>
 * <ul>
 *   <li>{@link io.dataease.extensions.datafilling.dto} - DTO包，定义数据传输对象</li>
 *   <li>{@link io.dataease.extensions.datafilling.vo} - VO包，定义视图对象</li>
 *   <li>{@link io.dataease.extensions.datafilling.api} - API包，定义对外接口</li>
 *   <li>{@link io.dataease.extensions.datafilling.plugin} - 插件包，定义插件基类</li>
 *   <li>{@link io.dataease.extensions.datafilling.provider} - 提供者包，定义DDL生成抽象类</li>
 *   <li>{@link io.dataease.extensions.datafilling.factory} - 工厂包，管理和获取插件实例</li>
 *   <li>{@link io.dataease.extensions.datafilling.utils} - 工具包，提供通用工具方法</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li><b>core模块</b> - 数据填报业务逻辑的实现（表单管理、数据CRUD、权限控制）</li>
 *   <li><b>plugins模块</b> - 各数据库的数据填报插件实现（Oracle、SQL Server等）</li>
 *   <li><b>前端模块</b> - 表单设计器、数据填报界面、数据展示页面</li>
 * </ul>
 *
 * <p><b>完整使用示例：</b></p>
 * <pre>
 * // ====================
 * // 场景1：创建数据填报表单
 * // ====================
 *
 * {@literal @}Service
 * public class DataFillingFormService {
 *
 *     {@literal @}Autowired
 *     private JdbcTemplate jdbcTemplate;
 *
 *     // 1. 前端设计器配置表单字段
 *     public void createForm(String datasourceType, String tableName) {
 *         // 构建表单字段配置
 *         List&lt;ExtTableField&gt; fields = new ArrayList&lt;&gt;();
 *
 *         // 字段1：姓名（必填，唯一）
 *         fields.add(ExtTableField.builder()
 *             .type("input")
 *             .typeName("单行文本")
 *             .id("field_name")
 *             .settings(ExtTableFieldSetting.builder()
 *                 .name("姓名")
 *                 .required(true)
 *                 .unique(true)
 *                 .placeholder("请输入姓名")
 *                 .mapping(ExtTableFieldMapping.builder()
 *                     .columnName("name")
 *                     .type(ExtTableField.BaseType.nvarchar)
 *                     .size(100)
 *                     .build())
 *                 .build())
 *             .build());
 *
 *         // 字段2：年龄（数字，验证规则）
 *         fields.add(ExtTableField.builder()
 *             .type("number")
 *             .typeName("数字")
 *             .id("field_age")
 *             .settings(ExtTableFieldSetting.builder()
 *                 .name("年龄")
 *                 .required(true)
 *                 .mapping(ExtTableFieldMapping.builder()
 *                     .columnName("age")
 *                     .type(ExtTableField.BaseType.number)
 *                     .build())
 *                 .build())
 *             .build());
 *
 *         // 字段3：部门（下拉选择）
 *         fields.add(ExtTableField.builder()
 *             .type("select")
 *             .typeName("下拉选择")
 *             .id("field_dept")
 *             .settings(ExtTableFieldSetting.builder()
 *                 .name("部门")
 *                 .required(true)
 *                 .options(List.of(
 *                     ExtTableFieldSetting.Option.builder()
 *                         .name("技术部").value("tech").build(),
 *                     ExtTableFieldSetting.Option.builder()
 *                         .name("市场部").value("market").build()
 *                 ))
 *                 .mapping(ExtTableFieldMapping.builder()
 *                     .columnName("department")
 *                     .type(ExtTableField.BaseType.nvarchar)
 *                     .size(50)
 *                     .build())
 *                 .build())
 *             .build());
 *
 *         // 字段4：入职日期
 *         fields.add(ExtTableField.builder()
 *             .type("date")
 *             .typeName("日期")
 *             .id("field_join_date")
 *             .settings(ExtTableFieldSetting.builder()
 *                 .name("入职日期")
 *                 .required(true)
 *                 .dateType("date")
 *                 .enableCurrentTime(true)
 *                 .mapping(ExtTableFieldMapping.builder()
 *                     .columnName("join_date")
 *                     .type(ExtTableField.BaseType.datetime)
 *                     .build())
 *                 .build())
 *             .build());
 *
 *         // 2. 获取DDL提供者
 *         ExtDDLProvider provider = ExtDDLProviderFactory.getExtDDLProvider(datasourceType);
 *
 *         // 3. 生成建表SQL并执行
 *         String createTableSql = provider.createTableSql(tableName, fields);
 *         jdbcTemplate.execute(createTableSql);
 *         // 生成SQL示例：
 *         // CREATE TABLE `t_employee` (
 *         //   `id` BIGINT NOT NULL AUTO_INCREMENT,
 *         //   `name` VARCHAR(100) NOT NULL,
 *         //   `age` INT NOT NULL,
 *         //   `department` VARCHAR(50) NOT NULL,
 *         //   `join_date` DATETIME NOT NULL,
 *         //   PRIMARY KEY (`id`),
 *         //   UNIQUE KEY `uk_name` (`name`)
 *         // )
 *
 *         // 4. 创建索引
 *         ExtIndexField nameIndex = ExtIndexField.builder()
 *             .name("idx_name")
 *             .columns(List.of(
 *                 ExtIndexField.ColumnSetting.builder()
 *                     .column("name")
 *                     .order("ASC")
 *                     .build()
 *             ))
 *             .build();
 *         List&lt;String&gt; indexSqls = provider.createTableIndexSql(tableName, List.of(nameIndex));
 *         indexSqls.forEach(jdbcTemplate::execute);
 *
 *         // 5. 保存表单配置到数据库
 *         // saveFormConfig(tableName, fields, indexes);
 *     }
 * }
 *
 * // ====================
 * // 场景2：用户填写表单（数据插入）
 * // ====================
 *
 * {@literal @}Service
 * public class DataFillingDataService {
 *
 *     {@literal @}Autowired
 *     private JdbcTemplate jdbcTemplate;
 *
 *     public void submitForm(String datasourceType, String tableName, Map&lt;String, Object&gt; formData) {
 *         // 1. 构建字段值列表
 *         List&lt;TableFieldWithValue&gt; fieldValues = new ArrayList&lt;&gt;();
 *         fieldValues.add(new TableFieldWithValue("name", formData.get("name"), null));
 *         fieldValues.add(new TableFieldWithValue("age", formData.get("age"), null));
 *         fieldValues.add(new TableFieldWithValue("department", formData.get("department"), null));
 *         fieldValues.add(new TableFieldWithValue("join_date", formData.get("join_date"), null));
 *
 *         // 2. 获取DDL提供者
 *         ExtDDLProvider provider = ExtDDLProviderFactory.getExtDDLProvider(datasourceType);
 *
 *         // 3. 生成插入SQL并执行
 *         String insertSql = provider.insertDataSql(tableName, fieldValues, 1);
 *         // 生成SQL: INSERT INTO `t_employee` (`name`, `age`, `department`, `join_date`) VALUES (?, ?, ?, ?)
 *
 *         Object[] values = fieldValues.stream().map(TableFieldWithValue::getValue).toArray();
 *         jdbcTemplate.update(insertSql, values);
 *     }
 * }
 *
 * // ====================
 * // 场景3：查询填报数据
 * // ====================
 *
 * {@literal @}Service
 * public class DataFillingQueryService {
 *
 *     {@literal @}Autowired
 *     private JdbcTemplate jdbcTemplate;
 *
 *     public List&lt;Map&lt;String, Object&gt;&gt; queryData(String tableName, int page, int pageSize) {
 *         // 构建查询SQL
 *         String sql = "SELECT * FROM `" + tableName + "` ORDER BY id DESC LIMIT ? OFFSET ?";
 *         int offset = (page - 1) * pageSize;
 *
 *         return jdbcTemplate.queryForList(sql, pageSize, offset);
 *     }
 *
 *     public long countData(String tableName) {
 *         String sql = "SELECT COUNT(*) FROM `" + tableName + "`";
 *         return jdbcTemplate.queryForObject(sql, Long.class);
 *     }
 * }
 *
 * // ====================
 * // 场景4：更新填报数据
 * // ====================
 *
 * {@literal @}Service
 * public class DataFillingUpdateService {
 *
 *     {@literal @}Autowired
 *     private JdbcTemplate jdbcTemplate;
 *
 *     public void updateData(String datasourceType, String tableName, Long id, Map&lt;String, Object&gt; formData) {
 *         // 1. 构建更新字段列表
 *         List&lt;TableFieldWithValue&gt; fieldValues = new ArrayList&lt;&gt;();
 *         fieldValues.add(new TableFieldWithValue("name", formData.get("name"), null));
 *         fieldValues.add(new TableFieldWithValue("age", formData.get("age"), null));
 *         fieldValues.add(new TableFieldWithValue("department", formData.get("department"), null));
 *
 *         // 2. 主键
 *         TableFieldWithValue pk = new TableFieldWithValue("id", id, null);
 *
 *         // 3. 获取DDL提供者并生成更新SQL
 *         ExtDDLProvider provider = ExtDDLProviderFactory.getExtDDLProvider(datasourceType);
 *         String updateSql = provider.updateDataByIdSql(tableName, fieldValues, pk);
 *         // 生成SQL: UPDATE `t_employee` SET `name` = ?, `age` = ?, `department` = ? WHERE `id` = ?
 *
 *         Object[] values = new Object[fieldValues.size() + 1];
 *         for (int i = 0; i < fieldValues.size(); i++) {
 *             values[i] = fieldValues.get(i).getValue();
 *         }
 *         values[values.length - 1] = id;
 *
 *         jdbcTemplate.update(updateSql, values);
 *     }
 * }
 *
 * // ====================
 * // 场景5：删除填报数据
 * // ====================
 *
 * {@literal @}Service
 * public class DataFillingDeleteService {
 *
 *     {@literal @}Autowired
 *     private JdbcTemplate jdbcTemplate;
 *
 *     public void deleteData(String datasourceType, String tableName, List&lt;Long&gt; ids) {
 *         // 1. 构建主键列表
 *         List&lt;TableFieldWithValue&gt; pks = ids.stream()
 *             .map(id -> new TableFieldWithValue("id", id, null))
 *             .toList();
 *
 *         // 2. 获取DDL提供者并生成删除SQL
 *         ExtDDLProvider provider = ExtDDLProviderFactory.getExtDDLProvider(datasourceType);
 *         String deleteSql = provider.deleteDataByIdsSql(tableName, pks);
 *         // 生成SQL: DELETE FROM `t_employee` WHERE `id` IN (?, ?, ?)
 *
 *         jdbcTemplate.update(deleteSql, ids.toArray());
 *     }
 * }
 *
 * // ====================
 * // 场景6：查询可用的数据填报插件
 * // ====================
 *
 * {@literal @}RestController
 * {@literal @}RequestMapping("/api/datafilling")
 * public class DataFillingPluginController {
 *
 *     {@literal @}Autowired
 *     private DfPluginManageApi dfPluginManageApi;
 *
 *     {@literal @}GetMapping("/plugins")
 *     public Result&lt;List&lt;XpackPluginsDfVO&gt;&gt; listPlugins() {
 *         List&lt;XpackPluginsDfVO&gt; plugins = dfPluginManageApi.queryPluginDf();
 *         // 返回示例：
 *         // [
 *         //   {type: "mysql", icon: "...", category: "datafilling", flag: 1},
 *         //   {type: "oracle", icon: "...", category: "datafilling", flag: 1}
 *         // ]
 *         return Result.success(plugins);
 *     }
 * }
 * </pre>
 *
 * <p><b>开发插件示例：</b></p>
 * <pre>
 * // 1. 创建插件类
 * public class OracleDataFillingPlugin extends DataFillingPlugin {
 *
 *     {@literal @}Override
 *     public String createTableSql(String table, List&lt;ExtTableField&gt; formFields) {
 *         // 实现Oracle的CREATE TABLE语句生成逻辑
 *         StringBuilder sql = new StringBuilder("CREATE TABLE ");
 *         sql.append(table).append(" (\\n");
 *
 *         // 添加自增主键
 *         sql.append("  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\\n");
 *
 *         // 添加表单字段
 *         for (ExtTableField field : formFields) {
 *             ExtTableFieldMapping mapping = field.getSettings().getMapping();
 *             sql.append("  ").append(mapping.getColumnName()).append(" ");
 *
 *             // 数据类型映射
 *             switch (mapping.getType()) {
 *                 case nvarchar -> sql.append("VARCHAR2(").append(mapping.getSize()).append(")");
 *                 case text -> sql.append("CLOB");
 *                 case number -> sql.append("NUMBER");
 *                 case decimal -> sql.append("NUMBER(").append(mapping.getSize())
 *                     .append(",").append(mapping.getAccuracy()).append(")");
 *                 case datetime -> sql.append("TIMESTAMP");
 *             }
 *
 *             if (field.getSettings().isRequired()) {
 *                 sql.append(" NOT NULL");
 *             }
 *             sql.append(",\\n");
 *         }
 *
 *         sql.deleteCharAt(sql.length() - 2); // 删除最后的逗号
 *         sql.append("\\n)");
 *
 *         return sql.toString();
 *     }
 *
 *     // 实现其他抽象方法...
 * }
 *
 * // 2. 创建plugin.json配置文件
 * {
 *   "moduleName": "oracle-datafilling",
 *   "version": "1.0.0",
 *   "icon": "&lt;svg&gt;...&lt;/svg&gt;",
 *   "config": {
 *     "type": "oracle",
 *     "category": "datafilling",
 *     "flag": 1
 *   }
 * }
 *
 * // 3. 打包成JAR并放入plugins目录，系统启动时自动加载
 * </pre>
 *
 * <p><b>设计原则：</b></p>
 * <ul>
 *   <li><b>插件化架构</b> - 通过插件机制支持多种数据库，便于扩展</li>
 *   <li><b>面向接口编程</b> - 定义清晰的接口和抽象类，降低耦合</li>
 *   <li><b>工厂模式</b> - 使用工厂统一管理插件实例</li>
 *   <li><b>模板方法</b> - 在抽象类中定义算法骨架，子类实现具体步骤</li>
 *   <li><b>配置驱动</b> - 通过DTO配置表单，而非硬编码</li>
 * </ul>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>数据填报功能是企业版功能，需要License验证</li>
 *   <li>DDL操作不可逆，执行前应做好数据备份</li>
 *   <li>不同数据库的SQL语法差异较大，需要仔细测试</li>
 *   <li>表单字段修改可能影响现有数据，需要做兼容性处理</li>
 *   <li>SQL注入防护是安全的基本要求，使用PreparedStatement</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>建表前先检查表是否存在，避免重复创建</li>
 *   <li>为常用查询字段创建索引，提高查询性能</li>
 *   <li>对敏感字段进行加密存储</li>
 *   <li>定期备份填报数据，防止数据丢失</li>
 *   <li>合理设置字段长度和类型，避免浪费存储空间</li>
 *   <li>使用事务确保数据一致性</li>
 * </ul>
 *
 * @author DataEase
 * @since 2.0.0
 * @see io.dataease.extensions.datafilling.dto
 * @see io.dataease.extensions.datafilling.provider.ExtDDLProvider
 * @see io.dataease.extensions.datafilling.factory.ExtDDLProviderFactory
 * @see io.dataease.extensions.datafilling.plugin.DataFillingPlugin
 */
package io.dataease.extensions.datafilling;
