/**
 * RSA密钥管理包
 * <p>
 * 本包提供完整的RSA密钥生命周期管理功能，包括密钥的生成、存储、查询和系统启动时的自动初始化。
 * 主要用于DataEase系统中的数据加密传输、安全认证等场景。
 * </p>
 *
 * <h2>包结构说明</h2>
 * <ul>
 *   <li><b>{@link io.dataease.rsa.dao}</b> - 数据访问层
 *     <ul>
 *       <li>{@link io.dataease.rsa.dao.entity.CoreRsa} - RSA密钥实体类，对应core_rsa表</li>
 *       <li>{@link io.dataease.rsa.dao.mapper.CoreRsaMapper} - MyBatis Plus数据访问接口</li>
 *     </ul>
 *   </li>
 *   <li><b>{@link io.dataease.rsa.manage}</b> - 业务管理层
 *     <ul>
 *       <li>{@link io.dataease.rsa.manage.RsaManage} - RSA密钥管理服务，提供密钥的创建、查询等功能</li>
 *     </ul>
 *   </li>
 *   <li><b>{@link io.dataease.rsa.starter}</b> - 系统启动器
 *     <ul>
 *       <li>{@link io.dataease.rsa.starter.RsaStarter} - Spring Boot启动器，确保系统启动时密钥可用</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>主要功能</h2>
 * <ul>
 *   <li><b>密钥生成</b> - 自动生成RSA 2048位密钥对和AES密钥</li>
 *   <li><b>密钥存储</b> - 将密钥安全存储在数据库core_rsa表中</li>
 *   <li><b>密钥查询</b> - 支持缓存的密钥查询功能，提高性能</li>
 *   <li><b>自动初始化</b> - 系统启动时自动检查并创建必要的密钥</li>
 *   <li><b>缓存支持</b> - 集成Spring Cache，避免频繁数据库查询</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <ul>
 *   <li><b>数据加密传输</b> - 客户端与服务端之间的敏感数据传输</li>
 *   <li><b>安全认证</b> - 用于用户登录、API调用等安全验证场景</li>
 *   <li><b>配置信息保护</b> - 对系统配置中的敏感信息进行加密存储</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>1. 获取RSA密钥信息</h3>
 * <pre><code>
 * // 通过Spring依赖注入获取RsaManage
 * &#64;Resource
 * private RsaManage rsaManage;
 *
 * // 查询系统中的RSA密钥信息
 * CoreRsa rsaInfo = rsaManage.query();
 * if (rsaInfo != null) {
 *     String publicKey = rsaInfo.getPublicKey();
 *     String privateKey = rsaInfo.getPrivateKey();
 *     String aesKey = rsaInfo.getAesKey();
 *     // 使用密钥进行加密解密操作
 * }
 * </code></pre>
 *
 * <h3>2. 手动生成和保存密钥</h3>
 * <pre><code>
 * &#64;Resource
 * private RsaManage rsaManage;
 *
 * // 重新生成并保存RSA密钥（会清除缓存）
 * rsaManage.save();
 *
 * // 检查密钥是否存在，不存在则自动创建
 * rsaManage.check();
 * </code></pre>
 *
 * <h3>3. 配合RsaUtils工具类使用</h3>
 * <pre><code>
 * &#64;Resource
 * private RsaUtils rsaUtils;
 * &#64;Resource
 * private RsaManage rsaManage;
 *
 * // 获取系统密钥信息
 * CoreRsa rsaInfo = rsaManage.query();
 * String publicKey = rsaInfo.getPublicKey();
 * String privateKey = rsaInfo.getPrivateKey();
 *
 * // 使用公钥加密数据
 * String data = "需要加密的敏感数据";
 * String encryptedData = rsaUtils.publicEncrypt(data, publicKey);
 *
 * // 使用私钥解密数据
 * String decryptedData = rsaUtils.privateDecrypt(encryptedData, privateKey);
 * </code></pre>
 *
 * <h2>数据表结构</h2>
 * <p>core_rsa表结构：</p>
 * <ul>
 *   <li><b>id</b> - 主键，固定为1确保系统唯一性</li>
 *   <li><b>private_key</b> - RSA私钥，Base64编码</li>
 *   <li><b>public_key</b> - RSA公钥，Base64编码</li>
 *   <li><b>aes_key</b> - AES密钥，用于对称加密</li>
 *   <li><b>create_time</b> - 创建时间戳</li>
 * </ul>
 *
 * <h2>缓存配置</h2>
 * <p>
 * 本包使用Spring Cache进行缓存管理：
 * </p>
 * <ul>
 *   <li><b>缓存名称</b>：{@code RSA_CACHE}</li>
 *   <li><b>缓存键</b>：{@code '-de-'}</li>
 *   <li><b>缓存策略</b>：查询时缓存，保存时清除缓存</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>系统只维护一套RSA密钥（ID固定为1），确保全局一致性</li>
 *   <li>密钥生成使用RSA 2048位算法，安全强度高</li>
 *   <li>支持缓存机制，减少数据库访问压力</li>
 *   <li>启动器会在应用启动时自动检查密钥可用性</li>
 *   <li>异常情况下会记录日志但不影响应用启动</li>
 * </ul>
 *
 * <h2>相关依赖</h2>
 * <ul>
 *   <li>{@link io.dataease.utils.RsaUtils} - RSA/AES加密解密工具类</li>
 *   <li>{@link io.dataease.model.RSAModel} - RSA密钥模型</li>
 *   <li>{@link io.dataease.utils.CommonBeanFactory} - Spring Bean工厂工具</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2023-04-03
 * @version 1.0
 */
package io.dataease.rsa;