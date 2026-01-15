package io.dataease.extensions.datafilling.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据填报插件配置视图对象
 * 用于封装数据填报扩展插件的基本配置信息，包括插件类型、图标、分类等元数据
 * 该类是数据填报插件系统中插件配置的核心数据模型，用于插件的加载、识别和展示
 */
@Data
public class XpackPluginsDfVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5059944608544058565L;

    /**
     * 插件ID
     * 用于唯一标识数据填报插件的主键
     */
    private Long id;

    /**
     * 插件图标
     * 用于在UI界面上展示的插件图标路径或图标标识
     * 通常为SVG格式的图标内容或图标URL
     */
    private String icon;

    /**
     * 插件分类
     * 标识插件所属的分类，用于插件的分组管理和展示
     * 例如："datasource"表示数据源类插件，"datafilling"表示数据填报类插件
     */
    private String category;

    /**
     * 插件类型
     * 标识数据填报插件支持的数据库类型，与数据源类型对应
     * 例如："oracle"、"sqlserver"、"postgresql"等
     * 该类型值用于在ExtDDLProviderFactory中查找对应的DDL提供者
     */
    private String type;

    /**
     * 插件标志位
     * 用于标识插件的状态或特殊属性的标志位
     * 具体含义由插件系统的业务逻辑定义，可能包括启用状态、版本标识等
     */
    private Integer flag;

}
