package io.dataease.extensions.view.util;

import io.dataease.extensions.datasource.dto.DatasetTableFieldDTO;
import io.dataease.extensions.view.dto.ChartViewFieldBaseDTO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字段工具类
 * 提供字段数据转换相关的工具方法
 */
public class FieldUtil {

    /**
     * 转换图表视图字段为数据集表字段
     * 将图表视图字段基础DTO列表转换为数据集表字段DTO列表
     *
     * @param list 图表视图字段基础DTO列表
     * @return 数据集表字段DTO列表
     */
    public static List<DatasetTableFieldDTO> transFields(List<? extends ChartViewFieldBaseDTO> list) {
        // 使用Stream API遍历列表，逐个转换字段对象
        return list.stream().map(ele -> {
            // 创建新的数据集表字段DTO对象
            DatasetTableFieldDTO dto = new DatasetTableFieldDTO();
            // 使用BeanUtils复制属性，将源对象的属性值复制到目标对象
            BeanUtils.copyProperties(ele, dto);
            return dto;
        }).collect(Collectors.toList());
    }
}
