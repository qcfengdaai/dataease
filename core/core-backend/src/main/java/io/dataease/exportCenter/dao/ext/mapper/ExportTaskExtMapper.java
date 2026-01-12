package io.dataease.exportCenter.dao.ext.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dataease.exportCenter.dao.auto.entity.CoreExportTask;
import io.dataease.model.ExportTaskDTO;
import org.apache.ibatis.annotations.*;


/**
 * 导出任务扩展Mapper接口
 * 提供导出任务相关的复杂查询和分页查询功能，扩展基础Mapper的功能
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>继承BaseMapper，提供基础的CRUD操作</li>
 *   <li>提供自定义的分页查询功能</li>
 *   <li>支持复杂的条件查询和数据映射</li>
 *   <li>返回包含组织信息和来源名称的扩展DTO</li>
 * </ul>
 *
 * <p>扩展功能：</p>
 * <ul>
 *   <li>分页查询导出任务列表</li>
 *   <li>自定义结果集映射</li>
 *   <li>支持动态查询条件</li>
 * </ul>
 */
@Mapper
public interface ExportTaskExtMapper extends BaseMapper<CoreExportTask> {

    /**
     * 分页查询导出任务列表
     * 根据查询条件分页获取导出任务信息，返回包含扩展信息的DTO对象
     *
     * @param page 分页参数，包含页码、页大小等信息
     * @param queryWrapper 查询条件包装器，支持动态条件拼接
     * @return 分页后的导出任务DTO列表，包含任务详情和扩展信息
     */
    @Select(
            """
                    select *
                    from core_export_task
                    ${ew.customSqlSegment}
                    """
    )
    @Results(
            id = "exportTasksMap",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "user_id", column = "userId"),
                    @Result(property = "file_name", column = "fileName"),
                    @Result(property = "file_size", column = "fileSize"),
                    @Result(property = "file_size_unit", column = "fileSizeUnit"),
                    @Result(property = "export_from", column = "exportFrom"),
                    @Result(property = "export_status", column = "exportStatus"),
                    @Result(property = "msg", column = "msg"),
                    @Result(property = "export_from_type", column = "exportFromType"),
                    @Result(property = "export_progress", column = "exportProgress"),
                    @Result(property = "export_machine_name", column = "exportMachineName"),
                    @Result(property = "export_from_name", column = "exportFromName"),
                    @Result(property = "org_name", column = "orgName"),
                    @Result(property = "export_time", column = "exportTime")
            }
    )
    IPage<ExportTaskDTO> pager(IPage<ExportTaskDTO> page, @Param("ew") QueryWrapper queryWrapper);


}
