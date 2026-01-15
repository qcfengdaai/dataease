package io.dataease.api.permissions.auth.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.dataease.model.TreeResultModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 资源节点视图对象
 *
 * <p>表示权限系统中的资源树节点，用于展示业务资源或菜单的树形结构。
 * 该对象实现了树形结构接口，支持递归展示资源的层级关系。</p>
 *
 * <p><strong>资源类型：</strong></p>
 * <ul>
 *   <li>业务资源：数据集、仪表板、数据源、文件夹等</li>
 *   <li>菜单资源：系统功能菜单项</li>
 * </ul>
 *
 * <p><strong>节点类型：</strong></p>
 * <ul>
 *   <li>容器节点：可以包含子节点的文件夹或菜单组</li>
 *   <li>叶子节点：不能包含子节点的实际资源</li>
 * </ul>
 *
 * <p><strong>应用场景：</strong></p>
 * <ul>
 *   <li>权限授予界面的资源树</li>
 *   <li>资源选择器</li>
 *   <li>菜单权限配置</li>
 *   <li>组织资源视图</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0
 */
@Data
@Schema(description = "资源结点")
public class ResourceVO implements TreeResultModel<ResourceVO>, Serializable {

    @Serial
    private static final long serialVersionUID = -8523999682424087399L;

    /**
     * 资源的唯一标识符
     * <p>使用 Long 类型存储，前端序列化为字符串避免精度丢失</p>
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "ID")
    private Long id;

    /**
     * 资源名称
     * <p>用于界面显示的资源名称，如"销售数据集"、"财务报表"等</p>
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 子节点列表
     * <p>当前资源节点的直接子节点集合，用于构建树形结构。
     * 如果为叶子节点则为空列表或 null。</p>
     */
    @Schema(description = "子节点")
    private List<ResourceVO> children;

    /**
     * 是否为叶子节点
     * <ul>
     *   <li>true: 叶子节点，不能包含子节点（如数据集、仪表板）</li>
     *   <li>false: 容器节点，可以包含子节点（如文件夹、菜单组）</li>
     * </ul>
     * <p>默认值为 false</p>
     */
    @Schema(description = "叶子节点")
    private boolean leaf = false;

    /**
     * 额外标识
     * <p>用于标记资源的特殊状态或属性的扩展字段。具体含义由业务模块定义，
     * 例如：是否公开、是否启用、是否有权限等。</p>
     * <p>默认值为 0</p>
     */
    @Schema(description = "额外标识")
    private Integer extraFlag = 0;
}
