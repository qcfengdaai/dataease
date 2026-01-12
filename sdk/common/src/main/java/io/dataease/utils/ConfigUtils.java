package io.dataease.utils;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.Objects;

/**
 * 配置文件读取工具类
 * <p>
 * 提供从YAML配置文件中读取配置项的功能，支持占位符替换和默认值设置。
 * 主要用于读取DataEase应用的外部配置文件（application.yml），实现配置的灵活管理。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>读取YAML配置 - 从指定路径的YAML文件中读取配置项</li>
 *   <li>占位符替换 - 自动替换配置中的占位符（${user.home}、${base-path}）</li>
 *   <li>默认值支持 - 配置项不存在时返回默认值</li>
 *   <li>异常容错 - 读取失败时返回默认值，不抛出异常</li>
 * </ul>
 *
 * <p><b>配置文件路径：</b></p>
 * <ul>
 *   <li>默认路径：{user.home}/opt/dataease2.0/config/application.yml</li>
 *   <li>user.home：用户主目录（通过System.getProperty("user.home")获取）</li>
 *   <li>支持Windows和Linux系统的路径格式</li>
 * </ul>
 *
 * <p><b>占位符说明：</b></p>
 * <ul>
 *   <li>${user.home} - 用户主目录，如 /home/user 或 C:\\Users\\user</li>
 *   <li>${base-path} - 基础路径，从配置文件的base-path项读取</li>
 *   <li>占位符替换顺序：先替换${user.home}，再替换${base-path}</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.constant.StaticResourceConstants#getHomeData()} - 获取数据存储路径</li>
 *   <li>{@link io.dataease.utils.ModelUtils} - 判断运行模式时读取配置</li>
 *   <li>应用启动时读取数据库连接配置</li>
 *   <li>读取文件上传路径、临时文件路径等系统配置</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：读取数据存储路径（带默认值）
 * String dataPath = ConfigUtils.getConfig("dataease.path.data", "/opt/dataease2.0/data");
 * System.out.println("数据路径: " + dataPath);
 * // 如果配置文件中存在dataease.path.data，返回配置值
 * // 如果不存在，返回默认值 /opt/dataease2.0/data
 *
 * // 示例2：读取数据库配置
 * String dbUrl = ConfigUtils.getConfig("spring.datasource.url",
 *         "jdbc:mysql://localhost:3306/dataease");
 * String dbUsername = ConfigUtils.getConfig("spring.datasource.username", "root");
 * String dbPassword = ConfigUtils.getConfig("spring.datasource.password", "password");
 *
 * // 示例3：读取文件上传路径（会自动替换占位符）
 * // 假设application.yml中配置：
 * // base-path: ${user.home}/dataease
 * // file.upload-path: ${base-path}/upload
 * String uploadPath = ConfigUtils.getConfig("file.upload-path", "/tmp/upload");
 * // Windows: C:\\Users\\username\\dataease\\upload
 * // Linux: /home/username/dataease/upload
 *
 * // 示例4：读取端口配置
 * String port = ConfigUtils.getConfig("server.port", "8080");
 * int serverPort = Integer.parseInt(port);
 *
 * // 示例5：读取缓存配置
 * String cacheType = ConfigUtils.getConfig("spring.cache.type", "redis");
 * String redisHost = ConfigUtils.getConfig("spring.redis.host", "localhost");
 *
 * // 示例6：实际应用 - 获取数据目录（参考StaticResourceConstants）
 * public static String getDataDirectory() {
 *     // 从配置文件读取，如果不存在则使用默认路径
 *     String dataDir = ConfigUtils.getConfig("dataease.path.data", "/opt/dataease2.0/data");
 *     // 确保目录存在
 *     File dir = new File(dataDir);
 *     if (!dir.exists()) {
 *         dir.mkdirs();
 *     }
 *     return dataDir;
 * }
 *
 * // 示例7：实际应用 - 占位符替换示例
 * // application.yml内容：
 * // base-path: ${user.home}/opt/dataease2.0
 * // static-resource.path: ${base-path}/static
 * String staticPath = ConfigUtils.getConfig("static-resource.path", "/tmp/static");
 * // 结果: /home/user/opt/dataease2.0/static（Linux）
 * // 或: C:\\Users\\user\\opt\\dataease2.0\\static（Windows）
 *
 * // 示例8：配置不存在时使用默认值
 * String logLevel = ConfigUtils.getConfig("logging.level.root", "INFO");
 * // 如果配置文件中没有logging.level.root，返回"INFO"
 * </pre>
 *
 * <p><b>配置文件示例（application.yml）：</b></p>
 * <pre>
 * # 基础路径配置
 * base-path: ${user.home}/opt/dataease2.0
 *
 * # DataEase配置
 * dataease:
 *   path:
 *     data: ${base-path}/data
 *     upload: ${base-path}/upload
 *     temp: ${base-path}/temp
 *
 * # 数据库配置
 * spring:
 *   datasource:
 *     url: jdbc:mysql://localhost:3306/dataease
 *     username: root
 *     password: dataease123
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>配置文件必须是YAML格式，不支持properties格式</li>
 *   <li>配置文件路径是固定的，无法自定义（由configPath常量定义）</li>
 *   <li>如果配置文件不存在或读取失败，会返回默认值而不会抛出异常</li>
 *   <li>占位符替换只支持${user.home}和${base-path}两种</li>
 *   <li>Windows系统下路径分隔符会自动转换为正斜杠</li>
 *   <li>配置键（key）区分大小写，需要精确匹配</li>
 *   <li>默认值应该设置合理的值，避免返回null导致空指针异常</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>所有配置项都应该提供合理的默认值</li>
 *   <li>配置键应该使用点号分隔的层次结构，如：dataease.path.data</li>
 *   <li>敏感配置（如密码）应该使用环境变量或加密存储</li>
 *   <li>路径配置应该使用${base-path}占位符，便于统一管理</li>
 *   <li>读取配置后应该进行验证，确保配置值的合法性</li>
 *   <li>对于数值型配置，读取后需要进行类型转换和异常处理</li>
 * </ul>
 *
 * <p><b>异常处理：</b></p>
 * <ul>
 *   <li>配置文件不存在 - 返回默认值</li>
 *   <li>配置文件格式错误 - 返回默认值</li>
 *   <li>配置项不存在 - 返回默认值</li>
 *   <li>所有异常都被捕获，不会向外传播</li>
 * </ul>
 *
 * @author Junjun
 * @since 1.0.0
 * @see io.dataease.constant.StaticResourceConstants#getHomeData()
 * @see io.dataease.utils.ModelUtils
 */
public class ConfigUtils {

    /**
     * 配置文件相对路径
     * <p>
     * 配置文件位于用户主目录下的opt/dataease2.0/config/application.yml
     * </p>
     */
    public static String configPath = "opt" + File.separator + "dataease2.0" + File.separator + "config" + File.separator + "application.yml";

    /**
     * 从配置文件中读取配置项
     * <p>
     * 从YAML配置文件中读取指定的配置项，支持占位符自动替换和默认值。
     * 该方法会自动处理以下占位符：
     * <ul>
     *   <li>${user.home} - 替换为用户主目录</li>
     *   <li>${base-path} - 替换为配置文件中定义的base-path值（base-path本身也会先替换${user.home}）</li>
     * </ul>
     * </p>
     *
     * <p><b>执行流程：</b></p>
     * <ol>
     *   <li>获取用户主目录（System.getProperty("user.home")）</li>
     *   <li>去除可能存在的"file:"前缀</li>
     *   <li>拼接完整的配置文件路径</li>
     *   <li>使用YamlPropertiesFactoryBean加载YAML文件</li>
     *   <li>读取base-path配置项并替换其中的${user.home}占位符</li>
     *   <li>读取目标配置项（不存在时使用defaultValue）</li>
     *   <li>替换目标配置项中的${base-path}占位符</li>
     *   <li>返回处理后的配置值</li>
     * </ol>
     *
     * <p><b>占位符替换示例：</b></p>
     * <pre>
     * // 假设：
     * // - user.home = /home/dataease
     * // - base-path配置 = ${user.home}/opt/dataease2.0
     * // - dataease.path.data配置 = ${base-path}/data
     *
     * String dataPath = getConfig("dataease.path.data", "/tmp/data");
     * // 返回: /home/dataease/opt/dataease2.0/data
     *
     * // 占位符替换过程：
     * // 1. base-path: ${user.home}/opt/dataease2.0
     * //    -> /home/dataease/opt/dataease2.0
     * // 2. dataease.path.data: ${base-path}/data
     * //    -> /home/dataease/opt/dataease2.0/data
     * </pre>
     *
     * @param key 配置项的键名，支持点号分隔的层次结构（如：dataease.path.data）
     * @param defaultValue 配置项不存在或读取失败时返回的默认值
     * @return 配置项的值（已替换占位符），如果配置不存在或读取失败则返回defaultValue
     */
    public static String getConfig(String key, String defaultValue) {
        try {
            String filePath = System.getProperty("user.home");
            filePath = filePath.replace("file:", "");
            Resource resource = new FileSystemResource(filePath + File.separator + configPath);
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource);

            String basePath = Objects.requireNonNull(factory.getObject()).getProperty("base-path", "");
            basePath = basePath.replaceAll("\\\\$\\\\{user.home}", System.getProperty("user.home").replaceAll("\\\\\\\\", "/"));

            String property = Objects.requireNonNull(factory.getObject()).getProperty(key, defaultValue);
            return property.replaceAll("\\\\$\\\\{base-path}", basePath);
        } catch (Exception e) {
        }
        return defaultValue;
    }
}
