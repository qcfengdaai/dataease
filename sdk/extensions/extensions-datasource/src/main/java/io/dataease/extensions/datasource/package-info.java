/**
 * DataEase数据源扩展包（SDK/Extensions模块）
 * <p>
 * 提供数据源插件化扩展机制，支持各种类型数据库和数据源的连接、查询、元数据获取等功能。
 * 采用SPI（Service Provider Interface）模式，便于扩展新的数据源类型。
 * </p>
 *
 * <h2>支持的数据源类型</h2>
 *
 * <h3>关系型数据库</h3>
 * <ul>
 *   <li>MySQL / MariaDB</li>
 *   <li>PostgreSQL</li>
 *   <li>Oracle</li>
 *   <li>SQL Server</li>
 *   <li>DB2</li>
 *   <li>达梦（DM）</li>
 *   <li>人大金仓（KingBase）</li>
 * </ul>
 *
 * <h3>大数据平台</h3>
 * <ul>
 *   <li>Apache Hive</li>
 *   <li>Apache Impala</li>
 *   <li>ClickHouse</li>
 *   <li>StarRocks</li>
 *   <li>Doris</li>
 * </ul>
 *
 * <h3>NoSQL数据库</h3>
 * <ul>
 *   <li>MongoDB</li>
 *   <li>Redis</li>
 *   <li>Elasticsearch</li>
 * </ul>
 *
 * <h3>其他数据源</h3>
 * <ul>
 *   <li>Excel文件</li>
 *   <li>API数据源</li>
 *   <li>JSON数据</li>
 * </ul>
 *
 * <h2>核心功能</h2>
 *
 * <h3>1. 数据源连接</h3>
 * <ul>
 *   <li>连接测试 - 验证数据源配置是否正确</li>
 *   <li>连接池管理 - 管理数据库连接，提高性能</li>
 *   <li>连接复用 - 避免频繁创建和销毁连接</li>
 * </ul>
 *
 * <h3>2. 元数据获取</h3>
 * <ul>
 *   <li>获取数据库列表</li>
 *   <li>获取表列表</li>
 *   <li>获取表结构（字段名、类型、注释等）</li>
 *   <li>获取索引信息</li>
 * </ul>
 *
 * <h3>3. 数据查询</h3>
 * <ul>
 *   <li>执行SQL查询</li>
 *   <li>参数化查询（防SQL注入）</li>
 *   <li>分页查询</li>
 *   <li>查询结果转换</li>
 * </ul>
 *
 * <h3>4. SQL方言适配</h3>
 * <ul>
 *   <li>不同数据库的SQL语法差异处理</li>
 *   <li>函数映射（如日期函数、聚合函数等）</li>
 *   <li>数据类型映射</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>示例1：数据源配置</h3>
 * <pre>
 * // 配置MySQL数据源
 * DatasourceConfiguration config = new DatasourceConfiguration();
 * config.setType("mysql");
 * config.setHost("localhost");
 * config.setPort(3306);
 * config.setDatabase("dataease");
 * config.setUsername("root");
 * config.setPassword("password");
 *
 * // 测试连接
 * DatasourceProvider provider = getDatasourceProvider("mysql");
 * boolean connected = provider.testConnection(config);
 * </pre>
 *
 * <h3>示例2：查询数据</h3>
 * <pre>
 * // 执行查询
 * QueryRequest request = new QueryRequest();
 * request.setQuery("SELECT * FROM users WHERE age > ?");
 * request.setParameters(Arrays.asList(18));
 *
 * QueryResult result = provider.executeQuery(config, request);
 * List&lt;Map&lt;String, Object&gt;&gt; data = result.getData();
 * </pre>
 *
 * <h3>示例3：获取元数据</h3>
 * <pre>
 * // 获取所有表
 * List&lt;String&gt; tables = provider.getTables(config, "dataease");
 *
 * // 获取表结构
 * List&lt;FieldInfo&gt; fields = provider.getTableFields(config, "users");
 * for (FieldInfo field : fields) {
 *     System.out.println(field.getName() + " - " + field.getType());
 * }
 * </pre>
 *
 * <h2>扩展新数据源</h2>
 *
 * <h3>实现步骤</h3>
 * <ol>
 *   <li>实现DatasourceProvider接口</li>
 *   <li>配置SPI文件：META-INF/services/io.dataease.extensions.datasource.DatasourceProvider</li>
 *   <li>实现连接、查询、元数据获取等方法</li>
 *   <li>添加JDBC驱动依赖</li>
 *   <li>编写单元测试</li>
 * </ol>
 *
 * <h2>注意事项</h2>
 *
 * <h3>1. 安全性</h3>
 * <ul>
 *   <li>密码加密存储</li>
 *   <li>使用参数化查询防止SQL注入</li>
 *   <li>限制查询权限</li>
 * </ul>
 *
 * <h3>2. 性能优化</h3>
 * <ul>
 *   <li>使用连接池</li>
 *   <li>合理设置查询超时</li>
 *   <li>大数据量使用流式查询</li>
 * </ul>
 *
 * <h3>3. 兼容性</h3>
 * <ul>
 *   <li>处理不同数据库版本的差异</li>
 *   <li>兼容JDBC驱动的不同实现</li>
 *   <li>优雅处理不支持的特性</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 */
package io.dataease.extensions.datasource;
