package io.dataease.api.permissions.dataset.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 通用项目对象
 *
 * <p>用于表示授权对象（组织、角色、用户等）的基本信息，
 * 通常作为下拉选择框、树形选择器等组件的数据源。</p>
 *
 * <p>该类是一个通用的键值对封装，可以表示任何需要ID和名称的对象。
 * 在数据集权限管理中常用于表示可授权的对象列表。</p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Data
public class Item {
    /**
     * 对象唯一标识
     * <p>可能代表用户ID、角色ID、组织ID等，具体含义根据使用场景而定</p>
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 对象名称
     *
     * <p>显示给用户的友好名称，如用户名、角色名、部门名等</p>
     */
    private String name;
}
