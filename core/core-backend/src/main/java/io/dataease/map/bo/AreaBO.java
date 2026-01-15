package io.dataease.map.bo;

import io.dataease.map.dao.auto.entity.Area;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 区域业务对象
 *
 * 功能描述：
 * 继承自Area实体类，扩展了custom标识字段，用于区分系统内置区域和用户自定义区域
 *
 * 使用场景：
 * - 在构建地图树时，需要标识哪些是系统区域，哪些是用户自定义区域
 * - 前端展示时可能需要对自定义区域做特殊处理
 *
 * @author DataEase
 * @since 2023-07-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AreaBO extends Area implements Serializable {

    /**
     * 是否为自定义区域
     * true: 用户自定义的区域
     * false: 系统内置的区域
     */
    private boolean custom = false;
}
