package io.dataease.visualization.dao.ext.po;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 可视化资源持久化对象
 * 用于存储可视化资源的详细信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisualizationResourcePO implements Serializable {
    @Serial
    private static final long serialVersionUID = 627770173259978185L;

    /** ID(使用字符串序列化避免前端精度丢失) */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 资源ID(使用字符串序列化避免前端精度丢失) */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resourceId;

    /** 资源名称 */
    private String name;

    /** 资源类型 */
    private String type;

    /** 创建者ID */
    private Long creator;

    /** 最后编辑者ID */
    private Long lastEditor;

    /** 最后编辑时间 */
    private Long lastEditTime;

    /** 是否收藏 */
    private Boolean favorite;

    /** 权重(用于排序) */
    private int weight;

    /** 扩展标记 */
    private Integer extFlag;
}
