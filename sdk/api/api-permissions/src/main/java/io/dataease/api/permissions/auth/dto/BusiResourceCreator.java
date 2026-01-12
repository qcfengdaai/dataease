package io.dataease.api.permissions.auth.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 业务资源创建器
 *
 * <p>用于将业务模块新建的资源同步到权限系统。当业务模块创建数据集、仪表板等资源时，
 * 需要调用权限服务的资源同步接口，将资源信息注册到权限系统中，以便后续进行权限管理。</p>
 *
 * <p><strong>同步时机：</strong></p>
 * <ul>
 *   <li>创建数据集后立即同步</li>
 *   <li>创建仪表板后立即同步</li>
 *   <li>创建文件夹后立即同步</li>
 *   <li>其他需要权限控制的资源创建后同步</li>
 * </ul>
 *
 * <p><strong>同步内容：</strong></p>
 * <ul>
 *   <li>资源的基本信息（ID、名称、类型）</li>
 *   <li>资源的层级关系（父节点 ID）</li>
 *   <li>资源的特性标识（是否叶子节点、额外标识等）</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * BusiResourceCreator creator = new BusiResourceCreator();
 * creator.setId(datasetId);           // 数据集ID
 * creator.setName("销售数据集");       // 资源名称
 * creator.setFlag("dataset");         // 资源类型
 * creator.setPid(folderId);           // 父文件夹ID
 * creator.setLeaf(true);              // 叶子节点
 * creator.setExtraFlag(0);            // 额外标识
 *
 * authApi.saveResource(creator);      // 同步到权限系统
 * </pre>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "业务资源创建器")
@Data
public class BusiResourceCreator implements Serializable {
    @Serial
    private static final long serialVersionUID = 3656747026193567757L;

    /**
     * 资源 ID
     * <p>业务资源的唯一标识符，由业务模块生成。
     * 使用 Long 类型存储，前端序列化为字符串避免精度丢失。</p>
     */
    @Schema(description = "资源ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 资源名称
     * <p>资源的显示名称，用于权限管理界面展示。
     * 例如："销售数据集"、"财务仪表板"、"数据文件夹"等。</p>
     */
    @Schema(description = "资源名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 资源类型标识
     * <p>标识资源的类型，用于区分不同类别的业务资源：</p>
     * <ul>
     *   <li>dataset: 数据集</li>
     *   <li>dashboard: 仪表板</li>
     *   <li>datasource: 数据源</li>
     *   <li>folder: 文件夹</li>
     *   <li>其他业务自定义类型</li>
     * </ul>
     */
    @Schema(description = "资源类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String flag;

    /**
     * 父节点 ID
     * <p>资源所属的父节点 ID，用于构建资源的树形结构。
     * 如果为顶级资源则为 0 或 null。</p>
     *
     * <p>使用 Long 类型存储，前端序列化为字符串避免精度丢失。
     * 默认值为 0L 表示顶级节点。</p>
     */
    @Schema(description = "父节点ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long pid = 0L;

    /**
     * 是否为叶子节点
     * <p>标识该资源是否可以包含子节点：</p>
     * <ul>
     *   <li>true: 叶子节点，不能包含子节点（如数据集、仪表板）</li>
     *   <li>false: 容器节点，可以包含子节点（如文件夹）</li>
     * </ul>
     *
     * <p>叶子节点不能作为其他资源的父节点。</p>
     */
    @Schema(description = "是否叶子节点")
    private Boolean leaf;

    /**
     * 额外标识 1
     * <p>用于标记资源的特殊状态或属性的扩展字段。具体含义由业务模块定义，
     * 例如：是否公开、是否启用、是否有权限等。</p>
     */
    @Schema(description = "额外标识1")
    private int extraFlag;

    /**
     * 额外标识 2
     * <p>第二个扩展标识字段，用于支持更多的资源状态标记。
     * 具体含义由业务模块定义。</p>
     */
    @Schema(description = "额外标识2")
    private int extraFlag1;
}
