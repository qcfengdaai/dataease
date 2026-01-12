package io.dataease.constant;

import java.util.Arrays;

/**
 * DataEase报告任务状态枚举
 * <p>
 * 定义报告任务在执行过程中的各种状态，用于任务调度、状态跟踪和执行流控制。
 * 支持任务的全生命周期管理，从创建到完成的所有状态变迁。
 * </p>
 *
 * @author fit2cloud
 * @since 1.0
 */
public enum ReportTaskEnum {

    /** 等待执行状态 - 任务已创建但尚未开始执行，标志位:0 */
    WAIT(0),

    /** 发送执行状态 - 任务正在执行或发送过程中，标志位:1 */
    SEND(1),

    /** 停止执行状态 - 任务被手动停止或因错误中断，标志位:2 */
    STOP(2),

    /** 完成执行状态 - 任务成功完成所有执行步骤，标志位:3 */
    FINISH(3);

    private Integer flag;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    ReportTaskEnum(Integer flag) {
        this.flag = flag;
    }

    ReportTaskEnum() {
    }

    /**
     * 根据标志位获取对应的报告任务状态枚举
     *
     * @param flag 任务状态标志位
     * @return 对应的任务状态枚举
     */
    public static ReportTaskEnum fromValue(Integer flag) {
        return Arrays.stream(values()).filter(v -> v.flag.equals(flag)).findFirst().get();
    }
}
