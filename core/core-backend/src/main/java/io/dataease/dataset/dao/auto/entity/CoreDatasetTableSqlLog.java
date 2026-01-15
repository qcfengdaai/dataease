package io.dataease.dataset.dao.auto.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 数据集表SQL执行日志实体类
 * 对应数据库表 core_dataset_table_sql_log，用于记录数据集查询SQL的执行日志
 * 提供SQL执行性能监控和调试信息
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>记录SQL执行的详细信息（开始时间、结束时间、耗时）</li>
 *   <li>存储执行的SQL语句内容</li>
 *   <li>跟踪SQL执行状态和结果</li>
 *   <li>支持SQL性能分析和优化</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2023-08-08
 */
@TableName("core_dataset_table_sql_log")
public class CoreDatasetTableSqlLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private String id;

    /**
     * 数据集SQL节点ID
     */
    private String tableId;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 耗时(毫秒)
     */
    private Long spend;

    /**
     * 详细信息
     */
    private String sql;

    /**
     * 状态
     */
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getSpend() {
        return spend;
    }

    public void setSpend(Long spend) {
        this.spend = spend;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CoreDatasetTableSqlLog{" +
        "id = " + id +
        ", tableId = " + tableId +
        ", startTime = " + startTime +
        ", endTime = " + endTime +
        ", spend = " + spend +
        ", sql = " + sql +
        ", status = " + status +
        "}";
    }
}
