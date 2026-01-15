package io.dataease.visualization.template;

import io.dataease.extensions.view.dto.ChartExtFilterDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 过滤器构建模板类
 *
 * <p>用于构建可视化组件的过滤器映射关系</p>
 *
 * @author DataEase
 * @since 2024-01-12
 */
public class FilterBuildTemplate {

    /**
     * 构建空的过滤器映射
     *
     * <p>从组件列表中提取所有UserView类型的组件，为每个视图创建空的过滤器列表</p>
     *
     * @param components 组件列表，每个组件包含component类型和id信息
     * @return 视图ID到过滤器列表的映射，初始时所有过滤列表都为空
     */
    public static Map<String, List<ChartExtFilterDTO>> buildEmpty(List<Map<String, Object>> components) {
        // 创建结果映射
        Map<String, List<ChartExtFilterDTO>> result = new HashMap<>();

        // 遍历所有组件
        components.forEach(element -> {
            // 检查是否是UserView类型的组件
            if (StringUtils.equals(element.get("component").toString(), "UserView")) {
                // 获取视图ID
                String viewId = element.get("id").toString();
                // 为该视图创建空的过滤器列表
                result.put(viewId, new ArrayList<>());
            }
        });

        return result;
    }
}
