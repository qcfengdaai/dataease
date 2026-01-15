package io.dataease.extensions.view.filter;

import lombok.Data;

import java.util.List;


/**
 * 过滤器树对象
 * 用于构建复合过滤条件的树形结构
 *
 * @Author Junjun
 */
@Data
public class FilterTreeObj {
    /**
     * 逻辑操作符，如 AND、OR
     */
    private String logic;

    /**
     * 过滤条件项列表
     */
    private List<FilterTreeItem> items;
}
