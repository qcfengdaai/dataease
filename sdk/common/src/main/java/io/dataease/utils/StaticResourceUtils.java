package io.dataease.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import static io.dataease.constant.StaticResourceConstants.*;

/**
 * 静态资源工具类
 * <p>
 * 提供静态资源处理相关的工具方法，包括字符串前缀/后缀处理、图片文件Base64编码等功能。
 * 主要用于处理静态资源路径规范化和图片文件的读取转换。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>前缀/后缀处理 - 确保字符串包含指定的前缀或后缀</li>
 *   <li>路径规范化 - 统一路径分隔符格式</li>
 *   <li>图片Base64编码 - 将本地图片文件转换为Base64字符串</li>
 *   <li>资源路径管理 - 处理静态资源的文件路径</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.constant.StaticResourceConstants} - 静态资源常量配置（路径规范化）</li>
 *   <li>静态资源目录配置 - MAP_DIR、APPEARANCE_DIR、REPORT_DIR等路径配置</li>
 *   <li>文件上传下载 - 静态资源的存储路径处理</li>
 *   <li>外观定制 - 系统外观图片资源的处理</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：确保路径有正确的分隔符后缀
 * String path = "/opt/dataease2.0/data";
 * String normalizedPath = StaticResourceUtils.ensureSuffix(path, "/");
 * System.out.println(normalizedPath);  // 输出: /opt/dataease2.0/data/
 *
 * // 示例2：同时确保前缀和后缀
 * String uri = "api/user";
 * String fullUri = StaticResourceUtils.ensureBoth(uri, "/");
 * System.out.println(fullUri);  // 输出: /api/user/
 *
 * // 示例3：不同的前后缀
 * String fileName = "report.pdf";
 * String fullPath = StaticResourceUtils.ensureBoth(fileName, "/tmp/", ".pdf");
 * System.out.println(fullPath);  // 输出: /tmp/report.pdf
 *
 * // 示例4：确保前缀（常用于URL路径）
 * String apiPath = "user/login";
 * String absolutePath = StaticResourceUtils.ensurePrefix(apiPath, "/");
 * System.out.println(absolutePath);  // 输出: /user/login
 *
 * // 示例5：图片文件转Base64（用于外观定制）
 * String imgPath = "appearance/logo.png";
 * String base64Data = StaticResourceUtils.getImgFileToBase64(imgPath);
 * if (base64Data != null) {
 *     // 可以直接在HTML中使用
 *     String imgTag = "&lt;img src='data:image/png;base64," + base64Data + "' /&gt;";
 * }
 *
 * // 示例6：实际应用场景 - 静态资源目录配置
 * // 参考 StaticResourceConstants.java
 * String userHome = "/opt/dataease2.0/data";
 * String separator = File.separator;
 * String mapDir = StaticResourceUtils.ensureSuffix(userHome, separator) + "map";
 * // 确保路径格式统一：/opt/dataease2.0/data/map
 *
 * // 示例7：处理用户上传的文件路径
 * String uploadPath = "static-resource/uploads";
 * String normalizedUpload = StaticResourceUtils.ensureSuffix(uploadPath, "/");
 * String fullFilePath = normalizedUpload + "user-avatar.jpg";
 * // 结果: static-resource/uploads/user-avatar.jpg
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>字符串参数不能为空，否则会抛出IllegalArgumentException</li>
 *   <li>ensurePrefix和ensureSuffix会自动去重，避免重复添加</li>
 *   <li>getImgFileToBase64读取的是本地文件，需确保文件存在且有读取权限</li>
 *   <li>Base64编码会增加数据大小约33%，大文件需注意内存占用</li>
 *   <li>图片读取失败时返回null，需要调用方进行null检查</li>
 *   <li>文件基础路径为：USER_HOME/static-resource/</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>使用ensureSuffix处理目录路径，确保路径分隔符统一</li>
 *   <li>使用ensurePrefix处理URL路径，确保以斜杠开头</li>
 *   <li>大图片文件不建议使用Base64编码，应使用文件流或URL引用</li>
 *   <li>Base64编码适用于小图标、Logo等小尺寸图片</li>
 *   <li>跨平台应用中，使用File.separator而不是硬编码"/"或"\"</li>
 *   <li>处理文件路径前，应先验证文件是否存在</li>
 * </ul>
 *
 * <p><b>性能考虑：</b></p>
 * <ul>
 *   <li>ensurePrefix/ensureSuffix操作是轻量级的字符串操作</li>
 *   <li>Base64编码对大文件会消耗较多CPU和内存</li>
 *   <li>频繁读取同一文件时，建议缓存Base64结果</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.constant.StaticResourceConstants
 */
public class StaticResourceUtils {

    /**
     * 文件基础路径
     * 所有静态资源文件的根目录，格式：USER_HOME/static-resource/
     */
    private final static String FILE_BASE_PATH = USER_HOME + FILE_SEPARATOR + UPLOAD_URL_PREFIX;

    /**
     * 确保字符串同时包含指定的前缀和后缀（前后缀相同）
     * <p>
     * 这是{@link #ensureBoth(String, String, String)}的便捷方法，
     * 当前缀和后缀相同时使用此方法。
     * </p>
     *
     * <p><b>示例：</b></p>
     * <pre>
     * String result = ensureBoth("api/user", "/");
     * // 结果: /api/user/
     * </pre>
     *
     * @param string 待处理的字符串，不能为null或空
     * @param bothfix 前缀和后缀（相同的字符串），不能为null或空
     * @return 包含指定前缀和后缀的字符串
     * @throws IllegalArgumentException 当string或bothfix为空时抛出
     */
    public static String ensureBoth(@NonNull String string, @NonNull String bothfix) {
        return ensureBoth(string, bothfix, bothfix);
    }

    /**
     * 确保字符串同时包含指定的前缀和后缀
     * <p>
     * 先确保字符串包含前缀，再确保包含后缀。
     * 如果字符串已经包含指定的前缀或后缀，不会重复添加。
     * </p>
     *
     * <p><b>应用场景：</b></p>
     * <ul>
     *   <li>文件路径格式化：确保路径以特定目录开始和结束</li>
     *   <li>URL路径规范化：确保路径的开始和结束符合规范</li>
     *   <li>字符串包装：在字符串两端添加特定字符</li>
     * </ul>
     *
     * <p><b>示例：</b></p>
     * <pre>
     * // 示例1：相同的前后缀
     * String uri = ensureBoth("api/user", "/", "/");
     * // 结果: /api/user/
     *
     * // 示例2：不同的前后缀
     * String path = ensureBoth("config.properties", "classpath:", ".bak");
     * // 结果: classpath:config.properties.bak
     * </pre>
     *
     * @param string 待处理的字符串，不能为null或空
     * @param prefix 前缀字符串，不能为null或空
     * @param suffix 后缀字符串，不能为null或空
     * @return 同时包含指定前缀和后缀的字符串
     * @throws IllegalArgumentException 当任何参数为空时抛出
     */
    public static String ensureBoth(@NonNull String string, @NonNull String prefix,
                                    @NonNull String suffix) {
        return ensureSuffix(ensurePrefix(string, prefix), suffix);
    }

    /**
     * 确保字符串包含指定的前缀
     * <p>
     * 如果字符串已经以指定的前缀开头，则直接返回原字符串。
     * 否则，在字符串前面添加指定的前缀。
     * 使用Apache Commons的StringUtils.removeStart确保不会重复添加前缀。
     * </p>
     *
     * <p><b>应用场景：</b></p>
     * <ul>
     *   <li>URL路径规范化：确保路径以"/"开头</li>
     *   <li>文件协议处理：确保路径以"file:"开头</li>
     *   <li>命名空间处理：确保标识符包含特定前缀</li>
     * </ul>
     *
     * <p><b>示例：</b></p>
     * <pre>
     * // 示例1：添加前缀
     * String path1 = ensurePrefix("api/user", "/");
     * // 结果: /api/user
     *
     * // 示例2：已有前缀，不重复添加
     * String path2 = ensurePrefix("/api/user", "/");
     * // 结果: /api/user（不会变成//api/user）
     *
     * // 示例3：文件协议
     * String filePath = ensurePrefix("opt/data/file.txt", "file:");
     * // 结果: file:opt/data/file.txt
     * </pre>
     *
     * @param string 待处理的字符串，不能为null或空白
     * @param prefix 要确保存在的前缀，不能为null或空白
     * @return 包含指定前缀的字符串
     * @throws IllegalArgumentException 当string或prefix为空白时抛出
     */
    public static String ensurePrefix(@NonNull String string, @NonNull String prefix) {
        Assert.hasText(string, "String must not be blank");
        Assert.hasText(prefix, "Prefix must not be blank");

        return prefix + StringUtils.removeStart(string, prefix);
    }


    /**
     * 确保字符串包含指定的后缀
     * <p>
     * 如果字符串已经以指定的后缀结尾，则直接返回原字符串。
     * 否则，在字符串末尾添加指定的后缀。
     * 使用Apache Commons的StringUtils.removeEnd确保不会重复添加后缀。
     * </p>
     *
     * <p><b>应用场景：</b></p>
     * <ul>
     *   <li>目录路径规范化：确保路径以分隔符结尾</li>
     *   <li>文件扩展名处理：确保文件名包含正确的扩展名</li>
     *   <li>URL处理：确保URL以特定字符结尾</li>
     * </ul>
     *
     * <p><b>实际使用示例（来自StaticResourceConstants）：</b></p>
     * <pre>
     * // 确保用户主目录路径以分隔符结尾
     * String mapDir = ensureSuffix(USER_HOME, FILE_SEPARATOR) + "map";
     * String workDir = ensureSuffix(USER_HOME, FILE_SEPARATOR) + "static-resource" + FILE_SEPARATOR;
     * </pre>
     *
     * <p><b>示例：</b></p>
     * <pre>
     * // 示例1：添加后缀
     * String path1 = ensureSuffix("/opt/data", "/");
     * // 结果: /opt/data/
     *
     * // 示例2：已有后缀，不重复添加
     * String path2 = ensureSuffix("/opt/data/", "/");
     * // 结果: /opt/data/（不会变成/opt/data//）
     *
     * // 示例3：文件扩展名
     * String fileName = ensureSuffix("report", ".pdf");
     * // 结果: report.pdf
     * </pre>
     *
     * @param string 待处理的字符串，不能为null或空白
     * @param suffix 要确保存在的后缀，不能为null或空白
     * @return 包含指定后缀的字符串
     * @throws IllegalArgumentException 当string或suffix为空白时抛出
     */
    public static String ensureSuffix(@NonNull String string, @NonNull String suffix) {
        Assert.hasText(string, "String must not be blank");
        Assert.hasText(suffix, "Suffix must not be blank");

        return StringUtils.removeEnd(string, suffix) + suffix;
    }

    /**
     * 将本地图片文件转换为Base64编码字符串
     * <p>
     * 读取指定路径的图片文件，将其内容转换为Base64编码的字符串。
     * 主要用于将图片嵌入到HTML、CSS或JSON中，无需额外的HTTP请求。
     * </p>
     *
     * <p><b>文件路径规则：</b></p>
     * <ul>
     *   <li>相对路径：相对于FILE_BASE_PATH（USER_HOME/static-resource/）</li>
     *   <li>完整路径：FILE_BASE_PATH + FILE_SEPARATOR + imgFile</li>
     *   <li>示例：imgFile="appearance/logo.png" → "/opt/dataease2.0/data/static-resource/appearance/logo.png"</li>
     * </ul>
     *
     * <p><b>应用场景：</b></p>
     * <ul>
     *   <li>系统外观定制：Logo、图标等小图片的Base64编码</li>
     *   <li>邮件模板：将图片直接嵌入HTML邮件</li>
     *   <li>离线页面：将资源打包到单个HTML文件中</li>
     *   <li>API响应：直接返回图片的Base64数据</li>
     * </ul>
     *
     * <p><b>示例：</b></p>
     * <pre>
     * // 示例1：读取Logo图片
     * String logoBase64 = StaticResourceUtils.getImgFileToBase64("appearance/logo.png");
     * if (logoBase64 != null) {
     *     // 在HTML中使用
     *     String html = "&lt;img src='data:image/png;base64," + logoBase64 + "' /&gt;";
     * }
     *
     * // 示例2：读取用户头像
     * String avatarBase64 = StaticResourceUtils.getImgFileToBase64("user/avatar/123.jpg");
     * if (avatarBase64 != null) {
     *     // 返回给前端
     *     return JsonUtil.toJson(Map.of("avatar", "data:image/jpeg;base64," + avatarBase64));
     * }
     *
     * // 示例3：处理读取失败的情况
     * String imgBase64 = StaticResourceUtils.getImgFileToBase64("nonexistent.png");
     * if (imgBase64 == null) {
     *     LogUtil.warn("图片文件不存在或读取失败");
     *     // 使用默认图片或返回错误信息
     * }
     * </pre>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>仅适用于小尺寸图片（建议小于1MB），大图片会占用大量内存</li>
     *   <li>Base64编码后的数据大小约为原文件的133%</li>
     *   <li>文件不存在或读取失败时返回null，调用方需要进行null检查</li>
     *   <li>方法会捕获所有异常并记录日志，不会向上抛出异常</li>
     *   <li>文件流会在finally块中正确关闭，避免资源泄漏</li>
     * </ul>
     *
     * <p><b>性能考虑：</b></p>
     * <ul>
     *   <li>文件读取是IO密集型操作，避免在高频接口中使用</li>
     *   <li>建议对Base64结果进行缓存，避免重复读取和编码</li>
     *   <li>对于大文件，建议使用流式处理而非一次性加载到内存</li>
     * </ul>
     *
     * @param imgFile 图片文件的相对路径，相对于FILE_BASE_PATH
     * @return 图片的Base64编码字符串，读取失败时返回null
     */
    public static String getImgFileToBase64(String imgFile) {
        // 将图片文件转换为字节数组并进行Base64编码
        InputStream inputStream = null;
        byte[] buffer = null;

        // 读取图片文件的字节数组
        try {
            // 构建完整的文件路径
            inputStream = new FileInputStream(FILE_BASE_PATH + FILE_SEPARATOR + imgFile);

            // 等待输入流准备就绪，获取可读取的字节数
            int count = 0;
            while (count == 0) {
                count = inputStream.available();
            }

            // 创建字节数组并读取文件内容
            buffer = new byte[count];
            inputStream.read(buffer);

        } catch (IOException e) {
            // IO异常：文件不存在、无权限等
            LogUtil.error(e);
        } catch (Exception e) {
            // 其他异常
            LogUtil.error(e);
        } finally {
            // 确保输入流被正确关闭，避免资源泄漏
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 将字节数组编码为Base64字符串
        if (buffer != null) {
            return Base64.getEncoder().encodeToString(buffer);
        } else {
            return null;
        }
    }

}
