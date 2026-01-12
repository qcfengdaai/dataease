package io.dataease.api.ds.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据源简化视图对象
 * <p>
 * 用于数据源基本信息的简化展示，不包含敏感配置信息。
 * 主要用于列表展示、快速选择等场景。
 * </p>
 *
 * @author DataEase团队
 * @since 1.0.0
 */
@Data
public class DsSimpleVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 46446424188194481L;

    /**
     * 数据源名称
     * <p>
     * 数据源的显示名称，用户自定义的可读标识。
     * </p>
     */
    private String name;

    /**
     * 数据源类型
     * <p>
     * 数据源的类型标识（如mysql、postgresql、oracle等）。
     * </p>
     */
    private String type;

    /**
     * 数据源描述
     * <p>
     * 数据源的详细描述信息，用于说明数据源的用途和特点。
     * </p>
     */
    private String description;

    /**
     * 服务器主机地址
     * <p>
     * 数据源服务器的主机地址，用于显示连接目标。
     * </p>
     */
    private String host;
}
