package io.dataease.api.permissions.dataset.dto;


import io.dataease.model.ITreeBase;
import lombok.Data;

import java.util.List;

/**
 * 基础树节点对象
 *
 * <p>通用的树形结构节点，实现了 ITreeBase 接口，支持构建层级结构数据。
 * 在数据集权限管理中用于表示组织树、数据字段树、过滤条件树等各种树形结构。</p>
 *
 * <p>树形结构特点：
 * <ul>
 *   <li>父子关系：通过 id 和 pid 建立节点的父子关系</li>
 *   <li>节点类型：通过 nodeType 区分不同类型的节点（如组织、部门、字段等）</li>
 *   <li>递归结构：通过 children 实现多级嵌套</li>
 *   <li>文本显示：text 字段用于在界面上显示节点名称</li>
 * </ul>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Data
public class BaseTreeNode implements ITreeBase<BaseTreeNode> {

    /**
     * 节点ID
     *
     * <p>树节点的唯一标识，可能代表组织ID、字段ID等</p>
     */
    private Long id;

    /**
     * 父节点ID
     *
     * <p>指向父节点的ID，根节点的 pid 通常为 null 或 0</p>
     */
    private Long pid;

    /**
     * 节点显示文本
     *
     * <p>在界面上显示的节点名称，如组织名称、字段名称等</p>
     */
    private String text;

    /**
     * 节点类型
     *
     * <p>用于区分不同类型的树节点，如：
     * <ul>
     *   <li>dept: 部门/组织节点</li>
     *   <li>field: 数据字段节点</li>
     *   <li>condition: 过滤条件节点</li>
     *   <li>operator: 操作符节点（AND、OR等）</li>
     * </ul>
     * </p>
     */
    private String nodeType;

    /**
     * 子节点列表
     *
     * <p>该节点的所有直接子节点，支持递归的多级树形结构</p>
     */
    private List<BaseTreeNode> children;

    /**
     * 构造函数
     *
     * @param id 节点ID
     * @param pid 父节点ID
     * @param text 节点显示文本
     * @param nodeType 节点类型
     */
    public BaseTreeNode(Long id, Long pid, String text, String nodeType) {
        this.id = id;
        this.pid = pid;
        this.text = text;
        this.nodeType = nodeType;
    }

}
