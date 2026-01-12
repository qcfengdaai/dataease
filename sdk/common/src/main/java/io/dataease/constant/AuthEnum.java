package io.dataease.constant;

/**
 * DataEase权限类型枚举
 * <p>
 * 定义系统中各种权限类型及其权重值，用于权限控制和权限验证。
 * 权重值用于确定权限的优先级和范围，数值越高权限范围越大。
 * </p>
 *
 * <p><b>权限层级关系：</b></p>
 * <ul>
 *   <li>READ (1) - 基础读权限</li>
 *   <li>USER (2) - 用户权限</li>
 *   <li>EXPORT (4) - 导出权限</li>
 *   <li>EXPORT_VIEW (5) - 视图导出权限</li>
 *   <li>EXPORT_DETAIL (6) - 详细导出权限</li>
 *   <li>MANAGE (7) - 管理权限</li>
 *   <li>AUTH (9) - 授权权限（最高权限）</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 */
public enum AuthEnum {
    /** 读取权限 - 基础的数据查看权限，权重值1 */
    READ(1),

    /** 用户权限 - 普通用户操作权限，权重值2 */
    USER(2),

    /** 导出权限 - 数据导出功能权限，权重值4 */
    EXPORT(4),

    /** 视图导出权限 - 视图级别的导出权限，权重值5 */
    EXPORT_VIEW(5),

    /** 详细导出权限 - 详细数据导出权限，权重值6 */
    EXPORT_DETAIL(6),

    /** 管理权限 - 资源管理和配置权限，权重值7 */
    MANAGE(7),

    /** 授权权限 - 最高级别权限，可授予他人权限，权重值9 */
    AUTH(9);
    /** 权限权重值，用于判断权限级别和优先级 */
    private Integer weight;

    /**
     * 获取权限权重值
     * <p>
     * 权重值用于权限比较和验证，数值越高表示权限级别越高。
     * 在权限验证时，通常需要当前用户权限的权重值大于等于所需权限的权重值。
     * </p>
     *
     * @return 权限权重值
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * 设置权限权重值
     *
     * @param weight 权限权重值
     */
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    /**
     * 权限枚举构造函数
     *
     * @param weight 权限权重值
     */
    AuthEnum(Integer weight) {
        this.weight = weight;
    }

    /**
     * 默认构造函数
     */
    AuthEnum() {
    }
}
