package io.dataease.api.permissions.relation.api;

import io.dataease.exception.DEException;

/**
 * 关系管理 API
 * <p>
 * 提供用户、角色、组织与业务资源之间关联关系的查询和权限校验功能。
 * 主要用于权限体系中的资源关联和授权检查。
 * <p>
 * 核心功能：
 * <ul>
 *   <li>获取数据源与组织的关联关系</li>
 *   <li>获取数据集与组织的关联关系</li>
 *   <li>权限校验和授权检查</li>
 * </ul>
 * <p>
 * 关系类型说明：
 * <ul>
 *   <li>用户-组织关系：用户所属的组织单位</li>
 *   <li>角色-权限关系：角色拥有的权限集合</li>
 *   <li>资源-组织关系：业务资源（数据源、数据集等）与组织的归属关系</li>
 * </ul>
 *
 * @author Junjun
 * @since 1.0
 */
public interface RelationApi {

    /**
     * 获取数据源的资源 ID
     * <p>
     * 根据数据源 ID 查询其关联的组织资源 ID。
     * 用于权限判断时确定数据源所属的组织范围。
     * <p>
     * 应用场景：
     * <ul>
     *   <li>判断用户是否有权限访问该数据源（通过组织权限）</li>
     *   <li>数据源列表过滤（只显示用户有权限的组织下的数据源）</li>
     *   <li>数据源授权时的组织范围确定</li>
     * </ul>
     *
     * @param id 数据源 ID
     * @return 数据源关联的组织资源 ID，如果未关联则返回 null
     */
    Long getDsResource(Long id);

    /**
     * 获取数据集的资源 ID
     * <p>
     * 根据数据集 ID 查询其关联的组织资源 ID。
     * 用于权限判断时确定数据集所属的组织范围。
     * <p>
     * 应用场景：
     * <ul>
     *   <li>判断用户是否有权限访问该数据集（通过组织权限）</li>
     *   <li>数据集列表过滤（只显示用户有权限的组织下的数据集）</li>
     *   <li>数据集授权时的组织范围确定</li>
     * </ul>
     *
     * @param id 数据集 ID
     * @return 数据集关联的组织资源 ID，如果未关联则返回 null
     */
    Long getDatasetResource(Long id);

    /**
     * 权限校验
     * <p>
     * 执行权限检查，验证当前用户是否具有执行某项操作的权限。
     * 如果权限校验失败，会抛出 DEException 异常。
     * <p>
     * 使用场景：
     * <ul>
     *   <li>在执行敏感操作前进行权限预检查</li>
     *   <li>自定义权限逻辑的校验入口</li>
     *   <li>API 调用前的统一权限验证</li>
     * </ul>
     * <p>
     * 注意：具体的权限校验逻辑由实现类决定，可能包括：
     * <ul>
     *   <li>用户身份验证</li>
     *   <li>角色权限检查</li>
     *   <li>资源访问权限确认</li>
     * </ul>
     *
     * @throws DEException 权限校验失败时抛出异常，包含具体的错误信息
     */
    void checkAuth() throws DEException;
}
