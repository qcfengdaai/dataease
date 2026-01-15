package io.dataease.visualization.dao.ext.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


/**
 * 可视化节点持久化对象
 * 用于存储树形结构的可视化节点信息
 */
@Data
public class VisualizationNodePO implements Serializable {

    /** 节点ID */
    private Long id;
    /** 节点名称 */
    private String name;
    /** 父节点ID */
    private Long pid;
    /** 节点类型 */
    private String nodeType;
    /** 额外标识 */
    @Schema(description = "额外标识")
    private int extraFlag;
    /** 额外标识1 */
    @Schema(description = "额外标识1")
    private int extraFlag1;

}
