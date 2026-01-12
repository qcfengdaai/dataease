package io.dataease.operation.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dataease.commons.constants.OptConstants;
import io.dataease.operation.dao.auto.entity.CoreOptRecent;
import io.dataease.operation.dao.auto.mapper.CoreOptRecentMapper;
import io.dataease.utils.AuthUtils;
import io.dataease.utils.IDUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 核心操作记录管理类
 * 负责管理用户的操作记录,包括保存操作记录和查询最近使用的模板
 */
@Component
public class CoreOptRecentManage {

    /**
     * 核心操作记录 Mapper
     */
    @Autowired
    private CoreOptRecentMapper coreStoreMapper;

    /**
     * 保存操作记录(仅通过资源ID)
     * @param resourceId 资源ID
     * @param resourceType 资源类型
     * @param optType 操作类型(1-新建, 2-修改)
     */
    public void saveOpt(Long resourceId, int resourceType, int optType) {
        saveOpt(resourceId, null, resourceType, optType);
    }

    /**
     * 保存操作记录(仅通过资源名称)
     * @param resourceName 资源名称
     * @param resourceType 资源类型
     * @param optType 操作类型(1-新建, 2-修改)
     */
    public void saveOpt(String resourceName, int resourceType, int optType) {
        saveOpt(null, resourceName, resourceType, optType);
    }

    /**
     * 保存操作记录(完整版本)
     * 如果记录已存在则更新操作类型和时间,否则插入新记录
     * @param resourceId 资源ID(可选)
     * @param resourceName 资源名称(可选)
     * @param resourceType 资源类型
     * @param optType 操作类型(1-新建, 2-修改)
     */
    public void saveOpt(Long resourceId, String resourceName, int resourceType, int optType) {
        // 获取当前登录用户ID
        Long uid = AuthUtils.getUser().getUserId();

        // 构建更新条件
        QueryWrapper<CoreOptRecent> updateWrapper = new QueryWrapper<>();
        if (resourceId != null) {
            updateWrapper.eq("resource_id", resourceId);
        }
        if (StringUtils.isNotEmpty(resourceName)) {
            updateWrapper.eq("resource_name", resourceName);
        }
        updateWrapper.eq("resource_type", resourceType);
        updateWrapper.eq("uid", uid);

        // 构建更新参数
        CoreOptRecent updateParam = new CoreOptRecent();
        updateParam.setOptType(optType);
        updateParam.setTime(System.currentTimeMillis());

        // 尝试更新,如果没有记录则插入新记录
        if (coreStoreMapper.update(updateParam, updateWrapper) == 0) {
            CoreOptRecent optRecent = new CoreOptRecent();
            optRecent.setId(IDUtils.snowID());
            optRecent.setResourceId(resourceId);
            optRecent.setResourceName(resourceName);
            optRecent.setResourceType(resourceType);
            optRecent.setOptType(optType);
            optRecent.setTime(System.currentTimeMillis());
            optRecent.setUid(AuthUtils.getUser().getUserId());
            coreStoreMapper.insert(optRecent);
        }
    }

    /**
     * 查询当前用户的模板最近使用时间
     * @return 模板名称与使用时间的映射 Map
     */
    public Map<String, Long> findTemplateRecentUseTime() {
        // 获取当前登录用户ID
        Long uid = AuthUtils.getUser().getUserId();

        // 构建查询条件:查询当前用户的模板操作记录
        QueryWrapper<CoreOptRecent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resource_type", OptConstants.OPT_RESOURCE_TYPE.TEMPLATE);
        queryWrapper.eq("uid", uid);

        // 执行查询
        List<CoreOptRecent> result = coreStoreMapper.selectList(queryWrapper);

        // 将结果转换为 Map:模板名称 -> 使用时间
        if (CollectionUtils.isNotEmpty(result)) {
            return result.stream().collect(Collectors.toMap(CoreOptRecent::getResourceName, CoreOptRecent::getTime));
        } else {
            return new HashMap<>();
        }
    }

}
