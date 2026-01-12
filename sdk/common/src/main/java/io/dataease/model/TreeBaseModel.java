package io.dataease.model;

import java.io.Serializable;

/**
 * 树形基础模型接口
 * 定义树形结构数据模型的基本属性和方法
 *
 * @param <T> 泛型类型参数
 */
public interface TreeBaseModel<T> extends Serializable {

    /**
     * 获取节点ID
     * @return 节点的唯一标识
     */
    Long getId();

    /**
     * 获取父节点ID
     * @return 父节点ID，根节点返回null或0
     */
    Long getPid();

    /**
     * 获取节点名称
     * @return 节点的显示名称
     */
    String getName();

    /**
     * 设置节点名称
     * @param name 节点名称
     */
    void setName(String name);
}
