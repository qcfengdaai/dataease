package io.dataease.api.permissions.relation.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * 关系数据传输对象
 * <p>
 * 用于表示用户、角色、组织与资源之间的关联关系。
 * 采用树形结构，通过 subRelation 字段支持层级关系的表达。
 * <p>
 * 主要用途：
 * <ul>
 *   <li>展示用户与组织的关联关系</li>
 *   <li>展示角色的权限配置关系</li>
 *   <li>展示资源的授权关系</li>
 *   <li>构建权限树或组织树</li>
 * </ul>
 * <p>
 * 关系类型示例：
 * <ul>
 *   <li>user-org：用户所属的组织</li>
 *   <li>role-resource：角色对资源的权限</li>
 *   <li>org-resource：组织对资源的管理权</li>
 * </ul>
 *
 * @author Junjun
 * @since 1.0
 */
@Data
public class RelationDTO {

    /**
     * 关系 ID
     * <p>
     * 关系记录的唯一标识。
     * 使用 ToStringSerializer 序列化为字符串，避免 JavaScript 精度问题。
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 名称
     * <p>
     * 关系对象的名称，可能是：
     * <ul>
     *   <li>用户名称</li>
     *   <li>组织名称</li>
     *   <li>角色名称</li>
     *   <li>资源名称</li>
     * </ul>
     * 根据 type 字段确定具体含义。
     */
    private String name;

    /**
     * 权限标识
     * <p>
     * 表示该关系拥有的权限集合，通常为权限代码的组合字符串。
     * <p>
     * 格式示例：
     * <ul>
     *   <li>"read"：只读权限</li>
     *   <li>"read,write"：读写权限</li>
     *   <li>"manage"：管理权限</li>
     *   <li>"read,write,delete,manage"：完整权限</li>
     * </ul>
     * <p>
     * 具体的权限代码定义取决于业务模块。
     */
    private String auths;

    /**
     * 关系类型
     * <p>
     * 标识关系的类型，用于区分不同的关联关系。
     * <p>
     * 常见类型：
     * <ul>
     *   <li>user：用户类型</li>
     *   <li>role：角色类型</li>
     *   <li>org：组织类型</li>
     *   <li>resource：资源类型</li>
     *   <li>datasource：数据源类型</li>
     *   <li>dataset：数据集类型</li>
     * </ul>
     */
    private String type;

    /**
     * 创建者
     * <p>
     * 创建该关系的用户标识，通常为用户名或用户 ID。
     * 用于追踪关系的建立者，便于审计和问题排查。
     */
    private String creator;

    /**
     * 更新时间
     * <p>
     * 关系最后更新的时间戳（毫秒）。
     * 用于显示关系的最新变更时间，便于跟踪关系的变化历史。
     */
    private Long updateTime;

    /**
     * 子关系列表
     * <p>
     * 当前关系的下级关系集合，采用递归结构。
     * 用于构建关系树：
     * <ul>
     *   <li>组织树：组织-子组织关系</li>
     *   <li>权限树：资源-子资源关系</li>
     *   <li>用户树：用户-角色-权限关系</li>
     * </ul>
     * <p>
     * 如果没有子关系，则为 null 或空列表。
     */
    private List<RelationDTO> subRelation;
}
