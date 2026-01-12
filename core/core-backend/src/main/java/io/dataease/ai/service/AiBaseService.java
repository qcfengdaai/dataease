package io.dataease.ai.service;

import io.dataease.api.ai.AiComponentApi;
import io.dataease.commons.utils.UrlTestUtils;
import io.dataease.system.manage.SysParameterManage;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * AI基础服务控制器
 * 提供AI组件相关的基础功能和配置管理
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>获取AI服务配置参数</li>
 *   <li>管理AI服务的连接信息</li>
 *   <li>为AI功能模块提供基础服务支持</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>AI功能初始化时获取配置</li>
 *   <li>AI服务连接参数管理</li>
 *   <li>为其他AI相关服务提供配置支持</li>
 * </ul>
 *
 * @author WangJiaHao
 * @date 2024/3/27 09:47
 */
@RestController
@RequestMapping("aiBase")
public class AiBaseService implements AiComponentApi {
    /**
     * 系统参数管理器
     * 用于获取和管理系统配置参数
     */
    @Resource
    private SysParameterManage sysParameterManage;

    /**
     * 查找AI服务目标URL配置
     * 从系统参数中获取AI相关的配置信息，包括基础URL等
     *
     * <p>该方法会检查系统中是否配置了AI相关参数：</p>
     * <ul>
     *   <li>如果配置了ai.baseUrl且不为空，返回完整的AI配置参数</li>
     *   <li>如果未配置或配置为空，返回空的参数映射</li>
     * </ul>
     *
     * @return AI配置参数映射，key为参数名，value为参数值
     *         如果未配置AI参数，则返回空的HashMap
     */
    @Override
    public Map<String, String> findTargetUrl() {
        // 获取所有以"ai."开头的系统参数
        Map<String, String> templateParams = sysParameterManage.groupVal("ai.");

        // 检查是否存在有效的AI基础URL配置
        if (templateParams != null && StringUtils.isNotEmpty(templateParams.get("ai.baseUrl"))) {
            // 返回完整的AI配置参数
            return templateParams;
        } else {
            // 如果没有配置AI参数，返回空映射
            return new HashMap<>();
        }
    }
}
