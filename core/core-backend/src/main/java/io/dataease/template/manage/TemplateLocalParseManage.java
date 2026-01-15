package io.dataease.template.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dataease.api.visualization.request.DataVisualizationBaseRequest;
import io.dataease.license.utils.LogUtil;
import io.dataease.template.dao.auto.entity.DeTemplateVersion;
import io.dataease.template.dao.auto.mapper.DeTemplateVersionMapper;
import io.dataease.utils.JsonUtil;
import io.dataease.visualization.server.StaticResourceServer;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 模板本地解析管理类
 * 负责初始化和解析本地模板文件
 *
 * @author WangJiaHao
 * @date 2024/5/7
 */
@Service
public class TemplateLocalParseManage {

    @Resource
    private StaticResourceServer staticResourceServer;

    @Resource
    private DeTemplateVersionMapper deTemplateVersionMapper;

    @Resource(type = ResourceLoader.class)
    private ResourceLoader resourceLoader;

    /**
     * 初始化模板
     * 扫描并加载classpath下template目录中的所有模板文件
     *
     * @throws Exception 初始化异常
     */
    public void doInit() throws Exception {
        // 获取template目录下的所有模板文件
        org.springframework.core.io.Resource[] templateFiles = getAllFilesInResourceDirectory("template");
        if (templateFiles != null && templateFiles.length > 0) {
            for (int i = 0; i < templateFiles.length; i++) {
                org.springframework.core.io.Resource templateFile = templateFiles[i];
                String templateName = templateFile.getFilename();
                // 检查模板是否已存在
                QueryWrapper<DeTemplateVersion> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("script", templateName);
                if (!deTemplateVersionMapper.exists(queryWrapper)) {
                    DeTemplateVersion version = new DeTemplateVersion();
                    version.setScript(templateName);
                    version.setInstalledOn(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
                    // 解析模板文件
                    try {
                        String content = new String(templateFile.getInputStream().readAllBytes());
                        ;
                        DataVisualizationBaseRequest template = JsonUtil.parseObject(content, DataVisualizationBaseRequest.class);
                        // 解析核心内容
                        parseCore(template);
                        version.setSuccess(true);
                        deTemplateVersionMapper.insert(version);
                    } catch (Exception e) {
                        // 解析失败,记录失败状态
                        LogUtil.error("De Template Version Error : " + templateName);
                        LogUtil.error("De Template Version Error : " + templateName);
                        version.setSuccess(false);
                        deTemplateVersionMapper.insert(version);
                        break;
                    }
                }
            }

        }
    }

    /**
     * 解析模板核心内容
     * 提取并保存模板中的静态资源
     *
     * @param template 数据可视化基础请求
     */
    public void parseCore(DataVisualizationBaseRequest template) {
        // 保存静态资源到服务器
        staticResourceServer.saveFilesToServe(template.getStaticResource());
    }


    /**
     * 获取资源目录下的所有文件
     *
     * @param directoryName 目录名称
     * @return 资源文件数组
     * @throws Exception 获取文件异常
     */
    public org.springframework.core.io.Resource[] getAllFilesInResourceDirectory(String directoryName) throws Exception {
        // 创建资源解析器
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);

        // 获取classpath下template目录中的所有文件
        org.springframework.core.io.Resource[] resources = resolver.getResources("classpath:template/*");

        return resources;
    }

    /**
     * 读取文件内容
     *
     * @param file 文件对象
     * @return 文件内容字符串
     * @throws IOException 读取异常
     */
    public static String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (InputStream inputStream = Files.newInputStream(file.toPath());
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

}
