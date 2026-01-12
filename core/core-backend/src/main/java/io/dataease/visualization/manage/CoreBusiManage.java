package io.dataease.visualization.manage;

import io.dataease.dataset.manage.DatasetGroupManage;
import io.dataease.datasource.manage.DataSourceManage;
import io.dataease.model.BusiNodeRequest;
import io.dataease.model.BusiNodeVO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 核心业务管理类
 * 统一管理数据源、数据集、可视化仪表板等业务模块的树形结构
 */
@Component("coreBusiManage")
public class CoreBusiManage {

    @Resource
    private CoreVisualizationManage coreVisualizationManage;

    @Resource
    private DataSourceManage dataSourceManage;

    @Resource
    private DatasetGroupManage datasetGroupManage;

    /**
     * 生成交互式树形结构
     * 根据请求的模块类型返回对应的树形数据
     *
     * @param requestMap 请求映射,键为模块类型(datasource/dataset/dashboard/dataV),值为查询请求
     * @return 模块树形结构映射
     */
    public Map<String, List<BusiNodeVO>> interactiveTree(Map<String, BusiNodeRequest> requestMap) {
        Map<String, List<BusiNodeVO>> result = new HashMap<>();
        // 遍历所有请求的模块
        for (Map.Entry<String, BusiNodeRequest> entry : requestMap.entrySet()) {
            BusiNodeRequest busiNodeRequest = entry.getValue();
            String key = entry.getKey();
            // 根据模块类型调用对应的管理器
            if (StringUtils.equalsIgnoreCase(key, "datasource")) {
                // 数据源模块
                result.put(key, dataSourceManage.tree(busiNodeRequest));
            } else if (StringUtils.equalsIgnoreCase(key, "dataset")) {
                // 数据集模块
                result.put(key, datasetGroupManage.tree(busiNodeRequest));
            } else if (StringUtils.equalsAnyIgnoreCase(key, "dashboard", "dataV")) {
                // 可视化仪表板模块
                result.put(key, coreVisualizationManage.tree(busiNodeRequest));
            }
        }
        return result;
    }
}
