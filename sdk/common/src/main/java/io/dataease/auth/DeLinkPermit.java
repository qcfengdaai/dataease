package io.dataease.auth;

import java.lang.annotation.*;

/**
 * DataEase分享链接访问许可注解
 * <p>
 * 用于标记允许通过分享链接访问的API方法，无需登录即可访问。
 * 主要用于仪表板分享、图表分享等公开访问场景。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>跳过登录验证 - 允许未登录用户访问</li>
 *   <li>链接Token验证 - 通过分享链接Token进行身份识别</li>
 *   <li>公开访问控制 - 限定特定API可公开访问</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>仪表板分享API - 通过链接查看仪表板</li>
 *   <li>图表分享API - 通过链接查看图表数据</li>
 *   <li>数据集公开查询API - 分享链接的数据查询</li>
 *   <li>导出任务下载API - 通过链接下载导出文件</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：仪表板分享访问
 * {@literal @}DeLinkPermit
 * {@literal @}GetMapping("/share/panel/{shareId}")
 * public PanelVO getSharedPanel(@PathVariable String shareId,
 *                                @RequestParam(required = false) String linkToken) {
 *     // 通过linkToken验证分享链接有效性
 *     // 无需用户登录即可访问
 *     return panelShareService.getByShareId(shareId, linkToken);
 * }
 *
 * // 示例2：图表数据分享
 * {@literal @}DeLinkPermit("chart:share:data")
 * {@literal @}PostMapping("/share/chart/data")
 * public ChartData getSharedChartData(@RequestBody ChartDataRequest request) {
 *     // 获取分享图表的数据
 *     return chartService.getSharedData(request);
 * }
 *
 * // 示例3：导出文件下载
 * {@literal @}DeLinkPermit
 * {@literal @}GetMapping("/export/download/{taskId}")
 * public void downloadExport(@PathVariable String taskId,
 *                            @RequestParam String token,
 *                            HttpServletResponse response) {
 *     // 通过token验证下载权限
 *     exportService.download(taskId, token, response);
 * }
 * </pre>
 *
 * <p><b>与{@link DePermit}的区别：</b></p>
 * <table border="1">
 *   <tr>
 *     <th>注解</th>
 *     <th>用途</th>
 *     <th>登录要求</th>
 *     <th>验证方式</th>
 *   </tr>
 *   <tr>
 *     <td>{@code @DePermit}</td>
 *     <td>正常权限控制</td>
 *     <td>必须登录</td>
 *     <td>用户Token + 权限验证</td>
 *   </tr>
 *   <tr>
 *     <td>{@code @DeLinkPermit}</td>
 *     <td>分享链接访问</td>
 *     <td>无需登录</td>
 *     <td>分享链接Token验证</td>
 *   </tr>
 * </table>
 *
 * <p><b>安全机制：</b></p>
 * <ul>
 *   <li><b>Token验证</b>：分享链接包含唯一Token，验证链接有效性</li>
 *   <li><b>时效控制</b>：支持设置链接过期时间</li>
 *   <li><b>密码保护</b>：支持为分享链接设置访问密码</li>
 *   <li><b>访问限制</b>：可限制访问次数或IP地址</li>
 *   <li><b>权限隔离</b>：分享用户只能访问被分享的资源</li>
 * </ul>
 *
 * <p><b>实现原理：</b></p>
 * <ol>
 *   <li>TokenFilter识别{@code @DeLinkPermit}注解的方法</li>
 *   <li>跳过常规的用户Token验证</li>
 *   <li>从请求参数中提取linkToken或shareToken</li>
 *   <li>验证分享链接的有效性（是否过期、是否需要密码等）</li>
 *   <li>构造临时用户上下文（LinkTokenUserBO）</li>
 *   <li>放行请求，执行业务逻辑</li>
 * </ol>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>该注解会跳过登录验证，需谨慎使用</li>
 *   <li>必须在方法内部验证linkToken的有效性</li>
 *   <li>避免暴露敏感信息，只返回必要的数据</li>
 *   <li>建议配合限流机制，防止恶意访问</li>
 *   <li>分享链接应该使用HTTPS传输，防止Token泄露</li>
 *   <li>不要将{@code @DePermit}和{@code @DeLinkPermit}同时标注在一个方法上</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see DePermit
 * @see io.dataease.auth.bo.LinkTokenUserBO
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeLinkPermit {

    /**
     * 分享访问标识
     * <p>
     * 用于标识不同类型的分享访问，便于审计和统计
     * </p>
     *
     * <p><b>示例：</b></p>
     * <ul>
     *   <li><code>"panel:share"</code> - 仪表板分享</li>
     *   <li><code>"chart:share"</code> - 图表分享</li>
     *   <li><code>"dataset:share"</code> - 数据集分享</li>
     *   <li><code>"export:download"</code> - 导出下载</li>
     * </ul>
     *
     * @return 分享访问标识，默认为空字符串
     */
    String value() default "";

}
