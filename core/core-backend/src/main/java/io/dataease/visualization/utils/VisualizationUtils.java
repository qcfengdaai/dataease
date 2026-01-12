package io.dataease.visualization.utils;

import io.dataease.extensions.view.dto.ChartViewDTO;
import io.dataease.utils.JsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 可视化工具类
 * 提供可视化相关的工具方法
 */
public class VisualizationUtils {

    /**
     * 将图表视图对象转换为字符串
     * @param source 图表视图映射
     * @return JSON字符串映射
     */
    public static Map<Long, String> viewTransToStr(Map<Long, ChartViewDTO> source) {
        Map<Long, String> result = new HashMap<>();
        source.forEach((key, value) -> {
            result.put(key, (String) JsonUtil.toJSONString(value));
        });
        return result;
    }

    /**
     * 将字符串转换为图表视图对象
     * @param source JSON字符串映射
     * @return 图表视图映射
     */
    public static Map<Long, ChartViewDTO> viewTransToObj(Map<Long, String> source) {
        Map<Long, ChartViewDTO> result = new HashMap<>();
        source.forEach((key, value) -> {
            result.put(key, JsonUtil.parseObject(value, ChartViewDTO.class));
        });
        return result;
    }
}
