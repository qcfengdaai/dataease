package io.dataease.model;

import io.dataease.constant.LogST;
import lombok.Data;

import java.io.Serializable;

/**
 * 日志项目模型
 * 用于记录系统操作日志的基本信息
 */
@Data
public class LogItemModel implements Serializable {

    /**
     * 日志项ID
     * 日志记录的唯一标识
     */
    private Long id;

    /**
     * 日志项名称
     * 操作或资源的名称描述
     */
    private String name;

    /**
     * 日志状态
     * 记录操作的执行状态
     */
    private LogST st;
}
