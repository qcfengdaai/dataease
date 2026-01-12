package io.dataease.auth;

import io.dataease.constant.AuthResourceEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * DataEase API路径定义注解
 * <p>
 * 用于Controller类级别，定义该Controller管理的资源路径和资源类型。
 * 配合{@link DePermit}注解实现完整的权限控制体系。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>定义API路径前缀 - 标识Controller的基础路径</li>
 *   <li>声明资源类型 - 指定该Controller管理的资源类型（数据集、图表等）</li>
 *   <li>权限元数据 - 为权限系统提供资源分类信息</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>DatasetController - 数据集管理API</li>
 *   <li>ChartController - 图表管理API</li>
 *   <li>PanelController - 仪表板管理API</li>
 *   <li>DatasourceController - 数据源管理API</li>
 *   <li>所有需要权限控制的Controller</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：数据集Controller
 * {@literal @}RestController
 * {@literal @}DeApiPath(value = "/api/dataset", rt = AuthResourceEnum.DATASET)
 * {@literal @}RequestMapping("/api/dataset")
 * public class DatasetController {
 *     // 该Controller管理数据集资源，路径前缀为/api/dataset
 *
 *     {@literal @}DePermit("#p0.id + ':read'")
 *     {@literal @}GetMapping("/{id}")
 *     public Dataset getDataset(@PathVariable Long id) {
 *         return datasetService.getById(id);
 *     }
 * }
 *
 * // 示例2：仪表板Controller
 * {@literal @}RestController
 * {@literal @}DeApiPath(path = {"/api/panel", "/api/dashboard"}, rt = AuthResourceEnum.PANEL)
 * public class PanelController {
 *     // 支持多个路径别名
 * }
 *
 * // 示例3：系统管理Controller
 * {@literal @}RestController
 * {@literal @}DeApiPath("/api/system", rt = AuthResourceEnum.SYSTEM)
 * public class SystemController {
 *     // 系统管理类资源
 * }
 * </pre>
 *
 * <p><b>与DePermit配合使用：</b></p>
 * <pre>
 * {@literal @}DeApiPath(value = "/api/chart", rt = AuthResourceEnum.CHART)
 * {@literal @}RestController
 * public class ChartController {
 *
 *     // 读取权限检查
 *     {@literal @}DePermit("#p0.id + ':read'")
 *     {@literal @}GetMapping("/{id}")
 *     public Chart getChart(@PathVariable Long id) {...}
 *
 *     // 编辑权限检查
 *     {@literal @}DePermit("#p0.id + ':edit'")
 *     {@literal @}PutMapping("/{id}")
 *     public void updateChart(@PathVariable Long id, @RequestBody ChartRequest req) {...}
 *
 *     // 删除权限检查
 *     {@literal @}DePermit("#p0.id + ':delete'")
 *     {@literal @}DeleteMapping("/{id}")
 *     public void deleteChart(@PathVariable Long id) {...}
 * }
 * </pre>
 *
 * <p><b>资源类型（AuthResourceEnum）：</b></p>
 * <ul>
 *   <li>DATASET - 数据集资源</li>
 *   <li>CHART - 图表资源</li>
 *   <li>PANEL - 仪表板资源</li>
 *   <li>DATASOURCE - 数据源资源</li>
 *   <li>SYSTEM - 系统管理资源</li>
 *   <li>USER - 用户资源</li>
 *   <li>ROLE - 角色资源</li>
 * </ul>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>必须配合{@code @RestController}或{@code @Controller}使用</li>
 *   <li>path和value互为别名，使用其一即可</li>
 *   <li>支持多个路径（数组形式），用于API版本兼容</li>
 *   <li>rt（资源类型）是必填项，用于权限分类管理</li>
 *   <li>该注解会被权限系统扫描，构建权限树</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see DePermit
 * @see io.dataease.constant.AuthResourceEnum
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface DeApiPath {

    /**
     * API路径（value的别名）
     * <p>
     * 定义该Controller管理的API路径前缀，支持多个路径
     * </p>
     *
     * @return API路径数组
     */
    @AliasFor("path")
    String[] value() default {};

    /**
     * API路径（与value互为别名）
     * <p>
     * 定义该Controller管理的API路径前缀，支持多个路径
     * </p>
     *
     * <p><b>示例：</b></p>
     * <ul>
     *   <li><code>"/api/dataset"</code> - 单个路径</li>
     *   <li><code>{"/api/v1/dataset", "/api/dataset"}</code> - 多个路径（版本兼容）</li>
     * </ul>
     *
     * @return API路径数组
     */
    @AliasFor("value")
    String[] path() default {};

    /**
     * 资源类型（Resource Type）
     * <p>
     * 定义该Controller管理的资源类型，用于权限分类和管理
     * </p>
     *
     * <p><b>常用资源类型：</b></p>
     * <ul>
     *   <li>{@code AuthResourceEnum.DATASET} - 数据集</li>
     *   <li>{@code AuthResourceEnum.CHART} - 图表</li>
     *   <li>{@code AuthResourceEnum.PANEL} - 仪表板</li>
     *   <li>{@code AuthResourceEnum.DATASOURCE} - 数据源</li>
     *   <li>{@code AuthResourceEnum.SYSTEM} - 系统管理</li>
     * </ul>
     *
     * @return 资源类型枚举
     */
    AuthResourceEnum rt();
}
