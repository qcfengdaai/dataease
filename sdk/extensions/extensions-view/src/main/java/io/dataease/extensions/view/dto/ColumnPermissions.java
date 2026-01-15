package io.dataease.extensions.view.dto;

import lombok.Data;

import java.util.List;

/**
 * 列权限配置DTO
 * 用于配置数据集的列级别权限
 */
@Data
public class ColumnPermissions {
    /** 是否启用列权限 */
    private Boolean enable;

    /** 列权限项列表 */
    private List<ColumnPermissionItem> columns;
}
