package io.dataease.extensions.view.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 数据集行权限树对象DTO
 * 用于定义数据集行级权限的树形结构
 */
@Data
public class DatasetRowPermissionsTreeObj implements Serializable {

    /** 逻辑关系：'and'或'or' */
    private String logic;

    /** 树节点项列表 */
    private List<DatasetRowPermissionsTreeItem> items;

    private static final long serialVersionUID = 1L;
}
