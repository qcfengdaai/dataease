package io.dataease.visualization.server;

import io.dataease.api.visualization.StaticResourceApi;
import io.dataease.api.visualization.request.StaticResourceRequest;
import io.dataease.exception.DEException;
import io.dataease.utils.FileUtils;
import io.dataease.utils.JsonUtil;
import io.dataease.utils.LogUtil;
import io.dataease.utils.StaticResourceUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 静态资源服务
 * <p>
 * 处理静态资源（主要是图片）的上传、保存和查询
 * <p>
 * 主要功能：
 * <ul>
 * <li>上传图片文件并验证格式</li>
 * <li>保存Base64编码的静态资源</li>
 * <li>查询静态资源并转换为Base64</li>
 * <li>验证SVG内容安全性</li>
 * </ul>
 *
 * @author DataEase
 * @since 2024-06-21
 */
@RestController
@RequestMapping("/staticResource")
public class StaticResourceServer implements StaticResourceApi {

    /**
     * 静态资源存储目录
     * 默认值: /opt/dataease2.0/data/static-resource/
     */
    @Value("${dataease.path.static-resource:/opt/dataease2.0/data/static-resource/}")
    private String staticDir;

    /**
     * 上传静态资源文件
     * <p>
     * 支持的格式：JPEG、PNG、GIF、SVG
     * <p>
     * 上传流程：
     * <ol>
     * <li>验证文件是否为图片</li>
     * <li>生成新文件名（fileId + 原扩展名）</li>
     * <li>保存到静态资源目录</li>
     * </ol>
     *
     * @param fileId 文件ID
     * @param file   上传的文件
     */
    @Override
    public void upload(String fileId, MultipartFile file) {
        // 1. 校验文件是否为空
        Assert.notNull(file, "Multipart file must not be null");
        try {
            // 2. 验证是否为图片格式（支持JPEG、PNG、GIF、SVG）
            if (!isImage(file)) {
                DEException.throwException("Multipart file must be image");
            }
            // 3. 获取原始文件名并提取扩展名
            String originName = file.getOriginalFilename();
            // 4. 生成新文件名（使用fileId + 原始扩展名，避免文件名冲突）
            String newFileName = fileId + originName.substring(originName.lastIndexOf("."), originName.length());
            // 5. 获取静态资源存储目录路径
            Path basePath = Paths.get(staticDir.toString());
            // 6. 如果目录不存在，则创建
            FileUtils.createIfAbsent(basePath);
            // 7. 构建完整文件路径
            Path uploadPath = basePath.resolve(newFileName);
            Files.createFile(uploadPath);
            // 8. 保存文件到磁盘
            file.transferTo(uploadPath);
        } catch (IOException e) {
            // 9. IO异常处理：记录日志并抛出友好的错误信息
            LogUtil.error("文件上传失败", e);
            DEException.throwException("文件上传失败");
        } catch (Exception e) {
            // 10. 其他异常处理：直接抛出原始异常
            DEException.throwException(e);
        }
    }

    /**
     * 验证文件是否为图片
     * <p>
     * 验证步骤：
     * <ol>
     * <li>检查文件是否为空</li>
     * <li>检查MIME类型</li>
     * <li>检查文件扩展名</li>
     * <li>验证图片内容或SVG格式</li>
     * </ol>
     *
     * @param file 待验证的文件
     * @return 如果是图片返回true，否则返回false
     */
    private boolean isImage(MultipartFile file) {
        // 1. 检查文件是否为null或空
        if (file == null || file.isEmpty()) {
            return false;
        }
        // 2. 获取MIME类型并检查是否为空
        String mimeType = file.getContentType();
        if (StringUtils.isEmpty(mimeType)) {
            return false;
        }
        // 3. 检查文件扩展名是否合法
        if (!hasValidImageExtension(file.getOriginalFilename())) {
            return false;
        }
        // 4. 判断是否为普通图片(JPEG/PNG/GIF)或SVG
        return (isImageOther(file)) || isValidSVG(file);
    }

    private boolean hasValidImageExtension(String filename) {
        // 1. 检查文件名是否为空
        if (StringUtils.isEmpty(filename)) {
            return false;
        }
        // 2. 转换为小写进行比较，避免大小写问题
        String lowerFilename = filename.toLowerCase();
        // 3. 定义允许的图片后缀名列表
        Set<String> allowedExtensions = Set.of(
                ".gif", ".svg", ".png", ".jpeg", ".jpg"
        );

        // 4. 遍历允许的扩展名列表，检查文件名是否以其中任意一个结尾
        for (String ext : allowedExtensions) {
            if (lowerFilename.endsWith(ext)) {
                return true;
            }
        }

        // 5. 如果没有匹配的扩展名，返回false
        return false;
    }

    private boolean isImageOther(MultipartFile file) {
        BufferedImage image = null;
        try (InputStream input = file.getInputStream()) {
            // 1. 尝试读取图片（使用ImageIO可以验证JPEG、PNG、GIF格式）
            image = ImageIO.read(input);
        } catch (IOException e) {
            // 2. IO异常：记录日志并返回false（不是有效图片）
            LogUtil.error(e.getMessage(), e);
            return false;
        }
        // 3. 检查图片是否成功读取，并且宽高都大于0
        if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
            return false;
        }
        // 4. 所有检查通过，返回true
        return true;
    }

    public void saveFilesToServe(String staticResource) {
        // 1. 检查静态资源JSON字符串是否为空
        if (StringUtils.isNotEmpty(staticResource)) {
            // 2. 解析JSON字符串为Map（key为路径，value为Base64编码的文件内容）
            Map<String, String> resource = JsonUtil.parse(staticResource, Map.class);
            // 3. 遍历所有资源
            for (Map.Entry<String, String> entry : resource.entrySet()) {
                // 3.1 提取文件路径
                String path = entry.getKey();
                // 3.2 从路径中提取文件名（去除路径前缀）
                String fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
                // 3.3 保存单个文件到服务器
                saveSingleFileToServe(fileName, entry.getValue());
            }
        }
    }

    public void saveSingleFileToServe(String fileName, String content) {
        // 1. 获取静态资源基础路径
        Path basePath = Paths.get(staticDir.toString());
        // 2. 解析完整文件路径
        Path uploadPath = basePath.resolve(fileName);
        try {
            // 3. 检查文件是否已存在
            if (Files.exists(uploadPath)) {
                // 3.1 文件已存在，记录日志并跳过
                LogUtil.info("file exists");
            } else {
                // 3.2 文件不存在，创建新文件
                if (StringUtils.isNotEmpty(content)) {
                    // 3.2.1 创建空文件
                    Files.createFile(uploadPath);
                    // 3.2.2 将Base64编码的内容解码后写入文件
                    FileCopyUtils.copy(Base64.getDecoder().decode(content), Files.newOutputStream(uploadPath));
                }
            }
        } catch (Exception e) {
            // 4. 异常处理：记录错误日志
            LogUtil.error("template static resource save error" + e.getMessage());
        }
    }

    @Override
    public Map<String, String> findResourceAsBase64(StaticResourceRequest resourceRequest) {
        // 1. 创建结果Map，用于存储路径到Base64编码的映射
        Map<String, String> result = new HashMap<>();
        // 2. 检查请求的资源路径列表是否为空
        if (CollectionUtils.isNotEmpty(resourceRequest.getResourcePathList())) {
            // 3. 遍历所有资源路径
            for (String path : resourceRequest.getResourcePathList()) {
                // 3.1 从路径中提取文件名
                String fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
                // 3.2 读取文件并转换为Base64编码
                String value = StaticResourceUtils.getImgFileToBase64(fileName);
                // 3.3 将结果存入Map（key为完整路径，value为Base64编码）
                result.put(path, value);
            }
        }
        // 4. 返回结果Map
        return result;
    }

    private static boolean isValidSVG(MultipartFile file){
        // 1. 检查文件是否为null或空
        if (file == null || file.isEmpty()) {
            return false;
        }

        // 2. 创建文档构建器工厂（用于解析XML）
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try (InputStream inputStream = file.getInputStream()) {
            // 3. 配置安全设置，防止XXE攻击
            // 3.1 禁用DOCTYPE声明
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            // 3.2 禁用外部通用实体
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            // 3.3 禁用外部参数实体
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // 3.4 禁用加载外部DTD
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            // 3.5 设置命名空间感知
            dbf.setNamespaceAware(true);
            // 4. 创建文档构建器并解析XML
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(inputStream);

            // 5. 检查根元素是否是<svg>
            if ("svg".equals(doc.getDocumentElement().getNodeName())) {
                return true;
            } else {
                return false;
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // 6. 解析异常处理：检查是否包含DOCTYPE（安全风险）
            if(e.getMessage() != null && e.getMessage().indexOf("DOCTYPE")>-1){
                // 6.1 发现DOCTYPE，抛出安全异常
                DEException.throwException("svg 内容禁止使用 DOCTYPE");
            }else {
                // 6.2 其他解析错误，抛出原始异常
                DEException.throwException(e);
            }
        }
        // 7. 默认返回false
        return false;
    }
    public static FileType getFileType(InputStream is) throws IOException {
        // 1. 创建字节数组，读取文件头（前28个字节）
        byte[] src = new byte[28];
        is.read(src, 0, 28);
        // 2. 创建StringBuilder用于构建十六进制字符串
        StringBuilder stringBuilder = new StringBuilder("");
        // 3. 检查读取的字节数组是否有效
        if (src == null || src.length <= 0) {
            return null;
        }
        // 4. 遍历字节数组，将每个字节转换为十六进制字符串
        for (int i = 0; i < src.length; i++) {
            // 4.1 将字节转换为无符号整数（0-255）
            int v = src[i] & 0xFF;
            // 4.2 转换为十六进制字符串并转为大写
            String hv = Integer.toHexString(v).toUpperCase();
            // 4.3 如果十六进制字符串长度小于2，前面补0
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            // 4.4 追加到StringBuilder
            stringBuilder.append(hv);
        }
        // 5. 获取所有文件类型枚举值
        FileType[] fileTypes = FileType.values();
        // 6. 遍历文件类型，匹配魔数（文件头）
        for (FileType fileType : fileTypes) {
            // 6.1 检查文件头是否以该类型的魔数开头
            if (stringBuilder.toString().startsWith(fileType.getValue())) {
                return fileType;
            }
        }
        // 7. 没有匹配的文件类型，返回null
        return null;
    }

    private static Boolean isImageCheckType(MultipartFile file) {
        try {
            // 1. 通过文件头魔数判断文件类型
            // 2. 如果能识别出文件类型，返回true；否则返回false
            return getFileType(file.getInputStream()) != null;
        } catch (Exception e) {
            // 3. 异常处理：记录错误日志并返回false
            LogUtil.error(e.getMessage());
            return false;
        }
    }

    public static String getImageType(InputStream fileInputStream) {
        // 1. 创建字节数组，读取文件头（前10个字节）
        byte[] b = new byte[10];
        int l = -1;
        try {
            // 2. 读取文件头数据
            l = fileInputStream.read(b);
            // 3. 关闭输入流
            fileInputStream.close();
        } catch (Exception e) {
            // 4. 异常处理：返回null
            return null;
        }
        // 5. 检查是否成功读取10个字节
        if (l == 10) {
            // 6. 提取关键位置的字节
            byte b0 = b[0];
            byte b1 = b[1];
            byte b2 = b[2];
            byte b3 = b[3];
            byte b6 = b[6];
            byte b7 = b[7];
            byte b8 = b[8];
            byte b9 = b[9];
            // 7. 根据魔数判断文件类型
            // 7.1 GIF格式：文件头为"GIF"
            if (b0 == (byte) 'G' && b1 == (byte) 'I' && b2 == (byte) 'F') {
                return "gif";
            // 7.2 PNG格式：文件头包含"PNG"
            } else if (b1 == (byte) 'P' && b2 == (byte) 'N' && b3 == (byte) 'G') {
                return "png";
            // 7.3 JPEG格式：文件头包含"JFIF"
            } else if (b6 == (byte) 'J' && b7 == (byte) 'F' && b8 == (byte) 'I' && b9 == (byte) 'F') {
                return "jpg";
            // 7.4 未知格式
            } else {
                return null;
            }
        } else {
            // 8. 读取的字节数不足10，返回null
            return null;
        }
    }
}
