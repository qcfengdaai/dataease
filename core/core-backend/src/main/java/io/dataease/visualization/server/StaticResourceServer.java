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
        // 检查文件是否为空
        Assert.notNull(file, "Multipart file must not be null");
        try {
            // 验证是否为图片格式
            if (!isImage(file)) {
                DEException.throwException("Multipart file must be image");
            }
            // 获取原始文件名
            String originName = file.getOriginalFilename();
            // 生成新文件名（fileId + 原扩展名）
            String newFileName = fileId + originName.substring(originName.lastIndexOf("."), originName.length());
            Path basePath = Paths.get(staticDir.toString());
            // 如果目录不存在则创建
            FileUtils.createIfAbsent(basePath);
            Path uploadPath = basePath.resolve(newFileName);
            Files.createFile(uploadPath);
            // 保存文件
            file.transferTo(uploadPath);
        } catch (IOException e) {
            LogUtil.error("文件上传失败", e);
            DEException.throwException("文件上传失败");
        } catch (Exception e) {
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
        if (file == null || file.isEmpty()) {
            return false;
        }
        String mimeType = file.getContentType();
        if (StringUtils.isEmpty(mimeType)) {
            return false;
        }
        if (!hasValidImageExtension(file.getOriginalFilename())) {
            return false;
        }
        // 判断是否为图片或SVG
        return (isImageOther(file)) || isValidSVG(file);
    }

    private boolean hasValidImageExtension(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return false;
        }
        // 转换为小写进行比较
        String lowerFilename = filename.toLowerCase();
        // 允许的图片后缀名列表
        Set<String> allowedExtensions = Set.of(
                ".gif", ".svg", ".png", ".jpeg", ".jpg"
        );

        for (String ext : allowedExtensions) {
            if (lowerFilename.endsWith(ext)) {
                return true;
            }
        }

        return false;
    }

    private boolean isImageOther(MultipartFile file) {
        BufferedImage image = null;
        try (InputStream input = file.getInputStream()) {
            image = ImageIO.read(input);
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return false;
        }
        if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
            return false;
        }
        return true;
    }

    public void saveFilesToServe(String staticResource) {
        if (StringUtils.isNotEmpty(staticResource)) {
            Map<String, String> resource = JsonUtil.parse(staticResource, Map.class);
            for (Map.Entry<String, String> entry : resource.entrySet()) {
                String path = entry.getKey();
                String fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
                saveSingleFileToServe(fileName, entry.getValue());
            }
        }
    }

    public void saveSingleFileToServe(String fileName, String content) {
        Path basePath = Paths.get(staticDir.toString());
        Path uploadPath = basePath.resolve(fileName);
        try {
            if (Files.exists(uploadPath)) {
                LogUtil.info("file exists");
            } else {
                if (StringUtils.isNotEmpty(content)) {
                    Files.createFile(uploadPath);
                    FileCopyUtils.copy(Base64.getDecoder().decode(content), Files.newOutputStream(uploadPath));
                }
            }
        } catch (Exception e) {
            LogUtil.error("template static resource save error" + e.getMessage());
        }
    }

    @Override
    public Map<String, String> findResourceAsBase64(StaticResourceRequest resourceRequest) {
        Map<String, String> result = new HashMap<>();
        if (CollectionUtils.isNotEmpty(resourceRequest.getResourcePathList())) {
            for (String path : resourceRequest.getResourcePathList()) {
                String value = StaticResourceUtils.getImgFileToBase64(path.substring(path.lastIndexOf("/") + 1, path.length()));
                result.put(path, value);
            }
        }
        return result;
    }

    private static boolean isValidSVG(MultipartFile file){
        if (file == null || file.isEmpty()) {
            return false;
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try (InputStream inputStream = file.getInputStream()) {
            // 禁用外部实体解析以防止XXE攻击
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(inputStream);

            // 检查根元素是否是<svg>
            if ("svg".equals(doc.getDocumentElement().getNodeName())) {
                return true;
            } else {
                return false;
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // 如果出现任何解析错误，说明该文件不是合法的SVG
            if(e.getMessage() != null && e.getMessage().indexOf("DOCTYPE")>-1){
                DEException.throwException("svg 内容禁止使用 DOCTYPE");
            }else {
                DEException.throwException(e);
            }
        }
        return false;
    }
    public static FileType getFileType(InputStream is) throws IOException {
        byte[] src = new byte[28];
        is.read(src, 0, 28);
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        FileType[] fileTypes = FileType.values();
        for (FileType fileType : fileTypes) {
            if (stringBuilder.toString().startsWith(fileType.getValue())) {
                return fileType;
            }
        }
        return null;
    }

    private static Boolean isImageCheckType(MultipartFile file) {
        try {
            return getFileType(file.getInputStream()) != null;
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            return false;
        }
    }

    public static String getImageType(InputStream fileInputStream) {
        byte[] b = new byte[10];
        int l = -1;
        try {
            l = fileInputStream.read(b);
            fileInputStream.close();
        } catch (Exception e) {
            return null;
        }
        if (l == 10) {
            byte b0 = b[0];
            byte b1 = b[1];
            byte b2 = b[2];
            byte b3 = b[3];
            byte b6 = b[6];
            byte b7 = b[7];
            byte b8 = b[8];
            byte b9 = b[9];
            if (b0 == (byte) 'G' && b1 == (byte) 'I' && b2 == (byte) 'F') {
                return "gif";
            } else if (b1 == (byte) 'P' && b2 == (byte) 'N' && b3 == (byte) 'G') {
                return "png";
            } else if (b6 == (byte) 'J' && b7 == (byte) 'F' && b8 == (byte) 'I' && b9 == (byte) 'F') {
                return "jpg";
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
