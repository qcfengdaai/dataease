package io.dataease.commons.constants;

/**
 * 操作常量类
 * 定义系统中各种操作类型和资源类型的常量，用于统一管理操作日志、权限控制等功能
 *
 * <p>主要用途：</p>
 * <ul>
 *   <li>操作日志记录</li>
 *   <li>权限控制判断</li>
 *   <li>审计追踪</li>
 *   <li>操作分类统计</li>
 * </ul>
 *
 * @author WangJiaHao
 * @date 2023/10/8 09:37
 */
public class OptConstants {

    /**
     * 操作类型常量
     * 定义系统支持的基本操作类型，用于记录和区分不同的用户操作
     */
    public static final class OPT_TYPE {

        /**
         * 新建操作
         * 表示创建新的资源或实体
         */
        public static final int NEW = 1;

        /**
         * 更新操作
         * 表示修改已存在的资源或实体
         */
        public static final int UPDATE = 2;

        /**
         * 删除操作
         * 表示删除已存在的资源或实体
         */
        public static final int DELETE = 3;
    }

    /**
     * 操作资源类型常量
     * 定义系统中可以进行操作的各种资源类型，用于分类管理不同类型的业务对象
     */
    public static final class OPT_RESOURCE_TYPE {

        /**
         * 可视化资源
         * 包括图表、报表等可视化组件
         */
        public static final int VISUALIZATION = 1;

        /**
         * 仪表板
         * 包含多个可视化组件的综合展示面板
         */
        public static final int DASHBOARD = 2;

        /**
         * 数据大屏
         * 用于大屏展示的数据可视化页面
         */
        public static final int DATA_VISUALIZATION = 3;

        /**
         * 数据集
         * 经过处理和组织的数据集合
         */
        public static final int DATASET = 4;

        /**
         * 数据源
         * 原始数据的来源，如数据库连接等
         */
        public static final int DATASOURCE = 5;

        /**
         * 模板
         * 预定义的可复用资源模板
         */
        public static final int TEMPLATE = 6;
    }
}
