package io.dataease.exportCenter.server;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.dataease.api.exportCenter.ExportCenterApi;
import io.dataease.exportCenter.manage.ExportCenterManage;
import io.dataease.exportCenter.util.ExportCenterUtils;
import io.dataease.model.ExportTaskDTO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 导出中心服务控制器
 * 提供导出中心相关的REST API接口，作为前端与导出管理业务逻辑之间的桥梁
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>导出任务的查询和管理</li>
 *   <li>导出文件的下载和访问控制</li>
 *   <li>导出任务的状态统计</li>
 *   <li>导出任务的删除和重试操作</li>
 *   <li>导出限制的查询</li>
 * </ul>
 *
 * <p>URL映射：</p>
 * <ul>
 *   <li>基础路径：/exportCenter</li>
 *   <li>所有导出中心相关的API调用都通过此控制器处理</li>
 * </ul>
 *
 * <p>实现模式：</p>
 * <ul>
 *   <li>实现ExportCenterApi接口，确保API契约的一致性</li>
 *   <li>将所有业务逻辑委托给ExportCenterManage组件处理</li>
 *   <li>负责HTTP请求的接收和响应的格式化</li>
 *   <li>提供事务管理保证数据一致性</li>
 * </ul>
 */
@RestController
@RequestMapping("/exportCenter")
@Transactional(rollbackFor = Exception.class)
public class ExportCenterServer implements ExportCenterApi {

    /**
     * 导出中心管理组件
     * 负责处理所有导出相关的业务逻辑操作
     */
    @Resource
    private ExportCenterManage exportCenterManage;

    /**
     * 统计导出任务数量
     * 获取当前用户各状态下的导出任务数量统计信息
     *
     * @return 包含各状态任务数量的映射表
     */
    @Override
    public Map<String, Long> exportTasks() {
        return exportCenterManage.exportTasks();
    }

    /**
     * 分页查询导出任务
     * 根据状态分页查询当前用户的导出任务列表
     *
     * @param goPage 页码，从1开始
     * @param pageSize 每页显示数量
     * @param status 任务状态过滤条件
     * @return 分页后的导出任务DTO列表
     */
    @Override
    public IPage<ExportTaskDTO> pager(int goPage, int pageSize, String status) {
        Page<ExportTaskDTO> page = new Page<>(goPage, pageSize);
        return exportCenterManage.pager(page, status);
    }

    /**
     * 删除导出任务
     * 删除指定的导出任务，包括数据库记录和导出文件
     *
     * @param id 导出任务ID
     */
    @Override
    public void delete(String id) {
        exportCenterManage.delete(id);
    }

    /**
     * 批量删除导出任务
     * 根据任务ID列表批量删除导出任务
     *
     * @param ids 导出任务ID列表
     */
    @Override
    public void delete(List<String> ids) {
        exportCenterManage.delete(ids);
    }

    /**
     * 按状态批量删除导出任务
     * 根据状态类型批量删除当前用户的导出任务
     *
     * @param type 任务状态类型，支持SUCCESS、FAILED、PENDING、IN_PROGRESS、ALL
     */
    @Override
    public void deleteAll(String type) {
        exportCenterManage.deleteAll(type);
    }

    /**
     * 下载导出文件
     * 根据任务ID下载对应的导出文件
     *
     * @param id 导出任务ID
     * @param response HTTP响应对象，用于输出文件流
     * @throws Exception 如果任务不存在或下载过程中出现错误
     */
    @Override
    public void download(String id, HttpServletResponse response) throws Exception {
        exportCenterManage.download(id, response);
    }

    /**
     * 生成下载链接
     * 为指定的导出任务生成下载链接，用于前端调用下载接口
     *
     * @param id 导出任务ID
     * @return 下载链接，当前实现返回空字符串
     * @throws Exception 如果生成过程中出现错误
     */
    @Override
    public String generateDownloadUri(String id) throws Exception {
        exportCenterManage.generateDownloadUri(id);
        return "";
    }

    /**
     * 重试导出任务
     * 重新执行失败的导出任务
     *
     * @param id 导出任务ID
     */
    @Override
    public void retry(String id) {
        exportCenterManage.retry(id);
    }

    /**
     * 获取导出限制
     * 获取数据集导出的限制数量配置
     *
     * @return 导出限制数量字符串表示
     */
    public String exportLimit() {
        return String.valueOf(ExportCenterUtils.getExportLimit("dataset"));
    }
}
