package io.dataease.dataset.dto;

import io.dataease.model.TreeBaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 数据集树形结构节点业务对象
 * 用于表示数据集在树形展示中的节点信息，支持层次结构展示
 *
 * @author Junjun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataSetNodeBO implements TreeBaseModel {

    @Serial
    private static final long serialVersionUID = 728340676442387790L;

    /**
     * 节点ID
     * 唯一标识数据集树形结构中的节点
     */
    private Long id;

    /**
     * 节点名称
     * 用于在界面上显示的节点标题
     */
    private String name;

    /**
     * 是否为叶子节点
     * true表示叶子节点(没有子节点)，false表示目录节点(可能有子节点)
     */
    private Boolean leaf;

    /**
     * 节点权重
     * 用于排序显示，默认权重为3
     */
    private Integer weight = 3;

    /**
     * 父节点ID
     * 指向父级节点的ID，顶级节点的父ID为null或0
     */
    private Long pid;

    /**
     * 扩展标识
     * 用于标识节点的额外状态或类型信息
     */
    private Integer extraFlag;
}
