package io.dataease.visualization.server;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import io.dataease.api.dataset.vo.CoreDatasetGroupVO;
import io.dataease.api.dataset.vo.CoreDatasetTableFieldVO;
import io.dataease.api.visualization.VisualizationOuterParamsApi;
import io.dataease.api.visualization.dto.VisualizationOuterParamsDTO;
import io.dataease.api.visualization.dto.VisualizationOuterParamsInfoDTO;
import io.dataease.api.visualization.response.VisualizationOuterParamsBaseResponse;
import io.dataease.auth.DeLinkPermit;
import io.dataease.constant.CommonConstants;
import io.dataease.dataset.dao.auto.entity.CoreDatasetTable;
import io.dataease.dataset.dao.auto.mapper.CoreDatasetTableMapper;
import io.dataease.constant.DeTypeConstants;
import io.dataease.dataset.utils.FieldUtils;
import io.dataease.extensions.view.dto.SqlVariableDetails;
import io.dataease.utils.BeanUtils;
import io.dataease.utils.JsonUtil;
import io.dataease.visualization.dao.auto.entity.*;
import io.dataease.visualization.dao.auto.mapper.*;
import io.dataease.visualization.dao.ext.mapper.ExtVisualizationOuterParamsMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 可视化外部参数服务
 * <p>
 * 处理仪表板的外部参数配置
 * <p>
 * 主要功能：
 * <ul>
 * <li>配置外部参数</li>
 * <li>查询外部参数信息</li>
 * <li>查询关联的数据集和字段</li>
 * <li>支持SQL变量作为外部参数</li>
 * </ul>
 *
 * @author DataEase
 * @since 2024-06-21
 */
@RestController
@RequestMapping("outerParams")
public class VisualizationOuterParamsService implements VisualizationOuterParamsApi {

    @Resource
    private ExtVisualizationOuterParamsMapper extOuterParamsMapper;
    @Resource
    private VisualizationOuterParamsMapper outerParamsMapper;
    @Resource
    private SnapshotVisualizationOuterParamsMapper snapshotOuterParamsMapper;

    @Resource
    private VisualizationOuterParamsInfoMapper outerParamsInfoMapper;

    @Resource
    private SnapshotVisualizationOuterParamsInfoMapper snapshotOuterParamsInfoMapper;

    @Resource
    private VisualizationOuterParamsTargetViewInfoMapper outerParamsTargetViewInfoMapper;

    @Resource
    private SnapshotVisualizationOuterParamsTargetViewInfoMapper snapshotOuterParamsTargetViewInfoMapper;

    @Resource
    private CoreDatasetTableMapper coreDatasetTableMapper;
    @Autowired
    private DataVisualizationServer dataVisualizationServer;
    @Autowired
    private SnapshotDataVisualizationInfoMapper snapshotDataVisualizationInfoMapper;


    /**
     * 根据仪表板ID查询外部参数配置
     *
     * @param visualizationId 仪表板ID
     * @return 外部参数配置
     */
    @Override
    public VisualizationOuterParamsDTO queryWithVisualizationId(String visualizationId) {
        // 根据仪表板ID查询外部参数配置
        VisualizationOuterParamsDTO visualizationOuterParamsDTO = extOuterParamsMapper.queryWithVisualizationIdSnapshot(visualizationId);
        return visualizationOuterParamsDTO;
    }

    /**
     * 更新外部参数配置
     * <p>
     * 会清除原有配置，然后保存新的配置
     *
     * @param outerParamsDTO 外部参数配置
     */
    @Override
    public void updateOuterParamsSet(VisualizationOuterParamsDTO outerParamsDTO) {
        // ========== 第一阶段：参数校验和初始化 ==========
        String visualizationId = outerParamsDTO.getVisualizationId();
        Assert.notNull(visualizationId, "visualizationId cannot be null");
        // 1. 获取现有参数名称到ID的映射（用于保持参数ID一致性）
        Map<String,String> paramsInfoNameIdMap = new HashMap<>();
        List<SnapshotVisualizationOuterParamsInfo> paramsInfoNameIdList = extOuterParamsMapper.getVisualizationOuterParamsInfoBase(visualizationId);
        if(!CollectionUtils.isEmpty(paramsInfoNameIdList)){
            paramsInfoNameIdMap = paramsInfoNameIdList.stream()
                    .collect(Collectors.toMap(SnapshotVisualizationOuterParamsInfo::getParamName, SnapshotVisualizationOuterParamsInfo::getParamsInfoId));
        }

        // ========== 第二阶段：清理原有配置 ==========
        // 2.1 删除目标视图信息
        extOuterParamsMapper.deleteOuterParamsTargetWithVisualizationIdSnapshot(visualizationId);
        // 2.2 删除外部参数信息
        extOuterParamsMapper.deleteOuterParamsInfoWithVisualizationIdSnapshot(visualizationId);
        // 2.3 删除外部参数配置
        extOuterParamsMapper.deleteOuterParamsWithVisualizationIdSnapshot(visualizationId);
        // 2.4 如果没有新的参数配置，直接返回
        if(CollectionUtils.isEmpty(outerParamsDTO.getOuterParamsInfoArray())){
            return;
        }

        // ========== 第三阶段：插入新的配置 ==========
        // 3.1 生成参数配置ID
        String paramsId = UUID.randomUUID().toString();
        outerParamsDTO.setParamsId(paramsId);
        // 3.2 创建外部参数配置实体并保存
        SnapshotVisualizationOuterParams newOuterParams = new SnapshotVisualizationOuterParams();
        BeanUtils.copyBean(newOuterParams, outerParamsDTO);
        snapshotOuterParamsMapper.insert(newOuterParams);
        // 3.3 保存参数名称ID映射的最终引用（用于lambda表达式）
        Map<String, String> finalParamsInfoNameIdMap = paramsInfoNameIdMap;
        // 3.4 遍历外部参数信息数组
        Optional.ofNullable(outerParamsDTO.getOuterParamsInfoArray()).orElse(new ArrayList<>()).forEach(outerParamsInfo -> {
            // 3.4.1 获取或生成参数信息ID（如果参数名已存在，复用原ID）
            String paramsInfoId = finalParamsInfoNameIdMap.get(outerParamsInfo.getParamName());
            if(StringUtils.isEmpty(paramsInfoId)){
                paramsInfoId = UUID.randomUUID().toString();
            }
            // 3.4.2 设置参数信息ID和关联的参数配置ID
            outerParamsInfo.setParamsInfoId(paramsInfoId);
            outerParamsInfo.setParamsId(paramsId);
            // 3.4.3 创建参数信息实体并保存
            SnapshotVisualizationOuterParamsInfo newOuterParamsInfo = new SnapshotVisualizationOuterParamsInfo();
            BeanUtils.copyBean(newOuterParamsInfo, outerParamsInfo);
            snapshotOuterParamsInfoMapper.insert(newOuterParamsInfo);
            // 3.4.4 保存参数信息ID的最终引用
            String finalParamsInfoId = paramsInfoId;
            // 3.4.5 遍历目标视图信息列表
            Optional.ofNullable(outerParamsInfo.getTargetViewInfoList()).orElse(new ArrayList<>()).forEach(targetViewInfo -> {
                // 3.4.5.1 生成目标视图信息ID
                String targetViewInfoId = UUID.randomUUID().toString();
                // 3.4.5.2 设置目标ID和参数信息ID
                targetViewInfo.setTargetId(targetViewInfoId);
                targetViewInfo.setParamsInfoId(finalParamsInfoId);
                // 3.4.5.3 创建目标视图信息实体并保存
                SnapshotVisualizationOuterParamsTargetViewInfo newOuterParamsTargetViewInfo = new SnapshotVisualizationOuterParamsTargetViewInfo();
                BeanUtils.copyBean(newOuterParamsTargetViewInfo, targetViewInfo);
                snapshotOuterParamsTargetViewInfoMapper.insert(newOuterParamsTargetViewInfo);
            });
        });
    }

    /**
     * 获取外部参数信息
     *
     * @param visualizationId 仪表板ID
     * @return 外部参数信息
     */
    @DeLinkPermit
    @Override
    public VisualizationOuterParamsBaseResponse getOuterParamsInfo(String visualizationId) {
        // 1. 查询外部参数信息列表
        List<VisualizationOuterParamsInfoDTO> result = extOuterParamsMapper.getVisualizationOuterParamsInfo(visualizationId);
        // 2. 构建响应对象，包含两个Map：
        //    2.1 第一个Map：key为源信息，value为目标信息列表
        //    2.2 第二个Map：key为源信息，value为参数信息本身
        return new VisualizationOuterParamsBaseResponse(Optional.ofNullable(result).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(VisualizationOuterParamsInfoDTO::getSourceInfo, VisualizationOuterParamsInfoDTO::getTargetInfoList)),
                Optional.ofNullable(result).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(VisualizationOuterParamsInfoDTO::getSourceInfo, paramsInfo -> paramsInfo))
        );
    }

    /**
     * 查询仪表板关联的数据集和字段
     * <p>
     * 包括:
     * <ul>
     * <li>数据集分组信息</li>
     * <li>数据集字段列表</li>
     * <li>SQL变量（作为外部参数）</li>
     * </ul>
     *
     * @param visualizationId 仪表板ID
     * @return 数据集信息列表
     */
    @Override
    public List<CoreDatasetGroupVO> queryDsWithVisualizationId(String visualizationId) {
        // 1. 查询仪表板关联的数据集列表
        List<CoreDatasetGroupVO> result = extOuterParamsMapper.queryDsWithVisualizationId(visualizationId);
        // 2. 如果查询结果不为空，进行处理
        if (!CollectionUtils.isEmpty(result)) {
            // 2.1 获取仪表板中实际使用的图表ID列表
            List<Long> activeViewIds = dataVisualizationServer.getEnabledViewIds(Long.valueOf(visualizationId), CommonConstants.RESOURCE_TABLE.SNAPSHOT);
            // 2.2 遍历每个数据集组
            result.forEach(coreDatasetGroupVO -> {
                // 2.2.1 过滤已删除的图表（只保留实际使用的图表）
                if(!CollectionUtils.isEmpty(coreDatasetGroupVO.getDatasetViews())){
                    coreDatasetGroupVO.setDatasetViews(coreDatasetGroupVO.getDatasetViews().stream().filter(item ->activeViewIds.contains(item.getChartId())).toList());
                }
                // 2.2.2 获取数据集字段列表
                List<CoreDatasetTableFieldVO> fields = coreDatasetGroupVO.getDatasetFields();
                // 2.2.3 查询该数据集组下的所有数据集表
                QueryWrapper<CoreDatasetTable> wrapper = new QueryWrapper<>();
                wrapper.eq("dataset_group_id", coreDatasetGroupVO.getId());
                List<CoreDatasetTable> tableResult = coreDatasetTableMapper.selectList(wrapper);
                // 2.2.4 如果数据集表不为空，处理SQL变量
                if (!CollectionUtils.isEmpty(tableResult)) {
                    tableResult.forEach(coreDatasetTable -> {
                        // 2.2.4.1 获取SQL变量详情（JSON字符串）
                        String sqlVarDetail = coreDatasetTable.getSqlVariableDetails();
                        if (StringUtils.isNotEmpty(sqlVarDetail)) {
                            // 2.2.4.2 解析SQL变量列表
                            TypeReference<List<SqlVariableDetails>> listTypeReference = new TypeReference<List<SqlVariableDetails>>() {
                            };
                            List<SqlVariableDetails> defaultsSqlVariableDetails = JsonUtil.parseList(sqlVarDetail, listTypeReference);
                            // 2.2.4.3 遍历SQL变量，添加到字段列表
                            defaultsSqlVariableDetails.forEach(sqlVariableDetails -> {
                                // 2.2.4.3.1 生成变量字段ID（格式：数据集表ID|DE|变量名）
                                String varFieldId = coreDatasetTable.getId() + "|DE|" + sqlVariableDetails.getVariableName();
                                // 2.2.4.3.2 确定字段类型（如果是DATETIME相关类型，统一为DATETIME）
                                int fieldType = FieldUtils.transType2DeType(sqlVariableDetails.getType().get(0).contains("DATETIME") ? "DATETIME" : sqlVariableDetails.getType().get(0));
                                // 2.2.4.3.3 创建字段VO并添加到字段列表
                                fields.add(new CoreDatasetTableFieldVO(varFieldId, sqlVariableDetails.getVariableName(), fieldType));
                            });
                        }
                    });
                }
            });
        }
        // 3. 返回处理后的数据集列表
        return result;
    }
}
