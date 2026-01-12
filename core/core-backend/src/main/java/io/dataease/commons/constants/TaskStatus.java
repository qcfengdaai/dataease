package io.dataease.commons.constants;

/**
 * 任务状态枚举
 * 定义系统中各种任务的执行状态，用于统一管理任务的生命周期状态
 *
 * <p>适用场景：</p>
 * <ul>
 *   <li>数据同步任务</li>
 *   <li>定时调度任务</li>
 *   <li>数据处理任务</li>
 *   <li>导入导出任务</li>
 *   <li>其他异步执行任务</li>
 * </ul>
 *
 * <p>状态流转：</p>
 * <pre>
 * WaitingForExecution -> UnderExecution -> [Completed|Error|Warning]
 *                     -> Stopped
 *                     -> Suspend -> UnderExecution
 * </pre>
 */
public enum TaskStatus {

    /**
     * 等待执行
     * 任务已创建但尚未开始执行，处于队列等待状态
     */
    WaitingForExecution,

    /**
     * 停止
     * 任务被手动停止或因其他原因终止执行
     */
    Stopped,

    /**
     * 暂停
     * 任务暂时暂停执行，可以恢复继续执行
     */
    Suspend,

    /**
     * 执行中
     * 任务正在执行过程中
     */
    UnderExecution,

    /**
     * 完成
     * 任务成功完成执行，所有操作都正常结束
     */
    Completed,

    /**
     * 错误
     * 任务执行过程中出现错误，导致执行失败
     */
    Error,

    /**
     * 警告
     * 任务执行完成但存在警告信息，可能需要关注
     */
    Warning
}
