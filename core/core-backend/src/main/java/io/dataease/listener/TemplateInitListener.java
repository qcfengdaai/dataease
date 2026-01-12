package io.dataease.listener;

import io.dataease.license.utils.LogUtil;
import io.dataease.template.manage.TemplateLocalParseManage;
import jakarta.annotation.Resource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 模板初始化监听器
 *
 * <p>功能描述：</p>
 * <ul>
 *   <li>在应用启动完成后，自动初始化系统模板</li>
 *   <li>从代码中解析并加载模板定义</li>
 *   <li>确保模板功能在系统启动后立即可用</li>
 * </ul>
 *
 * <p>执行顺序：@Order(value = 3)，在第3位执行，优先级较高</p>
 * <p>执行内容：</p>
 * <p>解析代码中定义的模板，包括图表模板、仪表板模板等</p>
 *
 * @author DataEase
 * @since 2.0.0
 */
@Component
@Order(value = 3)
public class TemplateInitListener implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    private TemplateLocalParseManage templateLocalParseManage;

    /**
     * 应用启动完成后的回调方法
     * 执行模板的初始化操作
     *
     * @param applicationReadyEvent 应用启动完成事件
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        LogUtil.info("=====Template init from code [Start]=====");
        try{
            // 从代码中解析并初始化模板
            templateLocalParseManage.doInit();
        }catch (Exception e){
            LogUtil.error("=====Template init from code ERROR=====");
        }
        LogUtil.info("=====Template init from code [End]=====");
    }
}
