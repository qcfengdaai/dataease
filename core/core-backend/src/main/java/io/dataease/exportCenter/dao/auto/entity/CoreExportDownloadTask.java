package io.dataease.exportCenter.dao.auto.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 导出下载任务实体类
 * 记录导出文件的下载任务信息，用于管理导出文件的生命周期和访问权限
 *
 * @author fit2cloud
 * @since 2025-06-16
 */
@TableName("core_export_download_task")
public class CoreExportDownloadTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 下载任务ID
     * 下载任务的唯一标识符，通常关联到对应的导出任务
     */
    private String id;

    /**
     * 创建时间
     * 下载任务创建的时间戳
     */
    private Long createTime;

    /**
     * 有效时间
     * 下载任务的过期时间戳，超过此时间后文件将不可下载
     */
    private Long validTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getValidTime() {
        return validTime;
    }

    public void setValidTime(Long validTime) {
        this.validTime = validTime;
    }

    @Override
    public String toString() {
        return "CoreExportDownloadTask{" +
        "id = " + id +
        ", createTime = " + createTime +
        ", validTime = " + validTime +
        "}";
    }
}
