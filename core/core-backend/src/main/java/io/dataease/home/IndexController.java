package io.dataease.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页控制器
 * 负责处理应用主页和仪表板页面的路由请求
 *
 * 主要功能：
 * - 处理根路径请求，返回主页
 * - 处理仪表板路径请求，返回仪表板页面
 *
 * 使用场景：
 * - 用户访问应用根路径时，返回index.html页面
 * - 用户访问仪表板时，返回panel.html页面
 */
@Controller
@RequestMapping
public class IndexController {

    /** 主页页面文件名 */
    private static final String INDEX_PAGE = "index.html";
    /** 仪表板页面文件名 */
    private static final String PANEL_PAGE = "panel.html";

    /**
     * 处理根路径请求
     * @return 主页页面名称
     */
    @GetMapping("/")
    public String index() {
        return INDEX_PAGE;
    }

    /**
     * 处理仪表板路径请求
     * @return 仪表板页面名称
     */
    @GetMapping("/panel")
    public String panel() {
        return PANEL_PAGE;
    }


}
