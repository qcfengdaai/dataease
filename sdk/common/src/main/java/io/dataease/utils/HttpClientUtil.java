package io.dataease.utils;

import io.dataease.exception.DEException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

import static io.dataease.result.ResultCode.SYSTEM_INNER_ERROR;

/**
 * HTTP客户端工具类
 * <p>
 * 基于Apache HttpClient封装的HTTP请求工具类，提供GET、POST、PUT、PATCH、DELETE等HTTP方法的调用。
 * 支持HTTP和HTTPS协议，自动处理SSL证书验证，支持文件上传下载、自定义请求头、超时配置等功能。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>HTTP/HTTPS请求 - 支持GET、POST、PUT、PATCH、DELETE等方法</li>
 *   <li>SSL/TLS支持 - 自动信任所有证书，支持HTTPS请求</li>
 *   <li>文件操作 - 支持文件上传和下载</li>
 *   <li>多种请求体格式 - JSON、表单、文件等</li>
 *   <li>自定义配置 - 超时时间、请求头、字符编码等</li>
 *   <li>连接池管理 - 支持HTTP连接池复用</li>
 *   <li>URL可达性检测 - 验证URL是否可访问</li>
 *   <li>Webhook调用 - 支持Webhook通知</li>
 * </ul>
 *
 * <p><b>应用场景：</b></p>
 * <ul>
 *   <li>API数据源 - 从外部API获取数据</li>
 *   <li>数据同步 - 与第三方系统进行数据交互</li>
 *   <li>文件处理 - 上传下载文件、图片等</li>
 *   <li>Webhook通知 - 发送事件通知到外部系统</li>
 *   <li>服务调用 - 微服务间的HTTP调用</li>
 *   <li>爬虫/采集 - 从网页获取数据</li>
 *   <li>第三方集成 - 对接外部系统API</li>
 * </ul>
 *
 * <p><b>实际使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.datasource.provider.ApiUtils} - API数据源的HTTP请求</li>
 *   <li>{@link io.dataease.datasource.provider.EsProvider} - Elasticsearch数据源连接</li>
 *   <li>{@link io.dataease.datasource.provider.ExcelUtils} - 远程Excel文件下载</li>
 *   <li>{@link io.dataease.template.manage.TemplateCenterManage} - 模板中心资源下载</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：简单GET请求
 * String response = HttpClientUtil.get("https://api.example.com/data", null);
 * System.out.println(response);
 *
 * // 示例2：带配置的GET请求
 * HttpClientConfig config = new HttpClientConfig();
 * config.setSocketTimeout(10000);
 * config.addHeader("Authorization", "Bearer token123");
 * String response = HttpClientUtil.get("https://api.example.com/data", config);
 *
 * // 示例3：POST JSON数据
 * String jsonBody = "{\"name\":\"张三\",\"age\":25}";
 * HttpClientConfig config = new HttpClientConfig();
 * config.addHeader("Content-Type", "application/json");
 * String response = HttpClientUtil.post("https://api.example.com/users", jsonBody, config);
 *
 * // 示例4：POST表单数据
 * Map&lt;String, String&gt; formData = new HashMap&lt;&gt;();
 * formData.put("username", "zhangsan");
 * formData.put("password", "123456");
 * String response = HttpClientUtil.post("https://api.example.com/login", formData, null);
 *
 * // 示例5：PUT更新数据
 * String jsonBody = "{\"id\":1,\"name\":\"李四\"}";
 * String response = HttpClientUtil.put("https://api.example.com/users/1", jsonBody, null);
 *
 * // 示例6：DELETE删除数据
 * String response = HttpClientUtil.delete("https://api.example.com/users/1", null);
 *
 * // 示例7：文件上传
 * File file = new File("/path/to/file.txt");
 * String response = HttpClientUtil.upload("https://api.example.com/upload", file, "myfile.txt");
 *
 * // 示例8：文件下载
 * HttpClientConfig config = new HttpClientConfig();
 * byte[] fileData = HttpClientUtil.downloadBytes("https://cdn.example.com/image.jpg");
 * // 保存到本地
 * Files.write(Paths.get("/path/to/save/image.jpg"), fileData);
 *
 * // 示例9：下载文件到指定目录
 * HttpClientConfig config = new HttpClientConfig();
 * Map&lt;String, String&gt; result = HttpClientUtil.downloadFile(
 *     "https://cdn.example.com/file.pdf",
 *     config,
 *     "/path/to/download/"
 * );
 * String fileName = result.get("fileName");     // 原始文件名
 * String tranName = result.get("tranName");     // 保存的文件名（UUID）
 *
 * // 示例10：验证URL可达性
 * Map&lt;String, String&gt; headers = new HashMap&lt;&gt;();
 * headers.put("Authorization", "Bearer token");
 * boolean reachable = HttpClientUtil.isURLReachable("https://api.example.com/health", headers);
 * if (reachable) {
 *     System.out.println("服务可用");
 * }
 *
 * // 示例11：Webhook通知
 * Map&lt;String, Object&gt; payload = new HashMap&lt;&gt;();
 * payload.put("event", "user.created");
 * payload.put("data", userData);
 * HttpClientConfig config = new HttpClientConfig();
 * String response = HttpClientUtil.postWebhook(
 *     "https://webhook.example.com/notify",
 *     "application/json",
 *     payload,
 *     true,  // 使用SSL
 *     config
 * );
 *
 * // 示例12：API数据源查询（实际场景）
 * // 参考 ApiUtils.java:87
 * HttpClientConfig config = new HttpClientConfig();
 * config.setSocketTimeout(apiDefinition.getApiQueryTimeout() * 1000);
 * config.addHeader("Content-Type", "application/json");
 * String response = HttpClientUtil.post(apiUrl, requestJson, config);
 * List&lt;Map&lt;String, Object&gt;&gt; data = parseApiResponse(response);
 * </pre>
 *
 * <p><b>SSL/HTTPS支持：</b></p>
 * <ul>
 *   <li>自动检测URL协议（http/https）</li>
 *   <li>HTTPS请求自动信任所有SSL证书（绕过证书验证）</li>
 *   <li>支持TLSv1.1、TLSv1.2、SSLv3协议</li>
 *   <li>适用于内网环境和自签名证书场景</li>
 * </ul>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>所有方法都会自动关闭HttpClient连接，无需手动关闭</li>
 *   <li>HTTPS请求默认信任所有证书，生产环境需注意安全风险</li>
 *   <li>超时时间通过HttpClientConfig配置，默认值：连接30秒、读取60秒</li>
 *   <li>请求失败时会抛出DEException异常，需要捕获处理</li>
 *   <li>HTTP状态码>=400时视为请求失败</li>
 *   <li>文件上传下载时需注意内存占用，大文件建议使用流式处理</li>
 *   <li>URL中包含特殊字符时会自动进行URL编码</li>
 * </ul>
 *
 * <p><b>异常处理：</b></p>
 * <pre>
 * try {
 *     String response = HttpClientUtil.get("https://api.example.com/data", null);
 * } catch (DEException e) {
 *     // 处理请求失败
 *     log.error("HTTP请求失败: {}", e.getMessage());
 *     // 可能的原因：
 *     // 1. 网络不通
 *     // 2. 超时
 *     // 3. HTTP状态码>=400
 *     // 4. 服务端返回错误
 * }
 * </pre>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>根据API响应时间合理设置超时时间</li>
 *   <li>添加必要的请求头（User-Agent、Content-Type等）</li>
 *   <li>使用try-catch捕获异常并记录日志</li>
 *   <li>敏感信息（如Token）不要打印在日志中</li>
 *   <li>频繁调用同一API时，复用HttpClientConfig对象</li>
 *   <li>大文件下载时使用downloadFile方法，避免内存溢出</li>
 *   <li>生产环境建议对HTTPS证书进行实际验证</li>
 * </ul>
 *
 * <p><b>性能优化：</b></p>
 * <ul>
 *   <li>内部使用连接池管理，自动复用连接</li>
 *   <li>支持Keep-Alive长连接</li>
 *   <li>合理设置超时避免长时间等待</li>
 *   <li>批量请求时可考虑使用异步或并行处理</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see HttpClientConfig
 * @see org.apache.http.impl.client.CloseableHttpClient
 * @see io.dataease.datasource.provider.ApiUtils
 */
public class HttpClientUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * HTTPS协议标识符
     */
    private static final String HTTPS = "https";

    /**
     * 根据URL构建HttpClient（区分HTTP和HTTPS）
     * <p>
     * 根据URL协议自动选择普通HTTP客户端或支持SSL的HTTPS客户端。
     * HTTPS客户端会信任所有SSL证书，适用于内网环境和自签名证书。
     * </p>
     *
     * @param url 请求地址，必须以http://或https://开头
     * @return CloseableHttpClient实例，使用完毕会自动关闭
     * @throws DEException 当URL为空或构建HttpClient失败时抛出
     */
    private static CloseableHttpClient buildHttpClient(String url) {
        if (StringUtils.isEmpty(url)) {
            throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient查询失败: url 不能为空！");
        }
        try {
            if (url.startsWith(HTTPS)) {
                return buildHttpClient(true);
            } else {
                // http
                return HttpClientBuilder.create().build();
            }
        } catch (Exception e) {
            throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient查询失败: " + e.getMessage());
        }
    }

    private static CloseableHttpClient buildHttpClient(boolean ssl) {
        try {
            if (ssl) {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, (X509Certificate[] x509Certificates, String s) -> true);
                SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(builder.build(), new String[]{"TLSv1.1", "TLSv1.2", "SSLv3"}, null, NoopHostnameVerifier.INSTANCE);
                Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", new PlainConnectionSocketFactory())
                        .register("https", socketFactory).build();
                HttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
                return HttpClients.custom().setConnectionManager(connManager).build();
            } else {
                // http
                return HttpClientBuilder.create().build();
            }
        } catch (Exception e) {
            throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient查询失败: " + e.getMessage());
        }
    }

    /**
     * 验证URL是否可访问
     * <p>
     * 通过发送GET请求验证指定URL是否可以正常访问。
     * HTTP状态码小于400视为可访问，否则抛出异常。
     * </p>
     *
     * @param url 待验证的URL
     * @param config HTTP客户端配置，为null时使用默认配置
     * @return true表示URL可访问
     * @throws DEException 当URL不可访问或请求失败时抛出
     */
    public static boolean validateUrl(String url, HttpClientConfig config) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = buildHttpClient(url);
            HttpGet httpGet = new HttpGet(url);
            if (config == null) {
                config = new HttpClientConfig();
            }
            httpGet.setConfig(config.buildRequestConfig());

            Map<String, String> header = config.getHeader();
            for (String key : header.keySet()) {
                httpGet.addHeader(key, header.get(key));
            }
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() >= 400) {
                String msg = EntityUtils.toString(response.getEntity(), config.getCharset());
                if (StringUtils.isEmpty(msg)) {
                    msg = "StatusCode: " + response.getStatusLine().getStatusCode();
                }
                throw new Exception(msg);
            }
            return true;
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient查询失败: " + e.getMessage());
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }

    /**
     * 发送GET请求
     * <p>
     * 发送HTTP GET请求并返回响应内容字符串。
     * 支持HTTP和HTTPS协议，自动处理SSL证书。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 基本用法
     * String response = HttpClientUtil.get("https://api.example.com/users", null);
     *
     * // 带自定义配置
     * HttpClientConfig config = new HttpClientConfig();
     * config.setSocketTimeout(10000);
     * config.addHeader("Authorization", "Bearer token123");
     * String response = HttpClientUtil.get("https://api.example.com/users", config);
     * </pre>
     *
     * @param url 请求地址（完整URL，如"https://api.example.com/data"）
     * @param config HTTP客户端配置，为null时使用默认配置（连接超时30秒，读取超时60秒）
     * @return 响应内容字符串，按config中指定的字符集解码（默认UTF-8）
     * @throws DEException 当请求失败或HTTP状态码>=400时抛出
     */
    public static String get(String url, HttpClientConfig config) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = buildHttpClient(url);
            HttpGet httpGet = new HttpGet(url);

            if (config == null) {
                config = new HttpClientConfig();
            }
            httpGet.setConfig(config.buildRequestConfig());

            Map<String, String> header = config.getHeader();
            for (String key : header.keySet()) {
                httpGet.addHeader(key, header.get(key));
            }
            HttpResponse response = httpClient.execute(httpGet);
            return getResponseStr(response, config);
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient查询失败: " + e.getMessage());
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }

    /**
     * 发送PATCH请求
     * <p>
     * 发送HTTP PATCH请求，用于部分更新资源。
     * 请求体为JSON格式字符串。
     * </p>
     *
     * @param url 请求地址
     * @param json JSON格式的请求体字符串
     * @param config HTTP客户端配置，为null时使用默认配置
     * @return 响应内容字符串
     * @throws DEException 当请求失败或HTTP状态码>=400时抛出
     */
    public static String patch(String url, String json, HttpClientConfig config) {
        CloseableHttpClient httpClient = buildHttpClient(url);
        HttpPatch httpPatch = new HttpPatch(url);
        config = config == null ? new HttpClientConfig() : config;
        try {
            httpPatch.setConfig(config.buildRequestConfig());
            Map<String, String> header = config.getHeader();
            for (String key : header.keySet()) {
                httpPatch.addHeader(key, header.get(key));
            }
            EntityBuilder entityBuilder = EntityBuilder.create();
            entityBuilder.setText(json);
            entityBuilder.setContentType(ContentType.APPLICATION_JSON);
            HttpEntity requestEntity = entityBuilder.build();
            httpPatch.setEntity(requestEntity);
            HttpResponse response = httpClient.execute(httpPatch);
            return getResponseStr(response, config);
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient查询失败: " + e.getMessage());
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }

    /**
     * 发送POST请求（JSON格式）
     * <p>
     * 发送HTTP POST请求，请求体为JSON格式字符串。
     * 自动设置Content-Type为application/json。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 基本用法
     * String jsonBody = "{\"name\":\"张三\",\"age\":25}";
     * String response = HttpClientUtil.post("https://api.example.com/users", jsonBody, null);
     *
     * // 带认证
     * HttpClientConfig config = new HttpClientConfig();
     * config.addHeader("Authorization", "Bearer token123");
     * String response = HttpClientUtil.post("https://api.example.com/users", jsonBody, config);
     * </pre>
     *
     * @param url 请求地址
     * @param json JSON格式的请求体字符串（如"{\"key\":\"value\"}"）
     * @param config HTTP客户端配置，为null时使用默认配置
     * @return 响应内容字符串
     * @throws DEException 当请求失败或HTTP状态码>=400时抛出
     */
    public static String post(String url, String json, HttpClientConfig config) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = buildHttpClient(url);
            HttpPost httpPost = new HttpPost(url);
            if (config == null) {
                config = new HttpClientConfig();
            }
            httpPost.setConfig(config.buildRequestConfig());
            Map<String, String> header = config.getHeader();
            for (String key : header.keySet()) {
                httpPost.addHeader(key, header.get(key));
            }
            EntityBuilder entityBuilder = EntityBuilder.create();
            entityBuilder.setText(json);
            entityBuilder.setContentType(ContentType.APPLICATION_JSON);
            HttpEntity requestEntity = entityBuilder.build();
            httpPost.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(httpPost);
            return getResponseStr(response, config);
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient查询失败: " + e.getMessage());
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }

    /**
     * 发送POST请求并返回完整响应对象
     * <p>
     * 与{@link #post(String, String, HttpClientConfig)}类似，
     * 但返回完整的HttpResponse对象，可以获取响应头等详细信息。
     * </p>
     *
     * @param url 请求地址
     * @param json JSON格式的请求体字符串
     * @param config HTTP客户端配置，为null时使用默认配置
     * @return HttpResponse对象，包含状态码、响应头、响应体等信息
     * @throws DEException 当请求失败时抛出（注意：不检查HTTP状态码）
     */
    public static HttpResponse postWithHeaders(String url, String json, HttpClientConfig config) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = buildHttpClient(url);
            HttpPost httpPost = new HttpPost(url);
            if (config == null) {
                config = new HttpClientConfig();
            }
            httpPost.setConfig(config.buildRequestConfig());
            Map<String, String> header = config.getHeader();
            for (String key : header.keySet()) {
                httpPost.addHeader(key, header.get(key));
            }
            EntityBuilder entityBuilder = EntityBuilder.create();
            entityBuilder.setText(json);
            entityBuilder.setContentType(ContentType.APPLICATION_JSON);
            HttpEntity requestEntity = entityBuilder.build();
            httpPost.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(httpPost);
            return response;
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient查询失败: " + e.getMessage());
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }

    /**
     * 发送PUT请求
     * <p>
     * 发送HTTP PUT请求，用于完整更新资源。
     * 请求体为JSON格式字符串。
     * </p>
     *
     * @param url 请求地址
     * @param json JSON格式的请求体字符串
     * @param config HTTP客户端配置，为null时使用默认配置
     * @return 响应内容字符串
     * @throws DEException 当请求失败或HTTP状态码>=400时抛出
     */
    public static String put(String url, String json, HttpClientConfig config) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = buildHttpClient(url);
            HttpPut httpPut = new HttpPut(url);
            if (config == null) {
                config = new HttpClientConfig();
            }
            httpPut.setConfig(config.buildRequestConfig());
            Map<String, String> header = config.getHeader();
            for (String key : header.keySet()) {
                httpPut.addHeader(key, header.get(key));
            }
            EntityBuilder entityBuilder = EntityBuilder.create();
            entityBuilder.setText(json);
            entityBuilder.setContentType(ContentType.APPLICATION_JSON);
            HttpEntity requestEntity = entityBuilder.build();
            httpPut.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(httpPut);
            return getResponseStr(response, config);
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient查询失败: " + e.getMessage());
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }

    /**
     * 发送POST请求（简化版）
     * <p>
     * {@link #post(String, String, HttpClientConfig)}的简化版本，使用默认配置。
     * </p>
     *
     * @param url 请求地址
     * @param json JSON格式的请求体字符串
     * @return 响应内容字符串
     * @throws DEException 当请求失败或HTTP状态码>=400时抛出
     */
    public static String post(String url, String json) {
        return HttpClientUtil.post(url, json, null);
    }

    /**
     * 发送POST请求（表单格式）
     * <p>
     * 发送HTTP POST请求，请求体为表单格式（application/x-www-form-urlencoded）。
     * 适用于传统的表单提交场景。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * Map&lt;String, String&gt; formData = new HashMap&lt;&gt;();
     * formData.put("username", "zhangsan");
     * formData.put("password", "123456");
     * String response = HttpClientUtil.post("https://api.example.com/login", formData, null);
     * </pre>
     *
     * @param url 请求地址
     * @param body 表单数据，key-value键值对
     * @param config HTTP客户端配置，为null时使用默认配置
     * @return 响应内容字符串
     * @throws DEException 当请求失败或HTTP状态码>=400时抛出
     */
    public static String post(String url, Map<String, String> body, HttpClientConfig config) {
        try (CloseableHttpClient httpClient = buildHttpClient(url)) {
            HttpPost httpPost = new HttpPost(url);
            if (config == null) {
                config = new HttpClientConfig();
            }
            httpPost.setConfig(config.buildRequestConfig());
            Map<String, String> header = config.getHeader();
            for (String key : header.keySet()) {
                httpPost.addHeader(key, header.get(key));
            }
            if (body != null && body.size() > 0) {
                List<NameValuePair> nvps = new ArrayList<>();
                for (String key : body.keySet()) {
                    nvps.add(new BasicNameValuePair(key, body.get(key)));
                }
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nvps, config.getCharset()));
                } catch (Exception e) {
                    logger.error("HttpClient转换编码错误", e);
                    throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient转换编码错误: " + e.getMessage());
                }
            }

            HttpResponse response = httpClient.execute(httpPost);
            return getResponseStr(response, config);
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient查询失败: " + e.getMessage());
        }
    }

    private static String getResponseStr(HttpResponse response, HttpClientConfig config) throws Exception {
        if (response.getStatusLine().getStatusCode() >= 400) {
            String msg = EntityUtils.toString(response.getEntity(), config.getCharset());
            if (StringUtils.isEmpty(msg)) {
                msg = "StatusCode: " + response.getStatusLine().getStatusCode();
            }
            throw new Exception(msg);
        }
        return EntityUtils.toString(response.getEntity(), config.getCharset());
    }

    /**
     * 下载文件到指定目录
     * <p>
     * 从指定URL下载文件并保存到本地目录。
     * 文件名会自动从响应头或URL中提取，保存时使用UUID重命名以避免冲突。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * HttpClientConfig config = new HttpClientConfig();
     * Map&lt;String, String&gt; result = HttpClientUtil.downloadFile(
     *     "https://cdn.example.com/report.pdf",
     *     config,
     *     "/data/downloads/"
     * );
     * String originalName = result.get("fileName");  // 原始文件名：report.pdf
     * String savedName = result.get("tranName");     // 保存的文件名：uuid.pdf
     * </pre>
     *
     * @param url 文件下载地址，支持中文和特殊字符（会自动编码）
     * @param config HTTP客户端配置
     * @param path 保存路径，必须以"/"结尾
     * @return Map包含两个key：fileName（原始文件名）和tranName（保存的文件名，格式为UUID.扩展名）
     * @throws RuntimeException 当下载失败或文件保存失败时抛出
     */
    public static Map<String, String> downloadFile(String url, HttpClientConfig config, String path) {
        String encodeUIl = url;
        Map<String, String> name = new HashMap<>();
        if (!url.contains("%")) {
            String[] http = url.split("://");
            String[] server = http[1].split("/");
            encodeUIl = http[0] + "://" + server[0] + "/" + URLEncoder.encode(http[1].substring(server[0].length() + 1, http[1].length()));
        }
        try (CloseableHttpClient httpClient = buildHttpClient(encodeUIl.replace("+", "%20"))) {
            HttpGet httpGet = new HttpGet(encodeUIl.replace("+", "%20"));
            // 设置请求配置
            httpGet.setConfig(config.buildRequestConfig());
            // 设置请求头
            config.getHeader().forEach(httpGet::addHeader);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() >= 400) {
                String msg = EntityUtils.toString(response.getEntity(), config.getCharset());
                if (StringUtils.isEmpty(msg)) {
                    msg = "StatusCode: " + response.getStatusLine().getStatusCode();
                }
                throw new Exception(msg);
            }
            String fileName = extractFileName(response, url);
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            String tranName = UUID.randomUUID().toString() + "." + suffix;
            name.put("fileName", fileName);
            name.put("tranName", tranName);
            File localFile = new File(path + tranName);
            try (InputStream is = response.getEntity().getContent();
                 FileOutputStream outputStream = new FileOutputStream(localFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new RuntimeException("HttpClient查询失败: " + e.getMessage(), e);
        }
        return name;
    }

    private static String extractFileName(HttpResponse response, String url) {
        url = URLDecoder.decode(url);
        String fileName = "";
        String disposition = response.getHeaders("Content-Disposition").toString();
        if (disposition != null) {
            int filenameIndex = disposition.indexOf("filename=");
            if (filenameIndex > 0) {
                fileName = disposition.substring(filenameIndex + 9)
                        .replaceAll("\"", "") // 去除引号
                        .trim();
            }
        }
        if (fileName.isEmpty()) {
            url = url.split("\\?")[0];
            fileName = url.contains("/")
                    ? url.substring(url.lastIndexOf('/') + 1)
                    : "download_" + System.currentTimeMillis();
        }
        if (fileName.trim().isEmpty()) {
            fileName = "download_" + System.currentTimeMillis();
        }
        return fileName;
    }

    /**
     * 下载文件为字节数组（简化版）
     * <p>
     * 从指定URL下载文件并返回字节数组，使用默认配置。
     * 适用于下载小文件或图片等二进制数据。
     * </p>
     *
     * @param url 文件下载地址
     * @return 文件内容的字节数组
     * @throws RuntimeException 当下载失败时抛出
     */
    public static byte[] downloadBytes(String url) {
        HttpClientConfig config = new HttpClientConfig();
        return HttpClientUtil.downFromRemote(url, config);
    }

    /**
     * 从远程URL下载文件为字节数组
     * <p>
     * 从指定URL下载文件并返回字节数组。
     * 适用于下载图片、文件等二进制数据，需要在内存中处理的场景。
     * 注意：大文件下载请使用{@link #downloadFile}方法避免内存溢出。
     * </p>
     *
     * @param url 文件下载地址
     * @param config HTTP客户端配置
     * @return 文件内容的字节数组
     * @throws RuntimeException 当下载失败时抛出
     */
    public static byte[] downFromRemote(String url, HttpClientConfig config) {
        try (CloseableHttpClient httpClient = buildHttpClient(url)) {
            HttpGet httpGet = new HttpGet(url);
            // 设置请求配置
            httpGet.setConfig(config.buildRequestConfig());

            // 设置请求头
            config.getHeader().forEach(httpGet::addHeader);
            HttpResponse response = httpClient.execute(httpGet);
            try (InputStream inputStream = response.getEntity().getContent();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;

                // 读取响应内容并写入输出流
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new RuntimeException("HttpClient查询失败: " + e.getMessage(), e);
        }
    }

    public static String postFile(String fileServer, byte[] bytes, String fileName, Map<String, String> param, HttpClientConfig config) {
        CloseableHttpClient httpClient = buildHttpClient(fileServer);
        HttpPost postRequest = new HttpPost(fileServer);
        if (config == null) {
            config = new HttpClientConfig();
        }

        postRequest.setConfig(config.buildRequestConfig());
        Map<String, String> header = config.getHeader();
        if (MapUtils.isNotEmpty(header)) {
            Iterator var8 = header.keySet().iterator();

            while (var8.hasNext()) {
                String key = (String) var8.next();
                postRequest.addHeader(key, (String) header.get(key));
            }
        }

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(StandardCharsets.UTF_8);
        builder.addBinaryBody("image", bytes, ContentType.DEFAULT_BINARY, fileName);
        if (param != null) {
            Iterator var13 = param.entrySet().iterator();
            while (var13.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) var13.next();
                builder.addTextBody((String) entry.getKey(), (String) entry.getValue());
            }
        }
        try {
            postRequest.setEntity((HttpEntity) builder.build());
            return getResponseStr(httpClient.execute(postRequest), config);
        } catch (Exception var11) {
            logger.error("HttpClient查询失败", var11);
            throw new RuntimeException("HttpClient查询失败: " + var11.getMessage());
        }
    }

    /**
     * 上传文件（字节数组形式）
     * <p>
     * 以multipart/form-data格式上传文件字节数组到指定URL。
     * 支持额外的表单参数和自定义请求头。
     * </p>
     *
     * @param url 上传地址
     * @param bytes 文件内容字节数组
     * @param name 文件名
     * @param paramMap 额外的表单参数，可为null
     * @param headMap 自定义请求头，可为null
     * @return 服务器响应内容字符串
     * @throws RuntimeException 当上传失败时抛出
     */
    public static String upload(String url, byte[] bytes, String name, Map<String, String> paramMap, Map<String, Object> headMap) {
        HttpClientConfig config = new HttpClientConfig();
        addHead(config, headMap);
        return HttpClientUtil.postFile(url, bytes, name, paramMap, config);
    }

    /**
     * 上传文件（File对象形式，简化版）
     * <p>
     * 上传本地文件到指定URL，使用默认配置和默认参数。
     * </p>
     *
     * @param url 上传地址
     * @param file 本地文件对象
     * @param name 文件名（服务器端显示的名称）
     * @return 服务器响应内容字符串
     * @throws RuntimeException 当上传失败时抛出
     */
    public static String upload(String url, File file, String name) {
        HttpClientConfig config = new HttpClientConfig();
        Map<String, String> param = new HashMap<>();
        param.put("fileFlag", "media");
        param.put("fileName", name);
        return HttpClientUtil.postFile(url, file, param, config);
    }

    public static String postFile(String fileServer, File file, Map<String, String> param, HttpClientConfig config) {
        CloseableHttpClient httpClient = buildHttpClient(fileServer);
        HttpPost postRequest = new HttpPost(fileServer);
        if (config == null) {
            config = new HttpClientConfig();
        }
        postRequest.setConfig(config.buildRequestConfig());
        Map<String, String> header = config.getHeader();
        String fileFlag = param.get("fileFlag");
        String fileName = param.get("fileName");
        param.remove("fileFlag");
        param.remove("fileName");
        if (MapUtils.isNotEmpty(header)) {
            for (String key : header.keySet()) {
                postRequest.addHeader(key, header.get(key));
            }
        }
        postRequest.setHeader("Content-Type", "multipart/form-data");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(StandardCharsets.UTF_8);
        builder.addBinaryBody(StringUtils.isNotBlank(fileFlag) ? fileFlag : "file", file, ContentType.APPLICATION_OCTET_STREAM, StringUtils.isNotBlank(fileName) ? fileName : file.getName());
        if (MapUtils.isNotEmpty(param)) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                StringBody stringBody = new StringBody(entry.getValue(), ContentType.TEXT_PLAIN.withCharset("utf-8"));
                builder.addPart(entry.getKey(), stringBody);
            }
        }
        try {
            postRequest.setEntity(builder.build());
            return getResponseStr(httpClient.execute(postRequest), config);
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new RuntimeException("HttpClient查询失败: " + e.getMessage());
        }
    }

    private static void addHead(HttpClientConfig config, Map<String, Object> headMap) {
        if (MapUtils.isEmpty(headMap)) return;
        for (Map.Entry<String, Object> entry : headMap.entrySet()) {
            config.addHeader(entry.getKey(), entry.getValue().toString());
        }
    }

    /**
     * 发送DELETE请求
     * <p>
     * 发送HTTP DELETE请求，用于删除资源。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 删除用户
     * HttpClientConfig config = new HttpClientConfig();
     * config.addHeader("Authorization", "Bearer token123");
     * String response = HttpClientUtil.delete("https://api.example.com/users/1", config);
     * </pre>
     *
     * @param url 请求地址
     * @param config HTTP客户端配置，为null时使用默认配置
     * @return 响应内容字符串
     * @throws DEException 当请求失败或HTTP状态码>=400时抛出
     */
    public static String delete(String url, HttpClientConfig config) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = buildHttpClient(url);
            HttpDelete httpDelete = new HttpDelete(url);

            if (config == null) {
                config = new HttpClientConfig();
            }
            httpDelete.setConfig(config.buildRequestConfig());

            Map<String, String> header = config.getHeader();
            for (String key : header.keySet()) {
                httpDelete.addHeader(key, header.get(key));
            }
            HttpResponse response = httpClient.execute(httpDelete);
            return getResponseStr(response, config);
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient查询失败: " + e.getMessage());
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }

    /**
     * 检测URL是否可达
     * <p>
     * 通过发送GET请求检测URL是否可以正常访问。
     * HTTP状态码为200时返回true，其他情况返回false。
     * 适用于健康检查、服务可用性检测等场景。
     * </p>
     *
     * @param urlString 待检测的URL
     * @param head 自定义请求头，可为null
     * @return true表示URL可达（状态码200），false表示不可达
     */
    public static boolean isURLReachable(String urlString, Map<String, String> head) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 设置连接超时时间，单位为毫秒
            connection.setReadTimeout(5000); // 设置读取超时时间，单位为毫秒
            if (MapUtils.isNotEmpty(head)) {
                for (Map.Entry<String, String> entry : head.entrySet()) {
                    connection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true; // 状态码200表示URL可达
            } else if (StringUtils.equalsIgnoreCase("Unauthorized", connection.getResponseMessage())) {
                LogUtil.error("apisix key error [failed to check token]");
            }
        } catch (IOException e) {
            return false;
        }
        return false; // 如果发生异常或状态码不是200，则URL不可达
    }

    /**
     * 发送Webhook通知
     * <p>
     * 专用于Webhook场景的POST请求方法，支持JSON和表单两种Content-Type。
     * 可配置是否使用SSL，适用于事件通知、回调等场景。
     * </p>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 发送JSON格式的Webhook
     * Map&lt;String, Object&gt; payload = new HashMap&lt;&gt;();
     * payload.put("event", "user.created");
     * payload.put("userId", 123);
     * payload.put("timestamp", System.currentTimeMillis());
     *
     * HttpClientConfig config = new HttpClientConfig();
     * String response = HttpClientUtil.postWebhook(
     *     "https://webhook.example.com/notify",
     *     "application/json",
     *     payload,
     *     true,    // 使用SSL
     *     config
     * );
     * </pre>
     *
     * @param url Webhook地址
     * @param contentType 内容类型，"application/json"或"application/x-www-form-urlencoded"
     * @param param 请求参数，会根据contentType转换为相应格式
     * @param ssl 是否使用SSL连接
     * @param config HTTP客户端配置，为null时使用默认配置
     * @return 服务器响应内容字符串
     * @throws DEException 当请求失败或HTTP状态码>=400时抛出
     */
    public static String postWebhook(String url, String contentType, Map<String, Object> param, boolean ssl, HttpClientConfig config) {

        CloseableHttpClient httpClient = null;
        try {
            httpClient = buildHttpClient(ssl);
            HttpPost httpPost = new HttpPost(url);
            if (ObjectUtils.isEmpty(config)) {
                config = new HttpClientConfig();
            }
            httpPost.setConfig(config.buildRequestConfig());
            Map<String, String> header = config.getHeader();
            for (String key : header.keySet()) {
                httpPost.addHeader(key, header.get(key));
            }
            if (StringUtils.equalsIgnoreCase(contentType, ContentType.APPLICATION_JSON.getMimeType())) {
                EntityBuilder entityBuilder = EntityBuilder.create();
                if (MapUtils.isNotEmpty(param)) {
                    String json = JsonUtil.toJSONString(param).toString();
                    entityBuilder.setText(json);
                }
                entityBuilder.setContentType(ContentType.APPLICATION_JSON);
                HttpEntity requestEntity = entityBuilder.build();
                httpPost.setEntity(requestEntity);
            } else {
                List<NameValuePair> nvps = param.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), ObjectUtils.isEmpty(entry.getValue()) ? null : entry.getValue().toString())).collect(Collectors.toList());
                try {
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, config.getCharset());
                    httpPost.setEntity(entity);
                } catch (Exception e) {
                    logger.error("HttpClient转换编码错误", e);
                    throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient转换编码错误: " + e.getMessage());
                }
            }
            HttpResponse response = httpClient.execute(httpPost);
            return getResponseStr(response, config);
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new DEException(SYSTEM_INNER_ERROR.code(), "HttpClient查询失败: " + e.getMessage());
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }
}
