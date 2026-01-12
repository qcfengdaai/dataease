package io.dataease.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 导出任务DTO
 * 用于传输数据导出任务的状态和进度信息
 */
@Data
public class ExportTaskDTO  {
    /**
     * 任务ID
     * 导出任务的唯一标识
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private String id;

    /**
     * 用户ID
     * 发起导出任务的用户
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long userId;

    /**
     * 文件名
     * 导出文件的名称
     */
    private String fileName;

    /**
     * 文件大小
     * 导出文件的大小数值
     */
    private Double fileSize;

    /**
     * 文件大小单位
     * 文件大小的单位（MB、KB等）
     */
    private String fileSizeUnit;

    /**
     * 导出源ID
     * 导出数据的源数据标识
     */
    private Long exportFrom;

    /**
     * 导出状态
     * 任务当前执行状态
     */
    private String exportStatus;

    /**
     * 消息
     * 任务执行过程中的提示或错误信息
     */
    private String msg;

    /**
     * 导出源类型
     * 数据源的类型（报表、仪表板等）
     */
    private String exportFromType;

    /**
     * 导出时间
     * 任务开始执行的时间戳
     */
    private Long exportTime;

    /**
     * 导出进度
     * 任务完成进度百分比
     */
    private String exportProgress;

    /**
     * 导出机器名
     * 执行导出任务的服务器名称
     */
    private String exportMachineName;

    /**
     * 导出源名称
     * 导出数据源的名称
     */
    private String exportFromName;

    /**
     * 组织名称
     * 用户所属的组织名称
     */
    private String orgName;
}
