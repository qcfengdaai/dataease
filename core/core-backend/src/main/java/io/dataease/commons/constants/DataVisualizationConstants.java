package io.dataease.commons.constants;

/**
 * 数据可视化常量类
 * 定义数据可视化模块中使用的各种常量，包括仪表板创建、查询来源、节点类型、操作类型等
 *
 * <p>主要用途：</p>
 * <ul>
 *   <li>仪表板和可视化组件的创建管理</li>
 *   <li>模板系统的类型区分</li>
 *   <li>资源节点的类型标识</li>
 *   <li>操作类型的统一定义</li>
 * </ul>
 */
public class DataVisualizationConstants {

    /**
     * 查询来源常量
     * 定义数据可视化查询请求的来源类型，用于区分不同的访问场景和权限控制
     */
    public static final class QUERY_SOURCE {

        /**
         * 定时报告
         * 来自定时报告功能的查询请求
         */
        public static final String REPORT = "report";

        /**
         * 主工程
         * 来自主应用程序的查询请求
         */
        public static final String MAIN = "main";

        /**
         * 主工程编辑区
         * 来自主应用程序编辑模式的查询请求
         */
        public static final String MAIN_EDIT = "main-edit";
    }

    /**
     * 新建仪表板来源常量
     * 定义创建新仪表板时的不同来源方式，用于跟踪和管理仪表板的创建路径
     */
    public static final class NEW_PANEL_FROM {

        /**
         * 直接新建
         * 从空白开始创建新的仪表板
         */
        public static final String NEW = "new";

        /**
         * 内部模板新建
         * 基于系统内部模板创建新的仪表板
         */
        public static final String NEW_INNER_TEMPLATE = "new_inner_template";

        /**
         * 外部模板新建
         * 基于外部导入的模板创建新的仪表板
         */
        public static final String NEW_OUTER_TEMPLATE = "new_outer_template";

        /**
         * 模板市场新建
         * 基于模板市场中的模板创建新的仪表板
         */
        public static final String NEW_MARKET_TEMPLATE = "new_market_template";
    }

    /**
     * 删除标志常量
     * 定义资源的删除状态标识，用于软删除机制
     */
    public static final class DELETE_FLAG {

        /**
         * 已删除
         * 标识资源已被删除，不可用状态
         */
        public static final boolean DELETED = true;

        /**
         * 未删除（可用）
         * 标识资源可用，正常状态
         */
        public static final boolean AVAILABLE = false;
    }

    /**
     * 节点类型常量
     * 定义资源树结构中不同节点的类型，用于区分目录和实际资源
     */
    public static final class NODE_TYPE {

        /**
         * 目录
         * 表示文件夹类型的节点，可以包含子节点
         */
        public static final String FOLDER = "folder";

        /**
         * 资源节点
         * 表示实际的资源节点，如仪表板、图表等
         */
        public static final String LEAF = "leaf";
    }

    /**
     * 资源操作类型常量
     * 定义对资源进行的各种操作类型，用于权限控制和操作记录
     */
    public static final class RESOURCE_OPT_TYPE {

        /**
         * 新建资源节点
         * 创建新的资源节点（如仪表板、图表等）
         */
        public static final String NEW_LEAF = "newLeaf";

        /**
         * 新建文件夹
         * 创建新的目录节点
         */
        public static final String NEW_FOLDER = "newFolder";

        /**
         * 移动
         * 移动资源到不同的目录位置
         */
        public static final String MOVE = "move";

        /**
         * 重命名
         * 修改资源的名称
         */
        public static final String RENAME = "rename";

        /**
         * 编辑
         * 编辑资源的内容或配置
         */
        public static final String EDIT = "edit";

        /**
         * 复制
         * 复制资源创建副本
         */
        public static final String COPY = "copy";
    }

    /**
     * 模板来源常量
     * 定义模板的不同来源类型，用于模板管理和分类
     */
    public static final class TEMPLATE_SOURCE {

        /**
         * 模板市场
         * 来自在线模板市场的模板
         */
        public static final String MARKET = "market";

        /**
         * 模板管理
         * 来自系统模板管理功能的模板
         */
        public static final String MANAGE = "manage";

        /**
         * 公共
         * 公共可用的模板
         */
        public static final String PUBLIC = "public";
    }
}
