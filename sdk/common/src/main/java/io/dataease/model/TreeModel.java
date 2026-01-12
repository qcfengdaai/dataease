package io.dataease.model;

import lombok.Data;

import java.util.List;

/**
 * 树形模型包装类
 * 用于封装树形数据结构，提供统一的树形节点表示
 *
 * @param <T> 树节点数据的具体类型
 */
@Data
public class TreeModel<T>{

    /**
     * 树节点数据
     * 包含节点的具体业务数据
     */
    private TreeBaseModel<T> data;

    /**
     * 子节点列表
     * 存储当前节点的所有子节点
     */
    private List<TreeModel> children;

    /**
     * 构造函数
     * @param data 树节点数据
     */
    public TreeModel(TreeBaseModel<T> data) {
        this.data = data;
    }

    /**
     * 获取节点ID
     * @return 节点唯一标识
     */
    public Long getId() {
        return data.getId();
    }

    /**
     * 获取父节点ID
     * @return 父节点ID
     */
    public Long getPid() {
        return data.getPid();
    }

    /**
     * 获取节点名称
     * @return 节点显示名称
     */
    public String getName() {
        return data.getName();
    }
}
