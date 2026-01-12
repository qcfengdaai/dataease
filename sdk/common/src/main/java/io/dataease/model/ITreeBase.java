package io.dataease.model;

import java.util.List;

/**
 * 树形结构基础接口
 * 定义树形数据结构的基本方法，用于构建层次化的数据模型
 *
 * @param <T> 树节点的具体类型
 */
public interface ITreeBase<T> {

    /**
     * 获取节点类型
     * @return 节点类型标识
     */
    String getNodeType();

    /**
     * 获取节点ID
     * @return 节点唯一标识
     */
    Long getId();

    /**
     * 设置节点ID
     * @param id 节点唯一标识
     */
    void setId(Long id);

    /**
     * 获取父节点ID
     * @return 父节点ID，如果是根节点则为null
     */
    Long getPid();

    /**
     * 设置父节点ID
     * @param pid 父节点ID
     */
    void setPid(Long pid);

    /**
     * 获取子节点列表
     * @return 子节点列表，如果是叶子节点则为空列表
     */
    List<T> getChildren();

    /**
     * 设置子节点列表
     * @param children 子节点列表
     */
    void setChildren(List<T> children);
}
