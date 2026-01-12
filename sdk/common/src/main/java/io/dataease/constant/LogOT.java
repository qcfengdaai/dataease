package io.dataease.constant;

import java.util.Arrays;

/**
 * 操作类型枚举
 * <p>
 * 定义DataEase系统中所有可能的用户操作类型，用于操作日志的记录和分类。
 * 每个枚举值包含数值码和名称，用于在数据库中存储和国际化显示。
 * </p>
 *
 * <p><b>操作类型分类：</b></p>
 * <ul>
 *   <li><b>基础操作：</b>CREATE、MODIFY、DELETE、READ - 数据的增删改查操作</li>
 *   <li><b>文件操作：</b>EXPORT、DOWNLOAD、UPLOADFILE - 文件的上传下载和导出</li>
 *   <li><b>权限操作：</b>AUTHORIZE、UNAUTHORIZE、BIND、UNBIND - 权限授予和撤销</li>
 *   <li><b>链接管理：</b>CREATELINK、DELETELINK、MODIFYLINK - 分享链接的管理</li>
 *   <li><b>系统操作：</b>LOGIN、CLEAR - 登录和清理操作</li>
 *   <li><b>任务管理：</b>定时任务和同步任务的启停和执行操作</li>
 *   <li><b>导出操作：</b>各种格式的导出操作（模板、PDF、图片等）</li>
 * </ul>
 */
public enum LogOT {
    /** 创建操作 - 创建新的资源对象（如用户、数据源、仪表板等） */
    CREATE(1, "OPERATE_TYPE_CREATE"),
    /** 修改操作 - 更新现有资源的信息或配置 */
    MODIFY(2, "OPERATE_TYPE_MODIFY"),
    /** 删除操作 - 删除指定的资源对象 */
    DELETE(3, "OPERATE_TYPE_DELETE"),
    /** 查询操作 - 读取或查询资源信息（一般用于重要数据的访问记录） */
    READ(4, "OPERATE_TYPE_READ"),
    /** 导出操作 - 通用的数据导出操作 */
    EXPORT(5, "OPERATE_TYPE_EXPORT"),
    /** 授权操作 - 向用户或角色授予访问权限 */
    AUTHORIZE(6, "OPERATE_TYPE_AUTHORIZE"),
    /** 取消授权操作 - 撤销用户或角色的访问权限 */
    UNAUTHORIZE(7, "OPERATE_TYPE_UNAUTHORIZE"),
    /** 创建链接 - 生成分享链接或外部访问链接 */
    CREATELINK(8, "OPERATE_TYPE_CREATELINK"),
    /** 删除链接 - 删除已生成的分享链接 */
    DELETELINK(9, "OPERATE_TYPE_DELETELINK"),
    /** 修改链接 - 更新分享链接的设置或权限 */
    MODIFYLINK(10, "OPERATE_TYPE_MODIFYLINK"),
    /** 上传文件 - 上传文件到系统（如驱动程序、数据文件等） */
    UPLOADFILE(11, "OPERATE_TYPE_UPLOADFILE"),
    /** 绑定操作 - 将资源与用户或其他对象建立关联关系 */
    BIND(12, "OPERATE_TYPE_BIND"),
    /** 解绑操作 - 解除资源与用户或其他对象的关联关系 */
    UNBIND(13, "OPERATE_TYPE_UNBIND"),
    /** 登录操作 - 用户登录系统的操作记录 */
    LOGIN(14, "OPERATE_TYPE_LOGIN"),
    /** 下载操作 - 下载文件或资源 */
    DOWNLOAD(15, "OPERATE_TYPE_DOWNLOAD"),
    /** 模板导出 - 导出仪表板模板或数据模板 */
    TEMPLATE_EXPORT(16, "OPERATE_TYPE_TEMPLATE_EXPORT"),
    /** 应用模板导出 - 导出应用模板或APP模板 */
    APP_TEMPLATE_EXPORT(17, "OPERATE_TYPE_APP_EXPORT"),
    /** PDF导出 - 将仪表板或报表导出为PDF格式 */
    PDF_EXPORT(18, "OPERATE_TYPE_PDF_EXPORT"),
    /** 图片导出 - 将图表或仪表板导出为图片格式 */
    IMG_EXPORT(19, "OPERATE_TYPE_IMG_EXPORT"),

    /** 任务启用 - 启用定时任务或报表任务 */
    TASK_ENABLE(20, "OPERATE_TYPE_TASK_ENABLE"),
    /** 任务禁用 - 禁用定时任务或报表任务 */
    TASK_DISENABLE(21, "OPERATE_TYPE_TASK_DISENABLE"),
    /** 任务立即执行 - 手动触发定时任务的即时执行 */
    TASK_RUN_IMMEDIATELY(22, "OPERATE_TYPE_TASK_RUN_IMMEDIATELY"),

    /** 同步任务启用 - 启用数据同步任务 */
    SYNC_TASK_ENABLE(23, "OPERATE_TYPE_SYNC_TASK_ENABLE"),
    /** 同步任务禁用 - 禁用数据同步任务 */
    SYNC_TASK_DISENABLE(24, "OPERATE_TYPE_SYNC_TASK_DISENABLE"),
    /** 同步任务立即执行 - 手动触发数据同步任务的即时执行 */
    SYNC_TASK_RUN_IMMEDIATELY(25, "OPERATE_TYPE_SYNC_TASK_RUN_IMMEDIATELY"),
    /** 同步任务终止 - 终止正在执行的数据同步任务 */
    SYNC_TASK_RUN_TERMINATION(26, "OPERATE_TYPE_SYNC_TASK_RUN_TERMINATION"),
    /** 清理操作 - 清理数据、缓存或日志等系统维护操作 */
    CLEAR(27, "CLEAR");
    /** 操作类型的数值码，用于数据库存储 */
    private Integer value;
    /** 操作类型的名称标识，用于国际化和显示 */
    private String name;

    /**
     * 操作类型枚举构造函数
     *
     * @param value 操作类型的数值码
     * @param name 操作类型的名称标识
     */
    LogOT(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * 根据数值码获取对应的操作类型枚举
     *
     * @param value 操作类型的数值码
     * @return 对应的LogOT枚举值
     * @throws java.util.NoSuchElementException 当找不到匹配的枚举值时
     */
    public static LogOT fromValue(Integer value) {
        return Arrays.stream(values()).filter(v -> v.value.equals(value)).findFirst().get();
    }

    /**
     * 获取操作类型的数值码
     *
     * @return 操作类型数值码
     */
    public Integer getValue() {
        return value;
    }

    /**
     * 获取操作类型的名称标识
     *
     * @return 操作类型名称标识
     */
    public String getName() {
        return name;
    }
}
