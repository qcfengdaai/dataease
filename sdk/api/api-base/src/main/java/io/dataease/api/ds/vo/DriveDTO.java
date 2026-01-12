package io.dataease.api.ds.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据源驱动数据传输对象
 * <p>
 * 用于数据源驱动信息的传输，包含驱动的基本信息、类型、描述等。
 * 在驱动管理功能中用于前后端数据交互。
 * </p>
 *
 * @author DataEase团队
 * @since 1.0.0
 */
@Data
public class DriveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1175287571828910222L;

    /**
     * 驱动ID
     * <p>
     * 驱动的唯一标识符，系统内部使用。
     * </p>
     */
    private Long id;

    /**
     * 驱动名称
     * <p>
     * 驱动程序的显示名称，用户可见的标识名。
     * </p>
     */
    private String name;

    /**
     * 数据源类型
     * <p>
     * 驱动支持的数据源类型标识（如mysql、postgresql、oracle等）。
     * 用于关联具体的数据源类型。
     * </p>
     */
    private String type;

    /**
     * 数据源类型描述
     * <p>
     * 数据源类型的详细描述信息，用于界面展示。
     * </p>
     */
    private String typeDesc;

    /**
     * 驱动描述
     * <p>
     * 驱动的详细描述信息，包括版本、功能特性等。
     * </p>
     */
    private String desc;
}
