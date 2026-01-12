package io.dataease.dataset.server;

import io.dataease.api.dataset.DatasetTableSqlLogApi;
import io.dataease.api.dataset.dto.SqlLogDTO;
import io.dataease.dataset.manage.DatasetTableSqlLogManage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据集SQL日志服务控制器
 * 提供数据集SQL执行日志的查询和管理API接口
 * 实现DatasetTableSqlLogApi接口，用于SQL性能监控和调试
 *
 * <p>主要接口：</p>
 * <ul>
 *   <li>保存日志: save</li>
 *   <li>查询日志: listByTableId</li>
 *   <li>删除日志: deleteByTableId</li>
 * </ul>
 *
 * @author Junjun
 */
@RestController
@RequestMapping("datasetTableSqlLog")
public class DatasetTableSqlLogServer implements DatasetTableSqlLogApi {
    @Resource
    private DatasetTableSqlLogManage datasetTableSqlLogManage;

    @Override
    public void save(SqlLogDTO sqlLogDTO) throws Exception {
        datasetTableSqlLogManage.save(sqlLogDTO);
    }

    @Override
    public List<SqlLogDTO> listByTableId(SqlLogDTO sqlLogDTO) throws Exception {
        return datasetTableSqlLogManage.listByTableId(sqlLogDTO);
    }

    @Override
    public void deleteByTableId(String id) throws Exception {
        datasetTableSqlLogManage.deleteByTableId(id);
    }
}
