package io.dataease.visualization.dao.ext.po;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 存储资源持久化对象
 * 用于存储可视化的资源信息
 */
@Data
public class StorePO implements Serializable {
    @Serial
    private static final long serialVersionUID = 9130790627765997999L;

    /** 资源ID */
    private Long resourceId;

    /** 资源类型 */
    private String type;

    /** 创建者ID */
    private Long creator;

    /** 编辑者ID */
    private Long editor;

    /** 编辑时间 */
    private Long editTime;

    /** 存储ID */
    private Long storeId;

    /** 名称 */
    private String name;

    /** 扩展标记 */
    private Integer extFlag;

    /** 扩展标记1 */
    private Integer extFlag1;

}
