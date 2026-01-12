package io.dataease.dataset.server;

import io.dataease.api.dataset.DataAssistantApi;
import io.dataease.api.dataset.vo.DataSQLBotAssistantVO;
import io.dataease.api.dataset.vo.DataSQLBotDatasetVO;
import io.dataease.dataset.manage.DatasetSQLBotManage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * SQL Bot助手服务控制器
 * 提供智能SQL查询助手功能的API接口
 * 实现DataAssistantApi接口，支持自然语言查询数据集
 *
 * <p>主要接口：</p>
 * <ul>
 *   <li>数据源列表: getDatasourceList</li>
 *   <li>数据集列表: getDatasetList</li>
 * </ul>
 *
 * @author Junjun
 */
@RestController
@RequestMapping("/sqlbot")
public class DatasetSQLBotServer implements DataAssistantApi {

    @Resource
    private DatasetSQLBotManage datasetSQLBotManage;
    @Override
    public List<DataSQLBotAssistantVO> getDatasourceList(Long dsId, Long tableId) {
        return datasetSQLBotManage.getDatasourceList(dsId, tableId);
    }

    @Override
    public List<DataSQLBotDatasetVO> getDatasetList(String dvInfo) {
        return datasetSQLBotManage.getDatasetList(dvInfo);
    }
}
