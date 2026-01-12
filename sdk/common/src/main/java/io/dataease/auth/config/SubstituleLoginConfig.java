package io.dataease.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dataease.utils.CommonBeanFactory;
import io.dataease.utils.LogUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 替代登录配置类
 * 用于在没有完整登录服务的环境下提供基础的密码配置功能
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>替代登录数据管理 - 在缺少登录服务时提供基础认证支持</li>
 *   <li>密码配置存储 - 将登录密码配置存储到JSON文件</li>
 *   <li>默认密码设置 - 自动创建默认密码配置</li>
 *   <li>动态密码修改 - 支持运行时修改登录密码</li>
 * </ul>
 *
 * <p>适用场景：</p>
 * <ul>
 *   <li>社区版部署环境</li>
 *   <li>简化部署配置</li>
 *   <li>开发和测试环境</li>
 *   <li>单机版本部署</li>
 * </ul>
 *
 * <p>配置条件：</p>
 * <ul>
 *   <li>只在没有"loginServer" Bean的情况下生效</li>
 *   <li>确保与完整登录服务不冲突</li>
 * </ul>
 */
@ConditionalOnMissingBean(name = "loginServer")
@Configuration
public class SubstituleLoginConfig {

    /**
     * 替代登录数据文件路径
     * 默认为classpath:substitule.json，可通过dataease.path.substitule配置修改
     */
    @Value("${dataease.path.substitule:classpath:substitule.json}")
    private String jsonFilePath;

    /**
     * 缓存的登录密码
     * 存储当前配置的登录密码，避免重复读取文件
     */
    private static String pwd;

    /**
     * 密码是否已准备就绪
     * 标识密码配置是否已从文件中加载完成
     */
    private static boolean ready = false;


    /**
     * 创建替代登录数据Bean
     *
     * <p>功能说明：</p>
     * <ul>
     *   <li>读取JSON配置文件获取登录数据</li>
     *   <li>如果配置文件不存在，使用默认密码创建</li>
     *   <li>支持通过环境变量dataease.default-pwd自定义默认密码</li>
     * </ul>
     *
     * @param resourceLoader Spring资源加载器（未使用但保留参数）
     * @return 包含登录配置数据的Map对象
     * @throws IOException 文件读取或创建失败时抛出
     */
    @ConditionalOnMissingBean(name = "loginServer")
    @Bean
    public Map<String, Object> substituleLoginData(ResourceLoader resourceLoader) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(jsonFilePath);

        // 如果配置文件不存在，使用默认密码创建新文件
        if (!jsonFile.exists()) {
            pwd = CommonBeanFactory.getBean(Environment.class)
                .getProperty("dataease.default-pwd", "DataEase@123456");
            modifyPwd(pwd);
        }

        // 读取并返回JSON配置数据
        return objectMapper.readValue(jsonFile, Map.class);
    }

    /**
     * 获取当前配置的登录密码
     *
     * <p>获取逻辑：</p>
     * <ol>
     *   <li>首次调用时从Spring容器获取登录数据Bean</li>
     *   <li>从登录数据中提取密码并缓存</li>
     *   <li>后续调用直接返回缓存的密码</li>
     * </ol>
     *
     * @return 当前配置的登录密码
     */
    public static String getPwd() {
        if (!ready) {
            ready = true;
            // 从Spring容器获取替代登录数据
            Object substituleLoginDataObject = CommonBeanFactory.getBean("substituleLoginData");
            if (substituleLoginDataObject != null) {
                Map<String, Object> substituleLoginData = (Map<String, Object>) substituleLoginDataObject;
                if (ObjectUtils.isNotEmpty(substituleLoginData.get("pwd"))) {
                    pwd = substituleLoginData.get("pwd").toString();
                    return substituleLoginData.get("pwd").toString();
                }
            }
        }
        return pwd;
    }

    /**
     * 修改登录密码
     *
     * <p>操作流程：</p>
     * <ol>
     *   <li>创建包含新密码的Map对象</li>
     *   <li>更新内存中的密码缓存</li>
     *   <li>将新密码写入JSON配置文件</li>
     *   <li>异常情况下记录错误日志</li>
     * </ol>
     *
     * @param pwd 新的登录密码
     */
    public void modifyPwd(String pwd) {
        File file = new File(jsonFilePath);
        Map<String, String> myObject = new HashMap<>();
        myObject.put("pwd", pwd);

        // 更新内存中的密码缓存
        SubstituleLoginConfig.pwd = pwd;

        ObjectMapper mapper = new ObjectMapper();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // 将密码配置写入JSON文件
            mapper.writeValue(fos, myObject);
        } catch (IOException e) {
            // 记录文件写入失败的错误日志
            LogUtil.error(e.getCause(), new Throwable(e));
        }
    }
}
