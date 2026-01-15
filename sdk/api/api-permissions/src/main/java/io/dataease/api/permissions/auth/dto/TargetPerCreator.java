package io.dataease.api.permissions.auth.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 目标权限构造器基类
 *
 * <p>用于从资源维度或菜单维度批量授权的基础类，定义了要授权的目标对象 ID 列表。
 * 该类是 BusiTargetPerCreator 和 MenuTargetPerCreator 的父类。</p>
 *
 * <p><strong>目标维度授权：</strong></p>
 * <ul>
 *   <li>从资源角度出发，为单个或多个资源批量授权给多个对象</li>
 *   <li>适用于快速配置新资源的访问权限</li>
 *   <li>适用于批量调整多个对象对资源的访问权限</li>
 * </ul>
 *
 * <p><strong>与对象维度授权的区别：</strong></p>
 * <ul>
 *   <li>对象维度：为一个对象配置对多个资源的权限</li>
 *   <li>目标维度：为一个或多个资源配置多个对象的权限</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0
 */
@Schema(description = "权限构造器")
@Data
public class TargetPerCreator implements Serializable {

    @Serial
    private static final long serialVersionUID = 6469957337188015981L;

    /**
     * 目标对象 ID 列表
     * <p>要被授权的对象 ID 集合，可以是用户 ID、角色 ID 或组织 ID 的列表。
     * 使用 Long 类型存储，前端序列化为字符串避免精度丢失。</p>
     *
     * <p><strong>使用场景：</strong></p>
     * <ul>
     *   <li>为多个用户授予资源访问权限</li>
     *   <li>为多个角色配置资源权限</li>
     *   <li>为多个组织开放资源访问</li>
     * </ul>
     *
     * <p><strong>注意事项：</strong></p>
     * <ul>
     *   <li>列表不能为空</li>
     *   <li>ID 不能重复</li>
     *   <li>所有 ID 对应的对象必须存在</li>
     *   <li>所有对象的类型必须一致（全部是用户、角色或组织）</li>
     * </ul>
     */
    @JsonSerialize(using= ToStringSerializer.class)
    @Schema(description = "权限ID集合", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> ids;
}
