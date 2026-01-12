package io.dataease.api.permissions.auth.api;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.permissions.auth.dto.*;
import io.dataease.api.permissions.auth.vo.PermissionValVO;
import io.dataease.api.permissions.auth.vo.ResourceNodeVO;
import io.dataease.model.BusiNodeRequest;
import io.dataease.model.BusiNodeVO;
import io.dataease.model.ExportTaskDTO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 内部资源交互 API 接口
 *
 * <p>提供系统内部模块之间的权限资源交互功能，主要用于服务间的权限数据同步、
 * 权限校验和资源管理。此接口不对外开放，仅供系统内部调用。</p>
 *
 * <p><strong>主要功能模块：</strong></p>
 * <ul>
 *   <li><strong>资源同步：</strong>业务模块资源的创建、编辑、删除同步到权限系统</li>
 *   <li><strong>权限校验：</strong>提供快速的权限检查和权限查询接口</li>
 *   <li><strong>资源查询：</strong>查询资源树、菜单列表等基础数据</li>
 *   <li><strong>批量授权：</strong>支持批量授权操作提升性能</li>
 *   <li><strong>资源移动：</strong>处理资源移动时的权限继承和调整</li>
 * </ul>
 *
 * <p><strong>设计原则：</strong></p>
 * <ul>
 *   <li>所有接口均为内部调用，不暴露给前端</li>
 *   <li>通过 Feign 或内部 RPC 调用</li>
 *   <li>不进行额外的权限验证（由调用方负责）</li>
 *   <li>保证数据一致性和性能</li>
 * </ul>
 *
 * @author fit2cloud-someone
 * @since 1.0
 */
@Tag(name = "内部资源交互")
@ApiSupport(order = 998)
@Hidden
public interface InteractiveAuthApi {

    /**
     * 查询当前用户可访问的菜单 ID 列表
     *
     * <p>获取当前登录用户被授权访问的所有菜单 ID，用于前端动态构建菜单树
     * 和控制页面显示权限。</p>
     *
     * @return 菜单 ID 列表
     */
    @Operation(summary = "查询菜单ID")
    @ApiOperationSupport(order = 1)
    @GetMapping("/menuIds")
    List<Long> menuIds();

    /**
     * 查询业务资源节点树
     *
     * <p>根据请求条件查询业务资源的节点信息，支持按类型、父节点等条件筛选。
     * 返回树形结构的业务节点数据，用于资源选择器等场景。</p>
     *
     * @param request 业务节点查询请求，包含查询条件
     * @return 业务节点列表，按树形结构组织
     */
    @Operation(summary = "查询资源树")
    @ApiOperationSupport(order = 2)
    @PostMapping("/resource")
    List<BusiNodeVO> resource(@RequestBody BusiNodeRequest request);

    /**
     * 同步保存业务资源到权限系统
     *
     * <p>当业务模块创建新资源（如数据集、仪表板等）时，调用此接口将资源信息
     * 同步到权限系统，为后续的权限管理奠定基础。</p>
     *
     * <p><strong>同步时机：</strong></p>
     * <ul>
     *   <li>创建数据集后立即调用</li>
     *   <li>创建仪表板后立即调用</li>
     *   <li>创建文件夹后立即调用</li>
     * </ul>
     *
     * <p><strong>同步内容：</strong></p>
     * <ul>
     *   <li>资源 ID、名称、类型</li>
     *   <li>资源父节点关系</li>
     *   <li>资源额外标识（如是否为叶子节点）</li>
     * </ul>
     *
     * @param creator 业务资源创建器，包含资源的完整信息
     */
    @Operation(summary = "同步保存资源")
    @ApiOperationSupport(order = 3)
    @PostMapping("/resource/create")
    void saveResource(@RequestBody BusiResourceCreator creator);

    /**
     * 同步更新业务资源信息
     *
     * <p>当业务模块修改资源信息（如重命名、修改状态等）时，调用此接口将变更
     * 同步到权限系统，保持数据一致性。</p>
     *
     * <p>支持更新的字段：
     * <ul>
     *   <li>资源名称</li>
     *   <li>资源额外标识</li>
     *   <li>其他扩展字段</li>
     * </ul>
     * </p>
     *
     * <p><strong>注意：</strong>资源 ID 和类型不可修改。</p>
     *
     * @param editor 业务资源编辑器，包含要更新的资源信息
     */
    @Operation(summary = "同步更新资源")
    @ApiOperationSupport(order = 4)
    @PostMapping("/resource/edit")
    void editResource(@RequestBody BusiResourceEditor editor);

    /**
     * 同步删除业务资源
     *
     * <p>当业务模块删除资源时，调用此接口将资源从权限系统中移除，
     * 同时清理该资源相关的所有权限记录。</p>
     *
     * <p><strong>删除影响：</strong></p>
     * <ul>
     *   <li>删除资源的权限记录</li>
     *   <li>删除资源的授权关系</li>
     *   <li>如果是文件夹，递归删除子资源</li>
     * </ul>
     *
     * <p><strong>注意：</strong>此操作不可恢复，请确保业务侧已完成删除检查。</p>
     *
     * @param id 资源 ID
     */
    @Operation(summary = "同步删除资源")
    @ApiOperationSupport(order = 5)
    @GetMapping("/resource/del/{id}")
    void delResource(@PathVariable("id") Long id);

    /**
     * 检查资源是否可以删除
     *
     * <p>在删除资源前进行检查，判断该资源是否被其他对象引用或依赖。
     * 用于避免删除正在使用的资源导致系统异常。</p>
     *
     * <p>检查项：
     * <ul>
     *   <li>是否存在子资源</li>
     *   <li>是否被其他资源引用</li>
     *   <li>是否有活跃的权限分配</li>
     * </ul>
     * </p>
     *
     * @param id 资源 ID
     * @return true 表示可以删除，false 表示不能删除
     */
    @Operation(summary = "删除检测")
    @ApiOperationSupport(order = 6)
    @GetMapping("/resource/checkDel/{id}")
    boolean checkDel(@PathVariable("id") Long id);

    /**
     * 移动资源到新的父节点
     *
     * <p>处理资源移动操作，更新资源的父子关系。移动资源时需要重新计算权限继承，
     * 确保移动后的权限设置符合新的层级关系。</p>
     *
     * <p><strong>移动规则：</strong></p>
     * <ul>
     *   <li>不能移动到自己的子节点下</li>
     *   <li>移动后继承新父节点的权限设置</li>
     *   <li>保留资源原有的直接授权</li>
     * </ul>
     *
     * <p><strong>权限处理：</strong></p>
     * <ul>
     *   <li>检查新父节点的权限</li>
     *   <li>合并或覆盖权限配置</li>
     *   <li>递归处理子资源的权限</li>
     * </ul>
     *
     * @param mover 资源移动器，包含资源 ID 和新父节点 ID
     */
    @Operation(summary = "移动资源")
    @ApiOperationSupport(order = 7)
    @PostMapping("/moveResource")
    void moveResource(@RequestBody BusiResourceMover mover);

    /**
     * 校验当前用户对资源的权限
     *
     * <p>检查当前登录用户是否具有访问指定资源的权限。如果没有权限，会抛出权限异常。
     * 此方法用于在业务操作前进行权限验证。</p>
     *
     * <p>校验维度：
     * <ul>
     *   <li>用户的直接权限</li>
     *   <li>用户所属角色的权限</li>
     *   <li>用户所属组织的权限</li>
     *   <li>继承的父节点权限</li>
     * </ul>
     * </p>
     *
     * <p><strong>使用场景：</strong></p>
     * <ul>
     *   <li>访问数据集前验证读取权限</li>
     *   <li>编辑仪表板前验证编辑权限</li>
     *   <li>删除资源前验证管理权限</li>
     * </ul>
     *
     * @param checkDTO 权限检查参数，包含资源 ID 和所需权限级别
     * @throws io.dataease.exception.DEException 如果权限不足
     */
    @Operation(summary = "权限校验")
    @ApiOperationSupport(order = 8)
    @PostMapping("/checkAuth")
    void checkAuth(@RequestBody BusiPerCheckDTO checkDTO);

    /**
     * 查询用户对指定资源的权限值
     *
     * <p>查询当前用户对指定资源拥有的权限值，包括权限权重和扩展权限。
     * 不抛出异常，只返回权限信息，由调用方决定如何处理。</p>
     *
     * <p>返回信息：
     * <ul>
     *   <li>weight: 权限权重（0-15，对应无权限、读取、编辑、管理等）</li>
     *   <li>ext: 扩展权限标识</li>
     * </ul>
     * </p>
     *
     * @param id 资源 ID
     * @return 权限值对象，包含权重和扩展权限信息
     */
    @Operation(summary = "权限查询")
    @ApiOperationSupport(order = 9)
    @PostMapping("/queryAuth/{id}")
    PermissionValVO queryAuth(@PathVariable("id") Long id);

    /**
     * 查询从指定资源到根节点的路径
     *
     * <p>查询指定资源到根节点的完整路径，返回路径上的所有节点信息。
     * 用于面包屑导航、权限继承分析等场景。</p>
     *
     * @param id 资源 ID
     * @param flag 资源类型标识
     * @param logOT 日志操作类型（可选）
     * @return 从资源到根节点的路径节点列表
     */
    @GetMapping("/query2Root/{id}/{flag}/{logOT}")
    List<ResourceNodeVO> query2Root(@PathVariable("id") Long id, @PathVariable("flag") Integer flag, Integer logOT);

    /**
     * 检查权限系统是否为空
     *
     * <p>检查权限系统中是否存在任何资源或权限配置。
     * 用于系统初始化判断或数据清理确认。</p>
     *
     * @return true 表示权限系统为空，false 表示存在数据
     */
    @GetMapping("/checkEmpty")
    boolean checkEmpty();

    /**
     * 获取资源所属组织的名称
     *
     * <p>查询指定资源所属的组织名称，用于导出任务等场景中
     * 标识资源的归属信息。</p>
     *
     * @param exportTaskDTO 导出任务参数，包含资源信息
     * @return 组织名称
     */
    @GetMapping("/OrgNameForResource")
    String OrgNameForResource(ExportTaskDTO exportTaskDTO);

    /**
     * 编辑资源的额外标识
     *
     * <p>更新资源的扩展标识字段，用于标记资源的特殊状态或属性。
     * 这些标识不影响核心业务逻辑，主要用于辅助功能。</p>
     *
     * @param editor 业务资源编辑器，包含要更新的额外标识
     */
    void editResourceExtraFlag(BusiResourceEditor editor);

    /**
     * 批量授权
     *
     * <p>批量为多个资源授予权限，提高大量权限配置时的性能。
     * 支持一次性为多个资源配置相同或不同的权限。</p>
     *
     * <p><strong>应用场景：</strong></p>
     * <ul>
     *   <li>初始化系统时批量配置权限</li>
     *   <li>组织架构调整后批量更新权限</li>
     *   <li>角色变更后批量调整资源访问权限</li>
     * </ul>
     *
     * <p><strong>性能优化：</strong></p>
     * <ul>
     *   <li>使用批量插入减少数据库交互</li>
     *   <li>支持事务回滚保证数据一致性</li>
     *   <li>异步处理大批量授权任务</li>
     * </ul>
     *
     * @param request 批量授权请求，包含资源列表和权限配置
     */
    @PostMapping("/batchAuthorize")
    void batchAuthorize(@RequestBody BusiBatchAuthorizeRequest request);

    /**
     * 权限回滚
     *
     * <p>回滚权限配置到之前的状态，用于权限配置错误后的恢复操作。
     * 此操作需要谨慎使用，仅在特殊情况下调用。</p>
     *
     * <p><strong>警告：</strong>此操作可能影响大量用户的访问权限，使用前请确认。</p>
     */
    @Hidden
    @PostMapping("/revert")
    void revert();
}
