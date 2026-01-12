package io.dataease.menu.bo;

import io.dataease.menu.dao.auto.entity.CoreMenu;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树节点业务对象
 *
 * 功能描述：
 * 继承自CoreMenu实体类，扩展了children字段，用于构建树形菜单结构
 *
 * 使用场景：
 * - 在构建菜单树时，需要维护父子关系
 * - 将扁平的菜单列表转换为树形结构
 * - 递归处理菜单的层级关系
 *
 * @author DataEase
 * @since 2023-06-02
 */
@Data
public class MenuTreeNode extends CoreMenu {

    /**
     * 子菜单列表
     * 包含当前菜单的所有直接子菜单
     */
    private List<MenuTreeNode> children = new ArrayList<>();
}
