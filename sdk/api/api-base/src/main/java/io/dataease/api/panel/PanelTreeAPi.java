package io.dataease.api.panel;

import io.dataease.api.panel.vo.PanelTreeNodeVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * DataEase面板树形结构API接口
 * <p>
 * 提供面板（仪表板）的树形结构查询功能。面板是DataEase中数据可视化的主要载体，
 * 支持多层级的文件夹组织结构，便于用户管理和查找仪表板。
 * </p>
 *
 * <p><b>主要功能：</b></p>
 * <ul>
 *   <li><b>树形查询</b> - 获取面板的层级结构</li>
 *   <li><b>关键字搜索</b> - 支持按关键字过滤面板</li>
 *   <li><b>文件夹管理</b> - 支持面板的分类组织</li>
 * </ul>
 *
 * @author DataEase团队
 * @since 1.0.0
 */
public interface PanelTreeAPi {

    /**
     * 获取面板树形结构
     * <p>
     * 根据关键字搜索并返回面板的树形结构列表。支持按面板名称进行模糊匹配，
     * 返回的结果包含文件夹和仪表板的层级关系。
     * </p>
     *
     * @param keyword 搜索关键字，支持面板名称的模糊匹配
     * @return 匹配的面板树形结构列表
     */
    @GetMapping("/tree/{keyword}")
    List<PanelTreeNodeVO> tree(@PathVariable("keyword") String keyword);


}
