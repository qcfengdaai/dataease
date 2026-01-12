package io.dataease.model;

import java.util.List;

/**
 * 树形结果模型接口
 * 用于标识可以设置子节点列表的树形结构对象
 * 通常用于树形数据的查询结果封装
 *
 * @param <T> 子节点的类型
 */
public interface TreeResultModel<T> {

    /**
     * 设置子节点列表
     * @param children 子节点列表
     */
    void setChildren(List<T> children);
}
