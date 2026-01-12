package io.dataease.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Schema(description = "业务资源结点")
@Data
public class BusiNodeVO implements TreeResultModel<BusiNodeVO>, Serializable {


    @Serial
    private static final long serialVersionUID = 8191619596741217494L;

    /**
     * 节点ID
     * 业务资源节点的唯一标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "ID")
    private Long id;

    /**
     * 节点名称
     * 业务资源的显示名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 是否为叶子节点
     * true表示叶子节点，false表示有子节点
     */
    @Schema(description = "是否叶子")
    private Boolean leaf;

    /**
     * 权重
     * 用于节点排序和优先级计算
     */
    @Schema(description = "权重")
    private Integer weight;

    /**
     * 额外标识
     * 用于业务逻辑扩展的标记字段
     */
    @Schema(description = "额外标识")
    private int extraFlag;

    /**
     * 额外标识1
     * 用于业务逻辑扩展的辅助标记字段
     */
    @Schema(description = "额外标识1")
    private int extraFlag1;

    /**
     * 节点类型
     * 标识业务资源的类型（如文件夹、文件等）
     */
    @Schema(description = "类型")
    private String type;

    /**
     * 子节点列表
     * 当前节点的所有子节点
     */
    @Schema(description = "子节点")
    private List<BusiNodeVO> children;

    /**
     * 扩展权重
     * 独立的权重值，用于特殊排序逻辑
     */
    @Schema(description = "独立权重")
    private Integer ext;
}
