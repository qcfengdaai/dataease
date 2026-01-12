package io.dataease.template.dao.auto.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 可视化模板分类映射实体类
 * 用于存储模板与分类的多对多关联关系
 *
 * @author fit2cloud
 * @since 2023-12-04
 */
@TableName("visualization_template_category_map")
public class VisualizationTemplateCategoryMap implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;

    /**
     * 分类ID
     */
    private String categoryId;

    /**
     * 模板ID
     */
    private String templateId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    @Override
    public String toString() {
        return "VisualizationTemplateCategoryMap{" +
        "id = " + id +
        ", categoryId = " + categoryId +
        ", templateId = " + templateId +
        "}";
    }
}
