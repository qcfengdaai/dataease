package io.dataease.exportCenter.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.dataease.api.chart.request.ChartExcelRequest;
import io.dataease.api.dataset.dto.DataSetExportRequest;
import io.dataease.api.export.BaseExportApi;
import io.dataease.api.xpack.dataFilling.DataFillingApi;
import io.dataease.commons.utils.ExcelWatermarkUtils;
import io.dataease.constant.LogOT;
import io.dataease.constant.LogST;
import io.dataease.dataset.manage.*;
import io.dataease.exception.DEException;
import io.dataease.exportCenter.dao.auto.entity.CoreExportDownloadTask;
import io.dataease.exportCenter.dao.auto.entity.CoreExportTask;
import io.dataease.exportCenter.dao.auto.mapper.CoreExportDownloadTaskMapper;
import io.dataease.exportCenter.dao.auto.mapper.CoreExportTaskMapper;
import io.dataease.exportCenter.dao.ext.mapper.ExportTaskExtMapper;
import io.dataease.license.config.XpackInteract;
import io.dataease.log.DeLog;
import io.dataease.model.ExportTaskDTO;
import io.dataease.system.manage.SysParameterManage;
import io.dataease.utils.*;
import io.dataease.visualization.dao.auto.entity.VisualizationWatermark;
import io.dataease.visualization.dao.auto.mapper.VisualizationWatermarkMapper;
import io.dataease.visualization.dao.ext.mapper.ExtDataVisualizationMapper;
import io.dataease.visualization.server.DataVisualizationServer;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import io.dataease.visualization.dto.WatermarkContentDTO;
import io.dataease.api.permissions.user.vo.UserFormVO;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Future;

/**
 * 导出中心管理组件
 * 提供数据导出功能的核心业务逻辑，支持图表、数据集和数据填报的导出任务管理
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>导出任务的生命周期管理（创建、执行、下载、删除）</li>
 *   <li>支持多种导出类型：图表、数据集、数据填报</li>
 *   <li>导出文件的水印处理</li>
 *   <li>导出任务的分页查询和状态统计</li>
 *   <li>导出文件的清理和定时任务</li>
 * </ul>
 *
 * <p>业务特性：</p>
 * <ul>
 *   <li>异步执行导出任务，避免长时间阻塞</li>
 *   <li>支持任务重试机制</li>
 *   <li>提供导出进度跟踪</li>
 *   <li>支持批量删除和状态过滤</li>
 *   <li>自动清理过期的导出文件</li>
 * </ul>
 *
 * <p>安全机制：</p>
 * <ul>
 *   <li>基于用户隔离的任务管理</li>
 *   <li>导出文件的时效性控制</li>
 *   <li>支持Excel水印添加</li>
 *   <li>操作日志记录</li>
 * </ul>
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class ExportCenterManage implements BaseExportApi {
    /**
     * 导出任务数据访问层
     */
    @Resource
    private CoreExportTaskMapper exportTaskMapper;

    /**
     * 导出下载任务数据访问层
     */
    @Resource
    private CoreExportDownloadTaskMapper coreExportDownloadTaskMapper;

    /**
     * 导出任务扩展数据访问层
     */
    @Resource
    private ExportTaskExtMapper exportTaskExtMapper;

    /**
     * 数据集组管理组件
     */
    @Resource
    private DatasetGroupManage datasetGroupManage;

    /**
     * 数据可视化服务器组件
     */
    @Resource
    DataVisualizationServer dataVisualizationServer;

    /**
     * 导出中心下载管理组件
     */
    @Resource
    private ExportCenterDownLoadManage exportCenterDownLoadManage;

    /**
     * 系统参数管理组件
     */
    @Resource
    private SysParameterManage sysParameterManage;

    /**
     * 导出核心线程数配置
     */
    @Value("${dataease.export.core.size:10}")
    private int core;

    /**
     * 导出最大线程数配置
     */
    @Value("${dataease.export.max.size:10}")
    private int max;

    /**
     * 导出数据存储路径
     */
    @Value("${dataease.path.exportData:/opt/dataease2.0/data/exportData/}")
    private String exportData_path;

    /**
     * 可视化水印数据访问层
     */
    @Resource
    private VisualizationWatermarkMapper watermarkMapper;

    /**
     * 扩展数据可视化数据访问层
     */
    @Resource
    private ExtDataVisualizationMapper visualizationMapper;

    /**
     * 导出任务状态枚举
     */
    static private List<String> STATUS = Arrays.asList("SUCCESS", "FAILED", "PENDING", "IN_PROGRESS", "ALL");

    /**
     * 正在运行的任务映射表
     * 键为任务ID，值为Future对象，用于管理异步任务的执行
     */
    private Map<String, Future> Running_Task = new HashMap<>();

    /**
     * 数据填报API（可选依赖）
     */
    @Autowired(required = false)
    private DataFillingApi dataFillingApi = null;

    /**
     * 获取数据填报API实例
     * 返回数据填报API的实例，如果未配置则返回null
     *
     * @return 数据填报API实例或null
     */
    private DataFillingApi getDataFillingApi() {
        return dataFillingApi;
    }

    /**
     * 下载导出文件
     * 根据任务ID下载对应的导出文件，验证下载任务存在性后委托给下载管理组件处理
     *
     * @param id 导出任务ID
     * @param response HTTP响应对象，用于输出文件流
     * @throws Exception 如果任务不存在或下载过程中出现错误
     */
    public void download(String id, HttpServletResponse response) throws Exception {
        // 验证下载任务是否存在
        if (coreExportDownloadTaskMapper.selectById(id) == null) {
            DEException.throwException("任务不存在");
        }
        // 获取导出任务信息并委托下载管理组件处理
        CoreExportTask exportTask = exportTaskMapper.selectById(id);
        exportCenterDownLoadManage.download(exportTask, response);
    }

    /**
     * 删除导出任务
     * 删除指定的导出任务，包括取消正在运行的任务、删除文件和数据库记录
     *
     * @param id 导出任务ID
     */
    public void delete(String id) {
        // 取消正在运行的任务
        Iterator<Map.Entry<String, Future>> iterator = Running_Task.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Future> entry = iterator.next();
            if (entry.getKey().equalsIgnoreCase(id)) {
                entry.getValue().cancel(true);
                iterator.remove();
            }
        }
        // 删除导出文件和目录
        FileUtils.deleteDirectoryRecursively(exportData_path + id);
        // 删除数据库记录
        exportTaskMapper.deleteById(id);
    }

    /**
     * 批量删除导出任务
     * 根据状态类型批量删除当前用户的导出任务，支持并行处理以提高性能
     *
     * @param type 任务状态类型，支持SUCCESS、FAILED、PENDING、IN_PROGRESS、ALL
     * @throws DEException 如果状态类型无效
     */
    public void deleteAll(String type) {
        // 验证状态类型是否有效
        if (!STATUS.contains(type)) {
            DEException.throwException("无效的状态");
        }

        // 构建查询条件，只查询当前用户的任务
        QueryWrapper<CoreExportTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", AuthUtils.getUser().getUserId());
        if (!type.equalsIgnoreCase("ALL")) {
            queryWrapper.eq("export_status", type);
        }

        // 获取符合条件的任务并并行删除
        List<CoreExportTask> exportTasks = exportTaskMapper.selectList(queryWrapper);
        exportTasks.parallelStream().forEach(exportTask -> {
            // 取消正在运行的任务
            Iterator<Map.Entry<String, Future>> iterator = Running_Task.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Future> entry = iterator.next();
                if (entry.getKey().equalsIgnoreCase(exportTask.getId())) {
                    entry.getValue().cancel(true);
                    iterator.remove();
                }
            }
            // 删除文件和数据库记录
            FileUtils.deleteDirectoryRecursively(exportData_path + exportTask.getId());
            exportTaskMapper.deleteById(exportTask.getId());
        });
    }

    /**
     * 批量删除指定的导出任务
     * 根据任务ID列表批量删除导出任务
     *
     * @param ids 导出任务ID列表
     */
    public void delete(List<String> ids) {
        ids.forEach(this::delete);
    }

    /**
     * 重试失败的导出任务
     * 将失败状态的导出任务重置为待执行状态，并重新启动导出流程
     *
     * @param id 导出任务ID
     * @throws DEException 如果任务不是失败状态
     */
    public void retry(String id) {
        // 获取导出任务并验证状态
        CoreExportTask exportTask = exportTaskMapper.selectById(id);
        if (!exportTask.getExportStatus().equalsIgnoreCase("FAILED")) {
            DEException.throwException("正在导出中!");
        }

        // 重置任务状态为待执行
        exportTask.setExportStatus("PENDING");
        exportTask.setExportProgress("0");
        exportTask.setExportMachineName(hostName());
        exportTask.setExportTime(System.currentTimeMillis());
        exportTaskMapper.updateById(exportTask);

        // 清理之前的导出文件
        FileUtils.deleteDirectoryRecursively(exportData_path + id);

        // 根据导出类型重新启动相应的导出任务
        if (exportTask.getExportFromType().equalsIgnoreCase("chart")) {
            ChartExcelRequest request = JsonUtil.parseObject(exportTask.getParams(), ChartExcelRequest.class);
            exportCenterDownLoadManage.startViewTask(exportTask, request);
        }
        if (exportTask.getExportFromType().equalsIgnoreCase("dataset")) {
            DataSetExportRequest request = JsonUtil.parseObject(exportTask.getParams(), DataSetExportRequest.class);
            exportCenterDownLoadManage.startDatasetTask(exportTask, request);
        }
        if (exportTask.getExportFromType().equalsIgnoreCase("data_filling")) {
            HashMap request = JsonUtil.parseObject(exportTask.getParams(), HashMap.class);
            exportCenterDownLoadManage.startDataFillingTask(exportTask, request);
        }
    }

    /**
     * 分页查询导出任务
     * 根据状态分页查询当前用户的导出任务列表，并补充任务的扩展信息
     *
     * @param page 分页参数，包含页码和页大小
     * @param status 任务状态过滤条件
     * @return 分页后的导出任务DTO列表，包含任务详情和扩展信息
     * @throws DEException 如果状态参数无效
     */
    public IPage<ExportTaskDTO> pager(Page<ExportTaskDTO> page, String status) {
        // 验证状态参数的有效性
        if (!STATUS.contains(status)) {
            DEException.throwException("Invalid status: " + status);
        }

        // 构建查询条件，限制为当前用户的任务
        QueryWrapper<CoreExportTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", AuthUtils.getUser().getUserId());
        if (!status.equalsIgnoreCase("ALL")) {
            queryWrapper.eq("export_status", status);
        }
        queryWrapper.orderByDesc("export_time");

        // 执行分页查询
        IPage<ExportTaskDTO> pager = exportTaskExtMapper.pager(page, queryWrapper);

        // 为查询结果补充扩展信息
        List<ExportTaskDTO> records = pager.getRecords();
        records.forEach(exportTask -> {
            if (status.equalsIgnoreCase("ALL") || status.equalsIgnoreCase(exportTask.getExportStatus())) {
                // 设置导出来源的绝对路径名称
                setExportFromAbsName(exportTask);
            }
            if (status.equalsIgnoreCase("ALL") || status.equalsIgnoreCase(exportTask.getExportStatus())) {
                // 设置组织信息
                proxy().setOrg(exportTask);
            }
        });

        return pager;
    }

    /**
     * 统计导出任务数量
     * 按状态统计当前用户的导出任务数量，用于前端展示任务概览
     *
     * @return 包含各状态任务数量的映射表，键为状态名称，值为任务数量
     */
    public Map<String, Long> exportTasks() {
        Map<String, Long> result = new HashMap<>();
        Long userId = AuthUtils.getUser().getUserId();
        QueryWrapper<CoreExportTask> queryWrapper = new QueryWrapper<>();

        // 统计进行中的任务数量
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("export_status", "IN_PROGRESS");
        result.put("IN_PROGRESS", exportTaskMapper.selectCount(queryWrapper));

        // 统计成功的任务数量
        queryWrapper.clear();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("export_status", "SUCCESS");
        result.put("SUCCESS", exportTaskMapper.selectCount(queryWrapper));

        // 统计失败的任务数量
        queryWrapper.clear();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("export_status", "FAILED");
        result.put("FAILED", exportTaskMapper.selectCount(queryWrapper));

        // 统计待执行的任务数量
        queryWrapper.clear();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("export_status", "PENDING");
        result.put("PENDING", exportTaskMapper.selectCount(queryWrapper));

        // 统计所有任务数量
        queryWrapper.clear();
        queryWrapper.eq("user_id", userId);
        result.put("ALL", exportTaskMapper.selectCount(queryWrapper));
        return result;
    }

    @XpackInteract(value = "exportCenter", before = false)
    public void setOrg(ExportTaskDTO exportTaskDTO) {
    }

    private ExportCenterManage proxy() {
        return CommonBeanFactory.getBean(ExportCenterManage.class);
    }

    private void setExportFromAbsName(ExportTaskDTO exportTaskDTO) {
        if (exportTaskDTO.getExportFromType().equalsIgnoreCase("chart")) {
            exportTaskDTO.setExportFromName(dataVisualizationServer.getAbsPath(exportTaskDTO.getExportFrom()));
        }
        if (exportTaskDTO.getExportFromType().equalsIgnoreCase("dataset")) {
            List<String> fullName = new ArrayList<>();
            datasetGroupManage.geFullName(Long.valueOf(exportTaskDTO.getExportFrom()), fullName);
            Collections.reverse(fullName);
            List<String> finalFullName = fullName;
            exportTaskDTO.setExportFromName(String.join("/", finalFullName));
        }
        if (exportTaskDTO.getExportFromType().equalsIgnoreCase("data_filling")) {
            List<String> fullName = new ArrayList<>();
            getDataFillingApi().geFullName(Long.valueOf(exportTaskDTO.getExportFrom()), fullName);
            Collections.reverse(fullName);
            List<String> finalFullName = fullName;
            exportTaskDTO.setExportFromName(String.join("/", finalFullName));
        }
    }

    /**
     * 获取主机名
     * 获取当前服务器的主机名，用于标识导出任务的执行节点
     *
     * @return 主机名
     * @throws DEException 如果无法获取主机名
     */
    private String hostName() {
        String hostname = null;
        try {
            InetAddress localMachine = InetAddress.getLocalHost();
            hostname = localMachine.getHostName();
        } catch (Exception e) {
            DEException.throwException("请设置主机名！");
        }
        return hostname;
    }

    public void addTask(String exportFrom, String exportFromType, ChartExcelRequest request, String busiFlag) {
        CoreExportTask exportTask = new CoreExportTask();
        exportTask.setId(IDUtils.snowID().toString());
        exportTask.setUserId(AuthUtils.getUser().getUserId());
        exportTask.setExportFrom(Long.valueOf(exportFrom));
        exportTask.setExportFromType(exportFromType);
        exportTask.setExportStatus("PENDING");
        exportTask.setFileName(request.getViewName() + ".xlsx");
        exportTask.setExportProgress("0");
        exportTask.setExportTime(System.currentTimeMillis());
        exportTask.setParams(JsonUtil.toJSONString(request).toString());
        exportTask.setExportMachineName(hostName());
        exportTaskMapper.insert(exportTask);
        if (busiFlag.equalsIgnoreCase("dashboard")) {
            exportCenterDownLoadManage.startPanelViewTask(exportTask, request);
        } else {
            exportCenterDownLoadManage.startDataVViewTask(exportTask, request);
        }

    }

    public void addTask(Long exportFrom, String exportFromType, DataSetExportRequest request) throws Exception {
        datasetGroupManage.getDatasetGroupInfoDTO(exportFrom, null);
        CoreExportTask exportTask = new CoreExportTask();
        exportTask.setId(IDUtils.snowID().toString());
        exportTask.setUserId(AuthUtils.getUser().getUserId());
        exportTask.setExportFrom(exportFrom);
        exportTask.setExportFromType(exportFromType);
        exportTask.setExportStatus("PENDING");
        exportTask.setFileName(request.getFilename() + ".xlsx");
        exportTask.setExportProgress("0");
        exportTask.setExportTime(System.currentTimeMillis());
        exportTask.setParams(JsonUtil.toJSONString(request).toString());
        exportTask.setExportMachineName(hostName());
        exportTaskMapper.insert(exportTask);
        exportCenterDownLoadManage.startDatasetTask(exportTask, request);
    }

    @Override
    public void addTask(String exportFromId, String exportFromType, HashMap<String, Object> request, Long userId, Long org) {
        CoreExportTask exportTask = new CoreExportTask();
        request.put("org", org);
        exportTask.setId(IDUtils.snowID().toString());
        exportTask.setUserId(userId);
        exportTask.setExportFrom(Long.valueOf(exportFromId));
        exportTask.setExportFromType(exportFromType);
        exportTask.setExportStatus("PENDING");
        exportTask.setFileName(request.get("name") + ".xlsx");
        exportTask.setExportProgress("0");
        exportTask.setExportTime(System.currentTimeMillis());
        exportTask.setParams(JsonUtil.toJSONString(request).toString());
        exportTask.setExportMachineName(hostName());
        exportTaskMapper.insert(exportTask);
        if (StringUtils.equals(exportFromType, "data_filling")) {
            exportCenterDownLoadManage.startDataFillingTask(exportTask, request);
        }
    }

    public void cleanLog() {
        String key = "basic.exportFileLiveTime";
        String val = sysParameterManage.singleVal(key);
        if (StringUtils.isBlank(val)) {
            DEException.throwException("未获取到文件保留时间");
        }
        QueryWrapper<CoreExportTask> queryWrapper = new QueryWrapper<>();
        long expTime = Long.parseLong(val) * 24L * 3600L * 1000L;
        long threshold = System.currentTimeMillis() - expTime;
        queryWrapper.lt("export_time", threshold);
        exportTaskMapper.selectList(queryWrapper).forEach(coreExportTask -> {
            delete(coreExportTask.getId());
        });

    }

    public void addWatermarkTools(Workbook wb) {
        VisualizationWatermark watermark = watermarkMapper.selectById("system_default");
        WatermarkContentDTO watermarkContent = JsonUtil.parseObject(watermark.getSettingContent(), WatermarkContentDTO.class);
        if (watermarkContent.getEnable() && watermarkContent.getExcelEnable()) {
            UserFormVO userInfo = visualizationMapper.queryInnerUserInfo(AuthUtils.getUser().getUserId());
            // 在主逻辑中添加水印
            int watermarkPictureIdx = ExcelWatermarkUtils.addWatermarkImage(wb, watermarkContent, userInfo); // 生成水印图片并获取 ID
            for (Sheet sheet : wb) {
                ExcelWatermarkUtils.addWatermarkToSheet(sheet, watermarkPictureIdx); // 为每个 Sheet 添加水印
            }
        }
    }

    @DeLog(id = "#p0", ot = LogOT.DOWNLOAD, st = LogST.DATA)
    public void generateDownloadUri(String id) {
        CoreExportDownloadTask coreExportDownloadTask = coreExportDownloadTaskMapper.selectById(id);
        if (coreExportDownloadTask != null) {
            coreExportDownloadTask.setCreateTime(System.currentTimeMillis());
            coreExportDownloadTaskMapper.updateById(coreExportDownloadTask);
        } else {
            coreExportDownloadTask = new CoreExportDownloadTask();
            coreExportDownloadTask.setId(id);
            coreExportDownloadTask.setCreateTime(System.currentTimeMillis());
            coreExportDownloadTask.setValidTime(5L);
            coreExportDownloadTaskMapper.insert(coreExportDownloadTask);
        }
    }


    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void checkDownLoadInfos() {
        coreExportDownloadTaskMapper.selectList(null).forEach(downLoadInfo -> {
            if (System.currentTimeMillis() - downLoadInfo.getCreateTime() > downLoadInfo.getValidTime() * 60 * 1000) {
                coreExportDownloadTaskMapper.deleteById(downLoadInfo.getId());
            }
        });
    }

    @Data
    public class DownLoadInfo {
        String id;
        Long validTime; // 单位：minutes
        Long createTime;
    }
}

