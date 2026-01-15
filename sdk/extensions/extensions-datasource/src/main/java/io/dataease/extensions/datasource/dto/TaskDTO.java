package io.dataease.extensions.datasource.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * DataEase任务数据传输对象
 * <p>
 * 用于传输任务相关的配置信息，包括任务调度、同步策略和时间配置等。
 * 主要用于数据源同步任务的创建、修改和管理操作。
 * </p>
 *
 * @author fit2cloud
 * @since 1.0
 */
@Data
public class TaskDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1175287571828910222L;
    /**
     * 任务唯一标识符
     * <p>
     * 任务在系统中的唯一ID，用于标识和管理特定的数据同步任务。
     * 使用ToStringSerializer进行JSON序列化，确保长整型数值的精确传输。
     * </p>
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;
    /**
     * 更新类型
     * <p>
     * 定义数据同步的更新策略，如全量更新、增量更新等。
     * 决定任务执行时如何处理目标数据的更新方式。
     * </p>
     */
    private String updateType;
    /**
     * 同步频率
     * <p>
     * 任务的同步执行频率设置，可能包括定时同步、实时同步等策略。
     * 与调度配置配合使用，控制任务的执行节奏。
     * </p>
     */
    private String syncRate;
    /**
     * 简单调度数值
     * <p>
     * 用于简单调度模式的数值参数，如间隔时间、执行次数等。
     * 与simpleCronType配合使用，定义简化的调度规则。
     * </p>
     */
    private Long simpleCronValue;
    /**
     * 简单调度类型
     * <p>
     * 简化调度模式的类型标识，如按小时、按天、按周等。
     * 为用户提供简单易用的调度配置选项。
     * </p>
     */
    private String simpleCronType;
    /**
     * 任务开始时间
     * <p>
     * 任务计划开始执行的时间戳（毫秒）。
     * 用于设定任务的生效时间，控制任务的启动时机。
     * </p>
     */
    private Long startTime;
    /**
     * 任务结束时间
     * <p>
     * 任务计划停止执行的时间戳（毫秒）。
     * 用于设定任务的失效时间，自动终止任务执行。
     * </p>
     */
    private Long endTime;
    /**
     * 结束限制条件
     * <p>
     * 任务结束的限制条件设置，如执行次数限制、错误次数限制等。
     * 提供灵活的任务终止控制机制。
     * </p>
     */
    private String endLimit;
    /**
     * Cron表达式
     * <p>
     * 标准的Cron调度表达式，用于精确控制任务的执行时间。
     * 支持复杂的调度需求，如特定时间点、周期性执行等。
     * </p>
     */
    private String cron;
}
