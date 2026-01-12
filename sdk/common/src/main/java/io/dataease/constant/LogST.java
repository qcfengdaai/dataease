package io.dataease.constant;

import java.util.Arrays;

/**
 * 资源类型枚举
 * <p>
 * 定义DataEase系统中所有可操作的资源类型，用于操作日志中标识操作对象的类型。
 * 每个枚举值包含数值码和名称，用于在数据库中存储和国际化显示。
 * </p>
 *
 * <p><b>资源类型分类：</b></p>
 * <ul>
 *   <li><b>数据相关：</b>DATASOURCE、DATASET、DATA - 数据源、数据集和数据记录</li>
 *   <li><b>可视化：</b>PANEL、SCREEN、VIEW - 仪表板、大屏和视图</li>
 *   <li><b>用户管理：</b>USER、ROLE、ORG - 用户、角色和组织架构</li>
 *   <li><b>模板类：</b>STYLE_TEMPLATE、APP_TEMPLATE - 样式模板和应用模板</li>
 *   <li><b>系统管理：</b>MENU、DRIVER、APIKEY - 菜单、驱动和API密钥</li>
 *   <li><b>功能模块：</b>LINK、DATA_FILLING、REPORT_TASK - 分享链接、数据填报和报表任务</li>
 *   <li><b>同步任务：</b>SYNC_DATASOURCE、SYNC_TASK、SYNC_TASK_LOG - 同步数据源和任务</li>
 * </ul>
 */
public enum LogST {
    /** 仪表板 - 数据分析仪表板，包含多个图表的数据可视化页面 */
    PANEL(1, "SOURCE_TYPE_PANEL"),
    /** 数据大屏 - 用于大屏展示的数据可视化页面 */
    SCREEN(2, "SOURCE_TYPE_SCREEN"),
    /** 数据集 - 数据表或数据集合，是图表和仪表板的数据源 */
    DATASET(3, "SOURCE_TYPE_DATASET"),
    /** 数据源 - 数据库连接配置，如MySQL、Oracle等数据库连接 */
    DATASOURCE(4, "SOURCE_TYPE_DATASOURCE"),
    /** 样式模板 - 仪表板或图表的样式模板配置 */
    STYLE_TEMPLATE(4, "SOURCE_STYLE_TEMPLATE"),
    /** 应用模板 - 应用程序或系统模板 */
    APP_TEMPLATE(4, "SOURCE_APP_TEMPLATE"),
    /** 用户 - 系统用户账户 */
    USER(5, "SOURCE_TYPE_USER"),
    /** 角色 - 系统角色，用于权限管理 */
    ROLE(6, "SOURCE_TYPE_ROLE"),
    /** 组织部门 - 组织架构中的部门或组织单位 */
    ORG(7, "SOURCE_TYPE_DEPT"),
    /** 视图 - 数据视图或图表组件 */
    VIEW(8, "SOURCE_TYPE_VIEW"),
    /** 分享链接 - 用于分享仪表板或图表的外部访问链接 */
    LINK(9, "SOURCE_TYPE_LINK"),
    /** 数据库驱动 - 数据库JDBC驱动程序 */
    DRIVER(10, "SOURCE_TYPE_DRIVER"),
    /** 驱动文件 - 上传的数据库驱动文件 */
    DRIVER_FILE(11, "SOURCE_TYPE_DRIVER_FILE"),
    /** 系统菜单 - 系统导航菜单配置 */
    MENU(12, "SOURCE_TYPE_MENU"),
    /** API密钥 - 用于API访问的安全密钥 */
    APIKEY(13, "SOURCE_TYPE_APIKEY"),
    /** 数据填报 - 数据填报表单或数据录入功能 */
    DATA_FILLING(14, "SOURCE_TYPE_DATAFILLING"),
    /** 数据记录 - 具体的数据记录或数据行 */
    DATA(15, "SOURCE_TYPE_DATA"),
    /** 报表任务 - 定时报表生成和发送任务 */
    REPORT_TASK(20, "SOURCE_TYPE_REPORT_TASK"),
    /** 同步数据源 - 用于数据同步的目标数据源配置 */
    SYNC_DATASOURCE(21, "SOURCE_TYPE_SYNC_DATASOURCE"),
    /** 同步任务 - 数据同步任务配置和执行计划 */
    SYNC_TASK(22, "SOURCE_TYPE_SYNC_TASK"),
    /** 同步任务日志 - 数据同步任务的执行日志记录 */
    SYNC_TASK_LOG(23, "SOURCE_TYPE_SYNC_TASK_LOG");
    /** 资源类型的数值码，用于数据库存储 */
    private Integer value;

    /** 资源类型的名称标识，用于国际化和显示 */
    private String name;

    /**
     * 获取资源类型的数值码
     *
     * @return 资源类型数值码
     */
    public Integer getValue() {
        return value;
    }

    /**
     * 获取资源类型的名称标识
     *
     * @return 资源类型名称标识
     */
    public String getName() {
        return name;
    }

    /**
     * 资源类型枚举构造函数
     *
     * @param value 资源类型的数值码
     * @param name 资源类型的名称标识
     */
    LogST(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * 根据数值码获取对应的资源类型枚举
     *
     * @param value 资源类型的数值码
     * @return 对应的LogST枚举值
     * @throws java.util.NoSuchElementException 当找不到匹配的枚举值时
     */
    public static LogST fromValue(Integer value) {
        return Arrays.stream(values()).filter(v -> v.value.equals(value)).findFirst().get();
    }

    /**
     * 默认构造函数
     */
    LogST() {
    }
}
