package io.dataease.visualization.dto;

import io.dataease.model.TreeBaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 可视化节点业务对象
 * 用于树形结构展示可视化资源
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisualizationNodeBO implements TreeBaseModel {
    @Serial
    private static final long serialVersionUID = -4998292096597683628L;

    /** 节点ID */
    private Long id;
    /** 节点名称 */
    private String name;
    /** 是否为叶子节点 */
    private Boolean leaf;
    /** 权重(用于排序) */
    private Integer weight = 3;
    /** 父节点ID */
    private Long pid;
    /** 扩展标记 */
    private Integer extraFlag;
    /** 扩展标记1 */
    private Integer extraFlag1;


}
