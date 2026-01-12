package io.dataease.visualization.server;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.type.TypeReference;
import io.dataease.api.dataset.union.DatasetGroupInfoDTO;
import io.dataease.api.dataset.union.DatasetTableInfoDTO;
import io.dataease.api.dataset.union.UnionDTO;
import io.dataease.api.template.dto.TemplateManageFileDTO;
import io.dataease.api.template.dto.VisualizationTemplateExtendDataDTO;
import io.dataease.api.visualization.DataVisualizationApi;
import io.dataease.api.visualization.dto.VisualizationViewTableDTO;
import io.dataease.api.visualization.request.DataVisualizationBaseRequest;
import io.dataease.api.visualization.request.VisualizationAppExportRequest;
import io.dataease.api.visualization.request.VisualizationWorkbranchQueryRequest;
import io.dataease.api.visualization.vo.*;
import io.dataease.auth.DeLinkPermit;
import io.dataease.chart.dao.auto.entity.CoreChartView;
import io.dataease.chart.dao.auto.mapper.CoreChartViewMapper;
import io.dataease.chart.dao.ext.mapper.ExtChartViewMapper;
import io.dataease.chart.manage.ChartDataManage;
import io.dataease.chart.manage.ChartViewManege;
import io.dataease.commons.constants.DataVisualizationConstants;
import io.dataease.commons.constants.OptConstants;
import io.dataease.constant.CommonConstants;
import io.dataease.constant.LogOT;
import io.dataease.dataset.dao.auto.entity.CoreDatasetGroup;
import io.dataease.dataset.dao.auto.entity.CoreDatasetTable;
import io.dataease.dataset.dao.auto.entity.CoreDatasetTableField;
import io.dataease.dataset.dao.auto.mapper.CoreDatasetGroupMapper;
import io.dataease.dataset.dao.auto.mapper.CoreDatasetTableFieldMapper;
import io.dataease.dataset.dao.auto.mapper.CoreDatasetTableMapper;
import io.dataease.dataset.manage.DatasetDataManage;
import io.dataease.dataset.manage.DatasetGroupManage;
import io.dataease.dataset.manage.DatasetSQLManage;
import io.dataease.dataset.utils.DatasetUtils;
import io.dataease.datasource.dao.auto.entity.CoreDatasource;
import io.dataease.datasource.dao.auto.mapper.CoreDatasourceMapper;
import io.dataease.datasource.provider.ExcelUtils;
import io.dataease.datasource.server.DatasourceServer;
import io.dataease.exception.DEException;
import io.dataease.extensions.datasource.dto.DatasetTableDTO;
import io.dataease.extensions.datasource.vo.DatasourceConfiguration;
import io.dataease.extensions.view.dto.ChartViewDTO;
import io.dataease.i18n.Translator;
import io.dataease.license.config.XpackInteract;
import io.dataease.license.manage.CoreLicManage;
import io.dataease.log.DeLog;
import io.dataease.menu.dao.auto.entity.CoreMenu;
import io.dataease.model.BusiNodeRequest;
import io.dataease.model.BusiNodeVO;
import io.dataease.operation.manage.CoreOptRecentManage;
import io.dataease.system.manage.CoreUserManage;
import io.dataease.template.dao.auto.entity.VisualizationTemplate;
import io.dataease.template.dao.auto.entity.VisualizationTemplateExtendData;
import io.dataease.template.dao.auto.mapper.VisualizationTemplateExtendDataMapper;
import io.dataease.template.dao.auto.mapper.VisualizationTemplateMapper;
import io.dataease.template.dao.ext.ExtVisualizationTemplateMapper;
import io.dataease.template.manage.TemplateCenterManage;
import io.dataease.utils.*;
import io.dataease.visualization.dao.auto.entity.DataVisualizationInfo;
import io.dataease.visualization.dao.auto.entity.SnapshotDataVisualizationInfo;
import io.dataease.visualization.dao.auto.entity.VisualizationWatermark;
import io.dataease.visualization.dao.auto.mapper.DataVisualizationInfoMapper;
import io.dataease.visualization.dao.auto.mapper.SnapshotCoreChartViewMapper;
import io.dataease.visualization.dao.auto.mapper.SnapshotDataVisualizationInfoMapper;
import io.dataease.visualization.dao.auto.mapper.VisualizationWatermarkMapper;
import io.dataease.visualization.dao.ext.mapper.ExtDataVisualizationMapper;
import io.dataease.visualization.manage.CoreBusiManage;
import io.dataease.visualization.manage.CoreVisualizationManage;
import io.dataease.visualization.utils.VisualizationUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * 数据可视化服务控制器
 *
 * <p>提供仪表板、数据大屏的可视化资源管理功能</p>
 *
 * <h3>主要功能:</h3>
 * <ul>
 *   <li>仪表板资源的创建、编辑、删除、查询</li>
 *   <li>仪表板组件管理（视图、过滤器、联动等）</li>
 *   <li>数据集关联和字段管理</li>
 *   <li>仪表板导出和导入</li>
 *   <li>快照管理</li>
 * </ul>
 *
 * <h3>API路径:</h3>
 * <pre>
 * GET  /api/dataVisualization/tree/{id} - 查询资源树
 * POST /api/dataVisualization/save - 保存仪表板
 * POST /api/dataVisualization/edit - 编辑仪表板
 * DELETE /api/dataVisualization/delete - 删除仪表板
 * ...以及其他API
 * </pre>
 *
 * @author DataEase
 * @since 2024-01-12
 */
@RestController
@RequestMapping("/dataVisualization")
public class DataVisualizationServer implements DataVisualizationApi {

    @Resource
    private DataVisualizationInfoMapper visualizationInfoMapper;

    @Resource
    private ChartViewManege chartViewManege;
    @Resource
    private CoreChartViewMapper coreChartViewMapper;

    @Resource
    private ExtDataVisualizationMapper extDataVisualizationMapper;

    @Resource
    private CoreVisualizationManage coreVisualizationManage;

    @Resource
    private ChartDataManage chartDataManage;

    @Resource
    private VisualizationTemplateMapper templateMapper;

    @Resource
    private TemplateCenterManage templateCenterManage;

    @Resource
    private StaticResourceServer staticResourceServer;

    @Resource
    private VisualizationTemplateExtendDataMapper templateExtendDataMapper;

    @Resource
    private CoreOptRecentManage coreOptRecentManage;

    @Resource
    private VisualizationWatermarkMapper watermarkMapper;

    @Resource
    private DatasetGroupManage datasetGroupManage;

    @Resource
    private DatasetDataManage datasetDataManage;

    @Resource
    private ExtVisualizationTemplateMapper appTemplateMapper;

    @Resource
    private CoreDatasetGroupMapper coreDatasetGroupMapper;

    @Resource
    private CoreDatasetTableMapper coreDatasetTableMapper;

    @Resource
    private CoreDatasetTableFieldMapper coreDatasetTableFieldMapper;

    @Resource
    private CoreDatasourceMapper coreDatasourceMapper;

    @Resource
    private CoreBusiManage coreBusiManage;

    @Resource
    private CoreLicManage coreLicManage;

    @Resource
    private CoreUserManage coreUserManage;
    @Resource
    private DatasourceServer datasourceServer;

    @Resource
    private SnapshotDataVisualizationInfoMapper snapshotMapper;
    @Resource
    private ExtChartViewMapper extChartViewMapper;
    @Resource
    private DatasetSQLManage datasetSQLManage;

    /**
     * 查找可复制的资源
     *
     * @param dvId 数据可视化ID
     * @param busiFlag 业务标识
     * @return 可复制的可视化资源对象，如果不满足条件则返回null
     */
    @Override
    public DataVisualizationVO findCopyResource(Long dvId, String busiFlag) {
        // 1. 调用代理对象查询资源快照信息
        DataVisualizationVO result = Objects.requireNonNull(CommonBeanFactory.proxy(this.getClass())).findById(new DataVisualizationBaseRequest(dvId, busiFlag, CommonConstants.RESOURCE_TABLE.SNAPSHOT, DataVisualizationConstants.QUERY_SOURCE.MAIN_EDIT));
        // 2. 判断资源是否可以被复制（pid为-1表示根节点，可以被复制）
        if (result != null && result.getPid() == -1) {
            return result;
        } else {
            return null;
        }
    }

    /**
     * 根据ID查询可视化资源详细信息
     *
     * @param request 查询请求对象，包含ID、业务标识、资源表类型等
     * @return 可视化资源详细信息
     * @throws DEException 当资源不存在时抛出异常
     */
    @DeLinkPermit("#p0.id")
    @DeLog(id = "#p0.id", ot = LogOT.READ, stExp = "#p0.busiFlag")
    @Override
    @XpackInteract(value = "dataVisualizationServer", original = true)
    public DataVisualizationVO findById(DataVisualizationBaseRequest request) {
        // 1. 提取请求参数
        Long dvId = request.getId();
        String busiFlag = request.getBusiFlag();
        String resourceTable = request.getResourceTable();
        // 2. 如果是编辑查询，进行镜像检查
        if (DataVisualizationConstants.QUERY_SOURCE.MAIN_EDIT.equals(request.getSource())) {
            // 2.1 构建查询条件：查询未发布或已保存未发布的快照
            QueryWrapper<SnapshotDataVisualizationInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", dvId);
            queryWrapper.in("status", Arrays.asList(CommonConstants.DV_STATUS.UNPUBLISHED, CommonConstants.DV_STATUS.SAVED_UNPUBLISHED)); // 状态为0 未发布 和 2 已保存未发布的 不需要重置镜像
            // 2.2 如果快照不存在，则恢复快照
            if (!snapshotMapper.exists(queryWrapper)) {
                coreVisualizationManage.dvSnapshotRecover(dvId);
            }
        }
        // 3. 查询仪表板基本信息
        DataVisualizationVO result = extDataVisualizationMapper.findDvInfo(dvId, busiFlag, resourceTable);
        // 4. 如果查询到结果，进行数据组装
        if (result != null) {
            // 4.1 获取创建者姓名
            String userName = coreUserManage.getUserName(Long.valueOf(result.getCreateBy()));
            if (StringUtils.isNotBlank(userName)) {
                result.setCreatorName(userName);
            }
            // 4.2 获取图表信息
            List<ChartViewDTO> chartViewDTOS = chartViewManege.listBySceneId(dvId, resourceTable);
            if (!CollectionUtils.isEmpty(chartViewDTOS)) {
                // 4.2.1 过滤当前使用的图表信息（通过组件数据中的ID匹配）
                Map<Long, ChartViewDTO> viewInfo = chartViewDTOS.stream().filter(item -> result.getComponentData().indexOf("\"id\":\"" + item.getId()) > 0).collect(Collectors.toMap(ChartViewDTO::getId, chartView -> chartView));
                result.setCanvasViewInfo(viewInfo);
            }
            // 4.3 设置水印信息
            VisualizationWatermark watermark = watermarkMapper.selectById("system_default");
            VisualizationWatermarkVO watermarkVO = new VisualizationWatermarkVO();
            BeanUtils.copyBean(watermarkVO, watermark);
            result.setWatermarkInfo(watermarkVO);

            // 4.4 如果是定时报告查询，获取自定义过滤组件信息
            if (DataVisualizationConstants.QUERY_SOURCE.REPORT.equals(request.getSource()) && request.getTaskId() != null) {
                // 4.4.1 查询定时报告的自定义过滤组件
                List<VisualizationReportFilterVO> filterVOS = extDataVisualizationMapper.queryReportFilter(dvId, request.getTaskId());
                if (!CollectionUtils.isEmpty(filterVOS)) {
                    // 4.4.2 将过滤组件信息转为Map，key为过滤组件ID
                    Map<Long, VisualizationReportFilterVO> reportFilterInfo = filterVOS.stream().collect(Collectors.toMap(VisualizationReportFilterVO::getFilterId, filterVo -> filterVo));
                    result.setReportFilterInfo(reportFilterInfo);
                }
            }
            // 4.5 如果不需要显示水印，禁用水印
            if (ObjectUtils.isNotEmpty(request.getShowWatermark()) && !request.getShowWatermark()) {
                VisualizationWatermarkVO watermarkInfo = result.getWatermarkInfo();
                String settingContent = null;
                if (ObjectUtils.isNotEmpty(watermarkInfo) && StringUtils.isNotBlank(settingContent = watermarkInfo.getSettingContent())) {
                    // 4.5.1 解析水印配置
                    Map map = JsonUtil.parse(settingContent, Map.class);
                    // 4.5.2 禁用水印
                    map.put("enable", false);
                    settingContent = JsonUtil.toJSONString(map).toString();
                    watermarkInfo.setSettingContent(settingContent);
                    result.setWatermarkInfo(watermarkInfo);
                }
            }
            // 4.6 设置权重（用于排序）
            result.setWeight(9);
            return result;
        } else {
            // 5. 如果资源不存在，抛出异常
            DEException.throwException(Translator.get("i18n_resource_not_exists"));
        }
        return null;
    }

    /**
     * 应用数据集匹配
     * 将应用导出数据中的数据集信息与系统中的数据集进行匹配
     *
     * @param appData 应用导出数据
     * @param datasourceIdMap 数据源ID映射
     * @param dsGroupIdMap 数据集组ID映射
     * @param dsTableIdMap 数据集表ID映射
     * @param dsTableFieldsIdMap 数据集字段ID映射
     * @param dsTableFieldsDatasetNameMap 数据集字段名称映射
     */
    private void appDatasetMatch(VisualizationExport2AppVO appData, Map<Long, Long> datasourceIdMap, Map<Long, Long> dsGroupIdMap, Map<Long, Long> dsTableIdMap, Map<Long, Long> dsTableFieldsIdMap,Map<String, String> dsTableFieldsDatasetNameMap) {
        // 1. 提取应用数据中的数据集信息
        List<AppCoreDatasetGroupVO> sourceDatasetGroupList = appData.getDatasetGroupsInfo();
        List<AppCoreDatasetTableVO> sourceDatasetTableList = appData.getDatasetTablesInfo();
        List<AppCoreDatasetTableFieldVO> sourceDatasetTableFieldList = appData.getDatasetTableFieldsInfo();

        // 2. 构建数据集表的映射关系（按数据集组ID分组）
        Map<Long, List<AppCoreDatasetTableVO>> sourceDatasetTableMap =
                DeCollectionUtils.groupBy(sourceDatasetTableList, AppCoreDatasetTableVO::getDatasetGroupId);

        // 3. 构建数据集字段的映射关系（按数据集表ID分组）
        Map<Long, List<AppCoreDatasetTableFieldVO>> sourceDatasetTableFieldMap =
                DeCollectionUtils.groupBy(sourceDatasetTableFieldList, AppCoreDatasetTableFieldVO::getDatasetTableId);

        // 4. 构建数据集字段的映射关系（按数据集组ID分组，用于计算字段匹配）
        Map<Long, List<AppCoreDatasetTableFieldVO>> sourceDatasetTableFieldMapGroup =
                DeCollectionUtils.groupBy(sourceDatasetTableFieldList, AppCoreDatasetTableFieldVO::getDatasetGroupId);


        // 5. 遍历每个数据集组，进行匹配
        sourceDatasetGroupList.forEach(sourceDatasetGroup -> {
            // 5.1 获取源ID和系统ID
            Long systemDatasetGroupId = sourceDatasetGroup.getSystemDatasetId();
            Long sourceDatasetGroupId = sourceDatasetGroup.getId();
            // 5.2 保存数据集组ID映射
            dsGroupIdMap.put(sourceDatasetGroup.getId(), systemDatasetGroupId);
            // 5.3 查询系统中的数据集组信息
            CoreDatasetGroup systemDatasetGroup = coreDatasetGroupMapper.selectById(systemDatasetGroupId);
            if (systemDatasetGroup != null) {
                // 5.4 查询系统中的数据集表列表
                QueryWrapper<CoreDatasetTable> wrapper = new QueryWrapper<>();
                wrapper.eq("dataset_group_id", systemDatasetGroupId);
                List<CoreDatasetTable> systemDatasetTableList = coreDatasetTableMapper.selectList(wrapper);
                // 5.5 获取源数据集表列表
                List<AppCoreDatasetTableVO> sourceDatasetTableListSub = sourceDatasetTableMap.get(sourceDatasetGroupId);
                if (systemDatasetTableList != null && sourceDatasetTableListSub != null) {
                    // 5.6 遍历源数据集表，进行表名匹配
                    for (AppCoreDatasetTableVO sourceTable : sourceDatasetTableListSub) {
                        for (CoreDatasetTable systemTable : systemDatasetTableList) {
                            // 5.6.1 根据表名匹配
                            if (sourceTable.getTableName().equals(systemTable.getTableName())) {
                                // 5.6.2 保存数据集表ID映射和数据源ID映射
                                dsTableIdMap.put(sourceTable.getId(), systemTable.getId());
                                datasourceIdMap.put(sourceTable.getDatasourceId(), systemTable.getDatasourceId());

                                // 5.6.3 获取源数据集字段列表
                                List<AppCoreDatasetTableFieldVO> sourceDatasetTableFieldListSub = sourceDatasetTableFieldMap.get(sourceTable.getId());

                                // 5.6.4 查询系统中的数据集字段列表
                                QueryWrapper<CoreDatasetTableField> wrapperField = new QueryWrapper<>();
                                wrapperField.eq("dataset_table_id", systemTable.getId());
                                List<CoreDatasetTableField> systemDatasetTableFieldSub = coreDatasetTableFieldMapper.selectList(wrapperField);

                                // 5.6.5 遍历源数据集字段，根据原始字段名匹配
                                for (AppCoreDatasetTableFieldVO sourceTableField : sourceDatasetTableFieldListSub) {
                                    for (CoreDatasetTableField systemTableField : systemDatasetTableFieldSub) {
                                        if (sourceTableField.getOriginName().equals(systemTableField.getOriginName())) {
                                            // 5.6.5.1 保存字段ID映射和字段名称映射
                                            dsTableFieldsIdMap.put(sourceTableField.getId(), systemTableField.getId());
                                            dsTableFieldsDatasetNameMap.put(sourceTableField.getDataeaseName(),systemTableField.getDataeaseName());
                                            break;
                                        }
                                    }
                                }

                                // 5.6.6 进行二次匹配，解决计算字段没有tableId的问题
                                List<AppCoreDatasetTableFieldVO> sourceDatasetTableFieldListSubGroup = sourceDatasetTableFieldMapGroup.get(sourceTable.getDatasetGroupId());

                                // 5.6.7 查询系统中的数据集字段列表（按数据集组ID）
                                QueryWrapper<CoreDatasetTableField> wrapperFieldGroup = new QueryWrapper<>();
                                wrapperFieldGroup.eq("dataset_group_id", systemTable.getDatasetGroupId());
                                List<CoreDatasetTableField> systemDatasetTableFieldSubGroup = coreDatasetTableFieldMapper.selectList(wrapperFieldGroup);

                                // 5.6.8 遍历源数据集字段，根据字段名匹配（用于计算字段）
                                for (AppCoreDatasetTableFieldVO sourceTableField : sourceDatasetTableFieldListSubGroup) {
                                    for (CoreDatasetTableField systemTableField : systemDatasetTableFieldSubGroup) {
                                        // 5.6.8.1 只处理未匹配的字段
                                        if (dsTableFieldsIdMap.get(sourceTableField.getId())==null && sourceTableField.getName().equals(systemTableField.getName())) {
                                            // 5.6.8.2 保存字段ID映射和字段名称映射
                                            dsTableFieldsIdMap.put(sourceTableField.getId(), systemTableField.getId());
                                            dsTableFieldsDatasetNameMap.put(sourceTableField.getDataeaseName(),systemTableField.getDataeaseName());
                                            break;
                                        }
                                    }
                                }
                                // 5.6.9 表名匹配成功，跳出内层循环
                                break;
                            }
                        }
                    }
                }

            }
        });


    }

    /**
     * 保存画布（仪表板或数据大屏）
     *
     * <p>发布兼容逻辑：</p>
     * <ul>
     * <li>saveCanvas 为初次保存，包括模板、应用、普通创建，所有变更操作都走snapshot表</li>
     * <li>如果是文件夹直接保存在主表中，如果是仪表板（数据大屏），主表和镜像表各保存一份，主表仅作为权限和预览控制，此时主表状态为'未发布'</li>
     * <li>编辑检查：如果存在未发布的仪表板snapshot，则默认加载snapshot进行编辑，所有操作均为snapshot操作</li>
     * <li>发布（重新发布）：将snapshot表中的所有数据复制到主表中，同时变更主表状态为'已发布'</li>
     * <li>如果对已发布的仪表板编辑并存在已保存的镜像，此时仪表板状态为'已保存未发布'</li>
     * </ul>
     *
     * @param request 保存请求对象
     * @return 新创建的可视化资源ID
     * @throws Exception 保存过程中的异常
     */
    @DeLog(id = "#p0.id", pid = "#p0.pid", ot = LogOT.CREATE, stExp = "#p0.type")
    @Override
    @Transactional
    public String saveCanvas(DataVisualizationBaseRequest request) throws Exception {
        /*
         * 发布兼容逻辑
         * saveCanvas 为初次保存 包括 模板 应用 普通创建 所有变更操作都走snapshot表
         * 1.如果是文件夹直接保存在主表中，如果是仪表板（数据大屏），主表和镜像表各保存一份 主表仅作为权限和预览控制此时主表状态为‘未发布’
         * 2.编辑检查：如果存在未发布的仪表板snapshot，则默认加载snapshot进行编辑所有操作均为snapshot操作
         * 3.发布（重新发布）：将snapshot表中的所有数据复制到主表中，同时变更主表状态为‘已发布’
         * 4.如果对已发布的仪表板编辑并存在已保存的镜像，此时仪表板状态为‘已保存未发布’
         */
        boolean isAppSave = false;
        Long time = System.currentTimeMillis();
        // 如果是应用 则新进行应用校验 数据集名称和 数据源名称校验
        VisualizationExport2AppVO appData = request.getAppData();
        Map<Long, Long> dsGroupIdMap = new HashMap<>();
        List<DatasetGroupInfoDTO> newDsGroupInfo = new ArrayList<>();
        Map<Long, Long> dsTableIdMap = new HashMap<>();
        Map<Long, Long> dsTableFieldsIdMap = new HashMap<>();
        Map<String, String> dsTableFieldsDatasetNameMap = new HashMap<>();
        List<CoreDatasetTableField> dsTableFieldsList = new ArrayList();
        Map<Long, Long> datasourceIdMap = new HashMap<>();
        Map<Long, Map<String, String>> dsTableNamesMap = new HashMap<>();
        List<Long> newDatasourceId = new ArrayList<>();
        List<Long> excelDatasourceId = new ArrayList<>();
        Map<String, String> excelTableNamesMap = new HashMap<>();
        if (appData != null) {
            isAppSave = true;
            if ("dataset".equals(request.getDataType())) {
                appDatasetMatch(appData, datasourceIdMap, dsGroupIdMap, dsTableIdMap, dsTableFieldsIdMap,dsTableFieldsDatasetNameMap);
            } else {
                try {
                    List<AppCoreDatasourceVO> appCoreDatasourceVO = appData.getDatasourceInfo();
                    //  app 数据源 excel 表名映射
                    appCoreDatasourceVO.forEach(datasourceOld -> {
                        newDatasourceId.add(datasourceOld.getSystemDatasourceId());
                        // Excel 数据表明映射
                        if (StringUtils.isNotEmpty(datasourceOld.getConfiguration())) {
                            if (datasourceOld.getType().equals(DatasourceConfiguration.DatasourceType.API.name())) {
                                DEException.throwException(Translator.get("i18n_app_error_no_api"));
                            } else if (datasourceOld.getType().equals(DatasourceConfiguration.DatasourceType.Excel.name())) {
                                dsTableNamesMap.put(datasourceOld.getId(), ExcelUtils.getTableNamesMap(datasourceOld.getType(), datasourceOld.getConfiguration()));
                            } else if (datasourceOld.getType().contains(DatasourceConfiguration.DatasourceType.API.name())) {
                                dsTableNamesMap.put(datasourceOld.getId(), (Map<String, String>) datasourceServer.invokeMethod(datasourceOld.getType(), "getTableNamesMap", String.class, datasourceOld.getConfiguration()));
                            }
                        }
                    });

                    List<CoreDatasource> systemDatasource = coreDatasourceMapper.selectBatchIds(newDatasourceId);
                    systemDatasource.forEach(datasourceNew -> {
                        // Excel 数据表明映射
                        if (StringUtils.isNotEmpty(datasourceNew.getConfiguration())) {
                            if (datasourceNew.getType().equals(DatasourceConfiguration.DatasourceType.Excel.name())) {
                                dsTableNamesMap.put(datasourceNew.getId(), ExcelUtils.getTableNamesMap(datasourceNew.getType(), datasourceNew.getConfiguration()));
                                excelDatasourceId.add(datasourceNew.getId());
                            } else if (datasourceNew.getType().contains(DatasourceConfiguration.DatasourceType.API.name())) {
                                dsTableNamesMap.put(datasourceNew.getId(), (Map<String, String>) datasourceServer.invokeMethod(datasourceNew.getType(), "getTableNamesMap", String.class, datasourceNew.getConfiguration()));
                            }
                        }
                    });
                    datasourceIdMap.putAll(appData.getDatasourceInfo().stream().collect(Collectors.toMap(AppCoreDatasourceVO::getId, AppCoreDatasourceVO::getSystemDatasourceId)));
                    Long datasetFolderPid = request.getDatasetFolderPid();
                    String datasetFolderName = request.getDatasetFolderName();
                    //新建数据集分组
                    DatasetGroupInfoDTO datasetFolderNewRequest = new DatasetGroupInfoDTO();
                    datasetFolderNewRequest.setName(datasetFolderName);
                    datasetFolderNewRequest.setNodeType("folder");
                    datasetFolderNewRequest.setPid(datasetFolderPid);
                    DatasetGroupInfoDTO datasetFolderNew = datasetGroupManage.save(datasetFolderNewRequest, false, false);
                    Long datasetFolderNewId = datasetFolderNew.getId();
                    //新建数据集
                    appData.getDatasetGroupsInfo().forEach(appDatasetGroup -> {
                        if ("dataset".equals(appDatasetGroup.getNodeType())) {
                            Long oldId = appDatasetGroup.getId();
                            Long newId = IDUtils.snowID();
                            DatasetGroupInfoDTO datasetNewRequest = new DatasetGroupInfoDTO();
                            BeanUtils.copyBean(datasetNewRequest, appDatasetGroup);
                            datasetNewRequest.setId(newId);
                            datasetNewRequest.setCreateBy(AuthUtils.getUser().getUserId() + "");
                            datasetNewRequest.setUpdateBy(AuthUtils.getUser().getUserId() + "");
                            datasetNewRequest.setCreateTime(time);
                            datasetNewRequest.setLastUpdateTime(time);
                            datasetNewRequest.setPid(datasetFolderNewId);
                            try {
                                newDsGroupInfo.add(datasetNewRequest);
                                dsGroupIdMap.put(oldId, newId);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }

                    });
                    // 新建数据集表
                    appData.getDatasetTablesInfo().forEach(appCoreDatasetTableVO -> {
                        Long oldId = appCoreDatasetTableVO.getId();
                        Long newId = IDUtils.snowID();
                        CoreDatasetTable datasetTable = new CoreDatasetTable();
                        BeanUtils.copyBean(datasetTable, appCoreDatasetTableVO);
                        datasetTable.setDatasetGroupId(dsGroupIdMap.get(datasetTable.getDatasetGroupId()));
                        datasetTable.setId(newId);
                        datasetTable.setDatasourceId(datasourceIdMap.get(datasetTable.getDatasourceId()));
                        coreDatasetTableMapper.insert(datasetTable);
                        dsTableIdMap.put(oldId, newId);

                    });
                    // 新建数据字段
                    appData.getDatasetTableFieldsInfo().forEach(appDsTableFields -> {
                        Long oldId = appDsTableFields.getId();
                        Long newId = IDUtils.snowID();
                        CoreDatasetTableField dsDsField = new CoreDatasetTableField();
                        BeanUtils.copyBean(dsDsField, appDsTableFields);
                        dsDsField.setDatasetGroupId(dsGroupIdMap.get(dsDsField.getDatasetGroupId()));
                        dsDsField.setDatasetTableId(dsTableIdMap.get(dsDsField.getDatasetTableId()));
                        dsDsField.setDatasourceId(datasourceIdMap.get(dsDsField.getDatasourceId()));
                        dsDsField.setId(newId);
                        dsTableFieldsList.add(dsDsField);
                        dsTableFieldsIdMap.put(oldId, newId);
                    });

                    // dsTableFields 中存在计算字段在OriginName中 也需要替换
                    dsTableFieldsList.forEach(dsTableFields -> {
                        dsTableFieldsIdMap.forEach((key, value) -> {
                            dsTableFields.setOriginName(dsTableFields.getOriginName().replaceAll(key.toString(), value.toString()));
                        });
                        coreDatasetTableFieldMapper.insert(dsTableFields);
                    });

                    List<String> dsGroupNameSave = new ArrayList<>();
                    // 持久化数据集
                    newDsGroupInfo.forEach(dsGroup -> {
                        dsTableIdMap.forEach((key, value) -> {
                            dsGroup.setInfo(dsGroup.getInfo().replaceAll(key.toString(), value.toString()));
                        });

                        dsTableFieldsIdMap.forEach((key, value) -> {
                            dsGroup.setInfo(dsGroup.getInfo().replaceAll(key.toString(), value.toString()));
                        });

                        datasourceIdMap.forEach((key, value) -> {
                            dsGroup.setInfo(dsGroup.getInfo().replaceAll(key.toString(), value.toString()));
                            //表名映射更新
                            Map<String, String> appDsTableNamesMap = dsTableNamesMap.get(key);
                            Map<String, String> systemDsTableNamesMap = dsTableNamesMap.get(value);
                            if (MapUtils.isNotEmpty(appDsTableNamesMap)) {
                                appDsTableNamesMap.forEach((keyName, valueName) -> {
                                    if (MapUtils.isNotEmpty(systemDsTableNamesMap) && StringUtils.isNotEmpty(systemDsTableNamesMap.get(keyName))) {
                                        dsGroup.setInfo(dsGroup.getInfo().replaceAll(valueName, systemDsTableNamesMap.get(keyName)));
                                        excelTableNamesMap.put(valueName, systemDsTableNamesMap.get(keyName));
                                    } else {
                                        dsGroup.setInfo(dsGroup.getInfo().replaceAll(valueName, "excel_can_not_find"));
                                    }
                                });
                            }

                        });
                        if (dsGroupNameSave.contains(dsGroup.getName())) {
                            dsGroup.setName(dsGroup.getName() + "-" + UUID.randomUUID().toString());
                        }
                        dsGroupNameSave.add(dsGroup.getName());
                        if (dsGroup.getIsCross() == null) {
                            if (dsGroup.getUnion() == null) {
                                dsGroup.setUnion(JsonUtil.parseList(dsGroup.getInfo(), new TypeReference<>() {
                                }));
                            }
                            datasetSQLManage.mergeDatasetCrossDefault(dsGroup);
                        }
                        excelAdaptor(dsGroup, excelTableNamesMap, excelDatasourceId);
                        datasetGroupManage.innerSave(dsGroup);
                    });

                } catch (Exception e) {
                    LogUtil.error(e);
                    DEException.throwException(e);
                }
            }

            // 更换主数据内容
            AtomicReference<String> componentDataStr = new AtomicReference<>(request.getComponentData());
            dsGroupIdMap.forEach((key, value) -> {
                componentDataStr.set(componentDataStr.get().replaceAll(key.toString(), value.toString()));
            });
            dsTableIdMap.forEach((key, value) -> {
                componentDataStr.set(componentDataStr.get().replaceAll(key.toString(), value.toString()));
            });

            dsTableFieldsIdMap.forEach((key, value) -> {
                componentDataStr.set(componentDataStr.get().replaceAll(key.toString(), value.toString()));
            });

            datasourceIdMap.forEach((key, value) -> {
                componentDataStr.set(componentDataStr.get().replaceAll(key.toString(), value.toString()));
                //表名映射更新
                Map<String, String> appDsTableNamesMap = dsTableNamesMap.get(key);
                Map<String, String> systemDsTableNamesMap = dsTableNamesMap.get(value);
                if (MapUtils.isNotEmpty(appDsTableNamesMap) && MapUtils.isNotEmpty(systemDsTableNamesMap)) {
                    appDsTableNamesMap.forEach((keyName, valueName) -> {
                        if (StringUtils.isNotEmpty(systemDsTableNamesMap.get(keyName))) {
                            componentDataStr.set(componentDataStr.get().replaceAll(key.toString(), value.toString()));
                        }
                    });
                }

            });
            request.setComponentData(componentDataStr.get());
        }
        DataVisualizationInfo visualizationInfo = new DataVisualizationInfo();
        BeanUtils.copyBean(visualizationInfo, request);
        visualizationInfo.setNodeType(request.getNodeType() == null ? DataVisualizationConstants.NODE_TYPE.LEAF : request.getNodeType());
        if (request.getSelfWatermarkStatus() != null && request.getSelfWatermarkStatus()) {
            visualizationInfo.setSelfWatermarkStatus(1);
        } else {
            visualizationInfo.setSelfWatermarkStatus(0);
        }
        if (DataVisualizationConstants.RESOURCE_OPT_TYPE.COPY.equals(request.getOptType())) {
            // 复制更新 新建权限插入
            visualizationInfoMapper.deleteById(request.getId());
            snapshotMapper.deleteById(request.getId());
            visualizationInfo.setNodeType(DataVisualizationConstants.NODE_TYPE.LEAF);
        }
        // 文件夹走默认发布 非文件夹默认未发布
        visualizationInfo.setStatus(DataVisualizationConstants.NODE_TYPE.FOLDER.equals(visualizationInfo.getNodeType()) ? CommonConstants.DV_STATUS.PUBLISHED : CommonConstants.DV_STATUS.UNPUBLISHED);
        Long newDvId = coreVisualizationManage.innerSave(visualizationInfo);
        request.setId(newDvId);
        // 还原ID信息
        Map<Long, ChartViewDTO> canvasViews = request.getCanvasViewInfo();
        if (isAppSave) {
            Map<Long, String> canvasViewsStr = VisualizationUtils.viewTransToStr(canvasViews);
            canvasViewsStr.forEach((viewId, viewInfoStr) -> {
                AtomicReference<String> mutableViewInfoStr = new AtomicReference<>(viewInfoStr);
                datasourceIdMap.forEach((key, value) -> {
                    mutableViewInfoStr.set(mutableViewInfoStr.get().replaceAll(key.toString(), value.toString()));
                });
                dsTableIdMap.forEach((key, value) -> {
                    mutableViewInfoStr.set(mutableViewInfoStr.get().replaceAll(key.toString(), value.toString()));
                });
                dsTableFieldsIdMap.forEach((key, value) -> {
                    mutableViewInfoStr.set(mutableViewInfoStr.get().replaceAll(key.toString(), value.toString()));
                });
                dsGroupIdMap.forEach((key, value) -> {
                    mutableViewInfoStr.set(mutableViewInfoStr.get().replaceAll(key.toString(), value.toString()));
                });
                dsTableFieldsDatasetNameMap.forEach((key, value) -> {
                    mutableViewInfoStr.set(mutableViewInfoStr.get().replaceAll(key, value));
                });
                canvasViewsStr.put(viewId, mutableViewInfoStr.get());
            });
            canvasViews = VisualizationUtils.viewTransToObj(canvasViewsStr);
            canvasViews.forEach((key, viewInfo) -> {
                viewInfo.setDataFrom("dataset");
                if (viewInfo.getTableId() == null) {
                    viewInfo.setTableId(viewInfo.getSourceTableId());
                }
            });
        }
        //保存图表信息
        chartDataManage.saveChartViewFromVisualization(request.getComponentData(), newDvId, canvasViews);
        return newDvId.toString();
    }

    /**
     * Excel数据适配器
     * 处理Excel数据源的表名映射
     *
     * @param dsInfo 数据集信息
     * @param excelTableNamesMap Excel表名映射
     * @param excelDsId Excel数据源ID列表
     */
    private void excelAdaptor(DatasetGroupInfoDTO dsInfo, Map<String, String> excelTableNamesMap, List<Long> excelDsId) {
        List<UnionDTO> unionDTOList = JsonUtil.parseList(dsInfo.getInfo(), new TypeReference<>() {
        });
        if (CollectionUtils.isNotEmpty(excelDsId) && MapUtils.isNotEmpty(excelTableNamesMap)) {
            for (UnionDTO unionDTO : unionDTOList) {
                DatasetTableDTO tableDTO = unionDTO.getCurrentDs();
                if (excelDsId.contains(tableDTO.getDatasourceId())) {
                    DatasetTableInfoDTO infoDTO = JsonUtil.parseObject(tableDTO.getInfo(), DatasetTableInfoDTO.class);
                    String s = new String(Base64.getDecoder().decode(infoDTO.getSql()));
                    excelTableNamesMap.forEach((key, value) -> {
                        infoDTO.setSql(Base64.getEncoder().encodeToString(s.replaceAll(key, value).getBytes()));
                    });
                    tableDTO.setInfo((String) JsonUtil.toJSONString(infoDTO));
                }

            }
        }
        dsInfo.setInfo((String) JsonUtil.toJSONString(unionDTOList));
    }

    /**
     * 应用画布名称检查
     * 检查数据集文件夹名称是否重复
     *
     * @param request 检查请求对象
     * @return "repeat"表示重复，"success"表示可用
     * @throws Exception 检查过程中的异常
     */
    @Override
    public String appCanvasNameCheck(DataVisualizationBaseRequest request) throws Exception {
        Long datasetFolderPid = request.getDatasetFolderPid();
        String datasetFolderName = request.getDatasetFolderName();
        QueryWrapper<CoreDatasetGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", datasetFolderName);
        queryWrapper.eq("pid", datasetFolderPid);
        queryWrapper.eq("node_type", DataVisualizationConstants.NODE_TYPE.FOLDER);
        if (coreDatasetGroupMapper.exists(queryWrapper)) {
            return "repeat";
        } else {
            return "success";
        }
    }

    /**
     * 检查画布变更
     * 验证内容ID是否一致，用于检查资源是否被其他用户修改
     *
     * @param request 检查请求对象
     * @return "Repeat"表示内容已被修改，"Success"表示内容一致
     */
    @Override
    public String checkCanvasChange(DataVisualizationBaseRequest request) {
        Long dvId = request.getId();
        if (dvId == null) {
            DEException.throwException("ID can not be null");
        }
        // 内容ID校验
        QueryWrapper<DataVisualizationInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("content_id", request.getContentId());
        queryWrapper.eq("id", dvId);
        if (!visualizationInfoMapper.exists(queryWrapper)) {
            return "Repeat";
        }
        return "Success";
    }

    /**
     * 更新画布（仪表板或数据大屏）
     *
     * @param request 更新请求对象
     * @return 更新后的可视化信息
     */
    @DeLog(id = "#p0.id", ot = LogOT.MODIFY, stExp = "#p0.type")
    @Override
    @Transactional
    public DataVisualizationVO updateCanvas(DataVisualizationBaseRequest request) {
        for (Map.Entry<Long, ChartViewDTO> ele : request.getCanvasViewInfo().entrySet()) {
            DatasetUtils.viewDecode(ele.getValue());
        }
        Long dvId = request.getId();
        if (dvId == null) {
            DEException.throwException("ID can not be null");
        }
        DataVisualizationInfo visualizationInfo = new DataVisualizationInfo();
        BeanUtils.copyBean(visualizationInfo, request);
        if (request.getSelfWatermarkStatus() != null && request.getSelfWatermarkStatus()) {
            visualizationInfo.setSelfWatermarkStatus(1);
        } else {
            visualizationInfo.setSelfWatermarkStatus(0);
        }

        // 检查当前节点的pid是否一致如果不一致 需要调用move 接口(预存 可能会出现pid =-1的情况)
        if (request.getPid() != -1) {
            QueryWrapper<DataVisualizationInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("pid", request.getPid());
            queryWrapper.eq("id", dvId);
            if (!visualizationInfoMapper.exists(queryWrapper)) {
                request.setMoveFromUpdate(true);
                coreVisualizationManage.move(request);
            }
        }
        // 状态修改统一为后端操作：历史状态检查 如果 状态为 0（未发布） 或者 2（已发布未保存）则状态不变
        // 如果当前状态为 1 则状态修改为  2（已发布未保存）
        Integer curStatus = extDataVisualizationMapper.findDvInfoStats(dvId);
        visualizationInfo.setStatus(curStatus == 1 ? CommonConstants.DV_STATUS.SAVED_UNPUBLISHED : curStatus);
        coreVisualizationManage.innerEdit(visualizationInfo);
        //保存图表信息
        chartDataManage.saveChartViewFromVisualization(request.getComponentData(), dvId, request.getCanvasViewInfo());
        return new DataVisualizationVO(visualizationInfo.getStatus());
    }

    /**
     * 更新发布状态
     *
     * <p>状态处理逻辑：</p>
     * <ul>
     * <li>如果当前传入状态是1（已发布），则原始状态0（未发布）-》1（已发布）；2（已保存未发布）-》1（已发布）</li>
     * <li>统一处理为1.删除主表数据，2.将镜像表数据统一copy到主表 不删除镜像数据（发布状态后镜像数据和主表数据是保持一致的）</li>
     * <li>其他状态仅更新主表和镜像表状态</li>
     * </ul>
     *
     * @param request 更新请求对象
     */
    @Override
    @Transactional
    public void updatePublishStatus(DataVisualizationBaseRequest request) {
        /**
         * 如果当前传入状态是1（已发布），则原始状态0（未发布）-》1（已发布）；2（已保存未发布）-》1（已发布）
         * 统一处理为1.删除主表数据，2.将镜像表数据统一copy到主表 不删除镜像数据（发布状态后镜像数据和主表数据是保持一致的）
         * 其他状态仅更新主表和镜像表状态
         * */
        Long dvId = request.getId();
        DataVisualizationInfo visualizationInfo = new DataVisualizationInfo();
        visualizationInfo.setMobileLayout(request.getMobileLayout());
        visualizationInfo.setId(dvId);
        visualizationInfo.setName(request.getName());
        visualizationInfo.setStatus(request.getStatus());
        coreVisualizationManage.innerEdit(visualizationInfo);
        if (CommonConstants.DV_STATUS.PUBLISHED == request.getStatus()) {
            List<Long> viewIds = this.getEnabledViewIds(dvId, CommonConstants.RESOURCE_TABLE.SNAPSHOT);
            extDataVisualizationMapper.deleteUselessViewsBatchSnapshot(viewIds, dvId);
            coreVisualizationManage.removeDvCore(dvId);
            coreVisualizationManage.dvRestore(dvId);
            chartViewManege.publishThreshold(dvId, request.getActiveViewIds());
        } else if (CommonConstants.DV_STATUS.UNPUBLISHED == request.getStatus()) {
            chartViewManege.publishThreshold(dvId, request.getActiveViewIds());
        }
    }

    /**
     * 恢复到已发布状态
     * 将仪表板恢复到最近一次发布的状态
     *
     * @param request 恢复请求对象
     */
    @Override
    public void recoverToPublished(DataVisualizationBaseRequest request) {
        coreVisualizationManage.dvSnapshotRecover(request.getId());
        DataVisualizationInfo visualizationInfo = new DataVisualizationInfo();
        visualizationInfo.setId(request.getId());
        visualizationInfo.setName(request.getName());
        visualizationInfo.setStatus(CommonConstants.DV_STATUS.PUBLISHED);
        coreVisualizationManage.innerEdit(visualizationInfo);
    }

    /**
     * 更新基础信息
     *
     * <p>为什么单独接口：</p>
     * <ul>
     * <li>1.基础信息更新频繁数据且数据载量较小</li>
     * <li>2.防止出现更新过多信息的情况，造成图表的误删等操作</li>
     * </ul>
     *
     * @param request 更新请求对象
     */
    @DeLog(id = "#p0.id", ot = LogOT.MODIFY, stExp = "#p0.type")
    @Override
    @Transactional
    public void updateBase(DataVisualizationBaseRequest request) {
        if (request.getId() != null) {
            coreVisualizationManage.innerEdit(BeanUtils.copyBean(new DataVisualizationInfo(), request));
        } else {
            DEException.throwException("Id can not be null");
        }
    }

    /**
     * 逻辑删除可视化信息
     *
     * <p>将delete_flag 置为0</p>
     *
     * @param dvId 数据可视化ID
     * @param busiFlag 业务标识
     */
    @DeLog(id = "#p0", ot = LogOT.DELETE, stExp = "#p1")
    @Transactional
    @Override
    public void deleteLogic(Long dvId, String busiFlag) {
        coreVisualizationManage.delete(dvId);
    }

    /**
     * 资源树类型适配器
     * 递归设置资源树节点的类型
     *
     * @param tree 资源树节点列表
     * @param type 资源类型
     */
    private void resourceTreeTypeAdaptor(List<BusiNodeVO> tree, String type) {
        if (!CollectionUtils.isEmpty(tree)) {
            tree.forEach(busiNodeVO -> {
                busiNodeVO.setType(type);
                resourceTreeTypeAdaptor(busiNodeVO.getChildren(), type);
            });
        }
    }

    /**
     * 查询资源树
     *
     * @param request 查询请求对象
     * @return 资源树节点列表
     */
    @Override
    public List<BusiNodeVO> tree(BusiNodeRequest request) {
        if (StringUtils.isEmpty(request.getResourceTable())) {
            request.setResourceTable(CommonConstants.RESOURCE_TABLE.SNAPSHOT);
        }
        String busiFlag = request.getBusiFlag();
        if (busiFlag.equals("dashboard-dataV")) {
            BusiNodeRequest requestDv = new BusiNodeRequest();
            BeanUtils.copyBean(requestDv, request);
            requestDv.setBusiFlag("dashboard");
            List<BusiNodeVO> dashboardResult = coreVisualizationManage.tree(requestDv);
            requestDv.setBusiFlag("dataV");
            List<BusiNodeVO> dataVResult = coreVisualizationManage.tree(requestDv);
            List<BusiNodeVO> result = new ArrayList<>();
            if (!CollectionUtils.isEmpty(dashboardResult)) {
                resourceTreeTypeAdaptor(dashboardResult, "dashboard");
                BusiNodeVO dashboardResultParent = new BusiNodeVO();
                dashboardResultParent.setName(Translator.get("i18n_menu.panel"));
                dashboardResultParent.setId(-101L);
                if (dashboardResult.get(0).getId() == 0) {
                    dashboardResultParent.setChildren(dashboardResult.get(0).getChildren());
                } else {
                    dashboardResultParent.setChildren(dashboardResult);
                }
                result.add(dashboardResultParent);
            }
            if (!CollectionUtils.isEmpty(dataVResult)) {
                resourceTreeTypeAdaptor(dataVResult, "dataV");
                BusiNodeVO dataVResultParent = new BusiNodeVO();
                dataVResultParent.setName(Translator.get("i18n_menu.screen"));
                dataVResultParent.setId(-102L);
                if (dataVResult.get(0).getId() == 0) {
                    dataVResultParent.setChildren(dataVResult.get(0).getChildren());
                } else {
                    dataVResultParent.setChildren(dataVResult);
                }
                result.add(dataVResultParent);
            }
            return result;
        } else {
            return coreVisualizationManage.tree(request);
        }
    }

    /**
     * 查询交互式资源树
     * 支持多个业务类型的资源树查询
     *
     * @param requestMap 请求映射，key为业务标识，value为查询请求
     * @return 交互式资源树映射
     */
    @Override
    public Map<String, List<BusiNodeVO>> interactiveTree(Map<String, BusiNodeRequest> requestMap) {
        return coreBusiManage.interactiveTree(requestMap);
    }

    /**
     * 移动可视化资源
     * 将资源移动到新的父节点下
     *
     * @param request 移动请求对象
     */
    @DeLog(id = "#p0.id", pid = "#p0.pid", ot = LogOT.MODIFY, stExp = "#p0.type")
    @Transactional
    @Override
    public void move(DataVisualizationBaseRequest request) {
        coreVisualizationManage.move(request);
    }

    /**
     * 查询最近使用的资源
     *
     * @param request 查询请求对象
     * @return 最近使用的资源列表
     */
    @Override
    public List<VisualizationResourceVO> findRecent(@RequestBody VisualizationWorkbranchQueryRequest request) {
        request.setQueryFrom("recent");
        IPage<VisualizationResourceVO> result = coreVisualizationManage.query(1, 20, request);
        List<VisualizationResourceVO> resourceVOS = result.getRecords();
        if (!CollectionUtils.isEmpty(resourceVOS)) {
            resourceVOS.forEach(item -> {
                item.setCreator(StringUtils.equals(item.getCreator(), "1") ? Translator.get("i18n_sys_admin") : item.getCreator());
                item.setLastEditor(StringUtils.equals(item.getLastEditor(), "1") ? Translator.get("i18n_sys_admin") : item.getLastEditor());
            });
        }
        return result.getRecords();
    }

    /**
     * 复制仪表板
     *
     * <p>复制步骤：</p>
     * <ul>
     * <li>1.复制基础可视化数据</li>
     * <li>2.复制图表数据</li>
     * <li>3.附加数据（包括联动信息，跳转信息，外部参数信息等仪表板附加信息）</li>
     * </ul>
     *
     * @param request 复制请求对象
     * @return 新复制的仪表板ID
     */
    @Transactional
    @Override
    public String copy(DataVisualizationBaseRequest request) {
        Long sourceDvId = request.getId(); //源仪表板ID
        Long newDvId = IDUtils.snowID(); //目标仪表板ID
        Long copyId = IDUtils.snowID() / 100; // 本次复制执行ID
        // 复制仪表板
        DataVisualizationInfo newDv = visualizationInfoMapper.selectById(sourceDvId);
        newDv.setName(request.getName());
        newDv.setId(newDvId);
        newDv.setPid(request.getPid());
        newDv.setCreateTime(System.currentTimeMillis());
        // 复制图表 chart_view
        extDataVisualizationMapper.viewCopyWithDv(sourceDvId, newDvId, copyId, CommonConstants.RESOURCE_TABLE.CORE);
        extDataVisualizationMapper.viewCopyWithDv(sourceDvId, newDvId, copyId, CommonConstants.RESOURCE_TABLE.SNAPSHOT);
        List<CoreChartView> viewList = extDataVisualizationMapper.findViewInfoByCopyId(copyId);
        if (!CollectionUtils.isEmpty(viewList)) {
            String componentData = newDv.getComponentData();
            // componentData viewId 数据  并保存
            for (CoreChartView viewInfo : viewList) {
                componentData = componentData.replaceAll(String.valueOf(viewInfo.getCopyFrom()), String.valueOf(viewInfo.getId()));
            }
            newDv.setComponentData(componentData);
        }
        // 复制图表联动信息
        extDataVisualizationMapper.copyLinkage(copyId);
        extDataVisualizationMapper.copyLinkageField(copyId);
        // 复制图表跳转信息
        extDataVisualizationMapper.copyLinkJump(copyId);
        extDataVisualizationMapper.copyLinkJumpInfo(copyId);
        extDataVisualizationMapper.copyLinkJumpTargetInfo(copyId);
        DataVisualizationInfo visualizationInfoTarget = new DataVisualizationInfo();
        BeanUtils.copyBean(visualizationInfoTarget, newDv);
        visualizationInfoTarget.setPid(-1L);
        coreVisualizationManage.preInnerSave(visualizationInfoTarget);
        return String.valueOf(newDvId);
    }

    /**
     * 查找可视化资源类型
     *
     * @param dvId 数据可视化ID
     * @return 资源类型（dashboard或dataV）
     * @throws DEException 当资源不存在时抛出异常
     */
    @Override
    public String findDvType(Long dvId) {
        String result = extDataVisualizationMapper.findDvType(dvId);
        if (StringUtils.isEmpty(result)) {
            DEException.throwException(Translator.get("i18n_resource_not_exists"));
        }
        return result;
    }

    /**
     * 更新检查版本
     * 更新资源的版本检查信息
     *
     * @param dvId 数据可视化ID
     * @return 空字符串
     */
    @Override
    public String updateCheckVersion(Long dvId) {
        DataVisualizationInfo updateInfo = new DataVisualizationInfo();
        updateInfo.setId(dvId);
        updateInfo.setCheckVersion(coreLicManage.getVersion());
        visualizationInfoMapper.updateById(updateInfo);
        return "";
    }

    /**
     * 解压模板数据
     * 从模板（内部模板、外部模板或市场模板）创建新的可视化资源
     *
     * @param request 解压请求对象
     * @return 可视化资源信息
     * @throws Exception 解压过程中的异常
     */
    @Override
    public DataVisualizationVO decompression(DataVisualizationBaseRequest request) throws Exception {
        try {
            Long newDvId = IDUtils.snowID();
            String newFrom = request.getNewFrom();
            String templateStyle = null;
            String templateData = null;
            String dynamicData = null;
            String staticResource = null;
            String appDataStr = null;
            String name = null;
            String dvType = null;
            Integer version = null;
            //内部模板新建
            if (DataVisualizationConstants.NEW_PANEL_FROM.NEW_INNER_TEMPLATE.equals(newFrom)) {
                VisualizationTemplate visualizationTemplate = templateMapper.selectById(request.getTemplateId());
                templateStyle = visualizationTemplate.getTemplateStyle();
                templateData = visualizationTemplate.getTemplateData();
                dynamicData = visualizationTemplate.getDynamicData();
                name = visualizationTemplate.getName();
                dvType = visualizationTemplate.getDvType();
                version = visualizationTemplate.getVersion();
                appDataStr = visualizationTemplate.getAppData();
                // 模板市场记录
                coreOptRecentManage.saveOpt(request.getTemplateId(), OptConstants.OPT_RESOURCE_TYPE.TEMPLATE, OptConstants.OPT_TYPE.NEW);
                VisualizationTemplate visualizationTemplateUpdate = new VisualizationTemplate();
                visualizationTemplateUpdate.setId(visualizationTemplate.getId());
                visualizationTemplateUpdate.setUseCount(visualizationTemplate.getUseCount() == null ? 0 : visualizationTemplate.getUseCount() + 1);
                templateMapper.updateById(visualizationTemplateUpdate);
            } else if (DataVisualizationConstants.NEW_PANEL_FROM.NEW_OUTER_TEMPLATE.equals(newFrom)) {
                templateStyle = request.getCanvasStyleData();
                templateData = request.getComponentData();
                dynamicData = request.getDynamicData();
                staticResource = request.getStaticResource();
                name = request.getName();
                dvType = request.getType();
            } else if (DataVisualizationConstants.NEW_PANEL_FROM.NEW_MARKET_TEMPLATE.equals(newFrom)) {
                TemplateManageFileDTO templateFileInfo = templateCenterManage.getTemplateFromMarketV2(request.getResourceName());
                if (templateFileInfo == null) {
                    DEException.throwException("Can't find the template's info from market,please check");
                }
                templateStyle = templateFileInfo.getCanvasStyleData();
                templateData = templateFileInfo.getComponentData();
                dynamicData = templateFileInfo.getDynamicData();
                staticResource = templateFileInfo.getStaticResource();
                name = templateFileInfo.getName();
                dvType = templateFileInfo.getDvType();
                version = templateFileInfo.getVersion();
                appDataStr = templateFileInfo.getAppData();
                // 模板市场记录
                coreOptRecentManage.saveOpt(request.getResourceName(), OptConstants.OPT_RESOURCE_TYPE.TEMPLATE, OptConstants.OPT_TYPE.NEW);
            }
            if (StringUtils.isNotEmpty(appDataStr) && appDataStr.length() > 10) {
                try {
                    VisualizationExport2AppVO appDataFormat = JsonUtil.parseObject(appDataStr, VisualizationExport2AppVO.class);
                    String dvInfo = appDataFormat.getVisualizationInfo();
                    VisualizationBaseInfoVO baseInfoVO = JsonUtil.parseObject(dvInfo, VisualizationBaseInfoVO.class);
                    Long sourceDvId = baseInfoVO.getId();
                    appDataStr = appDataStr.replaceAll(sourceDvId.toString(), newDvId.toString());
                } catch (Exception e) {
                    LogUtil.error(e);
                    appDataStr = null;
                }
            } else {
                appDataStr = null;
            }
            // 解析动态数据
            Map<String, String> dynamicDataMap = JsonUtil.parseObject(dynamicData, Map.class);
            List<ChartViewDTO> chartViews = new ArrayList<>();
            Map<Long, ChartViewDTO> canvasViewInfo = new HashMap<>();
            Map<Long, VisualizationTemplateExtendDataDTO> extendDataInfo = new HashMap<>();
            for (Map.Entry<String, String> entry : dynamicDataMap.entrySet()) {
                String originViewId = entry.getKey();
                Object viewInfo = entry.getValue();
                try {
                    // 旧模板图表过滤器适配
                    if (viewInfo instanceof Map && ((Map) viewInfo).get("customFilter") instanceof ArrayList) {
                        ((Map) viewInfo).put("customFilter", new HashMap<>());
                    }
                } catch (Exception e) {
                    LogUtil.error("History Adaptor Error", e);
                }
                String originViewData = JsonUtil.toJSONString(entry.getValue()).toString();
                ChartViewDTO chartView = JsonUtil.parseObject(originViewData, ChartViewDTO.class);
                if (chartView == null) {
                    continue;
                }
                Long newViewId = IDUtils.snowID();
                chartView.setId(newViewId);
                chartView.setSceneId(newDvId);
                chartView.setSourceTableId(chartView.getTableId());
                chartView.setTableId(null);

                chartView.setDataFrom(CommonConstants.VIEW_DATA_FROM.TEMPLATE);
                // 数据处理 1.替换viewId 2.加入模板view data数据
                VisualizationTemplateExtendDataDTO extendDataDTO = new VisualizationTemplateExtendDataDTO(newDvId, newViewId, originViewData);
                extendDataInfo.put(newViewId, extendDataDTO);
                templateData = templateData.replaceAll(originViewId, newViewId.toString());
                if (StringUtils.isNotEmpty(appDataStr)) {
                    chartView.setTableId(chartView.getSourceTableId());
                    appDataStr = appDataStr.replaceAll(originViewId, newViewId.toString());
                }
                canvasViewInfo.put(chartView.getId(), chartView);
                //插入模板数据 此处预先插入减少数据交互量
                VisualizationTemplateExtendData extendData = new VisualizationTemplateExtendData();
                templateExtendDataMapper.insert(BeanUtils.copyBean(extendData, extendDataDTO));
            }
            request.setComponentData(templateData);
            request.setCanvasStyleData(templateStyle);
            //Store static resource into the server
            staticResourceServer.saveFilesToServe(staticResource);
            return new DataVisualizationVO(newDvId, name, dvType, version, templateStyle, templateData, appDataStr, canvasViewInfo, null);
        } catch (Exception e) {
            e.printStackTrace();
            DEException.throwException("解析错误");
            return null;
        }

    }

    /**
     * 解压本地文件
     * 从本地文件导入可视化资源
     *
     * @param file 上传的文件
     * @return 可视化资源信息
     */
    @Override
    public DataVisualizationVO decompressionLocalFile(MultipartFile file) {
        return null;
    }

    /**
     * 查询可视化资源详情列表
     * 获取仪表板中使用的所有图表视图详情
     *
     * @param dvId 数据可视化ID
     * @return 图表视图详情列表
     */
    @Override
    public List<VisualizationViewTableDTO> detailList(Long dvId) {
        List<VisualizationViewTableDTO> result = extDataVisualizationMapper.getVisualizationViewDetails(dvId);
        SnapshotDataVisualizationInfo dvInfo = snapshotMapper.selectById(dvId);
        if (dvInfo != null && !CollectionUtils.isEmpty(result)) {
            String componentData = dvInfo.getComponentData();
            return result.stream().filter(item -> componentData.indexOf("\"id\":\"" + item.getId()) > 0).toList();
        } else {
            return result;
        }
    }

    /**
     * 导出应用前的检查
     * 检查导出应用所需的所有数据是否完整
     *
     * @param appExportRequest 应用导出请求对象
     * @return 应用导出数据信息
     */
    @Override
    public VisualizationExport2AppVO export2AppCheck(VisualizationAppExportRequest appExportRequest) {
        List<Long> viewIds = appExportRequest.getViewIds();
        List<Long> dsIds = appExportRequest.getDsIds();
        Long dvId = appExportRequest.getDvId();
        List<AppCoreChartViewVO> chartViewVOInfo = null;
        List<AppCoreDatasetGroupVO> datasetGroupVOInfo = null;
        List<AppCoreDatasetTableVO> datasetTableVOInfo = null;
        List<AppCoreDatasetTableFieldVO> datasetTableFieldVOInfo = null;
        List<AppCoreDatasourceVO> datasourceVOInfo = null;
        List<AppCoreDatasourceTaskVO> datasourceTaskVOInfo = null;
        //获取所有视图信息
        if (!CollectionUtils.isEmpty(viewIds)) {
            chartViewVOInfo = appTemplateMapper.findAppViewInfo(viewIds);
        }
        if (!CollectionUtils.isEmpty(dsIds)) {
            datasetGroupVOInfo = appTemplateMapper.findAppDatasetGroupInfo(dsIds);
            datasetTableVOInfo = appTemplateMapper.findAppDatasetTableInfo(dsIds);
            datasetTableFieldVOInfo = appTemplateMapper.findAppDatasetTableFieldInfo(dsIds);
            datasourceVOInfo = appTemplateMapper.findAppDatasourceInfo(dsIds);
            datasourceTaskVOInfo = appTemplateMapper.findAppDatasourceTaskInfo(dsIds);
        }

        if (CollectionUtils.isEmpty(datasourceVOInfo)) {
            DEException.throwException("当前不存在数据源无法导出");
        } else if (datasourceVOInfo.stream().anyMatch(datasource -> datasource.getType().contains(DatasourceConfiguration.DatasourceType.API.name()))) {
            DEException.throwException(Translator.get("i18n_app_error_no_api"));
        }

        List<VisualizationLinkageVO> linkageVOInfo = appTemplateMapper.findAppLinkageInfo(dvId);
        List<VisualizationLinkageFieldVO> linkageFieldVOInfo = appTemplateMapper.findAppLinkageFieldInfo(dvId);
        List<VisualizationLinkJumpVO> linkJumpVOInfo = appTemplateMapper.findAppLinkJumpInfo(dvId);
        List<VisualizationLinkJumpInfoVO> linkJumpInfoVOInfo = appTemplateMapper.findAppLinkJumpInfoInfo(dvId);
        List<VisualizationLinkJumpTargetViewInfoVO> listJumpTargetViewInfoVO = appTemplateMapper.findAppLinkJumpTargetViewInfoInfo(dvId);

        return new VisualizationExport2AppVO(chartViewVOInfo, datasetGroupVOInfo, datasetTableVOInfo, datasetTableFieldVOInfo, datasourceVOInfo, datasourceTaskVOInfo, linkJumpVOInfo, linkJumpInfoVOInfo, listJumpTargetViewInfoVO, linkageVOInfo, linkageFieldVOInfo);
    }

    /**
     * 记录应用导出日志
     *
     * @param request 请求对象
     */
    @DeLog(id = "#p0.id", ot = LogOT.APP_TEMPLATE_EXPORT, stExp = "#p0.type")
    public void exportLogApp(DataVisualizationBaseRequest request) {

    }

    /**
     * 记录模板导出日志
     *
     * @param request 请求对象
     */
    @DeLog(id = "#p0.id", ot = LogOT.TEMPLATE_EXPORT, stExp = "#p0.type")
    public void exportLogTemplate(DataVisualizationBaseRequest request) {

    }

    /**
     * 记录PDF导出日志
     *
     * @param request 请求对象
     */
    @DeLog(id = "#p0.id", ot = LogOT.PDF_EXPORT, stExp = "#p0.type")
    public void exportLogPDF(DataVisualizationBaseRequest request) {

    }

    /**
     * 记录图片导出日志
     *
     * @param request 请求对象
     */
    @DeLog(id = "#p0.id", ot = LogOT.IMG_EXPORT, stExp = "#p0.type")
    public void exportLogImg(DataVisualizationBaseRequest request) {

    }


    /**
     * 名称检查
     * 检查资源名称是否重复
     *
     * @param request 检查请求对象
     * @throws DEException 当名称已存在时抛出异常
     */
    @Override
    public void nameCheck(DataVisualizationBaseRequest request) {
        QueryWrapper<DataVisualizationInfo> wrapper = new QueryWrapper<>();
        if (DataVisualizationConstants.RESOURCE_OPT_TYPE.MOVE.equals(request.getOpt()) || DataVisualizationConstants.RESOURCE_OPT_TYPE.RENAME.equals(request.getOpt()) || DataVisualizationConstants.RESOURCE_OPT_TYPE.EDIT.equals(request.getOpt()) || DataVisualizationConstants.RESOURCE_OPT_TYPE.COPY.equals(request.getOpt())) {
            if (request.getPid() == null) {
                DataVisualizationInfo result = visualizationInfoMapper.selectById(request.getId());
                request.setPid(result.getPid());
            }
            if (DataVisualizationConstants.RESOURCE_OPT_TYPE.MOVE.equals(request.getOpt()) || DataVisualizationConstants.RESOURCE_OPT_TYPE.RENAME.equals(request.getOpt()) || DataVisualizationConstants.RESOURCE_OPT_TYPE.EDIT.equals(request.getOpt())) {
                wrapper.ne("id", request.getId());
            }
        }
        wrapper.eq("delete_flag", 0);
        wrapper.eq("pid", request.getPid());
        wrapper.ne("pid", -1);
        wrapper.eq("name", request.getName().trim());
        wrapper.eq("node_type", request.getNodeType());
        wrapper.eq("type", request.getType());
        if (AuthUtils.getUser().getDefaultOid() != null) {
            wrapper.eq("org_id", AuthUtils.getUser().getDefaultOid());
        }
        List<DataVisualizationInfo> existList = visualizationInfoMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(existList) && existList.stream().anyMatch(item -> item.getName().equals(request.getName().trim()))) {
            DEException.throwException("当前名称已经存在");
        }
    }

    /**
     * 获取图表的绝对路径
     *
     * @param id 图表ID
     * @return 绝对路径字符串
     */
    public String getAbsPath(Long id) {
        ChartViewDTO viewDTO = chartViewManege.findChartViewAround(String.valueOf(id));
        if (viewDTO == null) {
            return null;
        }
        if (viewDTO.getPid() == null) {
            return viewDTO.getTitle();
        }
        List<DataVisualizationInfo> parents = getParents(viewDTO.getPid());
        StringBuilder stringBuilder = new StringBuilder();
        parents.forEach(ele -> {
            if (ObjectUtils.isNotEmpty(ele)) {
                stringBuilder.append(ele.getName()).append("/");
            }
        });
        stringBuilder.append(viewDTO.getTitle());
        return stringBuilder.toString();
    }

    /**
     * 获取父节点列表
     *
     * @param id 当前节点ID
     * @return 父节点列表（从根节点到直接父节点）
     */
    public List<DataVisualizationInfo> getParents(Long id) {
        List<DataVisualizationInfo> list = new ArrayList<>();
        DataVisualizationInfo dataVisualizationInfo = visualizationInfoMapper.selectById(id);
        list.add(dataVisualizationInfo);
        if (dataVisualizationInfo.getPid().equals(dataVisualizationInfo.getId())) {
            return list;
        }
        getParent(list, dataVisualizationInfo);
        Collections.reverse(list);
        return list;
    }

    /**
     * 递归获取父节点
     *
     * @param list 父节点列表
     * @param dataVisualizationInfo 当前节点信息
     */
    public void getParent(List<DataVisualizationInfo> list, DataVisualizationInfo dataVisualizationInfo) {
        if (ObjectUtils.isNotEmpty(dataVisualizationInfo) && dataVisualizationInfo.getPid() != null && !dataVisualizationInfo.getPid().equals(dataVisualizationInfo.getId())) {
            DataVisualizationInfo d = visualizationInfoMapper.selectById(dataVisualizationInfo.getPid());
            list.add(d);
            getParent(list, d);
        }
    }

    /**
     * 获取启用的视图ID列表
     * 从仪表板的组件数据中筛选出实际使用的图表视图
     *
     * @param dvId 数据可视化ID
     * @param resourceTable 资源表类型
     * @return 启用的视图ID列表
     */
    public List<Long> getEnabledViewIds(Long dvId, String resourceTable) {
        List<Long> result = new ArrayList<>();
        DataVisualizationVO dvInfo = extDataVisualizationMapper.findDvInfo(dvId, null, resourceTable);
        List<CoreChartView> views = extChartViewMapper.selectListCustom(dvId, resourceTable);
        if (CollectionUtils.isNotEmpty(views) && dvInfo != null) {
            String componentData = dvInfo.getComponentData();
            result = views.stream().filter(item -> componentData.indexOf("\"id\":\"" + item.getId()) > 0).map(CoreChartView::getId).collect(Collectors.toList());

        }
        return result;
    }
}
