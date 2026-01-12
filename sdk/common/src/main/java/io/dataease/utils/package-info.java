/**
 * 通用工具类包
 * <p>
 * 提供DataEase系统中常用的工具类，涵盖ID生成、JSON处理、加密解密、日期处理、
 * 文件操作、认证管理等多个方面。所有工具类均为静态方法，可直接调用。
 * </p>
 *
 * <h2>核心工具类分类</h2>
 *
 * <h3>1. ID生成类</h3>
 * <ul>
 *   <li>{@link io.dataease.utils.IDUtils} - ID生成工具，提供随机字符串和雪花算法ID</li>
 *   <li>{@link io.dataease.utils.SnowFlake} - 雪花算法实现，生成分布式唯一ID</li>
 * </ul>
 *
 * <h3>2. 数据处理类</h3>
 * <ul>
 *   <li>{@link io.dataease.utils.JsonUtil} - JSON序列化/反序列化工具</li>
 *   <li>{@link io.dataease.utils.BeanUtils} - Bean对象拷贝和属性处理</li>
 *   <li>{@link io.dataease.utils.ModelUtils} - 模型转换工具</li>
 *   <li>{@link io.dataease.utils.TreeUtils} - 树形结构数据处理</li>
 *   <li>{@link io.dataease.utils.DeCollectionUtils} - 集合处理增强工具</li>
 * </ul>
 *
 * <h3>3. 加密安全类</h3>
 * <ul>
 *   <li>{@link io.dataease.utils.Md5Utils} - MD5哈希计算</li>
 *   <li>{@link io.dataease.utils.AesUtils} - AES对称加密</li>
 *   <li>{@link io.dataease.utils.RsaUtils} - RSA非对称加密</li>
 *   <li>{@link io.dataease.utils.AuthUtils} - 认证和权限工具</li>
 *   <li>{@link io.dataease.utils.TokenUtils} - Token生成和验证</li>
 * </ul>
 *
 * <h3>4. 用户上下文类</h3>
 * <ul>
 *   <li>{@link io.dataease.utils.AuthUtils} - 当前用户信息管理（ThreadLocal）</li>
 *   <li>{@link io.dataease.utils.UserUtils} - 用户相关操作工具</li>
 * </ul>
 *
 * <h3>5. 日期时间类</h3>
 * <ul>
 *   <li>{@link io.dataease.utils.DateUtils} - 日期格式化和计算</li>
 *   <li>{@link io.dataease.utils.CalendarUtils} - 日历和时间范围计算</li>
 * </ul>
 *
 * <h3>6. 文件处理类</h3>
 * <ul>
 *   <li>{@link io.dataease.utils.FileUtils} - 文件上传下载和操作</li>
 *   <li>{@link io.dataease.utils.CommonExcelUtils} - Excel文件处理</li>
 *   <li>{@link io.dataease.utils.StaticResourceUtils} - 静态资源管理</li>
 * </ul>
 *
 * <h3>7. HTTP和网络类</h3>
 * <ul>
 *   <li>{@link io.dataease.utils.HttpClientUtil} - HTTP客户端工具</li>
 *   <li>{@link io.dataease.utils.HttpClientConfig} - HTTP客户端配置</li>
 *   <li>{@link io.dataease.utils.ServletUtils} - Servlet请求响应处理</li>
 *   <li>{@link io.dataease.utils.IPUtils} - IP地址获取和处理</li>
 * </ul>
 *
 * <h3>8. 缓存和配置类</h3>
 * <ul>
 *   <li>{@link io.dataease.utils.CacheUtils} - 缓存操作工具</li>
 *   <li>{@link io.dataease.utils.ConfigUtils} - 配置读取工具</li>
 *   <li>{@link io.dataease.utils.SystemSettingUtils} - 系统设置管理</li>
 * </ul>
 *
 * <h3>9. 日志和分页类</h3>
 * <ul>
 *   <li>{@link io.dataease.utils.LogUtil} - 日志记录工具</li>
 *   <li>{@link io.dataease.utils.Pager} - 分页参数和结果封装</li>
 * </ul>
 *
 * <h3>10. 反射和类处理类</h3>
 * <ul>
 *   <li>{@link io.dataease.utils.DeClassUtils} - 类加载和处理</li>
 *   <li>{@link io.dataease.utils.DeReflectUtil} - 反射操作工具</li>
 *   <li>{@link io.dataease.utils.MappingUtils} - 对象映射工具</li>
 * </ul>
 *
 * <h3>11. 并发和异步类</h3>
 * <ul>
 *   <li>{@link io.dataease.utils.CommonThreadPool} - 通用线程池</li>
 *   <li>{@link io.dataease.utils.DelayQueueUtils} - 延迟队列工具</li>
 * </ul>
 *
 * <h3>12. 其他工具类</h3>
 * <ul>
 *   <li>{@link io.dataease.utils.CommonBeanFactory} - Spring Bean工厂</li>
 *   <li>{@link io.dataease.utils.VersionUtil} - 版本比较工具</li>
 *   <li>{@link io.dataease.utils.WhitelistUtils} - 白名单管理</li>
 *   <li>{@link io.dataease.utils.CommunityUtils} - 社区版工具</li>
 * </ul>
 *
 * <h2>使用位置</h2>
 *
 * <h3>核心业务模块（core-backend）</h3>
 * <ul>
 *   <li><b>数据集管理</b>：使用JsonUtil处理数据集配置，TreeUtils处理目录结构</li>
 *   <li><b>图表管理</b>：使用JsonUtil存储图表配置，IDUtils生成图表ID</li>
 *   <li><b>仪表板</b>：使用JsonUtil处理布局，FileUtils处理导出文件</li>
 *   <li><b>用户管理</b>：使用Md5Utils加密密码，AuthUtils管理用户上下文</li>
 *   <li><b>权限控制</b>：使用AuthUtils判断权限，TreeUtils处理权限树</li>
 *   <li><b>系统设置</b>：使用RsaUtils处理敏感配置，ConfigUtils读取配置</li>
 * </ul>
 *
 * <h3>API层（sdk/api）</h3>
 * <ul>
 *   <li>DTO/VO对象的序列化和反序列化</li>
 *   <li>请求参数的验证和处理</li>
 *   <li>分页参数的封装</li>
 * </ul>
 *
 * <h3>扩展模块（sdk/extensions）</h3>
 * <ul>
 *   <li>数据源连接：HttpClientUtil发起HTTP请求</li>
 *   <li>视图插件：JsonUtil处理视图配置</li>
 *   <li>数据填报：CommonExcelUtils处理Excel导入导出</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>示例1：生成数据库主键</h3>
 * <pre>
 * // 创建新的数据集时
 * Dataset dataset = new Dataset();
 * dataset.setId(IDUtils.snowID());  // 使用雪花算法生成Long类型ID
 * dataset.setName("销售数据");
 * datasetMapper.insert(dataset);
 * </pre>
 *
 * <h3>示例2：JSON序列化和反序列化</h3>
 * <pre>
 * // 保存图表配置到数据库
 * ChartConfig config = new ChartConfig();
 * config.setType("bar");
 * config.setColors(Arrays.asList("#409EFF", "#67C23A"));
 * String configJson = (String) JsonUtil.toJSONString(config);
 * chart.setConfig(configJson);
 *
 * // 从数据库读取图表配置
 * String savedConfig = chart.getConfig();
 * ChartConfig chartConfig = JsonUtil.parseObject(savedConfig, ChartConfig.class);
 * </pre>
 *
 * <h3>示例3：获取当前登录用户</h3>
 * <pre>
 * // 在Service层获取当前用户
 * {@literal @}Service
 * public class DatasetService {
 *     public void saveDataset(Dataset dataset) {
 *         // 获取当前登录用户
 *         TokenUserBO user = AuthUtils.getUser();
 *         dataset.setCreateBy(user.getUserId());
 *         dataset.setCreateTime(System.currentTimeMillis());
 *
 *         // 判断是否为系统管理员
 *         if (AuthUtils.isSysAdmin()) {
 *             // 管理员可以设置为公共数据集
 *             dataset.setIsPublic(true);
 *         }
 *
 *         datasetMapper.insert(dataset);
 *     }
 * }
 * </pre>
 *
 * <h3>示例4：文件上传和下载</h3>
 * <pre>
 * // 上传Excel文件
 * {@literal @}PostMapping("/upload")
 * public String uploadFile(MultipartFile file) {
 *     // 生成唯一文件名
 *     String fileName = IDUtils.randomID(16) + ".xlsx";
 *     String filePath = "/data/upload/" + fileName;
 *
 *     // 保存文件
 *     FileUtils.saveFile(file.getInputStream(), filePath);
 *
 *     // 解析Excel
 *     List&lt;Map&lt;String, Object&gt;&gt; data = CommonExcelUtils.parseExcel(filePath);
 *
 *     return fileName;
 * }
 * </pre>
 *
 * <h3>示例5：树形结构处理</h3>
 * <pre>
 * // 构建数据集目录树
 * List&lt;DatasetFolder&gt; allFolders = datasetFolderMapper.selectList(null);
 * List&lt;DatasetFolder&gt; tree = TreeUtils.buildTree(
 *     allFolders,
 *     "0",  // 根节点ID
 *     DatasetFolder::getId,
 *     DatasetFolder::getPid,
 *     DatasetFolder::setChildren
 * );
 * </pre>
 *
 * <h3>示例6：HTTP请求</h3>
 * <pre>
 * // 调用外部API
 * Map&lt;String, String&gt; headers = new HashMap&lt;&gt;();
 * headers.put("Content-Type", "application/json");
 *
 * Map&lt;String, Object&gt; params = new HashMap&lt;&gt;();
 * params.put("query", "SELECT * FROM table");
 *
 * String response = HttpClientUtil.post(
 *     "https://api.example.com/query",
 *     JsonUtil.toJSONString(params),
 *     headers
 * );
 *
 * Result result = JsonUtil.parseObject(response, Result.class);
 * </pre>
 *
 * <h3>示例7：密码加密</h3>
 * <pre>
 * // 用户注册时加密密码
 * String plainPassword = "User@123";
 * String encryptedPassword = Md5Utils.md5(plainPassword);
 * user.setPassword(encryptedPassword);
 *
 * // 用户登录时验证密码
 * String inputPassword = "User@123";
 * if (Md5Utils.md5(inputPassword).equals(user.getPassword())) {
 *     // 密码正确，生成Token
 *     String token = TokenUtils.createToken(user);
 *     return token;
 * }
 * </pre>
 *
 * <h2>注意事项</h2>
 *
 * <h3>1. ThreadLocal使用规范</h3>
 * <ul>
 *   <li><b>AuthUtils</b>使用了ThreadLocal，必须在请求结束时调用{@code AuthUtils.remove()}</li>
 *   <li>通常在Filter的finally块中清理，避免内存泄漏</li>
 *   <li>异步任务无法直接使用ThreadLocal，需要手动传递用户信息</li>
 * </ul>
 *
 * <h3>2. JSON处理性能</h3>
 * <ul>
 *   <li>JsonUtil支持最大50MB的JSON字符串</li>
 *   <li>超大JSON会占用较多内存，建议分页或分批处理</li>
 *   <li>频繁序列化/反序列化的场景可以考虑缓存</li>
 * </ul>
 *
 * <h3>3. ID生成策略</h3>
 * <ul>
 *   <li><b>数据库主键</b>：推荐使用{@code IDUtils.snowID()}，Long类型，有序且唯一</li>
 *   <li><b>临时标识</b>：使用{@code IDUtils.randomID()}，字符串类型，适合token和短码</li>
 *   <li>分布式部署时需要配置不同的workerId，避免ID冲突</li>
 * </ul>
 *
 * <h3>4. 加密安全</h3>
 * <ul>
 *   <li><b>密码存储</b>：使用Md5Utils（建议加盐）或更安全的BCrypt</li>
 *   <li><b>敏感数据传输</b>：使用RsaUtils非对称加密</li>
 *   <li><b>数据库敏感字段</b>：使用AesUtils对称加密</li>
 *   <li>密钥管理：不要硬编码在代码中，使用配置文件或密钥管理服务</li>
 * </ul>
 *
 * <h3>5. 异常处理</h3>
 * <ul>
 *   <li>大部分工具类在异常时返回null，调用方需要判空</li>
 *   <li>JsonUtil在解析失败时会记录错误日志并返回null</li>
 *   <li>FileUtils在文件操作失败时可能抛出IOException</li>
 * </ul>
 *
 * <h2>扩展开发指南</h2>
 *
 * <h3>添加新工具类的规范</h3>
 * <ol>
 *   <li>工具类命名以Utils结尾，如XxxUtils</li>
 *   <li>所有方法均为public static，不需要实例化</li>
 *   <li>添加完整的JavaDoc注释，包括功能描述、参数说明、使用示例</li>
 *   <li>提供单元测试，覆盖主要场景和边界情况</li>
 *   <li>异常处理要统一，避免吞掉异常</li>
 *   <li>性能关键的工具类要考虑缓存和优化</li>
 * </ol>
 *
 * <h3>工具类设计原则</h3>
 * <ul>
 *   <li><b>单一职责</b>：每个工具类只负责一个领域的功能</li>
 *   <li><b>无状态</b>：工具类不应该有实例变量（静态变量除外）</li>
 *   <li><b>线程安全</b>：所有方法必须是线程安全的</li>
 *   <li><b>向后兼容</b>：修改现有方法时保持签名兼容</li>
 *   <li><b>性能优先</b>：工具类会被频繁调用，需要考虑性能</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.utils.IDUtils
 * @see io.dataease.utils.JsonUtil
 * @see io.dataease.utils.AuthUtils
 */
package io.dataease.utils;
