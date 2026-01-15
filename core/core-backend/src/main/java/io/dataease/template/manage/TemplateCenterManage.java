package io.dataease.template.manage;

import io.dataease.api.template.dto.TemplateManageDTO;
import io.dataease.api.template.dto.TemplateManageFileDTO;
import io.dataease.api.template.dto.TemplateMarketDTO;
import io.dataease.api.template.dto.TemplateMarketPreviewInfoDTO;
import io.dataease.api.template.response.*;
import io.dataease.api.template.vo.MarketApplicationMetaDataVO;
import io.dataease.api.template.vo.MarketApplicationSpecVO;
import io.dataease.api.template.vo.MarketLatestReleaseVO;
import io.dataease.api.template.vo.MarketMetaDataVO;
import io.dataease.constant.CommonConstants;
import io.dataease.exception.DEException;
import io.dataease.i18n.Translator;
import io.dataease.operation.manage.CoreOptRecentManage;
import io.dataease.system.manage.SysParameterManage;
import io.dataease.template.dao.auto.entity.VisualizationTemplateCategoryMap;
import io.dataease.template.dao.auto.mapper.VisualizationTemplateCategoryMapMapper;
import io.dataease.template.dao.ext.ExtVisualizationTemplateMapper;
import io.dataease.utils.HttpClientConfig;
import io.dataease.utils.HttpClientUtil;
import io.dataease.utils.JsonUtil;
import io.dataease.utils.LogUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 模板中心管理类
 * 负责从模板市场获取模板信息、分类信息、推荐模板等功能
 *
 * @author wangjiahao
 */
@Service
public class TemplateCenterManage {
    // 模板市场 V2 版本 API 地址
    private final static String POSTS_API_V2 = "/apis/api.store.halo.run/v1alpha1/applications?keyword=&priceMode=&sort=latestReleaseTimestamp%2Cdesc&type=THEME&deVersion=V2&templateType=&label=&page=1&size=2000";
    // 模板元数据 URL
    private final static String TEMPLATE_META_DATA_URL = "/upload/meta_data.json";
    // 模板基础信息 URL
    private final static String TEMPLATE_BASE_INFO_URL = "/apis/api.store.halo.run/v1alpha1/applications/";
    @Resource
    private SysParameterManage sysParameterManage;

    @Resource
    private CoreOptRecentManage coreOptRecentManage;

    @Resource
    private ExtVisualizationTemplateMapper templateManageMapper;

    @Resource
    private VisualizationTemplateCategoryMapMapper categoryMapMapper;

    /**
     * 从模板市场获取模板文件
     * @param templateUrl 模板URL
     * @return 模板文件信息
     */
    public TemplateManageFileDTO getTemplateFromMarket(String templateUrl) {
        if (StringUtils.isNotEmpty(templateUrl)) {
            // 从URL中提取模板文件名
            String templateName = templateUrl.substring(templateUrl.lastIndexOf("/") + 1, templateUrl.length());
            // 对文件名进行URL编码，处理中文和特殊字符
            templateUrl = templateUrl.replace(templateName, URLEncoder.encode(templateName, StandardCharsets.UTF_8).replace("+", "%20"));
            // 获取模板市场的基础URL
            String sufUrl = sysParameterManage.groupVal("template.").get("template.url");
            // 通过HTTP请求获取模板文件内容
            String templateInfo = HttpClientUtil.get(sufUrl + templateUrl, null);
            // 将JSON字符串解析为模板文件DTO对象
            return JsonUtil.parseObject(templateInfo, TemplateManageFileDTO.class);
        } else {
            return null;
        }
    }

    /**
     * 从模板市场获取模板文件(V2版本)
     * @param templateName 模板名称
     * @return 模板文件信息
     */
    public TemplateManageFileDTO getTemplateFromMarketV2(String templateName) {
        if (StringUtils.isNotEmpty(templateName)) {
            // 获取模板市场的基础URL
            String sufUrl = sysParameterManage.groupVal("template.").get("template.url");
            // 获取模板的基础信息（包含版本、下载链接等）
            String templateBaseInfo = HttpClientUtil.get(sufUrl + TEMPLATE_BASE_INFO_URL + templateName, null);
            MarketTemplateV2ItemResult baseItemInfo = JsonUtil.parseObject(templateBaseInfo, MarketTemplateV2ItemResult.class);
            String templateUrl = "";
            // 优先使用最新发布的版本
            if (baseItemInfo.getLatestRelease() != null) {
                // 构建最新版本的下载URL
                templateUrl = sufUrl + "/store/apps/" + templateName +
                        "/releases/download/" + baseItemInfo.getLatestRelease().getRelease().getMetadata().getName()
                        + "/assets/" + baseItemInfo.getLatestRelease().getAssets().getFirst().getMetadata().getName();
            } else {
                // 如果没有最新版本，使用应用规范中的默认链接
                templateUrl = sufUrl + baseItemInfo.getApplication().getSpec().getLinks().get(0).getUrl();
            }

            // 通过构建的URL获取模板文件内容
            String templateInfo = HttpClientUtil.get(templateUrl, null);
            // 将JSON字符串解析为模板文件DTO对象
            return JsonUtil.parseObject(templateInfo, TemplateManageFileDTO.class);
        } else {
            return null;
        }
    }

    /**
     * 从模板市场内容 API 获取信息
     * @param url 内容API地址
     * @param accessKey 访问密钥
     * @return 返回内容
     */
    public String marketGet(String url, String accessKey) {
        HttpClientConfig config = new HttpClientConfig();
        config.addHeader("API-Authorization", accessKey);
        config.setConnectTimeout(5000);
        config.setSocketTimeout(10000);
        config.setConnectionRequestTimeout(5000);
        return HttpClientUtil.
                get(url, config);
    }

    /**
     * 查询模板市场模板
     * @param templateParams 模板参数
     * @return 模板查询结果
     */
    private MarketTemplateV2BaseResponse templateQuery(Map<String, String> templateParams) {
        try {
            // 调用模板市场 API
            String result = marketGet(templateParams.get("template.url") + POSTS_API_V2, null);
            MarketTemplateV2BaseResponse postsResult = JsonUtil.parseObject(result, MarketTemplateV2BaseResponse.class);
            return postsResult;
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 搜索模板
     * 合并模板市场和本地管理的模板数据
     * @return 模板基础响应
     */
    public MarketBaseResponse searchTemplate() {
        try {
            // 获取模板配置参数
            Map<String, String> templateParams = sysParameterManage.groupVal("template.");
            // 合并模板市场和本地管理的模板数据，并转换为统一的响应格式
            return baseResponseV2Trans(templateQuery(templateParams), searchTemplateFromManage(), templateParams.get("template.url"));
        } catch (Exception e) {
            LogUtil.error(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从本地管理搜索模板
     * @return 模板市场DTO列表
     */
    private List<TemplateMarketDTO> searchTemplateFromManage() {
        try {
            // 从本地数据库查询所有基础模板列表
            List<TemplateManageDTO> manageResult = templateManageMapper.findBaseTemplateList();
            // 查询所有模板分类
            List<TemplateManageDTO> categories = templateManageMapper.findCategories(null);
            // 构建分类ID到分类名称的映射关系
            Map<String, String> categoryMap = categories.stream()
                    .collect(Collectors.toMap(TemplateManageDTO::getId, TemplateManageDTO::getName));
            // 将本地管理数据转换为市场数据格式
            return baseManage2MarketTrans(manageResult, categoryMap);
        } catch (Exception e) {
            DEException.throwException(e);
        }
        return null;
    }

    /**
     * 将管理数据转换为市场数据格式
     * @param manageResult 管理数据列表
     * @param categoryMap 分类映射
     * @return 模板市场DTO列表
     */
    private List<TemplateMarketDTO> baseManage2MarketTrans(List<TemplateManageDTO> manageResult, Map<String, String> categoryMap) {
        List<TemplateMarketDTO> result = new ArrayList<>();
        // 遍历本地管理的模板数据
        manageResult.stream().forEach(templateManageDTO -> {
            // 设置主分类名称
            templateManageDTO.setCategoryName(categoryMap.get(templateManageDTO.getPid()));
            List<String> categories = templateManageDTO.getCategories();
            if (!CollectionUtils.isEmpty(categories)) {
                // 将所有分类ID转换为分类名称
                List<String> categoryNames = categories.stream().map(categoryId -> categoryMap.get(categoryId)).collect(Collectors.toList());
                templateManageDTO.setCategoryNames(categoryNames);
                // 转换为市场DTO格式并添加到结果列表
                result.add(new TemplateMarketDTO(templateManageDTO));
            }
        });
        return result;
    }


    /**
     * 搜索推荐模板
     * 合并模板市场和本地管理的推荐模板数据
     * @return 模板基础响应
     */
    public MarketBaseResponse searchTemplateRecommend() {
        MarketTemplateV2BaseResponse v2BaseResponse = null;
        // 获取模板配置参数
        Map<String, String> templateParams = sysParameterManage.groupVal("template.");
        // 获取模板市场推荐的模板
        try {
            v2BaseResponse = templateQuery(templateParams);
        } catch (Exception e) {
            DEException.throwException(e);
        }
        // 获取本地管理中根据使用次数推荐的模板
        List<TemplateMarketDTO> manage = searchTemplateFromManage();
        // 合并市场推荐和本地推荐，转换为统一的响应格式
        return baseResponseV2TransRecommend(v2BaseResponse, manage, templateParams.get("template.url"));
    }

    /**
     * 搜索模板预览
     * @return 模板预览基础响应
     */
    public MarketPreviewBaseResponse searchTemplatePreview() {
        try {
            // 获取所有模板的基础信息
            MarketBaseResponse baseContentRsp = searchTemplate();
            // 过滤掉"最近使用"分类，只保留业务分类
            List<MarketMetaDataVO> categories = baseContentRsp.getCategories().stream().filter(category -> !Translator.get("i18n_template_recent").equals(category.getLabel())).toList();
            List<TemplateMarketDTO> contents = baseContentRsp.getContents();
            List<TemplateMarketPreviewInfoDTO> previewContents = new ArrayList<>();
            // 为每个分类构建预览信息
            categories.forEach(category -> {
                if (Translator.get("i18n_template_recommend").equals(category.getLabel())) {
                    // 推荐分类：过滤标记为推荐的模板
                    previewContents.add(new TemplateMarketPreviewInfoDTO(category, contents.stream().filter(template -> "Y".equals(template.getSuggest())).collect(Collectors.toList())));
                } else {
                    // 其他分类：根据模板的分类标签进行匹配
                    previewContents.add(new TemplateMarketPreviewInfoDTO(category, contents.stream().filter(template -> checkCategoryMatch(template, category.getLabel())).collect(Collectors.toList())));
                }
            });
            // 构建预览响应对象
            return new MarketPreviewBaseResponse(baseContentRsp.getBaseUrl(), categories.stream().map(MarketMetaDataVO::getLabel)
                    .collect(Collectors.toList()), previewContents);
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return null;
    }

    /**
     * 检查模板分类是否匹配
     * @param template 模板信息
     * @param categoryNameMatch 分类名称
     * @return 是否匹配
     */
    private Boolean checkCategoryMatch(TemplateMarketDTO template, String categoryNameMatch) {
        try {
            return template.getCategories().stream()
                    .anyMatch(category -> categoryNameMatch.equals(category.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 转换 V2 响应为推荐模板响应
     * @param v2BaseResponse V2 基础响应
     * @param templateManages 本地管理模板列表
     * @param url 基础URL
     * @return 模板基础响应
     */
    private MarketBaseResponse baseResponseV2TransRecommend(MarketTemplateV2BaseResponse v2BaseResponse, List<TemplateMarketDTO> templateManages, String url) {
        // 获取每个模板的最近使用时间，用于排序
        Map<String, Long> useTime = coreOptRecentManage.findTemplateRecentUseTime();
        // 获取所有分类并过滤掉"全部"分类
        List<MarketMetaDataVO> categoryVO = getCategoriesV2().stream().filter(node -> !"全部".equalsIgnoreCase(node.getLabel())).collect(Collectors.toList());
        // 构建分类slug到分类名称的映射
        Map<String, String> categoriesMap = categoryVO.stream()
                .collect(Collectors.toMap(MarketMetaDataVO::getSlug, MarketMetaDataVO::getLabel));
        List<TemplateMarketDTO> contents = new ArrayList<>();
        // 处理模板市场的推荐模板
        if (v2BaseResponse != null) {
            v2BaseResponse.getItems().stream().forEach(marketTemplateV2ItemResult -> {
                MarketApplicationSpecVO spec = marketTemplateV2ItemResult.getApplication().getSpec();
                MarketApplicationMetaDataVO metadata = marketTemplateV2ItemResult.getApplication().getMetadata();
                // 只添加标记为推荐的模板
                if ("Y".equalsIgnoreCase(spec.getSuggest())) {
                    contents.add(new TemplateMarketDTO(metadata.getName(), spec.getDisplayName(), spec.getScreenshots().get(0).getUrl(), spec.getLinks().get(0).getUrl(), categoriesMap.get(spec.getLabel()), spec.getTemplateType(), useTime.get(spec.getReadmeName()), "Y", spec.getTemplateClassification()));
                }
            });
        }
        // 按最近使用时间降序排序（最近使用的排在前面）
        Collections.sort(contents);
        // 统计仪表板(PANEL)和数据大屏(SCREEN)类型的模板数量
        Long countDataV = contents.stream().filter(item -> "PANEL".equals(item.getTemplateType())).count();
        Long countDashboard = contents.stream().filter(item -> "SCREEN".equals(item.getTemplateType())).count();
        // 从本地管理中获取对应类型的模板
        List<TemplateMarketDTO> templateDataV = templateManages.stream().filter(item -> "PANEL".equals(item.getTemplateType())).collect(Collectors.toList());
        List<TemplateMarketDTO> templateDashboard = templateManages.stream().filter(item -> "SCREEN".equals(item.getTemplateType())).collect(Collectors.toList());
        // 如果仪表板模板不足 10 个,从本地管理中补充
        if (countDataV < 10) {
            Long addItemCount = 10 - countDataV;
            Long addIndex = templateDataV.size() < addItemCount ? templateDataV.size() : addItemCount;
            contents.addAll(templateDataV.subList(0, addIndex.intValue()));
        }

        // 如果数据大屏模板不足 10 个,从本地管理中补充
        if (countDashboard < 10) {
            Long addItemCount = 10 - countDashboard;
            Long addIndex = templateDashboard.size() < addItemCount ? templateDashboard.size() : addItemCount;
            contents.addAll(templateDashboard.subList(0, addIndex.intValue()));
        }

        // 构建并返回统一的响应对象
        return new MarketBaseResponse(url, categoryVO, contents);
    }

    /**
     * 转换 V2 响应为模板响应
     * @param v2BaseResponse V2 基础响应
     * @param contents 模板内容列表
     * @param url 基础URL
     * @return 模板基础响应
     */
    private MarketBaseResponse baseResponseV2Trans(MarketTemplateV2BaseResponse v2BaseResponse, List<TemplateMarketDTO> contents, String url) {
        // 获取每个模板的最近使用时间
        Map<String, Long> useTime = coreOptRecentManage.findTemplateRecentUseTime();
        // 获取所有分类对象并过滤掉"全部"分类
        List<MarketMetaDataVO> categoryVO = getCategoriesObject().stream().filter(node -> !"全部".equalsIgnoreCase(node.getLabel())).collect(Collectors.toList());
        // 构建分类值到分类名称的映射
        Map<String, String> categoriesMap = categoryVO.stream()
                .collect(Collectors.toMap(MarketMetaDataVO::getValue, MarketMetaDataVO::getLabel));
        // 初始化活跃分类列表，包含"最近使用"和"推荐"
        List<String> activeCategoriesName = new ArrayList<>(Arrays.asList(Translator.get("i18n_template_recent"), Translator.get("i18n_template_recommend")));
        // 设置本地模板的最近使用时间，并收集所有活跃的分类名称
        contents.stream().forEach(templateMarketDTO -> {
            Long recentUseTime = useTime.get(templateMarketDTO.getId());
            templateMarketDTO.setRecentUseTime(recentUseTime == null ? 0 : recentUseTime);
            activeCategoriesName.addAll(templateMarketDTO.getCategoryNames());
        });
        // 处理模板市场的模板数据
        if (v2BaseResponse != null) {
            v2BaseResponse.getItems().stream().forEach(marketTemplateV2ItemResult -> {
                MarketApplicationSpecVO spec = marketTemplateV2ItemResult.getApplication().getSpec();
                MarketApplicationMetaDataVO metadata = marketTemplateV2ItemResult.getApplication().getMetadata();
                // 将市场模板添加到内容列表
                contents.add(new TemplateMarketDTO(metadata.getName(), spec.getDisplayName(), spec.getScreenshots().get(0).getUrl(), spec.getLinks().get(0).getUrl(), categoriesMap.get(spec.getLabel()), spec.getTemplateType(), useTime.get(spec.getReadmeName()), spec.getSuggest(), spec.getTemplateClassification()));
                // 收集市场模板使用的分类名称
                if (categoriesMap.get(spec.getLabel()) != null) {
                    activeCategoriesName.add(categoriesMap.get(spec.getLabel()));
                }
            });
        }
        // 按最近使用时间降序排序
        Collections.sort(contents);
        // 只返回有模板的分类
        return new MarketBaseResponse(url, categoryVO.stream().filter(node -> activeCategoriesName.contains(node.getLabel())).collect(Collectors.toList()), contents);
    }


    /**
     * 获取所有分类名称列表
     * @return 分类名称列表
     */
    public List<String> getCategories() {
        return getCategoriesV2().stream().map(MarketMetaDataVO::getLabel)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有分类对象列表
     * @return 分类对象列表
     */
    public List<MarketMetaDataVO> getCategoriesObject() {
        List<MarketMetaDataVO> result = getCategoriesV2();
        result.add(0, new MarketMetaDataVO("recent", Translator.get("i18n_template_recent"), CommonConstants.TEMPLATE_SOURCE.PUBLIC));
        return result;
    }

    /**
     * 获取 V2 版本的分类映射
     * @return 分类映射Map(slug -> label)
     */
    public Map<String, String> getCategoriesBaseV2() {
        Map<String, String> categories = getCategoriesV2().stream()
                .collect(Collectors.toMap(MarketMetaDataVO::getSlug, MarketMetaDataVO::getLabel));
        return categories;
    }

    /**
     * 获取 V2 版本的分类列表
     * 合并模板市场分类和本地管理分类
     * @return 分类对象列表
     */
    public List<MarketMetaDataVO> getCategoriesV2() {
        List<MarketMetaDataVO> allCategories = new ArrayList<>();
        // 从本地数据库查询所有分类
        List<TemplateManageDTO> manageCategories = templateManageMapper.findCategories(null);
        // 将本地分类转换为市场分类格式
        List<MarketMetaDataVO> manageCategoriesTrans = manageCategories.stream()
                .map(templateCategory -> new MarketMetaDataVO(templateCategory.getId(), templateCategory.getName(), CommonConstants.TEMPLATE_SOURCE.MANAGE))
                .collect(Collectors.toList());
        // 从模板市场获取分类
        try {
            Map<String, String> templateParams = sysParameterManage.groupVal("template.");
            // 调用模板市场的元数据接口
            String resultStr = marketGet(templateParams.get("template.url") + TEMPLATE_META_DATA_URL, null);
            MarketMetaDataBaseResponse metaData = JsonUtil.parseObject(resultStr, MarketMetaDataBaseResponse.class);
            // 添加市场分类
            allCategories.addAll(metaData.getLabels());
            // 在列表开头添加"推荐"分类
            allCategories.add(0, new MarketMetaDataVO("suggest", Translator.get("i18n_template_recommend"), CommonConstants.TEMPLATE_SOURCE.PUBLIC));
        } catch (Exception e) {
            LogUtil.error("模板市场分类获取错误", e);
        }

        // 合并市场分类和本地分类，并根据标签去重
        return mergeAndDistinctByLabel(allCategories, manageCategoriesTrans);

    }

    /**
     * 合并两个分类列表并根据标签去重
     * @param list1 分类列表1
     * @param list2 分类列表2
     * @return 合并后的分类列表
     */
    private List<MarketMetaDataVO> mergeAndDistinctByLabel(List<MarketMetaDataVO> list1, List<MarketMetaDataVO> list2) {
        // 合并两个列表
        List<MarketMetaDataVO> mergedList = new ArrayList<>(list1);
        mergedList.addAll(list2);
        // 使用 LinkedHashMap 保持顺序并去重（根据分类标签）
        Map<String, MarketMetaDataVO> marketMetaDataMap = mergedList.stream()
                .collect(Collectors.toMap(
                        MarketMetaDataVO::getLabel,  // 使用分类标签作为唯一键
                        Function.identity(),             // 值为分类对象本身
                        (existing, replacement) -> {    // 当出现重复键时的处理策略
                            // 如果有重复，保留市场分类（标记为PUBLIC）
                            existing.setSource(CommonConstants.TEMPLATE_SOURCE.PUBLIC);
                            return existing;
                        },
                        LinkedHashMap::new               // 使用LinkedHashMap保持插入顺序
                ));
        return new ArrayList<>(marketMetaDataMap.values());
    }
}
