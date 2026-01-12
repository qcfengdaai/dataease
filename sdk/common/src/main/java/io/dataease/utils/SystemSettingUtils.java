package io.dataease.utils;

import io.dataease.constant.XpackSettingConstants;

import java.util.List;

/**
 * 系统设置工具类
 * <p>
 * 提供系统设置相关的工具方法，主要用于判断某个配置项是否属于Xpack扩展包的设置项。
 * DataEase分为社区版和企业版（Xpack），企业版包含额外的高级功能和配置项。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>Xpack设置判断 - 判断配置项是否属于企业版专属设置</li>
 *   <li>功能权限控制 - 用于区分社区版和企业版的功能边界</li>
 *   <li>配置项分类 - 将系统配置项分为基础配置和扩展配置</li>
 * </ul>
 *
 * <p><b>Xpack扩展包配置项：</b></p>
 * <ul>
 *   <li>{@link XpackSettingConstants#AUTO_CREATE_USER} - 自动创建用户（basic.autoCreateUser）</li>
 *   <li>{@link XpackSettingConstants#LOG_LIVE_TIME} - 日志保留时间（basic.logLiveTime）</li>
 *   <li>{@link XpackSettingConstants#PLATFORM_OID} - 平台组织ID（basic.platformOid）</li>
 *   <li>{@link XpackSettingConstants#DIP} - 数据集成平台（basic.dip）</li>
 *   <li>{@link XpackSettingConstants#PVP} - 预览验证平台（basic.pvp）</li>
 *   <li>{@link XpackSettingConstants#PLATFORM_RID} - 平台角色ID（basic.platformRid）</li>
 *   <li>{@link XpackSettingConstants#DEFAULT_LOGIN} - 默认登录方式（basic.defaultLogin）</li>
 *   <li>{@link XpackSettingConstants#THRESHOLD_LOG_LIVE_TIME} - 阈值日志保留时间（basic.thresholdLogLiveTime）</li>
 *   <li>{@link XpackSettingConstants#DATA_FILLING_LOG_LIVE_TIME} - 数据填报日志保留时间（basic.dataFillingLogLiveTime）</li>
 *   <li>{@link XpackSettingConstants#LOGIN_LIMIT} - 登录限制开关（basic.loginLimit）</li>
 *   <li>{@link XpackSettingConstants#LOGIN_LIMIT_RATE} - 登录限制频率（basic.loginLimitRate）</li>
 *   <li>{@link XpackSettingConstants#LOGIN_LIMIT_TIME} - 登录限制时间（basic.loginLimitTime）</li>
 *   <li>{@link XpackSettingConstants#THRESHOLD_LIMIT} - 阈值限制（basic.thresholdLimit）</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>系统设置模块 - 根据版本控制设置项的显示和编辑权限</li>
 *   <li>权限控制 - 限制社区版用户访问企业版功能</li>
 *   <li>功能开关 - 根据是否为Xpack设置项来启用或禁用功能</li>
 *   <li>配置验证 - 验证配置项是否符合当前版本的权限</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：判断配置项是否为Xpack专属设置
 * String settingKey = "basic.autoCreateUser";
 * if (SystemSettingUtils.xpackSetting(settingKey)) {
 *     System.out.println("这是企业版专属设置");
 *     // 检查用户是否有企业版授权
 *     if (!hasXpackLicense()) {
 *         throw new BusinessException("该功能需要企业版授权");
 *     }
 * } else {
 *     System.out.println("这是基础设置，所有版本都可使用");
 * }
 *
 * // 示例2：系统设置保存时的权限校验
 * public void saveSetting(String key, String value) {
 *     // 检查是否为Xpack设置
 *     if (SystemSettingUtils.xpackSetting(key)) {
 *         // 验证企业版授权
 *         if (!licenseService.hasXpackLicense()) {
 *             throw new BusinessException("该设置项需要企业版授权");
 *         }
 *     }
 *     // 保存设置
 *     settingRepository.save(key, value);
 * }
 *
 * // 示例3：前端显示设置项时过滤
 * public List&lt;SystemSetting&gt; getAvailableSettings() {
 *     List&lt;SystemSetting&gt; allSettings = getAllSettings();
 *     boolean hasXpack = licenseService.hasXpackLicense();
 *
 *     return allSettings.stream()
 *         .filter(setting -> {
 *             // 如果是Xpack设置且没有授权，则过滤掉
 *             if (SystemSettingUtils.xpackSetting(setting.getKey())) {
 *                 return hasXpack;
 *             }
 *             return true;
 *         })
 *         .collect(Collectors.toList());
 * }
 *
 * // 示例4：批量检查配置项类型
 * public Map&lt;String, Boolean&gt; checkSettingTypes(List&lt;String&gt; settingKeys) {
 *     Map&lt;String, Boolean&gt; result = new HashMap&lt;&gt;();
 *     for (String key : settingKeys) {
 *         boolean isXpack = SystemSettingUtils.xpackSetting(key);
 *         result.put(key, isXpack);
 *     }
 *     return result;
 * }
 *
 * // 示例5：实际应用 - 根据配置项类型分类展示
 * public SettingGroupDTO getSettingsByCategory() {
 *     SettingGroupDTO group = new SettingGroupDTO();
 *     List&lt;SystemSetting&gt; allSettings = getAllSettings();
 *
 *     // 分类：基础设置和高级设置（Xpack）
 *     group.setBasicSettings(allSettings.stream()
 *         .filter(s -> !SystemSettingUtils.xpackSetting(s.getKey()))
 *         .collect(Collectors.toList()));
 *
 *     group.setAdvancedSettings(allSettings.stream()
 *         .filter(s -> SystemSettingUtils.xpackSetting(s.getKey()))
 *         .collect(Collectors.toList()));
 *
 *     return group;
 * }
 *
 * // 示例6：配置导入时的验证
 * public void importSettings(List&lt;SettingItem&gt; settings) {
 *     boolean hasXpack = licenseService.hasXpackLicense();
 *
 *     for (SettingItem item : settings) {
 *         // 检查是否为Xpack设置
 *         if (SystemSettingUtils.xpackSetting(item.getKey())) {
 *             if (!hasXpack) {
 *                 // 跳过企业版设置
 *                 log.warn("跳过企业版设置项: {}", item.getKey());
 *                 continue;
 *             }
 *         }
 *         // 导入设置
 *         settingService.save(item);
 *     }
 * }
 *
 * // 示例7：获取不同版本的功能列表
 * public List&lt;String&gt; getAvailableFeatures() {
 *     List&lt;String&gt; features = new ArrayList&lt;&gt;();
 *
 *     // 基础功能
 *     features.add("数据源管理");
 *     features.add("数据集管理");
 *     features.add("仪表板管理");
 *
 *     // 企业版功能
 *     if (licenseService.hasXpackLicense()) {
 *         // 这些功能对应的设置项都是Xpack设置
 *         if (SystemSettingUtils.xpackSetting(XpackSettingConstants.AUTO_CREATE_USER)) {
 *             features.add("自动创建用户（SSO集成）");
 *         }
 *         if (SystemSettingUtils.xpackSetting(XpackSettingConstants.LOGIN_LIMIT)) {
 *             features.add("登录限制（安全增强）");
 *         }
 *         features.add("数据填报");
 *         features.add("阈值告警");
 *     }
 *
 *     return features;
 * }
 * </pre>
 *
 * <p><b>配置项说明：</b></p>
 * <table border="1">
 *   <tr>
 *     <th>配置项</th>
 *     <th>键名</th>
 *     <th>功能说明</th>
 *   </tr>
 *   <tr>
 *     <td>自动创建用户</td>
 *     <td>basic.autoCreateUser</td>
 *     <td>通过SSO/LDAP等方式自动创建用户</td>
 *   </tr>
 *   <tr>
 *     <td>日志保留时间</td>
 *     <td>basic.logLiveTime</td>
 *     <td>系统操作日志的保留时长</td>
 *   </tr>
 *   <tr>
 *     <td>登录限制</td>
 *     <td>basic.loginLimit</td>
 *     <td>启用登录失败次数限制</td>
 *   </tr>
 *   <tr>
 *     <td>登录限制频率</td>
 *     <td>basic.loginLimitRate</td>
 *     <td>允许的最大失败次数</td>
 *   </tr>
 *   <tr>
 *     <td>登录限制时间</td>
 *     <td>basic.loginLimitTime</td>
 *     <td>登录锁定的时长</td>
 *   </tr>
 *   <tr>
 *     <td>阈值日志保留时间</td>
 *     <td>basic.thresholdLogLiveTime</td>
 *     <td>阈值告警日志的保留时长</td>
 *   </tr>
 *   <tr>
 *     <td>数据填报日志保留时间</td>
 *     <td>basic.dataFillingLogLiveTime</td>
 *     <td>数据填报操作日志的保留时长</td>
 *   </tr>
 * </table>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>配置项的键名必须与XpackSettingConstants中定义的常量完全一致</li>
 *   <li>该方法只判断配置项类型，不验证用户是否有权限访问</li>
 *   <li>权限验证应该在业务层进行，配合License服务一起使用</li>
 *   <li>新增Xpack配置项时，必须同时更新xpackSettingList列表</li>
 *   <li>配置项的键名采用"basic."前缀，表示属于基础设置类别</li>
 *   <li>该方法为纯工具方法，不涉及数据库操作和业务逻辑</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>在设置项的增删改查操作前，先使用该方法判断权限</li>
 *   <li>前端展示设置项时，根据该方法的返回值决定是否显示</li>
 *   <li>API接口应该在接收请求时验证配置项类型和用户权限</li>
 *   <li>配置项导入导出时，应该过滤掉当前版本不支持的设置</li>
 *   <li>日志中应该记录因权限不足而被拒绝的操作</li>
 *   <li>错误提示应该明确告知用户需要企业版授权</li>
 * </ul>
 *
 * <p><b>与其他组件的关系：</b></p>
 * <ul>
 *   <li>{@link XpackSettingConstants} - 定义所有Xpack配置项的常量</li>
 *   <li>LicenseService - 验证企业版授权状态</li>
 *   <li>SystemSettingService - 系统设置的业务逻辑处理</li>
 *   <li>权限拦截器 - 在请求层面拦截未授权的操作</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see XpackSettingConstants
 */
public class SystemSettingUtils {

    /**
     * 判断配置项是否为Xpack扩展包设置
     * <p>
     * 检查指定的配置项键名是否属于企业版（Xpack）专属的设置项。
     * 该方法通过维护一个Xpack配置项列表，使用contains方法进行判断。
     * </p>
     *
     * <p><b>返回值说明：</b></p>
     * <ul>
     *   <li>true - 该配置项属于企业版专属，需要Xpack授权才能使用</li>
     *   <li>false - 该配置项为基础配置，社区版和企业版都可以使用</li>
     * </ul>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>权限控制：在保存或修改设置前，判断用户是否有权限操作该设置项</li>
     *   <li>界面展示：前端根据返回值决定是否显示某些高级设置项</li>
     *   <li>功能开关：根据配置项类型和授权状态，决定是否启用相关功能</li>
     *   <li>数据导入：导入配置时，过滤掉当前版本不支持的Xpack设置</li>
     * </ul>
     *
     * <p><b>支持的Xpack配置项：</b></p>
     * <ul>
     *   <li>basic.autoCreateUser - 自动创建用户（SSO/LDAP集成）</li>
     *   <li>basic.logLiveTime - 系统日志保留时间</li>
     *   <li>basic.platformOid - 平台组织ID</li>
     *   <li>basic.dip - 数据集成平台配置</li>
     *   <li>basic.pvp - 预览验证平台配置</li>
     *   <li>basic.platformRid - 平台角色ID</li>
     *   <li>basic.defaultLogin - 默认登录方式（本地/LDAP/CAS等）</li>
     *   <li>basic.thresholdLogLiveTime - 阈值告警日志保留时间</li>
     *   <li>basic.dataFillingLogLiveTime - 数据填报日志保留时间</li>
     *   <li>basic.loginLimit - 登录限制开关（启用/禁用）</li>
     *   <li>basic.loginLimitRate - 登录限制频率（失败次数）</li>
     *   <li>basic.loginLimitTime - 登录限制时间（锁定时长）</li>
     *   <li>basic.thresholdLimit - 阈值限制配置</li>
     * </ul>
     *
     * @param pkey 配置项的键名，格式通常为"basic.xxx"
     * @return true表示该配置项属于Xpack扩展包设置，false表示为基础设置
     */
    public static boolean xpackSetting(String pkey) {

        List<String> xpackSettingList = List.of(XpackSettingConstants.AUTO_CREATE_USER,
                XpackSettingConstants.LOG_LIVE_TIME,
                XpackSettingConstants.PLATFORM_OID,
                XpackSettingConstants.DIP,
                XpackSettingConstants.PVP,
                XpackSettingConstants.PLATFORM_RID,
                XpackSettingConstants.DEFAULT_LOGIN,
                XpackSettingConstants.THRESHOLD_LOG_LIVE_TIME,
                XpackSettingConstants.DATA_FILLING_LOG_LIVE_TIME,
                XpackSettingConstants.LOGIN_LIMIT,
                XpackSettingConstants.LOGIN_LIMIT_RATE,
                XpackSettingConstants.LOGIN_LIMIT_TIME,
                XpackSettingConstants.THRESHOLD_LIMIT);
        return xpackSettingList.contains(pkey);
    }
}
