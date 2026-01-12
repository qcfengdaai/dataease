package io.dataease.template.service;

import io.dataease.api.template.TemplateMarketApi;
import io.dataease.api.template.response.MarketBaseResponse;
import io.dataease.api.template.response.MarketPreviewBaseResponse;
import io.dataease.api.template.vo.MarketMetaDataVO;
import io.dataease.template.manage.TemplateCenterManage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 模板市场服务类
 * 提供模板市场的查询、推荐、预览等功能
 *
 * @author WangJiaHao
 * @date 2023/11/17 13:20
 */
@RestController
@RequestMapping("/templateMarket")
public class TemplateMarketService implements TemplateMarketApi {

    @Resource
    private TemplateCenterManage templateCenterManage;

    /**
     * 搜索模板
     * @return 模板基础响应
     */
    @Override
    public MarketBaseResponse searchTemplate() {
        return templateCenterManage.searchTemplate();
    }
    /**
     * 搜索推荐模板
     * @return 模板基础响应
     */
    @Override
    public MarketBaseResponse searchTemplateRecommend() {
        return templateCenterManage.searchTemplateRecommend();
    }

    /**
     * 搜索模板预览
     * @return 模板预览基础响应
     */
    @Override
    public MarketPreviewBaseResponse searchTemplatePreview() {
        return templateCenterManage.searchTemplatePreview();
    }

    /**
     * 获取所有分类名称列表
     * @return 分类名称列表
     */
    @Override
    public List<String> categories() {
        return templateCenterManage.getCategories();
    }

    /**
     * 获取所有分类对象列表
     * @return 分类对象列表
     */
    @Override
    public List<MarketMetaDataVO> categoriesObject() {
        return templateCenterManage.getCategoriesObject();
    }
}
