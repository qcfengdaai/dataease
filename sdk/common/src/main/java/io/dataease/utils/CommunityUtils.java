package io.dataease.utils;

/**
 * 社区版信息管理工具类
 * <p>
 * 使用ThreadLocal机制管理社区版特定的上下文信息，确保在多线程环境下每个线程拥有独立的信息副本。
 * 主要用于DataEase社区版和企业版的功能差异化处理，通过ThreadLocal传递权限过滤、功能限制等上下文信息。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>线程级信息存储 - 使用ThreadLocal确保线程安全</li>
 *   <li>上下文信息传递 - 在请求处理链路中传递社区版特定信息</li>
 *   <li>版本差异化 - 支持社区版和企业版功能区分</li>
 * </ul>
 *
 * <p><b>实现机制：</b></p>
 * <ul>
 *   <li>使用ThreadLocal<String>存储信息</li>
 *   <li>每个线程拥有独立的信息副本</li>
 *   <li>信息在线程执行期间保持可访问</li>
 *   <li>需要手动清理避免内存泄漏（通过线程池复用时）</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.dataset.manage.DatasetGroupManage} - 数据集权限过滤</li>
 *   <li>{@link io.dataease.datasource.manage.DataSourceManage} - 数据源权限过滤</li>
 *   <li>{@link io.dataease.share.manage.XpackShareManage} - 分享功能权限控制</li>
 *   <li>{@link io.dataease.visualization.manage.CoreVisualizationManage} - 可视化组件权限过滤</li>
 *   <li>{@link io.dataease.visualization.manage.VisualizationStoreManage} - 可视化存储权限过滤</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：设置和获取社区版信息
 * public void processRequest() {
 *     // 设置社区版过滤SQL
 *     String filterSql = "SELECT * FROM resource WHERE id NOT IN (SELECT resource_id FROM xpack_feature)";
 *     CommunityUtils.setInfo(filterSql);
 *
 *     try {
 *         // 在后续处理中获取信息
 *         String info = CommunityUtils.getInfo();
 *         if (StringUtils.isNotBlank(info)) {
 *             // 应用社区版过滤逻辑
 *             applyFilter(info);
 *         }
 *     } finally {
 *         // 清理ThreadLocal，避免内存泄漏
 *         CommunityUtils.setInfo(null);
 *     }
 * }
 *
 * // 示例2：在数据集查询中使用（实际使用场景）
 * // 参考 DatasetGroupManage.java:245
 * public List<DatasetGroup> query(DatasetGroupRequest request) {
 *     QueryWrapper<CoreDatasetGroup> queryWrapper = new QueryWrapper<>();
 *     // ... 其他查询条件
 *
 *     // 应用社区版过滤
 *     String info = CommunityUtils.getInfo();
 *     if (StringUtils.isNotBlank(info)) {
 *         // 使用notExists添加子查询过滤
 *         queryWrapper.notExists(String.format(info, "core_dataset_group.id"));
 *     }
 *
 *     return datasetGroupMapper.selectList(queryWrapper);
 * }
 *
 * // 示例3：在拦截器中设置社区版信息
 * public class CommunityInterceptor implements HandlerInterceptor {
 *     &#64;Override
 *     public boolean preHandle(HttpServletRequest request,
 *                              HttpServletResponse response,
 *                              Object handler) {
 *         // 判断是否为社区版
 *         if (isCommunityEdition()) {
 *             // 设置社区版过滤SQL模板
 *             String filterTemplate = buildCommunityFilterSql();
 *             CommunityUtils.setInfo(filterTemplate);
 *         }
 *         return true;
 *     }
 *
 *     &#64;Override
 *     public void afterCompletion(HttpServletRequest request,
 *                                 HttpServletResponse response,
 *                                 Object handler,
 *                                 Exception ex) {
 *         // 请求结束后清理ThreadLocal
 *         CommunityUtils.setInfo(null);
 *     }
 * }
 *
 * // 示例4：版本功能差异化处理
 * public void executeFeature() {
 *     String communityFilter = CommunityUtils.getInfo();
 *     if (communityFilter != null) {
 *         // 社区版逻辑
 *         executeCommunityVersion();
 *     } else {
 *         // 企业版逻辑
 *         executeEnterpriseVersion();
 *     }
 * }
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>使用ThreadLocal必须注意内存泄漏风险，使用完毕后及时清理</li>
 *   <li>在使用线程池的环境中，线程复用可能导致信息污染</li>
 *   <li>建议在拦截器或过滤器中统一设置和清理</li>
 *   <li>获取信息前应判空，避免NullPointerException</li>
 *   <li>不要存储大对象，ThreadLocal会增加内存占用</li>
 *   <li>该工具仅用于版本差异化，不应用于其他业务逻辑</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>始终在try-finally块中清理ThreadLocal</li>
 *   <li>使用拦截器统一管理信息的设置和清理</li>
 *   <li>存储的信息应该是轻量级的字符串</li>
 *   <li>获取信息后立即判空，提供默认行为</li>
 *   <li>避免在异步任务中使用，子线程无法继承ThreadLocal</li>
 * </ul>
 *
 * <p><b>ThreadLocal风险说明：</b></p>
 * <ul>
 *   <li>内存泄漏：未清理的ThreadLocal可能导致内存无法回收</li>
 *   <li>数据污染：线程池复用时，前一个请求的数据可能影响后续请求</li>
 *   <li>解决方案：在拦截器/过滤器的afterCompletion中清理</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see ThreadLocal
 * @see io.dataease.dataset.manage.DatasetGroupManage
 * @see io.dataease.datasource.manage.DataSourceManage
 */
public class CommunityUtils {

    /**
     * ThreadLocal存储社区版上下文信息
     * <p>
     * 每个线程拥有独立的信息副本，通常存储社区版权限过滤SQL或功能限制标识
     * </p>
     */
    private static final ThreadLocal<String> COMMUNITY_INFO = new ThreadLocal<>();

    /**
     * 设置当前线程的社区版信息
     * <p>
     * 将社区版特定的上下文信息保存到ThreadLocal中，供后续处理使用。
     * 通常在请求开始时（拦截器/过滤器）设置，在请求结束时清理。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 设置社区版过滤SQL
     * String filterSql = "SELECT resource_id FROM xpack_feature WHERE resource_id = %s";
     * CommunityUtils.setInfo(filterSql);
     *
     * // 使用完毕后清理（重要！）
     * CommunityUtils.setInfo(null);
     * </pre>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>设置null可以清理ThreadLocal，避免内存泄漏</li>
     *   <li>建议在finally块中清理，确保一定会执行</li>
     *   <li>不要存储大对象或敏感信息</li>
     * </ul>
     *
     * @param info 社区版上下文信息，通常是SQL过滤模板或功能标识；传null表示清理
     */
    public static void setInfo(String info) {
        COMMUNITY_INFO.set(info);
    }

    /**
     * 获取当前线程的社区版信息
     * <p>
     * 从ThreadLocal中获取之前设置的社区版上下文信息。
     * 如果未设置或已清理，返回null。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 获取社区版信息并应用过滤
     * String info = CommunityUtils.getInfo();
     * if (StringUtils.isNotBlank(info)) {
     *     // 社区版：应用权限过滤
     *     queryWrapper.notExists(String.format(info, "resource.id"));
     * } else {
     *     // 企业版：无需过滤
     * }
     * </pre>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>返回值可能为null，使用前必须判空</li>
     *   <li>仅能获取当前线程设置的信息，无法跨线程</li>
     *   <li>在子线程中调用会返回null（ThreadLocal不继承）</li>
     * </ul>
     *
     * @return 社区版上下文信息；如果未设置则返回null
     */
    public static String getInfo() {
        return COMMUNITY_INFO.get();
    }

}
