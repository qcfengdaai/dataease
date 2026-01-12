package io.dataease.utils;

import org.springframework.core.env.Environment;

/**
 * 版本信息工具类
 * <p>
 * 提供DataEase系统版本信息的获取功能。
 * 版本信息从Spring配置文件（application.yml）中读取，用于标识系统的当前版本号。
 * 主要用于HTTP响应头中添加版本标识，便于客户端识别服务端版本。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>版本号获取 - 从配置文件读取系统版本号</li>
 *   <li>默认版本 - 配置缺失时返回默认版本号（2.0.0）</li>
 *   <li>响应头标识 - 在HTTP响应中添加版本信息</li>
 * </ul>
 *
 * <p><b>版本号格式：</b></p>
 * <ul>
 *   <li>主版本号.次版本号.修订号 - 例如：2.10.0</li>
 *   <li>遵循语义化版本规范（Semantic Versioning）</li>
 *   <li>主版本号 - 不兼容的API修改</li>
 *   <li>次版本号 - 向下兼容的功能性新增</li>
 *   <li>修订号 - 向下兼容的问题修正</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.auth.filter.TokenFilter#doFilter} - HTTP请求过滤器中添加版本响应头</li>
 *   <li>系统信息接口 - 返回系统版本信息</li>
 *   <li>升级检查 - 版本比对和升级提示</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：在HTTP响应头中添加版本信息（实际使用场景）
 * // 参考 TokenFilter.java:70
 * String executeVersion = VersionUtil.getRandomVersion();
 * if (StringUtils.isNotBlank(executeVersion)) {
 *     ServletUtils.response().addHeader("DE-EXECUTE-VERSION", executeVersion);
 * }
 *
 * // 示例2：获取当前系统版本
 * String version = VersionUtil.getRandomVersion();
 * System.out.println("DataEase版本: " + version);
 * // 输出示例: DataEase版本: 2.10.0
 *
 * // 示例3：在系统信息接口中使用
 * {@code @GetMapping("/system/info")}
 * public SystemInfoVO getSystemInfo() {
 *     SystemInfoVO info = new SystemInfoVO();
 *     info.setVersion(VersionUtil.getRandomVersion());
 *     info.setProductName("DataEase");
 *     return info;
 * }
 *
 * // 示例4：版本比对（升级检查）
 * String currentVersion = VersionUtil.getRandomVersion();  // 例如: 2.10.0
 * String latestVersion = "2.11.0";
 * if (compareVersion(currentVersion, latestVersion) &lt; 0) {
 *     System.out.println("发现新版本，建议升级");
 * }
 *
 * // 示例5：配置文件中设置版本（application.yml）
 * // dataease:
 * //   version: 2.10.0
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>配置读取 - 版本号从Spring Environment中读取，确保配置文件正确</li>
 *   <li>默认值 - 如果配置文件中未设置版本号，返回默认值2.0.0</li>
 *   <li>方法名称 - getRandomVersion()方法名可能有误导性，实际上是获取配置的版本号，非随机版本</li>
 *   <li>空值断言 - 使用assert确保Environment不为null，生产环境应启用断言</li>
 *   <li>响应头名称 - HTTP响应头使用"DE-EXECUTE-VERSION"标识版本</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>配置文件管理 - 在application.yml中明确配置版本号，不要依赖默认值</li>
 *   <li>版本规范 - 遵循语义化版本规范，便于版本管理和升级</li>
 *   <li>自动化构建 - 通过CI/CD自动更新版本号，避免手动修改</li>
 *   <li>版本日志 - 记录每个版本的更新内容，便于追踪和回溯</li>
 *   <li>前端适配 - 前端可根据版本号判断API兼容性</li>
 * </ul>
 *
 * <p><b>配置说明：</b></p>
 * <pre>
 * # application.yml配置示例
 * dataease:
 *   version: 2.10.0  # 当前系统版本号
 *
 * # 版本号命名规则：
 * # 主版本号：重大架构变更或不兼容的API修改
 * # 次版本号：新功能添加，向下兼容
 * # 修订号：Bug修复和小改进
 *
 * # 示例：
 * # 2.0.0 - 初始版本
 * # 2.1.0 - 新增数据填报功能
 * # 2.1.1 - 修复数据填报Bug
 * # 3.0.0 - 全新架构，不兼容旧版本
 * </pre>
 *
 * <p><b>版本比对示例：</b></p>
 * <pre>
 * // 版本比对工具方法（供参考）
 * public static int compareVersion(String version1, String version2) {
 *     String[] v1 = version1.split("\\.");
 *     String[] v2 = version2.split("\\.");
 *     int maxLen = Math.max(v1.length, v2.length);
 *     for (int i = 0; i &lt; maxLen; i++) {
 *         int num1 = i &lt; v1.length ? Integer.parseInt(v1[i]) : 0;
 *         int num2 = i &lt; v2.length ? Integer.parseInt(v2[i]) : 0;
 *         if (num1 != num2) {
 *             return num1 - num2;
 *         }
 *     }
 *     return 0;
 * }
 *
 * // 使用示例：
 * int result = compareVersion("2.10.0", "2.9.0");
 * // result &gt; 0，表示2.10.0 &gt; 2.9.0
 * </pre>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.auth.filter.TokenFilter
 * @see io.dataease.utils.CommonBeanFactory
 */
public class VersionUtil {

    /**
     * 获取系统版本号
     * <p>
     * 从Spring配置文件中读取dataease.version配置项。
     * 如果配置文件中未设置，则返回默认版本号2.0.0。
     * </p>
     *
     * <p><b>配置路径：</b></p>
     * <ul>
     *   <li>配置项：dataease.version</li>
     *   <li>默认值：2.0.0</li>
     *   <li>配置文件：application.yml或application.properties</li>
     * </ul>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>HTTP响应头中添加版本标识</li>
     *   <li>系统信息接口返回版本号</li>
     *   <li>升级检查和版本比对</li>
     *   <li>日志记录系统版本</li>
     * </ul>
     *
     * <p><b>注意：</b></p>
     * <ul>
     *   <li>方法名getRandomVersion可能有误导性，实际返回的是配置的固定版本号</li>
     *   <li>Environment bean必须存在，否则会触发断言失败</li>
     * </ul>
     *
     * @return 系统版本号字符串，格式为X.Y.Z（如2.10.0）
     * @see io.dataease.auth.filter.TokenFilter#doFilter
     */
    public static String getRandomVersion() {
        Environment environment = CommonBeanFactory.getBean(Environment.class);
        assert environment != null;
        return environment.getProperty("dataease.version", "2.0.0");
    }

}
