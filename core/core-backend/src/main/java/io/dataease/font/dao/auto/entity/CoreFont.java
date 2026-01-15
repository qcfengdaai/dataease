package io.dataease.font.dao.auto.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 字体信息实体类
 * 存储系统中可用字体的相关信息，包括字体名称、文件信息和配置属性
 *
 * @author fit2cloud
 * @since 2024-08-28
 */
@TableName("core_font")
public class CoreFont implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 字体名称
     */
    private String name;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件转换名称
     */
    private String fileTransName;

    /**
     * 是否默认
     */
    private Boolean isDefault;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 是否内置
     */
    private Boolean isBuiltin;

    /**
     * 字体大小
     * 记录字体文件的大小，单位由sizeType字段指定
     */
    private Double size;

    /**
     * 字体大小单位类型
     * 如MB、KB等，表示字体文件大小的计量单位
     */
    private String sizeType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileTransName() {
        return fileTransName;
    }

    public void setFileTransName(String fileTransName) {
        this.fileTransName = fileTransName;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getIsBuiltin() {
        return isBuiltin;
    }

    public void setIsBuiltin(Boolean isBuiltin) {
        this.isBuiltin = isBuiltin;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public String getSizeType() {
        return sizeType;
    }

    public void setSizeType(String sizeType) {
        this.sizeType = sizeType;
    }

    @Override
    public String toString() {
        return "CoreFont{" +
        "id = " + id +
        ", name = " + name +
        ", fileName = " + fileName +
        ", fileTransName = " + fileTransName +
        ", isDefault = " + isDefault +
        ", updateTime = " + updateTime +
        ", isBuiltin = " + isBuiltin +
        ", size = " + size +
        ", sizeType = " + sizeType +
        "}";
    }
}
