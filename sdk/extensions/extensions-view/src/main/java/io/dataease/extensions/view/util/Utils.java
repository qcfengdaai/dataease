package io.dataease.extensions.view.util;

import io.dataease.extensions.datasource.dto.DatasourceSchemaDTO;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通用工具类
 * 提供数据源相关的工具方法
 */
public class Utils {

    /**
     * 判断是否需要排序
     * 检查给定的数据源类型列表中是否包含需要特殊排序处理的数据库类型
     *
     * @param dsList 数据源类型列表
     * @return 如果包含需要排序的数据库类型则返回true，否则返回false
     */
    public static boolean isNeedOrder(List<String> dsList) {
        // 定义需要特殊排序处理的数据库类型数组
        String[] list = {"sqlServer", "db2", "impala"};
        List<String> strings = Arrays.asList(list);
        // 通过Stream筛选出在数据源列表中存在的需要排序的数据库类型
        List<String> collect = strings.stream().filter(dsList::contains).collect(Collectors.toList());
        // 如果筛选结果不为空，说明需要排序
        return ObjectUtils.isNotEmpty(collect);
    }

    /**
     * 判断是否为跨数据源查询
     * 通过检查数据源映射表的大小来判断是否涉及多个数据源
     *
     * @param dsMap 数据源模式映射表，key为数据源ID，value为数据源模式DTO
     * @return 如果涉及多个数据源则返回true，否则返回false
     */
    public static boolean isCrossDs(Map<Long, DatasourceSchemaDTO> dsMap) {
        // 当数据源映射表大小不等于1时，说明涉及多个数据源
        return dsMap.size() != 1;
    }
}
