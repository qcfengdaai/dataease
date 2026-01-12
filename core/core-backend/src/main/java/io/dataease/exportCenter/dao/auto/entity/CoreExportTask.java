package io.dataease.exportCenter.dao.auto.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 导出任务实体类
 * 存储系统中各种数据导出任务的详细信息，包括导出状态、文件信息和执行进度
 *
 * @author fit2cloud
 * @since 2024-06-12
 */
@TableName("core_export_task")
public class CoreExportTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     * 导出任务的唯一标识符
     */
    private String id;

    /**
     * 用户ID
     * 发起导出任务的用户ID
     */
    private Long userId;

    /**
     * 文件名称
     * 导出生成的文件名称
     */
    private String fileName;

    /**
     * 文件大小
     * 导出文件的大小数值，配合fileSizeUnit使用
     */
    private Double fileSize;

    /**
     * 文件大小单位
     * 文件大小的计量单位，如KB、MB、GB等
     */
    private String fileSizeUnit;

    /**
     * 导出来源ID
     * 导出数据的来源对象ID，如图表ID、仪表板ID等
     */
    private Long exportFrom;

    /**
     * 导出状态
     * 任务当前的执行状态，如进行中、已完成、失败等
     */
    private String exportStatus;

    /**
     * 导出来源类型
     * 导出数据的来源类型，如chart、dashboard、dataset等
     */
    private String exportFromType;

    /**
     * 导出时间
     * 任务创建或开始执行的时间戳
     */
    private Long exportTime;

    /**
     * 导出进度
     * 任务执行的进度信息，通常为百分比
     */
    private String exportProgress;

    /**
     * 导出机器名称
     * 执行导出任务的服务器或节点名称
     */
    private String exportMachineName;

    /**
     * 过滤参数
     */
    private String params;

    /**
     * 错误信息
     */
    private String msg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Double getFileSize() {
        return fileSize;
    }

    public void setFileSize(Double fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileSizeUnit() {
        return fileSizeUnit;
    }

    public void setFileSizeUnit(String fileSizeUnit) {
        this.fileSizeUnit = fileSizeUnit;
    }

    public Long getExportFrom() {
        return exportFrom;
    }

    public void setExportFrom(Long exportFrom) {
        this.exportFrom = exportFrom;
    }

    public String getExportStatus() {
        return exportStatus;
    }

    public void setExportStatus(String exportStatus) {
        this.exportStatus = exportStatus;
    }

    public String getExportFromType() {
        return exportFromType;
    }

    public void setExportFromType(String exportFromType) {
        this.exportFromType = exportFromType;
    }

    public Long getExportTime() {
        return exportTime;
    }

    public void setExportTime(Long exportTime) {
        this.exportTime = exportTime;
    }

    public String getExportProgress() {
        return exportProgress;
    }

    public void setExportProgress(String exportProgress) {
        this.exportProgress = exportProgress;
    }

    public String getExportMachineName() {
        return exportMachineName;
    }

    public void setExportMachineName(String exportMachineName) {
        this.exportMachineName = exportMachineName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "CoreExportTask{" +
        "id = " + id +
        ", userId = " + userId +
        ", fileName = " + fileName +
        ", fileSize = " + fileSize +
        ", fileSizeUnit = " + fileSizeUnit +
        ", exportFrom = " + exportFrom +
        ", exportStatus = " + exportStatus +
        ", exportFromType = " + exportFromType +
        ", exportTime = " + exportTime +
        ", exportProgress = " + exportProgress +
        ", exportMachineName = " + exportMachineName +
        ", params = " + params +
        ", msg = " + msg +
        "}";
    }
}
