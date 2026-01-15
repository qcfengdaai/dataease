package io.dataease.dataset.dao.ext.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据集节点持久化对象
 * 用于扩展查询中的数据集节点信息传输
 * 包含节点的基本标识和层次结构信息
 *
 * @author Junjun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataSetNodePO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4457506330575500164L;

    /**
     * 节点ID
     * 数据集节点的唯一标识符
     */
    private Long id;

    /**
     * 节点名称
     * 数据集节点的显示名称
     */
    private String name;

    /**
     * 节点类型
     * 标识节点是文件夹(folder)还是数据集(dataset)
     */
    private String nodeType;

    /**
     * 父节点ID
     * 指向父级节点的ID，用于构建层次结构
     */
    private Long pid;
}
