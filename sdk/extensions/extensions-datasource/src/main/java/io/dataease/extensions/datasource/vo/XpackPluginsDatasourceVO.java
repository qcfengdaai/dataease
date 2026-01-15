package io.dataease.extensions.datasource.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * DataEase企业版数据源插件视图对象
 * <p>
 * 用于封装企业版（Xpack）插件数据源的详细信息，包括插件配置、
 * 显示信息、驱动路径等。支持动态加载和管理数据源插件。
 * </p>
 *
 * @author fit2cloud
 * @since 1.0
 */
@Data
public class XpackPluginsDatasourceVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 插件唯一标识符
     * <p>
     * 数据源插件在系统中的唯一ID，用于标识和管理插件。
     * 与数据库中的插件记录对应。
     * </p>
     */
    private Long id;

    /**
     * 插件名称
     * <p>
     * 数据源插件的显示名称，用于用户界面展示。
     * 应该为有意义的名称，便于用户理解和选择。
     * </p>
     */
    private String name;

    /**
     * 插件图标
     * <p>
     * 数据源插件的图标路径或名称，用于用户界面展示。
     * 提供更直观的视觉标识，便于用户快速识别插件类型。
     * </p>
     */
    private String icon;

    /**
     * 插件分类
     * <p>
     * 数据源插件的分类标识，如OLTP、OLAP、DL等。
     * 用于在用户界面中对插件进行分组和组织显示。
     * </p>
     */
    private String category;

    /**
     * 插件类型标识
     * <p>
     * 数据源插件的类型标识符，如mysql、oracle等。
     * 用于系统内部的插件识别和加载逻辑。
     * </p>
     */
    private String type;

    /**
     * 插件标志位
     * <p>
     * 数据源插件的数值标志位，用于快速标识和匹配。
     * 在插件管理和类型判断中使用。
     * </p>
     */
    private Integer flag;

    /**
     * 额外参数
     * <p>
     * 插件特有的额外配置参数，通常为JSON格式。
     * 用于存储插件特定的配置选项和高级设置。
     * </p>
     */
    private String extraParams;

    /**
     * SQL标识符前缀
     * <p>
     * 该插件数据源使用的SQL标识符转义前缀字符。
     * 用于正确处理数据库特有的关键字和特殊字符。
     * </p>
     */
    private String prefix;

    /**
     * SQL标识符后缀
     * <p>
     * 该插件数据源使用的SQL标识符转义后缀字符。
     * 与前缀配合使用，完成完整的标识符转义。
     * </p>
     */
    private String suffix;

    /**
     * 数据库驱动路径
     * <p>
     * 数据源驱动程序的文件路径或下载地址。
     * 用于动态加载和管理数据源的JDBC驱动程序。
     * </p>
     */
    private String driverPath;

    /**
     * 静态配置映射
     * <p>
     * 存储插件的静态配置信息的键值对映射。
     * 包含不可变的配置项和默认值，用于插件的初始化和配置。
     * </p>
     */
    private Map<String, String> staticMap;

}
