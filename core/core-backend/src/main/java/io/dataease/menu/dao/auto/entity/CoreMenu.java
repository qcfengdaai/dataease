package io.dataease.menu.dao.auto.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 系统菜单实体类
 *
 * 功能描述：
 * 表示系统中的菜单项，用于构建前端导航和路由
 * 支持多层级菜单结构（通过pid字段）
 *
 * 菜单类型：
 * - 0: 菜单项（具体的功能页面）
 * - 1: 目录（菜单分组，不含具体页面）
 *
 * 数据表：core_menu
 *
 * @author fit2cloud
 * @since 2023-06-02
 */
@TableName("core_menu")
public class CoreMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     * 自增主键，唯一标识一个菜单项
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父菜单ID
     * 用于构建树形结构，顶级菜单的pid为0
     */
    private Long pid;

    /**
     * 菜单类型
     * 0: 菜单项（具体页面）
     * 1: 目录（分组）
     */
    private Integer type;

    /**
     * 菜单名称
     * 存储的是国际化key，例如："sys_manage"
     * 实际显示的名称通过i18n_menu.sys_manage获取
     */
    private String name;

    /**
     * 前端组件路径
     * 对应的Vue组件路径，例如："system/index.vue"
     */
    private String component;

    /**
     * 菜单排序
     * 数值越小排序越靠前，用于控制菜单显示顺序
     */
    private Integer menuSort;

    /**
     * 菜单图标
     * 图标名称或图标类名，用于前端显示
     */
    private String icon;

    /**
     * 路由路径
     * 前端路由的路径，例如："/system"
     */
    private String path;

    /**
     * 是否隐藏
     * true: 隐藏该菜单（不在导航中显示）
     * false: 正常显示
     */
    private Boolean hidden;

    /**
     * 是否在布局内
     * true: 菜单在系统布局内显示
     * false: 菜单使用独立布局（如登录页）
     */
    private Boolean inLayout;

    /**
     * 是否参与权限控制
     * true: 需要权限验证
     * false: 不需要权限验证（公开菜单）
     */
    private Boolean auth;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Integer getMenuSort() {
        return menuSort;
    }

    public void setMenuSort(Integer menuSort) {
        this.menuSort = menuSort;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getInLayout() {
        return inLayout;
    }

    public void setInLayout(Boolean inLayout) {
        this.inLayout = inLayout;
    }

    public Boolean getAuth() {
        return auth;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "CoreMenu{" +
        "id = " + id +
        ", pid = " + pid +
        ", type = " + type +
        ", name = " + name +
        ", component = " + component +
        ", menuSort = " + menuSort +
        ", icon = " + icon +
        ", path = " + path +
        ", hidden = " + hidden +
        ", inLayout = " + inLayout +
        ", auth = " + auth +
        "}";
    }
}
