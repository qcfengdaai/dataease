package io.dataease.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP客户端配置类
 * <p>
 * 用于配置Apache HttpClient的各项参数，包括超时时间、字符编码、请求头等。
 * 该配置类与{@link HttpClientUtil}配合使用，提供灵活的HTTP请求配置能力。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>超时配置 - 连接超时、请求超时、Socket超时</li>
 *   <li>字符编码 - 请求和响应的字符集设置</li>
 *   <li>请求头管理 - 自定义HTTP请求头</li>
 *   <li>连接池配置 - 连接管理器超时设置</li>
 * </ul>
 *
 * <p><b>应用场景：</b></p>
 * <ul>
 *   <li>API数据源 - 配置外部API接口的超时和认证</li>
 *   <li>数据同步 - 从第三方系统同步数据时的HTTP配置</li>
 *   <li>文件下载 - 配置大文件下载的超时时间</li>
 *   <li>Webhook调用 - 配置Webhook通知的HTTP参数</li>
 *   <li>第三方集成 - 与外部系统集成时的HTTP配置</li>
 * </ul>
 *
 * <p><b>实际使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.datasource.provider.ApiUtils} - API数据源配置</li>
 *   <li>{@link io.dataease.datasource.provider.EsProvider} - Elasticsearch数据源配置</li>
 *   <li>{@link io.dataease.datasource.provider.ExcelUtils} - Excel数据源HTTP配置</li>
 *   <li>{@link io.dataease.template.manage.TemplateCenterManage} - 模板中心HTTP配置</li>
 *   <li>{@link HttpClientUtil} - 所有HTTP工具方法的配置</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：使用默认配置
 * HttpClientConfig config = new HttpClientConfig();
 * String response = HttpClientUtil.get("https://api.example.com/data", config);
 *
 * // 示例2：自定义超时时间
 * HttpClientConfig config = new HttpClientConfig();
 * config.setConnectTimeout(10000);    // 10秒连接超时
 * config.setSocketTimeout(30000);     // 30秒读取超时
 * String response = HttpClientUtil.get("https://slow-api.example.com/data", config);
 *
 * // 示例3：添加认证请求头
 * HttpClientConfig config = new HttpClientConfig();
 * config.addHeader("Authorization", "Bearer " + token);
 * config.addHeader("X-API-Key", apiKey);
 * String response = HttpClientUtil.get("https://api.example.com/secure-data", config);
 *
 * // 示例4：配置API数据源
 * HttpClientConfig config = new HttpClientConfig();
 * config.addHeader("Content-Type", "application/json");
 * config.addHeader("Accept", "application/json");
 * config.setConnectTimeout(5000);
 * config.setSocketTimeout(20000);
 * String jsonData = HttpClientUtil.post("https://api.example.com/query", requestBody, config);
 *
 * // 示例5：下载大文件（延长超时）
 * HttpClientConfig config = new HttpClientConfig();
 * config.setSocketTimeout(300000);  // 5分钟读取超时
 * byte[] fileData = HttpClientUtil.downloadBytes("https://cdn.example.com/large-file.zip");
 *
 * // 示例6：使用自定义字符编码
 * HttpClientConfig config = new HttpClientConfig();
 * config.setCharset("GBK");  // 处理GBK编码的接口
 * String response = HttpClientUtil.get("https://legacy-api.example.com/data", config);
 *
 * // 示例7：完整配置示例
 * HttpClientConfig config = new HttpClientConfig();
 * // 设置超时
 * config.setConnectTimeout(10000);           // 10秒连接超时
 * config.setConnectionRequestTimeout(5000);  // 5秒获取连接超时
 * config.setSocketTimeout(30000);            // 30秒数据读取超时
 *
 * // 设置编码
 * config.setCharset("UTF-8");
 *
 * // 添加请求头
 * config.addHeader("User-Agent", "DataEase/2.0");
 * config.addHeader("Accept-Language", "zh-CN");
 * config.addHeader("Authorization", "Bearer token123");
 *
 * // 发起请求
 * String response = HttpClientUtil.get("https://api.example.com/data", config);
 * </pre>
 *
 * <p><b>配置参数说明：</b></p>
 * <ul>
 *   <li><b>connectTimeout</b> - 连接超时时间，从发起连接到建立TCP连接的最大等待时间</li>
 *   <li><b>connectionRequestTimeout</b> - 从连接池获取连接的超时时间</li>
 *   <li><b>socketTimeout</b> - Socket超时时间，从发送请求到接收响应数据的最大等待时间</li>
 *   <li><b>charset</b> - 字符编码，用于请求和响应的字符串编解码</li>
 *   <li><b>header</b> - 自定义HTTP请求头，支持添加任意请求头</li>
 * </ul>
 *
 * <p><b>默认配置：</b></p>
 * <ul>
 *   <li>字符集：UTF-8</li>
 *   <li>连接超时：30秒（30000毫秒）</li>
 *   <li>连接请求超时：30秒（30000毫秒）</li>
 *   <li>Socket超时：60秒（60000毫秒）</li>
 *   <li>请求头：空Map（可通过addHeader方法添加）</li>
 * </ul>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>所有超时时间单位均为毫秒</li>
 *   <li>超时时间设置过短可能导致请求失败，过长可能影响系统响应</li>
 *   <li>Socket超时应大于等于API接口的实际响应时间</li>
 *   <li>请求头的key不区分大小写（HTTP协议规范）</li>
 *   <li>相同key的请求头会被覆盖，而非追加</li>
 *   <li>修改配置对象不会影响已发起的请求</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>根据目标API的实际响应时间合理设置超时</li>
 *   <li>快速接口使用较短超时（5-10秒），慢接口适当延长</li>
 *   <li>大文件下载、数据导出等场景需要设置更长的Socket超时</li>
 *   <li>使用连接池时，connectionRequestTimeout不宜过长</li>
 *   <li>统一管理常用配置，避免重复创建</li>
 *   <li>添加User-Agent等标识请求来源的请求头</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see HttpClientUtil
 * @see org.apache.http.client.config.RequestConfig
 */
public class HttpClientConfig {

    /**
     * 字符编码
     * <p>
     * 用于HTTP请求和响应的字符串编解码。
     * 默认为UTF-8，适用于绝大多数场景。
     * </p>
     */
    private String charset = "UTF-8";

    /**
     * 自定义HTTP请求头
     * <p>
     * 存储所有自定义的HTTP请求头，通过{@link #addHeader(String, String)}方法添加。
     * 常用请求头包括：Authorization、Content-Type、Accept等。
     * </p>
     */
    private Map<String, String> header = new HashMap<>();

    /**
     * 连接超时时间（毫秒）
     * <p>
     * 从发起连接请求到建立TCP连接的最大等待时间。
     * 默认30秒，如果目标服务器响应慢或网络延迟高，可适当增加。
     * </p>
     */
    private int connectTimeout = 30000;

    /**
     * 连接请求超时时间（毫秒）
     * <p>
     * 从连接池中获取连接的最大等待时间。
     * 默认30秒，适用于共享连接池的场景。
     * 如果连接池被耗尽，请求会等待此时长后超时。
     * </p>
     */
    private int connectionRequestTimeout = 30000;

    /**
     * Socket超时时间（毫秒）
     * <p>
     * 从发送请求到接收响应数据的最大等待时间。
     * 默认60秒，是最重要的超时配置。
     * 如果接口响应时间超过此值，请求会失败。
     * 对于慢接口、大文件下载等场景，需要适当增加此值。
     * </p>
     */
    private int socketTimeout = 60000;

    /**
     * 构建Apache HttpClient的RequestConfig对象
     * <p>
     * 根据当前配置的超时参数，创建HttpClient所需的RequestConfig对象。
     * 该方法在{@link HttpClientUtil}的各个请求方法中被调用。
     * </p>
     *
     * @return RequestConfig对象，包含所有超时配置
     */
    public RequestConfig buildRequestConfig() {
        Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(connectTimeout);
        builder.setConnectionRequestTimeout(connectionRequestTimeout);
        builder.setSocketTimeout(socketTimeout);
        return builder.build();
    }

    /**
     * 获取字符编码
     *
     * @return 当前配置的字符编码，默认为"UTF-8"
     */
    public String getCharset() {
        return charset;
    }

    /**
     * 设置字符编码
     * <p>
     * 设置HTTP请求和响应的字符编码。
     * 常用编码：UTF-8（默认）、GBK、ISO-8859-1等。
     * </p>
     *
     * @param charset 字符编码（如"UTF-8"、"GBK"）
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * 获取所有自定义请求头
     *
     * @return 请求头Map，key为请求头名称，value为请求头值
     */
    public Map<String, String> getHeader() {
        return header;
    }

    /**
     * 添加自定义请求头
     * <p>
     * 添加一个HTTP请求头，如果key已存在则覆盖原值。
     * 常用请求头：
     * <ul>
     *   <li>Authorization - 认证信息</li>
     *   <li>Content-Type - 内容类型</li>
     *   <li>Accept - 接受的响应类型</li>
     *   <li>User-Agent - 客户端标识</li>
     * </ul>
     * </p>
     *
     * @param key 请求头名称
     * @param value 请求头值
     */
    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    /**
     * 获取连接超时时间
     *
     * @return 连接超时时间（毫秒），默认为30000（30秒）
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 设置连接超时时间
     * <p>
     * 设置从发起连接到建立TCP连接的最大等待时间。
     * </p>
     *
     * @param connectTimeout 连接超时时间（毫秒），建议范围：5000-60000
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * 获取连接请求超时时间
     *
     * @return 连接请求超时时间（毫秒），默认为30000（30秒）
     */
    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    /**
     * 设置连接请求超时时间
     * <p>
     * 设置从连接池获取连接的最大等待时间。
     * 适用于使用连接池的场景。
     * </p>
     *
     * @param connectionRequestTimeout 连接请求超时时间（毫秒）
     */
    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    /**
     * 获取Socket超时时间
     *
     * @return Socket超时时间（毫秒），默认为60000（60秒）
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * 设置Socket超时时间
     * <p>
     * 设置从发送请求到接收响应数据的最大等待时间。
     * 这是最重要的超时配置，直接影响请求是否成功。
     * 对于慢接口或大文件下载，需要适当增加此值。
     * </p>
     *
     * @param socketTimeout Socket超时时间（毫秒），建议根据接口实际响应时间设置
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

}
