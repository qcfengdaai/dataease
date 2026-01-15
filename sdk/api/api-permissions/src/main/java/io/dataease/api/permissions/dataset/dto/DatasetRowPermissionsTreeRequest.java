package io.dataease.api.permissions.dataset.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据集行权限查询请求对象
 *
 * <p>继承自 DataSetRowPermissionsTreeDTO，在其基础上增加了排序参数。
 * 用于查询行权限列表时指定排序规则。</p>
 *
 * <p>支持按以下字段排序：
 * <ul>
 *   <li>updateTime: 更新时间（默认）</li>
 *   <li>authTargetType: 授权目标类型</li>
 *   <li>datasetName: 数据集名称</li>
 *   <li>enable: 启用状态</li>
 * </ul>
 * </p>
 *
 * @author gin
 * @since 2.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DatasetRowPermissionsTreeRequest extends DataSetRowPermissionsTreeDTO {
    /**
     * 排序字段
     *
     * <p>指定查询结果的排序规则，格式为：字段名 排序方式
     * <ul>
     *   <li>示例："updateTime desc" - 按更新时间倒序</li>
     *   <li>示例："datasetName asc" - 按数据集名称正序</li>
     * </ul>
     * 如果不指定，默认按更新时间倒序排列。
     * </p>
     */
    public String orderBy;
}
