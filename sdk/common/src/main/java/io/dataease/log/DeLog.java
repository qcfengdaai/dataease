package io.dataease.log;

import io.dataease.constant.LogOT;
import io.dataease.constant.LogST;

import java.lang.annotation.*;

/**
 * DataEase操作日志注解
 * <p>
 * 用于标记需要记录操作日志的方法，支持AOP切面自动记录用户操作行为。
 * 通过此注解可以自动记录用户在系统中的各种操作，包括创建、修改、删除等行为，
 * 便于系统审计、安全监控和操作追踪。
 * </p>
 *
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li>用户管理操作（创建、修改、删除用户）</li>
 *   <li>数据源管理（连接、测试、配置数据源）</li>
 *   <li>数据集操作（创建、更新、删除数据集）</li>
 *   <li>仪表板管理（创建、编辑、分享仪表板）</li>
 *   <li>权限管理（授权、取消授权）</li>
 *   <li>文件操作（上传、下载、导出）</li>
 *   <li>任务管理（启用、禁用、立即执行定时任务）</li>
 * </ul>
 *
 * <p><b>注解参数说明：</b></p>
 * <ul>
 *   <li><b>id</b> - 操作对象的ID，用于标识具体操作的资源</li>
 *   <li><b>pid</b> - 父级对象ID，用于标识操作对象的父级资源</li>
 *   <li><b>st</b> - 资源类型，使用{@link LogST}枚举定义（如数据源、数据集、仪表板等）</li>
 *   <li><b>ot</b> - 操作类型，使用{@link LogOT}枚举定义（如创建、修改、删除等），必填参数</li>
 *   <li><b>stExp</b> - 资源类型表达式，支持SpEL表达式动态计算资源类型</li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeLog {
    /**
     * 操作对象的主键ID
     * <p>
     * 用于标识本次操作涉及的具体资源对象，如用户ID、数据源ID、数据集ID等。
     * 支持SpEL表达式，可以从方法参数或返回值中动态获取。
     * </p>
     *
     * @return 操作对象ID，支持SpEL表达式
     */
    String id() default "";

    /**
     * 操作对象的父级ID
     * <p>
     * 用于标识操作对象所属的父级资源，如数据集所属的数据源ID、
     * 图表所属的仪表板ID等，建立操作日志的层级关系。
     * 支持SpEL表达式动态计算。
     * </p>
     *
     * @return 父级对象ID，支持SpEL表达式
     */
    String pid() default "";

    /**
     * 资源类型
     * <p>
     * 标识本次操作涉及的资源类型，如仪表板、数据源、数据集等。
     * 使用{@link LogST}枚举中定义的资源类型。
     * 默认为PANEL（仪表板）。
     * </p>
     *
     * @return 资源类型枚举值
     */
    LogST st() default LogST.PANEL;

    /**
     * 操作类型（必填）
     * <p>
     * 标识本次操作的具体行为类型，如创建、修改、删除等。
     * 使用{@link LogOT}枚举中定义的操作类型。
     * 这是必填参数，必须明确指定操作类型。
     * </p>
     *
     * @return 操作类型枚举值，必填
     */
    LogOT ot();

    /**
     * 资源类型表达式
     * <p>
     * 支持使用SpEL表达式动态计算资源类型，当静态的st()参数无法满足需求时使用。
     * 可以根据方法参数、返回值或其他上下文信息动态确定资源类型。
     * 表达式的计算结果应该是{@link LogST}枚举值。
     * </p>
     *
     * @return SpEL表达式字符串，用于动态计算资源类型
     */
    String stExp() default "";
}
