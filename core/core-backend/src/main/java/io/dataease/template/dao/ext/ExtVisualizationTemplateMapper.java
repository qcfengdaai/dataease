package io.dataease.template.dao.ext;

import io.dataease.api.template.dto.TemplateManageDTO;
import io.dataease.api.template.request.TemplateManageRequest;
import io.dataease.api.visualization.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 可视化模板扩展 Mapper 接口
 * 提供模板管理的复杂查询功能,包括模板列表查询、分类查询、重复检查等
 *
 * @author fit2cloud
 * @since 2023-11-10
 */
@Mapper
public interface ExtVisualizationTemplateMapper{

    /**
     * 查询模板列表
     * @param request 查询请求参数
     * @return 模板列表
     */
    List<TemplateManageDTO> findTemplateList(TemplateManageRequest request);

    /**
     * 查询分类列表
     * @param request 查询请求参数
     * @return 分类列表
     */
    List<TemplateManageDTO> findCategories(TemplateManageRequest request);

    /**
     * 查询基础模板列表
     * @return 基础模板列表
     */
    List<TemplateManageDTO> findBaseTemplateList();

    /**
     * 检查分类映射是否存在
     * @param categoryId 分类ID
     * @return 存在数量
     */
    Long checkCategoryMap(@Param("categoryId") String categoryId);

    /**
     * 检查模板ID是否在其他分类中重复使用
     * @param categoryId 分类ID
     * @param templateId 模板ID
     * @return 重复数量
     */
    Long checkRepeatTemplateId(@Param("categoryId") String categoryId, @Param("templateId") String templateId);

    /**
     * 根据模板删除分类映射
     * @param templateName 模板名称
     * @param templateId 模板ID
     */
    void deleteCategoryMapByTemplate(@Param("templateName") String templateName, @Param("templateId") String templateId);

    /**
     * 检查分类下是否存在指定模板名称
     * @param templateName 模板名称
     * @param categories 分类ID列表
     * @return 存在数量
     */
    Long checkCategoryTemplateName(@Param("templateName") String templateName,@Param("categories") List<String> categories);

    /**
     * 批量检查分类下模板名称是否存在
     * @param templateNames 模板名称列表
     * @param categories 分类ID列表
     * @param templateArray 模板数组
     * @return 存在数量
     */
    Long checkCategoryTemplateBatchNames(@Param("templateNames") List<String> templateNames,@Param("categories") List<String> categories,@Param("templateArray") List<String> templateArray);

    /**
     * 查找模板所属分类
     * @param templateId 模板ID
     * @return 分类ID列表
     */
    List<String> findTemplateCategories(@Param("templateId") String templateId);

    /**
     * 批量查找模板所属分类
     * @param templateArray 模板ID数组
     * @return 分类ID列表(逗号分隔)
     */
    List<String> findTemplateArrayCategories(@Param("templateArray") List<String> templateArray);

    /**
     * 查找应用视图信息
     * @param viewIds 视图ID列表
     * @return 视图信息列表
     */
    List<AppCoreChartViewVO> findAppViewInfo(@Param("viewIds") List<Long> viewIds);

    /**
     * 查找应用数据集分组信息
     * @param dsIds 数据集ID列表
     * @return 数据集分组信息列表
     */
    List<AppCoreDatasetGroupVO> findAppDatasetGroupInfo(@Param("dsIds") List<Long> dsIds);

    /**
     * 查找应用数据集表信息
     * @param dsIds 数据集ID列表
     * @return 数据集表信息列表
     */
    List<AppCoreDatasetTableVO> findAppDatasetTableInfo(@Param("dsIds") List<Long> dsIds);

    /**
     * 查找应用数据集表字段信息
     * @param dsIds 数据集ID列表
     * @return 数据集字段信息列表
     */
    List<AppCoreDatasetTableFieldVO> findAppDatasetTableFieldInfo(@Param("dsIds") List<Long> dsIds);

    /**
     * 查找应用数据源信息
     * @param dsIds 数据源ID列表
     * @return 数据源信息列表
     */
    List<AppCoreDatasourceVO> findAppDatasourceInfo(@Param("dsIds") List<Long> dsIds);

    /**
     * 查找应用数据源任务信息
     * @param dsIds 数据源ID列表
     * @return 数据源任务信息列表
     */
    List<AppCoreDatasourceTaskVO> findAppDatasourceTaskInfo(@Param("dsIds") List<Long> dsIds);

    /**
     * 查找应用联动信息
     * @param dvId 可视化ID
     * @return 联动信息列表
     */
    List<VisualizationLinkageVO> findAppLinkageInfo(@Param("dvId") Long dvId);

    /**
     * 查找应用联动字段信息
     * @param dvId 可视化ID
     * @return 联动字段信息列表
     */
    List<VisualizationLinkageFieldVO> findAppLinkageFieldInfo(@Param("dvId") Long dvId);

    /**
     * 查找应用跳转信息
     * @param dvId 可视化ID
     * @return 跳转信息列表
     */
    List<VisualizationLinkJumpVO> findAppLinkJumpInfo(@Param("dvId") Long dvId);

    /**
     * 查找应用跳转详细信息
     * @param dvId 可视化ID
     * @return 跳转详细信息列表
     */
    List<VisualizationLinkJumpInfoVO> findAppLinkJumpInfoInfo(@Param("dvId") Long dvId);

    /**
     * 查找应用跳转目标视图信息
     * @param dvId 可视化ID
     * @return 跳转目标视图信息列表
     */
    List<VisualizationLinkJumpTargetViewInfoVO> findAppLinkJumpTargetViewInfoInfo(@Param("dvId") Long dvId);

}
