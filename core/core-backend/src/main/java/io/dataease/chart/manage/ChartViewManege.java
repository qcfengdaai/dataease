package io.dataease.chart.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dataease.api.chart.vo.ChartBaseVO;
import io.dataease.api.chart.vo.ViewSelectorVO;
import io.dataease.chart.dao.auto.entity.CoreChartView;
import io.dataease.chart.dao.auto.mapper.CoreChartViewMapper;
import io.dataease.chart.dao.ext.entity.ChartBasePO;
import io.dataease.chart.dao.ext.mapper.ExtChartViewMapper;
import io.dataease.constant.CommonConstants;
import io.dataease.dataset.dao.auto.entity.CoreDatasetTableField;
import io.dataease.dataset.dao.auto.mapper.CoreDatasetTableFieldMapper;
import io.dataease.dataset.manage.DatasetTableFieldManage;
import io.dataease.dataset.manage.PermissionManage;
import io.dataease.dataset.utils.TableUtils;
import io.dataease.engine.constant.ExtFieldConstant;
import io.dataease.engine.func.FunctionConstant;
import io.dataease.engine.utils.Utils;
import io.dataease.exception.DEException;
import io.dataease.extensions.datasource.api.PluginManageApi;
import io.dataease.extensions.datasource.dto.CalParam;
import io.dataease.extensions.datasource.dto.DatasetTableFieldDTO;
import io.dataease.extensions.datasource.dto.FieldGroupDTO;
import io.dataease.extensions.datasource.model.SQLObj;
import io.dataease.extensions.view.dto.*;
import io.dataease.extensions.view.filter.FilterTreeObj;
import io.dataease.i18n.Lang;
import io.dataease.i18n.Translator;
import io.dataease.license.config.XpackInteract;
import io.dataease.utils.BeanUtils;
import io.dataease.utils.IDUtils;
import io.dataease.utils.JsonUtil;
import io.dataease.utils.LogUtil;
import io.dataease.visualization.dao.auto.entity.DataVisualizationInfo;
import io.dataease.visualization.dao.auto.entity.SnapshotCoreChartView;
import io.dataease.visualization.dao.auto.mapper.DataVisualizationInfoMapper;
import io.dataease.visualization.dao.auto.mapper.SnapshotCoreChartViewMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 图表视图管理类
 * 负责图表视图的增删改查和配置管理
 * 协调数据集、权限、可视化等多个模块完成图表的完整生命周期管理
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>图表的创建、编辑、删除</li>
 *   <li>图表配置的保存和加载</li>
 *   <li>图表快照管理</li>
 *   <li>字段复制和删除</li>
 *   <li>数据集关联检查</li>
 * </ul>
 *
 * @author Junjun
 */
@Component
public class ChartViewManege {
    @Resource
    private CoreChartViewMapper coreChartViewMapper;
    @Resource
    private SnapshotCoreChartViewMapper snapshotCoreChartViewMapper;
    @Resource
    private ChartDataManage chartDataManage;
    @Resource
    private CoreDatasetTableFieldMapper coreDatasetTableFieldMapper;
    @Resource
    private PermissionManage permissionManage;

    @Resource
    private DataVisualizationInfoMapper visualizationInfoMapper;

    @Resource
    private ExtChartViewMapper extChartViewMapper;

    @Resource
    private DatasetTableFieldManage datasetTableFieldManage;
    @Autowired(required = false)
    private PluginManageApi pluginManage;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 保存图表视图
     * 创建或更新图表视图配置信息
     *
     * @param chartViewDTO 图表视图数据传输对象
     * @return 保存后的图表视图信息
     * @throws Exception 当标题超过100字符或ID为空时抛出异常
     */
    @Transactional
    public ChartViewDTO save(ChartViewDTO chartViewDTO) throws Exception {
        if (chartViewDTO.getTitle().length() > 100) {
            DEException.throwException(Translator.get("i18n_name_limit_100"));
        }
        Long id = chartViewDTO.getId();
        if (id == null) {
            DEException.throwException(Translator.get("i18n_no_id"));
        }
        SnapshotCoreChartView coreChartView = snapshotCoreChartViewMapper.selectById(id);
        SnapshotCoreChartView record = transDTO2Record(chartViewDTO);
        if (ObjectUtils.isEmpty(coreChartView)) {
            snapshotCoreChartViewMapper.deleteById(record.getId());
            snapshotCoreChartViewMapper.insert(record);
        } else {
            UpdateWrapper<SnapshotCoreChartView> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", record.getId());
            //富文本允许设置空的tableId 这里额外更新一下
            if (record.getTableId() == null) {
                updateWrapper.set("table_id", null);
            }
            snapshotCoreChartViewMapper.update(record, updateWrapper);
        }
        return chartViewDTO;
    }

    /**
     * 删除图表视图
     * 根据图表ID删除对应的视图记录
     *
     * @param id 图表视图ID
     */
    public void delete(Long id) {
        coreChartViewMapper.deleteById(id);
    }

    /**
     * 停用图表视图
     * 批量停用指定的图表视图（企业版功能）
     *
     * @param chartIdList 图表ID列表
     */
    @XpackInteract(value = "chartViewManage")
    public void disuse(List<Long> chartIdList) {
    }

    /**
     * 镜像操作发布
     * 发布图表阈值的镜像配置（企业版功能）
     *
     * @param resourceId 资源ID
     * @param chartIdList 图表ID列表
     */
    //镜像操作发布
    @XpackInteract(value = "chartViewManage")
    public void publishThreshold(Long resourceId, List<Long> chartIdList) {
    }

    /**
     * 镜像操作删除
     * 删除图表阈值的镜像配置（企业版功能）
     *
     * @param resourceId 资源ID
     * @param resourceTable 资源表名称
     */
    //镜像操作删除
    @XpackInteract(value = "chartViewManage")
    public void removeThreshold(Long resourceId, String resourceTable) {

    }

    /**
     * 镜像操作恢复
     * 恢复图表阈值的镜像配置（企业版功能）
     *
     * @param resourceId 资源ID
     * @param resourceTable 资源表名称
     */
    //镜像操作恢复
    @XpackInteract(value = "chartViewManage")
    public void restoreThreshold(Long resourceId, String resourceTable) {
    }

    /**
     * 根据场景ID批量删除图表
     * 删除指定场景下除指定图表ID外的所有图表
     *
     * @param sceneId 场景ID（仪表板或数据大屏ID）
     * @param chartIds 保留的图表ID列表
     */
    @Transactional
    public void deleteBySceneId(Long sceneId, List<Long> chartIds) {
        QueryWrapper<CoreChartView> wrapper = new QueryWrapper<>();
        wrapper.eq("scene_id", sceneId);
        wrapper.notIn("id", chartIds);
        coreChartViewMapper.delete(wrapper);
    }

    /**
     * 获取图表详细信息
     * 根据ID和资源表类型查询图表的详细配置信息
     *
     * @param id 图表ID
     * @param resourceTable 资源表类型（CORE/SNAPSHOT）
     * @return 图表视图数据传输对象，不存在则返回null
     */
    public ChartViewDTO getDetails(Long id, String resourceTable) {
        CoreChartView coreChartView = null;
        if (CommonConstants.RESOURCE_TABLE.SNAPSHOT.equals(resourceTable)) {
            SnapshotCoreChartView snapshotCoreChartView = snapshotCoreChartViewMapper.selectById(id);
            if (ObjectUtils.isEmpty(snapshotCoreChartView)) {
                return null;
            }
            coreChartView = new CoreChartView();
            BeanUtils.copyBean(coreChartView, snapshotCoreChartView);
        } else {
            coreChartView = coreChartViewMapper.selectById(id);
            if (ObjectUtils.isEmpty(coreChartView)) {
                return null;
            }
        }
        ChartViewDTO dto = transRecord2DTO(coreChartView);
        return dto;
    }

    /**
     * 根据场景ID查询图表列表
     * 查询指定仪表板或数据大屏下的所有图表
     *
     * @param sceneId 场景ID（仪表板或数据大屏ID）
     * @param resourceTable 资源表类型
     * @return 图表视图列表
     */
    public List<ChartViewDTO> listBySceneId(Long sceneId, String resourceTable) {
        QueryWrapper<CoreChartView> wrapper = new QueryWrapper<>();
        wrapper.eq("scene_id", sceneId);
        List<ChartViewDTO> chartViewDTOS = transChart(extChartViewMapper.selectListCustom(sceneId, resourceTable));
        if (!CollectionUtils.isEmpty(chartViewDTOS)) {
            List<Long> tableIds = chartViewDTOS.stream()
                    .map(ChartViewDTO::getTableId)
                    .filter(tableId -> tableId != null) // 过滤掉空值
                    .distinct()
                    .toList();
            if (!CollectionUtils.isEmpty(tableIds)) {
                QueryWrapper<CoreDatasetTableField> wp = new QueryWrapper<>();
                wp.in("dataset_group_id", tableIds);
                List<CoreDatasetTableField> coreDatasetTableFields = coreDatasetTableFieldMapper.selectList(wp);
                Map<Long, List<CoreDatasetTableField>> groupedByTableId = coreDatasetTableFields.stream()
                        .collect(Collectors.groupingBy(CoreDatasetTableField::getDatasetGroupId));
                if (chartViewDTOS.size() < 10) {
                    chartViewDTOS.forEach(dto -> {
                        if (dto.getTableId() != null) {
                            dto.setCalParams(Utils.getParams(datasetTableFieldManage.transDTO(groupedByTableId.get(dto.getTableId()))));
                        }
                    });
                } else {
                    ExecutorService executor = Executors.newFixedThreadPool(10);
                    try {
                        // 超过10个图表要处理启用多线程处理
                        CountDownLatch latch = new CountDownLatch(chartViewDTOS.size());
                        chartViewDTOS.forEach(dto -> {
                            executor.submit(() -> {
                                try {
                                    if (dto.getTableId() != null) {
                                        dto.setCalParams(Utils.getParams(datasetTableFieldManage.transDTO(groupedByTableId.get(dto.getTableId()))));
                                    }
                                } finally {
                                    latch.countDown(); // 减少计数器
                                }
                            });
                        });

                        // 等待所有线程完成
                        boolean completedInTime = latch.await(200, TimeUnit.SECONDS);
                        if (!completedInTime) {
                            throw new InterruptedException("Tasks did not complete within 200 seconds");
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        LogUtil.error(e);
                    } finally {
                        executor.shutdown(); // 确保线程池关闭
                    }
                }

            }
        }
        return chartViewDTOS;
    }

    public List<ChartViewDTO> transChart(List<CoreChartView> list) {
        if (ObjectUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(ele -> {
            return transRecord2DTO(ele);
        }).collect(Collectors.toList());
    }

    public ChartViewDTO getChart(Long id, String resourceTable, boolean forThreshold) throws Exception {
        ChartViewDTO details = getDetails(id, resourceTable);
        if (details == null) {
            return null;
        }
        if (forThreshold) {
            ChartExtRequest chartExtRequest = details.getChartExtRequest();
            if (chartExtRequest == null) {
                chartExtRequest = new ChartExtRequest();
                chartExtRequest.setResultMode("all");
                chartExtRequest.setResultCount(1000);
                chartExtRequest.setGoPage(1L);
                chartExtRequest.setPageSize(50000L);
            }
            details.setChartExtRequest(chartExtRequest);
        }
        return chartDataManage.calcData(details);
    }

    /**
     * 获取图表视图
     * 根据图表ID和资源表类型获取图表视图信息
     *
     * @param id 图表ID
     * @param resourceTable 资源表类型
     * @return 图表视图数据传输对象
     * @throws Exception 查询异常
     */
    public ChartViewDTO getChart(Long id, String resourceTable) throws Exception {
        return getChart(id, resourceTable, false);
    }

    /**
     * 获取数据集的字段列表（用于图表编辑）
     * 查询数据集的所有可用字段，包括维度和度量，并进行权限过滤
     *
     * @param id 数据集ID
     * @param chartId 图表ID（用于获取图表计算字段）
     * @param chartViewDTO 图表视图信息
     * @return 包含维度列表和度量列表的Map，key为"dimensionList"和"quotaList"
     */
    public Map<String, List<ChartViewFieldDTO>> listByDQ(Long id, Long chartId, ChartViewDTO chartViewDTO) {
        QueryWrapper<CoreDatasetTableField> wrapper = new QueryWrapper<>();
        wrapper.eq("dataset_group_id", id);
        wrapper.eq("checked", true);
        wrapper.isNull("chart_id");

        TypeReference<List<CalParam>> typeToken = new TypeReference<>() {
        };
        TypeReference<List<FieldGroupDTO>> groupTokenType = new TypeReference<>() {
        };
        List<CoreDatasetTableField> fields = coreDatasetTableFieldMapper.selectList(wrapper);
        List<DatasetTableFieldDTO> collect = fields.stream().map(ele -> {
            DatasetTableFieldDTO dto = new DatasetTableFieldDTO();
            BeanUtils.copyBean(dto, ele);
            dto.setParams(JsonUtil.parseList(ele.getParams(), typeToken));
            dto.setGroupList(JsonUtil.parseList(ele.getGroupList(), groupTokenType));
            return dto;
        }).collect(Collectors.toList());
        // filter column disable field
        Map<String, ColumnPermissionItem> desensitizationList = new HashMap<>();
        List<DatasetTableFieldDTO> datasetTableFieldDTOS = permissionManage.filterColumnPermissions(collect, desensitizationList, id, null);
        datasetTableFieldDTOS.forEach(ele -> ele.setDesensitized(desensitizationList.containsKey(ele.getDataeaseName())));
        datasetTableFieldDTOS.add(createCountField(id));
        List<ChartViewFieldDTO> list = transFieldDTO(datasetTableFieldDTOS);

        // 获取图表计算字段
        wrapper.clear();
        wrapper.eq("chart_id", chartId);
        List<DatasetTableFieldDTO> chartFields = coreDatasetTableFieldMapper.selectList(wrapper).stream().map(ele -> {
            DatasetTableFieldDTO dto = new DatasetTableFieldDTO();
            BeanUtils.copyBean(dto, ele);
            dto.setGroupList(JsonUtil.parseList(ele.getGroupList(), groupTokenType));
            return dto;
        }).collect(Collectors.toList());
        list.addAll(transFieldDTO(chartFields));

        // 获取list中的聚合函数，将字段的summary设置成空
        SQLObj tableObj = new SQLObj();
        tableObj.setTableAlias("");

        for (ChartViewFieldDTO ele : list) {
            if (Objects.equals(ele.getExtField(), ExtFieldConstant.EXT_CALC)) {
                List<DatasetTableFieldDTO> f = list.stream().map(e -> {
                    DatasetTableFieldDTO dto = new DatasetTableFieldDTO();
                    BeanUtils.copyBean(dto, e);
                    return dto;
                }).collect(Collectors.toList());
                String originField = Utils.calcFieldRegex(ele, tableObj, f, true, null, Utils.mergeParam(Utils.getParams(f), null), pluginManage);
                for (String func : FunctionConstant.AGG_FUNC) {
                    if (Utils.matchFunction(func, originField)) {
                        ele.setSummary("");
                        ele.setAgg(true);
                        break;
                    }
                }
            }
        }

        List<ChartViewFieldDTO> dimensionList = list.stream().filter(ele -> StringUtils.equalsIgnoreCase(ele.getGroupType(), "d")).collect(Collectors.toList());
        List<ChartViewFieldDTO> quotaList = list.stream().filter(ele -> StringUtils.equalsIgnoreCase(ele.getGroupType(), "q")).collect(Collectors.toList());

        Map<String, List<ChartViewFieldDTO>> map = new LinkedHashMap<>();
        map.put("dimensionList", dimensionList);
        map.put("quotaList", quotaList);
        return map;
    }

    /**
     * 复制字段到图表
     * 将数据集字段复制为图表的计算字段
     *
     * @param id 原字段ID
     * @param chartId 目标图表ID
     */
    public void copyField(Long id, Long chartId) {
        CoreDatasetTableField coreDatasetTableField = coreDatasetTableFieldMapper.selectById(id);
        QueryWrapper<CoreDatasetTableField> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dataset_group_id", coreDatasetTableField.getDatasetGroupId());
        List<CoreDatasetTableField> coreDatasetTableFields = coreDatasetTableFieldMapper.selectList(queryWrapper);
        HashMap<String, String> map = new HashMap<>();
        for (CoreDatasetTableField ele : coreDatasetTableFields) {
            map.put(ele.getName(), ele.getName());
        }
        newName(map, coreDatasetTableField, coreDatasetTableField.getName());
        coreDatasetTableField.setChartId(chartId);
        coreDatasetTableField.setExtField(2);
        coreDatasetTableField.setOriginName("[" + id + "]");
        coreDatasetTableField.setId(IDUtils.snowID());
        coreDatasetTableField.setDataeaseName(TableUtils.fieldNameShort(coreDatasetTableField.getId() + "_" + coreDatasetTableField.getOriginName()));
        coreDatasetTableField.setFieldShortName(coreDatasetTableField.getDataeaseName());
        coreDatasetTableFieldMapper.insert(coreDatasetTableField);
    }

    private void newName(HashMap<String, String> map, CoreDatasetTableField coreDatasetTableField, String name) {
        name = name + "_copy";
        if (map.containsKey(name)) {
            newName(map, coreDatasetTableField, name);
        } else {
            coreDatasetTableField.setName(name);
        }
    }

    /**
     * 删除字段
     * 根据字段ID删除字段记录
     *
     * @param id 字段ID
     */
    public void deleteField(Long id) {
        coreDatasetTableFieldMapper.deleteById(id);
    }

    /**
     * 批量删除图表字段
     * 删除指定图表的所有计算字段
     *
     * @param chartId 图表ID
     */
    public void deleteFieldByChartId(Long chartId) {
        QueryWrapper<CoreDatasetTableField> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chart_id", chartId);
        coreDatasetTableFieldMapper.delete(queryWrapper);
    }

    /**
     * 获取图表基础信息
     * 查询图表的基础配置，包括坐标轴、样式等
     *
     * @param id 图表ID
     * @param resourceTable 资源表类型
     * @return 图表基础信息VO，不存在则返回null
     */
    public ChartBaseVO chartBaseInfo(Long id, String resourceTable) {
        ChartBasePO po = extChartViewMapper.queryChart(id, resourceTable);
        if (ObjectUtils.isEmpty(po)) return null;
        ChartBaseVO vo = BeanUtils.copyBean(new ChartBaseVO(), po);
        TypeReference<List<ChartViewFieldDTO>> tokenType = new TypeReference<>() {
        };
        vo.setXAxis(JsonUtil.parseList(po.getXAxis(), tokenType));
        vo.setXAxisExt(JsonUtil.parseList(po.getXAxisExt(), tokenType));
        vo.setYAxis(JsonUtil.parseList(po.getYAxis(), tokenType));
        vo.setYAxisExt(JsonUtil.parseList(po.getYAxisExt(), tokenType));
        vo.setExtStack(JsonUtil.parseList(po.getExtStack(), tokenType));
        vo.setExtBubble(JsonUtil.parseList(po.getExtBubble(), tokenType));
        vo.setFlowMapStartName(JsonUtil.parseList(po.getFlowMapStartName(), tokenType));
        vo.setFlowMapEndName(JsonUtil.parseList(po.getFlowMapEndName(), tokenType));
        if (StringUtils.isBlank(po.getExtColor()) || StringUtils.equals("null", po.getExtColor())) {
            vo.setExtColor(new ArrayList<>());
        } else {
            vo.setExtColor(JsonUtil.parseList(po.getExtColor(), tokenType));
        }
        vo.setExtLabel(JsonUtil.parseList(po.getExtLabel(), tokenType));
        vo.setExtTooltip(JsonUtil.parseList(po.getExtTooltip(), tokenType));
        return vo;
    }

    /**
     * 创建计数字段
     * 为数据集创建一个默认的计数字段
     *
     * @param id 数据集ID
     * @return 计数字段DTO
     */
    public DatasetTableFieldDTO createCountField(Long id) {
        DatasetTableFieldDTO dto = new DatasetTableFieldDTO();
        dto.setId(-1L);
        dto.setDatasetGroupId(id);
        dto.setOriginName("*");
        dto.setName("记录数*");
        dto.setDataeaseName("*");
        dto.setType("INT");
        dto.setChecked(true);
        dto.setColumnIndex(999);
        dto.setDeType(2);
        dto.setExtField(1);
        dto.setGroupType("q");
        return dto;
    }

    /**
     * 转换字段DTO列表
     * 将数据集字段DTO转换为图表视图字段DTO
     *
     * @param list 数据集字段DTO列表
     * @return 图表视图字段DTO列表
     */
    public List<ChartViewFieldDTO> transFieldDTO(List<DatasetTableFieldDTO> list) {
        return list.stream().map(ele -> {
            ChartViewFieldDTO dto = new ChartViewFieldDTO();
            if (ele == null) return null;
            BeanUtils.copyBean(dto, ele);
            dto.setDateStyle("y_M_d");
            dto.setDatePattern("date_sub");
            dto.setDateShowFormat("y_M_d");
            dto.setChartType("bar");

            if (dto.getId() == -1L || dto.getDeType() == 0 || dto.getDeType() == 1 || dto.getDeType() == 7) {
                dto.setSummary("count");
            } else {
                dto.setSummary("sum");
            }

            ChartFieldCompareDTO chartFieldCompareDTO = new ChartFieldCompareDTO();
            chartFieldCompareDTO.setType("none");
            dto.setCompareCalc(chartFieldCompareDTO);

            dto.setFormatterCfg(new FormatterCfgDTO().setUnitLanguage(Lang.isChinese() ? "ch" : "en"));

            dto.setSort("none");
            dto.setFilter(Collections.emptyList());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 将DTO转换为实体记录
     * 将图表视图DTO转换为持久化实体对象，用于数据库存储
     *
     * @param dto 图表视图数据传输对象
     * @return 快照核心图表视图实体
     * @throws Exception JSON序列化异常
     */
    public SnapshotCoreChartView transDTO2Record(ChartViewDTO dto) throws Exception {
        SnapshotCoreChartView record = new SnapshotCoreChartView();
        BeanUtils.copyBean(record, dto);

        record.setxAxis(objectMapper.writeValueAsString(dto.getXAxis()));
        record.setxAxisExt(objectMapper.writeValueAsString(dto.getXAxisExt()));
        record.setyAxis(objectMapper.writeValueAsString(dto.getYAxis()));
        record.setyAxisExt(objectMapper.writeValueAsString(dto.getYAxisExt()));
        record.setExtStack(objectMapper.writeValueAsString(dto.getExtStack()));
        record.setExtBubble(objectMapper.writeValueAsString(dto.getExtBubble()));
        record.setExtLabel(objectMapper.writeValueAsString(dto.getExtLabel()));
        record.setExtTooltip(objectMapper.writeValueAsString(dto.getExtTooltip()));
        record.setCustomAttr(objectMapper.writeValueAsString(dto.getCustomAttr()));
        if (dto.getCustomAttrMobile() != null) {
            record.setCustomAttrMobile(objectMapper.writeValueAsString(dto.getCustomAttrMobile()));
        }
        record.setCustomStyle(objectMapper.writeValueAsString(dto.getCustomStyle()));
        if (dto.getCustomAttrMobile() != null) {
            record.setCustomStyleMobile(objectMapper.writeValueAsString(dto.getCustomStyleMobile()));
        }
        record.setSenior(objectMapper.writeValueAsString(dto.getSenior()));
        record.setDrillFields(objectMapper.writeValueAsString(dto.getDrillFields()));
        record.setCustomFilter(objectMapper.writeValueAsString(dto.getCustomFilter()));
        record.setViewFields(objectMapper.writeValueAsString(dto.getViewFields()));
        record.setFlowMapStartName(objectMapper.writeValueAsString(dto.getFlowMapStartName()));
        record.setFlowMapEndName(objectMapper.writeValueAsString(dto.getFlowMapEndName()));
        record.setExtColor(objectMapper.writeValueAsString(dto.getExtColor()));
        record.setSortPriority(objectMapper.writeValueAsString(dto.getSortPriority()));
        return record;
    }

    /**
     * 将实体记录转换为DTO
     * 将数据库实体对象转换为图表视图DTO
     *
     * @param record 核心图表视图实体
     * @return 图表视图数据传输对象
     */
    public ChartViewDTO transRecord2DTO(CoreChartView record) {
        ChartViewDTO dto = new ChartViewDTO();
        BeanUtils.copyBean(dto, record);

        TypeReference<List<ChartViewFieldDTO>> tokenType = new TypeReference<>() {
        };

        dto.setXAxis(JsonUtil.parseList(record.getxAxis(), tokenType));
        dto.setXAxisExt(JsonUtil.parseList(record.getxAxisExt(), tokenType));
        dto.setYAxis(JsonUtil.parseList(record.getyAxis(), tokenType));
        dto.setYAxisExt(JsonUtil.parseList(record.getyAxisExt(), tokenType));
        dto.setExtStack(JsonUtil.parseList(record.getExtStack(), tokenType));
        dto.setExtBubble(JsonUtil.parseList(record.getExtBubble(), tokenType));
        dto.setExtLabel(JsonUtil.parseList(record.getExtLabel(), tokenType));
        dto.setExtTooltip(JsonUtil.parseList(record.getExtTooltip(), tokenType));
        dto.setCustomAttr(JsonUtil.parse(record.getCustomAttr(), Map.class));
        if (record.getCustomAttrMobile() != null) {
            dto.setCustomAttrMobile(JsonUtil.parse(record.getCustomAttrMobile(), Map.class));
        }
        dto.setCustomStyle(JsonUtil.parse(record.getCustomStyle(), Map.class));
        if (record.getCustomStyleMobile() != null) {
            dto.setCustomStyleMobile(JsonUtil.parse(record.getCustomStyleMobile(), Map.class));
        }
        dto.setSenior(JsonUtil.parse(record.getSenior(), Map.class));
        dto.setDrillFields(JsonUtil.parseList(record.getDrillFields(), tokenType));
        dto.setCustomFilter(JsonUtil.parseObject(record.getCustomFilter(), FilterTreeObj.class));
        dto.setViewFields(JsonUtil.parseList(record.getViewFields(), tokenType));
        dto.setFlowMapStartName(JsonUtil.parseList(record.getFlowMapStartName(), tokenType));
        dto.setFlowMapEndName(JsonUtil.parseList(record.getFlowMapEndName(), tokenType));
        dto.setExtColor(JsonUtil.parseList(record.getExtColor(), tokenType));
        dto.setSortPriority(JsonUtil.parseList(record.getSortPriority(), new TypeReference<List<SortAxis>>() {
        }));

        return dto;

    }

    /**
     * 检查两个图表是否使用相同数据集
     * 对比源图表和目标图表的数据集ID
     *
     * @param viewIdSource 源图表ID
     * @param viewIdTarget 目标图表ID
     * @return "yes"表示相同，"no"表示不同
     */
    public String checkSameDataSet(String viewIdSource, String viewIdTarget) {
        QueryWrapper<CoreChartView> wrapper = new QueryWrapper<>();
        wrapper.select("distinct table_id");
        wrapper.in("id", Arrays.asList(viewIdSource, viewIdTarget));
        coreChartViewMapper.selectCount(wrapper);
        if (coreChartViewMapper.selectCount(wrapper) == 1) {
            return "YES";
        } else {
            return "NO";
        }

    }

    /**
     * 获取视图选择器列表
     * 查询指定仪表板下的所有图表，用于视图选择器
     *
     * @param resourceId 资源ID（仪表板ID）
     * @return 视图选择器VO列表
     */
    public List<ViewSelectorVO> viewOption(Long resourceId) {
        List<ViewSelectorVO> result = extChartViewMapper.queryViewOption(resourceId);
        DataVisualizationInfo dvInfo = visualizationInfoMapper.selectById(resourceId);
        if (dvInfo != null && !CollectionUtils.isEmpty(result)) {
            String componentData = dvInfo.getComponentData();
            return result.stream().filter(item -> componentData.indexOf(String.valueOf(item.getId())) > 0).toList();
        } else {
            return result;
        }
    }

    /**
     * 查找图表视图
     * 根据视图ID查找对应的图表视图信息
     *
     * @param viewId 视图ID
     * @return 图表视图DTO
     */
    public ChartViewDTO findChartViewAround(String viewId) {
        return extChartViewMapper.findChartViewAround(viewId);
    }
}
