/**
 * 数据填报DDL提供者包
 * <p>
 * 本包定义DDL（Data Definition Language）生成的核心抽象类和接口，负责根据表单配置
 * 生成数据库的建表语句、索引语句、数据操作语句等。这是数据填报模块的核心逻辑层。
 * </p>
 *
 * <p><b>核心类：</b></p>
 * <ul>
 *   <li>{@link io.dataease.extensions.datafilling.provider.ExtDDLProvider} - DDL生成抽象基类</li>
 * </ul>
 *
 * <p><b>设计模式：</b></p>
 * <ul>
 *   <li><b>模板方法模式</b> - ExtDDLProvider定义DDL生成的模板方法，子类实现具体数据库的SQL生成</li>
 *   <li><b>策略模式</b> - 通过ExtDDLProviderFactory选择不同的DDL提供者策略</li>
 *   <li><b>抽象工厂模式</b> - 为每种数据库提供一套完整的DDL生成方法</li>
 * </ul>
 *
 * <p><b>DDL生成流程：</b></p>
 * <pre>
 * 1. 前端表单配置
 *    ↓
 * 2. ExtTableField DTO
 *    ↓
 * 3. ExtDDLProviderFactory.getExtDDLProvider(type)
 *    ↓
 * 4. ExtDDLProvider子类（如MysqlExtDDLProvider）
 *    ↓
 * 5. 生成数据库特定的DDL语句
 *    ↓
 * 6. 执行DDL创建/修改表结构
 * </pre>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li><b>表结构管理</b> - 创建表、添加列、删除列、删除表</li>
 *   <li><b>索引管理</b> - 创建索引、删除索引</li>
 *   <li><b>数据操作</b> - 插入数据、更新数据、删除数据</li>
 *   <li><b>查询构建</b> - 生成查询SQL、条件构建、分页查询</li>
 *   <li><b>数据验证</b> - 唯一性校验、主键查询</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 1. 获取DDL提供者
 * ExtDDLProvider provider = ExtDDLProviderFactory.getExtDDLProvider("mysql");
 *
 * // 2. 创建表
 * List&lt;ExtTableField&gt; fields = buildTableFields(); // 构建字段列表
 * String createTableSql = provider.createTableSql("t_employee", fields);
 * // 生成: CREATE TABLE `t_employee` (
 * //         `id` BIGINT NOT NULL AUTO_INCREMENT,
 * //         `name` VARCHAR(100) NOT NULL,
 * //         `age` INT,
 * //         PRIMARY KEY (`id`)
 * //       )
 * jdbcTemplate.execute(createTableSql);
 *
 * // 3. 添加列
 * List&lt;ExtTableField&gt; newFields = List.of(
 *     createField("email", BaseType.nvarchar, 200, false)
 * );
 * String alterTableSql = provider.addTableColumnSql("t_employee", newFields, Collections.emptyList());
 * // 生成: ALTER TABLE `t_employee` ADD COLUMN `email` VARCHAR(200)
 * jdbcTemplate.execute(alterTableSql);
 *
 * // 4. 创建索引
 * ExtIndexField index = ExtIndexField.builder()
 *     .name("idx_name")
 *     .columns(List.of(
 *         ExtIndexField.ColumnSetting.builder()
 *             .column("name")
 *             .order("ASC")
 *             .build()
 *     ))
 *     .build();
 * List&lt;String&gt; indexSqls = provider.createTableIndexSql("t_employee", List.of(index));
 * // 生成: CREATE INDEX `idx_name` ON `t_employee` (`name` ASC)
 * indexSqls.forEach(jdbcTemplate::execute);
 *
 * // 5. 插入数据
 * List&lt;TableFieldWithValue&gt; fieldValues = List.of(
 *     new TableFieldWithValue("name", "张三", null),
 *     new TableFieldWithValue("age", 30, null)
 * );
 * String insertSql = provider.insertDataSql("t_employee", fieldValues, 1);
 * // 生成: INSERT INTO `t_employee` (`name`, `age`) VALUES (?, ?)
 * jdbcTemplate.update(insertSql, "张三", 30);
 *
 * // 6. 更新数据
 * TableFieldWithValue pk = new TableFieldWithValue("id", 1L, null);
 * String updateSql = provider.updateDataByIdSql("t_employee", fieldValues, pk);
 * // 生成: UPDATE `t_employee` SET `name` = ?, `age` = ? WHERE `id` = ?
 * jdbcTemplate.update(updateSql, "李四", 31, 1L);
 *
 * // 7. 删除数据
 * List&lt;TableFieldWithValue&gt; pks = List.of(
 *     new TableFieldWithValue("id", 1L, null)
 * );
 * String deleteSql = provider.deleteDataByIdsSql("t_employee", pks);
 * // 生成: DELETE FROM `t_employee` WHERE `id` IN (?)
 * jdbcTemplate.update(deleteSql, 1L);
 * </pre>
 *
 * <p><b>抽象方法列表：</b></p>
 * <table border="1" cellpadding="5">
 *   <tr>
 *     <th>方法名</th>
 *     <th>功能</th>
 *     <th>返回值</th>
 *   </tr>
 *   <tr>
 *     <td>createTableSql</td>
 *     <td>生成建表SQL</td>
 *     <td>String</td>
 *   </tr>
 *   <tr>
 *     <td>addTableColumnSql</td>
 *     <td>生成添加/修改列的SQL</td>
 *     <td>String</td>
 *   </tr>
 *   <tr>
 *     <td>dropTableColumnSql</td>
 *     <td>生成删除列的SQL</td>
 *     <td>String</td>
 *   </tr>
 *   <tr>
 *     <td>dropTableSql</td>
 *     <td>生成删除表的SQL</td>
 *     <td>String</td>
 *   </tr>
 *   <tr>
 *     <td>createTableIndexSql</td>
 *     <td>生成创建索引的SQL</td>
 *     <td>List&lt;String&gt;</td>
 *   </tr>
 *   <tr>
 *     <td>dropTableIndexSql</td>
 *     <td>生成删除索引的SQL</td>
 *     <td>List&lt;String&gt;</td>
 *   </tr>
 *   <tr>
 *     <td>insertDataSql</td>
 *     <td>生成插入数据的SQL</td>
 *     <td>String</td>
 *   </tr>
 *   <tr>
 *     <td>updateDataByIdSql</td>
 *     <td>生成更新数据的SQL</td>
 *     <td>String</td>
 *   </tr>
 *   <tr>
 *     <td>deleteDataByIdsSql</td>
 *     <td>生成删除数据的SQL</td>
 *     <td>String</td>
 *   </tr>
 *   <tr>
 *     <td>getColumnType</td>
 *     <td>获取JDBC列类型</td>
 *     <td>Integer</td>
 *   </tr>
 *   <tr>
 *     <td>truncateTable</td>
 *     <td>生成清空表的SQL</td>
 *     <td>String</td>
 *   </tr>
 *   <tr>
 *     <td>listAllIds</td>
 *     <td>生成查询所有ID的SQL</td>
 *     <td>String</td>
 *   </tr>
 * </table>
 *
 * <p><b>实现要求：</b></p>
 * <ul>
 *   <li><b>SQL注入防护</b> - 使用预编译语句（PreparedStatement），避免SQL注入</li>
 *   <li><b>数据库兼容性</b> - 生成的SQL必须符合目标数据库的语法</li>
 *   <li><b>关键字转义</b> - 对表名、列名使用反引号或双引号转义</li>
 *   <li><b>数据类型映射</b> - 正确映射BaseType到数据库类型</li>
 *   <li><b>异常处理</b> - 对无效输入抛出有意义的异常</li>
 *   <li><b>事务支持</b> - DDL操作应在事务中执行（如果数据库支持）</li>
 * </ul>
 *
 * <p><b>与其他模块的关系：</b></p>
 * <ul>
 *   <li><b>plugin包</b> - DataFillingPlugin继承ExtDDLProvider并实现抽象方法</li>
 *   <li><b>factory包</b> - ExtDDLProviderFactory管理和获取DDL提供者实例</li>
 *   <li><b>dto包</b> - 使用ExtTableField、ExtIndexField等DTO作为输入参数</li>
 *   <li><b>core模块</b> - 核心业务逻辑调用DDL提供者生成SQL并执行</li>
 * </ul>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>不同数据库的DDL语法差异较大（如自增主键、数据类型、索引语法）</li>
 *   <li>某些数据库不支持事务DDL（如MySQL的部分版本）</li>
 *   <li>生成的SQL应该经过充分测试，避免破坏现有数据</li>
 *   <li>废弃的方法（@Deprecated）建议使用更灵活的替代方案</li>
 * </ul>
 *
 * @author DataEase
 * @since 2.0.0
 * @see io.dataease.extensions.datafilling.plugin.DataFillingPlugin
 * @see io.dataease.extensions.datafilling.factory.ExtDDLProviderFactory
 * @see io.dataease.extensions.datafilling.dto.ExtTableField
 * @see io.dataease.extensions.datafilling.dto.ExtIndexField
 */
package io.dataease.extensions.datafilling.provider;
