package io.dataease.exportCenter.util;

import io.dataease.exportCenter.manage.ExportCenterLimitManage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 导出中心工具类
 * 提供导出功能相关的静态工具方法，主要用于获取导出限制和配置信息
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>提供静态方法访问导出限制配置</li>
 *   <li>封装导出中心的通用工具方法</li>
 *   <li>支持不同导出类型的限制查询</li>
 * </ul>
 *
 * <p>设计模式：</p>
 * <ul>
 *   <li>使用Spring的依赖注入初始化静态字段</li>
 *   <li>提供静态方法供其他类方便调用</li>
 *   <li>避免在静态上下文中直接使用Spring Bean</li>
 * </ul>
 */
@Component
public class ExportCenterUtils {

    /**
     * 导出中心限制管理组件
     * 静态字段，用于在静态方法中访问Spring管理的Bean
     */
    private static ExportCenterLimitManage exportCenterLimitManage;

    /**
     * 设置导出中心限制管理组件
     * 通过Spring的依赖注入方式初始化静态字段，使静态方法能够访问Spring Bean
     *
     * @param exportCenterLimitManage 导出中心限制管理组件实例
     */
    @Resource(name = "exportCenterLimitManage")
    public void setExportCenterLimitManage(ExportCenterLimitManage exportCenterLimitManage) {
        ExportCenterUtils.exportCenterLimitManage = exportCenterLimitManage;
    }

    /**
     * 获取导出限制
     * 根据导出类型获取对应的导出限制配置，用于控制导出功能的使用
     *
     * @param type 导出类型，如"chart"、"dashboard"、"dataset"等
     * @return 该类型的导出限制数量，通常表示最大导出行数或文件大小限制
     */
    public static long getExportLimit(String type) {
        return exportCenterLimitManage.getExportLimit(type);
    }
}
