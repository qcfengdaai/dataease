package io.dataease.chart.manage;

import com.fasterxml.jackson.core.type.TypeReference;
import io.dataease.dataset.dao.auto.entity.CoreDatasetTableField;
import io.dataease.dataset.dao.auto.mapper.CoreDatasetTableFieldMapper;
import io.dataease.engine.utils.SQLUtils;
import io.dataease.extensions.datasource.dto.CalParam;
import io.dataease.extensions.datasource.dto.DatasetTableFieldDTO;
import io.dataease.extensions.datasource.dto.FieldGroupDTO;
import io.dataease.extensions.view.filter.FilterTreeItem;
import io.dataease.extensions.view.filter.FilterTreeObj;
import io.dataease.utils.BeanUtils;
import io.dataease.utils.JsonUtil;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 图表过滤树服务类
 * 负责图表过滤条件的字段查询和字符替换处理
 * 协调字段信息和过滤树对象之间的关系
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>过滤树字段查询和设置</li>
 *   <li>关键字字符的转义处理</li>
 *   <li>字段参数和分组信息的加载</li>
 * </ul>
 *
 * @author Junjun
 */
@Service
public class ChartFilterTreeService {
    @Resource
    private CoreDatasetTableFieldMapper coreDatasetTableFieldMapper;

    /**
     * 搜索字段并设置到过滤树
     * 递归遍历过滤树，为每个字段项加载完整的字段信息
     *
     * @param tree 过滤树对象，包含字段ID和过滤条件
     */
    public void searchFieldAndSet(FilterTreeObj tree) {
        if (ObjectUtils.isNotEmpty(tree)) {
            if (ObjectUtils.isNotEmpty(tree.getItems())) {
                for (FilterTreeItem item : tree.getItems()) {
                    if (ObjectUtils.isNotEmpty(item)) {
                        if (StringUtils.equalsIgnoreCase(item.getType(), "item") || ObjectUtils.isEmpty(item.getSubTree())) {
                            CoreDatasetTableField coreDatasetTableField = coreDatasetTableFieldMapper.selectById(item.getFieldId());
                            DatasetTableFieldDTO dto = new DatasetTableFieldDTO();
                            BeanUtils.copyBean(dto, coreDatasetTableField);
                            if (StringUtils.isNotEmpty(coreDatasetTableField.getParams())) {
                                TypeReference<List<CalParam>> tokenType = new TypeReference<>() {
                                };
                                List<CalParam> calParams = JsonUtil.parseList(coreDatasetTableField.getParams(), tokenType);
                                dto.setParams(calParams);
                            }
                            if (StringUtils.isNotEmpty(coreDatasetTableField.getGroupList())) {
                                TypeReference<List<FieldGroupDTO>> groupTokenType = new TypeReference<>() {
                                };
                                List<FieldGroupDTO> fieldGroups = JsonUtil.parseList(coreDatasetTableField.getGroupList(), groupTokenType);
                                dto.setGroupList(fieldGroups);
                            }
                            item.setField(dto);
                        } else if (StringUtils.equalsIgnoreCase(item.getType(), "tree") || (ObjectUtils.isNotEmpty(item.getSubTree()) && StringUtils.isNotEmpty(item.getSubTree().getLogic()))) {
                            searchFieldAndSet(item.getSubTree());
                        }
                    }
                }
            }
        }
    }

    /**
     * 字符替换处理
     * 对过滤树中的值和枚举值进行关键字转义处理，防止SQL注入
     *
     * @param tree 过滤树对象
     * @return 处理后的过滤树对象
     */
    public FilterTreeObj charReplace(FilterTreeObj tree) {
        if (ObjectUtils.isNotEmpty(tree)) {
            if (ObjectUtils.isNotEmpty(tree.getItems())) {
                for (FilterTreeItem item : tree.getItems()) {
                    if (ObjectUtils.isNotEmpty(item)) {
                        if (StringUtils.equalsIgnoreCase(item.getType(), "item") || ObjectUtils.isEmpty(item.getSubTree())) {
                            if (CollectionUtils.isNotEmpty(item.getEnumValue())) {
                                List<String> collect = item.getEnumValue().stream().map(SQLUtils::transKeyword).collect(Collectors.toList());
                                item.setEnumValue(collect);
                            }
                            item.setValue(SQLUtils.transKeyword(item.getValue()));
                        } else if (StringUtils.equalsIgnoreCase(item.getType(), "tree") || (ObjectUtils.isNotEmpty(item.getSubTree()) && StringUtils.isNotEmpty(item.getSubTree().getLogic()))) {
                            charReplace(item.getSubTree());
                        }
                    }
                }
            }
        }
        return tree;
    }
}
