package io.dataease.dataset.server;


import io.dataease.api.dataset.DatasetTreeApi;
import io.dataease.api.dataset.dto.DataSetExportRequest;
import io.dataease.api.dataset.dto.DatasetNodeDTO;
import io.dataease.api.dataset.union.DatasetGroupInfoDTO;
import io.dataease.api.dataset.vo.DataSetBarVO;
import io.dataease.constant.LogOT;
import io.dataease.constant.LogST;
import io.dataease.dataset.manage.DatasetGroupManage;
import io.dataease.exportCenter.manage.ExportCenterDownLoadManage;
import io.dataease.exportCenter.manage.ExportCenterManage;
import io.dataease.extensions.datasource.dto.DatasetTableDTO;
import io.dataease.extensions.view.dto.*;
import io.dataease.log.DeLog;
import io.dataease.model.BusiNodeRequest;
import io.dataease.model.BusiNodeVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.*;


/**
 * 数据集树服务控制器
 * 提供数据集和文件夹的树形结构管理API接口
 * 实现DatasetTreeApi接口，作为数据集核心操作的HTTP入口
 *
 * <p>主要接口：</p>
 * <ul>
 *   <li>数据集CRUD: save, create, delete, move</li>
 *   <li>树形查询: tree</li>
 *   <li>详情查询: barInfo, get, details</li>
 *   <li>SQL参数: getSqlParams</li>
 *   <li>数据集导出: exportDataset</li>
 * </ul>
 *
 * <p>所有操作都会记录操作日志</p>
 *
 * @author Junjun
 */
@RestController
@RequestMapping("datasetTree")
public class DatasetTreeServer implements DatasetTreeApi {
    @Resource
    private DatasetGroupManage datasetGroupManage;
    @Resource
    private ExportCenterManage exportCenterManage;
    @Resource
    private ExportCenterDownLoadManage exportCenterDownLoadManage;


    @DeLog(id = "#p0.id", ot = LogOT.MODIFY, st = LogST.DATASET)
    @Override
    public DatasetGroupInfoDTO save(DatasetGroupInfoDTO datasetNodeDTO) throws Exception {
        return datasetGroupManage.save(datasetNodeDTO, false, true);
    }

    @DeLog(id = "#p0.id", ot = LogOT.MODIFY, st = LogST.DATASET)
    @Override
    public DatasetNodeDTO rename(DatasetGroupInfoDTO dto) throws Exception {
        return datasetGroupManage.save(dto, true, false);
    }

    @DeLog(id = "#p0.id", pid = "#p0.pid", ot = LogOT.CREATE, st = LogST.DATASET)
    @Override
    public DatasetNodeDTO create(DatasetGroupInfoDTO dto) throws Exception {
        return datasetGroupManage.save(dto, false, true);
    }

    @DeLog(id = "#p0.id", ot = LogOT.MODIFY, st = LogST.DATASET)
    @Override
    public DatasetNodeDTO move(DatasetGroupInfoDTO dto) throws Exception {
        return datasetGroupManage.move(dto);
    }

    @Override
    public boolean perDelete(Long id) {
        return datasetGroupManage.perDelete(id);
    }

    @DeLog(id = "#p0", ot = LogOT.DELETE, st = LogST.DATASET)
    @Override
    public void delete(Long id) {
        datasetGroupManage.delete(id);
    }


    public List<BusiNodeVO> tree(BusiNodeRequest request) {
        return datasetGroupManage.tree(request);
    }

    @Override
    public DataSetBarVO barInfo(Long id) {
        return datasetGroupManage.queryBarInfo(id);
    }

    @Override
    public DatasetGroupInfoDTO get(Long id) throws Exception {
        return datasetGroupManage.getDatasetGroupInfoDTO(id, "preview");
    }

    @Override
    public DatasetGroupInfoDTO details(Long id) throws Exception {
        return datasetGroupManage.getDetail(id);
    }

    @Override
    public List<DatasetTableDTO> panelGetDsDetails(List<Long> ids) throws Exception {
        return datasetGroupManage.getDetail(ids);
    }

    @Override
    public List<SqlVariableDetails> getSqlParams(List<Long> ids) throws Exception {
        return datasetGroupManage.getSqlParams(ids);
    }

    @Override
    public List<DatasetTableDTO> detailWithPerm(List<Long> ids) throws Exception {
        return datasetGroupManage.getDetailWithPerm(ids);
    }

    @Override
    public void exportDataset(DataSetExportRequest request, HttpServletResponse response) throws Exception {
        if (request.isDataEaseBi()) {
            exportCenterDownLoadManage.downloadDataset(request, response);
        } else {
            exportCenterManage.addTask(request.getId(), "dataset", request);
        }
    }

}
