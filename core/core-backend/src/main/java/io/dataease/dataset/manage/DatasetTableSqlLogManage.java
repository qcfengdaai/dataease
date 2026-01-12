package io.dataease.dataset.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dataease.api.dataset.dto.SqlLogDTO;
import io.dataease.dataset.dao.auto.entity.CoreDatasetTableSqlLog;
import io.dataease.dataset.dao.auto.mapper.CoreDatasetTableSqlLogMapper;
import io.dataease.utils.BeanUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 数据集SQL日志管理类
 * 负责数据集查询SQL的执行日志记录
 * 用于SQL性能监控和调试
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>SQL执行日志的保存</li>
 *   <li>按表ID查询执行历史</li>
 *   <li>记录SQL开始时间、结束时间、耗时和状态</li>
 * </ul>
 *
 * @author Junjun
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class DatasetTableSqlLogManage {
    @Resource
    private CoreDatasetTableSqlLogMapper coreDatasetTableSqlLogMapper;

    public void save(SqlLogDTO dto) {
        if (dto == null) {
            return;
        }
        CoreDatasetTableSqlLog coreDatasetTableSqlLog = new CoreDatasetTableSqlLog();
        BeanUtils.copyBean(coreDatasetTableSqlLog, dto);
        if (ObjectUtils.isEmpty(coreDatasetTableSqlLog.getId())) {
            coreDatasetTableSqlLog.setId(UUID.randomUUID().toString());
            coreDatasetTableSqlLogMapper.insert(coreDatasetTableSqlLog);
        } else {
            coreDatasetTableSqlLogMapper.updateById(coreDatasetTableSqlLog);
        }
    }

    public List<SqlLogDTO> listByTableId(SqlLogDTO dto) {
        if (dto == null || ObjectUtils.isEmpty(dto.getTableId())) {
            return null;
        }
        QueryWrapper<CoreDatasetTableSqlLog> wrapper = new QueryWrapper<>();
        wrapper.eq("table_id", dto.getTableId());
        List<CoreDatasetTableSqlLog> coreDatasetTableSqlLogs = coreDatasetTableSqlLogMapper.selectList(wrapper);
        return coreDatasetTableSqlLogs.stream().map(ele -> {
            SqlLogDTO s = new SqlLogDTO();
            BeanUtils.copyBean(s, ele);
            return s;
        }).collect(Collectors.toList());
    }

    public void deleteByTableId(String id) {
        QueryWrapper<CoreDatasetTableSqlLog> wrapper = new QueryWrapper<>();
        wrapper.eq("table_id", id);
        coreDatasetTableSqlLogMapper.delete(wrapper);
    }
}
