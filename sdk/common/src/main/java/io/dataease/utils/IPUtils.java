package io.dataease.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.util.Arrays;

/**
 * IP地址工具类
 * <p>
 * 提供客户端真实IP地址获取和服务器IP地址获取功能。支持从HTTP请求头中提取客户端IP，
 * 能够正确处理代理、负载均衡等网络环境下的IP地址识别。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>客户端真实IP获取 - 从HTTP请求中获取客户端真实IP地址</li>
 *   <li>代理穿透 - 支持识别代理服务器转发的真实客户端IP</li>
 *   <li>本地IP转换 - 自动将IPv6本地地址转换为IPv4格式</li>
 *   <li>服务器IP获取 - 获取当前服务器的本机IP地址</li>
 * </ul>
 *
 * <p><b>应用场景：</b></p>
 * <ul>
 *   <li>访问日志记录 - 记录用户访问来源IP</li>
 *   <li>安全审计 - 追踪操作来源，用于安全分析</li>
 *   <li>IP白名单/黑名单 - 基于IP的访问控制</li>
 *   <li>地域统计 - 根据IP分析用户地域分布</li>
 *   <li>水印生成 - 在导出数据时嵌入操作者IP信息</li>
 * </ul>
 *
 * <p><b>实际使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.commons.utils.ExcelWatermarkUtils#transContent} - Excel水印中嵌入用户IP</li>
 *   <li>{@link io.dataease.substitute.permissions.user.SubstituteUserServer} - 用户操作审计记录IP</li>
 *   <li>登录日志 - 记录用户登录IP地址</li>
 *   <li>操作日志 - 记录敏感操作的来源IP</li>
 * </ul>
 *
 * <p><b>支持的代理类型：</b></p>
 * <ul>
 *   <li>x-forwarded-for - 标准HTTP代理头</li>
 *   <li>Proxy-Client-IP - Apache代理服务器</li>
 *   <li>WL-Proxy-Client-IP - WebLogic代理服务器</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：获取客户端IP地址（最常用）
 * String clientIp = IPUtils.get();
 * System.out.println("客户端IP: " + clientIp);
 * // 输出示例: 客户端IP: 192.168.1.100
 *
 * // 示例2：在日志中记录用户IP
 * public void userLogin(String username) {
 *     String ip = IPUtils.get();
 *     log.info("用户 {} 从 {} 登录系统", username, ip);
 *     // 保存登录日志到数据库
 *     saveLoginLog(username, ip, new Date());
 * }
 *
 * // 示例3：IP白名单验证
 * public boolean checkIpWhitelist(List&lt;String&gt; whitelist) {
 *     String clientIp = IPUtils.get();
 *     if (clientIp == null) {
 *         return false;
 *     }
 *     return whitelist.contains(clientIp);
 * }
 *
 * // 示例4：获取服务器IP
 * String serverIp = IPUtils.domain();
 * System.out.println("服务器IP: " + serverIp);
 * // 输出示例: 服务器IP: 10.0.0.15
 *
 * // 示例5：Excel水印中使用（实际场景）
 * // 参考 ExcelWatermarkUtils.java:34
 * String watermarkText = "操作者IP: " + (IPUtils.get() == null ? "127.0.0.1" : IPUtils.get());
 * // 将IP信息添加到Excel水印中
 *
 * // 示例6：操作审计日志
 * public void auditSensitiveOperation(String operation, String details) {
 *     String ip = IPUtils.get();
 *     String username = getCurrentUsername();
 *     AuditLog log = new AuditLog();
 *     log.setUsername(username);
 *     log.setIpAddress(ip);
 *     log.setOperation(operation);
 *     log.setDetails(details);
 *     log.setCreateTime(System.currentTimeMillis());
 *     auditLogService.save(log);
 * }
 *
 * // 示例7：地域分析
 * public String analyzeUserLocation() {
 *     String ip = IPUtils.get();
 *     if (ip != null) {
 *         // 使用IP库查询地域信息
 *         return ipLocationService.getLocation(ip);
 *     }
 *     return "未知";
 * }
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>依赖Spring Web环境，需要在HTTP请求上下文中调用</li>
 *   <li>如果在非HTTP请求线程中调用，{@link #get()}方法会返回null</li>
 *   <li>代理环境下，x-forwarded-for可能包含多个IP，会自动提取第一个非unknown的IP</li>
 *   <li>IPv6本地地址(0:0:0:0:0:0:0:1)会自动转换为IPv4格式(127.0.0.1)</li>
 *   <li>某些负载均衡器可能不传递客户端真实IP，需要配置相应的代理头</li>
 *   <li>在集群环境下，{@link #domain()}返回的是当前节点的IP</li>
 * </ul>
 *
 * <p><b>安全建议：</b></p>
 * <ul>
 *   <li>不要单独依赖IP进行身份认证，IP可能被伪造</li>
 *   <li>在记录IP时同时记录时间戳，便于追踪和分析</li>
 *   <li>定期清理历史IP日志，避免数据库膨胀</li>
 *   <li>对于敏感操作，建议结合IP和其他因素（如设备指纹）进行风控</li>
 *   <li>注意GDPR等隐私法规，IP地址可能被视为个人信息</li>
 * </ul>
 *
 * <p><b>代理环境配置：</b></p>
 * <pre>
 * // Nginx配置示例（转发真实IP）
 * location / {
 *     proxy_pass http://backend;
 *     proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
 *     proxy_set_header X-Real-IP $remote_addr;
 * }
 *
 * // Apache配置示例
 * ProxyPass / http://backend/
 * ProxyPreserveHost On
 * RequestHeader set X-Forwarded-For %{REMOTE_ADDR}s
 * </pre>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.utils.ServletUtils
 * @see io.dataease.commons.utils.ExcelWatermarkUtils
 */
public class IPUtils {

    /**
     * 用于获取客户端真实IP的HTTP请求头列表
     * <p>
     * 按优先级顺序依次检查这些请求头，以获取客户端的真实IP地址。
     * 这些请求头通常由代理服务器、负载均衡器添加。
     * </p>
     */
    private static final String HEAD_KEYS = "x-forwarded-for, Proxy-Client-IP, WL-Proxy-Client-IP";

    /**
     * 表示IP地址未知的标识符
     * <p>
     * 当代理服务器无法获取客户端真实IP时，通常会设置此值
     * </p>
     */
    private static final String UNKNOWN = "unknown";

    /**
     * IPv6格式的本地回环地址
     */
    private static final String LOCAL_IP_KEY = "0:0:0:0:0:0:0:1";

    /**
     * IPv4格式的本地回环地址
     */
    private static final String LOCAL_IP_VAL = "127.0.0.1";

    /**
     * 获取客户端真实IP地址
     * <p>
     * 从HTTP请求中提取客户端的真实IP地址。该方法会按照以下优先级获取IP：
     * <ol>
     *   <li>检查x-forwarded-for头（标准代理头）</li>
     *   <li>检查Proxy-Client-IP头（Apache代理）</li>
     *   <li>检查WL-Proxy-Client-IP头（WebLogic代理）</li>
     *   <li>如果以上都不存在，则获取request.getRemoteAddr()的值</li>
     * </ol>
     * </p>
     *
     * <p><b>IP处理逻辑：</b></p>
     * <ul>
     *   <li>如果请求头包含多个IP（逗号分隔），取第一个有效IP</li>
     *   <li>过滤掉"unknown"标识的IP</li>
     *   <li>自动将IPv6本地地址转换为IPv4格式</li>
     * </ul>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>用户登录日志记录</li>
     *   <li>操作审计追踪</li>
     *   <li>IP访问控制（白名单/黑名单）</li>
     *   <li>数据导出水印（嵌入操作者IP）</li>
     * </ul>
     *
     * @return 客户端IP地址字符串（如"192.168.1.100"）；
     *         如果无法获取或不在HTTP请求上下文中，返回null
     * @throws RuntimeException 当获取ServletRequest失败时（内部捕获，不会向外抛出）
     */
    public static String get() {

        String ipStr = null;
        boolean isProxy = false;

        HttpServletRequest request = null;
        try {
            request = ServletUtils.request();
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            return null;
        }
        if (ObjectUtils.isEmpty(request)) return null;
        String[] keyArr = HEAD_KEYS.split(",");
        for (String key : keyArr) {
            String header = request.getHeader(key.trim());
            if (StringUtils.isNotBlank(header) && !StringUtils.equalsIgnoreCase(UNKNOWN, header)) {
                ipStr = header;
                isProxy = true;
                break;
            }
        }

        if (!isProxy) {
            ipStr = request.getRemoteAddr();
        }
        ipStr = Arrays.stream(ipStr.split(",")).filter(item -> StringUtils.isNotBlank(item) && !StringUtils.equalsIgnoreCase(UNKNOWN, item.trim())).findFirst().orElse(ipStr);
        return StringUtils.equals(LOCAL_IP_KEY, ipStr) ? LOCAL_IP_VAL : ipStr;
    }

    /**
     * 获取当前服务器的本机IP地址
     * <p>
     * 通过Java网络API获取当前服务器（主机）的IP地址。
     * 该方法返回的是服务器在网络中的IP地址，不是客户端IP。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>集群环境 - 识别当前服务节点</li>
     *   <li>服务注册 - 向注册中心报告本机IP</li>
     *   <li>日志记录 - 标识日志来源服务器</li>
     *   <li>分布式任务 - 标识任务执行节点</li>
     *   <li>健康检查 - 服务节点自检</li>
     * </ul>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>多网卡环境下，返回的是Java默认选择的网卡IP</li>
     *   <li>容器环境（Docker/K8s）返回的是容器内部IP，不是宿主机IP</li>
     *   <li>如果网络配置异常，返回127.0.0.1</li>
     *   <li>该方法不依赖HTTP请求上下文，可在任何场景调用</li>
     * </ul>
     *
     * @return 服务器IP地址字符串（如"10.0.0.15"）；
     *         如果获取失败则返回"127.0.0.1"
     */
    public static String domain() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return LOCAL_IP_VAL;
        }
    }
}
