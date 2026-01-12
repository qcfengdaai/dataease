package io.dataease.share.dao.ext.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Xpack分享持久化对象
 *
 * 用于在查询时关联多个表的数据，包含分享信息和可视化资源信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XpackSharePO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7929343371768885789L;

    /** 分享ID */
    private Long shareId;

    /** 资源ID */
    private Long resourceId;

    /** 资源名称 */
    private String name;

    /** 资源类型 */
    private String type;

    /** 创建人ID */
    private Long creator;

    /** 创建时间（时间戳） */
    private Long time;

    /** 过期时间（时间戳） */
    private Long exp;

    /** 扩展标志位1：移动布局配置 */
    private Integer extFlag;

    /** 扩展标志位2：资源状态 */
    private Integer extFlag1;

}
