package io.dataease.utils;

import io.dataease.model.DeModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 部署模式工具类
 * <p>
 * 提供DataEase系统部署模式的判断和获取功能。
 * DataEase支持三种部署模式：桌面版(DESKTOP)、单机版(STANDALONE)、分布式版(DISTRIBUTED)。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>获取当前部署模式 - 从Spring配置中读取并返回部署模式枚举</li>
 *   <li>桌面版判断 - 快速判断当前是否为桌面版部署</li>
 *   <li>配置自动注入 - 通过Spring自动读取配置文件中的部署模式</li>
 * </ul>
 *
 * <p><b>部署模式说明：</b></p>
 * <ul>
 *   <li>DESKTOP（桌面版）- 单用户桌面应用，数据存储在本地，适合个人使用</li>
 *   <li>STANDALONE（单机版）- 单机服务器部署，适合小型团队或部门级使用</li>
 *   <li>DISTRIBUTED（分布式版）- 分布式集群部署，适合企业级大规模使用</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.constant.StaticResourceConstants#getHomeData} - 根据部署模式确定数据存储路径</li>
 *   <li>{@link io.dataease.auth.filter.TokenFilter} - 根据部署模式决定认证策略</li>
 *   <li>资源路径配置 - 不同部署模式使用不同的资源存储路径</li>
 *   <li>功能开关 - 某些功能仅在特定部署模式下可用</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：获取当前部署模式
 * DeModel currentModel = ModelUtils.get();
 * System.out.println("当前部署模式: " + currentModel);
 * // 输出: 当前部署模式: STANDALONE
 *
 * // 示例2：判断是否为桌面版
 * if (ModelUtils.isDesktop()) {
 *     System.out.println("当前运行在桌面版模式");
 *     // 桌面版特定逻辑
 *     String localDataPath = getLocalUserDataPath();
 * } else {
 *     System.out.println("当前运行在服务器模式");
 *     // 服务器版特定逻辑
 *     String serverDataPath = getServerDataPath();
 * }
 *
 * // 示例3：根据部署模式选择不同配置（实际使用案例）
 * // 参考 StaticResourceConstants.java:43
 * public static String getHomeData() {
 *     if (ModelUtils.isDesktop()) {
 *         // 桌面版从配置文件读取数据路径，支持用户自定义
 *         return ConfigUtils.getConfig("dataease.path.data", "/opt/dataease2.0/data");
 *     } else {
 *         // 服务器版使用固定路径
 *         return "/opt/dataease2.0/data";
 *     }
 * }
 *
 * // 示例4：根据部署模式控制功能
 * DeModel model = ModelUtils.get();
 * switch (model) {
 *     case DESKTOP:
 *         // 桌面版：禁用多租户功能
 *         multiTenantEnabled = false;
 *         break;
 *     case STANDALONE:
 *         // 单机版：启用基础多用户功能
 *         multiTenantEnabled = true;
 *         clusterEnabled = false;
 *         break;
 *     case DISTRIBUTED:
 *         // 分布式版：启用全部功能
 *         multiTenantEnabled = true;
 *         clusterEnabled = true;
 *         break;
 * }
 *
 * // 示例5：条件性功能启用
 * if (!ModelUtils.isDesktop()) {
 *     // 非桌面版才启用的功能
 *     enableCloudStorage();
 *     enableExternalAuth();
 * }
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>部署模式通过Spring配置文件的spring.profiles.active参数指定</li>
 *   <li>默认值为standalone，如果未配置则使用单机版模式</li>
 *   <li>配置值会自动转为大写后匹配DeModel枚举</li>
 *   <li>该类使用静态变量存储配置，在应用启动后不可变更</li>
 *   <li>必须在Spring容器中使用，依赖@Value注解注入配置</li>
 *   <li>get()方法在modelValue未初始化时调用会抛出NullPointerException</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>配置隔离：根据部署模式使用不同的配置文件（application-desktop.yml等）</li>
 *   <li>功能开关：使用isDesktop()等方法控制特定功能的启用/禁用</li>
 *   <li>资源路径：根据部署模式动态确定数据存储、日志、临时文件等路径</li>
 *   <li>性能优化：分布式版可以启用缓存、消息队列等高级特性</li>
 *   <li>安全策略：不同部署模式使用不同的认证和授权策略</li>
 * </ul>
 *
 * <p><b>配置示例：</b></p>
 * <pre>
 * # application.yml
 * spring:
 *   profiles:
 *     active: standalone  # 可选值: desktop, standalone, distributed
 * </pre>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.model.DeModel
 * @see io.dataease.constant.StaticResourceConstants
 */
@Component
public class ModelUtils {

    private static String modelValue;

    /**
     * 设置部署模式值
     * <p>
     * 通过Spring的@Value注解自动注入配置文件中的部署模式配置。
     * 该方法在Spring容器初始化时自动调用，不需要手动调用。
     * </p>
     *
     * @param modelValue 部署模式配置值（desktop/standalone/distributed），默认为standalone
     */
    @Value("${spring.profiles.active:standalone}")
    public void setModelValue(String modelValue) {
        ModelUtils.modelValue = modelValue;
    }

    /**
     * 获取当前部署模式
     * <p>
     * 返回当前系统的部署模式枚举值。
     * 配置值会自动转为大写后与DeModel枚举匹配。
     * </p>
     *
     * @return 部署模式枚举（DESKTOP/STANDALONE/DISTRIBUTED）
     * @throws IllegalArgumentException 当配置值无法匹配枚举时抛出
     * @throws NullPointerException 当modelValue未初始化时抛出（通常不会发生）
     */
    public static DeModel get() {
        return DeModel.valueOf(modelValue.toUpperCase());
    }

    /**
     * 判断是否为桌面版部署
     * <p>
     * 快捷方法，用于判断当前是否运行在桌面版模式。
     * 桌面版通常有不同的数据存储路径、功能限制和用户交互方式。
     * </p>
     *
     * @return true表示桌面版，false表示服务器版（单机版或分布式版）
     */
    public static boolean isDesktop() {
        return get() == DeModel.DESKTOP;
    }
}
